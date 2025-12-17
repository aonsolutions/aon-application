package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod303ActivityOven implements Serializable {

	private static final long serialVersionUID = 2436844329460375427L;
	
	private int ovenSurface;	// Superficie del horno - Superficie (dm2)
	private int ovenDays;		// Superficie del horno - Días
	
	public int getOvenSurface() {
		return ovenSurface;
	}
	public Mod303ActivityOven setOvenSurface(int ovenSurface) {
		this.ovenSurface = ovenSurface;
		return this;
	}
	
	public int getOvenDays() {
		return ovenDays;
	}
	public Mod303ActivityOven setOvenDays(int ovenDays) {
		this.ovenDays = ovenDays;
		return this;
	}
	
	public static Mod303ActivityOven clone(Mod303ActivityOven toClone) {
		return new Mod303ActivityOven()
					.setOvenSurface(toClone.getOvenSurface())
					.setOvenDays(toClone.getOvenDays())
					;
	}

}
