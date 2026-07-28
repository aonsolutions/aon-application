package com.esferalia.aon.gwt.fiscal.client.scope;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.scope.UserScopeAssign;
import com.esferalia.aon.occam.api.model.scope.UserScopeFull;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class ScopeAssignPanel extends AonCustomDockLayout {

	// ------------------------------------------------- CommonServiceAsync

	static CommonServiceAsync commonService;

	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- ContextMenu
	
//	class EndAuthorizationsCommand implements ScheduledCommand {
//
//		@Override
//		public void execute() {
//			AonCustomDialog dialog = new AonCustomDialog();
//			dialog.setCaption( "Finalizar Autorizaciones" );
//			dialog.showCloseButton(true);
//			
//			UserScopeAuthorizationEndList panel = new UserScopeAuthorizationEndList(options);
//			panel.setSize("100%", "100%");
//			dialog.setWidget(panel);
//
//			dialog.showLoadedCB(() -> {
//			    dialog.setRelativeSize(80, 80);
//			    dialog.center();
//			});
//			
//			// Este ya llama a userScopeAviableList.setUserAuthorization() para actualizar la lista de autorizaciones (onUserAuthorizationChange)
//			dialog.addCloseHandler(e -> userScopeAuthorizationList.onSearch());
//		}
//		
//	}
//	
//	class OptionsContextMenu extends ContextMenu {
//
//		public OptionsContextMenu() {
//			addMenuItem("Finalizar Autorizaciones", new EndAuthorizationsCommand(), AON.CSS.aonIconAudit(), "endAuthorizations");
//		}
//
//		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
//			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
//			item.ensureDebugId(debugId);
//			return item;
//		}
//
//	}
	
	// ------------------------------------------------- Variables
	
//	private OptionsContextMenu contextMenu;

	private HTMLPanel container;
	private HTMLPanel messagePanel;

	// FBatchDetail

	private SplitLayoutPanel scopesPanel;

	// UserScopeList (Aviable)

	private AssignScopeAviableList assignScopeAviableList;

	// UserScopeAuthorizationList (Selected)
	
	private AssignScopeAuthorizationList assignScopeAuthorizationList;

	// Variables

	private RegistryModuleOptions options;

	// ------------------------------------------------- Constructor

	public ScopeAssignPanel(RegistryModuleOptions options) {
		super(null);
		this.options = options;
		
//		this.contextMenu = new OptionsContextMenu();
		
		initializeCommonService();
		
//		createToolbar();
//		hideSearchWidget();
//		hideToolbar();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn2());
		
		messagePanel = new HTMLPanel("");
		messagePanel.getElement().getStyle().setProperty("margin-top", "1rem");
		container.add(messagePanel);

		scopesPanel = new SplitLayoutPanel();
		scopesPanel.setHeight("100%");
		
		assignScopeAviableList = new AssignScopeAviableList(this.options) {
			
			@Override
			protected void showError(String message) {
				AonMessagePanel.showError(messagePanel, message);
			}

			@Override
			protected void showWarning(String message) {
				AonMessagePanel.showWarning(messagePanel, message);
			}

			@Override
			protected void hideMessage() {
				AonMessagePanel.hideMessage(messagePanel);
			}

			@Override
			protected void onAuthorizeUserScopes(List<UserScopeFull> selectedUserScopes) {
				authorizeUserScopes(selectedUserScopes);
			}
			
		};
		
		assignScopeAuthorizationList = new AssignScopeAuthorizationList(this.options) {
			
			@Override
			protected void showError(String message) {
				AonMessagePanel.showError(messagePanel, message);
			}

			@Override
			protected void showWarning(String message) {
				AonMessagePanel.showWarning(messagePanel, message);
			}

			@Override
			protected void hideMessage() {
				AonMessagePanel.hideMessage(messagePanel);
			}
			
			@Override
			protected void onUserAuthorizationChange(List<UserScopeFull> userScope) {
				assignScopeAviableList.setUserAuthorization(userScope);
			}
		};
		
		scopesPanel.addWest(assignScopeAviableList, Window.getClientWidth() / 2);
		scopesPanel.add(assignScopeAuthorizationList);
		
		container.add(scopesPanel);
		
		add(container);
	}

	@Override
	protected void onClearFilter() {}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		Scheduler.get().scheduleDeferred(() -> {
	        int width = scopesPanel.getOffsetWidth();
	        if (width > 0) {
	            scopesPanel.setWidgetSize(assignScopeAviableList, width / 2);
	            scopesPanel.forceLayout();
	        }
	    });
	}

	// ------------------------------------------------- Toobar

//	private void createToolbar() {
//		AonToolbarButton moreOptions = new AonToolbarButton("Opciones", AON.CSS.aonIconMoreVertical());
//		moreOptions.addClickHandler(event -> {
//			NativeEvent nativeEvent = event.getNativeEvent();
//			contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
//			contextMenu.show();
//		});
//		addToolbarButton(moreOptions);
//	}
	
	// ------------------------------------------------- Methods

	private void authorizeUserScopes(List<UserScopeFull> selectedUserScopes) {
		Integer aviableSelectedUser = assignScopeAviableList.getSelectedUser();
		Integer authorizationSelectedUser = assignScopeAuthorizationList.getSelectedUser();
		
		Integer aviableSelectedSeller = assignScopeAviableList.getSelectedSeller();
		Integer authorizationSelectedSeller = assignScopeAuthorizationList.getSelectedSeller();
		
		Date assginStartDate = assignScopeAuthorizationList.getSelectedStartDate();
		
		if(aviableSelectedUser == null || authorizationSelectedUser == null || aviableSelectedUser.equals(authorizationSelectedUser)) {
			AonMessagePanel.showError(messagePanel, "Debe seleccionar agentes distintos.");
			return;
		}
		
		if(assginStartDate == null) {
			AonMessagePanel.showError(messagePanel, "Debe seleccionar una fecha de inicio para la reasignaci\u00f3n.");
			return;
		}
		
		AonDialog dialog = new AonDialog("Reasignaci\u00f3n de \u00e1mbitos",
				new HTML("Se va a proceder a reasignar el \u00e1mbito seleccionado. \u00bfDesea continuar?"));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {}

			@Override
			public void onAccept() {
				UserScopeAssign userScopeAssign = new UserScopeAssign()
						.setUserScopes(new ArrayList<>(selectedUserScopes))
						.setSellerUserOwner(aviableSelectedUser)
						.setSellerUserNewOwner(authorizationSelectedUser)
						.setSellerOwner(aviableSelectedSeller)
						.setSellerNewOwner(authorizationSelectedSeller)
						.setAssginDate(assginStartDate);

				
				commonService.assignSellerUserScopes(options.getDomainName(), options.getDomain(), options.getUser(), userScopeAssign, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						assignScopeAuthorizationList.onSearch(); // Este ya llama a userScopeAviableList.setUserAuthorization() para actualizar la lista de autorizaciones (onUserAuthorizationChange)
					}

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, caught.getMessage());
					}
				});
			}
		});
		
	}

}
