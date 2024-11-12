package com.esferalia.aon.salary.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;



public enum DeductionType implements IResourceable {

	
	COMMON_CONTINGENCY(true)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitCommonContigency(this);
		}
	},
	PROFESSIONAL_CONTINGENCY(true)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitProfessionalContigency(this);
		}
	},
	UNEMPLOYMENT(true)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitUnemployent(this);
		}
	},
	JOB_TRAINING(true)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitJobTraining(this);
		}
	},
	STRUCTURAL_OVERTIME(true)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitStructuralOvertime(this);
		}
	},
	NON_STRUCTURAL_OVERTIME(true)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitNonStructuralOvertime(this);
		}
	},
	IRPF(false, true)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitIrpf(this);
		}
	},
	ADVANCE_PAYMENT(false)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitAdvancePayment(this);
		}
	},
	IN_KIND(false)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitInkind(this);
		}
	},
	OTHER(false)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitOther(this);
		}
	},
	FOGASA(true) // TODO: After Professional Contingency?
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitFogasa(this);
		}
		
	},
	EMBARGO(false)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitEmbargo(this);
		}
	},
	BONUS(true)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitBonus(this);
		}
	},
	MEI(true)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitMei(this);
		}
	},
	SOLIDARITY(true)
	{
		@Override
		public void accept(DeductionTypeVisitor visitor) {
			visitor.visitSolidarity(this);
		}
	}
	;
	
	
	private boolean ssDeduction = false;
	private boolean taxDeduction = false;
	
	
	private DeductionType(boolean ssDeduction) {
		this.ssDeduction = ssDeduction;
	}

	private DeductionType(boolean ssDeduction, boolean taxDeduction) {
		this.ssDeduction = ssDeduction;
		this.taxDeduction = taxDeduction;
	}

	public abstract void accept( DeductionTypeVisitor visitor );
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_deduction_type_";
    
    
    
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



	public boolean isSsDeduction() {
		return ssDeduction;
	}
	
	public boolean isTaxDeduction() {
		return taxDeduction;
	}
}
