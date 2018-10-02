package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("enterprises")
public interface EnterprisesService extends RemoteService {
	
	Integer getDomain();

	ContextDescriptor getContext();

	Bonus saveBonusConcept(Bonus bonus);

	Payment savePaymentConcept(Payment payment);

	Deduction saveDeductionConcept(Deduction deduction);
	
	/**
	 * 
	 * @param agreement
	 * @param value: if value > 0 move to Agreements. Else, move to Trash
	 */	
	void updateAgreementId(Agreement agreement);

	void deleteBonusConcept(Bonus bonus);

	void deletePaymentConcept(Payment payment);

	void deleteDeductionConcept(Deduction deduction);
	
	void deleteAgreement(Agreement agreement);
	
	void moveAgreement2Parent(Agreement agreement);
	
	Agreement copyAgreement(Agreement agreement);

	List<Agreement> getAgreements(int offset, int limit) ;

	List<Enterprise> getEnterprises(int offset, int limit) ;

	List<Bonus> getBonusConcepts(int offset, int limit) ;

	List<Payment> getPaymentConcepts(int offset, int limit) ;

	List<Deduction> getDeductionConcepts(int offset, int limit) ;
	
	List<Cost> getEnterprisesCosts(List<Integer> enterpriseIds); 
	
	List<Extra> getWorkplacesExtras(List<Integer> workplaceIds) ;

	Integer getParentDomain();

	WorkplaceInfo getWorkplaceInfo(Integer workplaceId);

	WorkplaceInfo setWorkplaceInfo(WorkplaceInfo workplaceInfo);
	
	
}
