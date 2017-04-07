package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class DetailFootPanel extends Composite {

	interface Binder extends UiBinder<Widget, DetailFootPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
		
	
	CarrierPackingDetail parent;
	
	public API getAPI() {
		return parent.getAPI();
	}
	
	public JsCarrierPacking getJsCarrierPacking(){
		return parent.getJsCarrierPacking();
	}
	
	public void setJsCarrierPacking(JsCarrierPacking js){
		parent.setJsCarrierPacking(js);
	}
	
	public DetailFootPanel(CarrierPackingDetail parent) {
		this.parent = parent;
		initWidget(binder.createAndBindUi(this));
		parameterPanel();
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				Integer value  = event.getSelectedItem();
				JsCarrierPacking js = parent.getJsCarrierPacking();
				if(value == 0){
					openFootPanel();
					parameterPanel();
				}else if(value == 1){
					openFootPanel();
					TextArea textArea = new TextArea();
					textArea.setWidth("95%");
					textArea.setHeight("100px");
					if(js.getObservation() != null){
						textArea.setValue(js.getObservation());
					}
					textArea.addValueChangeHandler(new ValueChangeHandler<String>() {
						
						@Override
						public void onValueChange(ValueChangeEvent<String> event) {
							String requestData = "{\"carrier_packing\":\""+ js.getId() +"\","
									+ "\"observation\":\""+  textArea.getValue() +"\"}";

							parent.getAPI().getWarehouse().updateCarrierPacking(js.getId(), requestData, new AsyncCallback<JsCarrierPacking>() {
								
								@Override
								public void onSuccess(JsCarrierPacking result) {
									 parent.setJsCarrierPacking(result);
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
						}
					});
					observationPanel.add(textArea);
				}
			}
		});
	}
	public void parameterPanel() {
		parameterPanel.setWidget(new ParameterPanel(parent));
	}

	
	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabPanel;
	@UiField SimpleLayoutPanel parameterPanel;
	@UiField ScrollPanel observationPanel;
	
	public TabLayoutPanel getTabPanel() {
		return tabPanel;
	}
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
	}
	
	public void openFootPanel() {
		Integer clientHeight = Window.getClientHeight();
		parent.southContentSize(clientHeight.doubleValue() / 3);
		parent.contentSplitLayoutPanel.animate(500);
	}
	
	public void closeFootPanel() {
		parent.southContentSize(30.0);
		parent.contentSplitLayoutPanel.animate(500);
	}
}
