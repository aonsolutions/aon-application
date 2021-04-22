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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("agreement")
public interface AgreementService extends RemoteService{


	List<Result> eval(String domain, String expression, AgreementDraft agreementDraft,
			int levelId) throws IllegalArgumentException, EvalException;

	SortedSet<Date> getChanges(String domain, Agreement agreement)
			throws IllegalArgumentException;

	List<Payment> getAvailablePayments(String domain, int employeeId)
			throws IllegalArgumentException;

	AgreementDraft calculateAgreementDraft(String domain, AgreementDraft agreementDraft)
			throws IllegalArgumentException;

	AgreementDraft saveAgreementDraft(String domain, AgreementDraft agreementDraft) throws IllegalArgumentException;

	ContextDescriptor getContext(String domain, AgreementDraft agreementDraft, int levelId) throws IllegalArgumentException;

	String getAgreementDraftReceipt(String domain, AgreementDraft agreementDraft, int levelId, Salary.Type type, String mime)
			throws IllegalArgumentException;

	String getAgreementDraftReceiptHTML(String domain, AgreementDraft agreementDraft, int levelId, Salary.Type type, int zoom)
			throws IllegalArgumentException;

}