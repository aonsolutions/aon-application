package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Map;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Paragraph3_2 extends ResizeComposite {

	@UiField TextBox MA391000;
	@UiField TextBox MA3910009;

	@UiField TextBox MA391001;
	@UiField TextBox MA3910019;
	@UiField TextBox MA391002;
	@UiField TextBox MA3910029;
	@UiField TextBox MA391003;
	@UiField TextBox MA3910039;
	@UiField TextBox MA391004;
	@UiField TextBox MA3910049;

	//APLICACIÓN A
	@UiField TextBox MA391005;
	@UiField TextBox MA3910059;
	@UiField TextBox MA391006;
	@UiField TextBox MA3910069;
	@UiField TextBox MA391007;
	@UiField TextBox MA3910079;
	@UiField TextBox MA391008;
	@UiField TextBox MA3910089;
	@UiField TextBox MA391009;
	@UiField TextBox MA3910099;
	@UiField TextBox MA391010;
	@UiField TextBox MA3910109;
	@UiField TextBox MA391011;
	@UiField TextBox MA3910119;
	@UiField TextBox MA391012;
	@UiField TextBox MA3910129;

	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	Map<String, String> map;
	interface Paragraph3_2Binder extends UiBinder<Widget, Paragraph3_2> {
	}

	private static final Paragraph3_2Binder binder = GWT
			.create(Paragraph3_2Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);
	
	Enterprise enterprise;
	NormalizedMemory normalizedMemory;

	public Paragraph3_2() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}

	public Paragraph3_2(Enterprise enterprise, NormalizedMemory nm){
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		
		MA391000 = new TextBox();
		MA3910009 = new TextBox();

		MA391001 = new TextBox();
		MA3910019 = new TextBox();
		MA391002 = new TextBox();
		MA3910029 = new TextBox();
		MA391003 = new TextBox();
		MA3910039 = new TextBox();
		MA391004= new TextBox();
		MA3910049= new TextBox();

		//APLICACIÓN A
		MA391005= new TextBox();
		MA3910059= new TextBox();
		MA391006= new TextBox();
		MA3910069= new TextBox();
		MA391007= new TextBox();
		MA3910079= new TextBox();
		MA391008= new TextBox();
		MA3910089= new TextBox();
		MA391009= new TextBox();
		MA3910099= new TextBox();
		MA391010= new TextBox();
		MA3910109= new TextBox();
		MA391011= new TextBox();
		MA3910119= new TextBox();
		MA391012= new TextBox();
		MA3910129= new TextBox();

		
		init();
		
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void init() {
		inma.getSchema("MA3",enterprise.getDomain(), new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				
				keyExe(map, "MA391000", "91000", MA391000, "text", true);
				keyExe(map, "MA3910009", "910009", MA3910009, "text", true);
				keyExe(map, "MA391001", "91001", MA391001, "text", true);
				keyExe(map, "MA3910019", "910019", MA3910019, "text", true);
				keyExe(map, "MA391002", "91002", MA391002, "text", true);
				keyExe(map, "MA3910029", "910029", MA3910029, "text", true);
				keyExe(map, "MA391003", "91003", MA391003, "text", true);
				keyExe(map, "MA3910039", "910039", MA3910039, "text", true);
				keyExe(map, "MA391004", "91004", MA391004, "text", true);
				keyExe(map, "MA3910049", "910049", MA3910049, "text", true);
				keyExe(map, "MA391005", "91005", MA391005, "text", true);
				keyExe(map, "MA3910059", "910059", MA3910059, "text", true);
				keyExe(map, "MA391006", "91006", MA391006, "text", true);
				keyExe(map, "MA3910069", "910069", MA3910069, "text", true);
				keyExe(map, "MA391007", "91007", MA391007, "text", true);
				keyExe(map, "MA3910079", "910079", MA3910079, "text", true);
				keyExe(map, "MA391008", "91008", MA391008, "text", true);
				keyExe(map, "MA3910089", "910089", MA3910089, "text", true);
				keyExe(map, "MA391009", "91009", MA391009, "text", true);
				keyExe(map, "MA3910099", "910099", MA3910099, "text", true);
				keyExe(map, "MA391010", "91010", MA391010, "text", true);
				keyExe(map, "MA3910109", "910109", MA3910109, "text", true);
				keyExe(map, "MA391011", "91011", MA391011, "text", true);
				keyExe(map, "MA3910119", "910119", MA3910119, "text", true);
				keyExe(map, "MA391012", "91012", MA391012, "text", true);
				keyExe(map, "MA3910129", "910129", MA3910129, "text", true);
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
