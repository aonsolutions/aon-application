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

public class Paragraph6_2 extends ResizeComposite {
	@UiField TextBox MA6193001;  
	@UiField TextBox MA61930019;  
	@UiField TextBox MA6193002;  
	@UiField TextBox MA61930029;  
	@UiField TextBox MA6193003;  
	@UiField TextBox MA61930039;  
	@UiField TextBox MA6193004;  
	@UiField TextBox MA61930049;  
	
	@UiField TextBox MA6193011;  
	@UiField TextBox MA61930119;  
	@UiField TextBox MA6193012;  
	@UiField TextBox MA61930129;  
	@UiField TextBox MA6193013;  
	@UiField TextBox MA61930139;  
	@UiField TextBox MA6193014;  
	@UiField TextBox MA61930149;  
	
	@UiField TextBox MA6193021;  
	@UiField TextBox MA61930219;  
	@UiField TextBox MA6193022;  
	@UiField TextBox MA61930229;  
	@UiField TextBox MA6193023;  
	@UiField TextBox MA61930239;  

	@UiField TextBox MA6193024;  
	@UiField TextBox MA61930249;  
	
	@UiField TextBox MA6193031;  
	@UiField TextBox MA61930319;  
	@UiField TextBox MA6193032;  
	@UiField TextBox MA61930329;  
	@UiField TextBox MA6193033;  
	@UiField TextBox MA61930339;  
	@UiField TextBox MA6193034;  
	@UiField TextBox MA61930349;  
	
	@UiField TextBox MA6193041;  
	@UiField TextBox MA61930419;  
	@UiField TextBox MA6193042;  
	@UiField TextBox MA61930429;  
	@UiField TextBox MA6193043;  
	@UiField TextBox MA61930439;  
	@UiField TextBox MA6193044;  
	@UiField TextBox MA61930449;  

	@UiField TextBox MA6193051;  
	@UiField TextBox MA61930519;  
	@UiField TextBox MA6193052;  
	@UiField TextBox MA61930529;  
	@UiField TextBox MA6193053;  
	@UiField TextBox MA61930539;  
	@UiField TextBox MA6193054;  
	@UiField TextBox MA61930549;  

	@UiField TextBox MA6193101;  
	@UiField TextBox MA61931019;  
	@UiField TextBox MA6193102;  
	@UiField TextBox MA61931029;  
	@UiField TextBox MA6193103;  
	@UiField TextBox MA61931039;  
	@UiField TextBox MA6193104;  
	@UiField TextBox MA61931049;  

	@UiField TextBox MA6193111;  
	@UiField TextBox MA61931119;  
	@UiField TextBox MA6193112;  
	@UiField TextBox MA61931129;  
	@UiField TextBox MA6193113;  
	@UiField TextBox MA61931139;  
	@UiField TextBox MA6193114;  
	@UiField TextBox MA61931149;  
	
	@UiField TextBox MA6193121;  
	@UiField TextBox MA61931219; 
	@UiField TextBox MA6193122; 
	@UiField TextBox MA61931229; 
	@UiField TextBox MA6193123; 
	@UiField TextBox MA61931239; 
	@UiField TextBox MA6193124;  
	@UiField TextBox MA61931249; 

	@UiField TextBox MA6193131;  
	@UiField TextBox MA61931319; 
	@UiField TextBox MA6193132;  
	@UiField TextBox MA61931329;  
	@UiField TextBox MA6193133;  
	@UiField TextBox MA61931339; 
	@UiField TextBox MA6193134;  
	@UiField TextBox MA61931349; 
	@UiField TextBox MA6193141;  
	@UiField TextBox MA61931419;  
	@UiField TextBox MA6193142;  
	@UiField TextBox MA61931429; 
	@UiField TextBox MA6193143;  
	@UiField TextBox MA61931439;  
	@UiField TextBox MA6193144; 
	@UiField TextBox MA61931449;  

	@UiField TextBox MA6193151;  
	@UiField TextBox MA61931519; 
	@UiField TextBox MA6193152;  
	@UiField TextBox MA61931529; 
	@UiField TextBox MA6193153;  
	@UiField TextBox MA61931539; 
	@UiField TextBox MA6193154;  
	@UiField TextBox MA61931549; 

	@UiField TextBox MA6193203;  
	@UiField TextBox MA61932039;  

	@UiField TextBox MA6193212; 
	@UiField TextBox MA61932129;  
	

	@UiField TextBox MA6193222;  
	@UiField TextBox MA61932229;  
	
	
	@UiField TextBox MA6193233; 
	@UiField TextBox MA61932339;  
	
	@UiField TextBox MA6193241; 
	@UiField TextBox MA61932419; 
	@UiField TextBox MA6193242; 
	@UiField TextBox MA61932429; 
	
	// MEMORIA - AP6.2 CUADROS NORMALIZADOS
	@UiField TextBox MA6293301; 
	@UiField TextBox MA6293302; 
	@UiField TextBox MA6293303; 
	@UiField TextBox MA6293304;  
	@UiField TextBox MA6293305;  
	@UiField TextBox MA6293306;  
	
	@UiField TextBox MA6293311;
	@UiField TextBox MA6293312;
	@UiField TextBox MA6293313;
	@UiField TextBox MA6293314;
	@UiField TextBox MA6293315; 
	@UiField TextBox MA6293316; 

	@UiField TextBox MA6293321; 
	@UiField TextBox MA6293322; 
	@UiField TextBox MA6293323; 
	@UiField TextBox MA6293324; 
	@UiField TextBox MA6293325;  
	@UiField TextBox MA6293326;  

	@UiField TextBox MA6293331;  
	@UiField TextBox MA6293332;  
	@UiField TextBox MA6293333;  
	@UiField TextBox MA6293334;  
	@UiField TextBox MA6293335;  
	@UiField TextBox MA6293336;  
	
	@UiField TextBox MA6293341; 
	@UiField TextBox MA6293342;  
	@UiField TextBox MA6293343; 
	@UiField TextBox MA6293344;  
	@UiField TextBox MA6293345; 
	@UiField TextBox MA6293346; 
	
	@UiField TextBox MA6293351; 
	@UiField TextBox MA6293352;  
	@UiField TextBox MA6293353;  
	@UiField TextBox MA6293354;  
	@UiField TextBox MA6293355; 
	@UiField TextBox MA6293356;  
	
	@UiField TextBox MA62933119;  
	@UiField TextBox MA62933129;  
	@UiField TextBox MA62933139;  
	@UiField TextBox MA62933149;  
	@UiField TextBox MA62933159; 
	@UiField TextBox MA62933169; 

	@UiField TextBox MA62933219; 
	@UiField TextBox MA62933229;  
	@UiField TextBox MA62933239;  
	@UiField TextBox MA62933249;  
	@UiField TextBox MA62933259; 
	@UiField TextBox MA62933269;  

	@UiField TextBox MA62933319; 
	@UiField TextBox MA62933329;  
	@UiField TextBox MA62933339;  
	@UiField TextBox MA62933349;  
	@UiField TextBox MA62933359; 
	@UiField TextBox MA62933369;  

	@UiField TextBox MA62933419;  
	@UiField TextBox MA62933429;  
	@UiField TextBox MA62933439;  
	@UiField TextBox MA62933449;  
	@UiField TextBox MA62933459;  
	@UiField TextBox MA62933469;  

	@UiField TextBox MA62933519;  
	@UiField TextBox MA62933529;  
	@UiField TextBox MA62933539; 
	@UiField TextBox MA62933549; 
	@UiField TextBox MA62933559; 
	@UiField TextBox MA62933569; 
	
	@UiField TextBox MA6293401; 
	@UiField TextBox MA6293402; 
	@UiField TextBox MA6293403;  
	@UiField TextBox MA6293404;  
	
	@UiField TextBox MA6293411;  
	@UiField TextBox MA6293412; 
	@UiField TextBox MA6293413;  
	@UiField TextBox MA6293414; 
	
	@UiField TextBox MA6293421; 
	@UiField TextBox MA6293422; 
	@UiField TextBox MA6293423; 
	@UiField TextBox MA6293424; 
	
	@UiField TextBox MA6293431; 
	@UiField TextBox MA6293432;
	@UiField TextBox MA6293433; 
	@UiField TextBox MA6293434; 
	
	@UiField TextBox MA62934119;  
	@UiField TextBox MA62934129; 
	@UiField TextBox MA62934139; 
	@UiField TextBox MA62934149;  

	@UiField TextBox MA62934219;  
	@UiField TextBox MA62934229;  
	@UiField TextBox MA62934239;  
	@UiField TextBox MA62934249; 

	@UiField TextBox MA62934319; 
	@UiField TextBox MA62934329; 
	@UiField TextBox MA62934339;  
	@UiField TextBox MA62934349; 
	
	@UiField TextBox MA62935059; 
	@UiField TextBox MA6293501;  
	@UiField TextBox MA6293502;  
	@UiField TextBox MA6293503; 
	@UiField TextBox MA6293504;  
	@UiField TextBox MA6293505;  
	
	@UiField TextBox MA62935159; 
	@UiField TextBox MA6293511; 
	@UiField TextBox MA6293512;  
	@UiField TextBox MA6293513;  
	@UiField TextBox MA6293514;  
	@UiField TextBox MA6293515; 

	@UiField TextBox MA62935259;  
	@UiField TextBox MA6293521;  
	@UiField TextBox MA6293522;  
	@UiField TextBox MA6293523;  
	@UiField TextBox MA6293524; 
	@UiField TextBox MA6293525; 
	
	@UiField TextBox MA62935359;  
	@UiField TextBox MA6293531;  
	@UiField TextBox MA6293532;  
	@UiField TextBox MA6293533; 
	@UiField TextBox MA6293534;  
	@UiField TextBox MA6293535; 
	
	
	
	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	
	Map<String, String> map;
	
	interface Paragraph6_2Binder extends UiBinder<Widget, Paragraph6_2> {
	}

	private static final Paragraph6_2Binder binder = GWT
			.create(Paragraph6_2Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	Enterprise enterprise;
	NormalizedMemory normalizedMemory;
	
	public Paragraph6_2() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}

	public Paragraph6_2(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		
		MA6193001 = new TextBox();  
		MA61930019 = new TextBox();  
		MA6193002 = new TextBox();  
		MA61930029 = new TextBox();  
		MA6193003 = new TextBox();  
		MA61930039 = new TextBox();  
		MA6193004 = new TextBox();  
		MA61930049 = new TextBox();  
		
		MA6193011 = new TextBox();  
		MA61930119 = new TextBox();  
		MA6193012 = new TextBox();  
		MA61930129 = new TextBox();  
		MA6193013 = new TextBox();  
		MA61930139 = new TextBox();  
		MA6193014 = new TextBox();  
		MA61930149 = new TextBox();  
		
		MA6193021 = new TextBox();  
		MA61930219 = new TextBox();  
		MA6193022 = new TextBox();  
		MA61930229 = new TextBox();  
		MA6193023 = new TextBox();  
		MA61930239 = new TextBox();  
	
		MA6193024 = new TextBox();  
		MA61930249 = new TextBox();  
		
		MA6193031 = new TextBox();  
		MA61930319 = new TextBox();  
		MA6193032 = new TextBox();  
		MA61930329 = new TextBox();  
		MA6193033 = new TextBox();  
		MA61930339 = new TextBox();  
		MA6193034 = new TextBox();  
		MA61930349 = new TextBox();  
		
		MA6193041 = new TextBox();  
		MA61930419 = new TextBox();  
		MA6193042 = new TextBox();  
		MA61930429 = new TextBox();  
		MA6193043 = new TextBox();  
		MA61930439 = new TextBox();  
		MA6193044 = new TextBox();  
		MA61930449 = new TextBox();  
	
		MA6193051 = new TextBox();  
		MA61930519 = new TextBox();  
		MA6193052 = new TextBox();  
		MA61930529 = new TextBox();  
		MA6193053 = new TextBox();  
		MA61930539 = new TextBox();  
		MA6193054 = new TextBox();  
		MA61930549 = new TextBox();  
	
		MA6193101 = new TextBox();  
		MA61931019 = new TextBox();  
		MA6193102 = new TextBox();  
		MA61931029 = new TextBox();  
		MA6193103 = new TextBox();  
		MA61931039 = new TextBox();  
		MA6193104 = new TextBox();  
		MA61931049 = new TextBox();  
	
		MA6193111 = new TextBox();  
		MA61931119 = new TextBox();  
		MA6193112 = new TextBox();  
		MA61931129 = new TextBox();  
		MA6193113 = new TextBox();  
		MA61931139 = new TextBox();  
		MA6193114 = new TextBox();  
		MA61931149 = new TextBox();  
		
		MA6193121 = new TextBox();  
		MA61931219 = new TextBox(); 
		MA6193122 = new TextBox(); 
		MA61931229 = new TextBox(); 
		MA6193123 = new TextBox(); 
		MA61931239 = new TextBox(); 
		MA6193124 = new TextBox();  
		MA61931249 = new TextBox(); 

		MA6193131 = new TextBox();  
		MA61931319 = new TextBox(); 
		MA6193132 = new TextBox();  
		MA61931329 = new TextBox();  
		MA6193133 = new TextBox();  
		MA61931339 = new TextBox(); 
		MA6193134 = new TextBox();  
		MA61931349 = new TextBox(); 
		MA6193141 = new TextBox();  
		MA61931419 = new TextBox();  
		MA6193142 = new TextBox();  
		MA61931429 = new TextBox(); 
		MA6193143 = new TextBox();  
		MA61931439 = new TextBox();  
		MA6193144 = new TextBox(); 
		MA61931449 = new TextBox();  
	
		MA6193151 = new TextBox();  
		MA61931519 = new TextBox(); 
		MA6193152 = new TextBox();  
		MA61931529 = new TextBox(); 
		MA6193153 = new TextBox();  
		MA61931539 = new TextBox(); 
		MA6193154 = new TextBox();  
		MA61931549 = new TextBox(); 

		MA6193203 = new TextBox();  
		MA61932039 = new TextBox();  

		MA6193212 = new TextBox(); 
		MA61932129 = new TextBox();  
		
	
		MA6193222 = new TextBox();  
		MA61932229 = new TextBox();  
		
		
		MA6193233 = new TextBox(); 
		MA61932339 = new TextBox();  
		
		MA6193241 = new TextBox(); 
		MA61932419 = new TextBox(); 
		MA6193242 = new TextBox(); 
		MA61932429 = new TextBox(); 
		
		// MEMORIA - AP6.2 CUADROS NORMALIZADOS
		MA6293301 = new TextBox(); 
		MA6293302 = new TextBox(); 
		MA6293303 = new TextBox(); 
		MA6293304 = new TextBox();  
		MA6293305 = new TextBox();  
		MA6293306 = new TextBox();  
		
		MA6293311 = new TextBox();
		MA6293312 = new TextBox();
		MA6293313 = new TextBox();
		MA6293314 = new TextBox();
		MA6293315 = new TextBox(); 
		MA6293316 = new TextBox(); 
	
		MA6293321 = new TextBox(); 
		MA6293322 = new TextBox(); 
		MA6293323 = new TextBox(); 
		MA6293324 = new TextBox(); 
		MA6293325 = new TextBox();  
		MA6293326 = new TextBox();  

		MA6293331 = new TextBox();  
		MA6293332 = new TextBox();  
		MA6293333 = new TextBox();  
		MA6293334 = new TextBox();  
		MA6293335 = new TextBox();  
		MA6293336 = new TextBox();  
		
		MA6293341 = new TextBox(); 
		MA6293342 = new TextBox();  
		MA6293343 = new TextBox(); 
		MA6293344 = new TextBox();  
		MA6293345 = new TextBox(); 
		MA6293346 = new TextBox(); 
		
		MA6293351 = new TextBox(); 
		MA6293352 = new TextBox();  
		MA6293353 = new TextBox();  
		MA6293354 = new TextBox();  
		MA6293355 = new TextBox(); 
		MA6293356 = new TextBox();  
		
		MA62933119 = new TextBox();  
		MA62933129 = new TextBox();  
		MA62933139 = new TextBox();  
		MA62933149 = new TextBox();  
		MA62933159 = new TextBox(); 
		MA62933169 = new TextBox(); 

		MA62933219 = new TextBox(); 
		MA62933229 = new TextBox();  
		MA62933239 = new TextBox();  
		MA62933249 = new TextBox();  
		MA62933259 = new TextBox(); 
		MA62933269 = new TextBox();  

		MA62933319 = new TextBox(); 
		MA62933329 = new TextBox();  
		MA62933339 = new TextBox();  
		MA62933349 = new TextBox();  
		MA62933359 = new TextBox(); 
		MA62933369 = new TextBox();  
	
		MA62933419 = new TextBox();  
		MA62933429 = new TextBox();  
		MA62933439 = new TextBox();  
		MA62933449 = new TextBox();  
		MA62933459 = new TextBox();  
		MA62933469 = new TextBox();  
	
		MA62933519 = new TextBox();  
		MA62933529 = new TextBox();  
		MA62933539 = new TextBox(); 
		MA62933549 = new TextBox(); 
		MA62933559 = new TextBox(); 
		MA62933569 = new TextBox(); 
		
		MA6293401 = new TextBox(); 
		MA6293402 = new TextBox(); 
		MA6293403 = new TextBox();  
		MA6293404 = new TextBox();  
		
		MA6293411 = new TextBox();  
		MA6293412 = new TextBox(); 
		MA6293413 = new TextBox();  
		MA6293414 = new TextBox(); 
		
		MA6293421 = new TextBox(); 
		MA6293422 = new TextBox(); 
		MA6293423 = new TextBox(); 
		MA6293424 = new TextBox(); 
		
		MA6293431 = new TextBox(); 
		MA6293432 = new TextBox();
		MA6293433 = new TextBox(); 
		MA6293434 = new TextBox(); 
		
		MA62934119 = new TextBox();  
		MA62934129 = new TextBox(); 
		MA62934139 = new TextBox(); 
		MA62934149 = new TextBox();  
	
		MA62934219 = new TextBox();  
		MA62934229 = new TextBox();  
		MA62934239 = new TextBox();  
		MA62934249 = new TextBox(); 
	
		MA62934319 = new TextBox(); 
		MA62934329 = new TextBox(); 
		MA62934339 = new TextBox();  
		MA62934349 = new TextBox(); 
		
		MA62935059 = new TextBox(); 
		MA6293501 = new TextBox();  
		MA6293502 = new TextBox();  
		MA6293503 = new TextBox(); 
		MA6293504 = new TextBox();  
		MA6293505 = new TextBox();  
		
		MA62935159 = new TextBox(); 
		MA6293511 = new TextBox(); 
		MA6293512 = new TextBox();  
		MA6293513 = new TextBox();  
		MA6293514 = new TextBox();  
		MA6293515 = new TextBox(); 
	
		MA62935259 = new TextBox();  
		MA6293521 = new TextBox();  
		MA6293522 = new TextBox();  
		MA6293523 = new TextBox();  
		MA6293524 = new TextBox(); 
		MA6293525 = new TextBox(); 
		
		MA62935359 = new TextBox();  
		MA6293531 = new TextBox();  
		MA6293532 = new TextBox();  
		MA6293533 = new TextBox(); 
		MA6293534 = new TextBox();  
		MA6293535 = new TextBox(); 
		
		init();
		
		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public void init() {
		inma.getSchema("MA6",enterprise.getDomain(), new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				map = result;
				keyExe(map,"MA6193001", "93001", MA6193001, "text", true);
				keyExe(map,"MA61930019", "930019", MA61930019, "text", true);
				keyExe(map,"MA6193002", "93002", MA6193002, "text", true);
				keyExe(map,"MA61930029", "930029", MA61930029, "text", true);
				keyExe(map,"MA6193003", "93003", MA6193003, "text", true);
				keyExe(map,"MA61930039", "930039", MA61930039, "text", true);
				keyExe(map,"MA6193004", "93004", MA6193004, "text", true);
				keyExe(map,"MA61930049", "930049", MA61930049, "text", true);
				keyExe(map,"MA6193011", "93011", MA6193011, "text", true);
				keyExe(map,"MA61930119", "930119", MA61930119, "text", true);
				keyExe(map,"MA6193012", "93012", MA6193012, "text", true);
				keyExe(map,"MA61930129", "930129", MA61930129, "text", true);
				keyExe(map,"MA6193013", "93013", MA6193013, "text", true);
				keyExe(map,"MA61930139", "930139", MA61930139, "text", true);
				keyExe(map,"MA6193014", "93014", MA6193014, "text", true);
				keyExe(map,"MA61930149", "930149", MA61930149, "text", true);
				keyExe(map,"MA6193021", "93021", MA6193021, "text", true);
				keyExe(map,"MA61930219", "930219", MA61930219, "text", true);
				keyExe(map,"MA6193022", "93022", MA6193022, "text", true);
				keyExe(map,"MA61930229", "930229", MA61930229, "text", true);
				keyExe(map,"MA6193023", "93023", MA6193023, "text", true);
				keyExe(map,"MA61930239", "930239", MA61930239, "text", true);
				keyExe(map,"MA6193024", "93024", MA6193024, "text", true);
				keyExe(map,"MA6193024", "93024", MA6193024, "text", true);
				keyExe(map,"MA6193031", "93031", MA6193031, "text", true);
				keyExe(map,"MA61930319", "930319", MA61930319, "text", true);
				keyExe(map,"MA6193032", "93032", MA6193032, "text", true);
				keyExe(map,"MA61930329", "930329", MA61930329, "text", true);
				keyExe(map,"MA6193033", "93033", MA6193033, "text", true);
				keyExe(map,"MA61930339", "930339", MA61930339, "text", true);
				keyExe(map,"MA6193034", "93034", MA6193034, "text", true);
				keyExe(map,"MA61930349", "930349", MA61930349, "text", true);
				keyExe(map,"MA6193041", "93041", MA6193041, "text", true);
				keyExe(map,"MA61930419", "930419", MA61930419, "text", true);
				keyExe(map,"MA6193042", "93042", MA6193042, "text", true);
				keyExe(map,"MA61930429", "930429", MA61930429, "text", true);
				keyExe(map,"MA6193043", "93043", MA6193043, "text", true);
				keyExe(map,"MA61930439", "930439", MA61930439, "text", true);
				keyExe(map,"MA6193044", "93044", MA6193044, "text", true);
				keyExe(map,"MA61930449", "930449", MA61930449, "text", true);
				keyExe(map,"MA6193051", "93051", MA6193051, "text", true);
				keyExe(map,"MA61930519", "930519", MA61930519, "text", true);
				keyExe(map,"MA6193052", "93052", MA6193052, "text", true);
				keyExe(map,"MA61930529", "930529", MA61930529, "text", true);
				keyExe(map,"MA6193053", "93053", MA6193053, "text", true);
				keyExe(map,"MA61930539", "930539", MA61930539, "text", true);
				keyExe(map,"MA6193054", "93054", MA6193054, "text", true);
				keyExe(map,"MA61930549", "930549", MA61930549, "text", true);
				keyExe(map,"MA6193101", "93101", MA6193101, "text", true);
				keyExe(map,"MA61931019", "931019", MA61931019, "text", true);
				keyExe(map,"MA6193102", "93102", MA6193102, "text", true);
				keyExe(map,"MA61931029", "931029", MA61931029, "text", true);
				keyExe(map,"MA6193103", "93103", MA6193103, "text", true);
				keyExe(map,"MA61931039", "931039", MA61931039, "text", true);
				keyExe(map,"MA6193104", "93104", MA6193104, "text", true);
				keyExe(map,"MA61931049", "931049", MA61931049, "text", true);
				keyExe(map,"MA6193111", "93111", MA6193111, "text", true);
				keyExe(map,"MA61931119", "931119", MA61931119, "text", true);
				keyExe(map,"MA6193112", "93112", MA6193112, "text", true);
				keyExe(map,"MA61931129", "931129", MA61931129, "text", true);
				keyExe(map,"MA6193113", "93113", MA6193113, "text", true);
				keyExe(map,"MA61931139", "931139", MA61931139, "text", true);
				keyExe(map,"MA6193114", "93114", MA6193114, "text", true);
				keyExe(map,"MA61931149", "931149", MA61931149, "text", true);
				keyExe(map,"MA6193121", "93121", MA6193121, "text", true);
				keyExe(map,"MA61931219", "931219", MA61931219, "text", true);
				keyExe(map,"MA6193122", "93122", MA6193122, "text", true);
				keyExe(map,"MA61931229", "931229", MA61931229, "text", true);
				keyExe(map,"MA6193123", "93123", MA6193123, "text", true);
				keyExe(map,"MA61931239", "931239", MA61931239, "text", true);
				keyExe(map,"MA6193124", "93124", MA6193124, "text", true);
				keyExe(map,"MA61931249", "931249", MA61931249, "text", true);
				keyExe(map,"MA6193131", "93131", MA6193131, "text", true);
				keyExe(map,"MA61931319", "931319", MA61931319, "text", true);
				keyExe(map,"MA6193132", "93132", MA6193132, "text", true);
				keyExe(map,"MA61931329", "931329", MA61931329, "text", true);
				keyExe(map,"MA6193133", "93133", MA6193133, "text", true);
				keyExe(map,"MA61931339", "931339", MA61931339, "text", true);
				keyExe(map,"MA6193134", "93134", MA6193134, "text", true);
				keyExe(map,"MA61931349", "931349", MA61931349, "text", true);
				keyExe(map,"MA6193141", "93141", MA6193141, "text", true);
				keyExe(map,"MA61931419", "931419", MA61931419, "text", true);
				keyExe(map,"MA6193142", "93142", MA6193142, "text", true);
				keyExe(map,"MA61931429", "931429", MA61931429, "text", true);
				keyExe(map,"MA6193143", "93143", MA6193143, "text", true);
				keyExe(map,"MA61931439", "931439", MA61931439, "text", true);
				keyExe(map,"MA6193144", "93144", MA6193144, "text", true);
				keyExe(map,"MA61931449", "931449", MA61931449, "text", true);
				keyExe(map,"MA6193151", "93151", MA6193151, "text", true);
				keyExe(map,"MA61931519", "931519", MA61931519, "text", true);
				keyExe(map,"MA6193152", "93152", MA6193152, "text", true);
				keyExe(map,"MA61931529", "931529", MA61931529, "text", true);
				keyExe(map,"MA6193153", "93153", MA6193153, "text", true);
				keyExe(map,"MA61931539", "931539", MA61931539, "text", true);
				keyExe(map,"MA6193154", "93154", MA6193154, "text", true);
				keyExe(map,"MA61931549", "931549", MA61931549, "text", true);
				keyExe(map,"MA6193203", "93203", MA6193203, "text", true);
				keyExe(map,"MA61932039", "932039", MA61932039, "text", true);
				keyExe(map,"MA6193212", "93212", MA6193212, "text", true);
				keyExe(map,"MA61932129", "932129", MA61932129, "text", true);
				keyExe(map,"MA6193222", "93222", MA6193222, "text", true);
				keyExe(map,"MA61932229", "932229", MA61932229, "text", true);
				keyExe(map,"MA6193233", "93233", MA6193233, "text", true);
				keyExe(map,"MA61932339", "932339", MA61932339, "text", true);
				keyExe(map,"MA6193241", "93241", MA6193241, "text", true);
				keyExe(map,"MA61932419", "932419", MA61932419, "text", true);
				keyExe(map,"MA6193242", "93242", MA6193242, "text", true);
				keyExe(map,"MA61932429", "932429", MA61932429, "text", true);
				keyExe(map,"MA6293301", "93301", MA6293301, "text", true);
				keyExe(map,"MA6293302", "93302", MA6293302, "text", true);
				keyExe(map,"MA6293303", "93303", MA6293303, "text", true);
				keyExe(map,"MA6293304", "93304", MA6293304, "text", true);
				keyExe(map,"MA6293305", "93305", MA6293305, "text", true);
				keyExe(map,"MA6293306", "93306", MA6293306, "text", true);
				keyExe(map,"MA6293311", "93311", MA6293311, "text", true);
				keyExe(map,"MA6293312", "93312", MA6293312, "text", true);
				keyExe(map,"MA6293313", "93313", MA6293313, "text", true);
				keyExe(map,"MA6293314", "93314", MA6293314, "text", true);
				keyExe(map,"MA6293315", "93315", MA6293315, "text", true);
				keyExe(map,"MA6293316", "93316", MA6293316, "text", true);
				keyExe(map,"MA6293321", "93321", MA6293321, "text", true);
				keyExe(map,"MA6293322", "93322", MA6293322, "text", true);
				keyExe(map,"MA6293323", "93323", MA6293323, "text", true);
				keyExe(map,"MA6293324", "93324", MA6293324, "text", true);
				keyExe(map,"MA6293325", "93325", MA6293325, "text", true);
				keyExe(map,"MA6293326", "93326", MA6293326, "text", true);
				keyExe(map,"MA6293331", "93331", MA6293331, "text", true);
				keyExe(map,"MA6293332", "93332", MA6293332, "text", true);
				keyExe(map,"MA6293333", "93333", MA6293333, "text", true);
				keyExe(map,"MA6293334", "93334", MA6293334, "text", true);
				keyExe(map,"MA6293335", "93335", MA6293335, "text", true);
				keyExe(map,"MA6293336", "93336", MA6293336, "text", true);
				keyExe(map,"MA6293341", "93341", MA6293341, "text", true);
				keyExe(map,"MA6293342", "93342", MA6293342, "text", true);
				keyExe(map,"MA6293343", "93343", MA6293343, "text", true);
				keyExe(map,"MA6293344", "93344", MA6293344, "text", true);
				keyExe(map,"MA6293345", "93345", MA6293345, "text", true);
				keyExe(map,"MA6293346", "93346", MA6293346, "text", true);
				keyExe(map,"MA6293351", "93351", MA6293351, "text", true);
				keyExe(map,"MA6293352", "93352", MA6293352, "text", true);
				keyExe(map,"MA6293353", "93353", MA6293353, "text", true);
				keyExe(map,"MA6293354", "93354", MA6293354, "text", true);
				keyExe(map,"MA6293355", "93355", MA6293355, "text", true);
				keyExe(map,"MA6293356", "93356", MA6293356, "text", true);
				keyExe(map,"MA62933119", "933119", MA62933119, "text", true);
				keyExe(map,"MA62933129", "933129", MA62933129, "text", true);
				keyExe(map,"MA62933139", "933139", MA62933139, "text", true);
				keyExe(map,"MA62933149", "933149", MA62933149, "text", true);
				keyExe(map,"MA62933159", "933159", MA62933159, "text", true);
				keyExe(map,"MA62933169", "933169", MA62933169, "text", true);
				keyExe(map,"MA62933219", "933219", MA62933219, "text", true);
				keyExe(map,"MA62933229", "933229", MA62933229, "text", true);
				keyExe(map,"MA62933239", "933239", MA62933239, "text", true);
				keyExe(map,"MA62933249", "933249", MA62933249, "text", true);
				keyExe(map,"MA62933259", "933259", MA62933259, "text", true);
				keyExe(map,"MA62933269", "933269", MA62933269, "text", true);
				keyExe(map,"MA62933319", "933319", MA62933319, "text", true);
				keyExe(map,"MA62933329", "933329", MA62933329, "text", true);
				keyExe(map,"MA62933339", "933339", MA62933339, "text", true);
				keyExe(map,"MA62933349", "933349", MA62933349, "text", true);
				keyExe(map,"MA62933359", "933359", MA62933359, "text", true);
				keyExe(map,"MA62933369", "933369", MA62933369, "text", true);
				keyExe(map,"MA62933419", "933419", MA62933419, "text", true);
				keyExe(map,"MA62933429", "933429", MA62933429, "text", true);
				keyExe(map,"MA62933439", "933439", MA62933439, "text", true);
				keyExe(map,"MA62933449", "933449", MA62933449, "text", true);
				keyExe(map,"MA62933459", "933459", MA62933459, "text", true);
				keyExe(map,"MA62933469", "933469", MA62933469, "text", true);
				keyExe(map,"MA62933519", "933519", MA62933519, "text", true);
				keyExe(map,"MA62933529", "933529", MA62933529, "text", true);
				keyExe(map,"MA62933539", "933539", MA62933539, "text", true);
				keyExe(map,"MA62933549", "933549", MA62933549, "text", true);
				keyExe(map,"MA62933559", "933559", MA62933559, "text", true);
				keyExe(map,"MA62933569", "933569", MA62933569, "text", true);
				keyExe(map,"MA6293401", "93401", MA6293401, "text", true);
				keyExe(map,"MA6293402", "93402", MA6293402, "text", true);
				keyExe(map,"MA6293403", "93403", MA6293403, "text", true);
				keyExe(map,"MA6293404", "93404", MA6293404, "text", true);
				keyExe(map,"MA6293411", "93411", MA6293411, "text", true);
				keyExe(map,"MA6293412", "93412", MA6293412, "text", true);
				keyExe(map,"MA6293413", "93413", MA6293413, "text", true);
				keyExe(map,"MA6293414", "93414", MA6293414, "text", true);
				keyExe(map,"MA6293421", "93421", MA6293421, "text", true);
				keyExe(map,"MA6293422", "93422", MA6293422, "text", true);
				keyExe(map,"MA6293423", "93423", MA6293423, "text", true);
				keyExe(map,"MA6293424", "93424", MA6293424, "text", true);
				keyExe(map,"MA6293431", "93431", MA6293431, "text", true);
				keyExe(map,"MA6293432", "93432", MA6293432, "text", true);
				keyExe(map,"MA6293433", "93433", MA6293433, "text", true);
				keyExe(map,"MA6293434", "93434", MA6293434, "text", true);
				keyExe(map,"MA62934119", "934119", MA62934119, "text", true);
				keyExe(map,"MA62934129", "934129", MA62934129, "text", true);
				keyExe(map,"MA62934139", "934139", MA62934139, "text", true);
				keyExe(map,"MA62934149", "934149", MA62934149, "text", true);
				keyExe(map,"MA62934219", "934219", MA62934219, "text", true);
				keyExe(map,"MA62934229", "934229", MA62934229, "text", true);
				keyExe(map,"MA62934239", "934239", MA62934239, "text", true);
				keyExe(map,"MA62934249", "934249", MA62934249, "text", true);
				keyExe(map,"MA62934319", "934319", MA62934319, "text", true);
				keyExe(map,"MA62934329", "934329", MA62934329, "text", true);
				keyExe(map,"MA62934339", "934339", MA62934339, "text", true);
				keyExe(map,"MA62934349", "934349", MA62934349, "text", true);
				keyExe(map,"MA62935059", "935059", MA62935059, "text", true);
				keyExe(map,"MA6293501", "93501", MA6293501, "text", true);
				keyExe(map,"MA6293502", "93502", MA6293502, "text", true);
				keyExe(map,"MA6293503", "93503", MA6293503, "text", true);
				keyExe(map,"MA6293504", "93504", MA6293504, "text", true);
				keyExe(map,"MA6293505", "93505", MA6293505, "text", true);
				keyExe(map,"MA62935159", "935159", MA62935159, "text", true);
				keyExe(map,"MA6293511", "93511", MA6293511, "text", true);
				keyExe(map,"MA6293512", "93512", MA6293512, "text", true);
				keyExe(map,"MA6293513", "93513", MA6293513, "text", true);
				keyExe(map,"MA6293514", "93514", MA6293514, "text", true);
				keyExe(map,"MA6293515", "93515", MA6293515, "text", true);
				keyExe(map,"MA62935259", "935259", MA62935259, "text", true);
				keyExe(map,"MA6293521", "93521", MA6293521, "text", true);
				keyExe(map,"MA6293522", "93522", MA6293522, "text", true);
				keyExe(map,"MA6293523", "93523", MA6293523, "text", true);
				keyExe(map,"MA6293524", "93524", MA6293524, "text", true);
				keyExe(map,"MA6293525", "93525", MA6293525, "text", true);
				keyExe(map,"MA62935359", "935359", MA62935359, "text", true);
				keyExe(map,"MA6293531", "93531", MA6293531, "text", true);
				keyExe(map,"MA6293532", "93532", MA6293532, "text", true);
				keyExe(map,"MA6293533", "93533", MA6293533, "text", true);
				keyExe(map,"MA6293534", "93534", MA6293534, "text", true);
				keyExe(map,"MA6293535", "93535", MA6293535, "text", true);

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
