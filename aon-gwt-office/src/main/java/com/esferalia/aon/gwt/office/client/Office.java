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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Office extends Composite implements EntryPoint,
		IssueGrid.Listener, IssuePanel.Listener  {

	private static OfficeUiBinder uiBinder = GWT.create(OfficeUiBinder.class);

	interface OfficeUiBinder extends UiBinder<Widget, Office> {
	}

	@UiField
	Button newIssueButton;
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

	private JsRepo repo;
	private IssuePanel issuePanel;
	private IssueSelected issueSelected;
	private AonHub gitHub = new AonHub(GWT.getModuleBaseURL() + "api/");
	private List<IssueSelected> openIssues;
	private List<IssueSelected> closedIssues;
	
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
		
		initOpenIssues();
//		initCloseIssues();		

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
	// *************************** UI - HANDLERS ************************
	// ******************************************************************
	
	@UiHandler("newIssueButton")
	void onNewIssueClick (ClickEvent event) {
		issuePanel = new IssuePanel("Nueva Incidencia");
		issuePanel.addListener(this);
		issuePanel.setTagList(tagList);
		issuePanel.showPopupPanel();
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
	}


	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;
}
