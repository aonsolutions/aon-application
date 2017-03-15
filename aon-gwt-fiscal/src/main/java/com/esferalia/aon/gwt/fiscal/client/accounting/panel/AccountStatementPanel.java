package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;


public class AccountStatementPanel extends ScrollPanel implements HasSelectionHandlers<Integer>{

	FlowPanel root;
	
	static FiscalServiceAsync fiscalService;
	
	public AccountStatementPanel() {
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		root = new FlowPanel();
		setWidget(root);
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());		
	}

	public void show( AccountStatementParams params) {
		root.clear();
		final FlowPanel header = new FlowPanel("pre");
		header.setStyleName(AON.AON_CSS.aonFixedFont());
		header.addStyleName(AON.AON_CSS.aonFontMedium());
		root.add(header);
		
		final FlowPanel panel = new FlowPanel("pre");
		root.add(panel);		
		panel.setStyleName(AON.AON_CSS.aonFixedFont());
		panel.addStyleName(AON.AON_CSS.aonFontMedium());
		panel.addStyleName(AON.AON_CSS.aonBorderBottom());
		
		fiscalService.getAccountStatement(AccountEntryModule.getCurrentDomainName(),
				AccountEntryModule.getCurrentDomain(),params
				,  new AsyncCallback<AccountStatementReport>() {
					
					@Override
					public void onSuccess(final AccountStatementReport result) {
						FlexTable headerTab = new FlexTable();
						headerTab.setStyleName(AON.AON_CSS.aonWidthAll());
						headerTab.getColumnFormatter().setWidth(0, "auto");
						headerTab.getColumnFormatter().setWidth(1, "300px");

						InlineLabel accountLabel = new InlineLabel(result.getAccount().getFullName());
						accountLabel.addStyleName(AON.AON_CSS.aonBold());
						accountLabel.addStyleName(AON.AON_CSS.aonFontBig());
						headerTab.setWidget(0, 0, accountLabel);
						headerTab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonTextCenter());
						
						FlowPanel headerPanel = new FlowPanel();
						headerPanel.setStyleName(AON.AON_CSS.aonPanelGridSearch());
						headerPanel.addStyleName(AON.AON_CSS.aonWidthAll());
						headerPanel.addStyleName(AON.AON_CSS.aonNopadding());
						headerPanel.addStyleName(AON.AON_CSS.aonNoMargin());
						InlineLabel dateFromLabel = new InlineLabel(AON.MSG.from());
						dateFromLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
						dateFromLabel.addStyleName(AON.AON_CSS.aonItalic());
						final DateBoxEx dateFrom = new DateBoxEx();
						dateFrom.addStyleName(AON.AON_CSS.aonMarginLeft());
						dateFrom.setValue(result.getFrom());

						InlineLabel dateToLabel = new InlineLabel(AON.MSG.to());
						dateToLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
						dateToLabel.addStyleName(AON.AON_CSS.aonItalic());
						final DateBoxEx dateTo = new DateBoxEx();
						dateTo.addStyleName(AON.AON_CSS.aonMarginLeft());
						dateTo.setValue(result.getTo());
						Button filter = new Button();
						filter.setText(AON.MSG.searchAction());
						filter.setStyleName(AON.AON_CSS.aonIconCommandButton());
						filter.addStyleName(AON.AON_CSS.aonIconSearch());
						filter.addStyleName(AON.AON_CSS.aonMarginLeft());						
						filter.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								if ( (dateTo.getValue() == null && dateFrom.getValue() != null) 
								  || (dateTo.getValue() != null && dateFrom.getValue() == null)
								  || (!dateTo.getValue().before(dateFrom.getValue()))) {
									AccountStatementPanel.this.show(
											new AccountStatementParams()
											.setAccount( result.getAccount().getId())
											.setFromDate( dateFrom.getValue() )
											.setToDate( dateTo.getValue())
											);
								}
							}
						});
						headerPanel.add(dateFromLabel);
						headerPanel.add(dateFrom);
						headerPanel.add(dateToLabel);
						headerPanel.add(dateTo);
						headerPanel.add(filter);

						headerTab.setWidget(0, 1, headerPanel);
						headerTab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonTextRight());
						headerTab.getCellFormatter().addStyleName(0, 1, AON.AON_CSS.aonPaddingRight());


						header.add(headerTab);
						
//						int height = 70 + (14 * result.getSummary().size());
//						setWidgetSize(northPanel, height); 
						for (AccountStatement as : result.getSummary()) {
							Label label = new Label(
									AonStringUtils.repeat(' ', 10)
									+ AonStringUtils.rightPad(as.getConcept(), 43)
									+ AonStringUtils.leftPad(AonMathUtils.isZero(as.getDebit())
											?AonStringUtils.SPACE
											:AON.FMT.format(as.getDebit()),17)		
									+AonStringUtils.leftPad(AonMathUtils.isZero(as.getCredit())
											?AonStringUtils.SPACE
											:AON.FMT.format(as.getCredit()),17)
									+AonStringUtils.leftPad(AonMathUtils.isZero(as.getDebitBalance())
											?AonStringUtils.SPACE
											:AON.FMT.format(as.getDebitBalance()),17)		
									+AonStringUtils.leftPad(AonMathUtils.isZero(as.getUnpaidBalance())
											?AonStringUtils.SPACE
											:AON.FMT.format(as.getUnpaidBalance()),17)
									+AonStringUtils.repeat(AonStringUtils.SPACE, 10)
									);
							if (as.getType() == 1) {
								label.addStyleName(AON.AON_CSS.aonBold());	
								label.addStyleName(AON.AON_CSS.aonColorGreen());
							}
							header.add(label);
						}
						Label header1 = new Label(" DIARIO    FECHA   CONCEPTO                                      "
								+ "DEBE            HABER     SALDO DEUDOR    SALDO ACREDOR CONTRAPARTIDA         NUM.DOCUMENTO");
						header1.setStyleName(AON.AON_CSS.aonBold());
						header1.addStyleName(AON.AON_CSS.aonMarginTop());
						header1.addStyleName(AON.AON_CSS.aonBorderTop());
						header1.addStyleName(AON.AON_CSS.aonBorderBottom());
						header.add(header1);
						
						if (result.getDetails().isEmpty()) {
							Label label = new Label(AON.MSG.noData());
							panel.add(label );
						} else {
							for (final AccountStatement as : result.getDetails()) {
								final Label label = new Label(
										AonStringUtils.leftPad(AonStringUtils.defaultString(AonNumberUtils.toString(as.getJournal())), 7)
										+AonStringUtils.center(AON.DATE_FORMAT.format(as.getEntryDate()),12)
										+AonStringUtils.rightPad(AonStringUtils.abbreviate(as.getConcept(),32), 33)
										+AonStringUtils.leftPad(AonMathUtils.isZero(as.getDebit())?AonStringUtils.SPACE:AON.FMT.format(as.getDebit()),17)		
										+AonStringUtils.leftPad(AonMathUtils.isZero(as.getCredit())?AonStringUtils.SPACE:AON.FMT.format(as.getCredit()),17)
										+AonStringUtils.leftPad(AonMathUtils.isZero(as.getDebitBalance())?AonStringUtils.SPACE:AON.FMT.format(as.getDebitBalance()),17)		
										+AonStringUtils.leftPad(AonMathUtils.isZero(as.getUnpaidBalance())?AonStringUtils.SPACE:AON.FMT.format(as.getUnpaidBalance()),17)
										+AonStringUtils.SPACE
										+AonStringUtils.rightPad(AonStringUtils.defaultString(as.getBalancingAccountCode()),10)
										+AonStringUtils.rightPad(AonStringUtils.defaultString(AonStringUtils.abbreviate(as.getBalancingAccountDescription(),11)),12)
										+AonStringUtils.SPACE
										+AonStringUtils.rightPad(AonStringUtils.defaultString(as.getDocumentNumber()), 33));
								label.addClickHandler(new ClickHandler() {
									
									@Override
									public void onClick(ClickEvent event) {
										SelectionEvent.<Integer>fire(AccountStatementPanel.this, as.getAccountEntry());
									}
								});
								label.addMouseOverHandler(new MouseOverHandler() {
									@Override
									public void onMouseOver(MouseOverEvent event) {
										label.addStyleName(AON.AON_CSS.aonBold());
										label.addStyleName(AON.AON_CSS.aonBorderBottom());
									}
								});
								label.addMouseOutHandler(new MouseOutHandler() {
									
									@Override
									public void onMouseOut(MouseOutEvent event) {
										label.removeStyleName(AON.AON_CSS.aonBold());
										label.removeStyleName(AON.AON_CSS.aonBorderBottom());
									}
								});
								label.addStyleName(AON.AON_CSS.aonClickable());
								label.setTitle(AON.MSG.goAction());
								panel.add(label);
							}
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Label label = new Label(AON.MSG.noData() + " ["+caught+"]");
						panel.add(label );
					}
					
				});
		scrollToTop();
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
