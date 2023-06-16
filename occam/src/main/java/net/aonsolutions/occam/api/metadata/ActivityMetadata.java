package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum ActivityMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(ActivityMetadataVisitor<R> v) { return v.visitId();} }
	,DOMAIN 
		{ @Override public <R> R visit(ActivityMetadataVisitor<R> v) { return v.visitDomain();} }
	,DESCRIPTION
		{ @Override public <R> R visit(ActivityMetadataVisitor<R> v) { return v.visitDescription();} }
	,EPIGRAPH
		{ @Override public <R> R visit(ActivityMetadataVisitor<R> v) { return v.visitEpigraph();} }
	;
	public abstract <R> R visit(ActivityMetadataVisitor<R> visitor);

	public static interface ActivityMetadataVisitor<R> {
		 R visitId();
		 R visitDomain();
		 R visitDescription();
		 R visitEpigraph();
	}
}
