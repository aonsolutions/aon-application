package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLoadingPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.ItNotExist;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailStatus;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public abstract class ITDialog extends AonCustomDialog {
    
    	private static final String COVID_IT = "8";

	private static Date COVID_END_DATE = AonDateUtils.parse("dd/MM/yyyy", "25/07/2023");
	
	private static final Logger LOGGER = Logger.getLogger(ITDialog.class.getName());

	
	// --------------------------------------------------- UiBinder
	
	interface ITDialogUiBinder extends UiBinder<Widget, ITDialog> {}
	
	private static ITDialogUiBinder binder = GWT.create(ITDialogUiBinder.class);
	
	// --------------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerStyle();
		String columnWidth();
		String paddingContainer();
		String flexCustom();
		String flexColumn();
		String flex();
		String subTitle();
		String styleBorder();
		String tittle();
		String buttonsPanel();
	}
	
	@UiField
	HTMLPanel north;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	HTMLPanel employeePanel;
	
	@UiField
	HTMLPanel employeePanelDoc;
	
	@UiField
	HTMLPanel employeePanelNaf;

	@UiField
	VerticalPanel itDataTable;
	
	@UiField
	DateBoxEx itStartDate;
	
	@UiField
	Label realStartDate;
	
	@UiField
	ListBox causeHighPart;
	
	@UiField
	DateBoxEx itEndDate;
	
	@UiField
	ListBox causeLowPart;
	
	@UiField
	VerticalPanel informationDataTable;
	
	@UiField
	TextBox collegiateNumberITPart;
	
	@UiField
	TextBox observationTB;
	
	@UiField
	ListBox raggedList;
	
	@UiField
	Label baseRDBxNL;
	
	@UiField
	DoubleBox baseRDBxN;
	
	@UiField
	TextBox ciasITPart;
	
	@UiField
	DateBoxEx directPayDate;
	
	@UiField
	VerticalPanel confirmationsDataTable;
	
	@UiField
	Grid confirmationPartDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid confirmationPartDataTable;
	
	@UiField
	HTMLPanel footerOptionsToolbar;
	
	@UiField
	HTMLPanel mainTablePanel;
	
	@UiField
	VerticalPanel mainCommunicate;
	
	@UiField
	VerticalPanel maternityDataTable;
	
	@UiField
	ListBox applicantTypeList;
	
	@UiField
	ListBox applicantReasonList;
	
	@UiField
	DoubleBox baseRDBx;
	
	@UiField
	DoubleBox partialityCoefDBx;
	
	@UiField(provided = true)
	DataGrid<IT> itDataGrid;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	@UiField
	HTMLPanel enterpriseData;
	
	//-------------COMMUNICATE
	@UiField
	HTMLPanel itBaja;
	
	@UiField
	HTMLPanel itAlta;
	
	private HTMLPanel tramos;
	//-------------END COMMUNICATE
	
	// --------------------------------------------------- Variables
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private NoSelectionModel<IT> selectionITModel;
	
	private ITDialogObject itDialogObject;
	
	private List<IT> itList = Collections.emptyList();
	private IT it;
	private IT itCopy;
	private ITEmployee itEmployee;
	
	private List<ITEmployee> itEmployeeList = Collections.emptyList();
	
	private MultiWordSuggestOracle names = new MultiWordSuggestOracle();
	
	// --------------------------------------------------- Variables.Toolbar
	private AonToolbarButton deleteIT;
	private AonToolbarButton listIT;
	private AonToolbarButton backListIT;
	private AonToolbarButton newIT;
	private AonToolbarButton showCertificate;
	
	// --------------------------------------------------- Variables.Footer

	private DoubleBox baseCC;
	private DoubleBox quoteDayInput;

	private ITPart itPartTmp = null;
	AonLoadingPanel loading = new AonLoadingPanel("Espere...");

	private DoubleBox baseCP;
	
	private boolean userComunica = false;

	private AonToolbar toolbarDetail;
	
	// --------------------------------------------------- ProvideITDataGrid
	
	private void provideITDataGrid() {
		itList  = Collections.emptyList();
		
		// Resource Style CellTable
		itDataGrid = new CustomDataGrid<IT>(Integer.MAX_VALUE, IT.KEY_PROVIDER);
		itDataGrid.setWidth("100%");
		
		//Do not refresh the headers every time the dataGrid is updated.
		itDataGrid.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		itDataGrid.setEmptyTableWidget(new Label("No existen ITs".toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionITModel = new NoSelectionModel<IT>(IT.KEY_PROVIDER);
	    itDataGrid.setSelectionModel(this.selectionITModel);
		
	    // Initialize the columns.
	    addITInfoColumns(this.selectionITModel);
	    
	    new ListDataProvider<IT>(Collections.emptyList()).addDataDisplay(itDataGrid);
	}
	
	private void addITInfoColumns(NoSelectionModel<IT> selectionITModel) {
		selectionITModel.addSelectionChangeHandler(new Handler() {
	        
	        @Override
	        public void onSelectionChange(SelectionChangeEvent event) {
	        	IT itAux = selectionITModel.getLastSelectedObject();
	        	ITDialog.this.it = itAux;
	        	
	        	paintSelectedIT(ITDialog.this.it, true);
	        	
	        	backListIT.setVisible(false);
	    		newIT.setVisible(false);
	    		listIT.setVisible(true);
	    		deleteIT.setVisible(true);
	        }
	    });
	    
	    // Add Selection Column to table
	    itDataGrid.setSelectionModel(selectionITModel);
		
		TextColumn<IT> lowDateColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return formatFullDate.format(it.getStartDate());
	      }
	    };

	    lowDateColumn.setSortable(true);
	    lowDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(lowDateColumn, 90, Unit.PX);
	    
	    TextColumn<IT> lowCauseColumn = new TextColumn<IT>() {

			@Override
			public String getValue(IT it) {
				return parseShortLowCauseByte(it.getTypeLowPart());
			}
			
			@Override
			public void render(Context context, IT it, SafeHtmlBuilder sb) {
				if(null != it) {
					sb.appendHtmlConstant("<span title=\"" + getSelectedTextByValue(causeLowPart, it.getTypeLowPart()) + "\">" + parseShortLowCauseByte(it.getTypeLowPart()) + "</span>");
				}
			}
		};
	    
	    lowCauseColumn.setSortable(true);
	    lowCauseColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(lowCauseColumn, 90, Unit.PX);
	    
	    TextColumn<IT> highDateColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	        return null == it.getEndDate() ? "-" : formatFullDate.format(it.getEndDate());
	      }
	    };

	    highDateColumn.setSortable(true);
	    highDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(highDateColumn, 90, Unit.PX);
		    
	    TextColumn<IT> highCauseColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	    	return getSelectedTextByValue(causeHighPart, it.getTypeHighPart());
	      }
	    };

	    highCauseColumn.setSortable(true);
	    highCauseColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    
	   TextColumn<IT> rechargeColumn = new TextColumn<IT>() {
	      @Override
	      public String getValue(IT it) {
	    	  return it.getParent() == null || it.getParent() == 0 ? "NO" : "SI";
	      }

	    };

	    rechargeColumn.setSortable(true);
	    rechargeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    itDataGrid.setColumnWidth(rechargeColumn, 50, Unit.PX);
	    
	    // Add the columns.
	    itDataGrid.addColumn(lowDateColumn, "F. Baja");
	    itDataGrid.addColumn(lowCauseColumn, "Causa Baja");
	    itDataGrid.addColumn(highDateColumn, "F. Alta");
	    itDataGrid.addColumn(highCauseColumn, "Causa Alta");
	    itDataGrid.addColumn(rechargeColumn, "Rec.");
	      
	}
	
	// --------------------------------------------------- Constructor
	
	public ITDialog(String typeCaption) {	
		String caption = "Parte IT / " + typeCaption;
		onModuleLoad(caption);
		createITToolbar();
		createFooterButtons();
		
	}
	
	private void createEmployeePanel(EmployeeInfo employeeInfo) {
		employeePanel.clear();
		employeePanel.add(new Label(employeeInfo.getFullName()));
		updateEmployeePanel(employeeInfo);
		showListOption();
	}
	
	private void updateEmployeePanel(EmployeeInfo employeeInfo) {
		employeePanelDoc.clear();
		employeePanelNaf.clear();
		employeePanelDoc.add(new Label(employeeInfo.getDocument()));
		employeePanelNaf.add(new Label(employeeInfo.getSsNumber()));
		updateEnterprisePanel();
	}
	
	private void updateEnterprisePanel() {
		enterpriseData.getElement().getStyle().setDisplay(Display.NONE);
		enterpriseData.clear();
		if(itDialogObject!=null) {
			//---ENTERPRISE DATA
			String completeCcc =  itDialogObject.getContractInfo().getCompleteCCC();
			if(completeCcc!=null) {
				String regime = completeCcc.substring(0, 4);
				String ccc = completeCcc.substring(4, completeCcc.length());

				Label regimeEl = new Label("R\u00e9gimen:");
				regimeEl.setStyleName(style.subTitle());
				enterpriseData.add(regimeEl);
				enterpriseData.add(new Label(regime));

				Label cccEl = new Label("CCC:");
				cccEl.setStyleName(style.subTitle());
				enterpriseData.add(cccEl);
				enterpriseData.add(new Label(ccc));
				enterpriseData.getElement().getStyle().clearDisplay();
			}
		}
	}
	
	// --------------------------------------------------- onModuleLoad

	private void onModuleLoad(String caption){
		// ITDataGrid
		provideITDataGrid();
		addStyleToHeader();
	    
		setCaption(caption);
		setWidget(binder.createAndBindUi(this));
		
		initListBox();
		
		deckPanel.showWidget(0);
		deckPanel.setWidth("620px");
		confirmationsDataTable.getElement().getStyle().setDisplay(Display.NONE);
		maternityDataTable.getElement().getStyle().setDisplay(Display.NONE);
		enterpriseData.getElement().getStyle().setDisplay(Display.NONE);

		showBaseCGC();
		
	}
	
	// --------------------------------------------------- onModuleLoad.Methods
	
	private void initListBox(){
		raggedList.clear();
		raggedList.addItem("NO", "0");
		
		causeLowPart.clear();
		causeLowPart.addItem("-", "-1");
		causeLowPart.addItem("Enfermedad Com\u00Fan", "0");
		causeLowPart.addItem("Accidente de trabajo", "1");
		causeLowPart.addItem("Maternidad", "2");
		causeLowPart.addItem("Paternidad", "3");
		causeLowPart.addItem("Riesgo para el embarazo", "4");
		causeLowPart.addItem("Riesgo durante la lactancia", "5");
		causeLowPart.addItem("Menstruaci\u00F3n Incapacitante Secundaria","10");
		causeLowPart.addItem("Interrupci\u00F3n del Embarazo", "11" );
		causeLowPart.addItem("Semana Trig\u00E9sima Novena de Gestaci\u00F3n", "12");
		causeLowPart.addItem("Accidente no laboral", "6");
		causeLowPart.addItem("Enfermedad com\u00Fan periodo de carencia", "7");
		causeLowPart.addItem("Enfermedad com\u00Fan, prestaci\u00F3n profesional (COVID-19)", COVID_IT);		
		setOptionDisabled(causeLowPart, COVID_IT, true);
		
		
		// Uncomment this line when this cause is developed on SalaryDraft
		// causeLowPart.addItem("Periodo de Observaci\u00f3n por Enfermedad Profesional", "9");
		
		causeHighPart.clear();
		causeHighPart.addItem("-", "-1");
		causeHighPart.addItem("Curaci\u00F3n", "0");
		causeHighPart.addItem("Fallecimiento", "1");
		causeHighPart.addItem("Inspecci\u00F3n m\u00e9dica", "2");
		causeHighPart.addItem("Propuesta incapacidad", "3");
		causeHighPart.addItem("Agotamiento de plazo", "4");
		causeHighPart.addItem("Mejor\u00eda que permite realizar el trabajo habitual", "5");
		causeHighPart.addItem("Incomparecencia", "6");
		causeHighPart.addItem("Control INSS duraci\u00F3n 12 meses", "7");
		causeHighPart.addItem("Recuperaci\u00F3n capacidad profesional", COVID_IT);
		causeHighPart.addItem("Incomparecencia contratos de formaci\u00F3n", "9");
		
		applicantTypeList.addItem("Madre biologica", "0");
		applicantTypeList.addItem("Otro progenitor", "1");
		applicantTypeList.addItem("Primer adoptante", "2");
		applicantTypeList.addItem("Segundo adoptante", "3");
	}

	
	// --------------------------------------------------- setITDialogObject
	
	public void setITDialogObject(ITDialogObject itDialogObject) {
		setITDialogObject(itDialogObject, null);
	}

	public void setITDialogObject(ITDialogObject itDialogObject, IT it) {
		this.itDialogObject = itDialogObject;
		
		initRaggedListBox();
		initConfirmationsTable();
		
		// Check if exist IT
		this.itCopy = copyIT(null != it ? it : this.itDialogObject.checkIfIsOpenIt());
		this.it = copyIT(null != it ? it : this.itDialogObject.checkIfIsOpenIt());
		
		checkAndPaintIT();
		
		// Check type of part
		showAdvancedOpts(this.itDialogObject.getEmployeeStatus());
		
		showDialog();
	}

	public void setITDialogObject(ITDialogObject itDialogObject, IT it, boolean showAll) {
		this.itDialogObject = itDialogObject;
		
		initRaggedListBox();
		initConfirmationsTable();
		
		// Check if exist IT
		this.itCopy = copyIT(it);
		this.it = it;
		checkAndPaintIT();
		
		// Check type of part
		showAdvancedOpts(!showAll);
		createEmployeePanel(itDialogObject.getEmployeeinfo());
		
		showDialog();
	}
	
	private IT copyIT(IT originalIT) {
		if(null == originalIT) return null;
		
		IT itCopy = new IT();
		itCopy.setId(originalIT.getId());
		itCopy.setDomain(originalIT.getDomain());
		itCopy.setTypeLowPart(originalIT.getTypeLowPart());
		itCopy.setContract(originalIT.getContract());
		itCopy.setDescription(originalIT.getDescription());
		itCopy.setStartDate(originalIT.getStartDate());
		itCopy.setEndDate(originalIT.getEndDate());
		itCopy.setDailyCGCBase(originalIT.getDailyCGCBase());
		itCopy.setDailyCGPBase(originalIT.getDailyCGPBase());
		itCopy.setParent(originalIT.getParent());
		itCopy.setDailyREGBase(originalIT.getDailyREGBase());
		itCopy.setTypeHighPart(originalIT.getTypeHighPart());
		itCopy.setIsParent(originalIT.getIsParent());
		itCopy.setMaternityType(originalIT.getMaternityType());
		itCopy.setMaternityReason(originalIT.getMaternityReason());
		itCopy.setRegulationBase(originalIT.getRegulationBase());
		itCopy.setPartialityCoef(originalIT.getPartialityCoef());
		itCopy.setQuoteDays(originalIT.getQuoteDays());
		itCopy.setComunicationDate(originalIT.getComunicationDate());
		itCopy.setIsComunicate(originalIT.isComunicate());
		itCopy.setDirectPayDate(originalIT.getDirectPayDate());
		itCopy.setFullName(originalIT.getFullName());
		itCopy.setContractStartDate(originalIT.getContractStartDate());
		itCopy.setContractEndDate(originalIT.getContractEndDate());
		itCopy.setITParts(originalIT.getITParts());
		
		return itCopy;
	}
	
	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	// --------------------------------------------------- setITDialogObject.Methods
	
	private void initRaggedListBox(){
		raggedList.clear();
		raggedList.addItem("NO", "0");
		
		for(IT it : itDialogObject.getITList()) {
			if(null != it.getEndDate() && notSelectedId(it.getId())) {
				String item = parseShortLowCauseByte(it.getTypeLowPart()) + " (" + formatFullDate.format(it.getStartDate()) + " - " + formatFullDate.format(it.getEndDate()) + ")";
				raggedList.addItem(item, it.getId().toString());
			}
		}
	}
	
	private void showAdvancedOpts(boolean showAll) {
		if(showAll) {
			hideConfirmationParts();
			hideMaternityTable();
		} else {
			if(isPartenityPart(this.it)) {
				showMaternityTable();
				hideConfirmationParts();
			} else {
				hideMaternityTable();
				showConfirmationParts();
			}
		}
		
		//PRINT BTN IT COMUNICA
		if(this.it!=null && this.userComunica)
			printBtnCommunicate();
		
	}
	
	// --------------------------------------------------- PaintIt
	
	private void checkAndPaintIT() {
		if(isNotEmptyIT()) {
			paintSelectedIT(this.it, false);
			showDeleteOption();
			if(null != this.it.isComunicate() && this.it.isComunicate())
				showCertificate.setVisible(true);
		} else
			hideDeleteOption();
	}
	
	private void paintSelectedIT(IT it, boolean showAll) {
		normalizeITToPaint(it);
		itStartDate.setValue(it.getStartDate());
		setSelectedValueLB(causeLowPart, it.getTypeLowPart().toString());
		setOptionDisabled(causeLowPart, COVID_IT, it.getStartDate().after(COVID_END_DATE) );
		
		itEndDate.setValue(it.getEndDate());
		setSelectedValueLB(causeHighPart, null == it.getTypeHighPart() ? "-1" : it.getTypeHighPart().toString());
		
		setSelectedValueLB(raggedList, it.getParent().toString());
		
		observationTB.setValue(it.getDescription());
		
		directPayDate.setValue(it.getDirectPayDate());
		
		initConfirmationsTable();
		
		for(ITPart itPart : it.getITParts()) {
			switch (itPart.getType()) {
				case (byte)0:
					collegiateNumberITPart.setValue(itPart.getCollegeNumber());
					ciasITPart.setValue(itPart.getCias());
					continue;
				case (byte)2:
					continue;
				default:
					addConfirmationITPart(itPart);
					continue;
			}
		}
		
		calculateScrollPanelHeight();
		
		createRealStartDate();
		
		if(isPartenityPart(it)) {
			showMaternityTable();
			setSelectedValueLB(applicantTypeList, null == it.getMaternityType() ? "-1" : it.getMaternityType().toString());
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), applicantTypeList);
			setSelectedValueLB(applicantReasonList, null == it.getMaternityReason() ? "-1" : it.getMaternityReason().toString());
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), applicantReasonList);
			this.baseRDBx.setValue(it.getRegulationBase());
			this.partialityCoefDBx.setValue(it.getPartialityCoef());
		} else 
			this.baseRDBxN.setValue(it.getRegulationBase());
		
		if(showAll) {
			informationDataTable.getElement().getStyle().clearDisplay();
			itDataTable.getElement().getStyle().clearDisplay();
			deleteIT.setVisible(true);
			if(isPartenityPart(it)) {
				showMaternityTable();
			} else
				showConfirmationParts();
		}
		
		deckPanel.showWidget(0);
		deckPanel.setWidth("620px");
	}

	// --------------------------------------------------- setEmployeesList (on new it)
	
	public void setEmployeesList(List<ITEmployee> itEmployeeListIn) {
		itEmployeeList = Collections.emptyList();
		itEmployeeList = itEmployeeListIn;
		
		SuggestBox employeeSB = new SuggestBox(names);
		employeeSB.setStyleName("aon-inputText");
		employeeSB.getElement().getStyle().setWidth(99, Unit.PCT);
		employeeSB.setAutoSelectEnabled(true);
		
		names.clear();
		
		for(ITEmployee itEmployee : this.itEmployeeList) 
			names.add(itEmployee.getEmployeeInfo().getFullName());
		
		employeeSB.addSelectionHandler(e -> {
			
			itEmployee = getITEmployee(employeeSB.getValue());
			setITDialogObject(new ITDialogObject(itEmployee));

			if(itDialogObject != null) {
				updateEmployeePanel(itEmployee.getEmployeeInfo());
				showListOption();
			}
			
		});
		
		employeePanel.clear();
		employeePanel.add(employeeSB);
	}
	
	public void setITEmployee(ITEmployee itEmployee) {
		this.itEmployee = itEmployee;
		setITDialogObject(new ITDialogObject(itEmployee));
		createEmployeePanel(itEmployee.getEmployeeInfo());
	}
	
	private ITEmployee getITEmployee(String employeeName) {
		for(ITEmployee itEmployee : this.itEmployeeList) {
			if(AonStringUtils.equalsIgnoreCase(itEmployee.getEmployeeInfo().getFullName(), employeeName))
				return itEmployee;
		}
		return null;
	}
	
	// --------------------------------------------------- Initialize IT Table
	
	private void initITTable() {		
		// Create a data provider.
	    ListDataProvider<IT> dataProvider = new ListDataProvider<IT>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(itDataGrid);
	    
	    // Add the data to the data provider, which automatically pushes it to the
	    // widget.
	    List<IT> itListAux = dataProvider.getList();
	    itListAux.clear();
	    
	    this.itList = itDialogObject.getITList();
	    
	    for (IT it : this.itList) {
	    	itListAux.add(it);
	    } 
	    
	    // Set page size
	    itDataGrid.setPageSize(itList.size());
	    
	    // Add style to table header
	    addStyleToHeader();
	    
	    addSortColums(itListAux); 
	}
	
	private void addSortColums(List<IT> itList) {
		ListHandler<IT> columnSortHandler = new ListHandler<IT>(itList);
		
		columnSortHandler.setComparator(itDataGrid.getColumn(0), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getStartDate().compareTo(o2.getStartDate()) : 1;
		            }
		            
		            return -1;
	          }
	    });
		
	    columnSortHandler.setComparator(itDataGrid.getColumn(1), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getTypeLowPart().compareTo(o2.getTypeLowPart()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(2), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getEndDate().compareTo(o2.getEndDate()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(3), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getTypeHighPart().compareTo(o2.getTypeHighPart()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    
	    columnSortHandler.setComparator(itDataGrid.getColumn(4), new Comparator<IT>() {
	          public int compare(IT o1, IT o2) {
		            if (o1 == o2) {
		              return 0;
		            }
	
		            if (o1 != null) {
		              return (o2 != null) ? o1.getParent().compareTo(o2.getParent()) : 1;
		            }
		            
		            return -1;
	          }
	    });
	    
	    
	    itDataGrid.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    itDataGrid.getColumn(0).setDefaultSortAscending(false);
	    itDataGrid.getColumnSortList().push(itDataGrid.getColumn(0));   
	}
	
	// --------------------------------------------------- Header Styles IT Table
	
	public void addStyleToHeader() {
		itDataGrid.getHeader(0).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(1).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(2).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(3).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
		itDataGrid.getHeader(4).setHeaderStyleNames("rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header");
	}
	
	// --------------------------------------------------- UiHandlers
	
	@UiHandler("itStartDate")
	public void onItStartDateChange(ValueChangeEvent<Date> event) {
		checkAndCreateIT();
		Date startDate = event.getValue();
		
		this.it.setStartDate(startDate);
		setDateLowPart(startDate);
		
		createRealStartDate();
		setDirectPayDate();
		showConfirmationParts();
		
		setOptionDisabled(causeLowPart, COVID_IT, startDate.after(COVID_END_DATE));
	}
	
	@UiHandler("causeLowPart")
	public void onCauseLowPartChange(ChangeEvent event) {
		checkAndCreateIT();
		
		this.it.setTypeLowPart(Byte.parseByte(causeLowPart.getSelectedValue()));
		
		setCauseLowPart();
		createRealStartDate();
		
		Byte causeLowPartB = Byte.parseByte(causeLowPart.getSelectedValue());
		if(causeLowPartB == (byte)2 || causeLowPartB == (byte)3) {
			checkConfirmationParts();
			showMaternityTable();
			hideConfirmationParts();
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), applicantTypeList);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), applicantReasonList);
		}else {
			hideMaternityTable();
			showConfirmationParts();
		}
	}
	
	@UiHandler("itEndDate")
	public void onItEndDateChange(ValueChangeEvent<Date> event) {
		checkAndCreateIT();
		
		this.it.setEndDate(event.getValue());
		
		setDateHighPart(event.getValue());
	}
	
	@UiHandler("causeHighPart")
	public void onCauseHighPartChange(ChangeEvent event) {
		checkAndCreateIT();
		
		this.it.setTypeHighPart(Byte.parseByte(causeHighPart.getSelectedValue()));
		
		setCauseHighPart();
	}
	
	@UiHandler("collegiateNumberITPart")
	public void onCollegiateNumberLowPartChange(ValueChangeEvent<String> event) {
		checkAndCreateIT();
		
		setCollegiateNumberITPart(event.getValue());
	}
	
	@UiHandler("ciasITPart")
	public void onCiasLowPartChange(ValueChangeEvent<String> event) {
		checkAndCreateIT();
		
		setCiasITPart(event.getValue());
	}
	
	@UiHandler("observationTB")
	public void onObservationTBChange(ValueChangeEvent<String> event) {
		checkAndCreateIT();
		
		this.it.setDescription(observationTB.getValue());
	}
	
	@UiHandler("directPayDate")
	public void onDirectPayDateChange(ValueChangeEvent<Date> ev) {
		checkAndCreateIT();
		
		this.it.setDirectPayDate(directPayDate.getValue());
	}
	
	@UiHandler("raggedList")
	public void onRaggedListChange(ChangeEvent ev) {
		checkAndCreateIT();
		
		this.it.setParent(Integer.parseInt(raggedList.getSelectedValue()));
		
		Date raggedDate = this.itDialogObject.getRaggedDate(raggedList.getSelectedValue());
		
		if(null != raggedDate)
			realStartDate.setText(formatFullDate.format(raggedDate));
		else {
			if(null == itStartDate.getValue())
				realStartDate.setText("");
			else
				createRealStartDate();
		}		
	}
	
	@UiHandler("applicantTypeList")
	public void onApplicantTypeListChange(ChangeEvent ev) {
		createApplicantReasonList();
		this.it.setMaternityType(Byte.parseByte(applicantTypeList.getSelectedValue()));
	}
	
	@UiHandler("applicantReasonList")
	public void onApplicantReasonListChange(ChangeEvent ev) {
		LOGGER.info(applicantReasonList.getSelectedItemText()+" "+applicantReasonList.getSelectedValue());
		this.it.setMaternityReason(Byte.parseByte(applicantReasonList.getSelectedValue()));
	}
	
	@UiHandler("baseRDBx")
	public void onBaseRDBxChange(ValueChangeEvent<Double> ev) {
		this.it.setRegulationBase(ev.getValue());
		this.it.setDailyCGCBase(ev.getValue());
		this.it.setDailyCGPBase(ev.getValue());
	}
	
	@UiHandler("baseRDBxN")
	public void onBaseRDBxNChange(ValueChangeEvent<Double> ev) {
		this.it.setRegulationBase(ev.getValue());
		this.it.setDailyCGCBase(ev.getValue());
		this.it.setDailyCGPBase(ev.getValue());
	}
	
	@UiHandler("partialityCoefDBx")
	public void onPartialityCoefDBxChange(ValueChangeEvent<Double> ev) {
		this.it.setPartialityCoef(ev.getValue());
	}
	
	// --------------------------------------------------- UiHandlers.Methods

	private void checkAndCreateIT() {
		if(this.it == null) {
			this.it = new IT();
			this.it.setId(-1);
		}
	}

	private void checkConfirmationParts() {
		List<ITPart> newITParts = new ArrayList<>();
		
		for(ITPart itPart : this.it.getITParts()) {
			if(itPart.getType() == (byte)1 || itPart.getType().equals((byte)1))
				continue;
			newITParts.add(itPart);
		}
		
		this.it.setITParts(newITParts);
	}

	private void createApplicantReasonList() {
		Integer selectedIdx = applicantTypeList.getSelectedIndex();
		applicantReasonList.clear();
		switch (selectedIdx) {
		case 1:
			applicantReasonList.addItem("Nacimiento de hijo", "0");
			applicantReasonList.addItem("Parto multiple", "3");
			break;
		case 2:
			applicantReasonList.addItem("Adopcion/Tutela/Acogimiento", "5");
			break;
		case 3:
			applicantReasonList.addItem("Adopcion/Tutela/Acogimiento", "5");
			break;
		default:
			applicantReasonList.addItem("Nacimiento de hijo", "0");
			applicantReasonList.addItem("Fallecimiento de la madre", "1");
			applicantReasonList.addItem("Cesion/Opcion en favor del otro progenitor", "2");
			applicantReasonList.addItem("Parto multiple", "3");
			applicantReasonList.addItem("Inicio del descanso antes del parto (solo para madre biologica ET)", "4");
			break;
		}
	}
	
	// --------------------------------------------------- IT.SetterMethods

	private void setDateLowPart(Date date) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<>();
			
			ITPart itPart = new ITPart()
			.setType((byte) 0) // BAJA
			.setDate(date);
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 0) {
					itPart.setDate(date);
				}
			}
		}
	}
	
	private void setCauseLowPart() {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<>();
			
			ITPart itPart = new ITPart();
			itPart.setType((byte) 0); // BAJA
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		}
	}
	
	private void setDateHighPart(Date date) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<>();
			
			ITPart itPart = new ITPart()
			.setType((byte) 2) // ALTA
			.setDate(date)
			.setCollegeNumber(collegiateNumberITPart.getValue())
			.setCias(ciasITPart.getValue());
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			boolean added = false;
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 2) {
					itPart.setDate(date);
					itPart.setCollegeNumber(collegiateNumberITPart.getValue());
					itPart.setCias(ciasITPart.getValue());
					added = true;
				}
			}
			if(!added) {
				ITPart itPart = new ITPart()
				.setType((byte) 2) // ALTA
				.setDate(date)
				.setCollegeNumber(collegiateNumberITPart.getValue())
				.setCias(ciasITPart.getValue());
				
				this.it.addITPart(itPart);
			}
		}
	}
	
	private void setCauseHighPart() {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<>();
			itParts.add( 
				new ITPart()
				.setType((byte) 2) // ALTA
				.setCollegeNumber(collegiateNumberITPart.getValue())
				.setCias(ciasITPart.getValue())
			);
			this.it.setITParts(itParts);
		} else {
			boolean added = false;
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 2) {				    
				    added = true;
				}
			}
			if(!added) {
				ITPart itPart = new ITPart();
				itPart.setType((byte) 2); // ALTA
				itPart.setCollegeNumber(collegiateNumberITPart.getValue());
				itPart.setCias(ciasITPart.getValue());

				this.it.addITPart(itPart);
			}
		}
	}
	
	private void setCollegiateNumberITPart(String collegiateNumberLowPart) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<>();
			itParts.add(
					new ITPart()
					.setType((byte) 0) // BAJA
					.setCollegeNumber(collegiateNumberLowPart)
					.setDate(itStartDate.getValue())
			);
			this.it.setITParts(itParts);
		} else {
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 0 || itPart.getType() == (byte) 2) 
					itPart.setCollegeNumber(collegiateNumberLowPart);
			}
		}
	}
	
	private void setCiasITPart(String ciasLowPart) {
		if(this.it.getITParts().isEmpty()) {
			List<ITPart> itParts = new ArrayList<>();
			
			ITPart itPart = new ITPart()
			.setType((byte) 0) // BAJA
			.setCias(ciasLowPart)
			.setDate(itStartDate.getValue());
			
			itParts.add(itPart);
			
			this.it.setITParts(itParts);
		} else {
			for(ITPart itPart : this.it.getITParts()) {
				if(itPart.getType() == (byte) 0 || itPart.getType() == (byte) 2) {				    
				    itPart.setCias(ciasLowPart);
				}
			}
		}
	}
	
	private void setDirectPayDate() {
		Date date = itStartDate.getValue();
		if(null != date) {
			date = DateUtils.addDays2Date(date, 365);
			directPayDate.setValue(date);
			this.it.setDirectPayDate(date);
		}
	}

	private void createRealStartDate() {
		Date date = itStartDate.getValue();
		if(null != date) {
			if(null != this.it.getParent() && 0 != this.it.getParent()) {
				Date oldStartDate = itDialogObject.getRaggedDate(this.it.getParent().toString());
				realStartDate.setText(formatFullDate.format(oldStartDate));
				return;
			} else {
			
				if( (byte) 1 == Byte.parseByte(causeLowPart.getSelectedValue()) ||
					(byte) 8 == Byte.parseByte(causeLowPart.getSelectedValue())) {
					date = DateUtils.addDays2Date(date, 1);
					realStartDate.setText(formatFullDate.format(date));
				} else
					realStartDate.setText(formatFullDate.format(date));
			}
		}
	}

	// --------------------------------------------------- ConfirmationParts
	
	public void initConfirmationsTable() {
		confirmationPartDataTableHeader.clear();
		confirmationPartDataTableHeader.resize(0, 0);
		confirmationPartDataTableHeader.resizeColumns(7);
		confirmationPartDataTable.clear();
		confirmationPartDataTable.resize(0, 0);
		confirmationPartDataTable.resizeColumns(7);
		paintHeader();
		initFooterConfirmationParts();
		calculateScrollPanelHeight();
		setColumnWidth();
		center();
	}
	
	
	private void paintHeader() {
		int row = confirmationPartDataTableHeader.insertRow(confirmationPartDataTableHeader.getRowCount());
		Label orderNumber = new Label("N" + String.valueOf("\u00B0") + " DE ORDEN");
		Label date = new Label("FECHA");
		Label collegeNumber = new Label("N" + String.valueOf("\u00B0") + " COLEGIADO");
		Label cias = new Label("CIAS");
		Label blank = new Label("");
		
		orderNumber.addStyleName(style.headerStyle());
		date.addStyleName(style.headerStyle());
		collegeNumber.addStyleName(style.headerStyle());
		cias.addStyleName(style.headerStyle());
		blank.addStyleName(style.headerStyle());
		
		confirmationPartDataTableHeader.setWidget(row, 0, orderNumber);
		confirmationPartDataTableHeader.setWidget(row, 1, date);
		confirmationPartDataTableHeader.setWidget(row, 2, collegeNumber);
		confirmationPartDataTableHeader.setWidget(row, 3, cias);
		confirmationPartDataTableHeader.setWidget(row, 4, blank);
		confirmationPartDataTableHeader.setWidget(row, 5, blank);
		confirmationPartDataTableHeader.setWidget(row, 6, blank);
	}
	
	private void initFooterConfirmationParts() {
		footerOptionsToolbar.clear();
		
		AonTableButton newConfirmationPart = new AonTableButton("Nuevo parte de confirmaci\u00F3n",  AON.CSS.aonIconAdd());
		newConfirmationPart.addClickHandler(e -> {
			onNewConfirmationPart(e);
		});
		
		Label confirmationLabel = new Label("Parte de confirmaci\u00F3n");
		
		footerOptionsToolbar.add(newConfirmationPart);
		footerOptionsToolbar.add(confirmationLabel);
	}
	
	private void calculateScrollPanelHeight() {
	    int rows = confirmationPartDataTable.getRowCount();
		Integer height = 100;
		Integer extra = 30;
		Integer newHeight = 0;
		
		if(rows < 4) {
			int mod = rows%4;
			newHeight = mod*extra+extra;
		}else {
			int div = rows/4;
			int mod = rows%4;
			if(div < 2)
				newHeight = (height*div)+(extra*mod)+extra;
			else
				newHeight = 220;
		}
		
		if(newHeight > 100) {		    
		    newHeight = 95;
		}
		
		scrollPanel.setHeight(newHeight + "px");
	}
	
	private void setColumnWidth() {
		confirmationPartDataTableHeader.getCellFormatter().addStyleName(0, 0, style.columnWidth());
		confirmationPartDataTableHeader.getCellFormatter().addStyleName(0, 1, style.columnWidth());
		confirmationPartDataTableHeader.getCellFormatter().addStyleName(0, 2, style.columnWidth());
		confirmationPartDataTableHeader.getCellFormatter().addStyleName(0, 3, style.columnWidth());
		
		confirmationPartDataTable.getColumnFormatter().addStyleName(0, style.columnWidth());
		confirmationPartDataTable.getColumnFormatter().addStyleName(1, style.columnWidth());
		confirmationPartDataTable.getColumnFormatter().addStyleName(2, style.columnWidth());
		confirmationPartDataTable.getColumnFormatter().addStyleName(3, style.columnWidth());
	}
	
	// --------------------------------------------------- ITDIalog.Methods
	
	private void addConfirmationITPart(ITPart itPart) {

		int row = confirmationPartDataTable.insertRow(confirmationPartDataTable.getRowCount());
		
		TextBox orderNumberTB = new TextBox();
		orderNumberTB.setStyleName("aon-inputText");
		orderNumberTB.getElement().getStyle().setWidth(50, Unit.PX);
		orderNumberTB.setText(null == itPart.getConfirmOrderNumber() ? "" : itPart.getConfirmOrderNumber().toString());
		
		orderNumberTB.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				Byte value = Byte.parseByte(event.getValue());
				itPart.setConfirmOrderNumber(value);
			}
		});
		
		DateBoxEx dateBox = new DateBoxEx();
		dateBox.setValue(itPart.getDate());
		
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				itPart.setDate(event.getValue());
			}
		});

		TextBox collegeNumberTB = new TextBox();
		collegeNumberTB.setStyleName("aon-inputText");
		collegeNumberTB.getElement().getStyle().setWidth(80, Unit.PX);
		collegeNumberTB.setText(itPart.getCollegeNumber());
		
		collegeNumberTB.addKeyPressHandler(event -> {
			char key = event.getCharCode();
			// Ignorar el evento si no es un número
			if (!Character.isDigit(key))
			event.preventDefault();
		});
		
		collegeNumberTB.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String collegeNumber = event.getValue();
				collegeNumber = collegeNumber.replaceAll("[^0-9]", "");
				itPart.setCollegeNumber(collegeNumber);
				collegeNumberTB.setValue(collegeNumber, false);
			}
		});
		
		TextBox ciasTB = new TextBox();
		ciasTB.setStyleName("aon-inputText");
		ciasTB.getElement().getStyle().setWidth(80, Unit.PX);
		ciasTB.setText(itPart.getCias());
		
		ciasTB.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				itPart.setCias(event.getValue());
			}
		});
		
		AonTableButton deleteBTN = new AonTableButton("Eliminar", AON.CSS.aonIconDelete());
		deleteBTN.addClickHandler((e) -> {
			if(itPart.getIt() != null) {
				itPart.setDelete(true);
//				itDialogObject.deleteConfirmationPart(itPart.getIt(), itPart);
				confirmationPartDataTable.getRowFormatter().setVisible(row, false);
			}
		});
		
		confirmationPartDataTable.setWidget(row, 0, orderNumberTB);
		confirmationPartDataTable.setWidget(row, 1, dateBox);
		confirmationPartDataTable.setWidget(row, 2, collegeNumberTB);
		confirmationPartDataTable.setWidget(row, 3, ciasTB);
		confirmationPartDataTable.setWidget(row, 4, deleteBTN);
		
		if(this.userComunica) {
			Date checkDate = new Date(2023 - 1900, 3, 1);
			if(new Date().before(checkDate))
				buildBtnPart(itPart).ifPresent(btn->
				     confirmationPartDataTable.setWidget(row, 5, btn)
				);
			
			buildBtnPartPdf(itPart).ifPresent(btn->
			    confirmationPartDataTable.setWidget(row, 6, btn)
			);
		}
	}
	
	// --------------------------------------------------- ITDIalog.ShowHide_Elements
	
	private void showListOption() {
		listIT.setVisible(true);
	}
	
	private void hideDeleteOption() {
		deleteIT.setVisible(false);
	}
	
	private void showDeleteOption() {
		deleteIT.setVisible(true);
	}
	
	private void hideConfirmationParts() {
		this.confirmationsDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void showConfirmationParts() {
		this.confirmationsDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void showMaternityTable() {
		this.maternityDataTable.getElement().getStyle().clearDisplay();
		hideBaseCGC();
	}
	
	private void hideMaternityTable() {
		this.maternityDataTable.getElement().getStyle().setDisplay(Display.NONE);
		showBaseCGC();
	}
	
	// --------------------------------------------------- ITDIalog.Auxiliar_Methods
	
	public void setIsUserComunica(boolean userComunica) {
		this.userComunica = userComunica;
	}
	
	private boolean notSelectedId(Integer itId) {
		return (null == this.it || null == this.it.getId()) ? true : (this.it.getId() == itId || this.it.getId().equals(itId));
	}

	private boolean isPaternity() {
		return it.getTypeLowPart()!=null && (it.getTypeLowPart() == (byte)2 || it.getTypeLowPart() == (byte)3);
	}
	
	private String parseShortLowCauseByte(Byte typeLowPart) {
		switch (typeLowPart) {
			case (byte)0:
				return "ECC";
			case (byte)1:
				return "ATT";
			case (byte)2:
				return "MAT";
			case (byte)3:
				return "PAT";
			case (byte)4:
				return "REM";
			case (byte)5:
				return "RLA";
			case (byte)6:
				return "ANL";
			case (byte)7:
				return "ECC";
			case (byte)8:
				return "COV";
			case (byte)9:
				return "OEP";
			default:
				return "-";
		}
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
	
	private String getSelectedTextByValue(ListBox lBox, Byte value) {
		if(value!=null) {
		    String text = value.toString();
		    for (int i = 0; i < lBox.getItemCount(); i++) {
		        if (lBox.getValue(i).equals(text)) 
		            return lBox.getSelectedItemText();
		    }
		}
	    return "-";
	}
	
	
	// --------------------------------------------------- Toolbar
	
	private void createITToolbar() {
		north.add(getToolbarPanel());
		north.setHeight(AonToolbar.HEIGTH + "px");
	}
	
	private AonToolbar getToolbarPanel() {
		toolbarDetail = new AonToolbar("Baja IT");
		
		listIT = new AonToolbarButton("Listar ITs", AON.CSS.aonIconList() );
		listIT.setAccessKey('L');
		listIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onListIT(event);
			}
		});
		toolbarDetail.add(listIT);
		
		backListIT = new AonToolbarButton( "Volver a ITs", AON.CSS.aonIconBack() );
		backListIT.setAccessKey('B');
		backListIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onBackListIT(event);
				toolbarDetail.setTitle("Baja IT");
			}
		});
		toolbarDetail.add(backListIT);
		
		newIT = new AonToolbarButton( "Nueva IT", AON.CSS.aonIconAdd() );
		newIT.setAccessKey('N');
		newIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onNewIT(event);
			}
		});
		toolbarDetail.add(newIT);
		
		deleteIT = new AonToolbarButton( "Borrar IT", AON.CSS.aonIconDelete() );
		deleteIT.setAccessKey('D');
		deleteIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDeleteIT(event);
			}
		});
		toolbarDetail.add(deleteIT);
		
		showCertificate = new AonToolbarButton( "Certificado IT", AON.CSS.aonIconPdf());
		showCertificate.setAccessKey('C');
		showCertificate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onShowCertitificateIT(event);
			}
		});
		toolbarDetail.add(showCertificate);
		showCertificate.setVisible(false);

		backListIT.setVisible(false);
		newIT.setVisible(false);
		deleteIT.setVisible(false);

		return toolbarDetail;

	}
	
	// --------------------------------------------------- Toolbar.Methods
	
	private void onDeleteIT(ClickEvent event) {
		if(null != this.it.getId()) {
			if(this.it.getIsParent()) {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.info("AVISO: Reca\u00edda", "No se puede eliminar este parte por que tiene reca\\u00edda.");
			} else {
				AonConfirmDialog confirmDialog = new AonConfirmDialog();
				confirmDialog.confirm(
						"BORRADO", 
						String.valueOf("\u00BF") + "Realmente desea eliminar el parte IT?",
						new AonConfirmDialogCallback() {

							@Override
							public void onAccept() {
								onDelete(it);
								hide();
								ITDialog.this.hide();
							}
							
							public void onCancel() {}
						}
				);
			}
		}
	}
	
	private void onShowCertitificateIT(ClickEvent event) {
		if(null != this.it.getId()) {		    
		    onShowCertitificateIT(it);
		}
		
		hide();
	}
	
	private void onListIT(ClickEvent event) {
		// Check type of part
		newIT.setVisible(this.itDialogObject.getEmployeeStatus());
		
		backListIT.setVisible(true);
		listIT.setVisible(false);
		deleteIT.setVisible(false);
		
		deckPanel.showWidget(1);
		deckPanel.setWidth("620px");
		initITTable();
		setTableHeights();
	}
	
	private void setTableHeights() {
		itDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		mainTablePanel.getElement().getStyle().setHeight((Window.getClientHeight() - 400), Unit.PX);
	}
	
	private void onBackListIT(ClickEvent event) {
		deckPanel.showWidget(0);
		deckPanel.setWidth("620px");
		
		backListIT.setVisible(false);
		newIT.setVisible(false);
		deleteIT.setVisible(false);
		listIT.setVisible(true);
		createFooterButtons();
	}
	
	private void onNewIT(ClickEvent event) {
		clearITPage();
		this.it = new IT();
		this.it.setId(-1);
		hideConfirmationParts();
		deckPanel.showWidget(0);
		deckPanel.setWidth("620px");
		
		backListIT.setVisible(false);
		newIT.setVisible(false);
		deleteIT.setVisible(false);
		listIT.setVisible(true);
	}
	
	private void clearITPage() {
		this.raggedList.setSelectedIndex(0);
		
		this.itStartDate.setValue(null);
		this.collegiateNumberITPart.setText("");
		this.causeLowPart.setSelectedIndex(0);
		this.ciasITPart.setText("");
		
		this.itEndDate.setValue(null);
		this.causeHighPart.setSelectedIndex(0);
		
		this.realStartDate.setText("");
		this.directPayDate.setValue(null);
		this.observationTB.setText("");
		
		this.confirmationPartDataTable.clear();

		setOptionDisabled(causeLowPart, COVID_IT, true);
	}
	
	//--------------COMMUNICATE IT PART
	private void printBtnCommunicate(){
		for(ITPart part : it.getITParts()) {			
			LOGGER.info("PART: "+part.toString());
		}
		
		if(itDialogObject!=null) {    //---ENTERPRISE DATA
			String completeCcc =  itDialogObject.getContractInfo().getCompleteCCC();
			
			if(completeCcc!=null) {
				Optional<ITPart> bjOptional = this.itDialogObject.getITBaja(it);
				
				bjOptional.ifPresent(part-> {
				    buildBtnPart(part).ifPresent(btn-> itBaja.add(btn) );
				    buildBtnPartPdf(part).ifPresent(btn->itBaja.add(btn) );
				});
				
			 
				
				if(!isPaternity()) {
					Optional<ITPart> altaOptional = this.itDialogObject.getITAlta(it);
					altaOptional.ifPresent(part-> {
						Date checkDate = new Date(2023 - 1900, 3, 1);
						if(new Date().before(checkDate))
							buildBtnPart(part).ifPresent(btn-> itAlta.add(btn) );
					    buildBtnPartPdf(part).ifPresent(btn->itAlta.add(btn) );
					});
				}
			}
		}
	}
	
	private Optional<AonTableButton> buildBtnPart(ITPart part) {
		if(it.getId()!=null && part.getId()!=null) {
			boolean communicated = isCommunicatePart(part);
			String title = communicated ? "Borrar Parte IT comunicada" : "Comunicar parte";
			String icon  = communicated ? AON.CSS.aonIconSendCancel()  : AON.CSS.aonIconSend();
		
			AonTableButton btn = new AonTableButton(title, icon); 
			btn.addClickHandler(e-> {
				if(communicated) {
					removeITPartTGSS(part);
				} else {
					setViewPartComunica(part);
				}
			});
			return Optional.of(btn);
		}
		return Optional.empty();
	}
	
   private Optional<AonTableButton> buildBtnPartPdf(ITPart part) {
        if(it.getId()!=null && part.getId()!=null && isCommunicatePart(part)) {
            AonTableButton btn = new AonTableButton("Reporte PARTE", AON.CSS.aonIconPdf()); 
            btn.addClickHandler(e-> {
                getITReport(it, part);
            });
            return Optional.of(btn);
        }
        return Optional.empty();
    }
	
	private void removeITPartTGSS(ITPart part) {
		LOGGER.info(part.toString());
		onRemoveITPartTGSS(parseITByStatus(part));
	}
	
	private ItNotExist parseITByStatus(ITPart part) {
		if(itDialogObject!=null) {
			//---ENTERPRISE DATA
			String completeCcc =  itDialogObject.getContractInfo().getCompleteCCC();
			if(completeCcc!=null) {
				String regime = completeCcc.substring(0, 4);
				String ccc = completeCcc.substring(4, completeCcc.length());
			
				EmployeeInfo employeeInfo = itDialogObject.getEmployeeinfo();

			 	EmployeeIT employeeIT = new EmployeeIT()
			 			.setRegime(regime)
			 			.setCcc(ccc)
			 			.setNss(employeeInfo.getSsNumber())
						.setDni(employeeInfo.getDocument())
						.setStartDate(it.getStartDate())  // fecha de baja
						.setType(ContractLeaveType.safeValueOf(it.getTypeLowPart()));
				if(it.getEndDate()!=null) 
					employeeIT.setEndDate(it.getEndDate());
			
			 	EmployeeITPart employeeITPart = new EmployeeITPart()
			 			.setDate(part.getDate())
			 			.setType(ContractLeaveDetailType.safeValueOf(part.getType()));
			 	
			 	ItNotExist ItNotExist = new ItNotExist();
				ItNotExist.setEmployeeIT(employeeIT);
			 	ItNotExist.setEmployeeITPart(employeeITPart);
			 	return ItNotExist;
			} else {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.info("AVISO: Campos requeridos", "CCC es requerido");
			}
	 	}
		return null;
	}
	
	private void onNewConfirmationPart(ClickEvent e) {
		checkAndCreateIT();
		
		ITPart newITPart = new ITPart();
		newITPart.setType((byte)1);
		newITPart.setIt(this.it.getId());
		newITPart.setConfirmOrderNumber(getDefaultConfirmOrder());
		newITPart.setCollegeNumber(collegiateNumberITPart.getValue());
		newITPart.setCias(ciasITPart.getValue());
		this.itDialogObject.addITPart(this.it, newITPart);
		addConfirmationITPart(newITPart);
		calculateScrollPanelHeight();
	}

	private Byte getDefaultConfirmOrder() {
		int newConfirmOrder = 1;
		
		for(ITPart itPart : this.it.getITParts()) {
			if(itPart.getType() == (byte)0 || itPart.getType() == (byte)2)
				continue;
			
			newConfirmOrder++;
		}
		return (byte) newConfirmOrder;
	}
	
	private void showBaseCGC() {
		baseRDBxNL.getElement().getStyle().clearDisplay();
		baseRDBxN.getElement().getStyle().clearDisplay();
	}
	
	private void hideBaseCGC() {
		baseRDBxNL.getElement().getStyle().setDisplay(Display.NONE);
		baseRDBxN.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	// --------------------------------------------------- NormalizeIT.Methods
	protected void normalizeITToSave() {
		if((byte) 1 == Byte.parseByte(causeLowPart.getSelectedValue())) {
			Date realStartDate = DateUtils.copyDateOnly(this.it.getStartDate());
			
			ITPart lowPart = getLowPart(this.it);
			lowPart.setDate(DateUtils.copyDateOnly(realStartDate));
			
			this.it.setStartDate(DateUtils.addDays2Date(realStartDate, 1));
		}
	}
	
	private void normalizeITToPaint(IT it) {
		if( (byte) 1 == it.getTypeLowPart()) {
			Date realStartDate = DateUtils.copyDateOnly(this.it.getStartDate());
			it.setStartDate(DateUtils.addDays2Date(realStartDate, -1));
		}
	}
	
	private ITPart getLowPart(IT it) {
		for(ITPart itPart : it.getITParts())
			if(itPart.getType() == (byte)0)
				return itPart;
		return null;
	}

	
	// --------------------------------------------------- Footer
	private void createFooterButtonsCommunicate() {
		HTMLPanel panel = new HTMLPanel("");
		panel.setStyleName(style.buttonsPanel());
		mainCommunicate.add(panel);
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCloseDialog(event);
			}
		});
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		panel.add(closeBtnDialog);
		
		Button communicateDialog = new Button();
		communicateDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		communicateDialog.setText("Comunicar");
		communicateDialog.setAccessKey('A');
		communicateDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
                sendItPart();
			}
		});
		
		panel.add(communicateDialog);
	}
	
	protected void startLoading(boolean load) {
		if(load) 
			loading.show();
		else 
			loading.hide();
	}
	
	private void createFooterButtons() {
		buttonsPanel.clear();
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCloseDialog(event);
			}
		});
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.setAccessKey('A');
		acceptBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAcceptDialog(event);
			}
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		normalizeITToSave();
		this.it = this.itCopy;
		hide();
	}
	
	private void onAcceptDialog(ClickEvent event) {
		if(checkIfSaveIsPossible()) {
			if(null == this.it.getId() || -1 == this.it.getId())
				itDialogObject.addIT(this.it);
			
			normalizeITToSave();
			
			onAccept();
			hide();
			
		} else {
			AonConfirmDialog dialog = new AonConfirmDialog();
			dialog.info("AVISO: Fechas", "La fecha y la causa de baja deben estar rellenadas.");
		}
	}

	// --------------------------------------------------- Footer.Methods
	
	private boolean checkIfSaveIsPossible() {
		return causeLowPart.getSelectedIndex() != 0 && null != itStartDate.getValue();
	}
	
	public static boolean isPartenityPart(IT it) {
		return it != null && (it.getTypeLowPart() == (byte)2 || it.getTypeLowPart() == (byte)3);
	}
	
	private boolean isNotEmptyIT() {
		return this.it != null;
	}
	
	public ITEmployee getITEmployee() {
		return itEmployee;
	}

	public void setViewPartComunica(ITPart itPart) {

		listIT.setVisible(false);
		deleteIT.setVisible(false);
		backListIT.setVisible(true);
		deckPanel.showWidget(2);
		deckPanel.setWidth("620px");

		mainCommunicate.clear();
		
		mainCommunicate.add(loading);
		
		itPartTmp = itPart;

		paintPanel(itPartTmp);

		createFooterButtonsCommunicate();

	}

	private void paintPanel(ITPart itPart){
		
		String cause = null;
		switch(itPart.getType()){
			case (byte)0:
				cause = getSelectedTextByValue(causeLowPart, it.getTypeLowPart());
				toolbarDetail.setTitle("Baja");
			break;
			case (byte)1:
				checkComunicationNeeded();
				cause = "Confirmaci\u00F3n";
				toolbarDetail.setTitle(cause);
			break;
			case (byte)2:
				checkComunicationNeeded();
				cause = getSelectedTextByValue(causeHighPart, it.getTypeHighPart());
				toolbarDetail.setTitle("Alta");
			break;
			default:
				cause = "";
			break;
		}

		HTMLPanel panel = new HTMLPanel("");
		panel.setStyleName(style.styleBorder());
		mainCommunicate.add(panel);

		Label labelM = new Label("Parte IT");
		labelM.addStyleName("aon-group-title "+style.tittle());
		labelM.setWidth("105px");
		panel.add(labelM);

		//---EMPLOYEE DATA
		HTMLPanel flexColumn  = new HTMLPanel("");
		flexColumn.setStyleName(style.flexColumn());
		panel.add(flexColumn);

		HTMLPanel employeeData = new HTMLPanel("");
		employeeData.setStyleName(style.flex());
		flexColumn.add(employeeData);
		
		Label name = new Label("Trabajador:");
		name.setStyleName(style.subTitle());
		employeeData.add(name);
		employeeData.add(new Label(itDialogObject.getEmployeeName()));

		Label doc = new Label("Documento:");
		doc.setStyleName(style.subTitle());
		employeeData.add(doc);
		employeeData.add(new Label(itDialogObject.getEmployeeinfo().getDocument()));
		
		Label naf = new Label("NAF:");
		naf.setStyleName(style.subTitle());
		employeeData.add(naf);
		employeeData.add(new Label(itDialogObject.getEmployeeinfo().getSsNumber()));

		HTMLPanel itInfoEl = new HTMLPanel("");
		itInfoEl.setStyleName(style.flex());
		flexColumn.add(itInfoEl);
		
		Label dateEl = new Label("Fecha:");
		dateEl.setStyleName(style.subTitle());
		itInfoEl.add(dateEl);
		itInfoEl.add(new Label( formatFullDate.format(itPart.getDate()) ));

		Label causeEl = new Label("Causa:");
		causeEl.setStyleName(style.subTitle());
		itInfoEl.add(causeEl);
		itInfoEl.add(new Label(cause));

		//TRAMOS
		tramos = new HTMLPanel("");
		tramos.setStyleName(style.styleBorder());
		mainCommunicate.add(tramos);
		
		if(itPart.getType() == (byte)0)
			addInfoAditionalBaja(flexColumn);
		else if(itPart.getType() == (byte)1)
			addInfoAditionalConfirmation(itInfoEl, itPart);

	}
	
	private void checkComunicationNeeded() {
		Date checkDate = new Date(2023 - 1900, 3, 1);
		Date currentDate = new Date();
		
		if(currentDate.before(checkDate)) {
			AonDialog warning = new AonDialog("Comunicaciones IT", new HTML("Seg\u00fan el Real Decreto 1060/2022, con vigencia desde el pr\u00f3ximo 1 de abril de 2023, ya no ser\u00e1 necesario comunicar los partes de <b>confirmaci\u00f3n</b> ni lo partes de <b>Alta</b>."));
			warning.warning();
		}
	}

	private void addInfoAditionalBaja(HTMLPanel flexColumn) {
		HTMLPanel panel = new HTMLPanel("");
		panel.setStyleName(style.flex());
		flexColumn.add(panel);

		//BASE CC
		Label baseEl = new Label("Base CC");
		baseEl.setStyleName(style.subTitle());
		panel.add(baseEl);

		baseCC = new DoubleBox();
		baseCC.setStyleName("aon-inputText");
		panel.add(baseCC);
		baseCC.addChangeHandler(event->{
			it.setDailyCGCBase(baseCC.getValue());
//			it.setRegulationBase(baseCC.getValue());
		});
		
		//BASE CP
		Label baseCPEl = new Label("Base CP");
		baseCPEl.setStyleName(style.subTitle());
		panel.add(baseCPEl);

		baseCP = new DoubleBox();
		baseCP.setStyleName("aon-inputText");
		panel.add(baseCP);
		baseCP.addChangeHandler(event->{
			it.setDailyCGPBase(baseCP.getValue());
		});
		baseCPEl.setVisible(isPaternity());
		baseCP.setVisible(isPaternity());

	
		Label quoteDayEl = new Label("D\u00EDas cotizados");
		quoteDayEl.setStyleName(style.subTitle());
		panel.add(quoteDayEl);
		
		quoteDayInput = new DoubleBox();
		quoteDayInput.setStyleName("aon-inputText");
		panel.add(quoteDayInput);
		quoteDayInput.addChangeHandler(event->{
			it.setQuoteDays(quoteDayInput.getValue().intValue());
		});

		getTramos();
	}
	
	private void addInfoAditionalConfirmation(HTMLPanel panel, ITPart itPart) {
		Label name = new Label("N" + String.valueOf("\u00B0") + " de orden:");
		name.addStyleName(style.subTitle());
		panel.add(name);
		panel.add(new Label(itPart.getConfirmOrderNumber().toString()));
	}
	
	protected void changeStatus(ContractLeaveDetailStatus status) {
        for (ITPart itPart: this.it.getITParts()) {
            if(itPart.getId().equals(itPartTmp.getId())) {
                itPart.setStatus(status.value());
                itPart.setModify(true);
                break;
            }
        }
    }
	
	protected void changeStatusProcessed() {
	    changeStatus(ContractLeaveDetailStatus.PROCESSED);
	}
	
    protected void changeStatusPending() {
        changeStatus(ContractLeaveDetailStatus.PENDING);
    }
	
	private void getTramos() {
		tramos.clear();
		
		LOGGER.info("CONTRACT_INFO: "+ this.itDialogObject.getContractInfo().toString());
	
		Date startDate = DateUtils.getFirstDayOfMonth( DateUtils.addMonths2Date(it.getStartDate(), -4) );
		Date endDate = DateUtils.getLastDayOfMonth(  DateUtils.addMonths2Date(it.getStartDate(), -1)  );
		DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
		employeesService.getSalariesOccam(this.itDialogObject.getITEmployee(), startDate, endDate, new AsyncCallback<List<Certifica2Info>>() {

			@Override
			public void onFailure(Throwable caught) {
				AonDialog dialog = new AonDialog("Error", new HTML(caught.getMessage()));
				dialog.warning();
			}

			@Override
			public void onSuccess(List<Certifica2Info> result) {
				fillQuoteDataListPanel(result);
			}
		});		
	}

	private void fillQuoteDataListPanel(List<Certifica2Info> datas) {
		if(!datas.isEmpty()){
			Label lbl = new Label("Tramos");
			lbl.setWidth("105px");
			lbl.setStyleName("aon-group-title "+ style.tittle());
			tramos.add(lbl);

			boolean isPartial = this.itDialogObject.getContractInfo().isPartial();
			
			HTMLPanel list = new HTMLPanel("");
			list.setStyleName(style.flexColumn());
			tramos.add(list);

			Double base = 0.0;
			Double baseCp = 0.0;
			Boolean cgp = this.it.getTypeLowPart()!=null && this.it.getTypeLowPart()==(byte)1 ? true : false; // si es accidente laboral
			Integer quoteDay = 0;
			
			for (Certifica2Info data: datas) {
				
                HTMLPanel row = new HTMLPanel("");
				row.setStyleName(style.flexCustom());
	
				Label monthL = new Label("Mes: ");
				monthL.addStyleName(style.subTitle());
				
				Label monthValue = new Label( getMonthStr(data.getStartDate()) +"" );
				
				Label yearL = new Label("A\u00F1o: ");
				yearL.addStyleName(style.subTitle());
				Label yearValue = new Label(DateUtils.getYear(data.getStartDate())+"");
				
				Label daysL = new Label("D\u00EDas: ");
				daysL.addStyleName(style.subTitle());
				Label daysValue = new Label(getDayStr(data.getSettleQuoteDays()));
				
				Label cgcL = new Label("CGC: ");
				cgcL.addStyleName(style.subTitle());
				Double cgc = Math.round(data.getBaseCgc()*100.0)/100.0;
				Label cgcValue = new Label( cgc.toString());
				
				Double baseUnEmployee  = Math.round(data.getBaseUnemployment()*100.0)/100.0;
				Label unemploymentL = new Label("Desempleo: ");
				unemploymentL.addStyleName(style.subTitle());
				Label unemploymentValue = new Label( baseUnEmployee.toString());
				
				row.add(monthL);
				row.add(monthValue);
				row.add(yearL);
				row.add(yearValue);
				row.add(daysL);
				row.add(daysValue);
				row.add(cgcL);
				row.add(cgcValue);
				row.add(unemploymentL);
				row.add(unemploymentValue);
				list.add(row);

				base+= cgp ? data.getBaseUnemployment() : data.getBaseCgc();
				baseCp+= data.getBaseUnemployment();
				quoteDay+=data.getSettleQuoteDays();
				
				if((isPartial && quoteDay>=84) || (!isPartial && quoteDay>=28)) {
					break; 
				}
			}

            Double baseCcRound = Math.round(base*100.0)/100.0;
            baseCC.setValue(baseCcRound);
            quoteDayInput.setValue(quoteDay.doubleValue());
    		it.setQuoteDays(quoteDay);
    		
            Double baseCpRound = Math.round(baseCp*100.0)/100.0;
            baseCP.setValue(baseCpRound);
            
        	it.setDailyCGCBase(baseCcRound/quoteDay);
        	it.setDailyCGPBase(baseCpRound/quoteDay);
        }
	}
	
	private String getMonthStr(Date date) {
		Integer month = DateUtils.getMonth(date) +1;
		if(month > 0 && month <=9) 
			return "0"+month;
		
		return month.toString();
	}
	
	private String getDayStr(Integer day) {
		if(day > 0 && day <=9) 
			return "0"+day;
		
		return day.toString();
	}

    private void sendItPart() {
        changeStatusProcessed();
		onCommunicateITPart(it, itPartTmp);
	}
    
    private boolean isCommunicatePart(ITPart part){
        return part.getStatus()!=null && part.getStatus() == (byte)3;
    }

    private void getITReport(IT it, ITPart part) {
        DateTimeFormat fullDateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
        String completeCcc = itDialogObject.getContractInfo().getCompleteCCC();
        if(completeCcc!=null) {
            String regime = completeCcc.substring(0, 4);
            String ccc = completeCcc.substring(4, completeCcc.length());
            EmployeeInfo employeeInfo = itDialogObject.getEmployeeinfo();
            
            String dateFromStr = fullDateFormat.format(it.getStartDate());
            String dateToStr = fullDateFormat.format(part.getDate());
            
            String fileDownloadURL = GWT.getModuleBaseURL()+ "it_export/";
            String query = "?domainName=" + Wnd.getCurrentDomainNameURL()
                    + "&userLogin=" + Wnd.getCurrentUser()
                    + "&affiliationNumber=" + employeeInfo.getSsNumber()
                    + "&regime=" + regime
                    + "&contributionAccount=" + ccc
                    + "&dateFromStr=" + dateFromStr
                    + "&dateToStr=" + dateToStr
                    + "&startDateStr=" + dateToStr
                    + "&itType=" + it.getTypeLowPart()
                    + "&itPartType=" + part.getType();
            
            Window.open(fileDownloadURL+query, "ITExporter", "resizable=yes,scrollbars=yes,status=yes");
        }
    }
	
	// --------------------------------------------------- Abstract Methods
	protected abstract void onShowCertitificateIT(IT it);
	
	protected abstract void onDelete(IT it);
	
	protected abstract void onCommunicateITPart(IT it, ITPart itPart);
	
	protected abstract void onRemoveITPartTGSS(ItNotExist ItNotExist);
	
	protected abstract void onAccept();

	private static void setOptionDisabled(ListBox listBox, String value, boolean disabled) {
	    SelectElement select = listBox.getElement().cast();
	    NodeList<OptionElement> options = select.getOptions();
	    for (int i = 0; i < options.getLength(); i++) {
		OptionElement option = options.getItem(i);
		if (value.equals(option.getValue())) {
		    option.setDisabled(disabled);
		    break;
		}
	    }
	}

}
