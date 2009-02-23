package com.code.aon.ui.groupware.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

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
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.sun.faces.util.MessageFactory;

public class NoticeController extends BasicController implements IAonObjectClasses, ILdapConstants {
	
	private static final Logger LOGGER = Logger.getLogger(NoticeController.class.getName());
	
	public static final Integer SELECT_ONE_VALUE = -1;

	private static final String USER_ALTERNATIVE_EMAIL = "mail";

	private static final String USER_CELLULAR_NUMBER = "mobile";
	
	private List<SelectItem> workGroups;
	
	private List<SelectItem> users;

	private boolean sendMail = false;
	
	private String mailList = "";

	private boolean sendSMS = false;

	private boolean modify = false;

	private List<String> recipients = new ArrayList<String>();

	private String recipient;
	
	private int selected = -1;

	public Integer getSelectOneValue() {
		return SELECT_ONE_VALUE;
	}

	public List<SelectItem> getUsers() {
		return users;
	}
	
	public int getUserCount() {
		return this.users.size();
	}

	public List<SelectItem> getWorkGroups() {
		return this.workGroups;
	}

	public int getWorkGroupCount() {
		return this.workGroups.size();
	}

	public boolean isRecipientSelectable() {
		WorkGroup wg = ((Notice) getTo()).getWorkGroup();
		return (wg != null) && (! SELECT_ONE_VALUE.equals(wg.getId()));
	}
	
	public void workGroupChange(ValueChangeEvent event) {
		mailList = null;
		resetSMS();
		Integer workGroupId = (Integer) event.getNewValue();
    	if ( ! SELECT_ONE_VALUE.equals(workGroupId) ) {
    		((Notice)getTo()).setRecipient(null);
        	loadUsers(workGroupId);	
        }
    }

	public void recipientChange(ValueChangeEvent event) {
		mailList = null;
		resetSMS();
    }

    public void resetUsers() {
    	users = new LinkedList<SelectItem>();
    }
	
    @SuppressWarnings("unchecked")
	private List<User> getUser(Integer id) throws ManagerBeanException {
        IManagerBean managerBean = BeanManager.getManagerBean(User.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.USER_ID), id);
        criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.USER_AVAILABLE), true);
        criteria.addOrder(managerBean.getFieldName(IConfigAlias.USER_NAME));
        return (List) managerBean.getList(criteria);
    }

    @SuppressWarnings("unchecked")
	private List<User> getAllUsers() throws ManagerBeanException {
        IManagerBean managerBean = BeanManager.getManagerBean(User.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.USER_AVAILABLE), true);
        criteria.addOrder(managerBean.getFieldName(IConfigAlias.USER_NAME));
        return (List) managerBean.getList(criteria);
    }
    
	private List<User> getWorkGroupUsers( Integer workGroupId ) throws ManagerBeanException {
        IManagerBean managerBean = BeanManager.getManagerBean(UserWorkGroup.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workGroupId);
        criteria.addOrder(managerBean.getFieldName(IConfigAlias.USER_WORK_GROUP_USER_NAME));
        List<User> result = new LinkedList<User>();
        for( ITransferObject to : managerBean.getList(criteria) ) {
            UserWorkGroup userWorkGroup = (UserWorkGroup) to;
            if (userWorkGroup.getUser().getAvailable()) {
            	result.add( userWorkGroup.getUser() );
            }	            	
        }
        return result;
    }
	
	private List<User> getSelectedUsers() throws ManagerBeanException {
		Notice notice = (Notice) getTo();
		User recipient = notice.getRecipient();
		if ( (recipient != null) && (recipient.getId() != null) ) {
			return getUser(recipient.getId());
		} else {
			WorkGroup wg = notice.getWorkGroup();
			if ( wg != null ) {
				if ( wg.getId() == null ) {
					return getAllUsers();
				} else if (! SELECT_ONE_VALUE.equals(wg.getId()) ) {
					return getWorkGroupUsers(wg.getId());
				}
			}
		}
		return Collections.emptyList();
	}
    
    @SuppressWarnings("unchecked")
    public void loadUsers( Integer workGroupId ) {
    	resetUsers();
        try {
        	List<User> list = (workGroupId != null) ? getWorkGroupUsers(workGroupId) : getAllUsers();
            for( User user : list ) {
                SelectItem item = new SelectItem(user, user.getName());
                users.add(item);
	    	}
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error loading users of workgroup with id= " + workGroupId, e);
        }
    }

    private boolean hasUsers( WorkGroup workGroup ) {
        try {
	        IManagerBean managerBean = BeanManager.getManagerBean(UserWorkGroup.class);
	        Criteria criteria = new Criteria();
	        criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workGroup.getId());
	        return managerBean.getCount(criteria) > 0;
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error counting users of the workgroup " + workGroup.getId(), e);
        }    	
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public void loadWorkGroups() {
        try {    	
			this.workGroups = new LinkedList<SelectItem>();
			IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class); 
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_STATUS), WorkGroupStatus.ACTIVE);
			criteria.addOrder(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_DESCRIPTION));
			Iterator<ITransferObject> iter = workGroupBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				WorkGroup workGroup = (WorkGroup)iter.next();
				if ( hasUsers(workGroup) ) {
					SelectItem item = new SelectItem(workGroup.getId(), workGroup.getDescription());
					workGroups.add(item);
				}
			}
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error loading workgroups", e);
        }
    }    
    
	private void chargeMails() {
		String domain = UserUtils.getInstance().getPrincipal().getDomain();
		mailList = "";

		String SEP = "";
		try {
			for( User user : getSelectedUsers() ) {
				String userEmail = getLdapUserMail(domain, user.getLogin());
				if (userEmail != null) {
					mailList += SEP+userEmail;
					SEP = ", ";
				}			
			}
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving emails", e);
        }
	}

	private void chargeSMS() {
		String domain = UserUtils.getInstance().getPrincipal().getDomain();
		resetSMS();
		try {
			for( User user : getSelectedUsers() ) {
				String userSMS = getLdapUserSMS(domain, user.getLogin());
				if (userSMS != null) {
					this.recipients.add(userSMS);
				}
			}
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving cellulars", e);
        }
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
				Entry userEntry = session.get(userDN.toString(), filter,USER_ALTERNATIVE_EMAIL, COMMON_NAME_ATTRIBUTE, SURNAME_ATTRIBUTE);
				String alternativeEmail = null;
				String name = username;
				try {
					String cn = userEntry.getAsString(COMMON_NAME_ATTRIBUTE);
					String sn = userEntry.getAsString(SURNAME_ATTRIBUTE);
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
					Entry domainEntry = session.get(domainDN.toString(), filter, MEMBER_ATTRIBUTE);
					List<Object> alternativeDomain = null;
					try {
						alternativeDomain = (List<Object>)domainEntry.get(MEMBER_ATTRIBUTE);
						for (int i=0;i<alternativeDomain.size();i++) {
							String altdomain = ""+alternativeDomain.get(i);
							altdomain = altdomain.substring(3, altdomain.indexOf(","));
							DistinguishedName altuserDN = AonDN.getUserDN( altdomain, username );
							if ( ldap.exists(altuserDN, USER) ) {
								email = "<"+username+"@"+altdomain+">";
								try {
									LdapSession altsession = ldap.getLdapSession();
									String altfilter = LdapSession.getObjectClass(USER);
									Entry userEntry = altsession.get(altuserDN.toString(), altfilter, USER_ALTERNATIVE_EMAIL, COMMON_NAME_ATTRIBUTE, SURNAME_ATTRIBUTE);
									String alternativeEmail = null;
									String name = username;
									try {
										String cn = userEntry.getAsString(COMMON_NAME_ATTRIBUTE);
										String sn = userEntry.getAsString(SURNAME_ATTRIBUTE);
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
				Entry userEntry = session.get(userDN.toString(), filter,USER_CELLULAR_NUMBER, COMMON_NAME_ATTRIBUTE, SURNAME_ATTRIBUTE);
				try {
					String cn = userEntry.getAsString(COMMON_NAME_ATTRIBUTE);
					String sn = userEntry.getAsString(SURNAME_ATTRIBUTE);
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
	
	public void workGroupCheck(FacesContext context, UIComponent component, Object value) {
		if (SELECT_ONE_VALUE.equals(value) ) {
			throw new ValidatorException( MessageFactory.getMessage(
				context, UIInput.REQUIRED_MESSAGE_ID, MessageFactory.getLabel(context, component)));
		}
	}
	
}