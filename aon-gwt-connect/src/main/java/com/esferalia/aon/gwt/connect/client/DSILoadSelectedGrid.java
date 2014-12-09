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
		ULTIMA_FECHA_IMPORTADA("ULTIMA FECHA IMPORTADA"),
		RANGO("RANGO FECHAS"),
		NUEVO_TRASPASO("FECHA NUEVO TRASPASO");
		
		private final String mensaje;
		
		private Columns(String mensaje) {
			this.mensaje = mensaje;
		}
		
		public String getColumnName() {
			return mensaje;
		}
	}

	public static class DefaultDSILoadSelected implements DSILoadSelected {

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
	}

	public static class EnterpriseDSILoadSelected extends
			DefaultDSILoadSelected {

		public EnterpriseDSILoadSelected(JsEmpres empres) {
			super(empres);
		}
	}

	private class HeaderBuilder extends
			AbstractHeaderOrFooterBuilder<DSILoadSelected> {
		
		private Header<Boolean> checkHeader = DSILoadSelectedGrid.this.newCheckHeader();
		private Header<String> nifHeader = new TextHeader(Columns.NIF.getColumnName());
		private Header<String> enterpriseNameHeader = new TextHeader(Columns.EMPRESA.getColumnName());
		private Header<String> lastDateHeader = new TextHeader(Columns.ULTIMA_FECHA_IMPORTADA.getColumnName());
		private Header<String> dateIntervalsHeader = new TextHeader(
				Columns.RANGO.getColumnName());
		private Header<String> transferHeader = new TextHeader(
				Columns.NUEVO_TRASPASO.getColumnName());

		public HeaderBuilder() {
			super(DSILoadSelectedGrid.this, false);
			setSortIconStartOfLine(false);
		}

		@Override
		protected boolean buildHeaderOrFooterImpl() {
			TableRowBuilder tr = startRow();
			tr.startTH().colSpan(Columns.values().length).rowSpan(1);
			tr.endTH();
			
			// Get information about the sorted column.
			ColumnSortList sortList = DSILoadSelectedGrid.this.getColumnSortList();
			ColumnSortInfo sortedInfo = (sortList.size() == 0) ? null
					: sortList.get(0);
			Column<?, ?> sortedColumn = (sortedInfo == null) ? null
					: sortedInfo.getColumn();
			boolean isSortAscending = (sortedInfo == null) ? false : sortedInfo
					.isAscending();

			tr = startRow().className(AON.AON_CSS.aonLinkListItem());
			buildHeader(tr, checkHeader, checkBox, 
					sortedColumn, isSortAscending, false, false);
			buildHeader(tr, nifHeader, nif, 
					sortedColumn, isSortAscending, false, false);
			buildHeader(tr, enterpriseNameHeader, enterpriseName, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, lastDateHeader, lastDate, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, dateIntervalsHeader, intervalsDate, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, transferHeader, newTransferFrom, sortedColumn,
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
	
	private class TableBuilder extends AbstractCellTableBuilder<DSILoadSelected> {
		
		public TableBuilder() {
			super(DSILoadSelectedGrid.this);
		}

		@Override
		protected void buildRowImpl(DSILoadSelected rowValue, int absRowIndex) {
			buildEnterpriseImpl(rowValue, absRowIndex);			
		}
		
		public void buildEnterpriseImpl(DSILoadSelected rowValue, int rowIndex) {
			
			int col = 0;
			
			TableRowBuilder row = startRow();
			TableCellBuilder td;
			
			td = row.startTD().align(HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString())
					.className(AON.AON_CSS.childCell());
			renderCell(td, createContext(col++), checkBox, rowValue);
			td.endTD();

			td = row.startTD().align(HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			renderCell(td, createContext(col++), nif, rowValue);
			td.endTD();
			
			td = row.startTD().align(HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString());
			td.className(AON.AON_BOLD);			
			renderCell(td, createContext(col++), enterpriseName, rowValue);
			td.endTD();
			
			td = row.startTD().align(HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			renderCell(td, createContext(col++), lastDate, rowValue);
			td.endTD();
			
			td = row.startTD().align(HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			renderCell(td, createContext(col++), intervalsDate, rowValue);
			td.endTD();

			td = row.startTD().align(HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			renderCell(td, createContext(col++), newTransferFrom, rowValue);
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
		setTableBuilder(new TableBuilder());
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

		setSelectionModel(selectionModel, DefaultSelectionEventManager
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
		setColumnWidth(col++, 30, Unit.PX);
	
		nif = new Column<DSILoadSelected, String>(new TextCell()) {
			
			@Override
			public String getValue(DSILoadSelected object) {
				return object.getNif();
			}
		};
		setColumnWidth(col++, 20, Unit.PCT);
		
		enterpriseName = new Column<DSILoadSelected, String>(
				new TextCell()) {

			@Override
			public String getValue(DSILoadSelected object) {
				// TODO Auto-generated method stub
				return object.getName();
			}
		};
		setColumnWidth(col++, 40, Unit.PCT);
		
		lastDate = new Column<DSILoadSelected, String>(new TextCell()) {

			@Override
			public String getValue(DSILoadSelected object) {
				
				return AON.DATE_FORMAT.format(new Date());
			}
		};
		
		setColumnWidth(col++, 20, Unit.PCT);
		
		intervalsDate = new Column<DSILoadSelected, String>(new TextCell()) {

			@Override
			public String getValue(DSILoadSelected object) {
				
				return AON.DATE_FORMAT.format(new Date()) + 
						" - " + AON.DATE_FORMAT.format(new Date());			
			}
		};
		setColumnWidth(col++, 40, Unit.PCT);
				
		newTransferFrom = new Column<DSILoadSelected, String>(new TextCell()) {
			
			@Override
			public String getValue(DSILoadSelected object) {
				return AON.DATE_FORMAT.format(new Date());
			}
		};
		setColumnWidth(col++, 20, Unit.PCT);
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
				for(DSILoadSelected item : getVisibleItems())
					selectionModel.setSelected(item, value);
					//getSelectionModel().setSelected(item, value);			
			}
		});
		
		return header;
	}

	private Column<DSILoadSelected, Boolean> newCheckColumn() {
		
		Column<DSILoadSelected, Boolean> checkColumn = new Column<DSILoadSelected, Boolean>(new CheckboxCell()) {
			
			@Override
			public Boolean getValue(DSILoadSelected object) {
				return selectionModel.isSelected(object);
				//	return getSelectionModel().isSelected(object);
			}
		};

		return checkColumn;		
	}

	public Set<DSILoadSelected> getSelectedObject() {
		return selectionModel.getSelectedSet();
	}

	public void clearSelected(DSILoadSelected object) {
		selectionModel.setSelected(object, false);
	}
}
