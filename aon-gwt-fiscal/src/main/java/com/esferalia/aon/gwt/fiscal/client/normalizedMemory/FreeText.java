package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Map;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FreeText extends ResizeComposite {

	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);

	
	@UiField
	Label title1;
	@UiField
	Label title2;
	@UiField
	TextArea value;
	
	Map<String, String> map;
	Enterprise enterprise;
	String part;
	NormalizedMemory normalizedMemory;
	
	interface FreeTextBinder extends UiBinder<Widget, FreeText> {
	}

	private static final FreeTextBinder binder = GWT
			.create(FreeTextBinder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public FreeText(String pageHeader, boolean isFreeText, String part, Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		this.enterprise = enterprise;
		this.part = part;
		this.normalizedMemory = nm;
		value = new TextArea();
		init();
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		
		if(isFreeText){
			title1.setText("MEMORIA ABREVIADA - TEXTO LIBRE");
		} else {
			title1.setText("MEMORIA ABREVIADA - MODELO DE RESPUESTA NORMALIZADA");
		}
		title2.setText(pageHeader);
	}
	
	public void init(){
		
		inma.getSchema(part,enterprise.getDomain(), new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				switch (part) {
				case "MAT1":keyExe(map, "MAT19019001", "9019001", value, "text", true);break;
				case "MAT2":keyExe(map, "MAT29029001", "9029001", value, "text", true);break;
				case "MAT3":keyExe(map, "MAT39039001", "9039001", value, "text", true);break;
				case "MAT4":keyExe(map, "MAT49049001", "9049001", value, "text", true);break;
				case "MAT5":keyExe(map, "MAT59059001", "9059001", value, "text", true);break;
				case "MAT6":keyExe(map, "MAT69069001", "9069001", value, "text", true);break;
				case "MAT7":keyExe(map, "MAT79079001", "9079001", value, "text", true);break;
				case "MAT8":keyExe(map, "MAT89089001", "9089001", value, "text", true);break;
				case "MAT9":keyExe(map, "MAT99099001", "9099001", value, "text", true);break;
				case "MAT11":keyExe(map, "MAT119119001", "9119001", value, "text", true);break;
				case "MAT12":keyExe(map, "MAT129129001", "9129001", value, "text", true);break;
				case "MAT13":keyExe(map, "MAT139139001", "9139001", value, "text", true);break;
				case "MAT14":keyExe(map, "MAT149149001", "9149001", value, "text", true);break;
				default:
					break;
				}
				
				
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});	
	}

	String key2Aux;
	TextArea tAux;
	private void keyExe(Map<String, String> map, String key, String key2, Widget w, String type, Boolean enable) {
		key2Aux = key2;
		if(type.equals("text")) {
			TextArea t = (TextArea) w;
			if(map.containsKey(key)){
				t.setValue(map.get(key));
				t.setEnabled(enable);
			}
			tAux = t;
			t.addChangeHandler(new ChangeHandler() {
				String key2 = key2Aux ;
				TextArea t = tAux;
				@Override
				public void onChange(ChangeEvent event) {

					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					inma.updateSchema(enterprise.getDomain(),key2, t.getValue(), new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
				}
			});
		}
	}
	
}
