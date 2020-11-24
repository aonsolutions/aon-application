package solutions.aon.in.invoice.tedi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;

import es.translogia.tedi.ewok.TediInsightInvoice;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediNif;
import es.translogia.tedi.ewok.TediNifType;
import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.templates.Document;
import solutions.aon.in.invoice.templates.Document.DocumentType;

public class TediInsightInvoiceBuilder implements InvoiceBuilder<TediInvoice> {
	
	protected TediInvoice invoice;

	public TediInsightInvoiceBuilder( ) {
		invoice = new TediInvoice()
				.setInsight( new TediInsightInvoice());
	}
	
	@Override
	public TediInvoice get() {
		return invoice;
	}
	public TediInsightInvoice getInsight() {
		return invoice.getInsight();
	}
	
	@Override
	public void addInsightNifs(Collection<Document> nifs) {
		if (nifs != null && nifs.size() > 0) {
			LinkedHashSet<TediNif> uniqueDocuments = new LinkedHashSet<TediNif>();
			if (getInsight().getNifs() != null && getInsight().getNifs().length > 0 ) {
				for(int i = 0; i < getInsight().getNifs().length; i++) {
					uniqueDocuments.add( getInsight().getNifs()[i] );
				}
			}
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
				uniqueDocuments.add(tediNif);
			}
			getInsight().setNifs( uniqueDocuments.toArray( new TediNif[uniqueDocuments.size()] ));
		}
	}

	@Override
	public void addInsightDates(Collection<Date> dates) {
		if (dates != null && dates.size() > 0) {
			LinkedHashSet<Date> uniqueDates = new LinkedHashSet<Date>();
			if ( getInsight().getDates() != null && getInsight().getDates().length > 0) {
				for ( Date d : getInsight().getDates()) {
					uniqueDates.add(d);					
				}
			}
			uniqueDates.addAll( dates );
			getInsight().setDates(uniqueDates.toArray( new Date[uniqueDates.size()] ));
		}
	}

	@Override
	public void addInsightAmounts(Collection<Double> amounts) {
		if (amounts != null && amounts.size() > 0) {
			LinkedHashSet<Double> uniqueAmounts = new LinkedHashSet<Double>( amounts );
			if ( getInsight().getAmounts() != null && getInsight().getAmounts().length > 0) {
				for ( Double a : getInsight().getAmounts()) {
					uniqueAmounts.add(a);					
				}
			}
			uniqueAmounts.addAll( amounts );
			getInsight().setAmounts(uniqueAmounts.toArray( new Double[uniqueAmounts.size()] ));
		}
	}

	@Override
	public void setInsightIssueDate(Date issueDate) {
		if (!getInsight().hasIssueDate()) {
			getInsight().setIssueDate(issueDate);
		}
	}

	@Override
	public void setInsightTotal(Double total) {
		if (!getInsight().hasTotal()) {
			getInsight().setTotal(total);
		}
	}
	
	@Override
	public void finalizeParse() {
		// Se trata de asignar una fecha de factura a partir de las fechas parseadas.
		if (!getInsight().hasIssueDate() && getInsight().getDates() != null && getInsight().getDates().length > 0) {
			Date issueDate = null;
			if ( getInsight().getDates().length == 1) {
				// Si solo hay una fecha parseada, se asume como fecha de fatura
				issueDate = getInsight().getDates()[0];
			} else {
				// Se averigua la fecha mas alta parseada.
				Date last = Date.from(LocalDateTime.of(0, 1, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
				for (Date date : getInsight().getDates()) {
					last = date.after(last) ? date : last;
				}
				LocalDate lastLocalDate = LocalDate.ofInstant( last.toInstant(), ZoneId.systemDefault() );
				int lastYear = lastLocalDate.getYear() - 1;
				Date limit = Date.from(LocalDateTime.of(lastYear, 1, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
				// Se asume como fecha de factura la fecha mas cercana al uno de enero del año de la ultima fecha parseada.
				for (Date date : getInsight().getDates()) {
					if ( !date.before(limit) ) {
						if (issueDate == null || date.before(issueDate)) {
							issueDate = date;
						}
					}
				}
			}
			setInsightIssueDate( issueDate ); 
		}
		
		// No se ha parseado un total a partir de las expresiones definidas.
		// Se asume como total factura la cantidad mas alta parseada.
		if (!getInsight().hasTotal() && getInsight().getAmounts() != null && getInsight().getAmounts().length > 0) {
			Double maxValue = Double.MIN_VALUE;
			for (Double d : getInsight().getAmounts()) {
				maxValue = maxValue > d  ? maxValue : d;
			}
			setInsightTotal( maxValue );
		}
	}
	
	@Override
	public String[] getReferencePatterns() {
		return null;
	}
	
	@Override
	public void setReference(String reference) {
		if (get().getReference() == null || "".equals( get().getReference().trim() ) ) {
			get().setReference(reference);
		}
	}
}
