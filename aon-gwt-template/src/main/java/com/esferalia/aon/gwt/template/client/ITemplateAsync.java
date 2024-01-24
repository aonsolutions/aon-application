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
import com.esferalia.aon.gwt.template.shared.WorkPlace;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface ITemplateAsync {

	void newTemplate(Domain domain, User user, TemplateInfo ti, AsyncCallback<TemplateInfo> callback);

	void getTemplates(Domain domain, User user, AsyncCallback<List<TemplateInfo>> callback);

	void editTemplate(Domain domain, User user, TemplateInfo ti, AsyncCallback<TemplateInfo> callback);

	void deleteTemplate(Domain domain, User user, TemplateInfo ti, AsyncCallback<Void> callback);

	void searchTypeTemplate(String searchStr, List<TemplateInfo> templates, AsyncCallback<List<TemplateInfo>> callback);

	void searchNameTemplate(String searchStr, List<TemplateInfo> templates, AsyncCallback<List<TemplateInfo>> callback);
	
	void insertStock(Domain domain, User user, AsyncCallback<Error> callback);

	void getWarehouses(Domain domain, User user, AsyncCallback<List<Warehouse>> callback);

	void getSeries(Domain domain, User user, String warehouse, AsyncCallback<List<Series>> callback);

	void executeExcel(Domain domain, User user, TemplateInfo ti, ImportType importType, Boolean ignoreInactiveClient,
			Integer inventory, String warehouse1, String warehouse2, String series, String comments, Boolean istransfer,
			Integer number, AsyncCallback<Integer> callback);

	void insertProduct(Domain domain, User user, String value, AsyncCallback<Error> callback);
	
	void insertDelivery(Domain domain, User user, AsyncCallback<Error> callback);

	void insertProjectCommercial(Domain domain, User user, AsyncCallback<Error> callback);
	
	void insertInvoices(Domain domain, User user, Integer index, AsyncCallback<Error> callback);
	
	void insertRegistries(Domain domain, User user, Integer index, AsyncCallback<Error> callback);
	
	void insertDiary(Domain domain, User user, Integer index, AsyncCallback<Error> callback);
	
	void insertPGC(Domain domain, User user, Integer index, AsyncCallback<Error> callback);

	void insertCustomerIban(Domain domain, User user, AsyncCallback<Error> callback);

	void insertTransferStock(Domain domain, User user, AsyncCallback<Error> callback);

	void getWorkplaces(Domain domain, User user, AsyncCallback<List<WorkPlace>> callback);

	void getDepartments(Domain domain, User user, String workplace,
			AsyncCallback<List<Department>> callback);

	void insertProposal(Domain domain, User user, Integer proposal, Integer workplace, AsyncCallback<Error> callback);

	void getSeries(Domain domain, User user, AsyncCallback<List<Series>> callback);

	void getWarehousesToConsumption(Domain domain, User user, AsyncCallback<List<Warehouse>> callback);

	void getHotelsToConsumption(Domain domain, User user, AsyncCallback<List<Hotel>> callback);

	void getWorkplacesToConsumption(Domain domain, User user, AsyncCallback<List<Hotel>> callback);

	void getWarehousesToConsumption(Domain domain, User user, Integer workplaceId, AsyncCallback<List<Warehouse>> callback);

	void getProductCategories(Domain domain, User user, AsyncCallback<List<ProductCategory>> callback);

	void generateConsumptionExcel(Domain domain, User user, List<Warehouse> warehouses, String type, Boolean onlyNegative,
			Boolean detail, Integer size, Boolean packaged, Boolean withoutInv, Integer category, Boolean dif, AsyncCallback<String> callback);
	
	void excelRowNumber(Domain domain, User user, AsyncCallback<Integer> callback);

	void executeExcelEcommerce(Domain domain, User user, Ecommerce ecommerce, Seller seller, String type, Tag tag, AsyncCallback<Error> callback);

	void getSellerList(Domain domain, User user, AsyncCallback<List<Seller>> callback);

	void getProductRoles(Domain domain, User user, AsyncCallback<List<String>> callback);

	void getTypeList(Domain domain, User user, AsyncCallback<List<String>> callback);

	void print(String text, AsyncCallback<Void> callback);

	void generateConsumptionExcel(Domain domain, User user, List<Warehouse> warehouses, String type, Boolean onlyNegative,
			Boolean detail, Integer size, Date startDate, Date endDate, Boolean packaged, Integer category, Boolean dif,
			AsyncCallback<String> callback);
	
// TODO remove
//	void importFix(Domain domain, User user, AsyncCallback<Void> callback);
//	void importRegistryEmptyFix(Domain domain, User user, AsyncCallback<Void> callback);

	void insertInvoice(Domain domain, User user, InvoiceImportClass invoices, Integer index, AsyncCallback<Error> callback);
	void insertServalInvoice(Domain domain, User user, InvoiceImportClass invoices, Integer index, AsyncCallback<Error> callback);

	void executeInvoice(Domain domain, User user, String data, AsyncCallback<List<InvoiceImportClass>> callback);
	void executeServalInvoice(Domain domain, User user, String data, AsyncCallback<List<InvoiceImportClass>> callback);

	void executeRegistry(Domain domain, User user, String data,
			AsyncCallback<List<RegistryImportClass>> callback);

	void executePGC(Domain domain, User user, String data, AsyncCallback<List<AccountImportClass>> callback);

	void executeDiary(Domain domain, User user, String data,
			AsyncCallback<List<AccountEntryImportClass>> callback);

	void insertRegistry(Domain domain, User user, RegistryImportClass registry, Integer index,
			AsyncCallback<Error> callback);

	void insertDiary(Domain domain, User user, AccountEntryImportClass diary, Integer index,
			AsyncCallback<Error> callback);

	void insertPGC(Domain domain, User user, AccountImportClass pgc, Integer index,
			AsyncCallback<Error> callback);

}
