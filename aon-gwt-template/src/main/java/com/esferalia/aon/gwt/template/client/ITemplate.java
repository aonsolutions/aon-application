package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Series;
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
	

	public Vector<TemplateInfo> searchTypeTemplate(String searchStr, Vector<TemplateInfo> templates);
	
	public Vector<TemplateInfo> searchNameTemplate(String searchStr, Vector<TemplateInfo> templates);
	
	public Vector<Warehouse> getWarehouses();
	
	public Vector<Warehouse> getWarehouses(Integer domainId);
	
	public Vector<Series> getSeries(String warehouse);
	
	public Vector<Series> getSeries();
	
	public Error insertStock();
	
	public Integer executeExcel(Integer inventory,TemplateInfo ti, String warehouse,String warehouse2, String series, String comments, Boolean istransfer,Integer number);

	public Error insertProduct();
	
	public Integer executeExcel2(TemplateInfo ti);
	
	public Integer executeExcel3(TemplateInfo ti);
	
	public Error insertFee();
	
	public Error insertTransferStock();

	public Vector<com.esferalia.aon.gwt.template.shared.WorkPlace> getWorkplaces();
	
	public Vector<com.esferalia.aon.gwt.template.shared.Department> getDepartments(String workplace);
	
	public Integer executeExcelProposal(TemplateInfo templateInfo);
	
	public Error insertProposal(Integer proposal, Integer workplace);
}
