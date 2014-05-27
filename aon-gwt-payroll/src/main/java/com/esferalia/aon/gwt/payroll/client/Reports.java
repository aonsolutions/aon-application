package com.esferalia.aon.gwt.payroll.client;

import static com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.DATE_SHORT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.ReportsObject.ReportsType;
import com.esferalia.aon.gwt.payroll.client.SelectDialog.AcceptHandler;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;
import com.google.gwt.visualization.client.visualizations.Table.Options.CssClassNames;
import com.google.gwt.visualization.client.visualizations.Table.Options.Policy;

public class Reports extends ResizeComposite {

	interface Binder extends UiBinder<Widget, Reports> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	SimplePanel panel;

	@UiField
	Button excelButton;

	@UiField
	ListBox reportListBox;
	@UiField
	MonthListBox monthListBox;

	@UiField
	Label titleLabel;

	@UiField
	MenuItem filterMenuItem;

	private DataTable dataTable;
	private ReportsObject reportsObject;

	public Reports() {

		initWidget(binder.createAndBindUi(this));

		excelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String title = getSelectedType().getDescription()
						+ "_"
						+ DateTimeFormat.getFormat(DATE_SHORT).format(
								getSelectedMonth());

				Window.open(ReportsObject.toCSVDataURL(dataTable, ','), title,
						null);
			}
		});

		// init reportListBox
		for (ReportsType type : ReportsType.values())
			reportListBox.addItem(type.getDescription(), type.name());

		reportListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				reportsObject.setReportsType(getSelectedType());
				reloadData();
			}
		});

		monthListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				reportsObject.setMonth(monthListBox.getSelectedMonth());
				reloadData();
			}
		});

		filterMenuItem.setScheduledCommand(new ScheduledCommand() {

			class WorkPlaceReportDialog extends SelectDialog<Workplace>
					implements AcceptHandler {

				public WorkPlaceReportDialog() {
					super();

					addAcceptHandler(this);
					Column<Workplace, String> descriptionColumn = new Column<Workplace, String>(
							new TextCell()) {
						@Override
						public String getValue(Workplace workplace) {
							return workplace.getDescription();
						}
					};

					addColumn(descriptionColumn, "HOTEL");
				}

				@Override
				public void onAccept(AcceptEvent event) {
					reportsObject.setWorkplaces(new ArrayList<Workplace>(
							WorkPlaceReportDialog.this.getSelectedData()));
					Reports.this.reloadData();
				}

			}

			WorkPlaceReportDialog workplaceDialog = new WorkPlaceReportDialog();

			@Override
			public void execute() {

				workplaceDialog.setData(reportsObject.getAllWorkplaces());
				workplaceDialog.setSelectedData(reportsObject.getWorkplaces());

				workplaceDialog.setWidth(Window.getClientWidth() / 2 + "px");

				workplaceDialog.center();
			}
		});

	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		titleLabel.setText(title);
	}

	public void setReportsObject(ReportsObject reportsObject) {
		this.reportsObject = reportsObject;
		setSelectedMonth(reportsObject.getMonth());
		setSelectedType(reportsObject.getReportsType());
		// none of them cause change event... so we need reload data by hand
		reloadData();
	}

	// -------------------------------------------------------- Private methods

	private void reloadData() {
		reportsObject.getReport(new AsyncCallback<DataTable>() {

			@Override
			public void onFailure(Throwable t) {
				// TODO Auto-generated method stub
				Window.alert(t.getMessage());

			}

			@Override
			public void onSuccess(DataTable dataTable) {

				Reports.this.dataTable = dataTable;

				Options options = Options.create();

				options.setSort(Policy.ENABLE);
				options.setAlternatingRowStyle(true);

				CssClassNames cssClassNames = CssClassNames.createObject()
						.cast();
				cssClassNames.setHeaderRow("aon-dataTable-header");
				cssClassNames.setTableRow("aon-dataTable-row-even");
				cssClassNames.setOddTableRow("aon-dataTable-row-odd");
				cssClassNames.setHoverTableRow("aon-table-row-hover");

				options.setCssClassNames(cssClassNames);

				Table table = new Table(dataTable, options);
				table.addStyleName("aon-dataTable-chart");
				panel.setWidget(table);
			}

		});
	}

	private ReportsType getSelectedType() {

		int selected = reportListBox.getSelectedIndex();
		String value = reportListBox.getValue(selected);

		for (ReportsType type : ReportsType.values())
			if (StringUtils.equals(value, type.name()))
				return type;

		return null;
	}

	private void setSelectedType(ReportsType type) {
		int count = reportListBox.getItemCount();
		for (int i = 0; i < count; i++) {
			String value = reportListBox.getValue(i);
			if (StringUtils.equals(value, type.name())) {
				reportListBox.setSelectedIndex(i);
				return;
			}
		}

	}

	private Date getSelectedMonth() {
		return monthListBox.getSelected();
	}

	private void setSelectedMonth(final Date month) {
		monthListBox.setSelectedMonth(month);
	}

}
