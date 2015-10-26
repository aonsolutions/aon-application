package com.esferalia.aon.gwt.office.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.repos.JsRepo;
import com.esferalia.aon.gwt.office.client.values.IssueCommentValue;
import com.esferalia.aon.gwt.office.client.values.IssueValue;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Office extends Composite implements EntryPoint,
		IssueGrid.Listener, LeftButtonsMenuBar.LeftMenuBarListener,
		IssuesLayoutPanel.Listener, IssueWriteWidget.Listener {

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
	ListBox repoListBox;
	@UiField
	LeftButtonsMenuBar leftButtonBarMenu;

	private JsRepo repo;
	private IssuesLayoutPanel issueLayoutPanel;
	private IssueSelected issueSelected;
	private final GitHub gitHub = new GitHub();

	private List<IssueSelected> openIssues;
	private List<IssueSelected> closedIssues;

	private Map<Integer, JsIssue> issuesMap;
	private Map<Integer, JsRepo> repositories;

	private AonHubServiceAsync aonHubService;

	public Office() {
		Widget ui = uiBinder.createAndBindUi(this);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();

		this.gitHub.setAccessToken("06a75ef8dfa037f188c2075333ed73574ffd1971");
		this.dataGrid.addListener(this);
		this.leftButtonBarMenu.addListener(this);
		this.repositories = new HashMap<Integer, JsRepo>();
		this.openIssues = new LinkedList<IssueSelected>();
		this.closedIssues = new LinkedList<IssueSelected>();
		this.issuesMap = new TreeMap<Integer, JsIssue>();

		// Create a remote service proxy to talk to the server-side AonHub
		// service.
		AonHubServiceAsync aonHubServiceRaw = GWT.create(AonHubService.class);
		aonHubService = new AonHubServiceAsyncDecorator(aonHubServiceRaw);

		ListDataProvider<IssueSelected> openIssuesProvider = new ListDataProvider<IssueSelected>();
		openIssuesProvider.addDataDisplay(dataGrid);
		openIssues = openIssuesProvider.getList();

		ListDataProvider<IssueSelected> closeIssuesProvider = new ListDataProvider<IssueSelected>();
		closeIssuesProvider.addDataDisplay(dataGrid);
		closedIssues = openIssuesProvider.getList();
		
		aonHubService.getIssues(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("ojo lorito que hay problemas " + caught.getMessage()
						+ " " + caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(String result) {
				Window.alert(result.toString());
			}
		});

		loadReposList();
		loadIssuesList();

		showDockOfficePanel();
	}

	@UiHandler("repoListBox")
	void onChangeSelectionListBox(ChangeEvent event) {

		Integer index = Integer.parseInt(repoListBox.getValue(repoListBox
				.getSelectedIndex()));
		Office.this.repo = repositories.get(index);
	}

	@Override
	public void onSelectionChangeHandler(SelectionChangeEvent event) {	
		
	}

	// ******************************************************************
	// ********************** PRIVATE METHODS ***************************
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
						Office.this.repoListBox.addItem(repo.getName(),
								String.valueOf(repo.getId()));
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
		gitHub.getIssues("amtzdelagos", "aon-GwtOffice",
				new AsyncCallback<JSON<JsIssue>>() {

					@Override
					public void onSuccess(JSON<JsIssue> result) {

						for (int x = 0; x < result.getData().length(); x++) {
							JsIssue issue = result.getData().get(x);
							issuesMap.put(issue.getId(), issue);
							loadIssueComments(issue);
						}
						changeOpenIssuesText(result.getData().length());
					}

					@Override
					public void onFailure(Throwable caught) {
						GWT.log(caught.getMessage());
					}
				});
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

	private boolean isOpenIssue(JsIssue issue) {
		return issue.getState().equals(IssueValue.Prop.OPEN.value);
	}

	private void createAnIssue(JsRepo repo,
			com.esferalia.aon.gwt.office.client.values.issues.IssueValue prop) {

		gitHub.createIssue(repo, prop, new AsyncCallback<JsIssue>() {

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

	private void createIssueComment(JsRepo repo, JsIssue issue,
			IssueCommentValue commentValue) {
		gitHub.createIssueComment(repo, issue, commentValue,
				new AsyncCallback<JsIssueComment>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log(caught.getMessage());
					}

					@Override
					public void onSuccess(JsIssueComment result) {
						issueLayoutPanel.addCommentIssue(result);
					}
				});
	}

	private void editIssue(JsRepo repo, JsIssue issue,
			com.esferalia.aon.gwt.office.client.values.issues.IssueValue prop) {
		gitHub.editIssue(repo, issue, prop, new AsyncCallback<JsIssue>() {
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
		leftButtonBarMenu.addLabelIssueButton(row, label);
	}

	private void changeOpenIssuesText(Integer number) {
		leftButtonBarMenu.changeOpenIssuesText(number);
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

		this.issuesPanel.clear();
		this.issueSelected = issue;
		this.issueLayoutPanel = new IssuesLayoutPanel(issue);
		this.issueLayoutPanel.addListener(this);
		this.issuesPanel.add(issueLayoutPanel);

		showIssueLayoutPanel();
	}

	// ******************************************************************
	// ********************** LEFT BAR BUTTONS **************************
	// ******************************************************************

	@Override
	public void onNewIssueClickEvent(ClickEvent event) {
		issueWrite.clear();
		IssueWriteWidget issueWriteWidget = new IssueWriteWidget();
		issueWriteWidget.addListener(this);
		issueWrite.add(issueWriteWidget);
		showWriteIssueWritePanel();
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

	// ******************************************************************
	// *********************** LAYOUT ISSUES ***************************
	// ******************************************************************

	@Override
	public void onCloseIssueClickEvent(IssueSelected issue) {
		JsIssue jsIssue = issuesMap.get(issue.getId());
		com.esferalia.aon.gwt.office.client.values.issues.IssueValue value = new com.esferalia.aon.gwt.office.client.values.issues.IssueValue();
		value.setState(IssueValue.Prop.CLOSE.value);

		editIssue(this.repo, jsIssue, value);
	}

	@Override
	public void onReopenedIssueClickEvent(IssueSelected issue) {
		JsIssue jsIssue = issuesMap.get(issue.getId());
		com.esferalia.aon.gwt.office.client.values.issues.IssueValue value = new com.esferalia.aon.gwt.office.client.values.issues.IssueValue();
		value.setState(IssueValue.Prop.OPEN.value);

		editIssue(this.repo, jsIssue, value);

	}

	// ******************************************************************
	// *********************** ISSUES WRITE ****************************
	// ******************************************************************

	@Override
	public void onComment(IssueCommentValue issueCommentValue) {
		JsIssue jsIssue = issuesMap.get(issueSelected.getId());
		createIssueComment(this.repo, jsIssue, issueCommentValue);
	}

	@Override
	public void onCommentButtonClick(
			com.esferalia.aon.gwt.office.client.values.issues.IssueValue issueValue) {
		createAnIssue(repo, issueValue);
	}

}
