package com.esferalia.aon.occam.api;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.RRelationshipFilter;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;

public interface IRelationship {
	
	public Optional<RegistryRelationship> getRegistryRelationship(AONContext ctx, RRelationshipFilter filter);
	
	public Stream<RegistryRelationship> getRegistryRelationshipStream(AONContext ctx, RRelationshipFilter filter);		
	
	public RegistryRelationship saveRegistryRelationship(AONContext ctx, RegistryRelationship rrelationship);
	
	public void deletetRegistryRelationship(AONContext ctx, RRelationshipFilter filter);

}
