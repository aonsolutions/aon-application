package net.aonsolutions.aon.gwt.udapa.client.quality;

import com.esferalia.aon.gwt.api.client.API;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
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
					openFootPanel();
					imgPanel();
				}
			}
		});
	}
	
	public void imgPanel() {
		
		VaadinUpload upload = new VaadinUpload();
		String dataRequest = "?domain_name="+parent.getAonData().getDomain().getName() 
				+ "&domain_id="+ parent.getAonData().getDomain().getId()
				+ "&login="+ parent.getAonData().getUser().getLogin()
				+ "&id="+ parent.getDataResponse().getId()
				+ "&attach_type=" + AttachType.DATA.getName();
		upload.setTarget(GWT.getModuleBaseURL() + "uploadImages"+ dataRequest);
		upload.setAccept("image/*");
		
		
		imgPanel.setWidget(upload); //new ImagePanel(parent));
	}

	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabPanel;
	@UiField SimpleLayoutPanel imgPanel;
	
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
