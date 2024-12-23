package com.esferalia.aon.gwt.fiscal.client.mod193;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193.Model193Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model193DetailTable extends SimpleLayoutPanel implements HasSelectionHandlers<Mod193Detail> {

	private Mod193 model;
	private ScrollPanel tablePanel;
	private Integer selectionIndex;
	private AonTextBox filterBox;
	
	public Model193DetailTable(Model193Callback cbk, Mod193 model, Integer selectedIndex) {
		this.model = model;
		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		tableDockLayout.setStyleName(AON.CSS.aonBorderRight());
		tableDockLayout.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		tablePanel = new ScrollPanel();
		tablePanel.addStyleName(AON.CSS.aonScrollArea());
		tableDockLayout.add(tablePanel);
		setWidget(tableDockLayout);
		
		if (model.getDetails() == null || model.getDetails().isEmpty()) {
			refresh();
			newPerceptor( );	
		} else if (selectedIndex != null) {
			if (selectedIndex >= model.getDetails().size())
				selectionIndex = model.getDetails().size() - 1;
			else
				selectionIndex = selectedIndex;
			refresh();
		} else {
			selectionIndex = 0;
			refresh();
		}
	}
	
	private Mod193Detail getSelected() {
		return model.getDetails().get(selectionIndex);
	}

	private Widget getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar("");
		
		AonToolbarButton newDetailButton = new AonToolbarButton(AON.MSG.newPerceptor(),AON.CSS.aonIconAdd());
		AonToolbarButton deleteDetailButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		AonToolbarButton restoreDeletedButton = new AonToolbarButton(AON.MSG.restoreAction(),AON.CSS.aonIconRestore());

		addSelectionHandler(event -> {
			deleteDetailButton.setVisible(!event.getSelectedItem().isDeleted());
			restoreDeletedButton.setVisible(event.getSelectedItem().isDeleted());
		});

		newDetailButton.addClickHandler(event -> newPerceptor( ));
		toolbar.add(newDetailButton);
		
		restoreDeletedButton.addClickHandler(event -> {
			getSelected().setDeleted(false);
			if (!getSelected().isDirty()) {
				restoreDeletedButton.setVisible(false);
				deleteDetailButton.setVisible(true);
				refresh();
			}
		});
		toolbar.add(restoreDeletedButton);
		
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
		for (Mod193Detail detail : this.model.getDetails()) {
			AonDisplayGridRow row = tab.addRow();
			boolean visible = AonStringUtils.isBlank(filterBox.getValue()) ||
					AonStringUtils.isBlank(detail.getName()) ||
					AonStringUtils.containsIgnoreCase(detail.getName(), filterBox.getValue());
			row.addStyleName(AON.CSS.aonClickable()); 
			row.setVisible(visible);
			if (firstMatch == -1 && visible) {
				firstMatch = i;
			}
			if (i == Model193DetailTable.this.selectionIndex) {
				row.addStyleName(AON.CSS.aonBackgroundLigthBlue());
				resetSelection = !visible; 
			}
			final int idx = i;
			row.addClickHandler( event ->  {
				Model193DetailTable.this.selectionIndex = idx;
				styleTable( tab );				
				SelectionEvent.fire(Model193DetailTable.this, detail);	
			});
			String name = AonStringUtils.abbreviate(AonStringUtils.defaultIfBlank(detail.getName(), AON.MSG.newPerceptor()) , 30 );
			InlineLabel nameLabel = new InlineLabel( name );
			if (detail.isDirty()) {
				nameLabel.setText("* " + name);
			} else {
				nameLabel.setText(name);
			}
			if (detail.isDeleted()) {
				nameLabel.addStyleName(AON.CSS.aonTextLineThrough());
			} else {
				nameLabel.removeStyleName(AON.CSS.aonTextLineThrough());
			}
			row.addCell( new InlineLabel( detail.getKey() ) , AON.CSS.aonTextCenter(),AON.CSS.aonWidth20() )
				.addCell( nameLabel ,AON.CSS.aonWidthAuto());
			i++;
		}
		if (resetSelection) {
			selectionIndex = firstMatch;
			SelectionEvent.fire(Model193DetailTable.this, getSelected() );
		}
	}
	
	private void newPerceptor() {
		Mod193Detail detail = new Mod193Detail()
				.setDirty(true)
				.setType(Mod193Detail.DETAIL_TYPE)
				.setKey("A")
				.setNature("01")
				.setTempId((model.getDetails().size() * (-1)));
		model.getDetails().add(detail);
		selectionIndex = model.getDetails().size() - 1;
		refresh();
		tablePanel.scrollToBottom();
		SelectionEvent.fire(Model193DetailTable.this, detail);
	}

	public Integer getSelectionIndex() {
		return selectionIndex;
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod193Detail> handler) {
		HandlerRegistration hr = super.addHandler(handler, SelectionEvent.getType());
		if (selectionIndex != null) {
			SelectionEvent.fire(Model193DetailTable.this, getSelected());
		}
		return hr;
	}
	
}
