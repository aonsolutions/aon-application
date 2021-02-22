package com.esferalia.aon.salary.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SalaryType implements IResourceable {

	
	SALARY
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitSalary(this);
		}
	},
	EXTRA
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitExtra(this);
		}
	},
	SETTLE
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitSettle(this);
		}
	},
	DELAY
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitDelay(this);
		}
	},
	
	
	L00 
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitL00(this);
		}
	},
	L03 
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitL03(this);
		}
	},
	L13 
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitL13(this);
		}
	}
	;
	
	
	public abstract <E> E accept(SalaryTypeVisitor<E>  visitor);
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_salary_type_";

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
