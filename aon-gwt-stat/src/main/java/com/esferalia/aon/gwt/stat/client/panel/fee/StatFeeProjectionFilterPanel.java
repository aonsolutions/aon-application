package com.esferalia.aon.gwt.stat.client.panel.fee;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.common.client.polymer.AonFilterDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;

public class StatFeeProjectionFilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, StatFeeProjectionFilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    @UiField HorizontalPanel panel;
    @UiField InlineLabel categoryLabel;
    @UiField InlineLabel customerLabel;
    @UiField InlineLabel sellerLabel;
    @UiField InlineLabel workplaceLabel;
    @UiField InlineLabel periodLabel;
    @UiField PaperIconButton cleanFilter;
    
    StatFeeProjectionPanel stat;
    API API;
    
    public StatFeeProjectionFilterPanel(StatFeeProjectionPanel stat) {
    	this.stat = stat;
    	API = stat.API;
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
				LinkedList<String> fromList = new LinkedList<>();
				fromList.add(Long.toString(from.getValue().getTime()));
				stat.getFilterMap().put("from", fromList);
				stat.content();
			}
		});
		datePanel.add(from);
		
		panel.add(datePanel);
    
		
		// ------------------ FILTER BUTTONS
		FlowPanel fpanel = new FlowPanel(); 
		
		// ------------------ CATEGORY
		PaperButton categoryButton = filterButton(AON.MSG.category());
		categoryButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				API.getProduct().getProductCategories(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						buttonClick(categoryButton, result, "category", AON.MSG.category());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(categoryButton);
		
		// ------------------ CUSTOMER
		PaperButton customerButton = filterButton(AON.MSG.customer());
		customerButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				API.getRegistry().getCustomers(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						buttonClick(customerButton, result, "customer", AON.MSG.customer());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(customerButton);
		
		// ------------------ SELLER
		PaperButton sellerButton = filterButton(AON.MSG.seller());
		sellerButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				API.getRegistry().getSellers(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						buttonClick(sellerButton, result, "seller", AON.MSG.seller());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(sellerButton);
		
		// ------------------ WORKPLACE
		PaperButton workplaceButton = filterButton(AON.MSG.workplace());
		workplaceButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				API.getCommon().getWorkplaces(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						buttonClick(workplaceButton, result, "workplace", AON.MSG.workplace());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(workplaceButton);
		
		// ------------------ PERIODO
		PaperButton periodButton = filterButton(AON.MSG.period());
		periodButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				API.getFinance().getBillingPeriods(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						buttonClick(periodButton, result, "period", AON.MSG.period());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(periodButton);
		panel.add(fpanel);
    }
    
    private PaperButton filterButton(String title) {
		PaperButton button = new PaperButton();
		button.setNoink(true);
		button.setStyleName(ICSS.aonPaperButtonFilterIssues());
				
		InlineLabel label = new InlineLabel(title);
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		button.add(label);
				
		IronIcon icon = new IronIcon();
		icon.setStyleName(ICSS.aonIronIconFilterIssues()); 
		icon.setIcon("arrow-drop-down");
		button.add(icon);
		return button;
	}
    
    @UiHandler("cleanFilter")
	void cleanFilter(ClickEvent event){		
    	categoryLabel.setText("");
    	customerLabel.setText("");
    	sellerLabel.setText("");
    	workplaceLabel.setText("");
    	periodLabel.setText("");
    	
    	stat.getParams().setFrom(new Date());
		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> fromList = new LinkedList<>();
		fromList.add(Long.toString(new Date().getTime()));
		map.put("from", fromList);
    	stat.setFilterMap(map);
    	stat.content();
	}
    
    private void buttonClick(PaperButton pb, JSON<JsObject> result, String key, String label){
    	LinkedList<String> filterList = stat.getFilterMap().containsKey(key) ? 
    			stat.getFilterMap().get(key) : new LinkedList<>();
    	AonFilterDialog sw = new AonFilterDialog(pb, label, "",
    			filterList, result.getData().cast()){

			@Override
			protected void onSelect(JavaScriptObject o, Boolean apply) {
				JsObject js = o.cast();
				if(apply){
					if(stat.getFilterMap().containsKey(key)){
						stat.getFilterMap().get(key).add(js.getId()+"");
					} else {
						LinkedList<String> list = new LinkedList<>();
						list.add(js.getId()+"");
						stat.getFilterMap().put(key, list);
					}
				} else {
					if(stat.getFilterMap().containsKey(key)){
						stat.getFilterMap().get(key).remove(js.getId()+"");
					}
				}
				stat.content();
			}
    	};
    	sw.show();
    }
}
