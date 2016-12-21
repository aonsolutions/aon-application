package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.esferalia.aon.occam.api.model.stat.StatChartType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

public class StatFilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, StatFilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    @UiField HorizontalPanel panel;
    Incidence incidence;
    
    public StatFilterPanel(StatPanel stat, Incidence incidence) {
    	this.incidence = incidence;
    	initWidget(binder.createAndBindUi(this));       
    	
    	FlowPanel chartTypePanel = new FlowPanel(); 
		chartTypePanel.addStyleName(AON.AON_CSS.aonFloatRight());
		chartTypePanel.addStyleName(AON.AON_CSS.aonMarginTop());
		
		InlineLabel chartTypeLabel = new InlineLabel( AON.MSG.graphicType());
		chartTypeLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		chartTypePanel.add(chartTypeLabel);
		
		final ListBox chartType = new ListBox();
		chartType.setStyleName(AON.AON_CSS.aonMarginRight());
		chartType.addStyleName(AON.AON_CSS.aonWidth300());
		for (StatChartType type : StatChartType.values()) {
			if(type.getType().equals(StatChartType.TASK))
				chartType.addItem(type.getDescription());
		}
		
		chartType.setSelectedIndex(0);
		chartType.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Integer init = StatChartType.TASK_BY_STATUS.ordinal();
				stat.selectStat(StatChartType.values()[chartType.getSelectedIndex() + init]);
			}
		});
		
		chartTypePanel.add(chartType);
		
		panel.add(chartTypePanel);
    }


}
