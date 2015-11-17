package com.esferalia.aon.gwt.fiscal.client.stats;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.StatService;
import com.esferalia.aon.gwt.fiscal.client.StatServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.StatServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.ErrorPanel;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.stat.SelectableEnum;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;

public class StatControlPanel extends MainEntryPoint {

	static StatServiceAsync statService;

	interface StatControlPanelBinder extends UiBinder<Widget, StatControlPanel> {
	}

	private static final StatControlPanelBinder INVOICE_STAT_BINDER = GWT
			.create(StatControlPanelBinder.class);

	private int domain;
	private int enterprise;

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	HTMLPanel toolbarPanel;
	@UiField
	SimplePanel excelFormContainer;
	
	SplitLayoutPanel splitLayoutPanel;
	
	@UiField
	Button back;
	@UiField
	Button excel;
	
	@UiField
	ScrollPanel west;
	@UiField
	SimpleLayoutPanel content;
	@UiField
	ScrollPanel south;

	StatParams params;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		StatServiceAsync serviceRaw = GWT.create(StatService.class);
		statService = new StatServiceAsyncDecorator(serviceRaw);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		Widget ui = INVOICE_STAT_BINDER.createAndBindUi(this);
		root.add(ui);
		back.setEnabled(false);
		excel.setEnabled(false);
		
		VisualizationUtils.loadVisualizationApi( new Runnable() {
			
			@Override
			public void run() {
				statService.createStatParams(getCurrentDomainName(), getCurrentDomain()
						, new AsyncCallback<StatParams>() {
					
					@Override
					public void onSuccess(StatParams result) {
						params = result;
						filllWestPanel();
						drawYearsByTypeComboChart();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
				});
			}
		}, CoreChart.PACKAGE, Table.PACKAGE );
		


	}

	private void filllWestPanel() {
		FlowPanel panel = new FlowPanel();
		panel.add(getInvoiceTypesPanel());
		panel.add(getProductCategoriesPanel());
		west.setWidget(panel);
	}

	private Widget getInvoiceTypesPanel() {
		DisclosurePanel disclosurePanel = new DisclosurePanel( AON.MSG.invoiceType() );
		disclosurePanel.setOpen(true);
		
		FlowPanel checkPanel = new FlowPanel();
		for (final SelectableEnum<InvoiceType> type : params.getInvoiceTypes() ) {
			FlowPanel cp = new FlowPanel();
			final CheckBox check = new CheckBox( type.getType().getDescription() );
			cp.add(check);
			check.setValue(type.isSelected());
			check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					type.setSelected(event.getValue());
					drawYearsByTypeComboChart();
				}
			});
			checkPanel.add(cp);
		}
		disclosurePanel.setContent(checkPanel);
		return disclosurePanel;
	}

	private Widget getProductCategoriesPanel() {
		DisclosurePanel disclosurePanel = new DisclosurePanel( AON.MSG.productCategories() );
		disclosurePanel.setOpen(true);
		
		FlowPanel checkPanel = new FlowPanel();
		for (final ProductCategory pc : params.getProductCategories() ) {
			FlowPanel cp = new FlowPanel();
			final CheckBox check = new CheckBox( pc.getName() );
			cp.add(check);
			check.setValue(pc.isSelected());
			check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					pc.setSelected(event.getValue());
					drawYearsByTypeComboChart();
				}
			});
			checkPanel.add(cp);
		}
		disclosurePanel.setContent(checkPanel);
		return disclosurePanel;
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	@UiHandler("back")
	void onBackButtonClick(ClickEvent event) {
		back.setEnabled(false);
		drawYearsByTypeComboChart();
	}

	@UiHandler("excel")
	void onExcelButtonClick(ClickEvent event) {
		TableToExcelClient ttec = new TableToExcelClient(south.getWidget().getElement(), "table.xls");
		excelFormContainer.setWidget( ttec.getExportFormWidget() );
		ttec.getExportFormWidget().submit();
	}

	private void drawYearsByTypeComboChart() {
		YearInvoiceTypeComboChart.getChart(params
				,content.getOffsetWidth()
				,content.getOffsetHeight()
				, new AsyncCallback<ResizableComboChart>() {
			
			@Override
			public void onSuccess(final ResizableComboChart result) {
				content.setWidget(result);
				Options options = Options.create();
				options.setAlternatingRowStyle(true);
				options.setWidth(south.getOffsetWidth() + "px");
//				options.setHeight(south.getOffsetHeight()  + "px");
				final ResizableTable table = new ResizableTable(result.data, options);
				table.setStyleName(AON.AON_CSS.aonWidthAll());
//				table.setStyleName(AON.AON_CSS.aonHeightAll());
				south.setWidget(table);
				excel.setEnabled(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				ErrorPanel errors = new ErrorPanel();
				errors.showError("Error inesperado");
				content.setWidget(errors);
			}
		});
	}

	private void drawMonthsByTypeComboChart(Integer year) {
		MonthInvoiceTypeComboChart.getChart(params
				,content.getOffsetWidth()
				,content.getOffsetHeight()
				, new AsyncCallback<ResizableComboChart>() {
			
			@Override
			public void onSuccess(ResizableComboChart result) {
				back.setEnabled(true);
				content.setWidget(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				ErrorPanel errors = new ErrorPanel();
				errors.showError("Error inesperado");
				content.setWidget(errors);
			}
		});
	}
}
