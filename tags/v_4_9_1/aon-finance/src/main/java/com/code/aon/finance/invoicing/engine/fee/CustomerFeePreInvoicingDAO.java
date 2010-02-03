package com.code.aon.finance.invoicing.engine.fee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.engine.IInvoicingDAO;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.registry.Registry;

public class CustomerFeePreInvoicingDAO implements IInvoicingDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerFeePreInvoicingDAO.class.getName());
	
	private List<PreInvoice> preInvoicingCollection;
	
	private PreInvoice currentPreInvoice;
	
	private IPriceStrategy priceStrategy;
	
	public CustomerFeePreInvoicingDAO(){
		preInvoicingCollection = new ArrayList<PreInvoice>();
	}

	public Invoice insertInvoice(Invoice invoice) {
		invoice.setId(new Integer(preInvoicingCollection.size() + 1));
		fillTaxInfo(invoice);
		currentPreInvoice = new PreInvoice(invoice);
		preInvoicingCollection.add(currentPreInvoice);
		return invoice;
	}

	public void insertInvoiceDetail(InvoiceDetail invoiceDetail) {
		try {
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			currentPreInvoice.addPreInvoiceDetail(invoiceDetail);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error adding invoiceDetail with id=" + invoiceDetail.getId(), e);
		}
	}
	
	public void updateSource(ITransferObject to) {
	}
	
	public void createFinances(Invoice invoice) {
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(){
		return preInvoicingCollection;
	}

	private void fillTaxInfo(Invoice invoice) {
		Company company = obtainCompany();
		Customer customer = obtainCustomer(invoice.getRegistry());
		invoice.setWithholding(company.isWithholding() && customer.isWithholding());
		invoice.setSurcharge(customer.isSurcharge());
		invoice.setTaxFree(customer.isTaxFree());
	}

	private Company obtainCompany() {
		try {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			Iterator<ITransferObject> iterator = companyBean.getList(null, 0, 1).iterator();
			if (iterator.hasNext()) {
				return (Company) iterator.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining company", e);
		}
		return null;
	}

	private Customer obtainCustomer(Registry registry) {
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			return (Customer)customerBean.get(registry.getId());
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining customer with id=" + registry.getId(), e);
		}
		return null;
	}

	private IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

}