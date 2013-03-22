package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Salary implements Serializable {
	
	
	public enum Type {
		SALARY,
		EXTRA,
		SETTLE,
		DELAY,
		NOT_ENJOYED_VACATIONS;
		
		
		public String getDescription(){
			return DESCRIPTIONS.get(this);
		}

		static Map<Type, String> DESCRIPTIONS = 
				new HashMap<Salary.Type, String>() {
			{
				put(SALARY,"Nomina");
				put(EXTRA,"Extra");
				put(SETTLE,"Finiquito");
				put(DELAY,"Atrasos");
				put(NOT_ENJOYED_VACATIONS,"Vacaciones no disfrutadas");
			}
		};
		
	}

	private int id ;
	
	private Type type;
	
	private Date startDate;
	private Date endDate;
	
	private Date issueDate;
	private Date chargeDate;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}


	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Date getChargeDate() {
		return chargeDate;
	}

	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}
	
	
	
	
	
}
