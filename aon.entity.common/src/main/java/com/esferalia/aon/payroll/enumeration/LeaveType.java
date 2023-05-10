package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum LeaveType implements IResourceable {

	COMMON_DISEASE
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitCommonDisease(this);
		}
	},
	OCCUPATIONAL_DISEASE
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitOcupationalDisease(this);
		}
	},
	MATERNITY
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitMaternity(this);
		}
	},
	PATERNITY
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitPaternity(this);
		}
	},
	PREGNANCY_RISK
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitPregnacyRisk(this);
		}
	},
	BREASTFEEDING_RISK
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitBreastFeedingRisk(this);
		}
	},
	NON_OCCUPATIONAL_DISEASE
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitNonOcupationalDisease(this);
		}
	},
	COMMON_DISEASE_AT_LACK
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitCommonDiseaseAtLack(this);
		}
	},
	COMMON_OCCUPATIONAL_DISEASE
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitCommonProfessionalDisease(this);
		}
	},
	OBSERVATION_OCCUPATIONAL_DISEASE
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitOcupationalDisease(this);
		}
	}
	,MENSTRUATION
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitMenstruation(this);
		}
	}
	,PREGNANCY_STOP
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitPregnacyStop(this);
		}
	}
	,PREGNANCY_39_WEEK
	{
		@Override
		public <T> T accept(LeaveTypeVisitor<T> visitor) {
			return visitor.visitPregnacy39Week(this);
		}
	}
	;
	
	public abstract <T> T accept(LeaveTypeVisitor<T>  visitor );

    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_leave_type_";

	
	@Override
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

}
