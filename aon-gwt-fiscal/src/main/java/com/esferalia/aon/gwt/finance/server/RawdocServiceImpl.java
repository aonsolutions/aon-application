package com.esferalia.aon.gwt.finance.server;

import java.util.Base64;
import java.util.LinkedList;

import org.json.JSONObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.RawdocService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
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
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;

@WebServlet(name = "Rawdoc Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Rawdoc" })
public class RawdocServiceImpl extends AonStatelessRemoteServiceServlet implements RawdocService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public LinkedList<Rawdoc> getRawdocs(String domainName, int domain, String user, RawdocParams params, int offset,
			int limit) throws AonCoreException {
		return AON.getRawdocs(domainName, domain,user, params, offset, limit );
	}
	
	@Override
	public LinkedList<RawdocDomainData> getDomainData(String domainName, int domain, String user, int searchDomain) throws AonCoreException {
		return AON.getRawdocDomainData(domainName, domain,user,searchDomain);
	}
	
	@Override
	public TediResult parse(String domainName, int domain, String user, Integer rawdocId) throws AonCoreException {
		String url = null;
		if (AON.rawdocHasData(domainName, domain,user,rawdocId)) {
//			StringBuilder baseURL = new StringBuilder();
//			baseURL.append( getThreadLocalRequest().getContextPath() );
// ---------------------
			HttpServletRequest req = getThreadLocalRequest();
			String serverName = req.getServerName();
			int serverPort = req.getServerPort();
			StringBuilder baseURL = new StringBuilder();
			if (serverPort != 80 && serverPort != 443) {
				String scheme = req.getScheme();
				baseURL
					.append(scheme).append(":")
					.append("//").append(serverName)
					.append(":").append(serverPort);
			}
			baseURL.append( getThreadLocalRequest().getContextPath() );
//----------------------
			String params = "domain="+ domain + "&id=" +  rawdocId;
			params = Base64.getEncoder().encodeToString(params.getBytes());
			url = baseURL.toString() + "/ms/download_rawdoc" 
					+ "/" + domainName 
					+ "/" + user 
					+ "/" +  params;
		}
		try {
			TediContext tctx = new TediContext()
				.setDomainName(domainName)
				.setDomain(domain)
				.setUser(user);
			TediResult result = TEDI.fromRawdoc(tctx, rawdocId );
			result.getAccountingInvoice()
				.setFromRawdoc(true)
				.setTediParsed(true)
				.getAttach().setAttachURL(url);
			return result;
		} catch ( TediException t) {
			t.printStackTrace();
			throw new AonCoreException(t);
		}			
	}
	@Override
	public void delete(String domainName, int domain, String user, Integer rawdocId) {
		AON.rawdocDelete(domainName, domain,user,rawdocId);
	}
	@Override
	public void toDraft(String domainName, int domain, String user, Integer rawdocId) throws AonCoreException {
		AON.rawdocToDraft(domainName, domain,user,rawdocId);
	}

	@Override
	public void toRejected(String domainName, int domain, String user, Integer rawdocId, String reason) throws AonCoreException {
		AON.rawdocToRejected(domainName, domain,user,rawdocId,reason);
	}

	@Override
	public void toInbox(String domainName, int domain, String user, Integer rawdocId) throws AonCoreException {
		AON.rawdocToInbox(domainName, domain,user,rawdocId);
	}

	@Override
	public AccountingInvoice getAccountingInvoice(String domainName, int domainId, String login, String invoiceStr) {
		Domain domain = new Domain().setName(domainName).setId(domainId);
		User user = new User().setLogin(login);
		JSONObject json = new JSONObject(invoiceStr);
		String status = JsonUtils.getString(json, IJsonNames.STATUS);
		Invoice invoice = InvoiceJSON.fromJSON(json);
		if(invoice.getId() == null || isRawdoc(status)) {
			invoice = AON_SOLUTIONS.validateInvoice(domain, user, invoice);
			invoice.getDetails().stream().forEach(d -> d.setSource(InvoiceSource.ACCOUNT));
		} else invoice = AON_SOLUTIONS.getInvoice(domainName, domainId, login, invoice.getId());

		return tedi2Aon(domain, user, invoice, json);
	}
	
	private static RawdocStatus getRawdocStatus(String status) {
		return RawdocStatus.safeValueOf(status);
	}
	
	private static boolean isRawdoc(String status) {
		return getRawdocStatus(status) != null;
	}
	
	public static AccountingInvoice tedi2Aon(Domain domain, User user, Invoice invoice, JSONObject ti)  {
		AccountingInvoice ai = new AccountingInvoice();

		AonConfiguration aonCtx = AON.getConfiguration(domain.getName(), domain.getId(), user.getLogin());
	
		ai.setWorkplace(aonCtx.getWorkplaces().get(0).getId());

		String category = ti.optString("category");
		ai.setInvoice(invoice);
		ai.setRegistry(getRegistry(domain, user, invoice));
		
		if(invoice.isWithholding()) {
			InvoiceBreakdown tax = invoice.getBreakdown().stream().filter(br -> br.getTaxType().equals(TaxType.RETENTION)).findFirst().orElse(new InvoiceBreakdown());

			Account retentionAccount = (invoice.isSales()) ? getDetaultPaidRetAccount(domain, user, aonCtx)
					: getDefaultChargedRetAccount(domain, user, aonCtx);

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
			
		ai.setVats(new LinkedList<>());
		

		for (InvoiceDetail detail : invoice.getDetails()) {
			detail.getAccount();

			Account outputAccount = aonCtx.accounting().getDefaultChargedVatAccount();
			if (outputAccount == null || outputAccount.getId() == null) {
				outputAccount = getIVArepercutido(domain, user);
			}
			Account inputAccount = aonCtx.accounting().getDefaultPaidVatAccount();
			if (inputAccount == null || outputAccount.getId() == null) {
				inputAccount = getIVAsoportado(domain, user);
			}
			Account adjAccount = aonCtx.accounting().getVatNegativeAdjustAccount();
			Account expAccount = null;
			if(!AonStringUtils.isBlank(category)) {
				expAccount = detail.getAccount() != null
					? ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), detail.getAccount())
					: ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), category);
				if (expAccount == null) {
					expAccount = new Account().setCode(category)
						.setDescription("SIN DESCRIPCIÓN (CREADO DESDE TEDI INVOICE)").setAlias("SIN DESCRIPCIÓN")
						.setDomain(domain.getId()).setActive(true);
					checkNivelInferior(domain, user, category);
					expAccount = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), expAccount);
				}
			} else expAccount = new Account();
		
			InvoiceTax detailTax = detail.getInvoiceTaxes().stream().filter(f -> f.getTaxType().equals(TaxType.VAT)).findFirst().orElse(new InvoiceTax());
			
			InvoiceVAT vat = new InvoiceVAT()
					.setInvoiceDetail(detail)
					.setPrepayment(detail.isPrepayment())
					.setVatDeductionType(VatDeductionType.WITH_RIGHT)
					.setBase(detailTax.getBase())
					.setPercentage(detailTax.getPercentage())
					.setQuota(detailTax.getQuota())
					.setSurcharge(detailTax.getSurcharge())
					.setSurchargeQuota(detailTax.getSurchargeQuota())
					// .setInvestAsset(ivs.get(j).getInvestAsset())
					.setDeductiblePercent(detailTax.getDeductiblePercent())
					.setDeductibleQuota(detailTax.getDeductibleQuota())
					.setWithholding(detailTax.isWithholding())
					
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

			ai.addVat(vat);
		}

		ai.setAccountEntry(getEntryBase(domain, user.getLogin(), aonCtx, ai));

		return ai;
	}
	
	private static AccountingRegistry getRegistry(Domain domain, User user, Invoice invoice) {
		checkRegistryAccounts(domain, user);
		if(invoice.isSales()) return getCustomer(domain, user, invoice.getRegistry());
		else  if(invoice.isPurchase()) return getSupplier(domain, user, invoice.getRegistry());
		else return getCreditor(domain, user, invoice.getRegistry());
	}
	
	private static AccountingRegistry getCustomer(Domain domain, User user, Integer id) {
		Customer customer = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(id));
		Account account = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), customer.getAccount()); 
		if(account == null) account = new Account();
		return new AccountingRegistry()
				.setType(AccountingRegistryType.CUSTOMER)
				.setId(customer.getId())
				.setName(customer.getName())
				.setAccountId(customer.getAccount())
				.setAccountCode(account.getCode())
				.setAccountDescription(account.getDescription());
	}
	
	private static AccountingRegistry getSupplier(Domain domain, User user, Integer id) {
		Supplier supplier = AON.getSupplier(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(id)).orElse(new Supplier());
		Account account = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), supplier.getAccount()); 
		if(account == null) account = new Account();
		return new AccountingRegistry()
				.setType(AccountingRegistryType.SUPPLIER)
				.setId(supplier.getId())
				.setName(supplier.getName())
				.setAccountId(supplier.getAccount())
				.setAccountCode(account.getCode())
				.setAccountDescription(account.getDescription());
	}
	
	private static AccountingRegistry getCreditor(Domain domain, User user, Integer id) {
		Creditor creditor = AON.getCreditor(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(id)).orElse(new Creditor());
		Account account = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), creditor.getAccount()); 
		if(account == null) account = new Account();
		return new AccountingRegistry()
				.setType(AccountingRegistryType.SUPPLIER)
				.setId(creditor.getId())
				.setName(creditor.getName())
				.setAccountId(creditor.getAccount())
				.setAccountCode(account.getCode())
				.setAccountDescription(account.getDescription());
	}
	
	private static void checkRegistryAccounts(Domain domain, User user) {
		String code1 = "4";
		Account acc1 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code1);
		if(acc1 == null || acc1.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code1)
					.setDomain(domain.getId())
					.setDescription("ACREEDORES Y DEUDEROS POR OPERACIONES COMERCIALES")
					.setActive(true));
		}
		
		String code2 = "40";
		Account acc2 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code2);
		if(acc2 == null || acc2.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code2)
					.setDomain(domain.getId())
					.setDescription("ADMINISTRACIONES PÚBLICAS")
					.setActive(true));
		}
		
		String code4 = "400";
		Account acc4 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code4);
		if(acc4 == null || acc4.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code4)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA soportado")
					.setActive(true));
		}

		String code6 = "4000";
		Account acc6 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code6);
		if(acc6 == null || acc6.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code6)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA soportado")
					.setActive(true));
		}
		
		String code8 = "41";
		Account acc8 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code8);
		if(acc8 == null || acc8.getId() == null) {
			acc8 = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code8)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA soportado")
					.setActive(true));
		}
		String code3 = "410";
		Account acc3 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code3);
		if(acc3 == null || acc3.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code3)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA repercutido")
					.setActive(true));
		}

		String code5 = "4100";
		Account acc5 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code5);
		if(acc5 == null || acc5.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code5)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA repercutido")
					.setActive(true));
		}
		
		String code7 = "43";
		Account acc7 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code7);
		if(acc7 == null || acc7.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code7)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA repercutido")
					.setActive(true));
		}
		String code9 = "430";
		Account acc9 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code9);
		if(acc9 == null || acc9.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code9)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA repercutido")
					.setActive(true));
		}
		
		String code10 = "4300";
		Account acc10 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code10);
		if(acc10 == null || acc10.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code10)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA repercutido")
					.setActive(true));
		}
	}
	
	private static Account getDetaultPaidRetAccount(Domain domain, User user, AonConfiguration aonCtx) {
		Account acc = aonCtx.accounting().getDefaultPaidRetAccount();
		if(acc == null || acc.getId() == null) {
			acc = getRaidRet(domain, user);
		}
		return acc;
	}
	
	private static Account getDefaultChargedRetAccount(Domain domain, User user, AonConfiguration aonCtx) {
		Account acc = aonCtx.accounting().getDefaultChargedRetAccount();
		if(acc == null || acc.getId() == null) {
			acc = getChargedRet(domain, user);
		}
		return acc;
	}
	
	private static Account getRaidRet(Domain domain, User user) {
		String code1 = "4";
		Account acc1 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code1);
		if(acc1 == null || acc1.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code1)
					.setDomain(domain.getId())
					.setDescription("ACREEDORES Y DEUDEROS POR OPERACIONES COMERCIALES")
					.setActive(true));
		}
		
		String code2 = "47";
		Account acc2 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code2);
		if(acc2 == null || acc2.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code2)
					.setDomain(domain.getId())
					.setDescription("ADMINISTRACIONES PÚBLICAS")
					.setActive(true));
		}
		
		String code4 = "473";
		Account acc4 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code4);
		if(acc4 == null || acc4.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code4)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, retenciones y pagos a cuenta.")
					.setActive(true));
		}

		String code6 = "4730";
		Account acc6 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code6);
		if(acc6 == null || acc6.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code6)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, retenciones y pagos a cuenta.")
					.setActive(true));
		}
		
		String code8 = "473000000";
		Account acc8 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code8);
		if(acc8 == null || acc8.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code8)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, retenciones y pagos a cuenta.")
					.setActive(true));
			acc8 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code8);
			AON.insertApplicationParameter(domain.getName(), domain.getId(), user.getLogin(), new ApplicationParameter()
					.setDomain(domain.getId())
					.setName(AppParam.ACC_DEFAULT_PAID_RET_ACC.name()))
					.setValue(acc8.getId().toString());
		}
		return acc8;
	}
	
	private static Account getChargedRet(Domain domain, User user) {
		String code1 = "4";
		Account acc1 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code1);
		if(acc1 == null || acc1.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code1)
					.setDomain(domain.getId())
					.setDescription("ACREEDORES Y DEUDEROS POR OPERACIONES COMERCIALES")
					.setActive(true));
		}
		
		String code2 = "47";
		Account acc2 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code2);
		if(acc2 == null || acc2.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code2)
					.setDomain(domain.getId())
					.setDescription("ADMINISTRACIONES PÚBLICAS")
					.setActive(true));
		}
		
		String code4 = "475";
		Account acc4 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code4);
		if(acc4 == null || acc4.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code4)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública acreedora por conceptos fiscales.")
					.setActive(true));
		}

		String code6 = "4751";
		Account acc6 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code6);
		if(acc6 == null || acc6.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code6)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, acreedora por retenciones practicadas.")
					.setActive(true));
		}
		
		String code8 = "475100000";
		Account acc8 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code8);
		if(acc8 == null || acc8.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code8)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, acreedora por retenciones practicadas.")
					.setActive(true));
			acc8 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code8);
			AON.insertApplicationParameter(domain.getName(), domain.getId(), user.getLogin(), new ApplicationParameter()
					.setDomain(domain.getId())
					.setName(AppParam.ACC_DEFAULT_CHARGED_RET_ACC.name()))
					.setValue(acc8.getId().toString());
		}
		return acc8;
	}
	
	private static Account getIVArepercutido(Domain domain, User user) {
		String code1 = "4";
		Account acc1 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code1);
		if(acc1 == null || acc1.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code1)
					.setDomain(domain.getId())
					.setDescription("ACREEDORES Y DEUDEROS POR OPERACIONES COMERCIALES")
					.setActive(true));
		}
		
		String code2 = "47";
		Account acc2 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code2);
		if(acc2 == null || acc2.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code2)
					.setDomain(domain.getId())
					.setDescription("ADMINISTRACIONES PÚBLICAS")
					.setActive(true));
		}
		
		String code3 = "477";
		Account acc3 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code3);
		if(acc3 == null || acc3.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code3)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA repercutido")
					.setActive(true));
		}

		String code5 = "4770";
		Account acc5 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code5);
		if(acc5 == null || acc5.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code5)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA repercutido")
					.setActive(true));
		}
		
		String code7 = "477000000";
		Account acc7 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code7);
		if(acc7 == null || acc7.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code7)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA repercutido")
					.setActive(true));
			acc7 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code7);
			AON.insertApplicationParameter(domain.getName(), domain.getId(), user.getLogin(), new ApplicationParameter()
						.setDomain(domain.getId())
						.setName(AppParam.ACC_DEFAULT_CHARGED_VAT_ACC.name()))
						.setValue(acc7.getId().toString());
		}
		
		return acc7;
	}
	
	private static Account getIVAsoportado(Domain domain, User user) {
		String code1 = "4";
		Account acc1 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code1);
		if(acc1 == null || acc1.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code1)
					.setDomain(domain.getId())
					.setDescription("ACREEDORES Y DEUDEROS POR OPERACIONES COMERCIALES")
					.setActive(true));
		}
		
		String code2 = "47";
		Account acc2 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code2);
		if(acc2 == null || acc2.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code2)
					.setDomain(domain.getId())
					.setDescription("ADMINISTRACIONES PÚBLICAS")
					.setActive(true));
		}
		
		String code4 = "472";
		Account acc4 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code4);
		if(acc4 == null || acc4.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code4)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA soportado")
					.setActive(true));
		}

		String code6 = "4720";
		Account acc6 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code6);
		if(acc6 == null || acc6.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code6)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA soportado")
					.setActive(true));
		}
		
		String code8 = "472000000";
		Account acc8 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code8);
		if(acc8 == null || acc8.getId() == null) {
			ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
					.setCode(code8)
					.setDomain(domain.getId())
					.setDescription("Hacienda Pública, IVA soportado")
					.setActive(true));
			acc8 = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code8);
			AON.insertApplicationParameter(domain.getName(), domain.getId(), user.getLogin(), new ApplicationParameter()
					.setDomain(domain.getId())
					.setName(AppParam.ACC_DEFAULT_PAID_VAT_ACC.name()))
					.setValue(acc8.getId().toString());
		}
		return acc8;
	}
	
	private static void checkNivelInferior(Domain domain, User user, String account) {
		if(account.length() > 1) {
			String code = account.substring(0, 1);
			Account acc = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code);
			if(acc == null || acc.getId() == null) {
				ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
						.setCode(code)
						.setDomain(domain.getId())
						.setDescription("SIN DESCRIPCIÓN (CREADO DESDE IMPORTACIÓN DE SELFCONTA)")
						.setAlias("SIN DESCRIPCIÓN")
						.setActive(true));
			}
		}
		
		if(account.length() > 2) {
			String code = account.substring(0, 2);
			Account acc = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code);
			if(acc == null || acc.getId() == null) {
				ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
						.setCode(code)
						.setDomain(domain.getId())
						.setDescription("SIN DESCRIPCIÓN (CREADO DESDE IMPORTACIÓN DE SELFCONTA)")
						.setAlias("SIN DESCRIPCIÓN")
						.setActive(true));
			}
		}
		
		if(account.length() > 3) {
			String code = account.substring(0, 3);
			Account acc = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code);
			if(acc == null || acc.getId() == null) {
				ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
						.setCode(code)
						.setDomain(domain.getId())
						.setDescription("SIN DESCRIPCIÓN (CREADO DESDE IMPORTACIÓN DE SELFCONTA)")
						.setAlias("SIN DESCRIPCIÓN")
						.setActive(true));
			}
		}
		
		if(account.length() > 4) {
			String code = account.substring(0, 4);
			Account acc = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code);
			if(acc == null || acc.getId() == null) {
				ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), new Account()
						.setCode(code)
						.setDomain(domain.getId())
						.setDescription("SIN DESCRIPCIÓN (CREADO DESDE IMPORTACIÓN DE SELFCONTA)")
						.setAlias("SIN DESCRIPCIÓN")
						.setActive(true));
			}
		}
	}
	
	private static AccountEntry getEntryBase(Domain domain, String login, AonConfiguration aonCtx,AccountingInvoice ai) {
		EnterpriseActivity ea = aonCtx.getMainActivity();
		Integer activity = (ea==null?null:ea.getId());
		Integer periodId = null;
		if (ai.getInvoice().getIssueDate() != null) {
			CloseableAONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
				AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, ai.getInvoice().getIssueDate());
				if(period == null || period.getId() == null) {
					period = new AccountPeriod()
							.setDomain(domain.getId())
							.setName(Integer.toString(AonDateUtils.getYear(ai.getInvoice().getIssueDate())))
							.setInitiationDate(AonDateUtils.getYearFirstDay(ai.getInvoice().getIssueDate()))
							.setDeadline(AonDateUtils.getYearLastDay(ai.getInvoice().getIssueDate()));
					period = AccountPeriodDAO.save(ctx, period);
				}

				periodId = (period == null? null : period.getId());
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		AccountEntry accountEntry = new AccountEntry()
				.setPeriod(periodId)
				.setDomain(ai.getInvoice().getDomain())
				.setConfidential(false)
				.setEntryDate(ai.getInvoice().getIssueDate())
				.setActivity(activity)
				.setComments(ai.getInvoice().getComments())
				.setDirty(false);
		ai.getInvoice().getType().visit(ai.getInvoice(),  new IInvoiceTypeVisitor() {
			@Override public void visitUndeductible(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				accountEntry.setUndeductible(true);
			}
			@Override public void visitSales(Invoice invoice) {accountEntry.setEntryType(AccountEntryType.SALES_INVOICE);}
			@Override public void visitPurchase(Invoice invoice) {accountEntry.setEntryType(AccountEntryType.PURCHASE_INVOICE);}
			@Override public void visitExpenses(Invoice invoice) {
				accountEntry.setEntryType(AccountEntryType.EXPENSE_INVOICE);
				accountEntry.setUndeductible(false);
			}
		});
		return accountEntry;
	}
}
