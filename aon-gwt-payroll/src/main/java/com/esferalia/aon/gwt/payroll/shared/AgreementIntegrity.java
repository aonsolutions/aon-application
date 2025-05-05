package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class AgreementIntegrity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;

	private List<Payment> agreementPayments;
	
	private HashMap<AgreementIntegrityFix, List<String>> messages;
	
	public AgreementIntegrity() {
		super();
	}
	
	public Integer getId() {
		return id;
	}

	public AgreementIntegrity setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public AgreementIntegrity setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public List<Payment> getAgreementPayments() {
		return agreementPayments;
	}

	public AgreementIntegrity setAgreementPayments(List<Payment> agreementPayments) {
		this.agreementPayments = agreementPayments;
		return this;
	}

	public HashMap<AgreementIntegrityFix, List<String>> getMessages() {
		return messages;
	}

	public AgreementIntegrity setMessages(HashMap<AgreementIntegrityFix, List<String>> messages) {
		this.messages = messages;
		return this;
	}
	
	public boolean hasMessages() {
		if(null == messages) return false;
		
		for(List<String> messageIT : messages.values())
			if(!messageIT.isEmpty()) return true;
		
		return false;
	}

	
	
}
