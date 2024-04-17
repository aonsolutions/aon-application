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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCreditorFullPanel;
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
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IRegistryStatusVisitor;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
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

public class CreditorModule extends MainEntryPoint {
		
	private static RegistryServiceAsync service;
	static {
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		service = new RegistryServiceAsyncDecorator(registryServiceRaw);
	}
	
	private static CommonServiceAsync commonService;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);

	}
	
	private static class CreditorRow {
		private int row;
		private Creditor creditor;
		
		private CreditorRow( int row, Creditor creditor) {
			this.row = row;
			this.creditor = creditor;
		}
		
		private int getRow() {
			return row;
		}
		
		private Creditor getCreditor() {
			return creditor;
		}
	}
	
	private DockLayoutPanel dockLayoutPanel;
	private FlowPanel container;
	private AonDisplayGrid tab;
	
	private LinkedHashMap<Integer,CreditorRow> creditors = new LinkedHashMap<>();
	private LinkedHashSet<Integer> selectedItems = new LinkedHashSet<>();
	
	private CreditorModuleSearchPanel searchPanel;
	private AonToolbar toolbar;
	
	private AonToolbarButton checkAll; 
	private AonToolbarButton uncheckAll;
	
	private FlowPanel progressContainer = new FlowPanel();
	private FlowPanel progress = new FlowPanel();

	private InlineLabel selectedCount;
	
	private static final int LIMIT = 100;
	private static final MutableInt offset = new MutableInt(0);
	private static final MutableInt moreData = new MutableInt(0);
	private static final MutableInt searchEnabled = new MutableInt( 0 ); 
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
		dockLayoutPanel.addNorth(getToolbarPanel( opt ), AonToolbar.HEIGTH );
		searchPanel = new CreditorModuleSearchPanel(opt);
		dockLayoutPanel.addNorth(searchPanel, RegistryModuleSearchPanel.HEIGHT);
		progressContainer.setVisible(false);
		progressContainer.add(progress);
		dockLayoutPanel.addNorth(progressContainer, 5);
		SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(splitLayoutPanel);
		SimpleLayoutPanel centerLayoutPanel = new SimpleLayoutPanel();
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
		// , ACT(AonStringUtils.EMPTY, 20 ,AON.CSS.aonTextCenter())
		;

		String headerLabel;
		int colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,int colWidth,String cellStyleClass) {
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
		toolbar = new AonToolbar(AON.MSG.creditor());

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
				dialog.setCaption(AON.MSG.creditor());
				AonCreditorFullPanel creditorPanel = new AonCreditorFullPanel(opt, CreditorFull.initialize(opt.getDomain()), new AonRegistryFullPanelCallback<CreditorFull>() {
					
					@Override
					public void onError(Throwable caught) {
						showError(caught.getMessage());
					}
					
					@Override
					public void onAccept(CreditorFull cf) {
						dialog.hide();
					}
					
					@Override
					public void onCancel() {
						// Empty method
					}

					@Override
					public void onDocumenthanged(CreditorFull creditorFull) {
						// Empty method
					}
					
					@Override
					public void setFocus(boolean b) {
						// Empty method
					}
				});
				dialog.add( creditorPanel );
				dialog.center();
				dialog.show();
				
				Scheduler.get().scheduleDeferred(() -> creditorPanel.setFocus(true));
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

		checkAll  = new AonToolbarButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll.setEnabled(false);
		
		checkAll.addClickHandler(event -> checkAll(true));
		toolbar.add(checkAll );
		
		uncheckAll  = new AonToolbarButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.setEnabled(false);
		
		uncheckAll.addClickHandler(event -> checkAll(false));
		
		toolbar.add(uncheckAll);
		
		AonToolbarButton printPDF = new AonToolbarButton(AON.MSG.printPDF(), AON.CSS.aonIconPdf());
		printPDF.addClickHandler(e -> printPDFReport(opt));
		toolbar.add(printPDF);
		
		
		AonToolbarButton printXLS = new AonToolbarButton(AON.MSG.printExcel(), AON.CSS.aonIconExcel());
		printXLS.addClickHandler(e -> printXLSReport(opt));
		toolbar.add(printXLS);

		return toolbar;
	}
	
	
	protected void checkAll(boolean check) {
		for (CreditorRow creditorRow : creditors.values()) {
			creditorRow.getCreditor().setSelected(check);
			manageSelection(creditorRow.getCreditor());
			AonDisplayGridRow row = (AonDisplayGridRow) tab.getWidget(creditorRow.getRow());
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
	 * Method that searchs a creditor calling the method search
	 * @param opt
	 * @param params
	 */
	protected void search(final RegistryModuleOptions opt,RegistryParams params) {
		enableMoreData();
		creditors.clear();
		clearSelection();
		container.clear();
		tab = getTable();
		container.add(tab);
		offset.setValue(0);
		search(opt, params, offset.getValue());
	}

	/**
	 * Gets a creditor with the given params
	 * @param opt module options
	 * @param params creditor params
	 * @param ofs offset
	 */
	private void search(final RegistryModuleOptions opt, RegistryParams params, final int ofs) {
		if (!isMoreData()) return; 
		service.getCreditors(opt.getDomainName(),opt.getDomain(),opt.getUser(), params, ofs, LIMIT
				, new AsyncCallback<LinkedList<Creditor>>() {
					
					@Override
					public void onSuccess(LinkedList<Creditor> result) {
						checkAll.setEnabled(false);
						uncheckAll.setEnabled(false);
						if (result != null && !result.isEmpty()) {
							result.forEach( creditor -> paintRow(opt,creditor));
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
	
	private static class CreditorTableRow {
		Label confidential = new Label();
		Label documentTypeLabel  = new Label();
		Label documentCountryLabel  = new Label();
		Label documentLabel  = new Label();
		Label nameLabel  = new Label();
		Label aliasLabel  = new Label();
		Label status = new Label();
		
		public CreditorTableRow(Creditor creditor) {
			setData(creditor);
		}
		
		void setData(Creditor creditor) {
			if (creditor.isConfidential()) {
				confidential.setTitle(AON.MSG.confidential());
				confidential.setStyleName(AON.CSS.aonIconLabel());
				confidential.addStyleName(AON.CSS.aonIconConfidential());
			}
			
			documentTypeLabel.setText(creditor.getDocumentType() == null ? AonStringUtils.EMPTY : creditor.getDocumentType().getDescription());
			documentCountryLabel.setText(creditor.getDocumentCountry() == null ? AonStringUtils.EMPTY : creditor.getDocumentCountry().getIso2());
			documentLabel.setText(creditor.getDocument());
			nameLabel.setText(creditor.getName());
			aliasLabel.setText(creditor.getAlias());
			
			status.setTitle(creditor.getStatus() == null ?"":creditor.getStatus().getDescription());
			status.setStyleName(AON.CSS.aonIconLabel());
			creditor.getStatus().visit( new IRegistryStatusVisitor() {
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
	 * Paints a row of the creditor table for a determined creditor
	 * @param opt module options
	 * @param creditor the creditor
	 */
	private void paintRow(final RegistryModuleOptions opt, Creditor creditor) {
		int row = tab.getWidgetCount();
		creditors.put(creditor.getId(), new CreditorRow(row, creditor));
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), selectedItems.contains(creditor.getId())?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());
		
		checkButton.addClickHandler(event -> {
				if (selectedItems.contains(creditor.getId())) {
					creditor.setSelected(false);
					manageSelection( creditor );
					checkButton.addStyleName(AON.CSS.aonIconCheck());
					checkButton.removeStyleName(AON.CSS.aonIconChecked());
				} else {
					creditor.setSelected(true);
					manageSelection( creditor );
					checkButton.addStyleName(AON.CSS.aonIconChecked());
					checkButton.removeStyleName(AON.CSS.aonIconCheck());
				}
		});

		CreditorTableRow creditorTableRow = new CreditorTableRow(creditor);
	
		AonDisplayGridRow creditorRow = tab.addRow();
		
		creditorRow.addClickHandler(event -> selectCreditor(opt, creditor, new AonRegistryFullPanelCallback<CreditorFull>() {
			@Override
			public void onError(Throwable caught) {
				showError(caught.getMessage());
			}
			
			@Override
			public void onAccept(CreditorFull cf) {
				creditorTableRow.setData(cf.getRegistry());
			}

			@Override
			public void onCancel() {
				// Empty method
			}

			@Override
			public void onDocumenthanged(CreditorFull creditorFull) {
				// Empty method
			}

			@Override
			public void setFocus(boolean b) {
				// Empty method
			}
		}));
		
		creditorRow.addCell(checkButton)
			.addCell(creditorTableRow.status)
			.addCell(creditorTableRow.confidential)
			.addCell(creditorTableRow.documentTypeLabel)
			.addCell(creditorTableRow.documentCountryLabel)
			.addCell(creditorTableRow.documentLabel)
			.addCell(creditorTableRow.nameLabel)
			.addCell(creditorTableRow.aliasLabel)
			// .addCell(detailsButton)
			;
	}
	
	private void clearSelection() {
		selectedItems.clear();
	}
	private void manageSelection(Creditor creditor) {
		if (creditor.isSelected()) {
			selectedItems.add(creditor.getId());
		} else {
			selectedItems.remove(creditor.getId());
		}
		refreshIcons();
	}
	
	private void refreshIcons() {
		selectedCount.setText( (!selectedItems.isEmpty())?  AonNumberUtils.toString(selectedItems.size()) :""); 
	}
	
	/**
	 * Method that gets a creditorFull to call selectCreditor with that creditorFull 
	 * @param opt module options
	 * @param creditor creditor
	 * @param panelCallback
	 */
	private void selectCreditor(RegistryModuleOptions opt, Creditor creditor, AonRegistryFullPanelCallback<CreditorFull> panelCallback) {
		
		service.getCreditorFull(opt.getDomainName(), opt.getDomain(), opt.getUser(), creditor.getId(), new AsyncCallback<CreditorFull>() {	
			@Override
			public void onSuccess(CreditorFull result) {
				selectCreditor(opt, result, panelCallback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());	
			}
		});					
	}
	
	/**
	 * When the user clicks on a creditor of the creditor table, this panel opens and allows to edit and to see more details.
	 * The creditorPanel shows the creditor data. AonCreditorFullPanel extends AonRegistryFullPanel which includes more methods.
	 * @param opt module options
	 * @param creditor the creditor
	 * @param panelCallback
	 */
	private void selectCreditor(RegistryModuleOptions opt, CreditorFull creditor, AonRegistryFullPanelCallback<CreditorFull> panelCallback) {
		final AonSimpleDialog dialog = new AonSimpleDialog();
		dialog.setWidth(AonRegistryFullPanel.MIN_WIDTH +  "px");
		dialog.setHeight(AonRegistryFullPanel.MIN_HEIGHT +  "px");
		dialog.setCaption(AON.MSG.creditor());
		
		AonCreditorFullPanel creditorPanel = new AonCreditorFullPanel(opt, creditor, new AonRegistryFullPanelCallback<CreditorFull>() {
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
			public void onAccept(CreditorFull cf) {
				dialog.hide();
				if (panelCallback != null) panelCallback.onAccept(cf);
			}
			
			@Override
			public void onDocumenthanged(CreditorFull creditorFull) {
				if (panelCallback != null) panelCallback.onDocumenthanged(creditorFull);
			}
		});
		
		dialog.add( creditorPanel );
		dialog.center();
		dialog.show();
				
		Scheduler.get().scheduleDeferred(() -> creditorPanel.setFocus(true));		
	}
	
	private void printPDFReport(RegistryModuleOptions opt) {
		FormPanel diskForm = new FormPanel("_blank");
		String action = URL.encode(GWT.getModuleBaseURL() + "roms/CreditorPDFServlet");
		diskForm.setAction(action);
		diskForm.setMethod(FormPanel.METHOD_POST);
		
		Hidden registryParamsHidden = new Hidden(IRequestParamsNames.REGISTRY_PARAMS);
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		domainIdHidden.setValue(AonNumberUtils.toString(opt.getDomain()));
		userHidden.setValue(opt.getUser());
		domainNameHidden.setValue(opt.getDomainName());
		RegistryParams params = searchPanel.getParams(opt);
		registryParamsHidden.setValue(JsonParams.convert(params));

		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		
		formFlowPanel.add(registryParamsHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		
		toolbar.add(diskForm);
		diskForm.submit();
	}
	
	
	private void printXLSReport(RegistryModuleOptions opt) {
		FormPanel diskForm = new FormPanel("_blank");
		String action = URL.encode(GWT.getModuleBaseURL() + "roms/CreditorXLSServlet");
		diskForm.setAction(action);
		diskForm.setMethod(FormPanel.METHOD_POST);
		
		Hidden registryParamsHidden = new Hidden(IRequestParamsNames.REGISTRY_PARAMS);
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		domainIdHidden.setValue(AonNumberUtils.toString(opt.getDomain()));
		userHidden.setValue(opt.getUser());
		domainNameHidden.setValue(opt.getDomainName());
		RegistryParams params = searchPanel.getParams(opt);
		registryParamsHidden.setValue(JsonParams.convert(params));

		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		
		formFlowPanel.add(registryParamsHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		
		toolbar.add(diskForm);
		diskForm.submit();
	}
}		