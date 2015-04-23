package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Department;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.Series;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.esferalia.aon.gwt.template.shared.WorkPlace;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface ITemplateAsync {

	void newTemplate(TemplateInfo ti, AsyncCallback<TemplateInfo> callback);

	void getTemplates(AsyncCallback<TemplateList> callback);

	void initAux(AsyncCallback<Void> callback);

	void editTemplate(TemplateInfo ti, AsyncCallback<TemplateInfo> callback);

	void deleteTemplate(TemplateInfo ti, AsyncCallback<Void> callback);


	void searchTypeTemplate(String searchStr, Vector<TemplateInfo> templates,
			AsyncCallback<Vector<TemplateInfo>> callback);

	void searchNameTemplate(String searchStr, Vector<TemplateInfo> templates,
			AsyncCallback<Vector<TemplateInfo>> callback);

	void insertStock(AsyncCallback<Error> callback);

	void getWarehouses(AsyncCallback<Vector<Warehouse>> callback);

	void getSeries(String warehouse, AsyncCallback<Vector<Series>> callback);

	void executeExcel(Integer inventory, TemplateInfo ti, String warehouse,
			String warehouse2, String series, String comments,
			Boolean istransfer, Integer number, AsyncCallback<Integer> callback);

	void insertProduct(AsyncCallback<Error> callback);

	void executeExcel2(TemplateInfo ti, AsyncCallback<Integer> callback);

	void executeExcel3(TemplateInfo ti, AsyncCallback<Integer> callback);

	void insertFee(AsyncCallback<Error> callback);

	void insertTransferStock(AsyncCallback<Error> callback);

	void getWorkplaces(AsyncCallback<Vector<WorkPlace>> callback);

	void getDepartments(String workplace,
			AsyncCallback<Vector<Department>> callback);

	void executeExcelProposal(TemplateInfo templateInfo,
			AsyncCallback<Integer> callback);

	void insertProposal(Integer proposal, AsyncCallback<Error> callback);

	void getSeries(AsyncCallback<Vector<Series>> callback);

}
