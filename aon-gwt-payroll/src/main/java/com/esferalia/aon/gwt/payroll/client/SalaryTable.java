package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class SalaryTable extends ScrollPanel {
	
	// ------------------------------------------ Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private SalaryParams params;
	
	private DomainEnterprisesServiceAsync service = DomainEnterprisesServiceAsync.newInstance();

	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private SimplePanel container;
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private Map<Integer, SalaryInfo> rowSalaries = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();

	private Integer deleteIterator = 0;
	
	private static enum ENT_WORK_COL {
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"")
		, ENT("Empresa"								,"15rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, WOR("C. Trabajo"							,"10rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, EMP("Trabajador"							,"-moz-available"  	,"min-width: 10rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TYP("Tipo"								,"7rem" 			,"")
		, STD("F. Inicio"							,"5rem" 			,"")
		, END("F. Fin"								,"5rem" 			,"")
		, BRU("Bruto"								,"5rem" 			,"text-align: right;")
		, NET("Neto"								,"5rem" 			,"text-align: right;")
		, LIQ("Liquido"								,"5rem" 			,"text-align: right;")
		, BUT(AonStringUtils.EMPTY					,"5rem" 			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private ENT_WORK_COL(String headerLabel,String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private ENT_WORK_COL(String headerLabel,String colWidth,String cellStyleClass) {
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
	
	private static enum EMPLOYEE_COL {
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"")
		, TYP("Tipo"								,"-moz-available" 	,"min-width: 10rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STD("F. Inicio"							,"5rem" 			,"")
		, END("F. Fin"								,"5rem" 			,"")
		, BRU("Bruto"								,"5rem" 			,"text-align: right;")
		, NET("Neto"								,"5rem" 			,"text-align: right;")
		, LIQ("Liquido"								,"5rem" 			,"text-align: right;")
		, BUT(AonStringUtils.EMPTY					,"5rem" 			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private EMPLOYEE_COL(String headerLabel,String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private EMPLOYEE_COL(String headerLabel,String colWidth,String cellStyleClass) {
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
	
	// ------------------------------------------ Constructor

	protected SalaryTable(SalaryParams params) {
		this.params = params;
		
		container = new SimplePanel();
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		setWidget(container);
		
		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						searchData();
					}
				}
			}
		});
		
		onSearch();
	}
	
	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	private void onSearch() {
		enableMoreData();
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
		
		if(isEmployee()) {
			for ( EMPLOYEE_COL col : EMPLOYEE_COL.values()) 
				if(col == EMPLOYEE_COL.CHK) {
					AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
					checkAllButton.addClickHandler(e -> {
						List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
						if (selectedItemList.size() == rowSalaries.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
							checkAllButton.addStyleName(AON.CSS.aonIconCheck());
							checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
							selectedItems.values().forEach(check ->{
								check.addStyleName(AON.CSS.aonIconCheck());
								check.removeStyleName(AON.CSS.aonIconChecked());
							});
							onSelectionSalary(false);
						} else {
							checkAllButton.addStyleName(AON.CSS.aonIconChecked());
							checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
							selectedItems.values().forEach(check ->{
								check.addStyleName(AON.CSS.aonIconChecked());
								check.removeStyleName(AON.CSS.aonIconCheck());
							});
							onSelectionSalary(true);
						}
					});
					
					tab.addHeader(checkAllButton, col.getColWidth());
				} else 
					tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
				
		} else {
			for ( ENT_WORK_COL col : ENT_WORK_COL.values()) 
				if(col == ENT_WORK_COL.CHK) {
					AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
					checkAllButton.addClickHandler(e -> {
						List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
						if (selectedItemList.size() == rowSalaries.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
							checkAllButton.addStyleName(AON.CSS.aonIconCheck());
							checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
							selectedItems.values().forEach(check ->{
								check.addStyleName(AON.CSS.aonIconCheck());
								check.removeStyleName(AON.CSS.aonIconChecked());
							});
							onSelectionSalary(false);
						} else {
							checkAllButton.addStyleName(AON.CSS.aonIconChecked());
							checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
							selectedItems.values().forEach(check ->{
								check.addStyleName(AON.CSS.aonIconChecked());
								check.removeStyleName(AON.CSS.aonIconCheck());
							});
							onSelectionSalary(true);
						}
					});
					
					tab.addHeader(checkAllButton, col.getColWidth());
				} else {
					if(isWorkplace() && (col == ENT_WORK_COL.ENT || col == ENT_WORK_COL.WOR)) continue;
					else if(isEnterprise() && col == ENT_WORK_COL.ENT) continue;
					else tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
				}
		}
		
		
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(salaries -> {
			onHideMessage();
			
			boolean something = false;
			
			for(SalaryInfo salary : salaries) {
				something = true;
				paintRow(salary);
			}
			
			if (salaries.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + salaries.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				container.clear();
				container.add(line);
				disableMoreData();
			}
			enableSearch();
			
		});
	}
	
	private void paintRow(SalaryInfo salary) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton deleteButton = new AonTableButton("Borrar N\u00f3mina", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteButton.setEnabled(false);
			
			AonDialog deleteDialog = new AonDialog("Eliminaci\u00f3n N\u00f3minas",
					new HTML("Se va a proceder a eliminar la n\u00f3mina <b>" + salary.getEmployeeName() + " (" + formatDate.format(salary.getStartDate()) + " - " + formatDate.format(salary.getEndDate())  + ")" + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			deleteDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}
				
				@Override
				public void onAccept() {
					if(salary.isAlcatraz()) {
						List<SalaryInfo> alcatrazSalaries = new ArrayList<>();
						alcatrazSalaries.add(salary);
						createAlcatrazWarning(alcatrazSalaries);
					}else if(salary.isFinance()) {
						List<SalaryInfo> financeSalaries = new ArrayList<>();
						financeSalaries.add(salary);
						createFinanceWarning(financeSalaries);
					} else {
						onShowLoadingMessage("Eliminando n\u00f3mina seleccionada...");
						delete(salary);
					}
					
					
				}
			});
			
		});
		buttonContainer.add(deleteButton);
		
		if(salary.getType() == Type.SETTLE) {
			AonTableButton settlerButton = new AonTableButton("Carta Finiquito", AON.CSS.aonIconPdf());
			settlerButton.addStyleName(AON.CSS.aonCustomRowButtom());
			settlerButton.addClickHandler(e -> {
				e.stopPropagation();
				onShowSettlePDF(salary);
			});
			buttonContainer.add(settlerButton);
		}
		
		if(isEmployeeTree() && !salary.getType().equals(Type.SETTLE)) {
			AonTableButton draftButton = new AonTableButton("Ir a Borrador", AON.CSS.aonIconEdit());
			draftButton.addStyleName(AON.CSS.aonCustomRowButtom());
			draftButton.addClickHandler(e -> {
				e.stopPropagation();
		    	EmployeeTree.showSalaryDraft(
		    			salary.getContract(),
		    			salary.getWorkplaceId(), 
		    			salary.getStartDate(),
		    			salary.getEndDate());
			});
			buttonContainer.add(draftButton);
		}
		
		if(salary.isAlcatraz()) {
			AonTableButton aeatButton = new AonTableButton(
					"Mod111 (" + salary.getAlcatrazYear() + ", " + salary.getAlcatrazPeriod().getDescription() + ")", 
					getAeatButton(salary)
			);
			aeatButton.addStyleName(AON.CSS.aonCustomRowButtom());
			buttonContainer.add(aeatButton);
		}
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onShowPDF(salary), ClickEvent.getType());
		
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
			onSelectionSalary(!selectedItemList.isEmpty());
		});
		tab.addRow(row, checkButton, ENT_WORK_COL.CHK.getColWidth());
		
		if(!isEnterprise() && !isWorkplace() && !isEmployee()) {
			Label enterprise = new Label(salary.getEnterpriseName());
			enterprise.setTitle(salary.getEnterpriseName());
			tab.addInlineStyle(enterprise, ENT_WORK_COL.ENT.getCellStyleClass());
			tab.addRow(row, enterprise, ENT_WORK_COL.ENT.getColWidth());
		}
		
		if((!isEnterprise() && !isWorkplace() && !isEmployee()) || isEnterprise()) {
			Label workplace = new Label(salary.getWorkplaceName());
			workplace.setTitle(salary.getWorkplaceName());
			tab.addInlineStyle(workplace, ENT_WORK_COL.WOR.getCellStyleClass());
			tab.addRow(row, workplace, ENT_WORK_COL.WOR.getColWidth());
		}
		
		if((!isEnterprise() && !isWorkplace() && !isEmployee()) || isEnterprise() || isWorkplace()) {
			Label employee = new Label(salary.getEmployeeName());
			employee.setTitle(salary.getEmployeeName());
			tab.addInlineStyle(employee, ENT_WORK_COL.EMP.getCellStyleClass());
			tab.addRow(row, employee, ENT_WORK_COL.EMP.getColWidth());
		}
		
		if(isEmployee()) {
			Label type = new Label(salary.getType().getDescription());
			type.setTitle(salary.getType().getDescription());
			tab.addInlineStyle(type, EMPLOYEE_COL.TYP.getCellStyleClass());
			tab.addRow(row, type, EMPLOYEE_COL.TYP.getColWidth());
		} else
			tab.addRow(row, new Label(salary.getType().getDescription()), ENT_WORK_COL.TYP.getColWidth());
		
		tab.addRow(row, new Label(salary.getStartDate() == null ? "" : formatDate.format(salary.getStartDate())), ENT_WORK_COL.STD.getColWidth());
		tab.addRow(row, new Label(salary.getEndDate() == null ? "" : formatDate.format(salary.getEndDate())), ENT_WORK_COL.END.getColWidth());
		
		
		Label totalPayment = new Label(salary.getTotalPayment() + " \u20ac");
		totalPayment.setTitle(salary.getTotalPayment() + " \u20ac");
		tab.addInlineStyle(totalPayment, ENT_WORK_COL.BRU.getCellStyleClass());
		tab.addRow(row, totalPayment, ENT_WORK_COL.BRU.getColWidth());
		
		Label totalDecuction = new Label(salary.getTotalDecuction() + " \u20ac");
		totalDecuction.setTitle(salary.getTotalDecuction() + " \u20ac");
		tab.addInlineStyle(totalDecuction, ENT_WORK_COL.NET.getCellStyleClass());
		tab.addRow(row, totalDecuction, ENT_WORK_COL.NET.getColWidth());
		
		Label totalLiquid = new Label(salary.getTotalLiquid() + " \u20ac");
		totalLiquid.setTitle(salary.getTotalLiquid() + " \u20ac");
		tab.addInlineStyle(totalLiquid, ENT_WORK_COL.LIQ.getCellStyleClass());
		tab.addRow(row, totalLiquid, ENT_WORK_COL.LIQ.getColWidth());
		
		tab.addRow(row, buttonContainer, ENT_WORK_COL.BUT.getColWidth());
		
		rowSalaries.put(salary.getId(), salary);
		selectedItems.put(salary.getId(), checkButton);
	}
	
	private String getAeatButton(SalaryInfo salary) {
		switch (salary.getAlcatrazTerritory()) {
			case ARABA:
				return "aon-icon-araba";
			case BIZKAIA:
				return "aon-icon-bizkaia";
			case GIPUZKOA:
				return "aon-icon-gipuzkoa";
			case NAVARRA:
				return "aon-icon-navarra";
			default:
				return "aon-icon-aeat";
		}
	}

	public void resetSearchOffset() {
		offset.setValue(0);
	}
	
	// ------------------------------------------ Abstract Methods
	
	private void onSelectionSalary(boolean isSomethingSelected) {
		List<Integer> selectedSalaryIds = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
		List<SalaryInfo> selectedSalaries = rowSalaries.entrySet().stream().filter(entry -> selectedSalaryIds.contains(entry.getKey())).map(entry -> entry.getValue()).collect(Collectors.toList());
		
		Optional<SalaryInfo> settle = selectedSalaries.stream().filter(salary -> salary.getType().equals(Type.SETTLE)).findAny();
		onSelectionSalaryChange(isSomethingSelected, settle.isPresent());
	}
	
	public Integer getEnterpriseId() {
		List<Integer> selectedSalaryIds = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
		List<SalaryInfo> selectedSalaries = rowSalaries.entrySet().stream().filter(entry -> selectedSalaryIds.contains(entry.getKey())).map(entry -> entry.getValue()).collect(Collectors.toList());
		
		return selectedSalaries.isEmpty() ? null : selectedSalaries.get(0).getEnterpriseId();
	}
	
	public List<SalaryInfo> getSelectedSalaries() {
		List<Integer> selectedSalaryIds = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
		List<SalaryInfo> selectedSalaries = rowSalaries.entrySet().stream().filter(entry -> selectedSalaryIds.contains(entry.getKey())).map(entry -> entry.getValue()).collect(Collectors.toList());
		
		return selectedSalaries;
	}

	// ------------------------------------------ Abstract Methods
	
	private void createAlcatrazWarning(List<SalaryInfo> alcatrazSalaries) {
		DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
		
		String message = "No se pueden eliminar la n&oacute;minas que est&aacute;n presentadas en el <b>Modelo 111</b>. Estas n&oacute;minas son:<br><br>";
		for(SalaryInfo salary : alcatrazSalaries) {
			message += "&emsp;" + salary.getAlcatrazTerritory().getDescription() + ", " + salary.getAlcatrazYear() + " " + salary.getAlcatrazPeriod().getDescription()  + ".  " + salary.getEmployeeName() + " (" + formatDate.format(salary.getStartDate()) + " - " + formatDate.format(salary.getEndDate()) + ")<br>";
		}
		
		message += "<br>Para poder eliminar dichas n&oacute;minas, deber&aacute; eliminar primero el <b>Modelo 111</b> asociado.";
		
		AonDialog dialog = new AonDialog("Borraro", new HTML(message));
		dialog.info();
	}
	
	private void createFinanceWarning(List<SalaryInfo> financeSalaries) {
		DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
		
		String message = "No se pueden eliminar la n&oacute;minas que ya tienen <b>vencimientos remesados/saldados</b>. Estas n&oacute;minas son:<br><br>";
		for(SalaryInfo salary : financeSalaries) {
			message += "&emsp;" + salary.getEmployeeName() + " (" + formatDate.format(salary.getStartDate()) + " - " + formatDate.format(salary.getEndDate()) + ")<br>";
		}
		
		AonDialog dialog = new AonDialog("Borraro", new HTML(message));
		dialog.info();
	}
	
	public void deleteSalaries() {
		List<Integer> selectedSalaryIds = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).map(entry -> entry.getKey()).collect(Collectors.toList());
		
		AonDialog deleteDialog = new AonDialog("Eliminaci\u00f3n N\u00f3minas",
				new HTML("Se va a proceder a eliminar <b>" + selectedSalaryIds.size() + " n\u00f3minas</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
		
		deleteDialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				// Nothing to do here
			}
			
			@Override
			public void onAccept() {
				List<SalaryInfo> selectedSalaries = rowSalaries.entrySet().stream().filter(entry -> selectedSalaryIds.contains(entry.getKey())).map(entry -> entry.getValue()).collect(Collectors.toList());
				
				List<SalaryInfo> alcatrazSalaries = selectedSalaries.stream().filter(salary -> salary.isAlcatraz()).collect(Collectors.toList());
				List<SalaryInfo> financeSalaries = selectedSalaries.stream().filter(salary -> salary.isFinance()).collect(Collectors.toList());
				
				if(!alcatrazSalaries.isEmpty()) 
					createAlcatrazWarning(alcatrazSalaries);
				else if(!financeSalaries.isEmpty()) {
					createFinanceWarning(financeSalaries);
				} else {
					onShowLoadingMessage("Eliminando n\u00f3minas seleccionadas...");
					deleteIterator = 0;
					delete(selectedSalaryIds);
				}
			}
		});
	}
	
	private void delete(List<Integer> selectedSalaryList) {
		if(deleteIterator == selectedSalaryList.size()) {
			resetSearchOffset();
			onSearch();
			onShowSuccessMessage("N\u00f3minas eliminadas correctamente");
			onSelectionSalaryChange(false, false);
		} else {
			service.deleteSalary(selectedSalaryList.get(deleteIterator), new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					deleteIterator++;
					delete(selectedSalaryList);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error borrado: " + caught.getMessage());
				}
			});
		}
	}
	
	private void getList(Consumer<List<SalaryInfo>> success) {
		onShowLoadingMessage("Obteniendo n\u00f3minas de los trabajadores...");
		
		service.getSalaries(params, new AsyncCallback<List<SalaryInfo>>() {
			
			@Override
			public void onSuccess(List<SalaryInfo> salaries) {
				success.accept(salaries);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
	private void delete(SalaryInfo salary) {
		service.deleteSalary(salary.getId(), new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
			}
		});
	}
	
	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	protected abstract void onShowSuccessMessage(String successMessage);
	protected abstract void onHideMessage();
	
	protected abstract boolean isEnterprise();
	protected abstract boolean isWorkplace();
	protected abstract boolean isEmployee();
	protected abstract boolean isEmployeeTree();
	
	protected abstract void onSelectionSalaryChange(boolean isSomethingSelected, boolean hasSettleSelected);
	
	protected abstract void onShowPDF(SalaryInfo salary);
	protected abstract void onShowSettlePDF(SalaryInfo salary);
	
	
}
