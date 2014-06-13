package com.code.aon.ui.google.apis.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.code.aon.google.apis.TaskUtils;
import com.code.aon.pool.AonConnectionException;
import com.google.api.services.drive.Drive;
import com.google.api.services.tasks.Tasks;

public class GoogleTaskController {
	
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
	
	public void sync() throws SQLException, AonConnectionException, IOException{
		
		TaskUtils.synchronize(getClientSession());
		
	}

}
