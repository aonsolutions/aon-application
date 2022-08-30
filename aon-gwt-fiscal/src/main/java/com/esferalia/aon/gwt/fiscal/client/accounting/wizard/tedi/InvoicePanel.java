package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog.AonMessageDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonScalableImage;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.ISelectionCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.TediProblems.ITediProblemsCallback;
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
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
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
	private Focusable focusableWidget;
	
	private FormPanel fileSelectForm;
	private FileUpload fileSelect;
	private AsyncCallback<IAccountEntryWrapper> attachmentCallback; 

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
		focusableWidget = eip;
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
		focusableWidget = null;
		if (getWrapper() != null) {
			if (getWrapper().getRegistry() == null || isAccountSource()) {
				editInvoice();
			} else {
				viewInvoice();
			}
			paintAttach( false );
		}
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
		getCallback().getModule().onPreview(AccountEntryModule.getWrapperArray (entries) );		
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
		LOGGER.info("isAccountSource() ? -> " + sourceAccount);
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
		if (getWrapper().getInvoice().getFinances() != null && !getWrapper().getInvoice().getFinances().isEmpty()) {
			boolean pendingFinances = false;
			for (Finance finance : getWrapper().getInvoice().getFinances()) {
				pendingFinances = pendingFinances || finance.isFullPending();
			}
			return pendingFinances;
		}
		return true;
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
	public boolean isAttachmentManagementEnabled() {
		return true;
	}
	@Override
	public boolean hasAttachment() {
		return (getWrapper() != null 
			&& getWrapper().getAccountEntry() != null
			&& getWrapper().getAccountEntry().getId() != null
			&& getWrapper().isDocumentAttached());
	}
	
	@Override
	public void removeAttach(final AsyncCallback<IAccountEntryWrapper> cbk) {
		Integer invoiceId = getWrapper().getInvoice().getId();
		getAccountEntryService().removeInvoiceAttach(getCallback().getCurrentDomainName()
				, getCallback().getCurrentDomainId()
				, getCallback().getCurrentUser()
				, invoiceId , new AsyncCallback<AccountingInvoice>() {

			@Override
			public void onSuccess(AccountingInvoice result) {
				setWrapper(result);
				cbk.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				cbk.onFailure(caught);
			}

		});
	}
	@Override
	public void addAttach(final AsyncCallback<IAccountEntryWrapper> cbk) {
		this.attachmentCallback = cbk;
		fileSelect.click();
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
		public AccountEntryModule getModule() {
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
		if (focusableWidget != null) {
			focusableWidget.setFocus(true);
		}
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

			fileSelectForm = new FormPanel();
			fileSelect = new FileUpload();
			fileSelectForm.add(fileSelect);
			attachPanelTableCell1.add(fileSelectForm);
			fileSelect.ensureDebugId("fileSelect");
			fileSelect.getElement().getStyle().setDisplay(Style.Display.NONE);
			fileSelect.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					event.preventDefault();
					fileSelectHandler(fileSelect.getElement());
				}
			});
			

			rootPanel.setWidgetSize(attachPanelTable,0);
		}
	}
	
	private native void fileSelectHandler(Element fileSelect) /*-{
		var self = this;		
		var file = fileSelect.files[0];
		var reader = new FileReader();
		reader.addEventListener("load", function () {
			self.@com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoicePanel::addAttach(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(reader.result,file.name,file.type); 
			}, false);
		reader.readAsDataURL( file );
	}-*/;
	
	protected void addAttach(final String doc, final String name, String type) {
		InvoicePanelCallback invoiceCallback = new InvoicePanelCallback();
		setDocument(invoiceCallback, doc, name, type,false);
		AccountingInvoice ai = getWrapper();
		if (ai.isDocumentAttached()) {
			getAccountEntryService().addInvoiceAttach(getCallback().getCurrentDomainName()
					, getCallback().getCurrentDomainId()
					, getCallback().getCurrentUser()
					, getWrapper() , new AsyncCallback<AccountingInvoice>() {

				@Override
				public void onSuccess(AccountingInvoice result) {
					setWrapper(result);
					InvoicePanel.this.attachmentCallback.onSuccess(result);
				}

				@Override
				public void onFailure(Throwable caught) {
					InvoicePanel.this.attachmentCallback.onFailure(caught);
				}

			});
		} else {
			AonMessageDialog.error("Documento no adjuntado, se admiten PDF y archivos de imagen");
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
		setDocument(invoiceCallback, doc, name, type, true);
	}

	private void setDocument(InvoicePanelCallback invoiceCallback,final String doc, final String name, String type, boolean allowParse) {
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
				paintButtons();
				double from = rootPanel.getWidgetSize(attachPanelTable) == null? 0 : rootPanel.getWidgetSize(attachPanelTable);
				int to = Window.getClientWidth() - 900;
				if (from < to) {
					rootPanel.setWidgetSize(attachPanelTable, to);
				}
				AonScalableImage scalableImage = new AonScalableImage( );
				attachPanelTableCell2.add(scalableImage);
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						scalableImage.setImage(doc);
				}});
			}
			
			if ( allowParse && invoiceCallback.getConfiguration().isOCRActive() ) {
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
			_paintEntry();
			editInvoice();
			getCallback().getModule().onPreview(getWrapper());
		} else {
			paintProblemsWidget(centerContainer, result);
		}
	}
	
	private void paintProblemsWidget(SimpleLayoutPanel contentPanel, TediResult result) {
		  TediProblems scrollPanel = new TediProblems( new ITediProblemsCallback() {
		    
		    @Override public AccountEntryModuleOptions getModuleOptions() {return getCallback().getModuleOptions();}
		    @Override public AccountEntryModule getModule() {return getCallback().getModule();}
		    @Override public String getCurrentUser() {return getCallback().getCurrentUser();}
		    @Override public String getCurrentDomainName() {return getCallback().getCurrentDomainName();}
		    @Override public int getCurrentDomainId() {return getCallback().getCurrentDomainId();}
		    @Override public AonConfiguration getConfiguration() {return getCallback().getConfiguration();}
		    
		    @Override
		    public void onError(Throwable caught) {
		      AonMessageDialog.error(AON.MSG.error() + " [Interno: " + caught.getMessage() + "]");
		    }
		    
		    @Override
		    public void onChanged(TediResult result) {
		      afterTediParse( result );
		    }
		    
		    @Override
		    public TediResult getResult() {
		      return result;
		    }
		  });
	  contentPanel.setWidget(scrollPanel);
	}

}

