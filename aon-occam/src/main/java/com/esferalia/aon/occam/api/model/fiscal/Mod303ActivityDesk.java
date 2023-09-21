package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod303ActivityDesk implements Serializable {

	private static final long serialVersionUID = 6080727858224392417L;
	
	private int desks;				// Mesas - Mesas
	private int deskCapacity;		// Mesas - Capacidad
	private int deskDays;			// Mesas - Días (4T)
	
	
	public int getDesks() {
		return desks;
	}
	public Mod303ActivityDesk setDesks(int desks) {
		this.desks = desks;
		return this;
	}

	public int getDeskCapacity() {
		return deskCapacity;
	}
	public Mod303ActivityDesk setDeskCapacity(int deskCapacity) {
		this.deskCapacity = deskCapacity;
		return this;
	}

	public int getDeskDays() {
		return deskDays;
	}
	public Mod303ActivityDesk setDeskDays(int deskDays) {
		this.deskDays = deskDays;
		return this;
	}

	public static Mod303ActivityDesk clone(Mod303ActivityDesk toClone) {
		return new Mod303ActivityDesk()
			.setDesks(toClone.getDesks())
			.setDeskCapacity(toClone.getDeskCapacity())
			.setDeskDays(toClone.getDeskDays()) 
			;
	}
	

}
