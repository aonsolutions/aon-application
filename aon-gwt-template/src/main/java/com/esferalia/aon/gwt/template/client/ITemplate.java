package com.esferalia.aon.gwt.template.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.template.shared.AccountEntryImportClass;
import com.esferalia.aon.gwt.template.shared.AccountImportClass;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Hotel;
import com.esferalia.aon.gwt.template.shared.ImportType;
import com.esferalia.aon.gwt.template.shared.InvoiceImportClass;
import com.esferalia.aon.gwt.template.shared.ProductCategory;
import com.esferalia.aon.gwt.template.shared.RegistryImportClass;
import com.esferalia.aon.gwt.template.shared.Series;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("gwt_template")
public interface ITemplate extends RemoteService{
	
	public List<TemplateInfo> getTemplates(Domain domain, User user);
	
	public TemplateInfo newTemplate(Domain domain, User user, TemplateInfo ti );
	
	public TemplateInfo editTemplate(Domain domain, User user, TemplateInfo ti);
	
	public void deleteTemplate(Domain domain, User user, TemplateInfo ti);
	
	public List<TemplateInfo> searchTypeTemplate(String searchStr, List<TemplateInfo> templates);
	
	public List<TemplateInfo> searchNameTemplate(String searchStr, List<TemplateInfo> templates);
	
	public List<Warehouse> getWarehouses(Domain domain, User user);
	
	public List<Warehouse> getWarehousesToConsumption(Domain domain, User user);
	
	public List<Hotel> getHotelsToConsumption(Domain domain, User user);
	
	public List<Hotel> getWorkplacesToConsumption(Domain domain, User user);
	
	public List<Series> getSeries(Domain domain, User user, String warehouse);
	
	public List<Series> getSeries(Domain domain, User user);
	
	public Error insertStock(Domain domain, User user);
	
	public Error insertProduct(Domain domain, User user, String value);

	public Error insertDelivery(Domain domain, User user);
	
	public Error insertProjectCommercial(Domain domain, User user);
	
	public Error insertInvoices(Domain domain, User user, Integer index);
	public Error insertInvoice(Domain domain, User user, InvoiceImportClass invoices, Integer index);
	public Error insertServalInvoice(Domain domain, User user, InvoiceImportClass invoices, Integer index);

	public Error insertRegistries(Domain domain, User user, Integer index);
	public Error insertRegistry(Domain domain, User user, RegistryImportClass registry, Integer index);
	
	public Error insertDiary(Domain domain, User user, Integer index);
	public Error insertDiary(Domain domain, User user, AccountEntryImportClass diary, Integer index);
	
	public Error insertPGC(Domain domain, User user, Integer index);
	public Error insertPGC(Domain domain, User user, AccountImportClass pgc, Integer index);
	
	public List<InvoiceImportClass> executeInvoice(Domain domain , User user, String data);
	public List<InvoiceImportClass> executeServalInvoice(Domain domain , User user, String data);
	
	public List<RegistryImportClass> executeRegistry(Domain domain , User user, String data);
	
	public List<AccountImportClass> executePGC(Domain domain , User user, String data);

	public List<AccountEntryImportClass> executeDiary(Domain domain , User user, String data);	

	public Integer executeExcel(Domain domain, User user, TemplateInfo ti, ImportType importType, Boolean ignoreInactiveClient, 
			Integer inventory, String warehouse1,String warehouse2 , String series, String comments,Boolean istransfer ,Integer number, String data);
	
	public Error insertTransferStock(Domain domain, User user);

	public List<Workplace> getWorkplaces(Domain domain, User user);
	
	public List<Department> getDepartments(Domain domain, User user, String workplace);
	
	public Error insertProposal(Domain domain, User user, Integer proposal, Integer workplace);
	
	public List<Warehouse> getWarehousesToConsumption(Domain domain, User user, Integer workplaceId);

	public List<ProductCategory> getProductCategories(Domain domain, User user);
		
	Error executeExcelEcommerce(Domain domain, User user, Ecommerce ecommerce,
			Seller seller, String type,
			Tag tag, String data);
	
	public String generateConsumptionExcel(Domain domain, User user, List<Warehouse> warehouses, String type, Boolean onlyNegative, Boolean detail, 
			Integer size, Boolean packaged, Boolean withoutInv, Integer category, Boolean dif);
	
	public String generateConsumptionExcel(Domain domain, User user, List<Warehouse> warehouses, String type, Boolean onlyNegative, Boolean detail, 
			Integer size, Date startDate, Date endDate, Boolean packaged, Integer category, Boolean dif);
	
	public Integer excelRowNumber(Domain domain, User user, String data);
	
	public List<Seller> getSellerList(Domain domain, User user);
	
	public List<String> getProductRoles(Domain domain, User user);
	
	public List<String> getTypeList(Domain domain, User user);
	
	public void print(String text);

}
