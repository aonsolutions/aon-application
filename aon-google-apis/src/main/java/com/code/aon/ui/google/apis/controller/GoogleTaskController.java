package com.code.aon.ui.google.apis.controller;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import com.code.aon.AonVersion;
import com.code.aon.google.apis.TaskUtils;
import com.code.aon.pool.AonConnectionException;
import com.google.api.services.drive.Drive;
import com.google.api.services.tasks.Tasks;

public class GoogleTaskController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String beanName;
	
	public boolean google= isGoogle();
	
	public Tasks getClientSession(){
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Object session=((HttpSession) ec.getSession(false)).getAttribute("Tasks");
		Tasks tasks=(Tasks)session;
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
