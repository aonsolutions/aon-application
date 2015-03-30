package com.esferalia.aon.gwt.template.client;

import java.util.Vector;

import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface ITemplateAsync {

	void newTemplate(TemplateInfo ti, AsyncCallback<TemplateInfo> callback);

	void getTemplates(AsyncCallback<TemplateList> callback);

	void initAux(AsyncCallback<Void> callback);

	void editTemplate(TemplateInfo ti, AsyncCallback<TemplateInfo> callback);

	void deleteTemplate(TemplateInfo ti, AsyncCallback<Void> callback);

	void insertProducts(TemplateInfo ti, AsyncCallback<Error> callback);

	void searchTypeTemplate(String searchStr, Vector<TemplateInfo> templates,
			AsyncCallback<Vector<TemplateInfo>> callback);

	void searchNameTemplate(String searchStr, Vector<TemplateInfo> templates,
			AsyncCallback<Vector<TemplateInfo>> callback);

	void insertStock(AsyncCallback<Error> callback);

	void getWarehouses(AsyncCallback<Vector<Warehouse>> callback);

	void getSeries(AsyncCallback<Vector<String>> callback);

	void executeExcel(TemplateInfo ti, String warehouse, String series,
			String comments, AsyncCallback<Integer> callback);

	void insertProduct(AsyncCallback<Error> callback);

	void executeExcel2(TemplateInfo ti, AsyncCallback<Integer> callback);

	void executeExcel3(TemplateInfo ti, AsyncCallback<Integer> callback);

	void insertFee(AsyncCallback<Error> callback);

}
