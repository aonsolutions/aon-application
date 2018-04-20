package com.code.aon.ui.marketing.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.marketing.MailProcess;
import com.code.aon.marketing.Template;
import com.code.aon.marketing.enumeration.MailProcessType;
import com.code.aon.marketing.util.MailProcessUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailAccountDBController;
import com.code.aon.webmail.db.MailAccount;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.DomainType;

public class MailProcessController extends DataScrollerState {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<SelectItem> mailAccounts;
	
	private boolean isNevv;
	
	private MailProcess to;
	
	private MailProcessType type;

	private boolean isSelectable(MailProcessType type) {
		if(getTo().getType() == type) {
			return true;
		}
		for(MailProcess mp : getList() ) {
			if(mp.getType() == type) {
				return false;
			}
		}
		return true;
	}
	
	private Boolean onlyPms(MailProcessType type) {
		return MailProcessType.AGENCY_NO_SHOW.equals(type) || MailProcessType.GUEST_RESERVATION.equals(type);
	}
	
	public List<SelectItem> getMailAccounts() {
		return mailAccounts;
	}

	public List<SelectItem> getMailProcessTypes() {
		Locale locale = AonUtil.getCurrentLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (MailProcessType type : MailProcessType.values()) {
			if (isSelectable(type) && (!onlyPms(type) || (isPMS() && onlyPms(type)))) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				list.add(item);				
			}
		}
		return list;
	}
	
	public Boolean isPMS(){
		return isPlayasol() && isHotel();
	}

	public Boolean isHotel(){
		Integer domainId = DomainManager.getCurrentDomain();
		String domainName = AonUtil.getDomainName();
		String user = AonUtil.getRemoteUser();
		Domain domain = AON.getDomain(domainName, domainId, user);
		return domain.getDomainType().equals(DomainType.HOTEL);
	}

	public Boolean isPlayasol(){
		String domainName = AonUtil.getDomainName();
		return domainName.contains("playasol");
	}
	
	public void onInit( ActionEvent event ) {
		initializeModel();
		MailAccountDBController account = (MailAccountDBController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_ACCOUNT_DB);
		this.mailAccounts = account.getMailAccounts(false);
	}

	private void initializeModel() {
		List<MailProcess> list = new LinkedList<MailProcess>();
		for( MailProcessType type : MailProcessType.values() ) {
			MailProcess mailProcess = MailProcessUtil.get(type);
			if ( mailProcess != null ) {
				list.add(mailProcess);
			}
		}
		setModel(new SerializableListDataModel(list));
	}
	
	public boolean isNevv() {
		return isNevv;
	}

	public void setNevv(boolean isNevv) {
		this.isNevv = isNevv;
	}

	public MailProcess getTo() {
		return to;
	}

	public void setTo(MailProcess to) {
		this.to = to;
	}

	private void initializePOJO( MailProcess mp ) {
		if ( mp.getMailAccount() == null ) {
			mp.setMailAccount(new MailAccount());
		}
		if ( mp.getTemplate() == null ) {
			mp.setTemplate(new Template());
		}
	}
	
	public void onReset(ActionEvent event) {
		MailProcess mp = new MailProcess();
		initializePOJO(mp);
		setTo(mp);
		setNevv(true);
	}
	
	private MailProcess getSelectedTO() {
		if ( getDirectModel().isRowAvailable() ) {
			return (MailProcess) getDirectModel().getRowData();	
		}
		return null;
	}

	public void onSelect(ActionEvent event) {
		MailProcess mp = getSelectedTO();
		initializePOJO(mp);
		setTo(mp);
		setNevv(false);
		saveState(mp);
	}

	public void onRemove(ActionEvent event) {
		MailProcessUtil.remove(getTo());
		initializeModel();
		resetTo();
	}

	public void onCancel(ActionEvent event) {
		restoreState();
		resetTo();
	}

	public void onAccept(ActionEvent event) {
		MailProcessUtil.save(getTo());
		resetTo();
		initializeModel();
	}
	
	private void saveState(MailProcess to) {
		this.type = to.getType();
	}
	
	@SuppressWarnings("unchecked")
	private List<MailProcess> getList() {
		return (List<MailProcess>) getDirectModel().getWrappedData();
	}
	
	private void restoreState() {
		if ( this.type != null ) {
			MailProcess previous = MailProcessUtil.get(this.type);
			List<MailProcess> list = getList();
			for( int i = 0; i < list.size(); i++ ) {
				if ( list.get(i).getType() == this.type ) {
					list.set(i, previous);
					break;
				}
			}			
		}
	}
	
	private void resetTo() {
		this.to = null;
		setNevv(false);
	}
	
}
