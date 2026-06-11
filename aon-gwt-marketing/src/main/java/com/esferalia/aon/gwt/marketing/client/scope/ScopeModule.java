package com.esferalia.aon.gwt.marketing.client.scope;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.scope.ScopeParams;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

public class ScopeModule extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(ScopeModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private static CommonServiceAsync COMMON_SERVICE;

	private ScopeModuleOptions options;

	private DeckLayoutPanel deckLayoutPanel;
	private ScopeModulePanel scopeModulePanel;
	private ScopeEntryPanel scopeEntryPanel;
	
	private ArrayList<User> domainUsers = new ArrayList<User>();

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		options = new ScopeModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(),
				new AsyncCallback<AonConfiguration>() {

					@Override
					public void onSuccess(AonConfiguration config) {
						COMMON_SERVICE.getUsers(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(),new AsyncCallback<ArrayList<User>>() {

							@Override
							public void onFailure(Throwable arg0) {
								options.setConfiguration(config);
								moduleLoad();
							}

							@Override
							public void onSuccess(ArrayList<User> domainUsersDB) {
								domainUsers = domainUsersDB;
								options.setConfiguration(config);
								moduleLoad();
							}}
						);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al cargar el module");
						moduleLoad();
					}
				});
	}

	public void moduleLoad() {
		AON.ensureInjected();

		deckLayoutPanel = new DeckLayoutPanel();

		scopeModulePanel = new ScopeModulePanel(options, domainUsers) {

			@Override
			protected void onScopeSelect(Scope scope) {
				showSelectedScope(scope);
			}

			@Override
			protected void onScopeCreate(Scope scope) {
				showCreatedScope(scope);
			}

		};

		scopeEntryPanel = new ScopeEntryPanel(options) {

			@Override
			protected void onBackClick() {
				showProjectList();
			}

			@Override
			protected void onScopeDeleteClick(Integer scopeId) {
				deleteScope(scopeId);
			}

			@Override
			protected void getScopeListCount(Consumer<Integer> finish) {
				scopeModulePanel.getScopeListCount(count -> finish.accept(count));
			}

			@Override
			protected void onScopeSelectionChange(Scope scope, Integer position) {
				showSelectedScope(scope, position);
			}

			@Override
			protected ScopeParams getScopeListParams() {
				return scopeModulePanel.getScopeParams();
			}
		};

		deckLayoutPanel.add(scopeModulePanel);
		deckLayoutPanel.add(scopeEntryPanel);
		deckLayoutPanel.showWidget(scopeModulePanel);

		options.getParentWidget().add(deckLayoutPanel);
		
		// Remove customer from LS
		removeCustomer();
		removeOfficeDomain();
	}

	private void showProjectList() {
		deckLayoutPanel.showWidget(scopeModulePanel);
		scopeModulePanel.onSearch();
	}

	private void showSelectedScope(Scope scope) {
		deckLayoutPanel.showWidget(scopeEntryPanel);
		scopeEntryPanel.setScope(scope, scopeModulePanel.getScopeListPosition(scope.getId()));
	}

	private void showSelectedScope(Scope scope, Integer position) {
		deckLayoutPanel.showWidget(scopeEntryPanel);
		scopeEntryPanel.setScope(scope, position);
	}

	private void showCreatedScope(Scope scope) {
		deckLayoutPanel.showWidget(scopeEntryPanel);
		scopeEntryPanel.setScope(scope, -1);
	}

	private void deleteScope(Integer scopeId) {
		COMMON_SERVICE.deleteScope(options.getDomainName(), options.getDomain(), options.getUser(), scopeId, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						showProjectList();
						scopeModulePanel.showSuccess("\u00c1mbito eliminado correctamente");
					}

					@Override
					public void onFailure(Throwable caught) {
						scopeEntryPanel.showError("Error borrado: " + caught.getMessage());
					}
				});
	}

}
