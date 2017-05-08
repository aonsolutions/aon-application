package com.esferalia.aon.gwt.fiscal.client.mod200.e2013;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Page07 extends PageAbs {

	interface Page7Binder extends
			UiBinder<Widget, Page07> {
	}
	
	private static final Page7Binder page7Binder = GWT.create(Page7Binder.class);

	private static final String[] COLS = new String[] {"",
			AON.MSG.ecpnMsg1(),AON.MSG.ecpnMsg2(),AON.MSG.ecpnMsg3(),
			AON.MSG.ecpnMsg4(),AON.MSG.ecpnMsg5(),AON.MSG.ecpnMsg6(),
			AON.MSG.ecpnMsg7(),AON.MSG.ecpnMsg8(),AON.MSG.ecpnMsg9(),
			AON.MSG.ecpnMsg10(),AON.MSG.ecpnMsg11(),AON.MSG.ecpnMsg12(),
			AON.MSG.ecpnMsg13(),AON.MSG.ecpnMsg14()
	};
	
	private static enum Page7Row {
		 ROW1 (""             ,false,true ,true ,true ,null )
		,ROW2 (AON.MSG.ecpnMsg15(),true ,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC380,Mod2002013Key.TC381,Mod2002013Key.TC382,Mod2002013Key.TC383,Mod2002013Key.TC384,Mod2002013Key.TC385,Mod2002013Key.TC386,Mod2002013Key.TC387,Mod2002013Key.TC388,Mod2002013Key.TC389,Mod2002013Key.TC390,Mod2002013Key.TC391,Mod2002013Key.TC392,Mod2002013Key.TC393})
		,ROW3 (AON.MSG.ecpnMsg16(),false,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC394,Mod2002013Key.TC395,Mod2002013Key.TC396,Mod2002013Key.TC397,Mod2002013Key.TC398,Mod2002013Key.TC399,Mod2002013Key.TC400,Mod2002013Key.TC401,Mod2002013Key.TC402,Mod2002013Key.TC403,Mod2002013Key.TC404,Mod2002013Key.TC405,Mod2002013Key.TC406,Mod2002013Key.TC407})
		,ROW4 (AON.MSG.ecpnMsg17(),false,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC408,Mod2002013Key.TC409,Mod2002013Key.TC410,Mod2002013Key.TC411,Mod2002013Key.TC412,Mod2002013Key.TC413,Mod2002013Key.TC414,Mod2002013Key.TC415,Mod2002013Key.TC416,Mod2002013Key.TC417,Mod2002013Key.TC418,Mod2002013Key.TC419,Mod2002013Key.TC420,Mod2002013Key.TC421})
		,ROW5 (AON.MSG.ecpnMsg18(),true ,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC422,Mod2002013Key.TC423,Mod2002013Key.TC424,Mod2002013Key.TC425,Mod2002013Key.TC426,Mod2002013Key.TC427,Mod2002013Key.TC428,Mod2002013Key.TC429,Mod2002013Key.TC430,Mod2002013Key.TC431,Mod2002013Key.TC432,Mod2002013Key.TC433,Mod2002013Key.TC434,Mod2002013Key.TC435})
		,ROW6 (AON.MSG.ecpnMsg19(),false,true ,true ,false,new Mod2002013Key[] {Mod2002013Key.TC436,Mod2002013Key.TC437,Mod2002013Key.TC438,Mod2002013Key.TC439,Mod2002013Key.TC440,Mod2002013Key.TC441,Mod2002013Key.TC442,Mod2002013Key.TC443,Mod2002013Key.TC444,Mod2002013Key.TC445,Mod2002013Key.TC446,null           ,Mod2002013Key.TC448,Mod2002013Key.TC449})
		,ROW7 (AON.MSG.ecpnMsg20(),false,false,false,true ,new Mod2002013Key[] {Mod2002013Key.TC450,Mod2002013Key.TC451,Mod2002013Key.TC452,Mod2002013Key.TC453,Mod2002013Key.TC454,Mod2002013Key.TC455,Mod2002013Key.TC456,Mod2002013Key.TC457,Mod2002013Key.TC458,null           ,null           ,Mod2002013Key.TC461,Mod2002013Key.TC462,Mod2002013Key.TC463})
		,ROW8 (AON.MSG.ecpnMsg21(),true ,false,false,true ,new Mod2002013Key[] {Mod2002013Key.TC464,Mod2002013Key.TC465,Mod2002013Key.TC466,Mod2002013Key.TC467,Mod2002013Key.TC468,Mod2002013Key.TC469,Mod2002013Key.TC470,Mod2002013Key.TC471,Mod2002013Key.TC472,null           ,null           ,Mod2002013Key.TC475,Mod2002013Key.TC476,Mod2002013Key.TC477})
		,ROW9 (AON.MSG.ecpnMsg22(),false,false,false,true ,new Mod2002013Key[] {Mod2002013Key.TC478,Mod2002013Key.TC479,Mod2002013Key.TC480,Mod2002013Key.TC481,Mod2002013Key.TC482,Mod2002013Key.TC483,Mod2002013Key.TC484,Mod2002013Key.TC485,Mod2002013Key.TC486,null           ,null           ,Mod2002013Key.TC489,Mod2002013Key.TC490,Mod2002013Key.TC491})
		,ROW10(AON.MSG.ecpnMsg23(),false,false,false,true ,new Mod2002013Key[] {Mod2002013Key.TC492,Mod2002013Key.TC493,Mod2002013Key.TC494,Mod2002013Key.TC495,Mod2002013Key.TC496,Mod2002013Key.TC497,Mod2002013Key.TC498,Mod2002013Key.TC499,Mod2002013Key.TC502,null           ,null           ,Mod2002013Key.TC503,Mod2002013Key.TC504,Mod2002013Key.TC505})
		,ROW11(AON.MSG.ecpnMsg24(),true ,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC506,Mod2002013Key.TC507,Mod2002013Key.TC508,Mod2002013Key.TC509,Mod2002013Key.TC510,Mod2002013Key.TC511,Mod2002013Key.TC512,Mod2002013Key.TC513,Mod2002013Key.TC514,Mod2002013Key.TC515,Mod2002013Key.TC516,Mod2002013Key.TC517,Mod2002013Key.TC518,Mod2002013Key.TC519})
		,ROW12(AON.MSG.ecpnMsg25(),false,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC520,Mod2002013Key.TC521,Mod2002013Key.TC522,Mod2002013Key.TC523,Mod2002013Key.TC524,Mod2002013Key.TC525,Mod2002013Key.TC526,Mod2002013Key.TC527,Mod2002013Key.TC528,Mod2002013Key.TC529,Mod2002013Key.TC530,Mod2002013Key.TC531,Mod2002013Key.TC532,Mod2002013Key.TC533})
		,ROW13(AON.MSG.ecpnMsg26(),false,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC534,Mod2002013Key.TC535,Mod2002013Key.TC536,Mod2002013Key.TC537,Mod2002013Key.TC538,Mod2002013Key.TC539,Mod2002013Key.TC540,Mod2002013Key.TC541,Mod2002013Key.TC542,Mod2002013Key.TC543,Mod2002013Key.TC544,Mod2002013Key.TC545,Mod2002013Key.TC546,Mod2002013Key.TC547})
		,ROW14(AON.MSG.ecpnMsg27(),false,true ,false,false,new Mod2002013Key[] {Mod2002013Key.TC548,Mod2002013Key.TC549,Mod2002013Key.TC550,Mod2002013Key.TC551,Mod2002013Key.TC552,Mod2002013Key.TC553,Mod2002013Key.TC554,Mod2002013Key.TC555,Mod2002013Key.TC556,Mod2002013Key.TC557,Mod2002013Key.TC558,null           ,Mod2002013Key.TC560,Mod2002013Key.TC561})
		,ROW15(AON.MSG.ecpnMsg28(),false,true ,false,false,new Mod2002013Key[] {Mod2002013Key.TC562,Mod2002013Key.TC563,Mod2002013Key.TC564,Mod2002013Key.TC565,Mod2002013Key.TC566,Mod2002013Key.TC567,Mod2002013Key.TC568,Mod2002013Key.TC569,Mod2002013Key.TC570,Mod2002013Key.TC571,Mod2002013Key.TC572,null           ,Mod2002013Key.TC574,Mod2002013Key.TC575})
		,ROW16(AON.MSG.ecpnMsg29(),false,true ,false,false,new Mod2002013Key[] {Mod2002013Key.TC576,Mod2002013Key.TC577,Mod2002013Key.TC578,Mod2002013Key.TC579,Mod2002013Key.TC580,Mod2002013Key.TC581,Mod2002013Key.TC582,Mod2002013Key.TC583,Mod2002013Key.TC584,Mod2002013Key.TC585,Mod2002013Key.TC586,null           ,Mod2002013Key.TC588,Mod2002013Key.TC589})
		,ROW17(AON.MSG.ecpnMsg30(),false,true ,false,false,new Mod2002013Key[] {Mod2002013Key.TC590,Mod2002013Key.TC591,Mod2002013Key.TC592,Mod2002013Key.TC593,Mod2002013Key.TC594,Mod2002013Key.TC595,Mod2002013Key.TC596,Mod2002013Key.TC597,Mod2002013Key.TC598,Mod2002013Key.TC599,Mod2002013Key.TC600,null           ,Mod2002013Key.TC602,Mod2002013Key.TC603})
		,ROW18(AON.MSG.ecpnMsg31(),false,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC604,Mod2002013Key.TC605,Mod2002013Key.TC606,Mod2002013Key.TC607,Mod2002013Key.TC608,Mod2002013Key.TC609,Mod2002013Key.TC610,Mod2002013Key.TC611,Mod2002013Key.TC612,Mod2002013Key.TC613,Mod2002013Key.TC614,Mod2002013Key.TC615,Mod2002013Key.TC616,Mod2002013Key.TC617})
		,ROW19(AON.MSG.ecpnMsg32(),true ,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC618,Mod2002013Key.TC619,Mod2002013Key.TC620,Mod2002013Key.TC621,Mod2002013Key.TC622,Mod2002013Key.TC623,Mod2002013Key.TC624,Mod2002013Key.TC625,Mod2002013Key.TC626,Mod2002013Key.TC627,Mod2002013Key.TC628,Mod2002013Key.TC629,Mod2002013Key.TC630,Mod2002013Key.TC631})
		,ROW20(AON.MSG.ecpnMsg33(),false,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC715,Mod2002013Key.TC716,Mod2002013Key.TC717,Mod2002013Key.TC718,Mod2002013Key.TC719,Mod2002013Key.TC720,Mod2002013Key.TC721,Mod2002013Key.TC722,Mod2002013Key.TC723,Mod2002013Key.TC724,Mod2002013Key.TC725,Mod2002013Key.TC726,Mod2002013Key.TC727,Mod2002013Key.TC728})
		,ROW21(AON.MSG.ecpnMsg34(),false,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC729,Mod2002013Key.TC730,Mod2002013Key.TC731,Mod2002013Key.TC732,Mod2002013Key.TC733,Mod2002013Key.TC734,Mod2002013Key.TC735,Mod2002013Key.TC736,Mod2002013Key.TC737,Mod2002013Key.TC738,Mod2002013Key.TC739,Mod2002013Key.TC740,Mod2002013Key.TC741,Mod2002013Key.TC742})
		,ROW22(AON.MSG.ecpnMsg35(),true ,true ,true ,true ,new Mod2002013Key[] {Mod2002013Key.TC632,Mod2002013Key.TC633,Mod2002013Key.TC634,Mod2002013Key.TC635,Mod2002013Key.TC636,Mod2002013Key.TC637,Mod2002013Key.TC638,Mod2002013Key.TC639,Mod2002013Key.TC640,Mod2002013Key.TC641,Mod2002013Key.TC642,Mod2002013Key.TC643,Mod2002013Key.TC644,Mod2002013Key.TC645})
		;
		 
		private String name;
		private boolean title;
		private boolean normal;
		private boolean abbreviate;
		private boolean pymes;
		private Mod2002013Key[] keys;
		
		private Page7Row(String name,boolean title,boolean normal,boolean abbreviate,boolean pymes,Mod2002013Key[] keys) {
			this.name = name;
			this.title = title;
			this.normal = normal;
			this.abbreviate = abbreviate;
			this.pymes = pymes;
			this.keys = keys;
		}
		protected String getName() {
			return name;
		}
		protected boolean isTitle() {
			return title;
		}
		public boolean isNormal() {
			return normal;
		}
		public boolean isAbbreviate() {
			return abbreviate;
		}
		public boolean isPymes() {
			return pymes;
		}
		public Mod2002013Key[] getKeys() {
			return keys;
		}
	}

	public Page07() {
		super();
		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	protected void initializeTable() {
		table.setCellSpacing(0);
		Label label = null;
		for (int col = 0; col < COLS.length; col++) {
			//if (col > 0) {
				label = new Label(COLS[col]);
				table.setWidget(0, col, label);
			//}
			table.getColumnFormatter().setWidth(col, (col == 0)?"150px":"100px");
			table.getFlexCellFormatter().addStyleName(0, col, AON.AON_CSS.aonTextCenter());	
		}
		
		for (int row = 1; row < Page7Row.values().length; row++) {
			boolean rowVisible  = (
				   (mod200Object.getMod200().getBalanceType() == BalanceType.NORMAL && Page7Row.values()[row].isNormal())
				|| (mod200Object.getMod200().getBalanceType() == BalanceType.ABREVIADO && Page7Row.values()[row].isAbbreviate())
				|| (mod200Object.getMod200().getBalanceType() == BalanceType.PYMES && Page7Row.values()[row].isPymes())
				);
			if (rowVisible) {
				table.setWidget(row, 0, new Label(Page7Row.values()[row].getName()));
				table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalBorderBottom());
				if (Page7Row.values()[row].isTitle()) {
					table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());	
				}
				for (int col = 1; col < COLS.length; col++) {
					FlowPanel panel = new FlowPanel();
					panel.setStyleName(AON.AON_CSS.aonNowrap());
					final Mod2002013Key key = Page7Row.values()[row].getKeys()[col - 1];
					if (key != null) {
						BoxLabel code = new BoxLabel(key.getCode( mod200Object.getAdministration() ));
						panel.add(code);
						getLabels().put(key, code);
						
						final DoubleBox text = new DoubleBox(8);
						text.addChangeHandler(new ChangeHandler() {
							@Override
							public void onChange(ChangeEvent event) {
								try {
									Double d = text.getValueOrThrow();
									text.addStyleName(AON.AON_CSS.aonChanged());
									mod200Object.doubleValueChanged(key, d);
								} catch (ParseException e) {
									// nothing.
								}
							}
						});
						text.setValue(mod200Object.getDoubleValue(key));
						text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
						text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
						text.setEnabled( !isDisabled(key) );
						text.setTabIndex((col * 100 + row));
						getInputs().put(key, text);
						panel.add(text);
						table.setWidget(row, col, panel);
					} else {
						table.setWidget(row, col, panel);
					}
				}
			}
		}
		
	}
	
}
