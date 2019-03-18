package net.aonsolutions.aon.tedi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;

import es.translogia.tedi.TediAddress;
import es.translogia.tedi.TediDetail;
import es.translogia.tedi.TediFinance;
import es.translogia.tedi.TediInvoice;
import es.translogia.tedi.TediRegistry;
import es.translogia.tedi.TediTax;
import es.translogia.tedi.TediTransaction;
import es.translogia.tedi.TediUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Aon2TediServlet", urlPatterns = { "/aon2tedi/*"})
public class AON2TEDI extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject json = getRequestJSON(req);
		String domainName = json.getString("domain_name");
		Integer domainId = json.getInt("domain_id");
		Integer invoiceId = json.getInt("domain_id");
		String login = json.getString("login");
		Domain domain = AON.getDomain(domainName, domainId, login);

		Company company = AON.getCompany(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()));

		Stream<InvoiceDetail> invoiceDetailStream = AON.getInvoiceDetails(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(invoiceId));
		Invoice invoice = AON.getInvoice(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(invoiceId));
		Stream<Finance> financeStream = AON.getFinanceStream(domain.getName(), domain.getId(), login, f -> f.getInvoiceProperty().eq(invoice.getId()));
		Stream<InvoiceTax> invoiceTaxStream = AON.getInvoiceTaxStream(domain.getName(), domain.getId(), login, invoiceId);
		
		aon2tedi(domain, login, company.getDocument(), invoice, invoiceDetailStream, financeStream, invoiceTaxStream);		
	}
	
	
	private void aon2tedi(Domain domain, String login, String companyDocument, Invoice invoice, 
			Stream<InvoiceDetail> invoiceDetailStream, Stream<Finance> financeStream, Stream<InvoiceTax> invoiceTaxStream) {
		TediInvoice ti = new TediInvoice()
				.setCompany(companyDocument)
				.setReference(invoice.getReferenceCode())
				.setStatus("pendiente")
				.setSource("aon")
				.setDate(invoice.getIssueDate())
				.setComments(invoice.getComments())
				.setTransaction(TediTransaction.values()[invoice.getTransaction().value()])
				.setTotal(invoice.getTotal())
				.setInvestment(invoice.isInvestment());
		
		Registry registry = AON.getRegistry(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(invoice.getRegistry()));
		RAddress raddress = AON.getRAddres(domain.getName(), domain.getId(), login, registry.getId());
		TediRegistry tr = new TediRegistry();
		tr.setDocument(registry.getDocument());
		tr.setName(registry.getName());
		tr.setAddress(new TediAddress()
				.setAddress(raddress.getAddress())
				.setCity(raddress.getCity())
				.setPostalCode(raddress.getZip()));
		
		// sender
		if(InvoiceType.SALES.equals(invoice.getType()))
			ti.setReceiver(tr);
		else ti.setSender(tr);
		
		// details 
		LinkedList<TediDetail> details = new LinkedList<>();
		invoiceDetailStream.forEach(detail -> {
			TediDetail d = new TediDetail()
					.setDescription(detail.getDescription())
					.setPrice(detail.getPrice())
				//	.setPurchasePrice(detail.getItem().getPurchasePrice())
					.setQuantity(detail.getQuantity());
					//.setDiscount(detail.getDiscountExpression());
			details.add(d);
		});
		ti.setDetails(details);
		
		// finances
		LinkedList<TediFinance> finances = new LinkedList<>();
		financeStream.forEach(finance -> {
			TediFinance f = new TediFinance()
					.setAmount(finance.getAmount())
					.setIban(finance.getBankAccount().getIban())
					.setDueDate(finance.getDueDate())
					.setPayMethod(getPaymethod(finance.getPayMethod()))
					.setStatus(finance.isPayment() ? "pagado" : "pendiente");
			finances.add(f);
		});
		ti.setFinances(finances);
		
		// taxes
		LinkedList<TediTax> taxes = new LinkedList<>();
		invoiceTaxStream.forEach(tax -> {
			TediTax t = new TediTax()
					.setBase(tax.getBase())
					.setPercentage(tax.getPercentage())
					.setQuota(tax.getQuota())
					.setSurcharge(tax.getSurcharge())
					.setSurchargeQuota(tax.getSurchargeQuota());
			taxes.add(t);
		});
		ti.setTaxes(taxes);
		
		
		JSONObject aon = new JSONObject()
				.put("domain_name", domain.getName())
				.put("domain_id", domain.getId())
				.put("login", login)
				.put("invoice", invoice.getId());
		
		ti.addProperties("aon", aon);

		TediUtils tedi = TediUtils.getInstance("jgarcia@aonsolutions.es", "jg130365");

		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), login, DataResponseSource.TEDI_INVOICE, f -> 
			f.getSourceIdProperty().eq(invoice.getId()));
		if(dr.getId() != null) {
			Optional<DataResponseDetail> drd = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> 
				f.getDataResponseProperty().eq(dr.getId())
				.and(f.getDataVariableProperty().eq("tedi_number"))
			);
			if(drd.isPresent()) {
				ti.setNumber(Integer.parseInt(drd.get().getDataValue()));
				tedi.updateInvoice(ti);
			}
		} else {
			DataResponse dr2 = new DataResponse()
					.setDomain(invoice.getDomain())
					.setSource(DataResponseSource.TEDI_INVOICE)
					.setSourceId(invoice.getId());
			dr2 = AON.insertDataResponse(domain.getName(), domain.getId(), login, dr2);
			
			ti = tedi.createInvoice(ti);
			
			DataResponseDetail drd1 = new DataResponseDetail()
					.setDomain(invoice.getDomain())
					.setDataVariable("tedi_company")
					.setDataValue(ti.getCompany());
			
			DataResponseDetail drd2 = new DataResponseDetail()
					.setDomain(invoice.getDomain())
					.setDataVariable("tedi_number")
					.setDataValue(ti.getNumber().toString());

			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd1);
			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
		}
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
	
	private String getPaymethod(Integer p) {
		PayMethodType paymethod = PayMethodType.values()[p];
		if(PayMethodType.CASH_BASIS.equals(paymethod)) return "cash";
		else if(PayMethodType.DEBIT_CARD.equals(paymethod)) return "debit";
		else if(PayMethodType.CREDIT_CARD.equals(paymethod)) return "credit";
		else if(PayMethodType.BANK_TRANSFER.equals(paymethod)) return "transfer";
		else return "other";
	}
	
}
