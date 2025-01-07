package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractSalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus.AffiliatedNotFound;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDResults;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.ContractParams;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class MainContrataContract extends MainEntryPoint {

	// ------------------------------------------ UiBinder

	interface UIBinder extends UiBinder<Widget, MainContrataContract> {}

	private static final UIBinder binder = GWT.create(UIBinder.class);

	// ------------------------------------------------- ScheduledCommand (TGSS)

	class UpdateCertCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onUpdateCert();
		}
	}
	
	class LaboralLifeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onLaboralLife(new Date());
		}
	}
	
	class TGSSContextMenu extends ContextMenu {
		
		private MenuItem updateCert;

		public TGSSContextMenu() {
			updateCert = addMenuItem("Cert. de estar al corriente con TGSS", new UpdateCertCommand(), AON.CSS.aonIconTgss(), "updateCert");
			addMenuItem("Vida laboral", new LaboralLifeCommand(), AON.CSS.aonIconTgss(), "laboralLife");
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			item.ensureDebugId(debugId);
			return item;
		}
		
		public MenuItem getUpdateCert() {
			return this.updateCert;
		}

	}
	
	// ------------------------------------------ UiFields
	
	@UiField
	DeckPanel deckPanel;
	
	// Employees List
	
	@UiField(provided = true)
	AonCustomDockLayout employeesDockLayoutPanel;
	
	private HTMLPanel employeesContainer;
	private HTMLPanel employeesMessagePanel = new HTMLPanel("");
	
	private ScrollPanel employeesTableScrollPanel;
	private AonCustomTable employeesTable;
	
	// Employee
	
	@UiField(provided = true)
	ContrataEmployee contrataEmployee;
	
	// PDF
	
	@UiField(provided = true)
	AonCustomDockLayout pdfDockLayoutPanel;
	
	private HTMLPanel pdfContainer;
	private HTMLPanel pdfMessagePanel = new HTMLPanel("");
	
	private FullViewer viewer;
	
	// Enterprise Salary
	
	@UiField(provided = true)
	EnterpriseSalary enterpriseSalary;
	
	// ------------------------------------------ Variables
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat formatYear = DateTimeFormat.getFormat("yyyy");
	private DateTimeFormat formatMonth = DateTimeFormat.getFormat("MMMM");
	
	private MainContrataContractObject mainContrataContractObject;
	private EnterpriseSalaryObject enterpriseSalaryObject;
	
	private AonExpandButton tgssExpandButton;
	private TGSSContextMenu tgssContextMenu;
	
	private HTMLPanel sistemaREDMessagePanel;
	private SistemaREDResults sistemaREDResults;
	private AonToolbarButton sistemREDInfoBtn;
	
	private boolean contextLoaded = false;
	private boolean fetchingData = false;
	
	// Filter Employee List
	
	private AonCustomListBox active = new AonCustomListBox("Estado");
	private AonCustomListBox tc2LB = new AonCustomListBox("TC2");
	private AonCustomListBox workplaceLB = new AonCustomListBox("Centro Trabajo");
	private AonCustomDateBox fromDB = new AonCustomDateBox("Desde");
	private AonCustomDateBox toDB = new AonCustomDateBox("Hasta");
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	// Employee List Search
	private ContractParams params;
	
	private int limit = 100;
	private int offset = 0;
	private int moreData = 0;
	private int searchEnabled = 0;
	private int lastScrollPos = 0;
	
	// Employee List Cols
	
	private static enum EMPLOYEE_COL {
		  DES(AON.MSG.name()						,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DOC(AON.MSG.document()					,"5rem"				,"")
		, NSS("NSS"									,"5rem"				,"")
		, CON("TC2"									,"3rem"				,"")
		, WOR(AON.MSG.workplace()					,"7rem"				,"")
		, CAT("Categoria"							,"7rem"				,"min-width: 7rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("F. Inicio"							,"5rem"				,"")
		, END("F. Fin"								,"5rem"				,"")
		, BUT(AonStringUtils.EMPTY					,"2rem"				,"")
		;

		String headerLabel;
		String colWidth;
		String styles;

		private EMPLOYEE_COL(String headerLabel,String colWidth,String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getStyles() {
			return styles;
		}
	}
	
	// ------------------------------------------ ContrataEmployee

	private class ContrataEmployeeImpl extends ContrataEmployee {

		@Override
		protected void onListShow(boolean reloadEmployees) {
			if (getChanges()) {
				mainContrataContractObject.resetEmployeesList();
				offset = 0;
				onSearch();
				checkStatus(mainContrataContractObject);
				setChanges(false);
			} 

			showContracts();
		}

		private void loadEmployee(EmployeeContractInfo employee, Integer selectedEmployeeIdx, int employeesSize,
				int selectedTab) {
			if (null != employee) {
				Integer contractId = employee.getContractInfo().getContractId();
				contrataEmployee.setHasCertificateSEPE(mainContrataContractObject.hasCertificateSEPE());
				contrataEmployee.setIsComunica(mainContrataContractObject.isComunica());
				contrataEmployee.setHasPayroll(mainContrataContractObject.hasPayroll());
				ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject();
				contrataEmployeeDialogObject.setActivitiesCCC(mainContrataContractObject.getEnterpriseContext().getActivitiesCCC());
				contrataEmployeeDialogObject.setWorkplaces(mainContrataContractObject.getEnterpriseContext().getWorkplaces());
				contrataEmployeeDialogObject.setAgreements(mainContrataContractObject.getEnterpriseContext().getAgreements());
				contrataEmployeeDialogObject.setPayMethodsMap(mainContrataContractObject.getEnterpriseContext().getPayMethods());
				contrataEmployee.setContrataEmployeeObject(contrataEmployeeDialogObject, contractId,
						selectedEmployeeIdx, employeesSize, selectedTab, s -> deckPanel.showWidget(1));
			}
		}

		@Override
		protected DomainUserRoles getDomainUserRole() {
			return mainContrataContractObject.getDomainUserRoles();
		}

		@Override
		protected ContractParams getContractListParams() {
			return params;
		}

		@Override
		protected void getContractListCount(Consumer<Integer> finish) {
			mainContrataContractObject.getContractListCount(params, count -> finish.accept(count));
		}

		@Override
		protected void onContractSelectionChange(EmployeeContractInfo employeeContractInfo, Integer position) {
			loadEmployee(employeeContractInfo, position, mainContrataContractObject.getEmployeesList().size(), tabLayOutPanel.getSelectedIndex());
		}

	}

	// ------------------------------------------ EnterpriseSalary

	private class EnterpriseSalaryImpl extends EnterpriseSalary {

		@Override
		protected void onBackClick() {
			showContracts();
		}

	}

	// ------------------------------------------ Constructor

	public MainContrataContract() {
		contrataEmployee = new ContrataEmployeeImpl();
		enterpriseSalary = new EnterpriseSalaryImpl();

		employeesDockLayoutPanel = new AonCustomDockLayout("Contratos") {
			@Override
			protected void onClearFilter() { 
				mainContrataContractObject.resetEmployeesList();
				offset = 0;
				
				params.setDescription(null);
				employeesDockLayoutPanel.getSearchTextBox().setValue(null);
				
				params.setActive((byte)1);
				active.setValue("1");
				
				params.setTc2(null);
				tc2LB.setValue("");
				
				params.setWorkplace(null);
				workplaceLB.setValue("");
				
				params.setFrom(null);
				fromDB.setValue(null);
				
				params.setTo(null);
				toDB.setValue(null);
				
				onSearch();
			}
		};
		
		pdfDockLayoutPanel = new AonCustomDockLayout("PDF") {
			@Override
			protected void onClearFilter() { /* Nothing to do here */ }
		};

		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		tgssContextMenu = new TGSSContextMenu();
		
		// Employees List
		initEmployeeList();	
		showContracts();
		
		// PDF
		initPDF();
		
	}
	
	// Employees List

	private void initEmployeeList() {
		initEmployeeListToolbar();
		initEmployeeListFilter();
		
		employeesContainer = new HTMLPanel("");
		employeesContainer.addStyleName(AON.CSS.aonFlexColumn());
		
		employeesTable = new AonCustomTable();
		employeesTableScrollPanel = new ScrollPanel(employeesTable);
		employeesTableScrollPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		employeesTableScrollPanel.addScrollHandler(e -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = employeesTableScrollPanel.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = employeesTableScrollPanel.getWidget().getOffsetHeight() - employeesTableScrollPanel.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					searchEmployeesData();
				}
			}
		});
		
		employeesContainer.add(employeesMessagePanel);
		employeesContainer.add(employeesTableScrollPanel);
		
		employeesDockLayoutPanel.add(employeesContainer);
	}
	
	public boolean isSearchEnabled() {
		return searchEnabled == 0;
	}
	
	public void disableSearch() {
		searchEnabled = -1;
	}
	
	public void enableSearch() {
		searchEnabled = 0;
	}
	
	public boolean isMoreData() {
		return moreData == 0;
	}
	
	public void disableMoreData() {
		moreData = -1;
	}
	
	public void enableMoreData() {
		moreData = 0;
	}

	private void initEmployeeListToolbar() {
		AonToolbarButton newContract = new AonToolbarButton("Nuevo contrato", AON.CSS.aonIconAdd());
		newContract.addClickHandler(e -> onNewContract());
		employeesDockLayoutPanel.addToolbarButton(newContract);
		
		tgssExpandButton = new AonExpandButton("TGSS", AON.CSS.aonIconTgss()) {

			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				tgssContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				tgssContextMenu.show();
			}

			@Override
			public void onDefaultClick(ClickEvent evet) {
				onUpdateCert();
			}
		};
		employeesDockLayoutPanel.addToolbarButton(tgssExpandButton);
		
		AonToolbarButton salariesBtn = new AonToolbarButton("N\u00F3minas Empresa", AON.CSS.aonIconReceipt());
		salariesBtn.addClickHandler(e -> showEnterpriseSalary());
		employeesDockLayoutPanel.addToolbarButton(salariesBtn);
		
		AonToolbarButton exportExcelBtn = new AonToolbarButton("Exportar Contratos Empresa", AON.CSS.aonIconExcel());
		exportExcelBtn.addClickHandler(e -> exportEnterpriseContracts());
		employeesDockLayoutPanel.addToolbarButton(exportExcelBtn);
		
		sistemREDInfoBtn = new AonToolbarButton("Mensajes SistemaRED", AON.CSS.aonIconInfo());
		sistemREDInfoBtn.setVisible(false);
		sistemREDInfoBtn.addClickHandler(e -> {
			HTMLPanel hPanel = new HTMLPanel("");
			hPanel.addStyleName(AON.CSS.aonFlexColumn());
			
			sistemaREDMessagePanel = new HTMLPanel("");
			hPanel.add(sistemaREDMessagePanel);
			
			ResultsPanel resultPanel = new ResultsPanel();
			resultPanel.setHeight((sistemaREDResults.getTreeItems() > 20 ? (20 * 30) : sistemaREDResults.getTreeItems() * 30) + "px");
			resultPanel.setWidth("800px");
			resultPanel.setWidget(sistemaREDResults);
			hPanel.add(resultPanel);
			
			AonDialog dialog = new AonDialog("SistemaRED", hPanel);
			dialog.removeMaxWidth();
			dialog.info();
		});
		employeesDockLayoutPanel.addToolbarButton(sistemREDInfoBtn);
	}
	
	private void onNewContract() {
		EmployeeDialog employeeDialog = new EmployeeDialog(true) {
			@Override
			protected void onAccept(Integer contractId) {
				contrataEmployee.setHasCertificateSEPE(mainContrataContractObject.hasCertificateSEPE());
				contrataEmployee.setIsComunica(mainContrataContractObject.isComunica());
				contrataEmployee.setHasPayroll(mainContrataContractObject.hasPayroll());
				contrataEmployee.setChanges(true);

				ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject();
				contrataEmployeeDialogObject.setActivitiesCCC(mainContrataContractObject.getEnterpriseContext().getActivitiesCCC());
				contrataEmployeeDialogObject.setWorkplaces(mainContrataContractObject.getEnterpriseContext().getWorkplaces());
				contrataEmployeeDialogObject.setAgreements(mainContrataContractObject.getEnterpriseContext().getAgreements());
				contrataEmployeeDialogObject.setPayMethodsMap(mainContrataContractObject.getEnterpriseContext().getPayMethods());
				
				contrataEmployee.setContrataEmployeeObject(
						contrataEmployeeDialogObject, 
						contractId,
						0,
						s -> deckPanel.showWidget(1));
			}
		};

		EmployeeDialogObject employeeDialogObject = new EmployeeDialogObject(null);
		employeeDialog.setEmployeeDialogObject(employeeDialogObject);
		employeeDialog.setModal(true);
		employeeDialog.setAnimationEnabled(true);
	}
	
	public Integer getContractListPosition(Integer contractId) {
		for(int i=0; i<mainContrataContractObject.getEmployeesList().size(); i++)
			if(mainContrataContractObject.getEmployeesList().get(i).getContractInfo().getContractId().equals(contractId))
				return i;
		return 0;
	}
	
	private void onUpdateCert() {
		AonMessagePanel.showLoading(employeesMessagePanel, "Obteniendo Cert. de estar al corriente con TGSS ...");
		Pair<String, String> completeCCC = mainContrataContractObject.getPrincipalAccount();
		
		mainContrataContractObject.getUpdateCert(completeCCC.getKey(), completeCCC.getValue(),
				dataURI -> {
					pdfDockLayoutPanel.setToolbarTitle("Certificado de estar al corriente con TGSS");
					showPdf(false);
					viewer.open(dataURI);
					AonMessagePanel.hideMessage(employeesMessagePanel);
				}, f -> {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Error obtenci\u00f3n TGSS", f.getMessage());
					AonMessagePanel.showWarning(employeesMessagePanel, warningMap);
				});
	}
	
	public void onLaboralLife(Date date) {
		AonMessagePanel.showLoading(employeesMessagePanel, "Obteniendo vida laboral ...");
		
		Pair<String, String> completeCCC = mainContrataContractObject.getPrincipalAccount();
		
		mainContrataContractObject.getCCCLaboralLife(completeCCC.getKey(), completeCCC.getValue(), date, new Date(),
				dataURI -> {
					pdfDockLayoutPanel.setToolbarTitle("Vida laboral");
					showPdf(true);
					viewer.open(dataURI);
					AonMessagePanel.hideMessage(employeesMessagePanel);
				}, f -> {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Error obtenci\u00f3n TGSS", f.getMessage());
					AonMessagePanel.showWarning(employeesMessagePanel, warningMap);
				});

	}
	
	public void onLaboralLifeCahngeDate(Date date) {
		AonMessagePanel.showLoading(employeesMessagePanel, "Obteniendo vida laboral ...");
		
		Pair<String, String> completeCCC = mainContrataContractObject.getPrincipalAccount();
		
		mainContrataContractObject.getCCCLaboralLife(completeCCC.getKey(), completeCCC.getValue(), date, new Date(),
				dataURI -> {
					viewer.open(dataURI);
					AonMessagePanel.hideMessage(employeesMessagePanel);
				}, f -> {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Error obtenci\u00f3n TGSS", f.getMessage());
					AonMessagePanel.showWarning(employeesMessagePanel, warningMap);
				});

	}
	
	protected void exportEnterpriseContracts() {
		String printURL = URL.encode(GWT.getModuleBaseURL() + "enteprise_contracts/");
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_GET);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden("domain", Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden("user", Wnd.getCurrentUser()));
		
		flowPanel.add(new Hidden("description", employeesDockLayoutPanel.getSearchTextBox().getValue()));
		
		flowPanel.add(new Hidden("active", active.getValue()));
		
		flowPanel.add(new Hidden("tc2", tc2LB.getValue()));
		
		flowPanel.add(new Hidden("workplace", workplaceLB.getValue()));
		
		flowPanel.add(new Hidden("from", null == fromDB.getValue() ? null : formatFullDate.format(fromDB.getValue())));
		flowPanel.add(new Hidden("to",  null == toDB.getValue() ? null : formatFullDate.format(toDB.getValue())));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> employeesDockLayoutPanel.getToolbarButtonPanel().remove(formPanel));
		
		employeesDockLayoutPanel.addToolbarButton(formPanel);
		
		formPanel.submit();
	}

	private void initEmployeeListFilter() {
		
		employeesDockLayoutPanel.setSearchPlaceholder("Filtrar por nombre, documento o nss");
		
		employeesDockLayoutPanel.addKeyUpHandler(e -> {
			String value = employeesDockLayoutPanel.getSearchTextBox().getValue();
			if(e.getNativeKeyCode() == KeyCodes.KEY_ENTER || e.getNativeKeyCode() == KeyCodes.KEY_MAC_ENTER) return;
			
			if(AonStringUtils.isNotBlank(value) && value.length() > 2 && !fetchingData) {
				fetchingData = true;
				mainContrataContractObject.resetEmployeesList();
				offset = 0;
				params.setDescription(value);
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				offset = 0;
				params.setDescription(value);
				onSearch();
			}
		});
		
		active.addItem( "Todas", "");
		active.addItem( "Inactivas", "0");
		active.addItem( "Activas", "1");
		active.getListBox().setSelectedIndex(2);
		active.getListBox().addChangeHandler(event -> {
			mainContrataContractObject.resetEmployeesList();
			offset = 0;
			params.setActive(AonStringUtils.isBlank(active.getValue()) ? null : Byte.parseByte(active.getValue()));
			onSearch();
		});
		
		fromDB.getDateBox().addValueChangeHandler(e -> {
			mainContrataContractObject.resetEmployeesList();
			offset = 0;
			params.setFrom(e.getValue());
			onSearch();
		});
		
		toDB.getDateBox().addValueChangeHandler(e -> {
			mainContrataContractObject.resetEmployeesList();
			offset = 0;
			params.setTo(e.getValue());
			onSearch();
		});
		
		employeesDockLayoutPanel.addFilterWidget(active);
		employeesDockLayoutPanel.addFilterWidget(tc2LB);
		employeesDockLayoutPanel.addFilterWidget(workplaceLB);
		employeesDockLayoutPanel.addFilterWidget(fromDB);
		employeesDockLayoutPanel.addFilterWidget(toDB);
		
		sort.addItem("Nombre", "name");
		sort.addItem("Documento", "document");
		sort.getListBox().addChangeHandler(event -> {
			mainContrataContractObject.resetEmployeesList();
			offset = 0;
			params.setOrderBy(sort.getValue());
			onSearch();
		});
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> {
			mainContrataContractObject.resetEmployeesList();
			offset = 0;
			params.setAsc(Boolean.parseBoolean(asc.getValue()));
			onSearch();
		});
		
		employeesDockLayoutPanel.addSortWidget(sort);
		employeesDockLayoutPanel.addSortWidget(asc);
	}
	
	private void initWorkplaceLB() {
		workplaceLB.clearItems();
		workplaceLB.addItem("-", "");
		for (Workplace workplace : this.mainContrataContractObject.getEnterpriseContext().getWorkplaces())
			workplaceLB.addItem(workplace.getDescription(), workplace.getId().toString());
		
		workplaceLB.getListBox().addChangeHandler(e -> {
			mainContrataContractObject.resetEmployeesList();
			offset = 0;
			params.setWorkplace(AonStringUtils.isBlank(workplaceLB.getValue()) ? null : Integer.parseInt(workplaceLB.getValue()));
			onSearch();
		});
	}
	
	private void initTC2LB() {
		tc2LB.clearItems();
		tc2LB.addItem("-", "");
		
		for (Entry<String, String> entry : this.mainContrataContractObject.getEnterpriseContext().getContractTypes().entrySet())
			tc2LB.addItem(entry.getKey(), entry.getValue());
		
		tc2LB.getListBox().addChangeHandler(e -> {
			mainContrataContractObject.resetEmployeesList();
			offset = 0;
			params.setTc2(AonStringUtils.isBlank(tc2LB.getValue()) ? null : tc2LB.getValue());
			onSearch();
		});
	}
	
	// PDF
	
	private void initPDF() {
		initPDFToolbar();
		
		pdfContainer = new HTMLPanel("");
		pdfContainer.addStyleName(AON.CSS.aonFlexColumn());
		
		viewer = new FullViewer();
		
		pdfContainer.add(pdfMessagePanel);
		pdfContainer.add(viewer);
		
		pdfDockLayoutPanel.add(pdfContainer);
	}

	private void initPDFToolbar() {
		AonToolbarButton closePDF = new AonToolbarButton(AON.MSG.closed(), AON.CSS.aonIconBack());
		closePDF.addClickHandler(e -> showContracts());
		pdfDockLayoutPanel.addToolbarButton(closePDF);
		
		pdfDockLayoutPanel.hideSearchWidget();
	}
	
	// OnModuleLoad
	
	@Override
	public void onModuleLoad() {
		onModuleLoad(new MainContrataContractObject());
	}

	public void onModuleLoad(MainContrataContractObject mainContrataContractObject) {
		this.mainContrataContractObject = mainContrataContractObject;
		this.contextLoaded = false;
		
		AonMessagePanel.showLoading(employeesMessagePanel, "Obteniendo contexto de la empresa...");
		
		params = new ContractParams()
				.setActive((byte) 1)
				.setAsc(true)
				.setOrderBy("name");
		
		onSearch();
		this.mainContrataContractObject.getCertificateSEPE();

		this.mainContrataContractObject.getContextInfo(
				s -> {
					AonMessagePanel.hideMessage(employeesMessagePanel);
					enterpriseSalary.setBackButtonVisible();
					initWorkplaceLB();
					initTC2LB();
					contextLoaded = true;
					
					checkStatus(this.mainContrataContractObject);
				}, f -> {
					AonMessagePanel.showError(employeesMessagePanel, "Error contexto: " + f.getMessage());
					contextLoaded = true;
				}
		);

	}
	
	private void onSearch() {
		enableMoreData();
		createEmployeeTable();
		searchEmployeesData();
	}
	
	private void searchEmployeesData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset);
		params.setLimit(limit);
		
		this.mainContrataContractObject.getEmployeesInfo(params, 
				employees -> {
					
					boolean something = false;
					
					for(EmployeeContractInfo employee : employees) {
						something = true;
						paintEmployeeRow(employee);
					}
					
					if (employees.size() < limit) {
						disableMoreData();
					} else {
						offset = ( offset + employees.size());
						enableMoreData();
					}
					
					if (!something) {
						FlowPanel line = new FlowPanel();
						InlineLabel label = new InlineLabel(AON.MSG.noData());
						line.add(label);
						paintEmployeeNoDataRow();
						disableMoreData();
					}
					enableSearch();
					
					fetchingData = false;
				}, 
				f -> {
					AonMessagePanel.showError(employeesMessagePanel, "Error contratos: " + f.getMessage());
				}
		);
	}

	private void createEmployeeTable() {
		employeesTableScrollPanel.clear();
		employeesTable = new AonCustomTable();
		employeesTableScrollPanel.add(employeesTable);
		paintEmployeeHeader();
	}

	private void paintEmployeeHeader() {
		employeesTable.createHeader();
		for ( EMPLOYEE_COL col : EMPLOYEE_COL.values()) 
			employeesTable.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}
	
	private void paintEmployeeRow(EmployeeContractInfo employeeContractInfo) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton button;
		button = new AonTableButton("Borrar contrato", AON.CSS.aonIconDelete());
		button.addStyleName(AON.CSS.aonCustomRowButtom());
		button.addClickHandler(e -> {
			e.stopPropagation();
			button.setEnabled(false);
			
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Contrato", getDeleteMessageWidget(employeeContractInfo));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					button.setEnabled(true);
				}

				@Override
				public void onAccept() {
					mainContrataContractObject.delete4EverContract(employeeContractInfo.getContractInfo().getContractId(), s -> {
						mainContrataContractObject.resetEmployeesList();
						offset = 0;
						onSearch();
					}, f -> {});
				}
			});
		});
		buttonContainer.add(button);
		
		HTMLPanel row = employeesTable.createRow();
		row.addDomHandler(e -> onEmployeeOpen(employeeContractInfo), ClickEvent.getType());
		
		Label employeeNameLabel = new Label(employeeContractInfo.getEmployeeInfo().getFullName());
		if(checkInactive(employeeContractInfo)) {
			employeeNameLabel.setTitle("Inactivo");
			employeeNameLabel.getElement().getStyle().setColor("red");
			employeeNameLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		} 
		else if(checkPrevAlta(employeeContractInfo)) {
			employeeNameLabel.setTitle("Alta previa");
			employeeNameLabel.getElement().getStyle().setColor("green");
			employeeNameLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		} 
		else if(checkCloseEnd(employeeContractInfo)) {
			employeeNameLabel.setTitle("Contrato cerca de finalizar");
			employeeNameLabel.getElement().getStyle().setColor("orange");
			employeeNameLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		} 
		
		Label contractTypeLabel = new Label();
		if ((byte) 3 == employeeContractInfo.getContractInfo().getSsRegimen())
			contractTypeLabel.setText("RETA");
		else if ("000".equals(employeeContractInfo.getContractInfo().getContractType()))
			contractTypeLabel.setText("BECARIO");
		else {
			contractTypeLabel.setText(employeeContractInfo.getContractInfo().getContractType());
			contractTypeLabel.setTitle(getContractTypeTitle(employeeContractInfo.getContractInfo().getContractType()));
		}
		
		Label endDateLabel = new Label();
		if(null != employeeContractInfo.getContractInfo().getEndDate())
			endDateLabel.setText(formatFullDate.format(employeeContractInfo.getContractInfo().getEndDate()));
		
		employeeNameLabel.setTitle(employeeContractInfo.getEmployeeInfo().getFullName());
		employeesTable.addInlineStyle(employeeNameLabel, EMPLOYEE_COL.DES.getStyles());
		employeesTable.addRow(row, employeeNameLabel, EMPLOYEE_COL.DES.getColWidth());
		
		employeesTable.addRow(row, new Label(employeeContractInfo.getEmployeeInfo().getDocument()), EMPLOYEE_COL.DOC.getColWidth());
		employeesTable.addRow(row, new Label(employeeContractInfo.getEmployeeInfo().getSsNumber()), EMPLOYEE_COL.NSS.getColWidth());
		employeesTable.addRow(row, contractTypeLabel, EMPLOYEE_COL.CON.getColWidth());
		employeesTable.addRow(row, new Label(employeeContractInfo.getContractInfo().getWorkplaceName()), EMPLOYEE_COL.WOR.getColWidth());
		
		Label category = new Label(employeeContractInfo.getContractInfo().getAgreementCategory());
		category.setTitle(employeeContractInfo.getContractInfo().getAgreementCategory());
		employeesTable.addInlineStyle(category, EMPLOYEE_COL.CAT.getStyles());
		employeesTable.addRow(row, category, EMPLOYEE_COL.CAT.getColWidth());
		
		employeesTable.addRow(row, new Label(formatFullDate.format(employeeContractInfo.getContractInfo().getStartDate())), EMPLOYEE_COL.STA.getColWidth());
		employeesTable.addRow(row, endDateLabel, EMPLOYEE_COL.END.getColWidth());
		employeesTable.addRow(row, buttonContainer, EMPLOYEE_COL.BUT.getColWidth());
	}
	
	private Widget getDeleteMessageWidget(EmployeeContractInfo employeeContractInfo) {
		ContractInfo contractData = employeeContractInfo.getContractInfo();
		EmployeeInfo employeeData = employeeContractInfo.getEmployeeInfo();

		StringBuilder message = new StringBuilder();

		message.append(
				"Este contrato ser\u00E1 eliminado de forma permanente.<br> \u00BFDesea eliminar el contrato de <b>"
						+ employeeData.getFullName() + "</b>?");

		if (null != contractData.getSalariesCount() && contractData.getSalariesCount() > 0) {
			message = new StringBuilder();
			message.append(
					"Este contrato contiene n\u00F3minas existentes. Si lo elimina, se borrar\u00E1n todos los datos de este contrato incluidas las n\u00F3minas.<br> \u00BFDesea eliminar el contrato de <b>"
							+ employeeData.getFullName() + "</b>? <br><br>");
			message.append("<b>N\u00F3minas:</b><br><br>");
			for (ContractSalaryInfo salaryInfo : contractData.getContractSalariesInfo())
				message.append(
						"&emsp;" + salaryInfo.getType() + "&emsp;(" + formatFullDate.format(salaryInfo.getStart())
								+ " - " + formatFullDate.format(salaryInfo.getEnd()) + ")&emsp;Percibido : "
								+ salaryInfo.getTotalLiquid() + "\u20AC<br>");
		}

		return new HTML(message.toString());
	}
	
	private void paintEmployeeNoDataRow() {
		HTMLPanel row = employeesTable.createRow();
		Label noDataLabel = new Label("No hay mas datos");
		employeesTable.addRow(row, noDataLabel, EMPLOYEE_COL.DES.getColWidth());
		
	}

	private void onEmployeeOpen(EmployeeContractInfo employeeContractInfoSelected) {
		if(!contextLoaded)
			return;
		
		Integer contractId = employeeContractInfoSelected.getContractInfo().getContractId();

		Integer selectedEmployeeIdx = getContractListPosition(contractId);
		
		contrataEmployee.setHasCertificateSEPE(mainContrataContractObject.hasCertificateSEPE());
		contrataEmployee.setIsComunica(mainContrataContractObject.isComunica());
		contrataEmployee.setHasPayroll(mainContrataContractObject.hasPayroll());
		ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject();
		contrataEmployeeDialogObject.setActivitiesCCC(mainContrataContractObject.getEnterpriseContext().getActivitiesCCC());
		contrataEmployeeDialogObject.setWorkplaces(mainContrataContractObject.getEnterpriseContext().getWorkplaces());
		contrataEmployeeDialogObject.setAgreements(mainContrataContractObject.getEnterpriseContext().getAgreements());
		contrataEmployeeDialogObject.setPayMethodsMap(mainContrataContractObject.getEnterpriseContext().getPayMethods());
		contrataEmployee.setContrataEmployeeObject(contrataEmployeeDialogObject, contractId, selectedEmployeeIdx,
				mainContrataContractObject.getEmployeesList().size(), 0, s -> deckPanel.showWidget(1));
	}
	
	// Employee List Methods
	
	private boolean checkInactive(EmployeeContractInfo employeeContractInfo) {
		Date currentDate = new Date();
		Date endDate = employeeContractInfo.getContractInfo().getEndDate();
		return null != endDate && DateUtils.isBeforeOrEquals(endDate, currentDate);
	}

	private boolean checkPrevAlta(EmployeeContractInfo employeeContractInfo) {
		Date currentDate = new Date();
		Date startDate = employeeContractInfo.getContractInfo().getStartDate();
		return DateUtils.isAfterOrEquals(startDate, currentDate) && !DateUtils.equals(startDate, currentDate);
	}

	private boolean checkCloseEnd(EmployeeContractInfo employeeContractInfo) {
		Date currentDate = new Date();
		Date endDate = employeeContractInfo.getContractInfo().getEndDate();
		return null != endDate && DateUtils.isBeforeOrEquals(currentDate, endDate) && DateUtils.getDaysBetween(currentDate, endDate) < 30;
	}
	
	private String getContractTypeTitle(String contractType) {
		if(AonStringUtils.isBlank(contractType)) return "";
		try {
			return contractType  + " - " + new ContractType().getContractTypes().get(Integer.parseInt(contractType)).getContractTypeDescription();
		} catch (Exception e) {
			return "";
		}
	}

	// DeckPanel Methods
	
	private void showContracts() {
		deckPanel.showWidget(0);
	}

	private void showPdf(boolean isLaboralLife) {
		deckPanel.showWidget(2);
		checkPDFToolbar(isLaboralLife);
	}
	
	protected void showEnterpriseSalary() {
		if (null == enterpriseSalaryObject) {
			mainContrataContractObject.getEnterprise(
					enterprise -> {
						enterpriseSalaryObject = new EnterpriseSalaryObject(enterprise);
						enterpriseSalary.setEnterpriseSalaryObject(enterpriseSalaryObject);
						enterpriseSalary.hideEditSalaryButton();
					}, 
					f -> {}
			);
		}

		deckPanel.showWidget(3);
	}
	
	private void checkPDFToolbar(boolean isLaboralLife) {
		if(isLaboralLife && pdfDockLayoutPanel.getToolbarButtonCount() == 1) {
			Date currentDate = DateUtils.getFirstDayOfMonth(); 
			
			ListBox monthListBox = new ListBox();
			monthListBox.addItem(formatMonth.format(currentDate) + " de " + formatYear.format(currentDate), currentDate.getTime() + "");
			
			for(int i = 1; i < 4; i++) {
				Date auxDate = DateUtils.addMonths2Date(DateUtils.copyDateOnly(currentDate), -i);
				monthListBox.addItem(formatMonth.format(auxDate) + " de " + formatYear.format(auxDate), auxDate.getTime() + "");
			}
				
			monthListBox.addChangeHandler(e -> onLaboralLifeCahngeDate(new Date(Long.parseLong(monthListBox.getSelectedValue()))));
			monthListBox.setWidth("200px");
			pdfDockLayoutPanel.addToolbarButton(monthListBox);
		} else if(!isLaboralLife && pdfDockLayoutPanel.getToolbarButtonCount() > 1)
			pdfDockLayoutPanel.getToolbarButtonPanel().remove(pdfDockLayoutPanel.getToolbarButtonCount() - 1);
		
	}
	
	// SistemaRED

	private void checkStatus(MainContrataContractObject mainContrataContractObject) {
		mainContrataContractObject.checkStatus(enterpriseStatus -> {
			sistemaREDResults = new SistemaREDResults() {
				
				@Override
				public void up2Date() {
					// Up2Date
				}

				@Override
				public void up2DateEnterprise() {
					this.setUp2DateEnterprise();
				}

				@Override
				public void run() {
					mainContrataContractObject.checkStatus(enterpriseStatus -> {
						removeAll();
						enterpriseStatus.visit(this);
						finish();
					}, throwable -> {
						finish();
					});
				}

				@Override
				protected void newAffiliated(JsSistemaREDResults jsSaltraResults) {
					AonMessagePanel.showLoading(sistemaREDMessagePanel, "Importando Trabajador/es");
					mainContrataContractObject.resetEmployeesList();
					offset = 0;
					onSearch();
					run();
				}

				@Override
				protected void newAffiliated(JsArray<JsSistemaREDResults> jsSaltraResults) {
					AonMessagePanel.showLoading(sistemaREDMessagePanel, "Importados todos los trabajadores.");
					mainContrataContractObject.resetEmployeesList();
					offset = 0;
					onSearch();
					run();
				}
				@Override
				protected void newAffiliated(JsArray<JsSistemaREDResults> jsResults, int total) {
					AonMessagePanel.showLoading(sistemaREDMessagePanel, "Importando Trabajador/es");
				}

				@Override
				protected void newEmployees(AffiliatedNotFound[] affiliatedNotFound) {
					AonMessagePanel.showLoading(sistemaREDMessagePanel, "Importando trabajadores desde la Seguridad Social (Sistema R.E.D)");
					super.newEmployees(affiliatedNotFound);
				}

				@Override
				protected void saltraCredentialsFound() {
					mainContrataContractObject.checkStatus(enterpriseStatus -> {
						removeAll();
						enterpriseStatus.visit(this);
						EnterpriseStatus.ifSistemaREDEnabled(enterpriseStatus, () -> {
							MainContrataContract.this.setSistemaREDVisible(true);
						}, () -> {
							MainContrataContract.this.setSistemaREDVisible(false);
						});
					}, throwable -> {
						MainContrataContract.this.setSistemaREDVisible(false);
					});
				}
				
				protected void init() {
					AonMessagePanel.showLoading(sistemaREDMessagePanel, "Importando trabajadores desde la Seguridad Social (Sistema R.E.D)");
				}
				
				protected void finish() {
					AonMessagePanel.hideMessage(sistemaREDMessagePanel);
				}
			};

			sistemaREDResults.hideToolbar();
			
			enterpriseStatus.visit(sistemaREDResults);
			sistemREDInfoBtn.setVisible(true);
			
			EnterpriseStatus.ifSistemaREDEnabled(
				enterpriseStatus,
				() -> {},
				() -> {}
			);
			
			EnterpriseStatus.ifSistemaREDError(
					enterpriseStatus,
					() -> {},
					() -> {}
			);
			
		}, f -> {
			if(null != sistemaREDMessagePanel)
				AonMessagePanel.hideMessage(sistemaREDMessagePanel);
			MainContrataContract.this.setSistemaREDVisible(false);
			AonMessagePanel.showError(employeesMessagePanel, "Sincronizaci\u00f3n TGSS fallida : " + f.getMessage());
		});
	}
	
	private void setSistemaREDVisible(boolean visible) {
		tgssExpandButton.setVisible(visible);
	}

}
