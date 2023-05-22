package com.code.aon.ui.google.apis.controller;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import jakarta.servlet.http.HttpSession;

import com.code.aon.AonVersion;
import com.code.aon.google.apis.TaskUtils;
import com.code.aon.oauth2.sessionInfo.SessionInfo;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ui.util.AonUtil;
import com.google.api.services.tasks.Tasks;

public class GoogleTaskController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String beanName;
	
	public boolean google= isGoogle();
	
	public Tasks getClientSession(){
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Object session=((HttpSession) ec.getSession(false)).getAttribute("Oauth2callback.email");
		String email= (String) session;
		
		Tasks tasks=null;
		String domain= AonUtil.getDomainName();
		String username=AonUtil.getAuthPrincipal().getShortName();
		if (SessionInfo.table.containsKey(domain) && SessionInfo.table.get(domain).getUsers().containsKey(username)){
			tasks= SessionInfo.table.get(domain).getUsers().get(username).getGoogleUsers().get(email).getTasks();
		}
		return tasks;
	}
	
	public boolean isGoogle(){
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Object session=((HttpSession) ec.getSession(false)).getAttribute("isGoogle");
		
		/*HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
		Object session = (HttpSession) request.getSession().getAttribute("isGoogle");*/
		if (session == null) return false;
		return (Boolean) session;
		
		/*if(getClientSession()!=null) return true;
		else return false;*/
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
