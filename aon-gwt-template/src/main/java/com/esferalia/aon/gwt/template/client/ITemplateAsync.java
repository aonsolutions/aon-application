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
import com.esferalia.aon.gwt.template.shared.WorkPlace;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface ITemplateAsync {

	void newTemplate(Domain domain, TemplateInfo ti, AsyncCallback<TemplateInfo> callback);

	void getTemplates(Domain domain, AsyncCallback<TemplateList> callback);

	void initAux(AsyncCallback<Void> callback);

	void editTemplate(Domain domain, TemplateInfo ti, AsyncCallback<TemplateInfo> callback);

	void deleteTemplate(Domain domain, TemplateInfo ti, AsyncCallback<Void> callback);


	void searchTypeTemplate(String searchStr, Vector<TemplateInfo> templates,
			AsyncCallback<Vector<TemplateInfo>> callback);

	void searchNameTemplate(String searchStr, Vector<TemplateInfo> templates,
			AsyncCallback<Vector<TemplateInfo>> callback);

	void insertStock(Domain domain, AsyncCallback<Error> callback);

	void getWarehouses(Domain domain, AsyncCallback<Vector<Warehouse>> callback);

	void getSeries(Domain domain, String warehouse, AsyncCallback<Vector<Series>> callback);

	void executeExcel(Domain domain, TemplateInfo ti, ImportType importType, Boolean ignoreInactiveClient,
			Integer inventory, String warehouse1, String warehouse2, String series, String comments, Boolean istransfer,
			Integer number, AsyncCallback<Integer> callback);

	void insertProduct(Domain domain, String value, AsyncCallback<Error> callback);

	void insertFee(Domain domain, AsyncCallback<Error> callback);

	void insertTransferStock(Domain domain, AsyncCallback<Error> callback);

	void getWorkplaces(Domain domain, AsyncCallback<Vector<WorkPlace>> callback);

	void getDepartments(Domain domain, String workplace,
			AsyncCallback<LinkedList<Department>> callback);

	void insertProposal(Domain domain, Integer proposal, Integer workplace, AsyncCallback<Error> callback);

	void getSeries(Domain domain, AsyncCallback<Vector<Series>> callback);

	void getWarehousesToConsumption(Domain domain, AsyncCallback<Vector<Warehouse>> callback);

	void getHotelsToConsumption(Domain domain, AsyncCallback<List<Hotel>> callback);

	void getWarehousesToConsumption(Domain domain, Integer workplaceId,
			AsyncCallback<Vector<Warehouse>> callback);

	void getProductCategories(Domain domain, AsyncCallback<List<ProductCategory>> callback);

	void generateConsumptionExcel(Domain domain, Vector<Warehouse> warehouses, String type, Boolean onlyNegative,
			Boolean detail, Integer size, Boolean packaged, AsyncCallback<String> callback);

	void excelRowNumber(AsyncCallback<Integer> callback);

	void executeExcelEcommerce(Domain domain, Ecommerce ecommerce, Seller seller, String type, Tag tag,
			AsyncCallback<Error> callback);

	void getSellerList(Domain domain, AsyncCallback<List<Seller>> callback);

	void getProductRoles(Domain domain, AsyncCallback<LinkedList<String>> callback);

	void getTypeList(Domain domain, AsyncCallback<LinkedList<String>> callback);

	void print(String text, AsyncCallback<Void> callback);

	void generateConsumptionExcel(Domain domain, Vector<Warehouse> warehouses, String type, Boolean onlyNegative,
			Boolean detail, Integer size, Date startDate, Date endDate, Boolean packaged,
			AsyncCallback<String> callback);

}
