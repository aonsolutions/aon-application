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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Paragraph5_2 extends ResizeComposite {

	@UiField TextBox MA592001;
	@UiField TextBox MA592002;
	@UiField TextBox MA592003;
	@UiField TextBox MA592011;
	@UiField TextBox MA592012;
	@UiField TextBox MA592013;
	@UiField TextBox MA592141;
	@UiField TextBox MA592142;
	@UiField TextBox MA592143;
	@UiField TextBox MA592021;
	@UiField TextBox MA592022;
	@UiField TextBox MA592023;
	@UiField TextBox MA592031;
	@UiField TextBox MA592032;
	@UiField TextBox MA592033;
	@UiField TextBox MA592041;
	@UiField TextBox MA592042;
	@UiField TextBox MA592043;
	@UiField TextBox MA592051;
	@UiField TextBox MA592052;
	@UiField TextBox MA592053;
	@UiField TextBox MA592151;
	@UiField TextBox MA592152;
	@UiField TextBox MA592153;
	@UiField TextBox MA592061;
	@UiField TextBox MA592062;
	@UiField TextBox MA592063;
	@UiField TextBox MA592071;
	@UiField TextBox MA592072;
	@UiField TextBox MA592073;
	@UiField TextBox MA592081;
	@UiField TextBox MA592082;
	@UiField TextBox MA592083;
	@UiField TextBox MA592091;
	@UiField TextBox MA592092;
	@UiField TextBox MA592093;
	@UiField TextBox MA592101;
	@UiField TextBox MA592102;
	@UiField TextBox MA592103;
	@UiField TextBox MA592111;
	@UiField TextBox MA592112;
	@UiField TextBox MA592113;
	@UiField TextBox MA592121;
	@UiField TextBox MA592122;
	@UiField TextBox MA592123;
	@UiField TextBox MA592131;
	@UiField TextBox MA592132;
	@UiField TextBox MA592133;
	
	@UiField TextBox MA5920019;
	@UiField TextBox MA5920029;
	@UiField TextBox MA5920039;
	@UiField TextBox MA5920119;
	@UiField TextBox MA5920129;
	@UiField TextBox MA5920139;
	@UiField TextBox MA5921419;
	@UiField TextBox MA5921429;
	@UiField TextBox MA5921439;
	@UiField TextBox MA5920219;
	@UiField TextBox MA5920229;
	@UiField TextBox MA5920239;
	@UiField TextBox MA5920319;
	@UiField TextBox MA5920329;
	@UiField TextBox MA5920339;
	@UiField TextBox MA5920419;
	@UiField TextBox MA5920429;
	@UiField TextBox MA5920439;
	@UiField TextBox MA5920519;
	@UiField TextBox MA5920529;
	@UiField TextBox MA5920539;
	@UiField TextBox MA5921519;
	@UiField TextBox MA5921529;
	@UiField TextBox MA5921539;
	@UiField TextBox MA5920619;
	@UiField TextBox MA5920629;
	@UiField TextBox MA5920639;
	@UiField TextBox MA5920719;
	@UiField TextBox MA5920729;
	@UiField TextBox MA5920739;
	@UiField TextBox MA5920819;
	@UiField TextBox MA5920829;
	@UiField TextBox MA5920839;
	@UiField TextBox MA5920919;
	@UiField TextBox MA5920929;
	@UiField TextBox MA5920939;
	@UiField TextBox MA5921019;
	@UiField TextBox MA5921029;
	@UiField TextBox MA5921039;
	@UiField TextBox MA5921119;
	@UiField TextBox MA5921129;
	@UiField TextBox MA5921139;
	@UiField TextBox MA5921219;
	@UiField TextBox MA5921229;
	@UiField TextBox MA5921239;
	@UiField TextBox MA5921319;
	@UiField TextBox MA5921329;
	@UiField TextBox MA5921339;

	@UiField TextBox MA592200;
	@UiField TextBox MA592201;
	@UiField TextBox MA592202;
	@UiField TextBox MA592203;
	@UiField TextBox MA592204;
	@UiField TextBox MA592205;

	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	Map<String, String> map;
	
	interface Paragraph5_2Binder extends UiBinder<Widget, Paragraph5_2> {
	}
	
	Enterprise enterprise;
	NormalizedMemory normalizedMemory;

	private static final Paragraph5_2Binder binder = GWT
			.create(Paragraph5_2Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Paragraph5_2(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		
		MA592001 = new TextBox();
		MA592002 = new TextBox();
		MA592003 = new TextBox();
		MA592011 = new TextBox();
		MA592012 = new TextBox();
		MA592013 = new TextBox();
		MA592141 = new TextBox();
		MA592142 = new TextBox();
		MA592143 = new TextBox();
		MA592021 = new TextBox();
		MA592022 = new TextBox();
		MA592023 = new TextBox();
		MA592031 = new TextBox();
		MA592032 = new TextBox();
		MA592033 = new TextBox();
		MA592041 = new TextBox();
		MA592042 = new TextBox();
		MA592043 = new TextBox();
		MA592051 = new TextBox();
		MA592052 = new TextBox();
		MA592053 = new TextBox();
		MA592151 = new TextBox();
		MA592152 = new TextBox();
		MA592153 = new TextBox();
		MA592061 = new TextBox();
		MA592062 = new TextBox();
		MA592063 = new TextBox();
		MA592071 = new TextBox();
		MA592072 = new TextBox();
		MA592073 = new TextBox();
		MA592081 = new TextBox();
		MA592082 = new TextBox();
		MA592083 = new TextBox();
		MA592091 = new TextBox();
		MA592092 = new TextBox();
		MA592093 = new TextBox();
		MA592101 = new TextBox();
		MA592102 = new TextBox();
		MA592103 = new TextBox();
		MA592111 = new TextBox();
		MA592112 = new TextBox();
		MA592113 = new TextBox();
		MA592121 = new TextBox();
		MA592122 = new TextBox();
		MA592123 = new TextBox();
		MA592131 = new TextBox();
		MA592132 = new TextBox();
		MA592133 = new TextBox();
		
		MA5920019 = new TextBox();
		MA5920029 = new TextBox();
		MA5920039 = new TextBox();
		MA5920119 = new TextBox();
		MA5920129 = new TextBox();
		MA5920139 = new TextBox();
		MA5921419 = new TextBox();
		MA5921429 = new TextBox();
		MA5921439 = new TextBox();
		MA5920219 = new TextBox();
		MA5920229 = new TextBox();
		MA5920239 = new TextBox();
		MA5920319 = new TextBox();
		MA5920329 = new TextBox();
		MA5920339 = new TextBox();
		MA5920419 = new TextBox();
		MA5920429 = new TextBox();
		MA5920439 = new TextBox();
		MA5920519 = new TextBox();
		MA5920529 = new TextBox();
		MA5920539 = new TextBox();
		MA5921519 = new TextBox();
		MA5921529 = new TextBox();
		MA5921539 = new TextBox();
		MA5920619 = new TextBox();
		MA5920629 = new TextBox();
		MA5920639 = new TextBox();
		MA5920719 = new TextBox();
		MA5920729 = new TextBox();
		MA5920739 = new TextBox();
		MA5920819 = new TextBox();
		MA5920829 = new TextBox();
		MA5920839 = new TextBox();
		MA5920919 = new TextBox();
		MA5920929 = new TextBox();
		MA5920939 = new TextBox();
		MA5921019 = new TextBox();
		MA5921029 = new TextBox();
		MA5921039 = new TextBox();
		MA5921119 = new TextBox();
		MA5921129 = new TextBox();
		MA5921139 = new TextBox();
		MA5921219 = new TextBox();
		MA5921229 = new TextBox();
		MA5921239 = new TextBox();
		MA5921319 = new TextBox();
		MA5921329 = new TextBox();
		MA5921339 = new TextBox();

		MA592200 = new TextBox();
		MA592201 = new TextBox();
		MA592202 = new TextBox();
		MA592203 = new TextBox();
		MA592204 = new TextBox();
		MA592205 = new TextBox();
		
		init();
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void init() {
		
		
		inma.getSchema(enterprise.getDocument(),"MA5",enterprise.getDomain(),false, new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				keyExe(map, "MA592001", "92001", MA592001, "text", true);
				keyExe(map, "MA592002", "92002", MA592002, "text", true);
				keyExe(map, "MA592003", "92003", MA592003, "text", true);
				keyExe(map, "MA592011", "92011", MA592011, "text", true);
				keyExe(map, "MA592012", "92012", MA592012, "text", true);
				keyExe(map, "MA592013", "92013", MA592013, "text", true);			
				keyExe(map, "MA592141", "92141", MA592141, "text", true);
				keyExe(map, "MA592142", "92142", MA592142, "text", true);
				keyExe(map, "MA592143", "92143", MA592143, "text", true);
				keyExe(map, "MA592021", "92021", MA592021, "text", true);
				keyExe(map, "MA592022", "92022", MA592022, "text", true);
				keyExe(map, "MA592023", "92023", MA592023, "text", true);
				keyExe(map, "MA592031", "92031", MA592031, "text", true);
				keyExe(map, "MA592032", "92032", MA592032, "text", true);
				keyExe(map, "MA592033", "92033", MA592033, "text", true);
				keyExe(map, "MA592041", "92041", MA592041, "text", true);
				keyExe(map, "MA592042", "92042", MA592042, "text", true);
				keyExe(map, "MA592043", "92043", MA592043, "text", true);
				keyExe(map, "MA592051", "92051", MA592051, "text", true);
				keyExe(map, "MA592052", "92052", MA592052, "text", true);
				keyExe(map, "MA592053", "92053", MA592053, "text", true);
				keyExe(map, "MA592151", "92151", MA592151, "text", true);
				keyExe(map, "MA592152", "92152", MA592152, "text", true);
				keyExe(map, "MA592153", "92153", MA592153, "text", true);
				keyExe(map, "MA592061", "92061", MA592061, "text", true);
				keyExe(map, "MA592062", "92062", MA592062, "text", true);
				keyExe(map, "MA592063", "92063", MA592063, "text", true);
				keyExe(map, "MA592071", "92071", MA592071, "text", true);
				keyExe(map, "MA592072", "92072", MA592072, "text", true);
				keyExe(map, "MA592073", "92073", MA592073, "text", true);
				keyExe(map, "MA592081", "92081", MA592081, "text", true);
				keyExe(map, "MA592082", "92082", MA592082, "text", true);
				keyExe(map, "MA592083", "92083", MA592083, "text", true);
				keyExe(map, "MA592091", "92091", MA592091, "text", true);
				keyExe(map, "MA592092", "92092", MA592092, "text", true);
				keyExe(map, "MA592093", "92093", MA592093, "text", true);
				keyExe(map, "MA592101", "92101", MA592101, "text", true);
				keyExe(map, "MA592102", "92102", MA592102, "text", true);
				keyExe(map, "MA592103", "92103", MA592103, "text", true);
				keyExe(map, "MA592111", "92111", MA592111, "text", true);
				keyExe(map, "MA592112", "92112", MA592112, "text", true);
				keyExe(map, "MA592113", "92113", MA592113, "text", true);
				keyExe(map, "MA592121", "92121", MA592121, "text", true);
				keyExe(map, "MA592122", "92122", MA592122, "text", true);
				keyExe(map, "MA592123", "92123", MA592123, "text", true);
				keyExe(map, "MA592131", "92131", MA592131, "text", true);
				keyExe(map, "MA592132", "92132", MA592132, "text", true);
				keyExe(map, "MA592133", "92133", MA592133, "text", true);
				
				keyExe(map, "MA5920019", "920019", MA5920019, "text", true);
				keyExe(map, "MA5920029", "920029", MA5920029, "text", true);
				keyExe(map, "MA5920039", "920039", MA5920039, "text", true);
				keyExe(map, "MA5920119", "920119", MA5920119, "text", true);
				keyExe(map, "MA5920129", "920129", MA5920129, "text", true);
				keyExe(map, "MA5920139", "920139", MA5920139, "text", true);			
				keyExe(map, "MA5921419", "921419", MA5921419, "text", true);
				keyExe(map, "MA5921429", "921429", MA5921429, "text", true);
				keyExe(map, "MA5921439", "921439", MA5921439, "text", true);
				keyExe(map, "MA5920219", "920219", MA5920219, "text", true);
				keyExe(map, "MA5920229", "920229", MA5920229, "text", true);
				keyExe(map, "MA5920239", "920239", MA5920239, "text", true);
				keyExe(map, "MA5920319", "920319", MA5920319, "text", true);
				keyExe(map, "MA5920329", "920329", MA5920329, "text", true);
				keyExe(map, "MA5920339", "920339", MA5920339, "text", true);
				keyExe(map, "MA5920419", "920419", MA5920419, "text", true);
				keyExe(map, "MA5920429", "920429", MA5920429, "text", true);
				keyExe(map, "MA5920439", "920439", MA5920439, "text", true);
				keyExe(map, "MA5920519", "920519", MA5920519, "text", true);
				keyExe(map, "MA5920529", "920529", MA5920529, "text", true);
				keyExe(map, "MA5920539", "920539", MA5920539, "text", true);
				keyExe(map, "MA5921519", "921519", MA5921519, "text", true);
				keyExe(map, "MA5921529", "921529", MA5921529, "text", true);
				keyExe(map, "MA5921539", "921539", MA5921539, "text", true);
				keyExe(map, "MA5920619", "920619", MA5920619, "text", true);
				keyExe(map, "MA5920629", "920629", MA5920629, "text", true);
				keyExe(map, "MA5920639", "920639", MA5920639, "text", true);
				keyExe(map, "MA5920719", "920719", MA5920719, "text", true);
				keyExe(map, "MA5920729", "920729", MA5920729, "text", true);
				keyExe(map, "MA5920739", "920739", MA5920739, "text", true);
				keyExe(map, "MA5920819", "920819", MA5920819, "text", true);
				keyExe(map, "MA5920829", "920829", MA5920829, "text", true);
				keyExe(map, "MA5920839", "920839", MA5920839, "text", true);
				keyExe(map, "MA5920919", "920919", MA5920919, "text", true);
				keyExe(map, "MA5920929", "920929", MA5920929, "text", true);
				keyExe(map, "MA5920939", "920939", MA5920939, "text", true);
				keyExe(map, "MA5921019", "921019", MA5921019, "text", true);
				keyExe(map, "MA5921029", "921029", MA5921029, "text", true);
				keyExe(map, "MA5921039", "921039", MA5921039, "text", true);
				keyExe(map, "MA5921119", "921119", MA5921119, "text", true);
				keyExe(map, "MA5921129", "921129", MA5921129, "text", true);
				keyExe(map, "MA5921139", "921139", MA5921139, "text", true);
				keyExe(map, "MA5921219", "921219", MA5921219, "text", true);
				keyExe(map, "MA5921229", "921229", MA5921229, "text", true);
				keyExe(map, "MA5921239", "921239", MA5921239, "text", true);
				keyExe(map, "MA5921319", "921319", MA5921319, "text", true);
				keyExe(map, "MA5921329", "921329", MA5921329, "text", true);
				keyExe(map, "MA5921339", "921339", MA5921339, "text", true);

				keyExe(map, "MA592200", "92200", MA592200, "text", true);
				keyExe(map, "MA592201", "92201", MA592201, "text", true);
				keyExe(map, "MA592202", "92202", MA592202, "text", true);
				keyExe(map, "MA592203", "92203", MA592203, "text", true);
				keyExe(map, "MA592204", "92204", MA592204, "text", true);
				keyExe(map, "MA592205", "92205", MA592205, "text", true);

			
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
						Double d2 = round(d, 2);
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
					if(!isNumeric(event.getCharCode()))
						event.preventDefault();
				}
			});
			
		}
	}
	
	public static boolean isNumeric(Character cchar) {
	    switch (cchar) {
	      case '1':
	      case '2':
	      case '3':
	      case '4':
	      case '5':
	      case '6':
	      case '7':
	      case '8':
	      case '9':
	      case '0':
	      case '.':
	      case ',':
	        return true;
	      default:
	        return false;
	    }
	  }
	public static Double round(Double value, Integer places) {
	    if (places < 0) throw new IllegalArgumentException();

	    Long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    Long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
