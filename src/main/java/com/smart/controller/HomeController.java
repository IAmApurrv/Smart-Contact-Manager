package com.smart.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model, HttpSession session) {
		model.addAttribute("title", "Signup - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	// handler for user signup
	@PostMapping("/dosignup")
	public String doSignUp(@Valid @ModelAttribute("user") User user,
			@RequestParam("profileImage") MultipartFile multiPartFile, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {
			if (!agreement) {
				System.out.println("You havn't agreed to t&c.");
				throw new Exception("You havn't agreed to t&c."); // to go in catch block
			}
			if (bindingResult.hasErrors()) {
				System.out.println("Error " + bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			// user.setImageURL("profile.png");
			if (multiPartFile.isEmpty()) {
				System.out.println("file is Empty");
				user.setImageURL("defaultcontactpic.png");
			} else {

				// String[] split = contact.getEmail().split("@");
				user.setImageURL(multiPartFile.getOriginalFilename());

				File file = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(file.getAbsolutePath() + File.separator + multiPartFile.getOriginalFilename());

				Files.copy(multiPartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}
			// user.setAbout(user.getAbout().trim());
			// password encode
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement " + agreement);
			System.out.println("User " + user);
			User result = this.userRepository.save(user);
			System.out.println(result);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Signup successful !", "alert-success"));
			return "login";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong ! " + e.getMessage(), "alert-danger"));
			return "signup";
		}

	}

	@GetMapping("/login")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}

	// @GetMapping("/logout")
	// public String logout(HttpServletRequest request, HttpServletResponse
	// response) {
	// HttpSession session = request.getSession(false);
	// if (session != null) {
	// session.invalidate(); // Invalidate the session
	// }
	// return "redirect:/";
	// }

	// @PostMapping("/dologin")
	// public String doLogin(String email, String password, Model model) {
	// // Retrieve user from the database based on the provided username
	// User user = userRepository.findByEmail(email);
	//
	// if (user != null && user.getPassword().equals(password)) {
	// // Successful login
	// return "redirect:/dashboard";
	// } else {
	// // Failed login
	// model.addAttribute("error", "Invalid username or password");
	// return "login";
	// }
	// }

}
