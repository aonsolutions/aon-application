package com.esferalia.aon.salary.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum PaymentType implements IResourceable{
	
	CRA_0000(true,false),
	CRA_0001(true,false),
	CRA_0002(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitNonStructuralHours(this);
		}
	},
	CRA_0003(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitStructuralHours(this);
		}
	},
	CRA_0004(true,false),
	CRA_0005(true,false),
	CRA_0006(true,false),
	CRA_0007(true,false),
	CRA_0008(true,false),
	CRA_0009(true,false),
	CRA_0010(true,false),
	CRA_0011(true,false),
	CRA_0012(true,false),
	CRA_0013(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0014(true,true){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0015(true,true){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0016(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0017(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0018(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0019(true,true){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0020(true,true){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0021(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0022(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0023(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0024(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0025(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0026(true,false){
		@Override
		public void accept(PaymentTypeVisitor visitor) { 
			visitor.visitSalaryInKind(this); 
		}
	},
	CRA_0027(true,false),
	CRA_0028(true,false),
	CRA_0029(true,true),
	CRA_0030(true,false),
	CRA_0031(true,false),
	CRA_0032(true,false),
	CRA_0033(true,false),
	CRA_0034(true,false),
	CRA_0035(false,true), // 
	CRA_0036(true,false),
	CRA_0037(true,false),
	CRA_0038(true,false),
	CRA_0039(true,false),
	CRA_0040(true,false),
	CRA_0041(true,false),
	CRA_0042(true,true),
	CRA_0043(true,true),
	CRA_0044(true,true),
	CRA_0045(true,true),
	CRA_0046(true,true),
	CRA_0047(true,true),
	CRA_0048(true,true),
	CRA_0049(false,true),
	CRA_0050(true,true),
	CRA_0051(true,true),
	CRA_0052(true,true),
	CRA_0053(true,true),
	CRA_0054(true,true),
	CRA_0055(false,true),
	CRA_0056(true,false),
	
	CRA_0057(true,false), // CRA_0002
	CRA_0058(true,false), // CRA_0002
	CRA_0059(true,false), // CRA_0006
	CRA_0060(true,false), // CRA_0006
	CRA_0061(true,false)  // CRA_0032
	;

	private boolean bBCCIncluded;
	private boolean bBCCExcluded;
	

	private PaymentType(boolean bBCCIncluded, boolean bBCCExcluded) {
		this.bBCCIncluded = bBCCIncluded;
		this.bBCCExcluded = bBCCExcluded;
	}
	
	
	public boolean isBBCCExcluded() {
		return bBCCExcluded;
	}
	
	public boolean isBBCCIncluded() {
		return bBCCIncluded;
	}
	
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_payment_type_";

    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     *
     * @param locale Required Locale.
     *
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {

        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
                return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    
    public void accept(PaymentTypeVisitor visitor) {
    	visitor.visitOther(this);
    }
	
    @Deprecated
    public static PaymentType STRUCTURAL_HOURS = CRA_0003;
    @Deprecated
    public static PaymentType NON_STRUCTURAL_HOURS = CRA_0002;
    @Deprecated
	public static PaymentType MOVING_COMPENSATION = CRA_0052;
    
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
    		
    	}
    	SalaryInKind salaryInKind = new SalaryInKind();
    	type.accept(salaryInKind);
    	return salaryInKind.isSalaryInKind;
    }
    
}
