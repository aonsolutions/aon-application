package com.esferalia.aon.salary.enumeration;

public interface BonusTypeVisitor {
	
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
