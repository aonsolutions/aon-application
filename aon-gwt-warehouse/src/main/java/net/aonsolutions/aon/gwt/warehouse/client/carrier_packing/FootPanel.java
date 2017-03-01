package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
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
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.paper.widget.PaperFab;

public class FootPanel extends Composite {

	interface Binder extends UiBinder<Widget, FootPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	CarrierPacking parent;

	public FootPanel(CarrierPacking parent) {
		this.parent = parent;
		initWidget(binder.createAndBindUi(this));
		getTabLayout().selectTab(1);
		
		
		getTabLayout().addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				if(parent.isMainScreem && event.getItem() != 1){
					event.cancel();
				}
			}
		});
		getTabLayout().addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				Integer value  = event.getSelectedItem();
				if(value == 0){
					openFootPanel();
				} else if(value == 1){
					openFootPanel();
				}else if(value == 2){
					openFootPanel();
					TextArea textArea = new TextArea();
					textArea.setWidth("95%");
					textArea.setHeight("100px");
					JsCarrierPacking js = parent.getJsCarrierPacking();
					if(js.getObservation() != null){
						textArea.setValue(js.getObservation());
					}
					textArea.addValueChangeHandler(new ValueChangeHandler<String>() {
						
						@Override
						public void onValueChange(ValueChangeEvent<String> event) {
							String requestData = "{\"carrier_packing\":\""+ js.getId() +"\","
									+ "\"observation\":\""+  textArea.getValue() +"\"}";

							parent.API.getWarehouse().updateCarrierPacking(js.getId(), requestData, new AsyncCallback<JsCarrierPacking>() {
								
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
		//getTabLayout().getTabWidget(0).setVisible(false);
	}
	
	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabLayout;
	@UiField ScrollPanel selectionPanel;
	@UiField ScrollPanel parameterPanel;
	@UiField ScrollPanel observationPanel;
	@UiField PaperFab addButton;	
	
	public TabLayoutPanel getTabLayout() {
		return tabLayout;
	}
	
	public ScrollPanel getSelectionPanel() {
		return selectionPanel;
	}
	
	public ScrollPanel getParameterPanel() {
		return parameterPanel;
	}
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
	}
	
	@UiHandler("addButton")
	void onClickParamater(ClickEvent event) {
		parent.clickParameter();
	}
	
	public void openFootPanel() {
		Integer clientHeight = Window.getClientHeight();
		parent.southContentSize(clientHeight.doubleValue() / 3);
	}
	
	public void closeFootPanel() {
		parent.southContentSize(30.0);
	}
}
