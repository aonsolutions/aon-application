// ECPN: ESTADO TOTAL DE CAMBIOS EN EL PATRIMONIO NETO
package com.esferalia.aon.gwt.mod200.client.mod200.e2024;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2024.Model2002024.Model2002024PageCallback;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class Page08 extends PageAbs {

	private enum Page7Column {
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
	
	private enum Page7Row {
		 ROW1 (""                 ,false,true ,true ,true ,null )
		,ROW2 (AON.MSG.ecpnMsg15(),true ,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC380,Mod2002024Key.TC381,Mod2002024Key.TC382,Mod2002024Key.TC383,Mod2002024Key.TC384,Mod2002024Key.TC385,Mod2002024Key.TC386,Mod2002024Key.TC387,Mod2002024Key.TC388,Mod2002024Key.TC389,Mod2002024Key.TC390,Mod2002024Key.TC391,Mod2002024Key.TC392,Mod2002024Key.TC393})
		,ROW3 (AON.MSG.ecpnMsg16(),false,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC394,Mod2002024Key.TC395,Mod2002024Key.TC396,Mod2002024Key.TC397,Mod2002024Key.TC398,Mod2002024Key.TC399,Mod2002024Key.TC400,Mod2002024Key.TC401,Mod2002024Key.TC402,Mod2002024Key.TC403,Mod2002024Key.TC404,Mod2002024Key.TC405,Mod2002024Key.TC406,Mod2002024Key.TC407})
		,ROW4 (AON.MSG.ecpnMsg17(),false,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC408,Mod2002024Key.TC409,Mod2002024Key.TC410,Mod2002024Key.TC411,Mod2002024Key.TC412,Mod2002024Key.TC413,Mod2002024Key.TC414,Mod2002024Key.TC415,Mod2002024Key.TC416,Mod2002024Key.TC417,Mod2002024Key.TC418,Mod2002024Key.TC419,Mod2002024Key.TC420,Mod2002024Key.TC421})
		,ROW5 (AON.MSG.ecpnMsg18(),true ,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC422,Mod2002024Key.TC423,Mod2002024Key.TC424,Mod2002024Key.TC425,Mod2002024Key.TC426,Mod2002024Key.TC427,Mod2002024Key.TC428,Mod2002024Key.TC429,Mod2002024Key.TC430,Mod2002024Key.TC431,Mod2002024Key.TC432,Mod2002024Key.TC433,Mod2002024Key.TC434,Mod2002024Key.TC435})
		,ROW6 (AON.MSG.ecpnMsg19(),false,true ,true ,false,new Mod2002024Key[] {Mod2002024Key.TC436,Mod2002024Key.TC437,Mod2002024Key.TC438,Mod2002024Key.TC439,Mod2002024Key.TC440,Mod2002024Key.TC441,Mod2002024Key.TC442,Mod2002024Key.TC443,Mod2002024Key.TC444,Mod2002024Key.TC445,Mod2002024Key.TC446,null               ,Mod2002024Key.TC448,Mod2002024Key.TC449})
		,ROW7 (AON.MSG.ecpnMsg20(),false,false,false,true ,new Mod2002024Key[] {Mod2002024Key.TC450,Mod2002024Key.TC451,Mod2002024Key.TC452,Mod2002024Key.TC453,Mod2002024Key.TC454,Mod2002024Key.TC455,Mod2002024Key.TC456,Mod2002024Key.TC457,Mod2002024Key.TC458,null           	   ,null               ,Mod2002024Key.TC461,Mod2002024Key.TC462,Mod2002024Key.TC463})
		,ROW8 (AON.MSG.ecpnMsg21(),true ,false,false,true ,new Mod2002024Key[] {Mod2002024Key.TC464,Mod2002024Key.TC465,Mod2002024Key.TC466,Mod2002024Key.TC467,Mod2002024Key.TC468,Mod2002024Key.TC469,Mod2002024Key.TC470,Mod2002024Key.TC471,Mod2002024Key.TC472,null               ,null               ,Mod2002024Key.TC475,Mod2002024Key.TC476,Mod2002024Key.TC477})
		,ROW9 (AON.MSG.ecpnMsg22(),false,false,false,true ,new Mod2002024Key[] {Mod2002024Key.TC478,Mod2002024Key.TC479,Mod2002024Key.TC480,Mod2002024Key.TC481,Mod2002024Key.TC482,Mod2002024Key.TC483,Mod2002024Key.TC484,Mod2002024Key.TC485,Mod2002024Key.TC486,null               ,null               ,Mod2002024Key.TC489,Mod2002024Key.TC490,Mod2002024Key.TC491})
		,ROW10(AON.MSG.ecpnMsg23(),false,false,false,true ,new Mod2002024Key[] {Mod2002024Key.TC492,Mod2002024Key.TC493,Mod2002024Key.TC494,Mod2002024Key.TC495,Mod2002024Key.TC496,Mod2002024Key.TC497,Mod2002024Key.TC498,Mod2002024Key.TC499,Mod2002024Key.TC502,null               ,null               ,Mod2002024Key.TC503,Mod2002024Key.TC504,Mod2002024Key.TC505})
		,ROW11(AON.MSG.ecpnMsg24(),true ,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC506,Mod2002024Key.TC507,Mod2002024Key.TC508,Mod2002024Key.TC509,Mod2002024Key.TC510,Mod2002024Key.TC511,Mod2002024Key.TC512,Mod2002024Key.TC513,Mod2002024Key.TC514,Mod2002024Key.TC515,Mod2002024Key.TC516,Mod2002024Key.TC517,Mod2002024Key.TC518,Mod2002024Key.TC519})
		,ROW12(AON.MSG.ecpnMsg25(),false,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC520,Mod2002024Key.TC521,Mod2002024Key.TC522,Mod2002024Key.TC523,Mod2002024Key.TC524,Mod2002024Key.TC525,Mod2002024Key.TC526,Mod2002024Key.TC527,Mod2002024Key.TC528,Mod2002024Key.TC529,Mod2002024Key.TC530,Mod2002024Key.TC531,Mod2002024Key.TC532,Mod2002024Key.TC533})
		,ROW13(AON.MSG.ecpnMsg26(),false,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC534,Mod2002024Key.TC535,Mod2002024Key.TC536,Mod2002024Key.TC537,Mod2002024Key.TC538,Mod2002024Key.TC539,Mod2002024Key.TC540,Mod2002024Key.TC541,Mod2002024Key.TC542,Mod2002024Key.TC543,Mod2002024Key.TC544,Mod2002024Key.TC545,Mod2002024Key.TC546,Mod2002024Key.TC547})
		,ROW14(AON.MSG.ecpnMsg27(),false,true ,false,false,new Mod2002024Key[] {Mod2002024Key.TC548,Mod2002024Key.TC549,Mod2002024Key.TC550,Mod2002024Key.TC551,Mod2002024Key.TC552,Mod2002024Key.TC553,Mod2002024Key.TC554,Mod2002024Key.TC555,Mod2002024Key.TC556,Mod2002024Key.TC557,Mod2002024Key.TC558,null               ,Mod2002024Key.TC560,Mod2002024Key.TC561})
		,ROW15(AON.MSG.ecpnMsg28(),false,true ,false,false,new Mod2002024Key[] {Mod2002024Key.TC562,Mod2002024Key.TC563,Mod2002024Key.TC564,Mod2002024Key.TC565,Mod2002024Key.TC566,Mod2002024Key.TC567,Mod2002024Key.TC568,Mod2002024Key.TC569,Mod2002024Key.TC570,Mod2002024Key.TC571,Mod2002024Key.TC572,null               ,Mod2002024Key.TC574,Mod2002024Key.TC575})
		,ROW16(AON.MSG.ecpnMsg29(),false,true ,false,false,new Mod2002024Key[] {Mod2002024Key.TC576,Mod2002024Key.TC577,Mod2002024Key.TC578,Mod2002024Key.TC579,Mod2002024Key.TC580,Mod2002024Key.TC581,Mod2002024Key.TC582,Mod2002024Key.TC583,Mod2002024Key.TC584,Mod2002024Key.TC585,Mod2002024Key.TC586,null               ,Mod2002024Key.TC588,Mod2002024Key.TC589})
		,ROW17(AON.MSG.ecpnMsg30(),false,true ,false,false,new Mod2002024Key[] {Mod2002024Key.TC590,Mod2002024Key.TC591,Mod2002024Key.TC592,Mod2002024Key.TC593,Mod2002024Key.TC594,Mod2002024Key.TC595,Mod2002024Key.TC596,Mod2002024Key.TC597,Mod2002024Key.TC598,Mod2002024Key.TC599,Mod2002024Key.TC600,null               ,Mod2002024Key.TC602,Mod2002024Key.TC603})
		,ROW18(AON.MSG.ecpnMsg31(),false,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC604,Mod2002024Key.TC605,Mod2002024Key.TC606,Mod2002024Key.TC607,Mod2002024Key.TC608,Mod2002024Key.TC609,Mod2002024Key.TC610,Mod2002024Key.TC611,Mod2002024Key.TC612,Mod2002024Key.TC613,Mod2002024Key.TC614,Mod2002024Key.TC615,Mod2002024Key.TC616,Mod2002024Key.TC617})
		,ROW19(AON.MSG.ecpnMsg32(),true ,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC618,Mod2002024Key.TC619,Mod2002024Key.TC620,Mod2002024Key.TC621,Mod2002024Key.TC622,Mod2002024Key.TC623,Mod2002024Key.TC624,Mod2002024Key.TC625,Mod2002024Key.TC626,Mod2002024Key.TC627,Mod2002024Key.TC628,Mod2002024Key.TC629,Mod2002024Key.TC630,Mod2002024Key.TC631})
		,ROW20(AON.MSG.ecpnMsg33(),false,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC715,Mod2002024Key.TC716,Mod2002024Key.TC717,Mod2002024Key.TC718,Mod2002024Key.TC719,Mod2002024Key.TC720,Mod2002024Key.TC721,Mod2002024Key.TC722,Mod2002024Key.TC723,Mod2002024Key.TC724,Mod2002024Key.TC725,Mod2002024Key.TC726,Mod2002024Key.TC727,Mod2002024Key.TC728})
		,ROW21(AON.MSG.ecpnMsg34(),false,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC729,Mod2002024Key.TC730,Mod2002024Key.TC731,Mod2002024Key.TC732,Mod2002024Key.TC733,Mod2002024Key.TC734,Mod2002024Key.TC735,Mod2002024Key.TC736,Mod2002024Key.TC737,Mod2002024Key.TC738,Mod2002024Key.TC739,Mod2002024Key.TC740,Mod2002024Key.TC741,Mod2002024Key.TC742})
		,ROW22(AON.MSG.ecpnMsg35(),true ,true ,true ,true ,new Mod2002024Key[] {Mod2002024Key.TC632,Mod2002024Key.TC633,Mod2002024Key.TC634,Mod2002024Key.TC635,Mod2002024Key.TC636,Mod2002024Key.TC637,Mod2002024Key.TC638,Mod2002024Key.TC639,Mod2002024Key.TC640,Mod2002024Key.TC641,Mod2002024Key.TC642,Mod2002024Key.TC643,Mod2002024Key.TC644,Mod2002024Key.TC645})
		;
		 
		private String name;
		private boolean title;
		private boolean normal;
		private boolean abbreviate;
		private boolean pymes;
		private Mod2002024Key[] keys;
		
		private Page7Row(String name,boolean title,boolean normal,boolean abbreviate,boolean pymes,Mod2002024Key[] keys) {
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
		public Mod2002024Key[] getKeys() {
			return keys;
		}
	}

	public Page08( Model2002024PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		basePanel.add(getTitle(AON.MSG.patrimonioCambios() + " (*)"));
		
		FlexTable table = new FlexTable();		
		table.setStyleName(AON.AON_CSS.aonMarginBottom());
		
		FlowPanel tableContainer = new FlowPanel();
		tableContainer.setStyleName(AON.AON_CSS.aonBorderBottom());
		tableContainer.addStyleName(AON.AON_CSS.aonMarginBottom());
		tableContainer.addStyleName(AON.AON_CSS.aonFiscalScrollTableWrapper());		
		tableContainer.add(table);
		
		basePanel.add(tableContainer);
		
		Label label = null;
		int tableCol = 0;
		for (int col = 0; col < Page7Column.values().length; col++) {
			boolean colVisible  = (
					   (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.NORMAL && Page7Column.values()[col].isNormal())
					|| (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.ABREVIADO && Page7Column.values()[col].isAbbreviate())
					|| (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.PYMES && Page7Column.values()[col].isPymes())
					);
			if (colVisible) {
				label = new Label(Page7Column.values()[col].getName());
				table.setWidget(0, tableCol, label);
				table.getColumnFormatter().setWidth(tableCol, (col == 0)?"150px":"100px");
				table.getFlexCellFormatter().addStyleName(0, tableCol, AON.AON_CSS.aonTextCenter());
				++tableCol;
			}
		}
		
		for (int row = 1; row < Page7Row.values().length; row++) {
			boolean rowVisible  = (
				   (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.NORMAL && Page7Row.values()[row].isNormal())
				|| (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.ABREVIADO && Page7Row.values()[row].isAbbreviate())
				|| (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.PYMES && Page7Row.values()[row].isPymes())
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
							   (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.NORMAL && Page7Column.values()[col].isNormal())
							|| (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.ABREVIADO && Page7Column.values()[col].isAbbreviate())
							|| (callback.getMod200Object().getMod200().getEcpnType() == EcpnType.PYMES && Page7Column.values()[col].isPymes())
							);
					if (colVisible) {
						FlowPanel panel = new FlowPanel();
						panel.setStyleName(AON.AON_CSS.aonNowrap());
						final Mod2002024Key key = Page7Row.values()[row].getKeys()[col - 1];
						if (key != null) {
							AonBoxLabel code = new AonBoxLabel(key.getCode( callback.getMod200Object().getMod200().getAdministration() ));
							panel.add(code);
							
							final AonDoubleBox text = new AonDoubleBox(8);
							text.addChangeHandler(event -> {
								try {
									if (AonStringUtils.isEmpty(text.getText())) {
										text.setValue(0.0,false);
									}
									Double d = text.getValueOrThrow();
									text.addStyleName(AON.AON_CSS.aonChanged());
									callback.getMod200Object().doubleValueChanged(key, d);
									callback.markAsDirty();
								} catch (ParseException e) {
									// nothing.
								}								
							});
							text.setValue(callback.getMod200Object().getDoubleValue(key));
							text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
							text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
							text.setEnabled(isEditable(key));
							text.setTabIndex((tableCol * 100 + row));
							inputs.put(key, text);
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
		
		paintFooterNote(basePanel,"(*) El estado de cambios en el patrimonio neto ser\u00E1 de cumplimentaci\u00F3n voluntaria si se utiliza el modelo abreviado o PYMES del PGC.");
		paintFooterNote(basePanel, ACCOUNTING_STATEMENTS_FOOTER);
	}
	
	@Override
	protected boolean isAvailable() {
		return super.isAvailable()
  		  && ( (callback.getMod200Object().getMod200().isChecked(Mod2002024Key.C0075)) ||
  			   (callback.getMod200Object().getMod200().isChecked(Mod2002024Key.C0076)) ||
  			   (callback.getMod200Object().getMod200().isChecked(Mod2002024Key.C0077)) );  				  
	}	
	
}
