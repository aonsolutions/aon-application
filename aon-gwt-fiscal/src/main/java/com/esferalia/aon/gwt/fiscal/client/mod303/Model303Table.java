package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelProvidesKey;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelTable;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model303Table extends SimpleLayoutPanel implements HasSelectionHandlers<Mod303>{
	
	private FiscalModelTable<Mod303> table;
	
	public Model303Table(Model303Callback cbk) {
		table = new FiscalModelTable<Mod303>(new FiscalModelProvidesKey<Mod303>());
		if (table.getSelectionModel() != null) {
			table.getSelectionModel().addSelectionChangeHandler(new com.google.gwt.view.client.SelectionChangeEvent.Handler() {
				
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					SelectionEvent.fire(Model303Table.this, table.getSelected());
				}
			});
		}
		table.addRangeChangeHandler( new com.google.gwt.view.client.RangeChangeEvent.Handler() {
			
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				Model303.mod303Service.getMod303s(Model303.getCurrentDomainName(), Model303.getCurrentDomain(),
						new AsyncCallback<LinkedList<Mod303>>() {
							@Override
							public void onSuccess(LinkedList<Mod303> result) {
								table.setRowData(result);
							}

							@Override
							public void onFailure(Throwable caught) {
								cbk.showError( AON.MSG.unableToReadDeclaration(caught.getMessage()) );
							}
						});
			}
		});
		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		tableDockLayout.addNorth(getToolbarPanel(cbk), 25);
		ScrollPanel tablePanel = new ScrollPanel();
		tablePanel.addStyleName(AON.AON_CSS.aonScrollArea());
		tablePanel.add(table);
		tableDockLayout.add(tablePanel);
		setWidget(tableDockLayout);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod303> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void refresh() {
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}
	
	
	private Widget getToolbarPanel(Model303Callback cbk) {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label( "IVA. Autoliquidaci\u00F3n."));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		final Button newButton = new Button();
		newButton.setText(AON.MSG.newAction());
		newButton.setTitle(newButton.getText());
		newButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		newButton.addStyleName(AON.AON_CSS.aonIconReset());
		newButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cbk.onNew();
			}
		});
		buttonContainer.add(newButton);

		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
}
