package com.esferalia.aon.gwt.fiscal.client.scope;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.scope.UserScopeFull;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class UserScopeAviableList extends AonCustomDockLayout {
	
	// ------------------------------------------------- CommonServiceAsync

	static CommonServiceAsync commonService;

	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- UserScopeRow
	
	private static class UserScopeRow {

		private int row;
		private UserScopeFull userScope;

		private UserScopeRow(int row, UserScopeFull userScope) {
			this.row = row;
			this.userScope = userScope;
		}

		private int getRow() {
			return row;
		}

		private UserScopeFull getUserScope() {
			return userScope;
		}

	}
	
	// ------------------------------------------------- Variables

	private HTMLPanel container;
	
	private AonTableButton resetSearchButton;
	private AonTableButton checkAll; 
	private AonTableButton uncheckAll;
	
	private AonCustomListBox scopeOwnerLB = new AonCustomListBox("Propietario \u00e1mbito");
	private AonTableButton addAviableButton = new AonTableButton("Autorizar al usuario", AON.CSS.aonIconKeyboardDoubleArrowRight());
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;

	private InlineLabel aviableCount;
	private LinkedHashMap<Integer, UserScopeRow> aviableUserScopes;
	private LinkedHashSet<Integer> selectedUserScopes;
	
	private RegistryModuleOptions options;
	
	private List<UserScopeFull> userAuthorizationScope = new ArrayList<UserScopeFull>();
	private List<UserScopeFull> userScope = new ArrayList<UserScopeFull>();
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	// ------------------------------------------------- COLS
	
	private static enum COLS {
		  CHK(AonStringUtils.EMPTY				,"2rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SCO(AON.MSG.scope()					,"5rem"  			,"flex: 1 1 5rem; min-width: 5rem; width: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("F. Inicio"						,"5rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, END("F. Fin"							,"5rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PRO("Propietario"						,"6rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, QUA(AON.MSG.enterprise() + "(s)"		,"6rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, INF(AonStringUtils.EMPTY				,"2rem" 			,"")
		, ACT(AonStringUtils.EMPTY				,"2rem" 			,"")
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
	
	public UserScopeAviableList(RegistryModuleOptions options) {
		super("Propietario \u00e1mbito");
		
		initializeCommonService();
		
		this.options = options;
		
		aviableUserScopes = new LinkedHashMap<Integer, UserScopeRow>();
		selectedUserScopes = new LinkedHashSet<Integer>();
		
		addButtonsToolbar();

		hideFilterButton();
		setSearchPlaceholder("Busque por descripci\u00f3n ...");
		getSearchTextBox().getElement().getStyle().setProperty("min-width", "auto");
		
		addOnSearchHandler(e -> onSearch());
		
		//Remove when search 
		hideSearchWidget();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn2());
		
		scopeOwnerLB.clearItems();
		scopeOwnerLB.addItem("-", AonStringUtils.EMPTY);
		scopeOwnerLB.addChangeHandler(e -> onSearch());
		scopeOwnerLB.getElement().getStyle().setProperty("padding", "0 1rem");
		
		container.add(scopeOwnerLB);
	
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
		resetSearchButton = new AonTableButton(AON.MSG.clean() + " filtros", AON.CSS.aonIconClear());
		resetSearchButton.addClickHandler(e -> {
			hideMessage();
			getSearchTextBox().setValue(null, false);
			scopeOwnerLB.setValue("");
			onSearch();
		});
		addToolbarButton(resetSearchButton);
		
		checkAll = new AonTableButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll.addClickHandler(e -> checkAllAviable( true ));
		addToolbarButton(checkAll);
		
		uncheckAll = new AonTableButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.addClickHandler(e -> checkAllAviable( false ));
		addToolbarButton(uncheckAll);
		
		HTMLPanel checksPanel = new HTMLPanel(AonStringUtils.EMPTY);
		checksPanel.addStyleName(AON.CSS.aonDisplayFlex());
		checksPanel.add(checkAll);
		checksPanel.add(uncheckAll);
		addToolbarButton(checksPanel);
	}
	
	private void checkAllAviable(boolean check) {
		for (UserScopeRow userScopeRow : aviableUserScopes.values()) {
			if(userAuthorizationScope.stream().anyMatch(us -> us.getScope().getId().equals(userScopeRow.getUserScope().getScope().getId())))
				continue;
					
			userScopeRow.getUserScope().setSelected(check);
			
			if (check)
				selectedUserScopes.add(userScopeRow.getUserScope().getId());
			else
				selectedUserScopes.remove(userScopeRow.getUserScope().getId());
			
			Widget w = tab.getWidget(userScopeRow.getRow(), 0);
			
			if (null != w && w instanceof AonTableButton) {
				if (check) {
					w.addStyleName(AON.CSS.aonIconChecked());
					w.removeStyleName(AON.CSS.aonIconCheck());
				} else {
					w.addStyleName(AON.CSS.aonIconCheck());
					w.removeStyleName(AON.CSS.aonIconChecked());
				}
			}
		}

		aviableCount.setText((selectedUserScopes.size() > 0) ? AonNumberUtils.toString(selectedUserScopes.size()) : "");
		addAviableButton.setEnabled(selectedUserScopes.size() > 0);
	}
	

	public void setUserAuthorization(List<UserScopeFull> userScope) {
		this.userAuthorizationScope = userScope;
		onSearch();
	}
	
	public void onSearch() {
		userScope.clear();
		selectedUserScopes.clear();
		aviableUserScopes.clear();
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
		aviableCount = new InlineLabel("");
		aviableCount.addStyleName(AON.CSS.aonTextCenter());
		
		tab.createHeader();
		
		addAviableButton = new AonTableButton("Autorizar al usuario", AON.CSS.aonIconKeyboardDoubleArrowRight());
		addAviableButton.getElement().getStyle().setProperty("background-repeat", "no-repeat");
		addAviableButton.setEnabled(false);
		addAviableButton.addClickHandler(e -> {
			List<UserScopeFull> authUserScopes = userScope.stream().filter(us -> selectedUserScopes.contains(us.getId())).collect(Collectors.toList());
			onAuthorizeUserScopes(authUserScopes);
		});
		
		for ( COLS col : COLS.values()) 
			if(col.equals(COLS.ACT))
				tab.addHeader(addAviableButton, col.getColWidth(), col.getCellStyleClass());
			else
				tab.addHeader(col.equals(COLS.CHK) ? aviableCount : new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
		
	}

	private void searchDataList() {
		getList(userScopesDB -> {
			boolean something = false;
			
			for(UserScopeFull userScope : userScope) {
				something = true;
				paintRow(userScope);
			}
			
			if (!something) {
				HTMLPanel row = tab.createRow();
				Label noData = new Label("No existen usarios con \u00e1mbitos disponibles para el propietario seleccionado");
				noData.getElement().getStyle().setTextAlign(TextAlign.CENTER);
				tab.addRow(row, noData, "100%");
			}
		});
	}

	private void paintRow(UserScopeFull userScope_) {
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> {}, ClickEvent.getType());
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), selectedUserScopes.contains(userScope_.getId()) ? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
		
		checkButton.addClickHandler(e -> {
			e.stopPropagation();
			
			if (selectedUserScopes.contains(userScope_.getId())) {
				selectedUserScopes.remove(userScope_.getId());
				checkButton.addStyleName(AON.CSS.aonIconCheck());
				checkButton.removeStyleName(AON.CSS.aonIconChecked());
			} else {
				selectedUserScopes.add(userScope_.getId());
				checkButton.addStyleName(AON.CSS.aonIconChecked());
				checkButton.removeStyleName(AON.CSS.aonIconCheck());
			}

			aviableCount.setText((selectedUserScopes.size() > 0) ? AonNumberUtils.toString(selectedUserScopes.size()) : "");
			addAviableButton.setEnabled(selectedUserScopes.size() > 0);
		
		});
		
		if(null != userAuthorizationScope && !userAuthorizationScope.isEmpty())
			checkButton.setEnabled(userAuthorizationScope.stream().noneMatch(us -> us.getScope().getId().equals(userScope_.getScope().getId())));
		
		tab.addRow(row, checkButton, COLS.CHK.getColWidth());
		
		Label scope = new Label(userScope_.getScope().getDescription());
		scope.setTitle(userScope_.getScope().getDescription());
		tab.addInlineStyle(scope, COLS.SCO.getCellStyleClass());
		tab.addRow(row, scope, COLS.SCO.getColWidth());

		Label startDate = new Label(parseDate(userScope_.getStartDate()));
		startDate.setTitle(parseDate(userScope_.getStartDate()));
		tab.addInlineStyle(startDate, COLS.STA.getCellStyleClass());
		tab.addRow(row, startDate, COLS.STA.getColWidth());
		
		Label endDate = new Label(parseDate(userScope_.getEndDate()));
		endDate.setTitle(parseDate(userScope_.getEndDate()));
		tab.addInlineStyle(endDate, COLS.END.getCellStyleClass());
		tab.addRow(row, endDate, COLS.END.getColWidth());
		
		Label owner = new Label(null == userScope_.getOwner() ? "" : userScope_.getOwner().getName());
		owner.setTitle(null == userScope_.getOwner() ? "" : userScope_.getOwner().getName());
		tab.addInlineStyle(owner, COLS.PRO.getCellStyleClass());
		tab.addRow(row, owner, COLS.PRO.getColWidth());
		
		Label quantity = new Label(userScope_.getScope().getScopeDomains().isEmpty() ? "" : userScope_.getScope().getScopeDomains().size() + " empresa(s)");
		quantity.setTitle(userScope_.getScope().getScopeDomains().isEmpty() ? "" : userScope_.getScope().getScopeDomains().size() + " empresa(s)");
		tab.addInlineStyle(quantity, COLS.QUA.getCellStyleClass());
		tab.addRow(row, quantity, COLS.QUA.getColWidth());
		
		AonTableButton infoButon = new AonTableButton("Autorizar al usuario", AON.CSS.aonIconInfo());
		infoButon.addClickHandler(e -> {
			e.stopPropagation();
			e.getNativeEvent().stopPropagation();

		    HashMap<Integer, String> map = userScope_.getScope().getScopeDomains();
		    
		    AonCustomDialog dialog = new AonCustomDialog();
		    dialog.showCloseButton(true);
		    dialog.setCaption("Dominios del ambito");
		    
		    VerticalPanel root = new VerticalPanel();
	        root.setSpacing(10);
	        root.setWidth("300px");
	        root.getElement().getStyle().setProperty("margin", ".5rem");

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
		
		tab.addRow(row, userScope_.getScope().getScopeDomains().isEmpty() ? new Label() : infoButon, COLS.INF.getColWidth());

		AonTableButton addButton = new AonTableButton("Autorizar al usuario", AON.CSS.aonIconKeyboardArrowRight());
		addButton.addClickHandler(e -> {
			//addButton.setEnabled(false);
			onAuthorizeUserScopes(List.of(userScope_));
		});
		if(null != userAuthorizationScope && !userAuthorizationScope.isEmpty())
			addButton.setEnabled(userAuthorizationScope.stream().noneMatch(us -> us.getScope().getId().equals(userScope_.getScope().getId())));
		
		tab.addRow(row, addButton, COLS.ACT.getColWidth());
		
		aviableUserScopes.put(userScope_.getId(), new UserScopeRow(tab.getRowsCount(), userScope_));
	}
	
	private String parseDate(Date date) {
		if(null == date)
			return "";
		
		return formatDate.format(date);
	}
	
	private void getList(Consumer<List<UserScopeFull>> success) {
		Integer userId = AonStringUtils.isBlank(scopeOwnerLB.getValue()) ? null : Integer.valueOf(scopeOwnerLB.getValue());
		
		if(null == userId) {
			showWarning("Debe seleccionar un propietario de \u00e1mbito");
			success.accept(new ArrayList<UserScopeFull>());
				
			new Timer() {
				@Override
				public void run() {
					hideMessage();
				}
			}.schedule(2000);
			
			return;
		}
		
		commonService.getUserScopesByUser(options.getDomainName(), options.getDomain(), options.getUser(), userId, new AsyncCallback<List<UserScopeFull>>() {

			@Override
			public void onSuccess(List<UserScopeFull> result) {
				userScope = result;
				success.accept(userScope);
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

	public Integer getSelectedUser() {
		Integer userId = AonStringUtils.isBlank(scopeOwnerLB.getValue()) ? null : Integer.valueOf(scopeOwnerLB.getValue());
		return userId;
	}
	
	protected abstract void showWarning(String message);
	protected abstract void showError(String message);
	protected abstract void hideMessage();

	protected abstract void onAuthorizeUserScopes(List<UserScopeFull> selectedUserScopes);
	
}
