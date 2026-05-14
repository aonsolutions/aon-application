package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.LinkedHashSet;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.TrialBalancePanelReport;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;


public class AccountPreviewPanel extends DockLayoutPanel implements HasSelectionHandlers<Integer>{
	  
	private static final String SESSION_LOG_BACKGROUND_COLOR = "lightyellow";
	
	private SessionLog entryPanel;
	 
	public AccountPreviewPanel(AccountEntryModuleOptions moduleOptions, final IAccountEntryWrapper ... entries) {
		super(Unit.PX);
		String msg = "Click en el apunte para visualizar los saldos de las cuentas a la fecha del asiento.";
		Label northLabel = new Label( msg );
		northLabel.setStyleName(AON.CSS.aonTextRight());
		northLabel.addStyleName(AON.CSS.aonPaddingRight());
		northLabel.getElement().getStyle().setBackgroundColor(SESSION_LOG_BACKGROUND_COLOR);
		addNorth(northLabel, 15);
		
		entryPanel = new SessionLog();
		entryPanel.getElement().getStyle().setBackgroundColor(SESSION_LOG_BACKGROUND_COLOR);
		add(entryPanel);
		
		if (moduleOptions.isTrialBalanceFromPreviewEnabled()) {
			entryPanel.setTitle(msg);
			entryPanel.addSelectionHandler( event -> {
				
				LinkedHashSet<String> accounts = new LinkedHashSet<String>();
				AccountingReportParams params = null;
				IAccountEntryWrapper entryWrapper = event.getSelectedItem();
				AccountEntry entry = entryWrapper.getAccountEntry();
				for ( AccountEntryDetail detail : entry.getDetails()) {
					if (AonStringUtils.isNotBlank(detail.getAccountCode())) {
						accounts.add(detail.getAccountCode());
					}
					if (AonStringUtils.isNotBlank(detail.getBalancingAccountCode())) {
						accounts.add(detail.getBalancingAccountCode());
					}
				}
				if (params == null) {
					params = new AccountingReportParams ()
						.setDomain(moduleOptions.getDomain())
						.setPeriod(entry.getPeriod())
						.setFromDate(DateUtils.getFirstDayOfYear(entry.getEntryDate()))
						.setToDate(entry.getEntryDate())
						.setLevel(9)
						.setNoActivityAccountVisible(true);
				}
				if (accounts.size() > 0) {
					String accountsParam = ""; 
					for (String code : accounts) {
						if ( !"".equals(accountsParam)) {
							accountsParam = accountsParam + "|";
						}
						accountsParam = accountsParam + code;
					}
					params.setAccount(new Account().setCode(accountsParam));
					AonCustomPopup entryDialog = new AonCustomPopup();
					entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
					entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
					entryDialog.setAnimationEnabled(true);
					entryDialog.setGlassEnabled(true);
					entryDialog.setModal(true);
					entryDialog.setCaption(AON.MSG.trialBalabce());
					AccountingReportModuleOptions options = new AccountingReportModuleOptions();
					options.setParentWidget(entryDialog);
					options.setDomainName(moduleOptions.getDomainName());
					options.setDomain(moduleOptions.getDomain());
					options.setUser(moduleOptions.getUser());
					TrialBalancePanelReport trialPanel = new TrialBalancePanelReport(options, params );
					entryDialog.add(trialPanel);
					entryDialog.center();
					entryDialog.show();
				}
			});
		}
		entryPanel.addPreview(entries);
	}
	
	public void preview( final IAccountEntryWrapper ... entries) {
		entryPanel.clear();
		entryPanel.addPreview(entries);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	public void clearPreview() {
		entryPanel.clear();
	}
}
