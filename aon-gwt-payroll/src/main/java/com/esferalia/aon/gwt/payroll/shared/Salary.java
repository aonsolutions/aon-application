package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.salary.enumeration.SalaryTypeVisitor;

public class  Salary implements Serializable {
	
	public static interface TypeVisitor<E> {
		
		E visitSalary(Type type);

		E visitExtra(Type type);

		E visitSettle(Type type);

		E visitDelay(Type type);

		default E visitL00(Type type) { return visitSalary(type); };

		default E visitL03(Type type) { return visitDelay(type); };

		default E visitL13(Type type) { return visitSettle(type); };
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

		L00 
		{
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitL00(this);
			}
		},
		L03 
		{
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitL03(this);
			}
		},
		L13 
		{
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitL13(this);
			}
		}
		
		;
		
		
		public String getVariable(){
			return VARIABLES.get(this);
		}

		public String getDescription(){
			return DESCRIPTIONS.get(this);
		}
		
		public abstract <E> E accept(TypeVisitor<E>  visitor);


		static Map<Type, String> VARIABLES = 
				new HashMap<Salary.Type, String>() {
			{
				put(SALARY,"NOMINA");
				put(EXTRA,"EXTRA");
				put(SETTLE,"FINIQUITO");
				put(DELAY,"ATRASOS");
				put(L00,"L00");
				//put(SLD_RESULTS,null);

			}
		};
		
		static Map<Type, String> DESCRIPTIONS = 
				new HashMap<Salary.Type, String>() {
			{
				put(SALARY,"N\u00f3mina");
				put(EXTRA,"Extra");
				put(SETTLE,"Finiquito");
				put(DELAY,"Atrasos");
				put(L00,"Liquidaci\u00f3n Ordinaria L00");
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
	
	public <T extends Salary> T setId(int id) {
		this.id = id;
		return thix();
	}

	public Type getType() {
		return type;
	}
	
	public  <T extends Salary> T setType(Type type) {
		this.type = type;
		return thix();
	}


	public Date getStartDate() {
		return startDate;
	}

	public <T extends Salary> T  setStartDate(Date startDate) {
		this.startDate = startDate;
		return thix();
	}

	public Date getEndDate() {
		return endDate;
	}

	public <T extends Salary> T  setEndDate(Date endDate) {
		this.endDate = endDate;
		return thix();
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public <T extends Salary> T  setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return thix();
	}

	public Date getChargeDate() {
		return chargeDate;
	}

	public <T extends Salary> T  setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
		return thix();
	}
	
	// ------------------------------------------------------------------------
	
	private <T extends Salary> T thix() {
		return (T) this;
	}

	
	
	
}
