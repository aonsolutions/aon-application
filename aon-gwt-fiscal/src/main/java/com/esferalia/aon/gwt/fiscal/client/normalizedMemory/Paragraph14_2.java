package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Map;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Paragraph14_2 extends ResizeComposite {
	@UiField TextBox MA14199000;
	@UiField TextBox MA141990009;
	@UiField TextBox MA14199001;
	@UiField TextBox MA141990019;
	@UiField TextBox MA14199002;
	@UiField TextBox MA141990029;
	@UiField TextBox MA14199003;
	@UiField TextBox MA141990039;
	@UiField TextBox MA14199004;
	@UiField TextBox MA141990049;
	@UiField TextBox MA14199005;
	@UiField TextBox MA141990059;
	@UiField TextBox MA14199006;
	@UiField TextBox MA141990069;
	@UiField TextBox MA14199007;
	@UiField TextBox MA141990079;
	@UiField TextBox MA14199008;
	@UiField TextBox MA141990089;
	@UiField TextBox MA14199009;
	@UiField TextBox MA141990099;
	@UiField TextBox MA14199010;
	@UiField TextBox MA141990109;
	@UiField TextBox MA14199011;
	@UiField TextBox MA141990119;
	@UiField TextBox MA14199012;
	@UiField TextBox MA141990129;
	@UiField TextBox MA14199013;
	@UiField TextBox MA141990139;
	@UiField TextBox MA14199014;
	@UiField TextBox MA141990149;
	@UiField TextBox MA14199015;
	@UiField TextBox MA141990159;
	
	
	// MEMORIA -AP14.2 
	@UiField TextBox MA14294600;
	@UiField TextBox MA14294601;
	@UiField TextBox MA14294602;
	@UiField TextBox MA14294603;
	@UiField TextBox MA14294604;
	@UiField TextBox MA14294605;
	@UiField TextBox MA14294606;
	@UiField TextBox MA14294607;
	@UiField TextBox MA14294608;
	@UiField TextBox MA14294610;
	@UiField TextBox MA14294611;
	@UiField TextBox MA14294612;
	@UiField TextBox MA14294613;
	@UiField TextBox MA14294614;
	
	
	@UiField TabPanel tabPanel;
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	
	Map<String, String> map;
	interface Paragraph14_2Binder extends UiBinder<Widget, Paragraph14_2> {
	}

	Enterprise enterprise;
	NormalizedMemory normalizedMemory;
	
	private static final Paragraph14_2Binder binder = GWT
			.create(Paragraph14_2Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Paragraph14_2() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}


	public Paragraph14_2(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		
		tabPanel = new TabPanel();
		
		MA14199000 = new TextBox();
		MA141990009 = new TextBox();
		MA14199001 = new TextBox();
		MA141990019 = new TextBox();
		MA14199002 = new TextBox();
		MA141990029 = new TextBox();
		MA14199003 = new TextBox();
		MA141990039 = new TextBox();
		MA14199004 = new TextBox();
		MA141990049 = new TextBox();
		MA14199005 = new TextBox();
		MA141990059 = new TextBox();
		MA14199006 = new TextBox();
		MA141990069 = new TextBox();
		MA14199007 = new TextBox();
		MA141990079 = new TextBox();
		MA14199008 = new TextBox();
		MA141990089 = new TextBox();
		MA14199009 = new TextBox();
		MA141990099 = new TextBox();
		MA14199010 = new TextBox();
		MA141990109 = new TextBox();
		MA14199011 = new TextBox();
		MA141990119 = new TextBox();
		MA14199012 = new TextBox();
		MA141990129 = new TextBox();
		MA14199013 = new TextBox();
		MA141990139 = new TextBox();
		MA14199014 = new TextBox();
		MA141990149 = new TextBox();
		MA14199015 = new TextBox();
		MA141990159 = new TextBox();
		
		
		// MEMORIA -AP14.2 
		MA14294600 = new TextBox();
		MA14294601 = new TextBox();
		MA14294602 = new TextBox();
		MA14294603 = new TextBox();
		MA14294604 = new TextBox();
		MA14294605 = new TextBox();
		MA14294606 = new TextBox();
		MA14294607 = new TextBox();
		MA14294608 = new TextBox();
		MA14294610 = new TextBox();
		MA14294611 = new TextBox();
		MA14294612 = new TextBox();
		MA14294613 = new TextBox();
		MA14294614 = new TextBox();
		
		init();
		
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}

	public void init() {
		inma.getSchema(enterprise.getDocument(),"MA14",enterprise.getDomain(),false, new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				keyExe(map, "MA14199000", "99000", MA14199000, "text", true);
				keyExe(map, "MA14199001", "99001", MA14199001, "text", true);
				keyExe(map, "MA14199002", "99002", MA14199002, "text", true);
				keyExe(map, "MA14199003", "99003", MA14199003, "text", true);
				keyExe(map, "MA14199004", "99004", MA14199004, "text", true);
				keyExe(map, "MA14199005", "99005", MA14199005, "text", true);
				keyExe(map, "MA14199006", "99006", MA14199006, "text", true);
				keyExe(map, "MA14199007", "99007", MA14199007, "text", true);
				keyExe(map, "MA14199008", "99008", MA14199008, "text", true);
				keyExe(map, "MA14199009", "99009", MA14199009, "text", true);
				keyExe(map, "MA14199010", "99010", MA14199010, "text", true);
				keyExe(map, "MA14199011", "99011", MA14199011, "text", true);
				keyExe(map, "MA14199012", "99012", MA14199012, "text", true);
				keyExe(map, "MA14199013", "99013", MA14199013, "text", true);
				keyExe(map, "MA14199014", "99014", MA14199014, "text", true);
				keyExe(map, "MA14199015", "99015", MA14199015, "text", true);
				keyExe(map, "MA141990019", "990019", MA141990019, "text", true);
				keyExe(map, "MA141990029", "990029", MA141990029, "text", true);
				keyExe(map, "MA141990039", "990039", MA141990039, "text", true);
				keyExe(map, "MA141990049", "990049", MA141990049, "text", true);
				keyExe(map, "MA141990059", "990059", MA141990059, "text", true);
				keyExe(map, "MA141990069", "990069", MA141990069, "text", true);
				keyExe(map, "MA141990079", "990079", MA141990079, "text", true);
				keyExe(map, "MA141990089", "990089", MA141990089, "text", true);
				keyExe(map, "MA141990099", "990099", MA141990099, "text", true);
				keyExe(map, "MA141990109", "990109", MA141990109, "text", true);
				keyExe(map, "MA141990119", "990119", MA141990119, "text", true);
				keyExe(map, "MA141990129", "990129", MA141990129, "text", true);
				keyExe(map, "MA141990139", "990139", MA141990139, "text", true);
				keyExe(map, "MA141990149", "990149", MA141990149, "text", true);
				keyExe(map, "MA141990159", "990159", MA141990159, "text", true);

				keyExe(map, "MA14294600", "94600", MA14294600, "text", true);
				keyExe(map, "MA14294601", "94601", MA14294601, "text", true);
				keyExe(map, "MA14294602", "94602", MA14294602, "text", true);
				keyExe(map, "MA14294603", "94603", MA14294603, "text", true);
				keyExe(map, "MA14294604", "94604", MA14294604, "text", true);
				keyExe(map, "MA14294605", "94605", MA14294605, "text", true);
				keyExe(map, "MA14294606", "94606", MA14294606, "text", true);
				keyExe(map, "MA14294607", "94607", MA14294607, "text", true);
				keyExe(map, "MA14294608", "94608", MA14294608, "text", true);
				keyExe(map, "MA14294610", "94610", MA14294610, "text", true);
				keyExe(map, "MA14294611", "94611", MA14294611, "text", true);
				keyExe(map, "MA14294612", "94612", MA14294612, "text", true);
				keyExe(map, "MA14294613", "94613", MA14294613, "text", true);
				keyExe(map, "MA14294614", "94614", MA14294614, "text", true);

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
					String val  = t.getValue().replace(',', '.');
					Integer n = AonStringUtils.countMatches(val, '.');
					if(n<=1){
						Double d = Double.parseDouble(val);
						Double d2 = MemoryUtils.round(d, 2);
						t.setValue(d2.toString());
						normalizedMemory.saveButton.setEnabled(true);
						normalizedMemory.cancelButton.setVisible(true);
						inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),key2, t.getValue(), new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {}
							@Override
							public void onSuccess(Void result) {}
						});
					}
					else{
						//TODO ERROR
						t.setValue("");
						Window.alert("Valor incorrecto");
					}
				}
			});
			
			t.addKeyPressHandler(new KeyPressHandler() {
				
				@Override
				public void onKeyPress(KeyPressEvent event) {
					if(!MemoryUtils.isNumeric(event.getCharCode()))
						event.preventDefault();
				}
			});
		}
	}
}
