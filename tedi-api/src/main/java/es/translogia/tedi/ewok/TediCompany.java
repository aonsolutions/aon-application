package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediCompany implements Serializable{

	private static final long serialVersionUID = 8685884818179111880L;
	
	private String document;
	private String name;
	private String alias;
	private Boolean active;
	private TediPlan plan;
	private String iban;
	private String bic;
	private TediAddress address;

	public String getDocument() {
		return document;
	}

	public TediCompany setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public TediCompany setName(String name) {
		this.name = name;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public TediCompany setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public Boolean getActive() {
		return active;
	}

	public TediCompany setActive(Boolean active) {
		this.active = active;
		return this;
	}

	public TediAddress getAddress() {
		return address;
	}

	public TediCompany setAddress(TediAddress address) {
		this.address = address;
		return this;
	}

	public TediPlan getPlan() {
		return plan;
	}

	public TediCompany setPlan(TediPlan plan) {
		this.plan = plan;
		return this;
	}

	public String getIban() {
		return iban;
	}

	public TediCompany setIban(String iban) {
		this.iban = iban;
		return this;
	}

	public String getBic() {
		return bic;
	}

	public TediCompany setBic(String bic) {
		this.bic = bic;
		return this;
	}

	
}
