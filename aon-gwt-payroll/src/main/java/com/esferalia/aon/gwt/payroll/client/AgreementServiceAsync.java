package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>AgreeementService</code>.
 */
public interface AgreementServiceAsync {

	void eval(String domain, String expression, AgreementDraft agreementDraft, int levelId,
			AsyncCallback<List<Result>> callback)
			throws IllegalArgumentException, EvalException;

	void getAvailablePayments(String domain, int employeeId,
			AsyncCallback<List<Payment>> callback)
			throws IllegalArgumentException;

	void getContext(String domain, AgreementDraft agreementDraft, int levelId,
			AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException;

	void getAgreementDraftReceiptHTML(String domain, AgreementDraft agreementDraft,
			int levelId, Salary.Type type, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void saveAgreementDraft(String domain, AgreementDraft agreementDraft,
			AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException;

	void calculateAgreementDraft(String domain, AgreementDraft agreementDraft,
			AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException;

	void getChanges(String domain, Agreement agreement, AsyncCallback<SortedSet<Date>> callback)
			throws IllegalArgumentException;

	
}
