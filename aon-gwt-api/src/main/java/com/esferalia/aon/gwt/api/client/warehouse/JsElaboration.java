package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsElaboration extends JavaScriptObject {

	protected JsElaboration() {
	}

	public static final ProvidesKey<JsElaboration> PROVIDES_KEY = new ProvidesKey<JsElaboration>() {
		@Override
		public Object getKey(JsElaboration elaboration) {
			return elaboration == null ? null : elaboration.getId();
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

	public final native JsObject getStatus() /*-{
												return this.status;
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

	public final native JsObject getWarehouse() /*-{
												return this.warehouse;
												}-*/;

	public final native String getComments() /*-{
												return this.comments;
												}-*/;

	public final native JsObject getSource() /*-{
											return this.source;
											}-*/;

	public final native Integer getSourceId() /*-{
												return this.source_id;
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
