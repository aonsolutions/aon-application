package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.common.client.AON.round;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DefaultCellTableBuilder;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class SalaryResultsGrid extends CustomDataGrid<JsSalaryResult> implements
		HasSelectionHandlers<JsSalaryResult>  {

	/**
	 * The key provider that provides the unique ID of a contact.
	 */
	public static final ProvidesKey<JsSalaryResult> KEY_PROVIDER = new ProvidesKey<JsSalaryResult>() {
		@Override
		public Object getKey(JsSalaryResult item) {
			return item == null ? null : item.getEmployeeId();
		}
	};
	
	private static class WorkplaceResults {
		int id;
		String name;
		
	}

	private static class SalaryResultCellTableBuilder extends
			DefaultCellTableBuilder<JsSalaryResult> {

		public SalaryResultCellTableBuilder(
				AbstractCellTable<JsSalaryResult> cellTable) {
			super(cellTable);
		}
		
		@Override
		public void buildRowImpl(JsSalaryResult rowValue, int absRowIndex) {
			
			// TODO Auto-generated method stub
			int workplaceId = rowValue.getWorkplaceId();

			super.buildRowImpl(rowValue, absRowIndex);
		}
		
		
		public void buildRowImpl(WorkplaceResults results, int absRowIndex) {
			
		}
		
		
		
	}

	private abstract static class IconStyleColumn<T> extends TextColumn<T> {

		@Override
		public String getValue(T object) {
			return " ";
		}

		@Override
		public String getCellStyleNames(Context context, T object) {
			return getIconStyle(context, object);
		}

		public abstract String getIconStyle(Context context, T object);

	}

	public SalaryResultsGrid() {
		super();

		setHeight("100%");

		setAutoHeaderRefreshDisabled(false);

		// Attach a column sort handler to the ListDataProvider to sort the
		// list.
		ListHandler<JsSalaryResult> sortHandler = null; // new
														// ListHandler<JsSalaryResult>(
														// resultsList);
		// addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		initializeSelectionModel();
		// Initialize the columns
		initializeColumns(sortHandler);

	}

	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<JsSalaryResult> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}
	
	private JsSalaryResult getSelectedSalaryResult() {
		return ((SingleSelectionModel<JsSalaryResult>) getSelectionModel())
				.getSelectedObject();
	}

	private void initializeSelectionModel() {
		final SelectionModel<JsSalaryResult> selectionModel = new SingleSelectionModel<JsSalaryResult>(
				KEY_PROVIDER);
		setSelectionModel(selectionModel,
				DefaultSelectionEventManager
						.<JsSalaryResult> createDefaultManager());
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				SelectionEvent.fire(SalaryResultsGrid.this,
						getSelectedSalaryResult());
			}
		});

	}

	/**
	 * Defines the columns in the custom table. Maps the data in the ContactInfo
	 * for each row into the appropriate column in the table, and defines
	 * handlers for each column.
	 */
	private void initializeColumns(ListHandler<JsSalaryResult> sortHandler) {
		int col = 0;

		// Add a icon style column to show the workplace icon.
		addColumn(new IconStyleColumn<JsSalaryResult>() {
			@Override
			public String getIconStyle(Context context, JsSalaryResult result) {
				return isWorkplaceSep(result) ? AON.AON_ICON_WORKPLACE : null;
			}
		});
		setColumnWidth(col++, 20, Unit.PX);

		// Add a icon style column to show the salary icon.
		addColumn(new IconStyleColumn<JsSalaryResult>() {

			@Override
			public String getIconStyle(Context context, JsSalaryResult result) {
					return isWorkplaceSep(result) ? null : SalaryResultsGrid
						.getIconStyle(result);
			}

		});
		setColumnWidth(col++, 20, Unit.PX);

		// Add a text column to show the employee name.
		addColumn(new TextColumn<JsSalaryResult>() {

			@Override
			public String getCellStyleNames(Context context,
					JsSalaryResult object) {
				return AON.AON_BOLD + " " + AON.AON_BLACK;
			}

			@Override
			public String getValue(JsSalaryResult result) {
				return result.getEmployeeName();
				
			}
		});
		setColumnWidth(col++, 30, Unit.PCT);

		// Add a text column to show the total payment.
		TextHeader paymentHeader = new TextHeader("A. TOTAL DEVENGADO");
		paymentHeader.setHeaderStyleNames(AON.AON_TEXT_RIGHT);
		addColumn(new TextColumn<JsSalaryResult>() {
			@Override
			public String getCellStyleNames(Context context,
					JsSalaryResult result) {
				return AON.AON_TEXT_RIGHT
						+ " "
						+ getLabelStyle(result.getTotalPayment(),
								result.getCounterTotalPayment());
			}

			@Override
			public String getValue(JsSalaryResult result) {
				return SalaryResultsGrid.format(result.getCounterTotalPayment());
			}
		}, paymentHeader);
		addColumn(new TextColumn<JsSalaryResult>() {
			@Override
			public String getCellStyleNames(Context context,
					JsSalaryResult object) {
				return AON.AON_TEXT_RIGHT + " " + AON.AON_BOLD;
			}

			@Override
			public String getValue(JsSalaryResult result) {
					return SalaryResultsGrid.format(result.getTotalPayment());
			}
		}, paymentHeader);

		// Add a text column to show the total deductions.
		TextHeader deductionHeader = new TextHeader("A. TOTAL DEDUCCIR");
		deductionHeader.setHeaderStyleNames(AON.AON_TEXT_RIGHT);
		addColumn(new TextColumn<JsSalaryResult>() {
			@Override
			public String getCellStyleNames(Context context,
					JsSalaryResult result) {
				return AON.AON_TEXT_RIGHT
						+ " "
						+ getLabelStyle(result.getTotalDeduction(),
								result.getCounterTotalDeduction());
			}

			@Override
			public String getValue(JsSalaryResult result) {
				return SalaryResultsGrid.format(result
						.getCounterTotalDeduction());
			}

		}, deductionHeader);
		addColumn(new TextColumn<JsSalaryResult>() {
			@Override
			public String getCellStyleNames(Context context,
					JsSalaryResult object) {
				return AON.AON_TEXT_RIGHT + " " + AON.AON_BOLD;
			}

			@Override
			public String getValue(JsSalaryResult result) {
				return SalaryResultsGrid.format(result.getTotalDeduction());
			}

		}, deductionHeader);

		// Add a text column to show the total liquids.
		TextHeader liquidHeader = new TextHeader(
				"L\u00CDQUIDO A PERCIBIR (A-B)");
		liquidHeader.setHeaderStyleNames(AON.AON_TEXT_RIGHT);
		addColumn(new TextColumn<JsSalaryResult>() {

			public String getCellStyleNames(Context context,
					JsSalaryResult result) {
				return AON.AON_TEXT_RIGHT
						+ " "
						+ getLabelStyle(result.getTotalLiquid(),
								result.getCounterTotalLiquid());
			}

			@Override
			public String getValue(JsSalaryResult result) {
				return SalaryResultsGrid.format(result.getCounterTotalLiquid());
			}

		}, liquidHeader);
		addColumn(new TextColumn<JsSalaryResult>() {
			@Override
			public String getCellStyleNames(Context context,
					JsSalaryResult object) {
				return AON.AON_INPUT_REQUIRED + " " + AON.AON_TEXT_RIGHT;
			}

			@Override
			public String getValue(JsSalaryResult result) {
				return SalaryResultsGrid.format(result.getTotalLiquid());
			}

		}, liquidHeader);

		TextHeader daysHeader = new TextHeader("D\u00CDAS");
		daysHeader.setHeaderStyleNames(AON.AON_TEXT_RIGHT);
		// Add a text column to show the days.
		addColumn(new TextColumn<JsSalaryResult>() {
			public String getCellStyleNames(Context context,
					JsSalaryResult object) {
				return AON.AON_TEXT_RIGHT + " " + AON.AON_BOLD;
			}

			@Override
			public String getValue(JsSalaryResult result) {
				return Integer.toString(DateUtils.getDaysBetween(
						result.getStartDate(), result.getEndDate()) + 1);
			}

		}, daysHeader);
	}
	
	// ------------------------------------------------------------------------

	private static String getLabelStyle(Double d1, Double d2) {
		return (equals(d1, d2) ? AON.AON_LABEL_WARN : AON.AON_LABEL_ERROR);
	}

	private static String getIconStyle(JsSalaryResult result) {

		if (isWorkplaceSep(result))
			return "";

		if (!hasCounterPart(result))
			return AON.AON_ICON_OK;

		String str1 = format(result.getTotalLiquid());
		String str2 = format(result.getCounterTotalLiquid());
		if (!StringUtils.equals(str1, str2))
			return AON.AON_ICON_ERRORWARNING;

		str1 = format(result.getTotalDeduction());
		str2 = format(result.getCounterTotalDeduction());
		if (!StringUtils.equals(str1, str2))
			return AON.AON_ICON_ERRORWARNING;

		str1 = format(result.getTotalPayment());
		str2 = format(result.getCounterTotalPayment());
		if (!StringUtils.equals(str1, str2))
			return AON.AON_ICON_ERRORWARNING;

		return AON.AON_ICON_WARN;
	}
	


	private static boolean hasCounterPart(JsSalaryResult result) {
		return result.hasCounter();
	}

	private static boolean isWorkplaceSep(JsSalaryResult result) {
		return result.getEmployeeId() == 0;
	}

	private static String format(Double d) {
		return d == null || d.isNaN() ? "" : AON.CURRENCY_FORMAT.format(round(d));
	}

	private static boolean equals(Double d1, Double d2) {
		return StringUtils.equals(format(d1),
				format(d2));
	}
}
