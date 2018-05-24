package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceParams;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;


public class TrialBalancePanel extends ScrollPanel implements HasSelectionHandlers<Account>{

	private static FiscalServiceAsync fiscalService;
	
	private FlowPanel root;
	
	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	private AccountTrialBalanceParams params;
	
	public TrialBalancePanel(String currentDomainName, String currentUser, Integer currentDomainId, AccountTrialBalanceParams params) {
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
		fiscalService.getAccountTrialBalanceReport(currentDomainName,currentUser,currentDomainId,params
				,  new AsyncCallback<AccountTrialBalanceReport>() {
			
			@Override
			public void onSuccess(final AccountTrialBalanceReport report) {
				FlexTable tab = new FlexTable();
				tab.addStyleName(AON.AON_CSS.aonReportTable());
				int row = 0;
				int col = 0;
				
				if (report.isEmpty()) {
					tab.addStyleName(AON.AON_CSS.aonMarginTop());
					tab.setWidget(row, col++, new Label(AON.MSG.noData()));
					tab.addStyleName(AON.AON_CSS.aonReportTableBold());	
					tab.addStyleName(AON.AON_CSS.aonTextCenter());
				} else {
					tab.getColumnFormatter().setWidth( col++, "80px");
					tab.getColumnFormatter().setWidth( col++, "auto");
					
					if (report.hasBeforePeriodAmounts()) {
						tab.getColumnFormatter().setWidth( col++, "100px");
						tab.getColumnFormatter().setWidth( col++, "100px");
					}
					
					if (report.hasOpeningAmounts()) {
						tab.getColumnFormatter().setWidth( col++, "100px");
						tab.getColumnFormatter().setWidth( col++, "100px");
					}
					
					if (report.hasInPeriodPreviousAmounts()) {
						tab.getColumnFormatter().setWidth( col++, "100px");
						tab.getColumnFormatter().setWidth( col++, "100px");
					}
					
					tab.getColumnFormatter().setWidth( col++, "100px");
					tab.getColumnFormatter().setWidth( col++, "100px");
					
					tab.getColumnFormatter().setWidth( col++, "100px");
					tab.getColumnFormatter().setWidth( col++, "100px");
					
					col = 0;
					
					tab.setWidget(row, col, new Label(AON.MSG.account()));
					tab.getFlexCellFormatter().setColSpan(row, col, 2);
					tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
					col++;
					
					if (report.hasBeforePeriodAmounts()) {
						String msg = report.getPeriod() != null
								?"Saldos anter. al " + AON.DATE_FORMAT.format(report.getPeriod().getInitiationDate())
								:"Saldos anteriores";
						tab.setWidget(row, col, new Label(msg));
						tab.getFlexCellFormatter().setColSpan(row, col, 2);
						tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
						col++;
					}
					
					if (report.hasOpeningAmounts()) {
						tab.setWidget(row, col, new Label("Saldo apertura"));
						tab.getFlexCellFormatter().setColSpan(row, col, 2);
						tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
						col++;
					}
					
					if (report.hasInPeriodPreviousAmounts()) {
						
						tab.setWidget(row, col, new Label("Saldo hasta " + AON.DATE_FORMAT.format(report.getParams().getFromDate())));
						tab.getFlexCellFormatter().setColSpan(row, col, 2);
						tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
						col++;
					}
					
					tab.setWidget(row, col, new Label("Sumas periodo"));
					tab.getFlexCellFormatter().setColSpan(row, col, 2);
					tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
					col++;

					tab.setWidget(row, col, new Label("Saldos finales"));
					tab.getFlexCellFormatter().setColSpan(row, col, 2);
					tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
					col++;
					
					row++;
					
					
					for (AccountTrialBalance bal : report.getBalances().values()) {
						paintRow(report, tab,row,bal);
						row++;	
					}
					if (!report.getParams().isLowLevelAccountVisible()) {
						AccountTrialBalance tot = report.getTotalBalance();
						tot.setDescription("TOTALES");
						paintRow(report, tab,row, report.getTotalBalance());
						tab.getRowFormatter().addStyleName(row, AON.AON_CSS.aonReportTableBold());	
					}
				}
				root.add(tab);
			}
				
			private void paintRow(AccountTrialBalanceReport report, FlexTable tab, int row, AccountTrialBalance bal ) {
				tab.getRowFormatter().setStyleName(row, AON.AON_CSS.aonReportTableRowBckHover());
				int col = 0;
				Label codeLabel = new Label(bal.getCode() );
				boolean inBold = false;
				if (report.getParams().isLowLevelAccountVisible()) {
					int length = AonStringUtils.length(bal.getCode());
					inBold = (length<report.getParams().getLevel());
					codeLabel.getElement().getStyle().setMarginLeft( length > 4 ? 20.0 : (length-1)*5 , Unit.PX);
				}
				tab.setWidget(row, col, codeLabel);
				if (inBold) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
				col++;
				tab.setWidget(row, col, new Label(bal.getDescription()));
				if (inBold) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
				col++;
				
				boolean toogleStyle = true;
				// Saldos anteriores
				if (report.hasBeforePeriodAmounts()) {
					tab.setWidget(row, col, new Label(decorateNumber(bal.getBeforePeriodDebitBalance())));
					decorateCell(tab, row, col, toogleStyle, inBold);
					col++;
					
					tab.setWidget(row, col, new Label(decorateNumber(bal.getBeforePeriodUnpaidBalance())));
					decorateCell(tab, row, col, toogleStyle, inBold);
					col++;
					toogleStyle = !toogleStyle;
				}
				
				// Saldo apertura
				if (report.hasOpeningAmounts()) {
					tab.setWidget(row, col, new Label(decorateNumber(bal.getInPeriodOpeningDebitBalance())));
					decorateCell(tab, row, col, toogleStyle, inBold);
					col++;
					
					tab.setWidget(row, col, new Label(decorateNumber(bal.getInPeriodOpeningUnpaidBalance())));
					decorateCell(tab, row, col, toogleStyle, inBold);
					col++;
					
					toogleStyle = !toogleStyle; 
				}

				// Saldo previo
				if (report.hasInPeriodPreviousAmounts()) {						
					tab.setWidget(row, col, new Label(decorateNumber(bal.getInPeriodBeforeDebitBalance())));
					decorateCell(tab, row, col, toogleStyle, inBold);
					col++;

					tab.setWidget(row, col, new Label(decorateNumber(bal.getInPeriodBeforeUnpaidBalance())));
					decorateCell(tab, row, col, toogleStyle, inBold);
					col++;
					
					toogleStyle = !toogleStyle;
				}
				
				// Sumas periodo
				tab.setWidget(row, col, new Label(decorateNumber(bal.getInPeriodDebit())));
				decorateCell(tab, row, col, toogleStyle, inBold);
				col++;
				
				
				tab.setWidget(row, col, new Label(decorateNumber(bal.getInPeriodCredit())));
				decorateCell(tab, row, col, toogleStyle, inBold);
				col++;
				
				toogleStyle = !toogleStyle;
				
				// Saldos finales
				tab.setWidget(row, col, new Label(decorateNumber(bal.getAfterPeriodDebitBalance())));
				decorateCell(tab, row, col, toogleStyle, inBold);
				col++;
				
				tab.setWidget(row, col, new Label(decorateNumber(bal.getAfterPeriodUnpaidBalance())));
				decorateCell(tab, row, col, toogleStyle, inBold);
				col++;
			}

			private void decorateCell(FlexTable tab, int row, int col, boolean toogleStyle, boolean inBold) {
				tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight());
				if (toogleStyle) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBackgroundDisabled());
				if (inBold) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
			}

			@Override
			public void onFailure(Throwable caught) {
				Label label = new Label(AON.MSG.noData() + " ["+caught+"]");
				root.add(label );
			}
		});
	}
	private String decorateNumber(double value) {
		return AonMathUtils.isNotZero(value)?AON.FMT.format(value):AonStringUtils.SPACE;
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
