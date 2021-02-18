package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.logging.Logger;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Registry;

public class GlobalDAO {
	
	private static final Logger LOGGER = Logger.getLogger(GlobalDAO.class.getName());
	
	private static final String GLOBAL_DOMAIN_NAME = "global.aonsolutions.net";
	private static final int FAKE_GLOBAL_DOMAIN_ID = -1;

	protected static AONContext getGlobalAONContext(String user) {
		return AONContext.getAONContext(GLOBAL_DOMAIN_NAME, FAKE_GLOBAL_DOMAIN_ID , user); 
	}
	
	protected static Integer getGlobalDomain( AONContext ctx ) {
		return ctx.getDslContext()
			.select( DOMAIN.ID )
			.from(DOMAIN)
			.where(DOMAIN.NAME.eq( GLOBAL_DOMAIN_NAME ) )
			.fetch()
			.stream()
			.map( rec -> rec.getValue(DOMAIN.ID))
			.findFirst()
			.orElse(null);
	}
	
	public static Registry getRegistry( String user, String document ) {
		try ( AONContext ctx =  getGlobalAONContext(user) ) {
			final Integer globalDomain =  getGlobalDomain(ctx);
			return RegistryDAO.getStream(ctx, f -> 
						f.getDomainProperty().eq(globalDomain)
						.and(f.getDocumentProperty().eq(document)))
				.findFirst()
				.orElse(null);
			
		} catch (Throwable t) {
			LOGGER.severe("Con not read GLOBAL registries ("+ t.getMessage() +")");
			return null;
		}
	}
	
}

