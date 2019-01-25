package net.aonsolutions.aon.tedi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

import es.translogia.tedi.TediInvoice;
import es.translogia.tedi.TediInvoiceType;

@SuppressWarnings("serial")
@WebServlet(name = "Tedi2AonServlet", urlPatterns = { "/tedi2aon/*"})
public class TEDI2AON extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject json = getRequestJSON(req);
		String domainName = req.getServerName();
		Integer domainId = 1;
			
		String cp = json.getString("company");
		String login = "";
		Company company = AON.getCompany(domainName, domainId, login, f -> f.getDocumentProperty().eq(cp));
		Domain domain = AON.getDomain(domainName, company.getDomain(), login);
		tedi2aon(domain, login, json);		
	}
	
	private void tedi2aon(Domain domain, String login, JSONObject json) {
		TediInvoice ti = new TediInvoice(json);

		JSONObject aon = ti.getProperty("aon");
		Invoice invoice = new Invoice();
		
		if (aon.length() > 0) {
			invoice = AON.getInvoice(aon.getString("domain_name"), aon.getInt("domain_id"), aon.getString("login"),
					f -> f.getIdProperty().eq(aon.getInt("invoice")));
		} else {
			invoice.setDomain(domain.getId());
			invoice.setSeries("TEDI");
			invoice.setNumber(ti.getNumber());
		}
		
		LinkedList<Scope> scopeList = AON
				.getScopeStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()))
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
					registry = AON.insertRegistry(domain.getName(), domain.getId(), login, registry);
				}
				customer = new Customer();
				customer.setDomain(domain.getId());
				customer.setRegistry(registry);
				customer = AON.insertCustomer(domain.getName(), domain.getId(), login, customer);
			}
			invoice.setRegistry(customer.getRegistry().getId());
			invoice.setRegistryDocument(customer.getDocument());
			invoice.setRegistryDocumentCountry(customer.getDocumentCountry());
			invoice.setRegistryDocumentType(customer.getDocumentType());
			invoice.setRegistryName(customer.getName());
			// TODO invoice.setRegistryAddress(0);
			invoice.setType(InvoiceType.SALES);
		} else if (TediInvoiceType.RECEIVED.equals(ti.getType())) {
			Supplier supplier = AON.getSupplier(domain.getName(), domain.getId(), login,
					f -> f.getDocumentProperty().eq(ti.getSender().getDocument())).orElse(new Supplier());
			if (supplier.getId() == null) {
				Registry registry = AON.getRegistry(domain.getName(), domain.getId(), login,
						f -> f.getDocumentProperty().eq(ti.getReceiver().getDocument()));
				if (registry.getId() == null) {
					registry = new Registry();
					registry.setDocument(ti.getReceiver().getDocument());
					registry.setName(ti.getReceiver().getName());
					registry = AON.insertRegistry(domain.getName(), domain.getId(), login, registry);
				}
				supplier = new Supplier();
				supplier.setDomain(domain.getId());
				supplier.setDocument(ti.getSender().getDocument());
				supplier.setId(registry.getId());
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
		invoice.setIssueDate(ti.getDate());
		invoice.setTaxDate(ti.getDate());
		invoice.setSecurityLevel(SecurityLevel.OFFICIAL);
		invoice.setStatus((byte) 0);
		invoice.setScope(scope);
		invoice.setComments(ti.getComments());
		invoice.setRemarks(ti.getRemarks());

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

		invoice.setTaxableBase(ti.getTaxableBase());
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
			// id.setItem(null);
			// id.setTaxes(1);
			id.setWorkPlace(workPlace.getId());
			// id.setWarehouse(warehouse);
			id.setDescription(detail.getDescription());
			id.setDiscountExpression(detail.getDiscount().toString());
			id.setQuantity(detail.getQuantity());
			id.setPrice(detail.getPrice());
			AON.insertInvoiceDetail(domain.getName(), domain.getId(), login, id);
		});

		// INVOICE FINANCE

		ti.getFinances().stream().forEach(finance -> {
			Finance f = new Finance().setDomain(domain.getId())
					.setPayment(TediInvoiceType.RECEIVED.equals(ti.getType()) ? true : false)
					.setInvoice(new Invoice().setId(invoiceId)).setScope(scope).setDueDate(finance.getDueDate())
					.setBankAccount(new BankAccount(finance.getBankAccount()));
			AON.insertFinance(domain.getName(), domain.getId(), login, f);
		});

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
