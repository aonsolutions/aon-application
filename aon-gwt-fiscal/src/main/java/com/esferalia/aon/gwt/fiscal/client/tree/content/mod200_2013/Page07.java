package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.mod200.Mod200.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200.Mod200Key;
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
			AON.MSG.ecpnMsg1(),AON.MSG.ecpnMsg2(),AON.MSG.ecpnMsg3(),
			AON.MSG.ecpnMsg4(),AON.MSG.ecpnMsg5(),AON.MSG.ecpnMsg6(),
			AON.MSG.ecpnMsg7(),AON.MSG.ecpnMsg8(),AON.MSG.ecpnMsg9(),
			AON.MSG.ecpnMsg10(),AON.MSG.ecpnMsg11(),AON.MSG.ecpnMsg12(),
			AON.MSG.ecpnMsg13(),AON.MSG.ecpnMsg14()
	};
	
	private static enum Page7Row {
		 ROW1 (""             ,false,true ,true ,true ,null )
		,ROW2 (AON.MSG.ecpnMsg15(),true ,true ,true ,true ,new Mod200Key[] {Mod200Key.TC380,Mod200Key.TC381,Mod200Key.TC382,Mod200Key.TC383,Mod200Key.TC384,Mod200Key.TC385,Mod200Key.TC386,Mod200Key.TC387,Mod200Key.TC388,Mod200Key.TC389,Mod200Key.TC390,Mod200Key.TC391,Mod200Key.TC392,Mod200Key.TC393})
		,ROW3 (AON.MSG.ecpnMsg16(),false,true ,true ,true ,new Mod200Key[] {Mod200Key.TC394,Mod200Key.TC395,Mod200Key.TC396,Mod200Key.TC397,Mod200Key.TC398,Mod200Key.TC399,Mod200Key.TC400,Mod200Key.TC401,Mod200Key.TC402,Mod200Key.TC403,Mod200Key.TC404,Mod200Key.TC405,Mod200Key.TC406,Mod200Key.TC407})
		,ROW4 (AON.MSG.ecpnMsg17(),false,true ,true ,true ,new Mod200Key[] {Mod200Key.TC408,Mod200Key.TC409,Mod200Key.TC410,Mod200Key.TC411,Mod200Key.TC412,Mod200Key.TC413,Mod200Key.TC414,Mod200Key.TC415,Mod200Key.TC416,Mod200Key.TC417,Mod200Key.TC418,Mod200Key.TC419,Mod200Key.TC420,Mod200Key.TC421})
		,ROW5 (AON.MSG.ecpnMsg18(),true ,true ,true ,true ,new Mod200Key[] {Mod200Key.TC422,Mod200Key.TC423,Mod200Key.TC424,Mod200Key.TC425,Mod200Key.TC426,Mod200Key.TC427,Mod200Key.TC428,Mod200Key.TC429,Mod200Key.TC430,Mod200Key.TC431,Mod200Key.TC432,Mod200Key.TC433,Mod200Key.TC434,Mod200Key.TC435})
		,ROW6 (AON.MSG.ecpnMsg19(),false,true ,true ,false,new Mod200Key[] {Mod200Key.TC436,Mod200Key.TC437,Mod200Key.TC438,Mod200Key.TC439,Mod200Key.TC440,Mod200Key.TC441,Mod200Key.TC442,Mod200Key.TC443,Mod200Key.TC444,Mod200Key.TC445,Mod200Key.TC446,null           ,Mod200Key.TC448,Mod200Key.TC449})
		,ROW7 (AON.MSG.ecpnMsg20(),false,false,false,true ,new Mod200Key[] {Mod200Key.TC450,Mod200Key.TC451,Mod200Key.TC452,Mod200Key.TC453,Mod200Key.TC454,Mod200Key.TC455,Mod200Key.TC456,Mod200Key.TC457,Mod200Key.TC458,null           ,null           ,Mod200Key.TC461,Mod200Key.TC462,Mod200Key.TC463})
		,ROW8 (AON.MSG.ecpnMsg21(),true ,false,false,true ,new Mod200Key[] {Mod200Key.TC464,Mod200Key.TC465,Mod200Key.TC466,Mod200Key.TC467,Mod200Key.TC468,Mod200Key.TC469,Mod200Key.TC470,Mod200Key.TC471,Mod200Key.TC472,null           ,null           ,Mod200Key.TC475,Mod200Key.TC476,Mod200Key.TC477})
		,ROW9 (AON.MSG.ecpnMsg22(),false,false,false,true ,new Mod200Key[] {Mod200Key.TC478,Mod200Key.TC479,Mod200Key.TC480,Mod200Key.TC481,Mod200Key.TC482,Mod200Key.TC483,Mod200Key.TC484,Mod200Key.TC485,Mod200Key.TC486,null           ,null           ,Mod200Key.TC489,Mod200Key.TC490,Mod200Key.TC491})
		,ROW10(AON.MSG.ecpnMsg23(),false,false,false,true ,new Mod200Key[] {Mod200Key.TC492,Mod200Key.TC493,Mod200Key.TC494,Mod200Key.TC495,Mod200Key.TC496,Mod200Key.TC497,Mod200Key.TC498,Mod200Key.TC499,Mod200Key.TC502,null           ,null           ,Mod200Key.TC503,Mod200Key.TC504,Mod200Key.TC505})
		,ROW11(AON.MSG.ecpnMsg24(),true ,true ,true ,true ,new Mod200Key[] {Mod200Key.TC506,Mod200Key.TC507,Mod200Key.TC508,Mod200Key.TC509,Mod200Key.TC510,Mod200Key.TC511,Mod200Key.TC512,Mod200Key.TC513,Mod200Key.TC514,Mod200Key.TC515,Mod200Key.TC516,Mod200Key.TC517,Mod200Key.TC518,Mod200Key.TC519})
		,ROW12(AON.MSG.ecpnMsg25(),false,true ,true ,true ,new Mod200Key[] {Mod200Key.TC520,Mod200Key.TC521,Mod200Key.TC522,Mod200Key.TC523,Mod200Key.TC524,Mod200Key.TC525,Mod200Key.TC526,Mod200Key.TC527,Mod200Key.TC528,Mod200Key.TC529,Mod200Key.TC530,Mod200Key.TC531,Mod200Key.TC532,Mod200Key.TC533})
		,ROW13(AON.MSG.ecpnMsg26(),false,true ,true ,true ,new Mod200Key[] {Mod200Key.TC534,Mod200Key.TC535,Mod200Key.TC536,Mod200Key.TC537,Mod200Key.TC538,Mod200Key.TC539,Mod200Key.TC540,Mod200Key.TC541,Mod200Key.TC542,Mod200Key.TC543,Mod200Key.TC544,Mod200Key.TC545,Mod200Key.TC546,Mod200Key.TC547})
		,ROW14(AON.MSG.ecpnMsg27(),false,true ,false,false,new Mod200Key[] {Mod200Key.TC548,Mod200Key.TC549,Mod200Key.TC550,Mod200Key.TC551,Mod200Key.TC552,Mod200Key.TC553,Mod200Key.TC554,Mod200Key.TC555,Mod200Key.TC556,Mod200Key.TC557,Mod200Key.TC558,null           ,Mod200Key.TC560,Mod200Key.TC561})
		,ROW15(AON.MSG.ecpnMsg28(),false,true ,false,false,new Mod200Key[] {Mod200Key.TC562,Mod200Key.TC563,Mod200Key.TC564,Mod200Key.TC565,Mod200Key.TC566,Mod200Key.TC567,Mod200Key.TC568,Mod200Key.TC569,Mod200Key.TC570,Mod200Key.TC571,Mod200Key.TC572,null           ,Mod200Key.TC574,Mod200Key.TC575})
		,ROW16(AON.MSG.ecpnMsg29(),false,true ,false,false,new Mod200Key[] {Mod200Key.TC576,Mod200Key.TC577,Mod200Key.TC578,Mod200Key.TC579,Mod200Key.TC580,Mod200Key.TC581,Mod200Key.TC582,Mod200Key.TC583,Mod200Key.TC584,Mod200Key.TC585,Mod200Key.TC586,null           ,Mod200Key.TC588,Mod200Key.TC589})
		,ROW17(AON.MSG.ecpnMsg30(),false,true ,false,false,new Mod200Key[] {Mod200Key.TC590,Mod200Key.TC591,Mod200Key.TC592,Mod200Key.TC593,Mod200Key.TC594,Mod200Key.TC595,Mod200Key.TC596,Mod200Key.TC597,Mod200Key.TC598,Mod200Key.TC599,Mod200Key.TC600,null           ,Mod200Key.TC602,Mod200Key.TC603})
		,ROW18(AON.MSG.ecpnMsg31(),false,true ,true ,true ,new Mod200Key[] {Mod200Key.TC604,Mod200Key.TC605,Mod200Key.TC606,Mod200Key.TC607,Mod200Key.TC608,Mod200Key.TC609,Mod200Key.TC610,Mod200Key.TC611,Mod200Key.TC612,Mod200Key.TC613,Mod200Key.TC614,Mod200Key.TC615,Mod200Key.TC616,Mod200Key.TC617})
		,ROW19(AON.MSG.ecpnMsg32(),true ,true ,true ,true ,new Mod200Key[] {Mod200Key.TC618,Mod200Key.TC619,Mod200Key.TC620,Mod200Key.TC621,Mod200Key.TC622,Mod200Key.TC623,Mod200Key.TC624,Mod200Key.TC625,Mod200Key.TC626,Mod200Key.TC627,Mod200Key.TC628,Mod200Key.TC629,Mod200Key.TC630,Mod200Key.TC631})
		,ROW20(AON.MSG.ecpnMsg33(),false,true ,true ,true ,new Mod200Key[] {Mod200Key.TC715,Mod200Key.TC716,Mod200Key.TC717,Mod200Key.TC718,Mod200Key.TC719,Mod200Key.TC720,Mod200Key.TC721,Mod200Key.TC722,Mod200Key.TC723,Mod200Key.TC724,Mod200Key.TC725,Mod200Key.TC726,Mod200Key.TC727,Mod200Key.TC728})
		,ROW21(AON.MSG.ecpnMsg34(),false,true ,true ,true ,new Mod200Key[] {Mod200Key.TC729,Mod200Key.TC730,Mod200Key.TC731,Mod200Key.TC732,Mod200Key.TC733,Mod200Key.TC734,Mod200Key.TC735,Mod200Key.TC736,Mod200Key.TC737,Mod200Key.TC738,Mod200Key.TC739,Mod200Key.TC740,Mod200Key.TC741,Mod200Key.TC742})
		,ROW22(AON.MSG.ecpnMsg35(),true ,true ,true ,true ,new Mod200Key[] {Mod200Key.TC632,Mod200Key.TC633,Mod200Key.TC634,Mod200Key.TC635,Mod200Key.TC636,Mod200Key.TC637,Mod200Key.TC638,Mod200Key.TC639,Mod200Key.TC640,Mod200Key.TC641,Mod200Key.TC642,Mod200Key.TC643,Mod200Key.TC644,Mod200Key.TC645})
		;
		 
		private String name;
		private boolean title;
		private boolean normal;
		private boolean abbreviate;
		private boolean pymes;
		private Mod200Key[] keys;
		
		private Page7Row(String name,boolean title,boolean normal,boolean abbreviate,boolean pymes,Mod200Key[] keys) {
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
		public Mod200Key[] getKeys() {
			return keys;
		}
	}

	public Page07() {
		super();
		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	protected void initializeTable() {
		paintHeaderTable(this.mod200Object.getMod200());		
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
					final Mod200Key key = Page7Row.values()[row].getKeys()[col - 1];
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
