package com.app.module.otp.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.app.beans.OtpGenerationBean;
import com.app.module.otp.service.impl.OtpService;

@RestController
@RequestMapping(path = "/otp", produces = "application/json", consumes = "application/json")
public class OtpController {
	
	@Autowired
	OtpService otpService;

	@RequestMapping(value = "/generateOtp", method = RequestMethod.POST)
	public  ResponseEntity<?> generateOtp(@RequestBody OtpGenerationBean otpGenerationBean){
		String otp=otpService.generateOTP(otpGenerationBean.getLength());
		otpGenerationBean.setOtp(otp);
		
		return new ResponseEntity<OtpGenerationBean>(otpGenerationBean, HttpStatus.OK);
		
	}

}
