package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Date;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CarrierPackingDetail extends Composite{
	
	interface Binder extends UiBinder<Widget, CarrierPackingDetail> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField SplitLayoutPanel contentSplitLayoutPanel;
	@UiField SimpleLayoutPanel content;
	@UiField SimpleLayoutPanel southContent;
	
	@UiField ListBox series;
	@UiField TextBox number;
	@UiField ListBox type;
	@UiField ListBox status;
	@UiField DateBoxEx issueDate;
	@UiField DateBoxEx deliveryDate;
	@UiField ListBox carrier;
	@UiField TextBox reference;
	@UiField TextBox numberPlate;
	@UiField TextBox driverDocument;
	@UiField TextBox driverName;
	
	@UiField InlineLabel addObservations;
	
	private CarrierPacking parent;
	private JsCarrierPacking jsCarrierPacking;
	
	public CarrierPackingDetail(CarrierPacking parent, JsCarrierPacking js) {
		initWidget(binder.createAndBindUi(this));
		this.jsCarrierPacking = js;
		this.parent = parent;
		addObservations.setVisible(false);
		load();
		content();
		southContent();
		southContentSize(30.0);
	}
	
	private void load() {
		parent.getAPI().getWarehouse().getCarrierPackingSeries(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				result.getData().stream().forEach(s -> 
					series.addItem(s.getName()));
				if(jsCarrierPacking != null && jsCarrierPacking.getSeries() != null){
					for(Integer i = 0; i < series.getItemCount(); i++){
						if(jsCarrierPacking.getSeries().equals(series.getItemText(i))){
							series.setSelectedIndex(i);
						}
					}
				}
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		series.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateCarrierPacking();
			}
		});
		number.setText(jsCarrierPacking != null && jsCarrierPacking.getNumber() != null ? (jsCarrierPacking.getNumber() + "") : "");
		number.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateCarrierPacking();
			}
		});
		
		
		parent.getAPI().getWarehouse().getCarrierPackingTypes(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				result.getData().stream().forEach(s -> 
					type.addItem(s.getName(), s.getId()+""));
				type.setSelectedIndex(1);
				if(jsCarrierPacking != null &&  jsCarrierPacking.getType() != null){
					for(Integer i = 0; i < type.getItemCount(); i++){
						if(jsCarrierPacking.getType() != null & jsCarrierPacking.getType().getName().equals(type.getItemText(i))){
							type.setSelectedIndex(i);
						}
					}	
				}
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		type.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateCarrierPacking();
			}
		});
		

		parent.getAPI().getWarehouse().getCarrierPackingStatuses(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				result.getData().stream().forEach(s -> 
					status.addItem(s.getName(), s.getId()+""));
				
				if(jsCarrierPacking != null &&  jsCarrierPacking.getStatus() != null){
					for(Integer i = 0; i < status.getItemCount(); i++){
						if(jsCarrierPacking.getStatus() != null & jsCarrierPacking.getStatus().getName().equals(status.getItemText(i))){
							status.setSelectedIndex(i);
						}
					}
				}
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		status.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateCarrierPacking();
			}
		});
		
		if(jsCarrierPacking != null && jsCarrierPacking.getIssueDate() != null
				&& !"".equals(jsCarrierPacking.getIssueDate())){
			Date date = DateTimeFormat.getFormat("dd/MM/yyyy").parse(jsCarrierPacking.getIssueDate());
			issueDate.setValue(date);
		}	
		issueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				updateCarrierPacking();
			}
		});
		
		if(jsCarrierPacking != null && jsCarrierPacking.getDeliveryDate() != null
				&& !"".equals(jsCarrierPacking.getDeliveryDate())){
			Date date = DateTimeFormat.getFormat("dd/MM/yyyy").parse(jsCarrierPacking.getDeliveryDate());
			deliveryDate.setValue(date);
		}
		deliveryDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				updateCarrierPacking();
			}
		});
		
		parent.getAPI().getWarehouse().getCarrierPackingCarriers(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				result.getData().stream().forEach(s -> 
					carrier.addItem(s.getName(), s.getId()+""));
				if(jsCarrierPacking != null && jsCarrierPacking.getCarrier() != null){
					for(Integer i = 0; i < carrier.getItemCount(); i++){
						if(jsCarrierPacking.getCarrier() != null & jsCarrierPacking.getCarrier().getName().equals(carrier.getItemText(i))){
							carrier.setSelectedIndex(i);
						}
					}
				}
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		carrier.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateCarrierPacking();
			}
		});
		
		if(jsCarrierPacking != null && jsCarrierPacking.getCarrierReference() != null){
			reference.setText(jsCarrierPacking.getCarrierReference() != null ? jsCarrierPacking.getCarrierReference() : "");
		}
		reference.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateCarrierPacking();
			}
		});
		
		if(jsCarrierPacking != null && jsCarrierPacking.getNumberPlate() != null){
			numberPlate.setText(jsCarrierPacking.getNumberPlate() != null ? jsCarrierPacking.getNumberPlate() : "");
		}
		numberPlate.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateCarrierPacking();
			}
		});
	
		if(jsCarrierPacking != null && jsCarrierPacking.getDriverDocument() != null){
			driverDocument.setText(jsCarrierPacking.getDriverDocument() != null ? jsCarrierPacking.getDriverDocument() : "");
		}
		driverDocument.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateCarrierPacking();
			}
		});
		
		if(jsCarrierPacking != null && jsCarrierPacking.getDriverName() != null){
			driverName.setText(jsCarrierPacking.getDriverName() != null ? jsCarrierPacking.getDriverName() : "");
		}
		driverName.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateCarrierPacking();
			}
		});
		
		addObservations.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TextArea textArea = new TextArea();
				textArea.setWidth("255px");
				textArea.setHeight("100px");
				textArea.setValue(jsCarrierPacking.getObservation());
		    	AonDialog dialog = new AonDialog("Enviar Packing List", textArea) {
					
					@Override protected void onCancel() {hide();}
					
					@Override 
					protected void onAccept() {
					
						String requestData = "{\"carrier_packing\":\""+ jsCarrierPacking.getId() +"\","
								+ "\"observation\":\""+  textArea.getValue() +"\"}";

						parent.getAPI().getWarehouse().updateCarrierPacking(jsCarrierPacking.getId(), requestData, new AsyncCallback<JsCarrierPacking>() {
							
							@Override
							public void onSuccess(JsCarrierPacking result) {
								jsCarrierPacking = result;
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
						hide();
					}
				};
				dialog.setAutoHideEnabled(true);
				dialog.getElement().getStyle().setWidth(310, Unit.PX);
				dialog.center();
			}
		});
	}
	
	private void updateCarrierPacking(){
		if(jsCarrierPacking != null){
			parent.getAPI().getWarehouse().updateCarrierPacking(jsCarrierPacking.getId(), getData(), new AsyncCallback<JsCarrierPacking>() {
			
				@Override
				public void onSuccess(JsCarrierPacking result) {
					jsCarrierPacking = result;
					number.setValue(result.getNumber() + "");
					content();
				}
			
				@Override public void onFailure(Throwable caught) { }
			});
		} else {
			parent.getAPI().getWarehouse().insertCarrierPacking(getData(), new AsyncCallback<JsCarrierPacking>() {
				
				@Override
				public void onSuccess(JsCarrierPacking result) {
					jsCarrierPacking = result;
					number.setValue(result.getNumber() + "");
					content();
				}
				
				@Override public void onFailure(Throwable caught) { }
			});
		}
	}

	private String getData() {
		return "{\"series\":\""+ series.getSelectedValue() +"\","
				+ "\"number\":\""+ number.getValue() +"\","
				+ "\"type\":\""+ type.getSelectedValue() +"\","
				+ "\"status\":\""+ status.getSelectedValue() +"\","
				+ "\"issue_date\":\""+ (issueDate.getValue() != null ? issueDate.getValue().getTime() : "")+"\","
				+ "\"delivery_date\":\""+ (deliveryDate.getValue() != null ? deliveryDate.getValue().getTime() : "") +"\","
				+ "\"carrier\":\""+ carrier.getSelectedValue() +"\","
				+ "\"carrier_reference\":\""+ reference.getValue() +"\","
				+ "\"number_plate\":\""+ numberPlate.getValue() +"\","
				+ "\"driver_document\":\""+ driverDocument.getValue() +"\","
				+ "\"driver_name\":\""+ driverName.getValue() +"\""
				+ "}";
	}
	
	public void content(){
		content.setWidget(new SelectionPanel(this));	
	}
	
	public void southContent() {
		southContent.setWidget(new DetailFootPanel(this));
	}
	
	public void southContentSize(Double value) {
		contentSplitLayoutPanel.setWidgetSize(southContent, value);	
	}
	
	public API getAPI() {
		return parent.getAPI();
	}

	public JsCarrierPacking getJsCarrierPacking() {
		return jsCarrierPacking;
	}
	
	public void setJsCarrierPacking(JsCarrierPacking js) {
		jsCarrierPacking = js;
	}
}
