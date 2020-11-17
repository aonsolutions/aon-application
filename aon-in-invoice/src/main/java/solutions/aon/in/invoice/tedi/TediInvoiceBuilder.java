package solutions.aon.in.invoice.tedi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import es.translogia.tedi.ewok.TediInsightInvoice;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.ewok.TediNif;
import es.translogia.tedi.ewok.TediNifType;
import es.translogia.tedi.ewok.TediTaxType;
import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.templates.Document;
import solutions.aon.in.invoice.templates.Document.DocumentType;
import solutions.aon.in.invoice.templates.InvoiceTax;
import solutions.aon.in.invoice.templates.TaxType;

public class TediInvoiceBuilder implements InvoiceBuilder<TediInvoice> {
	
	private TediInvoiceContext ctx;
	private TediInvoice invoice;

	public TediInvoiceBuilder( TediInvoiceContext ctx) {
		this.ctx = ctx;
		invoice = new TediInvoice();
		invoice.setType(TediInvoiceType.RECIBIDA);
		invoice.ensureReceiver().setDocument(ctx.getDocument()).setName(ctx.getName());
	}
	
	public TediInvoice getInvoice() {
		return invoice;
	}
	
	@Override
	public void addInsightNifs(Collection<Document> nifs) {
		TediInsightInvoice insight =  invoice.ensureInsights();
		LinkedHashMap<String,TediNif> uniqueNifs = new LinkedHashMap<String, TediNif>();
		if (insight.getNifs() != null) {
			for (TediNif nif : insight.getNifs()) {
				uniqueNifs.put(nif.getStr(), nif);
			}
		}
		if (nifs != null && nifs.size() > 0) {
			LinkedList<TediNif> tediNifs = new LinkedList<TediNif>();
			for (Document nif : nifs ) {
				TediNif tediNif = new TediNif();
				tediNif.setStr(nif.getData());
				if ( nif.getType() == DocumentType.LEGAL_PERSON_NIF || nif.getType() == DocumentType.NATURAL_PERSON_NIF) {
					tediNif.setType( TediNifType.CIF);
				} else if ( nif.getType() == DocumentType.DNI) {
					tediNif.setType( TediNifType.DNI);
				} else if ( nif.getType() == DocumentType.NIE) {
					tediNif.setType( TediNifType.NIE);
				}
				tediNifs.add(tediNif);
				if (!uniqueNifs.containsKey(tediNif.getStr())) {
					uniqueNifs.put(tediNif.getStr(), tediNif);
					if (ctx.getDocument() != null && !ctx.getDocument().equals(tediNif.getStr())) {
						invoice.ensureSender().setDocument(tediNif.getStr());
					}
				}
			}
			insight.setNifs(uniqueNifs.values().toArray( new TediNif[uniqueNifs.size()] ));
		}
	}

	@Override
	public void addInsightDates(Collection<Date> dates) { 
		TediInsightInvoice insight =  invoice.ensureInsights();
		LinkedHashSet<Date> uniqueDates = new LinkedHashSet<Date>();
		if (insight.getDates() != null) {
			for (Date date : insight.getDates()) {
				uniqueDates.add(date);
			}
		}
		if (dates != null && dates.size() > 0) {
			for (Date date : dates) {
				uniqueDates.add(date);
			}
			insight.setDates(uniqueDates.toArray( new Date[uniqueDates.size()] ));
			
			// ----- Se asume la fecha que más se repite como fecha de factura
			if (!hasIssueDate()) {
				Date limit = Date.from(LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
				Date issueDate = null;
				for (Date date : dates) {
					if ( !date.before(limit) ) {
						if (issueDate == null || date.before(issueDate)) {
							issueDate = date;
						}
					}
				}
				invoice.setDate(issueDate);
			}
			// ----- 
		}
	}

	@Override
	public void addInsightAmounts(Collection<Double> amounts) {
		TediInsightInvoice insight =  invoice.ensureInsights();
		LinkedHashSet<Double> uniqueAmouts = new LinkedHashSet<Double>();
		if (insight.getAmounts() != null) {
			for (Double amount : insight.getAmounts()) {
				uniqueAmouts.add(amount);
			}
		}
		if (amounts != null && amounts.size() > 0) {
			uniqueAmouts.addAll(amounts);	
			insight.setAmounts(uniqueAmouts.toArray( new Double[uniqueAmouts.size()] ));
		}
	}

	@Override
	public void setInsightTotals(Collection<Double> totals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getTotal() {
		return invoice.getTotal();
	}
	@Override
	public void setTotal(double total) {
		invoice.setTotal(total);
		
	}

	@Override
	public LinkedList<InvoiceTax> getTaxes() {
		if ( hasTaxes() )  {
			LinkedList<InvoiceTax> taxes = new LinkedList<InvoiceTax>(); 
			for ( TediInvoiceTax tediTax : invoice.getTaxes() ) {
				taxes.add( new InvoiceTax()
					.setType( (tediTax.getTaxType() == TediTaxType.IRPF  ? TaxType.IRPF : TaxType.IVA)  )
					.setBase(tediTax.getBase())
					.setPercent(tediTax.getPercentage())
					.setQuota(tediTax.getQuota()));
			}
			return taxes;
		}
		return null;
	}
	@Override
	public void setTax(InvoiceTax tax) {
		if (tax != null) {
			invoice.ensureTax( 
				new TediInvoiceTax()
					.setTaxType( (tax.getType() == TaxType.IRPF  ? TediTaxType.IRPF : TediTaxType.IVA)  )
					.setBase(tax.getBase())
					.setPercentage(tax.getPercent())
					.setQuota(tax.getQuota()));
		}
	}
	
	@Override
	public void setIssueDate(Date date) {
		invoice.setDate(date);
	}
	
	@Override
	public void setReference(String reference) {
		invoice.setReference(reference);
	}
	
	@Override
	public String getSenderDocument() {
		if (hasSender()) {
			return invoice.getSender().getDocument();
		}
		return null;
	}
	@Override
	public boolean hasTaxes() {
		return invoice.getTaxes() != null;
	}
	@Override
	public boolean hasIssueDate() {
		return invoice.getDate() != null;
	}
	@Override
	public boolean hasReference() {
		return invoice.getReference() != null;
	}
	@Override
	public boolean hasTotal() {
		return invoice.getTotal() != null;
	}
	@Override
	public boolean hasSender() {
		return invoice.getSender() != null 
			&& invoice.getSender().getDocument() != null
			&& invoice.getSender().getDocument().trim() != "";
	}
	
}
