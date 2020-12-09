package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog.AonMessageDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.ISelectionCallback;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediService;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceRecorder;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.tedi.ICallback;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;
import net.aonsolutions.gwt.pdfjs.client.FullViewer.ViewerDefaultScale;

public class InvoicePanel extends WizardContentBase<AccountingInvoice> implements HasSelectionHandlers<AccountingInvoice>,HasAccountEntrySelectionHandlers {
	
	private static TediServiceAsync TEDI_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(InvoicePanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private SplitLayoutPanel rootPanel;
	private SimpleLayoutPanel centerContainer;
	private FlowPanel attachPanelTable;
	private FlowPanel attachPanelTableRow;
	private FlowPanel attachPanelTableCell1;
	private VerticalPanel buttons;
	private FlowPanel attachPanelTableCell2;
	private AonTableButton attachCloseButton;
	private AonTableButton attachOpenButton;
	private AccountingInvoice invoice;
	private AccountingRegistry lastRegistry;
	
	public InvoicePanel(final IAccountEntryModuleCallback callback) {
		TediServiceAsync serviceRaw = GWT.create(TediService.class);
		TEDI_SERVICE = new TediServiceAsyncDecorator(serviceRaw);
		
		setCallback(callback);
		rootPanel = new SplitLayoutPanel(4);
		
		centerContainer = new SimpleLayoutPanel();
		
		attachPanelTable = new FlowPanel();
		attachPanelTable.setStyleName(AON.CSS.aonDisplayTable());
		attachPanelTable.addStyleName(AON.CSS.aonWidthAll());
		attachPanelTable.setHeight("100%");
		
		attachPanelTableRow = new FlowPanel();
		attachPanelTableRow.addStyleName(AON.CSS.aonWidthAll());
		attachPanelTableRow.setHeight("100%");
		attachPanelTableRow.setStyleName(AON.CSS.aonDisplayTableRow());
		attachPanelTable.add(attachPanelTableRow);
		
		attachPanelTableCell1 = new FlowPanel();
		attachPanelTableCell1.setHeight("100%");
		attachPanelTableCell1.setStyleName(AON.CSS.aonDisplayTableCell());
		attachPanelTableRow.add(attachPanelTableCell1);
		
		attachPanelTableCell2= new FlowPanel();
		attachPanelTableCell2.setStyleName(AON.CSS.aonDisplayTableCell());
		attachPanelTableCell2.setHeight("100%");
		attachPanelTableRow.add(attachPanelTableCell2);

		rootPanel.addEast(attachPanelTable,0);
		rootPanel.setWidgetToggleDisplayAllowed(attachPanelTable, true);
				
		rootPanel.add(centerContainer);
		initWidget(rootPanel);
	}
	
	private void editInvoice() {
		InvoicePanelCallback invoiceCallback = new InvoicePanelCallback();
		LOGGER.info("editInvoice " + (invoiceCallback.getInvoice() != null && invoiceCallback.getInvoice().getInvoice() != null && invoiceCallback.getInvoice().getInvoice().getId() != null));
		LOGGER.info("Editing invoice as account source");
		editInvoice(invoiceCallback);
	}
	
	private void editInvoice(InvoicePanelCallback invoiceCallback) {
		LOGGER.info("Editing invoice as account source");
		centerContainer.clear();
		EditableInvoicePanel eip = new EditableInvoicePanel(invoiceCallback);
		eip.addSelectionHandler(new SelectionHandler<AccountingInvoice>() {
			@Override
			public void onSelection(SelectionEvent<AccountingInvoice> event) {
				SelectionEvent.<AccountingInvoice>fire( InvoicePanel.this, event.getSelectedItem());
			}
		});
		eip.addSelectionHandler(new AccountEntrySelectionHandler() {
			
			@Override
			public void onSelection(AccountEntrySelectionEvent event) {
				AccountEntrySelectionEvent.fire( InvoicePanel.this, event.getSelectedItem(), null);
			}
		});
		
		centerContainer.setWidget( eip );
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				eip.setFocus(true);
			}});
	}

	private void viewInvoice() {
		LOGGER.info("Viewing invoice as management source");
		centerContainer.clear();
		centerContainer.setWidget( new InvoiceViewer(getWrapper().getInvoice()));
	}
	
	public void select(AccountingInvoice ai,final ISelectionCallback cbk) {
		LOGGER.info("Select invoice");
		setWrapper(ai);
		if (getWrapper() != null) {
			if (getWrapper().getRegistry() == null || isAccountSource()) {
				editInvoice();
			} else {
				viewInvoice();
			}
			paintAttach( false );
		}
		getCallback().getModule().onBalance(getWrapper());
		getCallback().getModule().onPreview(getWrapper());
		if (cbk != null) {
			cbk.onSuccess();
		}
	}

	@Override
	public AccountingInvoice getWrapper() {
		return invoice;
	}

	@Override
	public void setWrapper(AccountingInvoice wrapper) {
		this.invoice = wrapper;
	}

	@Override
	public void reset(final AccountEntry base,final ISelectionCallback cbk) {
		if (base == null) {
			getCallback().getModule().onError("[ERROR INTERNO] No hay un apunte base del que crear la factura");
		}
		AccountingInvoice ai = new AccountingInvoice();
		ai.setAccountEntry(new AccountEntry()
			.setPeriod(base.getPeriod())
			.setDomain(getCallback().getCurrentDomainId())
			.setConfidential(base.isConfidential())
			.setEntryDate(base.getEntryDate())
			.setActivity(base.getActivity())
			.setJournal(null));
		select(null, ai, cbk);
	}
	
	@Override
	public void select(final Integer id,final IAccountEntryWrapper wrp,final ISelectionCallback cbk) {
		getCallback().getModule().onClearSessionLog();
		if (id != null) {
			getAccountEntryService().getAccountingInvoice(getCallback().getCurrentDomainName()
				,getCallback().getCurrentDomainId(),getCallback().getCurrentUser(),id
				,new AsyncCallback<AccountingInvoice>() {
						@Override
						public void onSuccess(AccountingInvoice result) {
							select(result,cbk);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							getCallback().getModule().onError(caught.getMessage());
						}
					});
		} else {
			if (wrp != null) {
				AccountingInvoice ai = (AccountingInvoice) wrp;
				if (getCallback().getModuleOptions() .getTediResult() != null) {
					afterTediParse(getCallback().getModuleOptions() .getTediResult());
					setWrapper(ai);
					paintAttach( true );
				} else {
					select(ai,cbk);
				}
			} else {
				getCallback().getModule().onError("[ERROR INTERNO] No hay que seleccionar.");
			}
		}
		
	}		


	private void _paintEntry() {
		AccountEntry[] entries = InvoiceRecorder.recordInvoice(getWrapper());
		getCallback().getModule().onPreview(AccountEntryModuleTEDI.getWrapperArray (entries) );		
	}
	
	private boolean isAccountSource() {
		boolean sourceAccount = true;
		if (getWrapper().getInvoice() != null && getWrapper().getInvoice().getDetails() != null) {
			for (InvoiceDetail detail : getWrapper().getInvoice().getDetails()) {
				LOGGER.info(detail.getSource().getDescription());
				if (detail.getSource() != InvoiceSource.ACCOUNT) {
					sourceAccount = false;
					break;
				}
			}
		}
		return sourceAccount;
	}
	
	private boolean isDUALinked() {
		return (getWrapper().getInvoice() != null 
			&& getWrapper().getInvoice().isDUALinkAllowed() 
			&& getWrapper().getDuaNationalInvoice() != null);
	}

	@Override
	public boolean isUpdatable() {
		return (super.isUpdatable()
				&& getAccountEntry().isInvoice()
				&& isAccountSource()
				&& !isDUALinked()
				&& hasPendingFinances()
				);
	}
	
	private boolean hasPendingFinances() {
		boolean pendingFinances = false;
		for (Finance finance : getWrapper().getInvoice().getFinances()) {
			pendingFinances = pendingFinances || finance.isFullPending();
		}
		return pendingFinances;
	}
	
	@Override
	public String getNoUpdatableCause() {
		if (isDUALinked()) {
			return AON.MSG.DUALinked();
		}
		if (!hasPendingFinances()) {
			return AON.MSG.hasPaidFinances();
		}
		if (!isAccountSource()) {
			return AON.MSG.managmentInvoice();
		}
		return null;
	}
	
	@Override
	public boolean isStatusMsgEnabled() {
		return getWrapper() != null && getWrapper().isAccountSource();
	}

	@Override
	public void save(final AsyncCallback<IAccountEntryWrapper> cbk) {
		getAccountEntryService().save(getCallback().getCurrentDomainName()
				, getCallback().getCurrentDomainId()
				, getCallback().getCurrentUser()
				, getWrapper(), new AsyncCallback<AccountingInvoice>() {

			@Override
			public void onSuccess(AccountingInvoice result) {
				setWrapper(result);
				lastRegistry = result.getRegistry();
				cbk.onSuccess(getWrapper());
			}

			@Override
			public void onFailure(Throwable caught) {
				cbk.onFailure(caught);
			}

		});
	}
	
	protected class InvoicePanelCallback implements IInvoicePanelCallback {
		@Override
		public AccountEntryModuleTEDI getModule() {
			return getCallback().getModule();
		}
		@Override
		public AonConfiguration getConfiguration() {
			return getCallback().getConfiguration();
		}
		@Override
		public AccountEntryModuleOptions getModuleOptions() {
			return getCallback().getModuleOptions();
		}
		@Override
		public String getCurrentDomainName() {
			return getCallback().getCurrentDomainName();
		}
		@Override
		public int getCurrentDomainId() {
			return getCallback().getCurrentDomainId();
		}
		@Override
		public String getCurrentUser() {
			return getCallback().getCurrentUser();
		}
		@Override
		public AccountingInvoice getInvoice() {
			return getWrapper();
		}
		public void setInvoice(AccountingInvoice result) {
			setWrapper(result);
		}
		public void setAccountEntry(AccountEntry ae) {
			getWrapper().setAccountEntry(ae);
		}
		
		@Override
		public boolean isInvestAssetsAvailable() {
			return !getWrapper().isSales() 
				&& !getWrapper().isSurcharge()
				&& getWrapper().isOutputVatEnabled() != getWrapper().isInputVatEnabled()
				&& getCallback().getConfiguration().isInvestAssetsAvailable();
			
		}
		
		@Override
		public void paintEntry() {
			_paintEntry();
			getWrapper().getAccountEntry().setDirty(true);
			getCallback().getModule().refreshIdLabel();
		}
		
		public void setDocument(final String doc, final String name, String type) {
			InvoicePanel.this.setDocument(this, doc, name, type);
		}
		@Override
		public AccountingRegistry getLastRegistry() {
			return lastRegistry;
		}
	};

	@Override
	public void manageWidgets(boolean canRemove, boolean canEdit) {
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingInvoice> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}

	@Override
	public int getTabIndex() {
		return 0;
	}

	@Override
	public void setAccessKey(char key) {
	}

	@Override
	public void setTabIndex(int index) {
	}

	@Override
	public void setFocus(boolean b) {
	}

	public void entryDateChanged(Date entryDate) {
	}
	public void activityChanged(Integer activty) {
	}
	public void confidentialChanged(boolean confidential) {
	}
	
	protected void paintAttach( boolean openWidget ) {
		LOGGER.info("paintAttach ..: " + (getWrapper().isDocumentAttached()?"DOCUMENT PRESENT":"NO DOCUMENT"));
		if (getWrapper().isDocumentAttached()) {
			paintButtons();
			buttons.addStyleName(AON.CSS.aonBackgroundYellow());
			if (openWidget) {
				openAttach();
			} else {
				rootPanel.setWidgetSize(attachPanelTable,20);
			}
			new Timer() {
				@Override
				public void run() {
					buttons.removeStyleName(AON.CSS.aonBackgroundYellow());
				}
			}.schedule(2000);
		} else {
			attachPanelTableCell1.setWidth("1px");
			attachPanelTableCell1.clear();
			attachPanelTableCell2.clear();
			rootPanel.setWidgetSize(attachPanelTable,0);
		}
	}
	
	private void paintButtons() {
		attachPanelTableCell1.setWidth("20px");
		buttons = new VerticalPanel();
		buttons.setHeight("100%");
		buttons.setStyleName(AON.CSS.aonFlexBlock());
		attachPanelTableCell1.add(buttons);
		
		attachCloseButton = new AonTableButton("Cerrar documento adjunto",AON.CSS.aonIconRight());
		buttons.add(attachCloseButton);

		attachOpenButton = new AonTableButton("Ver documento adjunto",AON.CSS.aonIconLeft());
		buttons.add(attachOpenButton);
		
		attachCloseButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				closeAttach();
			}
		});
		
		attachOpenButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				openAttach();
			}
		});
	}

	protected void openAttach() {
		double from = rootPanel.getWidgetSize(attachPanelTable) == null? 0 : rootPanel.getWidgetSize(attachPanelTable);
		int to = Window.getClientWidth() - 900;
		if (from < to) {
			rootPanel.setWidgetSize(attachPanelTable, to);
		}
		viewAttach();
	}

	protected void closeAttach() {
		rootPanel.setWidgetSize(attachPanelTable, 20);
	}

	protected void viewAttach() {
		if (attachPanelTableCell2.getWidgetCount() == 0) {
			InvoicePanelCallback invoicePanelCallback = new InvoicePanelCallback();
			InvoiceAttachPanel invoiceAttachPanel = new InvoiceAttachPanel(invoicePanelCallback);
			attachPanelTableCell2.add(invoiceAttachPanel);
		}
	}

	private void setDocument(InvoicePanelCallback invoiceCallback,final String doc, final String name, String type) {
		attachPanelTableCell2.clear();
		MimeType mimeType = MimeType.safeValueFromContenType(type);
		if (mimeType == null) {
			mimeType = MimeType.guessFromFileName(name);	
		}
		LOGGER.info("MimeType ..: " + (mimeType==null?"NULL":mimeType.getName()));
		if (mimeType != null && (mimeType.isPDF() || mimeType.isImage())) {
			if (mimeType.isPDF()) {
				FullViewer viewer = new FullViewer( ViewerDefaultScale.PAGE_WIDTH );
				viewer.addLoadHandler( new LoadHandler() {
					
					@Override
					public void onLoad(LoadEvent event) {
						viewer.open(doc);
					}
				});
				attachPanelTableCell2.add(viewer);
				paintButtons();
				openAttach();
			} if (mimeType.isImage()) {
				ScrollPanel scrollpanel = new ScrollPanel();
				scrollpanel.setStyleName(AON.CSS.aonScrollArea());
				scrollpanel.addStyleName(AON.CSS.aonTextCenter());
				Image image = new Image( doc );
				image.setWidth("100%");
				scrollpanel.setWidget(image);
				attachPanelTableCell2.add(scrollpanel);
				paintButtons();
				openAttach();
			}
			
			if ( invoiceCallback.getConfiguration().isOCRActive() ) {
				final MimeType attachMimeType = mimeType;
				TEDI_SERVICE.parseInvoice(invoiceCallback.getCurrentDomainName(), invoiceCallback.getCurrentUser(), 
					invoiceCallback.getCurrentDomainId(), name, doc, new AsyncCallback<TediResult>() {
					
					@Override
					public void onSuccess(TediResult result) {
						AccountingInvoice ai = result.getAccountingInvoice();
						Attach attach = new Attach();
						attach.setAttachType(AttachType.INVOICE);
						attach.setMimeType(attachMimeType);
						attach.setData(doc.getBytes());
						attach.setDescription(name);
						ai.setAttach(attach);
						afterTediParse( result );
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AonMessageDialog d = new AonMessageDialog();
						d.show("ERROR INESPERADO", caught.getMessage(), new AonMessageDialogCallback() {
							@Override
							public void onAccept() {
								reset(invoiceCallback.getInvoice().getAccountEntry(), null);
							}
						});
					}
				});		
			} else {
				AccountingInvoice ai = getWrapper();
				Attach attach = new Attach();
				attach.setAttachType(AttachType.INVOICE);
				attach.setMimeType(mimeType);
				attach.setData(doc.getBytes());
				attach.setDescription(name);
				ai.setAttach(attach);
			}
		}
	}



	protected void afterTediParse(TediResult result) {
		AccountingInvoice ai = result.getAccountingInvoice();
		LOGGER.info("setDocument result.isImportable() --- > " + result.isImportable());
	setWrapper(ai);
	getCallback().getModule().syncCurrent();
		if (result.isImportable()) {
			editInvoice();
			getCallback().getModule().onBalance(getWrapper());
			getCallback().getModule().onPreview(getWrapper());
		} else {
			paintProblemsWidget(centerContainer, result);
		}
	}

	private void paintProblemsWidget(SimpleLayoutPanel contentPanel, TediResult result) {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonScrollArea());
		FlowPanel mainPanel = new FlowPanel();
		scrollPanel.setWidget(mainPanel);
		if (result.getMessages() != null && result.getMessages().size() > 0) {
			mainPanel.setStyleName(AON.CSS.aonMarginTopSep());
			mainPanel.addStyleName(AON.CSS.aonMarginLeft());
			mainPanel.addStyleName(AON.CSS.aonBorder());
			mainPanel.addStyleName(AON.CSS.aonFixedFont());
			for (TediError error : result.getMessages()) {
				FlowPanel flowPanel = new FlowPanel();
				InlineLabel colorLabel = new InlineLabel("");
				colorLabel.setStyleName(AON.CSS.aonPaddingLeft());
				colorLabel.addStyleName(AON.CSS.aonPaddingRight());
				colorLabel.getElement().getStyle().setBackgroundColor(getBackgroundColor(error.getLevel()));
				flowPanel.add(colorLabel);

				InlineLabel errLabel = new InlineLabel(error.getLevel().getLabel());
				errLabel.setStyleName(AON.CSS.aonClickable());
				errLabel.addStyleName(AON.CSS.aonPaddingLeft());
				errLabel.addStyleName(AON.CSS.aonPaddingRight());
				errLabel.addStyleName(AON.CSS.aonBold());
				flowPanel.add(errLabel);

				InlineLabel msgLabel = new InlineLabel(error.getMessage());
				msgLabel.setStyleName(AON.CSS.aonMarginLeft());
				msgLabel.addStyleName(AON.CSS.aonBorderBottom());
				flowPanel.add(msgLabel);

				if (error.canBeFixed()) {
					SimplePanel container = new SimplePanel();
					container.setStyleName(AON.CSS.aonMarginTop());
					container.addStyleName(AON.CSS.aonMarginBottom());
					flowPanel.add(container);
					TediContextVisitor tediContextVisitor = new TediContextVisitor(getCallback().getCurrentDomainName(),
							getCallback().getCurrentDomainId(),getCallback().getCurrentUser(), getCallback().getConfiguration(), container);
					error.getContext().getKey().visit(tediContextVisitor, new ICallback() {

						@Override
						public TediResult getResult() {
							return result;
						}

						@Override
						public AonConfiguration getConfiguration() {
							return getCallback().getConfiguration();
						}

						@Override
						public void onCancel() {

						}

						@Override
						public void onAccept(TediResult result) {
							TEDI_SERVICE.validateInvoice(getCallback().getCurrentDomainName(),
									getCallback().getCurrentUser(), getCallback().getCurrentDomainId(), result, new AsyncCallback<TediResult>() {

										@Override
										public void onSuccess(TediResult result) {
											afterTediParse( result );
										}

										@Override
										public void onFailure(Throwable caught) {
											MessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
										}
									});
						}
					});
				}
				mainPanel.add(flowPanel);
			}
		}
		contentPanel.setWidget(scrollPanel);
	}

	private String getBackgroundColor(TediLevel curLevel) {
		String color = null;
		if (curLevel == null) {
			color = "#e6ffe6";
		} else if (curLevel == TediLevel.INF) {
			color = "#e7f5fe";
		} else if (curLevel == TediLevel.WRN) {
			color = "#ffbf80";
		} else if (curLevel == TediLevel.ERR) {
			color = "#ffc2b3";
		}
		return color;
	}


}

