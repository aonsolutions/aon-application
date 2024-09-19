package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.error.AonCoreException;


class InvoiceAttachAutoComplete {
		
	static void completeInvoiceAttach(AONContext ctx,Invoice inv) throws AonCoreException {
		inv.getAttach().ifPresent( a ->  a
			.setDomain( new Domain().setId(inv.getDomain()))
			.setAttachModule(inv.getId())
			.setDate(inv.getIssueDate())
			.setAttachType( AttachType.INVOICE )
			.setType( InvoiceAttachmentType.INVOICE.value() )
		);
	}
	
}
