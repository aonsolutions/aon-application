package com.esferalia.aon.gwt.api.client.communication;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsCommunicationInfo extends JavaScriptObject {

	public static final ProvidesKey<JsCommunicationInfo> PROVIDES_KEY = new ProvidesKey<JsCommunicationInfo>() {
		@Override
		public Object getKey(JsCommunicationInfo js) {
			return js == null ? null : js.getId();
		}
	};

	protected JsCommunicationInfo() {
	}

	public final native String getId() /*-{
										return this.id;
										}-*/;

	public final native String getCode() /*-{
											return this.code;
											}-*/;

	public final native String getReferenceCode() /*-{
													return this.reference_code;
													}-*/;

	public final native String getAddress() /*-{
											return this.address;
											}-*/;

	public final native String getDate() /*-{
											return this.date;
											}-*/;

	public final native String getRegistryName() /*-{
													return this.registry_name;
													}-*/;

	public final native String getStatus() /*-{	
											return this.status;
											}-*/;

	public final native String getDescription() /*-{	
												return this.description;
												}-*/;

}
