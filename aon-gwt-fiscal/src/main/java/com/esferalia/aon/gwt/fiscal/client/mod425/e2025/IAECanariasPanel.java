package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Activity425;
import com.esferalia.aon.occam.api.model.type.CanariasIAE;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class IAECanariasPanel extends AonCustomDialog implements HasSelectionHandlers<Activity425> {

	private DeckLayoutPanel deckPanel; 
	private ActivityTypeListBox activityTypeBox;
	private SimpleLayoutPanel tablePanel;
	private SimpleLayoutPanel descriptionPanel;
	
	public IAECanariasPanel() {
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
		activityTypeBox = new ActivityTypeListBox();
		activityTypeBox.setWidth("450px");
		activityTypeBox.addChangeHandler(event -> search());
		
		AonDisplayTable headerTable = new AonDisplayTable();
		headerTable.addStyleName(AON.CSS.aonWidthAlmostAll());
		headerTable.addStyleName(AON.CSS.aonBlockCenter());
		headerTable.addRow()
			.addCell(new Label(AON.MSG.activity()))
			.addCell(activityTypeBox);
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
		
		// FALTA - NO SE MUY BIEN QUE PINTA AQUI ESTE BOTON
//		FlowPanel buttonPanel = new FlowPanel();
//		buttonPanel.setStyleName(AON.CSS.aonTextCenter());
//    	final Button okButton = new Button();
//    	okButton.setStyleName(AON.CSS.aonOkButton());
//    	okButton.setText( AON.MSG.accept());
//    	okButton.addClickHandler(event -> {
//			okButton.setEnabled(false);
//			hide();
//			String activityType = activityTypeBox.getSelectedValue();
//			SelectionEvent.fire(IAECanariasPanel.this, 
//				new Activity425()
//		      		.setDescription(description.getValue())
//		      		.setEpigraph(null)
//		      		.setKey(activityType==null?null:activityType.toString()));
//		});
//    	buttonPanel.add(okButton);
//		descPanel.add(buttonPanel);
		
		descriptionPanel.add(descPanel);
		
		deckPanel.add(tablePanel);
		deckPanel.add(descriptionPanel);
		
		dockPanel.add(deckPanel);
		add(dockPanel);
		dockPanel.forceLayout();
	}

	private void search() {
		String activityType = activityTypeBox.getSelectedValue();
		AonDisplayGrid tab = new AonDisplayGrid();
		tab.addStyleName(AON.CSS.aonWidthAll());
		tab.addHeaderRow()
			.addCell( new Label(AON.MSG.code()))
			.addCell( new Label(AON.MSG.description()));
		if (activityType == null) {
			tab.addRow().addCell(new Label(AON.MSG.noData()));
		} else {
			Arrays.stream(CanariasIAE.values())
				.filter(act -> activityType.equals(act.getActivityType()))
				.forEach(act -> {
					AonDisplayGridRow row = tab.addRow();
					row.addStyleName(AON.CSS.aonClickable());
					row.addCell(new Label(act.getEpigraph()), AON.CSS.aonBold())
					   .addCell(new Label(act.getDescription()), AON.CSS.aonFlexGrow1(), AON.CSS.aonWrap());
					row.addClickHandler(event -> {
						hide();
						SelectionEvent.fire(IAECanariasPanel.this,
							new Activity425()
					      		.setDescription(act.getDescription())
					      		.setEpigraph(AonStringUtils.replace(act.getEpigraph(),".",""))
					      		.setKey(activityType));
						});
				});	
		}
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		scroll.add(tab);
		tablePanel.setWidget(scroll);
		deckPanel.showWidget(tablePanel);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Activity425> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
