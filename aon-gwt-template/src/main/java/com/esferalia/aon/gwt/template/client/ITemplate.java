package com.esferalia.aon.gwt.template.client;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Hotel;
import com.esferalia.aon.gwt.template.shared.ImportType;
import com.esferalia.aon.gwt.template.shared.ProductCategory;
import com.esferalia.aon.gwt.template.shared.Seller;
import com.esferalia.aon.gwt.template.shared.Series;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("gwt_template")
public interface ITemplate extends RemoteService{
	
	public LinkedList<TemplateInfo> getTemplates(Domain domain, User user);
	
	public TemplateInfo newTemplate(Domain domain, User user, TemplateInfo ti );
	
	public TemplateInfo editTemplate(Domain domain, User user, TemplateInfo ti);
	
	public void deleteTemplate(Domain domain, User user, TemplateInfo ti);
	
	public LinkedList<TemplateInfo> searchTypeTemplate(String searchStr, LinkedList<TemplateInfo> templates);
	
	public LinkedList<TemplateInfo> searchNameTemplate(String searchStr, LinkedList<TemplateInfo> templates);
	
	public LinkedList<Warehouse> getWarehouses(Domain domain, User user);
	
	public LinkedList<Warehouse> getWarehousesToConsumption(Domain domain, User user);
	
	public LinkedList<Hotel> getHotelsToConsumption(Domain domain, User user);
	
	public LinkedList<Hotel> getWorkplacesToConsumption(Domain domain, User user);
	
	public LinkedList<Series> getSeries(Domain domain, User user, String warehouse);
	
	public LinkedList<Series> getSeries(Domain domain, User user);
	
	public Error insertStock(Domain domain, User user);
	
	public Error insertProduct(Domain domain, User user, String value);

	public Error insertDelivery(Domain domain, User user);
	
	public Error insertProjectCommercial(Domain domain, User user);
	
	public Error insertInvoices(Domain domain, User user, Integer index);

	public Error insertRegistries(Domain domain, User user, Integer index);
	
	public Error insertDiary(Domain domain, User user, Integer index);
	
	public Error insertPGC(Domain domain, User user, Integer index);
	
	public Error insertCustomerIban(Domain domain, User user);

	public Integer executeExcel(Domain domain, User user, TemplateInfo ti, ImportType importType, Boolean ignoreInactiveClient, 
			Integer inventory, String warehouse1,String warehouse2 , String series, String comments,Boolean istransfer ,Integer number);
	
	public Error insertFee(Domain domain, User user);
	
	public Error insertTransferStock(Domain domain, User user);

	public LinkedList<com.esferalia.aon.gwt.template.shared.WorkPlace> getWorkplaces(Domain domain, User user);
	
	public LinkedList<Department> getDepartments(Domain domain, User user, String workplace);
	
	public Error insertProposal(Domain domain, User user, Integer proposal, Integer workplace);
	
	public LinkedList<Warehouse> getWarehousesToConsumption(Domain domain, User user, Integer workplaceId);

	public LinkedList<ProductCategory> getProductCategories(Domain domain, User user);
		
	Error executeExcelEcommerce(Domain domain, User user, Ecommerce ecommerce,
			Seller seller, String type,
			Tag tag);
	
	public String generateConsumptionExcel(Domain domain, User user, LinkedList<Warehouse> warehouses, String type, Boolean onlyNegative, Boolean detail, 
			Integer size, Boolean packaged, Boolean withoutInv, Integer category, Boolean dif);
	
	public String generateConsumptionExcel(Domain domain, User user, LinkedList<Warehouse> warehouses, String type, Boolean onlyNegative, Boolean detail, 
			Integer size, Date startDate, Date endDate, Boolean packaged, Integer category, Boolean dif);
	
	public Integer excelRowNumber();
	
	public LinkedList<Seller> getSellerList(Domain domain, User user);
	
	public LinkedList<String> getProductRoles(Domain domain, User user);
	
	public LinkedList<String> getTypeList(Domain domain, User user);
	
	public void print(String text);
}
