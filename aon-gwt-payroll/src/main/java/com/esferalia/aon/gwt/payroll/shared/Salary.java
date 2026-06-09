package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.occam.api.model.type.SalaryType.TypeVisitor;

public class  Salary implements Serializable{
	
	public static interface TypeVisitor<E> {
		
		E visitSalary(Type type);

		E visitExtra(Type type);

		E visitSettle(Type type);

		E visitDelay(Type type);
		
		E visitProcedural(Type type);

		default E visitL00(Type type) { return visitSalary(type); };

		default E visitL02(Type type) { return visitDelay(type); };

		default E visitL03(Type type) { return visitDelay(type); };

		default E visitL13(Type type) { return visitSettle(type); };
		
		default E visitM190(Type type) { return visitSalary(type); };
	}
	
	public static class UnknownSalaryTypeException extends RuntimeException {
		
		private Type type;
		
		public UnknownSalaryTypeException(Type type) {
			this.type = type;
		}

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
		UNKNOWN_4,
		UNKNOWN_5,
		UNKNOWN_6,
		UNKNOWN_7,
		UNKNOWN_8,
		PROCEDURAL	// 9 
		{
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitProcedural(this);
			}
		},

		L00 
		{
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitL00(this);
			}
		},
		L02
		{
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitL02(this);
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
		},
		UNKNOWN_14,
		UNKNOWN_15,
		UNKNOWN_16,
		UNKNOWN_17,
		UNKNOWN_18,
		UNKNOWN_19,
		M190 
		{
			@Override
			public <E> E accept(TypeVisitor<E> visitor) {
				return visitor.visitM190(this);
			}
		}
		
		;
		
		
		public String getVariable(){
			return VARIABLES.get(this);
		}

		public String getDescription(){
			return DESCRIPTIONS.get(this);
		}
		
		public <E> E accept(TypeVisitor<E>  visitor) {
			throw new UnknownSalaryTypeException(this);
		}


		static Map<Type, String> VARIABLES = 
				new HashMap<Salary.Type, String>() {
			{
				put(SALARY,"NOMINA");
				put(EXTRA,"EXTRA");
				put(SETTLE,"FINIQUITO");
				put(DELAY,"ATRASOS");
				put(PROCEDURAL,"TRAMITACION");
				put(L00,"L00");
				put(L02,"L02");
				put(L03,"L03");
				put(L13,"L13");
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
				put(PROCEDURAL,"Sal. Tramitaci\u00f3n");
				put(L00,"Liquidaci\u00f3n ordinaria L00");
				put(L02,"Liquidaci\u00f3n complementaria L02");
				put(L03,"Liquidaci\u00f3n complementaria L03");
				put(L13,"Liquidaci\u00f3n complementaria L13");
			}
		};
		
		public static Type SALARIES [] = {SALARY, EXTRA, DELAY, SETTLE, PROCEDURAL};
		
		public static Byte [] LIQUIDATIONS = { (byte) L00.ordinal(), (byte) L13.ordinal(), (byte) L03.ordinal() }; 
		
	}

	private int id ;
	
	private Type type;
	
	private String startDate;
	private String endDate;
	
	private String issueDate;
	private String chargeDate;
	
	
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

	public  <T extends Salary> T setType(byte type) {
	    	try {
	    	    this.type = Type.values()[type];
	    	} catch (Exception e ) {
	    	    this.type = null;
	    	}
		return thix();
	}

	public Date getStartDate() {
		return parse(startDate);
	}

	public <T extends Salary> T  setStartDate(Date startDate) {
		this.startDate =format(startDate);
		return thix();
	}

	public Date getEndDate() {
		return parse(endDate);
	}

	public <T extends Salary> T  setEndDate(Date endDate) {
		this.endDate = format(endDate);
		return thix();
	}

	public Date getIssueDate() {
		return parse(issueDate);
	}

	public <T extends Salary> T  setIssueDate(Date issueDate) {
		this.issueDate = format(issueDate);
		return thix();
	}

	public Date getChargeDate() {
		return parse(chargeDate);
	}

	public <T extends Salary> T  setChargeDate(Date chargeDate) {
		this.chargeDate = format(chargeDate);
		return thix();
	}
	
	// ------------------------------------------------------------------------
	
	private <T extends Salary> T thix() {
		return (T) this;
	}

	// ------------------------------------------------------------------------

	
}
