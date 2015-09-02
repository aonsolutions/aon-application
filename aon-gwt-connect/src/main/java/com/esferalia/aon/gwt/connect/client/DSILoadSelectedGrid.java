package com.esferalia.aon.gwt.connect.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.connect.shared.JsEmpres;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class DSILoadSelectedGrid extends CustomDataGrid<DSILoadSelected>
		implements HasSelectionHandlers<DSILoadSelected> {

	interface Listener {
		void onSelectionChangeHandler(SelectionChangeEvent event);
	}

	public enum Columns {
	
		CHECK(""), 
		NIF("NIF"), 
		EMPRESA("EMPRESA"), 
		ULTIMA_FECHA_IMPORTADA("PRIMERA NOMINA"), 
		RANGO("RANGO FECHAS"), 
		NUEVO_TRASPASO("NUEVO TRASPASO"), 
		INFORME("INFORME");
	
		private final String mensaje;

		private Columns(String mensaje) {
			this.mensaje = mensaje;
		}

		public String getColumnName() {
			return mensaje;
		}
	}

	private static class DefaultDSILoadSelected implements DSILoadSelected {

		private JsEmpres empres;

		public DefaultDSILoadSelected(JsEmpres empres) {
			setEmpres(empres);
		}

		private void setEmpres(JsEmpres empres) {
			this.empres = empres;
		}

		@Override
		public String getName() {
			return empres.getRSocial();
		}

		@Override
		public String getNif() {
			return empres.getNif();
		}
		
		@Override
		public String getFirstSalary() {
			return empres.getFirstSalary();
		}
		
		@Override
		public String getLastSalary() {
			return empres.getLastSalary();
		}
	}

	public static class EnterpriseDSILoadSelected extends
			DefaultDSILoadSelected {

		public EnterpriseDSILoadSelected(JsEmpres empres) {
			super(empres);
		}
	}

	private class HeaderBuilder extends
			AbstractHeaderOrFooterBuilder<DSILoadSelected> {

		private final Integer ROW_COUNT = 1;

		private Header<Boolean> checkHeader = DSILoadSelectedGrid.this
				.newCheckHeader();
		private Header<String> nifHeader = new TextHeader(
				Columns.NIF.getColumnName());
		private Header<String> enterpriseNameHeader = new TextHeader(
				Columns.EMPRESA.getColumnName());
		private Header<String> lastDateHeader = new TextHeader(
				Columns.ULTIMA_FECHA_IMPORTADA.getColumnName());
		private Header<String> dateIntervalsHeader = new TextHeader(
				Columns.RANGO.getColumnName());
		private Header<String> transferHeader = new TextHeader(
				Columns.NUEVO_TRASPASO.getColumnName());
		private Header<String> informeHeader = new TextHeader(
				Columns.INFORME.getColumnName());

		public HeaderBuilder() {
			super(DSILoadSelectedGrid.this, false);
			setSortIconStartOfLine(false);
		}

		@Override
		protected boolean buildHeaderOrFooterImpl() {
			TableRowBuilder tr = startRow();
			tr.startTH().colSpan(Columns.values().length).rowSpan(ROW_COUNT);
			tr.endTH();

			// Get information about the sorted column.
			ColumnSortList sortList = DSILoadSelectedGrid.this
					.getColumnSortList();
			ColumnSortInfo sortedInfo = (sortList.size() == 0) ? null
					: sortList.get(0);
			Column<?, ?> sortedColumn = (sortedInfo == null) ? null
					: sortedInfo.getColumn();
			boolean isSortAscending = (sortedInfo == null) ? false : sortedInfo
					.isAscending();

			tr = startRow().className(AON.AON_CSS.childCell());
			buildHeader(tr, checkHeader, checkBox, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, nifHeader, nif, sortedColumn, isSortAscending,
					false, false);
			buildHeader(tr, enterpriseNameHeader, enterpriseName, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, lastDateHeader, lastDate, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, dateIntervalsHeader, intervalsDate, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, transferHeader, newTransferFrom, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, informeHeader, informe, sortedColumn,
					isSortAscending, false, false);

			tr.endTR();

			return true;

		}

		private void buildHeader(TableRowBuilder out, Header<?> header,
				Column<DSILoadSelected, ?> column, Column<?, ?> sortedColumn,
				boolean isSortAscending, boolean isFirst, boolean isLast) {

			boolean isSorted = (sortedColumn == column);
			TableCellBuilder th = out.startTH();
			enableColumnHandlers(th, column);
			Context context = new Context(0, 1, header.getKey());
			renderSortableHeader(th, context, header, isSorted, isSortAscending);

			th.endTH();
		}

	}

	private class CellTableBuilder extends
			AbstractCellTableBuilder<DSILoadSelected> {

		private final String rowStyle;
		private final String selectedRowStyle;

		public CellTableBuilder() {
			super(DSILoadSelectedGrid.this);
			
			rowStyle = getResources().style().evenRow();
			selectedRowStyle = " " + getResources().style().selectedRow();
		}

		@Override
		protected void buildRowImpl(DSILoadSelected rowValue, int absRowIndex) {

			buildEnterpriseImpl(rowValue, absRowIndex);
		}

		public void buildEnterpriseImpl(DSILoadSelected rowValue, int rowIndex) {
			
			boolean isSelected = (selectionModel == null || rowValue == null) ? false
					: selectionModel.isSelected(rowValue);
			StringBuilder trClasses = new StringBuilder(rowStyle);
			if (isSelected) {
				trClasses.append(selectedRowStyle);
			}

			int col = 0;

			TableRowBuilder row = startRow().className(trClasses.toString());
			TableCellBuilder td;

			td = row.startTD()
					.align(HasHorizontalAlignment.ALIGN_LEFT
							.getTextAlignString())
					.className(AON.AON_CSS.childCell());
			renderCell(td, createContext(col++), checkBox, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			renderCell(td, createContext(col++), nif, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString());
			td.className(AON.AON_BOLD);
			renderCell(td, createContext(col++), enterpriseName, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			renderCell(td, createContext(col++), lastDate, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			renderCell(td, createContext(col++), intervalsDate, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			renderCell(td, createContext(col++), newTransferFrom, rowValue);
			td.endTD();
			
			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			renderCell(td, createContext(col++), informe, rowValue);
			td.endTD();

			row.endTR();
		}

	}

	// ******************************************************
	// TODO: EMPLOYEE
	// ******************************************************

	private MultiSelectionModel<DSILoadSelected> selectionModel;
	private List<Listener> listeners;

	/*
	 * ************* Columns
	 */
	private Column<DSILoadSelected, Boolean> checkBox;
	private Column<DSILoadSelected, String> nif;
	private Column<DSILoadSelected, String> enterpriseName;
	private Column<DSILoadSelected, String> lastDate;
	private Column<DSILoadSelected, String> intervalsDate;
	private Column<DSILoadSelected, String> newTransferFrom;
	private Column<DSILoadSelected, String> informe;

	public DSILoadSelectedGrid() {
		super();

		listeners = new ArrayList<DSILoadSelectedGrid.Listener>();
		checkBox = newCheckColumn();
		selectionModel = new MultiSelectionModel<DSILoadSelected>();

		setAutoHeaderRefreshDisabled(false);
		initializeSelectionModel();
		setSkipRowHoverCheck(true);
		initializeColumns();
		setHeaderBuilder(new HeaderBuilder());
		setTableBuilder(new CellTableBuilder());
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<DSILoadSelected> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	@SuppressWarnings("unchecked")
	private DSILoadSelected getLoadSelected() {
		return ((SingleSelectionModel<DSILoadSelected>) getSelectionModel())
				.getSelectedObject();
	}

	private void initializeSelectionModel() {

		setSelectionModel(selectionModel,
				DefaultSelectionEventManager
						.<DSILoadSelected> createCheckboxManager(0));
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				for (Listener listener : listeners)
					listener.onSelectionChangeHandler(event);
				SelectionEvent
						.fire(DSILoadSelectedGrid.this, getLoadSelected());
			}
		});
	}

	private void initializeColumns() {
		int col = 0;

		addColumn(checkBox);
		setColumnWidth(col++, 2, Unit.PCT);

		nif = new Column<DSILoadSelected, String>(new TextCell()) {

			@Override
			public String getValue(DSILoadSelected object) {
				return object.getNif();
			}
		};
		setColumnWidth(col++, 20, Unit.PX);

		enterpriseName = new Column<DSILoadSelected, String>(new TextCell()) {

			@Override
			public String getValue(DSILoadSelected object) {
				return object.getName();
			}
		};
		setColumnWidth(col++, 30, Unit.PX);
		
		lastDate = new Column<DSILoadSelected, String>(new TextCell()) {
			
			@Override
			public String getValue(DSILoadSelected object) {
								
				return object.getLastSalary();
			}
		};
		setColumnWidth(col++, 15, Unit.PX);
		
		intervalsDate = new Column<DSILoadSelected, String>(new TextCell()) {
			
			@Override
			public String getValue(DSILoadSelected object) {				
				return object.getFirstSalary() + " - " + object.getLastSalary();
			}
		};
		setColumnWidth(col++, 30, Unit.PX);

		newTransferFrom = new Column<DSILoadSelected, String>(new TextCell()) {

			@Override
			public String getValue(DSILoadSelected object) {
				return AON.DATE_FORMAT.format(new Date());
			}
		};
		setColumnWidth(col++, 15, Unit.PX);
		
		informe = new Column<DSILoadSelected, String>(new ClickableTextCell()) {

			@Override
			public String getValue(DSILoadSelected object) {
				return "Mostrar";
			}
		};
		setColumnWidth(col++, 15, Unit.PX);
	}

	private Header<Boolean> newCheckHeader() {

		Header<Boolean> header = new Header<Boolean>(new CheckboxCell(true,
				false)) {
			@Override
			public Boolean getValue() {
				return getVisibleItemCount() == ((MultiSelectionModel<?>) getSelectionModel())
						.getSelectedSet().size();
			}
		};
		header.setUpdater(new ValueUpdater<Boolean>() {
			@Override
			public void update(Boolean value) {
				for (DSILoadSelected item : getVisibleItems())
					selectionModel.setSelected(item, value);
				// getSelectionModel().setSelected(item, value);
			}
		});

		return header;
	}

	private Column<DSILoadSelected, Boolean> newCheckColumn() {

		Column<DSILoadSelected, Boolean> checkColumn = new Column<DSILoadSelected, Boolean>(
				new CheckboxCell()) {

			@Override
			public Boolean getValue(DSILoadSelected object) {
				return selectionModel.isSelected(object);
				// return getSelectionModel().isSelected(object);
			}
		};

		checkColumn
				.setFieldUpdater(new FieldUpdater<DSILoadSelected, Boolean>() {

					@Override
					public void update(int index, DSILoadSelected object,
							Boolean value) {
						redraw();
					}
				});

		return checkColumn;
	}

	public Set<DSILoadSelected> getSelectedObject() {
		return selectionModel.getSelectedSet();
	}

	public void clearSelected(DSILoadSelected object) {
		selectionModel.setSelected(object, false);
	}
}
