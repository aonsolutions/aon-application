package com.code.aon.sales;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.IInvoicingDAO;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.finance.invoicing.PreInvoice;
import com.code.aon.product.strategy.IPriceStrategy;

public class CustomerFeePreInvoicingDAO implements IInvoicingDAO {
	
	private static final Logger LOGGER = Logger.getLogger(CustomerFeePreInvoicingDAO.class.getName());
	
	private List<PreInvoice> preInvoicingCollection;
	
	private PreInvoice currentPreInvoice;
	
	private IPriceStrategy priceStrategy;
	
	public CustomerFeePreInvoicingDAO(){
		preInvoicingCollection = new ArrayList<PreInvoice>();
	}

	public Invoice insertInvoice(Invoice invoice) {
		invoice.setId(new Integer(preInvoicingCollection.size() + 1));
		currentPreInvoice = new PreInvoice(invoice);
		preInvoicingCollection.add(currentPreInvoice);
		return invoice;
	}

	public void insertInvoiceDetail(InvoiceDetail invoiceDetail) {
		try {
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			currentPreInvoice.addPreInvoiceDetail(invoiceDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error adding invoiceDetail with id=" + invoiceDetail.getId(), e);
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
	
	private IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

}