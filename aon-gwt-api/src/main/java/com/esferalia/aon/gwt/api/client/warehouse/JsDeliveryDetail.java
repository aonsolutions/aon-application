package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsDeliveryDetail extends JavaScriptObject {

	protected JsDeliveryDetail() {
	}

	public static final ProvidesKey<JsDeliveryDetail> PROVIDES_KEY = new ProvidesKey<JsDeliveryDetail>() {
		@Override
		public Object getKey(JsDeliveryDetail elaboration) {
			return elaboration == null ? null : elaboration.getId();
		}
	};

	public final native Integer getId() /*-{
										return this.id;
										}-*/;

	public final native Integer getDomain() /*-{
											return this.domain;
											}-*/;
	
	public final native JsDelivery getDelivery() /*-{
												return this.delivery;
												}-*/;

	public final native JsItem getItem() /*-{
											return this.item;
											}-*/;

	public final native Double getQuantity() /*-{
												return this.quantity;
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
