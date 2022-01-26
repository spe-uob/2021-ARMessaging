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

	@RequestMapping(value = "/getAllMessages", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String>  getAllMessages() {
		System.out.println("received call for getMessages");
		Iterable<Message> messages = messagesRepo.findAll();
		HashMap<String, String> response = new HashMap<>();
		for(Message message: messages) {
			response.put("id", message.getId().toString());
			response.put("message", message.getMessage());
			response.put("objfilename", message.getObjfilename());
		}
		return response;
	}

	public static void main(String[] args){
		SpringApplication.run(DbServerApplication.class, args);
	}
}
