package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum GeozoneMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(GeozoneMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,DOMAIN 
		{ @Override public <R,T> R visit(GeozoneMetadataVisitor<R,T> v, T t) { return v.visitDomain(t);} }
	,NAME
		{ @Override public <R,T> R visit(GeozoneMetadataVisitor<R,T> v, T t) { return v.visitName(t);} }
	,CODE
		{ @Override public <R,T> R visit(GeozoneMetadataVisitor<R,T> v, T t) { return v.visitCode(t);} }
	,SYSTEM
		{ @Override public <R,T> R visit(GeozoneMetadataVisitor<R,T> v, T t) { return v.visitSystem(t);} }
	;
	public abstract <R,T> R visit(GeozoneMetadataVisitor<R,T> visitor, T t);

	public static interface GeozoneMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitDomain( T t );
		 R visitName( T t );
		 R visitCode( T t );
		 R visitSystem( T t );
	}
}
