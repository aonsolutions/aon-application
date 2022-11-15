package com.esferalia.aon.occam.api;

import java.util.Optional;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.ContractDocFilter;
import com.esferalia.aon.occam.api.model.doc.Doc;
import com.esferalia.aon.occam.impl.jooq.dao.ContractDocDAO;

public class DOC {
	
	public static Optional<Doc<?>> getContratDoc(String domainName, String login, ContractDocFilter filter) {
		try (CloseableAONContext ctx  = AONContext.getAONContext(domainName, login) ) {
			return ContractDocDAO.getContractDoc(ctx, filter);
		}
	}
	
	
	

}
