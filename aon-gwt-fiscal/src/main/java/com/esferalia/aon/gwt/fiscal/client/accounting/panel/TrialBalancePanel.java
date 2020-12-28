package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportService;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport;
import com.esferalia.aon.occam.api.model.AccountTrialBalanceReport.AccountTrialBalance;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;


public class TrialBalancePanel extends ScrollPanel implements HasSelectionHandlers<AccountingReportParams>{

	private static AccountingReportServiceAsync SERVICE;
	
	private FlowPanel root;
	
	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	private AccountingReportParams params;
	
	public TrialBalancePanel(String currentDomainName, String currentUser, Integer currentDomainId, AccountingReportParams params) {
		this.currentDomainName = currentDomainName;
		this.currentUser = currentUser;
		this.currentDomainId = currentDomainId;
		this.params = params;

		root = new FlowPanel();
		setWidget(root);
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());		
		
		search();
		scrollToTop();
	}
	
	public AccountingReportParams getParams() {
		return params;
	}

	private void search() {
		root.clear();
		
		AccountingReportServiceAsync serviceRaw = GWT.create(AccountingReportService.class);
		SERVICE = new AccountingReportServiceAsyncDecorator(serviceRaw);
		
		SERVICE.getAccountTrialBalanceReport(currentDomainName,currentUser,currentDomainId,params
				,  new AsyncCallback<AccountTrialBalanceReport>() {
			
			@Override
			public void onSuccess(final AccountTrialBalanceReport report) {
				FlexTable tab = new FlexTable();
				tab.addStyleName(AON.CSS.aonGrid());
				int row = 0;
				int col = 0;
				
				if (report.isEmpty()) {
					tab.addStyleName(AON.CSS.aonMarginTop());
					tab.setWidget(row, col++, new Label(AON.MSG.noData()));
					tab.addStyleName(AON.CSS.aonBold());	
					tab.addStyleName(AON.CSS.aonTextCenter());
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
					int colPlus = 0;
					
					tab.setWidget(row,col, new Label());
					tab.getFlexCellFormatter().setColSpan(row, colPlus, 2);
					col++;
					
					tab.setWidget(row+1, colPlus, new Label(AON.MSG.account()));
					tab.getFlexCellFormatter().setColSpan(row+1, colPlus, 2);
					tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
					tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
					colPlus++;
					
					if (report.hasBeforePeriodAmounts()) {
						String msg = report.getParams().getSelectedPeriod() != null
								?"Saldos anter. al " + AON.DATE_FORMAT.format(report.getParams().getSelectedPeriod().getInitiationDate())
								:"Saldos anteriores";
						tab.setWidget(row, col, new Label(msg));
						tab.getFlexCellFormatter().setColSpan(row, col, 2);
						tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonGridHeader());
						tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonTextCenter());
						
						tab.setWidget(row+1, colPlus, new Label("Saldo deudor"));
						tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
						tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
						colPlus++;
						tab.setWidget(row+1, colPlus, new Label("Saldo acreed."));
						tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
						tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
						colPlus++;
						col++;
					}
					
					if (report.hasOpeningAmounts()) {
						tab.setWidget(row, col, new Label("Saldo apertura"));
						tab.getFlexCellFormatter().setColSpan(row, col, 2);
						tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonGridHeader());
						tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonTextCenter());
						
						tab.setWidget(row+1, colPlus, new Label("Saldo deudor"));
						tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
						tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
						colPlus++;
						tab.setWidget(row+1, colPlus, new Label("Saldo acreed."));
						tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
						tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
						colPlus++;
						
						col++;
					}
					
					if (report.hasInPeriodPreviousAmounts()) {
						
						tab.setWidget(row, col, new Label("Saldo hasta " + AON.DATE_FORMAT.format(report.getParams().getFromDate())));
						tab.getFlexCellFormatter().setColSpan(row, col, 2);
						tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonGridHeader());
						tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonTextCenter());
						
						tab.setWidget(row+1, colPlus, new Label("Saldo deudor"));
						tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
						tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
						colPlus++;
						tab.setWidget(row+1, colPlus, new Label("Saldo acreed."));
						tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
						tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
						colPlus++;
						col++;
					}
					
					tab.setWidget(row, col, new Label("Sumas periodo"));
					tab.getFlexCellFormatter().setColSpan(row, col, 2);
					tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonGridHeader());
					tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonTextCenter());
					
					tab.setWidget(row+1, colPlus, new Label("Debe"));
					tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
					tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
					colPlus++;
					tab.setWidget(row+1, colPlus, new Label("Haber."));
					tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
					tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
					colPlus++;
					col++;

					tab.setWidget(row, col, new Label("Saldos finales"));
					tab.getFlexCellFormatter().setColSpan(row, col, 2);
					tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonGridHeader());
					tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonTextCenter());
					
					tab.setWidget(row+1, colPlus, new Label("Saldo deudor"));
					tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
					tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
					colPlus++;
					tab.setWidget(row+1, colPlus, new Label("Saldo acreed."));
					tab.getCellFormatter().setStyleName(row+1, colPlus, AON.CSS.aonGridHeader());
					tab.getCellFormatter().addStyleName(row+1, colPlus, AON.CSS.aonTextCenter());
					colPlus++;
					col++;
					
					row++;
					row++;
					
					for (AccountTrialBalance bal : report.getBalances().values()) {
						paintRow(report, tab,row,bal);
						row++;	
					}
					if (!report.getParams().isLowLevelAccountVisible()) {
						AccountTrialBalance tot = report.getTotalBalance();
						tot.setDescription("TOTALES");
						paintRow(report, tab,row, report.getTotalBalance(),true);
						tab.getRowFormatter().addStyleName(row, AON.CSS.aonBold());	
					}
				}
				root.add(tab);
			}
			private void paintRow(AccountTrialBalanceReport report, FlexTable tab, int row, AccountTrialBalance bal ) {
				paintRow(report, tab, row, bal , false);	
			}
			
			private void paintRow(AccountTrialBalanceReport report, FlexTable tab, int row, AccountTrialBalance bal, boolean bold ) {
				int col = 0;
				Label codeLabel = new Label(bal.getCode() );
				boolean inBold = false;
				if (report.getParams().isLowLevelAccountVisible()) {
					int length = AonStringUtils.length(bal.getCode());
					inBold = (length<report.getParams().getLevel());
					codeLabel.getElement().getStyle().setMarginLeft( length > 4 ? 20.0 : (length-1)*5 , Unit.PX);
				}
				inBold = bold || inBold; 
				tab.setWidget(row, col, codeLabel);
				codeLabel.setStyleName(AON.CSS.aonClickableLabel());
				codeLabel.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						AccountingReportParams newParams = params.clone();
						newParams.setAccount( new Account().setCode(bal.getCode()));
						SelectionEvent.fire(TrialBalancePanel.this, newParams );						
					}
				});
				
				if (inBold) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
				col++;
				tab.setWidget(row, col, new Label(bal.getDescription()));
				if (inBold) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
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
				tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextRight());
				if (toogleStyle) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBackgroundLigthGray());
				if (inBold) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
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
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingReportParams> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
