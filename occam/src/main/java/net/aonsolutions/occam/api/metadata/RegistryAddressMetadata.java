package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum RegistryAddressMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitId();} }
	,DOMAIN 
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitDomain();} }
	,REGISTRY
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitRegistry();} }
	,MAIN
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitMain();} }
	,RECIPIENT
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitRecipient();} }
	,STREET_TYPE
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitStreetType();} }
	,ADDRESS
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitAddress();} }
	,NUMBER
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitNumber();} }
	,ADDRESS2
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitAddress2();} }
	,ADDRESS3
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitAddress3();} }
	,GEOZONE
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitGeozone();} }
	,PARENT_GEOZONE
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitParentGeozone();} }
	,ZIP
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitZip();} }
	,CITY
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitCity();} }
	,ALIAS
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitAlias();} }
	,MUNICIPALITY_CODE
		{ @Override public <R> R visit(RegistryAddressMetadataVisitor<R> v) { return v.visitMunicipalityCode();} }
	;
	public abstract <R> R visit(RegistryAddressMetadataVisitor<R> visitor);

	public static interface RegistryAddressMetadataVisitor<R> {
		 R visitId();
		 R visitDomain();
		 R visitRegistry();
		 R visitMain();
		 R visitRecipient();
		 R visitStreetType();
		 R visitAddress();
		 R visitNumber();
		 R visitAddress2();
		 R visitAddress3();
		 R visitGeozone();
		 R visitParentGeozone();
		 R visitZip();
		 R visitCity();
		 R visitAlias();
		 R visitMunicipalityCode();
	}
}
