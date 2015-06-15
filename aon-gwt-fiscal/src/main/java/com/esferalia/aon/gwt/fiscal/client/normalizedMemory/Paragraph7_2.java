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

public class Paragraph7_2 extends ResizeComposite {
	
	
	@UiField TextBox MA794001;
	@UiField TextBox MA7940019;
	@UiField TextBox MA794002;
	@UiField TextBox MA7940029;
	@UiField TextBox MA794003;
	@UiField TextBox MA7940039;
	@UiField TextBox MA794004;
	@UiField TextBox MA7940049;
	
	@UiField TextBox MA794011;
	@UiField TextBox MA7940119;
	@UiField TextBox MA794012;
	@UiField TextBox MA7940129;
	@UiField TextBox MA794013;
	@UiField TextBox MA7940139;
	@UiField TextBox MA794014;
	@UiField TextBox MA7940149;
	
	@UiField TextBox MA794021;
	@UiField TextBox MA7940219;
	@UiField TextBox MA794022;
	@UiField TextBox MA7940229;
	@UiField TextBox MA794023;
	@UiField TextBox MA7940239;
	@UiField TextBox MA794024;
	@UiField TextBox MA7940249;
	
	@UiField TextBox MA794031;
	@UiField TextBox MA7940319;
	@UiField TextBox MA794032;
	@UiField TextBox MA7940329;
	@UiField TextBox MA794033;
	@UiField TextBox MA7940339;
	@UiField TextBox MA794034;
	@UiField TextBox MA7940349;
	
	@UiField TextBox MA794101;
	@UiField TextBox MA7941019;
	@UiField TextBox MA794102;
	@UiField TextBox MA7941029;
	@UiField TextBox MA794103;
	@UiField TextBox MA7941039;
	@UiField TextBox MA794104;
	@UiField TextBox MA7941049;
	
	@UiField TextBox MA794111;
	@UiField TextBox MA7941119;
	@UiField TextBox MA794112;
	@UiField TextBox MA7941129;
	@UiField TextBox MA794113;
	@UiField TextBox MA7941139;
	@UiField TextBox MA794114;
	@UiField TextBox MA7941149;
	
	@UiField TextBox MA794121;
	@UiField TextBox MA7941219;
	@UiField TextBox MA794122;
	@UiField TextBox MA7941229;
	@UiField TextBox MA794123;
	@UiField TextBox MA7941239;
	@UiField TextBox MA794124;
	@UiField TextBox MA7941249;
	
	@UiField TextBox MA794131;
	@UiField TextBox MA7941319;
	@UiField TextBox MA794132;
	@UiField TextBox MA7941329;
	@UiField TextBox MA794133;
	@UiField TextBox MA7941339;
	@UiField TextBox MA794134;
	@UiField TextBox MA7941349;
	
	@UiField TextBox MA794201;
	@UiField TextBox MA794202;
	@UiField TextBox MA794203;
	@UiField TextBox MA794204;
	@UiField TextBox MA794205;
	@UiField TextBox MA794206;
	@UiField TextBox MA794207;
	
	@UiField TextBox MA794211;
	@UiField TextBox MA794212;
	@UiField TextBox MA794213;
	@UiField TextBox MA794214;
	@UiField TextBox MA794215;
	@UiField TextBox MA794216;
	@UiField TextBox MA794217;
	
	@UiField TextBox MA794221;
	@UiField TextBox MA794222;
	@UiField TextBox MA794223;
	@UiField TextBox MA794224;
	@UiField TextBox MA794225;
	@UiField TextBox MA794226;
	@UiField TextBox MA794227;
	
	@UiField TextBox MA794231;
	@UiField TextBox MA794232;
	@UiField TextBox MA794233;
	@UiField TextBox MA794234;
	@UiField TextBox MA794235;
	@UiField TextBox MA794236;
	@UiField TextBox MA794237;
	
	@UiField TextBox MA794241;
	@UiField TextBox MA794242;
	@UiField TextBox MA794243;
	@UiField TextBox MA794244;
	@UiField TextBox MA794245;
	@UiField TextBox MA794246;
	@UiField TextBox MA794247;
	
	@UiField TextBox MA794251;
	@UiField TextBox MA794252;
	@UiField TextBox MA794253;
	@UiField TextBox MA794254;
	@UiField TextBox MA794255;
	@UiField TextBox MA794256;
	@UiField TextBox MA794257;
	
	@UiField TextBox MA794261;
	@UiField TextBox MA794262;
	@UiField TextBox MA794263;
	@UiField TextBox MA794264;
	@UiField TextBox MA794265;
	@UiField TextBox MA794266;
	@UiField TextBox MA794267;
	
	@UiField TextBox MA794271;
	@UiField TextBox MA794272;
	@UiField TextBox MA794273;
	@UiField TextBox MA794274;
	@UiField TextBox MA794275;
	@UiField TextBox MA794276;
	@UiField TextBox MA794277;
	
	@UiField TextBox MA794281;
	@UiField TextBox MA794282;
	@UiField TextBox MA794283;
	@UiField TextBox MA794284;
	@UiField TextBox MA794285;
	@UiField TextBox MA794286;
	@UiField TextBox MA794287;
	
	@UiField TextBox MA794291;
	@UiField TextBox MA794292;
	@UiField TextBox MA794293;
	@UiField TextBox MA794294;
	@UiField TextBox MA794295;
	@UiField TextBox MA794296;
	@UiField TextBox MA794297;
	
	@UiField TextBox MA794303;
	@UiField TextBox MA794311;
	@UiField TextBox MA794312;
	@UiField TextBox MA794313;
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	
	Map<String, String> map;
	
	interface Paragraph7_2Binder extends UiBinder<Widget, Paragraph7_2> {
	}

	
	Enterprise  enterprise;
	NormalizedMemory normalizedMemory;
	
	private static final Paragraph7_2Binder binder = GWT
			.create(Paragraph7_2Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Paragraph7_2() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	public Paragraph7_2(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		
		MA794001 = new TextBox();
		MA7940019 = new TextBox();
		MA7940029 = new TextBox();
		MA794003 = new TextBox();
		MA7940039 = new TextBox();
		MA794004 = new TextBox();
		MA7940049 = new TextBox();
		
		MA794011 = new TextBox();
		MA7940119 = new TextBox();
		MA794012 = new TextBox();
		MA7940129 = new TextBox();
		MA794013 = new TextBox();
		MA7940139 = new TextBox();
		MA794014 = new TextBox();
		MA7940149 = new TextBox();
		
		MA794021 = new TextBox();
		MA7940219 = new TextBox();
		MA794022 = new TextBox();
		MA7940229 = new TextBox();
		MA794023 = new TextBox();
		MA7940239 = new TextBox();
		MA794024 = new TextBox();
		MA7940249 = new TextBox();
		
		MA794031 = new TextBox();
		MA7940319 = new TextBox();
		MA794032 = new TextBox();
		MA7940329 = new TextBox();
		MA794033 = new TextBox();
		MA7940339 = new TextBox();
		MA794034 = new TextBox();
		MA7940349 = new TextBox();
		
		MA794101 = new TextBox();
		MA7941019 = new TextBox();
		MA794102 = new TextBox();
		MA7941029 = new TextBox();
		MA794103 = new TextBox();
		MA7941039 = new TextBox();
		MA794104 = new TextBox();
		MA7941049 = new TextBox();
		
		MA794111 = new TextBox();
		MA7941119 = new TextBox();
		MA794112 = new TextBox();
		MA7941129 = new TextBox();
		MA794113 = new TextBox();
		MA7941139 = new TextBox();
		MA794114 = new TextBox();
		MA7941149 = new TextBox();
		
		MA794121 = new TextBox();
		MA7941219 = new TextBox();
		MA794122 = new TextBox();
		MA7941229 = new TextBox();
		MA794123 = new TextBox();
		MA7941239 = new TextBox();
		MA794124 = new TextBox();
		MA7941249 = new TextBox();
		
		MA794131 = new TextBox();
		MA7941319 = new TextBox();
		MA794132 = new TextBox();
		MA7941329 = new TextBox();
		MA794133 = new TextBox();
		MA7941339 = new TextBox();
		MA794134 = new TextBox();
		MA7941349 = new TextBox();
		
		MA794201 = new TextBox();
		MA794202 = new TextBox();
		MA794203 = new TextBox();
		MA794204 = new TextBox();
		MA794205 = new TextBox();
		MA794206 = new TextBox();
		MA794207 = new TextBox();
		
		MA794211 = new TextBox();
		MA794212 = new TextBox();
		MA794213 = new TextBox();
		MA794214 = new TextBox();
		MA794215 = new TextBox();
		MA794216 = new TextBox();
		MA794217 = new TextBox();
		
		MA794221 = new TextBox();
		MA794222 = new TextBox();
		MA794223 = new TextBox();
		MA794224 = new TextBox();
		MA794225 = new TextBox();
		MA794226 = new TextBox();
		MA794227 = new TextBox();
		
		MA794231 = new TextBox();
		MA794232 = new TextBox();
		MA794233 = new TextBox();
		MA794234 = new TextBox();
		MA794235 = new TextBox();
		MA794236 = new TextBox();
		MA794237 = new TextBox();
		
		MA794241 = new TextBox();
		MA794242 = new TextBox();
		MA794243 = new TextBox();
		MA794244 = new TextBox();
		MA794245 = new TextBox();
		MA794246 = new TextBox();
		MA794247 = new TextBox();
		
		MA794251 = new TextBox();
		MA794252 = new TextBox();
		MA794253 = new TextBox();
		MA794254 = new TextBox();
		MA794255 = new TextBox();
		MA794256 = new TextBox();
		MA794257 = new TextBox();
		
		MA794261 = new TextBox();
		MA794262 = new TextBox();
		MA794263 = new TextBox();
		MA794264 = new TextBox();
		MA794265 = new TextBox();
		MA794266 = new TextBox();
		MA794267 = new TextBox();
		
		MA794271 = new TextBox();
		MA794272 = new TextBox();
		MA794273 = new TextBox();
		MA794274 = new TextBox();
		MA794275 = new TextBox();
		MA794276 = new TextBox();
		MA794277 = new TextBox();
		
		MA794281 = new TextBox();
		MA794282 = new TextBox();
		MA794283 = new TextBox();
		MA794284 = new TextBox();
		MA794285 = new TextBox();
		MA794286 = new TextBox();
		MA794287 = new TextBox();
		
		MA794291 = new TextBox();
		MA794292 = new TextBox();
		MA794293 = new TextBox();
		MA794294 = new TextBox();
		MA794295 = new TextBox();
		MA794296 = new TextBox();
		MA794297 = new TextBox();
		
		MA794303 = new TextBox();
		MA794311 = new TextBox();
		MA794312 = new TextBox();
		MA794313 = new TextBox();
		
		init();
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void init() {
		inma.getSchema("MA7",enterprise.getDomain(), new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				
				keyExe(map, "MA794001", "94001", MA794001, "text", true);
				keyExe(map, "MA7940019", "940019", MA7940019, "text", true);
				keyExe(map, "MA794002", "94002", MA794002, "text", true);
				keyExe(map, "MA7940029", "940029", MA7940029, "text", true);
				keyExe(map, "MA794003", "94003", MA794003, "text", true);
				keyExe(map, "MA7940039", "940039", MA7940039, "text", true);
				keyExe(map, "MA794004", "94004", MA794004, "text", true);
				keyExe(map, "MA7940049", "940049", MA7940049, "text", true);
				
				keyExe(map, "MA794011", "94011", MA794011, "text", true);
				keyExe(map, "MA7940119", "940119", MA7940119, "text", true);
				keyExe(map, "MA794012", "94012", MA794012, "text", true);
				keyExe(map, "MA7940129", "940129", MA7940129, "text", true);
				keyExe(map, "MA794013", "94013", MA794013, "text", true);
				keyExe(map, "MA7940139", "940139", MA7940139, "text", true);
				keyExe(map, "MA794014", "94014", MA794014, "text", true);
				keyExe(map, "MA7940149", "940149", MA7940149, "text", true);
				
				keyExe(map, "MA794021", "94021", MA794021, "text", true);
				keyExe(map, "MA7940219", "940219", MA7940219, "text", true);
				keyExe(map, "MA794022", "94022", MA794022, "text", true);
				keyExe(map, "MA7940229", "940229", MA7940229, "text", true);
				keyExe(map, "MA794023", "94023", MA794023, "text", true);
				keyExe(map, "MA7940239", "940239", MA7940239, "text", true);
				keyExe(map, "MA794024", "94024", MA794024, "text", true);
				keyExe(map, "MA7940249", "940249", MA7940249, "text", true);
			
				keyExe(map, "MA794031", "94031", MA794031, "text", true);
				keyExe(map, "MA7940319", "940319", MA7940319, "text", true);
				keyExe(map, "MA794032", "94032", MA794032, "text", true);
				keyExe(map, "MA7940329", "940329", MA7940329, "text", true);
				keyExe(map, "MA794033", "94033", MA794033, "text", true);
				keyExe(map, "MA7940339", "940339", MA7940339, "text", true);
				keyExe(map, "MA794034", "94034", MA794034, "text", true);
				keyExe(map, "MA7940349", "940349", MA7940349, "text", true);
				
				
				keyExe(map, "MA794101", "94101", MA794101, "text", true);
				keyExe(map, "MA7941019", "941019", MA7941019, "text", true);
				keyExe(map, "MA794102", "94102", MA794102, "text", true);
				keyExe(map, "MA7941029", "941029", MA7941029, "text", true);
				keyExe(map, "MA794103", "94103", MA794103, "text", true);
				keyExe(map, "MA7941039", "941039", MA7941039, "text", true);
				keyExe(map, "MA794104", "94104", MA794104, "text", true);
				keyExe(map, "MA7941049", "941049", MA7941049, "text", true);
				
				keyExe(map, "MA794111", "94111", MA794111, "text", true);
				keyExe(map, "MA7941119", "941119", MA7941119, "text", true);
				keyExe(map, "MA794112", "94112", MA794112, "text", true);
				keyExe(map, "MA7941129", "941129", MA7941129, "text", true);
				keyExe(map, "MA794113", "94113", MA794113, "text", true);
				keyExe(map, "MA7941139", "941139", MA7941139, "text", true);
				keyExe(map, "MA794114", "94114", MA794114, "text", true);
				keyExe(map, "MA7941149", "941149", MA7941149, "text", true);
				
				keyExe(map, "MA794121", "94121", MA794121, "text", true);
				keyExe(map, "MA7941219", "941219", MA7941219, "text", true);
				keyExe(map, "MA794122", "94122", MA794122, "text", true);
				keyExe(map, "MA7941229", "941229", MA7941229, "text", true);
				keyExe(map, "MA794123", "94123", MA794123, "text", true);
				keyExe(map, "MA7941239", "941239", MA7941239, "text", true);
				keyExe(map, "MA794124", "94124", MA794124, "text", true);
				keyExe(map, "MA7941249", "941249", MA7941249, "text", true);
			
				keyExe(map, "MA794131", "94131", MA794131, "text", true);
				keyExe(map, "MA7941319", "941319", MA7941319, "text", true);
				keyExe(map, "MA794132", "94132", MA794132, "text", true);
				keyExe(map, "MA7941329", "941329", MA7941329, "text", true);
				keyExe(map, "MA794133", "94133", MA794133, "text", true);
				keyExe(map, "MA7941339", "941339", MA7941339, "text", true);
				keyExe(map, "MA794134", "94134", MA794134, "text", true);
				keyExe(map, "MA7941349", "941349", MA7941349, "text", true);
				
				
				keyExe(map, "MA794201", "94201", MA794201, "text", true);
				keyExe(map, "MA794202", "94202", MA794202, "text", true);
				keyExe(map, "MA794203", "94203", MA794203, "text", true);
				keyExe(map, "MA794204", "94204", MA794204, "text", true);
				keyExe(map, "MA794205", "94205", MA794205, "text", true);
				keyExe(map, "MA794206", "94206", MA794206, "text", true);
				keyExe(map, "MA794207", "94207", MA794207, "text", true);
				
				keyExe(map, "MA794211", "94211", MA794211, "text", true);
				keyExe(map, "MA794212", "94212", MA794212, "text", true);
				keyExe(map, "MA794213", "94213", MA794213, "text", true);
				keyExe(map, "MA794214", "94214", MA794214, "text", true);
				keyExe(map, "MA794215", "94215", MA794215, "text", true);
				keyExe(map, "MA794216", "94216", MA794216, "text", true);
				keyExe(map, "MA794217", "94217", MA794217, "text", true);
			
				keyExe(map, "MA794221", "94221", MA794221, "text", true);
				keyExe(map, "MA794222", "94222", MA794222, "text", true);
				keyExe(map, "MA794223", "94223", MA794223, "text", true);
				keyExe(map, "MA794224", "94224", MA794224, "text", true);
				keyExe(map, "MA794225", "94225", MA794225, "text", true);
				keyExe(map, "MA794226", "94226", MA794226, "text", true);
				keyExe(map, "MA794227", "94227", MA794227, "text", true);
				
				keyExe(map, "MA794231", "94231", MA794231, "text", true);
				keyExe(map, "MA794232", "94232", MA794232, "text", true);
				keyExe(map, "MA794233", "94233", MA794233, "text", true);
				keyExe(map, "MA794234", "94234", MA794234, "text", true);
				keyExe(map, "MA794235", "94235", MA794235, "text", true);
				keyExe(map, "MA794236", "94236", MA794236, "text", true);
				keyExe(map, "MA794237", "94237", MA794237, "text", true);
		
				keyExe(map, "MA794241", "94241", MA794241, "text", true);
				keyExe(map, "MA794242", "94242", MA794242, "text", true);
				keyExe(map, "MA794243", "94243", MA794243, "text", true);
				keyExe(map, "MA794244", "94244", MA794244, "text", true);
				keyExe(map, "MA794245", "94245", MA794245, "text", true);
				keyExe(map, "MA794246", "94246", MA794246, "text", true);
				keyExe(map, "MA794247", "94247", MA794247, "text", true);
		
				keyExe(map, "MA794251", "94251", MA794251, "text", true);
				keyExe(map, "MA794252", "94252", MA794252, "text", true);
				keyExe(map, "MA794253", "94253", MA794253, "text", true);
				keyExe(map, "MA794254", "94254", MA794254, "text", true);
				keyExe(map, "MA794255", "94255", MA794255, "text", true);
				keyExe(map, "MA794256", "94256", MA794256, "text", true);
				keyExe(map, "MA794257", "94257", MA794257, "text", true);
			
				keyExe(map, "MA794261", "94261", MA794261, "text", true);
				keyExe(map, "MA794262", "94262", MA794262, "text", true);
				keyExe(map, "MA794263", "94263", MA794263, "text", true);
				keyExe(map, "MA794264", "94264", MA794264, "text", true);
				keyExe(map, "MA794265", "94265", MA794265, "text", true);
				keyExe(map, "MA794266", "94266", MA794266, "text", true);
				keyExe(map, "MA794267", "94267", MA794267, "text", true);
				
				keyExe(map, "MA794271", "94271", MA794271, "text", true);
				keyExe(map, "MA794272", "94272", MA794272, "text", true);
				keyExe(map, "MA794273", "94273", MA794273, "text", true);
				keyExe(map, "MA794274", "94274", MA794274, "text", true);
				keyExe(map, "MA794275", "94275", MA794275, "text", true);
				keyExe(map, "MA794276", "94276", MA794276, "text", true);
				keyExe(map, "MA794277", "94277", MA794277, "text", true);
				
				keyExe(map, "MA794281", "94281", MA794281, "text", true);
				keyExe(map, "MA794282", "94282", MA794282, "text", true);
				keyExe(map, "MA794283", "94283", MA794283, "text", true);
				keyExe(map, "MA794284", "94284", MA794284, "text", true);
				keyExe(map, "MA794285", "94285", MA794285, "text", true);
				keyExe(map, "MA794286", "94286", MA794286, "text", true);
				keyExe(map, "MA794287", "94287", MA794287, "text", true);
			
				keyExe(map, "MA794291", "94291", MA794291, "text", true);
				keyExe(map, "MA794292", "94292", MA794292, "text", true);
				keyExe(map, "MA794293", "94293", MA794293, "text", true);
				keyExe(map, "MA794294", "94294", MA794294, "text", true);
				keyExe(map, "MA794295", "94295", MA794295, "text", true);
				keyExe(map, "MA794296", "94296", MA794296, "text", true);
				keyExe(map, "MA794297", "94297", MA794297, "text", true);
		
				keyExe(map, "MA794303", "94303", MA794303, "text", true);
				keyExe(map, "MA794311", "94311", MA794311, "text", true);
				keyExe(map, "MA794312", "94312", MA794312, "text", true);
				keyExe(map, "MA794313", "94313", MA794313, "text", true);
				

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
