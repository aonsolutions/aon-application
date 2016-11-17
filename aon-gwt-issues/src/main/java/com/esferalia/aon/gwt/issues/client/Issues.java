package com.esferalia.aon.gwt.issues.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.IssueFilter;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.polymer.AonToolbar;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.issues.shared.AonData;
import com.esferalia.aon.occam.api.model.office.NotificationType;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.IronListElement;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.PaperDialogElement;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperSliderElement;
import com.vaadin.polymer.paper.PaperTextareaElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperTextarea;
import com.vaadin.polymer.vaadin.VaadinComboBoxElement;

import net.aonsolutions.polymer.aon.AonComboBoxElement;
import net.aonsolutions.polymer.aon.widget.AonComboBox;
import net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent;
import net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler;

public class Issues implements EntryPoint {
	
	interface Binder extends UiBinder<Widget, Issues> {

	}
	private static final Binder binder = GWT.create(Binder.class);

	final IIssuesAsync serv = GWT.create(IIssues.class);
	
	private static final String HTTP = "http://";
	private static final String HTTPS = "https://";
	
	@UiField HTMLPanel searchContent;
	@UiField HTMLPanel content;
	@UiField HTMLPanel issueContent;
	@UiField HTMLPanel toolbar;
	@UiField HTMLPanel configurationPanel;
	@UiField DockLayoutPanel dockLayoutPanel;
	@UiField DockLayoutPanel contentDockLayoutPanel;
	
	IssueFilter issueFilter;
	Issues me;
	AonData aonData;
	Boolean more = true;

	private Incidence incidence;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				PaperInputElement.SRC,
				PaperTextareaElement.SRC,
				PaperDialogElement.SRC,
				VaadinComboBoxElement.SRC,
				AonComboBoxElement.SRC,
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
		me = this;
		serv.getAonData(getCurrentDomainName(), getCurrentDomain(), new AsyncCallback<AonData>() {
			
			@Override
			public void onSuccess(AonData result) {
				aonData = result;
				String h = GWT.getModuleBaseURL().contains("https") ? HTTPS : HTTP; 
				incidence = new Incidence(h+result.getDomain().getName()+"/", result.getMd5(),
						result.getLoggedUser(), result.getLoggedUser(), result.getDomain().getName());
				createAonToolbar();
				createFilterPanel(new FilterPanel(me, incidence));
				createIssueList(issueFilter = new IssueFilter());
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}

	protected void createFilterPanel(FilterPanel filterPanel){
		searchContent.add(filterPanel);
	}
	
	protected void createIssueList(IssueFilter filter) {
		filter.setState("open");
		incidence.getOrgIssues(filter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {	
				IssueList issueList = new IssueList(me, incidence, result.getData());
				content.add(issueList);
				
				FilterPanel fp = (FilterPanel) searchContent.getWidget(0);	
				fp.setButtonsLabels(result.getMeta());
			}
			
			@Override
			public void onFailure(Throwable arg0) {	}
		});
	}

	protected void updateIssueList(AonJsArray<JsIssue> array){
		IssueList issueList = (IssueList) content.getWidget(0);
		issueList.updateItems(array);
	}
	
	protected void showMoreupdateIssueList(AonJsArray<JsIssue> array){
		IssueList issueList = (IssueList) content.getWidget(0);
		issueList.updateItems(issueList.getItems().concat(array).cast());
	}

	protected void updateIssueList(IssueFilter filter, Boolean showMore) {
		if(!showMore) filter.setPage(1);
		incidence.getOrgIssues(filter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				more = result.getData().length()==30;				
				if(showMore) showMoreupdateIssueList(result.getData());
				else updateIssueList(result.getData());	
				FilterPanel fp = (FilterPanel) searchContent.getWidget(0);	
				fp.setButtonsLabels(result.getMeta());
			}
			
			@Override public void onFailure(Throwable arg0) {	}
		});
	} 
	
	
	private void createAonToolbar(){
		toolbar.add(new AonToolbar("Tareas") {
			
			@Override protected void onTemporalButtonClick() {
				
				serv.OLDTONEW(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getLoggedUser(), new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void arg0) {
						Window.alert("OLDTONEW EJECUTADO CORRECTAMENTE, ACTUALIZA LA PÁGINA!");
					}
					
					@Override public void onFailure(Throwable arg0) {}
				});
			}

			
			@Override
			protected void onRefreshButtonClick() {
				issueFilter = new IssueFilter();
				issueFilter.setState("open");
				
				FilterPanel fp = (FilterPanel) searchContent.getWidget(0);
				fp.initialize(false);
				
				updateIssueList(issueFilter, false);
			}
			
			@Override protected void onMoreOptionButtonClick() {}
			
			@Override
			protected void onMenuButtonClick() {
				if(dockLayoutPanel.getWidgetSize(configurationPanel) == 0){
					configurationPanel.add(new ConfigurationPanel(me, incidence));
					dockLayoutPanel.setWidgetSize(configurationPanel, 350);
				}
				else {
					configurationPanel.remove(0);
					dockLayoutPanel.setWidgetSize(configurationPanel, 0);
				}		
			}
			
			@Override protected void onEditButtonClick() {}
			
			@Override protected void onDeleteButtonClick() {}
			
			@Override
			protected void onAddButtonClick() {
				AonDialog2 dialog = createAddDialog();
				VerticalPanel vp = (VerticalPanel) dialog.content.getWidget(0);	
				AonComboBox acb = (AonComboBox) vp.getWidget(0);
				dialog.addAutoHidePartner(acb.getElementById("overlay"));
				dialog.getElement().getStyle().setWidth(310, Unit.PX);
				dialog.center();
				/*toolbar.add(dialog);
				dialog.dialog.addIronOverlayOpenedHandler(new IronOverlayOpenedEventHandler() {
							
					@Override
					public void onIronOverlayOpened(IronOverlayOpenedEvent event) {
						dialog.center();
						VerticalPanel vp = (VerticalPanel) dialog.content.getWidget(0);	
						AonComboBox acb = (AonComboBox) vp.getWidget(0);
						acb.open();
					}
				});
				dialog.open();
				*/
			}

			@Override
			protected void onInfoButtonClick() {
				VerticalPanel vp = new VerticalPanel();
				vp.add(getInfoPanel("green", "Abierto"));
				vp.add(getInfoPanel("purple", "Re-abierto"));
				vp.add(getInfoPanel("black", "Cerrado"));
				vp.add(getInfoPanel("gray", "Borrado"));
				vp.add(getInfoPanel("red", "Duplicado"));
				vp.add(getInfoPanel("blue", "FAQ"));
				vp.setWidth("200px");
				AonDialog2 dialog = new AonDialog2("Informaci\u00f3n", vp) {
					
					@Override protected void onCancel() {hide();}
					
					@Override protected void onAccept() {hide();}
				};
				dialog.cancel.setVisible(false);
				dialog.center();
			}
		}.setVisibleEditButton(false).setVisibleDeleteButton(false)
		.setVisibleMoreOptionButton(false));
	}
	
	private HorizontalPanel getInfoPanel(String color, String text){
		HorizontalPanel hp = new HorizontalPanel();
		IronIcon ii = new IronIcon();
		ii.setIcon("error-outline");
		ii.setStyle("color:"+color+";");
		Label label = new Label(text);
		label.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		label.getElement().getStyle().setPaddingTop(5, Unit.PX);
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		hp.add(ii);
		hp.add(label);
		return hp;
	}
	
	private AonDialog2 createAddDialog(){	
		VerticalPanel v = new VerticalPanel();

		AonComboBox acb = new AonComboBox();
		acb.setLabel("Remitente");
		acb.setItemLabelPath("login");
		acb.setFilterEnable(false);
		

		acb.addValueChangedHandler(new ValueChangedEventHandler() {
	
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				JsUser js = acb.getSelectedItem().cast();	
				incidence.getRegistries(js.getLogin(),new AsyncCallback<JSON<JsUser>>() {
					
					@Override
					public void onSuccess(JSON<JsUser> result) {
						acb.setItems(result.getData());
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		
		acb.addDomHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!isNotAlpKey(event.getNativeEvent().getKeyCode())){
					incidence.getRegistries(acb.getInputElementValue(),new AsyncCallback<JSON<JsUser>>() {
					
						@Override
						public void onSuccess(JSON<JsUser> result) {
							acb.setItems(result.getData());
							if(!acb.getOpened())
								acb.open();
						}
					
						@Override public void onFailure(Throwable caught) {}
					});
				}
			}
		}, KeyUpEvent.getType());
		v.add(acb);
		
		PaperInput pi = new PaperInput();
		pi.setLabel("Titulo");
		pi.setList("as");
		v.add(pi);
		
		PaperTextarea pi4 = new PaperTextarea();
		pi4.setLabel("Descripcion");
		v.add(pi4);
		v.setWidth("270px");
		return new AonDialog2("Nueva Incidencia",v){
			@Override protected void onCancel() {hide();}
			@Override protected void onAccept() {
				VerticalPanel vp = (VerticalPanel) content.getWidget(0);	
				AonComboBox acb = (AonComboBox) vp.getWidget(0);
				PaperInput pi = (PaperInput) vp.getWidget(1);
				PaperTextarea pi4 = (PaperTextarea) vp.getWidget(2);
				
				String r= "{\"title\":\""+ pi.getValue() +"\",\"body\":\""+ pi4.getValue()+" \",\"assignee\":\" \",\"labels\":[],"
						+ "\"enterprise\":\""+ acb.getInputElementValue() +"\", \"due_date\":\""+ "31/12/2100" +"\"}";
					
				incidence.createOrgIssue(r, new AsyncCallback<JsIssue>() {
					
					@Override
					public void onSuccess(JsIssue result) {
						contentDockLayoutPanel.removeFromParent();
						AonToolbar t = (AonToolbar)toolbar.getWidget(0);
						t.setVisibleRefreshButton(false);
						contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
						contentDockLayoutPanel.add(new IssuePanel(me, incidence, result));
						dockLayoutPanel.add(contentDockLayoutPanel);
						sendNotification(result, NotificationType.OPEN);
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
				hide();
			}
		};
	}
	
	public void sendNotification(JsIssue issue, NotificationType notificationType) {
		String r= "{\"notification_type\":\""+notificationType.value() +"\","
				+ "\"number\":\""+ issue.getNumber() +"\",}";
		incidence.sendNotification(issue, r);
	}

	public void remove(){
		dockLayoutPanel.removeFromParent();
	}
	
	
	public static boolean isNotAlpKey(int code) {
	    switch (code) {
	      case KeyCodes.KEY_ALT:
	      case KeyCodes.KEY_CAPS_LOCK:
	      case KeyCodes.KEY_CONTEXT_MENU:
	      case KeyCodes.KEY_CTRL:
	      case KeyCodes.KEY_DOWN:
	      case KeyCodes.KEY_END:
	      case KeyCodes.KEY_ENTER:
	      case KeyCodes.KEY_ESCAPE:
	      case KeyCodes.KEY_F1:
	      case KeyCodes.KEY_F2:
	      case KeyCodes.KEY_F3:
	      case KeyCodes.KEY_F4:
	      case KeyCodes.KEY_F5:
	      case KeyCodes.KEY_F6:
	      case KeyCodes.KEY_F7:
	      case KeyCodes.KEY_F8:
	      case KeyCodes.KEY_F9:
	      case KeyCodes.KEY_F10:
	      case KeyCodes.KEY_F11:
	      case KeyCodes.KEY_F12:
	      case KeyCodes.KEY_FIRST_MEDIA_KEY:
	      case KeyCodes.KEY_HOME:
	      case KeyCodes.KEY_INSERT:
	      case KeyCodes.KEY_LAST_MEDIA_KEY:
	      case KeyCodes.KEY_LEFT:
	      case KeyCodes.KEY_MAC_ENTER:
	      case KeyCodes.KEY_MAC_FF_META:
	      case KeyCodes.KEY_NUMLOCK:
	      case KeyCodes.KEY_PAGEDOWN:
	      case KeyCodes.KEY_PAGEUP:
	      case KeyCodes.KEY_PAUSE:
	      case KeyCodes.KEY_PRINT_SCREEN:
	      case KeyCodes.KEY_RIGHT:
	      case KeyCodes.KEY_SCROLL_LOCK:
	      case KeyCodes.KEY_SHIFT:
	      case KeyCodes.KEY_SPACE:
	      case KeyCodes.KEY_TAB:
	      case KeyCodes.KEY_UP:
	        return true;
	      default:
	        return false;
	    }
	  }
}
