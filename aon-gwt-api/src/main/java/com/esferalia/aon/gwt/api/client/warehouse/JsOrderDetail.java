package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsOrderDetail extends JavaScriptObject {

	protected JsOrderDetail() {}	public static final ProvidesKey<JsOrderDetail> PROVIDES_KEY = new ProvidesKey<JsOrderDetail>() {
		@Override
		public Object getKey(JsOrderDetail carrierPacking) {
			return carrierPacking == null ? null : carrierPacking.getId();
		}
	};
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native Integer getPurchase() /*-{
		return this.purchase;
	}-*/;

	public final native Integer getProject() /*-{
		return this.project;
	}-*/;
	
	public final native Integer getLine() /*-{
		return this.line;
	}-*/;
	
	public final native Integer getItem() /*-{
		return this.item;
	}-*/;
	
	public final native String getDescription() /*-{
		return this.description;
	}-*/;
	
	public final native Double getQuantity() /*-{
		return this.quantity;
	}-*/;
	
	public final native Double getPrice() /*-{
		return this.price;
	}-*/;
	
	public final native String getDiscountExpr() /*-{
		return this.discount_expr;
	}-*/;
	
	public final native Double getTaxes() /*-{
		return this.taxes;
	}-*/;
	
	public final native JsObject getStatus() /*-{
		return this.status;
	}-*/;
	
	public final native JsObject getSource() /*-{
		return this.source;
	}-*/;
	
	public final native Integer getSourceId() /*-{
		return this.source_id;
	}-*/;
	
	public final native Integer getProposalDetail() /*-{
		return this.proposal_detail;
	}-*/;
	
	public final native Double getDelivered() /*-{
		return this.delivered;
	}-*/;
	
	public final native String getDeliveryDate() /*-{
		return this.delivery_date;
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


	public final native String getProductCode() /*-{
		return this.product_code;
	}-*/;
	
	public final native String getProductName() /*-{
		return this.product_name;
	}-*/;
	
}
