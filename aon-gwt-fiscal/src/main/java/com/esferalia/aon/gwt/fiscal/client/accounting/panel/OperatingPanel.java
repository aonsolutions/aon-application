package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingParams;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;


public class OperatingPanel extends ScrollPanel implements HasSelectionHandlers<Account>{

	private static FiscalServiceAsync fiscalService;
	
	private FlowPanel root;
	
	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	private AccountOperatingParams params;
	
	public OperatingPanel(String currentDomainName, String currentUser, Integer currentDomainId, AccountOperatingParams params) {
		this.currentDomainName = currentDomainName;
		this.currentUser = currentUser;
		this.currentDomainId = currentDomainId;
		this.params = params;

		root = new FlowPanel();
		setWidget(root);
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());		
		
		search();
		scrollToTop();
	}

	private void search() {
		root.clear();
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		fiscalService.getAccountOperatingReport(currentDomainName,currentUser,currentDomainId,params
				,  new AsyncCallback<AccountOperatingReport>() {
			
			@Override
			public void onSuccess(final AccountOperatingReport report) {
				FlexTable tab = new FlexTable();
				tab.addStyleName(AON.AON_CSS.aonReportTable());

				tab.getColumnFormatter().setWidth( 0, "auto");
				
				if (report.isEmpty()) {
					tab.setWidget(0, 0, new Label(AON.MSG.noData()));
				} else {
					tab.setWidget(1, 0, new Label(AON.MSG.account()));
					tab.getCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonReportTableHeader());
	
					int columns = 2;
					if (report.showRatios() ) {
						columns = 5;	
					}
					if (report.showIncreasePercent() ) {
						columns = 3;
					}
	
					int iter = 1;
					for (DateInterval inter : report.getIntervals()) {
						tab.setWidget(0, iter, new Label(inter.getName()));
						tab.getFlexCellFormatter().setColSpan(0, iter, columns);
						tab.getCellFormatter().setStyleName(0, iter, AON.AON_CSS.aonReportTableHeader());
						tab.getCellFormatter().addStyleName(0, iter, AON.AON_CSS.aonTextCenter());
						int col = (iter * columns) - (columns -1);
	
						tab.setWidget(1, col  , new Label("S. Deudor"));
						tab.getColumnFormatter().setWidth( col,   "90px");
						tab.getCellFormatter().setStyleName(1, col, AON.AON_CSS.aonReportTableHeader());
						tab.getCellFormatter().addStyleName(1, col, AON.AON_CSS.aonTextCenter());
						
						tab.setWidget(1, col+1, new Label("S. Acreed."));
						tab.getColumnFormatter().setWidth( col+1, "90px");
						tab.getCellFormatter().setStyleName(1, col+1, AON.AON_CSS.aonReportTableHeader());
						tab.getCellFormatter().addStyleName(1, col+1, AON.AON_CSS.aonTextCenter());
						
						if (report.showRatios() ) {
							tab.setWidget(1, col+2, new Label(" % S/Vta."));	
							tab.getColumnFormatter().setWidth( col+2,  "60px");
							tab.getCellFormatter().setStyleName(1, col+2, AON.AON_CSS.aonReportTableHeader());
							tab.getCellFormatter().addStyleName(1, col+2, AON.AON_CSS.aonTextCenter());
	
							
							tab.setWidget(1, col+3, new Label(" % S/Com."));
							tab.getColumnFormatter().setWidth( col+3,  "60px");
							tab.getCellFormatter().setStyleName(1, col+3, AON.AON_CSS.aonReportTableHeader());
							tab.getCellFormatter().addStyleName(1, col+3, AON.AON_CSS.aonTextCenter());
	
							
							tab.setWidget(1, col+4, new Label(" % S/Gst."));
							tab.getColumnFormatter().setWidth( col+4,  "60px");
							tab.getCellFormatter().setStyleName(1, col+4, AON.AON_CSS.aonReportTableHeader());
							tab.getCellFormatter().addStyleName(1, col+4, AON.AON_CSS.aonTextCenter());
	
						}
						if (report.showIncreasePercent() ) {
							tab.setWidget(1, col+2, new Label(" % Incrm."));
							tab.getColumnFormatter().setWidth( col+2,  "60px");
							tab.getCellFormatter().setStyleName(1, col+2, AON.AON_CSS.aonReportTableHeader());
							tab.getCellFormatter().addStyleName(1, col+2, AON.AON_CSS.aonTextCenter());
	
						}
						iter++;
					}
					
					int row = 2;
					
					for (AccountOperatingAccount account : report.getAccounts()) {
						boolean title = account.getId() == null;
						tab.setWidget(row, 0, new Label((title?"":account.getCode()) + " " + account.getDescription()));
						if (title) {
							tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonTextRight());
							tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonReportTableBold());
							tab.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonFiscalPaddingRight());
						}
	
						int col = 1;
						String style = AON.AON_CSS.aonTextRight();
						for (DateInterval inter : report.getIntervals()) {
   							if (report.getParams().showIncreasePercent()) {
								style = (((col - 1)/columns)%2)==0? AON.AON_CSS.aonBackgroundDisabled() : AON.AON_CSS.aonBackgroundLightYellow();
							}
							AccountOperatingStatement aos = report.get(account.getCode(),inter);
							Double db = (aos != null)?aos.getDebitBalance() : 0.0;
							Double ub = (aos != null)?aos.getUnpaidBalance() : 0.0;
	
							String dbText = AonMathUtils.isNotZero(db)?AON.FMT.format(db):AonStringUtils.SPACE;
							String ubText = AonMathUtils.isNotZero(ub)?AON.FMT.format(ub):AonStringUtils.SPACE;
							
							tab.setWidget(row, col, new Label(dbText));
							tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight());
							tab.getCellFormatter().addStyleName(row, col, style);
							if (title) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
							col++;
							tab.setWidget(row, col, new Label(ubText));
							tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight());
							tab.getCellFormatter().addStyleName(row, col, style);
							if (title) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
							col++;
							if (report.showRatios()) {
								double percent = (aos != null)?aos.getSalesRatio() : 0.0;
								String percentText = AonMathUtils.isNotZero(percent)?AON.FMT.format(percent) + AonStringUtils.PERCENT:AonStringUtils.SPACE;
								tab.setWidget(row, col, new Label(percentText));
								tab.getCellFormatter().addStyleName(row, col, style);
								if (title) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
								tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBackgroundDisabled());
								col++;
								
								percent = (aos != null)?aos.getPurchasesRatio() : 0.0;
								percentText = AonMathUtils.isNotZero(percent)?AON.FMT.format(percent) + AonStringUtils.PERCENT:AonStringUtils.SPACE;
								tab.setWidget(row, col, new Label(percentText));
								tab.getCellFormatter().addStyleName(row, col, style);
								if (title) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
								tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBackgroundDisabled());
								col++;
	
								percent = (aos != null)?aos.getExpensesRatio() : 0.0;
								percentText = AonMathUtils.isNotZero(percent)?AON.FMT.format(percent) + AonStringUtils.PERCENT:AonStringUtils.SPACE;
								tab.setWidget(row, col, new Label(percentText));
								tab.getCellFormatter().addStyleName(row, col, style);
								if (title) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
								tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBackgroundDisabled());
								col++;
							}
							if (report.showIncreasePercent()) {
								double percent = (aos != null)?aos.getIncreasePercent() : 0.0;
								String percentText = AonMathUtils.isNotZero(percent)?AON.FMT.format(percent) + AonStringUtils.PERCENT:AonStringUtils.SPACE;
								tab.setWidget(row, col, new Label(percentText));
								tab.getCellFormatter().setStyleName(row, col, style);
								if (title) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
								tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
								if (AonMathUtils.isNegative(percent)) {
									tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonColorRed());	
								}
								col++;
							}
						}
						row++;	
					}
				}
				root.add(tab);
			}
				
			@Override
			public void onFailure(Throwable caught) {
				Label label = new Label(AON.MSG.noData() + " ["+caught+"]");
				root.add(label );
			}
		});
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
