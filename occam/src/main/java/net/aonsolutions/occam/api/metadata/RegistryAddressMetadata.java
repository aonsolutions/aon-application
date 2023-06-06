package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum RegistryAddressMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,DOMAIN 
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitDomain(t);} }
	,REGISTRY
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitRegistry(t);} }
	,MAIN
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitMain(t);} }
	,RECIPIENT
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitRecipient(t);} }
	,STREET_TYPE
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitStreetType(t);} }
	,ADDRESS
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitAddress(t);} }
	,NUMBER
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitNumber(t);} }
	,ADDRESS2
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitAddress2(t);} }
	,ADDRESS3
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitAddress3(t);} }
	,GEOZONE
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitGeozone(t);} }
	,PARENT_GEOZONE
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitParentGeozone(t);} }
	,ZIP
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitZip(t);} }
	,CITY
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitCity(t);} }
	,ALIAS
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitAlias(t);} }
	,MUNICIPALITY_CODE
		{ @Override public <R,T> R visit(RegistryAddressMetadataVisitor<R,T> v, T t) { return v.visitMunicipalityCode(t);} }
	;
	public abstract <R,T> R visit(RegistryAddressMetadataVisitor<R,T> visitor, T t);

	public static interface RegistryAddressMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitDomain( T t );
		 R visitRegistry( T t );
		 R visitMain( T t );
		 R visitRecipient( T t );
		 R visitStreetType( T t );
		 R visitAddress( T t );
		 R visitNumber( T t );
		 R visitAddress2( T t );
		 R visitAddress3( T t );
		 R visitGeozone( T t );
		 R visitParentGeozone( T t );
		 R visitZip( T t );
		 R visitCity( T t );
		 R visitAlias( T t );
		 R visitMunicipalityCode( T t );
	}
}
