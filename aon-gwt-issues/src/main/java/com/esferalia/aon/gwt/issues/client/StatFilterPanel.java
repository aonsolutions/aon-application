package com.esferalia.aon.gwt.issues.client;

import java.util.Date;

import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.esferalia.aon.occam.api.model.stat.task.TaskChartType;
import com.google.gwt.core.client.GWT;
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
    @UiField InlineLabel statusLabel;
    Incidence incidence;
    StatPanel stat;
    public StatFilterPanel(StatPanel stat, Incidence incidence) {
    	this.incidence = incidence;
    	this.stat = stat;
    	initWidget(binder.createAndBindUi(this));       
    	
    	// -------------------- DATE - FROM _____ TO ______
    	HorizontalPanel datePanel = new HorizontalPanel(); 
    	datePanel.addStyleName(AON.AON_CSS.aonMarginTop());  
    	InlineLabel fromLabel = new InlineLabel( AON.MSG.from());
		fromLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		fromLabel.setWidth("20px");
		datePanel.add(fromLabel);
		
		final DateBoxEx from = new DateBoxEx();
		from.setValue(stat.getParams().getFrom());
		from.setWidth("70px");
		from.addValueChangeHandler(event -> {
			stat.getParams().setFrom(from.getValue());
			stat.getIssueFilter().setFrom(from.getFormat().format(from, from.getValue()));
			stat.selectStat(stat.getSelectedChart());
		});
		datePanel.add(from);
		
		InlineLabel toLabel = new InlineLabel( AON.MSG.until());
		toLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		toLabel.setWidth("20px");
		datePanel.add(toLabel);
		
		final DateBoxEx to = new DateBoxEx();
		to.setValue(stat.getParams().getTo());
		to.setWidth("70px");
		to.addValueChangeHandler(event -> {
		    stat.getParams().setTo(to.getValue());
			stat.getIssueFilter().setTo(to.getFormat().format(to, to.getValue()));
			stat.selectStat(stat.getSelectedChart());
		});
		datePanel.add(to);
		panel.add(datePanel);
		
		// ------------------ FILTER BUTTONS

		FlowPanel dateFilterPanel = new FlowPanel(); 
		dateFilterPanel.addStyleName(AON.AON_CSS.aonFloatRight());
		dateFilterPanel.addStyleName(AON.AON_CSS.aonMarginTop());
        
        InlineLabel dateFilterLabel = new InlineLabel("Fecha");
        dateFilterLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
        dateFilterPanel.add(dateFilterLabel);
        
        final ListBox dateFilter = new ListBox();
        dateFilter.setStyleName(AON.AON_CSS.aonMarginRight());
        
        dateFilter.addItem("Hoy");
        dateFilter.addItem("Ayer");
        dateFilter.addItem("Hace 1 semana");
        dateFilter.addItem("Hace 1 mes");
        dateFilter.addItem("Hace 1 a\u00f1o");
        
        dateFilter.setSelectedIndex(4);
        dateFilter.addChangeHandler(event -> {
            if(dateFilter.getSelectedIndex() == 0) {
                changeFromValue(new Date());
            } else if(dateFilter.getSelectedIndex() == 1) {
                Date d = new Date();
                d.setDate(d.getDate()-1);
                changeFromValue(d);
            } else if(dateFilter.getSelectedIndex() == 2) {
                Date d = new Date();
                d.setDate(d.getDate()-7);
                changeFromValue(d);
            } else if(dateFilter.getSelectedIndex() == 3) {
                Date d = new Date();
                d.setMonth(d.getMonth()-1);
                changeFromValue(d);
            } else if(dateFilter.getSelectedIndex() == 4) {
                Date d = new Date();
                d.setYear(d.getYear()-1);
                changeFromValue(d);
            }   
            stat.selectStat(stat.getSelectedChart());
        });
        
        dateFilterPanel.add(dateFilter);
        
        panel.add(dateFilterPanel);
				
		// --------- STATUS
		
		FlowPanel statusFilterPanel = new FlowPanel(); 
		statusFilterPanel.addStyleName(AON.AON_CSS.aonFloatRight());
		statusFilterPanel.addStyleName(AON.AON_CSS.aonMarginTop());
        
        InlineLabel statusFilterLabel = new InlineLabel("Estado");
        statusFilterLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
        statusFilterPanel.add(statusFilterLabel);
        
        final ListBox statusFilter = new ListBox();
        statusFilter.setStyleName(AON.AON_CSS.aonMarginRight());
        
        statusFilter.addItem("Todas");
        statusFilter.addItem("Abiertas");
        statusFilter.addItem("Cerradas");
        statusFilter.addItem("Borradas");
        
        statusFilter.setSelectedIndex(0);
        statusFilter.addChangeHandler( event -> {
            if(statusFilter.getSelectedIndex() == 0) {
                statusLabel.setText("");
                stat.getIssueFilter().setState("all");
            } else if(statusFilter.getSelectedIndex() == 1) {
                statusLabel.setText("Estado: ABIERTAS;");
                stat.getIssueFilter().setState("open");
            } else if(statusFilter.getSelectedIndex() == 2) {
                statusLabel.setText("Estado: CERRADAS;");
                stat.getIssueFilter().setState("closed");
            } else if(statusFilter.getSelectedIndex() == 3) {
                statusLabel.setText("Estado: BORRADAS;");
                stat.getIssueFilter().setState("deleted");
            }
            stat.selectStat(stat.getSelectedChart());
        });
        
        statusFilterPanel.add(statusFilter);
        
        panel.add(statusFilterPanel);

		// ------------------ TIPO DE GRAFICO
    	FlowPanel chartTypePanel = new FlowPanel(); 
		chartTypePanel.addStyleName(AON.AON_CSS.aonFloatRight());
		chartTypePanel.addStyleName(AON.AON_CSS.aonMarginTop());
		
		InlineLabel chartTypeLabel = new InlineLabel( AON.MSG.graphicType());
		chartTypeLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		chartTypePanel.add(chartTypeLabel);
		
		final ListBox chartType = new ListBox();
		chartType.setStyleName(AON.AON_CSS.aonMarginRight());
		chartType.addStyleName(AON.AON_CSS.aonWidth300());
		for (TaskChartType type : TaskChartType.values()) {
			chartType.addItem(type.getDescription());
		}
		
		chartType.setSelectedIndex(0);
		chartType.addChangeHandler(event -> {
		    Integer init = TaskChartType.TASK_BY_STATUS.ordinal();
            stat.selectStat(TaskChartType.values()[chartType.getSelectedIndex() + init]);
		});
		
		chartTypePanel.add(chartType);
		
		panel.add(chartTypePanel);
    }

    private void changeFromValue(Date date){
    	HorizontalPanel hp = (HorizontalPanel) panel.getWidget(0);
    	DateBoxEx db = (DateBoxEx) hp.getWidget(1);
    	db.setValue(date);
    	stat.getParams().setFrom(db.getValue());
		stat.getIssueFilter().setFrom(db.getFormat().format(db, db.getValue()));
    }
}
