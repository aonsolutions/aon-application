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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.FBatchDetail;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class FBatchPaymentPayrollEntryModule extends SimpleLayoutPanel {

	private static FinanceServiceAsync FINANCE_SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

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

	private DockLayoutPanel dockLayoutPanel;

	// Toolbar

	private AonToolbar toolbar;
	private AonToolbarButton backButton;
	private AonToolbarButton saveButton;
	private AonToolbarButton sepaButton;
	private AonToolbarButton downloadButton;
	private AonToolbarButton deleteFileButton;
	private HTMLPanel messagePanel;

	private HTMLPanel container;

	// FBatch

	private HTMLPanel fbatchPanel;
	private FlexTable fbatchTable;
	private TextBox description = new TextBox();
	private AonDateBox issueDate = new AonDateBox();
	private ListBox bank = new ListBox();
	private ListBox type = new ListBox();
	private AonTableButton confidential = new AonTableButton("");
	private Label totalAmount = new Label();

	// Filter

	private HTMLPanel filterPanel;
	private FlexTable filterTable;
	private AonDateBox fromDueDate;
	private AonDateBox toDueDate;
	private ListBox confidentialFilter;
	private AonDoubleBox amount;
	private CheckBox nearbyNumbers;
	private TextBox concept;
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;

	private FinanceParams params;

	// FBatchDetail

	private SplitLayoutPanel financesPanel;

	// FBatchDetail (Aviable)

	private DockLayoutPanel aviableDockLayoutPanel;

	private HTMLPanel aviableFinanceToolbar;
	private AonToolbarSmallButton addAviableButton;

	private ScrollPanel aviableFinancePanel;
	private FlowPanel aviableFinanceContainer;
	private FlexTable aviableFinanceTable;
	private int aviableFinanceAutoWidth;

	private InlineLabel aviableCount;
	private LinkedHashMap<Integer, FinanceRow> aviableFinances;
	private LinkedHashSet<Integer> aviableFinancesSelected;

	// FBatchDetail (Selected)

	private DockLayoutPanel selectedDockLayoutPanel;

	private HTMLPanel selectedFinanceToolbar;
	private AonToolbarSmallButton addSelectedButton;

	private ScrollPanel selectedFinancePanel;
	private FlowPanel selectedFinanceContainer;
	private FlexTable selectedFinanceTable;
	private int selectedFinanceAutoWidth;

	private InlineLabel selectedCount;
	private LinkedHashMap<Integer, FinanceRow> selectedFinances;
	private LinkedHashSet<Integer> selectedFinancesSelected;

	// Search

	final private int limit = 100;
	final private MutableInt offset = new MutableInt(0);
	final private MutableInt moreData = new MutableInt(0);
	final private MutableInt searchEnabled = new MutableInt(0);
	private int lastScrollPos = 0;

	// Variables

	private FBatch fBatch;
	private FinanceModuleOptions opt;

	private boolean hasSaved;

	// -------------------------------------------------------------------
	// --------------------- ON MODULE LOAD ----------------------------
	// -------------------------------------------------------------------

	public void onModuleLoad(final FinanceModuleOptions opt, FBatch fBatch) {
		AON.ensureInjected();

		this.clear();

		this.opt = opt;
		this.fBatch = fBatch;
		this.hasSaved = false;

		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		aviableFinances = new LinkedHashMap<Integer, FinanceRow>();
		selectedFinances = new LinkedHashMap<Integer, FinanceRow>();

		aviableFinancesSelected = new LinkedHashSet<Integer>();
		selectedFinancesSelected = new LinkedHashSet<Integer>();

		createParams();

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		this.add(dockLayoutPanel);

		if (opt.getConfiguration() == null) {
			COMMON_SERVICE.getAonConfiguration(opt.getDomainName(), opt.getDomain(), opt.getUser(),
					new AsyncCallback<AonConfiguration>() {
						@Override
						public void onSuccess(AonConfiguration result) {
							opt.setConfiguration(result);
							loadModule(opt);
						}

						@Override
						public void onFailure(Throwable caught) {
							dockLayoutPanel.add(new Label(
									AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage() + "]"));
						}
					});
		} else {
			loadModule(opt);
		}
	}

	private void loadModule(final FinanceModuleOptions opt) {
		dockLayoutPanel.addNorth(getToolbarPanel(opt), AonToolbar.HEIGTH);

		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());

		messagePanel = new HTMLPanel("");
		container.add(messagePanel);

		financesPanel = new SplitLayoutPanel();
		financesPanel.setHeight((Window.getClientHeight() - 175) + "px");

		aviableDockLayoutPanel = new DockLayoutPanel(Unit.PX);

		filterPanel = new HTMLPanel("");
		filterPanel.getElement().getStyle().setProperty("margin", ".5rem");
		filterPanel.getElement().getStyle().setProperty("padding", ".5rem");
		filterPanel.getElement().getStyle().setProperty("background-color", "#f6f8fc");
		filterPanel.getElement().getStyle().setProperty("border", "#999 1px solid");
		filterTable = this.createFilterPanel(opt);
		filterPanel.add(filterTable);
		aviableDockLayoutPanel.addNorth(filterPanel, 125);

		aviableFinancePanel = new ScrollPanel();
		aviableFinancePanel.getElement().getStyle().setProperty("margin", "1rem");
		aviableFinanceContainer = new FlowPanel();
		aviableFinancePanel.setWidget(aviableFinanceContainer);
		aviableDockLayoutPanel.add(aviableFinancePanel);
		financesPanel.addWest(aviableDockLayoutPanel, Window.getClientWidth() / 2);

		selectedDockLayoutPanel = new DockLayoutPanel(Unit.PX);

		fbatchPanel = new HTMLPanel("");
		fbatchPanel.getElement().getStyle().setProperty("margin", ".5rem");
		fbatchPanel.getElement().getStyle().setProperty("padding", ".5rem");
		fbatchPanel.getElement().getStyle().setProperty("background-color", "#f6f8fc");
		fbatchPanel.getElement().getStyle().setProperty("border", "#999 1px solid");
		fbatchTable = this.createFbatchPanel(opt);
		fbatchPanel.add(fbatchTable);
		selectedDockLayoutPanel.addNorth(fbatchPanel, 125);

		selectedFinancePanel = new ScrollPanel();
		selectedFinancePanel.getElement().getStyle().setProperty("margin", "1rem");
		selectedFinanceContainer = new FlowPanel();
		selectedFinancePanel.setWidget(selectedFinanceContainer);
		selectedDockLayoutPanel.add(selectedFinancePanel);
		financesPanel.add(selectedDockLayoutPanel);

		container.add(financesPanel);

		dockLayoutPanel.add(container);

		aviableFinancePanel.addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = aviableFinancePanel.getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = aviableFinancePanel.getWidget().getOffsetHeight()
							- aviableFinancePanel.getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						search(opt, offset.getValue());
					}
				}
			}
		});

		// Auto search first time
		enableMoreData();
		aviableFinanceContainer.clear();
		aviableFinanceToolbar = getAviableToolbar();
		aviableFinanceContainer.add(aviableFinanceToolbar);
		aviableFinanceTable = getAviableFinanceTable();
		aviableFinanceContainer.add(aviableFinanceTable);
		offset.setValue(0);
		if (!fBatch.isAccounted())
			search(opt, offset.getValue());
		else {
			Label label = new Label("No se pueden a\u00f1adir vemcimientos a una remesa contabilizada");
			label.setStyleName(AON.CSS.aonBlockMessage());
			label.addStyleName(AON.CSS.aonBlockInfoMessage());
			label.addStyleName(AON.CSS.aonMarginTop());
			aviableFinanceContainer.add(label);
		}

		selectedFinanceContainer.clear();
		selectedFinanceToolbar = getSelectedToolbar();
		selectedFinanceContainer.add(selectedFinanceToolbar);
		selectedFinanceTable = getSelectedFinanceTable();
		selectedFinanceContainer.add(selectedFinanceTable);
		if (this.fBatch.getBatchDetails() == null || this.fBatch.getBatchDetails().isEmpty()) {
			Label label = new Label("No existen vencimientos en la remesa");
			label.setStyleName(AON.CSS.aonBlockMessage());
			label.addStyleName(AON.CSS.aonBlockInfoMessage());
			label.addStyleName(AON.CSS.aonMarginTop());
			selectedFinanceContainer.add(label);
		} else {
			paintSelectedFinanceTable(opt);
		}
	}

	private void initialize() {
		description.setValue(fBatch.getDescription());
		issueDate.setValue(this.fBatch.getIssueDate());
		type.setSelectedIndex(0);
		setSelectedValueLB(bank, null != fBatch.getRbank() ? fBatch.getRbank().getId().toString() : null);
		if (fBatch.isConfidential()) {
			confidential.addStyleName(AON.CSS.aonIconChecked());
			confidential.removeStyleName(AON.CSS.aonIconCheck());
		} else {
			confidential.addStyleName(AON.CSS.aonIconCheck());
			confidential.removeStyleName(AON.CSS.aonIconChecked());
		}
		String amountSum = null == fBatch.getBatchDetails() || fBatch.getBatchDetails().isEmpty() ? "0.00 \u20ac"
				: AON.FMT.format(fBatch.getBatchDetails().stream().map(fBatchDetail -> fBatchDetail.getAmount())
						.reduce(0.00, (a, b) -> a + b)) + " \u20ac";
		totalAmount.setText(amountSum);
		
		enableMoreData();
		enableSearch();
		offset.setValue(0);

		selectedFinances.clear();
		selectedFinancesSelected.clear();

		selectedFinanceContainer.clear();
		selectedFinanceToolbar = getSelectedToolbar();
		selectedFinanceContainer.add(selectedFinanceToolbar);
		selectedFinanceTable = getSelectedFinanceTable();
		selectedFinanceContainer.add(selectedFinanceTable);

		aviableFinances.clear();
		aviableFinancesSelected.clear();

		aviableFinanceContainer.clear();
		aviableFinanceToolbar = getAviableToolbar();
		aviableFinanceContainer.add(aviableFinanceToolbar);
		aviableFinanceTable = getAviableFinanceTable();
		aviableFinanceContainer.add(aviableFinanceTable);

		if (!fBatch.isAccounted())
			search(opt, offset.getValue());
		else {
			Label label = new Label("No se pueden a\u00f1adir vemcimientos a una remesa contabilizada");
			label.setStyleName(AON.CSS.aonBlockMessage());
			label.addStyleName(AON.CSS.aonBlockInfoMessage());
			label.addStyleName(AON.CSS.aonMarginTop());
			aviableFinanceContainer.add(label);
		}

		if (this.fBatch.getBatchDetails() == null || this.fBatch.getBatchDetails().isEmpty()) {
			Label label = new Label("No existen vencimientos en la remesa");
			label.setStyleName(AON.CSS.aonBlockMessage());
			label.addStyleName(AON.CSS.aonBlockInfoMessage());
			label.addStyleName(AON.CSS.aonMarginTop());
			selectedFinanceContainer.add(label);
		} else {
			paintSelectedFinanceTable(opt);
		}
	}

	// -------------------------------------------------------------------
	// --------------------- FILTER COLUMNS ----------------------------
	// -------------------------------------------------------------------

	private FlexTable createFilterPanel(FinanceModuleOptions opt) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.getElement().getStyle().setProperty("margin-left", "1rem");

		amount = new AonDoubleBox();
		amount.setVisibleLength(6);
		amount.setValue(null, false);
		amount.addValueChangeHandler(e -> search(opt));

		nearbyNumbers = new CheckBox(AON.MSG.nearbyNumbers());
		nearbyNumbers.setStyleName(AON.CSS.aonPaddingLeft());
		nearbyNumbers.addClickHandler(e -> search(opt));

		concept = new TextBox();
		concept.setStyleName(AON.CSS.aonInputText());
		concept.addValueChangeHandler(e -> search(opt));

		fromDueDate = new AonDateBox();
		fromDueDate.addValueChangeHandler(e -> search(opt));

		toDueDate = new AonDateBox();
		toDueDate.addValueChangeHandler(e -> search(opt));

		confidentialFilter = new ListBox();
		confidentialFilter.setStyleName(AON.CSS.aonMarginLeft());
		confidentialFilter.setWidth("100px");
		confidentialFilter.addItem("NO confidenciales");
		confidentialFilter.addItem("Confidenciales");
		confidentialFilter.addItem(" Todos ");
		confidentialFilter.setSelectedIndex(2);
		confidentialFilter.addChangeHandler(e -> search(opt));

		cleanButton = new AonSearchPanelButton(AON.MSG.clean(), AON.CSS.aonIconClear());
		cleanButton.addStyleName(AON.CSS.aonMarginLeft());
		cleanButton.addClickHandler(e -> {
			resetFilter();
			search(opt);
		});

		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(), AON.CSS.aonIconSearch());
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(e -> search(opt));

		table.setWidget(0, 0, new Label(AON.MSG.amount()));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());

		FlowPanel amountPanel = new FlowPanel();
		amountPanel.setStyleName(AON.CSS.aonNowrap());
		amountPanel.add(amount);
		amountPanel.add(nearbyNumbers);
		table.setWidget(0, 1, amountPanel);

		table.setWidget(0, 2, new Label(AON.MSG.concept()));
		table.getCellFormatter().setStyleName(0, 2, AON.CSS.aonTableLabel());

		table.setWidget(0, 3, concept);

		table.setWidget(1, 0, new Label(AON.MSG.dueDate()));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());

		FlowPanel dueDatePanel = new FlowPanel();
		dueDatePanel.setStyleName(AON.CSS.aonNowrap());
		dueDatePanel.add(fromDueDate);
		InlineLabel dueTo = new InlineLabel(AON.MSG.to());
		dueTo.setStyleName(AON.CSS.aonItalic());
		dueTo.addStyleName(AON.CSS.aonMarginRight());
		dueTo.addStyleName(AON.CSS.aonMarginLeft());
		dueDatePanel.add(dueTo);
		dueDatePanel.add(toDueDate);
		table.setWidget(1, 1, dueDatePanel);

		table.setWidget(1, 2, new Label(AON.MSG.confidential()));
		table.getCellFormatter().setStyleName(1, 2, AON.CSS.aonTableLabel());

		table.setWidget(1, 3, confidentialFilter);

		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonNowrap());
		buttonsPanel.add(cleanButton);
		buttonsPanel.add(refreshButton);
		table.setWidget(2, 0, buttonsPanel);

		return table;
	}

	private void search(FinanceModuleOptions opt2) {
		aviableFinances.clear();
		aviableFinancesSelected.clear();

		enableMoreData();
		aviableFinanceContainer.clear();
		aviableFinanceToolbar = getAviableToolbar();
		aviableFinanceContainer.add(aviableFinanceToolbar);
		aviableFinanceTable = getAviableFinanceTable();
		aviableFinanceContainer.add(aviableFinanceTable);
		offset.setValue(0);

		enableSearch();
		search(opt, offset.intValue());
	}

	private void resetFilter() {
		amount.setValue(null);
		nearbyNumbers.setValue(false);
		concept.setValue(null);
		fromDueDate.setValue(null);
		toDueDate.setValue(null);
		confidentialFilter.setSelectedIndex(2);

		createParams();
	}

	private void createParams() {
		params = new FinanceParams().setDomain(opt.getDomain()).setPayment(true).setPending(true).setIsPayroll(true)
				.setPayMethodType(PayMethodType.BANK_TRANSFER);
	}

	// -------------------------------------------------------------------
	// --------------------- FBATCH COLUMNS ----------------------------
	// -------------------------------------------------------------------

	private FlexTable createFbatchPanel(FinanceModuleOptions opt) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.getElement().getStyle().setProperty("margin-left", "1rem");

		table.setWidget(0, 0, new InlineLabel(AON.MSG.description()));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		description = new TextBox();
		description.setValue(fBatch.getDescription());
		description.setMaxLength(32);
		description.addValueChangeHandler(e -> {
			fBatch.setDescription(e.getValue());
			saveButton.setEnabled(true);
		});
		table.setWidget(0, 1, description);
		table.getWidget(0, 1).setStyleName(AON.CSS.aonInputText());

		table.setWidget(0, 2, new InlineLabel(AON.MSG.date()));
		table.getCellFormatter().setStyleName(0, 2, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(0, 2).getStyle().setTextAlign(TextAlign.RIGHT);
		issueDate.setValue(this.fBatch.getIssueDate());
		issueDate.addValueChangeHandler(e -> {
			fBatch.setIssueDate(e.getValue());
			saveButton.setEnabled(true);
		});
		table.setWidget(0, 3, issueDate);

		table.setWidget(1, 0, new InlineLabel("Tipo Fichero"));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		type.clear();
		type.addItem("SEPA 34-14 N\u00f3mina (XML)", "10");
		type.setSelectedIndex(0);
		table.setWidget(1, 1, type);
		table.getFlexCellFormatter().setColSpan(1, 1, 3);

		table.setWidget(2, 0, new InlineLabel(AON.MSG.bankAccount()));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		bank.clear();
		bank.addItem("-");
		getEnterpriseBanks(opt, companyBanks -> {
			companyBanks.stream().filter(companyBank -> companyBank.isActive()).forEach(companyBank -> {
				bank.addItem("(" + companyBank.getAlias() + ") " + companyBank.getBankAccount().toString(),
						companyBank.getId().toString());
			});
			setSelectedValueLB(bank, null != fBatch.getRbank() ? fBatch.getRbank().getId().toString() : null);
		});
		bank.addChangeHandler(e -> {
			fBatch.setRbank(bank.getSelectedIndex() == 0 ? null
					: new RegistryBank().setId(Integer.parseInt(bank.getSelectedValue())));
			saveButton.setEnabled(true);
		});
		table.setWidget(2, 1, bank);
		table.getFlexCellFormatter().setColSpan(2, 1, 3);

		table.setWidget(3, 0, new InlineLabel(AON.MSG.confidential()));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		confidential = new AonTableButton(AON.MSG.selectAction(),
				fBatch.isConfidential() ? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
		confidential.addClickHandler(e -> {
			fBatch.setConfidential(!fBatch.isConfidential());
			if (fBatch.isConfidential()) {
				confidential.addStyleName(AON.CSS.aonIconChecked());
				confidential.removeStyleName(AON.CSS.aonIconCheck());
			} else {
				confidential.addStyleName(AON.CSS.aonIconCheck());
				confidential.removeStyleName(AON.CSS.aonIconChecked());
			}
			saveButton.setEnabled(true);
		});
		table.setWidget(3, 1, confidential);

		table.setWidget(3, 2, new InlineLabel(AON.MSG.amount() + " " + AON.MSG.total()));
		table.getCellFormatter().setStyleName(3, 2, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(3, 2).getStyle().setTextAlign(TextAlign.RIGHT);
		String amountSum = null == fBatch.getBatchDetails() || fBatch.getBatchDetails().isEmpty() ? "0.00 \u20ac"
				: AON.FMT.format(fBatch.getBatchDetails().stream().map(fBatchDetail -> fBatchDetail.getAmount())
						.reduce(0.00, (a, b) -> a + b)) + " \u20ac";
		totalAmount.setText(amountSum);
		table.setWidget(3, 3, totalAmount);

		return table;
	}

	// -------------------------------------------------------------------
	// -------------------------- SEARCH -------------------------------
	// -------------------------------------------------------------------

	public void disableMoreData() {
		moreData.setValue(-1);
	}

	public void enableMoreData() {
		moreData.setValue(0);
	}

	public boolean isMoreData() {
		return (moreData.getValue() == 0);
	}

	public void enableSearch() {
		searchEnabled.setValue(0);
	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0);
	}

	public void disableSearch() {
		searchEnabled.setValue(-1);
	}

	private void search(final FinanceModuleOptions opt, final int ofs) {
		if (!isMoreData())
			return;
		createFilter();

		FINANCE_SERVICE.getFinances(opt.getDomainName(), opt.getDomain(), opt.getUser(), params, ofs, limit,
				new AsyncCallback<LinkedList<Finance>>() {

					@Override
					public void onSuccess(LinkedList<Finance> result) {
						if (result != null && !result.isEmpty()) {
							result.forEach(finance -> paintRow(opt, finance));
							offset.setValue(ofs + result.size());
							enableMoreData();
						} else {
							Label label = new Label(AON.MSG.noData());
							label.setStyleName(AON.CSS.aonBlockMessage());
							label.addStyleName(AON.CSS.aonBlockInfoMessage());
							label.addStyleName(AON.CSS.aonMarginTop());
							aviableFinanceContainer.add(label);
							disableMoreData();
						}
						enableSearch();
					}

					@Override
					public void onFailure(Throwable caught) {
						showError(caught.getMessage());
					}
				});
	}

	private void createFilter() {
		createParams();

		params.setAmount((amount.getValue() != null && amount.getValue() != 0) ? amount.getValue() : null)
				.setNearbyNumbers(nearbyNumbers.getValue()).setConcept(concept.getValue())
				.setFromDueDate(fromDueDate.getValue()).setToDueDate(toDueDate.getValue()).setSecurityLevel(
						confidentialFilter != null ? SecurityLevel.safeValueOf(confidentialFilter.getSelectedIndex())
								: SecurityLevel.OFFICIAL);
	}

	private void showError(String msg) {
		if (AonStringUtils.isBlank(msg)) {
			msg = "Se ha producido un error no codificado.";
		}
		toolbar.showErrorMessage(msg);
	}

	// -------------------------------------------------------------------
	// ---------------------- TABLE COLUMNS ----------------------------
	// -------------------------------------------------------------------

	private static enum AVIABLE_COLS {
		CHK(AonStringUtils.EMPTY, 20, AON.CSS.aonTextCenter()), FEC("F. Venc.", 80, AON.CSS.aonTextCenter()),
		DOC("Documento", 100, AON.CSS.aonTextLeft()), TIT("Titular", 0, AON.CSS.aonTextLeft()),
		AMO("Importe", 100, AON.CSS.aonTextRight()), SEL(AonStringUtils.EMPTY, 20, AON.CSS.aonTextCenter());

		String headerLabel;
		int colWidth;
		String cellStyleClass;

		private AVIABLE_COLS(String headerLabel, int colWidth) {
			this(headerLabel, colWidth, null);
		}

		private AVIABLE_COLS(String headerLabel, int colWidth, String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}

		public int getColWidth() {
			return colWidth;
		}

		public String getHeaderLabel() {
			return headerLabel;
		}

		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}

	// -------------------------------------------------------------------
	// ---------------------- AVIABLE TABLE ----------------------------
	// -------------------------------------------------------------------

	private HTMLPanel getAviableToolbar() {
		HTMLPanel toolbar = new HTMLPanel("");
		toolbar.setStyleName(AON.CSS.aonToolbarSmall());
		toolbar.addStyleName(AON.CSS.aonDisplayFlex());
		toolbar.getElement().getStyle().setProperty("border-bottom", "solid #c4c4c4 1px");
		toolbar.getElement().getStyle().setProperty("margin-bottom", ".5rem");
		toolbar.getElement().getStyle().setProperty("align-items", "center");

		AonToolbarSmallButton checkAll = new AonToolbarSmallButton(AON.MSG.selectAll(), AON.CSS.aonIconChecked());
		checkAll.setVisible(!fBatch.isAccounted());
		checkAll.addClickHandler(e -> checkAllAviable(true));
		toolbar.add(checkAll);

		AonToolbarSmallButton uncheckAll = new AonToolbarSmallButton(AON.MSG.selectNone(), AON.CSS.aonIconCheck());
		uncheckAll.setVisible(!fBatch.isAccounted());
		uncheckAll.addClickHandler(e -> checkAllAviable(false));
		toolbar.add(uncheckAll);

		Label title = new Label("Vencimientos disponibles");
		title.setStyleName(AON.CSS.aonToolbarSmallTitle());
		title.getElement().getStyle().setProperty("width", "100%");
		title.getElement().getStyle().setProperty("text-align", "center");
		toolbar.add(title);

		addAviableButton = new AonToolbarSmallButton("A\u00f1adir a la remesa", AON.CSS.aonIconMoveRightAll());
		addAviableButton.setVisible(!fBatch.isAccounted());
		addAviableButton.setEnabled(false);
		addAviableButton.addClickHandler(e -> {
			LinkedHashSet<Integer> moveIds = new LinkedHashSet<>();

			for (Integer financeId : aviableFinancesSelected) {
				FinanceRow financeRow = aviableFinances.get(financeId);
				if (null != financeRow) {
					fBatch.addBatchDetail(new FBatchDetail().setDomain(fBatch.getDomain()).setFbatch(fBatch.getId())
							.setFinance(financeRow.getFinance()).setAmount(financeRow.getFinance().getAmount())
							.setStatus((byte) 1).setRemoved(false));

					selectedFinances.put(financeRow.getFinance().getId(),
							new FinanceRow(financeRow.getRow(), financeRow.getFinance()));

					moveIds.add(financeRow.getFinance().getId());
				}
			}

			save();

//			moveIds.forEach(moveId -> aviableFinancesSelected.remove(moveId));
//			
//			selectedFinanceContainer.clear();
//			selectedFinanceToolbar = getSelectedToolbar();
//			selectedFinanceContainer.add(selectedFinanceToolbar);
//			selectedFinanceTable = getSelectedFinanceTable();
//			selectedFinanceContainer.add(selectedFinanceTable);
//			
//			selectedFinances.values().stream().map(financeRow -> financeRow.getFinance()).forEach(financeIt -> paintSelectedRow(opt, financeIt, selectedFinanceTable.getRowCount()));
//			
//			String amountSum = selectedFinances.size() == 0 ? "0.00 \u20ac" : AON.FMT.format(selectedFinances.values().stream().map(financeRow -> financeRow.getFinance().getAmount()).reduce(0.00, (a, b) -> a + b)) + " \u20ac";
//			totalAmount.setText(amountSum);
//			
//			if(aviableFinances.isEmpty()) {
//				Label label = new Label("No existen vencimientos disponibles para a\u00f1adir a la remesa");
//				label.setStyleName(AON.CSS.aonBlockMessage());
//				label.addStyleName(AON.CSS.aonBlockInfoMessage());
//				label.addStyleName(AON.CSS.aonMarginTop());
//				aviableFinanceContainer.add(label);
//			}
//			
//			
//			aviableFinances.clear();
//			aviableFinancesSelected.clear();
//			
//			enableMoreData();
//			aviableFinanceContainer.clear();
//			aviableFinanceToolbar = getAviableToolbar();
//			aviableFinanceContainer.add(aviableFinanceToolbar);
//			aviableFinanceTable = getAviableFinanceTable();
//			aviableFinanceContainer.add(aviableFinanceTable);
//			offset.setValue(0);
//			
//			enableSearch();
//			search(opt, offset.intValue());
//			
//			saveButton.setEnabled(true);
		});
		toolbar.add(addAviableButton);

		return toolbar;
	}

	private void checkAllAviable(boolean check) {
		for (FinanceRow financeRow : aviableFinances.values()) {
			if (AonStringUtils.isBlank(financeRow.getFinance().getBankAccountSafeValue())
					|| !financeRow.getFinance().getBankAccount().isValidBankAccount()
					|| AonStringUtils.isBlank(financeRow.getFinance().getBic()))
				continue;
			financeRow.getFinance().setSelected(check);
			if (check)
				aviableFinancesSelected.add(financeRow.getFinance().getId());
			else
				aviableFinancesSelected.remove(financeRow.getFinance().getId());
		}

		for (int row = 1; row < aviableFinanceTable.getRowCount(); row++) {
			Widget w = aviableFinanceTable.getWidget(row, 0);
			if (check) {
				w.addStyleName(AON.CSS.aonIconChecked());
				w.removeStyleName(AON.CSS.aonIconCheck());
			} else {
				w.addStyleName(AON.CSS.aonIconCheck());
				w.removeStyleName(AON.CSS.aonIconChecked());
			}
		}

		aviableCount.setText(
				(aviableFinancesSelected.size() > 0) ? AonNumberUtils.toString(aviableFinancesSelected.size()) : "");
		addAviableButton.setEnabled(aviableFinancesSelected.size() > 0);
	}

	protected FlexTable getAviableFinanceTable() {
		aviableFinanceTable = new FlexTable();
		aviableFinanceTable.setStyleName(AON.CSS.aonGrid());

		aviableFinanceAutoWidth = aviableFinanceContainer.getOffsetWidth() - 36;
		for (AVIABLE_COLS col : AVIABLE_COLS.values()) {
			if (col != AVIABLE_COLS.TIT) {
				aviableFinanceAutoWidth -= (col.getColWidth() + 2);
			}
		}

		aviableCount = new InlineLabel();
		for (AVIABLE_COLS col : AVIABLE_COLS.values()) {
			if (col == AVIABLE_COLS.TIT) {
				aviableFinanceTable.getColumnFormatter().setWidth(col.ordinal(), aviableFinanceAutoWidth + "px");
			} else {
				aviableFinanceTable.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth() + "px");
			}
			aviableFinanceTable.setWidget(0, col.ordinal(),
					col == AVIABLE_COLS.CHK ? aviableCount : new Label(col.getHeaderLabel()));
			aviableFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(), AON.CSS.aonGridHeader());
			if (col.getCellStyleClass() != null) {
				aviableFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(), col.getCellStyleClass());
				aviableFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(), AON.CSS.aonNowrap());
			}
		}
		return aviableFinanceTable;
	}

	private void paintRow(final FinanceModuleOptions opt, Finance finance) {
		if (!isBatchedFinance(finance)) {
			int row = aviableFinanceTable.getRowCount();
			aviableFinances.put(finance.getId(), new FinanceRow(row, finance));
			paintRow(opt, finance, row);
		}
	}

	private boolean isBatchedFinance(Finance finance) {
		Optional<FBatchDetail> findFinance = fBatch.getBatchDetails().stream().filter(
				fbatchDetail -> fbatchDetail.getFinance().getId().equals(finance.getId()) && !fbatchDetail.isRemoved())
				.findAny();
		return findFinance.isPresent();
	}

	private void paintRow(final FinanceModuleOptions opt, Finance finance, int row) {
		int col = 0;

		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(),
				aviableFinancesSelected.contains(finance.getId()) ? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
		checkButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (aviableFinancesSelected.contains(finance.getId())) {
					aviableFinancesSelected.remove(finance.getId());
					checkButton.addStyleName(AON.CSS.aonIconCheck());
					checkButton.removeStyleName(AON.CSS.aonIconChecked());
				} else {
					aviableFinancesSelected.add(finance.getId());
					checkButton.addStyleName(AON.CSS.aonIconChecked());
					checkButton.removeStyleName(AON.CSS.aonIconCheck());
				}

				aviableCount.setText(
						(aviableFinancesSelected.size() > 0) ? AonNumberUtils.toString(aviableFinancesSelected.size())
								: "");
				addAviableButton.setEnabled(aviableFinancesSelected.size() > 0);
			}
		});

		String infoTitle = AonStringUtils.isBlank(finance.getBankAccountSafeValue())
				? "No existe cuenta bacanria asociada al titular"
				: !finance.getBankAccount().isValidBankAccount()
						? "La cuenta bacanria asociada al titular no es correcta"
						: AonStringUtils.isBlank(finance.getBic()) ? "No existe BIC asociado al titular" : "";
		AonTableButton infoButton = new AonTableButton(infoTitle, AON.CSS.aonIconInfo());

		Label issueDate = new Label(AON.DATE_FORMAT.format(finance.getDueDate()));

		Label document = new Label(finance.getRegistryDocument());

		Label titular = new Label(finance.getRegistryName());
		titular.setTitle(finance.getRegistryName());
		titular.setWidth(aviableFinanceAutoWidth + "px");
		titular.setStyleName(AON.CSS.aonTruncate());

		Label amount = new Label(AON.FMT.format(finance.getAmount()) + " \u20ac");
		amount.addStyleName(AON.CSS.aonTextRight());

		if (AonStringUtils.isBlank(finance.getBankAccountSafeValue()) || !finance.getBankAccount().isValidBankAccount()
				|| AonStringUtils.isBlank(finance.getBic())) {
			issueDate.addStyleName(AON.CSS.aonColorOrange());
			document.addStyleName(AON.CSS.aonColorOrange());
			titular.addStyleName(AON.CSS.aonColorOrange());
			amount.addStyleName(AON.CSS.aonColorOrange());

			issueDate.setTitle(infoTitle);
			document.setTitle(infoTitle);
			amount.setTitle(infoTitle);
		}

		AonTableButton addButton = new AonTableButton("A\u00f1adir a la remesa", AON.CSS.aonIconMoveRight());
		addButton.addClickHandler(e -> {

			this.fBatch.addBatchDetail(new FBatchDetail().setDomain(fBatch.getDomain()).setFbatch(fBatch.getId())
					.setFinance(finance).setAmount(finance.getAmount()).setStatus((byte) 1).setRemoved(false));

			save();

//			selectedFinances.put(finance.getId(), new FinanceRow(row, finance));
//			
//			selectedFinanceContainer.clear();
//			selectedFinanceToolbar = getSelectedToolbar();
//			selectedFinanceContainer.add(selectedFinanceToolbar);
//			selectedFinanceTable = getSelectedFinanceTable();
//			selectedFinanceContainer.add(selectedFinanceTable);
//			
//			selectedFinances.values().stream().map(financeRow -> financeRow.getFinance()).forEach(financeIt -> paintSelectedRow(opt, financeIt, selectedFinanceTable.getRowCount()));
//			
//			String amountSum = selectedFinances.size() == 0 ? "0.00 \u20ac" : AON.FMT.format(selectedFinances.values().stream().map(financeRow -> financeRow.getFinance().getAmount()).reduce(0.00, (a, b) -> a + b)) + " \u20ac";
//			totalAmount.setText(amountSum);
//			
//			if(aviableFinances.isEmpty()) {
//				Label label = new Label("No existen vencimientos disponibles para a\u00f1adir a la remesa");
//				label.setStyleName(AON.CSS.aonBlockMessage());
//				label.addStyleName(AON.CSS.aonBlockInfoMessage());
//				label.addStyleName(AON.CSS.aonMarginTop());
//				aviableFinanceContainer.add(label);
//			}
//			
//			
//			aviableFinances.clear();
//			aviableFinancesSelected.clear();
//			
//			enableMoreData();
//			aviableFinanceContainer.clear();
//			aviableFinanceToolbar = getAviableToolbar();
//			aviableFinanceContainer.add(aviableFinanceToolbar);
//			aviableFinanceTable = getAviableFinanceTable();
//			aviableFinanceContainer.add(aviableFinanceTable);
//			offset.setValue(0);
//			
//			enableSearch();
//			search(opt, offset.intValue());
//			
//			saveButton.setEnabled(true);
		});

		aviableFinanceTable.setWidget(row, col,
				finance.getBankAccount().isValidBankAccount() && AonStringUtils.isNotBlank(finance.getBic())
						? checkButton
						: new Label());
		++col;
		aviableFinanceTable.setWidget(row, col, issueDate);
		++col;
		aviableFinanceTable.setWidget(row, col, document);
		++col;
		aviableFinanceTable.setWidget(row, col, titular);
		++col;
		aviableFinanceTable.setWidget(row, col, amount);
		++col;
		aviableFinanceTable.setWidget(row, col,
				finance.getBankAccount().isValidBankAccount() && AonStringUtils.isNotBlank(finance.getBic()) ? addButton
						: infoButton);

	}

	// -------------------------------------------------------------------
	// ---------------------- TABLE COLUMNS ----------------------------
	// -------------------------------------------------------------------

	private static enum SELECTED_COLS {
		SEL(AonStringUtils.EMPTY, 20, AON.CSS.aonTextCenter()), CHK(AonStringUtils.EMPTY, 20, AON.CSS.aonTextCenter()),
		FEC("F. Venc.", 80, AON.CSS.aonTextCenter()), DOC("Documento", 100, AON.CSS.aonTextLeft()),
		TIT("Titular", 0, AON.CSS.aonTextLeft()), AMO("Importe", 100, AON.CSS.aonTextRight());

		String headerLabel;
		int colWidth;
		String cellStyleClass;

		private SELECTED_COLS(String headerLabel, int colWidth) {
			this(headerLabel, colWidth, null);
		}

		private SELECTED_COLS(String headerLabel, int colWidth, String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}

		public int getColWidth() {
			return colWidth;
		}

		public String getHeaderLabel() {
			return headerLabel;
		}

		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}

	// -------------------------------------------------------------------
	// ---------------------- SELECTED TABLE ---------------------------
	// -------------------------------------------------------------------

	private HTMLPanel getSelectedToolbar() {
		HTMLPanel toolbar = new HTMLPanel("");
		toolbar.setStyleName(AON.CSS.aonToolbarSmall());
		toolbar.addStyleName(AON.CSS.aonDisplayFlex());
		toolbar.getElement().getStyle().setProperty("border-bottom", "solid #c4c4c4 1px");
		toolbar.getElement().getStyle().setProperty("margin-bottom", ".5rem");
		toolbar.getElement().getStyle().setProperty("align-items", "center");

		addSelectedButton = new AonToolbarSmallButton("Quitar de la remesa", AON.CSS.aonIconMoveLeftAll());
		addSelectedButton.setVisible(!fBatch.isAccounted());
		addSelectedButton.setEnabled(false);
		addSelectedButton.getElement().getStyle().setProperty("margin-right", ".8rem");
		addSelectedButton.addClickHandler(e -> {
			LinkedHashSet<Integer> moveIds = new LinkedHashSet<>();

			for (Integer financeId : selectedFinancesSelected) {
				FinanceRow financeRow = selectedFinances.get(financeId);
				if (null != financeRow) {
					Optional<FBatchDetail> fbatchDetail = fBatch.getBatchDetails().stream()
							.filter(fbatchDetial -> null != fbatchDetial.getFinance()
									&& fbatchDetial.getFinance().getId() == financeRow.getFinance().getId())
							.findFirst();
					if (fbatchDetail.isPresent())
						fbatchDetail.get().setRemoved(true);

					selectedFinances.remove(financeRow.getFinance().getId());

					moveIds.add(financeRow.getFinance().getId());
				}
			}

			moveIds.forEach(moveId -> selectedFinancesSelected.remove(moveId));

			save();

//			selectedFinanceContainer.clear();
//			selectedFinanceToolbar = getSelectedToolbar();
//			selectedFinanceContainer.add(selectedFinanceToolbar);
//			selectedFinanceTable = getSelectedFinanceTable();
//			selectedFinanceContainer.add(selectedFinanceTable);
//			
//			selectedFinances.values().stream().map(financeRow -> financeRow.getFinance()).forEach(financeIt -> paintSelectedRow(opt, financeIt, selectedFinanceTable.getRowCount()));
//			
//			String amountSum = selectedFinances.size() == 0 ? "0.00 \u20ac" : AON.FMT.format(selectedFinances.values().stream().map(financeRow -> financeRow.getFinance().getAmount()).reduce(0.00, (a, b) -> a + b)) + " \u20ac";
//			totalAmount.setText(amountSum);
//			
//			if(selectedFinances.isEmpty()) {
//				Label label = new Label("No existen vencimientos en la remesa");
//				label.setStyleName(AON.CSS.aonBlockMessage());
//				label.addStyleName(AON.CSS.aonBlockInfoMessage());
//				label.addStyleName(AON.CSS.aonMarginTop());
//				selectedFinanceContainer.add(label);
//			}
//			
//			aviableFinances.clear();
//			aviableFinancesSelected.clear();
//			
//			enableMoreData();
//			aviableFinanceContainer.clear();
//			aviableFinanceToolbar = getAviableToolbar();
//			aviableFinanceContainer.add(aviableFinanceToolbar);
//			aviableFinanceTable = getAviableFinanceTable();
//			aviableFinanceContainer.add(aviableFinanceTable);
//			offset.setValue(0);
//			
//			enableSearch();
//			search(opt, offset.intValue());
//			
//			saveButton.setEnabled(true);
		});
		toolbar.add(addSelectedButton);

		AonToolbarSmallButton checkAll = new AonToolbarSmallButton(AON.MSG.selectAll(), AON.CSS.aonIconChecked());
		checkAll.setVisible(!fBatch.isAccounted());
		checkAll.addClickHandler(e -> checkAllSelected(true));
		toolbar.add(checkAll);

		AonToolbarSmallButton uncheckAll = new AonToolbarSmallButton(AON.MSG.selectNone(), AON.CSS.aonIconCheck());
		uncheckAll.setVisible(!fBatch.isAccounted());
		uncheckAll.addClickHandler(e -> checkAllSelected(false));
		toolbar.add(uncheckAll);

		Label title = new Label("Vencimientos remesados");
		title.setStyleName(AON.CSS.aonToolbarSmallTitle());
		title.getElement().getStyle().setProperty("width", "100%");
		title.getElement().getStyle().setProperty("text-align", "center");
		toolbar.add(title);

		return toolbar;
	}

	private void checkAllSelected(boolean check) {
		for (FinanceRow financeRow : selectedFinances.values()) {
			financeRow.getFinance().setSelected(check);
			if (check)
				selectedFinancesSelected.add(financeRow.getFinance().getId());
			else
				selectedFinancesSelected.remove(financeRow.getFinance().getId());
		}

		for (int row = 1; row < selectedFinanceTable.getRowCount(); row++) {
			Widget w = selectedFinanceTable.getWidget(row, 1);
			if (check) {
				w.addStyleName(AON.CSS.aonIconChecked());
				w.removeStyleName(AON.CSS.aonIconCheck());
			} else {
				w.addStyleName(AON.CSS.aonIconCheck());
				w.removeStyleName(AON.CSS.aonIconChecked());
			}
		}

		selectedCount.setText(
				(selectedFinancesSelected.size() > 0) ? AonNumberUtils.toString(selectedFinancesSelected.size()) : "");
		addSelectedButton.setEnabled(selectedFinancesSelected.size() > 0);
	}

	protected FlexTable getSelectedFinanceTable() {
		selectedFinanceTable = new FlexTable();
		selectedFinanceTable.setStyleName(AON.CSS.aonGrid());

		selectedFinanceAutoWidth = selectedFinanceContainer.getOffsetWidth() - 36;
		for (SELECTED_COLS col : SELECTED_COLS.values()) {
			if (col != SELECTED_COLS.TIT) {
				selectedFinanceAutoWidth -= (col.getColWidth() + 2);
			}
		}

		selectedCount = new InlineLabel();
		for (SELECTED_COLS col : SELECTED_COLS.values()) {
			if (col == SELECTED_COLS.TIT) {
				selectedFinanceTable.getColumnFormatter().setWidth(col.ordinal(), selectedFinanceAutoWidth + "px");
			} else {
				selectedFinanceTable.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth() + "px");
			}
			selectedFinanceTable.setWidget(0, col.ordinal(),
					col == SELECTED_COLS.CHK ? selectedCount : new Label(col.getHeaderLabel()));
			selectedFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(), AON.CSS.aonGridHeader());
			if (col.getCellStyleClass() != null) {
				selectedFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(), col.getCellStyleClass());
				selectedFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(), AON.CSS.aonNowrap());
			}
		}
		return selectedFinanceTable;
	}

	private void paintSelectedFinanceTable(final FinanceModuleOptions opt) {
		if (this.fBatch.getBatchDetails() != null && !this.fBatch.getBatchDetails().isEmpty()) {
			this.fBatch.getBatchDetails().stream().map(batchDetail -> batchDetail.getFinance()).forEach(finance -> {
				paintSelectedRow(opt, finance);
			});
		}
	}

	private void paintSelectedRow(final FinanceModuleOptions opt, Finance finance) {
		int row = selectedFinanceTable.getRowCount();
		selectedFinances.put(finance.getId(), new FinanceRow(row, finance));
		paintSelectedRow(opt, finance, row);
	}

	private void paintSelectedRow(final FinanceModuleOptions opt, Finance finance, int row) {
		int col = 0;

		AonTableButton removeButton = new AonTableButton("Quitar de la remesa", AON.CSS.aonIconMoveLeft());
		removeButton.addClickHandler(e -> {
			Optional<FBatchDetail> fbatchDetail = this.fBatch.getBatchDetails().stream()
					.filter(fbatchDetial -> null != fbatchDetial.getFinance()
							&& fbatchDetial.getFinance().getId() == finance.getId())
					.findFirst();
			if (fbatchDetail.isPresent())
				fbatchDetail.get().setRemoved(true);

			save();

//			selectedFinances.remove(finance.getId());
//			selectedFinancesSelected.remove(finance.getId());
//			
//			selectedFinanceContainer.clear();
//			selectedFinanceToolbar = getSelectedToolbar();
//			selectedFinanceContainer.add(selectedFinanceToolbar);
//			selectedFinanceTable = getSelectedFinanceTable();
//			selectedFinanceContainer.add(selectedFinanceTable);
//			
//			selectedFinances.values().stream().map(financeRow -> financeRow.getFinance()).forEach(financeIt -> paintSelectedRow(opt, financeIt, selectedFinanceTable.getRowCount()));
//			
//			String amountSum = selectedFinances.size() == 0 ? "0.00 \u20ac" : AON.FMT.format(selectedFinances.values().stream().map(financeRow -> financeRow.getFinance().getAmount()).reduce(0.00, (a, b) -> a + b)) + " \u20ac";
//			totalAmount.setText(amountSum);
//			
//			if(selectedFinances.isEmpty()) {
//				Label label = new Label("No existen vencimientos en la remesa");
//				label.setStyleName(AON.CSS.aonBlockMessage());
//				label.addStyleName(AON.CSS.aonBlockInfoMessage());
//				label.addStyleName(AON.CSS.aonMarginTop());
//				selectedFinanceContainer.add(label);
//			}
//			
//			aviableFinances.clear();
//			aviableFinancesSelected.clear();
//			
//			enableMoreData();
//			aviableFinanceContainer.clear();
//			aviableFinanceToolbar = getAviableToolbar();
//			aviableFinanceContainer.add(aviableFinanceToolbar);
//			aviableFinanceTable = getAviableFinanceTable();
//			aviableFinanceContainer.add(aviableFinanceTable);
//			offset.setValue(0);
//			
//			enableSearch();
//			search(opt, offset.intValue());
//			
//			saveButton.setEnabled(true);
		});

		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(),
				selectedFinancesSelected.contains(finance.getId()) ? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
		checkButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectedFinancesSelected.contains(finance.getId())) {
					finance.setSelected(false);
					selectedFinancesSelected.remove(finance.getId());
					checkButton.addStyleName(AON.CSS.aonIconCheck());
					checkButton.removeStyleName(AON.CSS.aonIconChecked());
				} else {
					finance.setSelected(true);
					selectedFinancesSelected.add(finance.getId());
					checkButton.addStyleName(AON.CSS.aonIconChecked());
					checkButton.removeStyleName(AON.CSS.aonIconCheck());
				}

				selectedCount.setText(
						(selectedFinancesSelected.size() > 0) ? AonNumberUtils.toString(selectedFinancesSelected.size())
								: "");
				addSelectedButton.setEnabled(selectedFinancesSelected.size() > 0);
			}
		});

		Label issueDate = new Label(AON.DATE_FORMAT.format(finance.getDueDate()));

		Label document = new Label(finance.getRegistryDocument());

		Label titular = new Label(finance.getRegistryName());
		titular.setTitle(finance.getRegistryName());
		titular.setWidth(selectedFinanceAutoWidth + "px");
		titular.setStyleName(AON.CSS.aonTruncate());

		Label amount = new Label(AON.FMT.format(finance.getAmount()) + " \u20ac");
		amount.addStyleName(AON.CSS.aonTextRight());

		selectedFinanceTable.setWidget(row, col,
				finance.isPending() || finance.isBatched() ? removeButton : new Label());
		++col;
		selectedFinanceTable.setWidget(row, col,
				finance.isPending() || finance.isBatched() ? checkButton : new Label());
		++col;
		selectedFinanceTable.setWidget(row, col, issueDate);
		++col;
		selectedFinanceTable.setWidget(row, col, document);
		++col;
		selectedFinanceTable.setWidget(row, col, titular);
		++col;
		selectedFinanceTable.setWidget(row, col, amount);
	}

	// -------------------------------------------------------------------
	// --------------------- TOOLBAR --------------------------
	// -------------------------------------------------------------------

	private Widget getToolbarPanel(final FinanceModuleOptions opt) {
		toolbar = new AonToolbar("Remesa Transferencias N\u00f3minas");

		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);

		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden = new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		Hidden rattachHidden = new Hidden("rattach");
		Hidden attachTypeHidden = new Hidden("attachType");

		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(rattachHidden);
		formFlowPanel.add(attachTypeHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		toolbar.add(diskForm);

		backButton = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> back(hasSaved));
		toolbar.add(backButton);

		saveButton = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> save());
		saveButton.setEnabled(false);
		toolbar.add(saveButton);

		sepaButton = new AonToolbarButton("Crear fichero SEPA", AON.CSS.aonIconXml());
		sepaButton.addClickHandler(e -> createSepaFile());
		sepaButton.setVisible(!this.fBatch.getBatchDetails().isEmpty());
		toolbar.add(sepaButton);

		downloadButton = new AonToolbarButton(AON.MSG.download() + " fichero SEPA", AON.CSS.aonIconDownload());
		downloadButton.addClickHandler(e -> {
			diskForm.setAction(GWT.getHostPageBaseURL() + "/ms/download_attachment/");

			rattachHidden.setValue(fBatch.getRattach().toString());
			attachTypeHidden.setValue("registry");
			domainIdHidden.setValue(opt.getDomain() + "");
			domainNameHidden.setValue(opt.getDomainName());
			userHidden.setValue(opt.getUser());

			diskForm.submit();
		});
		downloadButton.setVisible(this.fBatch.getRattach() != null);
		toolbar.add(downloadButton);

		deleteFileButton = new AonToolbarButton(AON.MSG.deleteAction() + " fichero SEPA", AON.CSS.aonIconDeleteFile());
		deleteFileButton.addClickHandler(e -> deleteSepaFile());
		deleteFileButton.setVisible(this.fBatch.getRattach() != null);
		toolbar.add(deleteFileButton);

		return toolbar;
	}

	private void save() {
		AonMessagePanel.showLoading(messagePanel, "Guardando la remesa '" + fBatch.getDescription() + "'...");
		FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch,
				new AsyncCallback<FBatch>() {

					@Override
					public void onSuccess(FBatch savedFbatch) {
						fBatch = savedFbatch;
						hasSaved = true;
						initialize();

						AonMessagePanel.showSuccess(messagePanel, new HTMLPanel(
								"La remesa '<b>" + fBatch.getDescription() + "</b>' ha sido guarda correctamente."));
					}

					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al guardar la remesa '<b>"
								+ fBatch.getDescription() + "</b>': " + error.getMessage()));
					}
				});
	}

	private void createSepaFile() {
		AonMessagePanel.showLoading(messagePanel,
				"Generando fichero SEPA para la remesa '" + fBatch.getDescription() + "'...");
		FINANCE_SERVICE.createSepaFile(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch.getId(),
				new AsyncCallback<Integer>() {

					@Override
					public void onSuccess(Integer rattachId) {
						AonMessagePanel.showSuccess(messagePanel, new HTMLPanel("El fichero SEPA de la remesa '<b>"
								+ fBatch.getDescription() + "</b>' ha sido generado correctamente."));
						fBatch.setRattach(rattachId);
						fBatch.setStatus(FBatchStatus.GENERATED);
						sepaButton.setVisible(false);
						downloadButton.setVisible(true);
						deleteFileButton.setVisible(true);

						FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch,
								new AsyncCallback<FBatch>() {

									@Override
									public void onSuccess(FBatch result) {
										hasSaved = true;
									}

									@Override
									public void onFailure(Throwable error) {
										AonMessagePanel.showError(messagePanel,
												new HTMLPanel("Error al guardar la remesa '<b>"
														+ fBatch.getDescription() + "</b>': " + error.getMessage()));
									}
								});
					}

					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel,
								new HTMLPanel("Error al generar el fichero SEPA de la remesa '<b>"
										+ fBatch.getDescription() + "</b>': " + error.getMessage()));
					}
				});
	}

	private void deleteSepaFile() {
		AonMessagePanel.showLoading(messagePanel,
				"Eliminando fichero SEPA para la remesa '" + fBatch.getDescription() + "'...");
		FINANCE_SERVICE.deleteSepaFile(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch.getRattach(),
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void seccess) {
						AonMessagePanel.showSuccess(messagePanel, new HTMLPanel("El fichero SEPA de la remesa '<b>"
								+ fBatch.getDescription() + "</b>' ha sido eliminado correctamente."));
						fBatch.setRattach(null);
						fBatch.setStatus(FBatchStatus.PENDING);
						deleteFileButton.setVisible(false);
						downloadButton.setVisible(false);

						FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch,
								new AsyncCallback<FBatch>() {

									@Override
									public void onSuccess(FBatch savedFbatch) {
										hasSaved = true;
										sepaButton.setVisible(!savedFbatch.getBatchDetails().isEmpty());
									}

									@Override
									public void onFailure(Throwable error) {
										AonMessagePanel.showError(messagePanel,
												new HTMLPanel("Error al guardar la remesa '<b>"
														+ fBatch.getDescription() + "</b>': " + error.getMessage()));
									}
								});
					}

					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel,
								new HTMLPanel("Error al eliminar el fichero SEPA de la remesa '<b>"
										+ fBatch.getDescription() + "</b>': " + error.getMessage()));
					}
				});
	}

	public abstract void back(boolean refresh);

	// -------------------------------------------------------------------
	// --------------------- AUXILIAR METHODS --------------------------
	// -------------------------------------------------------------------

	private void setSelectedValueLB(ListBox lBox, String str) {
		String text = str;
		int indexToFind = 0;
		for (int i = 0; i < lBox.getItemCount(); i++) {
			if (lBox.getValue(i).equals(text)) {
				indexToFind = i;
				break;
			}
		}
		lBox.setSelectedIndex(indexToFind);
	}

	private void getEnterpriseBanks(final FinanceModuleOptions opt, Consumer<LinkedList<RegistryBank>> success) {
		COMMON_SERVICE.getCompanyBanks(opt.getDomainName(), opt.getDomain(), opt.getUser(),
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

}
