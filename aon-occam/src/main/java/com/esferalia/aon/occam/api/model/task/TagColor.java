package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;

public enum TagColor implements Serializable{

	RED("B60205"),
	ORANGE("D93F0B"),
	YELLOW("FBCA04"),
	GREEN("0E8A16"),
	BLUE1("006B75"),
	BLUE2("1D76DB"),
	BLUE3("0052CC"),
	PURPLE("5319E7"),
	BLACK("000000");

	private String color;
	
	private TagColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
}
