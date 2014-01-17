package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.Mod390DetailKey;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;

@SuppressWarnings("serial")
public class Mod390Detail implements Serializable, IsSerializable {

	public static final ProvidesKey<Mod390Detail> PROVIDES_KEY = new ProvidesKey<Mod390Detail>() {
		@Override
		public Object getKey(Mod390Detail mod390Detail) {
			return mod390Detail == null ? null : mod390Detail.getId();
		}
	};

	private Integer id;
	private Mod390DetailKey key;
	private double taxableBase;
	private double percent;
	private double quota;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Mod390DetailKey getKey() {
		return key;
	}
	public void setKey(Mod390DetailKey key) {
		this.key = key;
	}

	public int getTaxableBaseBox() {
		
		// Unica excepcion en todo el modelo.	
		if (key == Mod390DetailKey.K33) return 639;
		
		return (key.getBox() - 1);
	}

	public double getTaxableBase() {
		return taxableBase;
	}

	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public int getBox() {
		return key.getBox();
	}

	public double getQuota() {
		return quota;
	}

	public void setQuota(double quota) {
		this.quota = quota;
	}

	public boolean isShowDescription() {
		return (key.getRowspan() > 0);
	}


}
