package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.HTML;

class JSF extends HTML implements Handler {
	
	public static interface OnRerenderHandler {
		void onRerender();
	}

	public JSF() {
		super(getJsfElement());
		addAttachHandler(this);
	}

	@Override
	public void onAttachOrDetach(AttachEvent event) {
		if (event.isAttached()) {
			display();
		} else {
			hide();
		}
	}

	private void hide() {
		getElement().getStyle().setDisplay(Display.NONE);
	}

	private void display() {
		getElement().getStyle().clearDisplay();
	}

	public final native void enterpriseSelected(Integer id) /*-{
		$wnd.enterpriseSelected(id);
	}-*/;

	public final native void workplaceSelected(Integer id) /*-{
		$wnd.workplaceSelected(id);
	}-*/;

	public final native void activitySelected(Integer id) /*-{
		$wnd.activitySelected(id);
	}-*/;

	public final native void employeeSelected(Integer id) /*-{
		$wnd.employeeSelected(id);
	}-*/;

	public final native void bonusConceptsSelected() /*-{
		$wnd.bonusConceptsSelected();
	}-*/;

	public final native void deductionConceptsSelected() /*-{
		$wnd.deductionConceptsSelected();
	}-*/;

	public final native void paymentConceptsSelected() /*-{
		$wnd.paymentConceptsSelected();
	}-*/;

	public final native void setRerenderHandler(OnRerenderHandler handler) /*-{
		$wnd.rerenderHandler = function( ){
			handler.@com.esferalia.aon.gwt.payroll.client.JSF.OnRerenderHandler::onRerender()();
		};
	}-*/;

	private static final native Element getJsfElement() /*-{
		return $wnd.jsf;
	}-*/;

}