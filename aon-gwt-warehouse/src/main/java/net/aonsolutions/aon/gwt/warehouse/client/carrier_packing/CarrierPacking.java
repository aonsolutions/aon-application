package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.registry.JsRmedia;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate;
import com.esferalia.aon.gwt.common.client.polymer.AonToolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperRadioButtonElement;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperRadioButton;
import com.vaadin.polymer.paper.widget.event.ChangeEvent;
import com.vaadin.polymer.paper.widget.event.ChangeEventHandler;

import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class CarrierPacking extends AonTemplate{

	protected API API;
	private HashMap<String, LinkedList<String>> filterMap;
	private Boolean future = false;
	private CarrierPacking me = this;
	
	public CarrierPacking(AonData aonData, Boolean future) {
		filterMap = new HashMap<>();
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getUser().getLogin());
		this.future = future; 
	}
	
	public CarrierPacking(AonData aonData) {
		filterMap = new HashMap<>();
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				AonComboBoxElement.SRC,
				PaperButtonElement.SRC,
				PaperRadioButtonElement.SRC
		));
		
		Polymer.whenReady(o -> {
			super.onModuleLoad();
			startApplication();
			return null;
		});
	}
	
	private void startApplication() {
		toolbar();
		westContent();
		northContent();
		content();
		southContent();
	}

	private void toolbar() {
		if(future){
			AonToolbar toolbar = new AonToolbar("Carrier Packing") {
			
				@Override protected void onTitleClick() {}
				@Override protected void onStatsButtonClick() {}	
				@Override protected void onRefreshButtonClick() {}
				@Override protected void onMoreOptionButtonClick() {}
				@Override protected void onMenuButtonClick() {}
				@Override protected void onInfoButtonClick() {}
				@Override protected void onFastFilterButtonClick() {}
				@Override protected void onEditButtonClick() {}
				@Override protected void onDownloadButtonClick() {}
				@Override protected void onDeleteButtonClick() {}
				@Override protected void onAddButtonClick() {
					AonDialog dialog = createAddDialog();
					dialog.getElement().getStyle().setWidth(310, Unit.PX);
					dialog.center();
				}			
			}.setVisibleAllButton(false)
			 .setVisibleAddButton(true);
			setToolbar(toolbar);
		} else {
			getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
			Toolbar toolbar = new Toolbar() {
			
				@Override protected void reset() {
					Toolbar toolbar = (Toolbar) getToolbar().getWidget();
					toolbar.back.setVisible(true);
					toolbar.remove.setVisible(true);
					toolbar.packingList.setVisible(true);
					toolbar.sendPackingList.setVisible(true);
					getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 120);
					setNorthContent(new CarrierPackingPanel(me));
					setContent(new Label(""));
				}
				@Override protected void remove() {
					CarrierPackingPanel w = (CarrierPackingPanel) getNorthContent().getWidget();
					API.getWarehouse().deleteCarrierPacking(w.getJsCarrierPacking().getId());
					startApplication();
				}
				@Override protected void back() {
					startApplication();
				}
				@Override
				protected void packingList() {
					CarrierPackingPanel w = (CarrierPackingPanel) getNorthContent().getWidget();
					API.getWarehouse().downloadPackingList(w.getJsCarrierPacking().getId());
				}
				@Override
				protected void sendPackingList() {
					CarrierPackingPanel w = (CarrierPackingPanel) getNorthContent().getWidget();

					VerticalPanel panel = new VerticalPanel();
					panel.setStyleName(AON.AON_CSS.aonWidthAll());
					AonComboBox emailComboBox = new AonComboBox();
			    	emailComboBox.setLabel("De");
			    	emailComboBox.setItemLabelPath("name");
			    	emailComboBox.setItemValuePath("name");
					API.getCommon().getMailAccounts(new AsyncCallback<JSON<JsObject>>() {
						
						@Override
						public void onSuccess(JSON<JsObject> result) {
					    	emailComboBox.setItems(result.getData());
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
					panel.add(emailComboBox);
					
					PaperInput toText = new PaperInput();
					toText.setLabel("Para");
					API.getIncidence().getEnterpriseRmediaList(w.getJsCarrierPacking().getCarrier().getId(), new AsyncCallback<JSON<JsRmedia>>() {
						
						@Override
						public void onSuccess(JSON<JsRmedia> result) {
							result.getData().stream().forEach(rmedia -> {
								String media = rmedia.getMedia() + "";
								if(media.equals("4")){	
									toText.setValue(rmedia.getValue());
								}
							});
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
					panel.add(toText);

					AonComboBox signComboBox = new AonComboBox();
			    	signComboBox.setLabel("Firma de Correo");
			    	signComboBox.setItemLabelPath("name");
			    	signComboBox.setItemValuePath("name");
			    	API.getCommon().getSignatures(new AsyncCallback<JSON<JsObject>>() {
						
						@Override
						public void onSuccess(JSON<JsObject> result) {
							signComboBox.setItems(result.getData());
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
			    	panel.add(signComboBox);
			    	HorizontalPanel hp = new HorizontalPanel();
			    	PaperRadioButton prb1 = new PaperRadioButton();
			    	prb1.setChecked(true);			    	
			    	
			    	PaperRadioButton prb2 = new PaperRadioButton();

			    	prb1.addChangeHandler(new ChangeEventHandler() {
						
						@Override
						public void onChange(ChangeEvent event) {
							prb2.setChecked(!prb1.getChecked());
							if(prb1.getChecked()){
								toText.setVisible(true);
								API.getIncidence().getEnterpriseRmediaList(w.getJsCarrierPacking().getCarrier().getId(), new AsyncCallback<JSON<JsRmedia>>() {
									
									@Override
									public void onSuccess(JSON<JsRmedia> result) {
										result.getData().stream().forEach(rmedia -> {
											if(rmedia.getMedia().equals(4)){
												toText.setValue(rmedia.getValue());
											}
										});
									}
									
									@Override public void onFailure(Throwable caught) {}
								});
							}
						}
					});
			    	
			    	prb2.addChangeHandler(new ChangeEventHandler() {
						
						@Override
						public void onChange(ChangeEvent event) {
							prb1.setChecked(!prb2.getChecked());
							if(prb2.getChecked()){
								toText.setVisible(false);
							}
						}
					});
			    	hp.add(prb1);
			    	Label carrierLabel = new Label("Empresa de Transporte ");
			    	carrierLabel.addStyleName(AON.AON_CSS.aonPaddingRight());
			    	hp.add(carrierLabel);
			    	hp.add(prb2);
			    	if(w.getJsCarrierPacking().getType().getName().equals(CarrierPackingType.SHIPMENT_REQUEST.getName())){
						hp.add(new Label("Proveedor"));
					} else hp.add(new Label("Cliente"));

			    	panel.add(hp);
			    	AonDialog dialog = new AonDialog("Enviar Packing List", panel) {
						
						@Override protected void onCancel() {hide();}
						
						@Override 
						protected void onAccept() {
							JsObject jsEmail = (JsObject) emailComboBox.getSelectedItem();
							JsObject jsSign = (JsObject) signComboBox.getSelectedItem();
							String requestData = "{\"carrier_packing\":\""+ w.getJsCarrierPacking().getId() +"\","
									+ "\"mail_account\":\""+ jsEmail.getId() +"\","
									+ "\"signature\":\""+ ((jsSign != null) ? jsSign.getId() : "-1" )+"\"," 
									+ "\"to\":\""+ toText.getValue() + "\","
									+ "\"order\":\""+ -1 + "\","
									+ "\"type\":\"" + (prb1.getChecked() ? "carrier" : "registry") + "\"" + "}";

							API.getWarehouse().sendPackingList(requestData);
							hide();
						}
					};
					dialog.addAutoHidePartner(emailComboBox.getElementById("overlay"));
					dialog.addAutoHidePartner(signComboBox.getElementById("overlay"));
					dialog.setAutoHideEnabled(true);
					dialog.getElement().getStyle().setWidth(310, Unit.PX);
					dialog.center();
				}
			};
			setToolbar(toolbar);
		}
	}

	private void westContent(){
		
	}
	
	private void northContent(){
		getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 85);
		setNorthContent(new FilterPanel(this));
	}
	
	public void carrierPackingContent(JsCarrierPacking js){
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.back.setVisible(true);
		toolbar.remove.setVisible(true);
		toolbar.packingList.setVisible(true);
		toolbar.sendPackingList.setVisible(true);
		getContentDockLayoutPanel().setWidgetSize(getNorthContent(), 120);
		setNorthContent(new CarrierPackingPanel(me,js));
		setContent(new CarrierPackingSelect(me, js));
	}
	
	public void setSelectContent(JsCarrierPacking js){
		setContent(new CarrierPackingSelect(me, js));
	}
	
	public void content(){
		API.getWarehouse().getCarrierPacking(filterMap, new AsyncCallback<JSON<JsCarrierPacking>>() {
			
			@Override
			public void onSuccess(JSON<JsCarrierPacking> result) {
				setContent(new Grid(me, result.getData().toLinkedList()));
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}

	public void southContent(){
		getContentSplitLayoutPanel().setWidgetSize(getSouthContent(), 0);
	}
	
	public void southContent(JsCarrierPacking js, JsOrder p, AonJsArray<JsOrderDetail> details){
		if(js.getType().getName().equalsIgnoreCase(CarrierPackingType.SHIPMENT_REQUEST.getName())){
			getContentSplitLayoutPanel().setWidgetSize(getSouthContent(), 300);
			setSouthContent(new CarrierPackingSouth2(me, js, p, details));
		} else {
			getContentSplitLayoutPanel().setWidgetSize(getSouthContent(), 300);
			setSouthContent(new CarrierPackingSouth(me, p, details));
		}
	}
	
	private AonDialog createAddDialog(){
		VerticalPanel v = new VerticalPanel();
		
		HorizontalPanel hp = new HorizontalPanel();
		
		PaperInput series = new PaperInput();
		series.setLabel(AON.MSG.series());
		hp.add(series);
		
		hp.add(new Label("-"));
		
		PaperInput number = new PaperInput();
		number.setLabel(AON.MSG.number());
		hp.add(number);
		
		v.add(hp);
		
		AonComboBox type = new AonComboBox();
		type.setLabel(AON.MSG.type());
		type.setItemLabelPath("name");
		type.setItemValuePath("name");
		v.add(type);
		
		AonComboBox status = new AonComboBox();
		status.setLabel(AON.MSG.status());
		status.setItemLabelPath("name");
		status.setItemValuePath("name");
		v.add(status);
		
		PaperInput carrier = new PaperInput();
		carrier.setLabel(AON.MSG.carrier());
		v.add(carrier);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		
		PaperInput issueDate = new PaperInput();
		issueDate.setLabel("Fecha de Solicitud");
		hp2.add(issueDate);
		
		hp2.add(new Label("-"));
	
		PaperInput deliveryDate = new PaperInput();
		deliveryDate.setLabel("Fecha de Entrega");
		hp2.add(deliveryDate);
		
		v.add(hp2);
		
		return new AonDialog("Nuevo Carrier Packing", v) {
			@Override protected void onCancel() {hide();}
			
			@Override protected void onAccept() {hide();}
		};
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}
	
	public void setEnableType(Boolean enable){
		CarrierPackingPanel cpp = (CarrierPackingPanel) getNorthContent().getWidget();
		cpp.type.setEnabled(enable);
	}
	
	public void refreshSelect(){
		CarrierPackingSelect cps = (CarrierPackingSelect) getContent().getWidget();
		cps.refresh();
	}
	
	public void refreshSouth2(JsOrder order){
		CarrierPackingSouth2 cps = (CarrierPackingSouth2) getSouthContent().getWidget();
		cps.refresh(order);
	}
}
