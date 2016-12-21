package com.esferalia.aon.watson;

public enum AonDayOfWeek {

	SUNDAY("DOMINGO"),
	MONDAY("LUNES"),
	TUESDAY("MARTES"),
	WEDNESDAY("MIERCOLES"),
	THURSDAY("JUEVES"),
	FRIDAY("VIERNES"),
	SATURDAY("SABADO");
	
	String name;
	private AonDayOfWeek(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
