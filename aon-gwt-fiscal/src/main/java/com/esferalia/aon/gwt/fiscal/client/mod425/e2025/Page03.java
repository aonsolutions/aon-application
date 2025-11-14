// OPERACIONES REALIZADAS EN RÉGIMEN GENERAL
package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod425.e2025.Model4252025.Model4252025Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025.Mod425Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025DetailKeyGroup;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Page03 extends PageAbs  {

	public static class Mod425DetailFields {
		private Mod425Detail detail;
		private AonDoubleBox baseBox;
		private AonDoubleBox quotaBox;
		
		protected Mod425DetailFields(Mod425Detail detail) {
			this.detail = detail;
			baseBox = new AonDoubleBox();
			quotaBox = new AonDoubleBox();
		}
		public AonDoubleBox getBaseBox() {
			return baseBox;
		}
		public void setBaseBox(double value, boolean shouldDisplayChange) {
			this.baseBox.setValue(value,false,shouldDisplayChange);
			detail.setTaxableBase(value);
		}
		public AonDoubleBox getQuotaBox() {
			return quotaBox;
		}
		public void setQuotaBox(double value, boolean shouldDisplayChange) {
			this.quotaBox.setValue(value,false,shouldDisplayChange);
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
			for (Mod4252025DetailKeyGroup group: Mod4252025DetailKeyGroup.values() ) {
				if (group == Mod4252025DetailKeyGroup.DEV_001 || group == Mod4252025DetailKeyGroup.DED_001 || group == Mod4252025DetailKeyGroup.TOT_001 ) {
					String title = "BASE IMPONIBLE, TIPOS Y CUOTAS"; 
					if (group == Mod4252025DetailKeyGroup.DED_001) {
						title = "DEDUCCIONES";
					} else if (group == Mod4252025DetailKeyGroup.TOT_001) {
						title = "RESULTADO DE LAS AUTOLIQUIDACIONES";
					}
					Label l = new Label(title);
					l.addStyleName(AON.CSS.aonMarginTop());
					fmt.setColSpan(x, y, group == Mod4252025DetailKeyGroup.TOT_001 ? 7 : 2);
					fmt.addStyleName(x, y, AON.CSS.aonBold());
					fmt.addStyleName(x, y, AON.CSS.aonBorderBottom());
					table.setWidget(x, y, l);
					
					if (group == Mod4252025DetailKeyGroup.DEV_001) {
						addHeaderLabel(table, x, ++y, "Base imponible");
						addHeaderLabel(table, x, ++y, "Tipo (%)");
						addHeaderLabel(table, x, ++y, "");
						addHeaderLabel(table, x, ++y, "Cuota");
						y = 0;
					}
					if (group == Mod4252025DetailKeyGroup.DED_001) {
						addHeaderLabel(table, x, ++y, "Base");
						addHeaderLabel(table, x, ++y, "");
						addHeaderLabel(table, x, ++y, "");
						addHeaderLabel(table, x, ++y, "Cuota");
						y = 0;
					}				
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
					if (group == Mod4252025DetailKeyGroup.DEV_008 || group == Mod4252025DetailKeyGroup.DEV_011 || 
						group == Mod4252025DetailKeyGroup.DED_010 || group == Mod4252025DetailKeyGroup.TOT_001) {
						l0.addStyleName(AON.CSS.aonBold());
					}					
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
						if (key.hasTaxableBaseAvailable() || key == Mod4252025DetailKey.C074) {
							table.setWidget(x, y, new AonBoxLabel(det.getTaxableBaseBox()));
						}
						if (showBorder) {
							fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
						}
						++y;
						
						// Taxable Base 
						if (key.hasTaxableBaseAvailable() || key == Mod4252025DetailKey.C074) {
							fields.getBaseBox().setReadOnly(key.isReadonly());
							fields.getBaseBox().setEnabled(!key.isReadonly());
							if (!key.isReadonly()) {
								final double percent = det.getPercent();
								fields.getBaseBox().addValueChangeHandler(event -> {
									fields.getDetail().setTaxableBase(fields.getBaseBox().getValue());
									if  (percent != 0.0 && fields.getDetail().getQuota() == 0) {
										if (fields.getQuotaBox().getValue()==null) fields.getQuotaBox().setValue(0.0, false);
										fields.setQuotaBox( AonMathUtils.round( fields.getBaseBox().getValue() * percent / 100), true );
									}
									getModel().getGeneralRegime().put(key, fields.getDetail());
									markAsDirty();
									calculateAndRefresh();
								});
							}
							fields.getBaseBox().setValue(det.getTaxableBase());
							table.setWidget(x, y, fields.getBaseBox());
						}
						if (showBorder) {
							fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
						}
						++y;
			
						// Percent 
						FlowPanel p1 = new FlowPanel();
						if  (det.getKey().getPercent() != 0.0 || 
								det.getKey() == Mod4252025DetailKey.C003 ||
								det.getKey() == Mod4252025DetailKey.C021 ||
								det.getKey() == Mod4252025DetailKey.C036 ||
								det.getKey() == Mod4252025DetailKey.C051 
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
						
						if (key == Mod4252025DetailKey.C074) {
							Label l = new Label("");
							table.setWidget(x, y, l);
							if (showBorder) {
								fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
							}
							l = new Label("");
							table.setWidget(x, ++y, l);
							if (showBorder) {
								fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
							}
						} else {							
							// Quota Box 
							if (showBorder) {
								fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
							}
							table.setWidget(x, y, new AonBoxLabel(det.getQuotaBox()));
	
							++y;
						
							// Quota
							fields.getQuotaBox().setValue(det.getQuota());
							fields.getQuotaBox().setReadOnly(key.isReadonly());
							fields.getQuotaBox().setEnabled(!key.isReadonly());
							if (!key.isReadonly()) {
								fields.getQuotaBox().addValueChangeHandler(event -> {
									fields.getDetail().setQuota(fields.getQuotaBox().getValue());
									getModel().getGeneralRegime().put(key, fields.getDetail());
									markAsDirty();
									calculateAndRefresh();
								});
							}
							table.setWidget(x, y, fields.getQuotaBox());
							if (showBorder) {
								fmt.addStyleName(x, y,AON.CSS.aonBorderBottom());	
							}
							++y;							
						}
						
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

	private void addHeaderLabel(FlexTable table, int row, int col, String label) {
		Label l = new Label(label);
		l.addStyleName(AON.CSS.aonMarginTop());
		FlexCellFormatter fmt = table.getFlexCellFormatter();
		fmt.addStyleName(row, col, AON.CSS.aonBold());
		fmt.addStyleName(row, col, AON.CSS.aonBorderBottom());
		fmt.addStyleName(row, col, AON.CSS.aonFontSmaller());
		fmt.addStyleName(row, col, AON.CSS.aonTextCenter());
		table.setWidget(row, col, l);		
	}

	@Override
	protected void refresh() {
		if (getModel().getGeneralRegime() != null) {
			for (Mod4252025DetailKey key : getModel().getGeneralRegime().keySet()) {
				Mod425Detail detail = getModel().getGeneralRegime().get(key);
				Mod425DetailFields fields = map.get(key);
				if (fields.getBaseBox().getValue() != null 
						&& key.hasTaxableBaseAvailable() 
						&& !AonMathUtils.equals(fields.getBaseBox().getValue(),detail.getTaxableBase())) {
					fields.setBaseBox(detail.getTaxableBase(),true);	
				}
				if (fields.getQuotaBox().getValue() != null 
						&& !AonMathUtils.equals(fields.getQuotaBox().getValue(),detail.getQuota())) {
					fields.setQuotaBox(detail.getQuota(),true);
				}
			}
		}
	}

}



