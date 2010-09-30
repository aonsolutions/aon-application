package com.code.aon.marketplace.plu;

public class Field {
	
	int start = 0;
	int end = 0;
	String type;

	public Field(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getLength() {
		int length = end - start;
		if (length <= 0) length = 1; 
		return length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
