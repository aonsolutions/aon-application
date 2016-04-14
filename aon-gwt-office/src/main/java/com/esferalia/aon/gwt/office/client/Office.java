package com.esferalia.aon.gwt.office.client;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.ContextMenu;
import com.esferalia.aon.gwt.office.client.IssueReadPanel.Callback;
import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.AonJsData;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssueComment;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.repos.JsRMedia;
import com.esferalia.aon.gwt.office.client.models.repos.JsRegistry;
import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.esferalia.aon.gwt.office.client.notification.INotification;
import com.esferalia.aon.gwt.office.client.notification.INotificationAsync;
import com.esferalia.aon.gwt.office.client.notification.JsNotification;
import com.esferalia.aon.gwt.office.client.notification.NotificationDialog;
import com.esferalia.aon.gwt.office.client.values.IssueCommentValue;
import com.esferalia.aon.gwt.office.client.values.LabelControlValue;
import com.esferalia.aon.gwt.office.client.values.LabelValue;
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.NotificationType;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class Office extends Composite implements EntryPoint, IssueGrid.Listener,
		IssuePanel.Listener, IssueReadPanel.Listener, TagTree.Listener, 
		SearchPanel.Listener, DataGridDialog.Listener, ScrollHandler {

	private static OfficeUiBinder uiBinder = GWT.create(OfficeUiBinder.class);

	final INotificationAsync impl = GWT.create(INotification.class);
	
	interface OfficeUiBinder extends UiBinder<Widget, Office> {
	}
	
	@UiField
	RadioButton openIssuesRb;
	@UiField
	RadioButton closedIssuesRb;
	@UiField
	RadioButton allIssuesRb;
	@UiField
	RadioButton faqIssuesRb;

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	Button newIssueButton;
	@UiField
	Button tagButton;
	@UiField
	Button returnButton;
	@UiField
	Button clearButton;
	@UiField 
	Button configurationButton;
	@UiField
	Button refreshButton;
	
	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	DockLayoutPanel dockOfficePanel;
	@UiField
	SimpleLayoutPanel readIssueLayoutPanel;
	@UiField
	ResizeLayoutPanel readIssuePanel;

	@UiField
	IssueGrid dataGrid;

	@UiField
	StackLayoutPanel stackLayoutPanel;

	@UiField
	SearchPanel searchPanel;
	@UiField
	TagTree tagTree;

	/**
	 * The last scroll position
	 */

	private static final int DEFAULT_INCREMENT = 50;
	private int lastScrollPos = 0;
	private int incrementSize = 0;

	private User user;
	private IssuePanel issuePanel;
	private String registrySelected;

	private IssueSelected issueSelected;
	private AonHub gitHub = new AonHub(GWT.getModuleBaseURL() + "api/");

	private List<IssueSelected> issues;
	private ListDataProvider<IssueSelected> issuesProvider;

	private List<AonTagIssueSelected> tagList;	
	private List<RegistryMedia> rmedias;
	private List<User> users;

	private Map<Integer, Registry> registryMap;
	private Map<Integer, JsIssue> issuesMap;

	private DateTimeFormat fmt = DateTimeFormat
			.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public Office() {
		Widget ui = uiBinder.createAndBindUi(this);

		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();

		this.users = new LinkedList<User>();
		this.tagList = new LinkedList<AonTagIssueSelected>();
		this.registryMap = new HashMap<Integer, Registry>();
		this.rmedias = new LinkedList<RegistryMedia>();
		this.searchPanel.addListener(this);
		this.dataGrid.addListener(this);
		this.dataGrid.getScrollPanel().addScrollHandler(this);
		this.dataGrid.setEmptyTableWidget(new Label("No hay registros"));
		this.tagTree.addListener(this);
		this.gitHub.setRepositoryUrl(GWT.getModuleBaseURL() + "api");
		
		gitHub.loadRepositoryData(String.valueOf(getCurrentDomain()), getCurrentDomainName(), new AsyncCallback<AJSON<AonJsData>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Datos iniciales no cargados");
			}
			
			@Override
			public void onSuccess(AJSON<AonJsData> result) {
				JsUser current = result.getData().getCurrentUser();
				JsArray<JsUser> users = result.getData().getUsers();
				JsArray<JsRMedia> rmedias = result.getData().getRMedias();
				JsArray<JsRegistry> registries = result.getData().getRegistries();
				
				userIdentificated(current);
				
				for ( int x = 0; x < users.length(); x++)
					addUser2List(users.get(x));
				
				for ( int x = 0 ; x < rmedias.length(); x++)
					addRMedia2List(rmedias.get(x));
				
				for (int x = 0 ; x < registries.length(); x++)
					addRegistry2List(registries.get(x));
				
				Office.this.searchPanel.addUserList(Office.this.users);
			}			
		});

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
						
						Office.this.searchPanel.addTagList(tagList);
					}
				});

		initIssuesList();
		loadOpenIssues();
	}

	void loadOpenIssues() {
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
						
						openIssuesRb.setText(AON.AONHUB.openIssues() + " (" + (result.getCount()) + ")");
						allIssuesRb.setText(AON.AONHUB.allIssues());						
						closedIssuesRb.setText(AON.AONHUB.closedIssues());
						faqIssuesRb.setText(AON.AONHUB.FAQ());

					}
				});
		showDockOfficePanel();
	}

	void loadClosedIssues() {
		gitHub.getClosedIssues(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), new AsyncCallback<JSON<JsIssue>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JSON<JsIssue> result) {

						for (int x = 0; x < result.getData().length(); x++)
							addClosedIssue(result.getData().get(x));

						closedIssuesRb.setText(AON.AONHUB.closedIssues() + " (" + result.getCount() + ")");	
						allIssuesRb.setText(AON.AONHUB.allIssues());
						openIssuesRb.setText(AON.AONHUB.openIssues());
						faqIssuesRb.setText(AON.AONHUB.FAQ());
					}
				});
		showDockOfficePanel();
	}

	void loadAllIssues() {
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
						
						allIssuesRb.setText(AON.AONHUB.allIssues() + " (" + result.getCount() + ")");
						openIssuesRb.setText(AON.AONHUB.openIssues());
						closedIssuesRb.setText(AON.AONHUB.closedIssues());
						faqIssuesRb.setText(AON.AONHUB.FAQ());
					}
				});
		showDockOfficePanel();
	}
	
	void loadFaqIssues() {
		gitHub.getFaqIssues(String.valueOf(getCurrentDomain()),
				getCurrentDomainName(), new AsyncCallback<JSON<JsIssue>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(JSON<JsIssue> result) {
						for (int x = 0; x < result.getData().length(); x++)
							addFAQIssue(result.getData().get(x));
						
						faqIssuesRb.setText(AON.AONHUB.allIssues() + " (" + result.getCount() + ")");
						openIssuesRb.setText(AON.AONHUB.openIssues());
						closedIssuesRb.setText(AON.AONHUB.closedIssues());
					}
				});
		showDockOfficePanel();
	}


	@Override
	public void onScroll(ScrollEvent event) {

		int scrollPos = dataGrid.getScrollPanel().getVerticalScrollPosition();
		if (lastScrollPos >= scrollPos) {
			lastScrollPos = scrollPos;
			return;
		}

		lastScrollPos = scrollPos;
		int maxScrollPos = dataGrid.getScrollPanel()
				.getMaximumVerticalScrollPosition();
		if ((lastScrollPos + (maxScrollPos / 10)) >= maxScrollPos) {
			gitHub.setOffset(incrementSize + DEFAULT_INCREMENT);
			evalRadioButtons();
			incrementSize += 50;
			dataGrid.setVisibleRange(0,
					dataGrid.getVisibleRange().getLength() + incrementSize);
		}
	}

	// ******************************************************************
	// *************************** UI - HANDLERS ************************
	// ******************************************************************

	@UiHandler("newIssueButton")
	void onNewIssueClick(ClickEvent event) {
		issuePanel = new IssuePanel(this.user);
		issuePanel.addListener(this);

		if (registryMap.size() > 0)
			issuePanel.setRegistries(registryMap);

		if (rmedias.size() > 0)
			issuePanel.setRMedias(rmedias);

		if (registrySelected != null)
			issuePanel.setSender(this.registrySelected);

		issuePanel.showNoticePanel();
	}

	@UiHandler("tagButton")
	void onTagButtonClick(ClickEvent event) {
		dockLayoutPanel.setWidgetSize(Office.this.stackLayoutPanel, 220);
	}

	@UiHandler("returnButton")
	void onReturnButtonClick(ClickEvent event) {
		initIssuesList();
		this.incrementSize = 0;
		this.gitHub.setOffset(incrementSize);
		evalRadioButtons();
		returnButton.setEnabled(false);
	}
	
	@UiHandler("openIssuesRb")
	void onOpenIssuesRbSelected(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			closedIssuesRb.removeStyleName(AON.AON_BOLD);
			allIssuesRb.removeStyleName(AON.AON_BOLD);
			faqIssuesRb.removeStyleName(AON.AON_BOLD);
			openIssuesRb.addStyleName(AON.AON_BOLD);
			this.incrementSize = 0;
			this.gitHub.setOffset(incrementSize);
			initIssuesList();
			loadOpenIssues();
		}
	}

	@UiHandler("closedIssuesRb")
	void onClosedIssuesRbSelected(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			openIssuesRb.removeStyleName(AON.AON_BOLD);
			allIssuesRb.removeStyleName(AON.AON_BOLD);
			faqIssuesRb.removeStyleName(AON.AON_BOLD);
			closedIssuesRb.addStyleName(AON.AON_BOLD);
			this.incrementSize = 0;
			this.gitHub.setOffset(incrementSize);
			initIssuesList();
			loadClosedIssues();
		}
	}

	@UiHandler("allIssuesRb")
	void onAllIssuesRbSelected(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			openIssuesRb.removeStyleName(AON.AON_BOLD);
			closedIssuesRb.removeStyleName(AON.AON_BOLD);
			faqIssuesRb.removeStyleName(AON.AON_BOLD);
			allIssuesRb.addStyleName(AON.AON_BOLD);
			this.incrementSize = 0;
			this.gitHub.setOffset(incrementSize);
			initIssuesList();
			loadAllIssues();
		}
	}
	
	@UiHandler("faqIssuesRb")
	void onFaqIssuesRbSelected(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			openIssuesRb.removeStyleName(AON.AON_BOLD);
			closedIssuesRb.removeStyleName(AON.AON_BOLD);
			allIssuesRb.removeStyleName(AON.AON_BOLD);
			faqIssuesRb.setStyleName(AON.AON_BOLD);
			this.incrementSize = 0;
			this.gitHub.setOffset(incrementSize);
			initIssuesList();
			loadFaqIssues();
		}
	}

	@UiHandler("clearButton")
	void onClearButtonClickEvent(ClickEvent event) {
		searchPanel.cleanFilterPanels();
		this.incrementSize = 0;
		this.gitHub.setOffset(incrementSize);
		this.gitHub.setFilterTagList(null);
		this.gitHub.setText(null);
		this.gitHub.setSender(null);
		initIssuesList();
		evalRadioButtons();
	}
	
	@UiHandler("refreshButton")
	void onRefreshButtonClickEvent(ClickEvent event) {
		this.incrementSize = 0;
		this.gitHub.setOffset(incrementSize);
		initIssuesList();
		evalRadioButtons();
	}

	@UiHandler("configurationButton")
	void onConfigurationButtonClickEvent(ClickEvent event){
		ConfigurationContextMenu contextMenu = new ConfigurationContextMenu();
    	Integer width = contextMenu.getWidth();
    	contextMenu.setPopupPosition(Window.getClientWidth()-width , 130);
    	contextMenu.getElement().getStyle().setBorderWidth(1, Unit.PX);
    	contextMenu.getElement().getStyle().setBorderColor("#ccc");
    	contextMenu.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
    	contextMenu.show();
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
		else if (faqIssuesRb.getValue())
			loadFaqIssues();
	}

	void initIssuesList() {
		this.issues = new LinkedList<IssueSelected>();
		this.issuesProvider = new ListDataProvider<IssueSelected>();
		this.issuesProvider.addDataDisplay(dataGrid);
		this.issues = issuesProvider.getList();
	}

	void addOpenIssue(JsIssue issue) {
		IssueSelected selected = new IssueGrid.IssueOpenLoadSelected(issue);
		issues.add(selected);
	}

	void addClosedIssue(JsIssue issue) {
		IssueSelected selected = new IssueGrid.IssueClosedLoadSelected(issue);
		issues.add(selected);
	}
	
	void addFAQIssue(JsIssue issue) {
		IssueSelected selected = new IssueGrid.IssueFAQLoadSelected(issue);
		issues.add(selected);
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
			issues.add(issueSelected);
	}

	void userIdentificated(JsUser jsUser) {
		this.user = new User();
		this.user.setId(jsUser.getId());
		this.user.setName(URL.decode(jsUser.getName()));
		this.user.setLogin(URL.decode(jsUser.getLogin()));
	}

	private void addLabels2List(JsLabel jsLabel) {

		AonTagIssueSelected defaultTag = new AonTagIssueSelected(
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
	
	private void addUser2List(JsUser jsUser) {
		User user = new User();
		user.setId(jsUser.getId());
		user.setName(URL.decode(jsUser.getName()));
		user.setLogin(URL.decode(jsUser.getLogin()));
		users.add(user);

	}

	private void addRegistry2List(JsRegistry jsRegistry) {
		Registry registry = new Registry();
		registry.setId(jsRegistry.getId());
		registry.setAlias(URL.decode(jsRegistry.getAlias()));
		registry.setName(URL.decode(jsRegistry.getName()));
		registry.setDocument(jsRegistry.getDocument());

		this.registryMap.put(registry.getId(), registry);
		this.searchPanel.addRegistry(registry);
	}

	private void addRMedia2List(JsRMedia jsRMedia) {
		RegistryMedia rmedia = new RegistryMedia();
		rmedia.setId(jsRMedia.getId());
		rmedia.setDomain(jsRMedia.getDomain());
		rmedia.setMedia(jsRMedia.getMedia());
		rmedia.setValue(URL.decode(jsRMedia.getValue()));
		rmedia.setComment(URL.decode(jsRMedia.getComment()));

		Registry registry = new Registry();
		registry.setId(jsRMedia.getRegistry().getId());
		registry.setAlias(URL.decode(jsRMedia.getRegistry().getAlias()));
		registry.setName(URL.decode(jsRMedia.getRegistry().getName()));
		registry.setDocument(jsRMedia.getRegistry().getDocument());

		rmedia.setRegistry(registry);
		rmedias.add(rmedia);
	}

	private void showDockOfficePanel() {
		deckPanel.showWidget(dockOfficePanel);
	}

	private void showReadIssuePanel() {
		deckPanel.showWidget(readIssuePanel);
	}

	// ******************************************************************
	// ************************* SEARCH PANEL ***************************
	// ******************************************************************

	public void onSelectCompany(String company) {
		this.gitHub.setSender(company);
		this.incrementSize = 0;
		this.gitHub.setOffset(incrementSize);
		initIssuesList();
		evalRadioButtons();
	}
	
	@Override
	public void onSelectSubject(String subject) {	
		this.gitHub.setText( (subject.trim().isEmpty()) ? null : subject );
		this.incrementSize = 0;
		this.gitHub.setOffset(incrementSize);
		initIssuesList();
		evalRadioButtons();
	}
	
	@Override
	public void onCloseTagPanel(List<Tag> tags) {		

		if (tags.size() == 0)
			this.gitHub.setFilterTagList(null);
		else {
			String[] tagArr = new String[tags.size()];
			for (int x = 0; x < tags.size(); x++)
				tagArr[x] = String.valueOf(tags.get(x).getName());

			this.gitHub.setFilterTagList(tagArr);
		}

		this.incrementSize = 0;
		this.gitHub.setOffset(incrementSize);
		initIssuesList();
		evalRadioButtons();
	}
	
	@Override
	public void onSelectFrom(Date from) {		
		this.incrementSize = 0;
		this.gitHub.setOffset(incrementSize);
		this.gitHub.setSinceCriteria(from);
		initIssuesList();
		evalRadioButtons();
	}
	
	@Override
	public void onSelectOwner(String userSelected) {
		this.gitHub.setFilterUserList(userSelected);	
		this.incrementSize = 0;
		this.gitHub.setOffset(incrementSize);
		initIssuesList();
		evalRadioButtons();
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
		IssueReadPanel issueReadPanel = new IssueReadPanel(this.user, issue);		
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
						issues.add(0, issue);
						onSelectionTitle(issue);
						sendNotification(null, issue, NotificationType.OPEN);
					}
				});
	}
	
	@Override
	public void onCreateNewFAQ(Notice notice) {
		IssueValue value = new IssueValue();
		value.setTitle(notice.getTitle());
		value.setSender(String.valueOf(notice.getSender().getId()));
		value.setStartDate(fmt.format(notice.getStartDate()));
		
		gitHub.createFAQ(String.valueOf(getCurrentDomain()), getCurrentDomainName(), value, 
				new AsyncCallback<JsIssue>() {
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
			
			@Override
			public void onSuccess(JsIssue result) {
				IssueSelected issue = new IssueGrid.IssueFAQLoadSelected(result);
				issues.add(0, issue);
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
			
		sendNotification(null, issueSelected, getNotificationType(state));

	}
	
	@Override
	public void onDuplicateClickEvent() {
		DataGridDialog dataGridDialog = new DataGridDialog(issueSelected.getId());
		dataGridDialog.addListener(this);
		dataGridDialog.insertIssues(issues);
		dataGridDialog.showPopupPanel();
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
			final Callback<AonIssueComments> callback) {
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
						AonIssueComments comment = new AonIssueComments(
								result);
						issueSelected.addComment(result);
						callback.onSucess(comment);
					}
				});

		sendNotification(body, issueSelected, NotificationType.NEW_INFO);
	}

	@Override
	public void onUpdateIssueComment(final Integer id, final String body,
			final Callback<AonIssueComments> callback) {
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
						AonIssueComments comment = issueSelected
								.editComment(result);
						callback.onSucess(comment);
					}
				});
	}

	@Override
	public void onRemoveLabelFromIssue(final String oldName,
			final String newName,
			final Callback<AonTagIssueSelected> callback) {

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
						issues.add(0, issue);
						onSelectionTitle(issue);
					}
				});
	}

	@Override
	public void onReplaceLabelsForIssue(
			List<AonTagIssueSelected> addLabels,
			List<AonTagIssueSelected> deletedLabels) {

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
						issues.add(0, issue);
						onSelectionTitle(issue);
					}
				});		
	}

	// ******************************************************************
	// ******************** DATA GRID DIALOG LISTENER *******************
	// ******************************************************************
	
	@Override
	public void onAcceptButtonClick(IssueSelected issue) {		
		/*
		 * issue: Incidencia seleccionada del data grid.
		 * 
		 */
		
		IssueValue prop = new IssueValue();
		prop.setId(String.valueOf(issueSelected.getId()));
		
		this.gitHub.addDuplicateNotice(String.valueOf(getCurrentDomain()), getCurrentDomainName(), 
				issue.getId(), prop, new AsyncCallback<JsIssue>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al duplicar la incidencia: " + caught.getMessage());
					}

					@Override
					public void onSuccess(JsIssue result) {
						IssueSelected issue = new IssueGrid.IssueOpenLoadSelected(
								result);
						issues.add(0, issue);
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

	private void sendNotification(String body, IssueSelected issueSelected, NotificationType notificationType) {
		LinkedList<NotificationInfo> list = new LinkedList<NotificationInfo>();
		if(!notificationType.equals(NotificationType.OPEN)){
			for(AonTagIssueSelected tag : issueSelected.getTags()){
				if(getNotificationType(tag.getName()) != null ){
					list.add(new NotificationInfo().setNoticeId(issueSelected.getId())
						.setTitle(issueSelected.getTitle())
						.setDate(tag.getCreateAt())
						.setUserName(tag.getUser().getName())
						.setNotificationType(getNotificationType(tag.getName())));
				}
			}
			for(AonIssueComments comments :issueSelected.getComments()){
				list.add(new NotificationInfo().setBody(comments.getBody())
					.setNoticeId(issueSelected.getId()) 
					.setTitle(issueSelected.getTitle())
					.setDate(comments.getCreatedAt())
					.setUserName(comments.getUser().getName())
					.setNotificationType(NotificationType.NEW_INFO));
			}
		}
		NotificationInfo n = new NotificationInfo().setNoticeId(issueSelected.getId())
				.setTitle(issueSelected.getTitle())
				.setBody(body)
				.setDate(new Date())
				.setUserName(issueSelected.getUser().getName())
				.setCompanyName(issueSelected.getCompany());
		
		Domain domain  = new Domain().setName(getCurrentDomainName()).setId(getCurrentDomain());
	
		impl.sendNotification(domain, n, list, notificationType, false, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {}
			
			@Override
			public void onSuccess(Void result) {}
			
		});
	}
	
	private NotificationType getNotificationType(String state) {
		if(state.equalsIgnoreCase("open") ||state.equalsIgnoreCase("abierto")){
			return NotificationType.OPEN;
		}else 	if(state.equalsIgnoreCase("reopen") ||state.equalsIgnoreCase("reabierto")){
			return NotificationType.REOPEN;
		}else 	if(state.equalsIgnoreCase("closed") ||state.equalsIgnoreCase("cerrado")){
			return NotificationType.CLOSE;
		}
		return null;

	}
	// ******************************************************************
	// ******************* CONFIGURATION CONTEXT MENU *******************
	// ******************************************************************
	
	class ConfigurationContextMenu extends ContextMenu {

		ScheduledCommand NotificationCommand = new ScheduledCommand() {
			public void execute() {
				NotificationDialog dialog = new NotificationDialog() {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						ListBox lb1 = (ListBox) flex_table.getWidget(0, 1);
						ListBox lb2 = (ListBox) flex_table.getWidget(1, 1);
						CheckBox cb1 = (CheckBox) flex_table.getWidget(2, 0);
						CheckBox cb2 = (CheckBox) flex_table.getWidget(3, 0);
						TextBox tb = (TextBox) flex_table.getWidget(4, 1);
						ListBox lb3 = (ListBox) flex_table.getWidget(5, 1);
						
						NotificationInfo notificationInfo = new NotificationInfo()
								.setBcc(tb.getValue())
								.setHistory(cb2.getValue())
								.setMailAccount(new MailAccount().setId(lb1.getValue(lb1.getSelectedIndex()) != "-" ? 
										Integer.parseInt(lb1.getValue(lb1.getSelectedIndex())): null))
								.setSignature(new Signature().setId(lb2.getValue(lb2.getSelectedIndex()) != "-" ?
										Integer.parseInt(lb2.getValue(lb2.getSelectedIndex())): null))
								.setNotify(cb1.getValue())
								.setMode(Integer.parseInt(lb3.getValue(lb3.getSelectedIndex())));
						impl.insertNotificationInfo(JsNotification.getDomain(), notificationInfo, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								hide();
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						});
						
					}
				};
				dialog.addStyleName("gwt-PopupPanel-template");
				dialog.setGlassEnabled(true);
				dialog.show();
			};
		};
		
		ScheduledCommand TagCommand = new ScheduledCommand() {
			public void execute() {
				dockLayoutPanel.setWidgetSize(Office.this.stackLayoutPanel, 220);
			};
		};
		
		ScheduledCommand FAQsCommand = new ScheduledCommand() {
			public void execute() {
				Office.this.issuePanel = new IssuePanel(Office.this.user);				
				Office.this.issuePanel.addListener(Office.this);
				Office.this.issuePanel.showFAQPanel();
			};
		};

		private MenuItem tagItem;
		private MenuItem notificationItem;
		private MenuItem faqsItem;

		private Integer heigth;
		private Integer width;

		public Integer getHeigth() {
			heigth = 74;
			return heigth;
		}

		public void setHeigth(Integer heigth) {
			this.heigth = heigth;
		}

		public Integer getWidth() {
			width = 117;
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}
		
		public ConfigurationContextMenu(){
			tagItem = addItem("Etiquetas", TagCommand
					,"aon-icon-tag", AON.AON_ICON_CMD_BUTTON);
			tagItem.setEnabled(true);
			
			notificationItem = addItem("Notificaciones", NotificationCommand 
					,"aon-icon-notification", AON.AON_ICON_CMD_BUTTON);
			notificationItem.setEnabled(true);
			
			addSeparator();
			
			faqsItem = addItem("FAQs", FAQsCommand
					,"aon-icon-menu-help", AON.AON_ICON_CMD_BUTTON);
			faqsItem.setEnabled(true);
		}

		@Override
		public void show() {
			super.show();
		}
	
}
}

