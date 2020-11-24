package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import com.esferalia.aon.occam.api.AONContext;

public class OCRDAO {
	
	private static final int DOMAIN = 0;
	private static final String DOMAIN_NAME = "console.aonsolutions.org";
	
	private static final String OCR_REF_PATTERN = "OCR_REF_PATTERN";
 
	public static String[] getReferencePatterns( String user, String document ) {
		try ( AONContext ctx =  AONContext.getAONContext(DOMAIN_NAME, DOMAIN, user) ) {
			return ctx.getDslContext()
				.select( RADDINFO.VALUE )
				.from(RADDINFO)
				.innerJoin(REGISTRY).on(REGISTRY.ID.eq(RADDINFO.REGISTRY))
				.where(REGISTRY.DOMAIN.eq(DOMAIN))
				.and(REGISTRY.DOCUMENT.eq( document))
				.and(RADDINFO.ATTRIBUTE.eq( OCR_REF_PATTERN ))
				.orderBy(RADDINFO.VALUE_DATE.desc())
				.fetch()
				.stream()
				.map( rec -> rec.getValue(RADDINFO.VALUE))
				.toArray(String[]::new);
		} catch (Throwable t) {
			// TODO Asignar el dominio correctamente
			return null;
		}
		
	}
	
}
