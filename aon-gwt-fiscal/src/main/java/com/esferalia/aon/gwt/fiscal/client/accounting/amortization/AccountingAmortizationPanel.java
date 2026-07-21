package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class AccountingAmortizationPanel extends AonLayoutPanel {
	private static final String AMORTIZATION_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/AmortizationReportExcelPrint";
	
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
		
		filterPanel = new AccountingAmortizationFilterPanel(opts);
		filterPanel.addValueChangeHandler(e -> search(opts));

		AonToolbarButton refreshButton = new AonToolbarButton(AON.MSG.refresh(), AON.CSS.aonIconRefresh());
		refreshButton.addClickHandler(e -> search(opts));
		toolbar.add(refreshButton);
		
		AonToolbarButton clearButton = new AonToolbarButton(AON.MSG.clean(), AON.CSS.aonIconClean());
		clearButton.addClickHandler(e -> filterPanel.clean(opts));
		toolbar.add(clearButton);
		
		AonToolbarButton excelButton = new AonToolbarButton(AON.MSG.printExcel(), AON.CSS.aonIconExcel());
		excelButton.addClickHandler(e -> excel(opts, filterPanel.getWidgetParams( opts )));
		toolbar.add(excelButton);

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
		
		search(opts);	
	}
	
	private void search(AmortizationModuleOptions opts) {
		tablePanel.clear();
		AccountingAmortizationDetailTable amortizationTable = new AccountingAmortizationDetailTable(opts, getParams(opts));
		tablePanel.setWidget(amortizationTable);
	}
	
	private AmortizationParams getParams(AmortizationModuleOptions opts) {
		return filterPanel.getWidgetParams(opts);
	}
	
	private void excel(AmortizationModuleOptions opts , AmortizationParams params) {
		diskForm.setAction(GWT.getHostPageBaseURL() + AMORTIZATION_REPORT_EXCEL_PRINT);
		domainIdHidden.setValue( AonNumberUtils.toString(opts.getDomain()));
		domainNameHidden.setValue(opts.getDomainName());
		userHidden.setValue(opts.getUser());
		params.setOffset(0);
		params.setLimit(Integer.MAX_VALUE);
		paramsHidden.setValue(JsonParams.convert( params ));
		diskForm.submit();
	}
}
