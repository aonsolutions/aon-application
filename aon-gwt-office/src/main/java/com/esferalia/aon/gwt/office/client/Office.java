package com.esferalia.aon.gwt.office.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
	private List<IssueSelected> issues;
	private Map<Integer, JsRepo> repositories;
	private IssuesLayoutPanel issueLayoutPanel;
	private IssueSelected issueSelected;
	private final GitHub gitHub = new GitHub();

	private Set<IssueSelected> openIssues;
	private Set<IssueSelected> closedIssues;

	private Map<Integer, JsIssue> issuesMap;

	public Office() {
		Widget ui = uiBinder.createAndBindUi(this);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		this.gitHub.setAccessToken("253b1b1a0c0592768a03a8636e9d5e093a794cb8");
		this.dataGrid.addListener(this);
		this.leftButtonBarMenu.addListener(this);

		this.openIssues = new TreeSet<IssueSelected>();
		this.closedIssues = new TreeSet<IssueSelected>();
		this.repositories = new HashMap<Integer, JsRepo>();

		this.issuesMap = new TreeMap<Integer, JsIssue>();

		ListDataProvider<IssueSelected> listIssuesProvider = new ListDataProvider<IssueSelected>();
		listIssuesProvider.addDataDisplay(dataGrid);
		this.issues = listIssuesProvider.getList();	

		loadReposList();
		loadIssuesList();
	}

	@UiHandler("repoListBox")
	void onChangeSelectionListBox(ChangeEvent event) {

		Integer index = Integer.parseInt(repoListBox.getValue(repoListBox
				.getSelectedIndex()));
		Office.this.repo = repositories.get(index);
	}

	@Override
	public void onSelectionChangeHandler(SelectionChangeEvent event) {
		// TODO Auto-generated method stub

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

		showDockOfficePanel();
	}

	private void loadIssueComments(final JsIssue issue) {

		issue.getCommments(new AsyncCallback<AJSON<JsArray<JsIssueComment>>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
			}

			@Override
			public void onSuccess(AJSON<JsArray<JsIssueComment>> result) {

				if (issue.getState().equals(IssueValue.Prop.OPEN.value))
					addOpenIssue(issue, result.getData());

			}
		});
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
						Window.alert("Refresca el grid");
					}
				});

	}

	private void addOpenIssue(JsIssue issue, JsArray<JsIssueComment> comments) {

		IssueSelected issueSelected = new IssueGrid.IssueOpenLoadSelected(issue);
		issueSelected.setIssueComments(comments);
		issues.add(issueSelected);

	}

	private void addCloseIssue(JsIssue issue, JsArray<JsIssueComment> comments) {

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
		showDockOfficePanel();
	}

	@Override
	public void onShowClosedIssuesClickEvent(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowAllIssuesClickEvent(ClickEvent event) {
		// TODO Auto-generated method stub

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
