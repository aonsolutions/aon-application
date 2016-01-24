package com.esferalia.aon.gwt.office.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.JsArray;
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
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class IssueGrid extends CustomDataGrid<IssueSelected>
		implements HasSelectionHandlers<IssueSelected> {

	interface Listener {
		void onSelectionChangeHandler(SelectionChangeEvent event);

		void onSelectionTitle(IssueSelected issue);
	}

	public enum Columns {

		STATE(""), OWNER("CREADO POR"), COMPANY("EMPRESA"), TITLE("ASUNTO"), LABELS("ETIQUETAS"), CREATED_AT(
				"FECHA");

		private String mensaje;

		private Columns(String mensaje) {
			this.mensaje = mensaje;
		}

		public String getColumnName() {
			return mensaje;
		}
	}

	private static abstract class DefaultAonIssuesSelected
			implements IssueSelected, Comparable<IssueSelected> {

		protected JsIssue issue;
		private JsArray<JsIssueComment> comments;
		private DateTimeFormat timeFormat;

		public DefaultAonIssuesSelected(JsIssue issue) {
			this.timeFormat = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.S");
			setIssue(issue);
		}

		private void setIssue(JsIssue issue) {
			this.issue = issue;
		}

		@Override
		public String getTitle() {
			return issue.getTitle();
		}

		@Override
		public Integer getNumber() {
			return issue.getNumber();
		}

		@Override
		public String getStateIconStyle() {
			return "";
		}

		@Override
		public String getState() {
			return "";
		}

		@Override
		public String getPriority() {
			return issue.getPriority();
		}

		@Override
		public String getType() {
			return issue.getType();
		}

		@Override
		public Date getCreateAt() {
			return timeFormat.parse(issue.getCreatedAt());
		}

		@Override
		public Date getUpdatedAt() {
			return timeFormat.parse(issue.getCreatedAt());
		}

		@Override
		public String getBody() {
			return issue.getBody();
		}

		@Override
		public Integer getComments() {
			return issue.getComments();
		}

		@Override
		public String getAssignee() {
			return null;
		}

		@Override
		public Integer getId() {
			return issue.getId();
		}

		@Override
		public String getCompany() {
			return issue.getCompany();
		}

		@Override
		public JsArray<JsLabel> getLabels() {
			return issue.getLabels();
		}

		@Override
		public JsUser getUser() {
			return issue.getUser();
		}

		@Override
		public void setIssueComments(JsArray<JsIssueComment> comments) {
			this.comments = comments;
		}

		@Override
		public JsArray<JsIssueComment> getIssueComments() {
			return comments;
		}
	}

	public static class IssueOpenLoadSelected extends DefaultAonIssuesSelected {

		public IssueOpenLoadSelected(JsIssue issue) {
			super(issue);
		}

		@Override
		public JsIssue getJsIssue() {
			return issue;
		}

		@Override
		public String getStateIconStyle() {
			return AON.AON_CSS.aonIconIssueOpen();
		}

		@Override
		public String getState() {
			return issue.getState();
		}

		@Override
		public int compareTo(IssueSelected o) {
			return Comparators.NUMBER.compare(this, o);
		}
	}

	public static class IssueClosedLoadSelected
			extends DefaultAonIssuesSelected {

		public IssueClosedLoadSelected(JsIssue issue) {
			super(issue);
		}

		@Override
		public JsIssue getJsIssue() {
			return issue;
		}

		@Override
		public String getStateIconStyle() {
			return AON.AON_CSS.aonIconIssueClosed();
		}

		@Override
		public String getState() {
			return issue.getState();
		}

		@Override
		public int compareTo(IssueSelected o) {
			return Comparators.NUMBER.compare(this, o);
		}
	}

	private static abstract class IconStyleColumn<T extends IssueSelected>
			extends TextColumn<T> {

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

	public static class Comparators {

		public static Comparator<IssueSelected> NUMBER=new Comparator<IssueSelected>(){@Override public int compare(IssueSelected o1,IssueSelected o2){
		// TODO Auto-generated method stub
		return o2.getNumber()-o1.getNumber();}};
	}

	private class HeaderBuilder
			extends AbstractHeaderOrFooterBuilder<IssueSelected> {

		private final Integer ROW_COUNT = 1;
		private Header<String> stateHeader = new TextHeader(
				Columns.STATE.getColumnName());
		private Header<String> ownerHeader = new TextHeader(
				Columns.OWNER.getColumnName());
		private Header<String> companyHeader = new TextHeader(
						Columns.COMPANY.getColumnName());
		private Header<String> titleHeader = new TextHeader(
				Columns.TITLE.getColumnName());
		private Header<String> labelsHeader = new TextHeader(
				Columns.LABELS.getColumnName());
		private Header<String> createdAtHeader = new TextHeader(
				Columns.CREATED_AT.getColumnName());

		public HeaderBuilder() {
			super(IssueGrid.this, false);
			setSortIconStartOfLine(false);
		}

		@Override
		protected boolean buildHeaderOrFooterImpl() {
			TableRowBuilder tr = startRow();
			tr.startTH().colSpan(Columns.values().length).rowSpan(ROW_COUNT);
			tr.endTH();

			// Get information about the sorted column.
			ColumnSortList sortList = IssueGrid.this.getColumnSortList();
			ColumnSortInfo sortedInfo = (sortList.size() == 0) ? null
					: sortList.get(0);
			Column<?, ?> sortedColumn = (sortedInfo == null) ? null
					: sortedInfo.getColumn();
			boolean isSortAscending = (sortedInfo == null) ? false
					: sortedInfo.isAscending();

			tr = startRow().className(AON.AON_CSS.childCell());
			buildHeader(tr, stateHeader, stateColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, ownerHeader, ownerColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, companyHeader, companyColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, titleHeader, title, sortedColumn, isSortAscending,
					false, false);
			buildHeader(tr, labelsHeader, labels, sortedColumn, isSortAscending,
					false, false);
			buildHeader(tr, createdAtHeader, createdAt, sortedColumn,
					isSortAscending, false, false);

			tr.endTR();

			return true;
		}

		private void buildHeader(TableRowBuilder out, Header<?> header,
				Column<IssueSelected, ?> column, Column<?, ?> sortedColumn,
				boolean isSortAscending, boolean isFirst, boolean isLast) {

			boolean isSorted = (sortedColumn == column);
			TableCellBuilder th = out.startTH();
			th.className("aon-dataTable-header");
			enableColumnHandlers(th, column);
			Context context = new Context(0, 1, header.getKey());
			renderSortableHeader(th, context, header, isSorted,
					isSortAscending);

			th.endTH();

		}
	}

	private class CellTableBuilder
			extends AbstractCellTableBuilder<IssueSelected> {

		private final String rowStyle;
		private final String selectedRowStyle;

		public CellTableBuilder() {
			super(IssueGrid.this);

			rowStyle = getResources().style().evenRow();
			selectedRowStyle = " " + getResources().style().selectedRow();
		}

		@Override
		protected void buildRowImpl(IssueSelected rowValue, int absRowIndex) {
			buildIssuesImpl(rowValue, absRowIndex);
		}

		public void buildIssuesImpl(IssueSelected rowValue, int rowIndex) {
			boolean isSelected = (selectionModel == null || rowValue == null)
					? false : selectionModel.isSelected(rowValue);
			StringBuilder trClasses;

			if ((rowIndex % 2) == 0)
				trClasses = new StringBuilder(AON.AON_DATA_TABLE_ROW_EVEN);
			else
				trClasses = new StringBuilder(AON.AON_DATA_TABLE_ROW_ODD);

			if (isSelected) {
				trClasses.append(selectedRowStyle);
			}

			int col = 0;

			TableRowBuilder row = startRow().className(trClasses.toString());
			TableCellBuilder td;

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_RIGHT.getTextAlignString());
			td.className(rowValue.getStateIconStyle());
			td.title(rowValue.getState());
			renderCell(td, createContext(col++), stateColumn, rowValue);
			td.endTD();
			
			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			//td.className(rowValue.getStateIconStyle());
			//td.title("");
			td.className(AON.AON_CSS.aonDataTableTextColumn());
			renderCell(td, createContext(col++), ownerColumn, rowValue);
			td.endTD();
			
			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			//td.className(rowValue.getStateIconStyle());
			//td.title("");
			td.className(AON.AON_CSS.aonDataTableTextColumn());
			renderCell(td, createContext(col++), companyColumn, rowValue);
			td.endTD();


			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.style().cursor(Cursor.POINTER);
			td.className(AON.AON_CSS.aonDataTableTextColumn());
			renderCell(td, createContext(col++), title, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString());
			renderCell(td, createContext(col++), labels, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.className(AON.AON_BOLD);
			renderCell(td, createContext(col++), createdAt, rowValue);
			td.endTD();

			row.endTR();
		}
	}

	// **************************************************
	// **************************************************
	// **************************************************
	// **************************************************

	private MultiSelectionModel<IssueSelected> selectionModel;
	private List<Listener> listeners;

	/*
	 * ****************** Columns
	 */

	private Column<IssueSelected, String> stateColumn;
	private Column<IssueSelected, String> ownerColumn;
	private Column<IssueSelected, String> companyColumn;
	private Column<IssueSelected, String> title;
	private Column<IssueSelected, String> labels;
	private Column<IssueSelected, String> createdAt;

	public IssueGrid() {
		super();

		listeners = new ArrayList<IssueGrid.Listener>();

		selectionModel = new MultiSelectionModel<IssueSelected>();

		setStyleName(AON.AON_CSS.aonDataTable());

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
			SelectionHandler<IssueSelected> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	@SuppressWarnings("unchecked")
	private IssueSelected getIssueSelected() {
		return ((SingleSelectionModel<IssueSelected>) getSelectionModel())
				.getSelectedObject();
	}

	private void initializeSelectionModel() {

		setSelectionModel(selectionModel, DefaultSelectionEventManager
				.<IssueSelected> createCheckboxManager(0));
		selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				for (Listener listener : listeners)
					listener.onSelectionChangeHandler(event);
				SelectionEvent.fire(IssueGrid.this, getIssueSelected());
			}
		});
	}

	private void initializeColumns() {
		int col = 0;

		stateColumn = new IconStyleColumn<IssueSelected>() {

			@Override
			public String getIconStyle(Context context, IssueSelected object) {
				return object.getStateIconStyle();
			}

			@Override
			public String getCellStyleNames(Context context,
					IssueSelected object) {
				return object.getStateIconStyle();
			}
		};

		setColumnWidth(col++, 2, Unit.PCT);

		title = new Column<IssueSelected, String>(new ClickableTextCell()) {

			@Override
			public String getValue(IssueSelected object) {
				return object.getTitle();
			}
		};

		title.setFieldUpdater(new FieldUpdater<IssueSelected, String>() {

			@Override
			public void update(int index, IssueSelected object, String value) {
				for (Listener listener : listeners)
					listener.onSelectionTitle(object);
			}

		});
		setColumnWidth(col++, 40, Unit.PX);
		
		ownerColumn = new Column<IssueSelected, String>(new TextCell()) {

			@Override
			public String getValue(IssueSelected object) {				
				return object.getUser().getName();
			}
		};
		setColumnWidth(col++, 60, Unit.PX);
		
		companyColumn = new Column<IssueSelected, String>(new TextCell()) {

			@Override
			public String getValue(IssueSelected object) {
				return object.getCompany();
			}
		};
		setColumnWidth(col++, 60, Unit.PX);

		labels = new Column<IssueSelected, String>(new TextCell()) {

			@Override
			public String getValue(IssueSelected object) {

				String labels = "";
				if (object.getLabels() == null)
					return labels;

				for (int z = 0; z < object.getLabels().length(); z++)
					labels += object.getLabels().get(z).getName().toUpperCase()
							+ " - ";

				return labels;

			}
		};
		setColumnWidth(col++, 60, Unit.PX);

		createdAt = new Column<IssueSelected, String>(new TextCell()) {

			@Override
			public String getValue(IssueSelected object) {
				return AON.DATE_FORMAT.format(object.getCreateAt());
			}
		};
		setColumnWidth(col++, 40, Unit.PX);
	}

	public Set<IssueSelected> getSelectedObject() {
		return selectionModel.getSelectedSet();
	}

	public void clearSelected(IssueSelected object) {
		selectionModel.setSelected(object, false);
	}

}
