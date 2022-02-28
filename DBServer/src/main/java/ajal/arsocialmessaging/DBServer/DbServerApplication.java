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
		// TODO: switch to send a message so that the client can check whether to display notification or not
		// https://firebase.google.com/docs/cloud-messaging/send-message
		try {
			sendNotification(postcode);
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
	private void sendNotification(String postcode) throws FirebaseMessagingException {
		List<String> registrationTokens = getRegistrationTokens();
		if (registrationTokens.size() == 0) {
			return;
		}

		String title = "You have a new message in your area: "+postcode;
		String body = "Click here to view it!";
		MulticastMessage message = MulticastMessage.builder()
				.setNotification(Notification.builder()
						.setTitle(title)
						.setBody(body)
						.build())
				.putData("postcode", postcode)
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

	/**
	 * Sets up the Messages repository with messages
	 * @return
	 * @throws Exception
	 */
	@Bean
	public CommandLineRunner run() throws Exception {
		setupNotifications();
		return (String[] args) -> {
			messagesRepo.truncate();

			Message msg1 = new Message(1, "Happy Birthday", "happy-birthday.obj");
			Message msg2 = new Message(2, "Merry Christmas", "merry-christmas.obj");
			Message msg3 = new Message(3, "Congratulations", "congratulations.obj");
			Message msg4 = new Message(4, "Good luck", "good-luck.obj");
			Message msg5 = new Message(5, "Hope you feel better soon!", "feel-better.obj");
			Message msg6 = new Message(6, "Thank you", "thank-you.obj");
			Message[] messages = {msg1, msg2, msg3, msg4, msg5, msg6};

			for (Message msg : messages) {
				messagesRepo.save(msg);
			}
		};
	}

	public static void main(String[] args){
		SpringApplication.run(DbServerApplication.class, args);
	}
}