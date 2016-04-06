package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class DuplicatedDataGrid extends CustomDataGrid<IssueSelected>
		implements HasSelectionHandlers<IssueSelected> {
	
	interface Listener {
		
		void onEnabledAcceptButton(boolean enable);
		
		void onIssueSelection(IssueSelected issue);
	}
	
	enum Columns {
		CHECK(""),
		TYPE("TIPO"),
		PRIORITY("PRIORIDAD"),
		SUBJECT("ASUNTO"),
		CREATED_AT("FECHA");
		
		private final String mensaje;
		
		private Columns(String mensaje) {
			this.mensaje = mensaje;
		}
		
		public String getName() {
			return this.mensaje;
		}
	}
	
	public static class DefaultIssueSelected {
		
		DefaultAonIssueSelected issue;
		
		public DefaultIssueSelected(DefaultAonIssueSelected issue) {
			this.issue = issue;
		}
		
		public int getId() {
			return issue.getId();
		}
		
		public String getType() {
			return issue.getType();
		}
		
		public String getPriority() {
			return issue.getPriority();
		}
		
		public String getSubject() {
			return issue.getTitle();
		}
		
		public Date getCreatedAt() {
			return issue.getCreateAt();
		}
	}
	
	private class HeaderBuilder extends AbstractHeaderOrFooterBuilder<IssueSelected> {
		
		private final Integer ROW_COUNT = 1;
		private Header<String> checkHeader = new TextHeader(Columns.CHECK.getName());
		private Header<String> typeHeader = new TextHeader(Columns.TYPE.getName());
		private Header<String> priorityHeader = new TextHeader(Columns.PRIORITY.getName());
		private Header<String> subjectHeader = new TextHeader(Columns.SUBJECT.getName());
		private Header<String> dateHeader = new TextHeader(Columns.CREATED_AT.getName());
		
		public HeaderBuilder() {
			super(DuplicatedDataGrid.this, false);
			setSortIconStartOfLine(false);
		}
		
		@Override
		protected boolean buildHeaderOrFooterImpl() {			
			TableRowBuilder tr = startRow();
			tr.startTH().colSpan(Columns.values().length).rowSpan(ROW_COUNT);
			tr.endTH();

			// Get information about the sorted column.
			ColumnSortList sortList = DuplicatedDataGrid.this.getColumnSortList();
			ColumnSortInfo sortedInfo = (sortList.size() == 0) ? null
					: sortList.get(0);
			Column<?, ?> sortedColumn = (sortedInfo == null) ? null
					: sortedInfo.getColumn();
			boolean isSortAscending = (sortedInfo == null) ? false
					: sortedInfo.isAscending();

			tr = startRow().className(AON.AON_CSS.childCell());
			buildHeader(tr, checkHeader, checkBoxColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, typeHeader, typeColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, priorityHeader, priorityColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, subjectHeader, subjectColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, dateHeader, dateColumn, sortedColumn,
					isSortAscending, false, false);

			tr.endTR();

			return true;
		}

		private void buildHeader(TableRowBuilder out, Header<?> header,
				Column<IssueSelected, ?> column, Column<?, ?> sortedColumn,
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
			AbstractCellTableBuilder<IssueSelected> {

		private final String rowStyle;
		private final String selectedRowStyle;

		public CellTableBuilder() {
			super(DuplicatedDataGrid.this);
			
			rowStyle = getResources().style().evenRow();
			selectedRowStyle = " " + getResources().style().selectedRow();
		}

		@Override
		protected void buildRowImpl(IssueSelected rowValue, int absRowIndex) {

			buildEnterpriseImpl(rowValue, absRowIndex);
		}

		public void buildEnterpriseImpl(IssueSelected rowValue, int rowIndex) {
			
			boolean isSelected = (selectionModel == null || rowValue == null) ? false
					: selectionModel.isSelected(rowValue);
			StringBuilder trClasses = new StringBuilder(rowStyle);
			if (isSelected) {
				trClasses.append(selectedRowStyle);
			}

			int col = 0;

			TableRowBuilder row = startRow().className(trClasses.toString());
			TableCellBuilder td;
			
			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());			
			td.className(AON.AON_CSS.aonDataTableIconColumn());			
			renderCell(td, createContext(col++), checkBoxColumn, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.style().cursor(Cursor.POINTER);
			td.className(AON.AON_CSS.aonDataTableTextColumn() 
					+ " " + AON.AON_RESOURCES.css().tagStyle()
					+ " " + AON.AON_RESOURCES.css().tagType());
			renderCell(td, createContext(col++), typeColumn, rowValue);
			td.endTD();
			
			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.className(AON.AON_CSS.aonDataTableTextColumn() 
					+ " " + AON.AON_RESOURCES.css().tagStyle()
					+ " " + AON.AON_RESOURCES.css().tagPriority());
			renderCell(td, createContext(col++), priorityColumn, rowValue);
			td.endTD();
			
			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.className(AON.AON_CSS.aonDataTableTextColumn());
			renderCell(td, createContext(col++), subjectColumn, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.className(AON.AON_BOLD);
			renderCell(td, createContext(col++), dateColumn, rowValue);
			td.endTD();

			row.endTR();
		}
	}
	
	private SingleSelectionModel<IssueSelected> selectionModel;
	private List<Listener> listeners;
	
	/*
	 *  ************* Columns
	 */
	
	private Column<IssueSelected, Boolean> checkBoxColumn;
	private Column<IssueSelected, String> typeColumn;
	private Column<IssueSelected, String> priorityColumn;
	private Column<IssueSelected, String> subjectColumn;
	private Column<IssueSelected, String> dateColumn;
	
	public DuplicatedDataGrid() {
		super();
		
		listeners = new LinkedList<Listener>();
		checkBoxColumn = newCheckColumn();
		selectionModel = new SingleSelectionModel<IssueSelected>();
		
		setStyleName(AON.AON_CSS.aonGwtOfficeDataTable());
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
 
	@SuppressWarnings("unchecked")
	private IssueSelected getLoadSelected() {
		return ((SingleSelectionModel<IssueSelected>) getSelectionModel())
				.getSelectedObject();
	}
	
	private void initializeSelectionModel() {

		setSelectionModel(selectionModel,
				DefaultSelectionEventManager
						.<IssueSelected> createCheckboxManager(0));
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {				
				
				for (Listener listener: listeners)
					listener.onEnabledAcceptButton(selectionModel.isSelected(getLoadSelected()));
				
				for (Listener listener : listeners)
					listener.onIssueSelection(getLoadSelected());
				
				SelectionEvent 
						.fire(DuplicatedDataGrid.this, getLoadSelected());
			}
		});
	}
	
	private void initializeColumns() {
		int col = 0;
		addColumn(checkBoxColumn);
		setColumnWidth(col++, 5, Unit.PCT);
		
		typeColumn = new Column<IssueSelected, String>(new TextCell()) {
			@Override
			public String getValue(IssueSelected object) {
				return object.getType();
			}
		};
		setColumnWidth(col++, 20, Unit.PX);
		
		priorityColumn = new Column<IssueSelected, String>(new TextCell()) {

			@Override
			public String getValue(IssueSelected object) {
				return object.getPriority();
			}
		};
		setColumnWidth(col++, 20, Unit.PX);
		
		subjectColumn = new Column<IssueSelected, String>(new TextCell()) {
			
			@Override
			public String getValue(IssueSelected object) {
				return object.getTitle();
			}
		};
		setColumnWidth(col++, 65, Unit.PX);
		
		dateColumn = new Column<IssueSelected, String>(new TextCell()) {

			@Override
			public String getValue(IssueSelected object) {
				int days = CalendarUtil.getDaysBetween(object.getCreateAt(),
						new Date());

				switch (days) {
				case 0:
					return "Hoy (" + DateTimeFormat.getFormat("HH:mm")
							.format(object.getCreateAt()) + ")";
				case 1:
					return "Ayer (" + DateTimeFormat.getFormat("HH:mm")
							.format(object.getCreateAt()) + ")";
				default:
					return DateTimeFormat.getFormat("E dd MMMM")
							.format(object.getCreateAt());
				}
			}
		};
		setColumnWidth(col++, 25, Unit.PX);
	}
	
	private Column<IssueSelected, Boolean> newCheckColumn() {
		Column<IssueSelected, Boolean> checkColumn = new Column<IssueSelected, Boolean>(
				new CheckboxCell()) {
			@Override
			public Boolean getValue(IssueSelected object) {				
				return selectionModel.isSelected(object);				
			}
		};
		checkColumn.setFieldUpdater(new FieldUpdater<IssueSelected, Boolean>() {
			public void update(int index, IssueSelected object, Boolean value) {
				redraw();
			}
		});
		return checkColumn;
	}

	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<IssueSelected> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

}
