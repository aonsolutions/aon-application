package com.esferalia.aon.occam.api.model.type;


public enum BonusType {
	

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
	},
	
	CONTINUOUS_FORMATION
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitContinuousFormation(this);
		}
	},
	
	YOUTH_WARRANTY_RDL08_2014
	{
		@Override
		public void accept(BonusTypeVisitor visitor) {
			visitor.visitYouthWarrantyRdl082014(this);
		}
	}	;

	public abstract void accept( BonusTypeVisitor visitor );
	
	// ------------------------------------------------------------------------
	
	public static interface BonusTypeVisitor {
		
		void visitSocialSecurity(BonusType bonusType);

		void visitEmploymentPromotion(BonusType bonusType);

		void visitCeutaMelilla(BonusType bonusType);

		void visitHandicap(BonusType bonusType);

		void visitLaw19_94(BonusType bonusType);

		void visitDistanceFormation(BonusType bonusType);

		void visitClassroomFormation(BonusType bonusType);

		void visitEre(BonusType bonusType);

		void visitEncouragedIndustrialSector(BonusType bonusType);

		void visitGt60(BonusType bonusType);
		
		void visitExemptionGt30Child(BonusType bonusType);
		
		void visitReductionRightContract(BonusType bonusType);
		
		void visitReductionCommonContingencyExceptIT(BonusType bonusType);
		
		void visitReductionFarmerCommonContingency(BonusType bonusType);
		
		void visitReductionFarmerUnemployment(BonusType bonusType);
		
		void visitReductionFlatRateRdl032014(BonusType bonusType);
		
		void visitContinuousFormation(BonusType bonusType);
		
		void visitYouthWarrantyRdl082014(BonusType bonusType);

	}

	// ------------------------------------------------------------------------
	
}