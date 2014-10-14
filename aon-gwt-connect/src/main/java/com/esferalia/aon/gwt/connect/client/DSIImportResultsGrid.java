package com.esferalia.aon.gwt.connect.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class DSIImportResultsGrid extends CustomDataGrid<DSIImportResult> implements
		HasSelectionHandlers<DSIImportResult>  {

	public static class DefaultDSIImportResult implements DSIImportResult{
		
		private String message;
		
		public DefaultDSIImportResult(String message) {
			this.message = message;
		}
		
		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public String getResultIconStyle() {			
			return "";
		}
		@Override
		public String getEnterpriseIconStyle() {			
			return "";
		}
		
		@Override
		public String getEmployeeIconStyle() {			
			return "";
		}
	}
	
	public static class EnterpriseInsertedResult extends DefaultDSIImportResult {
		
		public EnterpriseInsertedResult(String message) {
			super(message);
		}
		
		@Override
		public String getResultIconStyle() {			
			return AON.AON_ICON_ACCEPT;
		}
		
		@Override
		public String getEnterpriseIconStyle() {			
			return AON.AON_CSS.aonIconEnterprise();
		}
	}
	
	public static class EnterpriseIgnoredResults extends DefaultDSIImportResult {
		public EnterpriseIgnoredResults(String message) {
			super(message);
		}
		
		@Override
		public String getResultIconStyle() {
			return AON.AON_ICON_ERRORWARNING;
		}
		
		@Override
		public String getEnterpriseIconStyle() {			
			return AON.AON_CSS.aonIconEnterprise();
		}
	}
	
	public static class EnterpriseUpdatedResults extends DefaultDSIImportResult {
		public EnterpriseUpdatedResults(String message) {
			super(message);
		}
		@Override
		public String getResultIconStyle() {			
			return AON.AON_ICON_VIEW;
		}
		@Override
		public String getEnterpriseIconStyle() {
			return AON.AON_CSS.aonIconEnterprise();
		}
	}

	private static abstract class IconStyleColumn<T extends DSIImportResult> extends TextColumn<T> {

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
	
	public DSIImportResultsGrid() {
		super();

		setHeight("100%");

		setAutoHeaderRefreshDisabled(false);

		// Initialize the columns
		initializeColumns();

		// Add a selection model so we can select cells.
		initializeSelectionModel();
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<DSIImportResult> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}
	
	private DSIImportResult getSelectedImportResult() {
		return ((SingleSelectionModel<DSIImportResult>) getSelectionModel())
				.getSelectedObject();
	}

	private void initializeSelectionModel() {
		final SelectionModel<DSIImportResult> selectionModel = new SingleSelectionModel<DSIImportResult>();
		setSelectionModel(selectionModel,
				DefaultSelectionEventManager
						.<DSIImportResult> createDefaultManager());
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				SelectionEvent.fire(DSIImportResultsGrid.this,
						getSelectedImportResult());
			}
		});
	}

	/**
	 * Defines the columns in the custom table. Maps the data in the JsDSIImportResult
	 * for each row into the appropriate column in the table, and defines
	 * handlers for each column.
	 */
	private void initializeColumns() {
		int col = 0;
		
		addColumn(new IconStyleColumn<DSIImportResult>() {

			@Override
			public String getIconStyle(Context context, DSIImportResult object) {				
				return object.getResultIconStyle();
			}			
		});
		setColumnWidth(col++, 18, Unit.PX);
		
		addColumn(new TextColumn<DSIImportResult>() {

			@Override
			public String getValue(DSIImportResult object) {				
				return object.getMessage();
			}
			
			@Override
			public String getCellStyleNames(Context context,
					DSIImportResult object) {				
				return AON.AON_BOLD + " " + AON.AON_BLACK;
			}
		});
		setColumnWidth(col++, 100, Unit.PX);
	}
	
	// ------------------------------------------------------------------------
}
