package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;

import java.util.function.Function;

import  org.jooq.Record;

import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class SellerDAO {
	
	protected static class SellerFiller  implements Function<Record,Seller> {

		@Override
		public Seller apply(Record r) {
			return buildSeller(r, REGISTRY);
		}
		
		public static Seller buildSeller(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			if(registry == null) 
				registry = REGISTRY;
			return  new Seller()
					.setDomain(r.getValue(SELLER.DOMAIN))
					.setId(r.getValue(SELLER.REGISTRY))
					.setActive(r.getValue(SELLER.STATUS) == 1)
					.setCommissionType(new CommissionType().setId(r.getValue(SELLER.COMMISSION_TYPE)))
					//.setScope(r.getValue(SCOPE.DESCRIPTION))
					
					.setRegistryAlias(r.getValue(registry.ALIAS))
					.setRegistryConfidential(r.getValue(registry.SECURITY_LEVEL) == 1)
					.setRegistryDocument(r.getValue(registry.DOCUMENT))
					.setRegistryDocumentCountry(Country.valueOf(r.getValue(registry.DOCUMENT_COUNTRY)))
					.setRegistryName(r.getValue(registry.NAME))
					.setRegistryDocumentType(DocumentType.values()[r.getValue(registry.DOCUMENT_TYPE)])
					.setRegistryNationality(Country.valueOf(r.getValue(registry.NATIONALITY)))
					;
		}
	}
	
}
