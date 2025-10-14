package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import java.io.Serializable;
import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod425.e2025.Model4252025.Model4252025Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025.Mod425Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025DetailKey;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Page03 extends PageAbs  {

	private static final String SDEV_001 = "R\u00E9gimen ordinario";
	private static final String SDEV_002 = "Operaciones intragrupo";
	private static final String SDEV_003 = "R\u00E9gimen especial del criterio de caja";
	private static final String SDEV_004 = "R\u00E9gimen especial de bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n";
	private static final String SDEV_005 = "R\u00E9gimen especial de agencias de viaje";
	private static final String SDEV_006 = "Adquisiciones intracomunitarias de bienes";
	private static final String SDEV_007 = "Adquisiciones intracomunitarias de servicios";
	private static final String SDEV_008 = "IVA devengado en otros supuestos de inversi\u00F3n del sujeto pasivo";
	private static final String SDEV_009 = "Modificaci\u00F3n de bases y cuotas";
	private static final String SDEV_010 = "Modificaci\u00F3n de bases y cuotas de operaciones intragrupo";
	private static final String SDEV_011 = "Modificaci\u00F3n de bases y cuotas por auto de declaraci\u00F3n de concurso de acreedores";
	private static final String SDEV_012 = "Total bases y cuotas IVA";
	private static final String SDEV_013 = "Recargo de equivalencia";
	private static final String SDEV_014 = "Modificaci\u00F3n recargo equivalencia";
	private static final String SDEV_015 = "Modificaci\u00F3n recargo equivalencia por auto de declaraci\u00F3n de concurso de acreedores";
	private static final String SDEV_016 = "Total cuotas IVA y recargo de equivalencia";
	private static final String SDED_001 = "IVA deducible en operaciones interiores de bienes y servicios corrientes";
	private static final String SDED_002 = "Total bases imponibles y cuotas deducibles en operaciones interiores de bienes y servicios corrientes";
	private static final String SDED_003 = "IVA deducible en operaciones intragrupo de bienes y servicios corrientes";
	private static final String SDED_004 = "Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes y servicios corrientes";
	private static final String SDED_005 = "IVA deducible en operaciones interiores de bienes de inversi\u00F3n";
	private static final String SDED_006 = "Total bases imponibles y cuotas deducibles en operaciones interiores de bienes de inversi\u00F3n";
	private static final String SDED_007 = "IVA deducible en operaciones intragrupo de bienes de inversi\u00F3n";
	private static final String SDED_008 = "Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes de inversi\u00F3n";
	private static final String SDED_009 = "IVA deducible en importaciones de bienes corrientes";
	private static final String SDED_010 = "Total bases imponibles y cuotas deducibles en importaciones de bienes corrientes";
	private static final String SDED_011 = "IVA deducible en importaciones de bienes de inversi\u00F3n";
	private static final String SDED_012 = "Total bases imponibles y cuotas deducibles en importaciones de bienes de inversi\u00F3n";
	private static final String SDED_013 = "IVA deducible en adquisiciones intracomunitarias de bienes corrientes";
	private static final String SDED_014 = "Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes corrientes";
	private static final String SDED_015 = "IVA deducible en adquisiciones intracomunitarias de bienes de inversi\u00F3n";
	private static final String SDED_016 = "Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes de inversi\u00F3n";
	private static final String SDED_017 = "IVA deducible en adquisiciones intracomunitarias de servicios";
	private static final String SDED_018 = "Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de servicios";
	private static final String SDED_019 = "Compensaci\u00F3n en r\u00E9gimen especial de la agricultura, ganaderia y pesca";
	private static final String SDED_020 = "Cuotas deducibles en virtud de resoluci\u00F3n administrativa o sentencia firmes con tipos no vigentes";
	private static final String SDED_021 = "Rectificaci\u00F3n de deducciones";
	private static final String SDED_022 = "Rectificaci\u00F3n de deducciones por operaciones intragrupo";
	private static final String SDED_023 = "Regularizaci\u00F3n de bienes de inversi\u00F3n";
	private static final String SDED_024 = "Regularizaci\u00F3n por aplicaci\u00F3n porcentaje definitivo de prorrata";
	private static final String SDED_025 = "Suma de deducciones";
	private static final String SDED_026 = "Resultado r\u00E9gimen general";


	public static class Mod425DetailFields {
		private Mod425Detail detail;
		private AonDoubleBox taxableBase;
		private AonDoubleBox quota;
		
		protected Mod425DetailFields(Mod425Detail detail) {
			this.detail = detail;
			taxableBase = new AonDoubleBox();
			quota = new AonDoubleBox();
		}
		public AonDoubleBox getTaxableBase() {
			return taxableBase;
		}
		public void setTaxableBase(double value, boolean shouldDisplayChange) {
			this.taxableBase.setValue(value,false,shouldDisplayChange);
			detail.setTaxableBase(value);
		}
		public AonDoubleBox getQuota() {
			return quota;
		}
		public void setQuota(double value, boolean shouldDisplayChange) {
			this.quota.setValue(value,false,shouldDisplayChange);
			detail.setQuota(value);
		}
		public Mod425Detail getDetail() {
			return detail;
		}
		public void setDetail(Mod425Detail mod425Detail) {
			detail = mod425Detail;
		}
	} 
	
	private FlexTable table;
	
	private EnumMap<Mod4252025DetailKey, Mod425DetailFields> map; 
					
	public Page03(Model4252025Callback callback) {
		super(callback);
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);

		table = new FlexTable();
		basePanel.add(table);
		setValue();
		initializeTable();
		
	}

	@Override
	protected void setValue() {
		map = new EnumMap<>(Mod4252025DetailKey.class);
		for (Mod4252025DetailKey key: Mod4252025DetailKey.values() ) {
			Mod425Detail detail = null;
			if (getModel().getGeneralRegime() != null) {
				detail = getModel().getGeneralRegime().get(key);	
			}
			if (detail == null) {
				detail = new Mod425Detail();
				detail.setKey(key);
				detail.setPercent(key.getPercent());
				getModel().getGeneralRegime().put(key,detail);
			}
			map.put(key, new Mod425DetailFields(detail));
		}
	}

	public void populate() {
		for (Mod4252025DetailKey key : map.keySet()) {
			Mod425DetailFields f = map.get(key);
			getModel().getGeneralRegime().put(key, f.getDetail());
		}
	}

	private void initializeTable() {
		if (table.getRowCount() > 0) {
			table.removeAllRows();
		}
		
		table.setWidth("100%");
		table.setStyleName(AON.CSS.aonMarginTop());
		table.addStyleName(AON.CSS.aonMarginBottom());
		table.addStyleName(AON.CSS.aonTable());
		table.setCellSpacing(0);
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(0, "auto");
		cf.addStyleName(0, AON.CSS.aonPaddingLeft() );
		cf.addStyleName(0, AON.CSS.aonPaddingRight() );
		cf.setWidth(1, "40px");
		cf.setStyleName(1, AON.CSS.aonTextCenter());
		cf.setWidth(2, "140px");
		cf.setWidth(3, "60px");
		cf.setWidth(4, "40px");
		cf.setStyleName(4, AON.CSS.aonTextCenter());
		cf.setWidth(5, "20px");

		FlexCellFormatter fmt = table.getFlexCellFormatter();
		fmt.setColSpan(0, 0, 6);
		table.setWidget(0, 0, getTitle(AON.MSG.generalRegimeOperations()));

		int x = 1;
		int y = 0;
		int z = 1;
		int rowspan = 0;
		boolean oddRow = false;
		boolean showBorder = false;
		if (map != null) {
			for (Mod425DetailKeyGroup group: Mod425DetailKeyGroup.values() ) {
				if (group == Mod425DetailKeyGroup.DEV_001) {
					Label l = new Label(AON.MSG.outputVat());
					l.addStyleName(AON.CSS.aonMarginTop());
					fmt.setColSpan(x, y, 7);
					fmt.addStyleName(x, y, AON.CSS.aonBold());
					fmt.addStyleName(x, y, AON.CSS.aonBorderBottom());
					table.setWidget(x, y, l);
					x++;
				} else if (group == Mod425DetailKeyGroup.DED_001) {
					Label l = new Label(AON.MSG.inputVat());
					l.addStyleName(AON.CSS.aonMarginTop());
					fmt.setColSpan(x, y, 7);
					fmt.addStyleName(x, y, AON.CSS.aonBold());
					fmt.addStyleName(x, y, AON.CSS.aonBorderBottom());
					table.setWidget(x, y, l);
					x++;
				}

				rowspan = group.getKeys().length;
				
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
					}
					fmt.addStyleName(x, y,AON.CSS.aonBorderBottom() );
					table.setWidget(x, y, p0);
					y++;
					
					for (Mod4252025DetailKey key: group.getKeys() ) {
						final Mod425DetailFields fields = map.get(key); 
						Mod425Detail det = getModel().getGeneralRegime().get(key);
						showBorder = (z == rowspan);
						
						// Taxable Base Box
						if ( key.hasTaxableBaseAvailable() ) {
							table.setWidget(x, y, new AonBoxLabel(det.getTaxableBaseBox()));
						}
						if (showBorder) {
							fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
						}
						++y;
						
						// Taxable Base 
						if ( key.hasTaxableBaseAvailable() ) {
							fields.getTaxableBase().setReadOnly(key.isReadonly());
							fields.getTaxableBase().setEnabled(!key.isReadonly());
							if (!key.isReadonly()) {
								final double percent = det.getPercent();
								fields.getTaxableBase().addValueChangeHandler(event -> {
									fields.getDetail().setTaxableBase(fields.getTaxableBase().getValue());
									if  (percent != 0.0 && fields.getDetail().getQuota() == 0) {
										if (fields.getQuota().getValue()==null) fields.getQuota().setValue(0.0, false);
										fields.setQuota( AonMathUtils.round( fields.getTaxableBase().getValue() * percent / 100), true );
									}
									getModel().getGeneralRegime().put(key, fields.getDetail());
									markAsDirty();
									calculateAndRefresh();
								});
							}
							fields.getTaxableBase().setValue(det.getTaxableBase());
							table.setWidget(x, y, fields.getTaxableBase());
						}
						if (showBorder) {
							fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
						}
						++y;
			
						// Percent 
						FlowPanel p1 = new FlowPanel();
						if  (det.getKey().getPercent() != 0.0 || 
								det.getKey() == Mod4252025DetailKey.C0701 ||
								det.getKey() == Mod4252025DetailKey.C0705 ||
								det.getKey() == Mod4252025DetailKey.C0709 ||
								det.getKey() == Mod4252025DetailKey.C0713 ||
								det.getKey() == Mod4252025DetailKey.C0717 ||
								det.getKey() == Mod4252025DetailKey.C0721 ||
								det.getKey() == Mod4252025DetailKey.C0664 
								) {
							p1.addStyleName(AON.CSS.aonTextCenter() );
							p1.addStyleName(AON.CSS.aonBorder() );
							p1.add(new Label(AON.FMT.format(det.getPercent()))); 
						} 
						table.setWidget(x, y, p1);
						if (showBorder) {
							fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
						}
						++y;
						
						// Quota Box 
						if (showBorder) {
							fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
						}
						table.setWidget(x, y, new AonBoxLabel(det.getBox()));

						++y;
					
						// Quota
						fields.getQuota().setValue(det.getQuota());
						fields.getQuota().setReadOnly(key.isReadonly());
						fields.getQuota().setEnabled(!key.isReadonly());
						if (!key.isReadonly()) {
							fields.getQuota().addValueChangeHandler(event -> {
								fields.getDetail().setQuota(fields.getQuota().getValue());
								getModel().getGeneralRegime().put(key, fields.getDetail());
								markAsDirty();
								calculateAndRefresh();
							});
						}
						table.setWidget(x, y, fields.getQuota());
						if (showBorder) {
							fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
						}
						++y;
						
						Label l = new Label("");
						table.setWidget(x, y, l);
						if (showBorder) {
							fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
						}
						++x;
						++z;
						y = 0;
					}
				}
			}
		}
	}

	public enum Mod425DetailKeyGroup implements Serializable {
		  DEV_001 (SDEV_001,Mod4252025DetailKey.C0701,Mod4252025DetailKey.C0668,Mod4252025DetailKey.C0002,Mod4252025DetailKey.C0703,Mod4252025DetailKey.C0670,Mod4252025DetailKey.C0004,Mod4252025DetailKey.C0006)
		 ,DEV_002 (SDEV_002,Mod4252025DetailKey.C0705,Mod4252025DetailKey.C0672,Mod4252025DetailKey.C0501,Mod4252025DetailKey.C0707,Mod4252025DetailKey.C0674,Mod4252025DetailKey.C0503,Mod4252025DetailKey.C0505)
		 ,DEV_003 (SDEV_003,Mod4252025DetailKey.C0709,Mod4252025DetailKey.C0676,Mod4252025DetailKey.C0644,Mod4252025DetailKey.C0711,Mod4252025DetailKey.C0678,Mod4252025DetailKey.C0646,Mod4252025DetailKey.C0648)
		 ,DEV_004 (SDEV_004,Mod4252025DetailKey.C0713,Mod4252025DetailKey.C0680,Mod4252025DetailKey.C0008,Mod4252025DetailKey.C0715,Mod4252025DetailKey.C0682,Mod4252025DetailKey.C0010,Mod4252025DetailKey.C0012)
		 ,DEV_005 (SDEV_005,Mod4252025DetailKey.C0014)  
		 ,DEV_006 (SDEV_006,Mod4252025DetailKey.C0717,Mod4252025DetailKey.C0684,Mod4252025DetailKey.C0022,Mod4252025DetailKey.C0719,Mod4252025DetailKey.C0686,Mod4252025DetailKey.C0024,Mod4252025DetailKey.C0026)
		 ,DEV_007 (SDEV_007,Mod4252025DetailKey.C0721,Mod4252025DetailKey.C0688,Mod4252025DetailKey.C0546,Mod4252025DetailKey.C0723,Mod4252025DetailKey.C0690,Mod4252025DetailKey.C0548,Mod4252025DetailKey.C0552)
		 ,DEV_008 (SDEV_008,Mod4252025DetailKey.C0028)  
		 ,DEV_009 (SDEV_009,Mod4252025DetailKey.C0030)  
		 ,DEV_010 (SDEV_010,Mod4252025DetailKey.C0650)  
		 ,DEV_011 (SDEV_011,Mod4252025DetailKey.C0032)  
		 ,DEV_012 (SDEV_012,Mod4252025DetailKey.C0034)  
		 ,DEV_013 (SDEV_013,Mod4252025DetailKey.C0664,Mod4252025DetailKey.C0692,Mod4252025DetailKey.C0036,Mod4252025DetailKey.C0666,Mod4252025DetailKey.C0694,Mod4252025DetailKey.C0600,Mod4252025DetailKey.C0602,Mod4252025DetailKey.C0042)
		 ,DEV_014 (SDEV_014,Mod4252025DetailKey.C0044)  
		 ,DEV_015 (SDEV_015,Mod4252025DetailKey.C0046)  
		 ,DEV_016 (SDEV_016,Mod4252025DetailKey.C0047)  
		                                                
		 ,DED_001 (SDED_001,Mod4252025DetailKey.C0696,Mod4252025DetailKey.C0191,Mod4252025DetailKey.C0725,Mod4252025DetailKey.C0698,Mod4252025DetailKey.C0604,Mod4252025DetailKey.C0606)
		 ,DED_002 (SDED_002,Mod4252025DetailKey.C0049)  
		 ,DED_003 (SDED_003,Mod4252025DetailKey.C0746,Mod4252025DetailKey.C0507,Mod4252025DetailKey.C0727,Mod4252025DetailKey.C0748,Mod4252025DetailKey.C0608,Mod4252025DetailKey.C0610)
		 ,DED_004 (SDED_004,Mod4252025DetailKey.C0513)  
		 ,DED_005 (SDED_005,Mod4252025DetailKey.C0750,Mod4252025DetailKey.C0197,Mod4252025DetailKey.C0729,Mod4252025DetailKey.C0752,Mod4252025DetailKey.C0612,Mod4252025DetailKey.C0614)
		 ,DED_006 (SDED_006,Mod4252025DetailKey.C0051)  
		 ,DED_007 (SDED_007,Mod4252025DetailKey.C0754,Mod4252025DetailKey.C0515,Mod4252025DetailKey.C0731,Mod4252025DetailKey.C0756,Mod4252025DetailKey.C0616,Mod4252025DetailKey.C0618)
		 ,DED_008 (SDED_008,Mod4252025DetailKey.C0521)  
		 ,DED_009 (SDED_009,Mod4252025DetailKey.C0758,Mod4252025DetailKey.C0203,Mod4252025DetailKey.C0733,Mod4252025DetailKey.C0760,Mod4252025DetailKey.C0620,Mod4252025DetailKey.C0622)
		 ,DED_010 (SDED_010,Mod4252025DetailKey.C0053)  
		 ,DED_011 (SDED_011,Mod4252025DetailKey.C0762,Mod4252025DetailKey.C0209,Mod4252025DetailKey.C0735,Mod4252025DetailKey.C0764,Mod4252025DetailKey.C0624,Mod4252025DetailKey.C0626)
		 ,DED_012 (SDED_012,Mod4252025DetailKey.C0055)  
		 ,DED_013 (SDED_013,Mod4252025DetailKey.C0766,Mod4252025DetailKey.C0215,Mod4252025DetailKey.C0737,Mod4252025DetailKey.C0768,Mod4252025DetailKey.C0628,Mod4252025DetailKey.C0630)
		 ,DED_014 (SDED_014,Mod4252025DetailKey.C0057)  
		 ,DED_015 (SDED_015,Mod4252025DetailKey.C0770,Mod4252025DetailKey.C0221,Mod4252025DetailKey.C0739,Mod4252025DetailKey.C0772,Mod4252025DetailKey.C0632,Mod4252025DetailKey.C0634)
		 ,DED_016 (SDED_016,Mod4252025DetailKey.C0059)  
		 ,DED_017 (SDED_017,Mod4252025DetailKey.C0774,Mod4252025DetailKey.C0588,Mod4252025DetailKey.C0741,Mod4252025DetailKey.C0776,Mod4252025DetailKey.C0636,Mod4252025DetailKey.C0638)
		 ,DED_018 (SDED_018,Mod4252025DetailKey.C0598)
		 ,DED_019 (SDED_019,Mod4252025DetailKey.C0061)
		 ,DED_020 (SDED_020,Mod4252025DetailKey.C0661)
		 ,DED_021 (SDED_021,Mod4252025DetailKey.C0062)
		 ,DED_022 (SDED_022,Mod4252025DetailKey.C0652)
		 ,DED_023 (SDED_023,Mod4252025DetailKey.C0063)
		 ,DED_024 (SDED_024,Mod4252025DetailKey.C0522)
		 ,DED_025 (SDED_025,Mod4252025DetailKey.C0064)
		 ,DED_026 (SDED_026,Mod4252025DetailKey.C0065)
	 ;
		
		private String label;
		private Mod4252025DetailKey[] keys;
		
		private Mod425DetailKeyGroup(String label, Mod4252025DetailKey ... keys) {
			this.label = label;
			this.keys = keys;
		}
		public Mod4252025DetailKey[] getKeys() {
			return keys;
		}
		public String getLabel() {
			return label;
		}
	}
	
	@Override
	protected void refresh() {
		if (getModel().getGeneralRegime() != null) {
			for (Mod4252025DetailKey key : getModel().getGeneralRegime().keySet()) {
				Mod425Detail detail = getModel().getGeneralRegime().get(key);
				Mod425DetailFields fields = map.get(key);
				if (fields.getTaxableBase().getValue() != null 
						&& key.hasTaxableBaseAvailable() 
						&& !AonMathUtils.equals(fields.getTaxableBase().getValue(),detail.getTaxableBase())) {
					fields.setTaxableBase(detail.getTaxableBase(),true);	
				}
				if (fields.getQuota().getValue() != null 
						&& !AonMathUtils.equals(fields.getQuota().getValue(),detail.getQuota())) {
					fields.setQuota(detail.getQuota(),true);
				}
			}
		}
	}

}



