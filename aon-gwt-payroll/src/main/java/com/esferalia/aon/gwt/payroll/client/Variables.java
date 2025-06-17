package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

public abstract class Variables extends Composite {

	public static interface Variable<T extends Enum<?>> {

		public Integer getId();

		public Date getEndDate();

		public Variable<T> setEndDate(Date endDate);

		public Date getStartDate();

		public Variable<T> setStartDate(Date startDate);

		public String getExpression();

		public Variable<T> setExpression(String expression);

		public String getDescription();

		public Variable<T> setDescription(String description);

		public boolean getHasChange();

		public Variable<T> setHasChange(boolean hasChanged);

		public T getVariableType();

		public Variable<T> setVariableType(T t);

		public static final ProvidesKey<Variable<?>> KEY_PROVIDER = item -> item == null ? null : item.getId();

	}

	public static interface VariablesObject<T extends Variable<?>> {

		public Date getEndDate();

		public Date getStartDate();

		public void deleteVariable(T variable);

		public void updateVariables(Consumer<Void> success, Consumer<Throwable> failure);

		public void getVariables(Consumer<List<T>> success, Consumer<Throwable> failure);

		public List<T> getVariables(String yearValue, String monthValue, String variableTypeValue);

		public void createVariable(T variable, Consumer<Void> success, Consumer<Throwable> failure);
	}

	// ----------------------------------------------- UiBinder

	private static VariablesUiBinder uiBinder = GWT.create(VariablesUiBinder.class);

	interface VariablesUiBinder extends UiBinder<Widget, Variables> {
	}

	// ----------------------------------------------- ContractConceptCalcTypeCell

	class VariablesTypeCell extends AbstractCell<String> {

		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			if (value == null)
				return;

			sb.appendHtmlConstant("<b title=\"" + Variables.this.getVariableType(value) + "\">" + value + "</b>");
		}

	}

	// ----------------------------------------------- UiField

	@UiField
	DockLayoutPanel dockLayoutPanel;

	@UiField(provided = true)
	AonToolbar toolbar;

	@UiField(provided = true)
	DataGrid<Variable<?>> variablesDG;

	// ----------------------------------------------- Variables

	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");

	private List<Variable<?>> variableList;
	private VariablesObject<Variable<?>> variablesObject;

	private ListBox yearLB;
	private ListBox monthLB;
	private ListBox variableTypeLB;

	// ----------------------------------------------- Constructor

	public Variables() {
		initializeToolbarPanel();
		providevariablesDG();
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().getStyle().setHeight(100, Unit.PCT);
		setScrollPanelHeight();
	}
	
	public void setTitle(String title) {
		this.toolbar.setTitle(title);
	}

	// ----------------------------------------------- Auxiliar Methods (Constructor
	// & DataGrid)

	private void setScrollPanelHeight() {
		variablesDG.setHeight("95%");
	}

	// ----------------------------------------------- ProvidevariablesDG

	private void providevariablesDG() {
		variableList = Collections.emptyList();

		// Resource Style CellTable
		variablesDG = new CustomDataGrid<>(Integer.MAX_VALUE, Variable.KEY_PROVIDER);

		// Do not refresh the headers every time the dataGrid is updated.
		variablesDG.setAutoHeaderRefreshDisabled(true);

		// Set the message to display when the table is empty.
		variablesDG.setEmptyTableWidget(new Label(("No existen datos").toUpperCase()));

		// Initialize the columns.
		addVariableColumns();

		new ListDataProvider<Variable<?>>(Collections.emptyList()).addDataDisplay(variablesDG);
	}

	private void addVariableColumns() {
		// Edit column.
		ActionCell<Variable<?>> editActionCell = new ActionCell<>("",
				selectedVariable -> openVariableDialog(selectedVariable));

		Column<Variable<?>, Variable<?>> editColumn = new Column<Variable<?>, Variable<?>>(editActionCell) {

			@Override
			public Variable<?> getValue(Variable<?> variable) {
				return variable;
			}

			@Override
			public void render(Context context, Variable<?> variable, SafeHtmlBuilder sb) {
				if (null != variable) {
					sb.appendHtmlConstant(
							"<button type=\"button\" class=\"aon_button aon_table_button aon_icon_right\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
				}
			}
		};

		editColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		variablesDG.setColumnWidth(editColumn, 5, Unit.PCT);

		// Type columns.
		Column<Variable<?>, String> typeColumn = new Column<Variable<?>, String>(new VariablesTypeCell()) {
			@Override
			public String getValue(Variable<?> variable) {
				return getVariableTypeShort(variable.getVariableType());
			}
		};

		typeColumn.setSortable(true);
		typeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		variablesDG.setColumnWidth(typeColumn, 5, Unit.PCT);

		// Description column.
		Column<Variable<?>, String> descriptionColumn = new Column<Variable<?>, String>(new TextCell()) {
			@Override
			public String getValue(Variable<?> variable) {
				return variable.getDescription();
			}
		};

		descriptionColumn.setSortable(true);
		variablesDG.setColumnWidth(descriptionColumn, 20, Unit.PCT);

		// Expression column.
		Column<Variable<?>, String> expressionColumn = new Column<Variable<?>, String>(new TextCell()) {
			@Override
			public String getValue(Variable<?> variable) {
				return variable.getExpression();
			}
		};

		expressionColumn.setSortable(true);
		variablesDG.setColumnWidth(expressionColumn, 35, Unit.PCT);

		// StartDate column.
		Column<Variable<?>, String> startDateColumn = new Column<Variable<?>, String>(new TextCell()) {
			@Override
			public String getValue(Variable<?> variable) {
				return formatDate.format(variable.getStartDate());
			}
		};

		startDateColumn.setSortable(true);
		startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		variablesDG.setColumnWidth(startDateColumn, 10, Unit.PCT);

		// EndDate column.
		Column<Variable<?>, String> endDateColumn = new Column<Variable<?>, String>(new TextCell()) {
			@Override
			public String getValue(Variable<?> variable) {
				Date endDate = variable.getEndDate();
				return endDate == null ? "" : formatDate.format(endDate);
			}
		};

		endDateColumn.setSortable(true);
		endDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		variablesDG.setColumnWidth(endDateColumn, 10, Unit.PCT);

		// Delete column.
		ActionCell<Variable<?>> deleteActionCell = new ActionCell<>("", variable -> {
			AonDialog deleteDialog = new AonDialog("Eliminar variable",
					new HTML("\u00BFDesea eliminar la variable seleccionada\u003F"));
			deleteDialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					// Nothing to do here
				}

				@Override
				public void onAccept() {
					variablesObject.deleteVariable(variable);
					onSave();
				}
			});
		});

		Column<Variable<?>, Variable<?>> deleteColumn = new Column<Variable<?>, Variable<?>>(deleteActionCell) {

			@Override
			public Variable<?> getValue(Variable<?> variable) {
				return variable;
			}

			@Override
			public void render(Context context, Variable<?> variable, SafeHtmlBuilder sb) {
				if (null != variable) {
					sb.appendHtmlConstant(
							"<button type=\"button\" class=\"aon_button aon_table_button aon_icon_delete\" style=\"border: none !important; height: 20px;\" title=\"Eliminar\"></button>");
				}
			}
		};

		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		variablesDG.setColumnWidth(deleteColumn, 5, Unit.PCT);

		// Add the columns.
		variablesDG.addColumn(editColumn, "");
		variablesDG.addColumn(typeColumn, "Tipo");
		variablesDG.addColumn(descriptionColumn, "Descripci\u00F3n");
		variablesDG.addColumn(expressionColumn, "Expresi\u00F3n");
		variablesDG.addColumn(startDateColumn, "F. Inicio");
		variablesDG.addColumn(endDateColumn, "F. Fin");
		variablesDG.addColumn(deleteColumn, "");
	}

	// ----------------------------------------------- InitVariables

	public void initVariablesTable() {
		// Create a data provider.
		ListDataProvider<Variable<?>> variableDataProvider = new ListDataProvider<>();

		// Connect the table to the data provider.
		variableDataProvider.addDataDisplay(variablesDG);

		// Add the data to the data provider, which automatically pushes it to the
		// widget.
		List<Variable<?>> variableListAux = variableDataProvider.getList();
		variableListAux.clear();

		this.variableList = variablesObject.getVariables(yearLB.getSelectedValue(), monthLB.getSelectedValue(),
				variableTypeLB.getSelectedValue());

		for (Variable<?> variable : this.variableList) {
			variableListAux.add(variable);
		}

		addSortColums(variableListAux);

		// Set page size
		variablesDG.setPageSize(variableListAux.size());

		setScrollPanelHeight();
	}

	private void addSortColums(List<Variable<?>> variableList) {
		ListHandler<Variable<?>> columnSortHandler = new ListHandler<>(variableList);

		columnSortHandler.setComparator(variablesDG.getColumn(1), (o1, o2) -> compareString(o1, o2,
				getVariableTypeShort(o1.getVariableType()), getVariableTypeShort(o2.getVariableType())));

		columnSortHandler.setComparator(variablesDG.getColumn(2),
				(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));

		columnSortHandler.setComparator(variablesDG.getColumn(3),
				(o1, o2) -> compareString(o1, o2, o1.getExpression(), o2.getExpression()));

		columnSortHandler.setComparator(variablesDG.getColumn(4),
				(o1, o2) -> compareDates(o1, o2, o1.getStartDate(), o2.getStartDate()));

		columnSortHandler.setComparator(variablesDG.getColumn(5),
				(o1, o2) -> compareDates(o1, o2, o1.getEndDate(), o2.getEndDate()));

		variablesDG.addColumnSortHandler(columnSortHandler);

		// We know that the data is sorted alphabetically by default.
		variablesDG.getColumn(1).setDefaultSortAscending(false);
		variablesDG.getColumnSortList().push(variablesDG.getColumn(1));
	}

	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2)
			return 0;
		else if (o1 == null || AonStringUtils.isBlank(s1))
			return -1;
		else if (o2 == null || AonStringUtils.isBlank(s2))
			return 1;
		else
			return s1.compareTo(s2);
	}

	private int compareDates(Object o1, Object o2, Date d1, Date d2) {
		if (o1 == o2)
			return 0;
		else if (o1 == null || d1 == null)
			return -1;
		else if (o2 == null || d2 == null)
			return 1;
		else
			return d1.compareTo(d2);
	}

	// -----------------------------------------------
	// setEmployeeVariablesObject

	public void setVariablesObject(VariablesObject variablesObject) {
		this.variablesObject = variablesObject;
		this.variablesObject.getVariables(r -> {
			initializeYearLB();
			initVariablesTable();
		}, t -> {
		});
	}

	// -----------------------------------------------
	// setEmployeeVariablesObject.Methods

	public void initializeYearLB() {
		Integer startYear = DateUtils.getYear(variablesObject.getStartDate());
		Integer endYear = null == variablesObject.getEndDate() ? DateUtils.getYear()
				: DateUtils.getYear(variablesObject.getEndDate());

		Integer auxYear = endYear;
		if (null == variablesObject.getEndDate())
			auxYear++;

		this.yearLB.clear();
		this.yearLB.addItem("-", "");

		while (auxYear >= startYear) {
			this.yearLB.addItem(auxYear.toString(), auxYear.toString());
			auxYear--;
		}

		this.yearLB.addChangeHandler(e -> {
			checkSelectedYear();
			changeYear();
		});

		setSelectedValueLB(this.yearLB, endYear.toString());
	}

	private void checkSelectedYear() {
		if (AonStringUtils.isBlank(yearLB.getSelectedValue())) {
			monthLB.setSelectedIndex(0);
			monthLB.setVisible(false);
		} else
			monthLB.setVisible(true);
	}

	public void initializeMonthLB() {
		this.monthLB.clear();
		this.monthLB.addItem("-", "");
		this.monthLB.addItem("Enero", "0");
		this.monthLB.addItem("Febrero", "1");
		this.monthLB.addItem("Marzo", "2");
		this.monthLB.addItem("Abril", "3");
		this.monthLB.addItem("Mayo", "4");
		this.monthLB.addItem("Junio", "5");
		this.monthLB.addItem("Julio", "6");
		this.monthLB.addItem("Agosto", "7");
		this.monthLB.addItem("Septiembre", "8");
		this.monthLB.addItem("Octubre", "9");
		this.monthLB.addItem("Noviembre", "10");
		this.monthLB.addItem("Diciembre", "11");

		this.monthLB.addChangeHandler(e -> changeYear());
	}

	public void setSelectedValueLB(ListBox lBox, String str) {
		String text = str;
		int indexToFind = 0;
		for (int i = 0; i < lBox.getItemCount(); i++) {
			if (lBox.getValue(i).equals(text)) {
				indexToFind = i;
				break;
			}
		}
		lBox.setSelectedIndex(indexToFind);
	}

	protected void changeYear() {
		initVariablesTable();
	}

	// ----------------------------------------------- Toolbar

	private void initializeToolbarPanel() {

		this.toolbar = new AonToolbar("Variables Contrato");

		AonToolbarButton addVariable = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
		addVariable.addClickHandler(e -> openVariableDialog(null));
		toolbar.add(addVariable);

		this.variableTypeLB = new ListBox();
		initializeVariableTypeLB(this.variableTypeLB);
		
		this.toolbar.add(this.variableTypeLB);

		this.yearLB = new ListBox();
		this.toolbar.add(this.yearLB);

		this.monthLB = new ListBox();
		initializeMonthLB();
		this.toolbar.add(this.monthLB);

	}

	// ----------------------------------------------- Toolbar.Methods

	public void onSave() {
		variablesObject.updateVariables(r -> changeYear(), t -> {
		});
	}

	public void openVariableDialog(Variable<?> selectedVariable) {
		new VariableDialog(selectedVariable, variablesObject.getStartDate(), variablesObject.getEndDate()) {
			@Override
			protected void onAcceptDialog(Variable<?> variable) {
				// Create new variable
				if (variable.getId() == null) {
					variablesObject.createVariable(variable,
							s -> variablesObject.getVariables(r -> initVariablesTable(), t -> {
							}), f -> {
							});
					// Update variable
				} else {
					variable.setHasChange(true);
					onSave();
				}
			}

			@Override
			protected Variable<?> newVariable() {
				return Variables.this.newVariable();
			}
			
			@Override
			protected <T extends Enum<?>> T getVariableType(String value) {
				return Variables.this.valueOf(value);
			}
			
			@Override
			protected void initListBox(ListBox variableTypeListBox) {
				Variables.this.initializeDialogVariableTypeLB(variableTypeListBox);
			}
		};
	}

	// -------------------------------------------------- ContrataEmployee.Methods

	public void hideToolbar() {
		dockLayoutPanel.remove(toolbar);
	}

	public void setVariableTypeLB(ListBox variableTypeLB) {
		this.variableTypeLB = variableTypeLB;
	}

	public void setYearLB(ListBox yearLB) {
		this.yearLB = yearLB;
	}

	public void setMonthLB(ListBox monthLB) {
		this.monthLB = monthLB;
	}

	public void setToolbarTitle(String title) {
		toolbar.setTitle(title);
	}
	
	// ---------------------------------------------------------- Abstract.Methods

	protected abstract Variable<?> newVariable();

	protected abstract <T extends Enum<?>> T valueOf(String type);
	
	protected abstract <T extends Enum<?>> String getVariableType(String shortType);

	protected abstract <T extends Enum<?>> String getVariableTypeShort(T variableType) ;

	protected abstract void initializeVariableTypeLB(ListBox variableTypeListBox) ;

	protected abstract void initializeDialogVariableTypeLB(ListBox variableTypeListBox) ;
}
