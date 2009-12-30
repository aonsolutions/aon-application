package com.code.aon.ui.employee.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.config.User;
import com.code.aon.ui.config.controller.UserController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class ICEEmployeeController extends BasicController{
	
	private static final String USER_CONTROLLER_NAME = "user";
	
	private User user;
	
	private boolean newUser;
	
	private String login;
	
	private String passWord;
	
	private String confirm;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isNewUser() {
		return newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public String[] getRelations() {
		UserController userController = (UserController)AonUtil.getController(USER_CONTROLLER_NAME);
		return userController.getUserManager().getRelations();
	}
	
    public void setRelations(String[] relations) {
    	UserController userController = (UserController)AonUtil.getController(USER_CONTROLLER_NAME);
		userController.getUserManager().setRelations(relations);
    }

	public List<SelectItem> getProfiles() {
		UserController userController = (UserController)AonUtil.getController(USER_CONTROLLER_NAME);
		return userController.getUserManager().getProfiles();
	}
}