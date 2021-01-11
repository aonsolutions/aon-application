package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonEnumUtils;

class RegistryDAO {

	protected static Registry insert(AONContext ctx, Registry registry) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext().insertInto(REGISTRY)
			.set(REGISTRY.DOMAIN, registry.getDomain().getId())
			.set(REGISTRY.DOCUMENT,registry.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE,registry.getDocumentType()==null?null:registry.getDocumentType().value())
			.set(REGISTRY.DOCUMENT_COUNTRY,registry.getDocumentCountry()==null?null:registry.getDocumentCountry().getIso2())
			.set(REGISTRY.NAME,registry.getName())
			.set(REGISTRY.ALIAS,registry.getAlias())
			.set(REGISTRY.TYPE,AonEnumUtils.getByte(registry.isLegalPerson()))
			.set(REGISTRY.NATIONALITY,registry.getNationality()==null?null:registry.getNationality().getIso2())
			.set(REGISTRY.SECURITY_LEVEL,(registry.isConfidential()
					?SecurityLevel.CONFIDENTIAL.value()
					:SecurityLevel.OFFICIAL.value()))
			.returning(REGISTRY.ID)
			.fetchOne()
			.getValue(REGISTRY.ID);
		registry.setId(id);
		ctx.log().info("INSERT REGISTRY id: " + registry.getId());
		return registry; 
	}
	
}
