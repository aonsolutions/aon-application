package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportService;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.AccountingReportServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountOperatingReport;
import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatement;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.DateInterval;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
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


public class OperatingPanel extends ScrollPanel implements HasSelectionHandlers<AccountingReportParams>{

	private static AccountingReportServiceAsync SERVICE;
	
	private FlowPanel root;
	
	private AccountingReportParams params;
	
	public OperatingPanel(AccountingReportModuleOptions options, AccountingReportParams params) {
		this.params = params;

		root = new FlowPanel();
		setWidget(root);
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());		
		
		search(options);
		scrollToTop();
	}

	private void search(AccountingReportModuleOptions options) {
		root.clear();
		
		AccountingReportServiceAsync serviceRaw = GWT.create(AccountingReportService.class);
		SERVICE = new AccountingReportServiceAsyncDecorator(serviceRaw);
		
		SERVICE.getAccountOperatingReport(options.getDomainName(),options.getUser(),options.getDomain(),params
				,  new AsyncCallback<AccountOperatingReport>() {
			
			@Override
			public void onSuccess(final AccountOperatingReport report) {
				FlexTable tab = new FlexTable();
				tab.addStyleName(AON.CSS.aonGrid());
//				if ( report.getIntervals() != null && report.getIntervals().size() < 3) {
//					tab.addStyleName(AON.CSS.aonFontMedium());
//				}
				int row = 0;
				int col = 1;
				
				if (report.isEmpty()) {
					tab.setWidget(0, 0, new Label(AON.MSG.noData()));
				} else {
					tab.setWidget(0, 0, new Label());
					tab.getFlexCellFormatter().setColSpan(0, 0, 2);
					
					if (params.isByMonth()) {
						tab.setWidget(0, 0, new Label(AON.MSG.account()));
						tab.getCellFormatter().setStyleName(0, 0, AON.CSS.aonGridHeader());
						int intervals = report.getIntervals().size();
						int columns = 2 + intervals;
						for (int i = 0; i < columns; i++) {
							tab.getColumnFormatter().setWidth( i,   "90px");
						}
						tab.getColumnFormatter().setWidth( 1, "auto");
						col = 1;
						for (DateInterval inter : report.getIntervals()) {
							tab.setWidget(0, col, new Label(inter.getName()));
							tab.getCellFormatter().setStyleName(0, col, AON.CSS.aonGridHeader());
							tab.getCellFormatter().addStyleName(0, col, AON.CSS.aonTextCenter());
							col++;
						}
						row = 1;
					} else {
						tab.setWidget(1, 0, new Label(AON.MSG.account()));
						tab.getCellFormatter().setStyleName(1, 0, AON.CSS.aonGridHeader());
						tab.getFlexCellFormatter().setColSpan(1, 0, 2);
		
						int intervals = report.getIntervals().size();
						int columnsPerInterval = 0;
						columnsPerInterval = 2;
						if (report.showRatios() ) {
							columnsPerInterval = 5;	
						}
						if (report.showIncreasePercent() ) {
							columnsPerInterval = 3;
						}
						int columns = 2 + (intervals * columnsPerInterval);
						if (intervals > 1 && report.showIncreasePercent()) {
							columns = columns - 1; 
						}

						for (int i = 0; i < columns; i++) {
							tab.getColumnFormatter().setWidth( i,   "90px");
						}
						tab.getColumnFormatter().setWidth( 1, "auto");

						int iter = 1;
						for (DateInterval inter : report.getIntervals()) {
							tab.setWidget(0, iter, new Label(inter.getName()));
							if (report.showIncreasePercent() && iter == 1) {
								tab.getFlexCellFormatter().setColSpan(0, iter, 2);
							} else {
								tab.getFlexCellFormatter().setColSpan(0, iter, columnsPerInterval);
							}
							tab.getCellFormatter().setStyleName(0, iter, AON.CSS.aonGridHeader());
							tab.getCellFormatter().addStyleName(0, iter, AON.CSS.aonTextCenter());
		
							tab.setWidget(1, col  , new Label("S. Deudor"));
							tab.getCellFormatter().setStyleName(1, col, AON.CSS.aonGridHeader());
							tab.getCellFormatter().addStyleName(1, col, AON.CSS.aonTextCenter());
							col++;
							
							tab.setWidget(1, col, new Label("S. Acreed."));
							tab.getCellFormatter().setStyleName(1, col, AON.CSS.aonGridHeader());
							tab.getCellFormatter().addStyleName(1, col, AON.CSS.aonTextCenter());
							col++;
							
							if (report.showRatios() ) {
								tab.setWidget(1, col, new Label(" % S/Vta."));	
								tab.getCellFormatter().setStyleName(1, col, AON.CSS.aonGridHeader());
								tab.getCellFormatter().addStyleName(1, col, AON.CSS.aonTextCenter());
								col++;
								
								tab.setWidget(1, col, new Label(" % S/Com."));
								tab.getCellFormatter().setStyleName(1, col, AON.CSS.aonGridHeader());
								tab.getCellFormatter().addStyleName(1, col, AON.CSS.aonTextCenter());
								col++;	
								
								tab.setWidget(1, col, new Label(" % S/Gst."));
								tab.getCellFormatter().setStyleName(1, col, AON.CSS.aonGridHeader());
								tab.getCellFormatter().addStyleName(1, col, AON.CSS.aonTextCenter());
								col++;
		
							}
							if (report.showIncreasePercent() && col != 3) {
								tab.setWidget(1, col, new Label(" % Incrm."));
								tab.getCellFormatter().setStyleName(1, col, AON.CSS.aonGridHeader());
								tab.getCellFormatter().addStyleName(1, col, AON.CSS.aonTextCenter());
								col++;
		
							}
							iter++;
						}
						row = 2;
					}

					
					
					for (AccountOperatingAccount account : report.getAccounts()) {
						boolean title = account.getId() == null;
						Label codeLabel = new Label(title?"":account.getCode());
						if (!title) {
							codeLabel.setStyleName(AON.CSS.aonClickableLabel());
							codeLabel.addClickHandler(new ClickHandler() {
								
								@Override
								public void onClick(ClickEvent event) {
									AccountingReportParams newParams = params.copy();
									newParams.setAccount( new Account()
											.setId(account.getId())
											.setCode(account.getCode()));
									SelectionEvent.fire(OperatingPanel.this, newParams );						
								}
							});
						}
						tab.setWidget(row, 0, codeLabel);
						
						Label descriptionLabel = new Label(account.getDescription());
						tab.setWidget(row, 1, descriptionLabel);
						
						if (title) {
							tab.getCellFormatter().setStyleName(row, 1, AON.CSS.aonTextRight());
							tab.getCellFormatter().addStyleName(row, 1, AON.CSS.aonBold());
							tab.getCellFormatter().addStyleName(row, 1, AON.CSS.aonPaddingRight());
							tab.getCellFormatter().addStyleName(row, 1, AON.CSS.aonNowrap());
						}
	
						col = 2;
						int oddIntervals = 0;
						for (DateInterval inter : report.getIntervals()) {
							String backgroundStyle = (oddIntervals%2==0)? AON.CSS.aonBackgroundLigthGray() : AON.CSS.aonBackgroundLigthYellow();
							AccountOperatingStatement aos = report.get(account.getCode(),inter);
							Double db = (aos != null)?aos.getDebitBalance() : 0.0;
							Double ub = (aos != null)?aos.getUnpaidBalance() : 0.0;
							if (params.isByMonth()) {
								Double saldo = AonMathUtils.round(ub - db);
								String dbSaldo = (title || AonMathUtils.isNotZero(saldo))?AON.FMT.format(saldo):AonStringUtils.SPACE;
								tab.setWidget(row, col, new Label(dbSaldo));
								tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextRight());
								if (title) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
								col++;
							} else {
		
								String dbText = (AonMathUtils.isNotZero(db))?AON.FMT.format(db):AonStringUtils.SPACE;
								String ubText = (AonMathUtils.isNotZero(ub))?AON.FMT.format(ub):AonStringUtils.SPACE;

								tab.setWidget(row, col, new Label(dbText));
								tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextRight());
								if (report.getParams().showIncreasePercent()) tab.getCellFormatter().addStyleName(row, col, backgroundStyle);
								if (title) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
								col++;
								tab.setWidget(row, col, new Label(ubText));
								tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextRight());
								if (report.getParams().showIncreasePercent()) tab.getCellFormatter().addStyleName(row, col, backgroundStyle);
								if (title) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
								col++;
								if (report.showRatios()) {
									double percent = (aos != null)?aos.getSalesRatio() : 0.0;
									String percentText = AonMathUtils.isNotZero(percent)?AON.FMT.format(percent) + AonStringUtils.PERCENT:AonStringUtils.SPACE;
									tab.setWidget(row, col, new Label(percentText));
									if (title) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
									tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonTextRight());
									tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBackgroundLigthGray());
									col++;
									
									percent = (aos != null)?aos.getPurchasesRatio() : 0.0;
									percentText = AonMathUtils.isNotZero(percent)?AON.FMT.format(percent) + AonStringUtils.PERCENT:AonStringUtils.SPACE;
									tab.setWidget(row, col, new Label(percentText));
									if (title) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
									tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonTextRight());
									tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBackgroundLigthGray());
									col++;
		
									percent = (aos != null)?aos.getExpensesRatio() : 0.0;
									percentText = AonMathUtils.isNotZero(percent)?AON.FMT.format(percent) + AonStringUtils.PERCENT:AonStringUtils.SPACE;
									tab.setWidget(row, col, new Label(percentText));
									if (title) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
									tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonTextRight());
									tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBackgroundLigthGray());
									col++;
								}
								if (report.showIncreasePercent() && col != 4) {
									double percent = (aos != null)?aos.getIncreasePercent() : -100.0;
									String percentText = 
//											AonMathUtils.isNotZero(percent)?
											AON.FMT.format(percent) + AonStringUtils.PERCENT
//											:AonStringUtils.SPACE
											;
									tab.setWidget(row, col, new Label(percentText));
									if (report.getParams().showIncreasePercent()) tab.getCellFormatter().setStyleName(row, col, backgroundStyle);
									if (title) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
									tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonTextRight());
									if (AonMathUtils.isNegative(percent)) {
										tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonColorRed());	
									}
									col++;
								}
							}
							oddIntervals++;
						}
						row++;	
					}
				}
				root.add(tab);
			}
				
			@Override
			public void onFailure(Throwable caught) {
				Label label = new Label(AON.MSG.noData() + " ["+caught.getMessage()+"]");
				label.setStyleName(AON.CSS.aonBlockMessage());
				label.addStyleName(AON.CSS.aonBlockErrorMessage());
				root.add(label );
			}
		});
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingReportParams> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
