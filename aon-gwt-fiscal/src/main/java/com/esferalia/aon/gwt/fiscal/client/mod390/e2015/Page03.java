package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import java.io.Serializable;
import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015DetailKey;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;

public class Page03 extends ResizeComposite implements RequiresResize , IMod3902015Page {

	public static class Mod390DetailFields {
		private Mod390Detail detail;
		private DoubleBox taxableBase;
		private DoubleBox quota;
		
		protected Mod390DetailFields(Mod390Detail detail) {
			this.detail = detail;
			taxableBase = new DoubleBox();
			quota = new DoubleBox();
		}
		public DoubleBox getTaxableBase() {
			return taxableBase;
		}
		public void setTaxableBase(double value, boolean shouldDisplayChange) {
			this.taxableBase.setValue(value,true,shouldDisplayChange);
			detail.setTaxableBase(value);
		}
		public DoubleBox getQuota() {
			return quota;
		}
		public void setQuota(double value, boolean shouldDisplayChange) {
			this.quota.setValue(value,true,shouldDisplayChange);
			detail.setQuota(value);
		}
		public Mod390Detail getDetail() {
			return detail;
		}
		public void setDetail(Mod390Detail mod390Detail) {
			detail = mod390Detail;
		}
	} 
	
	private FlexTable table;
	
	IMod3902015CallBack cbk;
	
	private EnumMap<Mod3902015DetailKey, Mod390DetailFields> map; 
					
	public Page03(Mod3902015 m390) {
		FlowPanel container = new FlowPanel();
		table = new FlexTable();
		initializeMap(m390);
		initializeTable(m390);
		
		container.add(table);
		initWidget(container);
	}

	private void initializeMap(Mod3902015 m390) {
		map = new EnumMap<Mod3902015DetailKey, Mod390DetailFields>(Mod3902015DetailKey.class);
		for (Mod3902015DetailKey key: Mod3902015DetailKey.values() ) {
			if (key.accept(m390.getYear())) {
				Mod390Detail detail = null;
				if (m390.getGeneralRegime() != null) {
					detail = m390.getGeneralRegime().get(key);	
				}
				if (detail == null) {
					detail = new Mod390Detail();
					detail.setKey(key);
					detail.setPercent(key.getPercent());
					m390.getGeneralRegime().put(key,detail);
				}
				map.put(key, new Mod390DetailFields(detail));
			}
		}
	}

	@Override
	public void populate(Mod3902015 mod390) {
		for (Mod3902015DetailKey key : map.keySet()) {
			Mod390DetailFields f = map.get(key);
			mod390.getGeneralRegime().put(key, f.getDetail());
		}
	}

	private void initializeTable(final Mod3902015 m390) {
		if (table.getRowCount() > 0) {
			table.removeAllRows();
		}
		
		table.setWidth("100%");
		table.setStyleName(AON.AON_CSS.aonMarginTop());
		table.addStyleName(AON.AON_CSS.aonMarginBottom());
		table.setCellSpacing(0);
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(0, "auto");
		cf.addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		cf.setWidth(1, "40px");
		cf.setStyleName(1, AON.AON_CSS.aonTextCenter());
		cf.setWidth(2, "140px");
		cf.setWidth(3, "60px");
		cf.setWidth(4, "40px");
		cf.setStyleName(4, AON.AON_CSS.aonTextCenter());
		cf.setWidth(5, "20px");

		FlexCellFormatter fmt = table.getFlexCellFormatter();
		
		
		fmt.setColSpan(0, 0, 6);
		fmt.addStyleName(0, 0,AON.AON_CSS.aonPageHeader());
		table.setWidget(0, 0, new Label(AON.MSG.generalRegimeOperations()));

		int x = 1;
		int y = 0;
		int z = 1;
		int rowspan = 0;
		boolean oddRow = false;
		boolean showBorder = false;
		if (map != null) {
			for (Mod390DetailKeyGroup group: Mod390DetailKeyGroup.values() ) {
				if (group == Mod390DetailKeyGroup.DEV_001) {
					Label l = new Label(AON.MSG.outputVat());
					fmt.setColSpan(x, y, 7);
					fmt.addStyleName(x, y, AON.AON_CSS.aonBold());
					fmt.addStyleName(x, y, AON.AON_CSS.aonBorderBottom());
					table.setWidget(x, y, l);
					x++;
				} else if (group == Mod390DetailKeyGroup.DED_001) {
					Label l = new Label(AON.MSG.inputVat());
					fmt.setColSpan(x, y, 7);
					fmt.addStyleName(x, y, AON.AON_CSS.aonBold());
					fmt.addStyleName(x, y, AON.AON_CSS.aonBorderBottom());
					table.setWidget(x, y, l);
					x++;
				}

				rowspan = 0;
				showBorder = false;
				for (Mod3902015DetailKey key: group.getKeys() ) {
					if (key.accept(m390.getYear())) {
						rowspan++;		
					}
				}
				if (rowspan > 0) {
					y = 0;
					z = 1;
					oddRow = !oddRow;
					fmt.setRowSpan(x, y, rowspan);
					FlowPanel p0 = new FlowPanel();
					Label l0 = new Label(group.getLabel());
					p0.add(l0);
					p0.setHeight("100%");
					if (rowspan > 1) {
						fmt.setHeight(x, y, (rowspan * 20) + "px;");
						p0.addStyleName(AON.AON_CSS.aonCurlyLT() );
					}
					fmt.addStyleName(x, y,AON.AON_CSS.aonBorderBottomImportant() );
					table.setWidget(x, y, p0);
					y++;
					
					for (Mod3902015DetailKey key: group.getKeys() ) {
						if (key.accept(m390.getYear())) {
							final Mod390DetailFields fields = map.get(key); 
							Mod390Detail det = m390.getGeneralRegime().get(key);
							showBorder = (z == rowspan);
							
							// Taxable Base Box
							if ( key.hasTaxableBaseAvailable() ) {
								table.setWidget(x, y, new BoxLabel(det.getTaxableBaseBox()));
							}
							if (showBorder) {
								fmt.addStyleName(x, y,AON.AON_CSS.aonBorderBottom());	
							}
							++y;
							
							// Taxable Base 
							if ( key.hasTaxableBaseAvailable() ) {
								fields.getTaxableBase().setReadOnly(key.isReadonly());
								fields.getTaxableBase().setEnabled(!key.isReadonly());
								if (!key.isReadonly()) {
									final double percent = det.getPercent();
									fields.getTaxableBase().addChangeHandler(new ChangeHandler() {
										@Override
										public void onChange(ChangeEvent event) {
												fields.getDetail().setTaxableBase(fields.getTaxableBase().getValue());
												if  (percent != 0.0 && fields.getDetail().getQuota() == 0) {
													fields.setQuota( AonMathUtils.round( fields.getTaxableBase().getValue() * percent / 100), true );
												}
											cbk.calculateAndRefresh();
											//refreshMap(m390,fields.detail.getKey());
										}
									});
								}
								fields.getTaxableBase().setValue(det.getTaxableBase());
								table.setWidget(x, y, fields.getTaxableBase());
							}
							if (showBorder) {
								fmt.addStyleName(x, y,AON.AON_CSS.aonBorderBottom());	
							}
							++y;
				
							// Percent 
							FlowPanel p1 = new FlowPanel();
							if  (det.getKey().getPercent() != 0.0) {
								p1.addStyleName(AON.AON_CSS.aonTextCenter() );
								p1.addStyleName(AON.AON_CSS.aonSimpleBorder() );
								p1.add(new Label(AON.FMT.format(det.getPercent()))); 
							} 
							table.setWidget(x, y, p1);
							if (showBorder) {
								fmt.addStyleName(x, y,AON.AON_CSS.aonBorderBottom());	
							}
							++y;
							
							// Quota Box 
							if (showBorder) {
								fmt.addStyleName(x, y,AON.AON_CSS.aonBorderBottom());	
							}
							table.setWidget(x, y, new BoxLabel(det.getBox()));

							++y;
						
							// Quota
							fields.getQuota().setValue(det.getQuota());
							fields.getQuota().setReadOnly(key.isReadonly());
							fields.getQuota().setEnabled(!key.isReadonly());
							if (!key.isReadonly()) {
								fields.getQuota().addChangeHandler(new ChangeHandler() {
									
									@Override
									public void onChange(ChangeEvent event) {
										fields.getDetail().setQuota(fields.getQuota().getValue());
										cbk.calculateAndRefresh();
										//refreshMap(m390,fields.detail.getKey());
									}
								});
							}
							table.setWidget(x, y, fields.getQuota());
							if (showBorder) {
								fmt.addStyleName(x, y,AON.AON_CSS.aonBorderBottom());	
							}
							++y;
							
							Label l = new Label("");
							table.setWidget(x, y, l);
							if (showBorder) {
								fmt.addStyleName(x, y,AON.AON_CSS.aonBorderBottom());	
							}
							++x;
							++z;
							y = 0;
						}
					}
				}
			}
		}
	}

	@Override
	public void setCallback(IMod3902015CallBack callback) {
		this.cbk = callback;
	}
	
	public static enum Mod390DetailKeyGroup implements Serializable {
		  DEV_001 ("R\u00E9gimen ordinario"
				  ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K00_04,Mod3902015DetailKey.K00_10,Mod3902015DetailKey.K00_21})
		 ,DEV_002 ("Operaciones intragrupo"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K01_04,Mod3902015DetailKey.K01_10,Mod3902015DetailKey.K01_21})
		 ,DEV_003  ("R\u00E9gimen especial del criterio de caja"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K40_04,Mod3902015DetailKey.K40_10
				 ,Mod3902015DetailKey.K40_21})
		 ,DEV_004  ("R\u00E9gimen especial de bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K02_04,Mod3902015DetailKey.K02_10,Mod3902015DetailKey.K02_21})
		 ,DEV_005  ("R\u00E9gimen especial de agencias de viaje"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K03_21})
		 ,DEV_006  ("Adquisiciones intracomunitarias de bienes"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K04_04
				 ,Mod3902015DetailKey.K04_10,Mod3902015DetailKey.K04_21})
		 ,DEV_007  ("Adquisiciones intracomunitarias de servicios"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K05_04,Mod3902015DetailKey.K05_10,Mod3902015DetailKey.K05_21})
		 ,DEV_008  ("IVA devengado en otros supuestos de inversi\u00F3n del sujeto pasivo"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K06})
		 ,DEV_009  ("Modificaci\u00F3n de bases y cuotas"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K07})
		 ,DEV_010  ("Modificaci\u00F3n de bases y cuotas de operaciones intragrupo"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K07_I})
		 ,DEV_011  ("Modificaci\u00F3n de bases y cuotas por auto de declaraci\u00F3n de concurso de acreedores"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K08})
		 ,DEV_012  ("Total bases y cuotas IVA"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K09})
		 ,DEV_013  ("Recargo de equivalencia",new Mod3902015DetailKey[]{Mod3902015DetailKey.K10_05
				 ,Mod3902015DetailKey.K10_14,Mod3902015DetailKey.K10_52
				 ,Mod3902015DetailKey.K10_175})
		 ,DEV_014  ("Modificaci\u00F3n recargo equivalencia"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K11})
		 ,DEV_015  ("Modificaci\u00F3n recargo equivalencia por auto de declaraci\u00F3n de concurso de acreedores"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K12})
		 ,DEV_016  ("Total cuotas IVA y recargo de equivalencia"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K13})
		 ,DED_001  ("IVA deducible en operaciones interiores de bienes y servicios corrientes"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K14_04,Mod3902015DetailKey.K14_07
				 ,Mod3902015DetailKey.K14_08,Mod3902015DetailKey.K14_10,Mod3902015DetailKey.K14_16
				 ,Mod3902015DetailKey.K14_18,Mod3902015DetailKey.K14_21})
		 ,DED_002  ("Total bases imponibles y cuotas deducibles en operaciones interiores de bienes y servicios corrientes"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K15})
		 ,DED_003  ("IVA deducible en operaciones intragrupo de bienes y servicios corrientes"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K16_04,Mod3902015DetailKey.K16_07
				 ,Mod3902015DetailKey.K16_08,Mod3902015DetailKey.K16_10,Mod3902015DetailKey.K16_16
				 ,Mod3902015DetailKey.K16_18,Mod3902015DetailKey.K16_21})
		 ,DED_004  ("Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes y servicios corrientes"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K17})
		 ,DED_005  ("IVA deducible en operaciones interiores de bienes de inversi\u00F3n"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K18_04,Mod3902015DetailKey.K18_07
				 ,Mod3902015DetailKey.K18_08,Mod3902015DetailKey.K18_10,Mod3902015DetailKey.K18_16
				 ,Mod3902015DetailKey.K18_18,Mod3902015DetailKey.K18_21})
		 ,DED_006  ("Total bases imponibles y cuotas deducibles en operaciones interiores de bienes de inversi\u00F3n"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K19})
		 ,DED_007  ("IVA deducible en operaciones intragrupo de bienes de inversi\u00F3n"
				 ,new Mod3902015DetailKey[]{Mod3902015DetailKey.K20_04,Mod3902015DetailKey.K20_07
				 ,Mod3902015DetailKey.K20_08,Mod3902015DetailKey.K20_10,Mod3902015DetailKey.K20_16
				 ,Mod3902015DetailKey.K20_18,Mod3902015DetailKey.K20_21})
		,DED_008  ("Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes de inversi\u00F3n"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K21})
		,DED_009  ("IVA deducible en importaciones de bienes corrientes"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K22_04,Mod3902015DetailKey.K22_07
				,Mod3902015DetailKey.K22_08,Mod3902015DetailKey.K22_10,Mod3902015DetailKey.K22_16
				,Mod3902015DetailKey.K22_18,Mod3902015DetailKey.K22_21})
		,DED_010  ("Total bases imponibles y cuotas deducibles en importaciones de bienes corrientes"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K23})
		
		,DED_011  ("IVA deducible en importaciones de bienes de inversi\u00F3n"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K24_04,Mod3902015DetailKey.K24_07
				,Mod3902015DetailKey.K24_08,Mod3902015DetailKey.K24_10,Mod3902015DetailKey.K24_16
				,Mod3902015DetailKey.K24_18,Mod3902015DetailKey.K24_21})
		,DED_012  ("Total bases imponibles y cuotas deducibles en importaciones de bienes de inversi\u00F3n",new Mod3902015DetailKey[]{Mod3902015DetailKey.K25})
		,DED_013  ("IVA deducible en adquisiciones intracomunitarias de bienes corrientes"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K26_04,Mod3902015DetailKey.K26_07
				,Mod3902015DetailKey.K26_08,Mod3902015DetailKey.K26_10,Mod3902015DetailKey.K26_16
				,Mod3902015DetailKey.K26_18,Mod3902015DetailKey.K26_21})
		,DED_014  ("Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes corrientes"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K27})
		,DED_015  ("IVA deducible en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K28_04,Mod3902015DetailKey.K28_07
				,Mod3902015DetailKey.K28_08,Mod3902015DetailKey.K28_10,Mod3902015DetailKey.K28_16
				,Mod3902015DetailKey.K28_18,Mod3902015DetailKey.K28_21})
		,DED_016  ("Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K29})
		,DED_017  ("IVA deducible en adquisiciones intracomunitarias de servicios"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K30_04,Mod3902015DetailKey.K30_07
				,Mod3902015DetailKey.K30_08,Mod3902015DetailKey.K30_10,Mod3902015DetailKey.K30_16
				,Mod3902015DetailKey.K30_18,Mod3902015DetailKey.K30_21})
		,DED_018  ("Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de servicios"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K31})
		,DED_019  ("Compensaci\u00F3n en r\u00E9gimen especial de la agricultura, ganaderia y pesca"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K32})
		,DED_020  ("Rectificaci\u00F3n de deducciones"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K33})
		,DED_021  ("Rectificaci\u00F3n de deducciones por operaciones intragrupo"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K33_I})
		,DED_022  ("Regularizaci\u00F3n de bienes de inversi\u00F3n"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K34})
		,DED_023  ("Regularizaci\u00F3n por aplicaci\u00F3n porcentaje definitivo de prorrata"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K35})
		,DED_024  ("Suma de deducciones"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K36})
		,DED_025  ("Resultado r\u00E9gimen general"
				,new Mod3902015DetailKey[]{Mod3902015DetailKey.K37})
		 ;
		
		private String label;
		private Mod3902015DetailKey[] keys;
		
		private Mod390DetailKeyGroup(String label,Mod3902015DetailKey[] keys) {
			this.label = label;
			this.keys = keys;
		}
		public Mod3902015DetailKey[] getKeys() {
			return keys;
		}
		public String getLabel() {
			return label;
		}
	}
	
	@Override
	public void refresh(Mod3902015 mod390) {  
		if (mod390.getGeneralRegime() != null) {
			for (Mod3902015DetailKey key : mod390.getGeneralRegime().keySet()) {
				Mod390Detail detail = mod390.getGeneralRegime().get(key);
				Mod390DetailFields fields = map.get(key);
				if (fields.getTaxableBase().getValue() != null) {
					if (key.hasTaxableBaseAvailable() && !AonMathUtils.equals(fields.getTaxableBase().getValue(),detail.getTaxableBase())) {
						fields.setTaxableBase(detail.getTaxableBase(),true);	
					}
				}
				if (fields.getQuota().getValue() != null) {
					if (!AonMathUtils.equals(fields.getQuota().getValue(),detail.getQuota())) {
						fields.setQuota(detail.getQuota(),true);
					}
				}
			}
		}
	}
	
}
