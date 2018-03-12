package com.esferalia.aon.gwt.api.client.warehouse;

import com.esferalia.aon.gwt.api.client.product.JsItem;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsIncomeDetail extends JavaScriptObject {

	protected JsIncomeDetail() {
	}

	public static final ProvidesKey<JsIncomeDetail> PROVIDES_KEY = new ProvidesKey<JsIncomeDetail>() {
		@Override
		public Object getKey(JsIncomeDetail js) {
			return js == null ? null : js.getId();
		}
	};

	public final native Integer getId() /*-{
										return this.id;
										}-*/;

	public final native Integer getDomain() /*-{
											return this.domain;
											}-*/;
	
	public final native JsIncome getIncome() /*-{
												return this.income;
												}-*/;

	public final native Integer getLine() /*-{
											return this.line;
											}-*/;
	
	public final native JsItem getItem() /*-{
											return this.item;
											}-*/;
	
	public final native String getDescription() /*-{
												return this.description;
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
