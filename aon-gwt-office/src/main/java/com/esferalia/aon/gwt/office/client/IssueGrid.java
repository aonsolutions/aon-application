package com.esferalia.aon.gwt.office.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.office.client.models.issues.Issue;
import com.esferalia.aon.gwt.office.client.models.issues.Label;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JsArray;
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

public class IssueGrid extends CustomDataGrid<IssueSelected> implements
		HasSelectionHandlers<IssueSelected> {

	interface Listener {
		void onSelectionChangeHandler(SelectionChangeEvent event);
		
		void onSelectionTitle(IssueSelected issue);
	}

	public enum Columns {

		CHECK(""), TITLE("Asunto"), LABELS(""), CREATED_AT("Creado en ..");

		private String mensaje;

		private Columns(String mensaje) {
			this.mensaje = mensaje;
		}

		public String getColumnName() {
			return mensaje;
		}
	}

	public static class DefaultAonIssuesSelected implements IssueSelected {

		private Issue issue;

		public DefaultAonIssuesSelected(Issue issue) {
			setIssue(issue);
		}

		private void setIssue(Issue issue) {
			this.issue = issue;
		}

		@Override
		public String getTitle() {
			return issue.getTitle();
		}

		@Override
		public String getState() {
			return issue.getState();
		}

		@Override
		public Date getCreateAt() {
			return new Date();
		}

		@Override
		public Date getUpdatedAt() {
			return issue.getUpdatedAt();
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
		public JsArray<Label> getLabels() {
			return issue.getLabels();
		}
	}
	
	public static class IssueLoadSelected extends DefaultAonIssuesSelected {

		public IssueLoadSelected(Issue issue) {			
			super(issue);
		}
	}

	private class HeaderBuilder extends
			AbstractHeaderOrFooterBuilder<IssueSelected> {

		private final Integer ROW_COUNT = 1;
		private Header<Boolean> checkHeader = IssueGrid.this.newCheckHeader();
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
			boolean isSortAscending = (sortedInfo == null) ? false : sortedInfo
					.isAscending();

			tr = startRow().className(AON.AON_CSS.childCell());
			buildHeader(tr, checkHeader, checkBox, sortedColumn,
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
			super(IssueGrid.this);
			
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

			td = row.startTD()
					.align(HasHorizontalAlignment.ALIGN_LEFT
							.getTextAlignString())
					.className(AON.AON_CSS.childCell());
			renderCell(td, createContext(col++), checkBox, rowValue);
			td.endTD();

			td = row.startTD().align(
					HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString());			
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

	private Column<IssueSelected, Boolean> checkBox;
	private Column<IssueSelected, String> title;
	private Column<IssueSelected, String> labels;
	private Column<IssueSelected, String> createdAt;

	public IssueGrid() {
		super();

		listeners = new ArrayList<IssueGrid.Listener>();
		checkBox = newCheckColumn();
		selectionModel = new MultiSelectionModel<IssueSelected>();

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

		setSelectionModel(selectionModel,
				DefaultSelectionEventManager
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
		
		
		addColumn(checkBox);
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
		
		labels = new Column<IssueSelected, String>(new TextCell()) {

			@Override
			public String getValue(IssueSelected object) {				
				
				String labels = "";
				if (object.getLabels() == null)
					return labels;
				
				for (int z = 0; z < object.getLabels().length(); z++) {
					
					String aux = object.getLabels().get(z).getName().toUpperCase();
					labels += object.getLabels().get(z).getName().toUpperCase() + " ";
				}
					
					 
				
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
				for (IssueSelected item : getVisibleItems())
					selectionModel.setSelected(item, value);
				// getSelectionModel().setSelected(item, value);
			}
		});
		return header;
	}

	private Column<IssueSelected, Boolean> newCheckColumn() {
		Column<IssueSelected, Boolean> checkColumn = new Column<IssueSelected, Boolean>(
				new CheckboxCell()) {

			@Override
			public Boolean getValue(IssueSelected object) {
				return selectionModel.isSelected(object);
			}
		};

		checkColumn
				.setFieldUpdater(new FieldUpdater<IssueSelected, Boolean>() {

					@Override
					public void update(int index, IssueSelected object,
							Boolean value) {
						redraw();
					}
				});
		return checkColumn;
	}

	public Set<IssueSelected> getSelectedObject() {
		return selectionModel.getSelectedSet();
	}

	public void clearSelected(IssueSelected object) {
		selectionModel.setSelected(object, false);
	}

}
