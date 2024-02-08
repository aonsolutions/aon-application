package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog.AonCustomDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFBatchPaymentPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFBatchPaymentPanel.AonFBatchPaymentPayrollPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFBatchStatusVisitor;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class FBatchPaymentModule extends MainEntryPoint {
	
	public static enum FBATCH_TYPE {
		  CHARGE
		, PAYMENT
		, PAYROLL_PAYMENT
		;
	}
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;
	
	// ------- FBatchRow
	
	private static class FBatchRow {
		
		private int row;
		private FBatch fBatch;
		
		private FBatchRow( int row, FBatch fBatch) {
			this.row = row;
			this.fBatch = fBatch;
		}
		
		private int getRow() {
			return row;
		}
		
		private FBatch getFBatch() {
			return fBatch;
		}
		
	}
	
	// Variables
	
	private DeckLayoutPanel deckLayoutPanel;
	private FBatchPaymentEntryModule fBatchPaymentPayrollEntryModule;
	
	// Listado Remesas
	private DockLayoutPanel dockLayoutPanel;
	private FlowPanel dockLayoutContent;
	private HTMLPanel messagePanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private FlexTable tab;
	private int autoWidth; 
	
	private LinkedHashMap<Integer,FBatchRow> fBatches = new LinkedHashMap<Integer,FBatchRow>();
	private LinkedHashSet<Integer> selectedItems = new LinkedHashSet<Integer>();
	
	private FBatchPaymentModuleSearchPanel searchPanel;
	private AonToolbar toolbar;
	private AonToolbarButton resetSearchButton;
	private AonToolbarButton checkAll; 
	private AonToolbarButton uncheckAll;
	private AonToolbarButton deleteButton;
	private AonToolbarButton newButton;

	private InlineLabel selectedCount;
	
	final private int limit = 100;
	final private MutableInt offset = new MutableInt(0);
	final private MutableInt moreData = new MutableInt(0);
	final private MutableInt searchEnabled = new MutableInt( 0 ); 
	private int lastScrollPos = 0;
	
	private FBATCH_TYPE fbatchType;
	
	// -------------------------------------------------------------------
	// ----------------------  ON MODULE LOAD  ---------------------------
	// -------------------------------------------------------------------
	
	public FBatchPaymentModule(FBATCH_TYPE fbatchType) {
		this.fbatchType = fbatchType;
	}

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		FinanceModuleOptions options = new FinanceModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final FinanceModuleOptions opt ) {
		AON.ensureInjected();

		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		deckLayoutPanel = new DeckLayoutPanel();

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		deckLayoutPanel.add(dockLayoutPanel);
		
		fBatchPaymentPayrollEntryModule = new FBatchPaymentEntryModule(fbatchType) {
			
			@Override
			public void back(boolean refresh) {
				if(refresh) {
					toolbar.hideMessages();
					enableMoreData();
					container.clear();
					tab = getTable();
					container.add(tab);
					offset.setValue(0);
					search(opt, searchPanel.getParams( opt ), offset.getValue());
				}
				
				deckLayoutPanel.showWidget(0);
			}
			
		};
		deckLayoutPanel.add(fBatchPaymentPayrollEntryModule);
		
		deckLayoutPanel.showWidget(0);
		opt.getParentWidget().add(deckLayoutPanel);
		
		if ( opt.getConfiguration() == null) {
			COMMON_SERVICE.getAonConfiguration(opt.getDomainName(),opt.getDomain(),opt.getUser(),new AsyncCallback<AonConfiguration>() {
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
	
	private void loadModule( final FinanceModuleOptions opt ) {
		dockLayoutPanel.addNorth(getToolbarPanel( opt ), AonToolbar.HEIGTH );
		searchPanel = new FBatchPaymentModuleSearchPanel(opt, this.fbatchType);
		dockLayoutPanel.addNorth(searchPanel, 100.00);
		dockLayoutContent = new FlowPanel();
		messagePanel = new HTMLPanel("");
		dockLayoutContent.add(messagePanel);
		dockLayoutPanel.add(dockLayoutContent);
		centerPanel = new ScrollPanel();
		centerPanel.setHeight((Window.getClientHeight() - 300) + "px");
		centerPanel.getElement().getStyle().setProperty("padding", "0 1.5em 1.5em 1.5em");
		centerPanel.addStyleName(AON.CSS.aonMarginBottom());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		dockLayoutContent.add(centerPanel);
		centerPanel.addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = centerPanel.getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = centerPanel.getWidget().getOffsetHeight() - centerPanel.getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						search(opt, searchPanel.getParams( opt ),offset.getValue());
					}
				}
			}
		});
		searchPanel.addValueChangeHandler( new ValueChangeHandler<FBatchParams>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<FBatchParams> event) {
				toolbar.hideMessages();
				FBatchParams params = event.getValue();
				search( opt, params );
			}
		});
		
		// Auto search first time
		enableMoreData();
		container.clear();
		tab = getTable();
		container.add(tab);
		offset.setValue(0);
		search(opt, searchPanel.getParams( opt ), offset.getValue());
	}

	// -------------------------------------------------------------------
	// --------------------------  COLUMNS  ------------------------------
	// -------------------------------------------------------------------

	private static enum COLS {
		  SEL(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, CHK(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, DESC("Descripci\u00f3n"		, 0  ,AON.CSS.aonTextLeft())
		, FEC("Fecha"					, 80 ,AON.CSS.aonTextCenter())
		, BAN("Banco"					, 200,AON.CSS.aonTextLeft())
		, CUE("Cuenta Bancaria"			, 250,AON.CSS.aonTextLeft())
		, STA("Estado"					, 100 ,AON.CSS.aonTextLeft())
		, REG("Registros"				, 75 ,AON.CSS.aonTextRight())
		, AMO("Importe"					, 100 ,AON.CSS.aonTextRight())
		, FIL("Tipo"					, 100 ,AON.CSS.aonTextCenter())
		, ACT(AON.MSG.actions()			, 80,AON.CSS.aonTextCenter())
		;

		String headerLabel;
		int colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,int colWidth) {
			this(headerLabel, colWidth, null);
		}

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
	
	// -------------------------------------------------------------------
	// ---------------------------  TABLE  -------------------------------
	// -------------------------------------------------------------------

	protected FlexTable getTable() {
		tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonGrid());
		
		autoWidth = container.getOffsetWidth() - 36;
		for ( COLS col : COLS.values()) {
			if (col != COLS.DESC ) {
				autoWidth -= (col.getColWidth() + 2); 
			}
		}
		
		selectedCount = new InlineLabel();
		for ( COLS col : COLS.values()) {
			if (col == COLS.DESC ) {
				tab.getColumnFormatter().setWidth(col.ordinal(), autoWidth + "px");
			} else {
				tab.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth() + "px");
			}
			tab.setWidget(0, col.ordinal(), col == COLS.CHK ? selectedCount : new Label( col.getHeaderLabel() ));
			tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonGridHeader());
			tab.getFlexCellFormatter().addStyleName(0, col.ordinal(), AON.CSS.aonFixedHeader());
			if ( col.getCellStyleClass() != null) {
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
				tab.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonNowrap());
			}
		}
		return tab;
	}
	
	private void paintRow(final FinanceModuleOptions opt, FBatch fBatch) {
		int row = tab.getRowCount();
		fBatches.put(fBatch.getId(), new FBatchRow(row, fBatch));
		paintRow(opt, fBatch, row);
	}
	
	private void paintRow(final FinanceModuleOptions opt, FBatch fBatch, int row) {
		int col = 0;
		
		AonTableButton selectionButton = new AonTableButton("Ir a la remesa", AON.CSS.aonIconRight()); 
		selectionButton.addClickHandler(e -> {
			fBatchPaymentPayrollEntryModule = new FBatchPaymentEntryModule(fbatchType) {
				
				@Override
				public void back(boolean refresh) {
					if(refresh) {
						toolbar.hideMessages();
						enableMoreData();
						container.clear();
						tab = getTable();
						container.add(tab);
						offset.setValue(0);
						search(opt, searchPanel.getParams( opt ), offset.getValue());
					}
					
					deckLayoutPanel.showWidget(0);
				}
				
			};
			deckLayoutPanel.remove(fBatchPaymentPayrollEntryModule);
			deckLayoutPanel.add(fBatchPaymentPayrollEntryModule);
			
			deckLayoutPanel.showWidget(fBatchPaymentPayrollEntryModule);
			fBatchPaymentPayrollEntryModule.onModuleLoad(opt, fBatch);
		});
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), selectedItems.contains(fBatch.getId()) ? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
		checkButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectedItems.contains(fBatch.getId())) {
					fBatch.setSelected(false);
					manageSelection( fBatch );
					checkButton.addStyleName(AON.CSS.aonIconCheck());
					checkButton.removeStyleName(AON.CSS.aonIconChecked());
				} else {
					fBatch.setSelected(true);
					manageSelection( fBatch );
					checkButton.addStyleName(AON.CSS.aonIconChecked());
					checkButton.removeStyleName(AON.CSS.aonIconCheck());
				}
			}
		});
		
		Label description = new Label(fBatch.getDescription());
		description.setTitle(fBatch.getDescription());
		
		Label issueDate = new Label(AON.DATE_FORMAT.format(fBatch.getIssueDate()));
		
		Label bank = new Label(null == fBatch.getRbank() ? "" : fBatch.getRbank().getAlias());
		Label bankAccount = new Label(null == fBatch.getRbank() ? "" : fBatch.getRbank().getBankAccount().toString());
		
		Label status = new Label(null == fBatch.getStatus() ? "" : fBatch.getStatus().getDescription());
		fBatch.getStatus().visit(new IFBatchStatusVisitor() {
			
			@Override
			public void visitUnknown() {
				// TODO Auto-generated method stub	
			}
			
			@Override
			public void visitPending() {
				status.setStyleName(AON.CSS.aonColorRed());
			}
			
			@Override
			public void visitGenerated() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void visitAccounted() {
				status.setStyleName(AON.CSS.aonColorGreen());
				status.addStyleName(AON.CSS.aonBold());
			}
		});
		
		Label registries = new Label(null == fBatch.getBatchDetails() || fBatch.getBatchDetails().isEmpty() ? "0" :  String.valueOf(fBatch.getBatchDetails().size()));
		registries.addStyleName(AON.CSS.aonTextRight());
		
		Label amount = new Label(null == fBatch.getBatchDetails() || fBatch.getBatchDetails().isEmpty() ? "0.00 \u20ac" : AON.FMT.format(fBatch.getBatchDetails().stream().map(fBatchDetail -> fBatchDetail.getAmount()).reduce(0.00, (a, b) -> a + b)) + " \u20ac");
		amount.addStyleName(AON.CSS.aonTextRight());
		
		Label fileType = new Label(fBatch.getType() == (byte)0 ? "VISA" : "SEPA 34-14 (XML)");
		fileType.addStyleName(AON.CSS.aonTextLeft());
		
		FlowPanel actionsPanel = new FlowPanel();
		
		// Actions Buttons
		AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete()); 
		AonTableButton downloadFile = new AonTableButton(AON.MSG.download() + " fichero SEPA", AON.CSS.aonIconDownload()); 
		AonTableButton deleteFile = new AonTableButton(AON.MSG.deleteAction() + " fichero SEPA", AON.CSS.aonIconDeleteFile());
		AonTableButton sepaButton = new AonTableButton("Crear fichero SEPA", AON.CSS.aonIconXml()); 
		
		// Delete FBatch
		
		setVisible(deleteButton, fBatch.isPending() || fBatch.isGenerated());
		deleteButton.addClickHandler(e -> deleteFBatch(opt, fBatch));
		actionsPanel.add(deleteButton);
		
		// Download File
		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);

		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden = new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		Hidden rattachHidden = new Hidden("rattach");
		Hidden attachTypeHidden = new Hidden("attachType");

		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(rattachHidden);
		formFlowPanel.add(attachTypeHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);

		downloadFile.addClickHandler(e -> {
			diskForm.setAction(GWT.getHostPageBaseURL() + "/ms/download_attachment/");

			rattachHidden.setValue(fBatch.getRattach().toString());
			attachTypeHidden.setValue("registry");
			domainIdHidden.setValue(opt.getDomain() + "");
			domainNameHidden.setValue(opt.getDomainName());
			userHidden.setValue(opt.getUser());

			diskForm.submit();
		});
		setVisible(downloadFile, fBatch.getRattach() != null && fBatch.getType() != (byte)0);
		actionsPanel.add(downloadFile);
		
		// Download File
		deleteFile.addClickHandler(e -> {
			setVisible(downloadFile, false);
			setVisible(deleteFile, false);
			setVisible(diskForm, false);
			setVisible(sepaButton, true);
			
			deleteFile(opt, fBatch);
			status.setText(FBatchStatus.PENDING.getDescription());
			status.setStyleName(AON.CSS.aonColorRed());
		});
		setVisible(deleteFile, fBatch.getRattach() != null && !fBatch.getStatus().equals(FBatchStatus.ACCOUNTED) && fBatch.getType() != (byte)0);
		actionsPanel.add(deleteFile);
		
		sepaButton.addClickHandler(e -> {
			setVisible(downloadFile, true);
			setVisible(deleteFile, true);
			setVisible(diskForm, true);
			setVisible(sepaButton, false);
			
			createSepeFile(opt, fBatch);
			status.setText(FBatchStatus.GENERATED.getDescription());
			status.removeStyleName(AON.CSS.aonColorRed());
		});
		setVisible(sepaButton, fBatch.getRattach() == null && !fBatch.getBatchDetails().isEmpty() && fBatch.getType() != (byte)0 && fBatch.getRbank() != null);
		actionsPanel.add(sepaButton);
		
		setVisible(diskForm, fBatch.getRattach() != null);
		actionsPanel.add(diskForm);
		
		tab.setWidget(row, col, selectionButton);
		++col;
		tab.setWidget(row, col, checkButton);
		++col;
		tab.setWidget(row, col, description);
		description.setWidth(autoWidth + "px");
		description.setStyleName(AON.CSS.aonTruncate());
		++col;
		tab.setWidget(row, col, issueDate);
		++col;
		tab.setWidget(row, col, bank);
		++col;
		tab.setWidget(row, col, bankAccount);
		++col;
		tab.setWidget(row, col, status);
		++col;
		tab.setWidget(row, col, registries);
		++col;
		tab.setWidget(row, col, amount);
		++col;
		tab.setWidget(row, col, fileType);
		++col;
		tab.setWidget(row, col, actionsPanel);
		
		tab.getRowFormatter().getElement(row).getStyle().setProperty("height", "1.5rem");
	}
	
	private void deleteFBatch(FinanceModuleOptions opt, FBatch fBatch) {
		AonDialog deleteDialog = new AonDialog("Eliminaci\u00f3n Remesa",
				new HTML("Se va a proceder a eliminar la remesa '<b>" + fBatch.getDescription() + "</b>'.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));

		deleteDialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				// Nothing to do here
			}

			@Override
			public void onAccept() {
				LinkedList<Integer> fbatchIds = new LinkedList<>();
				fbatchIds.add(fBatch.getId());
				FINANCE_SERVICE.deleteFBatches(opt.getDomainName(), opt.getDomain(), opt.getUser(), fbatchIds, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void arg0) {
						clearSelection();
						toolbar.hideMessages();
						enableMoreData();
						container.clear();
						tab = getTable();
						container.add(tab);
						offset.setValue(0);
						search(opt, searchPanel.getParams( opt ), offset.getValue());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						showError(caught.getMessage());
					}
				});
			}
		});
	}
	
	private void deleteFile(FinanceModuleOptions opt, FBatch fBatch) {
		AonMessagePanel.showLoading(messagePanel,
				"Eliminando fichero SEPA para la remesa '" + fBatch.getDescription() + "'...");
		FINANCE_SERVICE.deleteSepaFile(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch.getRattach(),
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void seccess) {
						fBatch.setRattach(null);
						fBatch.setStatus(FBatchStatus.PENDING);
						
						FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch,
								new AsyncCallback<FBatch>() {

									@Override
									public void onSuccess(FBatch savedFbatch) {
										AonMessagePanel.showSuccess(messagePanel, new HTMLPanel("El fichero SEPA de la remesa '<b>"
												+ fBatch.getDescription() + "</b>' ha sido eliminado correctamente."));
									}

									@Override
									public void onFailure(Throwable error) {
										AonMessagePanel.showError(messagePanel,
												new HTMLPanel("Error al guardar la remesa '<b>"
														+ fBatch.getDescription() + "</b>': " + error.getMessage()));
									}
								});
					}

					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel,
								new HTMLPanel("Error al eliminar el fichero SEPA de la remesa '<b>"
										+ fBatch.getDescription() + "</b>': " + error.getMessage()));
					}
				});
	}
	
	private void createSepeFile(FinanceModuleOptions opt, FBatch fBatch) {
		AonMessagePanel.showLoading(messagePanel,
				"Generando fichero SEPA para la remesa '" + fBatch.getDescription() + "'...");
		FINANCE_SERVICE.createSepaFile(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch.getId(),
				new AsyncCallback<Integer>() {

					@Override
					public void onSuccess(Integer rattachId) {
						fBatch.setRattach(rattachId);
						fBatch.setStatus(FBatchStatus.GENERATED);
						
						FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch,
								new AsyncCallback<FBatch>() {

									@Override
									public void onSuccess(FBatch result) {
										AonMessagePanel.showSuccess(messagePanel, new HTMLPanel("El fichero SEPA de la remesa '<b>"
												+ fBatch.getDescription() + "</b>' ha sido generado correctamente."));
									}

									@Override
									public void onFailure(Throwable error) {
										AonMessagePanel.showError(messagePanel,
												new HTMLPanel("Error al guardar la remesa '<b>"
														+ fBatch.getDescription() + "</b>': " + error.getMessage()));
									}
								});
					}

					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel,
								new HTMLPanel("Error al generar el fichero SEPA de la remesa '<b>"
										+ fBatch.getDescription() + "</b>': " + error.getMessage()));
					}
				});
	}
	
	private void clearSelection() {
		selectedItems.clear();
	}
	
	private void manageSelection(FBatch fBatch) {
		if (fBatch.isSelected()) {
			selectedItems.add(fBatch.getId());
		} else {
			selectedItems.remove(fBatch.getId());
		}
		refreshIcons();
	}
	
	private void refreshIcons() {
		deleteButton.setEnabled(selectedItems.size() > 0);
		selectedCount.setText(selectedItems.size() > 0 ?  AonNumberUtils.toString(selectedItems.size()) : ""); 
	}
	
	private void setVisible(Widget widget, boolean isVisible) {
		if(isVisible) widget.getElement().getStyle().clearDisplay();
		else widget.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	// -------------------------------------------------------------------
	// ---------------------------  SEARCH  ------------------------------
	// -------------------------------------------------------------------

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

	protected void search(final FinanceModuleOptions opt, FBatchParams params) {
		enableMoreData();
		fBatches.clear();
		clearSelection();
		container.clear();
		tab = getTable();
		container.add(tab);
		offset.setValue(0);
		search(opt, params, offset.getValue());
	}

	private void search(final FinanceModuleOptions opt, FBatchParams params, final int ofs) {
		if (!isMoreData()) return; 
		FINANCE_SERVICE.getFBatches(opt.getDomainName(),opt.getDomain(),opt.getUser(), params, ofs, limit
				, new AsyncCallback<LinkedList<FBatch>>() {
					
					@Override
					public void onSuccess(LinkedList<FBatch> result) {
						checkAll.setEnabled(false);
						uncheckAll.setEnabled(false);
						if (result != null && !result.isEmpty()) {
							result.forEach( fBatch -> paintRow(opt,fBatch));
							offset.setValue(ofs + result.size());
							enableMoreData();
						} else {
							Label label = new Label(AON.MSG.noData());
							label.setStyleName(AON.CSS.aonBlockMessage());
							label.addStyleName(AON.CSS.aonBlockInfoMessage());
							label.addStyleName(AON.CSS.aonMarginTop());
							container.add(label);
							disableMoreData();
						}
						checkAll.setEnabled(true);
						uncheckAll.setEnabled(true);
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
	
	// -------------------------------------------------------------------
	// --------------------------  TOOLBAR  ------------------------------
	// -------------------------------------------------------------------

	private Widget getToolbarPanel(final FinanceModuleOptions opt) {
		toolbar = new AonToolbar(getToolbarTitle());

		resetSearchButton = new AonToolbarButton(AON.MSG.clean() + " filtros", AON.CSS.aonIconClear());
		resetSearchButton.addClickHandler(e -> {
			toolbar.hideMessages();
			enableMoreData();
			container.clear();
			tab = getTable();
			container.add(tab);
			offset.setValue(0);
			searchPanel.initialize(opt);
			search(opt, searchPanel.getParams( opt ), offset.getValue());
		});
		toolbar.add(resetSearchButton);

		checkAll = new AonToolbarButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll.setEnabled(false);
		checkAll .addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				checkAll( opt, true );
			}
		});
		toolbar.add(checkAll );
		
		uncheckAll = new AonToolbarButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.setEnabled(false);
		uncheckAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				checkAll( opt, false );
			}
		});
		toolbar.add(uncheckAll);

		deleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		deleteButton.setEnabled(false);
		deleteButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				long notPendingFBatches = fBatches.values().stream().filter(fBatcheRow -> fBatcheRow.getFBatch().isSelected() && !fBatcheRow.getFBatch().isPending() && !fBatcheRow.getFBatch().isGenerated()).count();
				
				if(notPendingFBatches > 0) {
					AonDialog warningDialog = new AonDialog("Eliminaci\u00f3n Remesas",
							new HTML("No se pueden eliminar remesas cuyo estado sea distinto de <b>Pendiente</b> o <b>Fichero Generado</b>. Por favor revise las remesas seleccionadas."));

					warningDialog.warning();
				} else {
					AonDialog deleteDialog = new AonDialog("Eliminaci\u00f3n Remesas",
							new HTML("Se va a proceder a eliminar <b>" + selectedItems.size() + "</b> remesa(s).<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));

					deleteDialog.confirm(new AonAcceptDialogCallback() {

						@Override
						public void onCancel() {
							// Nothing to do here
						}

						@Override
						public void onAccept() {
							LinkedList<Integer> fbatchIds = new LinkedList<>();
							fbatchIds.addAll(selectedItems);
							FINANCE_SERVICE.deleteFBatches(opt.getDomainName(), opt.getDomain(), opt.getUser(), fbatchIds, new AsyncCallback<Void>() {
								
								@Override
								public void onSuccess(Void arg0) {
									clearSelection();
									toolbar.hideMessages();
									enableMoreData();
									container.clear();
									tab = getTable();
									container.add(tab);
									offset.setValue(0);
									search(opt, searchPanel.getParams( opt ), offset.getValue());
								}
								
								@Override
								public void onFailure(Throwable caught) {
									showError(caught.getMessage());
								}
							});
						}
					});
				}
			}
		});
		toolbar.add(deleteButton);
		
		newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final AonCustomDialog dialog = new AonCustomDialog();
				dialog.setCaption("Remesa Vencimientos");
				final AonFBatchPaymentPanel fbatchPanel = new AonFBatchPaymentPanel( opt.getDomainName(), opt.getDomain(), opt.getUser(), fbatchType.name(), new AonFBatchPaymentPayrollPanelCallback() {
					
					@Override
					public void onCancel() {
						dialog.hide();
					}
					
					@Override
					public void onAccept(FBatch fBatch) {
						dialog.hide();
						toolbar.hideMessages();
						enableMoreData();
						container.clear();
						tab = getTable();
						container.add(tab);
						offset.setValue(0);
						search(opt, searchPanel.getParams( opt ), offset.getValue());
						deckLayoutPanel.showWidget(1);
						fBatchPaymentPayrollEntryModule.onModuleLoad(opt, fBatch);
					}
				}) {

					@Override
					protected void onResize() {
						dialog.showLoaded();
					}};
				
				dialog.add( fbatchPanel );
				dialog.showLoadedCB(new AonCustomDialogCallback() {
					@Override
					public void onEnd() {
						fbatchPanel.setDescriptionFocus();
					}
				});
			}
		});
		toolbar.add(newButton);
	
		return toolbar;
	}
	
	private String getToolbarTitle() {
		if(FBATCH_TYPE.PAYROLL_PAYMENT == this.fbatchType)
			return "Remesa Transferencias N\u00f3minas";
		else if(FBATCH_TYPE.PAYMENT == this.fbatchType)
			return "Remesa Pagos";
		else
			return "Tipo Remesa Desconocido";
	}
	
	protected void checkAll(final FinanceModuleOptions opt, boolean check) {
		for (FBatchRow fbatchRow : fBatches.values()) {
			fbatchRow.getFBatch().setSelected(check);
			manageSelection(fbatchRow.getFBatch());
			Widget w = tab.getWidget(fbatchRow.getRow(), 1);
			if (check) {
				w.addStyleName(AON.CSS.aonIconChecked());
				w.removeStyleName(AON.CSS.aonIconCheck());
			} else {
				w.addStyleName(AON.CSS.aonIconCheck());
				w.removeStyleName(AON.CSS.aonIconChecked());
			}
		}
	}
	
}		
