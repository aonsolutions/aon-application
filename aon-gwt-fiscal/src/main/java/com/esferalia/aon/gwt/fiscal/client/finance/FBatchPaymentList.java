package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog.AonCustomDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFBatchPaymentPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFBatchPaymentPanel.AonFBatchPaymentPayrollPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.esferalia.aon.occam.api.model.type.FBatchStatus.FBatchStatusVisitor;
import com.esferalia.aon.occam.api.model.type.FBatchType;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class FBatchPaymentList extends AonCustomDockLayout {
	
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
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	
	// ProductList UI

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private AonToolbarButton resetSearchButton;
	private AonToolbarButton checkAll; 
	private AonToolbarButton uncheckAll;
	private AonToolbarButton deleteButton;
	private AonToolbarButton newButton;
	private AonToolbarButton showHideFilter;
	
	private FBatchPaymentModuleSearchPanel searchPanel;
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private FinanceModuleOptions options;
	
	// Table UI
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private int lastScrollPos = 0;

	private LinkedHashMap<Integer, FBatchRow> fBatches = new LinkedHashMap<Integer,FBatchRow>();
	private LinkedHashSet<Integer> selectedItems = new LinkedHashSet<Integer>();
	
	private InlineLabel selectedCount;
	
	private boolean fetchingData = false;
	
	private FBatchType fbatchType;
	
	private static enum COLS {
		  CHK(""									,"2rem"  			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DES(AON.MSG.description()					,"-moz-available"  	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, FEC(AON.MSG.date()						,"5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BAN("Banco"								,"9rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, CUE(AON.MSG.bankAccount()					,"12.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA(AON.MSG.status()						,"7.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;") // Pack o servicio
		, REG("Regs"								,"3rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;") // Pack o servicio
		, AMO("Importe"								,"5.5rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;") // Pack o servicio
		, TYP("Tipo"								,"10rem" 			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;") // Pack o servicio
		, ACT(AonStringUtils.EMPTY					,"5rem" 			,"")
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
	
	// Constructor
	public FBatchPaymentList(FinanceModuleOptions options, FBatchType fbatchType) {
		super("Remesas");
		
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);
		
		this.options = options;
		this.fbatchType = fbatchType;
		
		setToolbarTitle(getToolbarTitle());
		
		addButtonsToolbar();
		
		searchPanel = new FBatchPaymentModuleSearchPanel(options, this.fbatchType);
		searchPanel.hideDescription();
		searchPanel.addValueChangeHandler( new ValueChangeHandler<FBatchParams>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<FBatchParams> event) {
				AonMessagePanel.hideMessage(messagePanel);
				onSearch();
			}
		});

		hideToolbarFilterMessages();
		hideFilterButton();
		setSearchPlaceholder("Busque por descripci\u00f3n ...");
		addOnSearchHandler(e -> { if(!fetchingData) onSearch(); });
		
		showHideFilter = new AonToolbarButton("", AON.CSS.aonIconFilterList());
		showHideFilter.addClickHandler(e -> searchPanel.setVisible(!searchPanel.isVisible()));
		insertWidgetAfterSearchButton(showHideFilter, true);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
		container.add(searchPanel);
	
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		
		container.add(tableContainer);
		
		add(container);
		onSearch();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	getSearchTextBox().setFocus(true);
	        }
	    });		
	}

	@Override
	protected void onClearFilter() {}
	
	private void resetSearchOffset() {
		offset.setValue(0);
	}
	
	private void addButtonsToolbar() {
		resetSearchButton = new AonToolbarButton(AON.MSG.clean() + " filtros", AON.CSS.aonIconClear());
		resetSearchButton.addClickHandler(e -> {
			AonMessagePanel.hideMessage(messagePanel);
			getSearchTextBox().setValue(null, false);
			enableMoreData();
			resetSearchOffset();
			searchPanel.initialize(options);
			clearSelection();
			onSearch();
		});
		addToolbarButton(resetSearchButton);
		
		checkAll = new AonToolbarButton( AON.MSG.selectAll(), AON.CSS.aonIconChecked() );
		checkAll.addClickHandler(e -> checkAll( options, true ));
		addToolbarButton(checkAll);
		
		uncheckAll = new AonToolbarButton( AON.MSG.selectNone(), AON.CSS.aonIconCheck() );
		uncheckAll.addClickHandler(e -> checkAll( options, false ));
		addToolbarButton(uncheckAll);
		
		deleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		deleteButton.setEnabled(false);
		deleteButton.addClickHandler(e -> {
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
						FINANCE_SERVICE.deleteFBatches(options.getDomainName(), options.getDomain(), options.getUser(), fbatchIds, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void arg0) {
								AonMessagePanel.hideMessage(messagePanel);
								getSearchTextBox().setValue(null, false);
								enableMoreData();
								resetSearchOffset();
								searchPanel.initialize(options);
								clearSelection();
								onSearch();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, caught.getMessage());
							}
						});
					}
				});
			}
		});
		addToolbarButton(deleteButton);
		
		newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler(e -> {
			final AonCustomDialog dialog = new AonCustomDialog();
			dialog.setCaption("Remesa Vencimientos");
			final AonFBatchPaymentPanel fbatchPanel = new AonFBatchPaymentPanel( options.getDomainName(), options.getDomain(), options.getUser(), fbatchType, new AonFBatchPaymentPayrollPanelCallback() {
				
				@Override
				public void onCancel() {
					dialog.hide();
				}
				
				@Override
				public void onAccept(FBatch fBatch) {
					dialog.hide();
					AonMessagePanel.hideMessage(messagePanel);
					enableMoreData();
					resetSearchOffset();
					onFBatchSelect(fBatch, true);
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
		});
		addToolbarButton(newButton);
	}
	
	protected void checkAll(final FinanceModuleOptions opt, boolean check) {
		for (FBatchRow fbatchRow : fBatches.values()) {
			fbatchRow.getFBatch().setSelected(check);
			manageSelection(fbatchRow.getFBatch());
			Widget w = tab.getWidget(fbatchRow.getRow(), 0);
			if (w instanceof AonTableButton) {
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
	
	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	public void onSearch() {
		resetSearchOffset();
		onSearchData();
	}
	
	private void onSearchData() {
		fetchingData = true;
		enableMoreData();
		searchData();
	}
	
	private void searchData() {
		tableContainer.clear();
		tab = new AonCustomTable();
		tableScrollPanel = new ScrollPanel(tab);
		tableScrollPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		tableScrollPanel.addScrollHandler(e -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = tableScrollPanel.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = tableScrollPanel.getWidget().getOffsetHeight() - tableScrollPanel.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					searchDataList();
				}
			}
		});
		
		paintHeader();
		tableContainer.add(tableScrollPanel);
		searchDataList();
	}
	
	private void paintHeader() {
		selectedCount = new InlineLabel("");
		selectedCount.addStyleName(AON.CSS.aonTextCenter());
		
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			tab.addHeader(col.equals(COLS.CHK) ? selectedCount : new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
	}
	
	private void searchDataList() {
		if (!isMoreData()) return;
		
		getList(fBatchs -> {
			boolean something = false;
			
			for(FBatch fBatch : fBatchs) {
				something = true;
				paintRow(fBatch);
			}
			
			if (fBatchs.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + fBatchs.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
//				FlowPanel line = new FlowPanel();
//				InlineLabel label = new InlineLabel(AON.MSG.noData());
//				line.add(label);
//				tableContainer.clear();
//				tableContainer.add(line);
				disableMoreData();
			}
			enableSearch();
			fetchingData = false;
		});
	}
	
	private void paintRow(FBatch fBatch) {
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onFBatchSelect(fBatch, false), ClickEvent.getType());
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), selectedItems.contains(fBatch.getId()) ? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck());
		checkButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				
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
		tab.addRow(row, checkButton, COLS.CHK.getColWidth());
		
		Label description = new Label(fBatch.getDescription());
		description.setTitle(fBatch.getDescription());
		tab.addInlineStyle(description, COLS.DES.getCellStyleClass());
		tab.addRow(row, description, COLS.DES.getColWidth());
		
		Label issueDate = new Label(AON.DATE_FORMAT.format(fBatch.getIssueDate()));
		tab.addRow(row, issueDate, COLS.FEC.getColWidth());
		
		Label bank = new Label(null == fBatch.getRbank() ? "" : fBatch.getRbank().getAlias());
		tab.addRow(row, bank, COLS.BAN.getColWidth());
		
		Label bankAccount = new Label(null == fBatch.getRbank() ? "" : fBatch.getRbank().getBankAccount().toString());
		tab.addRow(row, bankAccount, COLS.CUE.getColWidth());
		
		Label status = new Label(null == fBatch.getStatus() ? "" : fBatch.getStatus().getDescription());
		fBatch.getStatus().visit(new FBatchStatusVisitor() {
			
			@Override public void visitUnknown() 	{ /*Nothing to do*/ }
			@Override public void visitGenerated() 	{ /*Nothing to do*/ }
			
			@Override
			public void visitPending() {
				status.setStyleName(AON.CSS.aonColorRed());
			}

			@Override
			public void visitRecorded() {
				status.setStyleName(AON.CSS.aonColorGreen());
				status.addStyleName(AON.CSS.aonBold());
			}
		});
		tab.addRow(row, status, COLS.STA.getColWidth());
		
		Label registries = new Label(null == fBatch.getBatchDetails() || fBatch.getBatchDetails().isEmpty() ? "0" :  String.valueOf(fBatch.getBatchDetails().size()));
		registries.addStyleName(AON.CSS.aonTextRight());
		tab.addRow(row, registries, COLS.REG.getColWidth());
		
		Label amount = new Label(null == fBatch.getBatchDetails() || fBatch.getBatchDetails().isEmpty() ? "0.00 \u20ac" : AON.FMT.format(fBatch.getBatchDetails().stream().map(fBatchDetail -> fBatchDetail.getAmount()).reduce(0.00, (a, b) -> a + b)) + " \u20ac");
		amount.addStyleName(AON.CSS.aonTextRight());
		tab.addRow(row, amount, COLS.AMO.getColWidth());
		
		Label fileType = new Label(fbatchType.getFileTypeDescription(fBatch.getType()));
		fileType.addStyleName(AON.CSS.aonTextLeft());
		tab.addRow(row, fileType, COLS.TYP.getColWidth());
		
		FlowPanel actionsPanel = new FlowPanel();
		
		// Actions Buttons
		AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete()); 
		AonTableButton downloadFile = new AonTableButton(AON.MSG.download() + " fichero SEPA", AON.CSS.aonIconDownload()); 
		AonTableButton deleteFile = new AonTableButton(AON.MSG.deleteAction() + " fichero SEPA", AON.CSS.aonIconDeleteFile());
		AonTableButton sepaButton = new AonTableButton("Crear fichero SEPA", AON.CSS.aonIconXml()); 
		
		// Delete FBatch
		
		setVisible(deleteButton, fBatch.isPending() || fBatch.isGenerated());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteFBatch(options, fBatch);
		});
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
			e.stopPropagation();
			
			diskForm.setAction(GWT.getHostPageBaseURL() + "/ms/download_attachment/");

			rattachHidden.setValue(fBatch.getRattach().toString());
			attachTypeHidden.setValue("registry");
			domainIdHidden.setValue(options.getDomain() + "");
			domainNameHidden.setValue(options.getDomainName());
			userHidden.setValue(options.getUser());

			diskForm.submit();
		});
		setVisible(downloadFile, fBatch.getRattach() != null && FBatchType.isSepaFile(fBatch.getType()));
		actionsPanel.add(downloadFile);
		
		// Download File
		deleteFile.addClickHandler(e -> {
			e.stopPropagation();
			
			setVisible(downloadFile, false);
			setVisible(deleteFile, false);
			setVisible(diskForm, false);
			setVisible(sepaButton, true);
			
			deleteFile(options, fBatch);
			status.setText(FBatchStatus.PENDING.getDescription());
			status.setStyleName(AON.CSS.aonColorRed());
		});
		setVisible(deleteFile, fBatch.getRattach() != null 
			&& !fBatch.getStatus().equals(FBatchStatus.RECORDED) 
			&& FBatchType.isSepaFile(fBatch.getType()));
		actionsPanel.add(deleteFile);
		
		sepaButton.addClickHandler(e -> {
			e.stopPropagation();
			
			setVisible(downloadFile, true);
			setVisible(deleteFile, true);
			setVisible(diskForm, true);
			setVisible(sepaButton, false);
			
			createSepeFile(options, fBatch);
			status.setText(FBatchStatus.GENERATED.getDescription());
			status.removeStyleName(AON.CSS.aonColorRed());
		});
		setVisible(sepaButton, fBatch.getRattach() == null && !fBatch.getBatchDetails().isEmpty() && FBatchType.isSepaFile(fBatch.getType()) && fBatch.getRbank() != null);
		actionsPanel.add(sepaButton);
		
		setVisible(diskForm, fBatch.getRattach() != null);
		actionsPanel.add(diskForm);
		
		tab.addRow(row, actionsPanel, COLS.ACT.getColWidth());
		
		fBatches.put(fBatch.getId(), new FBatchRow(tab.getRowsCount() - 1, fBatch));
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
						AonMessagePanel.hideMessage(messagePanel);
						getSearchTextBox().setValue(null, false);
						enableMoreData();
						resetSearchOffset();
						searchPanel.initialize(options);
						clearSelection();
						onSearch();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, caught.getMessage());
					}
				});
			}
		});
	}
	
	private void deleteFile(FinanceModuleOptions opt, FBatch fBatch) {
		AonMessagePanel.showLoading(messagePanel, "Eliminando fichero SEPA para la remesa '" + fBatch.getDescription() + "'...");
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
	
	private void setVisible(Widget widget, boolean isVisible) {
		if(isVisible) widget.getElement().getStyle().clearDisplay();
		else widget.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private String getToolbarTitle() {
		if(FBatchType.PAYROLL_PAYMENT == fbatchType)
			return "Remesa Transferencias N\u00f3minas";
		else if(FBatchType.PAYMENT == fbatchType)
			return "Remesa Pagos";
		else if(FBatchType.CHARGE == fbatchType)
			return "Remesa Cobros";
		else
			return "Tipo Remesa Desconocido";
	}
	
	private void clearSelection() {
		selectedItems.clear();
	}
	
	private void getList(Consumer<List<FBatch>> success) {
		if (!isMoreData()) return; 
		
		// Override description
		FBatchParams params = searchPanel.getParams(options);
		params.setDescription(getSearchTextBox().getValue());
		
		FINANCE_SERVICE.getFBatches(options.getDomainName(),options.getDomain(),options.getUser(), params, offset.intValue(), limit , new AsyncCallback<LinkedList<FBatch>>() {
					
			@Override
			public void onSuccess(LinkedList<FBatch> result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, caught.getMessage());
			}
			
		});
	}
	
	protected abstract void onFBatchSelect(FBatch fBatch, boolean newFBatch);
	
}
