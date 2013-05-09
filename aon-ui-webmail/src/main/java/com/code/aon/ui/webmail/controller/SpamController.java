package com.code.aon.ui.webmail.controller;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.bean.AonListEmail;
import com.code.aon.webmail.enumeration.SpamScoreType;

public class SpamController extends BasicController implements IWebMailConstants {

	public SpamScoreType getSpamScoreType() {
		return SpamScoreType.NORMAL;
	}

	public void setSpamScoreType(SpamScoreType spamScoreType) {
	}

	public String getRewrite_1() {
		return null;
	}

	public void setRewrite_1(String rewrite_1) {
	}

	public List<AonListEmail> getBlackLst() throws ManagerBeanException{
        return Collections.emptyList();
    }

    public List<AonListEmail> getWhiteLst() throws ManagerBeanException{
    	return Collections.emptyList();
    }
    
	public boolean isAddContactsToWhite() {
		return false;
	}

	public void setAddContactsToWhite(boolean addContactsToWhite) {
	}

	public void onLoad(ActionEvent event) {
	}

	public void onSave(ActionEvent event) {
	}
	
	public void updateSpamEnabled( IMailAccount mailAccount ) {
	}
	
	public boolean isSpamEnabled() {
		return false;
	}

	public String getNewEmail() {
		return null;
	}

	public void setNewEmail(String newEmail) {
	}

	public boolean isShowNewSpamAddressWindow() {
		return false;
	}

	public void setShowNewSpamAddressWindow(boolean showNewSpamAddressWindow) {
	}

	public void openNewBlackPanelPopup(ActionEvent event){
	}

	public void openNewWhitePanelPopup(ActionEvent event){
	}

	public void createNew(ActionEvent event){
	}

	public boolean isBlackList(){
		return false; 
	}
	
    //*************************************************************
    // NEW POPUP END
    //*************************************************************
	
    //*************************************************************
    // ADD EMAIL FROM MESSAGE
    //*************************************************************

	public boolean isWhiteListEmail(){
		return false;
	}

	public boolean isBlackListEmail(){
		return false;
	}
	
	public void onAddWhiteMessageFrom(ActionEvent event){
	}

	public void onAddBlackMessageFrom(ActionEvent event){
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

}