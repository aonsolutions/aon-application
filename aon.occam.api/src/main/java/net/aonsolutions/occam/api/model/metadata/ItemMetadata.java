package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum ItemMetadata implements Serializable {
	 ID 					{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN 				{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitDomain();} }
	,PRODUCT 				{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitProduct();} }
	,DETAIL 				{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitDetail();} }
	,DETAIL2 				{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitDetail2();} }
	,DETAIL3 				{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitDetail3();} }
	,DESCRIPTION 			{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitDescription();} }
	,SERIAL_NUMBER 			{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitSerialNumber();} }
	,SERIAL_DATE 			{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitSerialDate();} }
	,EXPIRE_DATE 			{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitExpireDate();} }
	,PRICE 					{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitPrice();} }
	,STATUS 				{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitStatus();} }
	,EXPENSES_PERCENT 		{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitExpensesPercent();} }
	,EXPENSES_FIXED 		{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitExpensesFixed();} }
	,PROFIT_PERCENT 		{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitProfitPercent();} }
	,PURCHASE_PRICE 		{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitPurchasePrice();} }
	,INTERNET 				{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitInternet();} }
	,BARCODE 				{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitBarcode();} }
	,PACK_FORMAT_TAG 		{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitPackFormatTag();} }
	,PACK_UNITS 			{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitPackUnits();} }
	,PACK_UNITS_TAG 		{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitPackUnitsTag();} }
	,PACK_MEASUREMENT 		{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitPackMeasurement();} }
	,PACK_MEASUREMENT_TAG	{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitPackMeasurementTag();} }
	,STOCK_UNIT_TAG 		{ @Override public <T> T visit(ItemMetadataVisitor<T> v) { return v.visitStockUnitTag();} }
	,	;

	public abstract <T> T visit(ItemMetadataVisitor<T> v);
	public static interface ItemMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitProduct();
		T visitDetail();
		T visitDetail2();
		T visitDetail3();
		T visitDescription();
		T visitSerialNumber();
		T visitSerialDate();
		T visitExpireDate();
		T visitPrice();
		T visitStatus();
		T visitExpensesPercent();
		T visitExpensesFixed();
		T visitProfitPercent();
		T visitPurchasePrice();
		T visitInternet();
		T visitBarcode();
		T visitPackFormatTag();
		T visitPackUnits();
		T visitPackUnitsTag();
		T visitPackMeasurement();
		T visitPackMeasurementTag();
		T visitStockUnitTag();
	}
}
