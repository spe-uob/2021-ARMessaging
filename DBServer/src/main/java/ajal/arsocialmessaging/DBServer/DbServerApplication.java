package ajal.arsocialmessaging.DBServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
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
	public void removeAfter24Hours() throws ParseException {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Iterable<Banner> banners = bannersRepo.findAll();
		for (Banner banner : banners) {
			long difference = now.getTime() - banner.getTimestamp().getTime();
			if (difference > TimeUnit.DAYS.toMillis(1)) {
				long count = bannersRepo.deleteByPostcodeAndTimestamp(banner.getPostcode(), banner.getTimestamp());
				assert(count == 1);
				System.out.println("Deleted banner at postcode " + banner.getPostcode() + " with timestamp " + banner.getTimestamp());
			}
		}
	}

	public static void main(String[] args){
		SpringApplication.run(DbServerApplication.class, args);
	}
}
