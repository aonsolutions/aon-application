package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsWarehouseTransferDetail extends JavaScriptObject {

	protected JsWarehouseTransferDetail() {
	}

	public static final ProvidesKey<JsWarehouseTransferDetail> PROVIDES_KEY = new ProvidesKey<JsWarehouseTransferDetail>() {
		@Override
		public Object getKey(JsWarehouseTransferDetail js) {
			return js == null ? null : js.getId();
		}
	};

	public final native Integer getId() /*-{
										return this.id;
										}-*/;

	public final native Integer getDomain() /*-{
											return this.domain;
											}-*/;

	public final native JsWarehouseTransfer getWarehouseTransfer() /*-{
																	return this.warehouse_transfer;
																	}-*/;

	public final native JsItem getItem() /*-{
											return this.item;
											}-*/;

	public final native Double getQuantity() /*-{
												return this.quantity;
												}-*/;

}