package solutions.aon.in.invoice.tedi;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import es.translogia.tedi.ewok.TediInsightInvoice;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.ewok.TediNif;
import es.translogia.tedi.ewok.TediNifType;
import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.templates.Document;
import solutions.aon.in.invoice.templates.Document.DocumentType;
import solutions.aon.in.invoice.templates.InvoiceTax;

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
	public void setInsightNifs(Collection<Document> nifs) {
		if (nifs != null && nifs.size() > 0) {
			LinkedHashMap<String,TediNif> uniqueNifs = new LinkedHashMap<String, TediNif>();
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
			TediInsightInvoice insight =  invoice.ensureInsights();
			insight.setNifs(tediNifs.toArray( new TediNif[tediNifs.size()] ));
		}
	}

	@Override
	public void setInsightDates(Collection<Date> dates) { 
		if (dates != null && dates.size() > 0) {
			TediInsightInvoice insight =  invoice.ensureInsights();
			insight.setDates(dates.toArray( new Date[dates.size()] ));
			
			// ----- Se asume la fecha que más se repite como fecha de factura
			if (invoice.getDate() == null) {
				TreeMap<Date, Integer> map = new TreeMap<>();
				dates.forEach(e -> map.put(e, map.getOrDefault(e, 0) + 1));
				Date issueDate = null;
				int i = -1;
				for (Date id : map.keySet()) {
					int x = map.get(id);
					if (x > i) {
						issueDate = id;
						i =x;
					}
				}
				invoice.setDate(issueDate);
			}
			// ----- 
		}
	}

	@Override
	public void setInsightAmounts(Collection<Double> amounts) {
		if (amounts != null && amounts.size() > 0) {
			TediInsightInvoice insight =  invoice.ensureInsights();
			insight.setAmounts(amounts.toArray( new Double[amounts.size()] ));
		}
	}

	@Override
	public void setInsightTotals(Collection<Double> totals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTotal(double total) {
		invoice.setTotal(total);
		
	}

	@Override
	public void setTax(InvoiceTax tax) {
		if (tax != null) {
			invoice.ensureVatTax(tax.getBase(), tax.getQuota(), tax.getPercent());
		}
	}
	
	@Override
	public boolean hasIssueDate() {
		return invoice.getDate() != null;
	}
	@Override
	public void setIssueDate(Date date) {
		invoice.setDate(date);
	}
}
