package com.esferalia.aon.gwt.payroll.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Triplet<A, B, C> implements Serializable{
	private A id;
	private B name;
	private C surName;

	public Triplet() {
		super();
	}
	
	public Triplet(A id, B name, C surName) {
		super();
		this.id = id;
		this.name = name;
		this.surName = surName;
	}

	public int hashCode() {
		int hashId = id != null ? id.hashCode() : 0;
		int hashName = name != null ? name.hashCode() : 0;
		int hashSurName = surName != null ? surName.hashCode() : 0;
		
		return (hashId + hashName + hashSurName) 
				* hashId + hashName + hashSurName;
	}

	public boolean equals(Object other) {
    	if (other instanceof Triplet) {
    		Triplet<?, ?, ?> otherQuartet = (Triplet<?, ?, ?>) other;
    		return 
    		((  this.id == otherQuartet.id ||
    			( this.id != null && otherQuartet.id != null &&
    			  this.id.equals(otherQuartet.id))) &&
    		 (	this.name == otherQuartet.name ||
    			( this.name != null && otherQuartet.name != null &&
    			  this.name.equals(otherQuartet.name))) &&
    		 (	this.surName == otherQuartet.surName ||
	 			( this.surName != null && otherQuartet.surName != null &&
	 			  this.surName.equals(otherQuartet.surName)))
    		);
    	}

    	return false;
    }

	public String toString() {
		return "(Id: " + this.id + 
				", Name " + this.name + 
				", SurName: " + this.surName + ")";
	}

	public A getId() {
		return id;
	}

	public Triplet<A, B, C> setId(A id) {
		this.id = id;
		return this;
	}

	public B getName() {
		return name;
	}

	public Triplet<A, B, C> setName(B name) {
		this.name = name;
		return this;
	}
	
	public C getSurName() {
		return surName;
	}

	public Triplet<A, B, C> setSurName(C surName) {
		this.surName = surName;
		return this;
	}
}
