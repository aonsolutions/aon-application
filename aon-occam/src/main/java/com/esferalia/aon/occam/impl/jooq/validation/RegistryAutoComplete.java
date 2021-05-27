package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RegistryAutoComplete {
	
	public static BiConsumer<AONContext,Registry> COMPLETE_DOCUMENT_COUNTRY = (ctx,reg) -> {
		if (reg.getDocumentCountry() == null) {
			ctx.log().info("\t saving registry: autocomplete document country: " + Country.ES.getIso2());
			reg.setDocumentCountry(Country.ES);
		}
	};

	public static BiConsumer<AONContext,Registry> COMPLETE_DOCUMENT_TYPE = (ctx,reg) -> {
		if (reg.getDocumentCountry() == Country.ES && AonStringUtils.isNotBlank( reg.getDocument()) ) {
			if (reg.getDocumentType() == null || reg.getDocumentType() == DocumentType.NIF 
			 || reg.getDocumentType() == DocumentType.CIF || reg.getDocumentType() == DocumentType.NIE) {
				
				if ( AonDocumentUtil.isValidDNI( reg.getDocument() )) {
					if (reg.getDocumentType() != DocumentType.NIF) {
						reg.setDocumentType(DocumentType.NIF);
						ctx.log().info("\t saving registry: autocomplete document type: " + DocumentType.NIF.getDescription());			
					}
				} else if ( AonDocumentUtil.isValidCIF( reg.getDocument() )) {
					if (reg.getDocumentType() != DocumentType.CIF) {
						reg.setDocumentType(DocumentType.CIF);
						ctx.log().info("\t saving registry: autocomplete document type: " + DocumentType.CIF.getDescription());			
					}
				} else if ( AonDocumentUtil.isValidNIE( reg.getDocument() )) {
					if (reg.getDocumentType() != DocumentType.NIE) {
						reg.setDocumentType(DocumentType.NIE);
						ctx.log().info("\t saving registry: autocomplete document type: " + DocumentType.NIE.getDescription());			
					}
				} 
			}
		}
	};

	public static BiConsumer<AONContext,Registry> COMPLETE_NATIONALITY = (ctx,reg) -> {
		if (reg.getNationality() == null) {
			ctx.log().info("\t saving registry: autocomplete nationality: " + Country.ES.getIso2());
			reg.setNationality(Country.ES);
		}
	};

	public static BiConsumer<AONContext,Registry> COMPLETE_LEGAL_ENTITY = (ctx,reg) -> {
		if (reg.getDocumentCountry()  == Country.ES) {
			boolean legalPerson = AonDocumentUtil.isEntity(reg.getDocument());
			if (legalPerson != reg.isLegalPerson()) {
				ctx.log().info("\t saving registry: autocomplete legal person: " + legalPerson + " ["+reg.getDocument()+"]");
				reg.setLegalPerson( legalPerson );
			}
		}
	};

	public static void autoComplete(AONContext ctx, Registry registry) throws AonCoreException {
		
		COMPLETE_DOCUMENT_COUNTRY
			.andThen(COMPLETE_DOCUMENT_TYPE)
			.andThen(COMPLETE_NATIONALITY)
			.andThen(COMPLETE_LEGAL_ENTITY)
			.accept(ctx, registry);

	}

}
