package com.esferalia.aon.occam.impl.jooq;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IRelationship;
import com.esferalia.aon.occam.api.model.Filter.RRelationshipFilter;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryRelationshipDAO;

public class RelationshipImpl implements IRelationship{

	@Override
	public Optional<RegistryRelationship> getRegistryRelationship(AONContext ctx, RRelationshipFilter filter) {
		return	ctx.getDslContext().transactionResult(configuration -> RegistryRelationshipDAO.get(ctx, filter));
	}

	@Override
	public Stream<RegistryRelationship> getRegistryRelationshipStream(AONContext ctx, RRelationshipFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> RegistryRelationshipDAO.getStream(ctx, filter));
	}

	@Override
	public RegistryRelationship saveRegistryRelationship(AONContext ctx, RegistryRelationship rrelationship) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryRelationshipDAO.save(ctx, rrelationship));
	}

	@Override
	public void deletetRegistryRelationship(AONContext ctx, RRelationshipFilter filter) {
		ctx.getDslContext().transaction(configuration -> RegistryRelationshipDAO.delete(ctx, filter));
	}

}
