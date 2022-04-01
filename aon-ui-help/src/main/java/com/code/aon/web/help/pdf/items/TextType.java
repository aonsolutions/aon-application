package com.code.aon.web.help.pdf.items;

public enum TextType {

	TITLE_1(0, 14.04f), 
	TITLE_2(1, 12.96f),
	TITLE_3(2, 12f), 
	TITLE_4(3, 12f),
	TEXT(4, 11.04f),;

	private int index;
	private float fontSize;
	
	private TextType(int index, float fontSize) {
		this.index = index;
		this.fontSize = fontSize;
	}

	public static TextType getValue(float value) {
		for (TextType e : TextType.values()) {
			
			//System.out.println(value);
			if (e.fontSize == value) {
				return e;
			}
		}
		return null; // not found
	}
	
	public boolean greaterThan(TextType type) {
		return type.index < this.index;
	}

}
