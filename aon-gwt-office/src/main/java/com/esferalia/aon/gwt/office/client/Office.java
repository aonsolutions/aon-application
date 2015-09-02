package com.esferalia.aon.gwt.office.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.Issue;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class Office extends Composite implements EntryPoint{

	private static OfficeUiBinder uiBinder = GWT.create(OfficeUiBinder.class);

	interface OfficeUiBinder extends UiBinder<Widget, Office> {}
	
	@UiField
	ResizeLayoutPanel dockPanel;
	@UiField
	SimpleLayoutPanel resultsPanel;
	
	@UiField
	IssueGrid dataGrid;
	
	private List<IssueSelected> issues;
	
	public Office() {
		Widget ui = uiBinder.createAndBindUi(this);
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		this.dockPanel.setSize("100%", "100%");
		
		ListDataProvider<IssueSelected> listIssuesProvider = new ListDataProvider<IssueSelected>();
		listIssuesProvider.addDataDisplay(dataGrid);
		this.issues = listIssuesProvider.getList();
		
		GitHub gitHub = new GitHub();
		
		gitHub.getIssues("amtzdelagos", "aon-GwtOffice", new AsyncCallback<JSON<Issue>>() {
			
			@Override
			public void onSuccess(JSON<Issue> result) {
				
				for (int x = 0; x < result.getData().length(); x++) {
					
					IssueSelected selected = new IssueGrid.IssueLoadSelected(result.getData().get(x));
					issues.add(selected);
					
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

}
