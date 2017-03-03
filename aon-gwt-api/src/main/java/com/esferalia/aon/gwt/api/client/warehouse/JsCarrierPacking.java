package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsCarrierPacking extends JavaScriptObject {

	protected JsCarrierPacking() {}	public static final ProvidesKey<JsCarrierPacking> PROVIDES_KEY = new ProvidesKey<JsCarrierPacking>() {
		@Override
		public Object getKey(JsCarrierPacking carrierPacking) {
			return carrierPacking == null ? null : carrierPacking.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;

	public final native String getSeriesNumber() /*-{
		return this.series_number;
	}-*/;
	
	public final native String getSeries() /*-{
		return this.series;
	}-*/;
	
	public final native Integer getNumber() /*-{
		return this.number;
	}-*/;

	public final native JsObject getType() /*-{
		return this.type;
	}-*/;

	public final native JsObject getStatus() /*-{
		return this.status;
	}-*/;

	public final native String getIssueDate() /*-{
		return this.issue_date;
	}-*/;
	
	public final native JsObject getCarrier() /*-{
		return this.carrier;
	}-*/;
	
	public final native String getDeliveryDate() /*-{
		return this.delivery_date;
	}-*/;
	
	public final native String getCarrierReference() /*-{
		return this.carrier_reference;
	}-*/;
	
	public final native String getNumberPlate() /*-{
		return this.number_plate;
	}-*/;
	
	public final native String getDriverName() /*-{	
		return this.driver_name;
	}-*/;	
	
	public final native String getDriverDocument() /*-{
		return this.driver_document;
	}-*/;
	
	public final native String getComments() /*-{
		return this.comments;
	}-*/;
	
	public final native String getObservation() /*-{
		return this.observation;
	}-*/;
	
	public final native String getParams() /*-{
		return this.params;
	}-*/;
	
	public final native String getCreationUser() /*-{
		return this.creation_user;
	}-*/;
	
	public final native String getCreationDate() /*-{
		return this.creation_date;
	}-*/;

	public final native String getModificationUser() /*-{
		return this.modification_user;
	}-*/;

	public final native String getModificationDate() /*-{
		return this.modification_date;
	}-*/;

}
