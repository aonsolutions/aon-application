package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod111 extends FiscalModel implements Serializable {
	
	public Mod111() {
		super();
		setModel(FiscalModelType.M111);
	}

	private static final long serialVersionUID = 3614782856588153510L;
}
