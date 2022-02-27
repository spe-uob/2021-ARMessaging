package ajal.arsocialmessaging.DBServer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

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

	@RequestMapping("/addBanner")
	public String addBanner(@RequestParam("bannerData") String bannerData){
		System.out.println("bannerData is "+bannerData);
		int commaIndex = bannerData.indexOf(",");
		String postcode = bannerData.substring(0 , commaIndex);
		int messageId = Integer.parseInt(bannerData.substring(commaIndex+1));
		Banner newBanner = new Banner(postcode, messageId);
		bannersRepo.save(newBanner);
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

	/**
	 * Sets up the Messages repository with messages
	 * @return
	 * @throws Exception
	 */
	@Bean
	public CommandLineRunner run() throws Exception {
		return (String[] args) -> {
			messagesRepo.deleteAll();

			List<Map<String, String>> messagesInServer = getAllMessages();
			int x = messagesInServer.size();
			Message msg1 = new Message(x+1, "Happy Birthday", "happy-birthday.obj");
			Message msg2 = new Message(x+2, "Merry Christmas", "merry-christmas.obj");
			Message msg3 = new Message(x+3, "Congratulations", "congratulations.obj");
			Message msg4 = new Message(x+4, "Good luck", "good-luck.obj");
			Message msg5 = new Message(x+5, "Hope you feel better soon!", "feel-better.obj");
			Message msg6 = new Message(x+6, "Thank you", "thank-you.obj");
			Message[] messages = {msg1, msg2, msg3, msg4, msg5, msg6};

			for (Message msg : messages) {
				messagesRepo.save(msg);
			}
			messagesRepo.findAll().forEach(msg -> System.out.println(msg));
		};
	}

	public static void main(String[] args){
		SpringApplication.run(DbServerApplication.class, args);
	}
}