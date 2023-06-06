package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum ApplicationParameterMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(ApplicationParameterMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,DOMAIN 
		{ @Override public <R,T> R visit(ApplicationParameterMetadataVisitor<R,T> v, T t) { return v.visitDomain(t);} }
	,NAME
		{ @Override public <R,T> R visit(ApplicationParameterMetadataVisitor<R,T> v, T t) { return v.visitName(t);} }
	,VALUE
		{ @Override public <R,T> R visit(ApplicationParameterMetadataVisitor<R,T> v, T t) { return v.visitValue(t);} }
	;
	public abstract <R,T> R visit(ApplicationParameterMetadataVisitor<R,T> visitor, T t);

	public static interface ApplicationParameterMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitDomain( T t );
		 R visitName( T t );
		 R visitValue( T t );
	}
}
