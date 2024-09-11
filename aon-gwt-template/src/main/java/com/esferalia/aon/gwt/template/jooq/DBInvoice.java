package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceAddress.INVOICE_ADDRESS;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jooq.Record;
import org.jooq.Result;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.Account;
import com.esferalia.aon.jooq.tables.Geotree;
import com.esferalia.aon.jooq.tables.Geozone;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilterOLD;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO.EnterpriseActivityFiller;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDetailOLDDAO.InvoiceDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO.RegistryAddressFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceAddressDAO.InvoiceAddressFiller;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

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
	
	public static Result<Record> getSaleInvoices(AONContext ctx, InvoiceFilterOLD filter) {
		return ctx.getDslContext().select()
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
				.leftOuterJoin(INVOICE_TAX).on(INVOICE_DETAIL.ID.eq(INVOICE_TAX.INVOICE_DETAIL))
				
				.leftOuterJoin(INVOICE_DETAIL_ACCOUNT).on(INVOICE_DETAIL_ACCOUNT.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
				.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(INVOICE_DETAIL_ACCOUNT.ACCOUNT))
				
				.leftOuterJoin(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
				.leftOuterJoin(CUSTOMER_ACCOUNT).on(CUSTOMER_ACCOUNT.ID.eq(CUSTOMER.ACCOUNT))
				
				.leftOuterJoin(CREDITOR).on(CREDITOR.REGISTRY.eq(INVOICE.REGISTRY))
				.leftOuterJoin(CREDITOR_ACCOUNT).on(CREDITOR_ACCOUNT.ID.eq(CREDITOR.ACCOUNT))
				
				.leftOuterJoin(SUPPLIER).on(SUPPLIER.REGISTRY.eq(INVOICE.REGISTRY))
				.leftOuterJoin(SUPPLIER_ACCOUNT).on(SUPPLIER_ACCOUNT.ID.eq(SUPPLIER.ACCOUNT))
				
				.leftOuterJoin(INVOICE_ADDRESS).on(INVOICE.ID.eq(INVOICE_ADDRESS.INVOICE))
				.leftOuterJoin(INVOICE_CHILD_GEOZONE).on(INVOICE_CHILD_GEOZONE.ID.eq(INVOICE_ADDRESS.GEOZONE))
				.leftOuterJoin(INVOICE_GEOTREE).on(INVOICE_GEOTREE.CHILD.eq(INVOICE_ADDRESS.GEOZONE))
				.leftOuterJoin(INVOICE_PARENT_GEOZONE).on(INVOICE_PARENT_GEOZONE.ID.eq(INVOICE_GEOTREE.PARENT))

				.leftOuterJoin(RADDRESS).on(RADDRESS.ID.eq(INVOICE.RADDRESS))
				.leftOuterJoin(REGISTRY_CHILD_GEOZONE).on(REGISTRY_CHILD_GEOZONE.ID.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(REGISTRY_GEOTREE).on(REGISTRY_GEOTREE.CHILD.eq(RADDRESS.GEOZONE))
				.leftOuterJoin(REGISTRY_PARENT_GEOZONE).on(REGISTRY_PARENT_GEOZONE.ID.eq(REGISTRY_GEOTREE.PARENT))

				.where(INVOICE_PROPERTIES.getConditions(filter))
				.fetch();
	}
	
	public static List<Invoice> getInvoices(Domain domain, User user, InvoiceFilterOLD filter) {
		try (CloseableAONContext ctx = AONContext.getAONContext(domain, user)){
			Result<Record> a = getSaleInvoices(ctx, filter);
			HashMap<Integer, Invoice> invoiceMap = new HashMap<>();
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
				if(!invoiceMap.containsKey(inv.getId())) {
					inv.setFileUrl(getInvoicesUrl(domain, user.getLogin(), inv));
					invoiceMap.put(inv.getId(), inv);
				}

				InvoiceDetail id = InvoiceDetailFiller.build(r);
				InvoiceTax it = InvoiceTaxFiller.build(r);
				
				Integer detailIndex = null;
				for(Integer j = 0; j < invoiceMap.get(inv.getId()).getDetails().size(); j++) {
					InvoiceDetail aux = invoiceMap.get(inv.getId()).getDetails().get(j);
					if(aux.getId().equals(id.getId())){
						detailIndex = j;
					}
				}
				
				if(detailIndex != null) {
					invoiceMap.get(inv.getId()).getDetails().get(detailIndex).getInvoiceTaxes().add(it);					
				} else {
					id.getInvoiceTaxes().add(it);
					invoiceMap.get(inv.getId()).getDetails().add(id);
				}
			}
			return new LinkedList<>(invoiceMap.values());
		}
	}
	
	public static String getInvoicesUrl(Domain domain, String login, Invoice invoice) {
		String url = "";
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getAttachModuleProperty().eq(invoice.getId()), AttachType.INVOICE, false);
		if(attach != null && !attach.isEmpty()) {
			JSONObject data = new JSONObject();
			data.put("domain_name", domain.getName());
			data.put("domain_id", domain.getId());
			data.put("id", attach.getId());
			data.put("attach_type", AttachType.INVOICE.getName());
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			url = "https://" + domain.getName() + "/ms/api/file/" +  result;
		} else if(invoice.isSales()) {
			JSONObject json = new JSONObject();
			json.put(IJsonNames.ID, invoice.getId());
			json.put(IJsonNames.SOURCE, "invoice");
			json.put("domain_id", invoice.getDomain());
			json.put("domain_name", domain.getName());
			json.put("login", login);
			String result = Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
			url = "https://" + domain.getName() + "/ms/api/download_invoice_pdf?json=" + result;	
		}
		return url;
	}
	
	private static class InvoiceFiller extends Filler implements Function<Record,Invoice> {

		@Override
		public Invoice apply(Record r) {
			return buildInvoice(r);
		}
		
		static Invoice buildInvoice(Record r) {
			return new Invoice()
				.setId(r.getValue(INVOICE.ID))
				.setDomain(r.getValue(INVOICE.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class, r.getValue(INVOICE.TYPE)))
				.setSeries(r.getValue(INVOICE.SERIES))
				.setNumber(r.getValue(INVOICE.NUMBER))
				.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
				.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
				.setTaxDate(r.getValue(INVOICE.TAX_DATE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class, r.getValue(INVOICE.SECURITY_LEVEL)))
				.setRegistry( r.getValue(INVOICE.REGISTRY))
				.setRegistryDocument(r.getValue(INVOICE.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,r.getValue(INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(r.getValue(INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(r.getValue(INVOICE.RNAME))
				.setRegistryAddress(r.getValue(INVOICE.RADDRESS))
				.setScope(checkField(r, SCOPE.ID)
						? ScopeFiller.buildScope(r)
						: new Scope().setId(r.getValue(INVOICE.SCOPE)))
				.setActivity(checkField(r, ENTERPRISE_ACTIVITY.ID)
						? EnterpriseActivityFiller.build(r)
						: new EnterpriseActivity().setId(r.getValue(INVOICE.ACTIVITY)))	
				.setInvestAsset(r.getValue(INVOICE.INVEST_ASSET))
				.setProject(r.getValue(INVOICE.PROJECT))
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class, r.getValue(INVOICE.RECTIFICATION_TYPE)))	
				.setRectificationInvoice(
					Optional.ofNullable( r.getValue(INVOICE.RECTIFICATION_INVOICE) ).map( rid -> new Invoice().setId(rid)).orElse(null)
				)	
				.setTransaction(InvoiceTransactionType.safeValueOf(r.getValue(INVOICE.TRANSACTION)))
				.setRecorded(r.getValue(INVOICE.STATUS) != null && r.getValue(INVOICE.STATUS) == 1 )	
				.setSurcharge(r.getValue(INVOICE.SURCHARGE) == 1 )	
				.setWithholding(r.getValue(INVOICE.WITHHOLDING) == 1 )	
				.setWithholdingFarmer(r.getValue(INVOICE.WITHHOLDING_FARMER) == 1 )	
				.setVatAccrualPayment(r.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1 )	
				.setInvestment(r.getValue(INVOICE.INVESTMENT) == 1 )	
				.setService(r.getValue(INVOICE.SERVICE) == 1 )	
				.setAdvance(r.getValue(INVOICE.ADVANCE) == 1 )	
				.setTaxableBase(r.getValue(INVOICE.TAXABLE_BASE))	
				.setVatQuota(r.getValue(INVOICE.VAT_QUOTA))	
				.setRetentionQuota(r.getValue(INVOICE.RETENTION_QUOTA))	
				.setTotal(r.getValue(INVOICE.TOTAL))	
				.setComments(r.getValue(INVOICE.COMMENTS))
				.setSeller(getValue(r, INVOICE.SELLER))
				.setCreationDate(r.getValue(INVOICE.CREATION_DATE))
				.setCreationUser(r.getValue(INVOICE.CREATION_USER))
				.setModificationDate(r.getValue(INVOICE.MODIFICATION_DATE))
				.setModificationUser(r.getValue(INVOICE.MODIFICATION_USER));
		}
	}
	
	private static class InvoiceTaxFiller extends Filler implements Function<Record, InvoiceTax> {

		@Override
		public InvoiceTax apply(Record r) {
			return build(r);
		}
		
		public static InvoiceTax build(Record r) {
			InvoiceTax tax = new InvoiceTax()
				.setId(getValue(r, INVOICE_TAX.ID))
				.setDomain(getValue(r, INVOICE_TAX.DOMAIN))
				.setTaxType(TaxType.safeValueOf(getValue(r, INVOICE_TAX.TAX_TYPE)))
				.setPercentage(getDouble(r, INVOICE_TAX.PERCENTAGE))
				.setBase(getDouble(r, INVOICE_TAX.BASE))
				.setSurcharge(getDouble(r, INVOICE_TAX.SURCHARGE))
				.setQuota(getDouble(r, INVOICE_TAX.QUOTA))
				.setSurchargeQuota(getDouble(r, INVOICE_TAX.SURCHARGE_QUOTA))
				.setVatDeductionType(VatDeductionType.safeValueOf(getValue(r, INVOICE_TAX.VAT_DEDUCTION_TYPE)))
				.setWithholdingType(WithholdingType.safeValueOf(getValue(r, INVOICE_TAX.WITHHOLDING_TYPE)))
				.setDeductiblePercent(getDouble(r, INVOICE_TAX.DEDUCTIBLE_PERCENT))
				.setDeductibleQuota(getDouble(r, INVOICE_TAX.DEDUCTIBLE_QUOTA));

			if(tax.getPercentage() > 0 && tax.getQuota() == 0.0) {
				tax.setQuota(AonMathUtils.round(tax.getBase() * tax.getPercentage() / 100));
			}
			if(tax.getSurcharge() > 0 && tax.getSurchargeQuota() == 0.0) {
				tax.setSurchargeQuota(AonMathUtils.round(tax.getBase() * tax.getSurcharge() / 100));
			}
			return tax;

		}
	}
	
}
