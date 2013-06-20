package com.esferalia.aon.salary.enumeration;

public abstract class AbstractDeductionTypeVisitor implements
		DeductionTypeVisitor {

	@Override
	public void visitCommonContigency(DeductionType deductionType) {
	}

	@Override
	public void visitProfessionalContigency(DeductionType deductionType) {
	}

	@Override
	public void visitUnemployent(DeductionType deductionType) {
	}

	@Override
	public void visitJobTraining(DeductionType deductionType) {
	}

	@Override
	public void visitStructuralOvertime(DeductionType deductionType) {
	}

	@Override
	public void visitNonStructuralOvertime(DeductionType deductionType) {
	}

	@Override
	public void visitIrpf(DeductionType deductionType) {

	}

	@Override
	public void visitAdvancePayment(DeductionType deductionType) {
	}

	@Override
	public void visitInkind(DeductionType deductionType) {
	}

	@Override
	public void visitOther(DeductionType deductionType) {
	}

	@Override
	public void visitFogasa(DeductionType deductionType) {
	}

}
