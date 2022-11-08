package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Relationship;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.impl.jooq.dao.RelationshipDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class RegistryRelationshipAutoComplete {
	
	private RegistryRelationshipAutoComplete() {
		throw new IllegalStateException("Utility class");
	}
	
	public static final BiConsumer<AONContext, RegistryRelationship> COMPLETE_RELATIONSHIP = (ctx, rrelationship) -> {
		if(rrelationship.getRelationship().getId()==null) {
			
			rrelationship.setRelationship(
				 RelationshipDAO.get(ctx, f->f.getDomainProperty().eq(0).and( f.getIdProperty().eq(-1) ) )
				 .orElseGet(() ->{
					 Relationship r = new Relationship()
					 .setId(-1)
					 .setDomain(new Domain().setId(0))
					 .setDescription("CUSTOMER_LINKED");
					 
					 return RelationshipDAO.save(ctx, r);
				 })
			 );
			
			ctx.log().info("\t saving rrelationship: autocomplete relationship: ");
		}
	};
	
	public static void autoComplete(AONContext ctx, RegistryRelationship rrelationship) throws AonCoreException {
		COMPLETE_RELATIONSHIP
			.accept(ctx, rrelationship);
	}
}
