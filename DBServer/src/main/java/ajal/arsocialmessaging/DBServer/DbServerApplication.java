package ajal.arsocialmessaging.DBServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController

public class DbServerApplication {
	@Autowired
	MessagesRepository messagesRepo;
	@Autowired
	BannersRepository bannersRepo;

	@RequestMapping("/addBanner")
	public String addBanner(@RequestParam("postcode") String postcode, @RequestParam("messageId") Message messageId){
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

	@RequestMapping("/getAllMessages")
	public String getAllMessages() {
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
