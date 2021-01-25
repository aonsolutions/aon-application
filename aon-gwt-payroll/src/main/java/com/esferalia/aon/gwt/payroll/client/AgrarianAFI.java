package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.AgrarianJourney;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class AgrarianAFI extends MainEntryPoint {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	interface Binder extends UiBinder<Widget, AgrarianAFI> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String bold();
		String paddingDays();
		String widthDays();
		String textCenter();
		String widthFirstColumn();
		String paddingText();
		String headerStyle();
		String cellStyle();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	Label enterprise;
	
	@UiField
	ListBox enterpriseList;
	
	@UiField
	ListBox cccs;
	
	@UiField
	CheckBox allCCCs;
	
	@UiField
	Label allCCCsLabel;
	
	@UiField
	ListBox monthList;
	
	@UiField
	ListBox yearList;
	
	@UiField
	HTMLPanel mainTablePanel;
	
	@UiField
	Grid employeeTable;
	
	private DateTimeFormat formatDayDate = DateTimeFormat.getFormat("dd");
	
	private Map<Integer, List<AgrarianJourney>> agrarianJourney = new HashMap<>();
	private ArrayList<Integer> selectedEmployees = new ArrayList<>();
	
	// All enterpises of a domain
	private List<Enterprise> enterprisesList;
	
	// Selected enterprise, if only one that is the one selected
	private Enterprise selectedEnterprise;
	
	// All the CCCs of the selected enterprise
	private Map<String, List<CCC>> agrarianCCCs = new HashMap<>();
	
	// The list of CCCs we are going to generate CRA
	private List<String> cccList = new ArrayList<String>();
	
	// The date to generate AFI
	private Date startDate = null;
	
	// CCC Id we are going to generate AFI of
	
	private AonToolbar toolbar;
	private AonToolbarButton searchButton;
	private AonToolbarButton exportButton;
	
	public void onModuleLoad() {
		// Inject style
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		// Create UiBinder
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		// Create toolbar
		toolbar = getToolbarPanel();
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		exportButton.setVisible(false);
		
		//Hide EmployeePanel
		setVisible(mainTablePanel, false);
		
		initListBoxes();
		initLogic();	
	}
	
	// ---------------------------------------------------------------------------------------------
	//										LOAD PREVIEW
	// ---------------------------------------------------------------------------------------------
	
	private void initListBoxes() {
		// Set list box for filter by dates
		monthList.clear();
		yearList.clear();
		
		String[] months = new String[]{"Enero", "Frebero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
		for(int i=0; i<months.length; i++) {
			monthList.addItem(months[i], i+"");
		}
		
		Integer yearInt = DateUtils.getYear();
		
		yearList.addItem(yearInt +"", yearInt + "");
		yearList.addItem((yearInt - 1) + "", (yearInt - 1) + "");
	}

	private void initLogic() {
		agrarianCCCs.clear();
		enterpriseList.clear();
		
		impl.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {
			
			@Override
			public void onSuccess(List<Enterprise> enterprises) {
				// Set all enterprises of a domain
				enterprisesList = enterprises;
				
				if(enterprises.size() == 1){
					setVisible(enterpriseList, false);
					setVisible(enterprise, true);
				} else {
					setVisible(enterpriseList, true);
					setVisible(enterprise, false);
					
					// Fill enterprise list box
					for (Enterprise enterprise: enterprises)
						enterpriseList.addItem(enterprise.getName());
				}
				
				selectedEnterprise = enterprises.get(0);
				
				for(Activity activity : selectedEnterprise.getActivities())
					for(CCC ccc : activity.getCccs())
						if(ccc.getRegime() == "0163")
							addCCCToActivity(activity.getDescription(), ccc);
				
				initializeView();
			}
			
			@Override
			public void onFailure(Throwable caught) { }
			
		});
	}
	
	private void initializeView(){
		//Enterprise Name
		enterprise.setText(this.selectedEnterprise.getName());
		allCCCsLabel.setText(" Todos los CCCs de " + this.selectedEnterprise.getName());
		enterprise.addStyleName(style.bold());
		
		if(this.agrarianCCCs.isEmpty()){
			this.cccs.setEnabled(false);
			this.monthList.setEnabled(false);
			this.yearList.setEnabled(false);
			this.searchButton.setEnabled(false);
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.info("AVISO", "No existe ninguna cuenta de cotizaci"+String.valueOf("\u00F3")+"n de tipo agrario.");
		}else{
			for(Entry<String, List<CCC>> entry : this.agrarianCCCs.entrySet()){
				for(CCC ccc : entry.getValue()) {
					this.cccs.addItem(entry.getKey() + " - " + getRegimeName(ccc.getRegime()) + " - " 
							+ ccc.getCode() + " - (" + ProvinceContract.getName(ccc.getGeozone()) +")", ccc.getCode());
				}
			}
			
			setFindingCCC(this.cccs.getSelectedValue(), false);
		}
	}

	// ---------------------------------------------------------------------------------------------
	//									PAINT AGRARIAN TABLE
	// ---------------------------------------------------------------------------------------------
	
	private void initializeTableJourney() {
		if(this.agrarianJourney.entrySet().size() == 0){
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.info("AVISO", "No hay contratos con peonadas para estas fechas.");
		}else{
			initializeHeader();
			for(Entry<Integer, List<AgrarianJourney>> entry :this.agrarianJourney.entrySet()){
				//Fill Practice Row
				Integer newRow = employeeTable.insertRow(employeeTable.getRowCount());
				CheckBox select = new CheckBox();
				select.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if(select.getValue()){
							selectedEmployees.add(entry.getKey());
							enableDisableExportBtn();
						}else {
							selectedEmployees.remove(entry.getKey());
							enableDisableExportBtn();
						}
					}
				});
				employeeTable.setWidget(newRow, 0, select);
				Label employeeName = new Label(entry.getValue().get(0).getSurname() + ", " + entry.getValue().get(0).getName());
				employeeTable.setWidget(newRow, 1, employeeName);
				Label totalDays = createTotalDays(entry.getKey());
				employeeTable.setWidget(newRow, 2, totalDays);
				HorizontalPanel agrarianMonth = createAgrarianMonthPanel(entry.getKey());
				agrarianMonth.getElement().getStyle().setWidth(100, Unit.PCT);
				employeeTable.setWidget(newRow, 3, agrarianMonth);
				
				for(int i=0; i < employeeTable.getColumnCount(); i++)
					employeeTable.getCellFormatter().addStyleName(newRow, i, style.cellStyle());
			}
		}	
	}
	
	private void initializeHeader() {
		//Remove hide
		setVisible(mainTablePanel, true);
		
		//Header Grid
		employeeTable.resize(0,4);
		Integer newRow = employeeTable.insertRow(employeeTable.getRowCount());
		CheckBox selectAll = new CheckBox();
		selectAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(selectAll.getValue()){
					Integer rows = employeeTable.getRowCount();
					for(int i=1; i<rows; i++){
						CheckBox checkBox = (CheckBox) employeeTable.getWidget(i, 0);
						checkBox.setValue(true);
					}
					selectedEmployees.clear();
					for(Integer contractId : agrarianJourney.keySet())
						selectedEmployees.add(contractId);
					enableDisableExportBtn();
					
				}else{
					Integer rows = employeeTable.getRowCount();
					for(int i=1; i<rows; i++){
						CheckBox checkBox = (CheckBox) employeeTable.getWidget(i, 0);
						checkBox.setValue(false);
					}
					selectedEmployees.clear();
					enableDisableExportBtn();
				}
			}
		});
		
		employeeTable.setWidget(newRow, 0, selectAll);
		Label employeeLabel = new Label("Empleado");
		employeeLabel.addStyleName(style.bold());
		employeeTable.setWidget(newRow, 1, employeeLabel);
		employeeTable.getWidget(newRow, 1).addStyleName(style.widthFirstColumn());
		Label totalDaysLabel = new Label("D"+String.valueOf("\u00ED")+"as");
		totalDaysLabel.addStyleName(style.bold());
		employeeTable.setWidget(newRow, 2, totalDaysLabel);
		HorizontalPanel month = createMonthPanel();
		month.getElement().getStyle().setWidth(100, Unit.PCT);
		employeeTable.setWidget(newRow, 3, month);
		
		for(int i=0; i < employeeTable.getColumnCount(); i++)
			employeeTable.getCellFormatter().addStyleName(0, i, style.headerStyle());
	}
	
	private HorizontalPanel createMonthPanel() {
		HorizontalPanel hPanel = new HorizontalPanel();
		
		Date findingDate = DateUtils.getDate(Integer.parseInt(monthList.getSelectedValue()), Integer.parseInt(yearList.getSelectedValue()));
		Date lastDayOfMonth = DateUtils.getLastDayOfMonth(findingDate);
		Integer maxDays = Integer.parseInt(formatDayDate.format(lastDayOfMonth));
		
		for(int i = 0; i < maxDays; i++) {
			Label day = new Label((i+1)+"");
			day.addStyleName(style.bold());
			day.addStyleName(style.widthDays());
			day.addStyleName(style.paddingDays());
			day.addStyleName(style.textCenter());
			hPanel.add(day);
		}
		return hPanel;
	}
	
	private HorizontalPanel createAgrarianMonthPanel(Integer contractId) {
		HorizontalPanel hPanel = new HorizontalPanel();

		Date findingDate = DateUtils.getDate(Integer.parseInt(monthList.getSelectedValue()), Integer.parseInt(yearList.getSelectedValue()));
		Date lastDayOfMonth = DateUtils.getLastDayOfMonth(findingDate);
		Integer maxDays = Integer.parseInt(formatDayDate.format(lastDayOfMonth));
		
		for(int i = 0; i < maxDays; i++) {
			Label day = new Label();
			Date findingDateCopy = DateUtils.copyDateOnly(findingDate);
			Date findingDateIterator = DateUtils.copyDateOnly(DateUtils.addDays2Date(findingDateCopy, i));
			if(checkDateAgraria(contractId, findingDateIterator))
				day.setText("S");
			else
				day.setText("-");
			
			day.addStyleName(style.widthDays());
			day.addStyleName(style.paddingDays());
			day.addStyleName(style.textCenter());
			hPanel.add(day);
		}
		return hPanel;
	}

	// ---------------------------------------------------------------------------------------------
	//										UI HANDLERS
	// ---------------------------------------------------------------------------------------------
	
	@UiHandler("enterpriseList")
	void changeEnterpriseList(ChangeEvent event){
		cccs.clear();
		
		for(Enterprise enterprise: enterprisesList) {
			if(enterpriseList.getSelectedValue().equals(enterprise.getName())) {
				this.selectedEnterprise = enterprise;
				continue;
			}
		}
		
		agrarianCCCs.clear();
		
		for(Activity activity : selectedEnterprise.getActivities())
			for(CCC ccc : activity.getCccs())
				if(ccc.getRegime() == "0163")
					addCCCToActivity(activity.getDescription(), ccc);
		
		for(Entry<String, List<CCC>> entry : this.agrarianCCCs.entrySet()){
			for(CCC ccc : entry.getValue()) {
				this.cccs.addItem(entry.getKey() + " - " + getRegimeName(ccc.getRegime()) + " - " 
						+ ccc.getCode() + " - (" + ProvinceContract.getName(ccc.getGeozone()) +")");
			}
			
		}
		
		if(this.agrarianCCCs.isEmpty()){
			this.cccs.setEnabled(false);
			this.monthList.setEnabled(false);
			this.yearList.setEnabled(false);
			this.searchButton.setEnabled(false);
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.info("AVISO", "No existe ninguna cuenta de cotizaci"+String.valueOf("\u00F3")+"n de tipo agrario.");
		}else{
			this.cccs.setEnabled(true);
			this.monthList.setEnabled(true);
			this.yearList.setEnabled(true);
			this.searchButton.setEnabled(true);
			
			setFindingCCC(this.cccs.getSelectedItemText().split("- ")[2].split(" ")[0], false);
		}
		
		allCCCsLabel.setText(" Todos los CCCs de " + this.selectedEnterprise.getName());
	}
	
	@UiHandler("cccs")
	void changeCCCList(ChangeEvent event){
		setFindingCCC( this.cccs.getSelectedItemText().split("- ")[2].split(" ")[0], false);
	}
	
	@UiHandler("allCCCs")
	void clickAllCCCs(ClickEvent event){
		cccs.setEnabled(!allCCCs.getValue());
		
		if(allCCCs.getValue()) {
			setFindingCCC( "", true);
		}else {
			setFindingCCC( this.cccs.getSelectedItemText().split("- ")[2].split(" ")[0], false);
		}
	}

	// ---------------------------------------------------------------------------------------------
	//										AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------
	
	private void enableDisableExportBtn() {
		this.exportButton.setVisible(this.selectedEmployees.size() != 0);
	}
	
	private void setVisible(Widget widget, boolean isVisible) {
		widget.setVisible(isVisible);
	}
	
	private void addCCCToActivity(String activityDescription, CCC ccc) {
		if(agrarianCCCs.get(activityDescription) == null) {
			List<CCC> cccs = new ArrayList<CCC>();
			cccs.add(ccc);
			agrarianCCCs.put(activityDescription, cccs);
		} else {
			agrarianCCCs.get(activityDescription).add(ccc);
		}
	}
	
	private Label createTotalDays(Integer contractId) {
		Label newLabel = new Label();
		Integer totalDays = getTotalDaysByContract(contractId);
		newLabel.setText(totalDays+"");
		return newLabel;
	}
	
	private Integer getTotalDaysByContract(Integer contractId) {
		Integer totalDays = 0;
		List<AgrarianJourney> journiesList = this.agrarianJourney.get(contractId);
		for(AgrarianJourney journey : journiesList){
			totalDays += journey.getTotalDays();
		}
		return totalDays;
	}
	
	private boolean checkDateAgraria(Integer contractId, Date date) {
		List<AgrarianJourney> journiesList = this.agrarianJourney.get(contractId);
		for(AgrarianJourney journey : journiesList){
			if((journey.getStartDate().before(date) || journey.getStartDate().equals(date)) &&
			   (journey.getEndDate().after(date) || journey.getEndDate().equals(date)))
			   return true;
		}
		return false;
	}
	
	private void setFindingCCC(String selectedCCC, Boolean all) {
		if(all) {
			this.cccList.clear();
			for(Activity activity : this.selectedEnterprise.getActivities()) {
				for(CCC ccc : activity.getCccs()) {
					if(ccc.getRegime() == "0163")
						this.cccList.add(ccc.getCode());
				}
			}
			
			this.enterprise.setText(this.selectedEnterprise.getName());
			
		} else {
			this.cccList.clear();
			for (Enterprise enterprise: enterprisesList)
				for(Activity activity : enterprise.getActivities())
					for(CCC ccc : activity.getCccs())
						if(ccc.getCode().equals(selectedCCC)) {
							selectedEnterprise = enterprise;
							continue;
						}
			
			this.enterprise.setText(this.selectedEnterprise.getName());
			this.cccList.add(selectedCCC);
		}
	}
	
	private boolean checkDate() {
		Date currentDate = DateUtils.getFirstDayOfMonth();
		Date selectedDate = DateUtils.getDate(Integer.parseInt(monthList.getSelectedValue()), Integer.parseInt(yearList.getSelectedValue()));
		
		return currentDate.after(selectedDate);
	}
	
	private String getRegimeName( String regimeCode ){
		switch (regimeCode) {
		case "0163":
			return "Trabajadores cuenta ajena agrarios";
		case "0138":
			return "Emplead@s de hogar";
		case "0111":
			return "Principal";
		default:
			return "Desconocido";
		}
	}
	
	// ---------------------------------------------------------------------------------------------
	//										TOOLBAR
	// ---------------------------------------------------------------------------------------------
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("AFI - Regimen Especial Agrario Jornadas");
		
		searchButton = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSearchButton(event);
			}
		});
		toolbar.add(searchButton);
		
		exportButton = new AonToolbarButton( "Generar AFI", AON.CSS.aonIconTgssAfi() );
		exportButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onExportButton(event);
			}
		});
		toolbar.add(exportButton);

		return toolbar;
	}
	
	private void onSearchButton(ClickEvent event) {
		//CheckDate for searching
		if(checkDate()){
			//Set Dates to find
			Date selectedDate = DateUtils.getDate(Integer.parseInt(monthList.getSelectedValue()), Integer.parseInt(yearList.getSelectedValue()));
			this.startDate = DateUtils.copyDateOnly(selectedDate);
			
			//Get Journies
			impl.getEmployeeAgrarianJourney(this.startDate.getTime(), this.cccList, new AsyncCallback<Map<Integer, List<AgrarianJourney>>>() {
				
				@Override
				public void onSuccess(Map<Integer, List<AgrarianJourney>> result) {
					agrarianJourney = result;
					selectedEmployees.clear();
					enableDisableExportBtn();
					initializeTableJourney();
				}
				
				@Override
				public void onFailure(Throwable caught) {
				}
			});
			
		}else{
			exportButton.setVisible(false);
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.info("ERROR", "No se puede generar el fichero AFI para el mismo mes o posteriores.");
		}
	}
	
	private void onExportButton(ClickEvent event) {
		String fileDownloadURL = GWT.getModuleBaseURL()+ "/agrarian_afi/"
	            + "?findingDate=" + startDate.getTime()
	            + "&selectedCCCs="+cccList.size();
			
		for(int i=0; i<cccList.size(); i++) {
			fileDownloadURL += "&ccc"+i+"Code=" + cccList.get(i);
		}
		
		fileDownloadURL += "&selectedEmployees=" + selectedEmployees.size();
		
		for(int i=0; i<selectedEmployees.size(); i++) {
			fileDownloadURL += "&employee"+i+"Id=" + selectedEmployees.get(i);
		}
		
		Window.open(fileDownloadURL, "_blank", null);
	}

}
