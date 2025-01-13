package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum ProductCategoryMetadata implements Serializable {
	 ID 		{ @Override public <T> T visit(ProductCategoryMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN 	{ @Override public <T> T visit(ProductCategoryMetadataVisitor<T> v) { return v.visitDomain();} }
	,NAME 		{ @Override public <T> T visit(ProductCategoryMetadataVisitor<T> v) { return v.visitName();} }
	,DETAIL 	{ @Override public <T> T visit(ProductCategoryMetadataVisitor<T> v) { return v.visitDetail();} }
	,DETAIL2 	{ @Override public <T> T visit(ProductCategoryMetadataVisitor<T> v) { return v.visitDetail2();} }
	,DETAIL3 	{ @Override public <T> T visit(ProductCategoryMetadataVisitor<T> v) { return v.visitDetail3();} }
	;

	public abstract <T> T visit(ProductCategoryMetadataVisitor<T> v);
	public static interface ProductCategoryMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitName();
		T visitDetail();
		T visitDetail2();
		T visitDetail3();
	}
}
