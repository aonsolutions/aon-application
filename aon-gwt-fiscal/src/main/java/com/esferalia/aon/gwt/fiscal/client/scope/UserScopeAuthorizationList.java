package com.esferalia.aon.gwt.fiscal.client.scope;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.scope.UserScopeFull;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class UserScopeAuthorizationList extends AonCustomDockLayout {
	
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
	
	private AonCustomListBox scopeOwnerLB = new AonCustomListBox("Autorizado temporal \u00e1mbito");
	private AonCustomDateBox startDate = new AonCustomDateBox("F. Inicio");
	private AonCustomDateBox endDate = new AonCustomDateBox("F. Inicio");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private RegistryModuleOptions options;
	
	private List<UserScopeFull> userScope = new ArrayList<UserScopeFull>();
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	// ------------------------------------------------- COLS
	
	private static enum COLS {
		  SCO(AON.MSG.scope()					,"5rem"  			,"flex: 1 1 5rem; min-width: 5rem; width: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("F. Inicio"						,"5rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, END("F. Fin"							,"5rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PRO("Propietario"						,"5rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, QUA(AON.MSG.enterprise() + "(s)"		,"6rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, INF(AonStringUtils.EMPTY				,"2rem" 			,"")
		;
		
		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		
		public String getColWidth() {
			return colWidth;
		}
		
		public String getHeaderLabel() {
			return headerLabel;
		}
		
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	
	}
	
	// ------------------------------------------------- Constructor
	
	public UserScopeAuthorizationList(RegistryModuleOptions options) {
		super("Autorizado del \u00e1mbito");
		
		initializeCommonService();
		
		this.options = options;
		
		addButtonsToolbar();

		hideFilterButton();
		setSearchPlaceholder("Busque por descripci\u00f3n ...");
		getSearchTextBox().getElement().getStyle().setProperty("min-width", "auto");
		
		addOnSearchHandler(e -> onSearch());
		
		//Remove when search 
		hideSearchWidget();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn2());
		
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName(AON.CSS.aonItemFlex());
		row.getElement().getStyle().setProperty("padding", "0 1rem");
		
		scopeOwnerLB.clearItems();
		scopeOwnerLB.addItem("-", AonStringUtils.EMPTY);
		scopeOwnerLB.addChangeHandler(e -> {
			onSearch();
		});
		
		startDate.getElement().getStyle().setProperty("max-width", "8rem");
		endDate.getElement().getStyle().setProperty("max-width", "8rem");
		
		row.add(scopeOwnerLB);
		row.add(startDate);
		row.add(endDate);
		
		container.add(row);
	
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		
		container.add(tableContainer);
		
		add(container);
		
		getUsers(users -> {
			users.stream().filter(u -> u.isActive()).forEach(u -> scopeOwnerLB.addItem(u.getName() + " (" + u.getLogin() + ")", u.getId().toString()));
			onSearch();	
		});
	}

	@Override
	protected void onClearFilter() {}
	
	private void addButtonsToolbar() {
		// addButtonsToolbar
	}
	
	public void onSearch() {
		searchData();
	}
	
	private void searchData() {
		tableContainer.clear();
		
		tab = new AonCustomTable();
		tab.getElement().getStyle().setProperty("min-width", "0");
		tableScrollPanel = new ScrollPanel(tab);
		
		paintHeader();
		tableContainer.add(tableScrollPanel);
		searchDataList();
	}
	
	private void paintHeader() {
		tab.createHeader();
		
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
		
	}
	
	private void searchDataList() {
		getList(userScopes -> {
			boolean something = false;
			
			for(UserScopeFull userScope : userScopes) {
				something = true;
				paintRow(userScope);
			}
			
			if (!something) {
				HTMLPanel row = tab.createRow();
				Label noData = new Label("No existen usuarios con \u00e1mbitos autorizados temporalmente");
				noData.getElement().getStyle().setTextAlign(TextAlign.CENTER);
				tab.addRow(row, noData, "100%");
			}
			
			onUserAuthorizationChange(userScope);
		});
	}

	private void paintRow(UserScopeFull userScope) {
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> {}, ClickEvent.getType());

		Label scope = new Label(userScope.getScope().getDescription());
		scope.setTitle(userScope.getScope().getDescription());
		tab.addInlineStyle(scope, COLS.SCO.getCellStyleClass());
		tab.addRow(row, scope, COLS.SCO.getColWidth());
		
		Label startDate = new Label(parseDate(userScope.getStartDate()));
		startDate.setTitle(parseDate(userScope.getStartDate()));
		tab.addInlineStyle(startDate, COLS.STA.getCellStyleClass());
		tab.addRow(row, startDate, COLS.STA.getColWidth());
		
		Label endDate = new Label(parseDate(userScope.getEndDate()));
		endDate.setTitle(parseDate(userScope.getEndDate()));
		tab.addInlineStyle(endDate, COLS.END.getCellStyleClass());
		tab.addRow(row, endDate, COLS.END.getColWidth());
		
		Label owner = new Label(null == userScope.getOwner() ? "" : userScope.getOwner().getName());
		owner.setTitle(null == userScope.getOwner() ? "" : userScope.getOwner().getName());
		tab.addInlineStyle(owner, COLS.PRO.getCellStyleClass());
		tab.addRow(row, owner, COLS.PRO.getColWidth());
		
		Label quantity = new Label(userScope.getScope().getScopeDomains().isEmpty() ? "" : userScope.getScope().getScopeDomains().size() + " empresa(s)");
		quantity.setTitle(userScope.getScope().getScopeDomains().isEmpty() ? "" : userScope.getScope().getScopeDomains().size() + " empresa(s)");
		tab.addInlineStyle(quantity, COLS.QUA.getCellStyleClass());
		tab.addRow(row, quantity, COLS.QUA.getColWidth());
		
		AonTableButton infoButon = new AonTableButton("Autorizar al usuario", AON.CSS.aonIconInfo());
		infoButon.addClickHandler(e -> {
			e.stopPropagation();
			e.getNativeEvent().stopPropagation();

		    HashMap<Integer, String> map = userScope.getScope().getScopeDomains();
		    
		    AonCustomDialog dialog = new AonCustomDialog();
		    dialog.showCloseButton(true);
		    dialog.setCaption("Dominios del ambito");
		    
		    VerticalPanel root = new VerticalPanel();
	        root.setSpacing(10);
	        root.setWidth("300px");
	        root.getElement().getStyle().setProperty("margin-top", ".5rem");

	        // Lista de valores del HashMap
	        for (String value : map.values()) {
	            Label item = new Label(value);
	            item.getElement().getStyle().setProperty("padding-left", ".5rem");
	            root.add(item);
	        }

	        dialog.add(root);
	        dialog.show();
	        dialog.center();
			
		});
		
		tab.addRow(row, userScope.getScope().getScopeDomains().isEmpty() ? new Label() : infoButon, COLS.INF.getColWidth());
	}
	
	private String parseDate(Date date) {
		if(null == date)
			return "";
		
		return formatDate.format(date);
	}
	
	private void getList(Consumer<List<UserScopeFull>> success) {
		Integer userId = AonStringUtils.isBlank(scopeOwnerLB.getValue()) ? null : Integer.valueOf(scopeOwnerLB.getValue());
		
		if(null == userId) {
			success.accept(new ArrayList<UserScopeFull>());
			return;
		}
		
		commonService.getUserScopesByUser(options.getDomainName(), options.getDomain(), options.getUser(), userId, new AsyncCallback<List<UserScopeFull>>() {

			@Override
			public void onSuccess(List<UserScopeFull> result) {
				userScope = result;
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
			}
		});
	}
	
	private void getUsers(Consumer<ArrayList<User>> success) {
		commonService.getUsers(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<ArrayList<User>>() {

			@Override
			public void onSuccess(ArrayList<User> result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
			}
		});
	}
	
	public List<UserScopeFull> getUserAuthScopes() {
		return userScope;
	}

	public Integer getSelectedUser() {
		Integer userId = AonStringUtils.isBlank(scopeOwnerLB.getValue()) ? null : Integer.valueOf(scopeOwnerLB.getValue());
		return userId;
	}

	public Date getSelectedStartDate() {
		return startDate.getValue();
	}
	
	public Date getSelectedEndDate() {
		return endDate.getValue();
	}
	
	protected abstract void showWarning(String message);
	protected abstract void showError(String message);
	protected abstract void hideMessage();

	protected abstract void onUserAuthorizationChange(List<UserScopeFull> userScope);
	
}
