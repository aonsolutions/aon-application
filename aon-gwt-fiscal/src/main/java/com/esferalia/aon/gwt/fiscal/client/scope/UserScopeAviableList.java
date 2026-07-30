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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.Domain;
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
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	
	private AonCustomSuggestBox scopeOwnerSB = new AonCustomSuggestBox("Usuario");
	private AonCustomListBox enterpriseStatusLB = new AonCustomListBox("Estado empresa");
	private AonTableButton addAviableButton = new AonTableButton("Autorizar al usuario", AON.CSS.aonIconKeyboardDoubleArrowRight());
	private HashMap<String, Integer> scopeOwnerByLabel = new HashMap<String, Integer>();
	private Integer selectedScopeOwnerId;
	private DomainStatusFilter selectedDomainStatusFilter = DomainStatusFilter.ALL;
	
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
	private int searchRequestToken;

	private static enum DomainStatusFilter {
		ALL("Todos"),
		ACTIVE("Activo"),
		INACTIVE("Inactivo"),
		BLOCKED("Bloqueado");

		private final String label;

		private DomainStatusFilter(String label) {
			this.label = label;
		}

		private String getLabel() {
			return label;
		}
	}
	
	// ------------------------------------------------- COLS
	
	private static enum COLS {
		  CHK(AonStringUtils.EMPTY				,"2rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SCO(AON.MSG.scope()					,"5rem"  			,"flex: 1 1 5rem; min-width: 5rem; width: 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("F. Inicio"						,"5rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, END("F. Fin"							,"5rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PRO("Propietario"						,"6rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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
	
	public UserScopeAviableList(RegistryModuleOptions options) {
		super("\u00e1mbitos");
		
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
		
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName(AON.CSS.aonItemFlex());
		row.getElement().getStyle().setProperty("padding", "0 1rem");
		
		scopeOwnerSB.setAutoSelectEnabled(false);
		scopeOwnerSB.setPlaceHolder("Ctrl + espacio para ver sugerencias");
		scopeOwnerSB.getSuggestBox().addSelectionHandler(e -> {
			selectedScopeOwnerId = scopeOwnerByLabel.get(e.getSelectedItem().getReplacementString());
			onSearch();
		});
		scopeOwnerSB.getSuggestBox().addValueChangeHandler(e -> {
			resolveSelectedScopeOwnerId();
			onSearch();
		});
		scopeOwnerSB.getSuggestBox().addKeyUpHandler(e -> {
			if (e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				scopeOwnerSB.showSuggestionList();
			}
		});
		
		row.add(scopeOwnerSB);

		enterpriseStatusLB.addItem(DomainStatusFilter.ALL.getLabel(), DomainStatusFilter.ALL.name());
		enterpriseStatusLB.addItem(DomainStatusFilter.ACTIVE.getLabel(), DomainStatusFilter.ACTIVE.name());
		enterpriseStatusLB.addItem(DomainStatusFilter.INACTIVE.getLabel(), DomainStatusFilter.INACTIVE.name());
		enterpriseStatusLB.addItem(DomainStatusFilter.BLOCKED.getLabel(), DomainStatusFilter.BLOCKED.name());
		enterpriseStatusLB.setValue(DomainStatusFilter.ALL.name());
		enterpriseStatusLB.addChangeHandler(e -> {
			selectedDomainStatusFilter = DomainStatusFilter.valueOf(enterpriseStatusLB.getValue());
			onSearch();
		});
		row.add(enterpriseStatusLB);
		
		enterpriseStatusLB.getElement().getStyle().setProperty("max-width", "8rem");
		
		container.add(row);
	
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		
		container.add(tableContainer);
		
		add(container);
		
		getUsers("", users -> {
			List<String> userLabels = new ArrayList<String>();
			scopeOwnerByLabel.clear();
			users.stream().filter(u -> u.isActive()).forEach(u -> {
				String label = u.getName() + " (" + u.getLogin() + ")";
				scopeOwnerByLabel.put(label, u.getId());
				userLabels.add(label);
			});
			MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) scopeOwnerSB.getSuggestBox().getSuggestOracle();
			oracle.clear();
			oracle.addAll(userLabels);
			oracle.setDefaultSuggestionsFromText(userLabels);
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
			scopeOwnerSB.setValue("");
			enterpriseStatusLB.setValue(DomainStatusFilter.ALL.name());
			selectedDomainStatusFilter = DomainStatusFilter.ALL;
			selectedScopeOwnerId = null;
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
		
		addAviableButton = new AonTableButton("Autorizar al usuario", AON.CSS.aonIconGroupAdd());
		addAviableButton.getElement().getStyle().setProperty("background-repeat", "no-repeat");
		addAviableButton.setEnabled(false);
		addAviableButton.addClickHandler(e -> {
			List<UserScopeFull> authUserScopes = userScope.stream().filter(us -> selectedUserScopes.contains(us.getId())).collect(Collectors.toList());
			onAuthorizeUserScopes(authUserScopes);
		});
		addToolbarButton(addAviableButton);
		
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
		searchRequestToken++;
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
		
		for ( COLS col : COLS.values()) 
			tab.addHeader(col.equals(COLS.CHK) ? aviableCount : new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
		
	}

	private void searchDataList() {
		final int currentSearchRequestToken = searchRequestToken;
		getList(userScopesDB -> {
			if (currentSearchRequestToken != searchRequestToken) {
				return;
			}

			boolean something = false;
			
			for(UserScopeFull userScope : userScopesDB) {
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
		
		Integer userId = resolveSelectedScopeOwnerId();
		checkButton.setEnabled(userScope_.getOwner() == null || userScope_.getOwner().getId() == null || userScope_.getOwner().getId().equals(userId));
		
		if(null != userAuthorizationScope && !userAuthorizationScope.isEmpty())
			checkButton.setEnabled(userAuthorizationScope.stream().noneMatch(us -> us.getScope().getId().equals(userScope_.getScope().getId()) && us.getEndDate() == null));
		
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

		    HashMap<Integer, Domain> map = userScope_.getScope().getScopeDomains();
		    
		    AonCustomDialog dialog = new AonCustomDialog();
		    dialog.showCloseButton(true);
		    dialog.setCaption("Dominios del ambito");
		    
		    VerticalPanel root = new VerticalPanel();
	        root.setSpacing(10);
	        root.setWidth("500px");
	        root.getElement().getStyle().setProperty("margin", ".5rem");

	        // Estado + descripción por dominio
	        for (Domain domain : map.values()) {
	        	HorizontalPanel item = new HorizontalPanel();
	        	item.setSpacing(6);

	        	Label status = new Label(resolveDomainStatusLabel(domain));
	        	status.getElement().getStyle().setProperty("font-weight", "600");
	        	status.getElement().getStyle().setProperty("width", "5rem");
	        	status.getElement().getStyle().setProperty("color", resolveDomainStatusColor(domain));

	            Label description = new Label(domain.getDescription());
	            description.getElement().getStyle().setProperty("padding-left", ".5rem");

	            item.add(status);
	            item.add(description);
	            root.add(item);
	        }

	        dialog.add(root);
	        dialog.show();
	        dialog.center();
			
		});
		
		tab.addRow(row, userScope_.getScope().getScopeDomains().isEmpty() ? new Label() : infoButon, COLS.INF.getColWidth());

		aviableUserScopes.put(userScope_.getId(), new UserScopeRow(tab.getRowsCount(), userScope_));
	}
	
	private String parseDate(Date date) {
		if(null == date)
			return "";
		
		return formatDate.format(date);
	}
	
	private void getList(Consumer<List<UserScopeFull>> success) {
		Integer userId = resolveSelectedScopeOwnerId();
		
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
				userScope = filterByDomainStatus(result);
				success.accept(userScope);
			}

			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
			}
		});
	}

	private List<UserScopeFull> filterByDomainStatus(List<UserScopeFull> userScopes) {
		if (selectedDomainStatusFilter == null || selectedDomainStatusFilter == DomainStatusFilter.ALL) {
			return userScopes;
		}

		Date todayDay = normalizeDay(new Date());

		return userScopes.stream().filter(us -> {
			HashMap<Integer, Domain> map = us.getScope() == null ? null : us.getScope().getScopeDomains();
			if (map == null || map.isEmpty()) {
				return false;
			}
			return map.values().stream().anyMatch(domain -> matchesFilter(domain, todayDay));
		}).collect(Collectors.toList());
	}

	private boolean matchesFilter(Domain domain, Date todayDay) {
		if (domain == null) {
			return false;
		}

		switch (selectedDomainStatusFilter) {
			case ALL:
				return true;
			case ACTIVE:
				return isActiveDomain(domain, todayDay);
			case INACTIVE:
				return isInactiveDomain(domain, todayDay);
			case BLOCKED:
				return isBlockedDomain(domain, todayDay);
			default:
				return false;
		}
	}

	private Date normalizeDay(Date date) {
		if (date == null) {
			return null;
		}
		return new Date(date.getYear(), date.getMonth(), date.getDate());
	}

	private String resolveDomainStatusLabel(Domain domain) {
		Date todayDay = normalizeDay(new Date());
		if (isBlockedDomain(domain, todayDay)) {
			return "Bloqueado";
		}
		return isActiveDomain(domain, todayDay) ? "Activo" : "Inactivo";
	}

	private String resolveDomainStatusColor(Domain domain) {
		Date todayDay = normalizeDay(new Date());
		if (isBlockedDomain(domain, todayDay)) {
			return "#d32f2f";
		}
		return isActiveDomain(domain, todayDay) ? "#2e7d32" : "#ef6c00";
	}

	private boolean isActiveDomain(Domain domain, Date todayDay) {
		return domain != null && domain.isActive() && !isBlockedDomain(domain, todayDay);
	}

	private boolean isInactiveDomain(Domain domain, Date todayDay) {
		return domain != null && !domain.isActive() && !isBlockedDomain(domain, todayDay);
	}

	private boolean isBlockedDomain(Domain domain, Date todayDay) {
		if (domain == null || domain.getExpirationDate() == null) {
			return false;
		}
		Date expirationDay = normalizeDay(domain.getExpirationDate());
		return expirationDay != null && todayDay != null && !expirationDay.after(todayDay);
	}
	
	private void getUsers(String description, Consumer<ArrayList<User>> success) {
		commonService.getUsers(options.getDomainName(), options.getDomain(), options.getUser(), description, new AsyncCallback<ArrayList<User>>() {

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
		return resolveSelectedScopeOwnerId();
	}

	private Integer resolveSelectedScopeOwnerId() {
		String selectedUser = scopeOwnerSB.getValue();
		if (AonStringUtils.isBlank(selectedUser) || "-".equals(selectedUser)) {
			selectedScopeOwnerId = null;
			return null;
		}

		selectedScopeOwnerId = scopeOwnerByLabel.get(selectedUser);
		return selectedScopeOwnerId;
	}
	
	protected abstract void showWarning(String message);
	protected abstract void showError(String message);
	protected abstract void hideMessage();

	protected abstract void onAuthorizeUserScopes(List<UserScopeFull> selectedUserScopes);
	
}
