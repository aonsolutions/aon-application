package com.esferalia.aon.occam.api;

import java.util.Optional;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.ContractDocFilter;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDocFilter;
import com.esferalia.aon.occam.api.model.doc.Doc;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.impl.jooq.dao.ContractDocDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDocDAO;

public class DOC {
	
	public static Optional<Doc<?>> getContratDoc(String domainName, String login, ContractDocFilter filter) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, login) ) {
			return ContractDocDAO.getContractDoc(ctx, filter);
		}
	}
	
	public static Optional<InvoiceDoc> getInvoiceDoc(String domainName, String login, InvoiceDocFilter filter) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, login) ) {
			return InvoiceDocDAO.get(ctx, filter);
		}
	}

}
