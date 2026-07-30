package com.esferalia.aon.gwt.marketing.client.scope;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonReasignScopePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonReasignScopePanel.AonReasignScopePanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonScopePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonScopePanel.AonScopePanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.scope.ScopeParams;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;


public abstract class ScopeModulePanel extends AonCustomDockLayout {
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel centerPanel;
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private ScopeModuleOptions options;
	private ArrayList<User> domainUsers;
	
	private ScopePanel scopePanel;
	
	private static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	public ScopeModulePanel(ScopeModuleOptions options, ArrayList<User> domainUsers) {
		super("\u00c1mbitos");
		
		initializeCommonService();
		
		this.options = options;
		this.domainUsers = domainUsers;
		
		addButtonsToolbar();
		
		setSearchPlaceholder("Busqueda por descripci\u00f3n...");
		
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
		
		sort.addItem("Descripci\u00f3n", "description");
		sort.getListBox().addChangeHandler(event -> onSearch());
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());
		
		addSortWidget(sort);
		addSortWidget(asc);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn2());
		
		container.add(messagePanel);
	
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");
		
		container.add(centerPanel);
		
		add(container);
		onSearch();
	}

	@Override
	protected void onClearFilter() {
		scopePanel.resetSearchOffset();
		getSearchTextBox().setValue(null, false);
		onSearch();
	}

	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo \u00c1mbito", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showScopeDialog());
		
		addToolbarButton(newButton);
		
		AonToolbarButton reassign = new AonToolbarButton( "Reasignar \u00c1mbitos", AON.CSS.aonIconMoveGroup());
		reassign.addClickHandler(e -> showReassignScopeDialog());
		
		addToolbarButton(reassign);
	}

	private void showScopeDialog() {
		new AonScopePanel( options.getDomainName(), options.getDomain(), options.getUser(), domainUsers, new AonScopePanelCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept(Scope scopeDB) {
					onScopeCreate(scopeDB);
				}
				
				public boolean existsScopeWithDescription(String description) {
					return scopePanel.existsScopeWithDescription(description);
				}
		});
	}
	
	private void showReassignScopeDialog() {
		getUsedScopesInDomain(scopes -> {
			new AonReasignScopePanel( options.getDomainName(), options.getDomain(), options.getUser(), scopes, new AonReasignScopePanelCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept() {
					onSearch();
				}
			});
		});
	}

	public void onSearch() {
		ScopeParams params = getWidgetParams();
		centerPanel.clear();
		scopePanel = new ScopePanel(params, isOffice()) {

			@Override
			protected void onScopeOpen(Scope scope) {
				onScopeSelect(scope);
			}

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected void onShowSuccessMessage(String successMessage) {
				AonMessagePanel.showSuccess(messagePanel, successMessage);
			}

			@Override
			protected void onShowLoadingMessage(String loadingMessage) {
				AonMessagePanel.showLoading(messagePanel, loadingMessage);
			}

			@Override
			protected void onScopeCreation(Scope scope) {
				onScopeCreate(scope);
			}
		
		};
		
		centerPanel.setWidget(scopePanel);
	}

	public ScopeParams getWidgetParams() {
		ScopeParams params = new ScopeParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(getSearchTextBox().getValue())
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
		
		return params;
	}
	
	public void getScopeListCount(Consumer<Integer> finish) {
		if(null == scopePanel || null ==  scopePanel.getTable()) finish.accept(0);
		
		scopePanel.getScopeListCount(count -> {
			finish.accept(count);
		});
	}
	
	public void showSuccess(String message) {
		AonMessagePanel.showSuccess(messagePanel, message);
	}

	public Integer getScopeListPosition(Integer scopeId) {
		return null == scopeId || null == scopePanel ? 0 : scopePanel.getScopeListPosition(scopeId);
	}
	
	public ScopeParams getScopeParams() {
		return getWidgetParams();
	}
	
	private boolean isOffice() {
		return DomainType.OFFICE.equals( options.getConfiguration().getDomain().getDomainType() );
	}
	
	private void getUsedScopesInDomain(Consumer<ArrayList<Scope>> finish) {
		commonService.getUsedScopesInDomain(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<ArrayList<Scope>>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error al obtener los \u00c1mbitos del dominio");
			}

			@Override
			public void onSuccess(ArrayList<Scope> result) {
				finish.accept(result);
			}
			
		});
	}
	
	public boolean existsScopeWithDescription(String description) {
		return null == scopePanel ? false : scopePanel.existsScopeWithDescription(description);
	}
	
	protected abstract void onScopeSelect(Scope scope);
	protected abstract void onScopeCreate(Scope scope);


}
