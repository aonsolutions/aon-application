package com.esferalia.aon.gwt.common.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.error.AonCoreException;

@WebServlet(name = "Aon Registry Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Registry", "/aon_gwt_aio/ms/Registry"})
public class RegistryServiceImpl extends AonStatelessRemoteServiceServlet implements RegistryService {

	private static final long serialVersionUID = -8911264415447499114L;

	// **************************************************
	// *************************************** [CUSTOMER]
	// **************************************************

	@Override
	public LinkedList<Customer> getCustomers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit) {
		return AON.getCustomers(domainName, domain,user, params, ofs, limit);
	}
	@Override
	public CustomerFull getCustomerFull(String domainName, int domain, String user, Integer id) throws AonCoreException {
		return AON.getCustomerFull(domainName, domain,user, id);
	}

	@Override
	public CustomerFull save(String domainName, int domain, String user, CustomerFull customerFull) throws AonCoreException {
		return AON.save(domainName, domain,user, customerFull);
	}

	// **************************************************
	// *************************************** [CREDITOR]
	// **************************************************
	@Override
	public LinkedList<Creditor> getCreditors(String domainName, int domain, String user, RegistryParams params, int ofs, int limit) {
		return AON.getCreditors(domainName, domain,user, params, ofs, limit);
	}
	@Override
	public CreditorFull getCreditorFull(String domainName, int domain, String user, Integer id) throws AonCoreException {
		return AON.getCreditorFull(domainName, domain,user, id);
	}

	@Override
	public CreditorFull save(String domainName, int domain, String user, CreditorFull creditorFull) throws AonCoreException {
		return AON.save(domainName, domain,user, creditorFull);
	}

	// **************************************************
	// *************************************** [SUPPLIER]
	// **************************************************

	@Override
	public LinkedList<Supplier> getSuppliers(String domainName, int domain, String user, RegistryParams params, int ofs, int limit) {
		return AON.getSuppliers(domainName, domain,user, params, ofs, limit);
	}
	@Override
	public SupplierFull getSupplierFull(String domainName, int domain, String user, Integer id) throws AonCoreException {
		return AON.getSupplierFull(domainName, domain,user, id);
	}

	@Override
	public SupplierFull save(String domainName, int domain, String user, SupplierFull supplierFull) throws AonCoreException {
		return AON.save(domainName, domain,user, supplierFull);
	}
	

	
}
