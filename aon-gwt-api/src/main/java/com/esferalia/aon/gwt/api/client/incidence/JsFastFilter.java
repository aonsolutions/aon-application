package com.esferalia.aon.gwt.api.client.incidence;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.google.gwt.core.client.JavaScriptObject;

public class JsFastFilter extends JavaScriptObject {

	protected JsFastFilter() {}
	
	public final native Boolean getMine() /*-{
		return this.mine;
	}-*/;
	
	public final native Boolean getAssignee() /*-{
		return this.assignee;
	}-*/;

	public final native Boolean getWithoutGroup() /*-{
		return this.without_group;
	}-*/;
	
	public final native Boolean getWithoutOperator() /*-{
		return this.without_operator;
	}-*/;
	
	public final native String getType() /*-{
		return this.type;
	}-*/;
	
	public final native String getPriority() /*-{
		return this.priority;
	}-*/;
	
	public final native AonJsArray<JsLabel> getTypes() /*-{
		return this.types;
	}-*/;
	
	public final native AonJsArray<JsLabel> getPriorities() /*-{
		return this.priorities;
	}-*/;

}
