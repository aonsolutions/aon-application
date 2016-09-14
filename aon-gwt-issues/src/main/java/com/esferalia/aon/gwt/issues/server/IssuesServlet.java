package com.esferalia.aon.gwt.issues.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.issues.client.IIssues;
import com.esferalia.aon.gwt.issues.shared.AonData;


public class IssuesServlet extends AonRemoteServiceServlet implements IIssues{

	// ------------------------------------------------------------------------

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getLoggedUser() {
		return AonServletUtils.getLoggedUser();
	}
	
	public AonData getAonData(String domainName){
		return new AonData().setLoggedUser(getLoggedUser())
				.setMd5(getMd5(getLoggedUser()+domainName));
	}
	
	public String getMd5(String str){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    md.update(str.getBytes());
	    byte byteData[] = md.digest();
	    //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
	     	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }       
        return sb.toString();
	}
}