package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.FBatchDetail;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.FBatchStatus.FBatchStatusVisitor;
import com.esferalia.aon.occam.api.model.type.FBatchType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class FBatchPaymentBatchedList extends AonCustomDockLayout {
	
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	static {
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);
	}
	
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
	
	// ProductList UI

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonTableButton checkAll; 
	private AonTableButton uncheckAll;
	private AonTableButton showHideFilter;
	private AonTableButton addSelectedButton;
	
	private HTMLPanel batchContainer = new HTMLPanel(AonStringUtils.EMPTY);
	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomDateBox issueDate = new AonCustomDateBox("Fecha");
	private AonCustomListBox bank = new AonCustomListBox("Cuenta Bancaria");
	private AonCustomListBox type = new AonCustomListBox("Tipo Fichero");
	private AonCustomTextBox status = new AonCustomTextBox("Estado");
	private AonCustomCheckBox confidential = new AonCustomCheckBox("Confidencial");
	private AonCustomTextBox totalAmount = new AonCustomTextBox("Importe Total");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private FinanceModuleOptions options;
	
	// Table UI
	private InlineLabel selectedCount = new InlineLabel("");;
	private LinkedHashMap<Integer, FinanceRow> batchedFinances;
	private LinkedHashSet<Integer> selectedFinances;
	
	private FBatchType fbatchType;
	private FBatch fbatch;
	
	private static enum COLS {
		  CHK(AonStringUtils.EMPTY		,"2rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, FEC("F. Venc."				,"5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, FFT("F. Factura"				,"5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, FAC("N. Factura"				,"8rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TIT("Titular"					,"5rem"  			,"flex: 1 1 5rem; min-width: 5rem; width: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, AMO("Importe"					,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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
		, AMO("Importe"					,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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
	public FBatchPaymentBatchedList(FinanceModuleOptions options, FBatchType fbatchType, FBatch fbatch) {
		super("Vencimientos Remesados");
		
		this.options = options;
		this.fbatchType = fbatchType;
		this.fbatch = fbatch;
		
		batchedFinances = new LinkedHashMap<Integer, FinanceRow>();
		selectedFinances = new LinkedHashSet<Integer>();
		
		addButtonsToolbar();
		
		hideToolbarFilterMessages();
		hideFilterButton();
		
		createBatchContainer();
		
		showHideFilter = new AonTableButton("", AON.CSS.aonIconFilterList());
		showHideFilter.addClickHandler(e -> batchContainer.setVisible(!batchContainer.isVisible()) );
		insertWidgetAfterSearchButton(showHideFilter, false);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("gap", "0");
		
		container.add(messagePanel);
		container.add(batchContainer);
	
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		
		container.add(tableContainer);
		
		add(container);
		
		onSearch();
	}

	@Override
	protected void onClearFilter() {}

	private void addButtonsToolbar() {
		checkAll = new AonTableButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll.setEnabled(!fbatch.isGenerated() && !fbatch.isRecorded());
		checkAll.addClickHandler(e -> checkAllAviable( true ));
		
		uncheckAll = new AonTableButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.setEnabled(!fbatch.isGenerated() && !fbatch.isRecorded());
		uncheckAll.addClickHandler(e -> checkAllAviable( false ));
		
		HTMLPanel checksPanel = new HTMLPanel(AonStringUtils.EMPTY);
		checksPanel.addStyleName(AON.CSS.aonDisplayFlex());
		checksPanel.add(checkAll);
		checksPanel.add(uncheckAll);
		addToolbarButton(checksPanel);
				
		addSelectedButton = new AonTableButton("Quitar de la remesa", AON.CSS.aonIconKeyboardDoubleArrowLeft());
		addSelectedButton.getElement().getStyle().setProperty("background-repeat", "no-repeat");
		addSelectedButton.setEnabled(false);
		addSelectedButton.addClickHandler(e -> {
			addSelectedButton.setEnabled(false);
			LinkedHashSet<Integer> moveIds = new LinkedHashSet<>();

			for (Integer financeId : selectedFinances) {
				FinanceRow financeRow = batchedFinances.get(financeId);
				if (null != financeRow) {
					Optional<FBatchDetail> fbatchDetail = fbatch.getBatchDetails().stream()
							.filter(fbatchDetial -> null != fbatchDetial.getFinance()
									&& fbatchDetial.getFinance().getId().equals(financeRow.getFinance().getId()))
							.findFirst();
					if (fbatchDetail.isPresent())
						fbatchDetail.get().setRemoved(true);

					batchedFinances.remove(financeRow.getFinance().getId());

					moveIds.add(financeRow.getFinance().getId());
				}
			}

			moveIds.forEach(moveId -> selectedFinances.remove(moveId));

			saveFBatch(fbatch);
		});
		addToolbarButton(addSelectedButton);
	}
	
	private void checkAllAviable(boolean check) {
		for (FinanceRow financeRow : batchedFinances.values()) {
			if (!isSelectable(financeRow.getFinance()) || notValidAccountBic(financeRow.getFinance()) || hasNegativeAmount(financeRow.getFinance()))
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

		selectedCount.setText((selectedFinances.size() > 0) ? AonNumberUtils.toString(selectedFinances.size()) : "");
		addSelectedButton.setEnabled(selectedFinances.size() > 0 && !fbatch.isGenerated() && !fbatch.isRecorded());
	}
	
	private void createBatchContainer() {
		batchContainer.addStyleName(AON.CSS.aonFlexColumn());
		batchContainer.getElement().getStyle().setProperty("margin", "0 1rem");
		batchContainer.getElement().getStyle().setProperty("padding", "1rem");
		batchContainer.getElement().getStyle().setProperty("background-color", "rgb(241, 241, 241)");
		batchContainer.getElement().getStyle().setProperty("border-radius", "10px");
		batchContainer.getElement().getStyle().setProperty("margin-bottom", ".5rem");
		
		description.getTextBox().setMaxLength(32);
		description.addValueChangeHandler(e -> {
			fbatch.setDescription(e.getValue());
			saveFBatch(fbatch);
		});
		
		issueDate.addValueChangeHandler(e -> {
			fbatch.setIssueDate(e.getValue());
			saveFBatch(fbatch);
		});
		
		type.clearItems();
		if(FBatchType.PAYROLL_PAYMENT == fbatchType)
			type.addItem("SEPA 34-14 N\u00f3mina (XML)", "10");
		else if(FBatchType.PAYMENT == fbatchType) {
			type.addItem("VISA", "0");
			type.addItem("SEPA 34-14 (XML)", "9");
		} else if(FBatchType.CHARGE == fbatchType) {
			type.addItem("Ninguno", "0");
			type.addItem("SEPA 19-14 CORE (XML)", "8");
			type.addItem("SEPA 58 ANTICIPO (XML)", "12");
			type.addItem("SEPA 58 COBRO (XML)", "13");
		}
		type.addChangeHandler(e -> {
			if (AonStringUtils.isBlank(type.getValue())) return;
			fbatch.setType(Byte.parseByte(type.getValue()));
			saveFBatch(fbatch);
		});
		
		bank.clearItems();
		bank.addItem("-", "");
		getEnterpriseBanks(companyBanks -> {
			companyBanks.stream().filter(companyBank -> companyBank.isActive()).forEach(companyBank -> {
				bank.addItem("(" + companyBank.getAlias() + ") " + companyBank.getBankAccount().toString(),
						companyBank.getId().toString());
			});
		});
		bank.addChangeHandler(e -> {
			fbatch.setRbank(AonStringUtils.isBlank(bank.getValue()) ? null
					: new RegistryBank().setId(Integer.parseInt(bank.getValue())));
			
			saveFBatch(fbatch);
		});
		
		
		if ( fbatch.isRecorded()) {
			status.addStyleName(AON.CSS.aonWidth320());
			status.getTextBox().setMaxLength(25);
			AonTableButton viewEntryButton = new AonTableButton(AON.MSG.viewAccountEntry(), AON.CSS.aonIconOpenInNew());
			viewEntryButton.addClickHandler(e -> showEntry() );
			status.addButton( viewEntryButton );
		}
		
		confidential.addValueChangeHandler(e -> {
			fbatch.setConfidential(e.getValue());
			saveFBatch(fbatch);
		});
		
		batchContainer.add(createRow(description, issueDate, totalAmount));
		batchContainer.add(createRow(type, status, confidential));
		batchContainer.add(createRow(bank));
	}
	
	private HTMLPanel createRow(Widget ...w) {
		HTMLPanel row = new HTMLPanel(AonStringUtils.EMPTY);
		row.addStyleName(AON.CSS.aonItemFlex());
		
		for(int i=0; i < w.length; i++)
			row.add(w[i]);
		
		return row;
	}
	
	public void setFBatch(FBatch fBatch) {
		this.fbatch = fBatch;
		onSearch();
	}
	
	private void onSearch() {
		fillFBatchInfo();
		searchData();
	}
	
	private void fillFBatchInfo() {
		description.setValue(fbatch.getDescription());
		description.setEnable(!fbatch.isRecorded());
		
		issueDate.setValue(fbatch.getIssueDate());
		issueDate.setEnable(!fbatch.isGenerated() && !fbatch.isRecorded());
		
		type.setValue(null != fbatch.getType() ? fbatch.getType().toString() : null);
		type.setEnabled(fbatch.getBatchDetails().isEmpty() || (!fbatch.isGenerated() && !fbatch.isRecorded()));
		
		status.setValue(null == fbatch.getStatus() ? "" : fbatch.getStatus().getDescription());
		status.setEnable(false);
		fbatch.getStatus().visit(new FBatchStatusVisitor() {
			
			@Override
			public void visitUnknown() {
				visitGenerated();
			}
			
			@Override
			public void visitPending() {
				status.removeStyleName(AON.CSS.aonColorGreen());
				status.removeStyleName(AON.CSS.aonBold());
				status.addStyleName(AON.CSS.aonColorRed());
			}
			
			@Override
			public void visitGenerated() {
				status.removeStyleName(AON.CSS.aonColorGreen());
				status.removeStyleName(AON.CSS.aonBold());
				status.removeStyleName(AON.CSS.aonColorRed());
			}
			
			@Override
			public void visitRecorded() {
				status.removeStyleName(AON.CSS.aonColorRed());
				status.addStyleName(AON.CSS.aonColorGreen());
				status.addStyleName(AON.CSS.aonBold());
			}
		});
		
		getEnterpriseBanks(companyBanks -> {
			bank.setValue(null != fbatch.getRbank() ? fbatch.getRbank().getId().toString() : "");
			bank.setEnabled(!fbatch.isGenerated() && !fbatch.isRecorded());
		});
		
		confidential.setValue(fbatch.isConfidential());
		confidential.setEnable(!fbatch.isGenerated() && !fbatch.isRecorded());
		
		String amountSum = null == fbatch.getBatchDetails() || fbatch.getBatchDetails().isEmpty() ? "0.00 \u20ac"
				: AON.FMT.format(fbatch.getBatchDetails().stream().map(fBatchDetail -> fBatchDetail.getAmount())
						.reduce(0.00, (a, b) -> a + b)) + " \u20ac";
		totalAmount.setValue(amountSum);
		totalAmount.setEnable(false);
	}
	
	private void searchData() {
		tableContainer.clear();
		tab = new AonCustomTable();
		tableScrollPanel = new ScrollPanel(tab);
		tableScrollPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		paintHeader();
		tableContainer.add(tableScrollPanel);
		searchDataList();
	}
	
	private void paintHeader() {
		selectedCount.addStyleName(AON.CSS.aonTextCenter());
		
		tab.createHeader();
		
		if(FBatchType.PAYROLL_PAYMENT.equals(this.fbatchType)) {
			for ( COLS_PAYROLL col : COLS_PAYROLL.values()) 
				tab.addHeader(col.equals(COLS_PAYROLL.CHK) ? selectedCount : new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
		} else {
			for ( COLS col : COLS.values()) 
				tab.addHeader(col.equals(COLS.CHK) ? selectedCount : new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
		}
		
	}
	
	private void searchDataList() {

		if (this.fbatch.getBatchDetails() == null || this.fbatch.getBatchDetails().isEmpty()) {
			HTMLPanel row = tab.createRow();
			InlineLabel label = new InlineLabel("No existen vencimientos en la remesa");
			row.add(label);
		} else if (this.fbatch.getBatchDetails() != null && !this.fbatch.getBatchDetails().isEmpty()) {
			this.fbatch.getBatchDetails().stream()
				.sorted((o1, o2) -> o1.getFinance().getRegistryName().compareTo(o2.getFinance().getRegistryName()))
				.forEach(batchDetail -> {
					paintRow(batchDetail);
				});
		}
	}
	
	private void paintRow(FBatchDetail fBatchDetail) {
		Finance finance = fBatchDetail.getFinance();
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> {}, ClickEvent.getType());
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), selectedFinances.contains(finance.getId()) ? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
		checkButton.setEnabled(!fbatch.isRecorded() && !fbatch.isGenerated());
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

				selectedCount.setText((selectedFinances.size() > 0) ? AonNumberUtils.toString(selectedFinances.size()) : "");
				addSelectedButton.setEnabled(selectedFinances.size() > 0 && !fbatch.isRecorded() && !fbatch.isGenerated());
			}
		});
		tab.addRow(row, finance.isPending() || finance.isBatched() || !missingSalary(finance) ? checkButton : new Label(), COLS.CHK.getColWidth());
		
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

		Label amount = new Label(AON.FMT.format(fBatchDetail.getAmount()) + " \u20ac");
		amount.addStyleName(AON.CSS.aonTextRight());
		tab.addRow(row, amount, COLS.AMO.getColWidth());
		
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

			String infoTitle = "Este vencimiento tiene asociada una nomina inexistente";
			issueDate.setTitle(infoTitle);
			titular.setTitle(infoTitle);
			amount.setTitle(infoTitle);
		}
		
		batchedFinances.put(finance.getId(), new FinanceRow(tab.getRowsCount(), finance));
	}
	
	private boolean notValidAccountBic(Finance finance) {
		return FBatchType.requiresBankAccount(this.fbatch.getType())
				&& (AonStringUtils.isBlank(finance.getBankAccountSafeValue())
					|| !finance.getBankAccount().isValidBankAccount()
					|| AonStringUtils.isBlank(finance.getBic()));
	}
	
	private boolean hasNegativeAmount(Finance finance) {
		// Mismo criterio que en la lista de disponibles: un cobro negativo es una devolución válida.
		return !negativeCharge(finance) && finance.getAmount() < 0.00;
	}
	
	private boolean negativeCharge(Finance finance) {
		return !finance.isPayment() && finance.getAmount() < 0.00;
	}

	private boolean missingSalary(Finance finance) {
		return FBatchType.PAYROLL_PAYMENT == this.fbatchType && !finance.hasSalary();
	}

	private boolean salaryAmountMismatch(Finance finance) {
		return FBatchType.PAYROLL_PAYMENT == this.fbatchType
				&& finance.hasSalary() && null != finance.getSalaryTotalLiquid()
				&& (finance.getAmount() + finance.getExpenses()) != finance.getSalaryTotalLiquid();
	}
	
	private boolean isSelectable(Finance finance) {
		return !notValidAccountBic(finance);
	}
	
	private void getEnterpriseBanks(Consumer<LinkedList<RegistryBank>> success) {
		COMMON_SERVICE.getCompanyBanks(options.getDomainName(), options.getDomain(), options.getUser(),
				new AsyncCallback<LinkedList<RegistryBank>>() {

					@Override
					public void onSuccess(LinkedList<RegistryBank> rbanks) {
						success.accept(rbanks);
					}

					@Override
					public void onFailure(Throwable caught) {
						// Error
					}
				});
	}
	
	private void showEntry() {
		FINANCE_SERVICE.getFBatchAccountEntry( options.getOccam(), fbatch.getId(), new AsyncCallback<AccountEntry>() {

			@Override
			public void onSuccess(AccountEntry entry) {
				AonCustomPopup entryDialog = new AonCustomPopup();
				entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
				entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
				entryDialog.setAnimationEnabled(true);
				entryDialog.setGlassEnabled(true);
				entryDialog.setModal(true);
				entryDialog.setCaption(AON.MSG.accountingDocument());
				AccountEntryModule module = new AccountEntryModule();
				module.onModuleLoad(new AccountEntryModuleOptions()
						.setParentWidget(entryDialog)
						.setDomainName(options.getDomainName())
						.setDomain(options.getDomain())
						.setUser(options.getUser())
						.setConfiguration(options.getConfiguration())
						.setAccountEntryId( entry.getId() )
						.setBackButtonVisible(false)
						.setSessionLogTabVisible(false)
						.setJournalTabVisible(false)
						.setExtraInfoTabVisible(false)
						.setResetAfterAccept(false)
						.setExternalCallback(new ModuleCallback() {

							private static final long serialVersionUID = -2947804456883665519L;

							@Override public void onRemove(IAccountEntryWrapper removed) {onChange(null);}
							@Override public void onFailure(Throwable caught) {onChange(null);}
							@Override public void onExit() { onChange(null);}

							@Override
							public void onChange(IAccountEntryWrapper changed) {
								entryDialog.clear();
								entryDialog.hide();
							}
						}));
				entryDialog.center();
				entryDialog.show();
				
			}

			@Override
			public void onFailure(Throwable caught) {
				AonMessageDialog.error("Error al cargar el asiento contable asociado a la remesa - " + caught.getMessage());
			}
		});
	}

	protected abstract void saveFBatch(FBatch fBatch);
	
}
