package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesParams;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeParams;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/AccountingUtilities")
public interface AccountingUtilitiesService extends RemoteService {
	
	Domain getDomain(String domainName,String user, int domain) throws AonCoreException;
	LinkedList<Domain> getChildDomains(String domainName,String user, int domain) throws AonCoreException;

	// ParentLinker Module
	AccUtilitiesResult checkParentLinker(String domainName, String user, Domain domain, Account account) throws AonCoreException;
	AccUtilitiesResult runParentLinker(String domainName, String user, Domain domain, Account account) throws AonCoreException;

	// Chequeo de integridad de cuentas contables.
	AccUtilitiesResult accountIntegrity(String domainName, String user, Domain domain) throws AonCoreException;
	AccUtilitiesResult accountIntegrityFix(String domainName, String user, Integer domain, Account account) throws AonCoreException;

	// Chequeo de integridad de dominios en asientos contables
	AccUtilitiesResult domainIntegrity(String domainName, String user, Domain domain) throws AonCoreException;
	AccUtilitiesResult domainIntegrityFix(String domainName, String user, Integer domain, Account account) throws AonCoreException;

	// Chequeo de cuentas contables sin niveles inferiores.
	AccUtilitiesResult noLowLevelAccounts(String domainName, String user, Domain domain) throws AonCoreException;

	// Apuntes sin lineas
	AccUtilitiesResult emptyEntries(String domainName, String user, Domain domain) throws AonCoreException;
	
	// Apuntes descuadrados
	AccUtilitiesResult unbalancedEntries(String domainName, String user, Domain domain) throws AonCoreException;
	
	// Apuntes fuera de fecha
	AccUtilitiesResult outOfDateEntries(String domainName, String user, Domain domain) throws AonCoreException;
	AccUtilitiesResult moveOutOfDateEntries(String domainName, String user, Domain domain, AccUtilitiesResult findResult) throws AonCoreException;
	
	// Facturas contabilizadas sin apuntes
	AccUtilitiesResult wrongRecordedInvoices(String domainName, String user, Domain domain) throws AonCoreException;
	AccUtilitiesResult removeWrongRecordedInvoice(String domainName,int domain, String user, Integer accountEntryId) throws AonCoreException;
	AccUtilitiesResult removeWrongCheckedInvoice(String domainName, int domain, String user, Integer invoice) throws AonCoreException;

	// Chequeo de cuentas asignadas a otras entidades
	AccUtilitiesResult getAccountLinks(String domainName, String user, Integer domain, AccUtilitiesParams params) throws AonCoreException;
	String changeAccountDescription(String domainName, String user, Integer domain, Integer accountId, String newDescription) throws AonCoreException;
	Account createAndLinkAccount(String domainName, String user, Integer domain, AccountingRegistryType registryType,Integer registryId) throws AonCoreException;

	// Regeneracion del numero de diario
	AccUtilitiesResult getJournalRegenerationInfo(String domainName, String user, Integer domain) throws AonCoreException;
	AccUtilitiesResult regenerateJournal(String domainName, String user, Integer domain, Integer accuountPeriod) throws AonCoreException;
	
	// Regeneracion del IVA soportado
	AccUtilitiesResult getInputVatRegenerationInfo(String domainName, String user, Integer domain) throws AonCoreException;
	AccUtilitiesResult regenerateInputVat(String domainName, String user, Integer domain, Integer year) throws AonCoreException;

	// Borrado de apuntes
	AccUtilitiesResult removeEntries(String domainName, String user, Domain domain, AccountEntryParams params) throws AonCoreException;

	// Integridad de facturas 
	AccUtilitiesResult invoiceIntegrity(String domainName, String user, Domain domain) throws AonCoreException;
	AccUtilitiesResult invoiceIntegrityFix(String domainName, String user, Integer domain, Integer invoiceId) throws AonCoreException;
	
	// Cambio de cuentas 
	AccUtilitiesResult searchAccountChange(String domainName, String user, Integer domain, AccUtilitiesAccountChangeParams params) throws AonCoreException;
	AccUtilitiesResult fixAccountChange(String domainName, String user, Integer domain, AccUtilitiesAccountChangeParams params, AccUtilitiesAccountChangeItem accountChanges) throws AonCoreException;
	
}
