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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.scope.UserScopeAuthorization;
import com.esferalia.aon.occam.api.model.scope.UserScopeFull;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

public class ScopeTemporaryAuthorizationPanel extends AonCustomDockLayout {

	// ------------------------------------------------- CommonServiceAsync

	static CommonServiceAsync commonService;

	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables

	private HTMLPanel container;
	private HTMLPanel messagePanel;

	// FBatchDetail

	private SplitLayoutPanel scopesPanel;

	// UserScopeList (Aviable)

	private UserScopeAviableList userScopeAviableList;

	// UserScopeAuthorizationList (Selected)
	
	private UserScopeAuthorizationList userScopeAuthorizationList;

	// Variables

	private RegistryModuleOptions options;

	// ------------------------------------------------- Constructor

	public ScopeTemporaryAuthorizationPanel(RegistryModuleOptions options) {
		super("Autorizaciones Temporales");
		this.options = options;
		
		initializeCommonService();
		
		createToolbar();
		hideSearchWidget();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn2());
		
		messagePanel = new HTMLPanel("");
		messagePanel.getElement().getStyle().setProperty("margin-top", "1rem");
		container.add(messagePanel);

		scopesPanel = new SplitLayoutPanel();
		scopesPanel.setHeight("100%");
		
		userScopeAviableList = new UserScopeAviableList(this.options) {
			
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
		
		userScopeAuthorizationList = new UserScopeAuthorizationList(this.options) {
			
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
				userScopeAviableList.setUserAuthorization(userScope);
			}
		};
		
		scopesPanel.addWest(userScopeAviableList, Window.getClientWidth() / 2);
		scopesPanel.add(userScopeAuthorizationList);
		
		container.add(scopesPanel);
		
		add(container);
	}

	@Override
	protected void onClearFilter() {}

	// ------------------------------------------------- Toobar

	private void createToolbar() {
		// TODO Auto-generated method stub		
	}
	
	// ------------------------------------------------- Methods

	private void authorizeUserScopes(List<UserScopeFull> selectedUserScopes) {
		Integer aviableSelectedUser = userScopeAviableList.getSelectedUser();
		Integer authorizationSelectedUser = userScopeAuthorizationList.getSelectedUser();
		
		Date authorizationStartDate = userScopeAuthorizationList.getSelectedStartDate();
		Date authorizationEndDate = userScopeAuthorizationList.getSelectedEndDate();
		
		if(aviableSelectedUser == null || authorizationSelectedUser == null || aviableSelectedUser.equals(authorizationSelectedUser)) {
			AonMessagePanel.showError(messagePanel, "Debe seleccionar usuarios distintos.");
			return;
		}
		
		if(authorizationStartDate == null) {
			AonMessagePanel.showError(messagePanel, "Debe seleccionar una fecha de inicio de autorizaci\u00f3n.");
			return;
		}
		
		AonDialog dialog = new AonDialog("Autorizaci\u00f3n de usuarios",
				new HTML("Se va a proceder a autorizar al usuario con los \u00e1mbitos seleccionado. \u00bfDesea continuar?"));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {}

			@Override
			public void onAccept() {
				UserScopeAuthorization userScopeAuthorization = new UserScopeAuthorization()
						.setUserScopes(new ArrayList<>(selectedUserScopes))
						.setAuthorizationId(authorizationSelectedUser)
						.setStartDate(authorizationStartDate)
						.setEndDate(authorizationEndDate);

				
				commonService.authorizateUserScopes(options.getDomainName(), options.getDomain(), options.getUser(), userScopeAuthorization, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						userScopeAuthorizationList.onSearch(); // Este ya llama a userScopeAviableList.setUserAuthorization() para actualizar la lista de autorizaciones (onUserAuthorizationChange)
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
