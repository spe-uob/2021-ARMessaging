package ajal.arsocialmessaging.DBServer;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	public List<Map<String, String>> getAllBanners() {
		System.out.println("received call for getBanners");
		Iterable<Banner> banners = bannersRepo.findAll();
		List<Map<String, String>> response = new ArrayList<>();
		for(Banner banner: banners) {
			HashMap<String, String> bannerData = new HashMap<>();
			bannerData.put("id", banner.getId().toString());
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



	/*
	@RequestMapping(value = "/getAllMessages", method = RequestMethod.GET)
	@ResponseBody
	public String getAllMessages() throws JSONException {
		System.out.println("received call to getMessages");
		JSONObject response = new JSONObject();
		Iterable<Message> messages = messagesRepo.findAll();
		for(Message message: messages){
			System.out.println("adding a new message to the json object");
			response.put("Message", new JSONObject()
					.put("id", message.getId().toString())
					.put("message", message.getMessage())
					.put("objfilename", message.getObjfilename()));
		}
		return response.toString();
	}

	 */


	public static void main(String[] args){
		SpringApplication.run(DbServerApplication.class, args);
	}
}
