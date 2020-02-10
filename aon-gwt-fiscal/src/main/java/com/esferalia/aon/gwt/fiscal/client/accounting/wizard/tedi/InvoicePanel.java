package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.ISelectionCallback;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediContextVisitor;
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
import com.esferalia.aon.occam.api.model.tedi.ICallback;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class InvoicePanel extends WizardContentBase<AccountingInvoice> implements HasSelectionHandlers<AccountingInvoice> {
	
	private static TediServiceAsync TEDI_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(InvoicePanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private SplitLayoutPanel rootPanel;
	private SimpleLayoutPanel centerContainer;
	private SimpleLayoutPanel attachPanel;
	private AccountingInvoice invoice;
	
	public InvoicePanel(final IAccountEntryModuleCallback callback) {
		TediServiceAsync serviceRaw = GWT.create(TediService.class);
		TEDI_SERVICE = new TediServiceAsyncDecorator(serviceRaw);
		
		setCallback(callback);
		rootPanel = new SplitLayoutPanel();
		centerContainer = new SimpleLayoutPanel();
		attachPanel = new SimpleLayoutPanel();
		rootPanel.addEast(attachPanel,0);
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
			paintAttach();
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
			getFiscalService().getAccountingInvoice(getCallback().getCurrentDomainName()
				,getCallback().getCurrentDomainId(),id
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
				select(ai,cbk);
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
	
	@Override
	public boolean isUpdatable() {
		
		return (super.isUpdatable()
				&& getAccountEntry().isInvoice()
				&& isAccountSource()
				&& !hasPaidFinances()
				);
	}
	
	private boolean hasPaidFinances() {
		boolean paidFinances = false;
		for (Finance finance : getWrapper().getFinances()) {
			paidFinances = paidFinances 
				|| finance.getFinanceStatus() == FinanceStatus.PAID
				|| finance.getFinanceStatus() == FinanceStatus.BATCHED
				|| finance.getFinanceStatus() == FinanceStatus.SETTLED;
		}
		return paidFinances;
	}
	
	@Override
	public String getNoUpdatableCause() {
		if (hasPaidFinances()) {
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
		getFiscalService().save(getCallback().getCurrentDomainName()
				, getCallback().getCurrentDomainId()
				, getWrapper(), new AsyncCallback<AccountingInvoice>() {

			@Override
			public void onSuccess(AccountingInvoice result) {
				setWrapper(result);
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
	};

	@Override
	public void manageWidgets(boolean canRemove, boolean canEdit) {
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingInvoice> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
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
//		getWrapper().getAccountEntry().setEntryDate(entryDate);
//		if (getWrapper().getInvoice() != null) {
//			getWrapper().getInvoice().setIssueDate(entryDate);
//			getWrapper().getInvoice().setTaxDate(entryDate);
//			taxDate.setValue(entryDate);
//		}
	}
	public void activityChanged(Integer activty) {
//		getWrapper().getAccountEntry().setActivity(activty);
//		if (getWrapper().getInvoice() != null) {
//			getWrapper().getInvoice().setActivity(activty);
//		}
	}
	public void confidentialChanged(boolean confidential) {
//		getWrapper().getAccountEntry().setConfidential(confidential);
//		if (getWrapper().getInvoice() != null) {
//			getWrapper().getInvoice().setConfidential(confidential);
//		}
	}
	
	protected void paintAttach() {
		if (getWrapper().isDocumentAttached()) {
			FlexTable hp = new FlexTable();
			hp.setHeight("100%");
			
			Button attachButton = new Button();
			attachButton.setTitle("Ver documento adjunto");
			attachButton.setStyleName(AON.AON_CSS.aonIconAttach());
			attachButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
			attachButton.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (rootPanel.getWidgetSize(attachPanel) <= 30) {
						rootPanel.setWidgetSize(attachPanel, Window.getClientWidth() / 3);
					}
					InvoicePanelCallback invoicePanelCallback = new InvoicePanelCallback();
					InvoiceAttachPanel invoiceAttachPanel = new InvoiceAttachPanel(invoicePanelCallback);
					attachPanel.setWidget(invoiceAttachPanel);
				}
			});
			
			hp.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonVerticalAlignMiddle());
			hp.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBackgroundHighlightedYellow());
			hp.setWidget(0, 0, attachButton);
			attachPanel.setWidget(hp);
			rootPanel.setWidgetSize(attachPanel,20);
			new Timer() {
				@Override
				public void run() {
					hp.getCellFormatter().removeStyleName(0, 0, AON.AON_CSS.aonBackgroundHighlightedYellow());
				}
			}.schedule(2000);
		} else {
			attachPanel.clear();
			rootPanel.setWidgetSize(attachPanel,0);
		}
	}

	private void setDocument(InvoicePanelCallback invoiceCallback,final String doc, final String name, String type) {
		attachPanel.clear();
		MimeType mimeType = MimeType.safeValueFromContenType(type);
		if (mimeType == null) {
			mimeType = MimeType.guessFromFileName(name);	
		}
		LOGGER.info("MimeType ..: " + (mimeType==null?"NULL":mimeType.getName()));
		if (mimeType != null && (mimeType.isPDF() || mimeType.isImage())) {
			if (mimeType.isPDF()) {
				FullViewer viewer = new FullViewer();
				viewer.addLoadHandler( new LoadHandler() {
					
					@Override
					public void onLoad(LoadEvent event) {
						viewer.open(doc);
					}
				});
				attachPanel.setWidget(viewer);
				if (rootPanel.getWidgetSize(attachPanel) <= 30) {
					rootPanel.setWidgetSize(attachPanel, Window.getClientWidth() / 3);
				}
			} if (mimeType.isImage()) {
				ScrollPanel scrollpanel = new ScrollPanel();
				scrollpanel.setStyleName(AON.AON_CSS.aonScrollArea());
				scrollpanel.addStyleName(AON.AON_CSS.aonTextCenter());
				Image image = new Image( doc );
				scrollpanel.setWidget(image);
				attachPanel.setWidget(scrollpanel);
				if (rootPanel.getWidgetSize(attachPanel) <= 30) {
					rootPanel.setWidgetSize(attachPanel, Window.getClientWidth() / 3);
				}
			}
			
			if ( invoiceCallback.getConfiguration().isTediActive() ) {
				final MimeType attachMimeType = mimeType;
				TEDI_SERVICE.parseInvoice(invoiceCallback.getCurrentDomainName(), invoiceCallback.getCurrentUser(), invoiceCallback.getCurrentDomainId(),
						invoiceCallback.getConfiguration().isTediSnapshotUser(), name, doc, new AsyncCallback<TediResult>() {
					
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
						Window.alert(caught.getMessage());
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
		if (result.isImportable()) {
			setWrapper(ai);
			getCallback().getModule().syncCurrent();
			editInvoice();
		} else {
			paintProblemsWidget(centerContainer, result);
		}
	}

	private void paintProblemsWidget(SimpleLayoutPanel contentPanel, TediResult result) {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel mainPanel = new FlowPanel();
		scrollPanel.setWidget(mainPanel);
		if (result.getMessages() != null && result.getMessages().size() > 0) {
			mainPanel.setStyleName(AON.AON_CSS.aonMarginTop5());
			mainPanel.addStyleName(AON.AON_CSS.aonMarginLeft());
			mainPanel.addStyleName(AON.AON_CSS.aonSimpleBorder());
			mainPanel.addStyleName(AON.AON_CSS.aonFixedFont());
			for (TediError error : result.getMessages()) {
				FlowPanel flowPanel = new FlowPanel();
				InlineLabel colorLabel = new InlineLabel("");
				colorLabel.setStyleName(AON.AON_CSS.aonPaddingLeft());
				colorLabel.addStyleName(AON.AON_CSS.aonPaddingRight());
				colorLabel.getElement().getStyle().setBackgroundColor(getBackgroundColor(error.getLevel()));
				flowPanel.add(colorLabel);

				InlineLabel errLabel = new InlineLabel(error.getLevel().getLabel());
				errLabel.setStyleName(AON.AON_CSS.aonClickableLabel());
				errLabel.addStyleName(AON.AON_CSS.aonPaddingLeft());
				errLabel.addStyleName(AON.AON_CSS.aonPaddingRight());
				errLabel.addStyleName(AON.AON_CSS.aonBold());
				flowPanel.add(errLabel);

				InlineLabel msgLabel = new InlineLabel(error.getMessage());
				msgLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
				msgLabel.addStyleName(AON.AON_CSS.aonBorderBottomImportant());
				flowPanel.add(msgLabel);

				if (error.canBeFixed()) {
					SimplePanel container = new SimplePanel();
					container.setStyleName(AON.AON_CSS.aonMarginTop());
					container.addStyleName(AON.AON_CSS.aonMarginBottom());
					flowPanel.add(container);
					TediContextVisitor tediContextVisitor = new TediContextVisitor(getCallback().getCurrentDomainName(),
							getCallback().getCurrentDomainId(), getCallback().getConfiguration(), container);
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
									getCallback().getCurrentUser(), getCallback().getCurrentDomainId(),
									getCallback().getConfiguration().isTediSnapshotUser(), result, new AsyncCallback<TediResult>() {

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

