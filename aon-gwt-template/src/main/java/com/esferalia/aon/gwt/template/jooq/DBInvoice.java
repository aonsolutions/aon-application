package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAddress.INVOICE_ADDRESS;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.Account;
import com.esferalia.aon.jooq.tables.Geotree;
import com.esferalia.aon.jooq.tables.Geozone;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO.InvoiceAddressFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO.InvoiceFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDetailDAO.InvoiceDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceTaxDAO.InvoiceTaxFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO.RegistryAddressFiller;

public class DBInvoice {
	
	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	public static final Account CUSTOMER_ACCOUNT = ACCOUNT.as("customer_account");
	public static final Account CREDITOR_ACCOUNT = ACCOUNT.as("creditor_account");
	public static final Account SUPPLIER_ACCOUNT = ACCOUNT.as("supplier_account");
	public static final Geozone  INVOICE_PARENT_GEOZONE = GEOZONE.as("invoice_parent_geozone");
	public static final Geozone  INVOICE_CHILD_GEOZONE = GEOZONE.as("invoice_child_geozone");
	public static final Geozone  REGISTRY_PARENT_GEOZONE = GEOZONE.as("registry_parent_geozone");
	public static final Geozone  REGISTRY_CHILD_GEOZONE = GEOZONE.as("registry_child_geozone");
	public static final Geotree INVOICE_GEOTREE = GEOTREE.as("invoice_geotree");
	public static final Geotree REGISTRY_GEOTREE = GEOTREE.as("registry_geotree");
	
	public static Result<Record> getSaleInvoices(AONContext ctx, InvoiceFilter filter) {
		return ctx.getDslContext().select()
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
				.join(INVOICE_TAX).on(INVOICE_DETAIL.ID.eq(INVOICE_TAX.INVOICE_DETAIL))
				
				.leftOuterJoin(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
				.leftOuterJoin(CUSTOMER_ACCOUNT).on(CUSTOMER_ACCOUNT.ID.eq(CUSTOMER.ACCOUNT))
				
				.leftOuterJoin(INVOICE_ADDRESS).on(INVOICE.ID.eq(INVOICE_ADDRESS.INVOICE))
				.leftOuterJoin(INVOICE_CHILD_GEOZONE).on(INVOICE_CHILD_GEOZONE.ID.eq(INVOICE_ADDRESS.GEOZONE))
				.leftOuterJoin(INVOICE_GEOTREE).on(INVOICE_GEOTREE.CHILD.eq(INVOICE_ADDRESS.GEOZONE))
				.leftOuterJoin(INVOICE_PARENT_GEOZONE).on(INVOICE_PARENT_GEOZONE.ID.eq(INVOICE_GEOTREE.PARENT))

				.leftOuterJoin(RADDRESS).on(RADDRESS.ID.eq(INVOICE.RADDRESS))
				.leftOuterJoin(REGISTRY_CHILD_GEOZONE).on(REGISTRY_CHILD_GEOZONE.ID.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(REGISTRY_GEOTREE).on(REGISTRY_GEOTREE.CHILD.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(REGISTRY_PARENT_GEOZONE).on(REGISTRY_PARENT_GEOZONE.ID.eq(REGISTRY_GEOTREE.PARENT))

				.where(INVOICE_PROPERTIES.getConditions(filter))
				.and(INVOICE.TYPE.eq(InvoiceType.SALES.value()))
				.fetch();
	}
	
	public static List<Invoice> getInvoices(Domain domain, User user, InvoiceFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(domain, user)){
			Result<Record> a = getSaleInvoices(ctx, filter);
			LinkedList<Invoice> list = new LinkedList<>();
			HashMap<Integer, InvoiceDetail>  invoiceDetailMap = new HashMap<>();
			Invoice invoice = new Invoice();
			for (Record r : a) {
				Invoice inv = InvoiceFiller.buildInvoice(r);
				
				if(inv.isSales()) inv.setRegistryAccount(FullAccountFiller.build(r, CUSTOMER_ACCOUNT));
				if(inv.isPurchase()) inv.setRegistryAccount(FullAccountFiller.build(r, SUPPLIER_ACCOUNT));
				if(inv.isExpenses()) inv.setRegistryAccount(FullAccountFiller.build(r, CREDITOR_ACCOUNT));
				
				RegistryAddress address = InvoiceAddressFiller.build(r, INVOICE_PARENT_GEOZONE, INVOICE_CHILD_GEOZONE); 
				if(address.isEmpty()) {
					address = RegistryAddressFiller.build(r, REGISTRY_PARENT_GEOZONE, REGISTRY_CHILD_GEOZONE);
				}
				inv.setAddress(address);
				
				if(!inv.getId().equals(invoice.getId())) {
					if(invoice.getId() != null) {
						for(Integer k : invoiceDetailMap.keySet()) {
							invoice.getDetails().add(invoiceDetailMap.get(k));
						}
						list.add(invoice);
					}
					invoice = inv;
					invoiceDetailMap = new HashMap<>();
				}
				
				InvoiceDetail id = InvoiceDetailFiller.build(r);
				if(!invoiceDetailMap.containsKey(id.getId())) {
					invoiceDetailMap.put(id.getId(), id);
					id.setInvoiceTaxes(new LinkedList<>());
				}
				
				InvoiceTax it = InvoiceTaxFiller.build(r);
				invoiceDetailMap.get(id.getId()).getInvoiceTaxes().add(it);
			}
			return list;
		}

	}
	
}
