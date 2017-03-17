package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import java.util.Arrays;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperRadioButtonElement;

import net.aonsolutions.aon.gwt.warehouse.client.IWarehouse;
import net.aonsolutions.aon.gwt.warehouse.client.IWarehouseAsync;
import net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.Toolbar;
import net.aonsolutions.polymer.aon.AonComboBoxElement;

public class CarrierPacking2 extends AonTemplate2{

	final IWarehouseAsync impl = GWT.create(IWarehouse.class);
	private API API;
	
	public CarrierPacking2(AonData aonData) {
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				AonComboBoxElement.SRC,
				PaperButtonElement.SRC,
				PaperRadioButtonElement.SRC,
				PaperItemElement.SRC
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
		content();
	}

	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar() {
			
			@Override protected void reset() {
				String requestData = "{\"action\":\"create\"}";
				API.getWarehouse().insertCarrierPacking(requestData, new AsyncCallback<JsCarrierPacking>() {
					
					@Override
					public void onSuccess(JsCarrierPacking result) {
						carrierPackingContent(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
			
			@Override protected void remove() {
				Integer carrierPackingId = 1;
				API.getWarehouse().deleteCarrierPacking(carrierPackingId);
				startApplication();
			}
			
			@Override protected void back() {
				startApplication();
			}
			
			@Override
			protected void print() {

			}
			
			@Override
			protected void email() {
				
			}
		};
		setToolbar(toolbar);
	}

	private void westContent(){
		
	}
	
	/**
	 * Contenido de la pantalla principal de la aplicación.
	 * Lista de todos los Carrier Packing.(FilterPanel-Grid-FooterPanel)
	 */
	public void content(){
		setContent(new CarrierPackingPrincipal(this));
	}
	
	/**
	 * Contenido con la información y funciones de 1 único CarrierPacking.
	 * (Información General - Selecciónar pedidos | envios - FooterPanel)
	 */
	public void carrierPackingContent(JsCarrierPacking js){
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		toolbar.setBackVisible(true);
		toolbar.setRemoveVisible(true);
		toolbar.setPrintVisible(true);
		toolbar.setEmailVisible(true);		
		setContent(new CarrierPackingDetail(this, js));
		//setContent(widget);
	}

	public API getAPI() {
		return API;
	}
}
