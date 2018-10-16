package com.esferalia.aon.gwt.common.shared;

import java.io.Serializable;

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
		if(null == this.socialSecurity)
			return true;
		
		String controlCode = this.socialSecurity.substring(10, 12);;
		String ssNumberWithoutCode = this.socialSecurity.substring(0, 10);
		long ssNumber = Long.parseLong(ssNumberWithoutCode);
		
		long calculateControlCode = ssNumber % 97;
		String calculateControlCodeStr = String.valueOf(calculateControlCode);
		
		if(calculateControlCodeStr.length() == 1)
			calculateControlCodeStr = "0"+calculateControlCodeStr;
		
		return (controlCode == calculateControlCodeStr) ? true : false;
	}
	
}
