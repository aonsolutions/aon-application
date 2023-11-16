package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.LinkedList;

public class NordigenRequisitions implements Serializable {
	
	private static final long serialVersionUID = 3317298827249852845L;
	
	private Integer count;
	private String next;
	private String previous;
	private LinkedList<NordigenRequisition> result;
	
	public Integer getCount() {
		return count;
	}
	public NordigenRequisitions setCount(Integer count) {
		this.count = count;
		return this;
	}
	
	public String getNext() {
		return next;
	}
	public NordigenRequisitions setNext(String next) {
		this.next = next;
		return this;
	}
	
	public String getPrevious() {
		return previous;
	}
	public NordigenRequisitions setPrevious(String previous) {
		this.previous = previous;
		return this;
	}
	
	public LinkedList<NordigenRequisition> getResult() {
		return result;
	}
	public NordigenRequisitions setResult(LinkedList<NordigenRequisition> result) {
		this.result = result;
		return this;
	}
	
}
