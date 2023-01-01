package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Activity;
import com.esferalia.aon.occam.api.model.type.Activities.Type1Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type2Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type3Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type4Activities;
import com.esferalia.aon.occam.api.model.type.Activities.Type7Activities;
import com.esferalia.aon.occam.api.model.type.Activities.TypeActivity;
import com.esferalia.aon.occam.api.model.type.ActivityGroup;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class AonActivityPanel extends AonCustomDialog implements HasSelectionHandlers<Activity> {

	private DeckLayoutPanel deckPanel; 
	private ListBox activityGroup;
	private SimpleLayoutPanel tablePanel;
	private SimpleLayoutPanel descriptionPanel;
	
	public AonActivityPanel() {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(AON.MSG.activitySelection());
		showCloseButton(true);
		
		DockLayoutPanel dockPanel = new DockLayoutPanel(Unit.PX);
		dockPanel.setWidth("550px");
		dockPanel.setHeight("500px");

		deckPanel = new DeckLayoutPanel();
		
		activityGroup = new ListBox();
		activityGroup.setWidth("380px");
		activityGroup.addItem("-------------", new String());
		for (ActivityGroup ag : ActivityGroup.values()) {
			activityGroup.addItem(AON.MSG.activityGroup(ag));
		}
		activityGroup.addChangeHandler(event -> {
			int i = activityGroup.getSelectedIndex() - 1;
			if (i >= 0 && (ActivityGroup.values()[i] == ActivityGroup.GROUP5 
					    || ActivityGroup.values()[i] == ActivityGroup.GROUP6)) {
				deckPanel.showWidget(descriptionPanel);
			} else {
				search();
			}
		});
		
		AonDisplayTable headerTable = new AonDisplayTable();
		headerTable.addStyleName(AON.CSS.aonWidthAlmostAll());
		headerTable.addStyleName(AON.CSS.aonBlockCenter());
		headerTable.addLabelWidgetRow(AON.MSG.activity(),activityGroup);
		dockPanel.addNorth(headerTable, 30);

		tablePanel = new SimpleLayoutPanel();
		descriptionPanel = new SimpleLayoutPanel();
		FlowPanel descPanel = new FlowPanel();
		
		AonDisplayTable descTable = new AonDisplayTable();
		descTable.addStyleName(AON.CSS.aonWidthAlmostAll());
		descTable.addStyleName(AON.CSS.aonBlockCenter());
		descTable.addStyleName(AON.CSS.aonMarginTop());
		AonTextBox description = new AonTextBox();
		description.setVisibleLength(30);
		descTable.addLabelWidgetRow(AON.MSG.activityDescription(),description);
		descPanel.add(descTable);
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.setStyleName(AON.CSS.aonTextCenter());
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(event -> {
			okButton.setEnabled(false);
			hide();
			ActivityGroup ag = ActivityGroup.values()[activityGroup .getSelectedIndex() - 1];
			SelectionEvent.fire(AonActivityPanel.this, 
				new Activity()
		      		.setDescription(description.getValue())
		      		.setEpigraph(null)
		      		.setKey(ag.getKey()));
		});
    	buttonPanel.add(okButton);
		descPanel.add(buttonPanel);
		descriptionPanel.add(descPanel);
		
		
		deckPanel.add(tablePanel);
		deckPanel.add(descriptionPanel);
		
		dockPanel.add(deckPanel);
		
		activityGroup.setSelectedIndex(1);
		search();
		
		add(dockPanel);
		dockPanel.forceLayout();
	}

	private void search() {
		int i = activityGroup.getSelectedIndex() - 1;
		AonDisplayGrid tab = new AonDisplayGrid();
		tab.addStyleName(AON.CSS.aonWidthAll());
		tab.addHeaderRow()
			.addCell( new Label(AON.MSG.code()))
			.addCell( new Label(AON.MSG.description()));
		if (i >= 0) {
			ActivityGroup ag = ActivityGroup.values()[i];
			for (Activity act : getActivities(ag.ordinal())) {
				AonDisplayGridRow row = tab.addRow();
				row.addStyleName(AON.CSS.aonClickable());
				row.addCell( new Label(act.getEpigraph()), AON.CSS.aonBold())
				   .addCell( new Label(act.getDescription()), AON.CSS.aonFlexGrow1(), AON.CSS.aonWrap());
				row.addClickHandler(event -> {
					hide();
					SelectionEvent.fire(AonActivityPanel.this, act.setKey(ag.getKey()) );
				});	
			}
		} else {
			tab.addRow().addCell(new Label(AON.MSG.noData()));
		}
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		scroll.add(tab);
		tablePanel.setWidget(scroll);
		deckPanel.showWidget(tablePanel);
	}
	
	public LinkedList<Activity> getActivities(int activityGroup) throws AonCoreException {
		LinkedList<Activity> list = new LinkedList<>();
		TypeActivity[] types = null;
		if (activityGroup == 0) {
			types = Type1Activities.values();
		} if (activityGroup == 1) {
			types = Type2Activities.values();
		} if (activityGroup == 2) {
			types = Type3Activities.values();
		} if (activityGroup == 3) {
			types = Type4Activities.values();
		} if (activityGroup == 6) {
			types = Type7Activities.values();
		}
		if (types == null) {
			throw new AonCoreException("Grupo de actividad no soportado " + activityGroup );
		}
		Activity a;
		for (TypeActivity type : types) {
			a = new Activity();
			a.setEpigraph(type.getEpigraph());
			a.setDescription(type.getLiteral());
			list.add(a);
		}
		return list;
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Activity> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
