package com.esferalia.aon.occam.api.model.finance;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.watson.server.io.AonIOUtils;

public class PrintInvoiceConfiguration {
	
	private Integer header;
	private Integer footer;
	private boolean detailed;
	private boolean adjustImage;
	private boolean logo;
	private boolean company;
	private Attach background;
	private boolean recordData;
	private boolean contactData;
	private int border;
	private AonLanguage language;
	private String legal;
	
	private PrintInvoiceThemeConfiguration theme;

	@Deprecated
	public InputStream getBackgroundImage() {
		return background.getData() != null ? new ByteArrayInputStream(background.getData()) : null;
	}
	
	@Deprecated
	public PrintInvoiceConfiguration setBackgroundImage(InputStream is) {
		if(background == null) background = new Attach();
		try {
			byte[] data = AonIOUtils.toByteArray(is);
			background.setData(data); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public boolean isBackground() {
		return getBackground() != null && !getBackground().isEmpty();
	}
	
	public Attach getBackground() {
		return background;
	}
	
	public PrintInvoiceConfiguration setBackground(Attach background) {
		this.background = background;
		return this;
	}
	
	public Integer getHeader() {
		if(header == null) header = 100;
		return header;
	}

	public PrintInvoiceConfiguration setHeader(Integer header) {
		this.header = header;
		return this;
	}

	public Integer getFooter() {
		if(footer == null) footer = 100;
		return footer;
	}

	public PrintInvoiceConfiguration setFooter(Integer footer) {
		this.footer = footer;
		return this;	
	}

	public boolean isDetailed() {
		return detailed;
	}
	
	public PrintInvoiceConfiguration setDetailed(Boolean detailed) {
		this.detailed = detailed;
		return this;
	}

	public boolean getAdjustImage() {
		return adjustImage;
	}

	public PrintInvoiceConfiguration setAdjustImage(Boolean adjustImage) {
		this.adjustImage = adjustImage;
		return this;
	}
	
	public boolean isLogo() {
		return logo;
	}
	
	public PrintInvoiceConfiguration setLogo(boolean logo) {
		this.logo = logo;
		return this;
	}
	
	public boolean isCompany() {
		return company;
	}
	
	public PrintInvoiceConfiguration setCompany(boolean company) {
		this.company = company;
		return this;
	}
	
	public boolean isRecordData() {
		return recordData;
	}
	
	public PrintInvoiceConfiguration setRecordData(boolean recordData) {
		this.recordData = recordData;
		return this;
	}
	
	public boolean isContactData() {
		return contactData;
	}
	
	public PrintInvoiceConfiguration setContactData(boolean contactData) {
		this.contactData = contactData;
		return this;
	}
	
	public AonLanguage getLanguage() {
		if(language == null) language = AonLanguage.SPANISH;
		return language;
	}
	
	public PrintInvoiceConfiguration setLanguage(AonLanguage language) {
		this.language = language;
		return this;
	}

	public int getBorder() {
		return border;
	}
	
	public boolean isBoxBodyBorder() {
		return border == 2;
	}
	
	public boolean isBoxTitleBorder() {
		return border == 1 || border == 2;
	}

	public PrintInvoiceConfiguration setBorder(int border) {
		this.border = border;
		return this;
	}

	public PrintInvoiceThemeConfiguration getTheme() {
		if(theme == null) theme = new PrintInvoiceThemeConfiguration();
		return theme;
	}
	
	public PrintInvoiceConfiguration setTheme(PrintInvoiceThemeConfiguration theme) {
		this.theme = theme;
		return this;
	}
	
	public String getLegal() {
		return legal;
	}
	
	public PrintInvoiceConfiguration setLegal(String legal) {
		this.legal = legal;
		return this;
	}
}
