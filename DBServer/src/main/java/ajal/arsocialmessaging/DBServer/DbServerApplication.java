package ajal.arsocialmessaging.DBServer;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.json.simple.JSONObject;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private FileInputStream GOOGLE_APPLICATION_CREDENTIALS;

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
		Iterable<Banner> banners = bannersRepo.findAll();
		for (Banner banner : banners) {
			long difference = now.getTime() - banner.getTimestamp().getTime();
			if (difference > TimeUnit.DAYS.toMillis(1)) {
				bannersRepo.deleteByPostcodeAndTimestamp(banner.getPostcode(), banner.getTimestamp());
				System.out.println("Deleted banner at postcode " + banner.getPostcode() + " with timestamp " + banner.getTimestamp());
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
		JSONObject obj = new JSONObject();
		obj.put("type", "service_account");
		obj.put("project_id", "skywrite-a1fb5");
		obj.put("private_key_id", "c422e0644d38212689e2cbb74c27cbd36119925b");
		obj.put("private_key", "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCzxEz+JjPayNOx\npNO8o1iLNW3t8f7xR1HMeG2dq7ndV9/akeO4YWwBdTvmNQ3GCcj8AaHre3b/6RCm\nepcSONiDbPTD2JL9eMDTQsTGpm5+R1PfjiTZ9Ssb5g0+2VLVUfhbO/2vditkLDZi\nn7wk1GMi1+6MCLir8VUpZQ/DiBOti6xwNciK9PWvTHtCMv2mwBy1QUTJXditl2ON\nOFFe4MHarqfE3TNe1l9HnbrW86Taus6eh6pvDYSBepfgC/pvyB6mZS5MWMZpBnnK\nxv+WDxA2UAZhtzI6VgMAZ/Jr0S5mVnA7mxe9F+BOTLYKpcYtPtarc44D7JddYcLK\nrH7UdU7XAgMBAAECggEAAiGtyuRZ7ho+dzdNEtxaH4U186x7De2jqM5NVCeGB5Qa\nKrKwcIOuc10FyhbF6OToVSN/PtMjn3f2VxqhTrZvRdhvau/XGWZqoD5IAZv0Q+1w\nHQTUCNg/tEa+q9fezQxz6rnKF8GGnhgsfqDhlbYFqcP2Xkvydoid5UZg4Z2pBU+G\nGX/mNkAi5mg4Mt6hcibTGkafIfZJwuaykbYcoz1p/aL8RVkykbfzTbChhmQcTM+O\n0FWNtXCa3Ijl2MRKbxlhtdKQKgAUF7+DWAQ3j72GzDPWbu13jTyyEZPVb4pJkGQd\nm4uOHf92tKF/P4Ixnu7RE/T/EuIktHtvAX7CUKrveQKBgQDj2qPtZgMBvYPxDFus\nZqqLFJmJ5fFiHr60uaWMoTpuWa9UrA91peQ694LbG/7ylAQqN0Cs69BQAenmIE9F\nKvt6r+wh5nXOweUoNqhOuTm8ZMcb3O6vfLueyUVkqvxjfDpDZBQfkUWDXd84HJu2\nxzK7XtYW4YcHfB4B3GyTBhTv6wKBgQDJ+QLyO8/svNCznGXPdo4qv7pzlfp2AmN8\nSeW7rucB0cdeB90wVGReeWJOOVOoq5EBtx03eqol1O+eG/bTbq6phvjXUKUE6yuI\nOyU28JX+TXFlOSALYTvOeBy+vy5hJbOav6ULFJH0blhaobdsmhAuhyar5hYRuJbM\nR8KUcc1NxQKBgBfDbsNMl1WwITmbk1gIoRK+REEYhTM5h6QrlHN1QTXPDrUi+L3J\nXmMz+ybE5bMA8upANvOR6HjfqjhA+GN7Vxz1iggDFBhLKo4mHSmQsc/PJuDmCtKs\njJjD3wPfvVDW3PC4WEzuhrRrruMYQLkwTz8xZdyfCskiDbMd/QjcYoSfAoGATmE1\nVH4DtdKch2dlVzqh91MKb3q/hPZuVzhyUACTI3Celw4kN3I4tTUUAlla7vUNxAWk\n7/fJef8Fsm0Vv32PiLiZby2brKn67dZOHeEFxHeXTvn5RpFIXVrdhOS6gfbYmXBQ\nA1nLPnloDlv9y8aKCxRl3IXhAKWK1+HtUCBN1SUCgYAndtGdheJo0+Idd5hJ/GUV\n0iD6cUrnQN6b+79HnU71XCuK/7irVag09mgG9Mc2jqrLMUbJQ24R/PL5DLLqE7gY\nYzse+05qN3FGS+D2bSIHBKIeZcsk1+m9lYPI7eCsgp2QDv83eX3CAGOsNZ2bo8Yj\nMGH1t1k3GQNFsCDEGiNZjg==\n-----END PRIVATE KEY-----\n");
		obj.put("client_email", "firebase-adminsdk-vvuex@skywrite-a1fb5.iam.gserviceaccount.com");
		obj.put("client_id", "116744195647642783285");
		obj.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
		obj.put("token_uri", "https://oauth2.googleapis.com/token");
		obj.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
		obj.put("client_x509_cert_url", "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-vvuex%40skywrite-a1fb5.iam.gserviceaccount.com");

		InputStream stream = new ByteArrayInputStream(obj.toString().getBytes());

//		String dir = new File(".").getAbsolutePath();
//		String path = dir.substring(0, dir.length() - 1)+"service-account-file.json";
//		FileInputStream GOOGLE_APPLICATION_CREDENTIALS =
//				new FileInputStream(path);
//		assert GOOGLE_APPLICATION_CREDENTIALS != null;

		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(stream))
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


	// Sets up the Messages repository with messages
	@Bean
	public CommandLineRunner run() throws Exception {
		setupNotifications();
		return (String[] args) -> {
			messagesRepo.truncate();

			Message msg1 = new Message(1, "Happy birthday", "happy-birthday.obj");
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