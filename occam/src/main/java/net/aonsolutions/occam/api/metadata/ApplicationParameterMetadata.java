package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum ApplicationParameterMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(ApplicationParameterMetadataVisitor<R> v) { return v.visitId();} }
	,DOMAIN 
		{ @Override public <R> R visit(ApplicationParameterMetadataVisitor<R> v) { return v.visitDomain();} }
	,NAME
		{ @Override public <R> R visit(ApplicationParameterMetadataVisitor<R> v) { return v.visitName();} }
	,VALUE
		{ @Override public <R> R visit(ApplicationParameterMetadataVisitor<R> v) { return v.visitValue();} }
	;
	public abstract <R> R visit(ApplicationParameterMetadataVisitor<R> visitor);

	public static interface ApplicationParameterMetadataVisitor<R> {
		 R visitId();
		 R visitDomain();
		 R visitName();
		 R visitValue();
	}
}
