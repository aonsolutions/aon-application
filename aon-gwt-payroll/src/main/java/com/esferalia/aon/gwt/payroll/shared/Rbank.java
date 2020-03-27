package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class Rbank implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String iban;
	private String bic;
	
	public Rbank() {
		super();
	}
	
	public Rbank(Integer id, String iban, String bic) {
		this.id = id;
		this.iban = iban;
		this.bic = bic;
	}

	public Integer getId() {
		return id;
	}

	public String getIban() {
		return iban;
	}

	public String getBic() {
		return bic;
	}

}
