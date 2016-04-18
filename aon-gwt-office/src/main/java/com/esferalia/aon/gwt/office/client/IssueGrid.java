package com.esferalia.aon.gwt.office.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ClickableTextCell;
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
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.MultiSelectionModel;

public class IssueGrid extends CustomDataGrid<IssueSelected>
		implements HasSelectionHandlers<IssueSelected> {

	interface Listener {

		void onSelectionTitle(IssueSelected issue);
	}

	public enum Columns {

		STATE(""), TYPE("TIPO"), PRIORITY("PRIORIDAD"), COMPANY(
				"EMPRESA"), TITLE("ASUNTO"), LABELS(
						"ETIQUETAS"), OWNER("CREADO POR"), CREATED_AT("FECHA");

		private String mensaje;

		private Columns(String mensaje) {
			this.mensaje = mensaje;
		}

		public String getColumnName() {
			return mensaje;
		}
	}

	public static class IssueOpenLoadSelected extends AonIssueSelected {

		public IssueOpenLoadSelected(JsIssue issue) {
			super(issue);
		}

		@Override
		public AonTagIssueSelected editTag(JsLabel label) {
			for (AonTagIssueSelected tag : getTags()) {
				if (tag.getType() == label.getType())
					getTags().remove(tag);
			}

			AonTagIssueSelected defaultTag = new AonTagIssueSelected(
					label);
			getTags().add(defaultTag);

			if (defaultTag.getType() == TagType.OFFICE_TYPE.value())
				setType(defaultTag.getName());
			else if (defaultTag.getType() == TagType.OFFICE_PRIORITY.value())
				setPriority(defaultTag.getName());

			return defaultTag;
		}

		@Override
		public String getStateIconStyle() {
			
			if (AonStringUtils.equals(NoticeStatus.DUPLICATED.getValue(), issue.getState()))				
				return AON.AON_CSS.aonIconIssueDuplicated();
			else if (AonStringUtils.equals(NoticeStatus.OPEN.getValue(), issue.getState()))
				return AON.AON_CSS.aonIconIssueOpenedGreen();
			else if (issue.getState()
					.compareTo(NoticeStatus.REOPEN.getValue()) == 0)
				return AON.AON_CSS.aonIconIssueReOpenedBlue();
			else
				return AON.AON_CSS.aonIconIssueOpen();
		}

		@Override
		public AonIssueComments editComment(JsIssueComment comment) {

			for (AonIssueComments aux : getComments()) {

				if (aux.getId() == comment.getId()) {
					getComments().remove(aux);
				}
			}

			return new AonIssueComments(comment);
		}
	}

	public static class IssueClosedLoadSelected
			extends AonIssueSelected {

		public IssueClosedLoadSelected(JsIssue issue) {
			super(issue);
		}

		@Override
		public AonTagIssueSelected editTag(JsLabel label) {
			return null;
		}

		@Override
		public String getStateIconStyle() {
			return AON.AON_CSS.aonIconIssueClosed();
		}

		@Override
		public AonIssueComments editComment(JsIssueComment comment) {
			return null;
		}
	}

	public static class IssueFAQLoadSelected
			extends AonIssueSelected {

		public IssueFAQLoadSelected(JsIssue issue) {
			super(issue);
		}

		@Override
		public AonTagIssueSelected editTag(JsLabel label) {
			return null;
		}

		@Override
		public String getStateIconStyle() {
			return AON.AON_CSS.aonIconQuestion() + " " + AON.AON_ICON_CMD_BUTTON;
		}

		@Override
		public AonIssueComments editComment(JsIssueComment comment) {
			return null;
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

	private class HeaderBuilder
			extends AbstractHeaderOrFooterBuilder<IssueSelected> {

		private final Integer ROW_COUNT = 1;
		private Header<String> stateHeader = new TextHeader(
				Columns.STATE.getColumnName());
		private Header<String> typeHeader = new TextHeader(
				Columns.TYPE.getColumnName());
		private Header<String> priorityHeader = new TextHeader(
				Columns.PRIORITY.getColumnName());
		private Header<String> companyHeader = new TextHeader(
				Columns.COMPANY.getColumnName());
		private Header<String> titleHeader = new TextHeader(
				Columns.TITLE.getColumnName());
		private Header<String> labelsHeader = new TextHeader(
				Columns.LABELS.getColumnName());
		private Header<String> ownerHeader = new TextHeader(
				Columns.OWNER.getColumnName());
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
			buildHeader(tr, typeHeader, typeColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, priorityHeader, priorityColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, companyHeader, companyColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, titleHeader, titleColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, labelsHeader, labelsColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, ownerHeader, ownerColumn, sortedColumn,
					isSortAscending, false, false);
			buildHeader(tr, createdAtHeader, createdAtColumn, sortedColumn,
					isSortAscending, false, false);

			tr.endTR();

			return true;
		}

		private void buildHeader(TableRowBuilder out, Header<?> header,
				Column<IssueSelected, ?> column, Column<?, ?> sortedColumn,
				boolean isSortAscending, boolean isFirst, boolean isLast) {

			boolean isSorted = (sortedColumn == column);
			TableCellBuilder th = out.startTH();
			th.className(AON.AON_CSS.aonDataTableHeader());
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
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.className(rowValue.getStateIconStyle());
			td.className(AON.AON_CSS.aonDataTableIconColumn());
			td.title(rowValue.getState());
			renderCell(td, createContext(col++), stateColumn, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.style().cursor(Cursor.POINTER);
			td.className(AON.AON_CSS.aonDataTableTextColumn());
			renderCell(td, createContext(col++), typeColumn, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.className(AON.AON_CSS.aonDataTableTextColumn());
			renderCell(td, createContext(col++), priorityColumn, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.className(AON.AON_CSS.aonDataTableTextColumn());
			renderCell(td, createContext(col++), companyColumn, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.className(AON.AON_CSS.aonDataTableTextColumn());
			renderCell(td, createContext(col++), titleColumn, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString());
			td.className(AON.AON_CSS.aonDataTableTextColumn());
			renderCell(td, createContext(col++), labelsColumn, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			td.className(AON.AON_CSS.aonDataTableTextColumn());
			renderCell(td, createContext(col++), ownerColumn, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());
			//td.className(AON.AON_BOLD);
			renderCell(td, createContext(col++), createdAtColumn, rowValue);
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
	private Column<IssueSelected, String> typeColumn;
	private Column<IssueSelected, String> priorityColumn;
	private Column<IssueSelected, String> companyColumn;
	private Column<IssueSelected, String> titleColumn;
	private Column<IssueSelected, String> labelsColumn;
	private Column<IssueSelected, String> ownerColumn;
	private Column<IssueSelected, String> createdAtColumn;

	public IssueGrid() {
		super();

		listeners = new ArrayList<IssueGrid.Listener>();

		selectionModel = new MultiSelectionModel<IssueSelected>();

		setStyleName(AON.AON_CSS.aonGwtOfficeDataTable());
		setAutoHeaderRefreshDisabled(false);
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
		setColumnWidth(col++, 5, Unit.PX);

		typeColumn = new Column<IssueSelected, String>(
				new ClickableTextCell()) {

			@Override
			public String getValue(IssueSelected object) {
				return object.getType();
			}
		};
		typeColumn.setFieldUpdater(new FieldUpdater<IssueSelected, String>() {

			@Override
			public void update(int index, IssueSelected object, String value) {
				onSelectionTitle(object);
			}
		});
		setColumnWidth(col++, 20, Unit.PX);

		priorityColumn = new Column<IssueSelected, String>(new TextCell()) {

			@Override
			public String getValue(IssueSelected object) {
				return object.getPriority();
			}
		};
		setColumnWidth(col++, 20, Unit.PX);

		companyColumn = new Column<IssueSelected, String>(new TextCell()) {

			@Override
			public String getValue(IssueSelected object) {
				return object.getCompany();
			}
		};
		setColumnWidth(col++, 40, Unit.PX);

		titleColumn = new Column<IssueSelected, String>(new TextCell()) {

			@Override
			public String getValue(IssueSelected object) {
				return object.getTitle();
			}
		};
		setColumnWidth(col++, 80, Unit.PX);

		labelsColumn = new Column<IssueSelected, String>(
				new ClickableTextCell()) {

			@Override
			public String getValue(IssueSelected object) {

				String labels = "";
				if (object.getTags() == null)
					return labels;

				StringBuffer buffer = new StringBuffer();
				ListIterator<AonTagIssueSelected> iterator = object
						.getTags().listIterator();
				while (iterator.hasNext()) {
					AonTagIssueSelected tag = iterator.next();
					if (tag.getType() == TagType.OFFICE_NOTICE.value()
							&& tag.getDeletedAt() == null) {
						buffer.append(tag.getName().toUpperCase());
						if (iterator.hasNext())
							buffer.append(" - ");
					}
				}

				return (buffer.toString().endsWith(" - "))
						? buffer.toString().substring(0,
								buffer.toString().length() - 2)
						: buffer.toString();
			}
		};
		setColumnWidth(col++, 40, Unit.PX);

		ownerColumn = new Column<IssueSelected, String>(
				new ClickableTextCell()) {

			@Override
			public String getValue(IssueSelected object) {
				return object.getUser().getName();
			}
		};

		setColumnWidth(col++, 30, Unit.PX);

		createdAtColumn = new Column<IssueSelected, String>(new TextCell()) {

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
		setColumnWidth(col++, 35, Unit.PX);
	}

	public Set<IssueSelected> getSelectedObject() {
		return selectionModel.getSelectedSet();
	}

	public void clearSelected(IssueSelected object) {
		selectionModel.setSelected(object, false);
	}

	private void onSelectionTitle(IssueSelected issue) {
		for (Listener listener : listeners)
			listener.onSelectionTitle(issue);
	}
}
