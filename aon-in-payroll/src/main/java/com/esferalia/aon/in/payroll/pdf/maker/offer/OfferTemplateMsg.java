package com.esferalia.aon.in.payroll.pdf.maker.offer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;

public class OfferTemplateMsg extends Properties implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AonLanguage language;
	
	public OfferTemplateMsg(AonLanguage language) {
		this.language = language;
		
		if(AonLanguage.SPANISH.equals(language)) {
			getProperties("spanish.properties");
		} else if(AonLanguage.ENGLISH.equals(language)) {
			getProperties("english.properties");
		} else if(AonLanguage.DEUTSCH.equals(language)) {
			getProperties("deutsch.properties");
		} else if(AonLanguage.BASQUE.equals(language)) {
			getProperties("basque.properties");
		} else if(AonLanguage.CATALAN.equals(language)) {
			getProperties("catalan.properties");
		} else if(AonLanguage.GALICIAN.equals(language)) {
			getProperties("galician.properties");
		} else if(AonLanguage.VALENCIAN.equals(language)) {
		getProperties("valencian.properties");
		}
	}
	
	public AonLanguage getLanguage() {
		return language;
	}
	
	public void setLanguage(AonLanguage language) {
		this.language = language;
	}
		
	public String invoice() {
		return getProperty("invoice");
	}
	
	public String simplifiedInvoice() {
		return getProperty("simplifiedInvoice");
	}
	
	public String rectifiedInvoice() {
		return getProperty("rectifiedInvoice");
	}
	
	public String rectifies() {
		return getProperty("rectifies");
	}
	
	public String number() {
		return getProperty("number");
	}
	
	public String date() {
		return getProperty("date");
	}
	
	public String description() {
		return getProperty("description");
	}
	
	public String quantity() {
		return getProperty("quantity");
	}	
	
	public String price() {
		return getProperty("price");
	}
	
	public String discount() {
		return getProperty("discount");
	}
	
	public String amount() {
		return getProperty("amount");
	}
	
	public String base() {
		return getProperty("base");
	}
	
	public String type() {
		return getProperty("type");
	}
	
	public String quota() {
		return getProperty("quota");
	}
	
	public String totalInvoice() {
		return getProperty("totalInvoice");
	}
	
	public String payMethod() {
		return getProperty("payMethod");
	}
	
	public String bankAccount() {
		return getProperty("bankAccount");
	}
	
	public String web() {
		return getProperty("web");
	}
	
	public String phone() {
		return getProperty("phone");
	}
	
	public String mail() {
		return getProperty("mail");
	}
	
	public String notes() {
		return getProperty("notes");
		
	}
	
	private void getProperties(String path) {
        try {
            this.load(getClass().getResourceAsStream(path));

        } catch (IOException e) {
        	e.printStackTrace();
        }
   }
}
