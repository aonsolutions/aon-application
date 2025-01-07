package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.Model347Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Asset;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model347AssetTable extends SimpleLayoutPanel implements HasSelectionHandlers<Mod347Asset> {

	private Mod347 model;
	private ScrollPanel tablePanel;
	private Integer selectionIndex;
	private AonTextBox filterBox;
	
	public Model347AssetTable(Model347Callback cbk, Mod347 model, Integer selectedIndex) {
		this.model = model;
		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		tableDockLayout.setStyleName(AON.CSS.aonBorderRight());
		tableDockLayout.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		tablePanel = new ScrollPanel();
		tablePanel.addStyleName(AON.CSS.aonScrollArea());
		tableDockLayout.add(tablePanel);
		setWidget(tableDockLayout);
		
		if (model.getAssets() == null || model.getAssets().isEmpty()) {
			refresh();
		} else if (selectedIndex != null) {
			if (selectedIndex >= model.getAssets().size())
				selectionIndex = model.getAssets().size() - 1;
			else
				selectionIndex = selectedIndex;
			refresh();
		} else {
			selectionIndex = 0;
			refresh();
		}
	}
	
	private Mod347Asset getSelected() {
		return model.getAssets().get(selectionIndex);
	}

	private Widget getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar("");
		
		AonToolbarButton newDetailButton = new AonToolbarButton(AON.MSG.newPerceptor(),AON.CSS.aonIconAdd());
		AonToolbarButton deleteDetailButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		AonToolbarButton restoreDeletedButton = new AonToolbarButton(AON.MSG.restoreAction(),AON.CSS.aonIconRestore());

		addSelectionHandler(event -> {
			deleteDetailButton.setVisible(event.getSelectedItem() != null && !event.getSelectedItem().isDeleted());
			restoreDeletedButton.setVisible(event.getSelectedItem() != null && event.getSelectedItem().isDeleted());
		});

		newDetailButton.addClickHandler(event -> newAsset( ));
		toolbar.add(newDetailButton);
		
		restoreDeletedButton.setVisible(false);
		restoreDeletedButton.addClickHandler(event -> {
			getSelected().setDeleted(false);
			if (!getSelected().isDirty()) {
				restoreDeletedButton.setVisible(false);
				deleteDetailButton.setVisible(true);
				refresh();
			}
		});
		toolbar.add(restoreDeletedButton);
		
		deleteDetailButton.setVisible(false);
		deleteDetailButton.addClickHandler(event -> {
			getSelected().setDeleted(true);
			restoreDeletedButton.setVisible(true);
			deleteDetailButton.setVisible(false);
			refresh();
		});
		toolbar.add(deleteDetailButton);
		
		FlowPanel searchPanel = new FlowPanel();
		searchPanel.setStyleName(AON.CSS.aonFlexBlockInline());
		searchPanel.addStyleName(AON.CSS.aonMarginLeft());
		
		InlineLabel searchIcon = new InlineLabel("Filtro:");
		searchIcon.setStyleName(AON.CSS.aonInnerLabel());
		searchPanel.add(searchIcon);
		filterBox = new AonTextBox( );
		filterBox.setVisibleLength(10);
		filterBox.addValueChangeHandler(event -> refresh());
		searchPanel.add(filterBox);
		
		AonToolbarButton cleanFilterButton = new AonToolbarButton(AON.MSG.clean(),AON.CSS.aonIconClear());
		cleanFilterButton.addClickHandler(event -> {
			filterBox.setValue("");
			refresh();	
		});
		searchPanel.add(cleanFilterButton);
		toolbar.add(searchPanel);
		
		return toolbar;
	}
	
	public void styleTable(AonDisplayGrid tab) {
		for ( int i = 0 ; i < tab.getWidgetCount(); i++) {
			if (i == selectionIndex) {
				tab.getWidget(i).addStyleName(AON.CSS.aonBackgroundLigthBlue());
			} else {
				tab.getWidget(i).removeStyleName(AON.CSS.aonBackgroundLigthBlue());
			}
		}
	}

	public void refresh() {
		AonDisplayGrid tab = new AonDisplayGrid();
		tab.addStyleName(AON.CSS.aonNoPadding());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tablePanel.clear();
		tablePanel.setWidget(tab);
		int i = 0;
		boolean resetSelection = false;
		int firstMatch = -1;
		if (this.model.getAssets().isEmpty()) {
			tab.addRow().addCell( new Label( AON.MSG.noData()),AON.CSS.aonWidthAuto());
		} else {
			for (Mod347Asset asset : this.model.getAssets()) {
				AonDisplayGridRow row = tab.addRow();
				boolean visible = AonStringUtils.isBlank(filterBox.getValue()) ||
						AonStringUtils.isBlank(asset.getName()) ||
						AonStringUtils.containsIgnoreCase(asset.getName(), filterBox.getValue());
				row.addStyleName(AON.CSS.aonClickable()); 
				row.setVisible(visible);
				if (firstMatch == -1 && visible) {
					firstMatch = i;
				}
				if (i == Model347AssetTable.this.selectionIndex) {
					row.addStyleName(AON.CSS.aonBackgroundLigthBlue());
					resetSelection = !visible; 
				}
				final int idx = i;
				row.addClickHandler( event ->  {
					Model347AssetTable.this.selectionIndex = idx;
					styleTable( tab );				
					SelectionEvent.fire(Model347AssetTable.this, asset);	
				});
				String name = AonStringUtils.abbreviate(AonStringUtils.defaultIfBlank(asset.getName(), "Inmueble") , 30 );
				InlineLabel nameLabel = new InlineLabel( name );
				if (asset.isDirty()) {
					nameLabel.setText("* " + name);
				} else {
					nameLabel.setText(name);
				}
				if (asset.isDeleted()) {
					nameLabel.addStyleName(AON.CSS.aonTextLineThrough());
				} else {
					nameLabel.removeStyleName(AON.CSS.aonTextLineThrough());
				}
				
				row.addCell( nameLabel ,AON.CSS.aonWidthAuto());
				i++;
			}
			if (resetSelection) {
				selectionIndex = firstMatch;
				SelectionEvent.fire(Model347AssetTable.this, getSelected() );
			}
		}
	}
	
	private void newAsset() {
		Mod347Asset detail = new Mod347Asset()
				.setDirty(true)
				.setTempId((model.getAssets().size() * (-1)));
		model.getAssets().add(detail);
		selectionIndex = model.getAssets().size() - 1;
		refresh();
		tablePanel.scrollToBottom();
		SelectionEvent.fire(Model347AssetTable.this, detail);
	}

	public Integer getSelectionIndex() {
		return selectionIndex;
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod347Asset> handler) {
		HandlerRegistration hr = super.addHandler(handler, SelectionEvent.getType());
		if (selectionIndex != null) {
			SelectionEvent.fire(Model347AssetTable.this, getSelected());
		}
		return hr;
	}
	
}
