package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

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
    
    private CarrierPackingPrincipal parent;
    
    private void onChange(String key, LinkedList<String> value) {
    	parent.getFilterMap().put(key, value);
		parent.gridContent();
	}
    
    public FilterPanel(CarrierPackingPrincipal parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));       

    	// -------------------- DATE - FROM _____ TO ______
    	HorizontalPanel datePanel = new HorizontalPanel(); 
    	datePanel.addStyleName(AON.AON_CSS.aonMarginTop());  
    	InlineLabel issueLabel = new InlineLabel("Fecha de Carga");
    	issueLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
    	issueLabel.setWidth("20px");
		datePanel.add(issueLabel);
		
		
		final DateBoxEx issue = new DateBoxEx();
		issue.getElement().getStyle().setBorderColor("#dedede");
		issue.getElement().getStyle().setHeight(16, Unit.PX);;
		issue.setWidth("70px");
		issue.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(issue.getValue().getTime()));
				onChange("issue_date", list);
			}
		});
		
		if (parent.getFilterMap().containsKey("issue_date")) {
		    try {
		        long millis = Long.parseLong(parent.getFilterMap().get("issue_date").get(0));
		        issue.setValue(new Date(millis));
		    } catch (Exception ignored) {}
		}
		
		datePanel.add(issue);
		
		InlineLabel deliveryLabel = new InlineLabel(AON.MSG.deliveryDate());
		deliveryLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		deliveryLabel.setWidth("20px");
		datePanel.add(deliveryLabel);

		final DateBoxEx delivery = new DateBoxEx();
		delivery.setWidth("70px");
		delivery.getElement().getStyle().setBorderColor("#dedede");
		delivery.getElement().getStyle().setHeight(16, Unit.PX);;

		delivery.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(delivery.getValue().getTime()));
				onChange("delivery_date", list);	
			}
		});
		
		if (parent.getFilterMap().containsKey("delivery_date")) {
		    try {
		        long millis = Long.parseLong(parent.getFilterMap().get("delivery_date").get(0));
		        delivery.setValue(new Date(millis));
		    } catch (Exception ignored) {}
		}
		
		datePanel.add(delivery);
	
		panel.add(datePanel);
    
//		TextBox tb = new TextBox();
//		tb.addStyleName(ICSS.aonSearchBoxIssues());
//		tb.addKeyUpHandler(new KeyUpHandler() {
//			
//			@Override
//			public void onKeyUp(KeyUpEvent event) {
//				LinkedList<String> list = new LinkedList<>();
//				list.add(tb.getValue());
//				onChange("text", list);
//			}
//		});
//		panel.add(tb);
		
		// ------------------ FILTER BUTTONS
		HorizontalPanel hpanel = new HorizontalPanel(); 
		hpanel.addStyleName(AON.AON_CSS.aonMarginTop());  
		
		// ------------------ SERIES
		Label series = new Label(AON.MSG.series() + ":");
		hpanel.add(series);
		ListBox seriesListBox = new ListBox();
		seriesListBox.addStyleName(AON.AON_CSS.aonMarginLeft());  
		seriesListBox.setWidth("150px");
		
		parent.getAPI().getWarehouse().getCarrierPackingSeries(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
		        seriesListBox.addItem("Todos", "");
				for (int i = 0; i < result.getData().length(); i++) {
			        JsObject item = result.getData().get(i);
			        String name = item.getName();
			        String id = String.valueOf(item.getId());

			        seriesListBox.addItem(name, id);
			    }
				
				if (parent.getFilterMap().containsKey("series")) {
				    String prev = parent.getFilterMap().get("series").get(0);
				    for (int i = 0; i < seriesListBox.getItemCount(); i++) {
				        if (seriesListBox.getValue(i).equals(prev)) {
				            seriesListBox.setSelectedIndex(i);
				            break;
				        }
				    }
				}
				
				changeValue(seriesListBox, AON.MSG.series());
				
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
//		seriesButton.addClickHandler(new ClickHandler() {
//					
//			@Override
//			public void onClick(ClickEvent event) {
//				parent.getAPI().getWarehouse().getCarrierPackingSeries(new AsyncCallback<JSON<JsObject>>() {
//					
//					@Override
//					public void onSuccess(JSON<JsObject> result) {
//						ButtonClick(seriesButton, result, AON.MSG.series());
//					}
//					
//					@Override public void onFailure(Throwable caught) {}
//				});
//			}
//		});
		hpanel.add(seriesListBox);
		
		// ------------------ CARRIERS
		Label carrier = new Label(AON.MSG.carrier() + ":");
		carrier.addStyleName(AON.AON_CSS.aonMarginLeft());  

		hpanel.add(carrier);
		ListBox carrierListBox = new ListBox();
		carrierListBox.addStyleName(AON.AON_CSS.aonMarginLeft());  
		carrierListBox.setWidth("150px");
		
		parent.getAPI().getWarehouse().getCarrierPackingCarriers(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
		        carrierListBox.addItem("Todos", "");
				for (int i = 0; i < result.getData().length(); i++) {
			        JsObject item = result.getData().get(i);
			        String name = item.getName();
			        String id = String.valueOf(item.getId());

			        carrierListBox.addItem(name, id);
			    }
				
				if (parent.getFilterMap().containsKey("carrier")) {
				    String prev = parent.getFilterMap().get("carrier").get(0);
				    for (int i = 0; i < carrierListBox.getItemCount(); i++) {
				        if (carrierListBox.getValue(i).equals(prev)) {
				            carrierListBox.setSelectedIndex(i);
				            break;
				        }
				    }
				}
				
				changeValue(carrierListBox, AON.MSG.carrier());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
//		carrierListBox.addClickHandler(new ClickHandler() {
//					
//			@Override
//			public void onClick(ClickEvent event) {
//				parent.getAPI().getWarehouse().getCarrierPackingCarriers(new AsyncCallback<JSON<JsObject>>() {
//					
//					@Override
//					public void onSuccess(JSON<JsObject> result) {
//						ButtonClick(carrierListBox, result, AON.MSG.carrier());
//					}
//					
//					@Override public void onFailure(Throwable caught) {}
//				});
//			}
//		});
		hpanel.add(carrierListBox);
		
		// ------------------ TYPE
		Label type = new Label(AON.MSG.type() + ":");
		type.addStyleName(AON.AON_CSS.aonMarginLeft());  

		hpanel.add(type);
		ListBox typeListBox = new ListBox();
		typeListBox.addStyleName(AON.AON_CSS.aonMarginLeft());  
		typeListBox.setWidth("150px");
		
		parent.getAPI().getWarehouse().getCarrierPackingTypes(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
		        typeListBox.addItem("Todos", "");
				for (int i = 0; i < result.getData().length(); i++) {
			        JsObject item = result.getData().get(i);
			        String name = item.getName();
			        String id = String.valueOf(item.getId());

			        typeListBox.addItem(name, id);
			    }
				
				if (parent.getFilterMap().containsKey("type")) {
				    String prev = parent.getFilterMap().get("type").get(0);
				    for (int i = 0; i < typeListBox.getItemCount(); i++) {
				        if (typeListBox.getValue(i).equals(prev)) {
				            typeListBox.setSelectedIndex(i);
				            break;
				        }
				    }
				}
				
				changeValue(typeListBox, AON.MSG.type());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
//		typeListBox.addClickHandler(new ClickHandler() {
//					
//			@Override
//			public void onClick(ClickEvent event) {
//				parent.getAPI().getWarehouse().getCarrierPackingTypes(new AsyncCallback<JSON<JsObject>>() {
//					
//					@Override
//					public void onSuccess(JSON<JsObject> result) {
//						ButtonClick(typeListBox, result, AON.MSG.type());
//					}
//					
//					@Override public void onFailure(Throwable caught) {}
//				});
//			}
//		});
		hpanel.add(typeListBox);
		
		// ------------------ STATUS
		Label status = new Label(AON.MSG.status() + ":");
		status.addStyleName(AON.AON_CSS.aonMarginLeft());  

		hpanel.add(status);
		ListBox statusListBox = new ListBox();
		statusListBox.addStyleName(AON.AON_CSS.aonMarginLeft());  
		statusListBox.setWidth("150px");
		
		parent.getAPI().getWarehouse().getCarrierPackingStatuses(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
		        statusListBox.addItem("Todos", "");
				for (int i = 0; i < result.getData().length(); i++) {
			        JsObject item = result.getData().get(i);
			        String name = item.getName();
			        String id = String.valueOf(item.getId());

			        statusListBox.addItem(name, id);
			    }
				statusListBox.addItem("No Finalizados", "NO_FINALIZADOS");
				
				if (parent.getFilterMap().containsKey("status")) {
		            LinkedList<String> prevStatuses = parent.getFilterMap().get("status");
		            if (prevStatuses.size() > 1 &&
		                prevStatuses.contains(CarrierPackingStatus.PENDING.ordinal() + "") &&
		                prevStatuses.contains(CarrierPackingStatus.ON_ROUTE.ordinal() + "") &&
		                prevStatuses.contains(CarrierPackingStatus.ON_BASCULA.ordinal() + "")) {

		                for (int i = 0; i < statusListBox.getItemCount(); i++) {
		                    if ("NO_FINALIZADOS".equals(statusListBox.getValue(i))) {
		                        statusListBox.setSelectedIndex(i);
		                        break;
		                    }
		                }
		            } else {
		                String prev = prevStatuses.get(0);
		                for (int i = 0; i < statusListBox.getItemCount(); i++) {
		                    if (statusListBox.getValue(i).equals(prev)) {
		                        statusListBox.setSelectedIndex(i);
		                        break;
		                    }
		                }
		            }
		        }
				changeValue(statusListBox, AON.MSG.status());
			}
			@Override public void onFailure(Throwable caught) {}
		});
		
//		statusListBox.addClickHandler(new ClickHandler() {
//					
//			@Override
//			public void onClick(ClickEvent event) {
//				parent.getAPI().getWarehouse().getCarrierPackingStatuses(new AsyncCallback<JSON<JsObject>>() {
//					
//					@Override
//					public void onSuccess(JSON<JsObject> result) {
//						ButtonClick(statusListBox, result, AON.MSG.status());
//					}
//					
//					@Override public void onFailure(Throwable caught) {}
//				});
//			}
//		});
		hpanel.add(statusListBox);
		
		panel.add(hpanel);
		
		AonSearchPanelButton closeButton = new AonSearchPanelButton(AON.MSG.close(),AON.CSS.aonWidgetClose());
		closeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				parent.closeFilterPanel();
			}
		});
		panel.add(closeButton);
    	panel.addStyleName("aon_search_panel");
    	panel.setWidth("99%");
    	panel.setHeight("60px");
    }
    
//    private PaperButton filterButton(String title) {
//		PaperButton button = new PaperButton();
//		button.setNoink(true);
//		button.setStyleName(ICSS.aonPaperButtonFilterIssues());
//				
//		InlineLabel label = new InlineLabel(title);
//		label.setStyleName(AON.AON_CSS.aonInnerLabel());
//		button.add(label);
//				
//		IronIcon icon = new IronIcon();
//		icon.setStyleName(ICSS.aonIronIconFilterIssues()); 
//		icon.setIcon("arrow-drop-down");
//		button.add(icon);
//		return button;
//	}
//    icon="close" style="position:absolute;right:15px;"
//    @UiHandler("panel") 
//	void cleanFilter(ClickEvent event){		
//    	categoryLabel.setText("");
//    	customerLabel.setText("");
//    	sellerLabel.setText("");
//    	workplaceLabel.setText("");
//    	periodLabel.setText("");
//    	
//    	onClean();
//	}
    
    
    private void changeValue(ListBox listBox, String label) {
    	String key;
    	if(AON.MSG.series().equals(label)){
    		key = "series"; 
    	} else if(AON.MSG.carrier().equals(label)){
    		key = "carrier"; 
    	} else if(AON.MSG.type().equals(label)){
    		key = "type"; 
    	} else if(AON.MSG.status().equals(label)){
    		key = "status"; 
    	} else {
    		return;
    	}
        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String selectedValue = listBox.getValue(listBox.getSelectedIndex());

                LinkedList<String> list = new LinkedList<>();

                if ("NO_FINALIZADOS".equals(selectedValue) && "status".equals(key)) {
                    LinkedList<String> status = new LinkedList<>();
                    status.add(CarrierPackingStatus.PENDING.ordinal() + "");
                    status.add(CarrierPackingStatus.ON_ROUTE.ordinal() + "");
                    status.add(CarrierPackingStatus.ON_BASCULA.ordinal() + "");
                    parent.getFilterMap().put(key, status);
                } else if (selectedValue != null && !selectedValue.trim().isEmpty()) {
	                list.add(selectedValue);
	                parent.getFilterMap().put(key, list);
	            } else {
	                parent.getFilterMap().remove(key);
	            }
	            parent.gridContent();

            }
        });
    }

//    private void ButtonClick(ListBox lb, JSON<JsObject> result, String label){
//    	if(AON.MSG.series().equals(label)){
//    		key = "series"; 
//    	} else if(AON.MSG.carrier().equals(label)){
//    		key = "carrier"; 
//    	} else if(AON.MSG.type().equals(label)){
//    		key = "type"; 
//    	} else if(AON.MSG.status().equals(label)){
//    		key = "status"; 
//    	}
//    	LinkedList<String> filterList = parent.getFilterMap().containsKey(key) ? 
//    			parent.getFilterMap().get(key) : new LinkedList<>();
//    	AonFilterDialog sw = new AonFilterDialog(lb, label, "",
//    			filterList, result.getData().cast()){
//
//			@Override
//			protected void onSelect(JavaScriptObject o, Boolean apply) {
//				JsObject js = o.cast();
//				if(apply){
//					if(parent.getFilterMap().containsKey(key)){
//						parent.getFilterMap().get(key).add(js.getId()+"");
//					} else {
//						LinkedList<String> list = new LinkedList<>();
//						list.add(js.getId()+"");
//						parent.getFilterMap().put(key, list);
//					}
//				} else {
//					if(parent.getFilterMap().containsKey(key)){
//						parent.getFilterMap().get(key).remove(js.getId()+"");
//					}
//				}
//				parent.gridContent();
//			}
//    	};
//    	sw.show();
//    }
}
