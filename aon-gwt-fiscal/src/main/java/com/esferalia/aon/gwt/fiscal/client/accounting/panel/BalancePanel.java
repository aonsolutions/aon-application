package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountBalanceLineStyle;
import com.esferalia.aon.occam.api.model.AccountBalanceReport;
import com.esferalia.aon.occam.api.model.AccountBalanceReport.BalanceLine;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


public class BalancePanel extends ScrollPanel implements HasSelectionHandlers<AccountingReportParams>{

	private static FiscalServiceAsync fiscalService;
	
	private FlowPanel root;
	
	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	private AccountingReportParams params;
	
	public BalancePanel(String currentDomainName, String currentUser, Integer currentDomainId, AccountingReportParams params) {
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
	
	public AccountingReportParams getParams() {
		return params;
	}

	private void search() {
		root.clear();
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		fiscalService.getAccountBalanceReport(currentDomainName,currentUser,currentDomainId,params
				,  new AsyncCallback<AccountBalanceReport>() {
			
			@Override
			public void onSuccess(final AccountBalanceReport report) {
				if (report.getUnreadAccounts() != null && report.getUnreadAccounts().size() > 0) {
					Widget w = getErrorPanel(report);
					if (w != null) root.add(w);
				}
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
					tab.getColumnFormatter().setWidth( col++, "auto");

					col = 0;
					tab.setWidget(row,col, new Label());
					tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
					col++;
					
					if (report.getParams().isBreakdownEnabled() ) {
						tab.getColumnFormatter().setWidth( col, "120px");
						tab.setWidget(row,col, new Label("S.Deudor"));
						tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
						col++;

						tab.getColumnFormatter().setWidth( col, "120px");
						tab.setWidget(row,col, new Label("S.Acreedor"));
						tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
						col++;
					}
					LinkedHashMap<String, Integer> columns = new LinkedHashMap<String, Integer>(); 
					for (String period : report.getPeriods() ) {
						tab.getColumnFormatter().setWidth( col, "120px");
						
						tab.setWidget(row,col, new Label(period));
						tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonReportTableHeader());
						columns.put(period, new Integer(col));	
						col++;
					}
					row++;
					
					for (BalanceLine line : report.getBalances().values()) {
						paintRow(report, columns, tab,row,line);
						row++;	
					}
				}
				root.add(tab);
			}
				
			private Widget getErrorPanel(AccountBalanceReport report) {
				boolean something = false;
				FlowPanel panel = new FlowPanel();
				panel.setStyleName(AON.AON_CSS.aonErrorPanel());
				panel.addStyleName(AON.AON_CSS.aonBold());
				for (String key : report.getUnreadAccounts().keySet()) {
					LinkedList<AccountBalance> balances = report.getUnreadAccounts().get(key); 
					if (balances != null && balances.size() > 0) {
						panel.add(new Label( (balances.size()>1
									?"El saldo de las siguientes cuentas contables"
									:"El saldo de la siguiente cuenta contable")
								+ " no se est\u00E1 teniendo en cuenta para el c\u00E1lculo del balance del ejericio "+key 								
						));
						for (AccountBalance bal : balances ) {
							Label b = new Label( "\u2022 " + bal.getAccountCode()
								+ (AonStringUtils.isBlank( bal.getAccountDescription() )
									?""
									:" ["+bal.getAccountDescription()+"]")
								+ (AonMathUtils.isGreatherThanZero( bal.getDebitBalance() ) 
									?" Saldo deudor: " +  AON.FMT.format(bal.getDebitBalance())
									:"")
								+ (AonMathUtils.isGreatherThanZero( bal.getCreditBalance() ) 
									?" Saldo acreedor: " +  AON.FMT.format(bal.getCreditBalance())
									:"")
							);
							b.setStyleName(AON.AON_CSS.aonFixedFont());
							b.addStyleName(AON.AON_CSS.aonMarginLeft());
							panel.add(b);
							something = true;
						}
					}
					 
				}
				if (something && AonStringUtils.isNotBlank(report.getHelpLink())) {
					FlowPanel helpPanel = new FlowPanel();
					helpPanel.setStyleName(AON.AON_CSS.aonTextRight());
					Anchor anchor = new Anchor("[AYUDA]", report.getHelpLink());
					anchor.setStyleName(AON.AON_CSS.aonMarginTop());
					anchor.setTarget("_blank");
					helpPanel.add(anchor);
					panel.add( helpPanel );
				}
				return something?panel:null;
			}

			private void paintRow(AccountBalanceReport report, LinkedHashMap<String, Integer> columns, FlexTable tab, int row, BalanceLine line) {
				tab.getRowFormatter().setStyleName(row, AON.AON_CSS.aonReportTableRowBckHover());
				int col = 0;
				boolean inBold = line.getType() == AccountBalanceLineStyle.HEADER0 
							  || line.getType() == AccountBalanceLineStyle.TOTAL0;
				boolean breakdown = line.getType() == AccountBalanceLineStyle.BREAKDOWN;
				double fontSize = 1.3;

				String p = AonStringUtils.repeat(' ', line.getLevel() * 2);
				FlowPanel descriptionPanel = new FlowPanel();
				Label descriptionLabel = new Label( AonStringUtils.rightPad(p + line.getPrefix(), 8) + " " + line.getDescription() );
				if (line.getType() == AccountBalanceLineStyle.HEADER0) {
					descriptionLabel.addStyleName(AON.AON_CSS.aonMarginTop());	
				}
				descriptionLabel.addStyleName(AON.AON_CSS.aonPre());
				if (breakdown) {
					descriptionPanel .getElement().getStyle().setFontSize(fontSize - 0.1, Unit.EM);
					descriptionLabel.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
					tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBackgroundDisabled());	
				} else {
					descriptionPanel.getElement().getStyle().setFontSize(fontSize, Unit.EM);
				}
				descriptionPanel.add(descriptionLabel);
				tab.setWidget(row, col, descriptionPanel);
				if (inBold) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
				col++;
				
				if (AonStringUtils.isNotBlank(line.getAccounts())) {
					descriptionLabel.addStyleName(AON.AON_CSS.aonClickableLabel());
					descriptionLabel.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							AccountingReportParams newParams = params.clone();
							newParams.setAccount( new Account().setCode(line.getAccounts()));
							SelectionEvent.fire(BalancePanel.this, newParams );						
						}
					});
				}

				if (report.getParams().isBreakdownEnabled() && breakdown) {
					AccountBalance bal =  line.getBreakdown();
					if (bal !=null) {
						Label debitLabel = new Label(
								AonMathUtils.isGreatherThanZero(bal.getDebitBalance())
								?AON.ACCOUNT_FMT.format( bal.getDebitBalance() )
								:""
								);
						debitLabel.getElement().getStyle().setFontSize(fontSize - 0.1, Unit.EM);
						debitLabel.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
						tab.setWidget(row, col, debitLabel);
						tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight());
						tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonPaddingRight());
						tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBackgroundDisabled());
						col++;
						
						Label creditLabel = new Label(
								AonMathUtils.isGreatherThanZero(bal.getCreditBalance())
								?AON.ACCOUNT_FMT.format( bal.getCreditBalance() )
								:""
								);
						creditLabel.getElement().getStyle().setFontSize(fontSize - 0.1, Unit.EM);
						creditLabel.getElement().getStyle().setFontStyle(FontStyle.ITALIC);
						tab.setWidget(row, col, creditLabel);
						tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight());
						tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonPaddingRight());
						tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBackgroundDisabled());
						col++;
					}
				}

				int offset = 1 + ((report.getParams().isBreakdownEnabled())?2:0);
				for ( ;col < ( offset + report.getPeriods().size());  col++) {
					tab.setWidget(row,col, new Label());
					tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight());
					if (inBold) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
					if (breakdown) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBackgroundDisabled());
				}
				
				for ( String period : line.getAmounts().keySet() ) {
					int column = columns.get(period);
					Label numLabel = new Label(AON.ACCOUNT_FMT.format(line.getAmounts().get(period)));
					if (line.getType() == AccountBalanceLineStyle.HEADER0) {
						numLabel.addStyleName(AON.AON_CSS.aonMarginTop());	
					}
					numLabel.getElement().getStyle().setMarginRight( (line.getLevel() * 12.0) , Unit.PX);
					numLabel.getElement().getStyle().setFontSize(fontSize, Unit.EM);
					tab.setWidget(row, column, numLabel);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				Label label = new Label(AON.MSG.noData() + " ["+caught.getMessage()+"]");
				root.add(label );
			}
		});
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingReportParams> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
