package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum SSRegimeType implements IResourceable{
	
	GENERAL
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitGeneralRegime(this);
		}
	},
	AGRICULTURAL
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitAgriculturalRegime(this);
		}
	},
	DOMESTIC_EMPLOYEES
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitDomesticEmployeesRegime(this);
		}
	},
	SELF_EMPLOYED
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitSelfEmployedRegime(this);
		}
	},
	COAL_MINING
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitCoalMiningRegime(this);
		}
	},
	SEA_WORKERS
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitSeaWorkersRegime(this);
		}
	},
	STUDENT_INSURANCE
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitStudentInsuranceRegime(this);
		}
	},
	ARTIST
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitArtistRegime(this);
		}
	},
	ISFAS{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitIsfasRegime(this);
		}
	},
	MUFACE{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitMufaceRegime(this);
		}
	}
	;

	public abstract <E> E accept(SSRegimeTypeVisitor<E> visitor);
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_ss_regime_";

	@Override
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}
