package com.esferalia.aon.occam.api.model.finance;

import java.awt.Color;

import com.esferalia.aon.watson.util.AonStringUtils;

public class PrintInvoiceThemeConfiguration {

	PrintInvoiceTheme theme;
	
	String textColor;
	String customerBackgroundColor;
	
	String boxTitleBackgroundColor;
	String boxTitleTextColor;
	boolean boxTitleBorder;
	
	String boxBodyBackgroundColor;
	String boxBodyTextColor;
	boolean boxBodyBorder;
	
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
	
	public Color getTextColor() {
		if(AonStringUtils.isBlank(textColor) || isBlackAndWhite() || isAonBlue()) return new Color(0x404040);
		else return new Color(Integer.parseInt(textColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setTextColor(String textColor) {
		this.textColor = textColor;
		return this;
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

	public Color getBoxTitleBackgroundColor() {
		if(isAonBlue()) return new Color(0x002469);
		else if(AonStringUtils.isBlank(boxTitleBackgroundColor) || isBlackAndWhite()) return new Color(0x404040);
		else return new Color(Integer.parseInt(boxTitleBackgroundColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setBoxTitleBackgroundColor(String boxTitleBackgroundColor) {
		this.boxTitleBackgroundColor = boxTitleBackgroundColor;
		return this;
	}

	public Color getBoxTitleTextColor() {
		if(AonStringUtils.isBlank(boxTitleTextColor) || isBlackAndWhite() || isAonBlue()) return new Color(0xffffff);
		else return new Color(Integer.parseInt(boxTitleTextColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setBoxTitleTextColor(String boxTitleTextColor) {
		this.boxTitleTextColor = boxTitleTextColor;
		return this;
	}

	public boolean isBoxTitleBorder() {
		if(isBlackAndWhite() || isAonBlue()) return false;
		else return boxTitleBorder;
	}

	public PrintInvoiceThemeConfiguration setBoxTitleBorder(boolean boxTitleBorder) {
		this.boxTitleBorder = boxTitleBorder;
		return this;
	}

	public Color getBoxBodyBackgroundColor() {
		if(AonStringUtils.isBlank(boxBodyBackgroundColor) || isBlackAndWhite() || isAonBlue()) return new Color(0xffffff);
		else return new Color(Integer.parseInt(boxBodyBackgroundColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setBoxBodyBackgroundColor(String boxBodyBackgroundColor) {
		this.boxBodyBackgroundColor = boxBodyBackgroundColor;
		return this;
	}

	public Color getBoxBodyTextColor() {
		if(AonStringUtils.isBlank(boxBodyTextColor) || isBlackAndWhite() || isAonBlue()) return new Color(0x404040);
		else return new Color(Integer.parseInt(boxBodyTextColor.replaceFirst("#", ""), 16)); 
	}

	public PrintInvoiceThemeConfiguration setBoxBodyTextColor(String boxBodyTextColor) {
		this.boxBodyTextColor = boxBodyTextColor;
		return this;
	}

	public boolean isBoxBodyBorder() {
		if(isBlackAndWhite() || isAonBlue()) return false;
		else return boxBodyBorder;
	}

	public PrintInvoiceThemeConfiguration setBoxBodyBorder(boolean boxBodyBorder) {
		this.boxBodyBorder = boxBodyBorder;
		return this;
	}
	
}
