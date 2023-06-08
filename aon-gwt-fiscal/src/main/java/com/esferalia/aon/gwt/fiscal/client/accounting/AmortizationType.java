package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AmortizationType extends MainEntryPoint {

	// Services
	private static AccountEntryServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

	// Options Config
	private RegistryModuleOptions options;

	// Content
	private DockLayoutPanel dockLayoutPanel;

	private AonToolbar toolbar;
	private AonToolbarButton createButton;
	private AonToolbarButton deleteFeeButton;
	
	// Filter
	
	private HTMLPanel filterDefaultPanel;
	private TextBox descriptionTextBox;
	private ListBox fixedAssetAccountListBox;
	private ListBox accumulatedAccountListBox;
	private ListBox allocationAccountListBox;
	
	// Fee Table
	private HTMLPanel container;
	private HTMLPanel messagePanel;
	private DeckPanel deckPanel;
	private ScrollPanel scrollPanel;
	private Grid amortizationTable;
	
	// Variables
	private Map<CheckBox, com.esferalia.aon.occam.api.model.accounting.AmortizationType> selectionModel = new HashMap<>();
	private LinkedList<com.esferalia.aon.occam.api.model.accounting.AmortizationType> amortizationTypeList = new LinkedList<>();

	// Search Variables
	private AmortizationTypeParams params;
	
	private List<Account> fixedAssetAccountList = new LinkedList<>();
	private List<Account> accumulatedAccountList = new LinkedList<>();
	private List<Account> allocationAccountList = new LinkedList<>();
	
	private int limit = 100;
	private MutableInt offset = new MutableInt(0);
	private MutableInt moreData = new MutableInt(0);
	private MutableInt searchEnabled = new MutableInt(0); 
	private int lastScrollPos = 0;

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		options = new RegistryModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad(options);
	}

	public void onModuleLoad(final RegistryModuleOptions opt) {
		AON.ensureInjected();

		AccountEntryServiceAsync accountEntryServiceAsync = GWT.create(AccountEntryService.class);
		SERVICE = new AccountEntryServiceAsyncDecorator(accountEntryServiceAsync);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		
		dockLayoutPanel.clear();

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
							dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
						}
					});
		} else {
			loadModule(opt);
		}
	}

	private void loadModule(final RegistryModuleOptions opt) {
		createToolbar();
		dockLayoutPanel.addNorth(toolbar, 50);

		container = new HTMLPanel("");
		container.clear();
		container.addStyleName(AON.CSS.aonFlexColumn());
		dockLayoutPanel.add(container);

		messagePanel = new HTMLPanel("");
		container.add(messagePanel);
		
		createFilterPanel(opt, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
			}

			@Override
			public void onSuccess(Void result) {
				deckPanel = new DeckPanel();
				deckPanel.getElement().getStyle().setProperty("margin", "0 1rem");
				container.add(deckPanel);
				
				scrollPanel = new ScrollPanel();
				scrollPanel.setHeight((Window.getClientHeight() - 230) + "px");
				scrollPanel.addScrollHandler(e -> {
					// ------------------------------------ Ignore scroll up.
					int oldScrollPos = lastScrollPos;
					lastScrollPos = scrollPanel.getVerticalScrollPosition();
					if (oldScrollPos >= lastScrollPos) {
						return;
					}
					// -----------------------------------------------------
					if (isSearchEnabled()) {
						int maxScrollTop = scrollPanel.getWidget().getOffsetHeight() - scrollPanel.getOffsetHeight();
						if (lastScrollPos >= maxScrollTop) {
							disableSearch();
							searchAmortizationType();
						}
					}
				});
				
				initializeDeckPanel();

				AonMessagePanel.showLoading(messagePanel, "Cargando tipos de amortizaci\u00f3n ...");
				resetFilter();
				onSearchAmortizationType();
				AonMessagePanel.hideMessage(messagePanel);
			}
			
		});
		
	}

	private void createFilterPanel(final RegistryModuleOptions opt, AsyncCallback<Void> endCallback) {
		HTMLPanel filterContentPanel = new HTMLPanel("");
		filterContentPanel.addStyleName(AON.CSS.aonFlexBetween());
		filterContentPanel.addStyleName(AON.CSS.aonFilterPanel());
		filterContentPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		HTMLPanel filterLeftPanel = new HTMLPanel("");
		filterLeftPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		filterDefaultPanel = new HTMLPanel("");
		filterDefaultPanel.addStyleName(AON.CSS.aonFlexWrap());
		
		// Description
		HTMLPanel descriptionItemPanel = new HTMLPanel("");
		descriptionItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label descriptionLabel = new Label("Descripci\u00f3n");
		descriptionLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		descriptionTextBox = new TextBox();
		setWidgetStyle(descriptionTextBox);
		descriptionTextBox.addValueChangeHandler(e -> onSearchAmortizationType());
		
		descriptionItemPanel.add(descriptionLabel);
		descriptionItemPanel.add(descriptionTextBox);
		
		filterDefaultPanel.add(descriptionItemPanel);
		
		createAccountsListBox(endCallback);
		
		// Right buttons
		HTMLPanel filterRightPanel = new HTMLPanel("");
		filterRightPanel.addStyleName(AON.CSS.aonFlexWrap());
		filterRightPanel.getElement().getStyle().setProperty("height", "100%");
		filterRightPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		filterRightPanel.getElement().getStyle().setProperty("flex-wrap", "nowrap");
		
		AonToolbarSmallButton resetBtn = new AonToolbarSmallButton("Borrar filtros", AON.CSS.aonIconClear());
		resetBtn.addClickHandler(e -> {
			resetFilter();
			amortizationTypeList.clear();
			resetAmortizationTypeTable();
			enableMoreData();
			offset.setValue(0);
			showInitialMessage();
			onSearchAmortizationType();
		});
		
		filterLeftPanel.add(filterDefaultPanel);
		
		filterRightPanel.add(resetBtn);
		
		filterContentPanel.add(filterLeftPanel);
		filterContentPanel.add(filterRightPanel);

		container.add(filterContentPanel);
	}
	
	private void createAccountsListBox(AsyncCallback<Void> endCallback) {
		SERVICE.getFixedAssetAccounts(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Account>>() {

			@Override
			public void onFailure(Throwable arg0) {
				// Nothing to do
			}

			@Override
			public void onSuccess(List<Account> fixedAssetAccountsDB) {
				fixedAssetAccountList = fixedAssetAccountsDB;
				
				// FixedAsset
				HTMLPanel fixedAssetItemPanel = new HTMLPanel("");
				fixedAssetItemPanel.addStyleName(AON.CSS.aonItemFlex());

				Label fixedAssetItemPanelLabel = new Label("C. Inmovilizado");
				fixedAssetItemPanelLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				
				fixedAssetAccountListBox = new ListBox();
				fixedAssetAccountListBox.setWidth("280px");
				setWidgetStyle(fixedAssetAccountListBox);
				fixedAssetAccountListBox.addItem("-", "");
				fixedAssetAccountList.forEach(account -> fixedAssetAccountListBox.addItem(account.getCode() + " - " + account.getDescription(), account.getCode()));
				fixedAssetAccountListBox.addChangeHandler(e -> onSearchAmortizationType());
				
				fixedAssetItemPanel.add(fixedAssetItemPanelLabel);
				fixedAssetItemPanel.add(fixedAssetAccountListBox);
				
				SERVICE.getAccumulatedAccounts(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Account>>() {

					@Override
					public void onFailure(Throwable arg0) {
						// Nothing to do
					}

					@Override
					public void onSuccess(List<Account> accumulatedAccountsDB) {
						accumulatedAccountList = accumulatedAccountsDB;
						
						// Accumulated
						HTMLPanel accumulatedItemPanel = new HTMLPanel("");
						accumulatedItemPanel.addStyleName(AON.CSS.aonItemFlex());

						Label accumulatedItemPanelLabel = new Label("C. Acumulada");
						accumulatedItemPanelLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
						
						accumulatedAccountListBox = new ListBox();
						accumulatedAccountListBox.setWidth("280px");
						setWidgetStyle(accumulatedAccountListBox);
						accumulatedAccountListBox.addItem("-", "");
						accumulatedAccountList.forEach(account -> accumulatedAccountListBox.addItem(account.getCode() + " - " + account.getDescription(), account.getCode()));
						accumulatedAccountListBox.addChangeHandler(e -> onSearchAmortizationType());
						
						accumulatedItemPanel.add(accumulatedItemPanelLabel);
						accumulatedItemPanel.add(accumulatedAccountListBox);
						
						SERVICE.getAllocationAccounts(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Account>>() {

							@Override
							public void onFailure(Throwable arg0) {
								// Nothing to do
							}

							@Override
							public void onSuccess(List<Account> allocationAccountsDB) {
								allocationAccountList = allocationAccountsDB;
								
								// Allocation
								HTMLPanel allocationItemPanel = new HTMLPanel("");
								allocationItemPanel.addStyleName(AON.CSS.aonItemFlex());

								Label allocationLabel = new Label("C. Dotaci\u00f3n");
								allocationLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
								
								allocationAccountListBox = new ListBox();
								allocationAccountListBox.setWidth("280px");
								setWidgetStyle(allocationAccountListBox);
								allocationAccountListBox.addItem("-", "");
								allocationAccountList.forEach(account -> allocationAccountListBox.addItem(account.getCode() + " - " + account.getDescription(), account.getCode()));
								allocationAccountListBox.addChangeHandler(e -> onSearchAmortizationType());
								
								allocationItemPanel.add(allocationLabel);
								allocationItemPanel.add(allocationAccountListBox);
								
								filterDefaultPanel.add(fixedAssetItemPanel);
								filterDefaultPanel.add(accumulatedItemPanel);
								filterDefaultPanel.add(allocationItemPanel);	
								
								endCallback.onSuccess(null);
							}
						
						});
					}
					
				});
			}
		
		});
		
	}
	
	private void setWidgetStyle(Widget widget) {
		widget.setHeight("2em");
		widget.getElement().getStyle().setProperty("padding", "0 5px");
	}
	
	// ------- RESET FILTER ---------

	private void resetFilter() {
		descriptionTextBox.setValue(null);
		
		fixedAssetAccountListBox.setSelectedIndex(0);
		accumulatedAccountListBox.setSelectedIndex(0);
		allocationAccountListBox.setSelectedIndex(0);
		
		if(null != params) {
			params.setDescription(null);
			
			params.setFixedAssetAccount(null);
			params.setAccumulatedAccount(null);
			params.setAllocationAccount(null);
		}
	}
	
	// ------- SEARCH ---------
	
	private void onSearchAmortizationType() {
		enableMoreData();
		offset.setValue(0);
		searchAmortizationType();
		amortizationTypeList.clear();
		resetAmortizationTypeTable();
	}

	private void searchAmortizationType() {
		if (!isMoreData()) return;
		
		createParams();
		
		SERVICE.getAmortizationTypeList(options.getDomainName(), options.getDomain(), options.getUser(), params,
			new AsyncCallback<List<com.esferalia.aon.occam.api.model.accounting.AmortizationType>>() {

				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Error cargando tipos de amortizaci\u00f3n: " + caught.getMessage());
				}

				@Override
				public void onSuccess(List<com.esferalia.aon.occam.api.model.accounting.AmortizationType> amortizationTypeListDB) {
					if(params.getOffset() == 0 && (amortizationTypeListDB == null || amortizationTypeListDB.isEmpty()))
						showEmptyFeeMessage();
					else if(amortizationTypeListDB == null || amortizationTypeListDB.isEmpty()) {
						disableMoreData();
						if(amortizationTypeList.isEmpty()) showEmptyFeeMessage();
					} else {
						if(offset.getValue() == 0) selectionModel.clear();
						showFeeTable();
						amortizationTypeListDB.forEach( amortizationType -> paintRow(amortizationType));
						amortizationTypeList.addAll(amortizationTypeListDB);
						enableMoreData();
						offset.setValue(offset.getValue() + amortizationTypeListDB.size());
					}
					
					enableSearch();
				}
			});
		
	}

	private void createParams() {
		if(null == params) params = new AmortizationTypeParams();
		params.setDomain(options.getDomain());
		
		params.setDescription(descriptionTextBox.getValue());
		
		params.setFixedAssetAccount(fixedAssetAccountListBox.getSelectedValue());
		params.setAccumulatedAccount(accumulatedAccountListBox.getSelectedValue());
		params.setAllocationAccount(allocationAccountListBox.getSelectedValue());
		
		params.setLimit(limit);
		params.setOffset(offset.getValue());
	}
	
	// ------- MAIN PAGE ---------

	private void initializeDeckPanel() {
		deckPanel.clear();
		createInitialMessage();
		createEmptyAmortizationTypeMessage();
		createAmortizationTypeTable();
		showInitialMessage();
	}
	
	private void showInitialMessage() {
		deckPanel.showWidget(0);
	}
	
	private void showEmptyFeeMessage() {
		deckPanel.showWidget(1);
	}
	
	private void showFeeTable() {
		deckPanel.showWidget(2);
	}	
	
	private void createInitialMessage() {
		HTMLPanel emptyPanel = new HTMLPanel("");
		emptyPanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		Label emptyMessage = new Label("RELLENE LA BUSQUEDA PARA CARGAR LOS TIPOS DE AMORTIZACION");
		emptyMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		emptyMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
		emptyPanel.add(emptyMessage);
		deckPanel.add(emptyPanel);
	}

	private void createEmptyAmortizationTypeMessage() {
		HTMLPanel emptyFeePanel = new HTMLPanel("");
		emptyFeePanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		Label emptyMessage = new Label("NO EXISTEN TIPOS DE AMORTIZACION PARA ESTE FILTRO");
		emptyMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		emptyMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
		emptyFeePanel.add(emptyMessage);
		deckPanel.add(emptyFeePanel);
	}
	
	private void createAmortizationTypeTable() {
		resetAmortizationTypeTable();
		deckPanel.add(scrollPanel);
	}
	
	private void resetAmortizationTypeTable() {
		scrollPanel.clear();
		createFeeHeader();
		setColumnWidth();
	}

	private void createFeeHeader() {
		amortizationTable = new Grid(0, 6);
		amortizationTable.clear();
		amortizationTable.setWidth("100%");

		int row = amortizationTable.insertRow(amortizationTable.getRowCount());

		CheckBox select = new CheckBox();
		select.addValueChangeHandler(e -> {
			selectionModel.forEach((checkBox, amortizationType) -> checkBox.setValue(e.getValue()));
			deleteFeeButton.setEnabled(e.getValue());
		});

		Label edit = new Label("");
		Label description = new Label("DESCRIPCI\u00f3N");
		Label accounts = new Label("CUENTAS");
		Label percentage = new Label("PORCENTAJE");
		Label years = new Label("A\u00f1OS");
		
		select.addStyleName(AON.CSS.aonHeaderTable());
		select.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		edit.addStyleName(AON.CSS.aonHeaderTable());
		description.addStyleName(AON.CSS.aonHeaderTable());
		accounts.addStyleName(AON.CSS.aonHeaderTable());
		percentage.addStyleName(AON.CSS.aonHeaderTable());
		years.addStyleName(AON.CSS.aonHeaderTable());
		
		amortizationTable.setWidget(row, 0, select);
		amortizationTable.setWidget(row, 1, edit);
		amortizationTable.setWidget(row, 2, description);
		amortizationTable.setWidget(row, 3, accounts);
		amortizationTable.setWidget(row, 4, percentage);
		amortizationTable.setWidget(row, 5, years);

		amortizationTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		amortizationTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		amortizationTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		amortizationTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		amortizationTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		amortizationTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		
		amortizationTable.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
		amortizationTable.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
		
		scrollPanel.add(amortizationTable);
	}
	
	private void setColumnWidth() {
		amortizationTable.getColumnFormatter().getElement(0).getStyle().setWidth(2, Unit.PCT);
		amortizationTable.getColumnFormatter().getElement(1).getStyle().setWidth(2, Unit.PCT);
		amortizationTable.getColumnFormatter().getElement(2).getStyle().setWidth(32, Unit.PCT);
		amortizationTable.getColumnFormatter().getElement(3).getStyle().setWidth(48, Unit.PCT);
		amortizationTable.getColumnFormatter().getElement(4).getStyle().setWidth(8, Unit.PCT);
		amortizationTable.getColumnFormatter().getElement(5).getStyle().setWidth(8, Unit.PCT);
	}
	
	private void disableMoreData() {
		moreData.setValue(-1);
	}
	
	private void enableMoreData() {
		moreData.setValue(0);
	}
	
	private boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	
	private void enableSearch() {
		searchEnabled.setValue(0);
	}
	
	private boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	
	private void disableSearch() {
		searchEnabled.setValue(-1);
	}

	private void paintRow(com.esferalia.aon.occam.api.model.accounting.AmortizationType amortizationType) {
		int row = amortizationTable.insertRow(amortizationTable.getRowCount());

		Widget select;
		if(amortizationType.getDomain().getId().equals(options.getConfiguration().getDomain().getId())) {
			select = new CheckBox();
			((CheckBox)select).addValueChangeHandler(e -> {
				Optional<CheckBox> checked = selectionModel.keySet().stream().filter(cb -> cb.getValue()).findAny();
				deleteFeeButton.setEnabled(checked.isPresent());
			});
		} else select = new Label();
		
		AonTableButton edit = new AonTableButton(
				amortizationType.getDomain().getId().equals(options.getConfiguration().getDomain().getId()) ? "Editar" : "Heredado del padre", 
				amortizationType.getDomain().getId().equals(options.getConfiguration().getDomain().getId()) ? AON.CSS.aonIconRight() : AON.CSS.aonIconConfidential()
		);
		
		if(amortizationType.getDomain().getId().equals(options.getConfiguration().getDomain().getId()))
			edit.addClickHandler(e ->
				new AmortizationTypeDialog(fixedAssetAccountList, accumulatedAccountList, allocationAccountList, amortizationType) {
					
					@Override
					protected void onAccept(com.esferalia.aon.occam.api.model.accounting.AmortizationType amortizationTypeResult) {
						saveAmortizationTypes(amortizationTypeResult);
					}
				}
			);
		
		Label descriptionLabel = new Label(amortizationType.getDescription());
		
		HTMLPanel accountsPanel = new HTMLPanel("");
		accountsPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		Label fixedAssetAccountLabel = new Label(getFixedAssetAccount(amortizationType.getFixedAssetAccount()));
		Label accumulatedAccountLabel = new Label(getAccumulatedAccount(amortizationType.getAccumulatedAccount()));
		Label allocationAccountLabel = new Label(getAllocationAccount(amortizationType.getAllocationAccount()));
		
		accountsPanel.add(fixedAssetAccountLabel);
		accountsPanel.add(accumulatedAccountLabel);
		accountsPanel.add(allocationAccountLabel);
		
		Label percentageLabel = new Label(null == amortizationType.getPercentage() ? "" : NumberFormat.getFormat("0.00").format(amortizationType.getPercentage()) + "%");
		Double years = null == amortizationType.getPercentage() ? 0.00 : 100 / amortizationType.getPercentage();
		Label yearsLabel = new Label(0.00 == years ? "" : years.intValue() + "");

		checkRowAndModify(amortizationTable, row, amortizationType, select);
		checkRowAndModify(amortizationTable, row, amortizationType, edit);
		checkRowAndModify(amortizationTable, row, amortizationType, descriptionLabel);
		checkRowAndModify(amortizationTable, row, amortizationType, accountsPanel);
		checkRowAndModify(amortizationTable, row, amortizationType, percentageLabel);
		checkRowAndModify(amortizationTable, row, amortizationType, yearsLabel);
		
		amortizationTable.setWidget(row, 0, select);
		amortizationTable.setWidget(row, 1, edit);
		amortizationTable.setWidget(row, 2, descriptionLabel);
		amortizationTable.setWidget(row, 3, accountsPanel);
		amortizationTable.setWidget(row, 4, percentageLabel);
		amortizationTable.setWidget(row, 5, yearsLabel);
		
		amortizationTable.getCellFormatter().getElement(row, 1).getStyle().setTextAlign(TextAlign.CENTER);
		amortizationTable.getCellFormatter().getElement(row, 2).getStyle().setPadding(0.5, Unit.EM);
		amortizationTable.getCellFormatter().getElement(row, 3).getStyle().setPadding(0.5, Unit.EM);
		amortizationTable.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
		amortizationTable.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
		
		if (row % 2 == 0) {
			amortizationTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonOddTableRow());
			amortizationTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonOddTableRow());
			amortizationTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonOddTableRow());
			amortizationTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonOddTableRow());
			amortizationTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonOddTableRow());
			amortizationTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonOddTableRow());
		}

		amortizationTable.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);

		if(amortizationType.getDomain().getId().equals(options.getConfiguration().getDomain().getId()))
			selectionModel.put((CheckBox)select, amortizationType);
	}

	private String getFixedAssetAccount(String fixedAssetAccountCode) {
		Optional<Account> fixedAssetAccount = fixedAssetAccountList.stream().filter(account -> AonStringUtils.equalsIgnoreCase(account.getCode(), fixedAssetAccountCode)).findFirst();
		return !fixedAssetAccount.isPresent() ? null : fixedAssetAccount.get().getCode() + " - " + fixedAssetAccount.get().getDescription();
	}

	private String getAccumulatedAccount(String accumulatedAccountCode) {
		Optional<Account> accumulatedAccount = accumulatedAccountList.stream().filter(account -> AonStringUtils.equalsIgnoreCase(account.getCode(), accumulatedAccountCode)).findFirst();
		return !accumulatedAccount.isPresent() ? null : accumulatedAccount.get().getCode() + " - " + accumulatedAccount.get().getDescription();
	}

	private String getAllocationAccount(String allocationAccountCode) {
		Optional<Account> allocationAccount = allocationAccountList.stream().filter(account -> AonStringUtils.equalsIgnoreCase(account.getCode(), allocationAccountCode)).findFirst();
		return !allocationAccount.isPresent() ? null : allocationAccount.get().getCode() + " - " + allocationAccount.get().getDescription();
	}

	private void checkRowAndModify(Grid grid, int row, com.esferalia.aon.occam.api.model.accounting.AmortizationType amortizationType, Widget widget) {
		if (row % 2 == 0)
			widget.addStyleName(AON.CSS.aonOddTableRow());

		if (amortizationType.isModify()) {
			widget.addStyleName(AON.CSS.aonModifyTableRow());

			grid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonModifyTableRow());
		} else {
			widget.removeStyleName(AON.CSS.aonModifyTableRow());

			grid.getCellFormatter().removeStyleName(row, 0, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 1, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 2, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 3, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 4, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 5, AON.CSS.aonModifyTableRow());
		}

	}

	// -------------------------------- TOOLBAR
	
	private void createToolbar() {
		toolbar = new AonToolbar("Tipos de Amortizaci\u00f3n");
		
		createButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
		createButton.addClickHandler(e -> {
			new AmortizationTypeDialog(fixedAssetAccountList, accumulatedAccountList, allocationAccountList) {
				
				@Override
				protected void onAccept(com.esferalia.aon.occam.api.model.accounting.AmortizationType amortizationType) {
					amortizationType.setDomain(options.getConfiguration().getDomain());
					saveAmortizationTypes(amortizationType);
				}
			};
		});
		
		deleteFeeButton = new AonToolbarButton("Eliminar", AON.CSS.aonIconDelete());
		deleteFeeButton.setEnabled(false);
		deleteFeeButton.addClickHandler(event -> {
			LinkedList<com.esferalia.aon.occam.api.model.accounting.AmortizationType> selectedAmortizationTypes = selectionModel.entrySet().stream().filter(e -> e.getKey().getValue()).map(e -> e.getValue()).collect(Collectors.toCollection(LinkedList::new));
			
			if(selectedAmortizationTypes.size() == amortizationTypeList.size()) {
				// Cambio masivo (todo seleccionado)
				offset.setValue(0);
				limit = Integer.MAX_VALUE;
				
				createParams();
				
				SERVICE.getAmortizationTypeList(options.getDomainName(), options.getDomain(), options.getUser(), params,
						new AsyncCallback<List<com.esferalia.aon.occam.api.model.accounting.AmortizationType>>() {
					
					@Override
					public void onSuccess(List<com.esferalia.aon.occam.api.model.accounting.AmortizationType> deleteAmortizationTypes) {
						AonDialog dialog = new AonDialog("Eliminaci\u00f3n Tipo Amortizaciones",
								new HTML("Se va a proceder a eliminar <b>" + deleteAmortizationTypes.size() + " tipo(s) de amortizaci\u00f3n</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
						
						dialog.confirm(new AonAcceptDialogCallback() {

							@Override
							public void onCancel() {
								// Nothing to do here
							}

							@Override
							public void onAccept() {
								deleteAmortizationTypes(deleteAmortizationTypes.stream().map(amortizationType -> amortizationType.getId()).collect(Collectors.toList()));
							}
							
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// Nothing to do here
					}
					
				});
			} else {
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Tipo Amortizaciones",
						new HTML("Se va a proceder a eliminar <b>" + selectedAmortizationTypes.size() + " tipo(s) de amortizaci\u00f3n</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f<br>Este proceso ser\u00e5 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						// Nothing to do here
					}

					@Override
					public void onAccept() {
						deleteAmortizationTypes(selectedAmortizationTypes.stream().map(amortizationType -> amortizationType.getId()).collect(Collectors.toList()));
					}
					
				});
			}
		});
		
		toolbar.add(createButton);
		toolbar.add(deleteFeeButton);
	}
	
	private void deleteAmortizationTypes(List<Integer> deleteIds) {
		AonMessagePanel.showLoading(messagePanel, "Elimando Tipos Amortizaciones ...");
		SERVICE.deleteAmortizationTypes(options.getDomainName(), options.getDomain(), options.getUser(), deleteIds,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error eliminando tipo(s) amortizaci\u00f3n: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						AonMessagePanel.showSuccess(messagePanel, "Se han eliminado " + deleteIds.size() + " tipo(s) amortizaci\u00f3n correctamente");
						selectionModel.clear();
						amortizationTypeList.clear();
						resetAmortizationTypeTable();
						enableMoreData();
						offset.setValue(0);
						limit = 100;
						searchAmortizationType();
					}
				});
	}
	
	private void saveAmortizationTypes(com.esferalia.aon.occam.api.model.accounting.AmortizationType amortizationType) {
		AonMessagePanel.showLoading(messagePanel, "Creando Tipo Amortizaci\u00f3n ...");
		SERVICE.saveAmortizationType(options.getDomainName(), options.getDomain(), options.getUser(), amortizationType,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error creando tipo amortizaci\u00f3n: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						AonMessagePanel.showSuccess(messagePanel, "Se ha creado el tipo de amortizaci\u00f3n correctamente");
						selectionModel.clear();
						amortizationTypeList.clear();
						resetAmortizationTypeTable();
						enableMoreData();
						offset.setValue(0);
						limit = 100;
						searchAmortizationType();
					}
				});
	}
	
}
