package com.code.aon.finance.invoicing.remover;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;

/**
 * @author ecastellano
 *
 */
public class InvoiceRemoverFactory {
	
	private static InvoiceRemoverFactory instance; 
	private List<IInvoiceDetailRemover> removers = new LinkedList<IInvoiceDetailRemover>();

	private InvoiceRemoverFactory() {
	}
	
	public static InvoiceRemoverFactory getInstance() {
		if (instance == null) {
			instance = new InvoiceRemoverFactory();
		}
		return instance;
	}

	public List<IInvoiceDetailRemover> getRemovers() {
		return removers;
	}

	public void setRemovers(List<IInvoiceDetailRemover> removers) {
		this.removers = removers;
	}

	public static void register(Class<? extends IInvoiceDetailRemover> clazz) throws InvoicingException{
		try {
			IInvoiceDetailRemover remover = clazz.newInstance();
			getInstance().getRemovers().add(remover);
		} catch (InstantiationException e) {
			throw new InvoicingException("Can not instantiate " + clazz.getName(),e);
		} catch (IllegalAccessException e) {
			throw new InvoicingException("Can not instantiate " + clazz.getName(),e);
		}
	}

	public static IInvoiceDetailRemover getInvoiceDetailRemover(InvoiceSource source) throws InvoicingException{
		for (IInvoiceDetailRemover remover: getInstance().getRemovers()) {
			if (remover.accept(source)) {
				return remover; 
			}
		}
		throw new InvoicingException("No suitable InvoiceRemover for source " + source);
	}

	static {
		try {
			register(DirectSalesInvoiceDetailRemover.class);
			register(DirectPurchaseInvoiceDetailRemover.class);
			register(DeliveryInvoiceDetailRemover.class);
			register(IncomeInvoiceDetailRemover.class);
			register(FeeInvoiceDetailRemover.class);
			register(AccountInvoiceDetailRemover.class);
			register(DirectInvoiceDetailRemover.class);
			register(OfferInvoiceDetailRemover.class);
		} catch (InvoicingException e) {
			e.printStackTrace();
		}
	}
	
}