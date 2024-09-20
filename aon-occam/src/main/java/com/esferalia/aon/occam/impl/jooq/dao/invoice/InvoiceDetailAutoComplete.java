package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import java.text.MessageFormat;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceDetailAutoComplete {
	
	private static final String DETAIL_MSG = "Fra. n\u00AA: {0} del {1,date,dd/MM/yyyy}. ";

	private InvoiceDetailAutoComplete() {
		
	}

	static void complete(AONContext ctx, Invoice invoice, InvoiceDetail detail) {
		
		detail.setInvoice(invoice.getId());
		detail.setDomain(invoice.getDomain());
		
		if (AonStringUtils.isBlank(detail.getDescription()) && detail.getSource() == InvoiceSource.ACCOUNT) {
			detail.setDescription(MessageFormat.format(DETAIL_MSG, invoice.getReferenceCode(), invoice.getIssueDate()));	
		}
		if (detail.getWorkplace() == null ||  detail.getWorkplace().getId() == null) {
			ctx.getConfiguration().getWorkplaceIfOnlyOne()
				.ifPresent( w -> detail.setWorkplace(w));
		}
	}


}
