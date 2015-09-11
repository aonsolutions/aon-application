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
import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.esferalia.aon.gwt.office.client.models.repos.JsRepo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Office extends Composite implements EntryPoint, IssueGrid.Listener {
	
	private static OfficeUiBinder uiBinder = GWT.create(OfficeUiBinder.class);

	interface OfficeUiBinder extends UiBinder<Widget, Office> {
	}
	
	@UiField
	SimpleLayoutPanel resultsPanel;
	@UiField
	IssueGrid dataGrid;
	@UiField
	ListBox repoListBox;
	@UiField
	Button newIssue;
	@UiField
	VerticalPanel labelsVPanel;

	@UiField(provided = true)
	SimplePager pager;

	private JsRepo repo;
	private List<IssueSelected> issues;
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

		SimplePager.Resources pagerResources = GWT
				.create(SimplePager.Resources.class);
		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0,
				true);
		pager.setWidth("100%");
		pager.setDisplay(dataGrid);

		this.repositories = new HashMap<Integer, JsRepo>();

		ListDataProvider<IssueSelected> listIssuesProvider = new ListDataProvider<IssueSelected>();
		listIssuesProvider.addDataDisplay(dataGrid);
		this.issues = listIssuesProvider.getList();

		GitHub gitHub = new GitHub();

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
							// TODO Auto-generated method stub

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

		gitHub.getIssues("amtzdelagos", "aon-GwtOffice",
				new AsyncCallback<JSON<JsIssue>>() {

					@Override
					public void onSuccess(JSON<JsIssue> result) {

						int contador = 0;

						while (contador < 500) {

							for (int x = 0; x < result.getData().length(); x++) {
								IssueSelected selected = new IssueGrid.IssueLoadSelected(
										result.getData().get(x));
								issues.add(selected);
							}

							contador++;

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
	void onChangeSelectionListBox(ChangeEvent event) {

		Integer index = Integer.parseInt(repoListBox.getValue(repoListBox
				.getSelectedIndex()));
		Office.this.repo = repositories.get(index);
	}

	@Override
	public void onSelectionChangeHandler(SelectionChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSelectionTitle(IssueSelected issue) {
		NewIssuePopupPanel getIssuePanel = new NewIssuePopupPanel();

		getIssuePanel.setTitle(issue.getTitle());
		getIssuePanel.setBodyTextArea(issue.getBody());

		getIssuePanel.showPopUpPanel();
	}

	// ******************************************************************
	// ********************** PRIVATE METHODS ***************************
	// ******************************************************************

	private void addLabel(int row, JsLabel label) {

		final Button labelButton = new Button();

		labelButton.setText(label.getName());
		labelButton.setTitle(label.getName() + "-" + label.getUrl());
		labelButton.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		
		if (!isWhite(label.getColor()))
			labelButton.getElement().getStyle().setColor("#" + label.getColor());

		labelsVPanel.add(labelButton);
	}
	
	private boolean isWhite(String color) {
		return color == "ffffff";
	}

}
