package net.aonsolutions.aon.gwt.udapa.client.quality;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.vaadin.widget.VaadinUpload;

public class FootPanel extends Composite {

	interface Binder extends UiBinder<Widget, FootPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
		
	
	QualitySheet parent;
	
	public API getAPI() {
		return parent.getAPI();
	}
	
	public FootPanel(QualitySheet parent) {
		this.parent = parent;
		initWidget(binder.createAndBindUi(this));
		imgPanel();
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				Integer value  = event.getSelectedItem();
				if(value == 0){
					imgPanel();
					openFootPanel();
				}
			}
		});
	}

	public void imgPanel() {
		getAPI().getAttachment().getQualityImages(parent.getDataResponse().getId(), new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				VerticalPanel vp = new VerticalPanel();
				vp.setWidth("100%");
				VaadinUpload upload = new VaadinUpload();
				String dataRequest = "?domain_name="+parent.getAonData().getDomain().getName() 
						+ "&domain_id="+ parent.getAonData().getDomain().getId()
						+ "&login="+ parent.getAonData().getUser().getLogin()
						+ "&id="+ parent.getDataResponse().getId()
						+ "&attach_type=" + AttachType.DATA.getName();
				upload.setTarget(GWT.getModuleBaseURL() + "uploadImages"+ dataRequest);
				upload.setAccept("image/*");

				vp.add(upload);
				
				//vp.add(new ImagePanel(result.getData()));
				imgPanel.setWidget(vp);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}

	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabPanel;
	@UiField ScrollPanel imgPanel;
	
	public TabLayoutPanel getTabPanel() {
		return tabPanel;
	}
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
	}
	
	public void openFootPanel() {
		Integer clientHeight = Window.getClientHeight();
		parent.southContentSize(clientHeight.doubleValue() / 3);
		parent.contentSplitLayoutPanel.animate(500);
	}
	
	public void closeFootPanel() {
		parent.southContentSize(30.0);
		parent.contentSplitLayoutPanel.animate(500);
	}
}
