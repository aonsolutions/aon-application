package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomerFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel.AonRegistryFullPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSimpleDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IRegistryStatusVisitor;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class CustomerModule extends MainEntryPoint {
	
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);
	
	private static RegistryServiceAsync service;
	static {
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		service = new RegistryServiceAsyncDecorator(registryServiceRaw);
	}
	
	private static final CommonServiceAsync commonService;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
	}
	
	private static class CustomerRow {
		private int row;
		private Customer customer;
		
		private CustomerRow( int row, Customer customer) {
			this.row = row;
			this.customer = customer;
		}
		private int getRow() {
			return row;
		}
		private Customer getCustomer() {
			return customer;
		}
	}
	
	private DockLayoutPanel dockLayoutPanel;
	private FlowPanel container;
	private AonDisplayGrid tab;
	private SplitLayoutPanel splitLayoutPanel;
	private AonMinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private ScrollPanel extraInfoContainer;
	
	private LinkedHashMap<Integer,CustomerRow> customers = new LinkedHashMap<>();
	private LinkedHashSet<Integer> selectedItems = new LinkedHashSet<>();
	
	private CustomerModuleSearchPanel searchPanel;
	private AonToolbar toolbar;
	
	private AonToolbarButton checkAll; 
	private AonToolbarButton uncheckAll;
	
	private FlowPanel progressContainer = new FlowPanel();
	private FlowPanel progress = new FlowPanel();

	private InlineLabel selectedCount;
	private boolean minimizedByUser;
	private int extraInfoTabIndex;
	
	private static final int LIMIT = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 ); 
	private int lastScrollPos = 0;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		RegistryModuleOptions options = new RegistryModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final RegistryModuleOptions opt ) {
		AON.ensureInjected();

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		
		if ( opt.getConfiguration() == null) {
			commonService.getAonConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					opt.setConfiguration(result);
					loadModule( opt );					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					dockLayoutPanel.add(new Label(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]"));
				}
			});
		} else {
			loadModule( opt );
		}
	}
	
	private void loadModule( final RegistryModuleOptions opt ) {
		SimpleLayoutPanel centerLayoutPanel = new SimpleLayoutPanel();
		dockLayoutPanel.addNorth(getToolbarPanel( opt ), AonToolbar.HEIGTH );
		searchPanel = new CustomerModuleSearchPanel(opt);
		dockLayoutPanel.addNorth(searchPanel, 75);
		progressContainer.setVisible(false);
		progressContainer.add(progress);
		dockLayoutPanel.addNorth(progressContainer, 5);
		splitLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(splitLayoutPanel);
		ScrollPanel centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.CSS.aonScrollArea());
		centerPanel.addStyleName(AON.CSS.aonMarginBottom());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.setWidget(centerPanel);
		splitLayoutPanel.add(centerLayoutPanel);
		
		centerPanel.addScrollHandler(event -> {
			int oldScrollPos = lastScrollPos;
			lastScrollPos = centerPanel.getVerticalScrollPosition();
			
			if (oldScrollPos >= lastScrollPos) {
				return;
			}

			if (isSearchEnabled()) {
				int maxScrollTop = centerPanel.getWidget().getOffsetHeight() - centerPanel.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					search(opt, searchPanel.getParams( opt ),offset.getValue());
				}
			}
		});
		
		searchPanel.addValueChangeHandler(event -> {
			RegistryParams params = event.getValue();
			search( opt, params );
		});
		
		Scheduler.get().scheduleDeferred(() -> search(opt, searchPanel.getParams(opt)));		
	}

	private enum COLS {
		  CHK(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
	    , STA(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, SEC(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, DCT("Tipo"				, 50 ,AON.CSS.aonTextLeft())
		, DCC("Pais"				, 40 ,AON.CSS.aonTextLeft())
		, DOC("N\u00BA Documento"		, 130,AON.CSS.aonTextLeft())
		, AUTO(AON.MSG.name()			, 0  ,AON.CSS.aonTextLeft())
		, ALS(AON.MSG.alias()			, 200,AON.CSS.aonTextLeft())
		//, ACT(AonStringUtils.EMPTY	, 20 ,AON.CSS.aonTextCenter())
		;

		String headerLabel;
		int colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,int colWidth, String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public int getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}

	protected AonDisplayGrid getTable() {
		tab = new AonDisplayGrid();
		tab.setStyleName(AON.CSS.aonGrid());
		tab.addStyleName(AON.CSS.aonNoPadding());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		
		int autoWidth = container.getOffsetWidth() - 80;
		for ( COLS col : COLS.values()) {
			if (col != COLS.AUTO ) {
				autoWidth -= (col.getColWidth() + 2); 
			}
		}
		
		selectedCount = new InlineLabel();
		AonDisplayGridHeaderRow headerRow = tab.addHeaderRow();
		for ( COLS col : COLS.values()) {
			Label label = col == COLS.CHK ? selectedCount : new Label( col.getHeaderLabel() );
			if (col == COLS.AUTO ) {
				label.setWidth(autoWidth + "px");
			} else {
				label.setWidth(col.getColWidth()  + "px");
			}
			headerRow.addCell(label,col.getCellStyleClass());
		}
		return tab;
	}

	private Widget getToolbarPanel(final RegistryModuleOptions opt) {
		toolbar = new AonToolbar(AON.MSG.customer());

		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		Hidden registryParamsHidden = new Hidden(IRequestParamsNames.REGISTRY_PARAMS);
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(registryParamsHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		toolbar.add(diskForm);

		AonToolbarButton addButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		
		addButton.addClickHandler(event -> {
			final AonSimpleDialog dialog = new AonSimpleDialog();
			dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
			dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
			dialog.setCaption(AON.MSG.customer());
				
			AonCustomerFullPanel customerPanel = new AonCustomerFullPanel(opt, CustomerFull.initialize(opt.getDomain()), new AonRegistryFullPanelCallback<CustomerFull>() {
				@Override
				public void setFocus(boolean b) {
					// Empty method
				}
						
				@Override
				public void onError(Throwable caught) {
					showError(caught.getMessage());
				}
					
				@Override
				public void onCancel() {
					dialog.hide();		
				}
						
				@Override
				public void onAccept(CustomerFull rf) {
					dialog.hide();
				}
	
				@Override
				public void onDocumenthanged(CustomerFull registryFull) {
					// Empty method
				}
			});
				
			dialog.add( customerPanel );
			dialog.center();
			dialog.show();
				
			Scheduler.get().scheduleDeferred(() -> customerPanel.setFocus(true));	
				
		});
		
		toolbar.add(addButton);

		AonToolbarButton searchButton = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
		searchButton.addClickHandler(event -> {
				enableMoreData();
				container.clear();
				tab = getTable();
				container.add(tab);
				offset.setValue(0);
				search(opt, searchPanel.getParams( opt ), offset.getValue());
		});
		
		toolbar.add(searchButton);

		checkAll = new AonToolbarButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll.setEnabled(false);
		
		checkAll .addClickHandler(event -> checkAll(true));
		
		toolbar.add(checkAll );
		
		uncheckAll = new AonToolbarButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.setEnabled(false);
		
		uncheckAll.addClickHandler(event -> checkAll(false));
		
		toolbar.add(uncheckAll);

		return toolbar;
	}
	
	protected void checkAll(boolean check) {
		for (CustomerRow customerRow : customers.values()) {
			customerRow.getCustomer().setSelected(check);
			manageSelection(customerRow.getCustomer());
			AonDisplayGridRow row = (AonDisplayGridRow) tab.getWidget(customerRow.getRow());
			AonDisplayGridCell cell = (AonDisplayGridCell) row.getWidget(0);
			Widget w = cell.getWidget(0);
			
			if (check) {
				w.addStyleName(AON.CSS.aonIconChecked());
				w.removeStyleName(AON.CSS.aonIconCheck());
			} else {
				w.addStyleName(AON.CSS.aonIconCheck());
				w.removeStyleName(AON.CSS.aonIconChecked());
			}
		}
	}
	
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}

	protected void search(final RegistryModuleOptions opt,RegistryParams params) {
		enableMoreData();
		customers.clear();
		clearSelection();
		container.clear();
		tab = getTable();
		container.add(tab);
		offset.setValue(0);
		search(opt, params, offset.getValue());
	}

	private void search(final RegistryModuleOptions opt, RegistryParams params, final int ofs) {
		if (!isMoreData()) return;
		
		service.getCustomers(opt.getDomainName(),opt.getDomain(),opt.getUser(), params, ofs, LIMIT
		, new AsyncCallback<LinkedList<Customer>>() {
			@Override
			public void onSuccess(LinkedList<Customer> result) {
				checkAll.setEnabled(false);
				uncheckAll.setEnabled(false);
				
				if (result != null && !result.isEmpty()) {
					result.forEach( customer -> paintRow(opt,customer));
					offset.setValue(ofs + result.size());
					enableMoreData();
					checkAll.setEnabled(true);
					uncheckAll.setEnabled(true);
				} else {
					Label label = new Label(AON.MSG.noData());
					label.setStyleName(AON.CSS.aonBlockMessage());
					label.addStyleName(AON.CSS.aonBlockInfoMessage());
					label.addStyleName(AON.CSS.aonMarginTop());
					container.add(label);
					disableMoreData();
				}
				enableSearch();
			}
					
			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
			}
		});	
	}
	
	public void addExtraInfo (String htmlText) {
		HTMLPanel panel = new HTMLPanel(htmlText);
		addExtraInfo(panel);
	}
	
	public void addExtraInfo (Widget widget) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(extraInfoTabIndex);
		extraInfoContainer.setWidget(widget);
		extraInfoContainer.scrollToTop();
	}

	private void showError(String msg) {
		if (AonStringUtils.isBlank(msg)) {
			msg = "Se ha producido un error no codificado.";
		}
		toolbar.showErrorMessage(msg);
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}

	private void openFootPanelIfNeeded() {
		if (!minimizedByUser && splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
			openFootPanel();
		}
	}
	
	private void openFootPanel() {
		double effectiveHeigth = 5;
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / effectiveHeigth);
		splitLayoutPanel.animate(500);
	}

	private void paintRow(final RegistryModuleOptions opt, Customer customer) {
		int row = tab.getWidgetCount();
		customers.put(customer.getId(), new CustomerRow(row, customer));
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction()
				, selectedItems.contains(customer.getId())?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());
		
		checkButton.addClickHandler(event -> {
				if (selectedItems.contains(customer.getId())) {
					customer.setSelected(false);
					manageSelection( customer );
					checkButton.addStyleName(AON.CSS.aonIconCheck());
					checkButton.removeStyleName(AON.CSS.aonIconChecked());
				} else {
					customer.setSelected(true);
					manageSelection( customer );
					checkButton.addStyleName(AON.CSS.aonIconChecked());
					checkButton.removeStyleName(AON.CSS.aonIconCheck());
				}
		});

		Label confidential = new Label();
		if (customer.isConfidential()) {
			confidential.setTitle(AON.MSG.confidential());
			confidential.setStyleName(AON.CSS.aonIconLabel());
			confidential.addStyleName(AON.CSS.aonIconConfidential());
		}

		Label documentTypeLabel  = new Label( customer.getDocumentType() == null ? AonStringUtils.EMPTY : customer.getDocumentType().getDescription());
		Label documentCountryLabel  = new Label( customer.getDocumentCountry() == null ? AonStringUtils.EMPTY : customer.getDocumentCountry().getIso2());
		Label documentLabel  = new Label( customer.getDocument() );
		Label nameLabel  = new Label( customer.getName() );
		Label aliasLabel  = new Label( customer.getAlias() );
		
		Label status = new Label();
		status.setTitle(customer.getStatus() == null ?"":customer.getStatus().getDescription());
		status.setStyleName(AON.CSS.aonIconLabel());
		customer.getStatus().visit( new IRegistryStatusVisitor() {
			@Override
			public void visitActive() {
				status.addStyleName(AON.CSS.aonIconValid());
			}
			
			@Override
			public void visitInactive() {
				status.addStyleName(AON.CSS.aonIconInvalid());
			}
			
			@Override
			public void visitBlocked() {
				status.addStyleName(AON.CSS.aonIconBlock());
			}
		});
		
		AonDisplayGridRow customerRow = tab.addRow();
		customerRow.addClickHandler(event -> selectCustomer(opt, customer));
		
		customerRow.addCell(checkButton)
			.addCell(status)
			.addCell(confidential)
			.addCell(documentTypeLabel)
			.addCell(documentCountryLabel)
			.addCell(documentLabel)
			.addCell(nameLabel)
			.addCell(aliasLabel)
			// .addCell(detailsButton)
			;
	}
	

	private void clearSelection() {
		selectedItems.clear();
	}
	
	private void manageSelection(Customer customer) {
		if (customer.isSelected()) {
			selectedItems.add(customer.getId());
		} else {
			selectedItems.remove(customer.getId());
		}
		refreshIcons();
	}
	
	private void refreshIcons() {
		selectedCount.setText( (!selectedItems.isEmpty())?  AonNumberUtils.toString(selectedItems.size()) :""); 
	}
	
	private void selectCustomer(RegistryModuleOptions opt, Customer customer) {
		service.getCustomerFull(opt.getDomainName(), opt.getDomain(), opt.getUser(), customer.getId(), new AsyncCallback<CustomerFull>() {
			
			@Override
			public void onSuccess(CustomerFull result) {
				final AonSimpleDialog dialog = new AonSimpleDialog();
				dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
				dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
				dialog.setCaption(AON.MSG.customer());
				AonCustomerFullPanel customerPanel = new AonCustomerFullPanel(opt, result, new AonRegistryFullPanelCallback<CustomerFull>() {
					
					@Override
					public void setFocus(boolean b) {
						// Empty method
					}
					
					@Override
					public void onError(Throwable caught) {
						showError(caught.getMessage());
					}
					
					@Override
					public void onCancel() {
						dialog.hide();		
					}
					
					@Override
					public void onAccept(CustomerFull rf) {
						dialog.hide();
					}
					@Override
					public void onDocumenthanged(CustomerFull registryFull) {
						// Empty method
					}	
				});
				
				dialog.add( customerPanel );
				dialog.center();
				dialog.show();
				
				Scheduler.get().scheduleDeferred(() -> customerPanel.setFocus(true));
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());	
			}
		});					
	}
}		
