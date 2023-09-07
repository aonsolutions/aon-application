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
		if(AonStringUtils.isBlank(socialSecurity) || socialSecurity.length() != 12) return false;
		
		String provinceCode = this.socialSecurity.substring(0, 2);;
		String ssNumberWithoutCode = this.socialSecurity.substring(2, 10);
		String controlCode = this.socialSecurity.substring(10, 12);;
		
		try {
			long ssNumber = Long.parseLong(ssNumberWithoutCode);
			long province = Long.parseLong(provinceCode);
			
			if (ssNumber < 10000000) ssNumber = ssNumber + province * 10000000;
			else ssNumber = Long.parseLong(provinceCode + ssNumberWithoutCode);
				
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
