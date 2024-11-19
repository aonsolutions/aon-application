package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

class ConsoleDomainTable extends AonDisplayGrid implements HasSelectionHandlers<ConsoleDomainTableRow>{
	
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainTable.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	interface ConsoleDomainTableCallback {
		public String getSchema();
		public String[] getSchemas();
		public int addCount();
		public void check(ConsoleDomainTableRow row);
		public boolean isRunning();
		public void setRunning(boolean running);
		
		public void showError(String message);
		public void showInfo(String message);
		public boolean isAdvancedMode();
		public void onMultipleDelete();
		public void onDelete(Integer domainId, String descrption, AsyncCallback<Boolean> cbk);
		public void onDuplicate(DomainParams origin, DomainParams target);
		public void onInfo(Integer domainId);
		public void onChangeActive(Integer domainId, boolean active, AsyncCallback<Domain> cbk);
		public void onChangeExpirationDate(Integer domainId, Date expireDate, AsyncCallback<Domain> cbk);
		public void onValidate(Integer domainId, String name, String descrption, AsyncCallback<Boolean> cbk);
		public void onSwitchRemoteAccess(Integer integer, AsyncCallback<Boolean> cbk);
		public void onAvailableUsers(JsConsoleDomain domain, AsyncCallback<LinkedList<User>> cbk);
		public void onEditDomain( Integer domainId, String descrption );
		public void onUtilitiesDomain( JsConsoleDomain domain );
	}
	
	
	ConsoleDomainTable() {
		this.addStyleName(AON.CSS.aonMarginTop());
		this.addStyleName(AON.CSS.aonBlockCenter());
		paintHeader();
	}
	
	private void paintHeader() {
		this.addHeaderRow()
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label("#"),AON.CSS.aonWidth20())
			.addCell(new Label("ID"),AON.CSS.aonWidth20())
			.addCell(new Label(AON.MSG.type()),AON.CSS.aonWidth80(), AON.CSS.aonNowrap())
			.addCell(new Label("Act."),AON.CSS.aonWidth20())
			.addCell(new Label("Crea"),AON.CSS.aonWidth20())
			.addCell(new Label("Hijos"),AON.CSS.aonWidth40())
			.addCell(new Label("Padre"),AON.CSS.aonWidth40())
			.addCell(new Label("Her."),AON.CSS.aonWidth20())
			.addCell(new Label("Usr."),AON.CSS.aonWidth20())
			.addCell(new Label(AON.MSG.name()),AON.CSS.aonWidthAuto())
			.addCell(new Label(AON.MSG.description()),AON.CSS.aonWidth150(), AON.CSS.aonNowrap())		
			.addCell(new Label("\u00FAlt. Acceso"),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
			.addCell(new Label("Expira"),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
			.addCell(new Label(""),AON.CSS.aonWidth100())
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label(""),AON.CSS.aonWidth20())
			.addCell(new Label(""),AON.CSS.aonWidth20())
		;
	}
	
	public void addRow(ConsoleDomainTableCallback callback, JsConsoleDomain domain) {
		this.add( new ConsoleDomainTableRow(callback, domain) );
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<ConsoleDomainTableRow> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
}
