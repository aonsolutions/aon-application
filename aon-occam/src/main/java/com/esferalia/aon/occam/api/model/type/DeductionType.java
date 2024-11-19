package com.esferalia.aon.occam.api.model.type;

public enum DeductionType {

	COMMON_CONTINGENCY {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitCommonContigency(this);
		}
	},
	PROFESSIONAL_CONTINGENCY {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitProfessionalContigency(this);
		}
	},
	UNEMPLOYMENT {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitUnemployent(this);
		}
	},
	JOB_TRAINING {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitJobTraining(this);
		}
	},
	STRUCTURAL_OVERTIME {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitStructuralOvertime(this);
		}
	},
	NON_STRUCTURAL_OVERTIME {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitNonStructuralOvertime(this);
		}
	},
	IRPF {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitIrpf(this);
		}
	},
	ADVANCE_PAYMENT {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitAdvancePayment(this);
		}
	},
	IN_KIND {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitInkind(this);
		}
	},
	OTHER {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitOther(this);
		}
	},
	FOGASA // TODO: After Professional Contingency?
	{
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitFogasa(this);
		}

	},
	IT // ¿ EMBARGO ?
	{
		@Override
		public <T> T accept(Visitor<T> visitor) {
			T t = visitor.visitIT(this);
			if ( t == null ) {
				t = visitor.visitEmbargo(this);
			}
			return t ;
		}

	},
	IMS  // ¿ BONUS ?
	{
		@Override
		public <T> T accept(Visitor<T> visitor) {
			T t = visitor.visitIMS(this);
			if ( t == null ) {
				t = visitor.visitBonus(this);
			}
			return t ;
		}

	},
	MEI {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitMEI(this);
		}
	},
	SOLIDARITY {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSolidarity(this);
		}
		
	}
	;
	
	public static DeductionType BONUS = IMS;
	public static DeductionType EMBARGO = IT;

	public abstract <T> T accept(Visitor<T> visitor);
	

	public static interface Visitor<T> {

		default T visitCommonContigency(DeductionType deductionType) {
			return null;
		}

		default T visitProfessionalContigency(DeductionType deductionType) {
			return null;
		}

		default T visitUnemployent(DeductionType deductionType) {
			return null;
		}

		default T visitJobTraining(DeductionType deductionType) {
			return null;
		}

		default T visitStructuralOvertime(DeductionType deductionType) {
			return null;
		}

		default T visitNonStructuralOvertime(DeductionType deductionType) {
			return null;
		}

		default T visitIrpf(DeductionType deductionType) {
			return null;
		}

		default T visitAdvancePayment(DeductionType deductionType) {
			return null;
		}

		default T visitInkind(DeductionType deductionType) {
			return null;
		}

		default T visitOther(DeductionType deductionType) {
			return null;
		}

		default T visitFogasa(DeductionType deductionType) {
			return null;
		}

		default T visitIT(DeductionType deductionType) {
			return null;
		}
		default T visitIMS(DeductionType deductionType) {
			return null;
		}

		default T visitEmbargo(DeductionType deductionType) {
			return null;
		}
		default T visitBonus(DeductionType deductionType) {
			return null;
		}
		default  T visitMEI(DeductionType deductionType) {
			return null;
		}
		
		T visitSolidarity(DeductionType deductionType);

	}
	
	@SuppressWarnings("serial")
	public static class NullDeductionException extends Exception{
		
	}
	@SuppressWarnings("serial")
	public static class UnknownDeductionException extends Exception{
		
	}
	
	public static DeductionType deductionTypeOf(Byte value) throws NullDeductionException, UnknownDeductionException{
		if ( value == null )
			throw new NullDeductionException();
		if ( value < 0 )
			throw new UnknownDeductionException();

		DeductionType types [] = DeductionType.values();
		if ( value >= types.length )
			throw new UnknownDeductionException();
		
		return types[value] ;
	}

}
