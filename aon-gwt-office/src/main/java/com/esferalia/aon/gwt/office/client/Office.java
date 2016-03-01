package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.shared.DateUtils;
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
import com.esferalia.aon.gwt.office.client.values.LabelControlValue;
import com.esferalia.aon.gwt.office.client.values.LabelValue;
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
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
	Button tagButton;
	@UiField
	Button returnButton;
	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	DockLayoutPanel dockOfficePanel;
	@UiField
	SimpleLayoutPanel readIssueLayoutPanel;
	@UiField
	ResizeLayoutPanel readIssuePanel;
	@UiField
	ListBox fromListBox;
	@UiField
	RadioButton openIssuesRb;
	@UiField
	RadioButton closedIssuesRb;
	@UiField
	RadioButton allIssuesRb;

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
	private List<IssueSelected> allIssues;

	private ListDataProvider<IssueSelected> openIssuesProvider;
	private ListDataProvider<IssueSelected> closeIssuesProvider;
	private ListDataProvider<IssueSelected> allIssuesProvider;

	private List<DefaultAonTagIssueSelected> tagList;
	private List<Registry> registries;

	private Map<Integer, JsIssue> issuesMap;
	private Map<Integer, JsRepo> repositories;
	
	private DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public Office() {
		Widget ui = uiBinder.createAndBindUi(this);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();

		this.tagList = new LinkedList<DefaultAonTagIssueSelected>();
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

		initFromListBox();
		loadRegistries();
		loadLabels();
		loadOpenIssues();
	}

	private void initFromListBox() {
		fromListBox.addItem(" -------- ", "all");
		fromListBox.addItem("Hoy", "today");
		fromListBox.addItem("Esta semana", "thisWeek");
		fromListBox.addItem("Este mes", "thisMonth");
		fromListBox.addItem("Este a\u00F1o", "thisYear");
		fromListBox.setSelectedIndex(0);
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

	private void loadAllIssues() {
		initAllIssues();

		gitHub.getAllIssues(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), new AsyncCallback<JSON<JsIssue>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JSON<JsIssue> result) {
						for (int x = 0; x < result.getData().length(); x++)
							addAllIssue(result.getData().get(x));
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
//		issuePanel.setTagList(tagList);
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
		evalRadioButtons();
		returnButton.setEnabled(false);
	}

	@UiHandler("openIssuesRb")
	void onSelectedOpenIssuesRb(ValueChangeEvent<Boolean> event) {

		if (event.getValue()) {
			closedIssuesRb.removeStyleName(AON.AON_BOLD);
			allIssuesRb.removeStyleName(AON.AON_BOLD);
			openIssuesRb.setStyleName(AON.AON_BOLD);
			loadOpenIssues();
		}
	}

	@UiHandler("closedIssuesRb")
	void onSelectedClosedIssuesRb(ValueChangeEvent<Boolean> event) {

		if (event.getValue()) {
			openIssuesRb.removeStyleName(AON.AON_BOLD);
			allIssuesRb.removeStyleName(AON.AON_BOLD);
			closedIssuesRb.setStyleName(AON.AON_BOLD);
			loadClosedIssues();
		}
	}

	@UiHandler("allIssuesRb")
	void onSelectedAllIssuesRb(ValueChangeEvent<Boolean> event) {

		if (event.getValue()) {
			openIssuesRb.removeStyleName(AON.AON_BOLD);
			closedIssuesRb.removeStyleName(AON.AON_BOLD);
			allIssuesRb.setStyleName(AON.AON_BOLD);
			loadAllIssues();
		}
	}

	@UiHandler("fromListBox")
	void onChangeEventListBox(ChangeEvent event) {

		String name = fromListBox.getSelectedValue();
		Date criteria = null;
		if (name.compareTo("today") == 0)
			criteria = new Date();
		else if (name.compareTo("thisWeek") == 0)
			criteria = DateUtils.getFirstDayOfWorkWeek(new Date());
		else if (name.compareTo("thisMonth") == 0)
			criteria = DateUtils.getFirstDayOfMonth();
		else if (name.compareTo("thisYear") == 0)
			criteria = DateUtils.getFirstDayOfYear();

		gitHub.setSinceCriteria(criteria);
		evalRadioButtons();

	}

	// ******************************************************************
	// ********************** PRIVATE METHODS ***************************
	// ******************************************************************

	void evalRadioButtons() {

		if (openIssuesRb.getValue())
			loadOpenIssues();
		else if (closedIssuesRb.getValue())
			loadClosedIssues();
		else if (allIssuesRb.getValue())
			loadAllIssues();
	}

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

	void initAllIssues() {
		allIssues = new LinkedList<IssueSelected>();
		allIssuesProvider = new ListDataProvider<IssueSelected>();
		allIssuesProvider.addDataDisplay(dataGrid);
		allIssues = allIssuesProvider.getList();
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

	void addAllIssue(JsIssue issue) {
		IssueSelected issueSelected = null;
		if (issue.getState().compareTo(NoticeStatus.OPEN.getValue()) == 0)
			issueSelected = new IssueGrid.IssueOpenLoadSelected(issue);
		else if (issue.getState()
				.compareTo(NoticeStatus.REOPEN.getValue()) == 0)
			issueSelected = new IssueGrid.IssueOpenLoadSelected(issue);
		else if (issue.getState()
				.compareTo(NoticeStatus.CLOSED.getValue()) == 0)
			issueSelected = new IssueGrid.IssueClosedLoadSelected(issue);

		if (issueSelected != null)
			allIssues.add(issueSelected);
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
		this.user.setName(URL.decode(jsUser.getName()));
		this.user.setLogin(URL.decode(jsUser.getLogin()));
	}

	private void addLabels2List(JsLabel jsLabel) {

		DefaultAonTagIssueSelected defaultTag = new DefaultAonTagIssueSelected(
				jsLabel);
		tagList.add(defaultTag);

		Tag tag = new Tag();
		tag.setId(defaultTag.getId());
		tag.setDomain(defaultTag.getDomain());
		tag.setName(defaultTag.getName());
		tag.setType(defaultTag.getType());

		if (defaultTag.getDomain() != 0)
			tagTree.insertTag(tag);
	}

	private void addRegistry2List(JsRegistry jsRegistry) {
		Registry registry = new Registry();
		registry.setId(jsRegistry.getId());
		registry.setAlias(URL.decode(jsRegistry.getAlias()));
		registry.setName(URL.decode(jsRegistry.getName()));
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
		issueReadPanel.setUser(this.user);
		issueReadPanel.setTags(this.tagList);
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
		value.setSender(String.valueOf(notice.getSender().getId()));
		value.setStartDate(fmt.format(notice.getStartDate()));

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
						IssueSelected issue = new IssueGrid.IssueOpenLoadSelected(
								result);
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
						final IssueSelected issue;
						if (result.getState()
								.compareTo(NoticeStatus.REOPEN.getValue()) == 0)
							issue = new IssueGrid.IssueOpenLoadSelected(result);
						else
							issue = new IssueGrid.IssueClosedLoadSelected(
									result);
						onSelectionTitle(issue);
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
						IssueSelected issue = new IssueGrid.IssueOpenLoadSelected(
								result);
						callback.onSucess(issue);
					}
				});
	}

	@Override
	public void onIssueCommentButtonClick(String body,
			final Callback<DefaultAonIssueComments> callback) {
		IssueCommentValue value = new IssueCommentValue();
		value.setBody(body);
		value.setStartDate(fmt.format(new Date()));

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

	@Override
	public void onRemoveLabelFromIssue(final String oldName,
			final String newName,
			final Callback<DefaultAonTagIssueSelected> callback) {

		gitHub.removeLabelFromIssue(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), issueSelected.getId(), oldName,
				new AsyncCallback<JsLabel>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(JsLabel result) {
						issueSelected.deleteTag(oldName);
						callback.onSucess(null);
					}
				});
	}

	@Override
	public void addLabelToAnIssue(List<String> labels) {

		String[] labelsArr = new String[labels.size()];
		for (int x = 0; x < labels.size(); x++)
			labelsArr[x] = labels.get(x);

		final IssueValue prop = new IssueValue();
		prop.setLabels(labelsArr);

		gitHub.addLabelsToAnIssue(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), issueSelected.getId(), prop,
				new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al cargar la incidencia");
					}

					@Override
					public void onSuccess(JsIssue result) {
						IssueSelected issue = new IssueGrid.IssueOpenLoadSelected(
								result);
						openIssues.add(0, issue);
						onSelectionTitle(issue);
					}
				});
	}

	@Override
	public void onReplaceLabelsForIssue(
			List<DefaultAonTagIssueSelected> addLabels,
			List<DefaultAonTagIssueSelected> deletedLabels) {

		String[] addLabelsArr = new String[addLabels.size()];
		String[] delLabelsArr = new String[deletedLabels.size()];

		for (int x = 0; x < addLabels.size(); x++)
			addLabelsArr[x] = addLabels.get(x).getName();

		for (int y = 0; y < deletedLabels.size(); y++)
			delLabelsArr[y] = deletedLabels.get(y).getName();

		final LabelControlValue prop = new LabelControlValue();
		prop.addLabels(addLabelsArr);
		prop.deletedLabels(delLabelsArr);

		gitHub.replaceLabelsForIssue(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), issueSelected.getId(), prop,
				new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error en la gestion de etiquetas");
					}

					@Override
					public void onSuccess(JsIssue result) {
						IssueSelected issue = new IssueGrid.IssueOpenLoadSelected(
								result);
						openIssues.add(0, issue);
						onSelectionTitle(issue);
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
