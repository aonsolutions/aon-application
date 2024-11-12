package com.esferalia.aon.gwt.fiscal.client.mod369;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod369.Model369.Model369Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod369VatType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

//public class Model369DetailTable extends SimpleLayoutPanel implements HasSelectionHandlers<Mod369Detail> {
//
//	private Mod369 model;
//	private ScrollPanel tablePanel;
//	private Integer selectionIndex;
//	
//	public Model369DetailTable(Model369Callback cbk, Mod369 model, Integer selectedIndex) {
//		this.model = model;
//		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
//		tableDockLayout.setStyleName(AON.CSS.aonBorderRight());
//		tableDockLayout.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
//		tablePanel = new ScrollPanel();
//		tablePanel.addStyleName(AON.CSS.aonScrollArea());
//		tableDockLayout.add(tablePanel);
//		setWidget(tableDockLayout);
//		
//		if (model.getDetails3() == null || model.getDetails3().isEmpty()) {
//			refresh();
//			newDetail( );	
//		} else if (selectedIndex != null) {
//			selectionIndex = selectedIndex;
//			refresh();
//		} else {
//			selectionIndex = 0;
//			refresh();
//		}
//	}
//	
//	private Mod369Detail getSelected() {
//		return model.getDetails3().get(selectionIndex);
//	}
//
//	private Widget getToolbarPanel() {
//		AonToolbar toolbar = new AonToolbar("");
//		
//		AonToolbarButton newDetailButton = new AonToolbarButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
//		AonToolbarButton deleteDetailButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
//		AonToolbarButton restoreDeletedButton = new AonToolbarButton(AON.MSG.restoreAction(),AON.CSS.aonIconRestore());
//
//		addSelectionHandler(event -> {
//			deleteDetailButton.setVisible(!event.getSelectedItem().isDeleted());
//			restoreDeletedButton.setVisible(event.getSelectedItem().isDeleted());
//		});
//
//		newDetailButton.addClickHandler(event -> newDetail( ));
//		toolbar.add(newDetailButton);
//		
//		restoreDeletedButton.addClickHandler(event -> {
//			getSelected().setDeleted(false);
//			if (!getSelected().isDirty()) {
//				restoreDeletedButton.setVisible(false);
//				deleteDetailButton.setVisible(true);
//				refresh();
//			}
//		});
//		toolbar.add(restoreDeletedButton);
//		
//		deleteDetailButton.addClickHandler(event -> {
//			getSelected().setDeleted(true);
//			restoreDeletedButton.setVisible(true);
//			deleteDetailButton.setVisible(false);
//			refresh();
//		});
//		toolbar.add(deleteDetailButton);
//		
//		return toolbar;
//	}
//	
//	public void styleTable(AonDisplayGrid tab) {
//		for ( int i = 0 ; i < tab.getWidgetCount(); i++) {
//			if (i == selectionIndex) {
//				tab.getWidget(i).addStyleName(AON.CSS.aonBackgroundLigthBlue());
//			} else {
//				tab.getWidget(i).removeStyleName(AON.CSS.aonBackgroundLigthBlue());
//			}
//		}
//	}
//
//	public void refresh() {
//		AonDisplayGrid tab = new AonDisplayGrid();
//		tab.addStyleName(AON.CSS.aonNoPadding());
//		tab.addStyleName(AON.CSS.aonBlockCenter());
//		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
//		tablePanel.clear();
//		tablePanel.setWidget(tab);
//		int i = 0;
//		for (Mod369Detail detail : this.model.getDetails3()) {
//			AonDisplayGridRow row = tab.addRow();
//			row.addStyleName(AON.CSS.aonClickable()); 
//			if (i == Model369DetailTable.this.selectionIndex) {
//				row.addStyleName(AON.CSS.aonBackgroundLigthBlue());
//			}
//			final int idx = i;
//			row.addClickHandler( event ->  {
//				Model369DetailTable.this.selectionIndex = idx;
//				styleTable( tab );				
//				SelectionEvent.fire(Model369DetailTable.this, detail);	
//			});
//			//String keySubkey = AonStringUtils.defaultString(income.getKey()) + " -" + AonStringUtils.defaultString(income.getSubKey());
//			String keySubkey = AonStringUtils.defaultString(Country.safeIso2(detail.getCountry()));
//			InlineLabel keyLabel = new InlineLabel( );
//			if (detail.isDirty()) {
//				keyLabel.setText("* " + keySubkey);
//			} else {
//				keyLabel.setText(keySubkey);
//			}
//			if (detail.isDeleted()) {
//				keyLabel.addStyleName(AON.CSS.aonTextLineThrough());
//			} else {
//				keyLabel.removeStyleName(AON.CSS.aonTextLineThrough());
//			}
//			row.addCell( keyLabel ,AON.CSS.aonWidthAuto());
//			i++;
//		}
//	}
//	
//	private void newDetail() {
//		Mod369Detail detail = new Mod369Detail()
//				.setDirty(true)
////				.setKey("A")
////				.setSubKey("01")
//				.setVatType(Mod369VatType.STANDARD)
//				.setTempId((model.getDetails3().size() * (-1)));
//		model.getDetails3().add(detail);
//		selectionIndex = model.getDetails3().size() - 1;
//		refresh();
//		tablePanel.scrollToBottom();
//		SelectionEvent.fire(Model369DetailTable.this, detail);
//	}
//
//	public Integer getSelectionIndex() {
//		return selectionIndex;
//	}
//
//	@Override
//	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod369Detail> handler) {
//		HandlerRegistration hr = super.addHandler(handler, SelectionEvent.getType());
//		if (selectionIndex != null) {
//			SelectionEvent.fire(Model369DetailTable.this, getSelected());
//		}
//		return hr;
//	}
//	
//}
