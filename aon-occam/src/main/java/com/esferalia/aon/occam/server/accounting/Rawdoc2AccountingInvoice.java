package com.esferalia.aon.occam.server.accounting;

import java.util.LinkedList;
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
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
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
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
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
		if (invoice.getId() == null || isRawdoc(status)) {
			invoice = AON_SOLUTIONS.validateInvoice(domain, user, invoice);
			invoice.getDetails().stream().forEach(d -> d.setSource(InvoiceSource.ACCOUNT));
		} else {
			invoice = AON_SOLUTIONS.getInvoice(domainName, domainId, login, invoice.getId());
		}
		
		return tedi2Aon(domain, user, invoice, json);
	}
	
	private static RawdocStatus getRawdocStatus(String status) {
		return RawdocStatus.safeValueOf(status);
	}
	
	private static boolean isRawdoc(String status) {
		return getRawdocStatus(status) != null;
	}
	
	public static AccountingInvoice tedi2Aon(Domain domain, User user, Invoice invoice, JSONObject ti)  {
		System.out.println( "JSON" );
		System.out.println( ti.toString(1) );

		
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

			String category = ti.optString("category");
			ai.setInvoice(invoice);
			ai.setRegistry(getRegistry(ctx, invoice));
			
			
			fillWithholding(ctx, aonConfig, ai);
			
			ai.setVats(new LinkedList<>());

			for (InvoiceDetail detail : invoice.getDetails()) {
				Account expAccount = getExpAccount(ctx, category , detail);
				InvoiceTax detailTax = detail.getInvoiceTaxes().stream()
					.filter(f -> f.getTaxType().equals(TaxType.VAT))
					.findFirst()
					.orElse(new InvoiceTax());
				double base = detail.isPrepayment()? detail.getTaxableBase() : detailTax.getBase();
				if (ai.isUndeductible()) {
					base = detailTax.getBase() + detailTax.getQuota();
					detail.setPrepayment( false );
				}
				InvoiceVAT vat = new InvoiceVAT()
					.setInvoiceDetail(detail)
					.setPrepayment(detail.isPrepayment())
					.setVatDeductionType(VatDeductionType.WITH_RIGHT)
					.setBase(base)
					.setPercentage(detailTax.getPercentage())
					.setQuota(detailTax.getQuota())
					.setSurcharge(detailTax.getSurcharge())
					.setSurchargeQuota(detailTax.getSurchargeQuota())
					// .setInvestAsset(ivs.get(j).getInvestAsset())
					.setDeductiblePercent(detailTax.getDeductiblePercent())
					.setDeductibleQuota(detailTax.getDeductibleQuota())
					.setWithholding(invoice.isWithholding() && !detail.isPrepayment())
					
					.setExpAccountId(expAccount.getId())
					.setExpAccountCode(expAccount.getCode())
					.setExpAccountDescription(expAccount.getDescription())

					.setOutputAccountCode(outputAccount.getCode())
					.setOutputAccountDescription(outputAccount.getDescription())
					.setOutputAccountId(outputAccount.getId())

					.setInputAccountCode(inputAccount.getCode())
					.setInputAccountDescription(inputAccount.getDescription()).setInputAccountId(inputAccount.getId())
					.setAdjAccountCode(adjAccount != null ? adjAccount.getCode() : null)
					.setAdjAccountDescription(adjAccount != null ? adjAccount.getDescription() : null)
					.setAdjAccountId(adjAccount != null ? adjAccount.getId() : null);
				ai.setPrepayments( ai.hasPrepayments() ||  vat.isPrepayment() );
				ai.addVat(vat);
			}
						
			ai.setAccountEntry(InvoiceRecorderDAO.getEntryBase(ctx, aonConfig, ai.getInvoice()));

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
			expAccount = detail.getAccount() != null
				? ACCOUNTING.getAccount(ctx, detail.getAccount())
				: ACCOUNTING.getAccount(ctx, category);
		}
		if (expAccount == null) {
			expAccount = new Account();
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
				.setAccountCode(retentionAccount.getCode())
				.setAccountDescription(retentionAccount.getDescription())
				.setAccountId(retentionAccount.getId());
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
