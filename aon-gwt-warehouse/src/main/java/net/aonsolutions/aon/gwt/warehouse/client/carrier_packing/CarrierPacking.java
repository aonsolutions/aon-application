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
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.client.widget.Toolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperRadioButton;
import com.vaadin.polymer.paper.widget.event.ChangeEvent;
import com.vaadin.polymer.paper.widget.event.ChangeEventHandler;

import net.aonsolutions.aon.gwt.warehouse.client.IWarehouse;
import net.aonsolutions.aon.gwt.warehouse.client.IWarehouseAsync;
import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class CarrierPacking extends AonTemplate2{

	final IWarehouseAsync impl = GWT.create(IWarehouse.class);
	private API API;
	public HashMap<String, LinkedList<String>> filterMap;
	private CarrierPacking me = this;
	
	public CarrierPacking(AonData aonData) {
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();

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
		Toolbar toolbar = new Toolbar("Carrier Packing");
		toolbar.addButton("Volver", AON.AON_CSS.aonIconCancel(), false).addClickHandler(backClickHandler());
		toolbar.addButton(AON.MSG.newAction(), AON.AON_CSS.aonIconReset()).addClickHandler(resetClickHandler());
		toolbar.addButton(AON.MSG.deleteAction(), AON.AON_CSS.aonIconDelete(), false).addClickHandler(deleteClickHandler());
		toolbar.addButton("Impresi\u00f3n", AON.AON_CSS.aonIconPdf(), false).addClickHandler(printClickHandler());
		toolbar.addButton("Enviar", "aon-icon-mail", false).addClickHandler(emailClickHandler());
		
		PaperIconButton ant = new PaperIconButton();
		ant.setNoink(true);
		ant.setVisible(false);
		ant.setStyle("margin:0px;padding:0px;height:20px;right:40px;position:absolute;");
		ant.setIcon("chevron-left");
		ant.addClickHandler(antClickHandler());
		toolbar.addWidget(ant);
		
		PaperIconButton next = new PaperIconButton();
		next.setNoink(true);
		next.setVisible(false);
		next.setStyle("margin:0px;padding:0px;height:20px;right:0px;position:absolute;");
		next.setIcon("chevron-right");
		next.addClickHandler(nextClickHandler());
		toolbar.addWidget(next);
		
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
		Toolbar toolbar = (Toolbar) getToolbar().getWidget();
		
		toolbar.getButtonPanel().getWidget(0).setVisible(true);// setBackVisible(false);
		toolbar.getButtonPanel().getWidget(2).setVisible(true);// setRemoveVisible(false);
		toolbar.getButtonPanel().getWidget(3).setVisible(true);// setPrintVisible(false);
		toolbar.getButtonPanel().getWidget(4).setVisible(true);// setEmailVisible(false);	
		toolbar.getButtonPanel().getWidget(5).setVisible(true);// setAntVisible(false);
		toolbar.getButtonPanel().getWidget(6).setVisible(true);// setNextVisible(false);

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
	
	private ClickHandler backClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Toolbar toolbar = (Toolbar) getToolbar().getWidget();
				
				toolbar.getButtonPanel().getWidget(0).setVisible(false);// setBackVisible(false);
				toolbar.getButtonPanel().getWidget(2).setVisible(false);// setRemoveVisible(false);
				toolbar.getButtonPanel().getWidget(3).setVisible(false);// setPrintVisible(false);
				toolbar.getButtonPanel().getWidget(4).setVisible(false);// setEmailVisible(false);	
				toolbar.getButtonPanel().getWidget(5).setVisible(false);// setAntVisible(false);
				toolbar.getButtonPanel().getWidget(6).setVisible(false);// setNextVisible(false);
				
				content(filterMap);
			}
		};
	}
	
	private ClickHandler resetClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				createCarrierPacking();
			}
		};
	}
	
	private ClickHandler deleteClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				removeCarrierPacking();
			}
		};
	}
	
	private ClickHandler printClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CarrierPackingDetail w = (CarrierPackingDetail) getContent().getWidget();
				API.getWarehouse().downloadPackingList(w.getJsCarrierPacking().getId());
			}
		};
	}
	
	private ClickHandler emailClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				sendEmail();
			}
		};
	}
	
	private ClickHandler antClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
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
		};
	}
	
	private ClickHandler nextClickHandler() {
		return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
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
		};
	}

	
}
