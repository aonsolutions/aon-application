package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.SSPECData;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class SSPECDraft extends Composite {
	
	// -------------------------------------------------- UiBinder
	
	interface SSPECDraftUiBinder extends UiBinder<Widget, SSPECDraft> {}
	
	private static SSPECDraftUiBinder uiBinder = GWT.create(SSPECDraftUiBinder.class);
	
	// -------------------------------------------------- UiFields
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField (provided = true)
	AonToolbar toolbar;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField(provided = true)
	DataGrid<SSPECData> ssPecDG;
	
	// -------------------------------------------------- Variables

	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private SSPECObject ssPECObject;
	private List<SSPECData> ssPecList;
	
	private ListBox yearLB;
	private ListBox monthLB;
	
	// -------------------------------------------------- Constructor

	public SSPECDraft() {
		initializeToolbarPanel();
		provideSSPecDG();
		initWidget(uiBinder.createAndBindUi(this));
		this.getElement().getStyle().setHeight(100, Unit.PCT);
		setScrollPanelHeight();
	}
	
	// ----------------------------------------------- Auxiliar Methods (Constructor & DataGrid) 
	
	private void setScrollPanelHeight() {
		ssPecDG.setHeight((Window.getClientHeight() - 230) + "px");
	}
	
	// ----------------------------------------------- ProvideContractVariablesDG
	
	private void provideSSPecDG() {
		ssPecList = Collections.emptyList();
		
		// Resource Style CellTable
		ssPecDG = new CustomDataGrid<>(Integer.MAX_VALUE, SSPECData.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		ssPecDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		ssPecDG.setEmptyTableWidget(new Label(("No existen datos").toUpperCase()));
		
		// Initialize the columns.
		addSSPecColumns();
	    
	    new ListDataProvider<SSPECData>(Collections.emptyList()).addDataDisplay(ssPecDG);   
	}
	
	private void addSSPecColumns() {
		// Info column.
	    ActionCell<SSPECData> infoActionCell = new ActionCell<>("", selectedSSPec -> { /* Que hacer aqui */});
	    
	    Column<SSPECData, SSPECData> infoColumn = new Column<SSPECData, SSPECData>(infoActionCell) {

			@Override
			public SSPECData getValue(SSPECData ssPECData) {
				return ssPECData;
			}
			
			@Override
			public void render(Context context, SSPECData ssPECData, SafeHtmlBuilder sb) {
				if(null != ssPECData) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_right\" style=\"border: none !important; height: 20px;\" title=\"Editar\"></button>");
				}
			}
		};
		
		infoColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		ssPecDG.setColumnWidth(infoColumn, 5, Unit.PCT);
		
		// StartDate column.
	    Column<SSPECData, String> startDateColumn = new Column<SSPECData, String>(new TextCell()) {
	    	@Override
	        public String getValue(SSPECData ssPECData) {
	    		return formatDate.format(ssPECData.getStartDate());
	        }
		};

	    startDateColumn.setSortable(true);
	    startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    ssPecDG.setColumnWidth(startDateColumn, 10, Unit.PCT);
	    
	    // EndDate column.	    
	    Column<SSPECData, String> endDateColumn = new Column<SSPECData, String>(new TextCell()) {
			@Override
			public String getValue(SSPECData ssPECData) {
				Date endDate = ssPECData.getEndDate();
				return endDate == null ? "" : formatDate.format(endDate);
			}
		};

	    endDateColumn.setSortable(true);
	    endDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    ssPecDG.setColumnWidth(endDateColumn, 10, Unit.PCT);
	    
	    // Description column.
		Column<SSPECData, String> descriptionColumn = new Column<SSPECData, String>(new TextCell()) {
			@Override
			public String getValue(SSPECData ssPECData) {
				return ssPECData.getDescription();
			}
		};
		
		descriptionColumn.setSortable(true);
		
	    // Add the columns.
		ssPecDG.addColumn(infoColumn, "");
		ssPecDG.addColumn(startDateColumn, "F. Inicio");
		ssPecDG.addColumn(endDateColumn, "F. Fin");
		ssPecDG.addColumn(descriptionColumn, "Descripci\u00F3n");
	}
	
	// ----------------------------------------------- InitContractVariables
	
	public void initSSPecTable() {		
		// Create a data provider.
		ListDataProvider<SSPECData> ssPecDataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
		ssPecDataProvider.addDataDisplay(ssPecDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<SSPECData> ssPecListAux = ssPecDataProvider.getList();
	    ssPecListAux.clear();
	    
	    this.ssPecList = ssPECObject.getSSPecDatas(yearLB.getSelectedValue(), monthLB.getSelectedValue());
	    
	    for (SSPECData ssPECData : this.ssPecList) {
	    	ssPecListAux.add(ssPECData);
	    }   
		
		addSortColums(ssPecListAux);
	    
		// Set page size
		ssPecDG.setPageSize(ssPecListAux.size());
		
		setScrollPanelHeight();
	}

	private void addSortColums(List<SSPECData> ssPecList) {
		ListHandler<SSPECData> columnSortHandler = new ListHandler<>(ssPecList);
		
		columnSortHandler.setComparator(ssPecDG.getColumn(1), 
	    	(o1, o2) -> compareDates(o1, o2, o1.getStartDate(), o2.getStartDate()));
	    
	    columnSortHandler.setComparator(ssPecDG.getColumn(2),
		    	(o1, o2) -> compareDates(o1, o2, o1.getEndDate(), o2.getEndDate()));
	    
	    columnSortHandler.setComparator(ssPecDG.getColumn(3), 
		    	(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));
		     
	    ssPecDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    ssPecDG.getColumn(1).setDefaultSortAscending(false);
	    ssPecDG.getColumnSortList().push(ssPecDG.getColumn(1));   
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}
	
	private int compareDates(Object o1, Object o2, Date d1, Date d2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return d1.compareTo(d2);
	}
	
	// -------------------------------------------------- SetContractSSPECObject
	
	public void setContractSSPECObject(SSPECObject ssPECObject) {
		showLoading("Obteniendo bonifinicaciones y peculiaridades...");
		this.ssPECObject = ssPECObject;
		initializeYearLB();
		loadSSPecs();
	}
	
	private void loadSSPecs() {
		this.ssPECObject.getSSPECData(
			s -> {
				hideMessage();
				initSSPecTable();
			}, 
			f -> showError("Error obtenci\u00f3n", f.getMessage())
		);
	}
	
	// -------------------------------------------------- Table methods
	
	public void initializeYearLB() {
		Integer startYear = DateUtils.getYear(ssPECObject.getContractStartDate());
		Integer endYear = null == ssPECObject.getContractEndDate() ? DateUtils.getYear() : DateUtils.getYear(ssPECObject.getContractEndDate());
		
		Integer auxYear = endYear;
		
		this.yearLB.clear();
		this.yearLB.addItem("-", "");
		
		while(auxYear >= startYear) {
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
		if(AonStringUtils.isBlank(yearLB.getSelectedValue())) {
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
	
	private void setSelectedValueLB(ListBox lBox, String str) {
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
	
	private void changeYear() {
		initSSPecTable();
	}

	// ----------------------------------------------- Toolbar
	
	private void initializeToolbarPanel() {
		
		this.toolbar = new AonToolbar("Peculiaridades");
		
		AonToolbarButton addBtn = new AonToolbarButton("Nueva peculiaridad", AON.CSS.aonIconAdd());
		addBtn.addClickHandler(e -> new EmployeePeculiaritiesDialog(ssPECObject.getContractId(), ssPECObject.getContractStartDate()) {

			@Override
			protected void onAccept() {
				loadSSPecs();
			}
			
		});
		toolbar.add(addBtn);
		
		AonToolbarButton syncBtn = new AonToolbarButton("Sincronizaci\u00f3n TGSS", AON.CSS.aonIconTgss());
		syncBtn.addClickHandler(e -> {

			Date startDate = null;
			Date endDate = null;
			if ( AonStringUtils.isNotBlank(this.yearLB.getSelectedValue())) {
				Integer year = Integer.parseInt(this.yearLB.getSelectedValue());
				startDate = DateUtils.getFirstDayOfYear(year - 1900);
				endDate = DateUtils.getLastDayOfYear(year - 1900);
				if ( AonStringUtils.isNotBlank(this.monthLB.getSelectedValue()) ) {
					Integer month = Integer.parseInt(this.monthLB.getSelectedValue());
					startDate.setMonth(month);
					startDate = DateUtils.getFirstDayOfMonth(startDate);
					endDate = DateUtils.getLastDayOfMonth(startDate);
				} 
			}
			
			showLoading("Sincronizando bonifinicaciones y peculiaridades TGSS...");
			this.ssPECObject.syncSSPECData(
					startDate,
					endDate,
					s -> {
						showSuccess("Sincronizaci\u00f3n TGSS", "Bonificaciones y peculiaridades sincronizadas correctamente");
						loadSSPecs();
					}, f -> showError("Error sincronizaci\u00f3n TGSS", f.getMessage()));
		});
		toolbar.add(syncBtn);
		
		this.yearLB = new ListBox();
		this.toolbar.add(this.yearLB);
		
		this.monthLB = new ListBox();
		initializeMonthLB();
		this.toolbar.add(this.monthLB);
		
	}
	
	// ------------------------------------------------- Aon Messages panel
	
	public void setToolbarTitle(String employeeName) {
		toolbar.setTitle(employeeName);
	}
	
	protected Panel getMessagePanel() {
		return messagePanel;
	}

	protected void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(getMessagePanel(), successMap);
	}
	
	protected void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(getMessagePanel(), errorMap);
	}
	
	protected void showLoading(String message) {
		AonMessagePanel.showLoading(getMessagePanel(), message);
	}
	
	protected void hideMessage() {
		AonMessagePanel.hideMessage(getMessagePanel());
	}
}
