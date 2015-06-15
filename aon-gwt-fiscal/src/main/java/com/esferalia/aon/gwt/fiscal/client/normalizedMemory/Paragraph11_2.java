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
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Paragraph11_2 extends ResizeComposite {

	@UiField TextBox MA1196000;
	@UiField TextBox MA11960009;
	@UiField TextBox MA1196001;
	@UiField TextBox MA11960019;
	@UiField TextBox MA1196002;
	@UiField TextBox MA11960029;
	@UiField TextBox MA1196010;
	@UiField TextBox MA11960109;
	@UiField TextBox MA1196011;
	@UiField TextBox MA11960119;
	@UiField TextBox MA1196012;
	@UiField TextBox MA11960129;
	@UiField TextBox MA1196013;
	@UiField TextBox MA11960139;
	@UiField TextBox MA1196014;
	@UiField TextBox MA11960149;
	@UiField TextBox MA1196015;
	@UiField TextBox MA11960159;
	@UiField TextBox MA1196016;
	@UiField TextBox MA11960169;
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	
	Map<String, String> map;
	interface Paragraph11_2Binder extends UiBinder<Widget, Paragraph11_2> {
	}
	Enterprise enterprise;
	NormalizedMemory normalizedMemory;
	
	private static final Paragraph11_2Binder binder = GWT
			.create(Paragraph11_2Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Paragraph11_2() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public Paragraph11_2(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		this.enterprise = enterprise;
		this.normalizedMemory = nm;

		MA1196000 = new TextBox();
		MA11960009 = new TextBox();
		MA1196001 = new TextBox();
		MA11960019 = new TextBox();
		MA1196002 = new TextBox();
		MA11960029 = new TextBox();
		MA1196010 = new TextBox();
		MA11960109 = new TextBox();
		MA1196011 = new TextBox();
		MA11960119 = new TextBox();
		MA1196012 = new TextBox();
		MA11960129 = new TextBox();
		MA1196013 = new TextBox();
		MA11960139 = new TextBox();
		MA1196014 = new TextBox();
		MA11960149 = new TextBox();
		MA1196015 = new TextBox();
		MA11960159 = new TextBox();
		MA1196016 = new TextBox();
		MA11960169 = new TextBox();
		
		init();
		
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void init() {
	inma.getSchema("MA11",enterprise.getDomain(), new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				
				keyExe(map, "MA1196000", "96000", MA1196000, "text", true);
				keyExe(map, "MA11960009", "960009", MA11960009, "text", true);
				keyExe(map, "MA1196001", "96001", MA1196001, "text", true);
				keyExe(map, "MA11960019", "960019", MA11960029, "text", true);
				keyExe(map, "MA1196002", "96002", MA1196002, "text", true);
				keyExe(map, "MA11960029", "960029", MA11960029, "text", true);
				
				keyExe(map, "MA1196010", "96010", MA1196010, "text", true);
				keyExe(map, "MA11960109", "960109", MA11960109, "text", true);
				keyExe(map, "MA1196011", "96011", MA1196011, "text", true);
				keyExe(map, "MA11960119", "960119", MA11960119, "text", true);
				keyExe(map, "MA1196012", "96012", MA1196012, "text", true);
				keyExe(map, "MA11960129", "960129", MA11960129, "text", true);
				keyExe(map, "MA1196013", "96013", MA1196013, "text", true);
				keyExe(map, "MA11960139", "960139", MA11960139, "text", true);
				keyExe(map, "MA1196014", "96014", MA1196014, "text", true);
				keyExe(map, "MA11960149", "960149", MA11960149, "text", true);
				keyExe(map, "MA1196015", "96015", MA1196015, "text", true);
				keyExe(map, "MA11960159", "960159", MA11960159, "text", true);
				keyExe(map, "MA1196016", "96016", MA1196016, "text", true);
				keyExe(map, "MA11960169", "960169", MA11960169, "text", true);
				
			}
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});	
	}

	String key2Aux;
	TextBox tAux;
	private void keyExe(Map<String, String> map, String key, String key2, Widget w, String type, Boolean enable) {
		key2Aux = key2;
		if(type.equals("text")) {
			TextBox t = (TextBox) w;
			if(map.containsKey(key)){
				t.setValue(map.get(key));
				t.setEnabled(enable);
			}
			tAux = t;
			t.addChangeHandler(new ChangeHandler() {
				String key2 = key2Aux ;
				TextBox t = tAux;
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
