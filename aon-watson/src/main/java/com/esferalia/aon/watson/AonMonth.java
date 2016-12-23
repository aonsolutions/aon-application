package com.esferalia.aon.watson;

public enum AonMonth {

	JANUARY("ENERO"),
	FEBRUARY("FEBRERO"),
	MARCH("MARZO"),
	APRIL("ABRIL"),
	MAY("MAYO"),
	JUNE("JUNIO"),
	JULY("JULIO"),
	AUGUST("AGOSTO"),
	SEPTEMBER("SEPTIEMBRE"),
	OCTOBER("OCTUBRE"),
	NOVEMBER("NOVIEMBRE"),
	DECEMBER("DICIEMBRE");

	String name;
	
	private AonMonth(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
