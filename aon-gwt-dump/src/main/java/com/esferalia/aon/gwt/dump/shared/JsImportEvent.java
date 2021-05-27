package com.esferalia.aon.gwt.dump.shared;

import com.google.gwt.core.client.JavaScriptObject;

public class JsImportEvent extends JavaScriptObject {
	
	
	public static enum EventType {
		COMMITTED{
			@Override
			<T> T visit(EventTypeVisitor<T> visitor, JavaScriptObject jso) {
				return visitor.onCommitted();
			}
		},
		ROLLBACKED{
			@Override
			<T> T visit(EventTypeVisitor<T> visitor, JavaScriptObject jso) {
				return visitor.onRollbacked();
			}
		},
		EMPLOYEE_IGNORED{
			@Override
			<T> T visit(EventTypeVisitor<T> visitor, JavaScriptObject jso) {
				return visitor.onEmployeeIgnored(jso.<JsEmployee>cast());
			}
		},
		EMPLOYEE_UPDATED{
			@Override
			<T> T visit(EventTypeVisitor<T> visitor, JavaScriptObject jso) {
				return visitor.onEmployeeUpdated(jso.<JsEmployee>cast());
			}
		},
		EMPLOYEE_INSERTED{
			@Override
			<T> T visit(EventTypeVisitor<T> visitor, JavaScriptObject jso) {
				return visitor.onEmployeeInserted(jso.<JsEmployee>cast());
			}
		},
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
		T onCommitted();
		T onRollbacked();
		T onEmployeeIgnored(JsEmployee employee);
		T onEmployeeUpdated(JsEmployee employee);
		T onEmployeeInserted(JsEmployee employee);
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
