package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomWidget;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvestAssetBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams.AmortizationParamsOrderBy;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AmortizationPanel extends AonCustomDockLayout {
	private static final int FORM_IDX = 1;
	private static final String AMORTIZATION_EXCEL = "/aon_gwt_fiscal/roms/AmortizationExcelServlet";
	
	static interface AmortizationPanelCallback {
		Amortization getAmortization();
		void showError(String message);
		void showInfo(String message);
		void showSuccess(String message);
		AonToolbarButton paintSaveButton( );
		AonToolbarButton paintDeleteButton( );
		AonToolbarButton paintCalculateButton( );
		AonToolbarButton paintSaleButton( );
		AonToolbarButton paintExcelButton( );
		void refresh();
		Integer getSelectedTab();
		void setSelectedTab( Integer index);
	}
	
	private DeckLayoutPanel deckPanel = new DeckLayoutPanel();
	private SimpleLayoutPanel tablePanel = new SimpleLayoutPanel();
	private SimpleLayoutPanel formPanel = new SimpleLayoutPanel();
	
	private final AonToolbarButton resetButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
	private final AonToolbarButton backButton = new AonToolbarButton(AON.MSG.backToListAction(), AON.CSS.aonIconBack());

	private AonToolbarButton saveButton;
	private AonToolbarButton deleteButton;
	private AonToolbarButton calculateButton;
	private AonToolbarButton saleButton;
	private AonToolbarButton excelButton;
	private AonToolbarButton excelListButton;
	
	private FormPanel diskForm = new FormPanel("_blank");
	private Hidden amortizationParamsHidden = new Hidden(IRequestParamsNames.AMORTIZATION_PARAMS);
	private Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
	private Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
	private Hidden userHidden = new Hidden(IRequestParamsNames.USER);
	
	private Integer selectedTab;

	// Filter widgets
	private AonCustomNumberBox amountBox = new AonCustomNumberBox(AON.MSG.amount());
	private AonCustomTextBox descriptionBox = new AonCustomTextBox(AON.MSG.description() );
	private AonCustomDateBox fromInitialDateBox = new AonCustomDateBox(AON.MSG.initiationDate());
	private AonCustomDateBox toInitialDateBox = new AonCustomDateBox(AON.MSG.to());
	private AonCustomDateBox fromDeadlineBox = new AonCustomDateBox(AON.MSG.saleDate());
	private AonCustomDateBox toDeadlineBox = new AonCustomDateBox(AON.MSG.to());
	private AonCustomListBox deadlineFilledBox = new AonCustomListBox(AON.MSG.status());
	private AonCustomListBox confidentialBox = new AonCustomListBox(AON.MSG.confidential());
	private AonAccountBox allocationBox;
	private AonAccountBox accumulatedBox;
	private AonAccountBox fixedAssetBox;
	private AonInvestAssetBox investAssetBox;
	private AonCustomListBox orderByBox = new AonCustomListBox(AON.MSG.orderBy());

	public AmortizationPanel( AmortizationModuleOptions opts ) {
		super(AON.MSG.amortizationModule());
		
		AON.ensureInjected();
		
		backButton.addClickHandler(e -> showTable(opts));
		this.addToolbarButton(backButton);
		
		resetButton.addClickHandler(e -> reset(opts));
		this.addToolbarButton(resetButton);
		
		excelListButton = new AonToolbarButton(AON.MSG.printExcel(), AON.CSS.aonIconExcel());
		excelListButton.addClickHandler(e -> excel(opts));
		this.addToolbarButton(excelListButton);

		
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formFlowPanel.add(amortizationParamsHidden);
		this.getToolbar().add(diskForm);
		
		createFilter(opts);
		
		// this.getSearchTextBox().addValueChangeHandler(e -> showTable(opts) );
		
		deckPanel.setStyleName(AON.CSS.aonSelector());
		this.add(deckPanel);

		// Table Panel
		tablePanel.setStyleName(AON.CSS.aonSelector());
		deckPanel.add(tablePanel);
		
		// Form Panel
		formPanel.setStyleName(AON.CSS.aonSelector());
		deckPanel.add(formPanel);

		showTable( opts );
		
	}
	
	private void reset(AmortizationModuleOptions opts) {
		Amortization newAmortization = new Amortization()
			.setDomain(opts.getDomain());
		showForm(opts, newAmortization);
	}

	private void delete(AmortizationModuleOptions opts, Amortization amortization) {
		AonConfirmDialog.showConfirm(AON.MSG.confirmDeleteAction(), () -> 
			AmortizationModule.SERVICE.delete( opts.getOccam(), amortization, new AsyncCallback<Void>() {
	
				@Override
				public void onFailure(Throwable ex) {
					AonMessageToast.showError(ex.getMessage());
				}
	
				@Override
				public void onSuccess(Void v) {
					AonMessageToast.showSuccess(AON.MSG.saveSuccess());
					showTable(opts);
				}
			})
		);
	}

	private void save(AmortizationModuleOptions opts , Amortization amortization) {
		AonConfirmDialog.showConfirm(AON.MSG.confirmSaveAction(), () ->
			AmortizationModule.SERVICE.save( opts.getOccam(), amortization, new AsyncCallback<Amortization>() {
	
				@Override
				public void onFailure(Throwable ex) {
					AonMessageToast.showError(ex.getMessage());
				}
	
				@Override
				public void onSuccess(Amortization saved) {
					AonMessageToast.showSuccess(AON.MSG.saveSuccess());
					showForm(opts, saved);
				}
				
			})
		);	
	}

	private void sale(AmortizationModuleOptions opts , Amortization amortization) {
		AonCustomPopup saleDialog = new AonCustomPopup( true );
		saleDialog.setHeight("300px");
		saleDialog.setWidth("500px");
		saleDialog.addStyleName(AON.CSS.aonPaddingTop());
		saleDialog.setCaption(AON.MSG.saleAmortizaton());
		saleDialog.setAnimationEnabled(true);
		saleDialog.setGlassEnabled(true);
		saleDialog.setModal(true);
		
		FlowPanel content = new FlowPanel();
		content.setStyleName(AON.CSS.aonWidthAll());
		saleDialog.add(content);
		
		FlowPanel errorContent = new FlowPanel();
		errorContent.setStyleName(AON.CSS.aonPadding());
		content.add(errorContent);
		
		AonDisplayTable saleTab = new AonDisplayTable();
		saleTab.addStyleName(AON.CSS.aonMarginTop());
		saleTab.addStyleName(AON.CSS.aonBlockCenter());

		AonDateBox saleDateBox = new AonDateBox();
		saleDateBox.setValue(amortization.getDeadline() );
		saleDateBox.addValueChangeHandler(e -> amortization.setDeadline( saleDateBox.getValue()));
		
		AonDoubleBox saleAmountBox = new AonDoubleBox();
		saleAmountBox.setValue(amortization.getSaleAmount() );
		saleAmountBox.addValueChangeHandler(e -> amortization.setSaleAmount(saleAmountBox.getValue()));
		
		saleTab
			.addLabelWidgetRow( AON.MSG.saleDate(), saleDateBox )
			.addLabelWidgetRow( AON.MSG.saleAmount(), saleAmountBox );
		content.add(saleTab);
		
		AonDisplayTable buttonsPanel = new AonDisplayTable();
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonBlockCenter());
		
		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText( AON.MSG.accept());
		acceptButton.addClickHandler(event -> {
			errorContent.clear();
			if (amortization.getDeadline() == null  || amortization.getSaleAmount() == null) {
				if (amortization.getDeadline() == null) {
					Label errorMsg = new Label( AON.MSG.requiredField(AON.MSG.saleDate()));
					errorMsg.setStyleName(AON.CSS.aonToolbarMessage());
					errorMsg.addStyleName(AON.CSS.aonToolbarErrorMessage());
					errorMsg.addStyleName(AON.CSS.aonWidthAll());
					errorMsg.addStyleName(AON.CSS.aonMarginTop());
					errorContent.add(errorMsg);
				}
				if (amortization.getSaleAmount() == null) {
					Label errorMsg = new Label( AON.MSG.requiredField(AON.MSG.saleAmount()));
					errorMsg.setStyleName(AON.CSS.aonToolbarMessage());
					errorMsg.addStyleName(AON.CSS.aonToolbarErrorMessage());
					errorMsg.addStyleName(AON.CSS.aonWidthAll());
					errorMsg.addStyleName(AON.CSS.aonMarginTop());
					errorContent.add(errorMsg);
				}
				return;
			}
			AmortizationModule.SERVICE.sale( opts.getOccam(), amortization, new AsyncCallback<Amortization>() {

				@Override
				public void onFailure(Throwable ex) {
					AonMessageToast.showError(ex.getMessage());
					saleDialog.hide();
				}

				@Override
				public void onSuccess(Amortization saved) {
					AonMessageToast.showSuccess(AON.MSG.saveSuccess());
					saleDialog.hide();
					showForm(opts, saved);
				}
				
			});
		});
		buttonsPanel.add(acceptButton);
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> saleDialog.hide() );
		buttonsPanel.add(cancelButton);
		
		content.add(buttonsPanel);
		
		saleDialog.center();
		saleDialog.show();
	}
	
	private void calculate(AmortizationModuleOptions opts , Amortization amortization) {
		AonConfirmDialog.showConfirm(AON.MSG.confirmAmortizationCalculateAction(), () -> 
			AmortizationModule.SERVICE.calculate( opts.getOccam(), amortization, new AsyncCallback<Amortization>() {
	
				@Override
				public void onFailure(Throwable ex) {
					AonMessageToast.showError(ex.getMessage());
				}
	
				@Override
				public void onSuccess(Amortization saved) {
					AonMessageToast.showSuccess(AON.MSG.saveSuccess());
					showForm(opts, saved);
				}
				
			})
		);
	}

	private AmortizationParams getParams(AmortizationModuleOptions opts) {
		String desc = AonStringUtils.trimToNull(descriptionBox.getValue());
		if (desc == null) {
			desc = AonStringUtils.trimToNull(getSearchTextBox().getValue());
		}
		return new AmortizationParams()
			.setDomain(opts.getDomain())
			.setDescription( desc )
			.setAmount( AonNumberUtils.nullIfZero( amountBox.getValue() ))
			.setFromInitialDate( fromInitialDateBox.getValue() )
			.setToInitialDate( toInitialDateBox.getValue() )
			.setFromDeadline( fromDeadlineBox.getValue() )
			.setToDeadline( toDeadlineBox.getValue() )
			.setFixedAssetAccount(fixedAssetBox.getId())
			.setAllocationAccount(allocationBox.getId())
			.setAccumulatedAccount(accumulatedBox.getId())
			.setInvestAsset(investAssetBox.getInvestAsset().map(ia -> ia.getId()).orElse(null))
			.setSecurityLevel( SecurityLevel.safeValueOf( AonNumberUtils.toInteger(confidentialBox.getValue()) ))
			.setDeadlineFilled(  AonEnumUtils.safeBoolean(deadlineFilledBox.getSelectedIndex()) )
			.setOrderBy( AmortizationParamsOrderBy.values()[orderByBox.getSelectedIndex()])
		;
		
	}

	private void manageButtons() {
		boolean show = deckPanel.getVisibleWidgetIndex() == FORM_IDX;
		backButton.setVisible(show);
	}

	private void show(Widget widget) {
		deckPanel.showWidget(widget);
		manageButtons();
	}
	
	private void showTable(AmortizationModuleOptions opts) {
		if (saveButton != null) saveButton.removeFromParent();
		if (deleteButton != null) deleteButton.removeFromParent();
		if (calculateButton != null) calculateButton.removeFromParent();
		if (saleButton != null) saleButton.removeFromParent();
		if (excelButton != null) excelButton.removeFromParent();
		excelListButton.setVisible(true);
		
		tablePanel.clear();
		AmortizationTable amortizationTable = new AmortizationTable(opts, getParams(opts));
		amortizationTable.addSelectionHandler(e -> {
			selectedTab = null;
			searchAndShowForm(opts, e.getSelectedItem());
		});
		tablePanel.setWidget(amortizationTable);
		show(tablePanel);
	}
	
	private void searchAndShowForm(AmortizationModuleOptions opts, Amortization amortization) {
		Integer id = amortization == null ? null : amortization.getId();
		AmortizationModule.SERVICE.get( opts.getOccam(), opts.getDomain(), id, new AsyncCallback<Amortization>() {

			@Override
			public void onFailure(Throwable ex) {
				AonMessageToast.showError(ex.getMessage());
			}

			@Override
			public void onSuccess(Amortization am) {
				showForm(opts, am);
			}
		});
	}
	
	private void showForm(AmortizationModuleOptions opts, Amortization amortization) {
		formPanel.clear();
		AmortizationPanelCallback callback = new AmortizationPanelCallback() {
			 
			@Override
			public Integer getSelectedTab() {
				return selectedTab;
			}
			
			@Override
			public void setSelectedTab(Integer index) {
				selectedTab = index;
			}
			
			@Override
			public void showError(String message) {
				AonMessageToast.showError(message);
			}

			@Override
			public void showInfo(String message) {
				AonMessageToast.showInfo(message);
			}
			
			@Override
			public void showSuccess(String message) {
				AonMessageToast.showSuccess(message);
			}

			@Override
			public Amortization getAmortization() {
				return amortization;
			}

			@Override
			public AonToolbarButton paintSaveButton() {
				if (saveButton != null) saveButton.removeFromParent();
				saveButton = new AonToolbarButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
				saveButton.addClickHandler(e -> save( opts , amortization));
				addToolbarButton(saveButton);
				return saveButton;
			}

			@Override
			public AonToolbarButton paintDeleteButton() {
				if (deleteButton != null) deleteButton.removeFromParent();
				deleteButton = new AonToolbarButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
				deleteButton.addClickHandler(e -> delete( opts , amortization));
				addToolbarButton(deleteButton);
				return deleteButton;
			}
			
			@Override
			public AonToolbarButton paintCalculateButton() {
				if (calculateButton != null) calculateButton.removeFromParent();
				calculateButton = new AonToolbarButton(AON.MSG.calculateAction(), AON.CSS.aonIconCalc());
				calculateButton.addClickHandler(e -> calculate( opts , amortization));
				addToolbarButton(calculateButton);
				return calculateButton;
			}
			
			@Override
			public AonToolbarButton paintSaleButton() {
				if (saleButton != null) saleButton.removeFromParent();
				saleButton = new AonToolbarButton(AON.MSG.saleAmortizaton(), AON.CSS.aonIconEuro());
				saleButton.addClickHandler(e -> sale( opts , amortization));
				addToolbarButton(saleButton);
				return saleButton;
			}
			
			@Override
			public AonToolbarButton paintExcelButton() {
				excelListButton.setVisible(false);
				if (excelButton != null) excelButton.removeFromParent();
				excelButton = new AonToolbarButton(AON.MSG.printExcel(), AON.CSS.aonIconExcel());
				excelButton.addClickHandler(e -> excel( opts , amortization));
				addToolbarButton(excelButton);
				return excelButton;
			}
			
			@Override
			public void refresh() {
				searchAndShowForm(opts, amortization);
			}
			
		};
		AmortizationFormPanel amortizationFormPanel = new AmortizationFormPanel(opts, callback);
		formPanel.setWidget(amortizationFormPanel);
		show(formPanel);
	}
	
	private void excel(AmortizationModuleOptions opts) {
		AmortizationParams params = getParams(opts)
			.setOffset(0)
			.setLimit(Integer.MAX_VALUE);
		excel( opts , params );
	}
	
	private void excel(AmortizationModuleOptions opts , Amortization amortization) {
		excel( opts , new AmortizationParams()
			.setDomain(opts.getDomain())
			.setId(amortization.getId()));
	}
	
	private void excel(AmortizationModuleOptions opts , AmortizationParams params) {
		diskForm.setAction(GWT.getHostPageBaseURL() + AMORTIZATION_EXCEL);
		domainIdHidden.setValue( AonNumberUtils.toString(opts.getDomain()));
		domainNameHidden.setValue(opts.getDomainName());
		userHidden.setValue(opts.getUser());
		amortizationParamsHidden.setValue(JsonParams.convert(params));
		diskForm.submit();
	}
	
	
	protected void clearFilter( AmortizationModuleOptions opts ) {
		initialize(opts);
		showTable( opts );
	}
	
	
	private void createFilter(AmortizationModuleOptions opts) {
		this.setSearchPlaceholder(AON.MSG.filterByDescription());
		
		getSearchTextBox().addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if ((AonStringUtils.isBlank(value))
			 || (AonStringUtils.isNotBlank(value) && value.length() > 3)) {
				showTable( opts );
			}
		});
		
		fixedAssetBox = new AonAccountBox( opts.getOccam(), false);
		fixedAssetBox.setRequired(false);
		allocationBox = new AonAccountBox( opts.getOccam(), false);
		allocationBox.setRequired(false);
		accumulatedBox = new AonAccountBox( opts.getOccam(), false);
		accumulatedBox.setRequired(false);
		investAssetBox = new AonInvestAssetBox( opts, AON.MSG.investAsset());
		
		confidentialBox.addItem( "NO confidenciales", "0" );
		confidentialBox.addItem( "Confidenciales", "1" );
		confidentialBox.addItem( "Todos", "2");
		
		deadlineFilledBox.addItem( "Fichas activas", "0" );
		deadlineFilledBox.addItem( "Fichas dadas de baja", "1" );
		deadlineFilledBox.addItem( "Todas", "2");
		
		AonCollectionUtils.stream(AmortizationParamsOrderBy.values())
			.forEach( e -> orderByBox.addItem( e.getDescription()));
				

		amountBox.addValueChangeHandler(e -> showTable(opts));	
		descriptionBox.addValueChangeHandler(e -> showTable(opts));
		fromInitialDateBox.addValueChangeHandler(e -> showTable(opts));
		toInitialDateBox.addValueChangeHandler(e -> showTable(opts));
		fromDeadlineBox.addValueChangeHandler(e -> showTable(opts));
		toDeadlineBox.addValueChangeHandler(e -> showTable(opts));
		allocationBox.addSelectionHandler(e -> showTable(opts));
		accumulatedBox.addSelectionHandler(e -> showTable(opts));
		fixedAssetBox.addSelectionHandler(e -> showTable(opts));
		investAssetBox.addSelectionHandler(e -> showTable(opts));
		confidentialBox.addChangeHandler(e -> showTable(opts));
		deadlineFilledBox.addChangeHandler(e -> showTable(opts));
		orderByBox.addChangeHandler(e -> showTable(opts));
		
		this.addFilterWidget(descriptionBox);
		this.addFilterWidget(amountBox);
		
		HTMLPanel initialDatePanel = new HTMLPanel("");
		initialDatePanel.setStyleName(AON.CSS.aonItemFlex());
		initialDatePanel.add(fromInitialDateBox);
		initialDatePanel.add(toInitialDateBox);
		this.addFilterWidget(initialDatePanel);
		
		HTMLPanel deadlinePanel = new HTMLPanel("");
		deadlinePanel.setStyleName(AON.CSS.aonItemFlex());
		deadlinePanel.add(fromDeadlineBox);
		deadlinePanel.add(toDeadlineBox);
		this.addFilterWidget(deadlinePanel);

		this.addFilterWidget(deadlineFilledBox);
		
		this.addFilterWidget(new AonCustomWidget<AonAccountBox>(AON.MSG.fixedAssetAccount(), fixedAssetBox));
		this.addFilterWidget(new AonCustomWidget<AonAccountBox>(AON.MSG.allocationAccount(), allocationBox));
		this.addFilterWidget(new AonCustomWidget<AonAccountBox>(AON.MSG.accumulatedAccount(), accumulatedBox));
		this.addFilterWidget(investAssetBox);
		
		if (opts.getConfiguration() != null 
			&& opts.getConfiguration().getUser() != null 
			&& opts.getConfiguration().getUser().hasConfidentialityRole()) {
			this.addFilterWidget(confidentialBox);
		}
		this.addFilterWidget(orderByBox);
		initialize(opts);
	}
		
	private void initialize(AmortizationModuleOptions opts) {
		getSearchTextBox().setValue(null, false);
		amountBox.setValue(null, false);
		fromInitialDateBox.setValue(null, false);
		toInitialDateBox.setValue(null, false);
		fromDeadlineBox.setValue(null, false);
		toDeadlineBox.setValue(null, false);
		deadlineFilledBox.setValue("2");
		confidentialBox.setValue("2");
		descriptionBox.setValue(null, false);
		allocationBox.setValue(null, false);
		accumulatedBox.setValue(null, false);
		fixedAssetBox.setValue(null, false);
		investAssetBox.setInvestAsset(null, false);
		orderByBox.setSelectedIndex(0);
	}
}
