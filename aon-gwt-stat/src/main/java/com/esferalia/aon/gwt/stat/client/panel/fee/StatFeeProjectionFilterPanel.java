package com.esferalia.aon.gwt.stat.client.panel.fee;

import java.util.Date;
import java.util.HashMap;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import net.aonsolutions.polymer.aon.widget.AonComboBox;

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
				stat.getParams().getFilterMap().put("from", new String[]{Long.toString(from.getValue().getTime())});
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
						ButtonClick(categoryButton, result, AON.MSG.category());
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
						ButtonClick(customerButton, result, AON.MSG.customer());
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
						ButtonClick(sellerButton, result, AON.MSG.seller());
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
						ButtonClick(workplaceButton, result, AON.MSG.workplace());
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
						ButtonClick(periodButton, result, AON.MSG.period());
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
		HashMap<String, String[]> map = new HashMap<String, String[]>();
		map.put("from", new String[]{Long.toString(new Date().getTime())});
    	stat.getParams().setFilterMap(map);
    	stat.content();
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
				String id = js.getId() + "";
				
				if(label.equalsIgnoreCase(AON.MSG.category())){
					categoryLabel.setText(AON.MSG.category() + ":" +js.getName() + "; ");
					stat.getParams().getFilterMap().put("category",new String[]{id});
				}
				if(label.equalsIgnoreCase(AON.MSG.seller())){
					sellerLabel.setText(AON.MSG.seller() + ":" +js.getName() + "; ");
					stat.getParams().getFilterMap().put("seller",new String[]{id});
				}
				if(label.equalsIgnoreCase(AON.MSG.customer())){
					customerLabel.setText(AON.MSG.customer() + ":" +js.getName() + "; ");
					stat.getParams().getFilterMap().put("customer",new String[]{id});
				}
				if(label.equalsIgnoreCase(AON.MSG.period())){
					periodLabel.setText(AON.MSG.period() + ":" +js.getName() + "; ");
					stat.getParams().getFilterMap().put("period",new String[]{id});
				}
				if(label.equalsIgnoreCase(AON.MSG.workplace())){
					workplaceLabel.setText(AON.MSG.workplace() + ":" +js.getName() + "; ");
					stat.getParams().getFilterMap().put("workplace",new String[]{id});
				}	
				stat.content();
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
}
