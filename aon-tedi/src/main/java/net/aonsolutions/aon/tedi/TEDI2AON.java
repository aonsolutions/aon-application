package net.aonsolutions.aon.tedi;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.SupplierStatus;

import es.translogia.tedi.TediInvoice;
import es.translogia.tedi.TediInvoiceType;
import es.translogia.tedi.TediPGC;

@SuppressWarnings("serial")
@WebServlet(name = "Tedi2AonServlet", urlPatterns = { "/tedi2aon/*"})
public class TEDI2AON extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}	
	
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		addCorsHeader(resp);
		resp.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject json = getRequestJSON(req);
		String domainName = req.getServerName();
		Integer domainId = 1;

		String cp = json.getString("company");
		String login = json.opt("login") != null ? json.getString("login") : "";
		Domain requestDomain = AON.getDomain(domainName, domainId, login, f -> f.getNameProperty().eq(domainName));
		Domain domain = AON.getCompanyDomain(requestDomain.getName(), requestDomain.getId(), login, cp);
		tedi2aon(domain, login, json);	
		
		resp.setContentType("application/json;charset=UTF-8");
		addCorsHeader(resp);
		PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
		os.println("");
		os.flush();
		os.close();
	}
	
	public static void addCorsHeader(HttpServletResponse response){
		response.addHeader("Access-Control-Allow-Origin", "*");
	    response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
	    response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
	    response.addHeader("Access-Control-Max-Age", "1728000");
	}
	
	private void tedi2aon(Domain domain, String login, JSONObject json) {
		TediInvoice ti = new TediInvoice(json);
		TediPGC pgc = TediPGC.getValue(ti.getCategory());
		JSONObject aon = ti.getProperty("aon");
		Invoice invoice = new Invoice();
		
		if (aon.length() > 0) {
			invoice = AON.getInvoice(aon.getString("domain_name"), aon.getInt("domain_id"), aon.getString("login"),
					f -> f.getIdProperty().eq(aon.getInt("invoice")));
		} else {
			invoice.setDomain(domain.getId());
			invoice.setSeries(ti.getSeries() != null ? ti.getSeries() : "TEDI");
			invoice.setNumber(ti.getNumber());
		}
		
		LinkedList<Scope> scopeList = AON
				.getScopeStream(domain.getName(), domain.getId(), login
						, f -> f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				.collect(Collectors.toCollection(LinkedList::new));
		Scope scope = invoice.getScope() != null ? invoice.getScope() :  scopeList.get(0);
		
		if (TediInvoiceType.ISSUED.equals(ti.getType())) {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), login,
					f -> f.getDocumentProperty().eq(ti.getReceiver().getDocument()));
			if (customer.getId() == null) {
				Registry registry = AON.getRegistry(domain.getName(), domain.getId(), login,
						f -> f.getDocumentProperty().eq(ti.getReceiver().getDocument()));
				if (registry.getId() == null) {
					registry = new Registry();
					registry.setDocument(ti.getReceiver().getDocument());
					registry.setName(ti.getReceiver().getName());
					registry.setDomain(domain.getId());
					registry = AON.insertRegistry(domain.getName(), domain.getId(), login, registry);
				}
				customer = new Customer();
				customer.setDomain(domain.getId());
				customer.setRegistry(registry);
				customer.setDocument(ti.getReceiver().getDocument());
				customer.setName(ti.getReceiver().getName());
				customer.setId(registry.getId());
				customer.setScope(scope.getId());
				customer.setStatus(CustomerStatus.ACTIVE);
				customer = AON.insertCustomer(domain.getName(), domain.getId(), login, customer);
			}
			invoice.setRegistry(customer.getRegistry().getId());
			invoice.setRegistryDocument(customer.getDocument());
			invoice.setRegistryDocumentCountry(customer.getDocumentCountry());
			invoice.setRegistryDocumentType(customer.getDocumentType());
			invoice.setRegistryName(customer.getName());
			// TODO invoice.setRegistryAddress(0);
			invoice.setType(InvoiceType.SALES);
		} else if (TediInvoiceType.RECEIVED.equals(ti.getType())
				|| TediInvoiceType.TICKET.equals(ti.getType())) {
			Supplier supplier = AON.getSupplier(domain.getName(), domain.getId(), login,
					f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(ti.getSender().getDocument()))).orElse(new Supplier());
			if (supplier.getId() == null) {
				Registry registry = AON.getRegistry(domain.getName(), domain.getId(), login,
						f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(ti.getSender().getDocument())));
				if (registry.getId() == null) {
					registry = new Registry();
					registry.setDocument(ti.getSender().getDocument());
					registry.setName(ti.getSender().getName());
					registry.setDomain(domain.getId());
					registry = AON.insertRegistry(domain.getName(), domain.getId(), login, registry);
				}
				supplier = new Supplier();
				supplier.setDomain(domain.getId());
				supplier.setDocument(ti.getSender().getDocument());
				supplier.setName(ti.getSender().getName());
				supplier.setId(registry.getId());
				supplier.setWithholding((short) 0);
				supplier.setWithholdingFarmer((short) 0);
				supplier.setVatAccrualPayment((short) 0);
				supplier.setTransaction((short) 0);
				supplier.setPurchaseValuated((short) 1);
				supplier.setStatus(SupplierStatus.ACTIVE);
				supplier.setScope(scope.getId());
				supplier = AON.insertSupplier(domain.getName(), domain.getId(), login, supplier);
			}
			invoice.setRegistry(supplier.getId());
			invoice.setRegistryDocument(supplier.getDocument());
			invoice.setRegistryDocumentCountry(supplier.getDocumentCountry());
			invoice.setRegistryDocumentType(supplier.getDocumentType());
			invoice.setRegistryName(supplier.getName());
			// TODO invoice.setRegistryAddress(0);
			invoice.setType(InvoiceType.PURCHASE);
			// TODO invoice.setType(InvoiceType.EXPENSES);
		}

		invoice.setReferenceCode(ti.getReference());
		invoice.setIssueDate(ti.getDate() != null ? ti.getDate() : new Date());
		invoice.setTaxDate(ti.getDate() != null ? ti.getDate() : new Date());
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus((byte) 0);
		invoice.setScope(scope);
		invoice.setComments(ti.getComments());
	//	invoice.setRemarks(ti.getRemarks());

		invoice.setSurcharge(false);
		invoice.setWithholding(false);
		invoice.setWithholdingFarmer(false);
		invoice.setVatAccrualPayment(false);
		invoice.setInvestment(false);
		invoice.setTransaction(InvoiceTransactionType.NATIONAL);
		invoice.setSigned(false);
		invoice.setService(false);
		// invoice.setRectificationType()
		// invoice.setRectificationInvoice()
		invoice.setAdvance(false);
		// invoice.setPosShift(0);
		// invoice.setSeller(1)

	//	invoice.setTaxableBase(ti.getTaxableBase());
		invoice.setTotal(ti.getTotal());
		invoice = AON.insertInvoice(domain.getName(), domain.getId(), login, invoice);

		Integer invoiceId = invoice.getId();
		// INVOICE DETAIL
		Workplace workPlace = AON.getWorkplace(domain.getName(), domain.getId(), login,
				f -> f.getDomainProperty().eq(domain.getId()));
		
		ti.getDetails().stream().forEach(detail -> {
			InvoiceDetail id = new InvoiceDetail();
			id.setDomain(domain.getId());
			id.setInvoice(new Invoice().setId(invoiceId));
			// id.setLine(null);
			id.setItem(getItem(domain, login, pgc, detail.getVat()));
			// id.setTaxes(1);
			id.setWorkPlace(workPlace.getId());
			// id.setWarehouse(warehouse);
			id.setDescription(detail.getDescription());
			id.setDiscountExpression(detail.getDiscount().toString());
			id.setQuantity(detail.getQuantity());
			id.setPrice(detail.getPrice());
			id.setTaxableBase(detail.getPrice() * detail.getQuantity());
			/*
			Double percentage = detail.getVat();
			Double base = detail.getPrice() * detail.getQuantity();
			Double quota = (base * percentage)/100;
			InvoiceTax it = new InvoiceTax()
					.setBase(base)
					.setPercentage(percentage)
					.setQuota(quota);
			*/

			AON.insertInvoiceDetail(domain.getName(), domain.getId(), login, id);
		});
		if(ti.getDetails().size() <= 0) {
			ti.getTaxes().stream().forEach(tax -> {
				InvoiceDetail id = new InvoiceDetail()
					.setDomain(domain.getId())
					.setInvoice(new Invoice().setId(invoiceId))
					.setItem(getItem(domain, login, pgc, tax.getPercentage()))
					.setWorkPlace(workPlace.getId())
					.setQuantity(1)
					.setDiscountExpression("0.0")
					.setPrice(tax.getBase())
					.setDescription(pgc.getDescription())
					.setTaxableBase(tax.getBase());
				
				AON.insertInvoiceDetail(domain.getName(), domain.getId(), login, id);
			});
		}
		// INVOICE FINANCE

		ti.getFinances().stream().forEach(finance -> {
			Finance f = new Finance()
					.setDomain(domain.getId())
					.setPayment(TediInvoiceType.RECEIVED.equals(ti.getType()) ? true : false)
					.setInvoice(new Invoice().setId(invoiceId)).setScope(scope)
					.setDueDate(finance.getDueDate())
					.setAmount(finance.getAmount())        
					.setFinanceStatus(finance.getStatus().equals("pagado") ? FinanceStatus.PAID : FinanceStatus.PENDING)
					.setBankAccount(new BankAccount(finance.getIban()));
			AON.insertFinance(domain.getName(), domain.getId(), login, f);
		});

	}
	
	
	private Item getItem(Domain domain, String login, TediPGC pgc, Double vat) {
		if(pgc != null) {
			Item item = AON.getItem(domain.getName(), domain.getId(), login, f -> 
				f.getBarcodeProperty().eq(pgc.getCategory() + "_" + vat)
				.and(f.getDetailProperty().eq(vat.toString())));
			if(item.getId() == null) {
				Tax tax = AON.getTax(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()).and(f.getPercentageProperty().eq(vat)));
				Product p = AON.getProduct(domain.getName(), domain.getId(), login, f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getCodeProperty().eq(pgc.getCategory() + "_" + tax.getPercentage())));
				if(p.getId() == null) {
					p = new Product()
						.setDomain(domain.getId())
						.setName(pgc.getName())
						.setCode(pgc.getCategory() + "_" + tax.getPercentage())
						.setKind(ProductKind.SALE_PURCHASE.value())
						.setVat(tax.getId())
						.setInventoriable(false)
						.setType(ProductType.COMMERCIAL_PRODUCT.value());
					p = AON.insertProduct(domain.getName(), domain.getId(), login, p);
				}
				item = new Item()
						.setDomain(domain.getId())
						.setProduct(p)
						.setProductId(p.getId())
						.setBarcode(pgc.getCategory() + "_" + vat)
						.setDescription(pgc.getDescription())
						.setDetail(vat.toString());
				item = AON.insertItem(domain.getName(), domain.getId(), login, item);
			}
			return item;
		} else return null;
	}
	
	private JSONObject getRequestJSON(HttpServletRequest req){
		String line = "";
		StringBuilder bld = new StringBuilder();
		try {
			while((line = req.getReader().readLine()) != null){
				bld.append(" " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String s = checkString(bld.toString());
		if(s == null || "".equals(s)){
			s = "{}";
		}
		return new JSONObject(s);
	}
	
	private static String checkString(String str){
		return new String(str.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8") );
	}
	
	
	public static void main(String[] args) {
		JSONObject json = new JSONObject();
		json.put("company", "B01487271");
		json.put("number", 1);
		TediInvoice ti = new TediInvoice(json);
		
		JSONObject aon = ti.getProperty("aon");
	
		System.out.println(aon.length() > 0);
	}
}
