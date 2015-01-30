package com.esferalia.aon.gwt.template.client;

import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("gwt_template")
public interface ITemplate extends RemoteService{

	public void initAux();
	
	public TemplateList getTemplates();
	
	public TemplateInfo newTemplate(TemplateInfo ti );
	
	public void editTemplate(TemplateInfo ti);
	
	public void deleteTemplate(TemplateInfo ti);

}
