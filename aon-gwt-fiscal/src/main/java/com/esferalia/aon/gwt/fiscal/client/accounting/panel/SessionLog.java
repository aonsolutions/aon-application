package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;


public class SessionLog extends ScrollPanel implements HasSelectionHandlers<AccountEntry>{
	FlowPanel root;	
	public SessionLog() {
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonTextCenter());
		addStyleName(AON.AON_CSS.aonMarginBottom());		
		root = new FlowPanel();
		setWidget(root);
	}
	public void clear( ) {
		root.clear();
	}
	public void add( final AccountEntry entry) {
		add(entry,"APARCADO");
	}
	
	public void add( final AccountEntry entry, String status) {
		final AccountEntry cloned = AccountEntry.clone(entry);
		final FocusPanel entryPanel = new FocusPanel();
		entryPanel.setTabIndex(Integer.MAX_VALUE);
		FlowPanel panel = new FlowPanel("pre");
		panel.setStyleName(AON.AON_CSS.aonClickableBlock());		
		panel.addStyleName(AON.AON_CSS.aonFixedFont());
		panel.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		// ------------------------------------- CABECERA DEL ASIENTO
		Label header = new Label(AonStringUtils.center( toString(cloned, status),160));
		header.setStyleName(AON.AON_CSS.aonBold());
		header.addStyleName(AON.AON_CSS.aonTextUnderline());
		panel.add(header);
		
		// ------------------------------------------------- APUNTES		
		double sumD = 0.0;
		double sumC = 0.0;
		for (AccountEntryDetail aed : cloned.getDetails()) {
			panel.add(new Label(toString(aed)));
			sumD = AonMathUtils.sum(sumD, aed.getDebit());	
			sumC = AonMathUtils.sum(sumC, aed.getCredit());
		}
		
		// ------------------------------------------------- TOTALES		
		Label totals = new Label(toString(sumD,sumC));
		totals.setStyleName(AON.AON_CSS.aonBold());
		panel.add(totals);
		// -----------------------------------------------------------
		
		entryPanel.setWidget(panel);
		entryPanel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				 SelectionEvent.<AccountEntry>fire(SessionLog.this, cloned);
			}
		});
		root.insert(entryPanel,0);
		entryPanel.addStyleName(AON.AON_CSS.aonValueChanged());
			new Timer() {
				@Override
				public void run() {
					entryPanel.removeStyleName(AON.AON_CSS.aonValueChanged());
				}
			}.schedule(500);
		scrollToTop();
	}
	
	private String toString(AccountEntry entry, String status) {
		StringBuffer buf = new StringBuffer();
		buf.append(AON.MSG.date());
		buf.append(AonStringUtils.COLON);
		buf.append(AonStringUtils.SPACE);
		buf.append(AON.DATE_FORMAT.format(entry.getEntryDate()));
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
		if (entry.getId() == null) {
			buf.append(status);	
		} else {
			if (entry.getId() < 0) {
				buf.append("BORRADO");	
			} else {
				buf.append("GUARDADO");
			}
		}
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
		StringBuffer buf = new StringBuffer();
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(ac),10));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate(AonStringUtils.defaultString(ad), 39), 40));
		buf.append(AonStringUtils.rightPad(AonStringUtils.abbreviate( AonStringUtils.defaultString(c), 32), 33));
		buf.append(AonStringUtils.leftPad(AON.FMT.format(deb),17));		
		buf.append(AonStringUtils.leftPad(AON.FMT.format(cre),17));
		buf.append(AonStringUtils.center(AonStringUtils.defaultString(bc),11));
		buf.append(AonStringUtils.rightPad(AonStringUtils.defaultString(dn), 33));
		return buf.toString();
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountEntry> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
