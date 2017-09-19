package net.aonsolutions.aon.gwt.invoice.client.sabbatic;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.gwt.common.client.polymer.AonTemplate2;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.vaadin.polymer.vaadin.widget.VaadinUpload;

public class SabbaticMain extends AonTemplate2{

	private API API;
	HashMap<String, LinkedList<String>> filterMap;
	private AonData aonData;
	
	public API getAPI() {
		return API;
	}
	
	public AonData getAonData() {
		return aonData;
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}
	
	public SabbaticMain(AonData aonData) {
		this.aonData = aonData;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getUser().getLogin());
	}
	
	@Override
	public void onModuleLoad() {
		super.onModuleLoad();
		startApplication();
	}
	
	private void startApplication() {
		initializeFilterMap();
		toolbar();
		westContent();
		content();
	}
	
	public void initializeFilterMap() {
		filterMap = new HashMap<>();
	}

	Button newButton;
	Button sendButton;
	Button removeButton;
	private void toolbar() {
		getDockLayoutPanel().setWidgetSize(getToolbar(), 23);
		Toolbar toolbar = new Toolbar("Sabbatic") {};
		
		newButton = toolbar.addButton(AON.MSG.newAction(), AON.AON_CSS.aonIconReset());
		newButton.setVisible(true);
		newButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				VaadinUpload upload = new VaadinUpload();
				upload.setTarget(GWT.getModuleBaseURL() + "uploadImages");
				upload.setAccept(MimeType.PDF.getName());
				
				AonDialog dialog = new AonDialog("Subir Facturas", upload) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						// TODO ACTUALIZAR LISTA!!!
					}
				};
				dialog.setAutoHideEnabled(true);
				dialog.getElement().getStyle().setWidth(310, Unit.PX);
				dialog.center();
			}
		});
		
		sendButton = toolbar.addButton(AON.MSG.uploaderSend(), AON.AON_CSS.aonIconSave());
		sendButton.setVisible(false);
		sendButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SabbaticPrincipal p = (SabbaticPrincipal) getContent().getWidget();	
				SimpleLayoutPanel slp = p.getContent();
				SabbaticGrid ig = (SabbaticGrid) slp.getWidget();
				ig.sendAction(); // TODO
			}
		});
		removeButton = toolbar.addButton(AON.MSG.deleteAction(), "aon-icon-removed");
		removeButton.setVisible(false);
		removeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SabbaticPrincipal p = (SabbaticPrincipal) getContent().getWidget();
				SimpleLayoutPanel slp = p.getContent();
				SabbaticGrid ig = (SabbaticGrid) slp.getWidget();
				ig.removeAction(); // TODO
			}
		});	
		setToolbar(toolbar);
	}
	
	public Button getSendButton() {
		return sendButton;
	}
	
	public Button getRemoveButton() {
		return removeButton;
	}
	
	private void westContent() {

	}
	
	private void content() {
		setContent(new SabbaticPrincipal(this));
	}

}
