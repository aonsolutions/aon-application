package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class TagPanel extends Composite {
	
	protected static String USER_NAME = "admin"; //"aibanez91";
	protected static String ORG_NAME = "aonPrueba"; //"aonsolutions";
	protected static String REPO_NAME = "aonPrueba"; //"aon-application";
	protected static String ACCESS_TOKEN = "d8aa641723e106b5d7c2d79d3cad963e0eb6e92d";
	protected static String DESCRIPTION = "Repositorio de prueba para metodos de TEST";

    interface Binder extends UiBinder<HTMLPanel, TagPanel> {
    	
    }
        
    @UiField Tree tree;
	@UiField TreeItem priorityTreeItem;
	@UiField TreeItem typeTreeItem;
	@UiField TreeItem issueTreeItem;

    private static Binder binder = GWT.create(Binder.class);
    
    public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/; 
	
    public TagPanel() {
        initWidget(binder.createAndBindUi(this));
        	
       getTagList();
    }
    
    protected void getTagList() {
		//Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME);
		String str = USER_NAME + getCurrentDomainName();
		String md5 = "aaaaa";//Md5Utils.getMd5Digest(str.getBytes()).toString();
		Incidence i = new Incidence(AonUrlApi.AONTEST, md5, USER_NAME, USER_NAME, getCurrentDomainName());
		i.getLabels(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				for(Integer i = 0; i < result.getData().length(); i++){
					TreeItem ti = new TreeItem();
					ti.setText(result.getData().get(i).getName());
					ti.getElement().getStyle().setBackgroundColor("#"+result.getData().get(i).getColor());
					ti.getElement().getStyle().setPadding(3, Unit.PX);
					ti.getElement().getStyle().setColor("white");
					ti.getElement().getStyle().setMarginBottom(5, Unit.PX);
					ti.getElement().getStyle().setMarginTop(5, Unit.PX);
					ti.getElement().getStyle().setMarginRight(100, Unit.PX);
					ti.getElement().getStyle().setFontWeight(FontWeight.BOLD);
					issueTreeItem.addItem(ti);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
}
