package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum ActivityMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(ActivityMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,DOMAIN 
		{ @Override public <R,T> R visit(ActivityMetadataVisitor<R,T> v, T t) { return v.visitDomain(t);} }
	,DESCRIPTION
		{ @Override public <R,T> R visit(ActivityMetadataVisitor<R,T> v, T t) { return v.visitDescription(t);} }
	,EPIGRAPH
		{ @Override public <R,T> R visit(ActivityMetadataVisitor<R,T> v, T t) { return v.visitEpigraph(t);} }
	;
	public abstract <R,T> R visit(ActivityMetadataVisitor<R,T> visitor, T t);

	public static interface ActivityMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitDomain( T t );
		 R visitDescription( T t );
		 R visitEpigraph( T t );
	}
}
