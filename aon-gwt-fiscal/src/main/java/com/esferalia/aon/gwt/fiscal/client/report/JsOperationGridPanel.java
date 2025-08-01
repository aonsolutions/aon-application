package com.esferalia.aon.gwt.fiscal.client.report;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;

public abstract class JsOperationGridPanel extends FlowPanel implements HasSelectionHandlers<JsOperationBreakdownNew> {
	
	abstract void addRow(JsOperationBreakdownNew jsOperationBreakdownNew);
	abstract void addFooterRow();

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<JsOperationBreakdownNew> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
}
