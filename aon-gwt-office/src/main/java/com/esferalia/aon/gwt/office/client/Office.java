package com.esferalia.aon.gwt.office.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.office.client.models.JSON;
import com.esferalia.aon.gwt.office.client.models.issues.JsIssue;
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.repos.JsRepo;
import com.esferalia.aon.gwt.office.client.values.LabelValue;
import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.office.Tag;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Office extends Composite implements EntryPoint,
		IssueGrid.Listener, IssuePanel.Listener, IssueReadPanel.Listener  {

	private static OfficeUiBinder uiBinder = GWT.create(OfficeUiBinder.class);

	interface OfficeUiBinder extends UiBinder<Widget, Office> {
	}

	@UiField
	Button newIssueButton;
	@UiField
	Button returnButton;
	@UiField
	Button openIssuesButton;
	@UiField
	Button closedIssuesButton;
	@UiField
	DeckLayoutPanel deckPanel;
	@UiField
	ResizeLayoutPanel dockOfficePanel;
	@UiField
	HorizontalPanel readIssuePanel;
	@UiField
	SimpleLayoutPanel resultsPanel;
	@UiField
	IssueGrid dataGrid;	
	
	

	private JsRepo repo;
	private IssuePanel issuePanel;	
	private IssueSelected issueSelected;
	private AonHub gitHub = new AonHub(GWT.getModuleBaseURL() + "api/");
	private List<IssueSelected> openIssues;
	private List<IssueSelected> closedIssues;
	
	private ListDataProvider<IssueSelected> openIssuesProvider;
	private ListDataProvider<IssueSelected> closeIssuesProvider;
	private List<Tag> tagList;
	
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
		
		this.dataGrid.addListener(this);
		this.gitHub.setRepositoryUrl(GWT.getModuleBaseURL() + "api");		

		gitHub.getLabels(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("getLabels(): " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				JsArray<JsLabel> labels = result.getData();
				
				for (int x = 0 ; x < labels.length(); x++)
					addLabels2List(labels.get(x));
			}
		});
		
		loadOpenIssues();
	}
	
	private void loadOpenIssues() {
		
		initOpenIssues();
		gitHub.getOpenIssues("user", "repo", new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Exception getOpenIssues: " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				
				for ( int x = 0; x < result.getData().length(); x++)
					addOpenIssue(result.getData().get(x));
			}
		});

		showDockOfficePanel();
	}
	
	private void loadClosedIssues() {
		initClosedIssues();
		gitHub.getClosedIssues("user", "repo", new AsyncCallback<JSON<JsIssue>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(JSON<JsIssue> result) {
				
				for ( int x = 0; x < result.getData().length(); x++)
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
	void onNewIssueClick (ClickEvent event) {
		issuePanel = new IssuePanel("Nueva Incidencia");
		issuePanel.addListener(this);
		issuePanel.setTagList(tagList);
		issuePanel.showPopupPanel();
	}
	
	@UiHandler("returnButton")
	void onReturnButtonClick(ClickEvent event) {
		showDockOfficePanel();
		returnButton.setEnabled(false);
	}
	
	@UiHandler("openIssuesButton")
	void onOpenIssuesButton(ClickEvent event) {
		loadOpenIssues();
		showDockOfficePanel();
	}
	
	@UiHandler("closedIssuesButton")
	void onClosedIssuesButton(ClickEvent event) {
		loadClosedIssues();
		showDockOfficePanel();
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
		IssueSelected issueSelected = new IssueGrid.IssueOpenLoadSelected(issue);
		openIssues.add(issueSelected);
	}
	
	void addCloseIssue(JsIssue issue) {
		IssueSelected issueSelected = new IssueGrid.IssueClosedLoadSelected(issue);
		closedIssues.add(issueSelected);
	}

	void initCloseIssues() {
		closedIssues = new LinkedList<IssueSelected>();
		closeIssuesProvider = new ListDataProvider<IssueSelected>();
		closeIssuesProvider.addDataDisplay(dataGrid);
		closedIssues = closeIssuesProvider.getList();
	}
	
	private void addLabels2List(JsLabel jsLabel) {
		
		Tag tag = new Tag();
		tag.setId(jsLabel.getId());
		tag.setName(jsLabel.getName());
		tag.setType(jsLabel.getType());
		tag.setColor(jsLabel.getColor());
		
		tagList.add(tag);

	}

	private void showDockOfficePanel() {
		deckPanel.showWidget(dockOfficePanel);
	}
	
	private void showReadIssuePanel() {
		deckPanel.showWidget(readIssuePanel);
	}

	// ******************************************************************
	// ********************** ISSUES DATA GRID **************************
	// ******************************************************************

	@Override
	public void onSelectionTitle(IssueSelected issue) {
		readIssuePanel.clear();
		this.issueSelected = issue;
		IssueReadPanel readPanel = new IssueReadPanel(issue);
		readPanel.addListener(this);
		readIssuePanel.add(readPanel);
		returnButton.setEnabled(true);
		showReadIssuePanel();
	}

	// ******************************************************************
	// ********************** ISSUES PANEL LISTENER *********************
	// ******************************************************************
	
	@Override
	public void onCreateNewTag(final Tag tag) {
		
		LabelValue value = new LabelValue();
		value.setName(tag.getName());
		Byte type = tag.getType();
		value.setType(type.intValue());
		
		gitHub.createLabel(value, new AsyncCallback<JsLabel>() {
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
			
			@Override
			public void onSuccess(JsLabel result) {
				addLabels2List(result);
				issuePanel.addTag(tag);				
			}
		});
	}
	
	@Override
	public void onCreateNewIssue(Notice notice) {
		IssueValue value = new IssueValue();
		value.setTitle(notice.getTitle());
		value.setPriority(notice.getPriority());
		value.setBody(notice.getBody());
		value.setState(notice.getStatus());
		value.setType(notice.getType());
		
		if ( notice.getTags().size() > 0 ) {
	
			String[] labels = new String[notice.getTags().size()];
			for ( int x = 0; x < notice.getTags().size() ; x++) {				
				labels[x] = notice.getTags().get(x).getName();
			}
				
			
			value.setLabels(labels);
		}
		
		gitHub.createIssue(value, new AsyncCallback<JsIssue>() {
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
			
			@Override
			public void onSuccess(JsIssue result) {
				loadOpenIssues();
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
		
		gitHub.editIssue(issueSelected.getJsIssue(), value, new AsyncCallback<JsIssue>() {
			
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


	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;
}
