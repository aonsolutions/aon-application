package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.type.TypeVisitor;

public class Salary implements Serializable {
	
	public static interface TypeVisitor<E> {

		E visitSalary(Type type);

		E visitExtra(Type type);

		E visitSettle(Type type);

		E visitDelay(Type type);

		E visitNotEnjoyedVacations(Type type);

	}
	
	public static enum Type implements HasDescription{
		SALARY {
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitSalary(this);
			}
		},
		EXTRA{
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitExtra(this);
			}
		},
		SETTLE {
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitSettle(this);
			}
		},
		DELAY {
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitDelay(this);
			}
		},
		NOT_ENJOYED_VACATIONS{
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitNotEnjoyedVacations(this);
			}
		};
		
		
		public String getDescription(){
			return DESCRIPTIONS.get(this);
		}
		
		public abstract <E> E accept(TypeVisitor<E>  visitor);

		static Map<Type, String> DESCRIPTIONS = 
				new HashMap<Salary.Type, String>() {
			{
				put(SALARY,"Nomina");
				put(EXTRA,"Extra");
				put(SETTLE,"Finiquito");
				put(DELAY,"Atrasos");
				put(NOT_ENJOYED_VACATIONS,"Vacaciones");
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
