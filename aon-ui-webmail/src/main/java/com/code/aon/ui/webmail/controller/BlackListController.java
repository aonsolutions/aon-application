package com.code.aon.ui.webmail.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Contact;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonListEmail;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.enumeration.SpamReportType;
import com.code.aon.webmail.enumeration.SpamScoreType;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class BlackListController {

	private Properties props;
	
	private List<AonListEmail> whiteLst;

	private List<AonListEmail> blackLst;
	
	private SpamReportType spamReportType;
	
	private SpamScoreType spamScoreType;
	
	private String rewrite_1;
	
	private boolean addContactsToWhite;
	
	public SpamReportType getSpamReportType() {
		return spamReportType;
	}

	public void setSpamReportType(SpamReportType spamReportType) {
		this.spamReportType = spamReportType;
	}

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
	
	public boolean isBlackListConfigured(){
		WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
		MailAccount mailAccount = webMailController.getServer().getAccount();
		String filename = mailAccount.getBlackList();
		if (filename == null ||
				filename.trim().equals(""))
			return false;
        return (new File(filename)).exists();
		
	}
	
	private void load(){
		addContactsToWhite = false;
		WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
		MailAccount mailAccount = webMailController.getServer().getAccount();
		String filename = mailAccount.getBlackList();
		BufferedReader rd = null;
		try {
	        String patternStr = "\t";
	        Pattern pattern = Pattern.compile(patternStr);
	        rd = new BufferedReader(new FileReader(filename));
	        String line = null;
	        props = new Properties();
	        whiteLst = new ArrayList<AonListEmail>();
	        blackLst = new ArrayList<AonListEmail>();
	        while ((line = rd.readLine()) != null) {
	        	String[] a = pattern.split(line);
	        	if (WHITE.equalsIgnoreCase(a[0])&&
	        			!whiteLst.contains(a[1])){
        			whiteLst.add(new AonListEmail(a[1]));
	        	}else if (BLACK.equalsIgnoreCase(a[0])&&
	        			!blackLst.contains(a[1])){
        			blackLst.add(new AonListEmail(a[1]));
	        	}else{
	        		props.put(a[0], a[1]);
	        	}
	        }
		    try {
			    int score = new Integer(props.getProperty(SCORE)).intValue();
			    if (score>=15){
					setSpamScoreType(SpamScoreType.VERY_HIGH);
			    }else if (score>=10){
					setSpamScoreType(SpamScoreType.HIGH);
			    }else if (score>=5){
					setSpamScoreType(SpamScoreType.NORMAL);
			    }else if (score>=3){
					setSpamScoreType(SpamScoreType.LOW);
			    }else{
					setSpamScoreType(SpamScoreType.VERY_LOW);
			    }
			} catch (Exception e) {
				setSpamScoreType(SpamScoreType.NORMAL);
			}
		    try {
			    int report = new Integer(props.getProperty(REPORT)).intValue();
			    if (report==0){
					setSpamReportType(SpamReportType.NOT_ATTACHED);
			    }else{
					setSpamReportType(SpamReportType.ATTACHED);
			    }
			} catch (Exception e) {
				setSpamReportType(SpamReportType.ATTACHED);
			}
			setRewrite_1(props.getProperty(REWRITE_1));
		    sort(whiteLst,true);
		    sort(blackLst,true);
		} catch (IOException e) {
	    } finally {
	    	try {
				rd.close();
			} catch (IOException e) {
			}
	    }
	}
	
	private void save(){
		WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
		MailAccount mailAccount = webMailController.getServer().getAccount();
		String filename = mailAccount.getBlackList();
		BufferedWriter wr = null;
		try {
	        String patternStr = "\t";
	        wr = new BufferedWriter(new FileWriter(filename));
	        String line = null;
	        if (SpamScoreType.VERY_HIGH.equals(getSpamScoreType())){
	        	line = SCORE+"\t15";
	        }else if (SpamScoreType.HIGH.equals(getSpamScoreType())){
	        	line = SCORE+"\t10";
	        }else if (SpamScoreType.NORMAL.equals(getSpamScoreType())){
	        	line = SCORE+"\t5";
	        }else if (SpamScoreType.LOW.equals(getSpamScoreType())){
	        	line = SCORE+"\t3";
	        }else if (SpamScoreType.VERY_LOW.equals(getSpamScoreType())){
	        	line = SCORE+"\t1";
	        }
        	wr.write(line);
        	wr.newLine();
	        if (SpamReportType.NOT_ATTACHED.equals(getSpamReportType())){
	        	line = REPORT+"\t0";
	        }else if (SpamReportType.ATTACHED.equals(getSpamReportType())){
	        	line = REPORT+"\t1";
	        }
        	wr.write(line);
        	wr.newLine();
        	line = REWRITE_1+"\t"+getRewrite_1();
        	wr.write(line);
        	wr.newLine();
	        for (Enumeration keys = props.keys() ; keys.hasMoreElements() ;) {
	        	String key = (String)keys.nextElement();
	        	if (!key.equalsIgnoreCase(SCORE) &&
	        			!key.equalsIgnoreCase(REPORT) &&
	        			!key.equalsIgnoreCase(REWRITE_1)
	        			){
		        	line = key+"\t"+props.getProperty(key);
		        	wr.write(line);
		        	wr.newLine();
	        	}
	        }
	        wr.flush();
	        if (addContactsToWhite){
	        	addContactsToWhiteList();
	        }
	        Iterator<AonListEmail> iter = whiteLst.iterator();
	        AonListEmail ale;
	        while (iter.hasNext()){
	        	ale = iter.next();
	        	if (!ale.isSelected()){
		        	line = "whitelist_from\t"+ale.getEmail();
		        	wr.write(line);
		        	wr.newLine();
	        	}
	        }
	        wr.flush();
	        iter = blackLst.iterator();
	        while (iter.hasNext()){
	        	ale = iter.next();
	        	if (!ale.isSelected()){
		        	line = "blacklist_from\t"+ale.getEmail();
		        	wr.write(line);
		        	wr.newLine();
	        	}
	        }
	        wr.flush();
	    } catch (IOException e) {
	    } finally {
	    	try {
				wr.close();
			} catch (IOException e) {
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
		try{
			WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
			MailAccount account = wmc.getServer().getAccount();
			IManagerBean bean = BeanManager.getManagerBean(Contact.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IGroupWareAlias.CONTACT_USER_ID), account.getUser().getId());
			contacts = bean.getList(criteria);
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
	
    public void rowSelection(RowSelectorEvent e) {
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
	
	private static String SCORE = "required_score";
	private static String REPORT = "report_safe";
	private static String REWRITE_1 = "rewrite_header subject";
	private static String REWRITE_2 = "rewrite_header from";
	private static String REWRITE_3 = "rewrite_header to";
	private static String WHITE = "whitelist_from";
	private static String BLACK = "blacklist_from";

	private static int LIST_WHITE_TYPE = 0;
	private static int LIST_BLACK_TYPE = 1;
	

    //*************************************************************
    // NEW POPUP
    //*************************************************************
    
    private boolean showNewBlackPanelPopup;
    
	public boolean isShowNewPanelPopup() {
		return showNewBlackPanelPopup;
	}

    private String newEmail;
    
	public String getNewEmail() {
		return newEmail;
	}

	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}

	private int list_type;
	
	public void closeNewPanelPopup(ActionEvent event){
		this.showNewBlackPanelPopup = false;
	}
	
	public void openNewPanelPopup(ActionEvent event){
		this.newEmail = "";
		this.showNewBlackPanelPopup = true;
	}

	public void openNewBlackPanelPopup(ActionEvent event){
		list_type = LIST_BLACK_TYPE;
		openNewPanelPopup(event);
	}

	public void openNewWhitePanelPopup(ActionEvent event){
		list_type = LIST_WHITE_TYPE;
		openNewPanelPopup(event);
	}

	public void createNew(ActionEvent event){
		addEmail(newEmail, list_type);
		closeNewPanelPopup(event);
	}

    //*************************************************************
    // NEW POPUP END
    //*************************************************************
	
    //*************************************************************
    // ADD EMAIL FROM MESSAGE
    //*************************************************************

	public boolean isWhiteListEmail(){
		if (!isBlackListConfigured())
			return false;
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	String aonFolderName = messageController.getMessage().getParent().getName();
    	if (AonFolder.SPAM_FOLDER_NAME.equalsIgnoreCase(aonFolderName))
    		return true;
		return false;
	}

	public boolean isBlackListEmail(){
		if (!isBlackListConfigured())
			return false;
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	String aonFolderName = messageController.getMessage().getParent().getName();
    	if (!AonFolder.SPAM_FOLDER_NAME.equalsIgnoreCase(aonFolderName) &&
    			!AonFolder.SENT_FOLDER_NAME.equalsIgnoreCase(aonFolderName))
    		return true;
		return false;
	}
	
	public void onAddWhiteMessageFrom(ActionEvent event){
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	try {
			String email = messageController.getMessage().getSenderEmail();
			load();
			addEmail(email, LIST_WHITE_TYPE);
			save();
		} catch (WebmailException e) {
		}
	}

	public void onAddBlackMessageFrom(ActionEvent event){
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	try {
			String email = messageController.getMessage().getSenderEmail();
			load();
			addEmail(email, LIST_BLACK_TYPE);
			save();
		} catch (WebmailException e) {
		}
	}

    //*************************************************************
    // ADD EMAIL FROM MESSAGE END
    //*************************************************************
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getReportTypes() throws ManagerBeanException{
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> types = new LinkedList<SelectItem>();
		for(SpamReportType type_ : SpamReportType.values()){
			SelectItem item = new SelectItem(type_, type_.getName(locale));
			types.add(item);
		}
		return types;
	}

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
		BlackListController b = new BlackListController();
		b.load();
		b.save();
	}
}
