package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.Issue;
import com.esferalia.aon.gwt.office.client.models.repos.Repo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class Office extends Composite implements EntryPoint{

	private static OfficeUiBinder uiBinder = GWT.create(OfficeUiBinder.class);

	interface OfficeUiBinder extends UiBinder<Widget, Office> {}
	
	@UiField
	TabPanel tabPanel;

	public Office() {
		Widget ui = uiBinder.createAndBindUi(this);
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		tabPanel.selectTab(0);
	}

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		GitHub gitHub = new GitHub();	
		
		gitHub.getRepos("amtzdelagos", new AsyncCallback<JSON<Repo>>() {
			
			@Override
			public void onSuccess(JSON<Repo> result) {
				Window.alert("Cargando Repositorios publicos.... ");
				
				for (int x = 0; x < result.getData().length(); x ++) {
					
					Window.alert(result.getData().get(x).getName() + " " 
					+ result.getData().get(x).getDescription());
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getMessage());
			}
		});
		
		gitHub.getIssues("amtzdelagos", "aon-GwtOffice", new AsyncCallback<JSON<Issue>>() {
			
			@Override
			public void onSuccess(JSON<Issue> result) {
				Window.alert("Cargando Issues.... ");
				
				String message = "";
				
				for (int x = 0; x < result.getData().length(); x ++) {
					message += result.getData().get(x).getTitle() + " " 
							+ result.getData().get(x).getBody();
				}
				
				Window.alert(message);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}

}
