package com.code.aon.ui.groupware.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.groupware.Notice;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;

public class NoticeController extends BasicController implements IAonObjectClasses {
	
	private static final Logger LOGGER = Logger.getLogger(NoticeController.class.getName());

	private List<SelectItem> users = new LinkedList<SelectItem>();
	
	private static final String COMMONNAME = "cn";

	private static final String SURNAME = "sn";

	private static final String USER_ALTERNATIVE_EMAIL = "mail";

	private static final String DOMAIN_MEMBER_ATTRIBUTE = "member";

	private boolean sendMail = false;
	
	private String mailList = "";

	private static final String USER_CELLULAR_NUMBER = "mobile";

	private boolean sendSMS = false;

	private boolean modify = false;

	private List<String> recipients = new ArrayList<String>();

	private String recipient;
	
	private int selected = -1;
	
	private Integer workGroupId;

	public List<SelectItem> getUsers() {
		if (users.size() == 0 && workGroupId == null) loadUsers(); 
		return users;
	}

	public void setUsers(List<SelectItem> users) {
		this.users = users;
	}
	
	public void workGroupChange(ValueChangeEvent event) {
		mailList = null;
		resetSMS();
        if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
        	workGroupId = new Integer(event.getNewValue().toString());
        	((Notice)getTo()).getRecipient().setId(null);
            loadUsers();
        } else {
        	workGroupId = null;
            loadUsers();
        }
    }

	public void recipientChange(ValueChangeEvent event) {
		mailList = null;
		resetSMS();
		if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
        	((Notice)getTo()).getRecipient().setId(new Integer(""+event.getNewValue()));
        }
    }

	public List<SelectItem> getWorkgroups() throws ManagerBeanException {
		List<SelectItem> workgroups = new LinkedList<SelectItem>(); 
		IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class); 
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
		criteria.addOrder(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_DESCRIPTION));
		Iterator<ITransferObject> iter = workGroupBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			WorkGroup workGroup = (WorkGroup)iter.next();
			SelectItem item = new SelectItem(workGroup.getId(), workGroup.getDescription());
			workgroups.add(item);
		}
		return workgroups;
	}

    @SuppressWarnings("unchecked")
    public void loadUsers() {
    	users = new LinkedList<SelectItem>();
        try {
	    	if (workGroupId == null) {
	            IManagerBean managerBean = BeanManager.getManagerBean(User.class);
	            Criteria criteria = new Criteria();
	            criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.USER_AVAILABLE), true);
	            criteria.addOrder(managerBean.getFieldName(IConfigAlias.USER_NAME));
	            Iterator iterator = managerBean.getList(criteria).iterator();
	            while (iterator.hasNext()) {
	                User user = (User)iterator.next();
	                SelectItem item = new SelectItem(user.getId(), user.getName());
	                users.add(item);
	            }
	    	}
	    	else {
	            IManagerBean managerBean = BeanManager.getManagerBean(UserWorkGroup.class);
	            Criteria criteria = new Criteria();
	            criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workGroupId);
	            criteria.addOrder(managerBean.getFieldName(IConfigAlias.USER_WORK_GROUP_USER_NAME));
	            Iterator iterator = managerBean.getList(criteria).iterator();
	            while (iterator.hasNext()) {
	                UserWorkGroup userWorkGroup = (UserWorkGroup)iterator.next();
	                if (userWorkGroup.getUser().getAvailable()) {
		                SelectItem item = new SelectItem(userWorkGroup.getUser().getId(), userWorkGroup.getUser().getName());
		                users.add(item);
	                }
	            }
	    	}
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error loading users of workgroup with id= " + workGroupId.toString(), e);
        }
    }
    
	public Integer getWorkGroupId() {
		return workGroupId;
	}

	public void setWorkGroupId(Integer workGroupId) {
		this.workGroupId = workGroupId;
	}

	private void chargeMails() {
		AuthPrincipal user = UserUtils.getInstance().getPrincipal();
		String domain = user.getDomain();
		mailList = "";
		
		if (((Notice)getTo()).getRecipient() != null && ((Notice)getTo()).getRecipient().getId() != null) {
			//Se envia a un solo usuario.
			Integer usernameId = ((Notice)getTo()).getRecipient().getId();
			String username = getUserName(usernameId);
			String userEmail = getLdapUserMail(domain, username);
			if (userEmail != null) mailList = userEmail;
		}
		else {
			//Se envia a un grupo.
			String SEP = "";
			for (int i=0;i<users.size();i++) {
				Integer usernameId = new Integer(""+users.get(i).getValue());
				String username = getUserName(usernameId);
				String userEmail = getLdapUserMail(domain, username);
				if (userEmail != null) {
					mailList += SEP+userEmail;
					SEP = ", ";
				}
			}
		}
	}

	private void chargeSMS() {
		AuthPrincipal user = UserUtils.getInstance().getPrincipal();
		String domain = user.getDomain();
		resetSMS();
		if (((Notice)getTo()).getRecipient().getId() != null) {
			//Se envia a un solo usuario.
			Integer usernameId = ((Notice)getTo()).getRecipient().getId();
			String username = getUserName(usernameId);
			String userSMS = getLdapUserSMS(domain, username);
			if (userSMS != null) this.recipients.add(userSMS);
		}
		else {
			//Se envia a un grupo.
			for (int i=0;i<users.size();i++) {
				Integer usernameId = new Integer(""+users.get(i).getValue());
				String username = getUserName(usernameId);
				String userSMS = getLdapUserSMS(domain, username);
				if (userSMS != null) this.recipients.add(userSMS);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String getUserName(Integer usernameId) {
		try {
	        IManagerBean userBean = BeanManager.getManagerBean(User.class);
	        Criteria criteria = new Criteria();
	        criteria.addEqualExpression(userBean.getFieldName(IConfigAlias.USER_AVAILABLE), true);
	        criteria.addEqualExpression(userBean.getFieldName(IConfigAlias.USER_ID), usernameId);
	        Iterator iterator = userBean.getList(criteria).iterator();
	        if (iterator.hasNext()) {
	            User user = (User)iterator.next();
	            return user.getLogin();
	        }
		}
		catch (ManagerBeanException mbe) {}
		return null;
	}

	private String getLdapUserMail(String domain, String username) {
		DistinguishedName userDN = AonDN.getUserDN( domain, username );
		BasicLdap ldap = new BasicLdap();
		String email = null;
		if ( ldap.exists(userDN, USER) ) {
			email = "<"+username+"@"+domain+">";
			try {
				LdapSession session = ldap.getLdapSession();
				String filter = LdapSession.getObjectClass(USER);
				Entry userEntry = session.get(userDN.toString(), filter,USER_ALTERNATIVE_EMAIL, COMMONNAME, SURNAME);
				String alternativeEmail = null;
				String name = username;
				try {
					String cn = userEntry.getAsString(COMMONNAME);
					String sn = userEntry.getAsString(SURNAME);
					name = "" + cn + " " + sn + "";
					alternativeEmail = userEntry.getAsString(USER_ALTERNATIVE_EMAIL);
				}catch (NullPointerException npe) {}
				email = ""+name+" "+email+"";
				if (alternativeEmail != null) email = "" + email + ", "+ name +" <"+alternativeEmail+">";
			} catch (LdapException e) {
                LOGGER.log(Level.SEVERE, "Error obteniendo propiedades del usuario " + username, e);
			} finally {
				ldap.closeSession();
			}
		}
		if (email == null) {
			//Comprobamos si el dominio tiene algun dominio alternativo.
			DistinguishedName domainDN = AonDN.getDomainDN(domain);
			if ( ldap.exists(domainDN, DOMAIN) ) {
				//Si existe entonces buscamos al usuario en el nuevo dominio.
				try {
					LdapSession session = ldap.getLdapSession();
					String filter = LdapSession.getObjectClass(DOMAIN);
					Entry domainEntry = session.get(domainDN.toString(), filter, DOMAIN_MEMBER_ATTRIBUTE);
					List<Object> alternativeDomain = null;
					try {
						alternativeDomain = (List<Object>)domainEntry.get(DOMAIN_MEMBER_ATTRIBUTE);
						for (int i=0;i<alternativeDomain.size();i++) {
							String altdomain = ""+alternativeDomain.get(i);
							altdomain = altdomain.substring(3, altdomain.indexOf(","));
							DistinguishedName altuserDN = AonDN.getUserDN( altdomain, username );
							if ( ldap.exists(altuserDN, USER) ) {
								email = "<"+username+"@"+altdomain+">";
								try {
									LdapSession altsession = ldap.getLdapSession();
									String altfilter = LdapSession.getObjectClass(USER);
									Entry userEntry = altsession.get(altuserDN.toString(), altfilter, USER_ALTERNATIVE_EMAIL, COMMONNAME, SURNAME);
									String alternativeEmail = null;
									String name = username;
									try {
										String cn = userEntry.getAsString(COMMONNAME);
										String sn = userEntry.getAsString(SURNAME);
										name = "" + cn + " " + sn + "";
										alternativeEmail = userEntry.getAsString(USER_ALTERNATIVE_EMAIL);
									}catch (NullPointerException npe) {}
									email = ""+name+" "+email+"";
									if (alternativeEmail != null) email = "" + email + ", "+ name +" <"+alternativeEmail+">";
								} catch (LdapException e) {
					                LOGGER.log(Level.SEVERE, "Error obteniendo propiedades del usuario " + username, e);
								}
							}
						}
					}catch (NullPointerException npe) {}
				} catch (LdapException e) {
	                LOGGER.log(Level.SEVERE, "Error obteniendo propiedades del dominio " + domain, e);
				} finally {
					ldap.closeSession();
				}
			}
		}
		return email;
	}

	private String getLdapUserSMS(String domain, String username) {
		DistinguishedName userDN = AonDN.getUserDN( domain, username );
		BasicLdap ldap = new BasicLdap();
		String sms = null;
		if ( ldap.exists(userDN, USER) ) {
			try {
				LdapSession session = ldap.getLdapSession();
				String filter = LdapSession.getObjectClass(USER);
				Entry userEntry = session.get(userDN.toString(), filter,USER_CELLULAR_NUMBER, COMMONNAME, SURNAME);
				try {
					String cn = userEntry.getAsString(COMMONNAME);
					String sn = userEntry.getAsString(SURNAME);
					String name = "" + cn + " " + sn + "";
					sms = userEntry.getAsString(USER_CELLULAR_NUMBER);
					sms = ""+name+"-"+sms+"";
				}catch (NullPointerException npe) {}
			} catch (LdapException e) {
                LOGGER.log(Level.SEVERE, "Error obteniendo propiedades del usuario " + username, e);
			} finally {
				ldap.closeSession();
			}
		}
		return sms;
	}

	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		mailList = null;
		this.sendMail = sendMail;
	}

	public boolean isSendSMS() {
		return sendSMS;
	}

	public void setSendSMS(boolean sendSMS) {
		if (!sendSMS) resetSMS();
		this.sendSMS = sendSMS;
	}

	public String getMailList() {
		if (sendMail && mailList == null) chargeMails();
		else if (!sendMail) mailList = null;
		return mailList;
	}

	public void setMailList(String mailList) {
		this.mailList = mailList;
	}

	public void add2List(ActionEvent event) {
		if ( this.recipient != null && !this.recipient.equals( "" ) ) {
			this.recipients.add( this.recipient );
			this.recipient = null;
		}
	}

	public void removeFromList(ActionEvent event) {
		modify = true;
		this.recipients.remove( this.selected );
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public List<String> getRecipients() {
		if (sendSMS && this.recipients.size() <= 0 && !modify) chargeSMS();
		else if (!sendSMS) resetSMS();
		return this.recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

	public void resetSMS() {
		recipients = new ArrayList<String>();
		recipient = null;
		selected = -1;
		modify = false;
	}
}