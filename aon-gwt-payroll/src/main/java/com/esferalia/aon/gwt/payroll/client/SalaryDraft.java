package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class SalaryDraft extends ResizeComposite {
	
	private static final int DEFAULT_ZOOM = 135;

	interface Binder extends UiBinder<Widget, SalaryDraft> { }
	private static final Binder binder = GWT.create(Binder.class);

	@UiField Button printButton;
	
	@UiField MenuItem printMenuItem;
	@UiField MenuItem downloadMenuItem;
	
	@UiField MenuItem reduceMenuItem;
	@UiField MenuItem enlargeMenuItem;

	@UiField HTML 	container;
	
	
	private int zoom = DEFAULT_ZOOM;
	private ISalaryDraft salaryDraft;
	
	public SalaryDraft() {
		initWidget(binder.createAndBindUi(this));	
	}
	
	
	public void setSalaryDraft(ISalaryDraft salaryDraft) {
		this.salaryDraft = salaryDraft;
		onSalaryDraftChanged();
	}
	
	
	private void onSalaryDraftChanged() {
		getAsHTML();
	}
	
	private void getAsHTML() {
		salaryDraft.getAsHTML(zoom, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String html) {
				container.setHTML(html);
			}
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				container.setHTML(caught.getLocalizedMessage());
			}
		});

	}
	
	
	
}
