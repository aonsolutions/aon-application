package com.esferalia.aon.salary.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum BonusType implements IResourceable {
	

	SOCIAL_SECURITY
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitSocialSecurity(this);
		}
	},
	
	EMPLOYMENT_PROMOTION
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitEmploymentPromotion(this);
		}
	},
	
	CEUTA_MELILLA
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitCeutaMelilla(this);
		}
	},
	
	HANDICAP
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitHandicap(this);
		}
	},
	
	LAW_19_94
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitLaw19_94(this);
		}
	},
	
	DISTANCE_FORMATION
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitDistanceFormation(this);
		}
	},

	CLASSROOM_FORMATION
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitClassroomFormation(this);
		}
	},
	
	ERE
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitEre(this);
		}
	},

	ENCOURAGED_INDUSTRIAL_SECTOR
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitEncouragedIndustrialSector(this);
		}
	},
	
	GT_60
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitGt60(this);
		}
	},
	
	EXEMPTION_GT30_CHILD
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitExemptionGt30Child(this);
		}
	},
	
	REDUCTION_RIGHT_CONTRACT
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitReductionRightContract(this);
		}
	},

	REDUCTION_COMMON_CONTINGENCY_EXCEPT_IT
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitReductionCommonContingencyExceptIT(this);
		}
	},
	
	REDUCTION_FARMER_COMMON_CONTINGENCY
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitReductionFarmerCommonContingency(this);
		}
	},
	
	REDUCTION_FARMER_UNEMPLOYMENT
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitReductionFarmerUnemployment(this);
		}
	},
	
	REDUCTION_FLAT_RATE_RDL03_2014
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitReductionFlatRateRdl032014(this);
		}
	}
	
	;

	
	public abstract void accept( BonusTypeVisitor visitor );
	
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_bonus_type_";
    
    
    
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
