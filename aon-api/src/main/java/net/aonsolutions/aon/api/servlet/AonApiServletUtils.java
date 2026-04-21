package net.aonsolutions.aon.api.servlet;

import com.esferalia.aon.occam.api.model.AccountProperties;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.invoice.InvoiceFilter;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class AonApiServletUtils {
	
	private AonApiServletUtils() {
		
	}

	static Filter invoiceFilter(InvoiceProperties f, Integer domainId, InvoiceFilter invoiceFilter) {
    	Filter filter =  f.getDomainProperty().eq(domainId);
    
    	if(invoiceFilter.getDescription() != null) {
    		Filter ft = f.getReferenceCodeProperty().like("%" + invoiceFilter.getDescription() + "%")
        			.or(f.getRegistryNameProperty().like("%" + invoiceFilter.getDescription() + "%"))
        			.or(f.getSeriesProperty().like("%" + invoiceFilter.getDescription() + "%"))
        			.or(f.getRegistryDocumentProperty().like("%" + invoiceFilter.getDescription() + "%"))
        			;
    		if (AonStringUtils.isNumeric(invoiceFilter.getDescription())) {
   				Integer i = AonNumberUtils.toInteger( invoiceFilter.getDescription() );
   				Double d = AonNumberUtils.toDouble( invoiceFilter.getDescription() );
				ft = ft.or (f.getNumberProperty().like(i))
					.or (f.getTotalProperty().like(d));
   			}
    		filter = filter.and( ft );
    	}

    	if(invoiceFilter.getTypes() != null && invoiceFilter.getTypes().length > 0) {
    		Filter filter2 = f.getTypeProperty().eq(InvoiceType.safeValueOf(invoiceFilter.getTypes()[0]).value())
    				.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(invoiceFilter.getTypes()[0].toUpperCase()).value()));
    		for(Integer i = 1; i < invoiceFilter.getTypes().length; i++) {
    			filter2 = filter2.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(invoiceFilter.getTypes()[i]).value()))
   					.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(invoiceFilter.getTypes()[i].toUpperCase()).value()));
    		}
    		filter = filter.and(filter2); 
    	}
    	
    	if(invoiceFilter.getFrom() != null) {
    		filter = filter.and(f.getStartIssueDateProperty().ge(invoiceFilter.getFrom()));
    	}
    	
    	if(invoiceFilter.getTo() != null) {
    		filter = filter.and(f.getEndIssueDateProperty().le(invoiceFilter.getTo()));
    	}
    	
    	if(invoiceFilter.getRecorded() != null) {
    		filter = filter.and(f.getStatusProperty().eq(invoiceFilter.getRecorded()));
    	}
    	
    	if(invoiceFilter.getRegistry() != null) {
    		filter = filter.and(f.getRegistryProperty().eq(invoiceFilter.getRegistry()));
    	}
    	
    	if(invoiceFilter.getPage() != null) {
    		filter.page(invoiceFilter.getPage());
    	} 
    	
    	if(invoiceFilter.getPerPage() != null) {
    		filter.perPage(invoiceFilter.getPerPage());
    	}
    	
		return filter;
    }
	
	static Filter accountFilter(AccountProperties f, Domain domain, String type) {
		Integer[] domains = { domain.getId(), domain.getParentId() };
		Filter filter = f.getDomainProperty().in(domains)
			.and(f.getActiveProperty().eq((byte) 1))
			.and(f.getEntryEnabledProperty().eq((byte) 1))
			.and(f.getLevelProperty().eq((byte) 5));
    	InvoiceType iType = InvoiceType.safeValueOf(type);
    	if(iType != null && InvoiceType.SALES.equals(iType)) {
    		filter = filter.and(f.getCodeProperty().like("700%")
    				.or(f.getCodeProperty().like("705%")
    				.or(f.getCodeProperty().like("75%")))); 
    	}
    	if(iType != null && InvoiceType.PURCHASE.equals(iType)) {
    		filter = filter.and(f.getCodeProperty().like("60%").or(f.getCodeProperty().like("62%"))); 
    	}
    	if(iType != null && (InvoiceType.EXPENSES.equals(iType) || InvoiceType.UNDEDUCTIBLE.equals(iType))) {
    		filter = filter.and(f.getCodeProperty().like("629%")); 
    	}
		return filter;
    }
}
