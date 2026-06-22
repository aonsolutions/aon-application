package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class AccountingAmortizationPanel extends AonLayoutPanel {
	private static final String AMORTIZATION_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/AmortizationReportExcelPrint";
	
	private final AonToolbar toolbar = new AonToolbar(AON.MSG.accountingAmortizationModule()); 
	private SimpleLayoutPanel tablePanel = new SimpleLayoutPanel();
	
	
	private FormPanel diskForm = new FormPanel("_blank");
	private Hidden amortizationIdHidden = new Hidden(IRequestParamsNames.ID);
	private Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
	private Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
	private Hidden userHidden = new Hidden(IRequestParamsNames.USER);
	
	public AccountingAmortizationPanel( AmortizationModuleOptions opts ) {
		super(Unit.PX);
		AON.ensureInjected();
		this.addStyleName(AON.CSS.aonSelector());
		
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		formFlowPanel.add(amortizationIdHidden);
		toolbar.add(diskForm);
		
		AccountingAmortizationFilterPanel filterPanel = new AccountingAmortizationFilterPanel(opts);
		
		AonToolbarButton excelButton = new AonToolbarButton(AON.MSG.printExcel(), AON.CSS.aonIconExcel());
		excelButton.addClickHandler(e -> excel(opts, filterPanel.getWidgetParams( opts )));
		
		this.addNorth(toolbar, AonToolbar.HEIGTH);
		
		this.addNorth(filterPanel, 100);
	
		// Table Panel
		tablePanel.setStyleName(AON.CSS.aonSelector());
		this.add(tablePanel);
		
		AmortizationParams params = getParams(opts);
		tablePanel.clear();
		AmortizationTable amortizationTable = new AmortizationTable(opts, params);
		tablePanel.setWidget(amortizationTable);
		
		
	}
	
	private AmortizationParams getParams(AmortizationModuleOptions opts) {
		return new AmortizationParams().setDomain(opts.getDomain());
	}
	
	private void excel(AmortizationModuleOptions opts , AmortizationParams params) {
//		diskForm.setAction(GWT.getHostPageBaseURL() + AMORTIZATION_REPORT_EXCEL_PRINT);
//		domainIdHidden.setValue( AonNumberUtils.toString(opts.getDomain()));
//		domainNameHidden.setValue(opts.getDomainName());
//		userHidden.setValue(opts.getUser());
//		amortizationIdHidden.setValue(AonNumberUtils.toString(amortization.getId()));
//		diskForm.submit();
	}
}
