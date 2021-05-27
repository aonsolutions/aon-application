package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import java.io.Serializable;
import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.IMod3902018CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.IMod3902018Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018DetailKey;
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

public class Page03 extends ResizeComposite implements RequiresResize , IMod3902018Page {

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
	
	IMod3902018CallBack cbk;
	
	private EnumMap<Mod3902018DetailKey, Mod390DetailFields> map; 
					
	public Page03(Mod3902018 m390) {
		FlowPanel container = new FlowPanel();
		table = new FlexTable();
		initializeMap(m390);
		initializeTable(m390);
		
		container.add(table);
		initWidget(container);
	}

	private void initializeMap(Mod3902018 m390) {
		map = new EnumMap<Mod3902018DetailKey, Mod390DetailFields>(Mod3902018DetailKey.class);
		for (Mod3902018DetailKey key: Mod3902018DetailKey.values() ) {
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
	public void populate(Mod3902018 mod390) {
		for (Mod3902018DetailKey key : map.keySet()) {
			Mod390DetailFields f = map.get(key);
			mod390.getGeneralRegime().put(key, f.getDetail());
		}
	}

	private void initializeTable(final Mod3902018 m390) {
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
				for (Mod3902018DetailKey key: group.getKeys() ) {
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
					
					for (Mod3902018DetailKey key: group.getKeys() ) {
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
											if (fields.getTaxableBase().getValue()==null) fields.getTaxableBase().setValue(0.0, false);
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
										if (fields.getQuota().getValue()==null) fields.getQuota().setValue(0.0, false);
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
	public void setCallback(IMod3902018CallBack callback) {
		this.cbk = callback;
	}
	
	public static enum Mod390DetailKeyGroup implements Serializable {
		  DEV_001 ("R\u00E9gimen ordinario"
				  ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0002,Mod3902018DetailKey.C0004,Mod3902018DetailKey.C0006})
		 ,DEV_002 ("Operaciones intragrupo"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0501,Mod3902018DetailKey.C0503,Mod3902018DetailKey.C0505})
		 ,DEV_003  ("R\u00E9gimen especial del criterio de caja"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0644,Mod3902018DetailKey.C0646
				 ,Mod3902018DetailKey.C0648})
		 ,DEV_004  ("R\u00E9gimen especial de bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0008,Mod3902018DetailKey.C0010,Mod3902018DetailKey.C0012})
		 ,DEV_005  ("R\u00E9gimen especial de agencias de viaje"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0014})
		 ,DEV_006  ("Adquisiciones intracomunitarias de bienes"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0022
				 ,Mod3902018DetailKey.C0024,Mod3902018DetailKey.C0026})
		 ,DEV_007  ("Adquisiciones intracomunitarias de servicios"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0546,Mod3902018DetailKey.C0548,Mod3902018DetailKey.C0552})
		 ,DEV_008  ("IVA devengado en otros supuestos de inversi\u00F3n del sujeto pasivo"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0028})
		 ,DEV_009  ("Modificaci\u00F3n de bases y cuotas"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0030})
		 ,DEV_010  ("Modificaci\u00F3n de bases y cuotas de operaciones intragrupo"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0650})
		 ,DEV_011  ("Modificaci\u00F3n de bases y cuotas por auto de declaraci\u00F3n de concurso de acreedores"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0032})
		 ,DEV_012  ("Total bases y cuotas IVA"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0034})
		 ,DEV_013  ("Recargo de equivalencia",new Mod3902018DetailKey[]{Mod3902018DetailKey.C0036
				 ,Mod3902018DetailKey.C0600,Mod3902018DetailKey.C0602
				 ,Mod3902018DetailKey.C0042})
		 ,DEV_014  ("Modificaci\u00F3n recargo equivalencia"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0044})
		 ,DEV_015  ("Modificaci\u00F3n recargo equivalencia por auto de declaraci\u00F3n de concurso de acreedores"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0046})
		 ,DEV_016  ("Total cuotas IVA y recargo de equivalencia"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0047})
		 ,DED_001  ("IVA deducible en operaciones interiores de bienes y servicios corrientes"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0191,Mod3902018DetailKey.C0604,Mod3902018DetailKey.C0606})
		 ,DED_002  ("Total bases imponibles y cuotas deducibles en operaciones interiores de bienes y servicios corrientes"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0049})
		 ,DED_003  ("IVA deducible en operaciones intragrupo de bienes y servicios corrientes"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0507,Mod3902018DetailKey.C0608,Mod3902018DetailKey.C0610})
		 ,DED_004  ("Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes y servicios corrientes"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0513})
		 ,DED_005  ("IVA deducible en operaciones interiores de bienes de inversi\u00F3n"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0197,Mod3902018DetailKey.C0612,Mod3902018DetailKey.C0614})
		 ,DED_006  ("Total bases imponibles y cuotas deducibles en operaciones interiores de bienes de inversi\u00F3n"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0051})
		 ,DED_007  ("IVA deducible en operaciones intragrupo de bienes de inversi\u00F3n"
				 ,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0515,Mod3902018DetailKey.C0616,Mod3902018DetailKey.C0618})
		,DED_008  ("Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes de inversi\u00F3n"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0521})
		,DED_009  ("IVA deducible en importaciones de bienes corrientes"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0203,Mod3902018DetailKey.C0620,Mod3902018DetailKey.C0622})
		,DED_010  ("Total bases imponibles y cuotas deducibles en importaciones de bienes corrientes"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0053})
		
		,DED_011  ("IVA deducible en importaciones de bienes de inversi\u00F3n"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0209,Mod3902018DetailKey.C0624,Mod3902018DetailKey.C0626})
		,DED_012  ("Total bases imponibles y cuotas deducibles en importaciones de bienes de inversi\u00F3n",new Mod3902018DetailKey[]{Mod3902018DetailKey.C0055})
		,DED_013  ("IVA deducible en adquisiciones intracomunitarias de bienes corrientes"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0215,Mod3902018DetailKey.C0628,Mod3902018DetailKey.C0630})
		,DED_014  ("Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes corrientes"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0057})
		,DED_015  ("IVA deducible en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0221,Mod3902018DetailKey.C0632,Mod3902018DetailKey.C0634})
		,DED_016  ("Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes de inversi\u00F3n"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0059})
		,DED_017  ("IVA deducible en adquisiciones intracomunitarias de servicios"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0588,Mod3902018DetailKey.C0636,Mod3902018DetailKey.C0638})
		,DED_018  ("Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de servicios"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0598})
		,DED_019  ("Compensaci\u00F3n en r\u00E9gimen especial de la agricultura, ganaderia y pesca"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0061})
		,DED_020  ("Cuotas deducibles en virtud de resoluci\u00F3n administrativa o sentencia firmes con tipos no vigentes"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0661})
		,DED_021  ("Rectificaci\u00F3n de deducciones"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0062})
		,DED_022  ("Rectificaci\u00F3n de deducciones por operaciones intragrupo"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0652})
		,DED_023  ("Regularizaci\u00F3n de bienes de inversi\u00F3n"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0063})
		,DED_024  ("Regularizaci\u00F3n por aplicaci\u00F3n porcentaje definitivo de prorrata"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0522})
		,DED_025  ("Suma de deducciones"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0064})
		,DED_026  ("Resultado r\u00E9gimen general"
				,new Mod3902018DetailKey[]{Mod3902018DetailKey.C0065})
		 ;
		
		private String label;
		private Mod3902018DetailKey[] keys;
		
		private Mod390DetailKeyGroup(String label,Mod3902018DetailKey[] keys) {
			this.label = label;
			this.keys = keys;
		}
		public Mod3902018DetailKey[] getKeys() {
			return keys;
		}
		public String getLabel() {
			return label;
		}
	}
	
	@Override
	public void refresh(Mod3902018 mod390) {  
		if (mod390.getGeneralRegime() != null) {
			for (Mod3902018DetailKey key : mod390.getGeneralRegime().keySet()) {
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
