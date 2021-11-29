package com.esferalia.aon.occam.api.model.finance;

import java.awt.Color;

import com.esferalia.aon.watson.util.AonStringUtils;

public class PrintInvoiceThemeConfiguration {

	PrintInvoiceTheme theme;
	
	String textColor;
	String customerBackgroundColor;
	String titleTextColor;
	
	String boxTitleBackgroundColor;
	String boxTitleTextColor;	
	String boxBodyBackgroundColor;

	
	public PrintInvoiceThemeConfiguration() {
		this.theme = PrintInvoiceTheme.BLACK_AND_WHITE;
	}
	
	public PrintInvoiceThemeConfiguration(PrintInvoiceTheme theme) {
		this.theme = theme;
	}
	
	public boolean isBlackAndWhite() {
		return PrintInvoiceTheme.BLACK_AND_WHITE.equals(getTheme());
	}
	
	public boolean isAonBlue() {
		return PrintInvoiceTheme.AON_BLUE.equals(getTheme());
	}
	
	public boolean isPersonalized() {
		return PrintInvoiceTheme.PERSONALIZED.equals(getTheme());
	}
	
	public PrintInvoiceTheme getTheme() {
		return theme;
	}
	
	public PrintInvoiceThemeConfiguration setTheme(PrintInvoiceTheme theme) {
		this.theme = theme;
		return this;
	}
	
	public String getTextColorHTML() {
		if(AonStringUtils.isBlank(textColor) || isBlackAndWhite() || isAonBlue()) return "#404040";
		else return textColor;
	}
	
	public Color getTextColor() {
		if(AonStringUtils.isBlank(textColor) || isBlackAndWhite() || isAonBlue()) return new Color(0x404040);
		else return new Color(Integer.parseInt(textColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setTextColor(String textColor) {
		this.textColor = textColor;
		return this;
	}

	public String getCustomerBackgroundColorHTML() {
		if(isAonBlue()) return "#CDDBF3";
		else if(AonStringUtils.isBlank(customerBackgroundColor) || isBlackAndWhite()) return "#E7E7E7";
		else return customerBackgroundColor; 
	}
	
	public Color getCustomerBackgroundColor() {
		if(isAonBlue()) return new Color(0xCDDBF3);
		else if(AonStringUtils.isBlank(customerBackgroundColor) || isBlackAndWhite()) return new Color(0xE7E7E7);
		else return new Color(Integer.parseInt(customerBackgroundColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setCustomerBackgroundColor(String customerBackgroundColor) {
		this.customerBackgroundColor = customerBackgroundColor;
		return this;
	}

	public String getBoxTitleBackgroundColorHTML() {
		if(isAonBlue()) return "#002469";
		else if(AonStringUtils.isBlank(boxTitleBackgroundColor) || isBlackAndWhite()) return "#404040";
		else return boxTitleBackgroundColor; 
	}
	
	public Color getBoxTitleBackgroundColor() {
		if(isAonBlue()) return new Color(0x002469);
		else if(AonStringUtils.isBlank(boxTitleBackgroundColor) || isBlackAndWhite()) return new Color(0x404040);
		else return new Color(Integer.parseInt(boxTitleBackgroundColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setBoxTitleBackgroundColor(String boxTitleBackgroundColor) {
		this.boxTitleBackgroundColor = boxTitleBackgroundColor;
		return this;
	}

	public String getBoxTitleTextColorHTML() {
		if(AonStringUtils.isBlank(boxTitleTextColor) || isBlackAndWhite() || isAonBlue()) return "#ffffff";
		else return boxTitleTextColor; 
	}
	
	public Color getBoxTitleTextColor() {
		if(AonStringUtils.isBlank(boxTitleTextColor) || isBlackAndWhite() || isAonBlue()) return new Color(0xffffff);
		else return new Color(Integer.parseInt(boxTitleTextColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setBoxTitleTextColor(String boxTitleTextColor) {
		this.boxTitleTextColor = boxTitleTextColor;
		return this;
	}

	public String getBoxBodyBackgroundColorHTML() {
		if(AonStringUtils.isBlank(boxBodyBackgroundColor) || isBlackAndWhite() || isAonBlue()) return "#ffffff";
		else return boxBodyBackgroundColor; 
	}
	
	public Color getBoxBodyBackgroundColor() {
		if(AonStringUtils.isBlank(boxBodyBackgroundColor) || isBlackAndWhite() || isAonBlue()) return new Color(0xffffff);
		else return new Color(Integer.parseInt(boxBodyBackgroundColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setBoxBodyBackgroundColor(String boxBodyBackgroundColor) {
		this.boxBodyBackgroundColor = boxBodyBackgroundColor;
		return this;
	}

	public String getTitleTextColorHTML() {
		if(AonStringUtils.isBlank(titleTextColor) || isBlackAndWhite() || isAonBlue()) return "#404040";
		else return titleTextColor; 
	}
	
	public Color getTitleTextColor() {
		if(AonStringUtils.isBlank(titleTextColor) || isBlackAndWhite() || isAonBlue()) return new Color(0x404040);
		else return new Color(Integer.parseInt(titleTextColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setTitleTextColor(String titleTextColor) {
		this.titleTextColor = titleTextColor;
		return this;
	}
	
}
