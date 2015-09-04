package com.esferalia.aon.gwt.office.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.Issue;
import com.esferalia.aon.gwt.office.client.models.repos.Repo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Office extends Composite implements EntryPoint, IssueGrid.Listener{

	private static OfficeUiBinder uiBinder = GWT.create(OfficeUiBinder.class);

	interface OfficeUiBinder extends UiBinder<Widget, Office> {}
	
	@UiField
	ResizeLayoutPanel dockPanel;
	@UiField
	SimpleLayoutPanel resultsPanel;
	@UiField
	IssueGrid dataGrid;
	@UiField
	ListBox repoListBox;
	@UiField
	Button newIssue;
	
	private Repo repo;
	private List<IssueSelected> issues;
	private Map<Integer, Repo> repositories;
	
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
		this.dataGrid.addListener(this);
		
		this.repositories = new HashMap<Integer, Repo>();
		
		ListDataProvider<IssueSelected> listIssuesProvider = new ListDataProvider<IssueSelected>();
		listIssuesProvider.addDataDisplay(dataGrid);
		this.issues = listIssuesProvider.getList();
		
		GitHub gitHub = new GitHub();
		
		gitHub.getRepos("amtzdelagos", new AsyncCallback<JSON<Repo>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error al obtener los respositorios. "
						+ "Amo a tene que revisar porque no esta bien."
						+ " " + caught.getMessage());
			}

			@Override
			public void onSuccess(JSON<Repo> result) {
				if (result != null) {
					for (int z = 0; z < result.getData().length(); z++) {						
						Repo repo = result.getData().get(z);
						Office.this.repositories.put(repo.getId(), repo);						
						Office.this.repoListBox.addItem(repo.getName(), String.valueOf(repo.getId()));
						Office.this.repo = repo;
					}
				}
			}
		});
		
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
	
	@UiHandler("newIssue")
	void onNewIssueClick(ClickEvent event) {
		NewIssuePopupPanel newIssue = new NewIssuePopupPanel();
		
		newIssue.showPopUpPanel();
	}
	
	@UiHandler("repoListBox")
	void onChangeSelectionListBox (ChangeEvent event){
	
		Integer index = Integer.parseInt(repoListBox.getValue(repoListBox.getSelectedIndex()));		
		Office.this.repo = repositories.get(index);
	}
	
	@Override
	public void onSelectionChangeHandler(SelectionChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectionTitle(IssueSelected issue) {
		Window.alert("Title: " + issue.getTitle() + " Id: " + issue.getId());
		
	}
}
