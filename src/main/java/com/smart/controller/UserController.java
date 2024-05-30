package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.smart.dao.ContactRepository;
import com.smart.dao.FeedbackRepository;
// import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.Feedback;
// import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private FeedbackRepository feedbackRepository;
	// @Autowired
	// private MyOrderRepository myOrderRepository;

	@ModelAttribute // used to send common data top all jsp pages
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME " + userName);

		// get the user using username(Email)
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER " + user);
		model.addAttribute("user", user);
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/dashboard";
	}

	// open add form handler
	@GetMapping("/addcontact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		System.out.println("contact saved.");
		return "normal/add-contact-form";
	}

	// handler for processing the contact form
	@PostMapping("/processcontact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile multiPartFile, Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// check if file is empty
			if (multiPartFile.isEmpty()) {
				System.out.println("file is Empty");
				contact.setImageURL("defaultcontactpic.png");
			} else {

				// String[] split = contact.getEmail().split("@");
				contact.setImageURL(multiPartFile.getOriginalFilename());

				File file = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(file.getAbsolutePath() + File.separator + multiPartFile.getOriginalFilename());

				Files.copy(multiPartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}

			contact.setUser(user);
			user.getContacts().add(contact); // User store the contact

			this.userRepository.save(user);

			System.out.println("Data : " + contact);
			// System.out.println("Contact Image :"+contact.getImage().toString());
			System.out.println("Successfully added to database");

			// success message
			session.setAttribute("message", new Message("Your contact is successfully added. Add more...", "success"));
		} catch (Exception e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();

			// error message
			session.setAttribute("message", new Message("Something went wrong. Try again...", "danger"));
		}

		// return "normal/add-contact-form";
		return "redirect:/user/showcontacts/0";
	}

	@GetMapping("/showcontacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contacts");

		// to fetch the contact list of current user
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// current page
		// contact per page - 4
		Pageable pageable = PageRequest.of(page, 4);

		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/view-contacts";
	}

	// showing particular contact details.
	@GetMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("CID " + cId);

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/contact-details";
	}

	@GetMapping("/delete/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, Principal principal,
			HttpSession session) {
		System.out.println("contact id " + cId);

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		// Contact contact = this.contactRepository.findById(cId).get();

		// System.out.println("contact" + contact.getcId());

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if (user.getId() == contact.getUser().getId()) {
			user.getContacts().remove(contact);
			this.userRepository.save(user);
			System.out.println("contact deleted.");
		}

		session.setAttribute("message", new Message("Contact deleted succesfully", "success"));

		return "redirect:/user/showcontacts/0";
	}

	// open update form handler
	@PostMapping("/updatecontact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);

		return "normal/update-contact-form";
	}

	// update contact handler
	@PostMapping("/processupdate")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {

		try {

			// old contact details
			Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();

			if (!file.isEmpty()) {
				// delete old photo
				File deleteFile = new ClassPathResource("static/image").getFile();
				File oldFile = new File(deleteFile, oldcontactDetail.getImageURL());
				oldFile.delete();

				// update new photo
				File saveFile = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImageURL(file.getOriginalFilename());

			} else {
				contact.setImageURL(oldcontactDetail.getImageURL());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);

			this.contactRepository.save(contact);

			session.setAttribute("message", new Message("Your contact is updated.", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("name " + contact.getName());
		System.out.println("id " + contact.getcId());

		return "redirect:/user/" + "contact/" + contact.getcId();
	}

	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

	@GetMapping("/settings")
	public String openSettings(Model model) {
		model.addAttribute("title", "Settings");
		return "normal/settings";
	}

	// change password handler
	@PostMapping("/changepassword")
	public String changePassword(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		System.out.println("Current password " + currentPassword);
		System.out.println("New password " + newPassword);

		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword()); // encrypted

		if (this.bCryptPasswordEncoder.matches(currentPassword, currentUser.getPassword())) {
			// change the password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Password changed successfully.", "success"));
		} else {
			// error
			session.setAttribute("message", new Message("Current password is incorrect. Please try again.", "danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/dashboard";
	}

	@PostMapping("/deleteaccount")
	public String deleteAccount(Model model, Principal principal, HttpSession session) {
		try {
			String userName = principal.getName();
			User user = this.userRepository.getUserByUserName(userName);

			// Delete user's contacts
			for (Contact contact : user.getContacts()) {
				this.contactRepository.delete(contact);
			}

			// Delete user's feedback
			for (Feedback feedback : user.getFeedback()) {
				this.feedbackRepository.delete(feedback);
			}

			// Delete user's orders
			// List<MyOrder> userOrders = this.myOrderRepository.findByUser(user);
			// for (MyOrder order : userOrders) {
			// this.myOrderRepository.delete(order);
			// }

			// Delete the user
			this.userRepository.delete(user);

			session.invalidate();
			return "login";
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Error occurred while deleting account.", "danger"));
			return "redirect:/user/settings";
		}
	}

	@GetMapping("/sendmessage")
	public String message(Model model) {
		model.addAttribute("title", "Feedback Form");
		return "normal/feedback";
	}

	@PostMapping("/processmessage")
	public String sendMessage(Model model, @ModelAttribute Feedback feedback, Principal principal,
			HttpSession session) {
		model.addAttribute("title", "Feedback Sent");

		// Get the currently authenticated user
		// User user = userRepository.findById(currentUser.getId()).orElse(null);
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		if (user == null) {
			// Handle error: User not found
			return "normal/dashboard";
		}

		// Create a new message
		feedback = new Feedback(feedback.getSubject(), feedback.getMessage(), user);

		// Save the message to the database
		feedbackRepository.save(feedback);

		// success message
		session.setAttribute("message", new Message("Message sent successfully.", "success"));

		// return "normal/feedback";
		return "redirect:/user/dashboard";
	}

	// @PostMapping("/createOrder")
	// @ResponseBody
	// public String createOrder(@RequestBody Map<String, Object> data, Principal
	// principal) throws Exception {
	// System.out.println(data);
	//
	// int amount = Integer.parseInt(data.get("amount").toString());
	//
	// var client = new RazorpayClient("rzp_test_KxOMePttMoP5hn",
	// "imFP9Z25v1B8rFIy3NbGkleV");
	//
	// JSONObject jsonObject = new JSONObject();
	// jsonObject.put("amount", amount * 100); // amount in paise
	// jsonObject.put("currency", "INR");
	// jsonObject.put("receipt", "txn_235425");
	//
	// Order order = client.orders.create(jsonObject);
	// System.out.println(order);
	//
	// // Save order details to the database or perform other actions
	//// System.out.println("a");
	// MyOrder myOrder = new MyOrder();
	// myOrder.setAmount(order.get("amount") + "");
	// myOrder.setOrderId(order.get("id"));
	// myOrder.setPaymentId(null);
	// myOrder.setStatus("created");
	// myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
	// myOrder.setReceipt(order.get("receipt"));
	//
	// this.myOrderRepository.save(myOrder);
	//
	// return order.toString();
	//
	//// return "done";
	// }
	//
	// @PostMapping("/updateOrder")
	// public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {
	// MyOrder myOrder =
	// this.myOrderRepository.findByOrderId(data.get("order_id").toString());
	//
	// myOrder.setPaymentId(data.get("payment_id").toString());
	// myOrder.setStatus(data.get("status").toString());
	// this.myOrderRepository.save(myOrder);
	//
	// System.out.println(data);
	//
	// return ResponseEntity.ok(Map.of("msg", "updated"));
	// }

	// Create order for payment
	@PostMapping("/create-order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) throws Exception {
		System.out.println(data);
		double amount = Integer.parseInt(data.get("amount").toString());

		RazorpayClient client = new RazorpayClient("rzp_test_KxOMePttMoP5hn", "imFP9Z25v1B8rFIy3NbGkleV");

		JSONObject options = new JSONObject();
		options.put("amount", amount * 100); // amount in paise
		options.put("currency", "INR");
		options.put("receipt", "txn_1234");

		Order order = client.orders.create(options);
		System.out.println(order);

		Map<String, Object> response = new HashMap<>();
		response.put("id", order.get("id"));
		response.put("amount", order.get("amount"));
		response.put("currency", order.get("currency"));
		response.put("status", "created");

		return new ObjectMapper().writeValueAsString(response);
	}

}
