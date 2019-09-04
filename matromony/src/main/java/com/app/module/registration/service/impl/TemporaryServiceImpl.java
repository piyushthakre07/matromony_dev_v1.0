package com.app.module.registration.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.app.beans.OtpGenerationBean;
import com.app.beans.TemporaryBean;
import com.app.model.OtpVerification;
import com.app.model.Temporary;
import com.app.module.registration.dao.ITemporaryDao;
import com.app.module.registration.service.ITemporaryService;

@Service
public class TemporaryServiceImpl implements ITemporaryService {

	@Autowired
	ITemporaryDao temporaryDao;
	
	@Autowired
	private Environment environment;
	
	@Value("${otp_expire_time_minute}")
	private Integer expireTime;
	
	@Value("${otp_registration_functionality}")
	private String otpRegistrationFunctionality;
	
	@Transactional
	public boolean saveTempDetails(TemporaryBean temporaryBean) {
		try {
			Temporary temporary = new Temporary();
			BeanUtils.copyProperties(temporary, temporaryBean);
			temporaryDao.save(temporary);
			 final String uri = environment.getRequiredProperty("generate_otp_url");
			 OtpGenerationBean OtpGenerationRequestBean=new OtpGenerationBean();
			 OtpGenerationRequestBean.setLength(Integer.parseInt(environment.getRequiredProperty("otp_length")));
			    RestTemplate restTemplate = new RestTemplate();
			    HttpHeaders headers = new HttpHeaders();
			    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			    HttpEntity<OtpGenerationBean> entity = new HttpEntity<OtpGenerationBean>(OtpGenerationRequestBean, headers);
			    ResponseEntity<OtpGenerationBean> result = restTemplate.exchange(uri, HttpMethod.POST, entity, OtpGenerationBean.class); 
			    OtpGenerationBean responseResult=result.getBody();
			    OtpVerification otpVerification=new OtpVerification();
			    otpVerification.setContactNumber(temporaryBean.getContactNumber());
			    otpVerification.setOtp(responseResult.getOtp());
			    otpVerification.setOtpSendingTime(new Date());
			    Date expireDate=increaseTime(expireTime);
			    otpVerification.setOtpExpiringTime(expireDate);
			    otpVerification.setCount(1);
			    otpVerification.setFunctionality(otpRegistrationFunctionality);
			    otpVerification.setIsVerified("N");
			    
			    temporaryDao.save(otpVerification);
			   
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static Date increaseTime(Integer timeIncreamentInMinute) throws ParseException {
		 SimpleDateFormat df = new SimpleDateFormat("dd:mm:yyyy HH:mm");
		 Date currentDate =new Date(); 
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(currentDate);
		 cal.add(Calendar.MINUTE, timeIncreamentInMinute);
		 Date date = df.parse(df.format(cal.getTime()));
		return date;
	}
	
	
}
