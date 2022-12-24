package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import static com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015DetailKey.*;

import java.io.Serializable;
import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.Model3902015Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015.Mod390Detail;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page03 extends PageAbs  {

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
	private static final String SDED_020 = "Rectificaci\u00F3n de deducciones";
	private static final String SDED_021 = "Rectificaci\u00F3n de deducciones por operaciones intragrupo";
	private static final String SDED_022 = "Regularizaci\u00F3n de bienes de inversi\u00F3n";
	private static final String SDED_023 = "Regularizaci\u00F3n por aplicaci\u00F3n porcentaje definitivo de prorrata";
	private static final String SDED_024 = "Suma de deducciones";
	private static final String SDED_025 = "Resultado r\u00E9gimen general";

	public static class Mod390DetailFields {
		private Mod390Detail detail;
		private AonDoubleBox taxableBase;
		private AonDoubleBox quota;
		
		protected Mod390DetailFields(Mod390Detail detail) {
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
		public Mod390Detail getDetail() {
			return detail;
		}
		public void setDetail(Mod390Detail mod390Detail) {
			detail = mod390Detail;
		}
	} 
	
	private FlexTable table;
	
	private EnumMap<Mod3902015DetailKey, Mod390DetailFields> map; 
					
	public Page03(Model3902015Callback callback) {
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
		map = new EnumMap<>(Mod3902015DetailKey.class);
		for (Mod3902015DetailKey key: Mod3902015DetailKey.values() ) {
			if (key.accept(getModel().getYear())) {
				Mod390Detail detail = null;
				if (getModel().getGeneralRegime() != null) {
					detail = getModel().getGeneralRegime().get(key);	
				}
				if (detail == null) {
					detail = new Mod390Detail();
					detail.setKey(key);
					detail.setPercent(key.getPercent());
					getModel().getGeneralRegime().put(key,detail);
				}
				map.put(key, new Mod390DetailFields(detail));
			}
		}
	}

	public void populate() {
		for (Mod3902015DetailKey key : map.keySet()) {
			Mod390DetailFields f = map.get(key);
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
			for (Mod390DetailKeyGroup group: Mod390DetailKeyGroup.values() ) {
				if (group == Mod390DetailKeyGroup.DEV_001) {
					Label l = new Label(AON.MSG.outputVat());
					fmt.setColSpan(x, y, 7);
					fmt.addStyleName(x, y, AON.CSS.aonBold());
					fmt.addStyleName(x, y, AON.CSS.aonBorderBottom());
					table.setWidget(x, y, l);
					x++;
				} else if (group == Mod390DetailKeyGroup.DED_001) {
					Label l = new Label(AON.MSG.inputVat());
					fmt.setColSpan(x, y, 7);
					fmt.addStyleName(x, y, AON.CSS.aonBold());
					fmt.addStyleName(x, y, AON.CSS.aonBorderBottom());
					table.setWidget(x, y, l);
					x++;
				}

				rowspan = 0;
				for (Mod3902015DetailKey key: group.getKeys() ) {
					if (key.accept(getModel().getYear())) {
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
					}
					fmt.addStyleName(x, y,AON.CSS.aonBorderBottom() );
					table.setWidget(x, y, p0);
					y++;
					
					for (Mod3902015DetailKey key: group.getKeys() ) {
						if (key.accept(getModel().getYear())) {
							final Mod390DetailFields fields = map.get(key); 
							Mod390Detail det = getModel().getGeneralRegime().get(key);
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
							if  (det.getKey().getPercent() != 0.0) {
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
	}

	public enum Mod390DetailKeyGroup implements Serializable {
 	  DEV_001 (SDEV_001,K00_04,K00_10,K00_21)
	 ,DEV_002 (SDEV_002,K01_04,K01_10,K01_21)
	 ,DEV_003 (SDEV_003,K40_04,K40_10,K40_21)
	 ,DEV_004 (SDEV_004,K02_04,K02_10,K02_21)
	 ,DEV_005 (SDEV_005,K03_21)
	 ,DEV_006 (SDEV_006,K04_04,K04_10,K04_21)
	 ,DEV_007 (SDEV_007,K05_04,K05_10,K05_21)
	 ,DEV_008 (SDEV_008,K06)
	 ,DEV_009 (SDEV_009,K07)
	 ,DEV_010 (SDEV_010,K07_I)
	 ,DEV_011 (SDEV_011,K08)
	 ,DEV_012 (SDEV_012,K09)
	 ,DEV_013 (SDEV_013,K10_05,K10_14,K10_52,K10_175)
	 ,DEV_014 (SDEV_014,K11)
	 ,DEV_015 (SDEV_015,K12)
	 ,DEV_016 (SDEV_016,K13)
	 ,DED_001 (SDED_001,K14_04,K14_07,K14_08,K14_10,K14_16,K14_18,K14_21)
	 ,DED_002 (SDED_002,K15)
	 ,DED_003 (SDED_003,K16_04,K16_07,K16_08,K16_10,K16_16,K16_18,K16_21)
	 ,DED_004 (SDED_004,K17)
	 ,DED_005 (SDED_005,K18_04,K18_07,K18_08,K18_10,K18_16,K18_18,K18_21)
	 ,DED_006 (SDED_006,K19)
	 ,DED_007 (SDED_007,K20_04,K20_07,K20_08,K20_10,K20_16,K20_18,K20_21)
	 ,DED_008 (SDED_008,K21)
	 ,DED_009 (SDED_009,K22_04,K22_07,K22_08,K22_10,K22_16,K22_18,K22_21)
	 ,DED_010 (SDED_010,K23)
	 ,DED_011 (SDED_011,K24_04,K24_07,K24_08,K24_10,K24_16,K24_18,K24_21)
	 ,DED_012 (SDED_012,K25)
	 ,DED_013 (SDED_013,K26_04,K26_07,K26_08,K26_10,K26_16,K26_18,K26_21)
	 ,DED_014 (SDED_014,K27)
	 ,DED_015 (SDED_015,K28_04,K28_07,K28_08,K28_10,K28_16,K28_18,K28_21)
	 ,DED_016 (SDED_016,K29)
	 ,DED_017 (SDED_017,K30_04,K30_07,K30_08,K30_10,K30_16,K30_18,K30_21)
	 ,DED_018 (SDED_018,K31)
	 ,DED_019 (SDED_019,K32)
	 ,DED_020 (SDED_020,K33)
	 ,DED_021 (SDED_021,K33_I)
	 ,DED_022 (SDED_022,K34)
	 ,DED_023 (SDED_023,K35)
	 ,DED_024 (SDED_024,K36)
	 ,DED_025 (SDED_025,K37)
	 ;
		
		private String label;
		private Mod3902015DetailKey[] keys;
		
		private Mod390DetailKeyGroup(String label,Mod3902015DetailKey ... keys) {
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
	protected void refresh() {
		if (getModel().getGeneralRegime() != null) {
			for (Mod3902015DetailKey key : getModel().getGeneralRegime().keySet()) {
				Mod390Detail detail = getModel().getGeneralRegime().get(key);
				Mod390DetailFields fields = map.get(key);
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
