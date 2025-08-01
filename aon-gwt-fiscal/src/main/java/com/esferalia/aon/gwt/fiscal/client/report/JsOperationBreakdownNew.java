package com.esferalia.aon.gwt.fiscal.client.report;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;

public class JsOperationBreakdownNew extends JavaScriptObject {
	
	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");

	protected JsOperationBreakdownNew() {
	}
	
	// FALTA - PONER AL FINAL TODAS LAS COLUMNAS QUE QUERAMOS MOSTRAR EN PANTALLA Y LAS QUE SE NECESITEN QUE NO SE MUESTREN (POR EJEMPLO entryId)
	
	// FALTA - DETERMINAR COMO SE VAN A PASAR LAS FECHAS, SI FORMATEADAS COMO TEXTO O COMO LONG DENTRO DE UN STRING
	private Date ensureDate(String dateString) {
		return dateString == null ? null : AonDateUtils.parseDate(dateString); // FORMATEADAS COMO TEXTO dd/MM/yyyy   yyyy-MM-dd
//		return dateString == null ? null : AonDateUtils.fromLong(dateString);   // COMO LONG EN UN STRING
	}
	
	public final native int getEntryId() /*-{
		return this.entryId;
	}-*/;
	public final Date getEntryDate() {
		return ensureDate(getEntryDateString());
	}

	private final native String getEntryDateString() /*-{
		return this.entryDate;
	}-*/;
	
	public final Date getTaxDate() {
		return ensureDate(getTaxDateString());
	}
	private final native String getTaxDateString() /*-{
		return this.taxDate;
	}-*/;
	
	public final native String getConceptCode() /*-{
		return this.conceptCode;
	}-*/;
	public final native int getInvoiceNumber() /*-{
		return this.invoiceNumber;
	}-*/;
	public final native String getDocument() /*-{
		return this.document;
	}-*/;
	public final native String getName() /*-{
		return this.name;
	}-*/;
	public final native double getBase() /*-{
		return this.base;
	}-*/;
	public final native double getPercent() /*-{
		return this.percent;
	}-*/;
	public final native double getQuota() /*-{
	 	return this.quota;
	}-*/;	
	public final native double getSurchargePercent() /*-{
		return this.surchargePercent;
	}-*/;
	public final native double getSurchargeQuota() /*-{
		return this.surchargeQuota;
	}-*/;
	public final native double getTotal() /*-{
		return this.total;
	}-*/;
//	public final String getRegistryFullName() {
//		return  AonStringUtils.abbreviate(
//			AonStringUtils.defaultIfBlank(getRegistryDocument(), AonStringUtils.EMPTY)
//			+ (AonStringUtils.isBlank(getRegistryDocument())?AonStringUtils.EMPTY:AonStringUtils.HYPHEN)
//			+ AonStringUtils.defaultIfBlank(getRegistryName(), AonStringUtils.EMPTY),34 );
//	}
	
}
