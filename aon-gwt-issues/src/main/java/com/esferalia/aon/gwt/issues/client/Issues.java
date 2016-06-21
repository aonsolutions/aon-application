package com.esferalia.aon.gwt.issues.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.IssueFilter;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.IronListElement;
import com.vaadin.polymer.paper.widget.PaperIconButton;

public class Issues implements EntryPoint {
	
	interface Binder extends UiBinder<Widget, Issues> {

	}
	private static final Binder binder = GWT.create(Binder.class);

	protected static String USER_NAME = "aibanez91";
	protected static String ORG_NAME = "aonPrueba";
	protected static String REPO_NAME = "repoPrueba6";
	protected static String ACCESS_TOKEN = "d8aa641723e106b5d7c2d79d3cad963e0eb6e92d";
	protected static String DESCRIPTION = "Repositorio de prueba para metodos de TEST";

	
	@UiField HTMLPanel content;
	@UiField PaperIconButton menuButton;
	@UiField PaperIconButton refreshButton;
	@UiField PaperIconButton addButton;
	@UiField HTMLPanel configurationPanel;
	@UiField DockLayoutPanel dockLayoutPanel;
	
	IssueFilter issueFilter;
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				IronListElement.SRC));
		
		Polymer.whenReady(o -> {
			startApplication();
			return null;
		});
	}
	
	private void startApplication() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css().ensureInjected();

		
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		content.add(new FilterPanel(this));
		issueFilter = new IssueFilter();
		getIssueList(issueFilter);
	}


	protected void getIssueList(IssueFilter filter) {
		Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME);
		i.getUserIssues(filter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				content.add(new IssueList(result.getData()));
			}
			
			@Override
			public void onFailure(Throwable arg0) {	}
		});
	}
	
	protected void updateIssueList(JsArray<JsIssue> array){
		IssueList issueList = (IssueList) content.getWidget(1);
		issueList.updateItems(array);
	}
	
	protected void updateIssueList(IssueFilter filter) {
		Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME);
		i.getUserIssues(filter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				JsArray<JsIssue> array = JavaScriptObject.createArray().cast();
				if(filter.getTitle() != null && !filter.getTitle().isEmpty()){
					for(Integer i = 0; i < result.getData().length(); i++){
						if(result.getData().get(i).getTitle().contains(filter.getTitle()))
							array.push(result.getData().get(i));
					}
				} else array = result.getData();
				
				updateIssueList(array);				
			}
			
			@Override
			public void onFailure(Throwable arg0) {	}
		});
	}
	
	@UiHandler("menuButton")
	void menuButtonClick(ClickEvent event) {
		if(dockLayoutPanel.getWidgetSize(configurationPanel) == 0){
			configurationPanel.add(new ConfigurationPanel());
			dockLayoutPanel.setWidgetSize(configurationPanel, 350);
		}
		else {
			configurationPanel.remove(0);
			dockLayoutPanel.setWidgetSize(configurationPanel, 0);
		}
	}
	
	@UiHandler("refreshButton")
	void refreshButtonClick(ClickEvent event) {
		issueFilter = new IssueFilter();
		updateIssueList(issueFilter);
	}
	
	@UiHandler("addButton")
	void addButtonClick(ClickEvent event) {
		Window.alert("ADD");
	}
}
