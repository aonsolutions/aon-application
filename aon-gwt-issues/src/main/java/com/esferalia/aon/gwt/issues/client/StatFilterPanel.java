package com.esferalia.aon.gwt.issues.client;

import java.util.Date;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.esferalia.aon.occam.api.model.stat.StatChartType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperButton;

import net.aonsolutions.polymer.aon.widget.AonComboBox;

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
		from.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				stat.getParams().setFrom(from.getValue());
				stat.getIssueFilter().setFrom(from.getFormat().format(from, from.getValue()));
				stat.selectStat(stat.getSelectedChart());
			}
		});
		datePanel.add(from);
		
		InlineLabel toLabel = new InlineLabel( AON.MSG.until());
		toLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		toLabel.setWidth("20px");
		datePanel.add(toLabel);
		
		final DateBoxEx to = new DateBoxEx();
		to.setValue(stat.getParams().getTo());
		to.setWidth("70px");
		to.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				stat.getParams().setTo(to.getValue());
				stat.getIssueFilter().setTo(to.getFormat().format(to, to.getValue()));
				stat.selectStat(stat.getSelectedChart());
			}
		});
		datePanel.add(to);
		panel.add(datePanel);
		
		// ------------------ FILTER BUTTONS
		FlowPanel fpanel = new FlowPanel(); 
		
		// DATE
		PaperButton datepb = new PaperButton();
		datepb.setStyleName(ICSS.aonPaperButtonFilterIssues());
		
		InlineLabel dateLabel = new InlineLabel("FECHA");
		dateLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		datepb.add(dateLabel);
		
		IronIcon dateii = new IronIcon();
		dateii.setStyleName(ICSS.aonIronIconFilterIssues()); 
		dateii.setIcon("arrow-drop-down");
		datepb.add(dateii);
		datepb.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				incidence.getDateOptions(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						ButtonClick(datepb, result, "Fecha");
					}

					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(datepb);
		
		panel.add(fpanel);
		
		// --------- STATUS
		PaperButton pb = new PaperButton();
		pb.setStyleName(ICSS.aonPaperButtonFilterIssues());
		
		InlineLabel statusLabel = new InlineLabel("Estado");
		statusLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		pb.add(statusLabel);
		
		IronIcon ii = new IronIcon();
		ii.setStyleName(ICSS.aonIronIconFilterIssues()); 
		ii.setIcon("arrow-drop-down");
		pb.add(ii);
		pb.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				incidence.getStatuses(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						ButtonClick(pb, result, "Estado");
					}

					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(pb);
		
		panel.add(fpanel);

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
    
    private void ButtonClick(PaperButton pb, JSON<JsObject> result, String label){
		PopupPanel popup = new PopupPanel();
		AonComboBox acb = new AonComboBox();
		acb.setItems(result.getData());
		acb.setItemLabelPath("name");
		acb.setItemValuePath("name");
		acb.setLabel(label);
		acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
				JsObject js = acb.getSelectedItem().cast();
				if(label.equalsIgnoreCase("estado")){
					statusClick(js);
				} else if(label.equalsIgnoreCase("fecha")){
					dateClick(js);
				}
				stat.selectStat(stat.getSelectedChart());
				
				popup.hide();
			}
		});
		popup.add(acb);
		int left = pb.getAbsoluteLeft();
		int top = pb.getAbsoluteTop()
				+ pb.getOffsetHeight();
		Integer width = Window.getClientWidth();
		if(left > width - 200){
			left = left - 200;
		}
		popup.setAutoHideEnabled(true);
		popup.addAutoHidePartner(acb.getElementById("overlay"));
		popup.setPopupPosition(left, top);
		popup.show();
		acb.open();
    }
    
    private void statusClick(JsObject js){
    	if(js.getName().equalsIgnoreCase("TODAS")){
    		statusLabel.setText("");
    		stat.getIssueFilter().setState("all");
    	}
		if(js.getName().equalsIgnoreCase("ABIERTAS")){
			statusLabel.setText("Estado: ABIERTAS;");
			stat.getIssueFilter().setState("open");
		}
		if(js.getName().equalsIgnoreCase("CERRADAS")){
			statusLabel.setText("Estado: CERRADAS;");
			stat.getIssueFilter().setState("closed");
		}
		if(js.getName().equalsIgnoreCase("BORRADAS")){
			statusLabel.setText("Estado: BORRADAS;");
			stat.getIssueFilter().setState("deleted");
		}
    }
    
    private void dateClick(JsObject js){
    	
    	if(js.getName().equalsIgnoreCase("HOY")) changeFromValue(new Date());
		if(js.getName().equalsIgnoreCase("AYER")){
			Date d = new Date();
			d.setDate(d.getDate()-1);
			changeFromValue(d);
		}
		if(js.getName().equalsIgnoreCase("HACE 1 SEMANA")){
			Date d = new Date();
			d.setDate(d.getDate()-7);
			changeFromValue(d);
		}
		if(js.getName().equalsIgnoreCase("HACE 1 MES")) {
			Date d = new Date();
			d.setMonth(d.getMonth()-1);
			changeFromValue(d);
		}
		if(js.getName().equalsIgnoreCase("Hace 1 a\u00f1o")){
			Date d = new Date();
			d.setYear(d.getYear()-1);
			changeFromValue(d);
		}
    }
    
    private void changeFromValue(Date date){
    	HorizontalPanel hp = (HorizontalPanel) panel.getWidget(0);
    	DateBoxEx db = (DateBoxEx) hp.getWidget(1);
    	db.setValue(date);
    	stat.getParams().setFrom(db.getValue());
		stat.getIssueFilter().setFrom(db.getFormat().format(db, db.getValue()));
    }
    
    private void changeToValue(Date date){
    	HorizontalPanel hp = (HorizontalPanel) panel.getWidget(0);
    	DateBoxEx db = (DateBoxEx) hp.getWidget(3);
    	db.setValue(date);
    	stat.getParams().setFrom(db.getValue());
		stat.getIssueFilter().setFrom(db.getFormat().format(db, db.getValue()));
    }
}
