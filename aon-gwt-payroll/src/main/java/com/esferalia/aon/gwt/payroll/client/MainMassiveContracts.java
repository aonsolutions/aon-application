package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class MainMassiveContracts extends MainEntryPoint{
	
	// ----------------------------------------------- UiBinder
	
	interface Binder extends UiBinder<Widget, MainMassiveContracts> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ----------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String filterPanel();
		String flex();
		String gridTitle();
		String headerFixed();
		String headerFSize();
		String modify();
		String oddRow();
		String pl05();
	}
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField
	DeckPanel mainDeckPanel;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	HTMLPanel filterEmployeePanel;
	
	@UiField
	Grid contractsDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	Grid contractDataTable;
	
	@UiField
	SimpleLayoutPanel scrolledPDFPanel;
	
	@UiField
	FullViewer pdfViewer;
	
	// ----------------------------------------------- Variables
	
	private DomainEmployeesServiceAsync impl = DomainEmployeesServiceAsync.newInstance();
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private MainMassiveContractsObject mainMassiveContractsObject;
	
	private Map<CheckBox, EmployeeContractInfo> selectionModel;
	
	private boolean hasChange = false;
	
	private ListBox dataType;
	private TextBox employeeSB;
	private ListBox workplaceLB;
	private HTMLPanel inactiveContractsPanel;
	private CheckBox inactiveContractsCB;
	private HTMLPanel noValuePanel;
	private CheckBox noValueCB;
	private HTMLPanel statusPanel;
	private ListBox statusLB;
	
	private AonToolbarButton saveBtn;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton addMasiveValueBtn;
	private AonToolbarButton cnoAFIBtn;
	private AonToolbarButton backBtn;
	
	// ----------------------------------------------- Constructor

	public MainMassiveContracts() {	
		createToolbar();
		
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);

		getFilterEmployeePanel();
		showList();
		
		scrolledPDFPanel.setHeight((Window.getClientHeight() - 200) + "px");
		
		AonMessagePanel.hideMessage(messagePanel);
	}
	
	// ------------------------------------------ Filter Panel

	private void getFilterEmployeePanel() {
		filterEmployeePanel.addStyleName(AON.CSS.aonSearchPanel());

		HTMLPanel filterPanel = new HTMLPanel("");
		filterPanel.addStyleName(style.filterPanel());
		
		HTMLPanel leftPanel = new HTMLPanel("");
		leftPanel.addStyleName(style.flex());
		
		HTMLPanel typePanel = new HTMLPanel("");
		typePanel.addStyleName(style.flex());
		Label typeL = new Label("Tipo : ");
		typeL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		dataType = createDataTypes();
		dataType.addChangeHandler(e -> dataTypeChange());
		
		typePanel.add(typeL);
		typePanel.add(dataType);

		HTMLPanel employeePanel = new HTMLPanel("");
		employeePanel.addStyleName(style.flex());
		Label employeeL = new Label("Persona : ");
		employeeL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		employeeSB = new TextBox();
		employeeSB.getElement().getStyle().setWidth(300, Unit.PX);
		employeeSB.addKeyUpHandler(e -> {
			String value = employeeSB.getValue();
			if (AonStringUtils.isBlank(value) || value.length() < 3)
				mainMassiveContractsObject.resetEmployeesList();
			else
				mainMassiveContractsObject.filterEmployeesList(value);

			initPreview();
		});
		
		employeePanel.add(employeeL);
		employeePanel.add(employeeSB);

		HTMLPanel showPanel = new HTMLPanel("");
		showPanel.addStyleName(style.flex());
		
		Label workplaceL = new Label("Centro Trabajo : ");
		workplaceL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		workplaceLB = new ListBox();
		workplaceLB.setStyleName("aon-selectOneMenu");

		inactiveContractsPanel = new HTMLPanel("");
		inactiveContractsPanel.addStyleName(style.flex());
		
		Label inactiveL = new Label("Empleados Inactivos");
		inactiveL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		inactiveContractsCB = new CheckBox();
		inactiveContractsCB.addValueChangeHandler(e -> {
			AonMessagePanel.showLoading(messagePanel, e.getValue() ? "Cargando trabajadores (inactivos incluidos) ..." : "Cargando trabajadores activos ...");
			getEmployees(false, finish -> {});
		});
		
		inactiveContractsPanel.add(inactiveL);
		inactiveContractsPanel.add(inactiveContractsCB);
		
		noValuePanel = new HTMLPanel("");
		noValuePanel.addStyleName(style.flex());
		
		Label noValueL = new Label("Sin Valor");
		noValueL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		noValueCB = new CheckBox();
		noValueCB.addValueChangeHandler(e -> {
			this.mainMassiveContractsObject.setHasValue(!e.getValue());
			employeeSB.setValue("");
			if(e.getValue()){
				this.mainMassiveContractsObject.filterHasValue();
				initPreview();
			} else DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.workplaceLB);
		});
		

		noValuePanel.add(noValueL);
		noValuePanel.add(noValueCB);
		
		statusPanel = new HTMLPanel("");
		statusPanel.addStyleName(style.flex());
		
		Label statusL = new Label("Estado : ");
		statusL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		statusLB = new ListBox();
		statusLB.addItem("Todo");
		statusLB.addItem("Llamamiento");
		statusLB.addItem("Pediente comunicar");
		statusLB.addItem("Comunicado");
		statusLB.addChangeHandler(e -> {
			mainMassiveContractsObject.filterEmployeesStatusList(statusLB.getSelectedIndex());
			initPreview();
		});
		

		statusPanel.add(statusL);
		statusPanel.add(statusLB);
		statusPanel.setVisible(false);
		
		leftPanel.add(typePanel);
		leftPanel.add(employeePanel);

		showPanel.add(workplaceL);
		showPanel.add(workplaceLB);
		showPanel.add(inactiveContractsPanel);
		showPanel.add(noValuePanel);
		showPanel.add(statusPanel);

		filterPanel.add(leftPanel);
		filterPanel.add(showPanel);

		filterEmployeePanel.add(filterPanel);
	}
	
	private void dataTypeChange() {
		AonMessagePanel.hideMessage(messagePanel);
		getEmployees(inactiveContractsCB.getValue(), finish -> {
			this.inactiveContractsCB.setValue(false);
			
			boolean isCNOSelected = AonStringUtils.equalsIgnoreCase(this.dataType.getSelectedValue(), "CNO");
			
			saveBtn.setVisible(isCNOSelected);
			undoAllButton.setVisible(isCNOSelected);
			cnoAFIBtn.setVisible(isCNOSelected);
			
			inactiveContractsPanel.setVisible(isCNOSelected);
			noValuePanel.setVisible(isCNOSelected);
			statusPanel.setVisible(!isCNOSelected);
		});
	}
	
	private void getEmployees(boolean allEmployees, Consumer<Void> success) {
		mainMassiveContractsObject.getEmployees(dataType.getSelectedValue(), allEmployees,
				employees -> {
					selectionModel = new HashMap<>();
					this.employeeSB.setValue("");
					this.noValueCB.setValue(false);
					this.workplaceLB.setSelectedIndex(0);
					
					initPreview();
					addMasiveValueBtn.setEnabled(false);
					setHasChange(false);
					
					if( AonStringUtils.equalsIgnoreCase(this.dataType.getSelectedValue(), "CNO"))
						AonMessagePanel.showWarning(messagePanel, new HashMap<String, String>(){{ put("Remesa AFI CNO", "Se recomienda notificar la remesa AFI, con el dato CNO actualizado para todos los contratos, antes del 22/03/2023.\nYa que es necesario antes de confirmar las liquidaciones de Seguridad Social de Marzo."); }});
				
					success.accept(null);
				}, f -> AonMessagePanel.showWarning(messagePanel, new HashMap<String, String>(){{ put("Error Contratos", f.getMessage()); }}));
	}

	private void initWorkplaceLB() {
		workplaceLB.clear();
		workplaceLB.addItem("-", "");
		for (Workplace workplace : mainMassiveContractsObject.getEnterpriseContext().getWorkplaces())
			workplaceLB.addItem(workplace.getDescription(), workplace.getId().toString());
		
		workplaceLB.addChangeHandler(e -> {
			String workplaceIdStr = workplaceLB.getSelectedValue();
			if (AonStringUtils.isBlank(workplaceIdStr))
				mainMassiveContractsObject.resetEmployeesList();
			else {
				Integer workplaceId = Integer.parseInt(workplaceIdStr);
				mainMassiveContractsObject.filterEmployeesList(workplaceId);
			}

			initPreview();
		});
	}
	
	// ----------------------------------------------- onModuleLoad
	
	private ListBox createDataTypes() {
		ListBox types = new ListBox();
		types.addItem("CNO", "CNO");
		types.addItem("Fijo/Discontinuo", "Llamamiento");
		return types;
	}

	@Override
	public void onModuleLoad() {
		onModuleLoad(new MainMassiveContractsObject());
	}

	public void onModuleLoad(MainMassiveContractsObject mainMassiveContractsObject) {
		this.mainMassiveContractsObject = mainMassiveContractsObject;
		AonMessagePanel.showLoading(messagePanel, "Cargando contratos");
		this.mainMassiveContractsObject.getContextInfo(
				s -> {
					initWorkplaceLB();
					getEmployees(false, finish -> {
						this.inactiveContractsCB.setValue(false);
					});
				}, f -> {}
		);
		
	}
	
	private void initPreview() {
		if(this.mainMassiveContractsObject.getEmployees().isEmpty()) {
			showContractMessage();
		} else {
			selectionModel.clear();
			
			showContractTable();
			contractsDataTableHeader.clear();
			contractsDataTableHeader.resize(0, 0);
			contractsDataTableHeader.resizeColumns(7);
			
			contractDataTable.clear();
			contractDataTable.resize(0, 0);
			contractDataTable.resizeColumns(7);
			
			paintHeader();
			fillEmployeesTable();
			setColumnWidth();
			setScrollHeight();
		}
	}

	private void paintHeader() {
		int row = contractsDataTableHeader.insertRow(contractsDataTableHeader.getRowCount());
		
		CheckBox select = new CheckBox();
		select.addValueChangeHandler(e -> {
			selectionModel.forEach((checkBox, employee) -> checkBox.setValue(e.getValue()));
			checkAddButton();
		});
		
		boolean isCNOSelected = AonStringUtils.equalsIgnoreCase(this.dataType.getSelectedValue(), "CNO");
		
		Label name = new Label("NOMBRE");
		Label document = new Label("DOCUMENTO");
		Label nss = new Label("NSS");
		Label startDate = new Label("F.INICIO");
		Label endDate = new Label("F.FIN");
		Label data = new Label(isCNOSelected ? "C.N.O." : "F. LLAMAMIENTO");
		
		select.addStyleName(style.gridTitle());
		select.addStyleName(style.headerFSize());
		name.addStyleName(style.gridTitle());
		name.addStyleName(style.headerFSize());
		document.addStyleName(style.gridTitle());
		document.addStyleName(style.headerFSize());
		nss.addStyleName(style.gridTitle());
		nss.addStyleName(style.headerFSize());
		startDate.addStyleName(style.gridTitle());
		startDate.addStyleName(style.headerFSize());
		endDate.addStyleName(style.gridTitle());
		endDate.addStyleName(style.headerFSize());
		data.addStyleName(style.gridTitle());
		data.addStyleName(style.headerFSize());
		
		contractsDataTableHeader.setWidget(row, 0, select);
		contractsDataTableHeader.setWidget(row, 1, name);
		contractsDataTableHeader.setWidget(row, 2, document);
		contractsDataTableHeader.setWidget(row, 3, nss);
		contractsDataTableHeader.setWidget(row, 4, startDate);
		contractsDataTableHeader.setWidget(row, 5, endDate);
		contractsDataTableHeader.setWidget(row, 6, data);
		
		contractsDataTableHeader.getCellFormatter().addStyleName(row, 0, style.headerFixed());
		contractsDataTableHeader.getCellFormatter().addStyleName(row, 1, style.headerFixed());
		contractsDataTableHeader.getCellFormatter().addStyleName(row, 2, style.headerFixed());
		contractsDataTableHeader.getCellFormatter().addStyleName(row, 3, style.headerFixed());
		contractsDataTableHeader.getCellFormatter().addStyleName(row, 4, style.headerFixed());
		contractsDataTableHeader.getCellFormatter().addStyleName(row, 5, style.headerFixed());
		contractsDataTableHeader.getCellFormatter().addStyleName(row, 6, style.headerFixed());
	}
	
	private void fillEmployeesTable() {
		for (EmployeeContractInfo employee : this.mainMassiveContractsObject.getEmployees()) {
			int row = contractDataTable.insertRow(contractDataTable.getRowCount());
			
			CheckBox select = new CheckBox();
			select.addValueChangeHandler(e -> checkAddButton() );
			
			Label name = new Label(employee.getEmployeeInfo().getFullName());
			Label document = new Label(employee.getEmployeeInfo().getDocument());
			Label nss = new Label(employee.getEmployeeInfo().getSsNumber());
			Label startDate = new Label(parseDate(employee.getContractInfo().getStartDate()));
			Label endDate = new Label(parseDate(employee.getContractInfo().getEndDate()));
			Widget dataWidget = createDataWidget(employee);
			
			checkRowAndModify(contractDataTable, row, employee, select);
			checkRowAndModify(contractDataTable, row, employee, name);
			checkRowAndModify(contractDataTable, row, employee, document);
			checkRowAndModify(contractDataTable, row, employee, nss);
			checkRowAndModify(contractDataTable, row, employee, startDate);
			checkRowAndModify(contractDataTable, row, employee, endDate);
			checkRowAndModify(contractDataTable, row, employee, dataWidget);
			
			select.addStyleName(style.pl05());
			name.addStyleName(style.pl05());
			document.addStyleName(style.pl05());
			document.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			nss.addStyleName(style.pl05());
			nss.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			startDate.addStyleName(style.pl05());
			startDate.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			endDate.addStyleName(style.pl05());
			endDate.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			dataWidget.addStyleName(style.pl05());
			
			if(AonStringUtils.equalsIgnoreCase(dataType.getSelectedValue(), "CNO") || 
				(AonStringUtils.equalsIgnoreCase(dataType.getSelectedValue(), "Llamamiento") && (null != employee.getContractInfo().getEndDate() || employee.getContractInfo().getStartDate().before(new Date()))))
				contractDataTable.setWidget(row, 0, select);
			
			contractDataTable.setWidget(row, 1, name);
			contractDataTable.setWidget(row, 2, document);
			contractDataTable.setWidget(row, 3, nss);
			contractDataTable.setWidget(row, 4, startDate);
			contractDataTable.setWidget(row, 5, endDate);
			contractDataTable.setWidget(row, 6, dataWidget);
			
			if (row % 2 == 0) {
				contractDataTable.getCellFormatter().addStyleName(row, 0, style.oddRow());
				contractDataTable.getCellFormatter().addStyleName(row, 1, style.oddRow());
				contractDataTable.getCellFormatter().addStyleName(row, 2, style.oddRow());
				contractDataTable.getCellFormatter().addStyleName(row, 3, style.oddRow());
				contractDataTable.getCellFormatter().addStyleName(row, 4, style.oddRow());
				contractDataTable.getCellFormatter().addStyleName(row, 5, style.oddRow());
				contractDataTable.getCellFormatter().addStyleName(row, 6, style.oddRow());
			}
			
			contractDataTable.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);
			
			if(AonStringUtils.equalsIgnoreCase(dataType.getSelectedValue(), "CNO") || 
					(AonStringUtils.equalsIgnoreCase(dataType.getSelectedValue(), "Llamamiento") && (null != employee.getContractInfo().getEndDate() || employee.getContractInfo().getStartDate().before(new Date()))))
				selectionModel.put(select, employee);
		}
	}
	
	private void checkAddButton() {
		Optional<CheckBox> someSelected = this.selectionModel.keySet().stream().filter(checkBox -> checkBox.getValue()).findAny();
		addMasiveValueBtn.setEnabled(someSelected.isPresent());
	}

	private Widget createDataWidget(EmployeeContractInfo employee) {
		switch (dataType.getSelectedValue()) {
		case "CNO":
			return createCNOWidget(employee);
		case "Llamamiento":
			return createFJWidget(employee);
		default:
			return null;
		}
	}

	private Widget createCNOWidget(EmployeeContractInfo employee) {
		SuggestBox cnoSB = new SuggestBox();
		cnoSB.setWidth("100%");
		
		List<String> cnoSuggest = new ArrayList<>();
		for(Entry<String, CNO> entry : mainMassiveContractsObject.getCNOs().entrySet())
			cnoSuggest.add(entry.getKey() + " - " + entry.getValue().getTitle());
		
		cnoSuggest.sort((o1, o2) -> o1.compareTo(o2));

		MultiWordSuggestOracle orclCno = (MultiWordSuggestOracle) cnoSB.getSuggestOracle();
		orclCno.addAll(cnoSuggest);
		orclCno.setDefaultSuggestionsFromText(cnoSuggest);
		cnoSB.setAutoSelectEnabled(false);
		cnoSB.getElement().setPropertyString("placeholder", "C\u00f3digo CNO... (Ctrl + espacio para ver sugerencias)");
		
		cnoSB.getValueBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				cnoSB.setText("");
				cnoSB.showSuggestionList();
			} else if(e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
				cnoSB.hideSuggestionList();
		});
		
		cnoSB.addSelectionHandler(e -> {
			String cnoCode = cnoSB.getValue();
			if(!AonStringUtils.isBlank(cnoCode))
				cnoCode = cnoCode.split(" -")[0];
			
			mainMassiveContractsObject.updateContractCNO(employee, cnoCode);
			initPreview();
			setHasChange(true);
		});
		
		Optional<ContractData> cno = mainMassiveContractsObject.getContractData(employee.getContractInfo().getContractId(), "CNO");
		if(cno.isPresent()) {
			String cnoCode = cno.get().getExpression().split("\"")[1];
			cnoSB.setValue(mainMassiveContractsObject.getCNODescription(cnoCode));
		}
		
		return cnoSB;
	}
	
	private Widget createFJWidget(EmployeeContractInfo employee) {
		if(null == employee.getContractInfo().getEndDate() || employee.getContractInfo().getStartDate().after(new Date())) {
			HTMLPanel panel = new HTMLPanel("");
			panel.addStyleName(AON.CSS.aonItemFlex());
			panel.getElement().getStyle().setProperty("justify-content", "center");
			
			AonTableButton tgss = new AonTableButton(employee.getContractInfo().isSSComunicate() ? "Descargar TA" : "Comunicar Contrato TGSS", AON.CSS.aonIconTgss());
			tgss.addClickHandler(e -> {
				if(employee.getContractInfo().isSSComunicate()) {
					AonMessagePanel.showLoading(messagePanel, "Obteniendo TA ...");
					impl.getEmployeeTa(
							employee.getContractInfo().getContractId(), 
							"ALTA", 
							employee.getContractInfo().getCompleteCCC().substring(0, 4), 
							employee.getContractInfo().getCompleteCCC().substring(4, employee.getContractInfo().getCompleteCCC().length()), 
							employee.getEmployeeInfo().getSsNumber(), 
							employee.getContractInfo().getStartDate(), 
							new AsyncCallback<String>() {
						
								@Override
								public void onSuccess(String dataURI) {
									AonMessagePanel.hideMessage(messagePanel);
									showPdf();
									pdfViewer.open(dataURI);
								}
								@Override
								public void onFailure(Throwable caught) {
									AonMessagePanel.showError(messagePanel, new HashMap<String, String>(){{ put("Error Obtenci\u00f3n TA", caught.getMessage()); }});
								}
					});
				} else {
					AonMessagePanel.showLoading(messagePanel, "Comunicando contrato TGSS ...");
					impl.getEmployeeInfoDataBase(employee.getContractInfo().getContractId(), null, new AsyncCallback<EmployeeContractInfo>() {
						
						@Override
						public void onSuccess(EmployeeContractInfo employeeDB) {
							impl.sendEmployeeAlta(employeeDB, new AsyncCallback<Void>() {
								
								@Override
								public void onSuccess(Void result) {
									AonMessagePanel.showSuccess(messagePanel, new HashMap<String, String>(){{ put("Comunicaci\u00f3n Contrato TGSS", "Se ha comunicado correctamente el contrato a la TGSS"); }});
									reloadAfterTimer();
								}
								
								@Override
								public void onFailure(Throwable caught) {
									AonMessagePanel.showError(messagePanel, new HashMap<String, String>(){{ put("Error Comunicaci\u00f3n Contrato TGSS", caught.getMessage()); }});
								}
							});
						}
						
						@Override
						public void onFailure(Throwable arg0) {
							// Nothing to do
						}
					});
	
				}
			});
			
			AonTableButton sepe = new AonTableButton(employee.getContractInfo().isSepeComunicate() ? "Descargar Copia Contrato" : "Comunicar Contrato SEPE", AON.CSS.aonIconSepe());
			sepe.addClickHandler(e -> {
				if(employee.getContractInfo().isSepeComunicate()) {
					AonMessagePanel.showLoading(messagePanel, "Obteniendo CTO ...");
					impl.getEmployeeInfoDataBase(employee.getContractInfo().getContractId(), null, new AsyncCallback<EmployeeContractInfo>() {
						
						@Override
						public void onSuccess(EmployeeContractInfo employeeDB) {
							impl.getEmployeeCto(employeeDB.getEmployeeInfo().getDocument(), employeeDB.getContractInfo().getContractId(), employeeDB.getContractInfo().getStartDate(), employeeDB.getContractInfo().getStartDate(), employeeDB.getContractInfo().getSepeId(), new AsyncCallback<String>() {
								@Override
								public void onSuccess(String dataURI) {
									AonMessagePanel.hideMessage(messagePanel);
									showPdf();
									pdfViewer.open(dataURI);
								}
								@Override
								public void onFailure(Throwable caught) {
									AonMessagePanel.showError(messagePanel, new HashMap<String, String>(){{ put("Error Obtenci\u00f3n CTO", caught.getMessage()); }});
								}
							});
						}
						
						@Override
						public void onFailure(Throwable arg0) {
							// Nothing to do
						}
					});
				} else {
					AonDialog dialog = new AonDialog("Desarrollo", new Label("Esta opci\u00f3n est\u00e1 en desarrollo"));
					dialog.info();
//					AonMessagePanel.showLoading(messagePanel, "Comunicando contrato SEPE ...");
//					impl.getEmployeeInfoDataBase(employee.getContractInfo().getContractId(), null, new AsyncCallback<EmployeeContractInfo>() {
//						
//						@Override
//						public void onSuccess(EmployeeContractInfo employeeDB) {
//							impl.sendLlamamientoSEPE(employeeDB, new AsyncCallback<Void>() {
//
//								@Override
//								public void onFailure(Throwable caught) {
//									AonMessagePanel.showError(messagePanel, new HashMap<String, String>(){{ put("Error Comunicaci\u00f3n Contrato SEPE", caught.getMessage()); }});
//								}
//
//								@Override
//								public void onSuccess(Void result) {
//									AonMessagePanel.showSuccess(messagePanel, new HashMap<String, String>(){{ put("Comunicaci\u00f3n Contrato SEPE", "Se ha comunicado correctamente el contrato al SEPE"); }});
//									reloadAfterTimer();
//								}
//								
//							});
//						}
//						
//						@Override
//						public void onFailure(Throwable arg0) {
//							// Nothing to do
//						}
//					});
				}
			});

			panel.add(tgss);
			panel.add(sepe);
			
			return panel;
		} else {
			AonDateBox dateBox = new AonDateBox();
			dateBox.setWidth("100%");
			dateBox.addValueChangeHandler(e -> {
				if(null != e.getValue()) {
					AonDialog fdDialog = new AonDialog("Llamamiento (Fijo/Discontinuo)", new HTMLPanel("\u00bfDesea realmente hacer un llamamiento al trabajador " + employee.getEmployeeInfo().getFullName() + " con fecha de inicio " + formatDate.format(e.getValue()) + "\u003f"));
					fdDialog.confirm(new AonAcceptDialogCallback() {
	
						@Override
						public void onCancel() {
							// Not use here
						}
	
						@Override
						public void onAccept() {
							AonMessagePanel.showLoading(messagePanel, "Creando contrato del llamamiento ...");
							mainMassiveContractsObject.duplicateContract(
									employee, 
									e.getValue(), 
									s -> {
										AonMessagePanel.showSuccess(messagePanel, new HashMap<String, String>(){{ put("Llamamiento (Fijo/Discontinuo)", "Se ha generado el llamamiento correctamente, puede encontrar el nuevo contrato en la parte Laboral > Contratos"); }});
										reloadAfterTimer();
									}, 
									f -> AonMessagePanel.showError(messagePanel, new HashMap<String, String>(){{ put("Error Llamamiento", f.getMessage()); }}));
						}
					});
				}
			});
			
			return dateBox;
		}
	}
	
	private void checkRowAndModify(Grid grid, int row, EmployeeContractInfo employee, Widget widget) {
		if (row % 2 == 0)
			widget.addStyleName(style.oddRow());

		if (employee.isModify()) {
			widget.addStyleName(style.modify());

			grid.getCellFormatter().addStyleName(row, 0, style.modify());
			grid.getCellFormatter().addStyleName(row, 1, style.modify());
			grid.getCellFormatter().addStyleName(row, 2, style.modify());
			grid.getCellFormatter().addStyleName(row, 3, style.modify());
			grid.getCellFormatter().addStyleName(row, 4, style.modify());
			grid.getCellFormatter().addStyleName(row, 5, style.modify());
			grid.getCellFormatter().addStyleName(row, 6, style.modify());
		} else {
			widget.removeStyleName(style.modify());

			grid.getCellFormatter().removeStyleName(row, 0, style.modify());
			grid.getCellFormatter().removeStyleName(row, 1, style.modify());
			grid.getCellFormatter().removeStyleName(row, 2, style.modify());
			grid.getCellFormatter().removeStyleName(row, 3, style.modify());
			grid.getCellFormatter().removeStyleName(row, 4, style.modify());
			grid.getCellFormatter().removeStyleName(row, 5, style.modify());
			grid.getCellFormatter().removeStyleName(row, 6, style.modify());
		}

	}

	private void setColumnWidth() {
		boolean isCNOSelected = AonStringUtils.equalsIgnoreCase(this.dataType.getSelectedValue(), "CNO");
		
		contractsDataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(3, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(isCNOSelected ? 27 : 47, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(10, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(3).getStyle().setWidth(10, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(4).getStyle().setWidth(10, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(5).getStyle().setWidth(10, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(6).getStyle().setWidth(isCNOSelected ? 30 : 10, Unit.PCT);
		
		contractDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(3, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(isCNOSelected ? 27 : 47, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(10, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(10, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(10, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(5).getStyle().setWidth(10, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(6).getStyle().setWidth(isCNOSelected ? 30 : 10, Unit.PCT);
	}
	
	private String parseDate(Date date) {
		return null == date ? "" : formatDate.format(date);
	}

	private void setScrollHeight() {
		scrollPanel.setHeight((Window.getClientHeight() - 290) + "px");
	}
	
	private void showContractTable() {
		deckPanel.showWidget(0);
		cnoAFIBtn.setEnabled(true);
	}
	
	private void showContractMessage() {
		deckPanel.showWidget(1);
		cnoAFIBtn.setEnabled(false);
	}
	
	private void showList() {
		mainDeckPanel.showWidget(0);
		
		boolean isCNOSelected = AonStringUtils.equalsIgnoreCase(this.dataType.getSelectedValue(), "CNO");
		
		saveBtn.setVisible(isCNOSelected);
		undoAllButton.setVisible(isCNOSelected);
		cnoAFIBtn.setVisible(isCNOSelected);
		addMasiveValueBtn.setVisible(true);
		backBtn.setVisible(false);
		
		inactiveContractsPanel.setVisible(isCNOSelected);
		noValuePanel.setVisible(isCNOSelected);
		statusPanel.setVisible(!isCNOSelected);
	}
	
	private void showPdf() {
		mainDeckPanel.showWidget(1);
		
		saveBtn.setVisible(false);
		undoAllButton.setVisible(false);
		addMasiveValueBtn.setVisible(false);
		cnoAFIBtn.setVisible(false);
		backBtn.setVisible(true);
	}
	
	// ----------------------------------------------- Toolbar
	
	private void createToolbar() {
		this.toolbar = new AonToolbar("Cambio masivo contratos");
		
		saveBtn = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveBtn.ensureDebugId("acceptButton");
		saveBtn.addClickHandler(e -> {
			AonMessagePanel.showLoading(messagePanel, "Guardando contratos ...");
			setHasChange(false);
			mainMassiveContractsObject.updateContractData(dataType.getSelectedValue(), 
					s -> {
						AonMessagePanel.hideMessage(messagePanel);
						onModuleLoad(mainMassiveContractsObject);
					}, f -> {});
		});

		toolbar.add(saveBtn);

		undoAllButton = new AonToolbarButton(AON.MSG.undo(), AON.CSS.aonIconUndoAll());
		undoAllButton.ensureDebugId("undoAllButton");
		undoAllButton.addClickHandler(e -> {
			AonDialog deleteDialog = new AonDialog("Restaurar contratos", new HTMLPanel("\u00bfDesea realmente deshacer los cambios sin guardar de los contratos\u003f"));
			deleteDialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					// Not use here
				}

				@Override
				public void onAccept() {
					AonMessagePanel.showLoading(messagePanel, "Deshaciendo cambios de los contratos ...");
					setHasChange(false);
					onModuleLoad(mainMassiveContractsObject);
				}
			});
		});

		toolbar.add(undoAllButton);
		
		addMasiveValueBtn = new AonToolbarButton(AON.MSG.newAction() + " valor masivo", AON.CSS.aonIconAdd());
		addMasiveValueBtn.setEnabled(false);
		addMasiveValueBtn.addClickHandler(e -> {
			new MassiveContractDataDialog(dataType.getSelectedValue()) {
				
				@Override
				protected Map<String, CNO> onGetCNOs() {
					return mainMassiveContractsObject.getCNOs();
				}
				
				@Override
				protected void onAccept() {
					switch (dataType.getSelectedValue()) {
					case "CNO":
						updateSelectedContractsCNO(getCNOCode());
						break;
					case "Llamamiento":
						updateSelectedContractsFD(getStartDate());
						break;
					default:
						break;
					}
				}
			};
		});
		
		cnoAFIBtn = new AonToolbarButton("AFI Masivo CNO", AON.CSS.aonIconTgss());
		cnoAFIBtn.addClickHandler(e -> {
			String fileDownloadURL = GWT.getModuleBaseURL() + "employee_cno_afi/" + "?domainId=" + mainMassiveContractsObject.getDomainId();
			Window.open(fileDownloadURL, "_blank", null);
		});
		
		toolbar.add(addMasiveValueBtn);
		toolbar.add(cnoAFIBtn);
		
		backBtn = new AonToolbarButton("Listado", AON.CSS.aonIconBack());
		backBtn.addClickHandler(e -> {
			showList();
		});
		
		toolbar.add(backBtn);
	}

	private void updateSelectedContractsCNO(String cnoCode) {
		this.selectionModel.forEach((checkBox, employee) -> {
			if(checkBox.getValue()) mainMassiveContractsObject.updateContractCNO(employee, cnoCode);
		});
		initPreview();
		setHasChange(true);
	}
	
	private void updateSelectedContractsFD(Date startDate) {
		List<EmployeeContractInfo> selectedEmployees = new ArrayList<>();
		this.selectionModel.forEach((checkBox, employee) -> {
			if(checkBox.getValue()) selectedEmployees.add(employee);
		});
		
		AonMessagePanel.showLoading(messagePanel, "Creando contratos del llamamiento ...");
		mainMassiveContractsObject.duplicateContract(
				selectedEmployees, 
				startDate, 
				s -> {
					AonMessagePanel.showSuccess(messagePanel, new HashMap<String, String>(){{ put("Llamamiento (Fijo/Discontinuo)", "Se ha generado el llamamiento correctamente, puede encontrar los nuevos contratos en la parte Laboral > Contratos"); }});
					reloadAfterTimer();
				}, 
				f -> AonMessagePanel.showError(messagePanel, new HashMap<String, String>(){{ put("Error Llamamiento", f.getMessage()); }}));
	}
	
	private void reloadAfterTimer() {
		Timer delay = new Timer() {
			@Override
			public void run() {
				dataTypeChange();
			}
		};
		delay.schedule(2500);
	}
	
	// ------------------------------------------ HasChange

	public boolean hasChange() {
		return hasChange;
	}

	public void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		if (hasChange()) {
			saveBtn.getElement().getStyle().clearDisplay();
			undoAllButton.getElement().getStyle().clearDisplay();
		}
		saveBtn.setEnabled(hasChange());
		undoAllButton.setEnabled(hasChange());
	}

	

}
