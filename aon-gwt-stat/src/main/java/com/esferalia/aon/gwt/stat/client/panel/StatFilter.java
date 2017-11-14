package com.esferalia.aon.gwt.stat.client.panel;

import java.util.Date;
import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.InvoiceRegistryBox;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.gwt.stat.client.StatServiceAsync;
import com.esferalia.aon.gwt.stat.client.StatServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.StatType;
import com.esferalia.aon.occam.api.model.stat.invoice.InvoiceChartType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

public class StatFilter extends FlowPanel implements HasValueChangeHandlers<StatParams> {

	private static class MediumLabel extends InlineLabel {
		private MediumLabel(String label) {
			super(label);
			setStyleName(AON.AON_CSS.aonFontMedium());
		}
	}

	private static StatServiceAsync STAT_SERVICE;

	public StatParams params;

	public StatFilter() {
		super();
		addStyleName(AON.AON_CSS.aonPanelGridSearch());
	}

	public StatParams getParams() {
		return params;
	}

	public void paintFilter(final AsyncCallback<StatParams> callback) {
		AON.ensureInjected();
		StatServiceAsync serviceRaw = GWT.create(StatService.class);
		STAT_SERVICE = new StatServiceAsyncDecorator(serviceRaw);

		STAT_SERVICE.createStatParams(getCurrentDomainName(), getCurrentDomain(),
			new AsyncCallback<StatParams>() {

				@Override
				public void onSuccess(StatParams result) {
					params = result;
					params.setStatType(StatType.INVOICE);
					
					FlexTable tab = new FlexTable();
					tab.setStyleName(AON.AON_CSS.aonWidthAll());
					tab.addStyleName(AON.AON_CSS.aonNowrap());
					
					tab.getColumnFormatter().setWidth(0, "40px");
					tab.getColumnFormatter().setWidth(1, "90px");
					tab.getColumnFormatter().setWidth(2, "40px");
					tab.getColumnFormatter().setWidth(3, "90px");
					
					tab.setWidget(0, 0, new MediumLabel(AON.MSG.from()));
					final DateBoxEx from = new DateBoxEx();
					from.setValue(params.getFrom());
					from.addValueChangeHandler(new ValueChangeHandler<Date>() {
						@Override
						public void onValueChange(ValueChangeEvent<Date> event) {
							params.setFrom(from.getValue());
							ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
						}
					});
					tab.setWidget(0, 1, from);
					
					
					tab.setWidget(0, 2, new MediumLabel(AON.MSG.until()));
					final DateBoxEx to = new DateBoxEx();
					to.setValue(params.getTo());
					to.addValueChangeHandler(new ValueChangeHandler<Date>() {
						@Override
						public void onValueChange(ValueChangeEvent<Date> event) {
							params.setTo(to.getValue());
							ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
						}
					});
					tab.setWidget(0, 3, to);
					
					int col = 4;
					final HashMap<StatFilterType,StatFilterMenu> menus = new HashMap<StatFilterType,StatFilterMenu>();					
					for (final StatFilterItem item : result.getFilterItems()) {
						if (!menus.containsKey( item.getType())) {
							final StatFilterMenu menu = new StatFilterMenu();
							menus.put(item.getType(), menu);								
							
							final Button button = new Button(item.getType().getName() );
							button.setStyleName(AON.AON_CSS.aonDropButton());
							
							button.addClickHandler(new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									int x =  button.getAbsoluteLeft();
									int y = button.getAbsoluteTop() + button.getOffsetHeight() + 2;
									menu.setPopupPosition(x,y);
									menu.show();
								}
							});
							
							tab.getColumnFormatter().setWidth(col, "120px");
							tab.setWidget(0, col++, button);
							menu.addSelectionHandler(new SelectionHandler<StatFilterItem>() {
								
								@Override
								public void onSelection(SelectionEvent<StatFilterItem> event) {
									ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
								}
							});
						}
						StatFilterMenu parent = menus.get( item.getType());								
						parent.addItem( item );						
						
					}
					
					tab.getColumnFormatter().setWidth(col, "auto");
					tab.getCellFormatter().setStyleName(0, col, AON.AON_CSS.aonTextRight());
					tab.setWidget(0, col++, new MediumLabel(AON.MSG.graphicType()));
					final ListBox chartType = new ListBox();
					chartType.setStyleName(AON.AON_CSS.aonMarginRight());
					chartType.addStyleName(AON.AON_CSS.aonWidth300());
					for (InvoiceChartType type : InvoiceChartType.values()) {
						chartType.addItem(type.getDescription());
					}

					chartType.setSelectedIndex(result.getChartType());
					chartType.addChangeHandler( new ChangeHandler() {
						
						@Override
						public void onChange(ChangeEvent event) {
							params.setChartType((byte) chartType.getSelectedIndex());
							ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
						}
					});
					
					tab.getColumnFormatter().setWidth(col, "300px");
					tab.setWidget(0, col, chartType);
					
					tab.setWidget(1, 0, new MediumLabel(AON.MSG.titular()));
					InvoiceRegistryBox titular = new InvoiceRegistryBox(getCurrentDomainName(),getCurrentDomain() );
					titular.setRequired(false);
					titular.addSelectionHandler(new  SelectionHandler<InvoiceRegistry>() {
						
						@Override
						public void onSelection(SelectionEvent<InvoiceRegistry> event) {
							if (event.getSelectedItem() != null) {
								params.setRegistry( event.getSelectedItem().getId());
							} else {
								params.setRegistry( null );
							}
							ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
						}
					});							
					tab.setWidget(1, 1, titular);
					tab.getFlexCellFormatter().setColSpan(1, 1, (col-1));

					final CheckBox quantities = new CheckBox( AON.MSG.quantities());
					quantities.setStyleName(AON.AON_CSS.aonFontMedium());
					quantities.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							params.setViewAmounts(quantities.getValue());
							ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
						}
					});
					tab.setWidget(1, 2, quantities);

//					tab.setWidget(2, 0, new MediumLabel(AON.MSG.product()));
//					IntegerBox product = new IntegerBox();
//					product.setVisibleLength(10);
//					product.addValueChangeHandler( new ValueChangeHandler<Integer>() {
//
//						@Override
//						public void onValueChange(ValueChangeEvent<Integer> event) {
//							params.setProduct(product.getValue());
//							ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
//						}
//					});
//					tab.setWidget(2, 1, product);
//					tab.getFlexCellFormatter().setColSpan(2, 1, (col+4));
					
					add(tab);
					callback.onSuccess(result);
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					callback.onFailure(caught);
				}
		});
	}
	

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<StatParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType()); 
	}
	
}
