package net.aonsolutions.aon.api.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
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
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.ewok.TediPayMethod;
import es.translogia.tedi.json.TediJSONUtils;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.InvoiceServlet;

public class BidoqRequest {
	
	private static final String BIDOQ_INVOICE_URL = "https://www.mispapeles.es/selfconta/api/informaFacturaAon"; 
	private static final String BIDOQ_INVOICE_API_KEY = "qmd3Ho*mpbhduav3w5mJ9fkt5%hkxN7aWn@Lfrxw9B6poRRyM8"; 
	
	private static JSONObject getSelfcontaInvoices(String document, Integer year) throws Exception {
		//String sendData = "cif=46230043C" ;
		if(year == null) year = AonDateUtils.getYear(new Date());
		String fechaInicio = year + "-01-01";
		String fechaFin = year + "-12-31";
		String sendData = "cif=" + document 
				+ "&fechaInicio=" + fechaInicio
				+ "&fechaFin=" + fechaFin;
		
		return post(BIDOQ_INVOICE_URL, sendData);
	
	}
	
	protected static JSONObject post(String bidoqUrl, String requestData) throws Exception {
		HttpsURLConnection conn = null;
		try {
			URL url = new URL(bidoqUrl);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("apiKey", BIDOQ_INVOICE_API_KEY);
			OutputStream os = conn.getOutputStream();
			os.write(requestData.getBytes());
			os.flush();
			
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
				
			String output;	
			String response = "";
			while ((output = br.readLine()) != null) {
				response = output; //.replace("'", "\'");	
			}	
			
			return new JSONObject(response);
		} catch (Exception  e) {
			e.printStackTrace();
			throw new Exception("No se ha podido establecer conexión con Selfconta.");
		} finally {
			if (conn != null) conn.disconnect();
		}
	}
	
	public static Integer selfcontaRecord(Domain domain, User user, Invoice invoice, JSONObject json) throws Exception {
		AccountingInvoice ai = tedi2Aon(domain, user, invoice, json);
		return ai.getInvoice().getId();
	}
	
	public static void selfconta(Domain domain, User user, String document, Integer year) throws Exception {
		System.out.println("");
		System.out.println("***** " + document + " *****");
		
		JSONObject json = getSelfcontaInvoices(document, year);
		saveImportation(domain, user, json.toString().getBytes());
	
		JSONObject ingresos = json.getJSONObject("ingresos");
		ingresos.keySet().stream().forEach(key -> {
			JSONObject invoice = ingresos.getJSONObject(key);
			selfconta2Aon(domain, user, invoice);
		});
		
		JSONObject gastos = json.getJSONObject("gastos");
		gastos.keySet().stream().forEach(key -> {
			JSONObject invoice = gastos.getJSONObject(key);
			selfconta2Aon(domain, user, invoice);
		});
	}
	
	public static AccountingInvoice selfconta2Aon(Domain domain, User user, JSONObject selfInvoice) {
		System.out.println(selfInvoice.toString());
		AccountingInvoice ai = new AccountingInvoice();
		try {
			AonConfiguration aonCtx = AON.getConfiguration(domain.getName(), domain.getId(), user.getLogin());
			System.out.println(domain.getName());
			LinkedList<Workplace> a =AON.getWorkplaceList(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()));
			System.out.println(a.size());
			
			if(selfInvoice.optString("series") != null && selfInvoice.optString("series").length() > 5) {
				throw new Exception("La serie no puede tener más de 5 carácteres");
			}
			
			ai.setWorkplace(aonCtx.getWorkplaces().get(0).getId());
		
			Invoice invoice = new Invoice();
			invoice.setScope(getScope(domain, user));
			//	invoice.setService(InvoiceOpType.PIS.equals(ivs.get(i).getType())|| InvoiceOpType.AIS.equals(ivs.get(i).getType()));
			invoice.setTransaction(InvoiceTransactionType.safeValueOf(selfInvoice.optString("transaction")));
			
			//	invoice.setInvestment(ivs.get(i).isInvestment() != null && ivs.get(i).isInvestment());
			invoice.setDomain(domain.getId());
			invoice.setIssueDate(AonDateUtils.parse(selfInvoice.optString("date"), "yyyy-MM-dd"));
			invoice.setTaxDate(AonDateUtils.parse(selfInvoice.optString("date"), "yyyy-MM-dd"));
			invoice.setComments(selfInvoice.optString("comments"));
			invoice.setType(getInvoiceType(selfInvoice));
			
			if(invoice.isSales()) {
				invoice.setSeries(selfInvoice.optString("series"));
				invoice.setNumber(selfInvoice.optInt("number"));
			}
			invoice.setReferenceCode(selfInvoice.optString("reference"));
			invoice.setTotal(selfInvoice.optDouble("total"));
			invoice.setWithholding(false);
			invoice.setSurcharge(false);
			invoice.setDetails(new LinkedList<InvoiceDetail>());
			AccountingRegistry ar = getRegistry(aonCtx, domain, user, InvoiceType.SALES.equals(invoice.getType()) ? selfInvoice.optJSONObject("receiver"): selfInvoice.optJSONObject("sender"), invoice.getType(), invoice.getTransaction() );			
			invoice.setRegistry(ar.getId());
			ai.setRegistry(ar);
			ai.setInvoice(invoice);
			
			JSONObject details = selfInvoice.optJSONObject("details");
			Double total = 0.0;
			Double base = 0.0;
			Double retBase = 0.0;
			Double retQuota = 0.0;
			Double retPercentage = 0.0;
			ai.setVats(new LinkedList<>());
			Double totalService = 0.0;
			Double totalNoService = 0.0;
			for (String  key : details.keySet()) {
				JSONObject detail = details.getJSONObject(key);

				String account = calculateAccount(detail.optString("account"));

				Account outputAccount = aonCtx.accounting().getDefaultChargedVatAccount();
				if(outputAccount == null || outputAccount.getId() == null) {
					outputAccount = getIVArepercutido(domain, user);
				}
				Account inputAccount = aonCtx.accounting().getDefaultPaidVatAccount();
				if(inputAccount == null || outputAccount.getId() == null) {
					inputAccount = getIVAsoportado(domain, user);
				}
				Account adjAccount = aonCtx.accounting().getVatNegativeAdjustAccount();
				Account expAccount = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), account);
				if(expAccount == null) {
					expAccount = new Account()
						.setCode(account)
						.setDescription("SIN DESCRIPCIÓN (CREADO DESDE IMPORTACIÓN DE SELFCONTA)")
						.setAlias("SIN DESCRIPCIÓN")
						.setDomain(domain.getId())
						.setActive(true);
					checkNivelInferior(domain, user, account);
					expAccount = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), expAccount);
				}
				
				if("705".equals(account.substring(0, 3)) || "62".equals(account.substring(0, 2))){
					totalService  = totalService + detail.optDouble("base");
				} else  {
					totalNoService = totalNoService + detail.optDouble("base");
				}
					
				// TODO AÑADIR PRODUCTO!!!
//				Product product = new Product()
//					.setCode(detail.optString("product_id"))
//					.setName(detail.optString("product_name"));
//				Item item = new Item()
//					.setProduct(product)
//					.setDescription(detail.optString("product_description"));
//				item = AON.save(domain.getName(), domain.getId(), user.getLogin(), item);
				
				
				InvoiceDetail id = new InvoiceDetail()
//						.setItem(new OldItem().setId(item.getId()))
						.setDescription(detail.optString("description"))
						.setDiscountExpression(detail.optString("discountPct"))
						.setQuantity(detail.optDouble("quantity"))
						.setAccount(expAccount.getId())
						.setAccountCode(expAccount.getCode())
						.setAccountDescription(expAccount.getDescription())
						.setDomain(domain.getId())
					//	.setInvestAsset(investAsset)
						.setSource(InvoiceSource.ACCOUNT)
						.setTaxableBase(detail.optDouble("base"))
						.setInvoice(ai.getInvoice())
						.setLine(Short.parseShort(key))
						.setPrepayment("5600".equals(account.substring(0, 4)) || "5660".equals(account.substring(0, 4)))
						.setPrice(detail.optDouble("price"))
						.setSurcharge(detail.optJSONObject("equivalenceSurcharge") != null
							? detail.optJSONObject("equivalenceSurcharge").optDouble("surchargePct")
							: 0.0)
						.setWorkPlace(ai.getWorkplace())
						.setPrepayment("5600".equals(account.substring(0, 4)) || "5660".equals(account.substring(0, 4)) || detail.optBoolean("suplidos"));
				
				InvoiceVAT vat = new InvoiceVAT()
					.setInvoiceDetail(id)	
					.setPrepayment("5600".equals(account.substring(0, 4)) || "5660".equals(account.substring(0, 4)) || detail.optBoolean("suplidos"))
					.setVatDeductionType(VatDeductionType.WITH_RIGHT)
					.setBase(detail.optDouble("base"))
					.setPercentage(detail.optDouble("vatPct"))
					.setQuota(detail.optDouble("quotaVat"))
					.setSurcharge(detail.optJSONObject("equivalenceSurcharge") != null
						? detail.optJSONObject("equivalenceSurcharge").optDouble("surchargePct")
						: 0.0)
					.setSurchargeQuota(detail.optJSONObject("equivalenceSurcharge") != null
						? detail.optJSONObject("equivalenceSurcharge").optDouble("surchargeQuota")
						: 0.0) 
					//.setInvestAsset(ivs.get(j).getInvestAsset())
					.setDeductiblePercent(100.0)
					.setDeductibleQuota(detail.getDouble("quotaVat"))
					.setWithholding(detail.optJSONObject("irpf") != null)

					.setExpAccountId(expAccount.getId())
					.setExpAccountCode(expAccount.getCode())
					.setExpAccountDescription(expAccount.getDescription())
							
					.setOutputAccountCode(outputAccount.getCode())
					.setOutputAccountDescription(outputAccount.getDescription())
					.setOutputAccountId(outputAccount.getId())
							
					.setInputAccountCode(inputAccount.getCode())
					.setInputAccountDescription(inputAccount.getDescription())
					.setInputAccountId(inputAccount.getId())
					.setAdjAccountCode(adjAccount != null ? adjAccount.getCode(): null)
					.setAdjAccountDescription(adjAccount != null ? adjAccount.getDescription(): null)
					.setAdjAccountId(adjAccount != null ? adjAccount.getId() : null);
				
				ai.addVat(vat);
				
				if(vat.isWithholding()) { 
					System.out.println(detail.toString());
					invoice.setWithholding(true);
					JSONObject irpf = detail.optJSONObject("irpf");

					retBase = retBase + vat.getBase();
					retPercentage = irpf.optDouble("percentage");
					retQuota = retQuota + irpf.optDouble("quota");
					ai.getInvoice().setWithholding(true);
				}
			
				if(vat.getSurcharge() > 0 ){ 	
					ai.getInvoice().setSurcharge(true);
				}
			
				total = total + (invoice.mustApplyISP() ? vat.getBase() : detail.optDouble("total"));
				base = base + vat.getBase();
		
				if(ai.getInvoice().isWithholding()) {
					Account retentionAccount = (invoice.isSales())
						? getDetaultPaidRetAccount(domain, user, aonCtx)
						: getDefaultChargedRetAccount(domain, user, aonCtx);
						
					InvoiceWithholding iw = new InvoiceWithholding()
						.setWithholdingType(retPercentage == 19.0 
							? WithholdingType.RENTING 
							: WithholdingType.PROFESSIONAL)
						.setBase(retBase)
						.setPercentage(retPercentage)
						.setQuota(retQuota)
						.setAccountCode(retentionAccount.getCode())
						.setAccountDescription(retentionAccount.getDescription())
						.setAccountId(retentionAccount.getId());
					ai.setWithholdingData(iw);
				}
			}
			ai.getInvoice().setService(totalService > totalNoService);
			
			ai.setAccountEntry(getEntryBase(domain, user.getLogin(), aonCtx, ai));
			
			JSONObject finances = selfInvoice.optJSONObject("finances");
			ai.getInvoice().setFinances(new LinkedList<Finance>());
			for (String  key : finances.keySet()) {
				if(!"contabilizado".equalsIgnoreCase(key)) {
					JSONObject finance = finances.getJSONObject(key);
					System.out.println(finance.toString());
					System.out.println(finance.optDouble("amount"));
					Finance f = new Finance()
							.setAmount(finance.optDouble("amount"))
							.setDueDate(AonDateUtils.parse(finance.optString("due_date"), "yyyy-MM-dd"))
							.setPayMethod(getPaymethod(aonCtx, finance.optString("paymethod")).getId())
							.setPayment(!invoice.isSales())
							.setBankAccount(new BankAccount(finance.optString("iban")));

					if(finance.opt("account") != null) {
						String code = calculateAccount(finance.optString("account"));
						Account financeAccount = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), code);
						if(financeAccount == null || financeAccount.getId() == null) {
							financeAccount = new Account()
									.setCode(code)
									.setDescription("SIN DESCRIPCIÓN (CREADO DESDE IMPORTACIÓN DE SELFCONTA)")
									.setAlias("SIN DESCRIPCIÓN")
									.setDomain(domain.getId())
									.setActive(true);
							financeAccount = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), financeAccount);
						}
						ai.setPayAccountId(financeAccount.getId())
						.setPayAccountCode(financeAccount.getCode())
						.setPayAccountDescription(financeAccount.getDescription());
						ai.getInvoice().addFinance(f);
					} else {
						f.setInvoice(new Invoice().setId(ai.getInvoice().getId()))
							.setDomain(domain.getId())
							.setRegistry(new Registry().setId(ai.getInvoice().getRegistry()))
							.setRegistryDocument(ai.getInvoice().getRegistryDocument())
							.setRegistryDocumentType(ai.getInvoice().getRegistryDocumentType())
							.setRegistryDocumentCountry(ai.getInvoice().getRegistryDocumentCountry())
							.setRegistryName(ai.getInvoice().getRegistryName())
							.setScope(ai.getInvoice().getScope())
							.setSecurityLevel(ai.getInvoice().getSecurityLevel())
							.setConcept(ai.getInvoice().getDocumentNumber())
							.setFinanceStatus(FinanceStatus.PENDING);
						ai.getInvoice().addFinance(f);
					}
				}
			}
			ai = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), ai);
		} catch (Exception e) {
			e.printStackTrace();
			rawdoc(domain, user, selfInvoice, e.getMessage());
		}
		return ai;
	}

	private static InvoiceType getInvoiceType(JSONObject json) {
		String account = !json.optJSONObject("details").isEmpty() ? json.optJSONObject("details").optJSONObject("0").optString("account"): "";
		System.out.println(json.optString("type"));
		System.out.println(account);
		if(json.optString("type").equalsIgnoreCase("emitida")) {
			return InvoiceType.SALES;
		} else if(json.optString("type").equalsIgnoreCase("recibida") && !AonStringUtils.isBlank(account) && "60".equals(account.substring(0, 2))) {
			return InvoiceType.PURCHASE;
		} else return InvoiceType.EXPENSES;
	}
	
	private static InvoiceType getTediInvoiceType(JSONObject json) {
		String account = json.optString("category");
		if(AonStringUtils.isBlank(account)) {
			account = !json.optJSONArray("details").isEmpty() &&
					!json.optJSONArray("details").optJSONObject(0).isEmpty()  ? json.optJSONArray("details").optJSONObject(0).optString("account"): "";
		}
		System.out.println(json.optString("type"));
		System.out.println(account);
		if(json.optString("type").equalsIgnoreCase("emitida")) {
			return InvoiceType.SALES;
		} else if(json.optString("type").equalsIgnoreCase("recibida") && !AonStringUtils.isBlank(account) && "60".equals(account.substring(0, 2))) {
			return InvoiceType.PURCHASE;
		} else return InvoiceType.EXPENSES;
	}
	
	
	private static Scope getScope(Domain domain, User user) {
		Scope scope = new Scope();
		if(domain.getScope() == null) {
			scope = AON.getUserScopeStream(domain.getName(), domain.getId(), user.getLogin(), user.getId(), 
					f -> f.getDescriptionProperty().eq("GENERAL")).findFirst().orElse(new Scope());
			if(scope.isEmpty()) {
				Integer[] scopes = AON.getUserScopes(domain.getName(), domain.getId(), user.getLogin(), user.getId());
				if(scopes != null && scopes.length > 0)
					scope = AON.getScope(domain.getName(), domain.getId(), user.getLogin(), scopes[0]);
				else {
					scope = AON.getScopeStream(domain.getName(), domain.getId(), user.getLogin(),  f ->
						f.getDomainProperty().eq(domain.getId())).findFirst().orElse(new Scope());
					if(scope.isEmpty()) {
						scope = AON.insertScope(domain.getName(), domain.getId(), user.getLogin(), new Scope()
							.setDescription("GENERAL")
							.setDomain(domain.getId()));
					}
				}
			}
		} else scope = AON.getScope(domain.getName(), domain.getId(), user.getLogin(), domain.getScope());
		return scope;
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
	
	private static AccountingRegistry getCustomer(Domain domain, User user, Integer id) {
		Customer customer = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(id));
		return new AccountingRegistry()
				.setType(AccountingRegistryType.CUSTOMER)
				.setId(customer.getId())
				.setName(customer.getName())
				.setAccountId(customer.getAccount());
	}
	
	private static AccountingRegistry getSupplier(Domain domain, User user, Integer id) {
		Supplier supplier = AON.getSupplier(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(id)).orElse(new Supplier());
		return new AccountingRegistry()
				.setType(AccountingRegistryType.SUPPLIER)
				.setId(supplier.getId())
				.setName(supplier.getName())
				.setAccountId(supplier.getAccount());
	}
	
	private static AccountingRegistry getCreditor(Domain domain, User user, Integer id) {
		Creditor creditor = AON.getCreditor(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(id)).orElse(new Creditor());
		return new AccountingRegistry()
				.setType(AccountingRegistryType.SUPPLIER)
				.setId(creditor.getId())
				.setName(creditor.getName())
				.setAccountId(creditor.getAccount());
	}

	private static AccountingRegistry getRegistry(Domain domain, User user, Invoice invoice) {
		checkRegistryAccounts(domain, user);
		if(invoice.isSales()) return getCustomer(domain, user, invoice.getRegistry());
		else  if(invoice.isPurchase()) return getSupplier(domain, user, invoice.getRegistry());
		else return getCreditor(domain, user, invoice.getRegistry());
	}
	
	private static AccountingRegistry getRegistry(AonConfiguration aonCtx, Domain domain, User user, JSONObject json, InvoiceType type, InvoiceTransactionType transaction) {
		checkRegistryAccounts(domain, user);
		String nif = json.optString("document").replaceAll("-", "");
		String name = json.optString("name");
		Country country = Country.safeValueOf(json.optString("country"));
		
		RegistryAddress ra = getRegistryAddress(aonCtx, domain, user, json.optJSONObject("address"));
		if(InvoiceType.SALES.equals(type)) {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(nif)));
			if(customer.getId() == null) {
				customer = createCustomer(domain, user, name, nif, country, transaction);
			}
			return new AccountingRegistry()
					.setType(AccountingRegistryType.CUSTOMER)
					.setId(customer.getId())
					.setName(customer.getName())
					.setAccountId(customer.getAccount());
		} else if(InvoiceType.PURCHASE.equals(type)) {
			Supplier supplier = AON.getSupplier(domain.getName(), domain.getId(), user.getLogin(), f ->
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(nif))).orElse(new Supplier());
			if(supplier.getId() == null) {
				supplier = createSupplier(domain, user, name, nif, country, transaction);
			}
			return new AccountingRegistry()
					.setType(AccountingRegistryType.SUPPLIER)
					.setId(supplier.getId())
					.setName(supplier.getName())
					.setAccountId(supplier.getAccount());
		} else if(InvoiceType.EXPENSES.equals(type)) {
			Creditor creditor = AON.getCreditor(domain.getName(), domain.getId(), user.getLogin(), f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDocumentProperty().eq(nif))).orElse(new Creditor());
			if(creditor.getId() == null) {
				creditor = createCreditor(domain, user, name, nif, country, transaction);
			}
			return new AccountingRegistry()
					.setType(AccountingRegistryType.CREDITOR)
					.setId(creditor.getId())
					.setName(creditor.getName())
					.setAccountId(creditor.getAccount());
		}
		else return new AccountingRegistry();
		
	}
	
	private static RegistryAddress getRegistryAddress(AonConfiguration aonCtx, Domain domain, User user, JSONObject json) {
		RegistryAddress address = new RegistryAddress();
		address.setDomain(domain.getId());
		address.setAddress(json.optString("address"));
		address.setCity(json.optString("city"));
		address.setZip(json.optString("zip"));
		if(json.optString("province") != null) {
			GeoZone prgz = null;
			GeoZone crgz = null;
			Provinces pr = Provinces.getProvince(json.optString("province"));
			if(pr == null && address.getZip() != null && address.getZip().length() > 2) {
				pr = Provinces.getProvinceById(address.getZip().substring(0,2));
			}
			for(GeoZone gz : aonCtx.getGeozones()) {
				if(gz.getCode().equals(json.optString("country"))) {
					crgz = gz;
				}
				if(pr != null && gz.getCode().equals(pr.getId())) {
					prgz = gz;
				}
			}
		
			if(prgz != null) {
				address.setGeozone(prgz.getId());
				address.setGeozoneCode(prgz.getCode());
				address.setGeozoneName(prgz.getName());
			} else if(crgz != null) {
				address.setGeozone(crgz.getId());
				address.setGeozoneCode(crgz.getCode());
				address.setGeozoneName(crgz.getName());
			}
		}
		return address;
	}
	
	
	private static Registry createRegistry(Domain domain, User user,String  name,String nif, Country country, InvoiceTransactionType transaction) {
		if(country == null) {
			country = Country.ES;
		}
		return AON.save(domain.getName(), domain.getId(), user.getLogin(), new Registry()
				.setDomain(domain)
				.setName(name.length() > 64 ? name.substring(0,63) : name)
				.setDocument(nif)
				.setDocumentCountry(country)
				.setNationality(country));
	}
	
	private static Creditor createCreditor(Domain domain, User user,String  name,String nif,Country country, InvoiceTransactionType transaction) {
		
		Registry reg = createRegistry(domain, user, name, nif, country, transaction );
		Account acc = ACCOUNTING.getAccounts(domain.getName(), domain.getId(), user.getLogin(), f -> 
		f.getDomainProperty().eq(domain.getId())
		.and(f.getAliasProperty().eq(nif))).findFirst().orElse(new Account());

		Creditor creditor = new Creditor()
			.copy(reg)
			.setAccount(acc==null?null:acc.getId())
			.setTransaction(transaction)
			.setStatus(RegistryStatus.ACTIVE)
			.setScope(getScope(domain, user));
		creditor.setDomain(domain);
		creditor.setId(reg.getId());
		return AON.insertCreditor(domain.getName(), domain.getId(), user.getLogin(), creditor);
	}
	
	private static Customer createCustomer(Domain domain, User user,String  name,String nif,Country country, InvoiceTransactionType transaction) {
		Registry reg = createRegistry(domain, user, name, nif, country, transaction );
		Account acc = ACCOUNTING.getAccounts(domain.getName(), domain.getId(), user.getLogin(), f -> 
		f.getDomainProperty().eq(domain.getId())
		.and(f.getAliasProperty().eq(nif))).findFirst().orElse(new Account());
		
		Customer customer = new Customer()
				.copy(reg)
				.setStatus(RegistryStatus.ACTIVE)
				.setTransaction( transaction )
				.setScope(getScope(domain, user));
			customer.setDomain(domain);
			customer.setName(reg.getName());
			customer.setId(reg.getId());
		return AON.insertCustomer(domain.getName(), domain.getId(), user.getLogin(), customer);
	}
	
	private static Supplier createSupplier(Domain domain, User user,String  name,String nif, Country country, InvoiceTransactionType transaction) {
		Registry reg = createRegistry(domain, user, name, nif, country,  transaction);
		Account acc = ACCOUNTING.getAccounts(domain.getName(), domain.getId(), user.getLogin(), f -> 
		f.getDomainProperty().eq(domain.getId())
		.and(f.getAliasProperty().eq(nif))).findFirst().orElse(new Account());

		Supplier supplier = new Supplier()
				.setTransaction(transaction)
				.setStatus(RegistryStatus.ACTIVE)
				.setScope(getScope(domain, user))
				.setAccount(acc.getId());
		supplier.setDomain(domain);
		supplier.setId(reg.getId());
		supplier.setName(reg.getName());
		return AON.insertSupplier(domain.getName(), domain.getId(), user.getLogin(), supplier);
		
	}
	
	public static String calculateAccount(String acc) {
		return calculateAccount(acc, 4);
	}
	
	public static String calculateAccount(String acc, Integer pos) {
		if(acc.length() > 9) {
			return acc.substring(0,pos) + acc.substring((acc.length() - 9) + pos);
		} else if(acc.length() > pos && acc.length() < 9) {
			return acc.substring(0, pos) + generateZeros(9 - acc.length()) + acc.substring(pos);
		}
		return acc;
	}
	
	public static String generateZeros(Integer index) {
		String zeros = "";
		for(Integer i = 0; i < index; i++) {
			zeros = zeros + "0";
		}
		return zeros;
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
	
	private static void rawdoc(Domain domain, User user, JSONObject invoice, String error) {
		JSONObject json = selfconta2Tedi(user, invoice, error);
		AonApiData api = new AonApiData()
				.setDomain(domain)
				.setUser(user)
				.setData(json)
				.setDur(new DomainUserRoles());
		InvoiceServlet.setInvoice(api);
	}
	
	private static JSONObject selfconta2Tedi(User user, JSONObject invoice, String error) {
		JSONObject json = new JSONObject();
		Date date = AonDateUtils.parse(invoice.optString("date"), "yyyy-MM-dd");
		System.out.println(TediJSONUtils.formatDate(date));
		json.put("date", TediJSONUtils.formatDate(date));
		json.put("type", invoice.optString("type"));
		json.put("reference", invoice.optString("reference"));
		json.put("series", invoice.optString("series"));
		json.put("number", invoice.optInt("number"));
		json.put("total", invoice.optDouble("total"));
		json.put("sender", invoice.optJSONObject("sender"));
		json.put("receiver", invoice.optJSONObject("receiver"));
		json.put("transaction", invoice.optString("transaction"));
		json.put("status", "refused");
		json.put("selfconta", true);

		JSONArray comments = new JSONArray();
		JSONObject comment = new JSONObject();
		comment.put("reason", error);
		comment.put("date", new Date().getTime());
		comment.put("status", "Rechazado");
		comment.put("user", user.getLogin());
		comments.put(comment);
		
		if(error.contains("No se puede grabar un asiento contable descuadrado.")) {
			if(json.optString("transaction").equalsIgnoreCase("INTR")
					&& json.optString("type").equalsIgnoreCase("emitida")) {
				JSONObject comment2 = new JSONObject();
				comment2.put("reason", "Factura emitida intracomunitaria no lleva IVA.");
				comment2.put("date", new Date().getTime());
				comment2.put("status", "Rechazado");
				comment2.put("user", user.getLogin());
				comments.put(comment2);
			}
		}
		if(error.contains("Ya existe una factura")) {
			json.put("status", "draft");
		}
		json.put("comments", comments);
		
		JSONArray dtls = new JSONArray();
		JSONArray taxes = new JSONArray();
		JSONArray fnncs = new JSONArray();
		
		JSONObject details = invoice.optJSONObject("details");

		HashMap<Double, JSONObject> vatMap = new HashMap<>();
		HashMap<Double, JSONObject> irpfMap = new HashMap<>();
		for (String  key : details.keySet()) {
			JSONObject dtl = new JSONObject();
			JSONObject detail = details.getJSONObject(key);
			String account = calculateAccount(detail.optString("account"));
			Boolean prepayment = false;
			if(account.length() > 4)
				prepayment = "5600".equals(account.substring(0, 4)) || "5660".equals(account.substring(0, 4));
			dtl.put("account", account);
			dtl.put("description", detail.optString("description"));
			dtl.put("quantity",detail.optDouble("quantity"));
			dtl.put("price", detail.optDouble("price"));
			dtl.put("discount", detail.optDouble("discountPct"));
			dtl.put("vat", detail.optDouble("vatPct"));
			dtl.put("amount", detail.optDouble("base"));
			dtl.put("prepayment", prepayment);
			dtl.put("base", detail.optDouble("base"));
			dtl.put("percentage", detail.optDouble("vatPct"));
			dtl.put("quota", detail.optDouble("quotaVat"));
			dtl.put("surcharge", detail.optJSONObject("equivalenceSurcharge") != null
					? detail.optJSONObject("equivalenceSurcharge").optDouble("surchargePct")
					: 0.0);
			dtl.put("surcharge_quota", detail.optJSONObject("equivalenceSurcharge") != null
					? detail.optJSONObject("equivalenceSurcharge").optDouble("surchargeQuota")
					: 0.0);
			
			if(!prepayment) {
				JSONArray dtlTaxes = new JSONArray();
				JSONObject tax = new JSONObject();
				tax.put("base", detail.optDouble("base"));
				tax.put("percentage", detail.optDouble("vatPct"));
				tax.put("quota", detail.optDouble("quotaVat"));
				tax.put("tax", "IVA");
				tax.put("type", "IVA");
				tax.put("surcharge", detail.optJSONObject("equivalenceSurcharge") != null
					? detail.optJSONObject("equivalenceSurcharge").optDouble("surchargePct")
					: 0.0);
				tax.put("surcharge_quota", detail.optJSONObject("equivalenceSurcharge") != null
					? detail.optJSONObject("equivalenceSurcharge").optDouble("surchargeQuota")
					: 0.0);
				Double percentage = tax.optDouble("percentage");
				if(vatMap.containsKey(percentage)) {
					vatMap.get(percentage).put("base", vatMap.get(percentage).optDouble("base") + tax.optDouble("base"));
					vatMap.get(percentage).put("quota", vatMap.get(percentage).optDouble("quota") + tax.optDouble("quota"));
					vatMap.get(percentage).put("surcharge_quota", vatMap.get(percentage).optDouble("surcharge_quota") + tax.optDouble("surcharge_quota"));
				} else vatMap.put(percentage, tax);
				dtlTaxes.put(tax);

				if(detail.optJSONObject("irpf")!= null) {
					JSONObject irpf = detail.optJSONObject("irpf");
					JSONObject tax2 = new JSONObject();
					tax2.put("base", irpf.optDouble("base"));
					tax2.put("percentage", irpf.optDouble("percentage"));
					tax2.put("quota", irpf.optDouble("quota"));
					tax2.put("tax", "IRPF");
					tax2.put("type", "IRPF");
					dtlTaxes.put(tax2);
					Double irpfPercentage = tax2.optDouble("percentage");
					if(irpfMap.containsKey(irpfPercentage)) {
						irpfMap.get(irpfPercentage).put("base", vatMap.get(irpfPercentage).optDouble("base") + tax2.optDouble("base"));
						irpfMap.get(irpfPercentage).put("quota", vatMap.get(irpfPercentage).optDouble("quota") + tax2.optDouble("quota"));
					} else irpfMap.put(irpfPercentage, tax2);
				}
			}

			dtls.put(dtl);
		}
		
		json.put("details", dtls);
		
		vatMap.entrySet().stream().forEach(value -> taxes.put(value.getValue()));
		irpfMap.entrySet().stream().forEach(value -> taxes.put(value.getValue()));
		json.put("taxes", taxes);
		
		
		JSONObject finances = invoice.optJSONObject("finances");
		for (String  key : finances.keySet()) {
			if(!"contabilizado".equalsIgnoreCase(key)) {
				JSONObject finance = finances.getJSONObject(key);
				JSONObject fnnc = new JSONObject();

				fnnc.put("amount", finance.optDouble("amount"));
				fnnc.put("due_date", finance.optString("due_date"));
				fnnc.put("iban", finance.optString("iban"));
				fnnc.put("bank_account", finance.optString("iban"));
				fnnc.put("paymethod", getTediPaymethod(finance.optString("paymethod")));

				if(finance.opt("account") != null) {
					String code = calculateAccount(finance.optString("account"));
					fnnc.put("account", code);
				}
				fnncs.put(fnnc);
			}
		}
		json.put("finances", fnncs);
		return json;
	}
	
	private static String getTediPaymethod(String paymethod) {
		if("contado".equalsIgnoreCase(paymethod)) {
			return TediPayMethod.CASH.name();
		} else if("cheque".equalsIgnoreCase(paymethod)) {
			return "CHECK";
		} else if("Transferencia bancaria".equalsIgnoreCase(paymethod)) {
			return  TediPayMethod.TRANSFER.name();
		} else if("Tarjeta de crédito".equalsIgnoreCase(paymethod)) {
			return TediPayMethod.CARD.name();
		} else if("Tarjeta de débito".equalsIgnoreCase(paymethod)) {
			return TediPayMethod.CARD.name();
		} else if("Recibo domiciliado".equalsIgnoreCase(paymethod)) {
			return TediPayMethod.BANK.name();
		} 
		return "";
	}
	
	private static PayMethod getTediPaymethod(AonConfiguration aonCtx, String paymethod) {
		PayMethodType type;
		if(TediPayMethod.CASH.name().equalsIgnoreCase(paymethod)) {
			type = PayMethodType.CASH_BASIS;
		} else if("CHECK".equalsIgnoreCase(paymethod)) {
			type = PayMethodType.CHEQUE;
		} else if(TediPayMethod.TRANSFER.name().equalsIgnoreCase(paymethod)) {
			type = PayMethodType.BANK_TRANSFER;
		} else if(TediPayMethod.CARD.name().equalsIgnoreCase(paymethod)) {
			type = PayMethodType.CREDIT_CARD;
		} else type = PayMethodType.OTHER;
		
		PayMethod pm = aonCtx.getPayMethods().stream().filter(
			f -> type.equals(f.getType())).findFirst().orElse(new PayMethod());
		
		return pm;
	}
	private static PayMethod getPaymethod(AonConfiguration aonCtx, String paymethod) {
		PayMethodType type;
		if("contado".equalsIgnoreCase(paymethod)) {
			type = PayMethodType.CASH_BASIS;
		} else if("cheque".equalsIgnoreCase(paymethod)) {
			type = PayMethodType.CHEQUE;
		} else if("Transferencia bancaria".equalsIgnoreCase(paymethod)) {
			type = PayMethodType.BANK_TRANSFER;
		} else if("Tarjeta de crédito".equalsIgnoreCase(paymethod)) {
			type = PayMethodType.CREDIT_CARD;
		} else if("Tarjeta de débito".equalsIgnoreCase(paymethod)) {
			type = PayMethodType.DEBIT_CARD;
		} else type = PayMethodType.OTHER;
		
		PayMethod pm = aonCtx.getPayMethods().stream().filter(
			f -> type.equals(f.getType())).findFirst().orElse(new PayMethod());
		
		return pm;
	}
	
	public static AccountingInvoice tedi2Aon(Domain domain, User user, Invoice invoice, JSONObject ti) throws Exception {
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

			
			Account expAccount = detail.getAccount() != null
				? ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), detail.getAccount())
				: ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), category);
			if (expAccount == null) {
				expAccount = new Account().setCode(category)
						.setDescription("SIN DESCRIPCIÓN (CREADO DESDE TEDI INVOICE)").setAlias("SIN DESCRIPCIÓN")
						.setDomain(domain.getId()).setActive(true);
				checkNivelInferior(domain, user, category);
				expAccount = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), expAccount);
			}

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

		ai = ACCOUNTING.save(domain.getName(), domain.getId(), user.getLogin(), ai);
		return ai;
	}
	
	private static void saveImportation(Domain domain, User user, byte[] data) {
		SaveImportation si = new SaveImportation(domain, user, data);
		si.start();
	}
	

}
