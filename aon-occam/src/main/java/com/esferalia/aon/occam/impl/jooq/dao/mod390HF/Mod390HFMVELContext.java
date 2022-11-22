package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.impl.jooq.dao.ModelMVELContext;

public class Mod390HFMVELContext extends ModelMVELContext implements Map<String, Object> {
	
	private Mod390HF mod;
	
	public Mod390HFMVELContext(Mod390HF mod) {
		this.mod = mod;
	}
	
	public Mod390HF getMod390HF() {
		return mod;
	}
	public boolean isToCompensate() {
		return this.mod.isToCompensate();
	}
	public boolean isToDeposit() {
		return this.mod.isToDeposit();
	}
	public boolean isToPayback() {
		return this.mod.isToPayback();
	}

}
