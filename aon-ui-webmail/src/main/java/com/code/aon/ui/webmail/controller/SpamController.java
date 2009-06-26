package com.code.aon.ui.webmail.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Contact;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonListEmail;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.enumeration.SpamScoreType;

public class SpamController extends BasicLdap implements WebMailConstants {

	private static final Logger LOGGER = Logger.getLogger(SpamController.class.getName());
	
	private static final String AMAVIS_ACCOUNT_OBJECT_CLASS = "amavisAccount";

	private static final String WHITE_LIST = "amavisWhitelistSender";
	
	private static final String BLACK_LIST = "amavisBlacklistSender";
	
	private static final String SUBJECT_TAG = "amavisSpamSubjectTag";

	private static final String SPAM_LEVEL = "amavisSpamTagLevel";
	
	private List<AonListEmail> whiteLst;

	private List<AonListEmail> blackLst;
	
	private SpamScoreType spamScoreType;
	
	private String rewrite_1;
	
	private boolean addContactsToWhite;
	
	private boolean spamEnabled;
	
	public SpamScoreType getSpamScoreType() {
		return spamScoreType;
	}

	public void setSpamScoreType(SpamScoreType spamScoreType) {
		this.spamScoreType = spamScoreType;
	}

	public String getRewrite_1() {
		return rewrite_1;
	}

	public void setRewrite_1(String rewrite_1) {
		this.rewrite_1 = rewrite_1;
	}

	public List<AonListEmail> getBlackLst() throws ManagerBeanException{
        return blackLst;
    }

    public List<AonListEmail> getWhiteLst() throws ManagerBeanException{
        return whiteLst;
    }
    
	public boolean isAddContactsToWhite() {
		return addContactsToWhite;
	}

	public void setAddContactsToWhite(boolean addContactsToWhite) {
		this.addContactsToWhite = addContactsToWhite;
	}

	public void onLoad(ActionEvent event) {
		load();
	}

	public void onSave(ActionEvent event) {
		save();
		load();
	}
	
	private DistinguishedName getUserDN() {
		AuthPrincipal principal = Utils.getAuthPrincipal();
		return AonDN.getUserDN( principal.getDomain(), principal.getShortName() );
	}
	
	public void updateSpamEnabled( MailAccount mailAccount ) {
		this.spamEnabled = false;
		if ( mailAccount.isDefault() ) {
			DistinguishedName userDN = getUserDN();
			this.spamEnabled = exists(userDN, AMAVIS_ACCOUNT_OBJECT_CLASS);
		}
	}
	
	public boolean isSpamEnabled() {
		return this.spamEnabled;
	}
	
	private Entry getSpamEntry( DistinguishedName dn ) {
		Entry entry = null;
		try {
			LdapSession session = getLdapSession();
			String filter = LdapSession.getObjectClass(AMAVIS_ACCOUNT_OBJECT_CLASS);
			entry = session.get(dn.toString(), filter, WHITE_LIST, BLACK_LIST, SUBJECT_TAG, SPAM_LEVEL );
		} catch ( LdapException e ) {
			AonUtil.addErrorMessage( "Error getting spam information" );
			throw new AbortProcessingException( e.getMessage(), e );
		} finally {
			closeSession();
		}		
		return entry;
	}
	
	private void load(){
		addContactsToWhite = false;
		DistinguishedName userDN = getUserDN();
		Entry entry = getSpamEntry( userDN );
		if ( entry != null ) {
			if ( entry.containsKey(SUBJECT_TAG) ) {
				this.rewrite_1 = entry.getAsString(SUBJECT_TAG);
			} else {
				this.rewrite_1 = null;
			}
			SpamScoreType score = null;
			if ( entry.containsKey(SPAM_LEVEL) ) {
 				String levelString = entry.getAsString(SPAM_LEVEL);
				if ( NumberUtils.isNumber(levelString) ) {
					double value = Double.valueOf(entry.getAsString(SPAM_LEVEL));
					score = SpamScoreType.get(value);
				}
			}
			setSpamScoreType( score != null ? score : SpamScoreType.NORMAL );
	        whiteLst = new ArrayList<AonListEmail>();
			if ( entry.containsKey(WHITE_LIST) ) {
				for( Object o : entry.get(WHITE_LIST) ) {
					whiteLst.add(new AonListEmail(o.toString()));	
				}
			}
	        blackLst = new ArrayList<AonListEmail>();
			if ( entry.containsKey(BLACK_LIST) ) {
				for( Object o : entry.get(BLACK_LIST) ) {
					blackLst.add(new AonListEmail(o.toString()));	
				}					
			}
		}
	}
	
	private List<Object> getSaveList( List<AonListEmail> emailList ) {
		List<Object> list = new ArrayList<Object>();
		for( AonListEmail email : emailList ) {
			if (! email.isSelected() ) {
				list.add( email.getEmail() );				
			}
		}
		return list;
	}
	
	private void save() {
		DistinguishedName userDN = getUserDN();		
		Entry entry = getSpamEntry( userDN );
		if ( entry != null ) {
			try {
				LdapSession session = getLdapSession();
				session.updateAttribute( entry, SUBJECT_TAG, StringUtils.trimToNull(this.rewrite_1) );
				String spamLevelValue = String.valueOf( getSpamScoreType().getValue() );
				session.updateAttribute( entry, SPAM_LEVEL, spamLevelValue);
				if ( isAddContactsToWhite() ) {
					addContactsToWhiteList();
				}
				session.updateAttribute( entry, WHITE_LIST, getSaveList(this.whiteLst) );
				session.updateAttribute( entry, BLACK_LIST, getSaveList(this.blackLst) );
			} catch ( LdapException e ) {
				AonUtil.addErrorMessage( "Error updating spam information" );
				throw new AbortProcessingException( e.getMessage(), e );
			} finally {
				closeSession();
			}					
		}		
	}
	
	protected void sort(List l, final boolean ascending) {
		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2){
				String s1 = ((AonListEmail) o1).getEmail();
				String s2 = ((AonListEmail) o2).getEmail();
				int a = s1.compareToIgnoreCase(s2);
				int d = s2.compareToIgnoreCase(s1);
				return ascending ? a : d ;
			}
		};
		if (l != null) {
			Collections.sort(l, comparator);
		}
	}	

	private void addContactsToWhiteList(){
	    List contacts = null;
		try {
			IManagerBean bean = AonUtil.getController(BEAN_CONTACT).getManagerBean();
			contacts = bean.getList(null);
		}catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		Iterator iterContacts = contacts.iterator();
		AonListEmail aonListEmail;
		while (iterContacts.hasNext()){
			String contact_email = ((Contact)iterContacts.next()).getEmail();
			aonListEmail = contains(whiteLst,contact_email);
			if (aonListEmail!=null){
				aonListEmail.setSelected(false);
			}else{
				addEmail(contact_email, LIST_WHITE_TYPE);
			}
		}
	}
	
	private void addEmail(String email, int _type){
		if (!email.equals("")){
			if (_type==LIST_WHITE_TYPE){
				if (contains(whiteLst,email)==null){
					AonListEmail ale = new AonListEmail(email);
					ale.setAdded(true);
		    		whiteLst.add(ale);			
				}
			}else if (_type==LIST_BLACK_TYPE){
				if (contains(blackLst,email)==null){
					AonListEmail ale = new AonListEmail(email);
					ale.setAdded(true);
					blackLst.add(ale);			
				}
			}
		}
	}

	private AonListEmail contains(List<AonListEmail> l, String s){
		Iterator<AonListEmail> iter = l.iterator();
		AonListEmail aonListEmail = null;
		while (iter.hasNext()){
			aonListEmail = iter.next();
			if (s.equalsIgnoreCase(aonListEmail.getEmail()))
				return aonListEmail;
		}
		return null;
	}

	private static int LIST_WHITE_TYPE = 0;
	private static int LIST_BLACK_TYPE = 1;
	

    //*************************************************************
    // NEW POPUP
    //*************************************************************
    
    private String newEmail;
    
	public String getNewEmail() {
		return newEmail;
	}

	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}

	private boolean showNewSpamAddressWindow;
	
	private int list_type;

	public boolean isShowNewSpamAddressWindow() {
		return showNewSpamAddressWindow;
	}

	public void setShowNewSpamAddressWindow(boolean showNewSpamAddressWindow) {
		this.showNewSpamAddressWindow = showNewSpamAddressWindow;
	}

	public void openNewBlackPanelPopup(ActionEvent event){
		this.newEmail = "";
		list_type = LIST_BLACK_TYPE;
		setShowNewSpamAddressWindow(true);
	}

	public void openNewWhitePanelPopup(ActionEvent event){
		this.newEmail = "";
		list_type = LIST_WHITE_TYPE;
		setShowNewSpamAddressWindow(true);
	}

	public void createNew(ActionEvent event){
		addEmail(newEmail, list_type);
	}

	public boolean isBlackList(){
		return (list_type==LIST_BLACK_TYPE); 
	}
	
    //*************************************************************
    // NEW POPUP END
    //*************************************************************
	
    //*************************************************************
    // ADD EMAIL FROM MESSAGE
    //*************************************************************

	public boolean isWhiteListEmail(){
		if (! isSpamEnabled() ) {
			return false;
		}
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(BEAN_MESSAGE);
    	String aonFolderName = messageController.getMessage().getParent().getName();
    	if (AonFolder.SPAM_FOLDER_NAME.equalsIgnoreCase(aonFolderName)) {
    		return true;
    	}
		return false;
	}

	public boolean isBlackListEmail(){
		if (!isSpamEnabled() ) {
			return false;
		}
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(BEAN_MESSAGE);
    	String aonFolderName = messageController.getMessage().getParent().getName();
    	if (!AonFolder.SPAM_FOLDER_NAME.equalsIgnoreCase(aonFolderName) &&
    			!AonFolder.SENT_FOLDER_NAME.equalsIgnoreCase(aonFolderName)) {
    		return true;
    	}
		return false;
	}
	
	public void onAddWhiteMessageFrom(ActionEvent event){
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(BEAN_MESSAGE);
    	try {
			String email = messageController.getMessage().getSenderEmail();
			load();
			addEmail(email, LIST_WHITE_TYPE);
			save();
		} catch (WebmailException e) {
			LOGGER.severe( e.getMessage() );
		}
	}

	public void onAddBlackMessageFrom(ActionEvent event){
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(BEAN_MESSAGE);
    	try {
			String email = messageController.getMessage().getSenderEmail();
			load();
			addEmail(email, LIST_BLACK_TYPE);
			save();
		} catch (WebmailException e) {
			LOGGER.severe( e.getMessage() );
		}
	}

    //*************************************************************
    // ADD EMAIL FROM MESSAGE END
    //*************************************************************

	@SuppressWarnings("unchecked")
	public List<SelectItem> getScoreTypes() throws ManagerBeanException{
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> types = new LinkedList<SelectItem>();
		for(SpamScoreType type_ : SpamScoreType.values()){
			SelectItem item = new SelectItem(type_, type_.getName(locale));
			types.add(item);
		}
		return types;
	}

	public static void main(String[] args) {
		SpamController b = new SpamController();
		b.load();
		b.save();
	}
}
