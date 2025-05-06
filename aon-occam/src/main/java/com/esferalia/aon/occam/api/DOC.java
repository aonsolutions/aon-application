package com.esferalia.aon.occam.api;

import java.util.Optional;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.ContractDocFilter;
import com.esferalia.aon.occam.api.model.doc.Doc;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.impl.jooq.dao.ContractDocDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDocDAO;

public class DOC {
	
	private DOC() {
		
	}
	
	public static Optional<Doc<?>> getContratDoc(String domainName, String login, ContractDocFilter filter) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, login) ) {
			return ContractDocDAO.getContractDoc(ctx, filter);
		}
	}
	
	public static Optional<InvoiceDoc> getInvoiceDocs(String domainName, String login, int domain, Integer invoiceId) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, login) ) {
			return InvoiceDocDAO.get(ctx, domain, invoiceId);
		}
	}

	public static void deleteInvoiceFiscal(String schema, Integer invoiceId) {
		try(CloseableAONContext ctx = AONContext.getAONContext(schema)){
			InvoiceDocDAO.delete(ctx, invoiceId);
		}
	}	
	
//	public static Optional<InvoiceDoc> getInvoiceDoc(String domainName, String login, InvoiceDocFilter filter) {
//		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, login) ) {
//			return InvoiceDocDAO.get(ctx, filter);
//		}
//	}

}
