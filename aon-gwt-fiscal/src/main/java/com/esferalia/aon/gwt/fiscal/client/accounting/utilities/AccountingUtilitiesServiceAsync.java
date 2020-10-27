package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccountingUtilitiesServiceAsync {

	void getDomain(String domainName, String user, int domain, AsyncCallback<Domain> callback) throws AonCoreException;
	void getChildDomains(String domainName, String user, int domain, AsyncCallback<LinkedList<Domain>> callback) throws AonCoreException;
	
	// ParentLinker Module
	void checkParentLinker(String domainName, String user, Domain domain, Account account, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;
	void runParentLinker(String domainName, String user, Domain domain, Account account, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;
	
	// Chequeo de integridad de cuentas contables.
	void accountIntegrity(String domainName, String user, Domain domain, AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException;
	void accountIntegrityFix(String domainName, String user, Integer domain, Account account, AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException;

	// Chequeo de integridad de dominios en asientos contables
	void domainIntegrity(String domainName, String user, Domain domain, AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException;
	void domainIntegrityFix(String domainName, String user, Integer domain, Account account, AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException;

	// Chequeo de cuentas contables sin niveles inferiores.
	void noLowLevelAccounts(String domainName, String user, Domain domain, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;
	
	// Apuntes sin lineas
	void emptyEntries(String domainName, String user, Domain domain, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;

	// Apuntes descuadrados
	void unbalancedEntries(String domainName, String user, Domain domain, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;
	
	// Facturas contabilizadas sin apuntes
	void wrongRecordedInvoices(String domainName, String user, Domain domain, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;
	void removeWrongRecordedInvoice(String domainName,int domain, String user, Integer accountEntryId,AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException;
	void removeWrongCheckedInvoice(String domainName, int domain, String user, Integer invoice,AsyncCallback<AccUtilitiesResult> callback) throws AonCoreException;

	// Chequeo de cuentas asignadas a otras entidades
	void getAccountLinks(String domainName, String user, Integer domain, AccUtilitiesParams params, AsyncCallback<AccUtilitiesResult> asyncCallback) throws AonCoreException;
	void changeAccountDescription(String domainName, String user, Integer domain, Integer accountId, String newDescription, AsyncCallback<String> asyncCallback) throws AonCoreException;
	void createAndLinkAccount(String domainName, String user, Integer domain, AccountingRegistryType registryType, Integer registryId, AsyncCallback<Account> callback) throws AonCoreException;

	// Regeneracion del numero de diario
	void getJournalRegenerationInfo(String domainName, String user, Integer domain, AsyncCallback<AccUtilitiesResult> callback);
	void regenerateJournal(String domainName, String user, Integer domain, Integer accuountPeriod, AsyncCallback<AccUtilitiesResult> callback);

	// Regeneracion del numero de diario
	void getInputVatRegenerationInfo(String domainName, String user, Integer domain, AsyncCallback<AccUtilitiesResult> callback);
	void regenerateInputVat(String domainName, String user, Integer domain, Integer year, AsyncCallback<AccUtilitiesResult> callback);

	// Borrado de apuntes
	void removeEntries(String domainName, String user, Domain domain, AccountEntryParams params, AsyncCallback<AccUtilitiesResult> asyncCallback);
	
}
