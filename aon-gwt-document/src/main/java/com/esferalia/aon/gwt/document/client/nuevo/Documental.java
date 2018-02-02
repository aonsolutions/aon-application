package com.esferalia.aon.gwt.document.client.nuevo;

import java.util.Arrays;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.Attachment;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonToolbar;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
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
import com.vaadin.polymer.paper.widget.PaperToggleButton;
import com.vaadin.polymer.vaadin.VaadinComboBoxElement;
import com.vaadin.polymer.vaadin.widget.VaadinUpload;

import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.AonIconsElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;

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
	
	private API API;
	private AonData aonData;
	private Attachment attachment;
	private Documental me = this;;
	
	public API getAPI() {
		return API;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
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
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
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
				PaperSliderElement.SRC,
				AonComboBoxElement.SRC,
				AonIconsElement.SRC,
				"aon-icons/aon-documental-icons.html",
				"iron-icons/maps-icons.html"
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
		
		createAonToolbar();
		createSearchPanel();
		createAttachListPanel();
	}
	
	private void createAonToolbar(){
		
		toolbar.add(new AonToolbar("Documental") {

			@Override protected void onMenuButtonClick() {
				if(dockLayoutPanel.getWidgetSize(configurationPanel) == 0){
					configurationPanel.add(new ConfigurationPanel(me));
					dockLayoutPanel.setWidgetSize(configurationPanel, 350);
				}
				else {
					configurationPanel.remove(0);
					dockLayoutPanel.setWidgetSize(configurationPanel, 0);
				}	
			}
			
			@Override protected void onRefreshButtonClick() {}
			@Override protected void onMoreOptionButtonClick() {}
			@Override protected void onEditButtonClick() {}
			@Override protected void onDeleteButtonClick() {}
			@Override protected void onAddButtonClick() {
				addFileClick();
			}
			@Override protected void onInfoButtonClick() {}
			@Override protected void onStatsButtonClick() {}
			@Override protected void onFastFilterButtonClick() {}
			@Override protected void onTitleClick() {}
			@Override protected void onDownloadButtonClick() {}
			
		}
		.setVisibleEditButton(false)
		.setVisibleDeleteButton(false)
		.setVisibleMoreOptionButton(false)
		.setVisibleInfoButton(false)
		.setVisibleStatsButton(false));
	}
	
	void addFileClick() {
		AonComboBox categoryBox = new AonComboBox();
		categoryBox.setLabel("Categor\u00eda");
		categoryBox.setWidth("100%");
		AonComboBox tagBox = new AonComboBox();
		tagBox.setWidth("100%");
		tagBox.setLabel("Etiqueta");
		AonComboBox scopeBox = new AonComboBox();
		scopeBox.setWidth("100%");
		scopeBox.setLabel("\u00c1mbito");
		
		HorizontalPanel hp = new HorizontalPanel();
		Label confidentialLabel = new Label("Confidencial");
		confidentialLabel.getElement().getStyle().setPaddingTop(20, Unit.PX);
		confidentialLabel.getElement().getStyle().setPaddingRight(10, Unit.PX);
		confidentialLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);		
		hp.add(confidentialLabel);
				
		PaperToggleButton confidential = new PaperToggleButton();
		confidential.getElement().getStyle().setPaddingTop(13, Unit.PX);
		hp.add(confidential);
		
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(categoryBox);
		vp.add(tagBox);
		vp.add(scopeBox);
		vp.add(hp);
		AonDialog dialog = new AonDialog("Nuevo Archivo", vp) {
			
			@Override protected void onCancel() {hide();}
			
			@Override
			protected void onAccept() {
			//	JsObject category = (JsObject) categoryBox.getSelectedItem();
			//	JsObject tag = (JsObject) tagBox.getSelectedItem();
			//	JsObject scope = (JsObject) scopeBox.getSelectedItem();
				for(Integer i = vp.getWidgetCount() - 1 ; i >= 0; i--){
					vp.getWidget(i).removeFromParent();
				}
				VaadinUpload upload = new VaadinUpload();
				String dataRequest = "?domain_name="+ aonData.getDomain().getName() 
						+ "&domain_id="+ aonData.getDomain().getId()
						+ "&login="+ "system"
				//		+ "&category="+ category.getId()
				//		+ "&tag=" + tag.getId()
				//		+ "&scope=" + scope.getId()
						+ "&confidential=" + confidential.getChecked();
				upload.setTarget(GWT.getModuleBaseURL() + "uploadDocumental"+ dataRequest);
				ScrollPanel scroll = new ScrollPanel();
				scroll.setHeight("300px");
				scroll.add(upload);
				this.getAccept().setVisible(false);
				this.getCancel().setVisible(false);
				this.getClose().setVisible(true);
				vp.add(scroll);
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void createSearchPanel(){
		searchContent.add(new FilterPanel());
	}
	
	private void createAttachListPanel() {
		getAPI().getAttachment().getAttachList(new AsyncCallback<JSON<JsAttach>>() {
				
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				content.add(new AttachListPanel2(me, result.getData()));
			}
				
			@Override public void onFailure(Throwable caught) {}
		});   
	}
}
