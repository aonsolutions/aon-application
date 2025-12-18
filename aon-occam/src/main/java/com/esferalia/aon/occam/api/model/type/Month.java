package com.esferalia.aon.occam.api.model.type;

import java.util.Optional;


public enum Month {

	JANUARY("Enero"),
	FEBRUARY("Febrero"),
	MARCH("Marzo"),
	APRIL("Abril"),
	MAY("Mayo"),
	JUNE("Junio"),
	JULY("Julio"),
	AUGUST("Agosto"),
	SEPTEMBER("Septiembre"),
	OCTOBER("Octubre"),
	NOVEMBER("Noviembre"),
	DECEMBER("Diciembre");
    
    private String name;
    
    private Month(String name) {
		this.name = name;
	}

    public String getName() {
		return this.name;
    }
    
	public byte value() {
		return (byte) ordinal();
	}

    public static Optional<Month> safeValueOf(Integer i){
    	if (i == null) return Optional.empty();
    	if (i<0 || i >= Month.values().length) return Optional.empty();
    	return Optional.of(Month.values()[i]);
    }
    
    public static Optional<Integer> value(Month month){
    	if (month == null) return Optional.empty();
		return Optional.of(month.ordinal());
    }
    	    
}