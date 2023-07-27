package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
public class SocialSecurity implements Serializable{
	
	private String socialSecurity;
	
	public SocialSecurity(){
		super();
	}
	
	public SocialSecurity(String socialSecurity){
		this.socialSecurity = socialSecurity;
	}
	
	public boolean checkSS(){
		if(AonStringUtils.isBlank(socialSecurity)) return false;
		
		String controlCode = this.socialSecurity.substring(10, 12);;
		String ssNumberWithoutCode = this.socialSecurity.substring(0, 10);
		try {
			long ssNumber = Long.parseLong(ssNumberWithoutCode);
			
			long calculateControlCode = ssNumber % 97;
			String calculateControlCodeStr = String.valueOf(calculateControlCode);
			
			if(calculateControlCodeStr.length() == 1)
				calculateControlCodeStr =  AonStringUtils.leftPad(calculateControlCodeStr, 2, '0');
			
			return AonStringUtils.equalsIgnoreCase(controlCode, calculateControlCodeStr);
		} catch (Exception e) {
			return false;
		}
	}
	
}
