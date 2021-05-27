package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable{

	private NumberVariable result;
	private List<Variable> context;

	public Result() {
		this(null,null);
	}
	public Result(NumberVariable result, List<Variable> context) {
		this.result = result;
		this.context = context;
	}
	
	public NumberVariable getResult() {
		return result;
	}

	public void setResult(NumberVariable result) {
		this.result = result;
	}
	
	public List<Variable> getContext() {
		return context;
	}
	
	public void setContext(List<Variable> context) {
		this.context = context;
	}
}
