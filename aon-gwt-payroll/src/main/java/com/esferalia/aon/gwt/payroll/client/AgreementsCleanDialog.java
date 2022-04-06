package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmall;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.AgreementsClean;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public abstract class AgreementsCleanDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface AgreementsCleanDialogUIBinder extends UiBinder<Widget, AgreementsCleanDialog> {}

	private static final AgreementsCleanDialogUIBinder binder = GWT.create(AgreementsCleanDialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flexColumn();
	}
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField(provided = true)
	DataGrid<AgreementsClean> agreementsDG;
	
	@UiField(provided = true)
	AonToolbarSmall contractToolbar;
	
	@UiField
	ScrollPanel contractScrollPanel;
	
	@UiField
	HTMLPanel contractsPanel;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	private final DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	
	// Button
	private Button acceptBtnDialog;
	
	private MultiSelectionModel<AgreementsClean> selectionModel;
	private List<AgreementsClean> agreementsCleanProvider;
	
	private AgreementCleanType cleanType;
	
	// ------------------------------------------------- AgreementCleanType
	
	public enum AgreementCleanType {
		DELETED,
		UNUSED
	}
	
	// ------------------------------------------------- Constructor
	
	protected AgreementsCleanDialog(AgreementCleanType cleanType) {
		
		provideAgreementDataGrid();
		provideContractToolbar();
		
		setCaption(cleanType.equals(AgreementCleanType.DELETED) ? "Papelera convenios" : "Convenios no usados");
		
		setWidget(binder.createAndBindUi(this));
		
		// Buttons footer
		getButtonsPanel();
		
		// Show close dialog button
		this.showCloseButton(true);
		this.acceptBtnDialog.setVisible(false);
		this.cleanType = cleanType;
		
		if(cleanType.equals(AgreementCleanType.UNUSED)) setAgreementsUnusedView();
		
		setGridHeight();
		showTable();
		showDialog();
		loadAgreements();
	}
	
	private void loadAgreements() {
		showLoading("Obteniendo convenios");
		enterprisesService.getAgreementsClean(cleanType, new AsyncCallback<List<AgreementsClean>>() {
			
			@Override
			public void onSuccess(List<AgreementsClean> result) {
				setAgreementsCleanList(result);
				initAgreementsTable();
				hideMessage();
			}

			@Override
			public void onFailure(Throwable error) {
				showError("Convenios : ", error.getMessage());
			}
			
		});
	}
	
	private void setGridHeight() {
		this.agreementsDG.setHeight("350px");
	}
	
	// ------------------------------------------ Provied Contract Toolbar
	
	private void provideContractToolbar() {
		this.contractToolbar = new AonToolbarSmall("Contratos asociados");
		
		AonToolbarSmallButton backBtn = new AonToolbarSmallButton("Volver al listado de convenios", AON.CSS.aonIconBack());
		backBtn.addClickHandler(e -> showTable());
		this.contractToolbar.add(backBtn);
	}
	
	// ------------------------------------------ Provied DataGrid

	private void provideAgreementDataGrid() {
		agreementsCleanProvider = Collections.emptyList();
		
		// Resource Style CellTable
		agreementsDG = new CustomDataGrid<>(Integer.MAX_VALUE, AgreementsClean.KEY_PROVIDER);
		
		//Do not refresh the headers every time the dataGrid is updated.
		agreementsDG.setAutoHeaderRefreshDisabled(true);
		
		// Set the message to display when the table is empty.
		agreementsDG.setEmptyTableWidget(new Label(("No existen convenios").toUpperCase()));
		
		// Add a selection model so we can select cells.
	    this.selectionModel = new MultiSelectionModel<>(AgreementsClean.KEY_PROVIDER);
	    agreementsDG.setSelectionModel(this.selectionModel, DefaultSelectionEventManager.<AgreementsClean> createCheckboxManager());
	    
	    // Initialize the columns.
	    addColumns(this.selectionModel);
	    
	    new ListDataProvider<AgreementsClean>(Collections.emptyList()).addDataDisplay(agreementsDG);
	    
	}
	
	private void addColumns(MultiSelectionModel<AgreementsClean> selectionModel) {
		
		selectionModel.addSelectionChangeHandler(e -> acceptBtnDialog.setVisible(!selectionModel.getSelectedSet().isEmpty()));
	    
		Column<AgreementsClean, Boolean> checkColumn = new Column<AgreementsClean, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(AgreementsClean agreement) {
				return selectionModel.isSelected(agreement);
			}
	    };
    
	    CheckboxCell selectAllHeaderCB = new CheckboxCell(true,true);
	    Header<Boolean> selectAllHeader = new Header<Boolean>(selectAllHeaderCB) {
	    	@Override
	    	public Boolean getValue() {
	    		if(null == agreementsCleanProvider) return false;
	    		return selectionModel.getSelectedSet().size() == agreementsCleanProvider.size();
	    	}
	    	
	    };
	    
	    selectAllHeader.setUpdater(value -> agreementsCleanProvider.forEach(salary -> selectionModel.setSelected(salary, value)));
	    
	    agreementsDG.addColumn(checkColumn,selectAllHeader);
	    agreementsDG.setColumnWidth(checkColumn, 20, Unit.PX);
	    checkColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		// Agreement description column.
	    TextColumn<AgreementsClean> agreementDescriptionColumn = new TextColumn<AgreementsClean>() {
	    	@Override
	    	public String getValue(AgreementsClean agreement) {
	    		return agreement.getDescription();
	    	}
	    };

	    agreementDescriptionColumn.setSortable(true);
	    agreementsDG.setColumnWidth(agreementDescriptionColumn, 300, Unit.PX);
	    
	    // SSNumber column.
	    TextColumn<AgreementsClean> ssNumberColumn = new TextColumn<AgreementsClean>() {
	    	@Override
	    	public String getValue(AgreementsClean agreement) {
	    		return agreement.getSsNumber();
	    	}
	    };

	    ssNumberColumn.setSortable(true);
	    agreementsDG.setColumnWidth(ssNumberColumn, 80, Unit.PX);
	    
	    // Show contracts button
	    ActionCell<AgreementsClean> contractsActionCell = new ActionCell<>("", agreement ->
	    	showContractsAgreement(agreement)
	    );
	    
	    Column<AgreementsClean, AgreementsClean> contractsColumn = new Column<AgreementsClean, AgreementsClean>(contractsActionCell) {

			@Override
			public AgreementsClean getValue(AgreementsClean agreement) {
				return agreement;
			}
			
			@Override
			public void render(Context context, AgreementsClean object, SafeHtmlBuilder sb) {
				if(null != object) {
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_table_button aon_icon_info\" style=\"border: none !important; height: 20px;\" title=\"Ver contratos asociados\"></button>");
				}
			}
		};
		
		agreementsDG.setColumnWidth(contractsColumn, 25, Unit.PX);
		
	    // Add the columns.
		agreementsDG.addColumn(agreementDescriptionColumn, "Descripci\u00f3n");
		agreementsDG.addColumn(ssNumberColumn, "N\u00ba SS");
		agreementsDG.addColumn(contractsColumn, "");   
	}
	
	private void showContractsAgreement(AgreementsClean agreement) {
		showContractsInfo();
		contractsPanel.clear();
		contractsPanel.add(new HTMLPanel(agreement.getContractsInfo()));
	}

	// ------------------------------------------ Init SalaryDG
	
	public void initAgreementsTable() {		
		//Reset Selection Model 
		selectionModel.clear();
		
		// Create a data provider.
	    ListDataProvider<AgreementsClean> dataProvider = new ListDataProvider<>();

	    // Connect the table to the data provider.
	    dataProvider.addDataDisplay(agreementsDG);
	    
	    // Add the data to the data provider, which automatically pushes it to the widget.
	    List<AgreementsClean> agreementList = dataProvider.getList();
	    agreementList.clear();
	    
	    for (AgreementsClean agreement : this.agreementsCleanProvider) {
	    	agreementList.add(agreement);
	    }   
		
		addSortColums(agreementList);
	    
		// Set page size
	    agreementsDG.setPageSize(agreementsCleanProvider.size());
	}

	private void addSortColums(List<AgreementsClean> agreementsList) {
		ListHandler<AgreementsClean> columnSortHandler = new ListHandler<>(agreementsList);
		
	    columnSortHandler.setComparator(agreementsDG.getColumn(1),
	    		(o1, o2) -> compareString(o1, o2, o1.getDescription(), o2.getDescription()));
	    
	    columnSortHandler.setComparator(agreementsDG.getColumn(2),
	    		(o1, o2) -> compareString(o1, o2, o1.getSsNumber(), o2.getSsNumber()));
	    
	    agreementsDG.addColumnSortHandler(columnSortHandler);

	    // We know that the data is sorted alphabetically by default.
	    agreementsDG.getColumn(1).setDefaultSortAscending(false);
	    agreementsDG.getColumnSortList().push(agreementsDG.getColumn(1));   
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}

	// ------------------------------------------------- Auxiliar Methods
	
	public void setAgreementsUnusedView() {
		agreementsDG.removeColumn(3);
	}

	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	// ------------------------------------------------- Setter Methods
	
	public void setAgreementsCleanList(List<AgreementsClean> agreementsCleanList) {
		this.agreementsCleanProvider = agreementsCleanList;
	}
	
	public Set<AgreementsClean> getSelectedAgreements() {
		return this.selectionModel.getSelectedSet();
	}

	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText("Cerrar");
		closeBtnDialog.addClickHandler(e -> hide());
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText("Borrar seleccionados");
		acceptBtnDialog.addClickHandler(e -> accept());
		
		buttonsPanel.add(acceptBtnDialog);
	}

	private void accept() {
		if(this.cleanType.equals(AgreementCleanType.DELETED)) {
			AonDialog confirm = new AonDialog("Contratos asociados", new HTMLPanel("\u00bfDesea eliminar los convenios con contratos asociados\u003f<br>Recuerde que puede consultar los contratos en el icono de informaci\u00f3n de la tabla.<br>Los contratos perderan su enlace con dichos convenios."));
			confirm.confirm(new AonAcceptDialogCallback() {
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept() {
					deleteAgreements();
				}
			});
		} else 
			deleteAgreements();
	}
	
	public void deleteAgreements() {
		messagePanel.getElement().getStyle().clearDisplay();
		showLoading("Borrando convenios seleccionados");
		List<Integer> agreementIds = new ArrayList<>();
		this.selectionModel.getSelectedSet().forEach(agreement -> agreementIds.add(agreement.getAgreementId()));
		enterprisesService.deleteAgreements(agreementIds, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				showError("Borrado convenios", caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				showSuccess("Borrado convenios", "Los convenios han sido elimiandos correctamente");
				reload();
			}
			
		});
	}
	
	public void reload() {
		Timer timer = new Timer() {
		     @Override
		     public void run() {
		    	 loadAgreements();
		     }
		 };
		 timer.schedule(2500);
	}
	
	// ------------------------------------------------- Abstract methods
	
	public abstract void onAccept();
	
	// ------------------------------------------------- DeckPanel
	
	private void showTable() {
		deckPanel.showWidget(0);
		messagePanel.getElement().getStyle().clearDisplay();
		buttonsPanel.setVisible(true);
	}
	
	private void showContractsInfo() {
		deckPanel.showWidget(1);
		messagePanel.getElement().getStyle().setDisplay(Display.NONE);
		buttonsPanel.setVisible(false);
	}
	
	// ------------------------------------------------- Aon Messages panel
	
	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	private void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}
	
	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}
	
	private void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}
	
}
