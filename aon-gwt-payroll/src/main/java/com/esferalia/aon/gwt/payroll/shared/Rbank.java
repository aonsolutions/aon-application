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
	private String bankAlias;
	
	public Rbank() {
		super();
	}
	
	public Rbank(Integer id, String iban, String bic, String bankAlias) {
		this.id = id;
		this.iban = iban;
		this.bic = bic;
		this.bankAlias = bankAlias;
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
	
	public String getBankAlias() {
		return bankAlias;
	}

}
