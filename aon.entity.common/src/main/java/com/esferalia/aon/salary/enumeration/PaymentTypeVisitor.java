package com.esferalia.aon.salary.enumeration;

public interface PaymentTypeVisitor {
	

	void visitOther(PaymentType paymentType);

	void visitSalaryInKind(PaymentType paymentType);

	void visitNonStructuralHours(PaymentType paymentType);

	void visitStructuralHours(PaymentType paymentType);


}
