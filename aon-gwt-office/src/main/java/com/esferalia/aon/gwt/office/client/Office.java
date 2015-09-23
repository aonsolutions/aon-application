package com.esferalia.aon.gwt.office.client;

import java.util.HashMap;
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
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
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

	private final GitHub gitHub = new GitHub();

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

		this.repositories = new HashMap<Integer, JsRepo>();

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

	private void addOpenIssue(JsIssue issue, JsArray<JsIssueComment> comments) {

		IssueSelected issueSelected = new IssueGrid.IssueOpenLoadSelected(issue);
		issueSelected.setIssueComments(comments);
		issues.add(issueSelected);

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

	// ******************************************************************
	// ******************************************************************
	// ******************************************************************

	@Override
	public void onSelectionTitle(IssueSelected issue) {
		
		issuesPanel.clear();

		IssuesLayoutPanel issueLayoutPanel = new IssuesLayoutPanel(issue);
		issuesPanel.add(issueLayoutPanel);
		
		showIssueLayoutPanel();
	}

	@Override
	public void onNewIssueClickEvent(ClickEvent event) {
		showIssueLayoutPanel();
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
}
