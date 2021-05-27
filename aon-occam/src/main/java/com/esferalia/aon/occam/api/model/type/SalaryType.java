package com.esferalia.aon.occam.api.model.type;

public enum SalaryType {

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
	
	
	public static interface TypeVisitor<E> {
		
		E visitSalary(SalaryType type);
	
		E visitExtra(SalaryType type);
	
		E visitSettle(SalaryType type);
	
		E visitDelay(SalaryType type);
	
		default E visitL00(SalaryType type) { return visitSalary(type); };
	
		default E visitL03(SalaryType type) { return visitDelay(type); };
	
		default E visitL13(SalaryType type) { return visitSettle(type); };
	}

	

	abstract <E> E accept( TypeVisitor<E> visitor); 
	
	public byte value() {
		return (byte) ordinal();
	}
}
