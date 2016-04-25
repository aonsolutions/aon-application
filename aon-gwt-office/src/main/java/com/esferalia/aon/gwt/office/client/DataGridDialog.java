package com.esferalia.aon.gwt.office.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DataGridDialog extends CustomDialog
		implements DuplicatedDataGrid.Listener {

	interface Listener {

		void onAcceptButtonClick(IssueSelected issue);

		void onLoadDuplicatedIssues(AsyncCallback<List<IssueSelected>> callback);

		void onLoadFaqsClick(AsyncCallback<List<IssueSelected>> callback);
		
		void onCreateNewDuplicated();		
	}

	private static DataGridDialogUiBinder uiBinder = GWT
			.create(DataGridDialogUiBinder.class);

	interface DataGridDialogUiBinder extends UiBinder<Widget, DataGridDialog> {
	}

	@UiField
	HTMLPanel principalPanel;
	@UiField
	DuplicatedDataGrid dataGrid;

	@UiField
	Button duplicatedButton;
	@UiField
	Button faqsButton;
	@UiField
	Button cancelButton;
	@UiField
	Button acceptButton;
	@UiField
	Button newButton;

	private int issueId;
	private IssueSelected issueSelected;

	private List<Listener> listeners;
	private List<IssueSelected> issues;
	private ListDataProvider<IssueSelected> issuesProvider;

	public DataGridDialog(int issueId) {

		setCaption("Seleccione la incidencia padre");
		setWidget(uiBinder.createAndBindUi(this));
		this.setHeight("520px");
		this.setWidth("750px");
		this.setWidget(principalPanel);
		this.getElement().getStyle().setProperty("resize", "none");

		this.issueId = issueId;
		this.listeners = new LinkedList<Listener>();
		this.dataGrid.setEmptyTableWidget(new Label("No hay registros"));
		this.dataGrid.addListener(this);

		initDataGridList();
	}

	void initDataGridList() {
		this.issuesProvider = new ListDataProvider<IssueSelected>();
		this.issues = new LinkedList<IssueSelected>();
		this.issuesProvider.addDataDisplay(dataGrid);
		this.issues = issuesProvider.getList();
	}

	private void insertIssues(List<IssueSelected> list) {
		for (IssueSelected issue : list) {
			if (this.issueId != issue.getId())
				this.issues.add(issue);
		}
	}

	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		this.listeners.remove(listener);
	}

	public void showPopupPanel() {

		for (Listener listener : listeners)
			listener.onLoadFaqsClick(new AsyncCallback<List<IssueSelected>>() {
				
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Error al cargar las FAQs");
				}
				
				@Override
				public void onSuccess(List<IssueSelected> result) {
					insertIssues(result);
				}
			});

		center();
	}
	
	@UiHandler("duplicatedButton")
	void onDuplicatedButtonClick(ClickEvent event) {
		initDataGridList();
		for (Listener listener : listeners) {
			listener.onLoadDuplicatedIssues(new AsyncCallback<List<IssueSelected>>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Error al cargar las Duplicadas");
				}

				@Override
				public void onSuccess(List<IssueSelected> result) {
					insertIssues(result);
				}
			});
		}
	}

	@UiHandler("faqsButton")
	void onFaqsButtonClick(ClickEvent event) {
		initDataGridList();
		for (Listener listener : listeners)
			listener.onLoadFaqsClick(new AsyncCallback<List<IssueSelected>>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Error al cargar las FAQs");
				}

				@Override
				public void onSuccess(List<IssueSelected> result) {
					insertIssues(result);
				}
			});
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		if (this.issueSelected != null)
			for (Listener listener : listeners)
				listener.onAcceptButtonClick(this.issueSelected);
		hide();
	}
	
	@UiHandler("newButton")
	void onCreateNewDuplicatedClick(ClickEvent event) {
		for (Listener listener : listeners)
			listener.onCreateNewDuplicated();
		hide();
	}

	// ******************************************************************
	// ***************************** DATA GRID **************************
	// ******************************************************************

	@Override
	public void onEnabledAcceptButton(boolean enable) {
		newButton.setEnabled(!enable);
		acceptButton.setEnabled(enable);
	}

	@Override
	public void onIssueSelection(IssueSelected issue) {
		this.issueSelected = issue;
	}
	
	public void setCreateNewButtonVisible(boolean visible) {
		newButton.setVisible(visible);
	}
}
