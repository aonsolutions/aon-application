package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.EnumMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390DetailKey;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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

//	private FiscalServiceAsync fiscalService;
	
	public static class Mod390DetailFields {
		Mod390Detail detail;
		DoubleTextBox taxableBase;
		DoubleTextBox quota;
		
		protected Mod390DetailFields(Mod390Detail detail) {
			this.detail = detail;
			taxableBase = new DoubleTextBox();
			quota = new DoubleTextBox();
		}
	} 
	
	@UiField(provided = true)
	FlexTable table;
	
	String domainName;
	int domain;
	int year;
//	private Page10 page10;
	
	EnumMap<Mod390DetailKey, Mod390DetailFields> map; 
	
	public Page5() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		Model390.RESOURCES.css().ensureInjected();
//		FiscalServiceAsync mod190ServiceRaw = GWT.create(FiscalService.class);
//		fiscalService = new FiscalServiceAsyncDecorator(mod190ServiceRaw);
		table = new FlexTable();
		Widget ui = page5Binder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}

//	public void setPage10(Page10 page10) {
//		this.page10 = page10;
//	}
	
	public void initialize(String domainName, int domain, int year, Mod390 mod390) {
		this.domainName = domainName;
		this.domain = domain;
		this.year = year;
//		initializeList(mod390);
	}

//	private void initializeList(final Mod390 mod390) {
//		map = new EnumMap<Mod390DetailKey, Mod390DetailFields>(Mod390DetailKey.class);
//		final PopupPanel popup = new PopupPanel(false, true);
//		Label label = new Label(Model390.MSG.processing());
//		label.addStyleName(Model390.RESOURCES.css().aonTimer());
//		popup.add(label);
//		popup.setGlassEnabled(true);
//		popup.setAnimationEnabled(true);
//		popup.center();
//		fiscalService.getMod390Details(domainName,domain, mod390,
//				new AsyncCallback<ArrayList<Mod390Detail>>() {
//					@Override
//					public void onSuccess(ArrayList<Mod390Detail> result) {
//						for (Mod390Detail detail : result) {
//							map.put(detail.getKey(),new Mod390DetailFields(detail));
//								
//							if (detail.getKey().isPage5Key()) {
//								if (!mod390.isSimplifiedRegime() ) {
//								} else {
//									detail.setPercent(0);
//									detail.setQuota(0);
//									detail.setTaxableBase(0);
//									map.put(detail.getKey(),detail);
//								}
//							}
//							if (detail.getKey().isPage10Key()) {
//								page10.fillBox( detail );
//							}
//						}
//						initializeTable();
//						calculate();
//						popup.hide();
//						populate(mod390);
//					}
//
//					@Override
//					public void onFailure(Throwable caught) {
//						popup.hide();
//						DialogMessages.alertErrorWidget(Model390.MSG
//								.unableToFindMod190Detail(caught
//										.getMessage()));
//					}
//				});
//	}

//	public Map<Mod390DetailKey, DoubleTextBox> getQuotaMap() {
//		return quotaMap;
//	}
	
	private void initializeTable() {
//		taxableBaseMap = new HashMap<Mod390DetailKey, DoubleTextBox>();
//		quotaMap = new HashMap<Mod390DetailKey, DoubleTextBox>();
		
		table.setWidth("100%");
		table.setCellSpacing(0);
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(0, "450px");
		cf.setWidth(1, "40px");
		cf.setWidth(2, "140px");
		cf.setWidth(3, "80px");
		cf.setWidth(4, "40px");
		cf.setWidth(5, "140px");
		
		
		FlexCellFormatter fmt = table.getFlexCellFormatter();
		

		int x = 0;
		int y = 0;
		int z = 1;
		int rowspan = 0;
		boolean oddRow = false;
		boolean showBorder = false;
		if (map != null) {
			for (final Mod390DetailFields fields : map.values() ) {
				Mod390Detail det = fields.detail;
				showBorder = false;
				y = 0;
				String label = Model390.FISCAL_MSG.mod390DetailKey(det.getKey());
				if ( AonStringUtils.isNotBlank(label )) {
					z = 1;
					oddRow = !oddRow;
					rowspan = 3;
					fmt.setRowSpan(x, y, rowspan);
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonVerticalAlignMiddle() );
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonPaddingLeft() );
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonPaddingRight() );
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom() );
					if (rowspan > 1) {
						fmt.addStyleName(x, y,Model390.RESOURCES.css().aonCurlyLT() );
					}
//					if (false) {
//						fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBold());	
//					}
					if (oddRow) {
						fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBackgroundDisabled() );	
					}
					table.setWidget(x, y, new Label(label));
					++y;
				}
				showBorder = (z == rowspan);
				
				if ( true ) {
					FlowPanel p = new FlowPanel();
					p.addStyleName(Model390.RESOURCES.css().aonTextCenter() );
					p.addStyleName(Model390.RESOURCES.css().aonSimpleBorder() );
					p.addStyleName(Model390.RESOURCES.css().aonFontSmall() );
					p.addStyleName(Model390.RESOURCES.css().aonPadding2() );
					p.add(new Label(Integer.toString(det.getTaxableBaseBox())));
					table.setWidget(x, y, p);
				} 
				if (oddRow) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBackgroundDisabled() );	
				}
				if (showBorder) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
				}
				++y;
				
				if ( true ) {
					DoubleTextBox taxableBase = fields.taxableBase;
					taxableBase.addStyleName(Model390.RESOURCES.css().aonInputTextImportant());
					if  (det.getPercent() != 0.0) {
						final double percent = det.getPercent();
						taxableBase.addValueChangeHandler(new ValueChangeHandler<String>() {
							
							@Override
							public void onValueChange(ValueChangeEvent<String> event) {
								try {
									double tBase = Model390.FMT.parse(event.getValue());
									if (fields.detail.getQuota() == 0) {
										fields.detail.setQuota( AonUtil.round( tBase * percent / 100));	
									}
									fields.detail.setTaxableBase(tBase);
								} catch (NumberFormatException e) {
									// Nothing
								}
							}
						});
					}
					taxableBase.setValue(det.getTaxableBase());
					table.setWidget(x, y, taxableBase);
				} 
				fmt.addStyleName(x, y ,Model390.RESOURCES.css().aonTextRight());
				if (oddRow) {
					fmt.addStyleName(x, y ,Model390.RESOURCES.css().aonBackgroundDisabled() );	
				}
				if (showBorder) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
				}
				++y;
	
				FlowPanel p1 = new FlowPanel();
				if  (det.getPercent() != 0.0) {
					p1.addStyleName(Model390.RESOURCES.css().aonTextCenter() );
					p1.addStyleName(Model390.RESOURCES.css().aonSimpleBorder() );
					p1.add(new Label(Model390.FMT.format(det.getPercent()))); 
				} 
				table.setWidget(x, y, p1);
				if (oddRow) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBackgroundDisabled() );	
				}
				if (showBorder) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
				}
				++y;
				
				FlowPanel p2 = new FlowPanel();
				p2.addStyleName(Model390.RESOURCES.css().aonTextCenter() );
				p2.addStyleName(Model390.RESOURCES.css().aonSimpleBorder() );
				p2.addStyleName(Model390.RESOURCES.css().aonFontSmall() );
				p2.addStyleName(Model390.RESOURCES.css().aonPadding2() );			
				p2.add(new Label(Integer.toString(det.getBox())));
				table.setWidget(x, y, p2);
				if (oddRow) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBackgroundDisabled() );	
				}
				if (showBorder) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
				}
				++y;
			
				final DoubleTextBox quota = fields.quota;
				quota.addStyleName(Model390.RESOURCES.css().aonInputTextImportant());
				quota.setValue(det.getQuota());
				quota.addValueChangeHandler(new ValueChangeHandler<String>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						try {
							double tBase = Model390.FMT.parse(event.getValue());
							fields.detail.setQuota(tBase);
						} catch (NumberFormatException e) {
							// Nothing
						}
					}
				});
	
				table.setWidget(x, y, quota);
				fmt.addStyleName(x, y ,Model390.RESOURCES.css().aonTextRight());
				if (oddRow) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBackgroundDisabled() );	
				}
				if (showBorder) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
				}
				++y;
				
				Label l = new Label("");
				table.setWidget(x, y, l);
				if (oddRow) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBackgroundDisabled() );	
				}
				if (showBorder) {
					fmt.addStyleName(x, y,Model390.RESOURCES.css().aonBorderBottom());	
				}
				++x;
				++z;
			}
		}		
	}
/*	
	private static final Mod390DetailKey[] K09_FORMULA = {
			Mod390DetailKey.K00_04, Mod390DetailKey.K00_08,
			Mod390DetailKey.K00_10, Mod390DetailKey.K00_18,
			Mod390DetailKey.K00_21, Mod390DetailKey.K01_04,
			Mod390DetailKey.K01_08, Mod390DetailKey.K01_10,
			Mod390DetailKey.K01_18, Mod390DetailKey.K01_21,
			Mod390DetailKey.K02_04, Mod390DetailKey.K02_08,
			Mod390DetailKey.K02_10, Mod390DetailKey.K02_18,
			Mod390DetailKey.K02_21, Mod390DetailKey.K03_18,
			Mod390DetailKey.K03_21, Mod390DetailKey.K04_04,
			Mod390DetailKey.K04_08, Mod390DetailKey.K04_10,
			Mod390DetailKey.K04_18, Mod390DetailKey.K04_21,
			Mod390DetailKey.K05_04, Mod390DetailKey.K05_08,
			Mod390DetailKey.K05_10, Mod390DetailKey.K05_18,
			Mod390DetailKey.K05_21, Mod390DetailKey.K06, Mod390DetailKey.K07,
			Mod390DetailKey.K08 };
	
	private static final Mod390DetailKey[] K13_FORMULA = { Mod390DetailKey.K09,
			Mod390DetailKey.K10_05, Mod390DetailKey.K10_1,
			Mod390DetailKey.K10_14, Mod390DetailKey.K10_4,
			Mod390DetailKey.K10_52, Mod390DetailKey.K10_175,
			Mod390DetailKey.K11, Mod390DetailKey.K12 };

	private static final Mod390DetailKey[] K15_FORMULA = {
			Mod390DetailKey.K14_04, Mod390DetailKey.K14_07,
			Mod390DetailKey.K14_08, Mod390DetailKey.K14_10,
			Mod390DetailKey.K14_16, Mod390DetailKey.K14_18,
			Mod390DetailKey.K14_21 };

	private static final Mod390DetailKey[] K17_FORMULA = {
			Mod390DetailKey.K16_04, Mod390DetailKey.K16_07,
			Mod390DetailKey.K16_08, Mod390DetailKey.K16_10,
			Mod390DetailKey.K16_16, Mod390DetailKey.K16_18,
			Mod390DetailKey.K16_21 };

	private static final Mod390DetailKey[] K19_FORMULA = {
			Mod390DetailKey.K18_04, Mod390DetailKey.K18_07,
			Mod390DetailKey.K18_08, Mod390DetailKey.K18_10,
			Mod390DetailKey.K18_16, Mod390DetailKey.K18_18,
			Mod390DetailKey.K18_21 };

	private static final Mod390DetailKey[] K21_FORMULA = {
			Mod390DetailKey.K20_04, Mod390DetailKey.K20_07,
			Mod390DetailKey.K20_08, Mod390DetailKey.K20_10,
			Mod390DetailKey.K20_16, Mod390DetailKey.K20_18,
			Mod390DetailKey.K20_21 };

	private static final Mod390DetailKey[] K23_FORMULA = {
			Mod390DetailKey.K22_04, Mod390DetailKey.K22_07,
			Mod390DetailKey.K22_08, Mod390DetailKey.K22_10,
			Mod390DetailKey.K22_16, Mod390DetailKey.K22_18,
			Mod390DetailKey.K22_21 };

	private static final Mod390DetailKey[] K25_FORMULA = {
			Mod390DetailKey.K24_04, Mod390DetailKey.K24_07,
			Mod390DetailKey.K24_08, Mod390DetailKey.K24_10,
			Mod390DetailKey.K24_16, Mod390DetailKey.K24_18,
			Mod390DetailKey.K24_21 };

	private static final Mod390DetailKey[] K27_FORMULA = {
			Mod390DetailKey.K26_04, Mod390DetailKey.K26_07,
			Mod390DetailKey.K26_08, Mod390DetailKey.K26_10,
			Mod390DetailKey.K26_16, Mod390DetailKey.K26_18,
			Mod390DetailKey.K26_21 };

	private static final Mod390DetailKey[] K29_FORMULA = {
			Mod390DetailKey.K28_04, Mod390DetailKey.K28_07,
			Mod390DetailKey.K28_08, Mod390DetailKey.K28_10,
			Mod390DetailKey.K28_16, Mod390DetailKey.K28_18,
			Mod390DetailKey.K28_21 };

	private static final Mod390DetailKey[] K31_FORMULA = {
			Mod390DetailKey.K30_04, Mod390DetailKey.K30_07,
			Mod390DetailKey.K30_08, Mod390DetailKey.K30_10,
			Mod390DetailKey.K30_16, Mod390DetailKey.K30_18,
			Mod390DetailKey.K30_21 };	
	
	private static final Mod390DetailKey[] K36_FORMULA = { Mod390DetailKey.K15,
			Mod390DetailKey.K17, Mod390DetailKey.K19, Mod390DetailKey.K21,
			Mod390DetailKey.K23, Mod390DetailKey.K25, Mod390DetailKey.K27,
			Mod390DetailKey.K29, Mod390DetailKey.K31 };

	private void calculate() {
		add(taxableBaseMap,Mod390DetailKey.K09,K09_FORMULA);
		add(quotaMap,Mod390DetailKey.K09,K09_FORMULA);
		
		add(quotaMap,Mod390DetailKey.K13,K13_FORMULA);
		
		add(taxableBaseMap,Mod390DetailKey.K15,K15_FORMULA);
		add(quotaMap,Mod390DetailKey.K15,K15_FORMULA);
		
		add(taxableBaseMap,Mod390DetailKey.K17,K17_FORMULA);
		add(quotaMap,Mod390DetailKey.K17,K17_FORMULA);
		
		add(taxableBaseMap,Mod390DetailKey.K19,K19_FORMULA);
		add(quotaMap,Mod390DetailKey.K19,K19_FORMULA);
		
		add(taxableBaseMap,Mod390DetailKey.K21,K21_FORMULA);
		add(quotaMap,Mod390DetailKey.K21,K21_FORMULA);
		
		add(taxableBaseMap,Mod390DetailKey.K23,K23_FORMULA);
		add(quotaMap,Mod390DetailKey.K23,K23_FORMULA);
		
		add(taxableBaseMap,Mod390DetailKey.K25,K25_FORMULA);
		add(quotaMap,Mod390DetailKey.K25,K25_FORMULA);
		
		add(taxableBaseMap,Mod390DetailKey.K27,K27_FORMULA);
		add(quotaMap,Mod390DetailKey.K27,K27_FORMULA);
		
		add(taxableBaseMap,Mod390DetailKey.K29,K29_FORMULA);
		add(quotaMap,Mod390DetailKey.K29,K29_FORMULA);
		
		add(taxableBaseMap,Mod390DetailKey.K31,K31_FORMULA);
		add(quotaMap,Mod390DetailKey.K31,K31_FORMULA);

		add(quotaMap,Mod390DetailKey.K36,K36_FORMULA);

		quotaMap.get(Mod390DetailKey.K37).setValue( AonUtil.round(
				quotaMap.get(Mod390DetailKey.K13).getDoubleValue() - 
				quotaMap.get(Mod390DetailKey.K36).getDoubleValue()));
		
	}
	
	private void add(Map<Mod390DetailKey, DoubleTextBox> map,
			Mod390DetailKey intoKey, Mod390DetailKey[] keys) {
		double result = 0;
		for (Mod390DetailKey key : keys ) {
			result = AonUtil.round(result + map.get(key).getDoubleValue());
		}
		map.get(intoKey).setValue(result);
	}
*/

	public void setValue(Mod390 m390) {
		wrap(m390.getGeneralRegime());
		if (map != null) {
			initializeTable();
		}
	}

	private void wrap(Map<Mod390DetailKey, Mod390Detail> generalRegime) {
		for (Mod390DetailKey key : generalRegime.keySet()) {
			map.put(key,new Mod390DetailFields(generalRegime.get(key)));	
		}
	}

	public void populate(Mod390 mod390) {
//		for (Mod390DetailKey key : taxableBaseMap.keySet()) {
//			DoubleTextBox box = taxableBaseMap.get(key);
//			Mod390Detail detail = map.get(key);
//			detail.setTaxableBase(box.getDoubleValue());
//		}
//		for (Mod390DetailKey key : quotaMap.keySet()) {
//			DoubleTextBox box = quotaMap.get(key);
//			Mod390Detail detail = map.get(key);
//			detail.setQuota(box.getDoubleValue());
//		}
//		mod390.setGeneralRegime(map);
	}

}
