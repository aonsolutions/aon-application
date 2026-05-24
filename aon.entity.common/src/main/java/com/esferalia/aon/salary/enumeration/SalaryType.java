package com.esferalia.aon.salary.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SalaryType implements IResourceable {

	
	SALARY 		// 0
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitSalary(this);
		}
	},
	EXTRA 		// 1
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitExtra(this);
		}
	},	
	SETTLE 		// 2
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitSettle(this);
		}
	},
	DELAY 		// 3
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
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
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitProcedural(this);
		}
	},	
	L00 		// 10
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitL00(this);
		}
	},
	L02 		// 11
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitL02(this);
		}
	},
	L03 		// 12
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitL03(this);
		}
	},
	L13 		// 13
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitL13(this);
		}
	},
	UNKNOWN_14,
	UNKNOWN_15,
	UNKNOWN_16,
	UNKNOWN_17,
	UNKNOWN_18,
	UNKNOWN_19,
	
	
	M190		// 20 	
	{
		@Override
		public <E> E accept(SalaryTypeVisitor<E> visitor) {
			return visitor.visitM190(this);
		}
	}
	;
	
	
	
	public <E> E accept(SalaryTypeVisitor<E>  visitor) { 
		throw new UnknownSalaryTypeException(this);
	}
	
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
	
    public static String getName(Locale locale, String name) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + name);
		
    }
    
}
