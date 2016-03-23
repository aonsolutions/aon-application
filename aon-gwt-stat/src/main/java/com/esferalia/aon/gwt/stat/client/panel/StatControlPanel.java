package com.esferalia.aon.gwt.stat.client.panel;

import java.util.Stack;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.stat.client.MainEntryPoint;
import com.esferalia.aon.gwt.stat.client.StatServiceAsync;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.Table.Options;

public class StatControlPanel extends MainEntryPoint {

	static StatServiceAsync statService;

	interface StatControlPanelBinder extends UiBinder<Widget, StatControlPanel> {
	}

	private static final StatControlPanelBinder INVOICE_STAT_BINDER = GWT.create(StatControlPanelBinder.class);

	private int domain;
	private int enterprise;
	private int cont = 0;

	// Crea el diseño del panel, con los widgtes(Norte, Sur...) y los métodos de
	// cada hijo: addSouth... getWidgetDirection, insertEast...
	@UiField
	DockLayoutPanel dockLayoutPanel;
	// Un panel que contiene HTML y que puede colocar widgets hijos para
	// identificar elementos dentro de ese HTML.
	@UiField
	HTMLPanel toolbarPanel;
	// Clase base para los paneles que contienen sólo un widget.
	@UiField
	SimplePanel excelFormContainer;
	// Este panel se utiliza en la misma forma que DockLayoutPanel, excepto que
	// los tamaños para sus hijos siempre se especifican en {Unidadlink # PX} y
	// cada par de widgets hijos tiene un divisor entre los que el usuario puede
	// arrastrar.
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

	@UiField
	Button pruebaButton;

	StatFilter filter;
	StatFilter filterList;

	Stack<Widget> stack = new Stack<Widget>();

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		Widget ui = INVOICE_STAT_BINDER.createAndBindUi(this);
		root.add(ui);

		filter = new StatFilter();
		// filter.addStyleName(AON.AON_CSS.aonStatView());
		filter.paintFilter(new AsyncCallback<StatParams>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(StatParams result) {
				west.setWidget(filter);

				drawYearsByTypeComboChart(result);
				back.setEnabled(false);
				excel.setEnabled(false);
			}

		}, new SelectionHandler<StatFilterItem>() {

			@Override
			public void onSelection(SelectionEvent<StatFilterItem> event) {
				drawYearsByTypeComboChart(filter.getParams());
			}
		});

		// DisclosurePanel disclosurePanel = new DisclosurePanel();
		// disclosurePanel.setOpen(true);
		//
		// filterList = new StatFilter();
		// filterList.paintList(new AsyncCallback<StatParams>() {
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// Window.alert(caught.getMessage());
		// }
		//
		// @Override
		// public void onSuccess(StatParams result) {
		//
		// south.setWidget(filterList);
		//
		// drawYearsByTypeComboChart(result);
		// back.setEnabled(false);
		// excel.setEnabled(false);
		// }
		// }, new SelectionHandler<ListBox>() {
		// public void onSelection(SelectionEvent<ListBox> event) {
		// drawYearsByTypeComboChart(filterList.getParams());
		// }
		// });
		//
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
//		Se añade un gráfico de más a la pila, el primero, pero es necesario si no se tocan los filtros. 
//		Si se clicka en un filtro no debería volver hasta el primero...
		if (stack.size() > 0 && cont > 0) {
			stack.pop();
			cont--;
		} else {
			back.setEnabled(false);
			cont = 0;
		}
		if (stack.size() > 0 && cont > 0) {
			Widget w = stack.pop();
			cont--;
			content.setWidget(w);
			stack.push(w);
			cont++;
		} else {
			back.setEnabled(false);
			cont = 0;
		}
	}

	@UiHandler("excel")
	void onExcelButtonClick(ClickEvent event) {
		TableToExcelClient ttec = new TableToExcelClient(south.getWidget().getElement(), "table.xls");
		excelFormContainer.setWidget(ttec.getExportFormWidget());
		ttec.getExportFormWidget().submit();
	}

	// Pintamos el gráfico del medio y la tabla, con los datos recogidos de la
	// BD en StatDao pasándole un dominio
	public void drawYearsByTypeComboChart(StatParams params) {
		YearInvoiceTypeComboChart.getChart(getCurrentDomainName(), getCurrentDomain(), params, content.getOffsetWidth(),
				content.getOffsetHeight(), new AsyncCallback<YearInvoiceTypeComboChart>() {
					@Override
					public void onSuccess(final YearInvoiceTypeComboChart chart) {

						content.setWidget(chart);

						stack.push(chart);
						cont++;

						Options options = Options.create();
						setMeasures(options);

						final ResizableTable table = new ResizableTable(chart.data, options);
						table.setStyleName(AON.AON_CSS.aonWidthAll());
						table.setStyleName(AON.AON_CSS.aonHeightAll());

						south.setWidget(table);

						excel.setEnabled(true);

						chart.addSelectHandler(new SelectHandler() {
							// PARA QUE SE PUEDA "CLIKAR" SOBRE EL GRÁFICO
							@Override
							public void onSelect(SelectEvent event) {

								Selection s = chart.getSelections().get(0);
								int row = s.getRow();

								String y = table.data.getValueString(row, 0);
								Integer year = (int) AON.FMT_INT.parse(y);

								drawMonthsByTypeComboChart(year);

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

	// Month
	private void drawMonthsByTypeComboChart(Integer year) {
		MonthInvoiceTypeComboChart.getChart(getCurrentDomainName(), getCurrentDomain(), filter.getParams(),
				content.getOffsetWidth(), content.getOffsetHeight(), new AsyncCallback<MonthInvoiceTypeComboChart>() {

					@Override
					public void onSuccess(final MonthInvoiceTypeComboChart chart) {

						// content.add(getDateFromTo());

						content.setWidget(chart);

						stack.push(chart);
						cont++;

						Options options = Options.create();
						setMeasures(options);

						final ResizableTable table = new ResizableTable(chart.data, options);
						table.setStyleName(AON.AON_CSS.aonWidthAll());
						table.setStyleName(AON.AON_CSS.aonHeightAll());

						south.setWidget(table);

						excel.setEnabled(true);

						back.setEnabled(true);

						chart.addSelectHandler(new SelectHandler() {
							// PARA QUE SE PUEDA "CLIKAR" SOBRE EL GRÁFICO
							@Override
							public void onSelect(SelectEvent event) {

								Selection s = chart.getSelections().get(0);
								int row = s.getRow();

								String month = table.data.getValueString(row, 0);

								drawDaysByTypeComboChart(month);

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

	private void drawDaysByTypeComboChart(String month) {
		DayInvoiceTypeComboChart.getChart(getCurrentDomainName(), getCurrentDomain(), filter.getParams(),
				content.getOffsetWidth(), content.getOffsetHeight(), new AsyncCallback<DayInvoiceTypeComboChart>() {

					@Override
					public void onSuccess(final DayInvoiceTypeComboChart chart) {

						// content.add(getDateFromTo());

						content.setWidget(chart);

						stack.push(chart);
						cont++;

						Options options = Options.create();
						setMeasures(options);

						final ResizableTable table = new ResizableTable(chart.data, options);

						table.setStyleName(AON.AON_CSS.aonWidthAll());
						table.setStyleName(AON.AON_CSS.aonHeightAll());

						south.setWidget(table);

						excel.setEnabled(true);

						back.setEnabled(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						ErrorPanel errors = new ErrorPanel();
						errors.showError("Error inesperado");
						content.setWidget(errors);
					}
				});

	}

	private void setMeasures(Options options) {
		// estas options se pueden meter en una clase
		// "setMeasures"
		options.setAlternatingRowStyle(true);
		options.setWidth(south.getOffsetWidth() + "px");
		options.setHeight(south.getOffsetHeight() + "px");

	}

	/**
	 * private void drawYearsByTypePieChart() {
	 * YearInvoiceTypePieChart.getChart(params, content.getOffsetWidth(),
	 * content.getOffsetHeight(), new AsyncCallback<ResizablePieChart>() {
	 * 
	 * @Override public void onSuccess(final ResizablePieChart result) {
	 *           content.setWidget(result); Options options = Options.create();
	 * 
	 *           // po.setAlternatingRowStyle(true);
	 *           options.setWidth(south.getOffsetWidth() + "px");
	 *           options.setHeight(south.getOffsetHeight() + "px");
	 * 
	 *           final ResizableTable table = new ResizableTable(result.data,
	 *           options); table.setStyleName(AON.AON_CSS.aonWidthAll());
	 *           table.setStyleName(AON.AON_CSS.aonHeightAll());
	 *           south.setWidget(table);
	 * 
	 *           excel.setEnabled(true); }
	 * 
	 * @Override public void onFailure(Throwable caught) { ErrorPanel errors =
	 *           new ErrorPanel(); errors.showError("Error inesperado");
	 *           content.setWidget(errors); } }); }
	 **/

}
