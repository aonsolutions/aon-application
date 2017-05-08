package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.watson.util.AonStringUtils;
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
	private static enum Page7Column {
		 COL00(""                 ,true ,true ,true )
		,COL01(AON.MSG.ecpnMsg1() ,true ,true ,true )
		,COL02(AON.MSG.ecpnMsg2() ,true ,true ,true )
		,COL03(AON.MSG.ecpnMsg3() ,true ,true ,true )
		,COL04(AON.MSG.ecpnMsg4() ,true ,true ,true )
		,COL05(AON.MSG.ecpnMsg5() ,true ,true ,true )
		,COL06(AON.MSG.ecpnMsg6() ,true ,true ,true )
		,COL07(AON.MSG.ecpnMsg7() ,true ,true ,true )
		,COL08(AON.MSG.ecpnMsg8() ,true ,true ,true )
		,COL09(AON.MSG.ecpnMsg9() ,true ,true ,true )
		,COL10(AON.MSG.ecpnMsg10(),true ,true ,false)
		,COL11(AON.MSG.ecpnMsg11(),true ,true ,false)
		,COL12(AON.MSG.ecpnMsg12(),false,false,true )
		,COL13(AON.MSG.ecpnMsg13(),true ,true ,true )
		,COL14(AON.MSG.ecpnMsg14(),true ,true ,true )
		;
		private String name;
		private boolean normal;
		private boolean abbreviate;
		private boolean pymes;
		
		private Page7Column(String name,boolean normal,boolean abbreviate,boolean pymes) {
			this.name = name;
			this.normal = normal;
			this.abbreviate = abbreviate;
			this.pymes = pymes;
		}
		protected String getName() {
			return name;
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
	}
	
//	private static final String[] COLS = new String[] {"",
//			AON.MSG.ecpnMsg1(),AON.MSG.ecpnMsg2(),AON.MSG.ecpnMsg3(),
//			AON.MSG.ecpnMsg4(),AON.MSG.ecpnMsg5(),AON.MSG.ecpnMsg6(),
//			AON.MSG.ecpnMsg7(),AON.MSG.ecpnMsg8(),AON.MSG.ecpnMsg9(),
//			AON.MSG.ecpnMsg10(),AON.MSG.ecpnMsg11(),AON.MSG.ecpnMsg12(),
//			AON.MSG.ecpnMsg14(),AON.MSG.ecpnMsg14()
//	};
	
	private static enum Page7Row {
		 ROW1 (""             ,false,true ,true ,true ,null )
		,ROW2 (AON.MSG.ecpnMsg15(),true ,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC380,Mod2002016Key.TC381,Mod2002016Key.TC382,Mod2002016Key.TC383,Mod2002016Key.TC384,Mod2002016Key.TC385,Mod2002016Key.TC386,Mod2002016Key.TC387,Mod2002016Key.TC388,Mod2002016Key.TC389,Mod2002016Key.TC390,Mod2002016Key.TC391,Mod2002016Key.TC392,Mod2002016Key.TC393})
		,ROW3 (AON.MSG.ecpnMsg16(),false,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC394,Mod2002016Key.TC395,Mod2002016Key.TC396,Mod2002016Key.TC397,Mod2002016Key.TC398,Mod2002016Key.TC399,Mod2002016Key.TC400,Mod2002016Key.TC401,Mod2002016Key.TC402,Mod2002016Key.TC403,Mod2002016Key.TC404,Mod2002016Key.TC405,Mod2002016Key.TC406,Mod2002016Key.TC407})
		,ROW4 (AON.MSG.ecpnMsg17(),false,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC408,Mod2002016Key.TC409,Mod2002016Key.TC410,Mod2002016Key.TC411,Mod2002016Key.TC412,Mod2002016Key.TC413,Mod2002016Key.TC414,Mod2002016Key.TC415,Mod2002016Key.TC416,Mod2002016Key.TC417,Mod2002016Key.TC418,Mod2002016Key.TC419,Mod2002016Key.TC420,Mod2002016Key.TC421})
		,ROW5 (AON.MSG.ecpnMsg18(),true ,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC422,Mod2002016Key.TC423,Mod2002016Key.TC424,Mod2002016Key.TC425,Mod2002016Key.TC426,Mod2002016Key.TC427,Mod2002016Key.TC428,Mod2002016Key.TC429,Mod2002016Key.TC430,Mod2002016Key.TC431,Mod2002016Key.TC432,Mod2002016Key.TC433,Mod2002016Key.TC434,Mod2002016Key.TC435})
		,ROW6 (AON.MSG.ecpnMsg19(),false,true ,true ,false,new Mod2002016Key[] {Mod2002016Key.TC436,Mod2002016Key.TC437,Mod2002016Key.TC438,Mod2002016Key.TC439,Mod2002016Key.TC440,Mod2002016Key.TC441,Mod2002016Key.TC442,Mod2002016Key.TC443,Mod2002016Key.TC444,Mod2002016Key.TC445,Mod2002016Key.TC446,null           ,Mod2002016Key.TC448,Mod2002016Key.TC449})
		,ROW7 (AON.MSG.ecpnMsg20(),false,false,false,true ,new Mod2002016Key[] {Mod2002016Key.TC450,Mod2002016Key.TC451,Mod2002016Key.TC452,Mod2002016Key.TC453,Mod2002016Key.TC454,Mod2002016Key.TC455,Mod2002016Key.TC456,Mod2002016Key.TC457,Mod2002016Key.TC458,null           ,null           ,Mod2002016Key.TC461,Mod2002016Key.TC462,Mod2002016Key.TC463})
		,ROW8 (AON.MSG.ecpnMsg21(),true ,false,false,true ,new Mod2002016Key[] {Mod2002016Key.TC464,Mod2002016Key.TC465,Mod2002016Key.TC466,Mod2002016Key.TC467,Mod2002016Key.TC468,Mod2002016Key.TC469,Mod2002016Key.TC470,Mod2002016Key.TC471,Mod2002016Key.TC472,null           ,null           ,Mod2002016Key.TC475,Mod2002016Key.TC476,Mod2002016Key.TC477})
		,ROW9 (AON.MSG.ecpnMsg22(),false,false,false,true ,new Mod2002016Key[] {Mod2002016Key.TC478,Mod2002016Key.TC479,Mod2002016Key.TC480,Mod2002016Key.TC481,Mod2002016Key.TC482,Mod2002016Key.TC483,Mod2002016Key.TC484,Mod2002016Key.TC485,Mod2002016Key.TC486,null           ,null           ,Mod2002016Key.TC489,Mod2002016Key.TC490,Mod2002016Key.TC491})
		,ROW10(AON.MSG.ecpnMsg23(),false,false,false,true ,new Mod2002016Key[] {Mod2002016Key.TC492,Mod2002016Key.TC493,Mod2002016Key.TC494,Mod2002016Key.TC495,Mod2002016Key.TC496,Mod2002016Key.TC497,Mod2002016Key.TC498,Mod2002016Key.TC499,Mod2002016Key.TC502,null           ,null           ,Mod2002016Key.TC503,Mod2002016Key.TC504,Mod2002016Key.TC505})
		,ROW11(AON.MSG.ecpnMsg24(),true ,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC506,Mod2002016Key.TC507,Mod2002016Key.TC508,Mod2002016Key.TC509,Mod2002016Key.TC510,Mod2002016Key.TC511,Mod2002016Key.TC512,Mod2002016Key.TC513,Mod2002016Key.TC514,Mod2002016Key.TC515,Mod2002016Key.TC516,Mod2002016Key.TC517,Mod2002016Key.TC518,Mod2002016Key.TC519})
		,ROW12(AON.MSG.ecpnMsg25(),false,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC520,Mod2002016Key.TC521,Mod2002016Key.TC522,Mod2002016Key.TC523,Mod2002016Key.TC524,Mod2002016Key.TC525,Mod2002016Key.TC526,Mod2002016Key.TC527,Mod2002016Key.TC528,Mod2002016Key.TC529,Mod2002016Key.TC530,Mod2002016Key.TC531,Mod2002016Key.TC532,Mod2002016Key.TC533})
		,ROW13(AON.MSG.ecpnMsg26(),false,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC534,Mod2002016Key.TC535,Mod2002016Key.TC536,Mod2002016Key.TC537,Mod2002016Key.TC538,Mod2002016Key.TC539,Mod2002016Key.TC540,Mod2002016Key.TC541,Mod2002016Key.TC542,Mod2002016Key.TC543,Mod2002016Key.TC544,Mod2002016Key.TC545,Mod2002016Key.TC546,Mod2002016Key.TC547})
		,ROW14(AON.MSG.ecpnMsg27(),false,true ,false,false,new Mod2002016Key[] {Mod2002016Key.TC548,Mod2002016Key.TC549,Mod2002016Key.TC550,Mod2002016Key.TC551,Mod2002016Key.TC552,Mod2002016Key.TC553,Mod2002016Key.TC554,Mod2002016Key.TC555,Mod2002016Key.TC556,Mod2002016Key.TC557,Mod2002016Key.TC558,null           ,Mod2002016Key.TC560,Mod2002016Key.TC561})
		,ROW15(AON.MSG.ecpnMsg28(),false,true ,false,false,new Mod2002016Key[] {Mod2002016Key.TC562,Mod2002016Key.TC563,Mod2002016Key.TC564,Mod2002016Key.TC565,Mod2002016Key.TC566,Mod2002016Key.TC567,Mod2002016Key.TC568,Mod2002016Key.TC569,Mod2002016Key.TC570,Mod2002016Key.TC571,Mod2002016Key.TC572,null           ,Mod2002016Key.TC574,Mod2002016Key.TC575})
		,ROW16(AON.MSG.ecpnMsg29(),false,true ,false,false,new Mod2002016Key[] {Mod2002016Key.TC576,Mod2002016Key.TC577,Mod2002016Key.TC578,Mod2002016Key.TC579,Mod2002016Key.TC580,Mod2002016Key.TC581,Mod2002016Key.TC582,Mod2002016Key.TC583,Mod2002016Key.TC584,Mod2002016Key.TC585,Mod2002016Key.TC586,null           ,Mod2002016Key.TC588,Mod2002016Key.TC589})
		,ROW17(AON.MSG.ecpnMsg30(),false,true ,false,false,new Mod2002016Key[] {Mod2002016Key.TC590,Mod2002016Key.TC591,Mod2002016Key.TC592,Mod2002016Key.TC593,Mod2002016Key.TC594,Mod2002016Key.TC595,Mod2002016Key.TC596,Mod2002016Key.TC597,Mod2002016Key.TC598,Mod2002016Key.TC599,Mod2002016Key.TC600,null           ,Mod2002016Key.TC602,Mod2002016Key.TC603})
		,ROW18(AON.MSG.ecpnMsg31(),false,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC604,Mod2002016Key.TC605,Mod2002016Key.TC606,Mod2002016Key.TC607,Mod2002016Key.TC608,Mod2002016Key.TC609,Mod2002016Key.TC610,Mod2002016Key.TC611,Mod2002016Key.TC612,Mod2002016Key.TC613,Mod2002016Key.TC614,Mod2002016Key.TC615,Mod2002016Key.TC616,Mod2002016Key.TC617})
		,ROW19(AON.MSG.ecpnMsg32(),true ,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC618,Mod2002016Key.TC619,Mod2002016Key.TC620,Mod2002016Key.TC621,Mod2002016Key.TC622,Mod2002016Key.TC623,Mod2002016Key.TC624,Mod2002016Key.TC625,Mod2002016Key.TC626,Mod2002016Key.TC627,Mod2002016Key.TC628,Mod2002016Key.TC629,Mod2002016Key.TC630,Mod2002016Key.TC631})
		,ROW20(AON.MSG.ecpnMsg33(),false,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC715,Mod2002016Key.TC716,Mod2002016Key.TC717,Mod2002016Key.TC718,Mod2002016Key.TC719,Mod2002016Key.TC720,Mod2002016Key.TC721,Mod2002016Key.TC722,Mod2002016Key.TC723,Mod2002016Key.TC724,Mod2002016Key.TC725,Mod2002016Key.TC726,Mod2002016Key.TC727,Mod2002016Key.TC728})
		,ROW21(AON.MSG.ecpnMsg34(),false,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC729,Mod2002016Key.TC730,Mod2002016Key.TC731,Mod2002016Key.TC732,Mod2002016Key.TC733,Mod2002016Key.TC734,Mod2002016Key.TC735,Mod2002016Key.TC736,Mod2002016Key.TC737,Mod2002016Key.TC738,Mod2002016Key.TC739,Mod2002016Key.TC740,Mod2002016Key.TC741,Mod2002016Key.TC742})
		,ROW22(AON.MSG.ecpnMsg35(),true ,true ,true ,true ,new Mod2002016Key[] {Mod2002016Key.TC632,Mod2002016Key.TC633,Mod2002016Key.TC634,Mod2002016Key.TC635,Mod2002016Key.TC636,Mod2002016Key.TC637,Mod2002016Key.TC638,Mod2002016Key.TC639,Mod2002016Key.TC640,Mod2002016Key.TC641,Mod2002016Key.TC642,Mod2002016Key.TC643,Mod2002016Key.TC644,Mod2002016Key.TC645})
		;
		 
		private String name;
		private boolean title;
		private boolean normal;
		private boolean abbreviate;
		private boolean pymes;
		private Mod2002016Key[] keys;
		
		private Page7Row(String name,boolean title,boolean normal,boolean abbreviate,boolean pymes,Mod2002016Key[] keys) {
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
		public Mod2002016Key[] getKeys() {
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
		int tableCol = 0;
		for (int col = 0; col < Page7Column.values().length; col++) {
			boolean colVisible  = (
					   (mod200Object.getMod200().getBalanceType() == BalanceType.NORMAL && Page7Column.values()[col].isNormal())
					|| (mod200Object.getMod200().getBalanceType() == BalanceType.ABREVIADO && Page7Column.values()[col].isAbbreviate())
					|| (mod200Object.getMod200().getBalanceType() == BalanceType.PYMES && Page7Column.values()[col].isPymes())
					);
			if (colVisible) {
				label = new Label(Page7Column.values()[col].getName());
				table.setWidget(0, tableCol, label);
				table.getColumnFormatter().setWidth(tableCol, (col == 0)?"150px":"100px");
				table.getFlexCellFormatter().addStyleName(0, tableCol, AON.AON_CSS.aonTextCenter());
				++tableCol;
			}
		}
//		for (int col = 0; col < COLS.length; col++) {
//			label = new Label(COLS[col]);
//			table.setWidget(0, col, label);
//			table.getColumnFormatter().setWidth(col, (col == 0)?"150px":"100px");
//			table.getFlexCellFormatter().addStyleName(0, col, tableStyle.cellTableStyle().cellTableHeader());
//			table.getFlexCellFormatter().addStyleName(0, col, AON.AON_CSS.aonTextCenter());	
//		}
		
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
				tableCol = 1;
				for (int col = 1; col < Page7Column.values().length; col++) {
					boolean colVisible  = (
							   (mod200Object.getMod200().getBalanceType() == BalanceType.NORMAL && Page7Column.values()[col].isNormal())
							|| (mod200Object.getMod200().getBalanceType() == BalanceType.ABREVIADO && Page7Column.values()[col].isAbbreviate())
							|| (mod200Object.getMod200().getBalanceType() == BalanceType.PYMES && Page7Column.values()[col].isPymes())
							);
					if (colVisible) {
						FlowPanel panel = new FlowPanel();
						panel.setStyleName(AON.AON_CSS.aonNowrap());
						final Mod2002016Key key = Page7Row.values()[row].getKeys()[col - 1];
						if (key != null) {
							BoxLabel code = new BoxLabel(key.getCode( mod200Object.getAdministration() ));
							panel.add(code);
							getLabels().put(key, code);
							
							final DoubleBox text = new DoubleBox(8);
							text.addChangeHandler(new ChangeHandler() {
								@Override
								public void onChange(ChangeEvent event) {
									try {
										if (AonStringUtils.isEmpty(text.getText())) {
											text.setValue(0.0,false);
										}
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
							text.setTabIndex((tableCol * 100 + row));
							getInputs().put(key, text);
							panel.add(text);
							table.setWidget(row, tableCol, panel);
						} else {
							table.setWidget(row, tableCol, panel);
						}
						++tableCol;
					}
				}
			}
		}
		
	}
	
}
