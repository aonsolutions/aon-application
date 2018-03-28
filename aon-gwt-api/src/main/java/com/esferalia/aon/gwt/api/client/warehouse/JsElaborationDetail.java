package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsElaborationDetail extends JavaScriptObject {

	protected JsElaborationDetail() {
	}

	public static final ProvidesKey<JsElaborationDetail> PROVIDES_KEY = new ProvidesKey<JsElaborationDetail>() {
		@Override
		public Object getKey(JsElaborationDetail elaboration) {
			return elaboration == null ? null : elaboration.getId();
		}
	};

	public final native Integer getId() /*-{
										return this.id;
										}-*/;

	public final native Integer getDomain() /*-{
											return this.domain;
											}-*/;

	public final native JsElaboration getElaboration() /*-{
														return this.elaboration;
														}-*/;

	public final native String getDate() /*-{
											return this.date;
											}-*/;

	public final native JsItem getItem() /*-{
											return this.item;
											}-*/;

	public final native Double getQuantity() /*-{
												return this.quantity;
												}-*/;

	public final native JsWarehouse getWarehouse() /*-{
													return this.warehouse;
													}-*/;

	public final native String getAddInfo() /*-{
												return this.add_info;
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
