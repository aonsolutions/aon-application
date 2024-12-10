package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.api.client.registry.JsRmedia;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTemplate;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.paper.PaperButtonElement;
import com.vaadin.polymer.paper.PaperFabElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperItemElement;
import com.vaadin.polymer.paper.PaperRadioButtonElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperRadioButton;
import com.vaadin.polymer.paper.widget.event.ChangeEvent;
import com.vaadin.polymer.paper.widget.event.ChangeEventHandler;

import net.aonsolutions.aon.gwt.warehouse.client.IWarehouse;
import net.aonsolutions.aon.gwt.warehouse.client.IWarehouseAsync;
import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class CarrierPacking extends AonTemplate {
	
	final static AonResources AON_RESOURCES = GWT.create(AonResources.class);


	final IWarehouseAsync impl = GWT.create(IWarehouse.class);
	private API API;
	public HashMap<String, LinkedList<String>> filterMap;
	private CarrierPacking me = this;
	
	private AonToolbarButton backButton;
	private AonToolbarButton newButton;
	private AonToolbarButton deleteButton;
	private AonToolbarButton printButton;
	private AonToolbarButton sendButton;
	private AonToolbarButton tokenButton;
	private AonToolbarButton previousButton;
	private AonToolbarButton nextButton;
	
	public CarrierPacking(AonData aonData) {
		super("Packing List");
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				"iron-icons/maps-icons.html",
				AonComboBoxElement.SRC,
				PaperButtonElement.SRC,
				PaperRadioButtonElement.SRC,
				PaperItemElement.SRC,
				PaperFabElement.SRC,
				PaperInputElement.SRC,
				PaperToggleButtonElement.SRC
		), o -> {
			startApplication();
			return null;
		});
		
		Polymer.whenReady( o -> {
			super.onModuleLoad();
			startApplication();
			return null;
		});
	}
	
	private void startApplication() {
		toolbar();
		content();
	}
	


	private void toolbar() {
		getToolbar().getButtonContainer().clear();

		backButton = new AonToolbarButton("Volver",
			AON.CSS.aonIconBack(), false, e -> back());
		getToolbar().add(backButton);
		
		newButton = new AonToolbarButton(AON.MSG.newAction(),
			AON.CSS.aonIconAdd(), true, e -> createCarrierPacking());
		getToolbar().add(newButton);
		
		deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),
			AON.CSS.aonIconDelete(), false, e -> removeCarrierPacking());
		getToolbar().add(deleteButton);
		
		printButton = new AonToolbarButton("Impresi\u00f3n",
			AON.CSS.aonIconPdf(), false, e -> print());
		getToolbar().add(printButton);
		
		sendButton = new AonToolbarButton("Enviar",
			AON.CSS.aonIconEmail(), false, e -> sendEmail());
		getToolbar().add(sendButton);
		
		tokenButton = new AonToolbarButton("Token",
			AON_RESOURCES.css().aonIconBrighteye(), false, e -> brighteye());
		getToolbar().add(tokenButton);
		
		previousButton = new AonToolbarButton("Anterior",
			AON.CSS.aonIconPrev(), false, e -> previous());
		getToolbar().add(previousButton);
		
		nextButton = new AonToolbarButton("Siguiente",
			AON.CSS.aonIconNext(), false, e -> next());
		getToolbar().add(nextButton);
	}
	
	/**
	 * Contenido de la pantalla principal de la aplicación.
	 * Lista de todos los Carrier Packing.(FilterPanel-Grid-FooterPanel)
	 */
	public void content(){
		setContent(new CarrierPackingPrincipal(this));
	}
	
	public void content(HashMap<String, LinkedList<String>> filterMap){
		setContent(new CarrierPackingPrincipal(this, filterMap));
	}
	
	/**
	 * Contenido con la información y funciones de 1 único CarrierPacking.
	 * (Información General - Selecciónar pedidos | envios - FooterPanel)
	 */
	public void carrierPackingContent(JsCarrierPacking js, HashMap<String, LinkedList<String>> filterMap){
		this.filterMap = filterMap;
		carrierPackingContent(js);
	}

	public void carrierPackingContent(JsCarrierPacking js){
		backButton.setVisible(true);
		deleteButton.setVisible(true);
		printButton.setVisible(true);
		sendButton.setVisible(true);
		tokenButton.setVisible(true);
		previousButton.setVisible(true);
		nextButton.setVisible(true);

		setContent(new CarrierPackingDetail(this, js));
	}
	
	public API getAPI() {
		return API;
	}
	
	//********** TOOLBAR FUNCTIONS **********//
	
	private void createCarrierPacking(){
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
	
	private void removeCarrierPacking(){
		Label label = new Label("Est\u00e1 seguro que quiere borrar el Carrier Packing ");
		
		AonDialog dialog = new AonDialog("Borrar Carrier Packing", label) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				CarrierPackingDetail w = (CarrierPackingDetail) getContent().getWidget();
				API.getWarehouse().deleteCarrierPacking(w.getJsCarrierPacking().getId(), new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						hide();
						startApplication();	
					}
					
					@Override public void onFailure(Throwable caught) {}
				});			
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void sendEmail() {
		CarrierPackingDetail w = (CarrierPackingDetail) getContent().getWidget();

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
				StringBuilder emails = new StringBuilder();
				result.getData().stream().forEach(rmedia -> {
					String media = rmedia.getMedia() + "";
					if(media.equals("4") && rmedia.isTechnical()){	
						emails.append(rmedia.getValue());
						emails.append(";");
					}
				});
				toText.setValue(emails.toString());
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
	
	/***** BUTTON CLICK HANDLER *****/
	
	private void back() {
		toolbar();
		content(filterMap);		
	}
	
	private void print() {
		CarrierPackingDetail w = (CarrierPackingDetail) getContent().getWidget();
		API.getWarehouse().downloadPackingList(w.getJsCarrierPacking().getId());
	}
	
	private void brighteye() {
		CarrierPackingDetail w = (CarrierPackingDetail) getContent().getWidget();
		Window.open("https://udapa.aonsolutions.net/udapa/qr?cp=" + w.getJsCarrierPacking().getId(), "_blank", null);
	}
	
	private void previous() {
		Integer page = Integer.parseInt(filterMap.get("page").get(0));
		if(page > 1 ){
			page = page - 1;
			LinkedList<String> list = new LinkedList<>();
			list.add(page.toString());
			filterMap.put("page", list);
			list = new LinkedList<>();
			list.add("1");
			filterMap.put("per_page", list);
			
			getAPI().getWarehouse().getCarrierPacking(filterMap, new AsyncCallback<JSON<JsCarrierPacking>>() {
					
				@Override
				public void onSuccess(JSON<JsCarrierPacking> result) {
					setContent(new CarrierPackingDetail(me, result.getData().get(0)));
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		}
	}
	
	private void next() {
		Integer page = Integer.parseInt(filterMap.get("page").get(0));
		page = page + 1;
		LinkedList<String> list = new LinkedList<>();
		list.add(page.toString());
		filterMap.put("page", list);
		list = new LinkedList<>();
		list.add("1");
		filterMap.put("per_page", list);
				
		getAPI().getWarehouse().getCarrierPacking(filterMap, new AsyncCallback<JSON<JsCarrierPacking>>() {
					
			@Override
			public void onSuccess(JSON<JsCarrierPacking> result) {
				if(result.getData().length() == 0){
					Integer page = Integer.parseInt(filterMap.get("page").get(0));
					page = page < 2 ? 1 : page - 1;
					LinkedList<String> list = new LinkedList<>();
					list.add(page.toString());
					filterMap.put("page", list);
				}
				setContent(new CarrierPackingDetail(me, result.getData().get(0)));					}
			
			@Override public void onFailure(Throwable caught) {}
		});	
	}

}
