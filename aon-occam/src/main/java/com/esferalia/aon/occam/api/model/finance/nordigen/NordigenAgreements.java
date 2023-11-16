package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.LinkedList;

public class NordigenAgreements implements Serializable {
	
	private static final long serialVersionUID = 3317298827249852845L;
	
	private Integer count;
	private String next;
	private String previous;
	private LinkedList<NordigenAgreement> result;
	
	public Integer getCount() {
		return count;
	}
	public NordigenAgreements setCount(Integer count) {
		this.count = count;
		return this;
	}
	
	public String getNext() {
		return next;
	}
	public NordigenAgreements setNext(String next) {
		this.next = next;
		return this;
	}
	
	public String getPrevious() {
		return previous;
	}
	public NordigenAgreements setPrevious(String previous) {
		this.previous = previous;
		return this;
	}
	
	public LinkedList<NordigenAgreement> getResult() {
		return result;
	}
	public NordigenAgreements setResult(LinkedList<NordigenAgreement> result) {
		this.result = result;
		return this;
	}
	
}
