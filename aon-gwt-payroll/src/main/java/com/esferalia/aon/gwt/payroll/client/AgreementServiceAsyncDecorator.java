/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author rtrepiana
 * 
 */
public class AgreementServiceAsyncDecorator implements AgreementServiceAsync {

	private AgreementServiceAsync agreementServiceAsync;

	public AgreementServiceAsyncDecorator(
			AgreementServiceAsync agreementServiceAsync) {
		this.agreementServiceAsync = agreementServiceAsync;
	}

	@Override
	public void getAvailablePayments(String domain, int employeeId,
			AsyncCallback<List<Payment>> callback)
			throws IllegalArgumentException {
		AON.start();
		agreementServiceAsync.getAvailablePayments(domain, employeeId,
				new AsyncCallbackWrapper<List<Payment>>(callback));
	}


	@Override
	public void saveAgreementDraft(String domain, AgreementDraft agreementDraft,
			AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		agreementServiceAsync.saveAgreementDraft(domain, agreementDraft,
				new AsyncCallbackWrapper<AgreementDraft>(callback));
	}


	@Override
	public void eval(String domain, String expression, AgreementDraft agreementDraft,
			int levelId, AsyncCallback<List<Result>> callback)
			throws IllegalArgumentException, EvalException {
		AON.start();
		agreementServiceAsync.eval(domain, expression, agreementDraft, levelId,
				new AsyncCallbackWrapper<List<Result>>(callback));
	}


	@Override
	public void getContext(String domain, AgreementDraft agreementDraft, int levelId,
			AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException {
		AON.start();
		agreementServiceAsync.getContext(domain, agreementDraft, levelId,
				new AsyncCallbackWrapper<ContextDescriptor>(callback));
	}


	@Override
	public void calculateAgreementDraft(String domain,AgreementDraft agreementDraft,
			AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		agreementServiceAsync.calculateAgreementDraft(
				domain, 
				agreementDraft,
				new AsyncCallbackWrapper<AgreementDraft>(callback));
	}

	@Override
	public void getAgreementDraftReceipt(String domain, AgreementDraft agreementDraft, List<Variable> context,
			int levelId, Salary.Type type, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		agreementServiceAsync
				.getAgreementDraftReceipt(domain, agreementDraft, context, levelId, type,
						mime, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getAgreementDraftReceiptHTML(String domain, AgreementDraft agreementDraft,
			int levelId, Salary.Type type, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		agreementServiceAsync
				.getAgreementDraftReceiptHTML(domain, agreementDraft, levelId, type,
						zoom, new AsyncCallbackWrapper<String>(callback));
	}


	@Override
	public void getChanges(String domain, Agreement agreement,
			AsyncCallback<SortedSet<Date>> callback)
			throws IllegalArgumentException {
		AON.start();
		agreementServiceAsync.getChanges(domain, agreement,
				new AsyncCallbackWrapper<SortedSet<Date>>(callback));
	}

	@Override
	public void checkAndUpdateServiAgreement(String domain, Integer agreementId, String ssNumber, Integer lastDateYear, AsyncCallback<Date> callback)
			throws IllegalArgumentException {
		AON.start();
		agreementServiceAsync.checkAndUpdateServiAgreement(domain, agreementId, ssNumber, lastDateYear, new AsyncCallbackWrapper<Date>(callback));
	}

}
