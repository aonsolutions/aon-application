package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractClauseUI extends ResizeComposite {

	// -------------------------------------------------- UiBinder

	private static ContractClauseUIBinder uiBinder = GWT.create(ContractClauseUIBinder.class);

	interface ContractClauseUIBinder extends UiBinder<Widget, ContractClauseUI> {}

	// -------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerLabelStyle();
		String maxWidthTB();
		String maxWidthLB();
		String clauseTD();
		String flex();
		String loading();
	}
	
	@UiField
	VerticalPanel clausesTable;

	@UiField
	Grid clausesDataTableHeader;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	ScrollPanel clausesScrollPanel;
	
	@UiField
	Grid clausesDataTable;

	@UiField
	HTMLPanel loadingClausesPanel;
	
	// ------------------------------------------------------ Constructor
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private EmployeeContractInfo employeeContractInfo;
	private Integer contractId;
	
	protected ContractClauseUI() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	// ------------------------------------------------------ setEmployeeContractInfo
	
	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		this.contractId = this.employeeContractInfo.getContractInfo().getContractId();
		
		reloadCaluses();
	}
	
	private void reloadCaluses() {
		initializeView();
		
		getContractClauses(s -> {
			if(employeeContractInfo.getContractClauses().isEmpty())
				showEmptyTable();
			else 
				loadClauses();
		}, f -> showErrorMessage("Error obtenci\u00f3n Clausulas", f.getMessage()));
	}

	private void loadClauses() {
		resetClauseDataTableStructure();
		showMainTable();
		for(ContractClause contractClause : employeeContractInfo.getContractClauses())
			if(contractClause.getId() == null || contractClause.getId() > 0)
				paintContractClause(contractClause);
	}

	// ------------------------------------------------------ Initialize View

	private void initializeView() {
		initClausesTable();
		paintHeaderClausesTable();
		
		setScrollPanelsHeight();
		setColumnsWidth();
		
		showLoadingPanel();
	}

	private void initClausesTable() {
		clausesDataTableHeader.clear();
		clausesDataTableHeader.resize(0, 0);
		clausesDataTableHeader.resizeColumns(4);
		clausesDataTable.clear();
		clausesDataTable.resize(0, 0);
		clausesDataTable.resizeColumns(4);
	}
	
	private void paintHeaderClausesTable() {
		int row = clausesDataTableHeader.insertRow(clausesDataTableHeader.getRowCount());
		
		Label line = new Label("L\u00CDNEA");
		Label name = new Label("NOMBRE");
		Label description = new Label("DESCRIPCI\u00D3N");
		Label action = new Label("");
		
		line.addStyleName(style.headerLabelStyle());
		name.addStyleName(style.headerLabelStyle());
		description.addStyleName(style.headerLabelStyle());
		action.addStyleName(style.headerLabelStyle());
		
		clausesDataTableHeader.setWidget(row, 0, line);
		clausesDataTableHeader.setWidget(row, 1, name);
		clausesDataTableHeader.setWidget(row, 2, description);
		clausesDataTableHeader.setWidget(row, 3, action);
	}
	
	private void setScrollPanelsHeight() {
		clausesScrollPanel.setHeight((Window.getClientHeight() - 390) + "px");
	}
	
	private void setColumnsWidth() {
		clausesDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(100, Unit.PX);
		clausesDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(200, Unit.PX);
		clausesDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(595, Unit.PX);
		clausesDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(50, Unit.PX);
		
		clausesDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(100, Unit.PX);
		clausesDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		clausesDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(595, Unit.PX);
		clausesDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(50, Unit.PX);
		
		clausesDataTable.getColumnFormatter().addStyleName(0, style.clauseTD());
		clausesDataTable.getColumnFormatter().addStyleName(1, style.clauseTD());
		clausesDataTable.getColumnFormatter().addStyleName(3, style.clauseTD());	
	}
	
	private void showLoadingPanel() {
		AonToolbarSmallButton loadingBtn = new AonToolbarSmallButton("Cargando Clausulas", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loading());
		Label loadingLabel = new Label("Cargando clausulas ...");

		loadingClausesPanel.clear();
		loadingClausesPanel.add(loadingBtn);
		loadingClausesPanel.add(loadingLabel);

		deckPanel.showWidget(1);
	}

	private void showMainTable() {
		deckPanel.showWidget(0);
	}
	
	private void showEmptyTable() {
		deckPanel.showWidget(2);
	}
	
	// ------------------------------------------------------ PaintContractAttach
	
	private void paintContractClause(ContractClause contractClause) {
		// Insert new row
		int row = this.clausesDataTable.insertRow(clausesDataTable.getRowCount());
		
		// Line TextBox
		TextBox lineTB = new TextBox();
		lineTB.setText(contractClause.getLineNumber() + "");
		lineTB.addValueChangeHandler(e -> contractClause.setLineNumber(Short.parseShort(e.getValue())));
		
		// Name TextBox
		TextBox nameTB = new TextBox();
		nameTB.setText(contractClause.getName());
		nameTB.addValueChangeHandler(e -> contractClause.setName(e.getValue()));
		
		// Description TextBox
		TextArea descriptionTA = new TextArea();
		descriptionTA.setValue(contractClause.getDescription());
		descriptionTA.addValueChangeHandler(e -> contractClause.setDescription(e.getValue()));
		
		// Delete Button
		AonTableButton deleteBTN = new AonTableButton("Eliminar", AON.CSS.aonIconDelete());
		deleteBTN.addClickHandler(e -> {
			if(contractClause.getId() == null) {
				employeeContractInfo.getContractClauses().remove(contractClause);
				resetClauseDataTableStructure();
				loadClauses();
			} else {
				AonDialog deleteDialog = new AonDialog("Eliminar clasula", new HTML("\u00BFDesea eliminar esta clausula\u003F"));
				deleteDialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						// Nothing to do here
					}
					
					@Override
					public void onAccept() {
				    	deleteContractClause(
				    			contractClause.getId(), 
				    			s -> {
				    				showSuccessMessage("Borrado Clausula", "La clausula ha sido eliminada correctamente");
				    				reloadCaluses();
				    			}, 
				    			f -> showErrorMessage("Borrado Clausula", f.getMessage()));
					}
				});
			}
			
		});
		
		// Add Styles
		lineTB.addStyleName(style.maxWidthTB());
		nameTB.addStyleName(style.maxWidthTB());
		descriptionTA.addStyleName(style.maxWidthTB());
		descriptionTA.setHeight("95px");
		
		// It null == contract ? global_attachs cant be deleted
		if(null == contractClause.getContract()) {
			lineTB.setReadOnly(true);
			nameTB.setReadOnly(true);
			descriptionTA.setReadOnly(true);
		}
		
		clausesDataTable.setWidget(row, 0, lineTB);
		clausesDataTable.setWidget(row, 1, nameTB);
		clausesDataTable.setWidget(row, 2, descriptionTA);
		if(null != contractClause.getContract())
			clausesDataTable.setWidget(row, 3, deleteBTN);
		else
			clausesDataTable.setWidget(row, 3, new Label());
		
		clausesDataTable.getCellFormatter().addStyleName(row, 0, style.clauseTD());
		clausesDataTable.getCellFormatter().addStyleName(row, 1, style.clauseTD());
		clausesDataTable.getCellFormatter().addStyleName(row, 3, style.clauseTD());
	}
	
	// ------------------------------------------------------ Toolbar methods
	
	public void newClause() {
		ContractClause contractClause = new ContractClause();
		contractClause.setDomain(employeeContractInfo.getEmployeeInfo().getDomain());
		contractClause.setContract(employeeContractInfo.getContractInfo().getContractId());
		contractClause.setLineNumber((short)(employeeContractInfo.getContractClauses().size()+1));
		contractClause.setDescription("");
		employeeContractInfo.getContractClauses().add(contractClause);
		
		loadClauses();
	}
	
	public void importClause() {
		new ImportClauseDialog(employeeContractInfo.getContractClauses()) {
			
			@Override
			protected void onClausesImport(List<Integer> clausesIds) {
				this.hide();
				importContractClauses(
						clausesIds, 
						s -> {
							showSuccessMessage("Importaci\u00f3n Clausulas", "Clausulas importadas correctamente");
							reloadCaluses();
						}, f -> showErrorMessage("Importaci\u00f3n Clausulas", f.getMessage()));
			}
		};
	}
	
	public void saveClauses() {
		saveContractClause(s -> {
			showSuccessMessage("Clausulas", "Clausulas guardadas correctamente");
			getContractClauses(su -> {
				if(employeeContractInfo.getContractClauses().isEmpty())
					showEmptyTable();
				else 
					loadClauses();
			}, f -> showErrorMessage("Error obtenci\u00f3n Clausulas", f.getMessage()));
		}, f -> {});
	}
	
	// ------------------------------------------------------ Abstract Methods

	protected abstract void showErrorMessage(String title, String message);

	protected abstract void showSuccessMessage(String title, String message);
	
	protected abstract void showLoadingMessage(String message);
	
	// ------------------------------------------------------ Refresh table

	private void resetClauseDataTableStructure() {
		clausesDataTable.clear();
		clausesDataTable.resize(0, 0);
		clausesDataTable.resizeColumns(4);
		
		setColumnsWidth();
	}
	
	// ------------------------------------------------------ ContractClause.CRUD
	
	private void saveContractClause(Consumer<Void> success, Consumer<Throwable> failure) {
		impl.setContractClauses(contractId, employeeContractInfo.getContractClauses(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
		});
		
	}
	
	public void getContractClauses(Consumer<List<ContractClause>> success, Consumer<Throwable> failure) {
		impl.getContractClauses(contractId, new AsyncCallback<List<ContractClause>>() {
			
			@Override
			public void onSuccess(List<ContractClause> contractClauses) {
				employeeContractInfo.setContractClauses(contractClauses);
				success.accept(contractClauses);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
		});
	}
	
	public void deleteContractClause(Integer clauseId, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.deleteContractClause(clauseId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
		});
	}
	
	public void importContractClauses(List<Integer> clausesIds, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.importContractClauses(clausesIds, this.contractId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
		});
	}

}
