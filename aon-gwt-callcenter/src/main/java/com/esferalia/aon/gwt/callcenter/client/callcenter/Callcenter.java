package com.esferalia.aon.gwt.callcenter.client.callcenter;


import java.util.ArrayList;

import com.esferalia.aon.gwt.callcenter.client.CallcenterService;
import com.esferalia.aon.gwt.callcenter.client.CallcenterServiceAsync;
import com.esferalia.aon.gwt.callcenter.client.CallcenterServiceAsyncDecorator;
import com.esferalia.aon.gwt.callcenter.client.IssueCell;
import com.esferalia.aon.gwt.callcenter.client.MainEntryPoint;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.callcenter.Issue;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class Callcenter extends MainEntryPoint {
	
	public static CommonServiceAsync COMMON_SERVICE;
	public static CallcenterServiceAsync CALLCENTER_SERVICE;
	
	interface CallcenterBinder extends UiBinder<Widget, Callcenter> {
	}
	
	private static final CallcenterBinder CALLCENTER_BINDER = GWT
			.create(CallcenterBinder.class);

	
	Enterprise enterprise;
	
	@UiField Label subtitle;
	
	@UiField(provided = true) TextBox searchBoxSubject;
	
	
	
	@UiField SimpleLayoutPanel content;
	
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	
	public SimpleLayoutPanel getContent() {
		return this.content;
	}
	public void setContent(Widget widget) {
		this.content.setWidget(widget);
	}
	
	@Override
	public void onModuleLoad() {
		AON.GWT_RESOURCES.css().ensureInjected();
		AON.AON_RESOURCES.css().ensureInjected();
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		CallcenterServiceAsync callcenterServiceRaw = GWT.create(CallcenterService.class);
		CALLCENTER_SERVICE = new CallcenterServiceAsyncDecorator(callcenterServiceRaw);
		
		searchBoxSubject = new TextBox();
		searchBoxSubject.addBitlessDomHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				loadIssueList();
			}
		}, ChangeEvent.getType());
		searchBoxSubject.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(!isNotAlpKey(event.getNativeEvent().getKeyCode())){
					loadIssueList();
				}
			}
		});
		
		
		Widget ui = CALLCENTER_BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
			
		loadIssueList();
		
	}
	
	
	
	private ArrayList<Issue> openIssues = null;
	private ArrayList<Issue> closedIssues = null;
	
	private void loadIssueList() {
		
		CALLCENTER_SERVICE.getOpenIssues(getCurrentDomainName(),getCurrentDomain(), searchBoxSubject.getText()
				,new AsyncCallback<ArrayList<Issue>>() {
			@Override
			public void onSuccess(ArrayList<Issue> result) {
				openIssues = result;
				renderIssueListContent();
			}

			@Override
			public void onFailure(Throwable caught) {
				PopupPanel box = DialogMessages
						.alertErrorWidget(AON.MSG.unableToShowData(caught
						.getMessage()));
				box.center();
				box.show();
			}
		});

		CALLCENTER_SERVICE.getClosedIssues(getCurrentDomainName(),getCurrentDomain(), searchBoxSubject.getText()
				,new AsyncCallback<ArrayList<Issue>>() {
			@Override
			public void onSuccess(ArrayList<Issue> result) {
				closedIssues = result;
				renderIssueListContent();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				PopupPanel box = DialogMessages
						.alertErrorWidget(AON.MSG.unableToShowData(caught
								.getMessage()));
				box.center();
				box.show();
			}
		});
		
		renderIssueListContent();
		
	}


	
	private TabPanel tabPanel;
	
	private void renderIssueListContent(){

		FlowPanel widget = new FlowPanel();

		if(openIssues!=null && closedIssues!=null){
			
			tabPanel = new TabPanel();
			
			// Open issues tab
			if(openIssues.size()>0){
				Cell<Issue> cellOpen = new IssueCell(); 
				CellList<Issue> cellListOpen = new CellList<Issue>(cellOpen);
				cellListOpen.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
				final SingleSelectionModel<Issue> selectionModelOpen = new SingleSelectionModel<Issue>();
				cellListOpen.setSelectionModel(selectionModelOpen);
				selectionModelOpen.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Issue selected = selectionModelOpen.getSelectedObject();
						if (selected != null) {
//							Window.alert("You selected: " + selected.getSubject());
//							loadIssueForm(selected);
						}
					}
				});
				cellListOpen.setRowCount(openIssues.size(), true);
				cellListOpen.setRowData(0, openIssues);
				tabPanel.add(cellListOpen, "Abiertos");
			} else{
				tabPanel.add(new InlineLabel("No se han encontrado resultados."), "Abiertos");
			}
			
			
			// Closed issues tab
			if(closedIssues.size()>0){
				Cell<Issue> cellClosed = new IssueCell(); 
				CellList<Issue> cellListClosed = new CellList<Issue>(cellClosed);
				cellListClosed.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
				final SingleSelectionModel<Issue> selectionModelClosed = new SingleSelectionModel<Issue>();
				cellListClosed.setSelectionModel(selectionModelClosed);
				selectionModelClosed.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						Issue selected = selectionModelClosed.getSelectedObject();
						if (selected != null) {
//							Window.alert("You selected: " + selected.getSubject());
//							loadIssueForm(selected);
						}
					}

				});
				cellListClosed.setRowCount(closedIssues.size(), true);
				cellListClosed.setRowData(0, closedIssues);
				tabPanel.add(cellListClosed, "Cerrados");
			} else{
				tabPanel.add(new InlineLabel("No se han encontrado resultados."), "Cerrados");
			}
			
			tabPanel.selectTab(0);
			tabPanel.setWidth("100%");

			widget.add(tabPanel);
			
		} else {
			widget.add(new InlineLabel("No se han encontrado resultados."));
		}
		setContent(widget);
	}
	
	private void loadIssueForm(Issue issue) {
		NoticeForm widget = new NoticeForm(issue);
		setContent(widget);
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
