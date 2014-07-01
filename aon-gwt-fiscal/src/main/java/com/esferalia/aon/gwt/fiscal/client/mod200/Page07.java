package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;
import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;

import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Page07 extends PageAbs {

	interface Page7Binder extends
			UiBinder<Widget, Page07> {
	}
	
	private static final Page7Binder page7Binder = GWT.create(Page7Binder.class);

	private static final String[] COLS = new String[] {"",
			MSG.ecpnMsg1(),MSG.ecpnMsg2(),MSG.ecpnMsg3(),
			MSG.ecpnMsg4(),MSG.ecpnMsg5(),MSG.ecpnMsg6(),
			MSG.ecpnMsg7(),MSG.ecpnMsg8(),MSG.ecpnMsg9(),
			MSG.ecpnMsg10(),MSG.ecpnMsg11(),MSG.ecpnMsg12(),
			MSG.ecpnMsg13(),MSG.ecpnMsg14()
	};
	
	private static enum Page7Row {
		 ROW1 ("",false)
		,ROW2 (MSG.ecpnMsg15(),true)
		,ROW3 (MSG.ecpnMsg16(),false)
		,ROW4 (MSG.ecpnMsg17(),false)
		,ROW5 (MSG.ecpnMsg18(),false)
		,ROW6 (MSG.ecpnMsg19(),false)
		,ROW7 (MSG.ecpnMsg20(),false)
		,ROW8 (MSG.ecpnMsg21(),false)
		,ROW9 (MSG.ecpnMsg22(),false)
		,ROW10(MSG.ecpnMsg23(),false)
		,ROW11(MSG.ecpnMsg24(),false)
		,ROW12(MSG.ecpnMsg25(),false)
		,ROW13(MSG.ecpnMsg26(),false)
		,ROW14(MSG.ecpnMsg27(),false)
		,ROW15(MSG.ecpnMsg28(),false)
		,ROW16(MSG.ecpnMsg29(),false)
		,ROW17(MSG.ecpnMsg30(),false)
		,ROW18(MSG.ecpnMsg31(),false)
		,ROW19(MSG.ecpnMsg32(),false)
		,ROW20(MSG.ecpnMsg32(),false)
		,ROW21(MSG.ecpnMsg32(),false)
		,ROW22(MSG.ecpnMsg35(),true)
		;
		 
		private String name;
		private boolean title;
		
		private Page7Row(String name,boolean title) {
			this.name = name;
			this.title = title;
		}
		protected String getName() {
			return name;
		}
		protected boolean isTitle() {
			return title;
		}
	}
	
	private static final Mod200Key[] KEYS = new Mod200Key[] {
		 Mod200Key.TC001,Mod200Key.TC002,Mod200Key.TC003,Mod200Key.TC004,Mod200Key.TC005,Mod200Key.TC006,Mod200Key.TC007
		,Mod200Key.TC134,Mod200Key.TC135,Mod200Key.TC136,Mod200Key.TC137,Mod200Key.TC138,Mod200Key.TC139,Mod200Key.TC140
		,Mod200Key.TC008,Mod200Key.TC009,Mod200Key.TC010,Mod200Key.TC011,Mod200Key.TC012,Mod200Key.TC013,Mod200Key.TC014
		,Mod200Key.TC141,Mod200Key.TC142,Mod200Key.TC143,Mod200Key.TC144,Mod200Key.TC145,Mod200Key.TC146,Mod200Key.TC147
		,Mod200Key.TC015,Mod200Key.TC016,Mod200Key.TC017,Mod200Key.TC018,Mod200Key.TC019,Mod200Key.TC020,Mod200Key.TC021
		,Mod200Key.TC148,Mod200Key.TC149,Mod200Key.TC150,Mod200Key.TC151,Mod200Key.TC152,Mod200Key.TC153,Mod200Key.TC154
		,Mod200Key.TC022,Mod200Key.TC023,Mod200Key.TC024,Mod200Key.TC025,Mod200Key.TC026,Mod200Key.TC027,Mod200Key.TC028
		,Mod200Key.TC155,Mod200Key.TC156,Mod200Key.TC157,Mod200Key.TC158,Mod200Key.TC159,Mod200Key.TC160,Mod200Key.TC161
		,Mod200Key.TC029,Mod200Key.TC030,Mod200Key.TC031,Mod200Key.TC032,Mod200Key.TC033,Mod200Key.TC034,Mod200Key.TC035
		,Mod200Key.TC162,Mod200Key.TC163,Mod200Key.TC164,Mod200Key.TC165,null           ,Mod200Key.TC166,Mod200Key.TC167
		,Mod200Key.TC036,Mod200Key.TC037,Mod200Key.TC038,Mod200Key.TC039,Mod200Key.TC040,Mod200Key.TC041,Mod200Key.TC042
		,Mod200Key.TC168,Mod200Key.TC169,null           ,null           ,Mod200Key.TC170,Mod200Key.TC171,Mod200Key.TC172
		,Mod200Key.TC043,Mod200Key.TC044,Mod200Key.TC045,Mod200Key.TC046,Mod200Key.TC047,Mod200Key.TC048,Mod200Key.TC049
		,Mod200Key.TC173,Mod200Key.TC174,null           ,null           ,Mod200Key.TC175,Mod200Key.TC176,Mod200Key.TC177
		,Mod200Key.TC050,Mod200Key.TC051,Mod200Key.TC052,Mod200Key.TC053,Mod200Key.TC054,Mod200Key.TC055,Mod200Key.TC056
		,Mod200Key.TC178,Mod200Key.TC179,null           ,null           ,Mod200Key.TC180,Mod200Key.TC181,Mod200Key.TC182
		,Mod200Key.TC057,Mod200Key.TC058,Mod200Key.TC059,Mod200Key.TC060,Mod200Key.TC061,Mod200Key.TC062,Mod200Key.TC063
		,Mod200Key.TC183,Mod200Key.TC184,null           ,null           ,Mod200Key.TC185,Mod200Key.TC186,Mod200Key.TC187
		,Mod200Key.TC064,Mod200Key.TC065,Mod200Key.TC066,Mod200Key.TC067,Mod200Key.TC068,Mod200Key.TC069,Mod200Key.TC070
		,Mod200Key.TC188,Mod200Key.TC189,Mod200Key.TC190,Mod200Key.TC191,Mod200Key.TC192,Mod200Key.TC193,Mod200Key.TC194
		,Mod200Key.TC071,Mod200Key.TC072,Mod200Key.TC073,Mod200Key.TC074,Mod200Key.TC075,Mod200Key.TC076,Mod200Key.TC077
		,Mod200Key.TC195,Mod200Key.TC196,Mod200Key.TC197,Mod200Key.TC198,Mod200Key.TC199,Mod200Key.TC200,Mod200Key.TC201
		,Mod200Key.TC078,Mod200Key.TC079,Mod200Key.TC080,Mod200Key.TC081,Mod200Key.TC082,Mod200Key.TC083,Mod200Key.TC084
		,Mod200Key.TC202,Mod200Key.TC203,Mod200Key.TC204,Mod200Key.TC205,Mod200Key.TC206,Mod200Key.TC207,Mod200Key.TC208
		,Mod200Key.TC085,Mod200Key.TC086,Mod200Key.TC087,Mod200Key.TC088,Mod200Key.TC089,Mod200Key.TC090,Mod200Key.TC091
		,Mod200Key.TC209,Mod200Key.TC210,Mod200Key.TC211,Mod200Key.TC212,null           ,Mod200Key.TC213,Mod200Key.TC214
		,Mod200Key.TC092,Mod200Key.TC093,Mod200Key.TC094,Mod200Key.TC095,Mod200Key.TC096,Mod200Key.TC097,Mod200Key.TC098
		,Mod200Key.TC215,Mod200Key.TC216,Mod200Key.TC217,Mod200Key.TC218,null           ,Mod200Key.TC219,Mod200Key.TC220
		,Mod200Key.TC099,Mod200Key.TC100,Mod200Key.TC101,Mod200Key.TC102,Mod200Key.TC103,Mod200Key.TC104,Mod200Key.TC105
		,Mod200Key.TC221,Mod200Key.TC222,Mod200Key.TC223,Mod200Key.TC224,null           ,Mod200Key.TC225,Mod200Key.TC226
		,Mod200Key.TC106,Mod200Key.TC107,Mod200Key.TC108,Mod200Key.TC109,Mod200Key.TC110,Mod200Key.TC111,Mod200Key.TC112
		,Mod200Key.TC227,Mod200Key.TC228,Mod200Key.TC229,Mod200Key.TC230,null           ,Mod200Key.TC231,Mod200Key.TC232
		,Mod200Key.TC113,Mod200Key.TC114,Mod200Key.TC115,Mod200Key.TC116,Mod200Key.TC117,Mod200Key.TC118,Mod200Key.TC119
		,Mod200Key.TC233,Mod200Key.TC234,Mod200Key.TC235,Mod200Key.TC236,Mod200Key.TC237,Mod200Key.TC238,Mod200Key.TC239
		,Mod200Key.TC120,Mod200Key.TC121,Mod200Key.TC122,Mod200Key.TC123,Mod200Key.TC124,Mod200Key.TC125,Mod200Key.TC126
		,Mod200Key.TC240,Mod200Key.TC241,Mod200Key.TC242,Mod200Key.TC243,Mod200Key.TC244,Mod200Key.TC245,Mod200Key.TC246
		,Mod200Key.TC700,Mod200Key.TC701,Mod200Key.TC702,Mod200Key.TC703,Mod200Key.TC704,Mod200Key.TC705,Mod200Key.TC706
		,Mod200Key.TC707,Mod200Key.TC708,Mod200Key.TC709,Mod200Key.TC710,Mod200Key.TC711,Mod200Key.TC712,Mod200Key.TC713
		,Mod200Key.TC714,Mod200Key.TC715,Mod200Key.TC716,Mod200Key.TC717,Mod200Key.TC718,Mod200Key.TC719,Mod200Key.TC720
		,Mod200Key.TC721,Mod200Key.TC722,Mod200Key.TC723,Mod200Key.TC724,Mod200Key.TC725,Mod200Key.TC726,Mod200Key.TC727
		,Mod200Key.TC127,Mod200Key.TC128,Mod200Key.TC129,Mod200Key.TC130,Mod200Key.TC131,Mod200Key.TC132,Mod200Key.TC133
		,Mod200Key.TC247,Mod200Key.TC248,Mod200Key.TC249,Mod200Key.TC250,Mod200Key.TC251,Mod200Key.TC252,Mod200Key.TC253
	};
	
	public Page07() {
		super();
		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	protected void initializeTable() {
		table.setCellSpacing(0);
		CellTable.Resources tableStyle = GWT.create(Mod200CellTable.class);
		Label label = null;
		for (int col = 0; col < COLS.length; col++) {
			if (col > 0) {
				label = new Label(COLS[col]);
				table.setWidget(0, col, label);
			}
			table.getColumnFormatter().setWidth(col, (col == 0)?"150px":"100px");
			table.getFlexCellFormatter().addStyleName(0, col, tableStyle.cellTableStyle().cellTableHeader());
			table.getFlexCellFormatter().addStyleName(0, col, RESOURCES.css().aonTextCenter());	
		}
		
		int keyIndex = 0;
		
		for (int row = 1; row < Page7Row.values().length; row++) {
			
			table.setWidget(row, 0, new Label(Page7Row.values()[row].getName()));
			table.getFlexCellFormatter().setStyleName(row, 0, RESOURCES.css().aonMod200BorderBottom());
			if (Page7Row.values()[row].isTitle()) {
				table.getFlexCellFormatter().addStyleName(row, 0, RESOURCES.css().aonBold());	
			}
			for (int col = 1; col < COLS.length; col++) {
				FlowPanel panel = new FlowPanel();
				panel.setStyleName(RESOURCES.css().aonNowrap());
				final Mod200Key key = KEYS[keyIndex];
				if (key != null) {
					Label code = new Label(key.getCode( mod200Object.getAdministration() ));
					code.setStyleName(RESOURCES.css().aonMod200Box());
					panel.add(code);
					getLabels().put(key, code);
					
					final DoubleTextBox text = new DoubleTextBox(8);
					text.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							if (text.isValidValue()) {
								text.addStyleName(RESOURCES.css().aonChanged());
								mod200Object.doubleValueChanged(key, text.getDoubleValue());
							}
						}
					});
					text.setValue(mod200Object.getDoubleValue(key));
					text.addStyleName(RESOURCES.css().aonMod200MarginLeft());
					text.addStyleName(RESOURCES.css().aonMod200PaddingLeft());
					text.setChangeDisplayStyleName(RESOURCES.css().aonValueChanged());
					getInputs().put(key, text);
					panel.add(text);
					table.setWidget(row, col, panel);
				} else {
					table.setWidget(row, col, panel);
				}
				keyIndex++;
			}
		}
		
	}
	
}
