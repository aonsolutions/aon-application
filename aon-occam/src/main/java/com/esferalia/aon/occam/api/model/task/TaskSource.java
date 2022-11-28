package com.esferalia.aon.occam.api.model.task;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum TaskSource {

	MANUAL,
	ASSIGNED,
	PROCESS,
	CAU,
	GITHUB,
	QUERY,
	REQUEST,
	GROUPED,
	TASK;

	public String getName() {
    	return this.toString().toLowerCase();
    }
	
    public byte value() {
    	return (byte) this.ordinal();
	}
    
    public static TaskSource valueOf(Integer index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static TaskSource valueOf(Byte index) {
    	if(index != null) {
    		return values()[index];
    	} 
    	return null;
    }
    
    public static TaskSource safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static TaskSource safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TaskSource.values().length) return null;
		return TaskSource.values()[i];
	}
	
	public static TaskSource safeValueOf(String name) {
		return valueNameOf(name);
	}
	
	public static TaskSource valueNameOf(String name) {
		if(AonStringUtils.isBlank(name)) return MANUAL;
		for(TaskSource p :TaskSource.values())
			if(name.equalsIgnoreCase(p.getName()) || name.equalsIgnoreCase(p.name()))
				return p;
		return MANUAL;
	}
	
	
	public String getESName() { // PROVISIONAL!!!!
		if(this.equals(QUERY)) return "Consulta";
		if(this.equals(CAU)) return "Call Center";
		if(this.equals(GROUPED)) return "Agrupadas";
		if(this.equals(REQUEST)) return "Tr\u00e1mites";
		if(this.equals(TASK)) return "Tarea";
		if(this.equals(MANUAL) || this.equals(ASSIGNED) || this.equals(PROCESS) || this.equals(GITHUB)) return getName();
		else return "";
    }
	
}
