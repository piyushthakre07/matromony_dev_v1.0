package com.app.module.otp.service.impl;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class OtpService {

	 public String generateOTP(int length) {
	      String numbers = "1234567890";
	      Random random = new Random();
	      char[] otp = new char[length];

	      for(int i = 0; i< length ; i++) {
	         otp[i] = numbers.charAt(random.nextInt(numbers.length()));
	      }
	     
	      return new String(otp);
	   }

}
