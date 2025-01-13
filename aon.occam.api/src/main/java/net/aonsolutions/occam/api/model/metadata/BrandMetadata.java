package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum BrandMetadata implements Serializable {
	 ID 	{ @Override public <T> T visit(BrandMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(BrandMetadataVisitor<T> v) { return v.visitDomain();} }
	,NAME 	{ @Override public <T> T visit(BrandMetadataVisitor<T> v) { return v.visitName();} }
	,	;

	public abstract <T> T visit(BrandMetadataVisitor<T> v);
	public static interface BrandMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitName();
	}
}
