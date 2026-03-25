package com.esferalia.aon.gwt.fiscal.client.registry;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

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
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomerFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonRegistryFullPanel.AonRegistryFullPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSimpleDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IRegistryStatusVisitor;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class CustomerModule  implements EntryPoint {
	
	private static final RegistryServiceAsync service;
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
	
	private LinkedHashMap<Integer,CustomerRow> customers = new LinkedHashMap<>();
	private LinkedHashSet<Integer> selectedItems = new LinkedHashSet<>();
	
	private CustomerModuleSearchPanel filterPanel;
	private AonToolbar toolbar;
	AonToolbarButton filterButton = new AonToolbarButton("", AON.CSS.aonToolbarFilterContainer()); 
	
	private AonToolbarButton checkAll; 
	private AonToolbarButton uncheckAll;
	
	private FlowPanel progressContainer = new FlowPanel();
	private FlowPanel progress = new FlowPanel();

	private InlineLabel selectedCount;
	
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
		filterPanel = new CustomerModuleSearchPanel(opt);
		filterPanel.addCloseHandler(new ClickHandler() {

		    @Override
		    public void onClick(ClickEvent event) {
		    	toogleFilterPanel();
		    }
		});
		dockLayoutPanel.addNorth(filterPanel, RegistryModuleSearchPanel.HEIGHT);
		progressContainer.setVisible(false);
		progressContainer.add(progress);
		dockLayoutPanel.addNorth(progressContainer, 5);
		SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
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
					search(opt, filterPanel.getParams( opt ),offset.getValue());
				}
			}
		});
		
		filterPanel.addValueChangeHandler(event -> {
			RegistryParams params = event.getValue();
			search( opt, params );
		});
		
		Scheduler.get().scheduleDeferred(() -> search(opt, filterPanel.getParams(opt)));		
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
			CustomerFull newCustomer = CustomerFull.initialize(opt.getDomain());
			selectCustomer(opt, newCustomer, new AonRegistryFullPanelCallback<CustomerFull>() {
				@Override
				public void onError(Throwable caught) {
					showError(caught.getMessage());
				}
					
				@Override
				public void onAccept(CustomerFull rf) {
					// Add new customer to table
				}

				@Override
				public void onCancel() {
					// Empty method
				}

				@Override
				public void onDocumenthanged(CustomerFull registryFull) {
					// Empty method
				}

				@Override
				public void setFocus(boolean b) {
					// Empty method	
				}
			});
		});
		
		toolbar.add(addButton);

//		AonToolbarButton searchButton = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
//		searchButton.addClickHandler(event -> {
//				enableMoreData();
//				container.clear();
//				tab = getTable();
//				container.add(tab);
//				offset.setValue(0);
//				search(opt, filterPanel.getParams( opt ), offset.getValue());
//		});
//		
//		toolbar.add(searchButton);

		checkAll = new AonToolbarButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll.setStyleName(AON.CSS.aonDisplayNone()); //se ocultan por que no tiene funcionalidad
		checkAll.setEnabled(false);
		
		checkAll.addClickHandler(event -> checkAll(true));
		
		toolbar.add(checkAll );
		
		uncheckAll = new AonToolbarButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.setStyleName(AON.CSS.aonDisplayNone()); //se ocultan por que no tiene funcionalidad
		uncheckAll.setEnabled(false);
		
		uncheckAll.addClickHandler(event -> checkAll(false));
		
		toolbar.add(uncheckAll);

		filterButton.setHTML("<span class='material-icons'>filter_alt_off</span>");
		
		filterButton.addClickHandler(new ClickHandler() {

		    @Override
		    public void onClick(ClickEvent event) {
		    	toogleFilterPanel();
		    }
		});
		
		toolbar.add(filterButton);
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

	/**
	 * Method that searchs a customer calling the method search
	 * @param opt
	 * @param params
	 */
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

	/**
	 * Gets a customer with the given params
	 * @param opt module options
	 * @param params customer params
	 * @param ofs offset
	 */
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

	private void showError(String msg) {
		if (AonStringUtils.isBlank(msg)) {
			msg = "Se ha producido un error no codificado.";
		}
		toolbar.showErrorMessage(msg);
	}

	private static class CustomerTableRow {
		
		Label confidential = new Label();
		Label documentTypeLabel  = new Label();
		Label documentCountryLabel = new Label();
		Label documentLabel  = new Label();
		Label nameLabel = new Label();
		Label aliasLabel = new Label();
		Label status = new Label();
		
		public CustomerTableRow(Customer customer) {
			setData(customer);
		}

		void setData(Customer customer) {
			 if (customer.isConfidential()) {
				 confidential.setTitle(AON.MSG.confidential());
				 confidential.setStyleName(AON.CSS.aonIconLabel());
				 confidential.addStyleName(AON.CSS.aonIconConfidential());
			 }
			 
			 documentTypeLabel.setText( customer.getDocumentType() == null ? AonStringUtils.EMPTY : customer.getDocumentType().getDescription());
			 documentCountryLabel.setText( customer.getDocumentCountry() == null ? AonStringUtils.EMPTY : customer.getDocumentCountry().getIso2());
			 documentLabel.setText( customer.getDocument() );
			 nameLabel.setText( customer.getName() );
			 aliasLabel.setText( customer.getAlias() );			 
			 
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
		 }
		
	}
	
	/**
	 * Paints a row of the customer table for a determined customer
	 * @param opt module options
	 * @param customer the customer
	 */
	private void paintRow(final RegistryModuleOptions opt, Customer customer) {
		
		int row = tab.getWidgetCount();
		customers.put(customer.getId(), new CustomerRow(row, customer));
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), selectedItems.contains(customer.getId())?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());
		checkButton.setStyleName(AON.CSS.aonDisplayNone());
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
		
		CustomerTableRow customerTableRow = new CustomerTableRow(customer);
		
		AonDisplayGridRow customerRow = tab.addRow();
		
		customerRow.addClickHandler(event -> selectCustomer(opt, customer, new AonRegistryFullPanelCallback<CustomerFull>() {
			@Override
			public void onError(Throwable caught) {
				showError(caught.getMessage());
			}
			
			@Override
			public void onAccept(CustomerFull rf) {
				customerTableRow.setData(rf.getRegistry());
			}

			@Override
			public void onCancel() {
				// Empty method
			}

			@Override
			public void onDocumenthanged(CustomerFull registryFull) {
				// Empty method
			}

			@Override
			public void setFocus(boolean b) {
				// Empty method
			}
		}));
		
		customerRow.addCell(checkButton)
			.addCell(customerTableRow.status)
			.addCell(customerTableRow.confidential)
			.addCell(customerTableRow.documentTypeLabel)
			.addCell(customerTableRow.documentCountryLabel)
			.addCell(customerTableRow.documentLabel)
			.addCell(customerTableRow.nameLabel)
			.addCell(customerTableRow.aliasLabel)
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
	
	/**
	 * Method that gets a customerFull to call selectCustomer with that customerFull 
	 * @param opt module options
	 * @param customer customer
	 * @param panelCallback
	 */
	private void selectCustomer(RegistryModuleOptions opt, Customer customer, AonRegistryFullPanelCallback<CustomerFull> panelCallback) {
		
		service.getCustomerFull(opt.getDomainName(), opt.getDomain(), opt.getUser(), customer.getId(), new AsyncCallback<CustomerFull>() {	
			@Override
			public void onSuccess(CustomerFull result) {
				selectCustomer(opt, result, panelCallback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());	
			}
		});					
	}
	
	/**
	 * When the user clicks on a customer of the customer table, this panel opens and allows to edit and to see more details.
	 * The customerPanel shows the customer data. AonCustomerFullPanel extends AonRegistryFullPanel which includes more methods.
	 * @param opt module options
	 * @param customer the costumer
	 * @param panelCallback
	 */
	private void selectCustomer(RegistryModuleOptions opt, CustomerFull customer, AonRegistryFullPanelCallback<CustomerFull> panelCallback) {
		final AonSimpleDialog dialog = new AonSimpleDialog();
		dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
		dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
		dialog.setCaption(AON.MSG.customer());
		
		AonCustomerFullPanel customerPanel = new AonCustomerFullPanel(opt, customer, new AonRegistryFullPanelCallback<CustomerFull>() {
			@Override
			public void setFocus(boolean b) {
				if (panelCallback != null) panelCallback.setFocus(b);
			}
			
			@Override
			public void onError(Throwable caught) {
				if (panelCallback != null) panelCallback.onError(caught);
			}
			
			@Override
			public void onCancel() {
				dialog.hide();
				if (panelCallback != null) panelCallback.onCancel();
			}
			
			@Override
			public void onAccept(CustomerFull rf) {
				dialog.hide();
				if (panelCallback != null) panelCallback.onAccept(rf);
			}
			@Override
			public void onDocumenthanged(CustomerFull registryFull) {
				if (panelCallback != null) panelCallback.onDocumenthanged(registryFull);
			}	
		});
		
		dialog.add( customerPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(() -> customerPanel.setFocus(true));				
	}
	
	public void closeFilterPanel() {
		dockLayoutPanel.remove(filterPanel);	
		dockLayoutPanel.animate(500, new AnimationCallback() {
			
			public void onAnimationComplete() {
				MinimizeEvent.fire(dockLayoutPanel);
	            filterButton.setHTML("<span class='material-icons'>filter_alt</span>");
			}
			@Override
			public void onLayout(Layer arg0, double arg1) {							
			}
		});
	}

	public void openFilterPanel() {
	    dockLayoutPanel.insertNorth(filterPanel, RegistryModuleSearchPanel.HEIGHT, progressContainer);
	    dockLayoutPanel.animate(500, new AnimationCallback() {
	        @Override
	        public void onAnimationComplete() {
	            MaximizeEvent.fire(dockLayoutPanel);
				filterButton.setHTML("<span class='material-icons'>filter_alt_off</span>");
	        }

	        @Override
	        public void onLayout(Layer layer, double progress) {
	        }
	    });
	}
	
	public void toogleFilterPanel() {
		if (dockLayoutPanel.getWidgetIndex(filterPanel) != -1) {
            closeFilterPanel();
        } else {
            openFilterPanel();
        }
	}

}		