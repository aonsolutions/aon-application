package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementReport;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;


public class AccountStatementPanel extends DockLayoutPanel implements HasSelectionHandlers<Integer>{

	private SimpleLayoutPanel northPanel;
	private ScrollPanel centerPanel;
	
	static FiscalServiceAsync fiscalService;
	
	public AccountStatementPanel(Unit unit) {
		super(unit);
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		
		northPanel = new SimpleLayoutPanel();
		addNorth(northPanel, 120);
		centerPanel = new ScrollPanel();
		centerPanel.addStyleName(AON.AON_CSS.aonMarginBottom());
		add(centerPanel);
	}

	public AccountStatementPanel() {
		this(Unit.PX);
	}
	
	public void show( Integer accountId, Date from, Date to) {
		final FlowPanel header = new FlowPanel("pre");
		header.setStyleName(AON.AON_CSS.aonFixedFont());
		header.addStyleName(AON.AON_CSS.aonFontMedium());
		northPanel.setWidget(header);
		
		final FlowPanel panel = new FlowPanel("pre");
		centerPanel.setWidget(panel);		
		panel.setStyleName(AON.AON_CSS.aonFixedFont());
		panel.addStyleName(AON.AON_CSS.aonFontMedium());
		panel.addStyleName(AON.AON_CSS.aonBorderBottom());
		
		fiscalService.getAccountStatement(AccountEntryModule.getCurrentDomainName(),
				AccountEntryModule.getCurrentDomain(),
				accountId, from, to,  new AsyncCallback<AccountStatementReport>() {
					
					@Override
					public void onSuccess(AccountStatementReport result) {
						Label accountLabel = new Label(result.getAccount().getFullName());
						accountLabel.setStyleName(AON.AON_CSS.aonFontBig());
						accountLabel.addStyleName(AON.AON_CSS.aonBold());
						accountLabel.addStyleName(AON.AON_CSS.aonTextCenter());
						header.add(accountLabel);
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
							header.add(label);
						}
						Label header1 = new Label("   ASIENTO    FECHA   CONCEPTO                                      "
								+ "DEBE            HABER     SALDO DEUDOR    SALDO ACREDOR CONTRAPR. NUM.DOCUMENTO");
						header1.setStyleName(AON.AON_CSS.aonBold());
						header1.addStyleName(AON.AON_CSS.aonMarginTop());
						header1.addStyleName(AON.AON_CSS.aonBorderTop());
						header1.addStyleName(AON.AON_CSS.aonBorderBottom());
						header.add(header1);
						
						for (AccountStatement as : result.getDetails()) {
							Label label = new Label(
								 AonStringUtils.leftPad(AonNumberUtils.toString(as.getAccountEntry()), 10)
								+AonStringUtils.center(AON.DATE_FORMAT.format(as.getEntryDate()),12)
								+AonStringUtils.rightPad(AonStringUtils.abbreviate(as.getConcept(),32), 33)
								+AonStringUtils.leftPad(AonMathUtils.isZero(as.getDebit())?AonStringUtils.SPACE:AON.FMT.format(as.getDebit()),17)		
								+AonStringUtils.leftPad(AonMathUtils.isZero(as.getCredit())?AonStringUtils.SPACE:AON.FMT.format(as.getCredit()),17)
								+AonStringUtils.leftPad(AonMathUtils.isZero(as.getDebitBalance())?AonStringUtils.SPACE:AON.FMT.format(as.getDebitBalance()),17)		
								+AonStringUtils.leftPad(AonMathUtils.isZero(as.getUnpaidBalance())?AonStringUtils.SPACE:AON.FMT.format(as.getUnpaidBalance()),17)
								+AonStringUtils.center(AonStringUtils.defaultString(as.getBalancingAccountCode()),11)
								+AonStringUtils.rightPad(AonStringUtils.defaultString(as.getDocumentNumber()), 33));
							panel.add(label);
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						InlineLabel label = new InlineLabel(AON.MSG.noData());
						panel.add(label );
					}
					
				});
		centerPanel.scrollToTop();
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
