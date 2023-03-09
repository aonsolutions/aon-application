package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public class MainMassiveContracts extends MainEntryPoint{
	
	// ----------------------------------------------- UiBinder
	
	interface Binder extends UiBinder<Widget, MainMassiveContracts> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ----------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String dockLayoutPanel();
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
	HTMLPanel messagePanel;
	
	@UiField
	Grid contractsDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	Grid contractDataTable;
	
	// ----------------------------------------------- Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private MainMassiveContractsObject mainMassiveContractsObject;
	
	private Map<CheckBox, EmployeeContractInfo> selectionModel;
	
	private boolean hasChange = false;
	
	private AonToolbarButton saveBtn;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton addMasiveValueBtn;
	private AonToolbarButton cnoAFIBtn;
	private ListBox dataType;
	
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
		ui.addStyleName(style.dockLayoutPanel());
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		AonMessagePanel.hideMessage(messagePanel);
	}
	
	// ----------------------------------------------- onModuleLoad
	
	private ListBox createDataTypes() {
		ListBox types = new ListBox();
		types.addItem("CNO", "CNO");
		return types;
	}

	@Override
	public void onModuleLoad() {
		onModuleLoad(new MainMassiveContractsObject());
	}

	public void onModuleLoad(MainMassiveContractsObject mainMassiveContractsObject) {
		this.mainMassiveContractsObject = mainMassiveContractsObject;
		AonMessagePanel.showLoading(messagePanel, "Cargando CNO contratos");
		this.mainMassiveContractsObject.getEmployees(dataType.getSelectedValue(), 
			employees -> {
				selectionModel = new HashMap<>();
				initPreview();
				addMasiveValueBtn.setEnabled(false);
				setHasChange(false);
				AonMessagePanel.showWarning(messagePanel, new HashMap<String, String>(){{ put("Remesa AFI CNO", "Se recomienda notificar la remesa AFI, con el dato CNO actualizado para todos los contratos, antes del 22/03/2023.\nYa que es necesario antes de confirmar las liquidaciones de Seguridad Social de Marzo."); }});
			}, f -> AonMessagePanel.showWarning(messagePanel, new HashMap<String, String>(){{ put("Error Contratos", f.getMessage()); }}));
		
	}
	
	private void initPreview() {
		if(this.mainMassiveContractsObject.getEmployees().isEmpty())
			showContractMessage();
		else {
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
		
		
		Label name = new Label("NOMBRE");
		Label document = new Label("DOCUMENTO");
		Label nss = new Label("NSS");
		Label startDate = new Label("F.INICIO");
		Label endDate = new Label("F.FIN");
		Label data = new Label(dataType.getSelectedValue());
		
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
		default:
			return null;
		}
	}

	private Widget createCNOWidget(EmployeeContractInfo employee) {
		SuggestBox cnoSB = new SuggestBox();
		cnoSB.setWidth("95%");
		
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
		contractsDataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(3, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(27, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(10, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(3).getStyle().setWidth(10, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(4).getStyle().setWidth(10, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(5).getStyle().setWidth(10, Unit.PCT);
		contractsDataTableHeader.getColumnFormatter().getElement(6).getStyle().setWidth(30, Unit.PCT);
		
		contractDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(3, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(27, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(10, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(10, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(10, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(5).getStyle().setWidth(10, Unit.PCT);
		contractDataTable.getColumnFormatter().getElement(6).getStyle().setWidth(30, Unit.PCT);
	}
	
	private String parseDate(Date date) {
		return null == date ? "" : formatDate.format(date);
	}

	private void setScrollHeight() {
		scrollPanel.setHeight((Window.getClientHeight() - 250) + "px");
	}
	
	private void showContractTable() {
		deckPanel.showWidget(0);
	}
	
	private void showContractMessage() {
		deckPanel.showWidget(1);
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
					// For now only CNO
					updateSelectedContractsCNO(getCNOCode());
				}
			};
		});
		
		cnoAFIBtn = new AonToolbarButton("AFI Masivo CNO", AON.CSS.aonIconTgss());
		cnoAFIBtn.addClickHandler(e -> {
			String fileDownloadURL = GWT.getModuleBaseURL() + "employee_cno_afi/" + "?domainId=" + mainMassiveContractsObject.getDomainId();
			Window.open(fileDownloadURL, "_blank", null);
		});
		
		dataType = createDataTypes();
		dataType.setVisible(false);
		
		toolbar.add(addMasiveValueBtn);
		toolbar.add(cnoAFIBtn);
		toolbar.add(dataType);
	}

	private void updateSelectedContractsCNO(String cnoCode) {
		this.selectionModel.forEach((checkBox, employee) -> {
			if(checkBox.getValue()) mainMassiveContractsObject.updateContractCNO(employee, cnoCode);
		});
		initPreview();
		setHasChange(true);
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
