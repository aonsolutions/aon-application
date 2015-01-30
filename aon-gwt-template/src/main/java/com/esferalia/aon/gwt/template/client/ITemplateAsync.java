package com.esferalia.aon.gwt.template.client;

import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface ITemplateAsync {

	void newTemplate(TemplateInfo ti, AsyncCallback<TemplateInfo> callback);

	void getTemplates(AsyncCallback<TemplateList> callback);

	void initAux(AsyncCallback<Void> callback);

	void editTemplate(TemplateInfo ti, AsyncCallback<Void> callback);

	void deleteTemplate(TemplateInfo ti, AsyncCallback<Void> callback);

}
