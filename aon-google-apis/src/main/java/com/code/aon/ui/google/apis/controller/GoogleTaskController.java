package com.code.aon.ui.google.apis.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import com.code.aon.google.apis.TaskUtils;
import com.code.aon.google.apis.sessionInfo.SessionInfo;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;
import com.google.api.services.drive.Drive;
import com.google.api.services.tasks.Tasks;

public class GoogleTaskController {
	
	private String beanName;
	
	public boolean google= isGoogle();
	
	public Tasks getClientSession(){
		String domain= AonUtil.getDomainName();
		String username=AonUtil.getAuthPrincipal().getShortName();
		Tasks tasks= SessionInfo.table.get(domain).getUsers().get(username).getTasks();
		System.out.println("TASKS:   --- ---- -"+tasks);
		return tasks;
	}
	
	public boolean isGoogle(){
		if(getClientSession()!=null) return true;
		else return false;
	}
	
	public void sync(ActionEvent event) throws SQLException, AonConnectionException, IOException{
		
		TaskUtils.synchronize(getClientSession());
		
	}
	
	public String getBeanName() {
		return beanName;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

}
