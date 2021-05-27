package com.esferalia.aon.occam.api.model.type;



public enum PaymentType {
    CRA_0000,
	CRA_0001,
	CRA_0002{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitNonStructuralHours(this);
		}
	},
	CRA_0003 {
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitStructuralHours(this);
		}
	},
	CRA_0004,
	CRA_0005,
	CRA_0006,
	CRA_0007,
	CRA_0008,
	CRA_0009,
	CRA_0010,
	CRA_0011,
	CRA_0012,
	CRA_0013{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0014{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0015{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0016{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0017{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0018{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0019{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0020{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0021{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0022{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0023{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0024{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0025{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0026{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0027,
	CRA_0028,
	CRA_0029,
	CRA_0030,
	CRA_0031,
	CRA_0032,
	CRA_0033,
	CRA_0034,
	CRA_0035,
	CRA_0036,
	CRA_0037,
	CRA_0038,
	CRA_0039,
	CRA_0040,
	CRA_0041,
	
	CRA_0042{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitExpenses(this); 
		}
	},
	CRA_0043{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitExpenses(this); 
		}
	},
	CRA_0044{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitExpenses(this); 
		}
	},
	CRA_0045{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitExpenses(this); 
		}
	},
	CRA_0046{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitExpenses(this); 
		}
	},
	CRA_0047{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitExpenses(this); 
		}
	},
	CRA_0048{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitExpenses(this); 
		}
	},
	CRA_0049{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitExpenses(this); 
		}
	},
	CRA_0050{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitExpenses(this); 
		}
	},
	CRA_0051,
	CRA_0052,
	CRA_0053,
	CRA_0054{
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitCompensation(this); 
		}
	},
	CRA_0055,
	CRA_0056,
	CRA_0057, // CRA_0002
	CRA_0058, // CRA_0002
	CRA_0059, // CRA_0006
	CRA_0060, // CRA_0006
	CRA_0061  // CRA_0032
	;
	private boolean inBBC;
	private boolean outBBC;
	
	private PaymentType() {
		this(true, true);
	}

	private PaymentType(boolean inBBC, boolean outBBC) {
		this.inBBC = inBBC;
		this.outBBC = outBBC;
	}
	
    
    public void accept(PaymentTypeVisitor visitor) {
    	visitor.visitOther(this);
    }
    
    // ------------------------------------------------------------------------
    //
	
	public static interface PaymentTypeVisitor {
    	
    	void visitOther(PaymentType paymentType);

    	void visitSalaryInKind(PaymentType paymentType);

    	void visitNonStructuralHours(PaymentType paymentType);

    	void visitStructuralHours(PaymentType paymentType);

    	default void visitCompensation(PaymentType paymentType){
    		visitOther(paymentType);	
    	}
    	
    	default void visitExpenses(PaymentType paymentType){
    		visitOther(paymentType);
    	}
    	
    }
    
    // ------------------------------------------------------------------------
    //

	public static boolean isSalaryInKind(PaymentType type) {
    	class SalaryInKind implements PaymentTypeVisitor{
    		
    		private boolean isSalaryInKind = false;
    		
			@Override
			public void visitOther(PaymentType paymentType) {
			}

			@Override
			public void visitSalaryInKind(PaymentType paymentType) {
				isSalaryInKind = true;
			}

			@Override
			public void visitNonStructuralHours(PaymentType paymentType) {
			}

			@Override
			public void visitStructuralHours(PaymentType paymentType) {
			}

			@Override
			public void visitCompensation(PaymentType paymentType) {
			}

			@Override
			public void visitExpenses(PaymentType paymentType) {
			}
    		
    	}
    	SalaryInKind salaryInKind = new SalaryInKind();
    	type.accept(salaryInKind);
    	return salaryInKind.isSalaryInKind;
    }

}
