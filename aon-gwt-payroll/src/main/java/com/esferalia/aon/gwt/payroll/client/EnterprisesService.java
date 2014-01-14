package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("enterprises")
public interface EnterprisesService extends RemoteService {
	
	ContextDescriptor getContext();

	Bonus saveBonusConcept(Bonus bonus);

	Payment savePaymentConcept(Payment payment);

	Deduction saveDeductionConcept(Deduction deduction);

	void deleteBonusConcept(Bonus bonus);

	void deletePaymentConcept(Payment payment);

	void deleteDeductionConcept(Deduction deduction);

	List<Agreement> getAgreements(int offset, int limit) ;

	List<Enterprise> getEnterprises(int offset, int limit) ;

	List<Bonus> getBonusConcepts(int offset, int limit) ;

	List<Payment> getPaymentConcepts(int offset, int limit) ;

	List<Deduction> getDeductionConcepts(int offset, int limit) ;
	
}
