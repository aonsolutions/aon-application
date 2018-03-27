package com.esferalia.aon.gwt.stat.client.panel.directsales;

import java.util.Date;
import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.InvoiceProductBox;
import com.esferalia.aon.gwt.common.client.widget.InvoiceRegistryBox;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.gwt.stat.client.StatServiceAsync;
import com.esferalia.aon.gwt.stat.client.StatServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.StatType;
import com.esferalia.aon.occam.api.model.stat.invoice.DirectSalesChartType;
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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;

public class StatFilter extends ScrollPanel implements HasValueChangeHandlers<StatParams> {

	private static class MediumLabel extends InlineLabel {
		private MediumLabel(String label) {
			super(label);
			setStyleName(AON.AON_CSS.aonFontMedium());
		}
	}

	private static StatServiceAsync STAT_SERVICE;

	public StatParams params;

	private String domainName;
	public int domain;
	public String user;

		
	
	public StatFilter() {
		super();
		addStyleName(AON.AON_CSS.aonPanelGridSearch());
		setHeight("100%");
	}
	
	public String getDomainName() {
		return getCurrentDomainName();
	}
	public int getDomain() {
		return getCurrentDomain();
	}
	public String getUser() {
		return getCurrentUser();
	}

	public StatParams getParams() {
		return params;
	}
	public void paintFilter(StatParams result) {
		params = result;
		params.setStatType(StatType.DIRECT_SALES);
		
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonWidth98Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab.getColumnFormatter().setWidth(0, "40px");
		tab.getColumnFormatter().setWidth(1, "90px");
		tab.getColumnFormatter().setWidth(2, "40px");
		tab.getColumnFormatter().setWidth(3, "90px");
		tab.getColumnFormatter().setWidth(4, "auto");
		tab.getColumnFormatter().setWidth(5, "90px");
		tab.getColumnFormatter().setWidth(6, "300px");
		tab.getColumnFormatter().setWidth(7, "90px");
		
		int row = 0;
		tab.setWidget(row, 0, new MediumLabel(AON.MSG.from()));
		final DateBoxEx from = new DateBoxEx();
		from.setValue(params.getFrom());
		from.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				params.setFrom(from.getValue());
				ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
			}
		});
		tab.setWidget(row, 1, from);
		
		
		tab.setWidget(row, 2, new MediumLabel(AON.MSG.until()));
		final DateBoxEx to = new DateBoxEx();
		to.setValue(params.getTo());
		to.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				params.setTo(to.getValue());
				ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
			}
		});
		tab.setWidget(row, 3, to);
		
		final CheckBox quantities = new CheckBox( AON.MSG.quantities());
		quantities.setStyleName(AON.AON_CSS.aonFontMedium());
		quantities.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				params.setViewAmounts(quantities.getValue());
				ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
			}
		});
		tab.setWidget(row, 4, quantities);
		
		tab.setWidget(row, 5, new MediumLabel(AON.MSG.graphicType()));
		final ListBox chartType = new ListBox();
		chartType.setStyleName(AON.AON_CSS.aonMarginRight());
		chartType.addStyleName(AON.AON_CSS.aonWidth300());
		for (DirectSalesChartType type : DirectSalesChartType.values()) {
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
		tab.setWidget(row, 6, chartType);
		
		
		Button clean = new Button( AON.MSG.clean() );
		clean.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				result.clean();
				paintFilter(result);	
				ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
			}
		});
		tab.setWidget(row, 7, clean );
		row++;
		
		tab.setWidget(row, 0, new MediumLabel(AON.MSG.titular()));
		InvoiceRegistryBox titular = new InvoiceRegistryBox(getDomainName(),getDomain() );
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
		tab.getFlexCellFormatter().setColSpan(row, 1, 7);
		tab.setWidget(row, 1, titular);
		row++;
		
		tab.setWidget(row, 0, new MediumLabel(AON.MSG.product()));
		InvoiceProductBox product = new InvoiceProductBox(getDomainName(),getDomain() );
		product.setRequired(false);
		product.addSelectionHandler(new  SelectionHandler<Product>() {

			@Override
			public void onSelection(SelectionEvent<Product> event) {
				if (event.getSelectedItem() != null) {
					params.setProduct( event.getSelectedItem().getId());
				} else {
					params.setProduct( null );
				}
				ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
			}
		});
		tab.getFlexCellFormatter().setColSpan(row, 1, 7);
		tab.setWidget(row, 1, product);
		row++;

		tab.setWidget(row, 0, new MediumLabel("Filtrar por: "));
		FlexTable filterTab = new FlexTable();
		int col = 1;
		final HashMap<StatFilterType,StatFilterMenu> menus = new HashMap<StatFilterType,StatFilterMenu>();					
		for (final StatFilterItem item : result.getFilterItems()) {
			if (item.getType() != StatFilterType.INVOICE_TYPE) {
				if (!menus.containsKey( item.getType())) {
					boolean multipleSelection = item.getType() == StatFilterType.SEGMENT || item.getType() == StatFilterType.PRODUCT_TAG; 
					final StatFilterMenu menu = new StatFilterMenu(multipleSelection);
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
					
					filterTab.getColumnFormatter().setWidth(col, "120px");
					filterTab.setWidget(0, col++, button);
					menu.addSelectionHandler(new SelectionHandler<StatFilterItem>() {
						
						@Override
						public void onSelection(SelectionEvent<StatFilterItem> event) {
							
							if (menu.getItemsSelected() != 0) {
								button.setText( item.getType().getName() + "(" + menu.getItemsSelected() +")");
								button.addStyleName(AON.AON_CSS.aonBold());			
							} else {
								button.setText( item.getType().getName());
								button.removeStyleName(AON.AON_CSS.aonBold());
							}
							ValueChangeEvent.<StatParams>fire(StatFilter.this, params);
						}
					});
				}
				StatFilterMenu parent = menus.get( item.getType());								
				parent.addItem( item );						
			}
		}
		tab.getFlexCellFormatter().setColSpan(row, 1, 7);
		tab.setWidget(row, 1, filterTab);
		row++;

		setWidget(tab);
	}

	public void paintFilter(final AsyncCallback<StatParams> callback) {
		AON.ensureInjected();
		StatServiceAsync serviceRaw = GWT.create(StatService.class);
		STAT_SERVICE = new StatServiceAsyncDecorator(serviceRaw);

		STAT_SERVICE.createStatParams(getDomainName(),getUser(), getDomain(),
			new AsyncCallback<StatParams>() {

				@Override
				public void onSuccess(StatParams result) {
					paintFilter(result);
					callback.onSuccess(result);
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					callback.onFailure(caught);
				}
		});
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<StatParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType()); 
	}
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getCurrentUser()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;
}
