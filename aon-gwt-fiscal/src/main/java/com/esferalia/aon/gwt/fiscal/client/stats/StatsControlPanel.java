package com.esferalia.aon.gwt.fiscal.client.stats;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.ErrorPanel;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.Table.Options;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;

public class StatsControlPanel extends MainEntryPoint {

	interface StatsControlPanelBinder extends UiBinder<Widget, StatsControlPanel> {
	}

	private static final StatsControlPanelBinder INVOICE_STATS_BINDER = GWT
			.create(StatsControlPanelBinder.class);

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

	StatParams params = new StatParams();

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		params.setDomainName(getCurrentDomainName());
		params.setDomain(getCurrentDomain());

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		Widget ui = INVOICE_STATS_BINDER.createAndBindUi(this);
		root.add(ui);
		back.setEnabled(false);
		excel.setEnabled(false);
		
		filllWestPanel();
		
				
		VisualizationUtils.loadVisualizationApi(new Runnable() {
			
			@Override
			public void run() {
				drawYearsByTypeComboChart();
			}
		}, CoreChart.PACKAGE);


	}

	private void filllWestPanel() {
		FlowPanel panel = new FlowPanel();
		
		DisclosurePanel disclosurePanel = new DisclosurePanel( AON.MSG.invoiceType() );
		disclosurePanel.setAnimationEnabled(true);
		disclosurePanel.setOpen(true);
		
		FlowPanel checkPanel = new FlowPanel();
		for (final InvoiceType type : InvoiceType.values()) {
			FlowPanel cp = new FlowPanel();
			final CheckBox check = new CheckBox( type.getDescription() );
			cp.add(check);
			check.setValue(true);
			params.addInvoiceType(type);
			check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (event.getValue()) {
						params.addInvoiceType(type);
					} else {
						params.removeInvoiceType(type);
					}
					drawYearsByTypeComboChart();
				}
			});
			checkPanel.add(cp);
		}
		disclosurePanel.setContent(checkPanel);
		panel.add(disclosurePanel);
		west.setWidget(panel);
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
				options.setHeight(south.getOffsetHeight()  + "px");
				final ResizableTable table = new ResizableTable(result.data, options);
				
				table.setStyleName(AON.AON_CSS.aonWidthAll());
				table.setStyleName(AON.AON_CSS.aonHeightAll());
				south.setWidget(table);
				excel.setEnabled(true);
				
				table.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						result.setSelections(table.getSelections());
					}
					
				});
				
				result.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(SelectEvent event) {
						table.setSelections(result.getSelections());
						
					}
					
				});
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
