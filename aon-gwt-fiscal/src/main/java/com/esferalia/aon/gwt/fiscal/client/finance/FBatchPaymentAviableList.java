package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.FBatchDetail;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.FBatchType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class FBatchPaymentAviableList extends AonCustomDockLayout {
	
	// ------- FBatchRow
	
	private static class FinanceRow {

		private int row;
		private Finance finance;

		private FinanceRow(int row, Finance finance) {
			this.row = row;
			this.finance = finance;
		}

		private int getRow() {
			return row;
		}

		private Finance getFinance() {
			return finance;
		}

	}
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	
	// ProductList UI

	private HTMLPanel container;
	
	private AonTableButton resetSearchButton;
	private AonTableButton checkAll; 
	private AonTableButton uncheckAll;
	private AonTableButton addAviableButton;
	private AonTableButton showHideFilter;
	
	private FBatchPaymentAviableModuleSearchPanel searchPanel;
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private FinanceModuleOptions options;
	
	// Table UI
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private int lastScrollPos = 0;

	private InlineLabel aviableCount = new InlineLabel("");;
	private LinkedHashMap<Integer, FinanceRow> aviableFinances;
	private LinkedHashSet<Integer> selectedFinances;
	
	private boolean fetchingData = false;
	
	private FBatchType fbatchType;
	private FBatch fbatch;
	
	private static enum COLS {
		  CHK(AonStringUtils.EMPTY		,"2rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, FEC("F. Venc."				,"5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, FFT("F. Factura"				,"5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, FAC("N. Factura"				,"8rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TIT("Titular"					,"5rem"  			,"flex: 1 1 5rem; min-width: 5rem; width: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, AMO("Importe"					,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;") // Pack o servicio
		, ACT(AonStringUtils.EMPTY		,"2rem" 			,"")
		;
		
		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
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
	
	private static enum COLS_PAYROLL {
		  CHK(AonStringUtils.EMPTY		,"2rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, FEC("F. Venc."				,"5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, FAC("Concepto"				,"9rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TIT("Titular"					,"5rem"  			,"flex: 1 1 5rem; min-width: 5rem; width: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, AMO("Importe"					,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;") // Pack o servicio
		, ACT(AonStringUtils.EMPTY		,"2rem" 			,"")
		;
		
		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS_PAYROLL(String headerLabel,String colWidth,String cellStyleClass) {
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
	
	// Constructor
	public FBatchPaymentAviableList(FinanceModuleOptions options, FBatchType fbatchType, FBatch fbatch) {
		super("Vencimientos Disponibles");
		
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);
		
		this.options = options;
		this.fbatchType = fbatchType;
		this.fbatch = fbatch;
		
		aviableFinances = new LinkedHashMap<Integer, FinanceRow>();
		selectedFinances = new LinkedHashSet<Integer>();
		
		addButtonsToolbar();
		
		searchPanel = new FBatchPaymentAviableModuleSearchPanel(options, fbatchType, fbatch);
		searchPanel.addValueChangeHandler( new ValueChangeHandler<FinanceParams>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<FinanceParams> event) {
				hideMessage();
				resetInfo();
				onSearch();
			}
		});

		hideToolbarFilterMessages();
		hideFilterButton();
		setSearchPlaceholder("Busque por descripci\u00f3n ...");
		getSearchTextBox().setTitle("Busque por titular, concepto, numero factura");
		getSearchTextBox().getElement().getStyle().setProperty("min-width", "auto");
		addOnSearchHandler(e -> { if(!fetchingData) onSearch(); });
		
		showHideFilter = new AonTableButton("", AON.CSS.aonIconFilterList());
		showHideFilter.addClickHandler(e -> searchPanel.setVisible(!searchPanel.isVisible()));
		insertWidgetAfterSearchButton(showHideFilter, true);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(searchPanel);
	
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		
		container.add(tableContainer);
		
		add(container);
		
		if (!fbatch.isRecorded())
			onSearch();
		else {
			Label label = new Label("No se pueden a\u00f1adir vencimientos a una remesa contabilizada");
			label.setStyleName(AON.CSS.aonBlockMessage());
			label.addStyleName(AON.CSS.aonBlockInfoMessage());
			label.addStyleName(AON.CSS.aonMarginTop());
			container.add(label);
		}
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	getSearchTextBox().setFocus(true);
	        	getSearchInfo();
	        }
	    });		
	}

	@Override
	protected void onClearFilter() {}
	
	private void addButtonsToolbar() {
		resetSearchButton = new AonTableButton(AON.MSG.clean() + " filtros", AON.CSS.aonIconClear());
		resetSearchButton.addClickHandler(e -> {
			hideMessage();
			getSearchTextBox().setValue(null, false);
			enableMoreData();
			resetSearchOffset();
			searchPanel.initialize();
			resetInfo();
			onSearch();
		});
		addToolbarButton(resetSearchButton);
		
		checkAll = new AonTableButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll.setEnabled(!fbatch.isGenerated() && !fbatch.isRecorded());
		checkAll.addClickHandler(e -> checkAllAviable( true ));
		addToolbarButton(checkAll);
		
		uncheckAll = new AonTableButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.setEnabled(!fbatch.isGenerated() && !fbatch.isRecorded());
		uncheckAll.addClickHandler(e -> checkAllAviable( false ));
		addToolbarButton(uncheckAll);
		
		HTMLPanel checksPanel = new HTMLPanel(AonStringUtils.EMPTY);
		checksPanel.addStyleName(AON.CSS.aonDisplayFlex());
		checksPanel.add(checkAll);
		checksPanel.add(uncheckAll);
		addToolbarButton(checksPanel);
		
		addAviableButton = new AonTableButton("A\u00f1adir a la remesa", AON.CSS.aonIconKeyboardDoubleArrowRight());
		addAviableButton.getElement().getStyle().setProperty("background-repeat", "no-repeat");
		addAviableButton.setEnabled(false);
		addAviableButton.addClickHandler(e -> {
			addAviableButton.setEnabled(false);
			
			for (Integer financeId : selectedFinances) {
				FinanceRow financeRow = aviableFinances.get(financeId);
				if (null != financeRow) {
					fbatch.addBatchDetail(
							new FBatchDetail()
								.setDomain(fbatch.getDomain())
								.setFbatch(fbatch.getId())
								.setFinance(financeRow.getFinance())
								.setAmount(financeRow.getFinance().getAmount())
								.setStatus((byte) 1)
								.setRemoved(false));

					
				}
			}

			saveFBatch(fbatch);
		});
		addToolbarButton(addAviableButton);
	}
	
	private void checkAllAviable(boolean check) {
		for (FinanceRow financeRow : aviableFinances.values()) {
			if (!isCheckable(financeRow.getFinance()))
				continue;
			
			financeRow.getFinance().setSelected(check);
			
			if (check)
				selectedFinances.add(financeRow.getFinance().getId());
			else
				selectedFinances.remove(financeRow.getFinance().getId());
			
			Widget w = tab.getWidget(financeRow.getRow(), 0);
			
			if (null != w && w instanceof AonTableButton) {
				if (check) {
					w.addStyleName(AON.CSS.aonIconChecked());
					w.removeStyleName(AON.CSS.aonIconCheck());
				} else {
					w.addStyleName(AON.CSS.aonIconCheck());
					w.removeStyleName(AON.CSS.aonIconChecked());
				}
			}
		}

		aviableCount.setText((selectedFinances.size() > 0) ? AonNumberUtils.toString(selectedFinances.size()) : "");
		addAviableButton.setEnabled(selectedFinances.size() > 0 && !fbatch.isGenerated() && !fbatch.isRecorded());
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
	
	private void resetSearchOffset() {
		offset.setValue(0);
	}
	
	public void setFBatch(FBatch fBatch) {
		this.fbatch = fBatch;
		searchPanel.setFBatch(fBatch);
		
		resetInfo();
		
		onSearch();
	}
	
	private void resetInfo() {
		checkAll.setEnabled(!fbatch.isGenerated() && !fbatch.isRecorded());
		uncheckAll.setEnabled(!fbatch.isGenerated() && !fbatch.isRecorded());
		
		aviableFinances.clear();
		selectedFinances.clear();
	}
	
	public void onSearch() {
		resetSearchOffset();
		onSearchData();
	}
	
	private void onSearchData() {
		fetchingData = true;
		enableMoreData();
		searchData();
	}
	
	private void searchData() {
		tableContainer.clear();
		tab = new AonCustomTable();
		tab.getElement().getStyle().setProperty("min-width", "0");
		tableScrollPanel = new ScrollPanel(tab);
		tableScrollPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		tableScrollPanel.addScrollHandler(e -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = tableScrollPanel.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = tableScrollPanel.getWidget().getOffsetHeight() - tableScrollPanel.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					searchDataList();
				}
			}
		});
		
		paintHeader();
		tableContainer.add(tableScrollPanel);
		searchDataList();
	}
	
	private void paintHeader() {
		aviableCount.addStyleName(AON.CSS.aonTextCenter());
		
		tab.createHeader();
		
		if(FBatchType.PAYROLL_PAYMENT.equals(this.fbatchType))
			for ( COLS_PAYROLL col : COLS_PAYROLL.values()) 
				tab.addHeader(col.equals(COLS_PAYROLL.CHK) ? aviableCount : new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
		else
			for ( COLS col : COLS.values()) 
				tab.addHeader(col.equals(COLS.CHK) ? aviableCount : new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
		
	}
	
	private void searchDataList() {
		if (!isMoreData()) return;
		
		getList(finances -> {
			boolean something = false;
			
			for(Finance finance : finances) {
				something = true;
				if (!isBatchedFinance(finance))
					paintRow(finance);
			}
			
			if (finances.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + finances.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				HTMLPanel row = tab.createRow();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				row.add(label);
				disableMoreData();
			}
			enableSearch();
			fetchingData = false;
		});
	}

	private boolean isBatchedFinance(Finance finance) {
		Optional<FBatchDetail> findFinance = fbatch.getBatchDetails().stream().filter(
				fbatchDetail -> fbatchDetail.getFinance().getId().equals(finance.getId()) && !fbatchDetail.isRemoved())
				.findAny();
		return findFinance.isPresent();
	}
	
	private void paintRow(Finance finance) {
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> {}, ClickEvent.getType());
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), selectedFinances.contains(finance.getId()) ? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
		checkButton.setEnabled(isCheckable(finance));
		checkButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				
				if (selectedFinances.contains(finance.getId())) {
					selectedFinances.remove(finance.getId());
					checkButton.addStyleName(AON.CSS.aonIconCheck());
					checkButton.removeStyleName(AON.CSS.aonIconChecked());
				} else {
					selectedFinances.add(finance.getId());
					checkButton.addStyleName(AON.CSS.aonIconChecked());
					checkButton.removeStyleName(AON.CSS.aonIconCheck());
				}

				aviableCount.setText((selectedFinances.size() > 0) ? AonNumberUtils.toString(selectedFinances.size()) : "");
				addAviableButton.setEnabled(selectedFinances.size() > 0 && !fbatch.isRecorded() && !fbatch.isGenerated());
			}
		});
		tab.addRow(row, checkButton, COLS.CHK.getColWidth());
		
		Label issueDate = new Label(AON.DATE_FORMAT.format(finance.getDueDate()));
		tab.addRow(row, issueDate, COLS.FEC.getColWidth());
		
		if(!FBatchType.PAYROLL_PAYMENT.equals(this.fbatchType)) {
			
			Label invDate = new Label(null == finance.getInvoice() ? "" : AON.DATE_FORMAT.format(finance.getInvoice().getIssueDate()));
			tab.addRow(row, invDate, COLS.FFT.getColWidth());
			
			Label invReference = new Label(null == finance.getInvoice() ? "" : finance.getInvoice().getReferenceCode());
			invReference.setTitle(null == finance.getInvoice() ? "" : finance.getInvoice().getReferenceCode());
			tab.addInlineStyle(invReference, COLS.FAC.getCellStyleClass());
			tab.addRow(row, invReference, COLS.FAC.getColWidth());
		
		} else {
			
			Label concept = new Label(finance.getConcept());
			concept.setTitle(finance.getConcept());
			tab.addInlineStyle(concept, COLS_PAYROLL.FAC.getCellStyleClass());
			tab.addRow(row, concept, COLS_PAYROLL.FAC.getColWidth());
			
		}
		
		Label titular = new Label(finance.getRegistryName());
		titular.setTitle(finance.getRegistryName());
		tab.addInlineStyle(titular, COLS.TIT.getCellStyleClass());
		tab.addRow(row, titular, COLS.TIT.getColWidth());

		Label amount = new Label(AON.FMT.format(finance.getAmount()) + " \u20ac");
		amount.addStyleName(AON.CSS.aonTextRight());
		tab.addRow(row, amount, COLS.AMO.getColWidth());
		
		String infoTitle = getInfoMessage(finance);
		AonTableButton infoButton = new AonTableButton(infoTitle, AON.CSS.aonIconInfo());

		if (notValidAccountBic(finance) || hasNegativeAmount(finance)) {
			issueDate.addStyleName(AON.CSS.aonColorOrange());
			titular.addStyleName(AON.CSS.aonColorOrange());
			amount.addStyleName(AON.CSS.aonColorOrange());

			issueDate.setTitle(infoTitle);
			amount.setTitle(infoTitle);
		}
		
		if(salaryAmountMismatch(finance)) {
			issueDate.addStyleName(AON.CSS.aonColorOrange());
			titular.addStyleName(AON.CSS.aonColorOrange());
			amount.addStyleName(AON.CSS.aonColorOrange());

			String title = "El importe de este vencimiento no coincide con el importe de la n\u00f3nmina asociada";
			issueDate.setTitle(title);
			titular.setTitle(title);
			amount.setTitle(title);
		} else if(missingSalary(finance)) {
			issueDate.addStyleName(AON.CSS.aonColorRed());
			titular.addStyleName(AON.CSS.aonColorRed());
			amount.addStyleName(AON.CSS.aonColorRed());

			issueDate.setTitle(infoTitle);
			titular.setTitle(infoTitle);
			amount.setTitle(infoTitle);
		} else if(finance.isReturned()) {
			issueDate.addStyleName(AON.CSS.aonColorRed());
			titular.addStyleName(AON.CSS.aonColorRed());
			amount.addStyleName(AON.CSS.aonColorRed());

			issueDate.setTitle(infoTitle);
			titular.setTitle(infoTitle);
			amount.setTitle(infoTitle);
		}

		tab.addRow(row, notValidAccountBic(finance) || hasNegativeAmount(finance) || missingSalary(finance) ? infoButton : new Label(), COLS.ACT.getColWidth());
		
		aviableFinances.put(finance.getId(), new FinanceRow(tab.getRowsCount(), finance));
	}
	
	private String getInfoMessage(Finance finance) {
		if(hasNegativeAmount(finance)) return "El vencimiento tiene un valor negativo";
		else if(notValidAccountBic(finance))
			return AonStringUtils.isBlank(finance.getBankAccountSafeValue())
			? "No existe cuenta bacanria asociada al titular"
			: !finance.getBankAccount().isValidBankAccount()
					? "La cuenta bacanria asociada al titular no es correcta"
					: AonStringUtils.isBlank(finance.getBic()) ? "No existe BIC asociado al titular" : "";
		else if (missingSalary(finance)) 
			return "Este vencimiento tiene asociada una nomina inexistente";
		else if(salaryAmountMismatch(finance))
 			return "El importe de este vencimiento no coincide con el importe de la n\u00f3nmina asociada";
		else if(finance.isReturned()) 
			return "El vencimiento es una devoluci\u00f3n";
		else
			return AonStringUtils.EMPTY;
	}
	
	private boolean notValidAccountBic(Finance finance) {
		return FBatchType.requiresBankAccount(this.fbatch.getType())
				&& (AonStringUtils.isBlank(finance.getBankAccountSafeValue())
					|| !finance.getBankAccount().isValidBankAccount()
					|| AonStringUtils.isBlank(finance.getBic()));
	}
	
	/** La nómina solo se exige en remesas de nóminas. */
	private boolean missingSalary(Finance finance) {
		return FBatchType.PAYROLL_PAYMENT == this.fbatchType && !finance.hasSalary();
	}

	private boolean salaryAmountMismatch(Finance finance) {
		return FBatchType.PAYROLL_PAYMENT == this.fbatchType
				&& finance.hasSalary() && null != finance.getSalaryTotalLiquid()
				&& (finance.getAmount() + finance.getExpenses()) != finance.getSalaryTotalLiquid();
	}

	/** Única condición de selección: la usan la casilla individual y "seleccionar todo". */
	private boolean isCheckable(Finance finance) {
		return !fbatch.isRecorded() && !fbatch.isGenerated()
				&& isSelectable(finance)
				&& !notValidAccountBic(finance)
				&& !hasNegativeAmount(finance)
				&& !missingSalary(finance);
	}
	
	private boolean hasNegativeAmount(Finance finance) {
		return !negativeCharge(finance) && finance.getAmount() < 0.00;
	}
	
	private boolean negativeCharge(Finance finance) {
		return !finance.isPayment() && finance.getAmount() < 0.00;
	}

	private boolean isSelectable(Finance finance) {
		return !notValidAccountBic(finance);
	}
	
	private void getList(Consumer<List<Finance>> success) {
		if (!isMoreData()) return; 
		
		if(this.fbatch.isRecorded()) return;
		
		clearSelection();
		
		FinanceParams params = searchPanel.getParams();
		params.setDescription(getSearchTextBox().getValue());

		FINANCE_SERVICE.getFinances(options.getDomainName(), options.getDomain(), options.getUser(), params, offset.intValue(), limit,
				new AsyncCallback<LinkedList<Finance>>() {

					@Override
					public void onSuccess(LinkedList<Finance> result) {
						success.accept(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						showError(caught.getMessage());
					}
				});
		
	}
	
	private void getSearchInfo() {
		FinanceParams params = searchPanel.getParams();
		params.setDescription(getSearchTextBox().getValue());

		FINANCE_SERVICE.getFinances(options.getDomainName(), options.getDomain(), options.getUser(), params, 0, Integer.MAX_VALUE,
				new AsyncCallback<LinkedList<Finance>>() {

					@Override
					public void onSuccess(LinkedList<Finance> result) {
						Set<PayMethodType> payMethods = result.stream()
								.filter(f -> null != f.getPayMethodType())
						        .map(Finance::getPayMethodType)
						        .collect(Collectors.toSet());
						
						Map<String, String> accountAliasMap = result.stream()
								.filter(f -> null != f.getBankAccount() && AonStringUtils.isNotBlank(f.getBankAccount().getBban1()) && AonStringUtils.isNotBlank(f.getBankAlias()))
						        .collect(Collectors.toMap(
						                f -> f.getBankAccount().getBban1(),
						                Finance::getBankAlias,
						                (v1, v2) -> v1   // Resolver conflictos si hay duplicados
						        ));
						
						searchPanel.setPaymethods(payMethods);
						searchPanel.setBankAccounts(accountAliasMap);
					}

					@Override
					public void onFailure(Throwable caught) {
						showError(caught.getMessage());
					}
				});
		
	}
	
	private void clearSelection() {
		aviableCount.setText("");
		addAviableButton.setEnabled(false);
		
		selectedFinances.clear();
	}
	
	protected abstract void saveFBatch(FBatch fBatch);
	protected abstract void showError(String message);
	protected abstract void hideMessage();
	
}
