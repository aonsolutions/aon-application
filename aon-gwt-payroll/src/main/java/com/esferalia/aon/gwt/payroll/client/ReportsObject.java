package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.ReportData;
import com.esferalia.aon.gwt.payroll.shared.ReportData.Column;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;

public class ReportsObject {
	
	private int MAX = 200;

	public static enum ReportsType {
		A3("Informe Generaci\u00f3n de N\u00f3minas A3") {
			@Override
			public <T> T visit(ReportsTypeVisitor<T> visitor) {
				return visitor.visitA3(this);
			}
		},
		FTE("Informe FTE") {
			@Override
			public <T> T visit(ReportsTypeVisitor<T> visitor) {
				return visitor.visitFTE(this);
			}

		},
		HOLIDAY("Informe Control Festivos, Libres y Vacaciones") {
			@Override
			public <T> T visit(ReportsTypeVisitor<T> visitor) {
				return visitor.visitHoliday(this);
			}

		};

		private String description;

		private ReportsType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public abstract <T> T visit(ReportsTypeVisitor<T> visitor);

	}

	private static interface ReportsTypeVisitor<T> {
		T visitA3(ReportsType type);

		T visitFTE(ReportsType type);

		T visitHoliday(ReportsType type);

	}

	private Enterprise enterprise;
	private GPSReportsServiceAsync serviceAsync;

	private Date month = DateUtils.getFirstDayOfMonth();
	private ReportsType reportsType = ReportsType.A3;

	public ReportsObject(Enterprise enterprise,
			GPSReportsServiceAsync serviceAsync) {
		this.enterprise = enterprise;
		this.serviceAsync = serviceAsync;
	}

	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}

	public ReportsType getReportsType() {
		return reportsType;
	}

	public void setReportsType(ReportsType reportsType) {
		this.reportsType = reportsType;
	}

	public void getReport(final AsyncCallback<DataTable> callback) {

		VisualizationUtils.loadVisualizationApi(new Runnable() {

			@Override
			public void run() {
				getReport(reportsType, month, new AsyncCallback<ReportData>() {
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(ReportData reportData) {
						callback.onSuccess(getDataTable(reportData));
					}
				});
			}
		}, Table.PACKAGE);

	}
	

	// ------------------------------------------------------------------------
	// Private methods
	private void getReport(ReportsType type, final Date month,
			final AsyncCallback<ReportData> callback) {

		List<Workplace> workplaces = enterprise.getWorkplaces();
		final int[] workplaceIds = new int[workplaces.size()];
		for (int i = 0; i < workplaceIds.length; i++)
			workplaceIds[i] = workplaces.get(i).getId();

		type.visit(new ReportsTypeVisitor<Void>() {
			@Override
			public Void visitA3(ReportsType type) {
				serviceAsync.getA3Report(month, workplaceIds, callback);
				return null;
			}

			@Override
			public Void visitFTE(ReportsType type) {
				Date start = DateUtils.getFirstDayOfMonth(month);
				Date end = DateUtils.getLastDayOfMonth(month);
				serviceAsync.getFTEReport(start, end, workplaceIds, callback);
				return null;
			}

			@Override
			public Void visitHoliday(ReportsType type) {
				Date start = DateUtils.getFirstDayOfMonth(month);
				Date end = DateUtils.getLastDayOfMonth(month);
				serviceAsync.getHolidayReport(start, end, workplaceIds,
						callback);
				return null;
			}
		});

	}

	// ------------------------------------------------- Private Static Methods

	private DataTable getDataTable(ReportData reportData) {
		DataTable dataTable = DataTable.create();
		Column columns[] = reportData.getColumns();
		for (Column column : columns)
			dataTable.addColumn(getColumnType(column.getType()),
					column.getLabel(), column.getId());
		
		int rows = reportData.rows();
		dataTable.addRows(rows);
		for (int row = 0; row < Math.min(rows, MAX ); row++) {
			String values[] = reportData.getRow(row);
			for (int col = 0; col < values.length; col++) {
				Object value = columns[col].parse(values[col]);
				if (value == null)
					dataTable.setValueNull(row, col);
				else if (value instanceof Date)
					dataTable.setValue(row, col, (Date) value);
				else if (value instanceof Boolean)
					dataTable.setValue(row, col, (Boolean) value);
				else if (value instanceof Integer)
					dataTable.setValue(row, col, (Integer) value);
				else if (value instanceof Double)
					dataTable.setValue(row, col, (Double) value);
				else
					dataTable.setValue(row, col, String.valueOf(value));

			}
		}

		return dataTable;

	}

	private static ColumnType getColumnType(Class<?> clazz) {
		if (clazz == Date.class)
			return ColumnType.DATE;
		if (clazz == Boolean.class)
			return ColumnType.BOOLEAN;
		if (clazz == Double.class)
			return ColumnType.NUMBER;
		if (clazz == Integer.class)
			return ColumnType.NUMBER;

		return ColumnType.STRING;
	}

}
