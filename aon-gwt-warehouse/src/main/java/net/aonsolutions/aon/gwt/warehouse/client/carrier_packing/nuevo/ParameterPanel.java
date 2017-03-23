package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo;

import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsAppParam;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperFab;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;
import com.vaadin.polymer.paper.widget.PaperRadioButton;
import com.vaadin.polymer.paper.widget.event.ChangeEvent;
import com.vaadin.polymer.paper.widget.event.ChangeEventHandler;

import net.aonsolutions.aon.gwt.warehouse.client.IWarehouse;
import net.aonsolutions.aon.gwt.warehouse.client.IWarehouseAsync;
import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingParams;
import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingParams.Param;


public class ParameterPanel extends Composite {
	final IWarehouseAsync impl = GWT.create(IWarehouse.class);

	interface Binder extends UiBinder<Widget, ParameterPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField PaperFab addButton;	
	@UiField ScrollPanel parameterPanel;	

	private com.esferalia.aon.gwt.api.client.API API;
	CarrierPackingPrincipal principalParent;
	CarrierPackingDetail detailParent;
	Boolean isPrincipal;
	
	public ParameterPanel(CarrierPackingPrincipal parent) {
    	initWidget(binder.createAndBindUi(this));       
		this.API = parent.getAPI();
		this.principalParent = parent;
		this.isPrincipal = true;
		addStyleName(AON.AON_CSS.aonWidthAll());
		API.getCommon().getAppParam("AON_PL", new AsyncCallback<JSON<JsAppParam>>() {
			
			@Override
			public void onSuccess(JSON<JsAppParam> result) {
				VerticalPanel vp = new VerticalPanel();
				vp.addStyleName(AON.AON_CSS.aonWidthAll());
				result.getData().stream().forEach(p -> vp.add(buildParameter(p)));
				parameterPanel.add(vp);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});	
	}
	
	public ParameterPanel(CarrierPackingDetail parent) {
    	initWidget(binder.createAndBindUi(this));       
		this.API = parent.getAPI();
		this.detailParent = parent;
		this.isPrincipal = false;
		addStyleName(AON.AON_CSS.aonWidthAll());
		
		impl.readXml(parent.getJsCarrierPacking().getParams(), new AsyncCallback<CarrierPackingParams>() {
			
			@Override
			public void onSuccess(CarrierPackingParams result) {
				VerticalPanel vp = getParameterPanel(result);
				parameterPanel.add(vp);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}
	
	@UiHandler("addButton")
	void onClickParamater(ClickEvent event) {
		clickParameter();
	}
	
	private void clickParameter() {
		VerticalPanel panel = new VerticalPanel();
		PaperInput param = new PaperInput();
		param.setLabel("Par\u00e1metro");
		param.setMaxlength(20);
		panel.add(param);
		
		PaperInput value = new PaperInput();
		value.setLabel("Valor por defecto");
		panel.add(value);
		
		PaperRadioButton hr = new PaperRadioButton();
		PaperRadioButton sc = new PaperRadioButton();
		
		if(isPrincipal){
			HorizontalPanel h1 = new HorizontalPanel();
			hr.setChecked(true);	
			h1.add(hr);
			h1.add(new Label("Hoja de Ruta"));
	
			HorizontalPanel h2 =new HorizontalPanel();
			sc.setChecked(false);	
			h2.add(sc);	
			h2.add(new Label("Solicitud de Carga"));  	
	
			hr.addChangeHandler(new ChangeEventHandler() {
			
				@Override
				public void onChange(ChangeEvent event) {
					sc.setChecked(!hr.getChecked());
				}
			});
			
			sc.addChangeHandler(new ChangeEventHandler() {
		
				@Override
				public void onChange(ChangeEvent event) {
					hr.setChecked(!sc.getChecked());
				}
			});
			panel.add(h1);
			panel.add(h2);
		}
		
		AonDialog dialog = new AonDialog("Nuevo Par\u00e1metro", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				if(isPrincipal){
					String parameter = "";
					if(hr.getChecked()){
						parameter = "AON_PL_HR_";
					}else parameter = "AON_PL_SC_";
				
					String requestData = "{\"parameter\":\""+ parameter + param.getValue() +"\","
						+ "\"value\":\""+ value.getValue() +"\"}";
					API.getCommon().insertAppParam(requestData, new AsyncCallback<JsAppParam>() {
						
						@Override public void onSuccess(JsAppParam result) {
							principalParent.southContent();
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				} else {
					JsCarrierPacking js = detailParent.getJsCarrierPacking();
					impl.readXml(js.getParams(), new AsyncCallback<CarrierPackingParams>() {
						
						@Override
						public void onSuccess(CarrierPackingParams result) {
							Param param2 = new Param();
							param2.setName(param.getValue());
							param2.setValue(value.getValue());
							if(result.getParam() == null) {
								result.setParam(new LinkedList<>());
							}
							result.getParam().add(param2);
							
							impl.writeXml(result, new AsyncCallback<String>() {
								
								@Override
								public void onSuccess(String result) {
									String requestData = "{\"params\":\""+ result  +"\"}";
									API.getWarehouse().updateCarrierPacking(js.getId(), requestData, new AsyncCallback<JsCarrierPacking>() {
										
										@Override public void onSuccess(JsCarrierPacking result2) {
											detailParent.setJsCarrierPacking(result2);
											detailParent.southContent();
										}
										@Override public void onFailure(Throwable caught) {}
									});
									
								}
								
								@Override
								public void onFailure(Throwable caught) {}
							});
						}
						
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
				hide();	
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	public HorizontalPanel buildParameter(JsAppParam js){
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName(AON.AON_CSS.aonWidthAll());
		Boolean sc = js.getName().contains("SC");
		String[] arr = js.getName().split("_");
		String parameter = arr[arr.length-1];
		PaperItem pi = new PaperItem();
		IronIcon ironIcon = new IronIcon();
		ironIcon.setIcon("receipt");
		pi.add(ironIcon);
		 
		String str = (sc ? "Solicitud de Carga": "Hoja de Ruta") + " - " + parameter + " - " + js.getValue();
	    pi.add(new Label(str));
	    pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
	    
	    PaperIconButton edit = new PaperIconButton();
	    edit.setStyle("height: 24px;padding: 0px;position: absolute;right: 30px;");
	    edit.setIcon("create");
		edit.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				editParameter(js);
			}
		});
		
		PaperIconButton del = new PaperIconButton();
	    del.setStyle("height: 24px;padding: 0px;position: absolute;right: 0px;");
	    del.setIcon("delete");
		del.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				deleteParameter(js);		
			}
		});
	    
		hp.add(pi);
		hp.add(edit);
		hp.add(del);
	    return hp;
	}
	
	private void editParameter(JsAppParam js) {
		String[] arr = js.getName().split("_");
		String parameter = arr[arr.length-1];
		VerticalPanel panel = new VerticalPanel();
		PaperInput param = new PaperInput();
		param.setDisabled(true);
		param.setLabel("Par\u00e1metro");
		param.setValue(parameter);
		panel.add(param);
		
		PaperInput value = new PaperInput();
		value.setLabel("Valor por defecto");
		value.setValue(js.getValue());
		panel.add(value);
		
		AonDialog dialog = new AonDialog("Editar Par\u00e1metro", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				String requestData = "{\"parameter\":\""+ js.getName() +"\","
					+ "\"value\":\""+ value.getValue() +"\"}";
				API.getCommon().insertAppParam(requestData, new AsyncCallback<JsAppParam>() {
					
					@Override public void onSuccess(JsAppParam result) {
						principalParent.southContent();
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
	
	private void deleteParameter(JsAppParam js) {
		String[] arr = js.getName().split("_");
		String parameter = arr[arr.length-1];
		Label label = new Label("Est\u00e1 seguro que quiere borrar el par\u00e1metro "+ parameter);
		
		AonDialog dialog = new AonDialog("Borrar Parametro", label) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				String requestData = "{\"action\":\"delete\","
						+"\"id\":\""+ js.getId() +"\"}";
				API.getCommon().deleteAppParam(requestData, new AsyncCallback<JsAppParam>() {
					
					@Override public void onSuccess(JsAppParam result) {
						principalParent.southContent();
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
	
	public VerticalPanel getParameterPanel(CarrierPackingParams params){
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		if(params.getParam() == null) {
			params.setParam(new LinkedList<>());
		}
		params.getParam().stream().forEach(p -> panel.add(buildParameter(params, p)));
		return panel;
	}
	
	public HorizontalPanel buildParameter(CarrierPackingParams params, Param param){ 
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName(AON.AON_CSS.aonWidthAll());
		PaperItem pi = new PaperItem();
		IronIcon ironIcon = new IronIcon();
		ironIcon.setIcon("receipt");
		pi.add(ironIcon);
		 
		String str = param.getName() + " - " + param.getValue();
	    pi.add(new Label(str));
	    pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
	    
	    
	    PaperIconButton edit = new PaperIconButton();
	    edit.setStyle("height: 24px;padding: 0px;position: absolute;right: 30px;");
	    edit.setIcon("create");
		edit.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				editParameter(params, param);
			}
		});
		
		PaperIconButton del = new PaperIconButton();
	    del.setStyle("height: 24px;padding: 0px;position: absolute;right: 0px;");
	    del.setIcon("delete");
		del.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				deleteParameter(params, param);
			}
		});
   
	    hp.add(pi);
	    hp.add(edit);
	    hp.add(del);
	    return hp;
	}
	
	private void editParameter(CarrierPackingParams params, Param parameter) {
		VerticalPanel panel = new VerticalPanel();
		PaperInput param = new PaperInput();
		param.setDisabled(true);
		param.setLabel("Par\u00e1metro");
		param.setValue(parameter.getName());
		panel.add(param);
		
		PaperInput value = new PaperInput();
		value.setLabel("Valor por defecto");
		value.setValue(parameter.getValue());
		panel.add(value);
		
		AonDialog dialog = new AonDialog("Editar Par\u00e1metro", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				for(Param p : params.getParam()){
					if(p.getName() != null && parameter.getName() != null 
							&& (p.getName().equals(parameter.getName()))){
						p.setValue(value.getValue());
					}
				}
				impl.writeXml(params, new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String result) {
						String requestData = "{\"params\":\""+ result +"\"}";
						API.getWarehouse().updateCarrierPacking(detailParent.getJsCarrierPacking().getId(), requestData, new AsyncCallback<JsCarrierPacking>() {
							
							@Override
							public void onSuccess(JsCarrierPacking result) {
								detailParent.setJsCarrierPacking(result);
								detailParent.southContent();
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						});
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
	
	private void deleteParameter(CarrierPackingParams params, Param parameter) {
		Label label = new Label("Est\u00e1 seguro que quiere borrar el par\u00e1metro "+ parameter.getName());
		
		AonDialog dialog = new AonDialog("Borrar Par\u00e1metro", label) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				for(Param p : params.getParam()){
					if(p.getName() != null && parameter.getName() != null 
						&& p.getName().equals(parameter.getName())){
						params.getParam().remove(p);
					}
				}
				impl.writeXml(params, new AsyncCallback<String>() {
				
					@Override
					public void onSuccess(String result) {
						String requestData = "{\"params\":\""+ result +"\"}";
						//parent.setParameterPanel(result);
						API.getWarehouse().updateCarrierPacking(detailParent.getJsCarrierPacking().getId(), requestData, new AsyncCallback<JsCarrierPacking>() {
						
							@Override
							public void onSuccess(JsCarrierPacking result) {
								detailParent.setJsCarrierPacking(result);
								detailParent.southContent();
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
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
	
}
