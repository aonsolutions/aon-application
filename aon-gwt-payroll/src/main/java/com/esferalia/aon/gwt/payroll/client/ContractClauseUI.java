package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.client.ContractClausePanel.AonContractClausePanelCallback;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class ContractClauseUI extends ScrollPanel {
	
	private SimplePanel container;
	private AonCustomTable tab;
	
	private static enum COLUMN {
		  LIN("Linea"								,"5rem"				,"")
		, NAM("Nombre"								,"10rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DES("Descripci\u00f3n"					,"-moz-available"  	,"min-width: 10rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					,"3rem" 			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLUMN(String headerLabel,String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private COLUMN(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}
	
	// ------------------------------------------------------ Constructor
	
	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private EmployeeContractInfo employeeContractInfo;
	private Integer contractId;
	
	protected ContractClauseUI() {
		this.setHeight("100%");
		
		container = new SimplePanel();
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		setWidget(container);
	}
	
	private void onSearch() {
		search();
	}
	
	private void search() {
		container.clear();
		tab = new AonCustomTable();
		
		paintHeader();
		container.setWidget(tab);
		searchData();
	}
	
	private void paintHeader() {
		HTMLPanel header = tab.createHeader();
		header.getElement().getStyle().setProperty("top", "0px");
		
		for ( COLUMN col : COLUMN.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());		
	}
	
	private void searchData() {
		showLoadingMessage("Cargando clausulas ...");
		getList(clauses -> {
			onHideMessage();
			
			boolean something = false;
			
			for(ContractClause clause : clauses) {
				something = true;
				paintRow(clause);
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
			}
		}, f -> showErrorMessage("Obtenci\u00f3n Clausulas", f.getMessage()));
	}

	private void paintRow(ContractClause clause) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		if(null != clause.getContract()) {
			AonTableButton deleteButton = new AonTableButton("Borrar Clausula", AON.CSS.aonIconDelete());
			deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
			deleteButton.addClickHandler(e -> {
				e.stopPropagation();
				deleteButton.setEnabled(false);
				
				AonDialog deleteDialog = new AonDialog("Eliminaci\u00f3n Documentos",
						new HTML("Se va a proceder a eliminar la clausula <b>" + clause.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
				
				deleteDialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						deleteButton.setEnabled(true);
					}
					
					@Override
					public void onAccept() {
						deleteContractClause(
								clause.getId(), 
				    			s -> {
				    				showSuccessMessage("Borrado Clausula", "La clausula ha sido eliminada correctamente");
				    				onSearch();
				    			}, 
				    			f -> showErrorMessage("Borrado Clausula", f.getMessage()));
					}
				});
				
			});
			buttonContainer.add(deleteButton);
		}
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> editClause(clause), ClickEvent.getType());
		
		tab.addRow(row, new Label(null == clause.getLineNumber() ? "" : clause.getLineNumber().toString()), COLUMN.LIN.getColWidth());
		
		Label name = new Label(clause.getName());
		name.setTitle(clause.getName());
		tab.addInlineStyle(name, COLUMN.NAM.getCellStyleClass());
		tab.addRow(row, name, COLUMN.NAM.getColWidth());
		
		Label description = new Label(clause.getDescription());
		description.setTitle(clause.getDescription());
		tab.addInlineStyle(description, COLUMN.DES.getCellStyleClass());
		tab.addRow(row, description, COLUMN.DES.getColWidth());
		
		tab.addRow(row, buttonContainer, COLUMN.BUT.getColWidth());
	}
	
	// ------------------------------------------------------ setEmployeeContractInfo
	
	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		this.contractId = this.employeeContractInfo.getContractInfo().getContractId();
		onSearch();
	}
	
	// ------------------------------------------------------ Toolbar methods
	
	public void newClause() {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "CLAUSULA" );
		ContractClausePanel contractClausePanel = new ContractClausePanel( employeeContractInfo.getEmployeeInfo().getDomain(), employeeContractInfo.getContractInfo().getContractId(), employeeContractInfo.getContractClauses().size(), new AonContractClausePanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept() {
				dialog.hide();
				onSearch();
			}
		});
		
		dialog.add( contractClausePanel );
		dialog.showLoaded();
	}
	
	private void editClause(ContractClause contractClause) {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "CLAUSULA" );
		ContractClausePanel contractClausePanel = new ContractClausePanel( contractClause, new AonContractClausePanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept() {
				dialog.hide();
				onSearch();
			}
		});
		
		dialog.add( contractClausePanel );
		dialog.showLoaded();
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
							onSearch();
						}, f -> showErrorMessage("Importaci\u00f3n Clausulas", f.getMessage()));
			}
		};
	}
	
	public void saveClauses() {
		saveContractClause(s -> {
			showSuccessMessage("Clausulas", "Clausulas guardadas correctamente");
			onSearch();
		}, f -> {});
	}
	
	// ------------------------------------------------------ Abstract Methods

	protected abstract void showErrorMessage(String title, String message);

	protected abstract void showSuccessMessage(String title, String message);
	
	protected abstract void showLoadingMessage(String message);
	
	protected abstract void onHideMessage();
	
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
	
	public void getList(Consumer<List<ContractClause>> success, Consumer<Throwable> failure) {
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
