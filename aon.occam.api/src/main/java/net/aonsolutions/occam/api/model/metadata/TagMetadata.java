package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum TagMetadata implements Serializable {
	 ID 	{ @Override public <T> T visit(TagMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(TagMetadataVisitor<T> v) { return v.visitDomain();} }
	,NAME 	{ @Override public <T> T visit(TagMetadataVisitor<T> v) { return v.visitName();} }
	,TYPE 	{ @Override public <T> T visit(TagMetadataVisitor<T> v) { return v.visitType();} }
	,COLOR 	{ @Override public <T> T visit(TagMetadataVisitor<T> v) { return v.visitColor();} }
	;

	public abstract <T> T visit(TagMetadataVisitor<T> v);
	public static interface TagMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitName();
		T visitType();
		T visitColor();
	}
}
