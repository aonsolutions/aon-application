package com.esferalia.aon.gwt.fiscal.client.mod200.e2018;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2018.Model2002018.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018.EcpnType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

// ECPN - Estado total de cambios en el patrimonio neto
public class Page07 extends PageAbs {

	private FlexTable table;
	
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
	
	private static enum Page7Row {
		 ROW1 (""             ,false,true ,true ,true ,null )
		,ROW2 (AON.MSG.ecpnMsg15(),true ,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC380,Mod2002018Key.TC381,Mod2002018Key.TC382,Mod2002018Key.TC383,Mod2002018Key.TC384,Mod2002018Key.TC385,Mod2002018Key.TC386,Mod2002018Key.TC387,Mod2002018Key.TC388,Mod2002018Key.TC389,Mod2002018Key.TC390,Mod2002018Key.TC391,Mod2002018Key.TC392,Mod2002018Key.TC393})
		,ROW3 (AON.MSG.ecpnMsg16(),false,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC394,Mod2002018Key.TC395,Mod2002018Key.TC396,Mod2002018Key.TC397,Mod2002018Key.TC398,Mod2002018Key.TC399,Mod2002018Key.TC400,Mod2002018Key.TC401,Mod2002018Key.TC402,Mod2002018Key.TC403,Mod2002018Key.TC404,Mod2002018Key.TC405,Mod2002018Key.TC406,Mod2002018Key.TC407})
		,ROW4 (AON.MSG.ecpnMsg17(),false,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC408,Mod2002018Key.TC409,Mod2002018Key.TC410,Mod2002018Key.TC411,Mod2002018Key.TC412,Mod2002018Key.TC413,Mod2002018Key.TC414,Mod2002018Key.TC415,Mod2002018Key.TC416,Mod2002018Key.TC417,Mod2002018Key.TC418,Mod2002018Key.TC419,Mod2002018Key.TC420,Mod2002018Key.TC421})
		,ROW5 (AON.MSG.ecpnMsg18(),true ,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC422,Mod2002018Key.TC423,Mod2002018Key.TC424,Mod2002018Key.TC425,Mod2002018Key.TC426,Mod2002018Key.TC427,Mod2002018Key.TC428,Mod2002018Key.TC429,Mod2002018Key.TC430,Mod2002018Key.TC431,Mod2002018Key.TC432,Mod2002018Key.TC433,Mod2002018Key.TC434,Mod2002018Key.TC435})
		,ROW6 (AON.MSG.ecpnMsg19(),false,true ,true ,false,new Mod2002018Key[] {Mod2002018Key.TC436,Mod2002018Key.TC437,Mod2002018Key.TC438,Mod2002018Key.TC439,Mod2002018Key.TC440,Mod2002018Key.TC441,Mod2002018Key.TC442,Mod2002018Key.TC443,Mod2002018Key.TC444,Mod2002018Key.TC445,Mod2002018Key.TC446,null           ,Mod2002018Key.TC448,Mod2002018Key.TC449})
		,ROW7 (AON.MSG.ecpnMsg20(),false,false,false,true ,new Mod2002018Key[] {Mod2002018Key.TC450,Mod2002018Key.TC451,Mod2002018Key.TC452,Mod2002018Key.TC453,Mod2002018Key.TC454,Mod2002018Key.TC455,Mod2002018Key.TC456,Mod2002018Key.TC457,Mod2002018Key.TC458,null           ,null           ,Mod2002018Key.TC461,Mod2002018Key.TC462,Mod2002018Key.TC463})
		,ROW8 (AON.MSG.ecpnMsg21(),true ,false,false,true ,new Mod2002018Key[] {Mod2002018Key.TC464,Mod2002018Key.TC465,Mod2002018Key.TC466,Mod2002018Key.TC467,Mod2002018Key.TC468,Mod2002018Key.TC469,Mod2002018Key.TC470,Mod2002018Key.TC471,Mod2002018Key.TC472,null           ,null           ,Mod2002018Key.TC475,Mod2002018Key.TC476,Mod2002018Key.TC477})
		,ROW9 (AON.MSG.ecpnMsg22(),false,false,false,true ,new Mod2002018Key[] {Mod2002018Key.TC478,Mod2002018Key.TC479,Mod2002018Key.TC480,Mod2002018Key.TC481,Mod2002018Key.TC482,Mod2002018Key.TC483,Mod2002018Key.TC484,Mod2002018Key.TC485,Mod2002018Key.TC486,null           ,null           ,Mod2002018Key.TC489,Mod2002018Key.TC490,Mod2002018Key.TC491})
		,ROW10(AON.MSG.ecpnMsg23(),false,false,false,true ,new Mod2002018Key[] {Mod2002018Key.TC492,Mod2002018Key.TC493,Mod2002018Key.TC494,Mod2002018Key.TC495,Mod2002018Key.TC496,Mod2002018Key.TC497,Mod2002018Key.TC498,Mod2002018Key.TC499,Mod2002018Key.TC502,null           ,null           ,Mod2002018Key.TC503,Mod2002018Key.TC504,Mod2002018Key.TC505})
		,ROW11(AON.MSG.ecpnMsg24(),true ,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC506,Mod2002018Key.TC507,Mod2002018Key.TC508,Mod2002018Key.TC509,Mod2002018Key.TC510,Mod2002018Key.TC511,Mod2002018Key.TC512,Mod2002018Key.TC513,Mod2002018Key.TC514,Mod2002018Key.TC515,Mod2002018Key.TC516,Mod2002018Key.TC517,Mod2002018Key.TC518,Mod2002018Key.TC519})
		,ROW12(AON.MSG.ecpnMsg25(),false,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC520,Mod2002018Key.TC521,Mod2002018Key.TC522,Mod2002018Key.TC523,Mod2002018Key.TC524,Mod2002018Key.TC525,Mod2002018Key.TC526,Mod2002018Key.TC527,Mod2002018Key.TC528,Mod2002018Key.TC529,Mod2002018Key.TC530,Mod2002018Key.TC531,Mod2002018Key.TC532,Mod2002018Key.TC533})
		,ROW13(AON.MSG.ecpnMsg26(),false,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC534,Mod2002018Key.TC535,Mod2002018Key.TC536,Mod2002018Key.TC537,Mod2002018Key.TC538,Mod2002018Key.TC539,Mod2002018Key.TC540,Mod2002018Key.TC541,Mod2002018Key.TC542,Mod2002018Key.TC543,Mod2002018Key.TC544,Mod2002018Key.TC545,Mod2002018Key.TC546,Mod2002018Key.TC547})
		,ROW14(AON.MSG.ecpnMsg27(),false,true ,false,false,new Mod2002018Key[] {Mod2002018Key.TC548,Mod2002018Key.TC549,Mod2002018Key.TC550,Mod2002018Key.TC551,Mod2002018Key.TC552,Mod2002018Key.TC553,Mod2002018Key.TC554,Mod2002018Key.TC555,Mod2002018Key.TC556,Mod2002018Key.TC557,Mod2002018Key.TC558,null           ,Mod2002018Key.TC560,Mod2002018Key.TC561})
		,ROW15(AON.MSG.ecpnMsg28(),false,true ,false,false,new Mod2002018Key[] {Mod2002018Key.TC562,Mod2002018Key.TC563,Mod2002018Key.TC564,Mod2002018Key.TC565,Mod2002018Key.TC566,Mod2002018Key.TC567,Mod2002018Key.TC568,Mod2002018Key.TC569,Mod2002018Key.TC570,Mod2002018Key.TC571,Mod2002018Key.TC572,null           ,Mod2002018Key.TC574,Mod2002018Key.TC575})
		,ROW16(AON.MSG.ecpnMsg29(),false,true ,false,false,new Mod2002018Key[] {Mod2002018Key.TC576,Mod2002018Key.TC577,Mod2002018Key.TC578,Mod2002018Key.TC579,Mod2002018Key.TC580,Mod2002018Key.TC581,Mod2002018Key.TC582,Mod2002018Key.TC583,Mod2002018Key.TC584,Mod2002018Key.TC585,Mod2002018Key.TC586,null           ,Mod2002018Key.TC588,Mod2002018Key.TC589})
		,ROW17(AON.MSG.ecpnMsg30(),false,true ,false,false,new Mod2002018Key[] {Mod2002018Key.TC590,Mod2002018Key.TC591,Mod2002018Key.TC592,Mod2002018Key.TC593,Mod2002018Key.TC594,Mod2002018Key.TC595,Mod2002018Key.TC596,Mod2002018Key.TC597,Mod2002018Key.TC598,Mod2002018Key.TC599,Mod2002018Key.TC600,null           ,Mod2002018Key.TC602,Mod2002018Key.TC603})
		,ROW18(AON.MSG.ecpnMsg31(),false,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC604,Mod2002018Key.TC605,Mod2002018Key.TC606,Mod2002018Key.TC607,Mod2002018Key.TC608,Mod2002018Key.TC609,Mod2002018Key.TC610,Mod2002018Key.TC611,Mod2002018Key.TC612,Mod2002018Key.TC613,Mod2002018Key.TC614,Mod2002018Key.TC615,Mod2002018Key.TC616,Mod2002018Key.TC617})
		,ROW19(AON.MSG.ecpnMsg32(),true ,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC618,Mod2002018Key.TC619,Mod2002018Key.TC620,Mod2002018Key.TC621,Mod2002018Key.TC622,Mod2002018Key.TC623,Mod2002018Key.TC624,Mod2002018Key.TC625,Mod2002018Key.TC626,Mod2002018Key.TC627,Mod2002018Key.TC628,Mod2002018Key.TC629,Mod2002018Key.TC630,Mod2002018Key.TC631})
		,ROW20(AON.MSG.ecpnMsg33(),false,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC715,Mod2002018Key.TC716,Mod2002018Key.TC717,Mod2002018Key.TC718,Mod2002018Key.TC719,Mod2002018Key.TC720,Mod2002018Key.TC721,Mod2002018Key.TC722,Mod2002018Key.TC723,Mod2002018Key.TC724,Mod2002018Key.TC725,Mod2002018Key.TC726,Mod2002018Key.TC727,Mod2002018Key.TC728})
		,ROW21(AON.MSG.ecpnMsg34(),false,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC729,Mod2002018Key.TC730,Mod2002018Key.TC731,Mod2002018Key.TC732,Mod2002018Key.TC733,Mod2002018Key.TC734,Mod2002018Key.TC735,Mod2002018Key.TC736,Mod2002018Key.TC737,Mod2002018Key.TC738,Mod2002018Key.TC739,Mod2002018Key.TC740,Mod2002018Key.TC741,Mod2002018Key.TC742})
		,ROW22(AON.MSG.ecpnMsg35(),true ,true ,true ,true ,new Mod2002018Key[] {Mod2002018Key.TC632,Mod2002018Key.TC633,Mod2002018Key.TC634,Mod2002018Key.TC635,Mod2002018Key.TC636,Mod2002018Key.TC637,Mod2002018Key.TC638,Mod2002018Key.TC639,Mod2002018Key.TC640,Mod2002018Key.TC641,Mod2002018Key.TC642,Mod2002018Key.TC643,Mod2002018Key.TC644,Mod2002018Key.TC645})
		;
		 
		private String name;
		private boolean title;
		private boolean normal;
		private boolean abbreviate;
		private boolean pymes;
		private Mod2002018Key[] keys;
		
		private Page7Row(String name,boolean title,boolean normal,boolean abbreviate,boolean pymes,Mod2002018Key[] keys) {
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
		public Mod2002018Key[] getKeys() {
			return keys;
		}
	}

	public Page07( Model200PageCallback callback ) {
		super(callback);
		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel baseContainerPanel = new FlowPanel();
		baseContainerPanel.setStyleName(AON.AON_CSS.aonFiscalContainer());
		
		FlowPanel groupPanel= new FlowPanel();
		groupPanel.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel groupHeaderPanel = new FlowPanel();
				groupHeaderPanel.setStyleName(AON.AON_CSS.aonGroupTitle());
				groupHeaderPanel.add (new InlineLabel(AON.MSG.patrimonioCambios())); 
				groupPanel.add(groupHeaderPanel);
				
				FlowPanel groupBodyPanel = new FlowPanel();
				groupBodyPanel.setStyleName(AON.AON_CSS.aonGroupBody());
				
				FlowPanel tableContainer = new FlowPanel();
				tableContainer.setStyleName(AON.AON_CSS.aonBorderBottom());
				tableContainer.addStyleName(AON.AON_CSS.aonFiscalScrollTableWrapper());
				table = new FlexTable();
				table.setStyleName(AON.AON_CSS.aonMarginBottom());
				tableContainer.add(table);
				groupBodyPanel.add(tableContainer);
				groupPanel.add(groupBodyPanel);
		
		baseContainerPanel.add(groupPanel);	
		container.add(baseContainerPanel);
		initWidget(container);
		initializeTable();		
		
	}

	@Override
	protected void initializeTable() {
		table.setCellSpacing(0);
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
						final Mod2002018Key key = Page7Row.values()[row].getKeys()[col - 1];
						if (key != null) {
							BoxLabel code = new BoxLabel(key.getCode( callback.getMod200Object().getAdministration() ));
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
										callback.getMod200Object().doubleValueChanged(key, d);
									} catch (ParseException e) {
										// nothing.
									}
								}
							});
							text.setValue(callback.getMod200Object().getDoubleValue(key));
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
	
	@Override
	protected void populate() {}
	
	@Override
	protected boolean isAvailable() {
		return super.isAvailable()
  		  && ( (callback.getMod200Object().getMod200().isChecked(Mod2002018Key.C0075)) ||
  			   (callback.getMod200Object().getMod200().isChecked(Mod2002018Key.C0076)) ||
  			   (callback.getMod200Object().getMod200().isChecked(Mod2002018Key.C0077)) );  				  
	}	
	
}
