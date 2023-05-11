package com.esferalia.aon.gwt.common.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/Common")
public interface CommonService extends RemoteService {

	// **************************************************
	// ********************************** [CONFIGURATION]
	// **************************************************
	AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain, String user) throws AonCoreException;
	AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain, String user, Date atDate) throws AonCoreException;
	AonConfiguration getAonConfiguration(Occam occam) throws AonCoreException;
	AonConfiguration getAonConfiguration(Occam occam, ConfigParams params) throws AonCoreException;
	
	// **************************************************
	// *************************************** [SECURITY]
	// **************************************************
	User getCurrentUser(String domainName, int domain, String currentUser) throws AonCoreException;
	
	// **************************************************
	// ************************************* [ENTERPRISE]
	// **************************************************
//	LinkedList<Enterprise> getParentEnterprises(String domainName, int domain, String user,String query) throws AonCoreException;
	Enterprise getEnterprise(String domainName, int domain, String user, int id) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain, String user) throws AonCoreException;
	LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain, String user,int enterprise) throws AonCoreException;
	
	// **************************************************
	// **************************************** [ACCOUNT]
	// **************************************************
	Account getAccount(String domainName,int domain, String user,Integer id) throws AonCoreException;
	Account getAccount(String domainName,int domain, String user,String code) throws AonCoreException;
	LinkedList<Account> getAccounts(String domainName,int domain, String user,String query) throws AonCoreException;
	Account save(String domainName, int domain, String user, Account account) throws AonCoreException;
	Account delete(String domainName, int domain, String user, Account account) throws AonCoreException;
	String getAccountNextCode(String domainName, int domain, String user, String prefix);
	
	// **************************************************
	// ************************************* [PAY_METHOD]
	// **************************************************
	LinkedList<PayMethod> getPayMethods(String domainName, int domain, String user);
	PayMethod savePayMethod(String domainName,int domain, String user, PayMethod payMethod) throws AonCoreException;
	void deletePayMethod(String domainName,int domain, String user, Integer id) throws AonCoreException;

	// **************************************************
	// **************************************** [INVOICE]
	// **************************************************
	LinkedList<InvoiceRegistry> getInvoiceRegistries(String domainName,int domain, String user,String query) throws AonCoreException;
	LinkedList<OldProduct> getInvoiceProducts(String domainName,int domain, String user,String query) throws AonCoreException;
	
	// Para carga del PDF del modelo
	void savePDFModel(Occam occam, IFiscalModel model, String data);
	
}
