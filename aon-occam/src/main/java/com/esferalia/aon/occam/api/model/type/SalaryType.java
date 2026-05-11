package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

public enum SalaryType implements Serializable {

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
	
	
	public static interface TypeVisitor<E> {
		
		E visitSalary(SalaryType type);
	
		E visitExtra(SalaryType type);
	
		E visitSettle(SalaryType type);
	
		E visitDelay(SalaryType type);
	
		E visitProcedural(SalaryType type);

		default E visitL00(SalaryType type) { return visitSalary(type); };
	
		default E visitL02(SalaryType type) { return visitSalary(type); };

		default E visitL03(SalaryType type) { return visitDelay(type); };
	
		default E visitL13(SalaryType type) { return visitSettle(type); };

		E visitM190(SalaryType type) ;
	}

	

	
	public byte value() {
		return (byte) ordinal();
	}
	
	public <E> E accept( TypeVisitor<E> visitor) {
		throw new UnknownSalaryTypeException(this);
	}

	public static final Collection<Byte> SALARIES = Arrays.asList(
		SALARY.value(),
		EXTRA.value(),
		SETTLE.value(),
		DELAY.value()
	);

	public static final Collection<Byte> IRPF_SALARIES = Arrays.asList(
		SALARY.value(),
		EXTRA.value(),
		SETTLE.value(),
		DELAY.value(),
		M190.value()
	);
}
