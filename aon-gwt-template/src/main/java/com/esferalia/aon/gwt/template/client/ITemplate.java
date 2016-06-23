package com.esferalia.aon.gwt.template.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Hotel;
import com.esferalia.aon.gwt.template.shared.ImportType;
import com.esferalia.aon.gwt.template.shared.ProductCategory;
import com.esferalia.aon.gwt.template.shared.Seller;
import com.esferalia.aon.gwt.template.shared.Series;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("gwt_template")
public interface ITemplate extends RemoteService{

	public void initAux();
	
	public TemplateList getTemplates(Domain domain);
	
	public TemplateInfo newTemplate(Domain domain, TemplateInfo ti );
	
	public TemplateInfo editTemplate(Domain domain, TemplateInfo ti);
	
	public void deleteTemplate(Domain domain, TemplateInfo ti);
	
	public Vector<TemplateInfo> searchTypeTemplate(String searchStr, Vector<TemplateInfo> templates);
	
	public Vector<TemplateInfo> searchNameTemplate(String searchStr, Vector<TemplateInfo> templates);
	
	public Vector<Warehouse> getWarehouses(Domain domain);
	
	public Vector<Warehouse> getWarehousesToConsumption(Domain domain);
	
	public List<Hotel> getHotelsToConsumption(Domain domain);
	
	public Vector<Series> getSeries(Domain domain, String warehouse);
	
	public Vector<Series> getSeries(Domain domain);
	
	public Error insertStock(Domain domain);
	
	public Error insertProduct(Domain domain, String value);
	
	public Integer executeExcel(Domain domain, TemplateInfo ti, ImportType importType, Boolean ignoreInactiveClient, 
			Integer inventory, String warehouse1,String warehouse2 , String series, String comments,Boolean istransfer ,Integer number);
	
	public Error insertFee(Domain domain);
	
	public Error insertTransferStock(Domain domain);

	public Vector<com.esferalia.aon.gwt.template.shared.WorkPlace> getWorkplaces(Domain domain);
	
	public LinkedList<Department> getDepartments(Domain domain, String workplace);
	
	public Error insertProposal(Domain domain, Integer proposal, Integer workplace);
	
	public Vector<Warehouse> getWarehousesToConsumption(Domain domain, Integer workplaceId);

	public List<ProductCategory> getProductCategories(Domain domain);
		
	Error executeExcelEcommerce(Domain domain, Ecommerce ecommerce,
			Seller seller, String type,
			Tag tag);
	
	public String generateConsumptionExcel(Domain domain,Vector<Warehouse> warehouses, String type, Boolean onlyNegative, Boolean detail, 
			Integer size, Boolean packaged);
	
	public String generateConsumptionExcel(Domain domain,Vector<Warehouse> warehouses, String type, Boolean onlyNegative, Boolean detail, 
			Integer size, Date startDate, Date endDate, Boolean packaged);
	
	public Integer excelRowNumber();
	
	public List<Seller> getSellerList(Domain domain);
	
	public LinkedList<String> getProductRoles(Domain domain);
	
	public LinkedList<String> getTypeList(Domain domain);
	
	public void print(String text);
}
