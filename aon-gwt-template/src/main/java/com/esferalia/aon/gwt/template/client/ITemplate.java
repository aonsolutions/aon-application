package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("gwt_template")
public interface ITemplate extends RemoteService{

	public void initAux();
	
	public TemplateList getTemplates();
	
	public TemplateInfo newTemplate(TemplateInfo ti );
	
	public TemplateInfo editTemplate(TemplateInfo ti);
	
	public void deleteTemplate(TemplateInfo ti);
	
	public com.esferalia.aon.gwt.template.shared.Error insertProducts(TemplateInfo ti);

	public Vector<TemplateInfo> searchTypeTemplate(String searchStr, Vector<TemplateInfo> templates);
	
	public Vector<TemplateInfo> searchNameTemplate(String searchStr, Vector<TemplateInfo> templates);
	
	public Vector<Warehouse> getWarehouses();
	
	public Vector<String> getSeries();
	
	public Error insertStock();
	
	public Integer executeExcel(TemplateInfo ti, String warehouse, String series, String comments);

	public Error insertProduct();
	
	public Integer executeExcel2(TemplateInfo ti);

}
