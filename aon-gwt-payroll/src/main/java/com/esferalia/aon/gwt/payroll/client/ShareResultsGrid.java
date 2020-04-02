package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class ShareResultsGrid extends CustomDataGrid<JsShareResult> implements
		HasSelectionHandlers<JsShareResult> {

	/**
	 * The key provider that provides the unique ID of a contact.
	 */
	public static final ProvidesKey<JsShareResult> KEY_PROVIDER = new ProvidesKey<JsShareResult>() {
		@Override
		public Object getKey(JsShareResult item) {
			return item == null ? null : item.getEmployeeId();
		}
	};

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

	public ShareResultsGrid() {
		super();

		setHeight("100%");

		setAutoHeaderRefreshDisabled(false);

		// Attach a column sort handler to the ListDataProvider to sort the
		// list.
		ListHandler<JsShareResult> sortHandler = null; // new
														// ListHandler<JsShareResult>(
														// resultsList);
		// addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		initializeSelectionModel();
		// Initialize the columns
		initializeColumns(sortHandler);

	}

	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<JsShareResult> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	private JsShareResult getSelectedShareResult() {
		return ((SingleSelectionModel<JsShareResult>) getSelectionModel())
				.getSelectedObject();
	}

	private void initializeSelectionModel() {
		final SelectionModel<JsShareResult> selectionModel = new SingleSelectionModel<JsShareResult>(
				KEY_PROVIDER);
		setSelectionModel(selectionModel,
				DefaultSelectionEventManager
						.<JsShareResult> createDefaultManager());
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				SelectionEvent.fire(ShareResultsGrid.this,
						getSelectedShareResult());
			}
		});

	}

	/**
	 * Defines the columns in the custom table. Maps the data in the ContactInfo
	 * for each row into the appropriate column in the table, and defines
	 * handlers for each column.
	 */
	private void initializeColumns(ListHandler<JsShareResult> sortHandler) {
		int col = 0;

		// Add a icon style column to show the workplace icon.
		addColumn(new IconStyleColumn<JsShareResult>() {
			@Override
			public String getIconStyle(Context context, JsShareResult result) {
				return isWorkplaceSep(result) ? AON.AON_ICON_WORKPLACE : null;
			}
		});
		setColumnWidth(col++, 20, Unit.PX);

		// Add a icon style column to show the salary icon.
		addColumn(new IconStyleColumn<JsShareResult>() {

			@Override
			public String getIconStyle(Context context, JsShareResult result) {
				return isWorkplaceSep(result) ? null : ShareResultsGrid
						.getIconStyle(result);
			}

		});
		setColumnWidth(col++, 20, Unit.PX);

		// Add a text column to show the employee name.
		addColumn(new TextColumn<JsShareResult>() {

			@Override
			public String getCellStyleNames(Context context,
					JsShareResult object) {
				return AON.AON_BOLD + " " + AON.AON_BLACK;
			}

			@Override
			public String getValue(JsShareResult result) {
				return result.getEmployeeName();

			}
		});

		// Add a text column to show description or error.
		addColumn(new TextColumn<JsShareResult>() {

			@Override
			public String getCellStyleNames(Context context,
					JsShareResult object) {
				return AON.AON_BOLD + " " + AON.AON_BLACK;
			}

			@Override
			public String getValue(JsShareResult result) {
				String description = (result.getError() != null ? result.getError() : "" ) + result.getDescription();
				return description;
			}
		});

		// Add a text column to show size.
		addColumn(new TextColumn<JsShareResult>() {

			@Override
			public String getCellStyleNames(Context context,
					JsShareResult object) {
				return AON.AON_BOLD + " " + AON.AON_BLACK;
			}

			@Override
			public String getValue(JsShareResult result) {
				int bytes = result.getSize();
				if (bytes < 1024)
					return bytes + " B";

				double kb = bytes / 1024;
				if (kb < 1024)
					return NumberFormat.getDecimalFormat().format(kb) + " K";

				return NumberFormat.getDecimalFormat().format(kb / 1024) + " M";

			}
		});
	}

	// ------------------------------------------------------------------------

	private static String getIconStyle(JsShareResult result) {

		if (isWorkplaceSep(result))
			return "";
		if ( !StringUtils.isBlank(result.getError()))
			return AON.AON_ICON_WARN;
		return AON.AON_ICON_OK;

	}

	private static boolean isWorkplaceSep(JsShareResult result) {
		return result.getEmployeeId() == 0;
	}

}
