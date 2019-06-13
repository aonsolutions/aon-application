package com.code.aon.ui.marketing.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Company;
import com.code.aon.marketing.Template;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailAccountDBController;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MailProcess;
import com.esferalia.aon.occam.api.model.MailTemplate;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MailProcessType;

public class MailProcessController extends DataScrollerState {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<SelectItem> mailAccounts;
	private List<SelectItem> uses;
	
	private boolean isNevv;
	
	private MailProcess to;
	private Template template;
	private com.code.aon.webmail.db.MailAccount mailAccount;
	private Integer priority;
	

	
	private MailProcessType type;

	private Boolean onlyPms(MailProcessType type) {
		return MailProcessType.AGENCY_NO_SHOW.equals(type) || MailProcessType.GUEST_RESERVATION.equals(type);
	}
	
	public List<SelectItem> getMailAccounts() {
		return mailAccounts;
	}

	public List<SelectItem> getUses() {
		return uses;
	}

	public List<SelectItem> getMailProcessTypes() {
		Locale locale = AonUtil.getCurrentLocale();
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (MailProcessType type : MailProcessType.values()) {
			if (!onlyPms(type) || (isPMS() && onlyPms(type))) {
				String name = com.code.aon.marketing.enumeration.MailProcessType.values()[type.ordinal()].getName(locale);
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

/*		Domain domain = AON.getDomain(AonUtil.getDomainName(), getCompany().getDomain(), "");
		
		AON.getMailAccountList(AonUtil.getDomainName(), getCompany().getDomain(), "", f -> f.getTypeProperty().eq((byte) MailAccountType.SYSTEM.ordinal())
				.and(
					f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
				)
		.stream().forEach(ma -> {
			mailAccounts.add(new SelectItem(ma.getId().toString(), ma.getDisplayName()));
		});
	*/
		this.uses = new LinkedList<SelectItem>();
		this.uses.add(new SelectItem(1, "Principal"));
		this.uses.add(new SelectItem(0, "Alternativo"));
	}

	private LinkedList<MailProcess> getMailProcess() {
		return AON.getApplicationParameterStream(AonUtil.getServerName(), getCompany().getDomain(), "", f -> 
			f.getDomainProperty().eq(getCompany().getDomain())
			.and(f.getNameProperty().like("AON_MAIL_PROCESS%"))).map(new MailProcessFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public class MailProcessFiller implements Function<ApplicationParameter, MailProcess> {
		@Override
		public MailProcess apply(ApplicationParameter r) {
			String[] ids = StringUtils.split(r.getValue());

			com.esferalia.aon.occam.api.model.MailAccount mailAccount = AON.getMailAccount(AonUtil.getDomainName(), getCompany().getDomain(), "", f -> f.getIdProperty().eq(Integer.parseInt(ids[0])));
			MailTemplate mailTemplate = AON.getMailTemplate(AonUtil.getDomainName(), getCompany().getDomain(), "", f -> f.getIdProperty().eq(Integer.parseInt(ids[1])));
			
			Integer pos = r.getName().lastIndexOf("_");
			String[] names = r.getName().split("_");
			
			return new MailProcess()
					.setId(r.getId())
					.setMailAccount(mailAccount)
					.setPriority(Integer.parseInt(r.getName().substring(pos+1)))
					.setTemplate(mailTemplate)
					.setType(MailProcessType.values()[Integer.parseInt(names[3])]);
		}
	}
	
	private CompanyController getCompanyController() {
		CompanyController cc = (CompanyController) AonUtil.getRegisteredBean(IMarketingConstants.COMPANY_CONTROLLER);
		return cc;
	}
	
	private Company getCompany() {
		return getCompanyController().obtainCompany();
	}
	
	private void initializeModel() {
		AON.getApplicationParameterStream(AonUtil.getDomainName(), getCompany().getDomain(), "", f -> 
			f.getDomainProperty().eq(getCompany().getDomain()).and(f.getNameProperty().like("AON_MAIL_PROCESS%")))
		.forEach(ap -> {
			if(!ap.getName().contains("AON_MAIL_PROCESS_")) {
				String name =  "AON_MAIL_PROCESS_" + ap.getName().substring(ap.getName().length() - 1) + "_" + 1;
				AON.insertApplicationParameter(AonUtil.getDomainName(), ap.getDomain(), ap.getName(), name , ap.getValue());
				AON.deleteApplicationParameter(AonUtil.getDomainName(), ap.getDomain(), "", f -> f.getIdProperty().eq(ap.getId()));
			}
		});

		setModel(new SerializableListDataModel(getMailProcess()));
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
			mp.setTemplate(new MailTemplate());
		}
		if(mp.getPriority() == null) {
			mp.setPriority(0);
		}
	}
	
	public void onReset(ActionEvent event) {
		MailProcess mp = new MailProcess();
		initializePOJO(mp);
		setTo(mp);
		setTemplate(new Template());
		setMailAccount(new com.code.aon.webmail.db.MailAccount());
		setType(null);
		setPriority(null);
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
		setTemplate(toTemplate());
		setMailAccount(toMailAccount());
		setType(getTo().getType());
		setPriority(getTo().getPriority());
		setNevv(false);
		saveState(mp);
	}

	public void onRemove(ActionEvent event) {
		AON.deleteApplicationParameter(AonUtil.getDomainName(), getCompany().getDomain(), "", f -> f.getIdProperty().eq(getTo().getId()));
		initializeModel();
		resetTo();
	}

	public void onCancel(ActionEvent event) {
		restoreState();
		resetTo();
	}

	public void onAccept(ActionEvent event) {
		if(getTo().getPriority().equals(1)) {
			String n = "AON_MAIL_PROCESS_" + getType().value() + "%1";
			AON.getApplicationParameterStream(AonUtil.getDomainName(), getCompany().getDomain(), AonUtil.getRemoteUser(), f -> 
					f.getDomainProperty().eq(getCompany().getDomain()).and(f.getNameProperty().like(n))).forEach(ap -> {
				String[] ids = StringUtils.split(ap.getValue());
				ap.setName(ap.getName().substring(0, ap.getName().length() - 1) + ids[1] + "_0");
				AON.updateApplicationParameter(AonUtil.getDomainName(), getCompany().getDomain(), AonUtil.getRemoteUser(), ap, f-> f.getIdProperty().eq(ap.getId()));
			});
		}
		
		String name = getTo().getPriority().equals(1) 
			? "AON_MAIL_PROCESS_" + getTo().getType().value() + "_" + getTo().getPriority() 
			: "AON_MAIL_PROCESS_" + getTo().getType().value() + "_" + getTo().getTemplate().getId() + "_" + getTo().getPriority();
		String value = getTo().getMailAccount().getId() + " " + getTo().getTemplate().getId();
		ApplicationParameter applicationParameter = new ApplicationParameter()
				.setDomain(getCompany().getDomain())
				.setName(name)
				.setValue(value);
		if(isNevv) {
			AON.insertApplicationParameter(AonUtil.getDomainName(), getCompany().getDomain(), AonUtil.getRemoteUser(), applicationParameter);
		} else {
			AON.updateApplicationParameter(AonUtil.getDomainName(), getCompany().getDomain(), AonUtil.getRemoteUser(), applicationParameter, 
					f -> f.getIdProperty().eq(getTo().getId()));
		}	

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
	/*		MailProcess previous = MailProcessUtil.get(this.type);
			List<MailProcess> list = getList();
			for( int i = 0; i < list.size(); i++ ) {
				if ( list.get(i).getType() == this.type ) {
					list.set(i, previous);
					break;
				}
			}	*/		
		}
	}
	
	private void resetTo() {
		this.to = null;
		setNevv(false);
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		to.setTemplate(AON.getMailTemplate(AonUtil.getDomainName(), template.getDomain(), "", f -> f.getIdProperty().eq(template.getId())));
		this.template = template;
	}
	
	public com.code.aon.webmail.db.MailAccount getMailAccount() {
		return mailAccount;
	}

	public void setMailAccount(com.code.aon.webmail.db.MailAccount mailAccount) {	
		to.setMailAccount(AON.getMailAccount(AonUtil.getDomainName(), mailAccount.getDomain(), "", f -> f.getIdProperty().eq(mailAccount.getId())));
		this.mailAccount = mailAccount;
	}
	
	public Integer getPriority() {
		return priority;
	}
	
	public void setPriority(Integer priority) {
		to.setPriority(priority);
		this.priority = priority;
	}
	
	public MailProcessType getType() {
		return type;
	}
	
	public void setType(MailProcessType type) {
		to.setType(type);
		this.type = type;
	}
	
	private com.code.aon.webmail.db.MailAccount toMailAccount() {
		com.code.aon.webmail.db.MailAccount ma = new com.code.aon.webmail.db.MailAccount();
		for (SelectItem si : mailAccounts) {
			com.code.aon.webmail.db.MailAccount m = (com.code.aon.webmail.db.MailAccount)si.getValue();
			if(getTo().getMailAccount().getId().equals(m.getId())) {
				ma = m;
			}
		}
		return ma;
	}
	
	private Template toTemplate() {
		Template t = new Template();
		t.setActive(getTo().getTemplate().isActive());
		t.setId(getTo().getTemplate().getId());
		t.setDomain(getTo().getTemplate().getDomain());
		t.setBackgroundColor(getTo().getTemplate().getBackgroundColor());
		t.setCreationDate(getTo().getTemplate().getCreationDate());
		t.setName(getTo().getTemplate().getName());
		t.setSubject(getTo().getTemplate().getSubject());
		t.setTitleColor(getTo().getTemplate().getTitleColor());
		t.setWidth(getTo().getTemplate().getWidth());
		return t;
	}
}
