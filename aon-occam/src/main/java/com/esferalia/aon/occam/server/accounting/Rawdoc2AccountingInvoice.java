package com.esferalia.aon.occam.server.accounting;

import java.util.Optional;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceRecorderDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Rawdoc2AccountingInvoice {

	private Rawdoc2AccountingInvoice() {
	}

	public static AccountingInvoice getAccountingInvoice(Domain domain, User user, JSONObject json) {
		return getAccountingInvoice(domain.getName(), domain.getId(), user.getLogin(), json);
	}
	
	public static AccountingInvoice getAccountingInvoice(Domain domain, String login, JSONObject json) {
		return getAccountingInvoice(domain.getName(), domain.getId(), login, json);
	}
	
	public static AccountingInvoice getAccountingInvoice(String domainName, int domainId, String login, JSONObject json) {
		Domain domain = new Domain().setName(domainName).setId(domainId);
		User user = new User().setLogin(login);
		String status = JsonUtils.getString(json, IJsonNames.STATUS);
		Invoice invoice = InvoiceJSON.fromJSON(json);
		Rawdoc rawdoc = null;
		if (invoice.getId() == null || isRawdoc(status)) {
			if (isRawdoc(status) && invoice.getId() != null) {
				rawdoc = AON.getRawdocFull(domainName, domainId, login, invoice.getId());
				invoice.setId(null);
			}
			invoice = AON_SOLUTIONS.validateInvoice(domain, user, invoice);
			// Se pone a 0 porque es rawdoc / ocr y hasta que no se grabe no tiene que tener un número asignado.
			invoice.setNumber(0);
			invoice.getDetails().stream().forEach(d -> d.setSource(InvoiceSource.ACCOUNT));
		} else {
			invoice = AON_SOLUTIONS.getInvoice(domainName, domainId, login, invoice.getId());
		}
		AccountingInvoice ai = tedi2Aon(domain, user, invoice, json); 
		if(rawdoc != null && rawdoc.getId() != null) {
			Attach attach = new Attach();
			attach.setId(rawdoc.getId());
			attach.setAttachType(AttachType.INVOICE);
			attach.setMimeType( rawdoc.getMimeType() );
			attach.setData(rawdoc.getData());
			attach.setAttachURL("RAWDOC");
			ai.setAttach(attach);
			ai.getInvoice().setRawdocId(rawdoc.getId());
		}
		return ai;
	}
	
	private static RawdocStatus getRawdocStatus(String status) {
		return RawdocStatus.safeValueOf(status);
	}
	
	private static boolean isRawdoc(String status) {
		return getRawdocStatus(status) != null;
	}
	
	public static AccountingInvoice tedi2Aon(Domain domain, User user, Invoice invoice, JSONObject ti)  {
//		System.out.println( "JSON" );
//		System.out.println( ti.toString(1) );
		Occam occam = new Occam( )
			.setDomainName( domain.getName())
			.setDomain(domain.getId())
			.setUser(user.getLogin());
		
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			AonConfiguration aonConfig = AON.getConfiguration(ctx);
			AccountingInvoice ai = new AccountingInvoice();
			ai.setWorkplace(aonConfig.getWorkplaces().get(0).getId());
			
			
			Account outputAccount = Optional.ofNullable( aonConfig.accounting().getDefaultChargedVatAccount() )
				.orElse( AccountDAO.ensureOutputVATAccount(ctx, domain.getId()) );
			Account inputAccount = Optional.ofNullable( aonConfig.accounting().getDefaultPaidVatAccount() )
				.orElse( AccountDAO.ensureInputVATAccount(ctx, domain.getId()) );
			Account adjAccount = aonConfig.accounting().getVatNegativeAdjustAccount();
			Account adjDirectTaxAccount = aonConfig.accounting().getDirectTaxAdjustAccount();

			String category = ti.optString("category");
			ai.setInvoice(invoice);
			ai.setRegistry(getRegistry(ctx, invoice));
			
			
			fillWithholding(ctx, aonConfig, ai);
			
			for (InvoiceDetail detail : invoice.getDetails()) {
				detail.setExpAccount(getExpAccount(ctx, category , detail));
				if (detail.getVatTax().isPresent()) {
					detail.getVatTax().get()
						.setOutputAccount(outputAccount)
						.setInputAccount(inputAccount)
						.setAdjAccount(adjAccount)
						.setAdjDirectTaxAccount( adjDirectTaxAccount )
					;
				}
				ai.setPrepayments( ai.hasPrepayments() ||  detail.isPrepayment() );
			}
						
			ai.setAccountEntry(InvoiceRecorderDAO.getEntryBase(ctx, aonConfig, ai.getInvoice()));
			
			if (invoice.getActivity().isPresent()) {
				ai.getAccountEntry().setActivity(invoice.getActivity().get().getId());
				ai.getAccountEntry().setActivityDescription(invoice.getActivity().get().getDescription());
			} else {
				ai.getAccountEntry().setActivity(null);
				ai.getAccountEntry().setActivityDescription(null);
			}
			
			
			fillAttach( ctx, ai, ti);
			return ai;
		}
	}
	
	private static AccountingInvoice fillAttach(AONContext ctx, AccountingInvoice ai,  JSONObject ti) {
		JSONObject file = ti.optJSONObject("file");
		if (file != null) {
			String s3Key = file.optString("s3Key");
			String url = file.optString("url");
			if (AonStringUtils.isNotBlank(url) && AonStringUtils.isNotBlank(s3Key)) {
				Attach attach = new Attach()
					.setAttachType(AttachType.INVOICE)
					.setAttachURL( url );
				String contentType = file.optString("content_type");
				if (AonStringUtils.isNotBlank(url)) {
					MimeType mimeType = MimeType.safeValueFromContenType(contentType);
					attach.setMimeType( mimeType );
				}
				ai.setAttach( attach );
			}
		}
		return ai;
	}


	private static Account getExpAccount(AONContext ctx, String category, InvoiceDetail detail) {
		Account expAccount = null;
		if (AonStringUtils.isNotBlank(category)) {
			expAccount = (detail.getExpAccount() != null && detail.getExpAccount().getId() != null)
				? ACCOUNTING.getAccount(ctx, detail.getExpAccount().getId())
				: ACCOUNTING.getAccount(ctx, category);
			detail.setExpAccount(expAccount);
		}
		return expAccount;
	}

	private static AccountingInvoice fillWithholding(AONContext ctx,  AonConfiguration aonConfig, AccountingInvoice ai) {
		if(ai.getInvoice().isWithholding()) {
			InvoiceBreakdown tax = ai.getInvoice().getBreakdown()
				.stream()
				.filter(br -> br.getTaxType().equals(TaxType.RETENTION))
				.findFirst()
				.orElse(new InvoiceBreakdown());
			Account retentionAccount = getDefaultRetentionAccount(ctx, aonConfig, ai.getInvoice().getDomain(), ai.getInvoice());
			InvoiceWithholding iw = new InvoiceWithholding()
				.setWithholdingType(tax.getWithholdingType())
				.setBase(tax.getBase())
				.setPercentage(tax.getPercentage())
				.setQuota(tax.getQuota())
				.setAccount(retentionAccount)
			;
			ai.setWithholdingData(iw); 
		}
		return ai;
	}

	private static AccountingRegistry getRegistry(AONContext ctx, Invoice invoice) {
		AccountDAO.ensureRegistryAccounts(ctx, invoice.getDomain());
		if(invoice.isSales()) return getCustomer(ctx, invoice.getRegistry());
		else  if(invoice.isPurchase()) return getSupplier(ctx, invoice.getRegistry());
		else return getCreditor(ctx, invoice.getRegistry());
	}
	
	private static AccountingRegistry getCustomer(AONContext ctx, Integer id) {
		Customer customer = AON.getCustomer(ctx, f -> f.getIdProperty().eq(id));
		Account account = ACCOUNTING.getAccount(ctx, customer.getAccount()); 
		if(account == null) account = new Account();
		return new AccountingRegistry()
				.setType(AccountingRegistryType.CUSTOMER)
				.setId(customer.getId())
				.setName(customer.getName())
				.setAccountId(customer.getAccount())
				.setAccountCode(account.getCode())
				.setAccountDescription(account.getDescription());
	}
	
	private static AccountingRegistry getSupplier(AONContext ctx, Integer id) {
		Supplier supplier = AON.getSupplier(ctx, f -> f.getIdProperty().eq(id)).orElse(new Supplier());
		Account account = ACCOUNTING.getAccount(ctx, supplier.getAccount()); 
		if(account == null) account = new Account();
		return new AccountingRegistry()
				.setType(AccountingRegistryType.SUPPLIER)
				.setId(supplier.getId())
				.setName(supplier.getName())
				.setAccountId(supplier.getAccount())
				.setAccountCode(account.getCode())
				.setAccountDescription(account.getDescription());
	}
	
	private static AccountingRegistry getCreditor(AONContext ctx, Integer id) {
		
		Creditor creditor = AON.getCreditor(ctx, f -> f.getIdProperty().eq(id)).orElse(new Creditor());
		Account account = ACCOUNTING.getAccount(ctx, creditor.getAccount()); 
		if(account == null) account = new Account();
		return new AccountingRegistry()
				.setType(AccountingRegistryType.SUPPLIER)
				.setId(creditor.getId())
				.setName(creditor.getName())
				.setAccountId(creditor.getAccount())
				.setAccountCode(account.getCode())
				.setAccountDescription(account.getDescription());
	}
	
	private static Account getDefaultRetentionAccount(AONContext ctx, AonConfiguration aonConfig, Integer domain, Invoice invoice) {
		return (invoice.isSales()) 
			? getDefaultPaidRetentionAccount(ctx, aonConfig, domain)
			: getDefaultChargedRetentionAccount(ctx, aonConfig, domain);
	}
	private static Account getDefaultPaidRetentionAccount(AONContext ctx, AonConfiguration aonCtx, Integer domain) {
		return Optional.ofNullable( aonCtx.accounting().getDefaultPaidRetAccount() )
				.orElse( AccountDAO.ensurePaidRetentionAccount(ctx, domain) );
	}
	
	private static Account getDefaultChargedRetentionAccount(AONContext ctx, AonConfiguration aonCtx, Integer domain) {
		return Optional.ofNullable( aonCtx.accounting().getDefaultChargedRetAccount() )
			.orElse( AccountDAO.ensureChargedRetentionAccount(ctx, domain) );
	}
	
}
