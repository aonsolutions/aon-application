package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Mod390CallBack;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.Mod390DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.Mod390DetailKeyGroup;
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

public class Page05 extends ResizeComposite implements RequiresResize {

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
		public void setTaxableBase(double value) {
			this.taxableBase.setValue(value);
			detail.setTaxableBase(value);
		}
		public DoubleBox getQuota() {
			return quota;
		}
		public void setQuota(double value) {
			this.quota.setValue(value);
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
	
	Mod390CallBack callback;
	
	private Mod3902014 mod390;
	
	private EnumMap<Mod390DetailKey, Mod390DetailFields> map; 
					
	public Page05() {
		FlowPanel container = new FlowPanel();
		table = new FlexTable();
		container.add(table);
		initWidget(container);
	}

	public void setValue(Mod3902014 m390) {
		this.mod390 = m390;
		initializeMap();
		refreshMap(m390,null);
		initializeTable();
	}
	private void initializeMap() {
		map = new EnumMap<Mod390DetailKey, Mod390DetailFields>(Mod390DetailKey.class);
		for (Mod390DetailKey key: Mod390DetailKey.values() ) {
			if (key.accept(mod390.getYear())) {
				Mod390Detail detail = new Mod390Detail();
				detail.setKey(key);
				detail.setPercent(key.getPercent());
				map.put(key, new Mod390DetailFields(detail));
			}
		}
	}

	public void refreshMap(Mod3902014 mod390, Mod390DetailKey eventSource) {
		if (mod390.getGeneralRegime() != null) {
			for (Mod390DetailKey key : mod390.getGeneralRegime().keySet()) {
				Mod390Detail detail = mod390.getGeneralRegime().get(key);
				Mod390DetailFields fields = map.get(key);
				if (fields == null) {
					fields = new Mod390DetailFields(detail);
					map.put(key,fields);	
				} 
				fields.setDetail(detail);
				if (eventSource != key) {
					fields.setTaxableBase(detail.getTaxableBase());
					fields.setQuota(detail.getQuota());
				}
			}
		}
	}
	public void populate(Mod3902014 mod390) {
		for (Mod390DetailKey key : map.keySet()) {
			Mod390DetailFields f = map.get(key);
			mod390.getGeneralRegime().put(key, f.getDetail());
		}
	}

	private void initializeTable() {
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
				for (Mod390DetailKey key: group.getKeys() ) {
					if (key.accept(mod390.getYear())) {
						rowspan++;		
					}
				}
				if (rowspan > 0) {
					y = 0;
					String label = AON.MSG.mod390DetailKeyGroup(group);
					z = 1;
					oddRow = !oddRow;
					fmt.setRowSpan(x, y, rowspan);
					FlowPanel p0 = new FlowPanel();
					Label l0 = new Label(label);
					p0.add(l0);
					p0.setHeight("100%");
					if (rowspan > 1) {
						fmt.setHeight(x, y, (rowspan * 20) + "px;");
						p0.addStyleName(AON.AON_CSS.aonCurlyLT() );
					}
					fmt.addStyleName(x, y,AON.AON_CSS.aonBorderBottomImportant() );
					table.setWidget(x, y, p0);
					y++;
					
					for (Mod390DetailKey key: group.getKeys() ) {
						if (key.accept(mod390.getYear())) {
							final Mod390DetailFields fields = map.get(key); 
							Mod390Detail det = fields.getDetail();
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
								final double percent = det.getPercent();
									fields.getTaxableBase().addChangeHandler(new ChangeHandler() {
										@Override
										public void onChange(ChangeEvent event) {
												fields.getDetail().setTaxableBase(fields.getTaxableBase().getValue());
												if  (percent != 0.0 && fields.getDetail().getQuota() == 0) {
													fields.setQuota( AonMathUtils.round( fields.getTaxableBase().getValue() * percent / 100) );
												}
											callback.calculateAndRefresh();
											refreshMap(mod390,fields.detail.getKey());
										}
								});
								fields.getTaxableBase().setValue(det.getTaxableBase());
								table.setWidget(x, y, fields.getTaxableBase());
							}
							if (showBorder) {
								fmt.addStyleName(x, y,AON.AON_CSS.aonBorderBottom());	
							}
							++y;
				
							// Percent 
							FlowPanel p1 = new FlowPanel();
							if  (det.getPercent() != 0.0) {
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
							fields.getQuota().addChangeHandler(new ChangeHandler() {
								
								@Override
								public void onChange(ChangeEvent event) {
									fields.getDetail().setQuota(fields.getQuota().getValue());
									callback.calculateAndRefresh();
									refreshMap(mod390,fields.detail.getKey());
								}
							});
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

	public void setCallback(Mod390CallBack callback) {
		this.callback = callback;
	}
}
