package net.aonsolutions.occam.test;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.dao.DomainDAO;

class DomainProvider {
	
	private DomainProvider() {
	}
	
	static Domain getOrCreateDomain(AONContext ctx,String domainName, String user) {
		return DomainDAO.get(ctx, p -> p.withName().eq(domainName))
			.orElseThrow(() -> new IllegalStateException(
				"NO DOMAIN!. Run aon-occam tests for provide suitable environment")); 
	}
	
}
