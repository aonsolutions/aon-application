package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryPrinter;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryWrapper;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;


public class SessionLog extends ScrollPanel implements HasSelectionHandlers<IAccountEntryWrapper> {
	private static final AccountEntryServiceAsync ACCOUNT_ENTRY_SERVICE;
	static {
		AccountEntryServiceAsync accountEntryServiceRaw = GWT.create(AccountEntryService.class);
		ACCOUNT_ENTRY_SERVICE = new AccountEntryServiceAsyncDecorator(accountEntryServiceRaw);
	}
	
	private static final String PREVIEW = "PREVISUALIAZACI\u00D3N"; 
	private static final String SUSPENDED = "APARCADO"; 
	private static final String DELETED = "BORRADO"; 
	private static final String SAVED = "GUARDADO"; 
	
	FlowPanel root;	
	public SessionLog() {
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonTextCenter());
		addStyleName(AON.CSS.aonPaddingBottom());		
		root = new FlowPanel();
		setWidget(root);
	}
	public SessionLog( AccountEntryModuleOptions options, Integer accountEntryId ) {
		this();
		ACCOUNT_ENTRY_SERVICE.getAccountEntry(options.getOccam(), accountEntryId, new AsyncCallback<AccountEntry>() {

			@Override
			public void onFailure(Throwable caught) {
				if (options.getExternalCallback() != null) {
					options.getExternalCallback().onFailure(caught);
				} else {
					Window.alert("Error al mostrar el apunte. [" + caught.getMessage() + "]");
				}
			}

			@Override
			public void onSuccess(AccountEntry result) {
				SessionLog.this.addSaved( new AccountEntryWrapper(result) );
			}
			
		});
	}
	
	
	@Override
	public void clear( ) {
		root.clear();
	}
	
	public void addPreview( final IAccountEntryWrapper wrapper) {
		add(wrapper,PREVIEW);
	}
	public void addPreview( final IAccountEntryWrapper[] wrappers) {
		for (int counter = wrappers.length - 1; counter >= 0; counter--) {
			add(wrappers[counter],PREVIEW);
		}
	}

	public void addDeleted( final IAccountEntryWrapper wrapper) {
		add(wrapper,DELETED);
	}
	public void addDeleted( final IAccountEntryWrapper[] wrappers) {
		for (IAccountEntryWrapper wrapper : wrappers) {
			add(wrapper,DELETED);
		}
	}

	public void addSaved( final IAccountEntryWrapper wrapper) {
		add(wrapper,SAVED);
	}
	public void addSaved( final IAccountEntryWrapper[] wrappers) {
		for (IAccountEntryWrapper wrapper : wrappers) {
			add(wrapper,SAVED);
		}
	}

	public void addSuspended( final IAccountEntryWrapper wrapper) {
		add(wrapper,SUSPENDED);
	}
	public void addSuspended( final IAccountEntryWrapper[] wrappers) {
		for (IAccountEntryWrapper wrapper : wrappers) {
			add(wrapper,SUSPENDED);
		}
	}
	
	private void add( final IAccountEntryWrapper wrapper, String status) {
		final AccountEntry entry = wrapper.getAccountEntry();
		final FocusPanel entryPanel = new FocusPanel();

		FlowPanel panel = new FlowPanel("pre");
		panel.setStyleName(AON.CSS.aonClickableBlock());		
		
		// ------------------------------------- CABECERA DEL ASIENTO
		Label header = new Label(AonStringUtils.center( toString(entry, status),160));
		header.setStyleName(AON.CSS.aonBold());
		header.addStyleName(AON.CSS.aonTextUnderline());
		panel.add(header);
		
		// ------------------------------------------------- APUNTES		
		double sumD = 0.0;
		double sumC = 0.0;
		for (AccountEntryDetail aed : entry.getDetails()) {
			panel.add(new Label(toString(aed)));
			sumD = AonMathUtils.sum(sumD, aed.getDebit());	
			sumC = AonMathUtils.sum(sumC, aed.getCredit());
		}
		
		// ------------------------------------------------- TOTALES		
		Label totals = new Label(toString(sumD,sumC));
		totals.setStyleName(AON.CSS.aonBold());
		if (!AonMathUtils.equals(sumD, sumD)) {
			totals.addStyleName(AON.CSS.aonColorRed());
		}

		panel.add(totals);
		// -----------------------------------------------------------
		
		entryPanel.setWidget(panel);
		entryPanel.addClickHandler(event -> SelectionEvent.<IAccountEntryWrapper>fire(SessionLog.this, wrapper));
		root.insert(entryPanel,0);
		entryPanel.addStyleName(AON.CSS.aonValueChanged());
		new Timer() {
			@Override
			public void run() {
				entryPanel.removeStyleName(AON.CSS.aonValueChanged());
			}
		}.schedule(500);
		scrollToTop();
	}
	
	private String toString(AccountEntry entry, String status) {
		StringBuilder buf = new StringBuilder();
		buf.append(AON.MSG.date());
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		if (entry.getEntryDate() != null) {
			buf.append(AON.DATE_FORMAT.format(entry.getEntryDate()));
		} else {
			buf.append("??/??/????");
		}
		buf.append(AonStringUtils.SPACE);
		if (entry.getJournal() != null) {
			buf.append(AON.MSG.journal());
			buf.append(AonStringUtils.COLON);
			buf.append(AonStringUtils.SPACE);
			buf.append(entry.getJournal());
			buf.append(AonStringUtils.SPACE);
		}
		buf.append(AonStringUtils.OPEN_BRACKET);
		if (AonStringUtils.isNotEmpty( entry.getModificationUser())) {
			buf.append(AON.MSG.auditBy(entry.getModificationUser(),entry.getModificationDate()));
			buf.append(AonStringUtils.SPACE);
		} 
		if (AonStringUtils.isNotEmpty( entry.getModificationUser())) {
			buf.append(AON.MSG.auditBy(entry.getCreationUser(), entry.getCreationDate()));
			buf.append(AonStringUtils.SPACE);
		}
			buf.append(status);	
		buf.append(AonStringUtils.CLOSE_BRACKET);
		return buf.toString();
	}

	private String toString(double deb, double cre) {
		return toString(null,null,null,deb,cre,null,null);
	}

	private String toString(AccountEntryDetail detail) {
		return toString(detail.getAccountCode()
			,detail.getAccountDescription()
			,detail.getConcept()
			,detail.getDebit()		
			,detail.getCredit()
			,detail.getBalancingAccountCode()
			,detail.getDocumentNumber());
	}

	private String toString(String ac,String ad,String c,double deb,double cre,String bc,String dn) {
		return AccountEntryPrinter.toString(ac, ad, c, deb, cre, bc, dn, false);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<IAccountEntryWrapper> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
