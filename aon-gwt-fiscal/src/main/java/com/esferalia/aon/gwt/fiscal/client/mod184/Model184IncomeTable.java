package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.Model184Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
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

public class Model184IncomeTable extends SimpleLayoutPanel implements HasSelectionHandlers<Mod184Income> {

	private Mod184 model;
	private ScrollPanel tablePanel;
	private Integer selectionIndex;
	
	public Model184IncomeTable(Model184Callback cbk, Mod184 model, Integer selectedIndex) {
		this.model = model;
		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		tableDockLayout.setStyleName(AON.CSS.aonBorderRight());
		tableDockLayout.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		tablePanel = new ScrollPanel();
		tablePanel.addStyleName(AON.CSS.aonScrollArea());
		tableDockLayout.add(tablePanel);
		setWidget(tableDockLayout);
		
		if (model.getIncomes() == null || model.getIncomes().isEmpty()) {
			refresh();
			newPerceptor( );	
		} else if (selectedIndex != null) {
			if (selectedIndex >= model.getIncomes().size())
				selectionIndex = model.getIncomes().size() - 1;
			else
				selectionIndex = selectedIndex;
			refresh();
		} else {
			selectionIndex = 0;
			refresh();
		}
	}
	
	private Mod184Income getSelected() {
		return model.getIncomes().get(selectionIndex);
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
		for (Mod184Income income : this.model.getIncomes()) {
			AonDisplayGridRow row = tab.addRow();
			row.addStyleName(AON.CSS.aonClickable()); 
			if (i == Model184IncomeTable.this.selectionIndex) {
				row.addStyleName(AON.CSS.aonBackgroundLigthBlue());
			}
			final int idx = i;
			row.addClickHandler( event ->  {
				Model184IncomeTable.this.selectionIndex = idx;
				styleTable( tab );				
				SelectionEvent.fire(Model184IncomeTable.this, income);	
			});
			String keySubkey = AonStringUtils.defaultString(income.getKey()) + " -" + AonStringUtils.defaultString(income.getSubKey());
			InlineLabel keyLabel = new InlineLabel( );
			if (income.isDirty()) {
				keyLabel.setText("* " + keySubkey);
			} else {
				keyLabel.setText(keySubkey);
			}
			if (income.isDeleted()) {
				keyLabel.addStyleName(AON.CSS.aonTextLineThrough());
			} else {
				keyLabel.removeStyleName(AON.CSS.aonTextLineThrough());
			}
			row.addCell( keyLabel ,AON.CSS.aonWidthAuto());
			i++;
		}
	}
	
	private void newPerceptor() {
		Mod184Income detail = new Mod184Income()
				.setDirty(true)
				.setKey("A")
				.setSubKey("01")
				.setTempId((model.getIncomes().size() * (-1)));
		model.getIncomes().add(detail);
		selectionIndex = model.getIncomes().size() - 1;
		refresh();
		tablePanel.scrollToBottom();
		SelectionEvent.fire(Model184IncomeTable.this, detail);
	}

	public Integer getSelectionIndex() {
		return selectionIndex;
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod184Income> handler) {
		HandlerRegistration hr = super.addHandler(handler, SelectionEvent.getType());
		if (selectionIndex != null) {
			SelectionEvent.fire(Model184IncomeTable.this, getSelected());
		}
		return hr;
	}
	
}
