package com.esferalia.aon.salary.enumeration;

public interface PaymentTypeVisitor {
	void visitBaseSalary(PaymentType type);

	void visitOtherNonWage(PaymentType paymentType);

	void visitMovingCompensation(PaymentType paymentType);

	void visitSocialSecurityBenefits(PaymentType paymentType);

	void visitCompensationExpense(PaymentType paymentType);

	void visitSalaryInKind(PaymentType paymentType);

	void visitSpecialBonos(PaymentType paymentType);

	void visitNonStructuralHours(PaymentType paymentType);

	void visitStructuralHours(PaymentType paymentType);

	void visitSalarySupplement(PaymentType paymentType);

}
