package com.esferalia.aon.gwt.office.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.office.client.IssueGrid.IssueOpenLoadSelected;
import com.esferalia.aon.gwt.office.client.IssueReadPanel.Callback;
import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.repos.JsRegistry;
import com.esferalia.aon.gwt.office.client.models.repos.JsRepo;
import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.esferalia.aon.gwt.office.client.values.IssueCommentValue;
import com.esferalia.aon.gwt.office.client.values.LabelValue;
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.office.User;
import com.esferalia.aon.occam.api.model.registry.Registry;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Office extends Composite implements EntryPoint, IssueGrid.Listener,
		IssuePanel.Listener, IssueReadPanel.Listener, TagTree.Listener {

	private static OfficeUiBinder uiBinder = GWT.create(OfficeUiBinder.class);

	interface OfficeUiBinder extends UiBinder<Widget, Office> {
	}

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	Button newIssueButton;
	@UiField
	Button openIssuesButton;
	@UiField
	Button closedIssuesButton;
	@UiField
	Button tagButton;
	@UiField
	Button returnButton;
	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	ResizeLayoutPanel dockOfficePanel;
	@UiField
	SimpleLayoutPanel readIssueLayoutPanel;
	@UiField
	DockLayoutPanel readIssuePanel;

	@UiField
	IssueGrid dataGrid;

	@UiField
	StackLayoutPanel stackLayoutPanel;
	@UiField
	TagTree tagTree;

	private User user;

	private IssuePanel issuePanel;	

	private IssueSelected issueSelected;
	private AonHub gitHub = new AonHub(GWT.getModuleBaseURL() + "api/");
	private List<IssueSelected> openIssues;
	private List<IssueSelected> closedIssues;

	private ListDataProvider<IssueSelected> openIssuesProvider;
	private ListDataProvider<IssueSelected> closeIssuesProvider;
	private List<Tag> tagList;
	private List<Registry> registries;

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

		this.tagList = new LinkedList<Tag>();
		this.registries = new LinkedList<Registry>();

		this.dataGrid.addListener(this);
		this.tagTree.addListener(this);
		this.gitHub.setRepositoryUrl(GWT.getModuleBaseURL() + "api");

		gitHub.getUser(String.valueOf(getCurrentDomain()),
				new AsyncCallback<AJSON<JsUser>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(AJSON<JsUser> result) {
						userIdentificated(result.getData());
					}
				});

		loadRegistries();
		loadLabels();
		loadOpenIssues();		
	}

	private void loadRegistries() {
		gitHub.getRegistries(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), new AsyncCallback<JSON<JsRegistry>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("getRegistries()" + caught.getMessage());
					}

					@Override
					public void onSuccess(JSON<JsRegistry> result) {
						JsArray<JsRegistry> registries = result.getData();
						for (int x = 0; x < registries.length(); x++)
							addRegistry2List(registries.get(x));
					}
				});

	}

	private void loadLabels() {

		gitHub.getLabels(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), new AsyncCallback<JSON<JsLabel>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("getLabels(): " + caught.getMessage());
					}

					@Override
					public void onSuccess(JSON<JsLabel> result) {
						JsArray<JsLabel> labels = result.getData();

						for (int x = 0; x < labels.length(); x++)
							addLabels2List(labels.get(x));
					}
				});
	}

	private void loadOpenIssues() {

		initOpenIssues();
		gitHub.getOpenIssues(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), new AsyncCallback<JSON<JsIssue>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Exception getOpenIssues: "
								+ caught.getMessage());
					}

					@Override
					public void onSuccess(JSON<JsIssue> result) {

						for (int x = 0; x < result.getData().length(); x++)
							addOpenIssue(result.getData().get(x));
					}
				});
		showDockOfficePanel();
	}

	private void loadClosedIssues() {
		initClosedIssues();
		gitHub.getClosedIssues(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), new AsyncCallback<JSON<JsIssue>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JSON<JsIssue> result) {

						for (int x = 0; x < result.getData().length(); x++)
							addCloseIssue(result.getData().get(x));
					}
				});	
		showDockOfficePanel();
	}

	@Override
	public void onSelectionChangeHandler(SelectionChangeEvent event) {

	}

	// ******************************************************************
	// *************************** UI - HANDLERS ************************
	// ******************************************************************

	@UiHandler("newIssueButton")
	void onNewIssueClick(ClickEvent event) {
		issuePanel = new IssuePanel(this.user);
		issuePanel.addListener(this);
		issuePanel.setTagList(tagList);
		if (registries.size() > 0)
			issuePanel.setRegistries(registries);
		issuePanel.showPopupPanel();
	}

	@UiHandler("tagButton")
	void onTagButtonClick(ClickEvent event) {
		dockLayoutPanel.setWidgetSize(Office.this.stackLayoutPanel, 220);
	}

	@UiHandler("returnButton")
	void onReturnButtonClick(ClickEvent event) {
		if ( openIssuesButton.isEnabled() == false)
			loadOpenIssues();
		else
			loadClosedIssues();
		
		returnButton.setEnabled(false);
	}

	@UiHandler("openIssuesButton")
	void onOpenIssuesButton(ClickEvent event) {
		enabledIssuesButton();
		loadOpenIssues();
	}

	@UiHandler("closedIssuesButton")
	void onClosedIssuesButton(ClickEvent event) {
		enabledIssuesButton();
		loadClosedIssues();	
	}

	private void initEnabledIssuesButton() {
	}

	private void enabledIssuesButton() {
		openIssuesButton.setEnabled(!openIssuesButton.isEnabled());
		closedIssuesButton.setEnabled(!closedIssuesButton.isEnabled());
	}

	// ******************************************************************
	// ********************** PRIVATE METHODS ***************************
	// ******************************************************************

	void initOpenIssues() {
		openIssues = new LinkedList<IssueSelected>();
		openIssuesProvider = new ListDataProvider<IssueSelected>();
		openIssuesProvider.addDataDisplay(dataGrid);
		openIssues = openIssuesProvider.getList();
	}

	void initClosedIssues() {
		closedIssues = new LinkedList<IssueSelected>();
		closeIssuesProvider = new ListDataProvider<IssueSelected>();
		closeIssuesProvider.addDataDisplay(dataGrid);
		closedIssues = closeIssuesProvider.getList();
	}

	void addOpenIssue(JsIssue issue) {
		IssueSelected issueSelected = new IssueGrid.IssueOpenLoadSelected(
				issue);
		openIssues.add(issueSelected);
	}

	void addCloseIssue(JsIssue issue) {
		IssueSelected issueSelected = new IssueGrid.IssueClosedLoadSelected(
				issue);
		closedIssues.add(issueSelected);
	}

	void initCloseIssues() {
		closedIssues = new LinkedList<IssueSelected>();
		closeIssuesProvider = new ListDataProvider<IssueSelected>();
		closeIssuesProvider.addDataDisplay(dataGrid);
		closedIssues = closeIssuesProvider.getList();
	}

	void userIdentificated(JsUser jsUser) {
		this.user = new User();
		this.user.setId(jsUser.getId());
		this.user.setName(jsUser.getName());
		this.user.setLogin(jsUser.getLogin());
	}

	private void addLabels2List(JsLabel jsLabel) {

		Tag tag = new Tag();
		tag.setId(jsLabel.getId());
		tag.setName(jsLabel.getName());
		tag.setType(jsLabel.getType());
		tag.setColor(jsLabel.getColor());
		tag.setDomain(jsLabel.getDomain());

		tagList.add(tag);

		if (tag.getDomain() != 0)
			tagTree.insertTag(tag);
	}

	private void addRegistry2List(JsRegistry jsRegistry) {
		Registry registry = new Registry();
		registry.setId(jsRegistry.getId());
		registry.setAlias(jsRegistry.getAlias());
		registry.setName(jsRegistry.getName());
		registry.setDocument(jsRegistry.getDocument());

		registries.add(registry);
	}

	private void showDockOfficePanel() {
		deckPanel.showWidget(dockOfficePanel);
	}

	private void showReadIssuePanel() {
		deckPanel.showWidget(readIssuePanel);
	}

	// ******************************************************************
	// *************************** TAG TREE *****************************
	// ******************************************************************

	@Override
	public void onAddNewTag(final Tag tag) {

		LabelValue value = new LabelValue();
		value.setName(tag.getName());
		Byte type = tag.getType();
		value.setType(type.intValue());

		gitHub.createLabel(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), value, new AsyncCallback<JsLabel>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JsLabel result) {
						addLabels2List(result);
					}
				});
	}

	@Override
	public void onDeleteTag(Tag tag) {

		gitHub.deleteLabel(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), tag.getName(),
				new AsyncCallback<JsLabel>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JsLabel result) {

					}
				});
	}

	@Override
	public void onHideTagsPanel() {
		dockLayoutPanel.setWidgetSize(Office.this.stackLayoutPanel, 0);
	}

	// ******************************************************************
	// ********************** ISSUES DATA GRID **************************
	// ******************************************************************

	@Override
	public void onSelectionTitle(IssueSelected issue) {
		this.issueSelected = issue;
		IssueReadPanel issueReadPanel = new IssueReadPanel(issue);
		issueReadPanel.setUser(user);
		issueReadPanel.addListener(this);
		readIssueLayoutPanel.clear();
		readIssueLayoutPanel.add(issueReadPanel);
		returnButton.setEnabled(true);
		
		showReadIssuePanel();
	}

	// ******************************************************************
	// ********************** ISSUES PANEL LISTENER *********************
	// ******************************************************************

	@Override
	public void onCreateNewIssue(Notice notice) {
		IssueValue value = new IssueValue();
		value.setTitle(notice.getTitle());
		value.setBody(notice.getBody());
		value.setState(notice.getStatus());
		value.setSender(String.valueOf(notice.getSender().getId()));

		if (notice.getCompany() != null) {
			value.setCompany(notice.getCompany());
			value.setSource(notice.getSource());
		}

		if (notice.getTags().size() > 0) {

			String[] labels = new String[notice.getTags().size()];
			for (int x = 0; x < notice.getTags().size(); x++) {
				labels[x] = notice.getTags().get(x).getName();
			}

			value.setLabels(labels);
		}

		gitHub.createIssue(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), value, new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JsIssue result) {
						IssueSelected issue = new IssueGrid.IssueOpenLoadSelected(result);
						openIssues.add(0, issue);
						onSelectionTitle(issue);						
					}
				});
	}

	// ******************************************************************
	// ******************* ISSUES READ PANEL LISTENER *******************
	// ******************************************************************

	@Override
	public void onUpdateIssueState(String state) {
		IssueValue value = new IssueValue();
		value.setState(state);

		gitHub.editIssue(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), issueSelected.getJsIssue(), value,
				new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JsIssue result) {
						loadOpenIssues();
						showDockOfficePanel();
					}
				});

	}

	@Override
	public void onUpdateIssueBody(String title, String body,
			final Callback<IssueSelected> callback) {
		final IssueValue value = new IssueValue();
		value.setBody(body);
		value.setTitle(title);

		gitHub.editIssue(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), issueSelected.getJsIssue(), value,
				new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(JsIssue result) {
						IssueSelected issue = new IssueGrid.IssueOpenLoadSelected(result);
						callback.onSucess(issue);
					}
				});
	}

	@Override
	public void onIssueCommentButtonClick(String body,
			final Callback<DefaultAonIssueComments> callback) {
		IssueCommentValue value = new IssueCommentValue();
		value.setBody(body);		

		gitHub.createIssueComment(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), issueSelected.getJsIssue(), value,
				new AsyncCallback<JsIssueComment>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(JsIssueComment result) {
						DefaultAonIssueComments comment = new DefaultAonIssueComments(
								result);
						issueSelected.addComment(result);
						callback.onSucess(comment);

					}
				});
	}

	@Override
	public void onUpdateIssueComment(final Integer id, final String body,
			final Callback<DefaultAonIssueComments> callback) {
		IssueCommentValue value = new IssueCommentValue();
		value.setBody(body);

		gitHub.editIssueComment(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), id, value,
				new AsyncCallback<JsIssueComment>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(JsIssueComment result) {
						DefaultAonIssueComments comment = issueSelected
								.editComment(result);
						callback.onSucess(comment);
					}
				});
	}

	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

}
