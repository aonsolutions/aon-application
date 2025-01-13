package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum ProductMetadata implements Serializable {
	 ID 				{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN 			{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitDomain();} }
	,NAME 				{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitName();} }
	,CODE 				{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitCode();} }
	,KIND 				{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitKind();} }
	,BRAND 				{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitBrand();} }
	,CATEGORY 			{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitCategory();} }
	,INVENTORIABLE 		{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitInventoriable();} }
	,SERIALIZABLE 		{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitSerializable();} }
	,LOTABLE 			{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitLotable();} }
	,STATUS 			{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitStatus();} }
	,VAT 				{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitVat();} }
	,RETENTION 			{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitRetention();} }
	,TYPE 				{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitType();} }
	,MANUFACTURED 		{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitManufactured();} }
	,COMPOSITION 		{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitComposition();} }
	,COMPOSITION_PRICE 	{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitCompositionPrice();} }
	,PACKAGED			{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitPackaged();} }
	,SALES_ACCOUNT 		{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitSalesAccount();} }
	,PURCHASE_ACCOUNT	{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitPurchaseAccount();} }
	,PERISHABLE 		{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitPerishable();} }
	,DAYS_TO_EXPIRE 	{ @Override public <T> T visit(ProductMetadataVisitor<T> v) { return v.visitDaysToExpire();} }
	,	;

	public abstract <T> T visit(ProductMetadataVisitor<T> v);
	public static interface ProductMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitName();
		T visitCode();
		T visitKind();
		T visitBrand();
		T visitCategory();
		T visitInventoriable();
		T visitSerializable();
		T visitLotable();
		T visitStatus();
		T visitVat();
		T visitRetention();
		T visitType();
		T visitManufactured();
		T visitComposition();
		T visitCompositionPrice();
		T visitPackaged();
		T visitSalesAccount();
		T visitPurchaseAccount();
		T visitPerishable();
		T visitDaysToExpire();
	}
}
