package com.esferalia.aon.salary.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum PaymentType implements IResourceable{
	
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
	CRA_0042,
	CRA_0043,
	CRA_0044,
	CRA_0045,
	CRA_0046,
	CRA_0047,
	CRA_0048,
	CRA_0049,
	CRA_0050,
	CRA_0051,
	CRA_0052,
	CRA_0053,
	CRA_0054,
	CRA_0055,
	CRA_0056;
	
	private boolean inBBC;
	private boolean outBBC;
	
	private PaymentType() {
		this(true, true);
	}

	private PaymentType(boolean inBBC, boolean outBBC) {
		this.inBBC = inBBC;
		this.outBBC = outBBC;
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
    
}
