package ajal.arsocialmessaging.DBServer;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
@RestController

public class DbServerApplication {

	@Autowired
	MessagesRepository messagesRepo;
	@Autowired
	BannersRepository bannersRepo;
	@Autowired
	TokensRepository tokenRepo;

	private InputStream GOOGLE_APPLICATION_CREDENTIALS;

	@RequestMapping("/addBanner")
	public String addBanner(@RequestParam("bannerData") String bannerData){
		System.out.println("bannerData is "+bannerData);
		int commaIndex = bannerData.indexOf(",");
		String postcode = bannerData.substring(0 , commaIndex);
		int messageId = Integer.parseInt(bannerData.substring(commaIndex+1));
		Banner newBanner = new Banner(postcode, messageId);
		bannersRepo.save(newBanner);

		// Send a notification to every user
		try {
			sendNotification(newBanner);
		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
		}

		return "OK";
	}

	@RequestMapping("/deleteBanner")
	public String deleteBanner(@RequestParam("id") Integer bannerId) {
		if(bannersRepo.existsById(bannerId)) {
			bannersRepo.deleteById(bannerId);
			return bannerId + " deleted";
		}
		else return bannerId + " not found";
	}

	@RequestMapping("/getAllBanners")
	public List<Map<String, String>> getAllBanners() {
		System.out.println("received call for getBanners");
		Iterable<Banner> banners = bannersRepo.findAll();
		List<Map<String, String>> response = new ArrayList<>();
		for(Banner banner: banners) {
			HashMap<String, String> bannerData = new HashMap<>();
			bannerData.put("postcode", banner.getPostcode());
			bannerData.put("message", banner.getMessage().toString());
			bannerData.put("timestamp", banner.getTimestamp().toString());
			response.add(bannerData);
		}
		return response;
	}

	@RequestMapping(value = "/getAllMessages", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, String>> getAllMessages() {
		System.out.println("received call for getMessages");
		Iterable<Message> messages = messagesRepo.findAll();
		List<Map<String, String>> response = new ArrayList<>();
		for(Message message: messages) {
			HashMap<String, String> messageData = new HashMap<>();
			messageData.put("id", message.getId().toString());
			messageData.put("message", message.getMessage());
			messageData.put("objfilename", message.getObjfilename());
			response.add(messageData);
		}
		return response;
	}

	@Scheduled(fixedRate = 3600000)
	public void removeAfter24Hours() {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		List<Map<String, String>> banners = getAllBanners();
		for (Map<String, String> banner : banners) {
			long difference = now.getTime() - Timestamp.valueOf(banner.get("timestamp")).getTime();
			if (difference > TimeUnit.DAYS.toMillis(1)) {
				deleteBanner(Integer.valueOf(banner.get("id")));
			}
		}
	}

	@RequestMapping("/addToken")
	public String addToken(@RequestParam("tokenData") String tokenData) {
		System.out.println("tokenData is "+tokenData);
		// Do not add an already existing token to the database
		List<String> registrationTokens = getRegistrationTokens();
		for (String t : registrationTokens) {
			if (t.equals(tokenData)) {
				System.out.println("Token already in database!");
				return "OK";
			}
		}

		// Add token to database
		Iterable<Token> allTokens = tokenRepo.findAll();
		int id = 1;
		for (Token t : allTokens) {
			id++;
		}
		Token newToken = new Token(id, tokenData);
		tokenRepo.save(newToken);

		return "OK";
	}

	// Sets up Firebase Cloud Messaging for Notifications
	private void setupNotifications() throws IOException {
		GOOGLE_APPLICATION_CREDENTIALS = getClass().getClassLoader().getResourceAsStream("./service-account-file.json");

		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(GOOGLE_APPLICATION_CREDENTIALS))
				.build();
		FirebaseApp.initializeApp(options);
	}

	// Sends a notification to all users
	// REFERENCE: https://firebase.google.com/docs/cloud-messaging/send-message 28/02/2002 00:20
	private void sendNotification(Banner banner) throws FirebaseMessagingException {
		List<String> registrationTokens = getRegistrationTokens();
		if (registrationTokens.size() == 0) {
			return;
		}

		String postcode = banner.getPostcode();
		int messageId = banner.getMessage();
		Timestamp timestamp = banner.getTimestamp();

		// NOTE: Server does not send a notification payload, because when app is in background state
		// onMessageReceived will never be called, and so the notification payload is sent straight to the system tray
		// rather than going through onMessageReceived()
		MulticastMessage message = MulticastMessage.builder()
				.putData("postcode", postcode)
				.putData("message", String.valueOf(messageId))
				.putData("timestamp", timestamp.toString())
				.addAllTokens(registrationTokens)
				.build();

		BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
		System.out.println(response.getSuccessCount() + "/" + registrationTokens.size() + " messages were sent successfully");
	}

	// Get the registration tokens as a list of strings
	private List<String> getRegistrationTokens() {
		Iterable<Token> allTokens = tokenRepo.findAll();
		List<String> registrationTokens = new ArrayList<>();
		for (Token t : allTokens) {
			registrationTokens.add(t.token);
		}
		return registrationTokens;
	}
	
	@Bean
	public void run() throws Exception {
		setupNotifications();
		return;
	}

	public static void main(String[] args){
		SpringApplication.run(DbServerApplication.class, args);
	}
}