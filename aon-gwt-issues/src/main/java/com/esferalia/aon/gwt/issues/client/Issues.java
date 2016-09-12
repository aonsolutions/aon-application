package com.esferalia.aon.gwt.issues.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.api.client.AonJsArray;
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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.IronListElement;
import com.vaadin.polymer.paper.widget.PaperInput;

public class Issues implements EntryPoint {
	
	interface Binder extends UiBinder<Widget, Issues> {

	}
	private static final Binder binder = GWT.create(Binder.class);

	protected static String USER_NAME = "admin";//"aibanez91";
	protected static String ORG_NAME = "aonPrueba"; //"aonsolutions";
	protected static String REPO_NAME = "aonPrueba"; //"aon-application";
	protected static String ACCESS_TOKEN = "d8aa641723e106b5d7c2d79d3cad963e0eb6e92d";
	protected static String DESCRIPTION = "Repositorio de prueba para metodos de TEST";

	@UiField HTMLPanel searchContent;
	@UiField HTMLPanel content;
	@UiField HTMLPanel issueContent;
	@UiField HTMLPanel toolbar;
	@UiField HTMLPanel configurationPanel;
	@UiField DockLayoutPanel dockLayoutPanel;
	@UiField DockLayoutPanel contentDockLayoutPanel;
	
	IssueFilter issueFilter;
	Issues me;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
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
		me = this;
				
		createAonToolbar();
		createFilterPanel(new FilterPanel(this));
		createIssueList(issueFilter = new IssueFilter());
	}

	protected void createFilterPanel(FilterPanel filterPanel){
		searchContent.add(filterPanel);
	}
	
	protected void createIssueList(IssueFilter filter) {
		//Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME);
		String str = USER_NAME + getCurrentDomainName();
		String md5 = "aaaaa"; //Md5Utils.getMd5Digest(str.getBytes()).toString();
				
		Incidence i = new Incidence(AonUrlApi.AONTEST, md5, USER_NAME, USER_NAME, getCurrentDomainName());

		i.getOrgIssues(filter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {			
				IssueList issueList = new IssueList(me, result.getData());
				content.add(issueList);
				createAddDialog();
			}
			
			@Override
			public void onFailure(Throwable arg0) {	}
		});
	}

	protected void updateIssueList(AonJsArray<JsIssue> array){
		IssueList issueList = (IssueList) content.getWidget(0);
		issueList.updateItems(array);
	}
	
	protected void showMoreupdateIssueList(AonJsArray<JsIssue> array){
		IssueList issueList = (IssueList) content.getWidget(0);
		issueList.updateItems(issueList.getItems().concat(array).cast());
	}

	protected void updateIssueList(IssueFilter filter, Boolean showMore) {
		if(!showMore) filter.setPage(1);
		
		//Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME);
		String str = USER_NAME + getCurrentDomainName();
		String md5 = "aaaaa";//Md5Utils.getMd5Digest(str.getBytes()).toString();
		Incidence i = new Incidence(AonUrlApi.AONTEST, md5, USER_NAME, USER_NAME, getCurrentDomainName());
		
		i.getOrgIssues(filter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				AonJsArray<JsIssue> array = JavaScriptObject.createArray().cast();
				if(filter.getTitle() != null && !filter.getTitle().isEmpty()){
					for(Integer i = 0; i < result.getData().length(); i++){
						if(result.getData().get(i).getTitle().contains(filter.getTitle()))
							array.push(result.getData().get(i));
					}
				} else array = result.getData();
				
				if(showMore) showMoreupdateIssueList(array);
				else updateIssueList(array);				
				
			}
			
			@Override
			public void onFailure(Throwable arg0) {	}
		});
	} 
	
	
	private void createAonToolbar(){
		toolbar.add(new AonToolbar() {
			
			@Override
			protected void onRefreshButtonClick() {
				issueFilter = new IssueFilter();
				updateIssueList(issueFilter, false);
			}
			
			@Override protected void onMoreOptionButtonClick() {}
			
			@Override
			protected void onMenuButtonClick() {
				Window.alert("Funcionalidad deshabilitada");
				/*
				if(dockLayoutPanel.getWidgetSize(configurationPanel) == 0){
					configurationPanel.add(new ConfigurationPanel());
					dockLayoutPanel.setWidgetSize(configurationPanel, 350);
				}
				else {
					configurationPanel.remove(0);
					dockLayoutPanel.setWidgetSize(configurationPanel, 0);
				}*/				
			}
			
			@Override protected void onEditButtonClick() {}
			
			@Override protected void onDeleteButtonClick() {}
			
			@Override
			protected void onAddButtonClick() {
				AonDialog dialog = createAddDialog();
				toolbar.add(dialog);
				dialog.open();
			}
		}.setVisibleEditButton(false).setVisibleDeleteButton(false)
		.setVisibleMoreOptionButton(false));
	}
	
	private AonDialog createAddDialog(){
		VerticalPanel v = new VerticalPanel();
		PaperInput pi = new PaperInput();
		pi.setLabel("Titulo");
		pi.setList("as");
		v.add(pi);
		
		PaperInput pi2 = new PaperInput();
		pi2.setLabel("Remitente");
		v.add(pi2);
		
		PaperInput pi3 = new PaperInput();
		pi3.setLabel("Fecha de Vencimiento");
		v.add(pi3);
		
		PaperInput pi4 = new PaperInput();
		pi4.setLabel("Descripcion");
		v.add(pi4);
		
		return new AonDialog("Nueva Incidencia",v){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				VerticalPanel vp = (VerticalPanel) content.getWidget(0);
				PaperInput pi = (PaperInput) vp.getWidget(0);
				PaperInput pi2 = (PaperInput) vp.getWidget(1);
				PaperInput pi3 = (PaperInput) vp.getWidget(2);
				PaperInput pi4 = (PaperInput) vp.getWidget(3);
				
				String r= "{\"title\":\""+ pi.getValue() +"\",\"body\":\""+ pi4.getValue()+" \",\"assignee\":\" \",\"labels\":[],"
						+ "\"enterprise\":\""+ pi2.getValue() +"\", \"due_date\":\""+ pi3.getValue() +"\"}";
				
				//Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME);
				String str = USER_NAME + getCurrentDomainName();
				String md5 = "aaaaa";//Md5Utils.getMd5Digest(str.getBytes()).toString();
				Incidence i = new Incidence(AonUrlApi.AONTEST, md5, USER_NAME, USER_NAME, getCurrentDomainName());
				
				i.createOrgIssue(r, new AsyncCallback<JsIssue>() {
					
					@Override
					public void onSuccess(JsIssue result) {
						contentDockLayoutPanel.removeFromParent();
						AonToolbar t = (AonToolbar)toolbar.getWidget(0);
						t.setVisibleRefreshButton(false);
						contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
						contentDockLayoutPanel.add(new IssuePanel(me,result));
						dockLayoutPanel.add(contentDockLayoutPanel);
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
	}
}
