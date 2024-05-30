package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	Random random = new Random(1000);

	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@GetMapping("/forgotemailform")
	public String openEmailForm(Model model) {
		model.addAttribute("title", "Email Form");
		return "forgot-email-form";
	}

	@PostMapping("/sendotp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		System.out.println("email : " + email);

		int otp = random.nextInt(999999);
		System.out.println("otp : " + otp);

		String from = "iamapurrv@gmail.com";
		String to = email;
		String subject = "OTP From SCM";
		String message = "" + "<div style='border:1px solid #e2e2e2; padding:20px'>" + "OTP IS" + "<h1>" + "<b>" + otp
				+ "</n>" + "</h1>" + "</div>";

		// boolean flag = this.emailService.sendEmail(subject, message, to, from);
		boolean flag = this.emailService.sendEmail(from, to, subject, message);

		if (flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			session.setAttribute("message", "OTP has been sent to your email.");
			return "verify-otp";
		} else {
			session.setAttribute("message", "Failed to send OTP. Please try again.");
			return "forgot-email-form";
		}
	}

	@GetMapping("/resendotp")
	public String resendOTP(HttpSession session) {
		String email = (String) session.getAttribute("email");
		int otp = random.nextInt(999999);
		String subject = "Resend OTP From SCM";
		String message = "<div style='border:1px solid #e2e2e2; padding:20px'>" + "Your new OTP is: " + "<h1>" + "<b>"
				+ otp + "</b>" + "</h1>" + "</div>";
		String from = "iamapurrv@gmail.com";
		boolean flag = this.emailService.sendEmail(subject, message, email, from);

		if (flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("message", "New OTP has been sent to your email.");
			return "verify-otp";
		} else {
			session.setAttribute("message", "Failed to resend OTP. Please try again.");
			return "verify-otp";
		}
	}

	@PostMapping("/verifyotp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
		int myOtp = (int) session.getAttribute("myotp");
		System.out.println("User OTP " + otp);
		System.out.println("Our OTP " + myOtp);

		String email = (String) session.getAttribute("email");

		if (myOtp == otp) {
			// password change form
			User user = this.userRepository.getUserByUserName(email);

			if (user == null) {
				session.setAttribute("message", "User does not exits with this email.");
				return "forgot-email-form";
			} else {

			}
			return "password-change-form";
		} else {
			session.setAttribute("message", "You have entered wrong OTP.");
			return "verify-otp";
		}
	}

	@PostMapping("/changepassword")
	public String changePassword(@RequestParam("newpassword") String newPassword, HttpSession session) {
		// String email = (String) session.getAttribute("email");
		// User user = this.userRepsitory.getUserByUserName(email);
		// user.setPassword(this.bcrypt.encode(newPassword));
		// this.userRepsitory.save(user);

		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bcrypt.encode(newPassword));
		this.userRepository.save(user);

		return "redirect:/login?change=password changed successfully.";
	}

}
