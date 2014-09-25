package com.esferalia.aon.gwt.connect.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class JsImportEvent extends JavaScriptObject {
	
	
	public static enum EventType {
		ENTERPRISE_IGNORED{
			@Override
			<T> T visit(EventTypeVisitor<T> visitor, JavaScriptObject jso) {
				return visitor.onEnterpriseIgnored(jso.<JsEmpres>cast());
			}
		},
		ENTERPRISE_UPDATED{
			@Override
			<T> T visit(EventTypeVisitor<T> visitor, JavaScriptObject jso) {
				return visitor.onEnterpriseUpdated(jso.<JsEmpres>cast());
			}
		},
		ENTERPRISE_INSERTED{
			@Override
			<T> T visit(EventTypeVisitor<T> visitor, JavaScriptObject jso) {
				return visitor.onEnterpriseInserted(jso.<JsEmpres>cast());
			}
		};
		
		abstract <T> T visit(EventTypeVisitor<T> visitor, JavaScriptObject jso);
		
	}
	
	public static interface EventTypeVisitor<T> {
		T onEnterpriseIgnored(JsEmpres empres);
		T onEnterpriseUpdated(JsEmpres empres);
		T onEnterpriseInserted(JsEmpres empres);
	}

	
	
	protected JsImportEvent() {
	}
    
	
	public final native String getType() /*-{
		return this.type;
	}-*/;
	
	public final native JavaScriptObject getSrc() /*-{
		return this.src;
	}-*/;


	public final EventType getEventType() {
		return EventType.valueOf(getType());
	}

	public final <T> T visit(EventTypeVisitor<T> visitor) {
		return getEventType().visit(visitor, getSrc());
	}

	
}
