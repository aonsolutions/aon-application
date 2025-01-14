package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class ImportClauseDialog extends AonCustomDialog {
	
	private static enum COLUMN {
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"")
		, LIN("Linea"								,"5rem"				,"")
		, NAM("Nombre"								,"10rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DES("Descripci\u00f3n"					,"-moz-available"  	,"min-width: 10rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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
	
	private FlowPanel clausesContainer;
	private HTMLPanel messagePanel;
	
	private ScrollPanel scrollPanel;
	private SimplePanel container;
	private AonCustomTable tab;
	
	private Map<Integer, ContractClause> rowClauses = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();
	
	private HTMLPanel buttonsPanel;
	private Button importBtnDialog;
	
	// ------------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private List<ContractClause> existingClauses = new ArrayList<>();
	
	// ------------------------------------------------- Constructor
	
	protected ImportClauseDialog(List<ContractClause> existingClauses) {
		setCaption("Clausulas");
		
		this.existingClauses = existingClauses;
		
		clausesContainer = new FlowPanel();
		clausesContainer.addStyleName(AON.CSS.aonFlexColumn());
		
		messagePanel = new HTMLPanel("");
		clausesContainer.add(messagePanel);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight("300px");
		scrollPanel.setWidth("700px");
		
		container = new SimplePanel();
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		scrollPanel.setWidget(container);
		
		clausesContainer.add(scrollPanel);
		
		getButtonsPanel();
		
		setWidget(clausesContainer);
		
		onSearch();
		
		showDialog();
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
			if(col == COLUMN.CHK) {
				AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
				checkAllButton.addClickHandler(e -> {
					List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
					if (selectedItemList.size() == rowClauses.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
						checkAllButton.addStyleName(AON.CSS.aonIconCheck());
						checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconCheck());
							check.removeStyleName(AON.CSS.aonIconChecked());
						});
						importBtnDialog.setEnabled(false);
					} else {
						checkAllButton.addStyleName(AON.CSS.aonIconChecked());
						checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
						selectedItems.values().forEach(check ->{
							check.addStyleName(AON.CSS.aonIconChecked());
							check.removeStyleName(AON.CSS.aonIconCheck());
						});
						importBtnDialog.setEnabled(true);
					}
				});
				
				tab.addHeader(checkAllButton, col.getColWidth());
			} else 
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
				
	}
	
	private void searchData() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo clausulas ...");
		getList(clauses -> {
			AonMessagePanel.hideMessage(messagePanel);
			
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
			
		}, f -> AonMessagePanel.showError(messagePanel, "Obtenci\u00f3n Clausulas: " + f.getMessage()));
	}
	
	private void paintRow(ContractClause clause) {
		HTMLPanel row = tab.createRow();
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
		checkButton.addClickHandler(e -> {
			e.stopPropagation();
			if (AonStringUtils.containsIgnoreCase(checkButton.getStyleName(), AON.CSS.aonIconChecked())) {
				checkButton.addStyleName(AON.CSS.aonIconCheck());
				checkButton.removeStyleName(AON.CSS.aonIconChecked());
			} else {
				checkButton.addStyleName(AON.CSS.aonIconChecked());
				checkButton.removeStyleName(AON.CSS.aonIconCheck());
			}
			List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
			importBtnDialog.setEnabled(!selectedItemList.isEmpty());
		});
		tab.addRow(row, checkButton, COLUMN.CHK.getColWidth());
		
		tab.addRow(row, new Label(null == clause.getLineNumber() ? "" : clause.getLineNumber().toString()), COLUMN.LIN.getColWidth());
		
		Label name = new Label(clause.getName());
		name.setTitle(clause.getName());
		tab.addInlineStyle(name, COLUMN.NAM.getCellStyleClass());
		tab.addRow(row, name, COLUMN.NAM.getColWidth());
		
		Label description = new Label(clause.getDescription());
		description.setTitle(clause.getDescription());
		tab.addInlineStyle(description, COLUMN.DES.getCellStyleClass());
		tab.addRow(row, description, COLUMN.DES.getColWidth());
		
		rowClauses.put(clause.getId(), clause);
		selectedItems.put(clause.getId(), checkButton);
	}

	// ------------------------------------------------- ShowDialog
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		buttonsPanel.getElement().getStyle().setProperty("padding", "1rem");
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText("Cerrar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
		
		importBtnDialog = new Button();
		importBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		importBtnDialog.setText("Importar Clausulas");
		importBtnDialog.setEnabled(false);
		importBtnDialog.addClickHandler(e -> onClausesImport(getClausesIds()));
		
		buttonsPanel.add(importBtnDialog);
		
		clausesContainer.add(buttonsPanel);
	}
	
	private List<Integer> getClausesIds() {
		List<Integer> selectedClauseIds = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
		return selectedClauseIds;
	}
	
	// ------------------------------------------------- AbstractMethods
	
	protected abstract void onClausesImport(List<Integer> clausesIds);

	// ------------------------------------------------- MessagePanel
	
	private void getList(Consumer<List<ContractClause>> success, Consumer<Throwable> error) {
		impl.getDomainClauses(new AsyncCallback<List<ContractClause>>() {

			@Override
			public void onFailure(Throwable caught) {
				error.accept(caught);
			}

			@Override
			public void onSuccess(List<ContractClause> clausesListDB) {
				List<ContractClause> clausesList = clausesListDB;
				
				if(null != existingClauses && !existingClauses.isEmpty())
					clausesList = filterClauses(clausesListDB, existingClauses);
				
				success.accept(clausesList);
			}

			private List<ContractClause> filterClauses(List<ContractClause> clausesListDB, List<ContractClause> existingClauses) {
				List<ContractClause> clausesList = new ArrayList<>();
				for(ContractClause contractClause : clausesListDB)
					if(!existClause(contractClause, existingClauses))
						clausesList.add(contractClause);
				
						return clausesList;
			}

			private boolean existClause(ContractClause contractClause, List<ContractClause> existingClauses) {
				for(ContractClause existingClause : existingClauses)
					if(AonStringUtils.equalsIgnoreCase(contractClause.getName(), existingClause.getName()))
						return true;
				return false;
			}
		});
	}
	
}
