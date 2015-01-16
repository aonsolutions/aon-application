package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Mod390CallBack;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390DetailKeyGroup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page5 extends ResizeComposite implements RequiresResize {

	interface Page5Binder extends UiBinder<Widget, Page5> {
	}

	private static final Page5Binder page5Binder = GWT
			.create(Page5Binder.class);

	public static class Mod390DetailFields {
		private Mod390Detail detail;
		private DoubleTextBox taxableBase;
		private DoubleTextBox quota;
		
		protected Mod390DetailFields(Mod390Detail detail) {
			this.detail = detail;
			taxableBase = new DoubleTextBox();
			quota = new DoubleTextBox();
		}
		public DoubleTextBox getTaxableBase() {
			return taxableBase;
		}
		public void setTaxableBase(double value) {
			this.taxableBase.setValue(value);
			detail.setTaxableBase(value);
		}
		public DoubleTextBox getQuota() {
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
	
	@UiField(provided = true)
	FlexTable table;
	
	Mod390CallBack callback;
	
	private Mod390 mod390;
	
	private EnumMap<Mod390DetailKey, Mod390DetailFields> map; 
	
	public Page5() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		Model390.RESOURCES.css().ensureInjected();
		table = new FlexTable();
		Widget ui = page5Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(Mod390 m390) {
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

	public void refreshMap(Mod390 mod390, Mod390DetailKey eventSource) {
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
	public void populate(Mod390 mod390) {
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
		table.setCellSpacing(0);
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(0, "auto");
		cf.setWidth(1, "40px");
		cf.setWidth(2, "140px");
		cf.setWidth(3, "60px");
		cf.setWidth(4, "40px");
		cf.setWidth(5, "20px");
		
		
		FlexCellFormatter fmt = table.getFlexCellFormatter();
		

		int x = 0;
		int y = 0;
		int z = 1;
		int rowspan = 0;
		boolean oddRow = false;
		boolean showBorder = false;
		if (map != null) {
			for (Mod390DetailKeyGroup group: Mod390DetailKeyGroup.values() ) {
				if (group == Mod390DetailKeyGroup.DEV_001) {
					table.setWidget(x, y, new Label("."));
					x++;
					Label l = new Label(Model390.MSG.outputVat());
					fmt.setColSpan(x, y, 7);
					fmt.addStyleName(x, y, Model390.RESOURCES.css().aonPageHeader());
					table.setWidget(x, y, l);
					x++;
				} else if (group == Mod390DetailKeyGroup.DED_001) {
					table.setWidget(x, y, new Label("."));
					x++;
					Label l = new Label(Model390.MSG.inputVat());
					fmt.setColSpan(x, y, 7);
					fmt.addStyleName(x, y, Model390.RESOURCES.css().aonPageHeader());
					table.setWidget(x, y, l);
					x++;
				}
				// <g:Label text="{msg.generalRegimeOperations}" styleName="{aonResources.css.aonPageHeader}"/>

				rowspan = 0;
				showBorder = false;
				for (Mod390DetailKey key: group.getKeys() ) {
					if (key.accept(mod390.getYear())) {
						rowspan++;		
					}
				}
				if (rowspan > 0) {
					y = 0;
					String label = Model390.FISCAL_MSG.mod390DetailKeyGroup(group);
					z = 1;
					oddRow = !oddRow;
					fmt.setRowSpan(x, y, rowspan);
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonVerticalAlignMiddle() );
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonPaddingLeft() );
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonPaddingRight() );
					FlowPanel p0 = new FlowPanel();
					Label l0 = new Label(label);
					p0.add(l0);
					p0.setHeight("100%");
					if (rowspan > 1) {
						fmt.setHeight(x, y, (rowspan * 20) + "px;");
						p0.addStyleName(Model390.RESOURCES.css().aonVerticalAlignMiddle() );
						p0.addStyleName(Model390.RESOURCES.css().aonCurlyLT() );
					}
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottomImportant() );
					table.setWidget(x, y, p0);
					y++;
					
					for (Mod390DetailKey key: group.getKeys() ) {
						if (key.accept(mod390.getYear())) {
							final Mod390DetailFields fields = map.get(key); 
							Mod390Detail det = fields.getDetail();
							showBorder = (z == rowspan);
							
							// Taxable Base Box
							if ( key.hasTaxableBaseAvailable() ) {
								FlowPanel p = new FlowPanel();
								p.addStyleName(Model390.RESOURCES.css().aonTextCenter() );
								p.addStyleName(Model390.RESOURCES.css().aonSimpleBorder() );
								p.addStyleName(Model390.RESOURCES.css().aonFontSmall() );
								p.addStyleName(Model390.RESOURCES.css().aonPadding2() );
								p.addStyleName(Model390.RESOURCES.css().aonBackgroundDisabled() );
								p.add(new Label(Integer.toString(det.getTaxableBaseBox())));
								table.setWidget(x, y, p);
							}
							if (showBorder) {
								fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
							}
							++y;
							
							// Taxable Base 
							if ( key.hasTaxableBaseAvailable() ) {
								fields.getTaxableBase().addStyleName(Model390.RESOURCES.css().aonInputTextImportant());
								fields.getTaxableBase().setReadOnly(key.isReadonly());
								final double percent = det.getPercent();
									fields.getTaxableBase().addChangeHandler(new ChangeHandler() {
										@Override
										public void onChange(ChangeEvent event) {
											if (fields.getTaxableBase().isValidValue()) {
												fields.getDetail().setTaxableBase(fields.getTaxableBase().getDoubleValue());
												if  (percent != 0.0 && fields.getDetail().getQuota() == 0) {
													fields.setQuota( AonUtil.round( fields.getTaxableBase().getDoubleValue() * percent / 100) );
												}
											callback.calculateAndRefresh();
											refreshMap(mod390,fields.detail.getKey());
										}
									}
								});
								fields.getTaxableBase().setValue(det.getTaxableBase());
								table.setWidget(x, y, fields.getTaxableBase());
							}
							
							fmt.addStyleName(x, y ,Model390.RESOURCES.css().aonTextRight());
							if (showBorder) {
								fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
							}
							++y;
				
							// Percent 
							FlowPanel p1 = new FlowPanel();
							if  (det.getPercent() != 0.0) {
								p1.addStyleName(Model390.RESOURCES.css().aonTextCenter() );
								p1.addStyleName(Model390.RESOURCES.css().aonSimpleBorder() );
								p1.add(new Label(Model390.FMT.format(det.getPercent()))); 
							} 
							table.setWidget(x, y, p1);
							if (showBorder) {
								fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
							}
							++y;
							
							// Quota Box 
							FlowPanel p2 = new FlowPanel();
							p2.addStyleName(Model390.RESOURCES.css().aonTextCenter() );
							p2.addStyleName(Model390.RESOURCES.css().aonSimpleBorder() );
							p2.addStyleName(Model390.RESOURCES.css().aonFontSmall() );
							p2.addStyleName(Model390.RESOURCES.css().aonPadding2() );			
							p2.addStyleName(Model390.RESOURCES.css().aonBackgroundDisabled() );
							p2.add(new Label(Integer.toString(det.getBox())));
							table.setWidget(x, y, p2);
							if (showBorder) {
								fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
							}
							++y;
						
							// Quota  
							fields.getQuota().addStyleName(Model390.RESOURCES.css().aonInputTextImportant());
							fields.getQuota().setValue(det.getQuota());
							fields.getQuota().setReadOnly(key.isReadonly());
							fields.getQuota().addChangeHandler(new ChangeHandler() {
								
								@Override
								public void onChange(ChangeEvent event) {
									if (fields.getQuota().isValidValue()) {
										fields.getDetail().setQuota(fields.getQuota().getDoubleValue());
										callback.calculateAndRefresh();
										refreshMap(mod390,fields.detail.getKey());
									}
								}
							});
							table.setWidget(x, y, fields.getQuota());
							fmt.addStyleName(x, y ,Model390.RESOURCES.css().aonTextRight());
							if (showBorder) {
								fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
							}
							++y;
							
							Label l = new Label("");
							table.setWidget(x, y, l);
							if (showBorder) {
								fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
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
