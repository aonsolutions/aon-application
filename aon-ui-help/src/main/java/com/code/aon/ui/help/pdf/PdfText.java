package com.code.aon.ui.help.pdf;

import com.code.aon.ui.help.pdf.items.TextType;

public class PdfText {

	private String text;
	private double x,y;
	private TextType type;
	private int page;
	
	public PdfText(String text, int page) {
		this.text = text;
		this.page = page;
	}
	
	public void appendText(String text) {
		if(this.text == null) {
			this.text = text;
			return;
		}
		this.text += text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getX() {
		return x;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	public int getPage() {
		return page;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		return "PdfText [" + page + "] [" + (int)x + ":" + (int)y + "] " + text ;
	}
	
	public TextType getType() {
		return type;
	}
	
	public void setType(TextType type) {
		this.type = type;
	}
	
	
	
}
