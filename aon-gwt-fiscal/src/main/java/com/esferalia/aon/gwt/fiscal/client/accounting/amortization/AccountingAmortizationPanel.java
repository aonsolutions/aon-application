package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class AccountingAmortizationPanel extends AonLayoutPanel {
	private static final String ACCOUNTING_AMORTIZATION_EXCEL = "/aon_gwt_fiscal/roms/AccountingAmortizationExcelServlet";
	
	private final AonToolbar toolbar = new AonToolbar(AON.MSG.accountingAmortizationModule()); 
	private SimpleLayoutPanel tablePanel = new SimpleLayoutPanel();
	private AccountingAmortizationFilterPanel filterPanel;
	
	private FormPanel diskForm = new FormPanel("_blank");
	private Hidden paramsHidden = new Hidden(IRequestParamsNames.AMORTIZATION_PARAMS);
	private Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
	private Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
	private Hidden userHidden = new Hidden(IRequestParamsNames.USER);
	
	public AccountingAmortizationPanel( AmortizationModuleOptions opts ) {
		super(Unit.PX);
		AON.ensureInjected();
		this.addStyleName(AON.CSS.aonSelector());
		
		IdSelectionHandler<AmortizationDetail> selectionHandler = new IdSelectionHandler<>();

		filterPanel = new AccountingAmortizationFilterPanel(opts);
		filterPanel.addValueChangeHandler(e -> search(opts, selectionHandler));

		AonToolbarButton refreshButton = new AonToolbarButton(AON.MSG.refresh(), AON.CSS.aonIconRefresh());
		refreshButton.addClickHandler(e -> search(opts, selectionHandler));
		toolbar.add(refreshButton);
		
		AonToolbarButton clearButton = new AonToolbarButton(AON.MSG.clean(), AON.CSS.aonIconClean());
		clearButton.addClickHandler(e -> filterPanel.clean(opts));
		toolbar.add(clearButton);
		
		AonToolbarButton excelButton = new AonToolbarButton(AON.MSG.printExcel(), AON.CSS.aonIconExcel());
		excelButton.addClickHandler(e -> excel(opts, filterPanel.getWidgetParams( opts )));
		toolbar.add(excelButton);

		toolbar.add(selectionHandler);
		
		AonToolbarButton recordButton = new AonToolbarButton(AON.MSG.record(), AON.CSS.aonIconAccountingRecord());
		recordButton.setVisible( false );
		recordButton.addClickHandler(e -> recordSelected(opts, selectionHandler, recordButton));
		toolbar.add(recordButton);

		AonToolbarButton unrecordButton = new AonToolbarButton(AON.MSG.unrecord(), AON.CSS.aonIconAccountingUnrecord());
		unrecordButton.setVisible( false );
		unrecordButton.addClickHandler(e -> unrecordSelected(opts, selectionHandler, unrecordButton));
		toolbar.add(unrecordButton);
		
		selectionHandler.addValueChangeHandler(e -> manageVisibility( selectionHandler, recordButton, unrecordButton) );

		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formFlowPanel.add(paramsHidden);
		toolbar.add(diskForm);
		
		this.addNorth(toolbar, AonToolbar.HEIGTH);
		
		this.addNorth(filterPanel, 100);
	
		// Table Panel
		tablePanel.setStyleName(AON.CSS.aonSelector());
		this.add(tablePanel);
		
		search(opts, selectionHandler);	
	}
	
	private void manageVisibility(IdSelectionHandler<AmortizationDetail> selectionHandler, AonToolbarButton recordButton, AonToolbarButton unrecordButton) {
		if (selectionHandler.isEmpty()) {
			recordButton.setVisible(false);
			unrecordButton.setVisible(false);
			return;
		}
		// Si todos tienes estado CONTABILIZADO, muestra el botón de descontabilizar
		unrecordButton.setVisible( selectionHandler.stream().allMatch( d -> d.isScored() ));
		// Si todos tienes estado PENDING, muestra el botón de contabilizar
		recordButton.setVisible( selectionHandler.stream().allMatch( d -> d.isPending() ));
	}

	private void recordSelected(AmortizationModuleOptions opts,IdSelectionHandler<AmortizationDetail> selectionHandler, AonToolbarButton recordButton) {
		recordButton.setEnabled(false);
		AonConfirmDialog.showConfirm(AON.MSG.record()
			,AON.MSG.confirmMultipleRecord( selectionHandler.selectedCount() )
			,new AonConfirmDialogCallback() {
				@Override
				public void onCancel() {
					recordButton.setEnabled(true);			
				}
				
				@Override
				public void onAccept() {
					AmortizationModule.SERVICE.recordAmortizationDetails(opts.getOccam()
						,opts.getDomain()
						,selectionHandler.array()
						,new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void arg0) {
								recordButton.setEnabled(true);
								selectionHandler.clean();
								search(opts, selectionHandler);
							};
							
							@Override
							public void onFailure(Throwable e) {
								AonMessageToast.showError(e.getMessage());
								recordButton.setEnabled(true);
							}
					});
				}
		});
	}
	
	private void unrecordSelected(AmortizationModuleOptions opts,IdSelectionHandler<AmortizationDetail> selectionHandler, AonToolbarButton unrecordButton) {
		unrecordButton.setEnabled(false);
		AonConfirmDialog.showConfirm(AON.MSG.unrecord()
			,AON.MSG.confirmMultipleUnrecord( selectionHandler.selectedCount() )
			,new AonConfirmDialogCallback() {
				@Override
				public void onCancel() {
					unrecordButton.setEnabled(true);			
				}
				@Override
				public void onAccept() {
					AmortizationModule.SERVICE.unrecordAmortizationDetails(opts.getOccam()
						,opts.getDomain()
						,selectionHandler.array()
						,new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void arg0) {
								unrecordButton.setEnabled(true);
								selectionHandler.clean();
								search(opts, selectionHandler);
							};
							
							@Override
							public void onFailure(Throwable e) {
								AonMessageToast.showError(e.getMessage());
								unrecordButton.setEnabled(true);
							}
					});
				}
		});
	}

	private void search(AmortizationModuleOptions opts, IdSelectionHandler<AmortizationDetail> selectionHandler) {
		tablePanel.clear();
		selectionHandler.clean();
		AccountingAmortizationDetailTable amortizationTable = new AccountingAmortizationDetailTable(opts, getParams(opts));
		amortizationTable.addCheckedHandler( event -> selectionHandler.select( event.getValue().getDetail() ) );
		amortizationTable.addUncheckedHandler( event -> selectionHandler.unselect( event.getValue().getDetail() ) );
		tablePanel.setWidget(amortizationTable);
	}
	
	private AmortizationParams getParams(AmortizationModuleOptions opts) {
		return filterPanel.getWidgetParams(opts);
	}
	
	private void excel(AmortizationModuleOptions opts , AmortizationParams params) {
		diskForm.setAction(GWT.getHostPageBaseURL() + ACCOUNTING_AMORTIZATION_EXCEL);
		domainIdHidden.setValue( AonNumberUtils.toString(opts.getDomain()));
		domainNameHidden.setValue(opts.getDomainName());
		userHidden.setValue(opts.getUser());
		params.setOffset(0);
		params.setLimit(Integer.MAX_VALUE);
		paramsHidden.setValue(JsonParams.convert( params ));
		diskForm.submit();
	}
}
