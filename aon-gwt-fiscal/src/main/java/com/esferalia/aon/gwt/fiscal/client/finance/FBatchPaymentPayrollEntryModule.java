package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.FBatchDetail;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class FBatchPaymentPayrollEntryModule extends SimpleLayoutPanel {
	
	private static FinanceServiceAsync FINANCE_SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;
	
	// ------- FBatchRow
	
	private static class FinanceRow {
		
		private int row;
		private Finance finance;
		
		private FinanceRow( int row, Finance finance) {
			this.row = row;
			this.finance = finance;
		}
		
		private int getRow() {
			return row;
		}
		
		private Finance getFinance() {
			return finance;
		}
		
	}
	
	private DockLayoutPanel dockLayoutPanel;
	
	private HTMLPanel container;
	
	private FlexTable fbatchPanel;
	private TextBox description = new TextBox();
	private AonDateBox issueDate = new AonDateBox();
	private ListBox bank = new ListBox();
	private ListBox type = new ListBox();
	private Label totalAmount = new Label();
	
	private SplitLayoutPanel financesPanel;
	
	private ScrollPanel aviableFinancePanel;
	private FlowPanel aviableFinanceContainer;
	private FlexTable aviableFinanceTable;
	private int aviableFinanceAutoWidth; 
	
	private ScrollPanel selectedFinancePanel;
	private FlowPanel selectedFinanceContainer;
	private FlexTable selectedFinanceTable;
	private int selectedFinanceAutoWidth; 
	
	private LinkedHashMap<Integer, FinanceRow> aviableFinances;
	private LinkedHashMap<Integer, FinanceRow> selectedFinances;
	
	private AonToolbar toolbar;
	private AonToolbarButton backButton;
	private AonToolbarButton saveButton;
	private AonToolbarButton sepaButton;
	private AonToolbarButton downloadButton;
	private AonToolbarButton deleteFileButton;
	private HTMLPanel messagePanel;
	
	private InlineLabel selectedCount;
	
	final private int limit = 100;
	final private MutableInt offset = new MutableInt(0);
	final private MutableInt moreData = new MutableInt(0);
	final private MutableInt searchEnabled = new MutableInt( 0 ); 
	private int lastScrollPos = 0;
	
	private FBatch fBatch;
	private FinanceModuleOptions opt;
	
	private boolean hasSaved;
	
	public void onModuleLoad( final FinanceModuleOptions opt, FBatch fBatch ) {
		AON.ensureInjected();
		
		this.clear();
		
		this.opt = opt;
		this.fBatch = fBatch;
		this.hasSaved = false;

		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		aviableFinances = new LinkedHashMap<Integer, FinanceRow>();
		selectedFinances = new LinkedHashMap<Integer, FinanceRow>();
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		this.add(dockLayoutPanel);
		
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
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		messagePanel = new HTMLPanel("");
		container.add(messagePanel);
		
		fbatchPanel = this.createFbatchPanel(opt);
		container.add(fbatchPanel);
		
		financesPanel = new SplitLayoutPanel();
		financesPanel.setHeight((Window.getClientHeight() - 265) + "px");
		
		aviableFinancePanel = new ScrollPanel();
		aviableFinancePanel.getElement().getStyle().setProperty("margin", "1rem");
		aviableFinanceContainer = new FlowPanel();
		aviableFinancePanel.setWidget(aviableFinanceContainer);
		financesPanel.addWest(aviableFinancePanel, Window.getClientWidth() / 2);
		
		selectedFinancePanel = new ScrollPanel();
		selectedFinancePanel.getElement().getStyle().setProperty("margin", "1rem");
		selectedFinanceContainer = new FlowPanel();
		selectedFinancePanel.setWidget(selectedFinanceContainer);
		financesPanel.add(selectedFinancePanel);
		
		container.add(financesPanel);
		
		dockLayoutPanel.add(container);
		
		aviableFinancePanel.addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = aviableFinancePanel.getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = aviableFinancePanel.getWidget().getOffsetHeight() - aviableFinancePanel.getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						search(opt, offset.getValue());
					}
				}
			}
		});
		
		// Auto search first time
		enableMoreData();
		aviableFinanceContainer.clear();
		aviableFinanceTable = getAviableFinanceTable();
		aviableFinanceContainer.add(aviableFinanceTable);
		offset.setValue(0);
		if(!fBatch.isAccounted())
			search(opt, offset.getValue());
		else {
			Label label = new Label("No se pueden a\u00f1adir vemcimientos a una remesa contabilizada");
			label.setStyleName(AON.CSS.aonBlockMessage());
			label.addStyleName(AON.CSS.aonBlockInfoMessage());
			label.addStyleName(AON.CSS.aonMarginTop());
			aviableFinanceContainer.add(label);
		}
		
		selectedFinanceContainer.clear();
		selectedFinanceTable = getSelectedFinanceTable();
		selectedFinanceContainer.add(selectedFinanceTable);
		if(this.fBatch.getBatchDetails() == null || this.fBatch.getBatchDetails().isEmpty()) {
			Label label = new Label("No existen vencimientos en la remesa");
			label.setStyleName(AON.CSS.aonBlockMessage());
			label.addStyleName(AON.CSS.aonBlockInfoMessage());
			label.addStyleName(AON.CSS.aonMarginTop());
			selectedFinanceContainer.add(label);
		} else {
			paintSelectedFinanceTable(opt);
		}
	}

	private FlexTable createFbatchPanel(FinanceModuleOptions opt) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.getElement().getStyle().setProperty("margin-left", "1rem");
		
		table.setWidget(0, 0, new InlineLabel(AON.MSG.description()));
		description = new TextBox();
		description.setValue(fBatch.getDescription());
		description.setMaxLength(32);
		description.addValueChangeHandler(e -> {
			fBatch.setDescription(e.getValue());
			saveButton.setEnabled(true);
		});
		table.setWidget(0,1,description);
		
		table.setWidget(0,2,new InlineLabel(AON.MSG.date()));
		issueDate.setValue(this.fBatch.getIssueDate());
		issueDate.addValueChangeHandler(e -> {
			fBatch.setIssueDate(e.getValue());
			saveButton.setEnabled(true);
		});
		table.setWidget(0,3,issueDate);
		
		table.setWidget(1,0,new InlineLabel("Tipo Fichero"));
		type.clear();
		type.addItem("SEPA 34-14 N\u00f3mina (XML)", "10");
		type.setSelectedIndex(0);
		table.setWidget(1,1,type);
		
		table.setWidget(1,2,new InlineLabel(AON.MSG.bankAccount()));
		bank.clear();
		bank.addItem("-");
		getEnterpriseBanks(opt, companyBanks -> {
			companyBanks.stream().filter(companyBank -> companyBank.isActive()).forEach(companyBank -> {
				bank.addItem("(" + companyBank.getAlias() + ") " + companyBank.getBankAccount().toString(), companyBank.getId().toString());
			});
			setSelectedValueLB(bank, null != fBatch.getRbank() ? fBatch.getRbank().getId().toString() : null);
		});
		bank.addChangeHandler(e -> {
			fBatch.setRbank(bank.getSelectedIndex() == 0 ? null : new RegistryBank().setId(Integer.parseInt(bank.getSelectedValue())));
			saveButton.setEnabled(true);
		});
		table.setWidget(1,3,bank);
		
		table.setWidget(2,0,new InlineLabel(AON.MSG.amount() + " " + AON.MSG.total()));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(2, 0).setPropertyString("min-width", "135px");
		String amountSum = null == fBatch.getBatchDetails() || fBatch.getBatchDetails().isEmpty() ? "0.00 \u20ac" : AON.FMT.format(fBatch.getBatchDetails().stream().map(fBatchDetail -> fBatchDetail.getAmount()).reduce(0.00, (a, b) -> a + b)) + " \u20ac";
		totalAmount.setText(amountSum);
		table.setWidget(2,1,totalAmount);
		
		
		// Apply Styles
		for(int i=0; i < table.getRowCount() - 1; i++) {
			table.getCellFormatter().setStyleName(i, 0, AON.CSS.aonTableLabel());
			table.getCellFormatter().getElement(i, 0).setPropertyString("min-width", "135px");
			table.getWidget(i, 1).setStyleName(AON.CSS.aonInputText());
			
			table.getCellFormatter().setStyleName(i, 2, AON.CSS.aonTableLabel());
			table.getCellFormatter().getElement(i, 2).setPropertyString("min-width", "135px");
			table.getWidget(i, 3).setStyleName(AON.CSS.aonInputText());
		}
		
		return table;
	}

	private static enum COLS {
		  SEL(AonStringUtils.EMPTY		, 20 ,AON.CSS.aonTextCenter())
		, FEC("F. Venc."				, 80 ,AON.CSS.aonTextCenter())
		, DOC("Documento"				, 100 ,AON.CSS.aonTextLeft())
		, TIT("Titular"					, 0  ,AON.CSS.aonTextLeft())
		, AMO("Importe"					, 100 ,AON.CSS.aonTextRight())
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

	protected FlexTable getAviableFinanceTable() {
		aviableFinanceTable = new FlexTable();
		aviableFinanceTable.setStyleName(AON.CSS.aonGrid());
		
		aviableFinanceAutoWidth = aviableFinanceContainer.getOffsetWidth() - 36;
		for ( COLS col : COLS.values()) {
			if (col != COLS.TIT ) {
				aviableFinanceAutoWidth -= (col.getColWidth() + 2); 
			}
		}
		
		selectedCount = new InlineLabel();
		for ( COLS col : COLS.values()) {
			if (col == COLS.TIT ) {
				aviableFinanceTable.getColumnFormatter().setWidth(col.ordinal(), aviableFinanceAutoWidth + "px");
			} else {
				aviableFinanceTable.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth() + "px");
			}
//			aviableFinanceTable.setWidget(0, col.ordinal(), col == COLS.CHK ? selectedCount : new Label( col.getHeaderLabel() ));
			aviableFinanceTable.setWidget(0, col.ordinal(), new Label( col.getHeaderLabel() ));
			aviableFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonGridHeader());
			if ( col.getCellStyleClass() != null) {
				aviableFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
				aviableFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonNowrap());
			}
		}
		return aviableFinanceTable;
	}
	
	protected FlexTable getSelectedFinanceTable() {
		selectedFinanceTable = new FlexTable();
		selectedFinanceTable.setStyleName(AON.CSS.aonGrid());
		
		selectedFinanceAutoWidth = selectedFinanceContainer.getOffsetWidth() - 36;
		for ( COLS col : COLS.values()) {
			if (col != COLS.TIT ) {
				selectedFinanceAutoWidth -= (col.getColWidth() + 2); 
			}
		}
		
		selectedCount = new InlineLabel();
		for ( COLS col : COLS.values()) {
			if (col == COLS.TIT ) {
				selectedFinanceTable.getColumnFormatter().setWidth(col.ordinal(), selectedFinanceAutoWidth + "px");
			} else {
				selectedFinanceTable.getColumnFormatter().setWidth(col.ordinal(), col.getColWidth() + "px");
			}
//			selectedFinanceTable.setWidget(0, col.ordinal(), col == COLS.CHK ? selectedCount : new Label( col.getHeaderLabel() ));
			selectedFinanceTable.setWidget(0, col.ordinal(), new Label( col.getHeaderLabel() ));
			selectedFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonGridHeader());
			if ( col.getCellStyleClass() != null) {
				selectedFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(),col.getCellStyleClass());
				selectedFinanceTable.getFlexCellFormatter().addStyleName(0, col.ordinal(),AON.CSS.aonNowrap());
			}
		}
		return selectedFinanceTable;
	}
	
	public abstract void back(boolean refresh);

	private Widget getToolbarPanel(final FinanceModuleOptions opt) {
		toolbar = new AonToolbar("Remesa Transferencias N\u00f3minas");
		
		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
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
		toolbar.add(diskForm);
		
		backButton = new AonToolbarButton( AON.MSG.backAction(), AON.CSS.aonIconBack() );
		backButton.addClickHandler(e -> back(hasSaved));
		toolbar.add(backButton);

		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> save());
		saveButton.setEnabled(false);
		toolbar.add(saveButton);
		
		sepaButton = new AonToolbarButton( "Crear fichero SEPA", AON.CSS.aonIconXml() );
		sepaButton.addClickHandler(e -> createSepaFile());
		sepaButton.setVisible(!this.fBatch.getBatchDetails().isEmpty());
		toolbar.add(sepaButton);
		
		downloadButton = new AonToolbarButton( AON.MSG.download() + " fichero SEPA", AON.CSS.aonIconDownload() );
		downloadButton.addClickHandler(e -> {
			diskForm.setAction(GWT.getHostPageBaseURL() + "/ms/download_attachment/");
			
			rattachHidden.setValue(fBatch.getRattach().toString());
			attachTypeHidden.setValue("registry");
			domainIdHidden.setValue(opt.getDomain() + "");
			domainNameHidden.setValue(opt.getDomainName());
			userHidden.setValue(opt.getUser());
			
			diskForm.submit();
		});
		downloadButton.setVisible(this.fBatch.getRattach() != null);
		toolbar.add(downloadButton);
		
		deleteFileButton = new AonToolbarButton( AON.MSG.deleteAction() + " fichero SEPA", AON.CSS.aonIconDeleteFile() );
		deleteFileButton.addClickHandler(e -> {
			AonMessagePanel.showLoading(messagePanel, "Eliminando fichero SEPA para la remesa '" + fBatch.getDescription() + "'...");
			FINANCE_SERVICE.deleteSepaFile(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch.getRattach(), new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void seccess) {
					AonMessagePanel.showSuccess(messagePanel, new HTMLPanel("El fichero SEPA de la remesa '<b>" + fBatch.getDescription() + "</b>' ha sido eliminado correctamente."));
					fBatch.setRattach(null);
					fBatch.setStatus(FBatchStatus.PENDING);
					deleteFileButton.setVisible(false);
					downloadButton.setVisible(false);
					
					FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch, new AsyncCallback<FBatch>() {
						
						@Override
						public void onSuccess(FBatch savedFbatch) {
							hasSaved = true;
							sepaButton.setVisible(!savedFbatch.getBatchDetails().isEmpty());
						}
						
						@Override
						public void onFailure(Throwable error) {
							AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al guardar la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
						}
					});
				}
				
				@Override
				public void onFailure(Throwable error) {
					AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al eliminar el fichero SEPA de la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
				}
			});
		});
		deleteFileButton.setVisible(this.fBatch.getRattach() != null);
		toolbar.add(deleteFileButton);
	
		return toolbar;
	}

	private void save() {
		AonMessagePanel.showLoading(messagePanel, "Guardando la remesa '" + fBatch.getDescription() + "'...");
		FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch, new AsyncCallback<FBatch>() {
			
			@Override
			public void onSuccess(FBatch savedFbatch) {
				hasSaved = true;
				saveButton.setEnabled(false);
				sepaButton.setVisible(!savedFbatch.getBatchDetails().isEmpty());
				fBatch = savedFbatch;
				selectedFinances = new LinkedHashMap<Integer, FinanceRow>();
				selectedFinanceContainer.clear();
				selectedFinanceTable = getSelectedFinanceTable();
				paintSelectedFinanceTable(opt);
				selectedFinanceContainer.add(selectedFinanceTable);
				AonMessagePanel.showSuccess(messagePanel, new HTMLPanel("La remesa '<b>" + fBatch.getDescription() + "</b>' ha sido guarda correctamente."));
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al guardar la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
			}
		});
	}
	
	private void createSepaFile() {
		AonMessagePanel.showLoading(messagePanel, "Generando fichero SEPA para la remesa '" + fBatch.getDescription() + "'...");
		FINANCE_SERVICE.createSepaFile(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch.getId(), new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer rattachId) {
				AonMessagePanel.showSuccess(messagePanel, new HTMLPanel("El fichero SEPA de la remesa '<b>" + fBatch.getDescription() + "</b>' ha sido generado correctamente."));
				fBatch.setRattach(rattachId);
				fBatch.setStatus(FBatchStatus.GENERATED);
				sepaButton.setVisible(false);
				downloadButton.setVisible(true);
				deleteFileButton.setVisible(true);
				
				FINANCE_SERVICE.createUpdateFBatch(opt.getDomainName(), opt.getDomain(), opt.getUser(), fBatch, new AsyncCallback<FBatch>() {
					
					@Override
					public void onSuccess(FBatch result) {
						hasSaved = true;
					}
					
					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al guardar la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
					}
				});
			}
			
			@Override
			public void onFailure(Throwable error) {
				AonMessagePanel.showError(messagePanel, new HTMLPanel("Error al generar el fichero SEPA de la remesa '<b>" + fBatch.getDescription() + "</b>': " + error.getMessage()));
			}
		});
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

	private void search(final FinanceModuleOptions opt, final int ofs) {
		if (!isMoreData()) return; 
		FinanceParams params = new FinanceParams()
			.setDomain(opt.getDomain())
			.setPayment(true)
			.setPending(true)
			.setIsPayroll(true);
		
		FINANCE_SERVICE.getFinances(opt.getDomainName(),opt.getDomain(),opt.getUser(), params, ofs, limit
				, new AsyncCallback<LinkedList<Finance>>() {
					
					@Override
					public void onSuccess(LinkedList<Finance> result) {
						if (result != null && !result.isEmpty()) {
							result.forEach( finance -> paintRow(opt,finance));
							offset.setValue(ofs + result.size());
							enableMoreData();
						} else {
							Label label = new Label(AON.MSG.noData());
							label.setStyleName(AON.CSS.aonBlockMessage());
							label.addStyleName(AON.CSS.aonBlockInfoMessage());
							label.addStyleName(AON.CSS.aonMarginTop());
							aviableFinanceContainer.add(label);
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

	private void paintRow(final FinanceModuleOptions opt, Finance finance) {
		int row = aviableFinanceTable.getRowCount();
		aviableFinances.put(finance.getId(), new FinanceRow(row, finance));
		paintRow(opt, finance, row);
	}
	
	private void paintRow(final FinanceModuleOptions opt, Finance finance, int row) {
		int col = 0;
		
		AonTableButton addButton = new AonTableButton("A\u00f1adir a la remesa", AON.CSS.aonIconRight()); 
		addButton.addClickHandler(e -> {
			aviableFinances.remove(finance.getId());
			selectedFinances.put(finance.getId(), new FinanceRow(row, finance));

			aviableFinanceContainer.clear();
			selectedFinanceContainer.clear();
			
			aviableFinanceTable = getAviableFinanceTable();
			selectedFinanceTable = getSelectedFinanceTable();
			
			aviableFinanceContainer.add(aviableFinanceTable);
			selectedFinanceContainer.add(selectedFinanceTable);
			
			aviableFinances.values().stream().map(financeRow -> financeRow.getFinance()).forEach(financeIt -> paintRow(opt, financeIt, aviableFinanceTable.getRowCount()));
			selectedFinances.values().stream().map(financeRow -> financeRow.getFinance()).forEach(financeIt -> paintSelectedRow(opt, financeIt, selectedFinanceTable.getRowCount()));
			
			String amountSum = selectedFinances.size() == 0 ? "0.00 \u20ac" : AON.FMT.format(selectedFinances.values().stream().map(financeRow -> financeRow.getFinance().getAmount()).reduce(0.00, (a, b) -> a + b)) + " \u20ac";
			totalAmount.setText(amountSum);
			
			this.fBatch.addBatchDetail(
					new FBatchDetail()
						.setDomain(fBatch.getDomain())
						.setFbatch(fBatch.getId())
						.setFinance(finance)
						.setAmount(finance.getAmount())
						.setStatus((byte)1)
						.setRemoved(false)
			);
			
			if(aviableFinances.isEmpty()) {
				Label label = new Label("No existen vencimientos disponibles para a\u00f1adir a la remesa");
				label.setStyleName(AON.CSS.aonBlockMessage());
				label.addStyleName(AON.CSS.aonBlockInfoMessage());
				label.addStyleName(AON.CSS.aonMarginTop());
				aviableFinanceContainer.add(label);
			}
			
			saveButton.setEnabled(true);
		});
		
		Label issueDate = new Label(AON.DATE_FORMAT.format(finance.getDueDate()));
		
		Label document = new Label(finance.getRegistryDocument());
		
		Label titular = new Label(finance.getRegistryName());
		titular.setTitle(finance.getRegistryName());
		
		Label amount = new Label(AON.FMT.format(finance.getAmount()) + " \u20ac");
		amount.addStyleName(AON.CSS.aonTextRight());
		
		
		aviableFinanceTable.setWidget(row, col, addButton);
		++col;
		aviableFinanceTable.setWidget(row, col, issueDate);
		++col;
		aviableFinanceTable.setWidget(row, col, document);
		++col;
		aviableFinanceTable.setWidget(row, col, titular);
		titular.setWidth(aviableFinanceAutoWidth + "px");
		titular.setStyleName(AON.CSS.aonTruncate());
		++col;
		aviableFinanceTable.setWidget(row, col, amount);
	}
	
	private void paintSelectedFinanceTable(final FinanceModuleOptions opt) {
		if(this.fBatch.getBatchDetails() != null && !this.fBatch.getBatchDetails().isEmpty()) {
			this.fBatch.getBatchDetails().stream().map(batchDetail -> batchDetail.getFinance()).forEach(finance -> {
				paintSelectedRow(opt, finance);
			});
		}
	}
	
	private void paintSelectedRow(final FinanceModuleOptions opt, Finance finance) {
		int row = selectedFinanceTable.getRowCount();
		selectedFinances.put(finance.getId(), new FinanceRow(row, finance));
		paintSelectedRow(opt, finance, row);
	}
	
	private void paintSelectedRow(final FinanceModuleOptions opt, Finance finance, int row) {
		int col = 0;
		
		AonTableButton removeButton = new AonTableButton("Quitar de la remesa", AON.CSS.aonIconLeft()); 
		removeButton.addClickHandler(e -> {
			selectedFinances.remove(finance.getId());
			aviableFinances.put(finance.getId(), new FinanceRow(row, finance));
			
			aviableFinanceContainer.clear();
			selectedFinanceContainer.clear();
			
			aviableFinanceTable = getAviableFinanceTable();
			selectedFinanceTable = getSelectedFinanceTable();
			
			aviableFinanceContainer.add(aviableFinanceTable);
			selectedFinanceContainer.add(selectedFinanceTable);
			
			aviableFinances.values().stream().map(financeRow -> financeRow.getFinance()).forEach(financeIt -> paintRow(opt, financeIt, aviableFinanceTable.getRowCount()));
			selectedFinances.values().stream().map(financeRow -> financeRow.getFinance()).forEach(financeIt -> paintSelectedRow(opt, financeIt, selectedFinanceTable.getRowCount()));
			
			String amountSum = selectedFinances.size() == 0 ? "0.00 \u20ac" : AON.FMT.format(selectedFinances.values().stream().map(financeRow -> financeRow.getFinance().getAmount()).reduce(0.00, (a, b) -> a + b)) + " \u20ac";
			totalAmount.setText(amountSum);
			
			Optional<FBatchDetail> fbatchDetail = this.fBatch.getBatchDetails().stream().filter(fbatchDetial -> null != fbatchDetial.getFinance() && fbatchDetial.getFinance().getId() == finance.getId()).findFirst();
			if(fbatchDetail.isPresent()) fbatchDetail.get().setRemoved(true);
			
			if(selectedFinances.isEmpty()) {
				Label label = new Label("No existen vencimientos en la remesa");
				label.setStyleName(AON.CSS.aonBlockMessage());
				label.addStyleName(AON.CSS.aonBlockInfoMessage());
				label.addStyleName(AON.CSS.aonMarginTop());
				selectedFinanceContainer.add(label);
			}
			
			saveButton.setEnabled(true);
		});

		Label issueDate = new Label(AON.DATE_FORMAT.format(finance.getDueDate()));
		
		Label document = new Label(finance.getRegistryDocument());
		
		Label titular = new Label(finance.getRegistryName());
		titular.setTitle(finance.getRegistryName());
		
		Label amount = new Label(AON.FMT.format(finance.getAmount()) + " \u20ac");
		amount.addStyleName(AON.CSS.aonTextRight());
		
		selectedFinanceTable.setWidget(row, col, finance.isPending() || finance.isBatched() ? removeButton : new Label());
		++col;
		selectedFinanceTable.setWidget(row, col, issueDate);
		++col;
		selectedFinanceTable.setWidget(row, col, document);
		++col;
		selectedFinanceTable.setWidget(row, col, titular);
		titular.setWidth(selectedFinanceAutoWidth + "px");
		titular.setStyleName(AON.CSS.aonTruncate());
		++col;
		selectedFinanceTable.setWidget(row, col, amount);
	}

	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	private void getEnterpriseBanks(final FinanceModuleOptions opt, Consumer<LinkedList<RegistryBank>> success) {
		COMMON_SERVICE.getCompanyBanks(opt.getDomainName(), opt.getDomain(), opt.getUser(), new AsyncCallback<LinkedList<RegistryBank>>() {
			
			@Override
			public void onSuccess(LinkedList<RegistryBank> rbanks) {
				success.accept(rbanks);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}
	
}		
