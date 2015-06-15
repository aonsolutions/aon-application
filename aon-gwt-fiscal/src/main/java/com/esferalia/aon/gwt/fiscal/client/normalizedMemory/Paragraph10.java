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

public class Paragraph10 extends ResizeComposite {
	@UiField TextBox MA1095000;
	@UiField TextBox MA10950009;
	@UiField TextBox MA1095001;
	@UiField TextBox MA10950019;
	@UiField TextBox MA1095002;
	@UiField TextBox MA10950029;
	@UiField TextBox MA1095003;
	@UiField TextBox MA10950039;
	@UiField TextBox MA1095004;
	@UiField TextBox MA10950049;
	@UiField TextBox MA1095005;
	@UiField TextBox MA10950059;
	@UiField TextBox MA1095006;
	@UiField TextBox MA10950069;
	@UiField TextBox MA1095007;
	@UiField TextBox MA10950079;
	@UiField TextBox MA1095008;
	@UiField TextBox MA10950089;
	@UiField TextBox MA1095009;
	@UiField TextBox MA10950099;
	@UiField TextBox MA1095010;
	@UiField TextBox MA10950109;
	@UiField TextBox MA1095011;
	@UiField TextBox MA10950119;
	@UiField TextBox MA1095012;
	@UiField TextBox MA10950129;
	@UiField TextBox MA1095013;
	@UiField TextBox MA10950139;
	@UiField TextBox MA1095014;
	@UiField TextBox MA10950149;
	@UiField TextBox MA1095015;
	@UiField TextBox MA10950159;
	@UiField TextBox MA1095016;
	@UiField TextBox MA10950169;
	@UiField TextBox MA1095017;
	@UiField TextBox MA10950179;
	@UiField TextBox MA1095018;
	@UiField TextBox MA10950189;
	@UiField TextBox MA1095019;
	@UiField TextBox MA10950199;
	@UiField TextBox MA1095020;
	@UiField TextBox MA10950209;
	
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	
	Map<String, String> map;
	
	interface Paragraph10Binder extends UiBinder<Widget, Paragraph10> {
	}
	
	Enterprise enterprise;
	NormalizedMemory normalizedMemory;
	
	private static final Paragraph10Binder binder = GWT
			.create(Paragraph10Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Paragraph10() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();


		
		init();
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}

	public Paragraph10(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		
		MA1095000 = new TextBox();
		MA10950009 = new TextBox();
		MA1095001 = new TextBox();
		MA10950019 = new TextBox();
		MA1095002 = new TextBox();
		MA10950029 = new TextBox();
		MA1095003 = new TextBox();
		MA10950039 = new TextBox();
		MA1095004 = new TextBox();
		MA10950049 = new TextBox();
		MA1095005 = new TextBox();
		MA10950059 = new TextBox();
		MA1095006 = new TextBox();
		MA10950069 = new TextBox();
		MA1095007 = new TextBox();
		MA10950079 = new TextBox();
		MA1095008 = new TextBox();
		MA10950089 = new TextBox();
		MA1095009 = new TextBox();
		MA10950099 = new TextBox();
		MA1095010 = new TextBox();
		MA10950109 = new TextBox();
		MA1095011 = new TextBox();
		MA10950119 = new TextBox();
		MA1095012 = new TextBox();
		MA10950129 = new TextBox();
		MA1095013 = new TextBox();
		MA10950139 = new TextBox();
		MA1095014 = new TextBox();
		MA10950149 = new TextBox();
		MA1095015 = new TextBox();
		MA10950159 = new TextBox();
		MA1095016 = new TextBox();
		MA10950169 = new TextBox();
		MA1095017 = new TextBox();
		MA10950179 = new TextBox();
		MA1095018 = new TextBox();
		MA10950189 = new TextBox();
		MA1095019 = new TextBox();
		MA10950199 = new TextBox();
		MA1095020 = new TextBox();
		MA10950209 = new TextBox();
		
		init();
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void init() {
		inma.getSchema("MA10",enterprise.getDomain(), new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				keyExe(map, "MA1095000", "95000", MA1095000, "text", true);
				keyExe(map, "MA10950009", "950009", MA10950009, "text", true);
				keyExe(map, "MA1095001", "95001", MA1095001, "text", true);
				keyExe(map, "MA10950019", "950019", MA10950019, "text", true);
				keyExe(map, "MA1095002", "95002", MA1095002, "text", true);
				keyExe(map, "MA10950029", "950029", MA10950029, "text", true);
				keyExe(map, "MA1095003", "95003", MA1095003, "text", true);
				keyExe(map, "MA10950039", "950039", MA10950039, "text", true);
				keyExe(map, "MA1095004", "95004", MA1095004, "text", true);
				keyExe(map, "MA10950049", "950049", MA10950049, "text", true);
				keyExe(map, "MA1095005", "95005", MA1095005, "text", true);
				keyExe(map, "MA10950059", "950059", MA10950059, "text", true);
				keyExe(map, "MA1095006", "95006", MA1095006, "text", true);
				keyExe(map, "MA10950069", "950069", MA10950069, "text", true);
				keyExe(map, "MA1095007", "95007", MA1095007, "text", true);
				keyExe(map, "MA10950079", "950079", MA10950079, "text", true);
				keyExe(map, "MA1095008", "95008", MA1095008, "text", true);
				keyExe(map, "MA10950089", "950089", MA10950089, "text", true);
				keyExe(map, "MA1095009", "95009", MA1095009, "text", true);
				keyExe(map, "MA10950099", "950099", MA10950099, "text", true);
				keyExe(map, "MA1095010", "95010", MA1095010, "text", true);
				keyExe(map, "MA10950109", "950109", MA10950109, "text", true);
				keyExe(map, "MA1095011", "95011", MA1095011, "text", true);
				keyExe(map, "MA10950119", "950119", MA10950119, "text", true);
				keyExe(map, "MA1095012", "95012", MA1095012, "text", true);
				keyExe(map, "MA10950129", "950129", MA10950129, "text", true);
				keyExe(map, "MA1095013", "95013", MA1095013, "text", true);
				keyExe(map, "MA10950139", "950139", MA10950139, "text", true);
				keyExe(map, "MA1095014", "95014", MA1095014, "text", true);
				keyExe(map, "MA10950149", "950149", MA10950149, "text", true);
				keyExe(map, "MA1095015", "95015", MA1095015, "text", true);
				keyExe(map, "MA10950159", "950159", MA10950159, "text", true);
				keyExe(map, "MA1095016", "95016", MA1095016, "text", true);
				keyExe(map, "MA10950169", "950169", MA10950169, "text", true);
				keyExe(map, "MA1095017", "95017", MA1095017, "text", true);
				keyExe(map, "MA10950179", "950179", MA10950179, "text", true);
				keyExe(map, "MA1095018", "95018", MA1095018, "text", true);
				keyExe(map, "MA10950189", "950189", MA10950189, "text", true);
				keyExe(map, "MA1095019", "95019", MA1095019, "text", true);
				keyExe(map, "MA10950199", "950199", MA10950199, "text", true);
				keyExe(map, "MA1095020", "95020", MA1095020, "text", true);
				keyExe(map, "MA10950209", "950209", MA10950209, "text", true);
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
