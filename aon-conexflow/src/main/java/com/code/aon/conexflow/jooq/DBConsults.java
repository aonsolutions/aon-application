package com.code.aon.conexflow.jooq;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;

import com.esferalia.aon.occam.api.AONContext;

public class DBConsults {

	public static String getEnterpriseId(String domain , Integer domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			return  ctx.getDslContext()
			.select(ENTERPRISE.REGISTRY)
			.from(ENTERPRISE)
			.where(ENTERPRISE.DOMAIN.eq(domainId))
			.fetchOne().value1().toString();
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	
	
	
}
