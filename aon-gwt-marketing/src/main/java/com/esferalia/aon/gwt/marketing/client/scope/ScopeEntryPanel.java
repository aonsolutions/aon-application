package com.esferalia.aon.gwt.marketing.client.scope;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDomainScopePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDomainScopePanel.AonDomainScopePanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonUserScopePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonUserScopePanel.AonUserScopePanelCallback;
import com.esferalia.aon.occam.api.model.scope.ScopeParams;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ScopeEntryPanel extends AonCustomDockLayout {
	
	// ------------------------------------------------- CommonServiceAsync
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables
	
	private final String EMPTY_STRING = "";
	
	private AonToolbarButton deleteButton;
	private AonToolbarButton saveButton;
	
	private Integer position = -1;
	private AonToolbarButton previusScope;
	private Label scopeIteration;
	private AonToolbarButton nextScope;
	
	private ScrollPanel scrollPanel;
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private HTMLPanel gridContainer = new HTMLPanel(EMPTY_STRING);
	private HTMLPanel gridPanel = new HTMLPanel(EMPTY_STRING);
	
	// General info
	private AonCustomTextBox id = new AonCustomTextBox("C\u00f3digo");
	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	
	// DomainScopeTable
	private DomainScopeTable domainScopeTable;
	
	// UserScopeTable
	private UserScopeTable userScopeTable;
	
	// Vairables
	private ScopeModuleOptions options;

	private Scope scope;
	
	private Integer count;

	public ScopeEntryPanel(ScopeModuleOptions options) {
		super("Expediente");
		
		this.options = options;
		
		initializeCommonService();
		
		addButtonsToolbar();
		hideSearchWidget();
		
		scrollPanel = new ScrollPanel();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn2());
		
		container.add(messagePanel);
		
		scrollPanel.setWidget(container);
		
		add(scrollPanel);
	}
	
	@Override
	protected void onClearFilter() {}
	
	private void addButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton( "Volver", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onBackClick());
		addToolbarButton(backButton);
		
		saveButton = new AonToolbarButton( "Guardar \u00c1mbito", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> {
			saveButton.setEnabled(false);
			
			if(AonStringUtils.isBlank(description.getValue()) || AonStringUtils.isBlank(description.getValue())) {
				saveButton.setEnabled(true);
    			AonMessagePanel.showWarning(messagePanel, "El campo descripci\u00f3n es obligatorio");
    		} else {
    			AonMessagePanel.showLoading(messagePanel, "Guardando \u00e1mbito ...");
    			
    			scope.setDescription(description.getValue());
        		
        		commonService.saveScope(options.getDomainName(), options.getDomain(), options.getUser(), scope, new AsyncCallback<Scope>() {

    				@Override
    				public void onSuccess(Scope savedScope) {
    					AonMessagePanel.showSuccess(messagePanel, "\u00c1mbito guardado correctamente");
    					saveButton.setEnabled(true);
    				}
    				
    				@Override
    				public void onFailure(Throwable caught) {
    					AonMessagePanel.showError(messagePanel, caught.getMessage());
    				}
    			});      		
    		}
		});
		
		addToolbarButton(saveButton);
		
		deleteButton = new AonToolbarButton( "Borrar \u00c1mbito", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n \u00c1mbito",
					new HTML("Se va a proceder a eliminar el \u00e1mbito <b>" + scope.getDescription() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					onScopeDeleteClick(scope.getId());
					deleteButton.setEnabled(true);
				}
			});
		});
		addToolbarButton(deleteButton);
		
		previusScope = new AonToolbarButton("Anterior \u00c1mbito", AON.CSS.aonIconLeft());
		previusScope.setEnabled(position > 0);
		previusScope.addClickHandler(e -> {
			position = position - 1;
			getNextScope(position, nextScope -> onScopeSelectionChange(nextScope, position));
		});
		addToolbarButton(previusScope);
		
		getScopeListCount(count -> {
			this.count = count;
			
			scopeIteration = new Label((null == scope ? "ND" : (position + 1)) + " / " + this.count);
			addToolbarButton(scopeIteration);
			
			nextScope = new AonToolbarButton("Siguiente \u00c1mbito", AON.CSS.aonIconRight());
			nextScope.setEnabled(position < (this.count - 1));
			nextScope.addClickHandler(e -> {
				position = position + 1;
				getNextScope(position, nextScope -> onScopeSelectionChange(nextScope, position));
			});
			addToolbarButton(nextScope);
		});
		
		
	}

	private void getNextScope(Integer nextPos, Consumer<Scope> scopeLoad) {
		ScopeParams params = getScopeListParams();
		params.setOffset(nextPos);
		params.setLimit(1);
		
		commonService.getScopeList(params, new AsyncCallback<List<Scope>>() {
			
			@Override
			public void onSuccess(List<Scope> scopes) {
				scopeLoad.accept(scopes.get(0));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

	private void hideNavegationOptions() {
		previusScope.setVisible(false);
		scopeIteration.setVisible(false);
		nextScope.setVisible(false);
	}

	private void showNavegationOptions() {
		previusScope.setVisible(true);
		scopeIteration.setVisible(true);
		nextScope.setVisible(true);
	}

	public void setScope(Scope scope, Integer sellectPos) {
		this.position = sellectPos;
		this.scope = scope;
		setScope(scope, finish -> {
			if(position >= 0) showNavegationOptions();
			else hideNavegationOptions();
		});
	}
	
	public void setScope(Scope scope, Consumer<Void> finish) {
		this.scope = scope;
		getScope(this.scope.getId(), scopeIt -> {
			getScopeListCount(count -> {
				this.count = count;
				
				setToolbarTitle(scope.getDescription());
				scopeIteration.setText((null == scope ? "ND" : (position + 1)) + " / " + count);
				previusScope.setEnabled(position > 0);
				nextScope.setEnabled(position < (this.count - 1));
				
				saveButton.setVisible( (null != scope && scope.getDomain().equals(options.getDomain())) || isOffice() );
				deleteButton.setVisible( (null != scope && scope.getDomain().equals(options.getDomain())) || isOffice() );
				
				paintView();
				
				finish.accept(null);
			});
			
		});
		
	}
	
	private void paintView() {
		gridContainer.clear();
		gridContainer.addStyleName(AON.CSS.aonFlexColumn2());
		gridContainer.getElement().getStyle().setProperty("padding", "0 1rem");
		
		AonCustomCard infoCard = new AonCustomCard("Informaci\u00f3n General");
		
		id.setWidth("10rem");
		id.setEnable(false);
		description.setEnable( (null != scope && scope.getDomain().equals(options.getDomain())) || isOffice() );
		
		id.setValue(null == scope.getId() ? "" : scope.getId().toString());
		description.setValue(scope.getDescription());
		
		HTMLPanel infoTable = createTable();
		infoTable.add(createRow(id, description));
		infoCard.add(infoTable);
		
		gridContainer.add(infoCard);
		
		gridPanel.clear();
		gridPanel.setStyleName(AON.CSS.aonGridTwoCols());
		
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Usuario", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showUserScopeDialog());
		
		AonCustomCard userScopeCard = new AonCustomCard("Usuarios", newButton);
		
		userScopeTable = new UserScopeTable(options.getDomainName(), options.getDomain(), options.getUser(), this.scope) {

			@Override
			protected void onDelete() {
				setScope(scope, position);
			}

			@Override
			protected void onUserScopeAdd() {
				setScope(scope, position);
			}
			
		};
		
		userScopeCard.add(userScopeTable);
		gridPanel.add( userScopeCard );
		
		AonToolbarButton newEntepriseButton = new AonToolbarButton( "Nueva Empresa", AON.CSS.aonIconAdd());
		newEntepriseButton.addClickHandler(e -> showDomainScopeDialog());
		
		AonCustomCard domainScopeCard = new AonCustomCard("Empresas (Dominios)", newEntepriseButton);
		
		domainScopeTable = new DomainScopeTable(options.getDomainName(), options.getDomain(), options.getUser(), this.scope) {

			@Override
			protected void onDelete() {
				setScope(scope, position);
			}

			@Override
			protected void onDomainScopeAdd() {
				setScope(scope, position);
			}
			
		};
		
		domainScopeCard.add(domainScopeTable);
		gridPanel.add( domainScopeCard );
		
		gridContainer.add(gridPanel);
		
		container.add(gridContainer);
	}

	private void showUserScopeDialog() {
		new AonUserScopePanel( options.getDomainName(), options.getDomain(), options.getUser(), this.scope.getId(),  new AonUserScopePanelCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept(Void result) {
				setScope(scope, position);
			}
		});
	}
	
	private void showDomainScopeDialog() {
		new AonDomainScopePanel( options.getDomainName(), options.getDomain(), options.getUser(), this.scope.getId(),  new AonDomainScopePanelCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept(Void result) {
				setScope(scope, position);
			}
		});
	}

	private HTMLPanel createRow(Widget w1, Widget w2) {
		HTMLPanel panel = new HTMLPanel("");
		panel.setStyleName(AON.CSS.aonItemFlex());
		
		panel.add(w1);
		
		if(null != w2) panel.add(w2);
		
		return panel;
	}
	
	private HTMLPanel createTable() {
		HTMLPanel table = new HTMLPanel("");
		table.setStyleName(AON.CSS.aonFlexColumn2());
		return table;
	}

	public void showError(String message) {
		AonMessagePanel.showError(messagePanel, message);
	}
	
	public void showSuccess(String message) {
		AonMessagePanel.showSuccess(messagePanel, message);
	}
	
	private boolean isOffice() {
		return DomainType.OFFICE.equals( options.getConfiguration().getDomain().getDomainType() );
	}
	
	private void getScope(Integer scopeId, Consumer<Scope> success) {
		commonService.getScope(options.getDomainName(), options.getDomain(), options.getUser(), scopeId, new AsyncCallback<Scope>() {
			
			@Override
			public void onSuccess(Scope scopeDB) {
				scope = scopeDB;
				success.accept(scopeDB);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo operario: " + caught.getMessage());
			}
			
		});
	}

	protected abstract void onBackClick();
	protected abstract void onScopeDeleteClick(Integer taskHolderId);
	
	protected abstract void getScopeListCount(Consumer<Integer> finish);
	protected abstract ScopeParams getScopeListParams();
	
	protected abstract void onScopeSelectionChange(Scope scope, Integer position);

}
