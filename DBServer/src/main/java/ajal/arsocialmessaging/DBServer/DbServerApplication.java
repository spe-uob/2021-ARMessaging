package ajal.arsocialmessaging.DBServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController

public class DbServerApplication {
	@Autowired
	MessagesRepository messagesRepo;
	@Autowired
	BannersRepository bannersRepo;

	@RequestMapping("/addBanner")
	public String addBanner(@RequestParam("postcode") String postcode, @RequestParam("messageId") Integer messageId){
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
	public String getAllBanners() {
		StringBuilder response = new StringBuilder();
		Iterable<Banner> banners = bannersRepo.findAll();
		for(Banner banner: banners) {
			response.append(banner.getId()).append(" ").append(banner.getPostcode()).append(" ").append(banner.getMessage()).append(" ").append(banner.getTimestamp()).append("<br>\n");
		}
		return response.toString();
	}

	@RequestMapping("/sayHello")
	public Map<String, String> sayHello() {
		System.out.println("received call for sayHello");
		HashMap<String, String> map = new HashMap<>();
		map.put("login", "lucy");
		map.put("id", "1666345");
		map.put("url", "https://api.github.com/users/lucy");
		return map;
	}

	@RequestMapping(value = "/getAllMessages", method = RequestMethod.GET)
	@ResponseBody
	public String getAllMessages() {
		System.out.println("received call for getMessages");
		StringBuilder response = new StringBuilder();
		Iterable<Message> messages = messagesRepo.findAll();
		for(Message message: messages) {
			response.append(message.getId()).append(" ").append(message.getMessage()).append(" ").append(message.getObjfilename()).append("<br>\n");
		}
		return response.toString();
	}

	public static void main(String[] args){
		SpringApplication.run(DbServerApplication.class, args);
	}
}
