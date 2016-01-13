package com.esferalia.aon.gwt.office.client;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.repos.JsRepo;
import com.esferalia.aon.gwt.office.client.values.IssueValue;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Office extends Composite implements EntryPoint,
		IssueGrid.Listener, LeftButtonsMenuBar.LeftMenuBarListener {

	private static OfficeUiBinder uiBinder = GWT.create(OfficeUiBinder.class);

	interface OfficeUiBinder extends UiBinder<Widget, Office> {
	}

	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	ResizeLayoutPanel dockOfficePanel;
	@UiField
	ResizeLayoutPanel issuesPanel;
	@UiField
	FlowPanel issueWrite;
	@UiField
	SimpleLayoutPanel resultsPanel;
	@UiField
	IssueGrid dataGrid;
	@UiField
	LeftButtonsMenuBar leftButtonBarMenu;

	private JsRepo repo;
	private IssueSelected issueSelected;
	private AonHub gitHub = new AonHub(GWT.getModuleBaseURL() + "api/");
	private List<IssueSelected> openIssues;
	private List<IssueSelected> closedIssues;

	private Map<Integer, JsIssue> issuesMap;
	private Map<Integer, JsRepo> repositories;

	public Office() {
		Widget ui = uiBinder.createAndBindUi(this);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		this.dataGrid.addListener(this);
		this.leftButtonBarMenu.addListener(this);
		this.gitHub.setRepositoryUrl(GWT.getModuleBaseURL() + "api");
		
		initOpenIssues();
//		initCloseIssues();
		
		gitHub.getOpenIssues("user", "repo", new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Exception getOpenIssues: " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {				
				JsArray<JsIssue> issues = result.getData();
				
				for ( int x = 0; x < issues.length(); x++) {
					IssueSelected issueSelected = new IssueGrid.IssueOpenLoadSelected(issues.get(x));
					openIssues.add(issueSelected);
				}
			}
		});

		showDockOfficePanel();
	}

	@Override
	public void onSelectionChangeHandler(SelectionChangeEvent event) {

	}

	// ******************************************************************
	// ********************** PRIVATE METHODS ***************************
	// ******************************************************************

	void initOpenIssues() {
		openIssues = new LinkedList<IssueSelected>();
		ListDataProvider<IssueSelected> openIssuesProvider = new ListDataProvider<IssueSelected>();
		openIssuesProvider.addDataDisplay(dataGrid);
		openIssues = openIssuesProvider.getList();

	}

	void initCloseIssues() {
		closedIssues = new LinkedList<IssueSelected>();
		ListDataProvider<IssueSelected> closeIssuesProvider = new ListDataProvider<IssueSelected>();
		closeIssuesProvider.addDataDisplay(dataGrid);
		closedIssues = closeIssuesProvider.getList();
	}

	private void loadNotices(JsArray<JsIssue> notices) {

		for (int x = 0; x < notices.length(); x++) {
			JsIssue issue = notices.get(x);
			issuesMap.put(issue.getId(), issue);
			addOpenIssue(issue, null);
			// loadIssueComments(issue);
		}
		changeOpenIssuesText(notices.length());

	}

	private boolean isOpenIssue(JsIssue issue) {
		return issue.getState().equals(IssueValue.Prop.OPEN.value);
	}

	private void createAnIssue(JsRepo repo,
			com.esferalia.aon.gwt.office.client.values.issues.IssueValue prop) {

		gitHub.createIssue(prop, new AsyncCallback<JsIssue>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
			}

			@Override
			public void onSuccess(JsIssue result) {
				Window.alert("Recarga el dataGrid");
			}
		});
	}

	private void editIssue(JsRepo repo, JsIssue issue,
			com.esferalia.aon.gwt.office.client.values.issues.IssueValue prop) {
		gitHub.editIssue(issue, prop, new AsyncCallback<JsIssue>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
			}

			@Override
			public void onSuccess(JsIssue result) {
				Window.alert("Recarga el datagrid");
			}
		});
	}

	private void addOpenIssue(JsIssue issue, JsArray<JsIssueComment> comments) {
		IssueSelected issueSelected = new IssueGrid.IssueOpenLoadSelected(issue);
		if (comments != null)
			issueSelected.setIssueComments(comments);
		openIssues.add(issueSelected);
	}

	private void addCloseIssue(JsIssue issue, JsArray<JsIssueComment> comments) {
		IssueSelected issueSelected = new IssueGrid.IssueClosedLoadSelected(
				issue);
		issueSelected.setIssueComments(comments);
		closedIssues.add(issueSelected);
	}

	private void loadOpenIssues() {
		List<IssueSelected> issues = new LinkedList<IssueSelected>();
		Collections.sort(openIssues, IssueGrid.Comparators.NUMBER);

		for (IssueSelected issue : openIssues)
			issues.add(issue);
	}

	private void loadCloseIssues() {
		List<IssueSelected> issues = new LinkedList<IssueSelected>();
		Collections.sort(closedIssues, IssueGrid.Comparators.NUMBER);

		for (IssueSelected issue : closedIssues)
			issues.add(issue);
	}

	private void loadAllIssues() {
		List<IssueSelected> issues = new LinkedList<IssueSelected>();

		List<IssueSelected> allIssues = new LinkedList<IssueSelected>();
		allIssues.addAll(openIssues);
		allIssues.addAll(closedIssues);

		Collections.sort(allIssues, IssueGrid.Comparators.NUMBER);
		ListDataProvider<IssueSelected> listIssuesProvider = new ListDataProvider<IssueSelected>();
		listIssuesProvider.addDataDisplay(dataGrid);
		issues = listIssuesProvider.getList();

		for (IssueSelected issue : allIssues)
			issues.add(issue);
	}

	private void addLabel(int row, JsLabel label) {
//		leftButtonBarMenu.addLabelIssueButton(row, label);
	}

	private void changeOpenIssuesText(Integer number) {
//		leftButtonBarMenu.changeOpenIssuesText(number);
	}

	private void showDockOfficePanel() {
		deckPanel.showWidget(dockOfficePanel);
	}

	private void showIssueLayoutPanel() {
		deckPanel.showWidget(issuesPanel);
	}

	private void showWriteIssueWritePanel() {
		deckPanel.showWidget(issueWrite);
	}

	// ******************************************************************
	// ********************** ISSUES DATA GRID **************************
	// ******************************************************************

	@Override
	public void onSelectionTitle(IssueSelected issue) {
		
		IssuePanel issuePanel = new IssuePanel("Mostrar Informacion");
		issuePanel.setTitle(issue.getTitle());
		issuePanel.setSender(issue.getUser().getLogin());
		issuePanel.setPriority(issue.getPriority());
		issuePanel.setType(issue.getType());
		issuePanel.setBody(issue.getBody());
		issuePanel.showPopupPanel();
		
//		this.issuesPanel.clear();
//		this.issueSelected = issue;
//		this.issueLayoutPanel = new IssuesLayoutPanel(issue);
//		this.issueLayoutPanel.addListener(this);
//		this.issuesPanel.add(issueLayoutPanel);
//		showIssueLayoutPanel();
	}

	// ******************************************************************
	// ********************** LEFT BAR BUTTONS **************************
	// ******************************************************************

	@Override
	public void onNewIssueClickEvent(ClickEvent event) {
		IssuePanel issuePanel = new IssuePanel("NUEVA INCIDENCIA");
		issuePanel.showPopupPanel();
	}

	@Override
	public void onShowOpenIssuesClickEvent(ClickEvent event) {
		loadOpenIssues();
		showDockOfficePanel();
	}

	@Override
	public void onShowClosedIssuesClickEvent(ClickEvent event) {
		loadCloseIssues();
		showDockOfficePanel();
	}

	@Override
	public void onShowAllIssuesClickEvent(ClickEvent event) {
		loadAllIssues();
		showDockOfficePanel();
	}

	@Override
	public void onShowDeletedIssuesClickEvent(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowQuestionIssuesClickEvent(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowErrorIssuesClickEvent(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowFaqsIssuesClickEvent(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLabelIssueClickEvent(Button button) {
		// TODO Auto-generated method stub

	}

	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;

	// ******************************************************************
	// *********************** GITHUB METHODS ***************************
	// ******************************************************************

	private void loadReposList() {
		gitHub.getRepos("amtzdelagos", new AsyncCallback<JSON<JsRepo>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error al obtener los respositorios. " + " "
						+ caught.getMessage());
			}

			@Override
			public void onSuccess(JSON<JsRepo> result) {
				if (result != null) {
					for (int z = 0; z < result.getData().length(); z++) {
						JsRepo repo = result.getData().get(z);
						Office.this.repositories.put(repo.getId(), repo);
						// Office.this.repoListBox.addItem(repo.getName(),
						// String.valueOf(repo.getId()));
						Office.this.repo = repo;
					}

					repo.getLabels(new AsyncCallback<AJSON<JsArray<JsLabel>>>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log(caught.getMessage());
						}

						@Override
						public void onSuccess(AJSON<JsArray<JsLabel>> result) {
							for (int x = 0; x < result.getData().length(); x++)
								addLabel(x, result.getData().get(x));
						}
					});
				}
			}
		});
	}

	private void loadIssuesList() {
	}

	private void loadIssueComments(final JsIssue issue) {
		issue.getCommments(new AsyncCallback<AJSON<JsArray<JsIssueComment>>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
			}

			@Override
			public void onSuccess(AJSON<JsArray<JsIssueComment>> result) {

				if (isOpenIssue(issue))
					addOpenIssue(issue, result.getData());
				else
					addCloseIssue(issue, result.getData());
			}
		});
	}
}
