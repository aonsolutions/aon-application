package com.esferalia.aon.gwt.document.client.nuevo;

import java.util.Arrays;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.Attachment;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.polymer.AonToolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.IronListElement;
import com.vaadin.polymer.paper.PaperDialogElement;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperSliderElement;
import com.vaadin.polymer.paper.PaperTextareaElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;
import com.vaadin.polymer.vaadin.VaadinComboBoxElement;

public class Documental implements EntryPoint {
	
	interface Binder extends UiBinder<Widget, Documental> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	@UiField DockLayoutPanel dockLayoutPanel;
	
	@UiField HTMLPanel toolbar;
	@UiField HTMLPanel configurationPanel;
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField HTMLPanel searchContent;
	@UiField HTMLPanel content;
	
	private AonData aonData;
	private Attachment attachment;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public Documental(AonData aonData) {
		this.aonData = aonData;
	}
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				PaperInputElement.SRC,
				PaperTextareaElement.SRC,
				PaperDialogElement.SRC,
				VaadinComboBoxElement.SRC,
				PaperIconButtonElement.SRC,
				IronListElement.SRC,
				PaperToggleButtonElement.SRC,
				PaperSliderElement.SRC
		));
		
		Polymer.whenReady(o -> {
			startApplication();
			return null;
		});
	}
	
	private void startApplication() {
		GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css().ensureInjected();
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		attachment = new Attachment(GWT.getModuleBaseURL(), "amigo", "user", "zuremoto.aibanez.net");
		
		attachment.getAttachList(new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> arg0) {
				
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				
			}
		});
		createAonToolbar();
		createSearchPanel();
		createAttachListPanel();
	}
	
	private void createAonToolbar(){
		
		toolbar.add(new AonToolbar("Documental") {
			
			@Override protected void onRefreshButtonClick() {}
			
			@Override protected void onMoreOptionButtonClick() {}
			
			@Override protected void onMenuButtonClick() {
				if(dockLayoutPanel.getWidgetSize(configurationPanel) == 0){
					configurationPanel.add(new ConfigurationPanel());
					dockLayoutPanel.setWidgetSize(configurationPanel, 350);
				}
				else {
					configurationPanel.remove(0);
					dockLayoutPanel.setWidgetSize(configurationPanel, 0);
				}	
			}
			
			@Override protected void onEditButtonClick() {}
			
			@Override protected void onDeleteButtonClick() {}
			
			@Override protected void onAddButtonClick() {}

			@Override protected void onInfoButtonClick() {}

			@Override protected void onStatsButtonClick() {}
		}
		.setVisibleEditButton(false)
		.setVisibleDeleteButton(false)
		.setVisibleMoreOptionButton(false)
		.setVisibleInfoButton(false)
		.setVisibleStatsButton(false));
	}
	
	private void createSearchPanel(){
		searchContent.add(new FilterPanel());
	}
	
	private void createAttachListPanel() {
		content.add(new AttachListPanel2());
	}
}
