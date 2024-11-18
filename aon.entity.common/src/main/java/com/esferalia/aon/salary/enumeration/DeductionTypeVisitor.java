package com.esferalia.aon.salary.enumeration;

public interface DeductionTypeVisitor {

	void visitMei(DeductionType deductionType);

	void visitCommonContigency(DeductionType deductionType);

	void visitProfessionalContigency(DeductionType deductionType);

	void visitUnemployent(DeductionType deductionType);

	void visitJobTraining(DeductionType deductionType);

	void visitStructuralOvertime(DeductionType deductionType);

	void visitNonStructuralOvertime(DeductionType deductionType);

	void visitIrpf(DeductionType deductionType);

	void visitAdvancePayment(DeductionType deductionType);

	void visitInkind(DeductionType deductionType);

	void visitOther(DeductionType deductionType);
	
	void visitFogasa(DeductionType deductionType);

	void visitEmbargo(DeductionType deductionType);

	void visitBonus(DeductionType deductionType);

	void visitSolidarity(DeductionType deductionType);
}
