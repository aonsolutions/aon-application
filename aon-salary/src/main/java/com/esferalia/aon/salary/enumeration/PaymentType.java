package com.esferalia.aon.salary.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum PaymentType implements IResourceable {

	
	BASE_SALARY 
	{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitBaseSalary(this);
		}
	},
	SALARY_SUPPLEMENTS
	{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitSalarySupplement(this);
		}
	},
	STRUCTURAL_HOURS
	{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitStructuralHours(this);
		}
	},
	NON_STRUCTURAL_HOURS
	{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitNonStructuralHours(this);
		}
	},
	SPECIAL_BONUSES
	{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitSpecialBonos(this);
		}
	},
	SALARY_IN_KIND
	{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitSalaryInKind(this);
		}
	},
	COMPENSATION_OR_PREPAID_EXPENSES
	{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitCompensationExpense(this);
		}
	},
	SOCIAL_SECURITY_BENEFITS
	{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitSocialSecurityBenefits(this);
		}
	},
	MOVING_COMPENSATION
	{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitMovingCompensation(this);
		}
	},
	OTHER_NON_WAGE
	{
		@Override
		public void accept(PaymentTypeVisitor visitor) {
			visitor.visitOtherNonWage(this);
		}
	}	;
	

    public abstract void accept( PaymentTypeVisitor visitor );
    

    /** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.salary.i18n.messages";
    
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
    
    

}
