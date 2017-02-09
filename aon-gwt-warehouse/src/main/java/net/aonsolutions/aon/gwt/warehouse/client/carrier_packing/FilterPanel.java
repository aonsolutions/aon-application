package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

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

public class FilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
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
    
    CarrierPacking carrierPacking;
    API API;
    
    public FilterPanel(CarrierPacking carrierPacking) {
    	this.carrierPacking = carrierPacking;
    	API = carrierPacking.API;
    	initWidget(binder.createAndBindUi(this));       

    	/*TextBox text = new TextBox(); 
    	text.setStyleName(ICSS.aonSearchBoxIssues());
    	text.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				// filtrar por referencia/conductor(name y document)/ matricula
			}
		});
    	panel.add(text);
    	*/
    	// -------------------- DATE - FROM _____ TO ______
    	HorizontalPanel datePanel = new HorizontalPanel(); 
    	datePanel.addStyleName(AON.AON_CSS.aonMarginTop());  
    	InlineLabel issueLabel = new InlineLabel( AON.MSG.issueDate());
    	issueLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
    	issueLabel.setWidth("20px");
		datePanel.add(issueLabel);
		
		final DateBoxEx issue = new DateBoxEx();
		//issue.setValue(new Date());
		issue.setWidth("70px");
		issue.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(issue.getValue().getTime()));
				carrierPacking.getFilterMap().put("issue_date", list);
				carrierPacking.content();
			}
		});
		datePanel.add(issue);
		
		InlineLabel deliveryLabel = new InlineLabel(AON.MSG.deliveryDate());
		deliveryLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		deliveryLabel.setWidth("20px");
		datePanel.add(deliveryLabel);
		
		final DateBoxEx delivery = new DateBoxEx();
		//delivery.setValue(new Date());
		delivery.setWidth("70px");
		delivery.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(delivery.getValue().getTime()));
				carrierPacking.getFilterMap().put("delivery_date", list);
				carrierPacking.content();			}
		});
		datePanel.add(delivery);
		
		panel.add(datePanel);
    
		
		// ------------------ FILTER BUTTONS
		FlowPanel fpanel = new FlowPanel(); 
		
		// ------------------ SERIES
		PaperButton seriesButton = filterButton(AON.MSG.series());
		seriesButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				API.getWarehouse().getCarrierPackingSeries(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						ButtonClick(seriesButton, result, AON.MSG.series());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(seriesButton);
		
		// ------------------ CARRIERS
		PaperButton carrierButton = filterButton(AON.MSG.carrier());
		carrierButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				API.getWarehouse().getCarrierPackingCarriers(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						ButtonClick(carrierButton, result, AON.MSG.carrier());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(carrierButton);
		
		// ------------------ TYPE
		PaperButton typeButton = filterButton(AON.MSG.type());
		typeButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				API.getWarehouse().getCarrierPackingTypes(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						ButtonClick(typeButton, result, AON.MSG.type());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(typeButton);
		
		// ------------------ STATUS
		PaperButton statusButton = filterButton(AON.MSG.status());
		statusButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				API.getWarehouse().getCarrierPackingStatuses(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						ButtonClick(statusButton, result, AON.MSG.status());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(statusButton);
		
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
    	

		HashMap<String, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> from = new LinkedList<>();
		from.add(Long.toString(new Date().getTime()));
		map.put("from", from );
    	carrierPacking.setFilterMap(map);
    	carrierPacking.content();
	}
    
    private String key;
    private void ButtonClick(PaperButton pb, JSON<JsObject> result, String label){
    	if(AON.MSG.series().equals(label)){
    		key = "series"; 
    	} else if(AON.MSG.carrier().equals(label)){
    		key = "carrier"; 
    	} else if(AON.MSG.type().equals(label)){
    		key = "type"; 
    	} else if(AON.MSG.status().equals(label)){
    		key = "status"; 
    	}
    	LinkedList<String> filterList = carrierPacking.getFilterMap().containsKey(key) ? 
    			carrierPacking.getFilterMap().get(key) : new LinkedList<>();
    	AonFilterDialog sw = new AonFilterDialog(pb, label, "",
    			filterList, result.getData().cast()){

			@Override
			protected void onSelect(JavaScriptObject o, Boolean apply) {
				JsObject js = o.cast();
				if(apply){
					if(carrierPacking.getFilterMap().containsKey(key)){
						carrierPacking.getFilterMap().get(key).add(js.getId()+"");
					} else {
						LinkedList<String> list = new LinkedList<>();
						list.add(js.getId()+"");
						carrierPacking.getFilterMap().put(key, list);
					}
				} else {
					if(carrierPacking.getFilterMap().containsKey(key)){
						carrierPacking.getFilterMap().get(key).remove(js.getId()+"");
					}
				}
				carrierPacking.content();
			}
    	};
    	sw.show();
    }
}
