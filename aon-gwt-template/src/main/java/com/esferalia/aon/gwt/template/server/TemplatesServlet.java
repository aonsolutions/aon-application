package com.esferalia.aon.gwt.template.server;


import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.template.client.ITemplate;
import com.esferalia.aon.gwt.template.jooq.DBConsults;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;



public class TemplatesServlet extends RemoteServiceServlet implements ITemplate{

	private static final long serialVersionUID = 6871016881549113129L;

	Integer domainId;
	
	void initFacesContext() {
		ServletContext context = getServletContext();
		HttpServletRequest request = getThreadLocalRequest();
		HttpServletResponse response = getThreadLocalResponse();
		AonServletUtils.initFacesContext(context, request, response);
	}

	void releaseFacesContext() {
		AonServletUtils.releaseFacesContext();
	}
	
	public void initAux(){
		try{
			initFacesContext();
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			domainId = ds.getDomainId();
		}
		finally{releaseFacesContext();}
	}
	
	public TemplatesServlet() {
		//initAux();
	}
	
	public TemplateList getTemplates(){
		String domain = AonUtil.getDomainName();
		TemplateList tl = null;
		try {
			tl = DBConsults.getTemplates(domain, domainId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tl;
	}
	
	public TemplateInfo newTemplate(TemplateInfo ti ){
		String domain = AonUtil.getDomainName();
		byte[] b = Utils.newXmlFile(ti);
		try {
			Integer id = DBConsults.insertTemplate(domain, ti, b,domainId);
			ti.setId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ti;
	}
	
	public void editTemplate(TemplateInfo ti){
		String domain = AonUtil.getDomainName();
		byte[] b = Utils.newXmlFile(ti);
		try {
			DBConsults.updateTemplate(domain, ti, domainId, b);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteTemplate(TemplateInfo ti){
		String domain = AonUtil.getDomainName();
		try {
			DBConsults.removeTemplate(domain, ti.getId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
