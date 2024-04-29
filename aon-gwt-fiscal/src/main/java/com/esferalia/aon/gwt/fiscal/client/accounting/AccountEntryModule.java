package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.WithholdingTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAuditDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.JournalPanelReport;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AccountPreviewPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.FinanceEntryPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoicePanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.Manual;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.SalaryEntryPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.SessionLog;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryTypeVisitorAdapter;
import com.esferalia.aon.occam.api.model.AccountEntryWrapper;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAccountEntryUpdateVisitor;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class AccountEntryModule extends MainEntryPoint {
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);

	private static final Logger LOGGER = Logger.getLogger(AccountEntryModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private int sessionLogTabIndex;
	private int previewTabIndex;
	private int journalTabIndex;
	private int extraInfoTabIndex;
	
	static AccountEntryServiceAsync ACCOUNT_ENTRY_SERVICE;
	static CommonServiceAsync COMMON_SERVICE;

	private AccountEntryModuleOptions options;
	private AccountEntry base;

	public interface IAccountEntryModuleCallback {
		@Deprecated String getCurrentDomainName();
		@Deprecated int getCurrentDomainId();
		@Deprecated String getCurrentUser();
		Occam getOccam();
		AonConfiguration getConfiguration();
		AccountEntryModule getModule();
		AccountEntryModuleOptions getModuleOptions();
	}

	private static interface IEntryTypeVisitor {
		void visitManual();
		void visitInvoice();
		void visitSalary();
		void visitFinance();
	}
	public interface IEntryTypeVisitorWalker {
		void visit( IEntryTypeVisitor visitor);
	}
	private static enum EntryType {
		 MANUAL( AON.MSG.manual(), new  IEntryTypeVisitorWalker() {
			@Override public void visit(IEntryTypeVisitor visitor) {visitor.visitManual();}})
		,INVOICE( AON.MSG.invoice(), new  IEntryTypeVisitorWalker() {
			@Override public void visit(IEntryTypeVisitor visitor) {visitor.visitInvoice();}})
		,SALARY( AON.MSG.salary(), new  IEntryTypeVisitorWalker() {
			@Override public void visit(IEntryTypeVisitor visitor) {visitor.visitSalary();}})
		,FINANCE( AON.MSG.treasury(), new  IEntryTypeVisitorWalker() {
			@Override public void visit(IEntryTypeVisitor visitor) {visitor.visitFinance();}})
		;
		private String description;
		private IEntryTypeVisitorWalker walker;
		
		private EntryType(String description,IEntryTypeVisitorWalker walker) {
			this.description = description;
			this.walker = walker;
		}
		public String getDescription() {
			return description;
		}
		private void visit(IEntryTypeVisitor visitor) {
			walker.visit(visitor);
		}
	}
	private IAccountEntryModuleCallback moduleCallback = new IAccountEntryModuleCallback() {
		@Override
		public AccountEntryModule getModule() {
			return AccountEntryModule.this;
		}
		@Override
		public AonConfiguration getConfiguration() {
			return AccountEntryModule.this.getOptions().getConfiguration();
		};
		@Override
		public AccountEntryModuleOptions getModuleOptions() {
			return AccountEntryModule.this.getOptions();
		};
		@Override
		public Occam getOccam() {
			return AccountEntryModule.this.getOptions().getOccam();
		}
		@Override
		public String getCurrentDomainName() {
			return AccountEntryModule.this.getOptions().getDomainName();
		}

		@Override
		public int getCurrentDomainId() {
			return AccountEntryModule.this.getOptions().getDomain();
		}

		@Override
		public String getCurrentUser() {
			return AccountEntryModule.this.getOptions().getUser();
		}
	};

	private SimpleLayoutPanel wizardPanel;
	private AonMinimizePanel footPanel;
	private TabLayoutPanel tabLayout;
	private SessionLog sessionLog;
	private JournalPanelReport journalPanel;
	private AccountPreviewPanel previewPanel;
	private ScrollPanel extraInfoContainer;
	// Toolbar
	private AonToolbar toolbar;
	private AonToolbarButton search;
	private AonToolbarButton reset;
	private AonToolbarButton accept;
	private AonToolbarButton remove;
	private AonToolbarButton back;
	private AonToolbarButton duplicate;
	private AonToolbarButton specialUpdate;
	private AonToolbarButton attachment;
	private AonToolbarButton removeAttachment;
	private AonToolbarButton audit;
	
	private SplitLayoutPanel splitLayoutPanel;	
	
	private DockLayoutPanel dockLayoutPanel;
	private DockLayoutPanel centerDockLayoutPanel;

	private FlowPanel entryHeader;
	private AccountPeriodBox period;
	private AonDateBox entryDate;
	private ListBox entryType;
	private InlineLabel journal;
	private InlineLabel id;
	private ListBox activity;
	private CheckBox confidential;
	private AonTableButton commentsButton;

	boolean minimizedByUser;
	
	private boolean periodErrorShown;
	private IWizardContent wizardContent;

	private AccountEntryModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new AccountEntryModuleOptions();
		}
		return this.options;
	}
	
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		AccountEntryModuleOptions options = new AccountEntryModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}

	public void onModuleLoad(AccountEntryModuleOptions options) {
		this.options = options;

		AON.ensureInjected();
		
		AccountEntryServiceAsync accountEntryServiceRaw = GWT.create(AccountEntryService.class);
		ACCOUNT_ENTRY_SERVICE = new AccountEntryServiceAsyncDecorator(accountEntryServiceRaw);
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		dockLayoutPanel.setStyleName(AON.CSS.aonSelector());
		toolbar = getToolbarPanel();
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		splitLayoutPanel = new SplitLayoutPanel();
		wizardPanel = new SimpleLayoutPanel();
		wizardPanel.setStyleName(AON.CSS.aonSelector());
		splitLayoutPanel.addSouth(getMinimizePanel(), 30);
		splitLayoutPanel.add(wizardPanel);
		centerDockLayoutPanel = new DockLayoutPanel(Unit.PX);
		centerDockLayoutPanel.addNorth( getEntryHeader(), 35);
		centerDockLayoutPanel.add(splitLayoutPanel);
		dockLayoutPanel.add(centerDockLayoutPanel);
		
		getOptions().getParentWidget().add(dockLayoutPanel);
		
		activity.setTabIndex(-1);
		confidential.setTabIndex(-1);
		commentsButton.setTabIndex(-1);
		
		for (EntryType et : EntryType.values() ) {
			entryType.addItem(et.getDescription());
		}
		
		entryType.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						LOGGER.info("BLUR entryType ");
						AccountEntryModule.this.getWizardContent().setFocus(true);
					}
				});
			}
		});
		
		if (getOptions().getConfiguration() != null) {
			loadModule();
		} else {
			COMMON_SERVICE.getAonConfiguration(getOptions().getDomainName(), getOptions().getDomain(), getOptions().getUser(),
					new AsyncCallback<AonConfiguration>() {
				@Override
				public void onSuccess(AonConfiguration result) {
					getOptions().setConfiguration(result);
					loadModule();
				}
				@Override
				public void onFailure(Throwable caught) {
					invalidateModule(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]");
				}
			});
			
		}
	}

	private IWizardContent getWizardContent() {
		return this.wizardContent;
	}


	private void loadModule() {
		boolean editing = (getOptions().getAccountEntryId() != null) || (getOptions().getAccountingInvoice() !=null);
		if (getOptions().getConfiguration().accounting().getPeriods() != null && !getOptions().getConfiguration().accounting().getPeriods().isEmpty()) {
			period.fill(getOptions().getConfiguration().accounting().getPeriods());
		} else {
			if (!editing) {
				invalidateModule(AON.MSG.noActiveAccountPeriod());
			}
		}

		if (getOptions().getConfiguration().hasActivities() && activity.getItemCount() == 0) {
			activity.setVisible(true);
			activity.addItem("-- Todas --", "");
			activity.setSelectedIndex(0);
			int i = 1;
			for (EnterpriseActivity ea : getOptions().getConfiguration().getActivities()) {
				activity.addItem(ea.getDescription(), AonNumberUtils.toString( ea.getId()));
				if (ea.isPrincipal()) {
					activity.setSelectedIndex(i);
					activity.setItemText(i, ea.getDescription() + AonStringUtils.ASTERISK);
				}
				i++;
			}
		} else {
			activity.setVisible(false);
		}
		confidential.setVisible(getOptions().getConfiguration().getUser().hasConfidentialityRole());
		
		if (getOptions().getAccountEntryId() != null) {
			selectEntry(getOptions().getAccountEntryId());
		} else if (getOptions().getAccountingInvoice() != null) {
			AccountingInvoice ai = getOptions().getAccountingInvoice();
			if (ai.isTediParsed() && ai.getAccountEntry() != null && ai.getAccountEntry().getEntryType() == null) {
				ai.getAccountEntry().setEntryType( AccountEntryType.EXPENSE_INVOICE );
			}
			selectWizardContent(null, ai);
		} else {
			reset();
		}
	}
	
	private int getSessionLogTabIndex(){
		return sessionLogTabIndex;
	}
	private int getPreviewTabIndex(){
		return previewTabIndex;
	}
	private int getJournalTabIndex(){
		return journalTabIndex;
	}
	private int getExtraInfoTabIndex(){
		return extraInfoTabIndex;
	}

	protected void invalidateModule(String msg) {
		showError(msg);
		entryHeader.setVisible(false);
		if (getOptions().isJournalTabVisible()) {
			search.setVisible(false);
		}
		reset.setVisible(false);
		accept.setVisible(false);
		remove.setVisible(false);
		audit.setVisible(false);
		duplicate.setVisible(false);
		specialUpdate.setVisible(false);
		attachment.setVisible(false);
		removeAttachment.setVisible(false);
		if (getOptions().isBackButtonVisible()) {
			back.setVisible(false);
		}
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
		int effectiveHeigth = getOptions().isEmbedded()?5:3;
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / effectiveHeigth);
		splitLayoutPanel.animate(500);
	}

	void onChangeEntryDate(ValueChangeEvent<Date> event) {
		this.wizardContent.getMainEntry().setEntryDate(event.getValue());
		checkDate();
		refreshIdLabel();
		this.wizardContent.entryDateChanged(event.getValue());
	}

	void onChangeAccountPeriod(ChangeEvent event) {
		Integer ap = AonNumberUtils.toInteger(period.getSelectedValue());
		this.wizardContent.getMainEntry().setPeriod(ap);
		checkDate();
		refreshIdLabel();
	}
	
	void onChangeActivity(ChangeEvent event) {
		Integer act = AonNumberUtils.toInteger(activity.getSelectedValue());
		this.wizardContent.getMainEntry().setActivity(act);
		refreshIdLabel();
		this.wizardContent.activityChanged(act);
	}

	void onChangeConfidential(ClickEvent event) {
		this.wizardContent.getMainEntry().setConfidential(confidential.getValue());
		refreshIdLabel();
		this.wizardContent.confidentialChanged(confidential.getValue());
	}

	private void checkDate() {
		if (period.isOutOfRange(entryDate.getValue())) {
			periodErrorShown = true;
			showError(AON.MSG.accountEntryOutOfRange());
		} else {
			if (periodErrorShown) {
				periodErrorShown = false;
				hideErrors();
			}
		}
	}

	public void onComments(ClickEvent event) {
		final AonCustomDialog toast = new AonCustomDialog();
		toast.setCaption(AON.MSG.comments());
		FlowPanel commentPanel = new FlowPanel();
		commentPanel.setStyleName(AON.CSS.aonTextCenter());
		commentPanel.addStyleName(AON.CSS.aonPadding());
		TextArea comment = new TextArea();
		comment.setText(wizardContent.getMainEntry().getComments());
		comment.setWidth("400px");
		comment.setHeight("100px");
		comment.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					toast.hide();
				}
			}
		});

    	FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	buttons.addStyleName(AON.CSS.aonMarginTop());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					toast.hide();
				}
			}
		});
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				wizardContent.getMainEntry().setComments(comment.getValue());
				styleCommentsButton();
				refreshIdLabel();
				toast.hide();
			}
		});
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				toast.hide();
			}
		});
    	cancelButton.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					toast.hide();
				}
			}
		});
    	buttons.add(cancelButton);
		
		
		commentPanel.add(comment);
		commentPanel.add(buttons);
		toast.add(commentPanel);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				comment.setFocus(true);
			}
		});
		
		
		toast.center();
		toast.show();
		
	}

	public boolean isManual() {
		return wizardContent != null && wizardContent.getMainEntry() != null && wizardContent.getMainEntry().getEntryType() == AccountEntryType.MANUAL;
	}
	public boolean isNew() {
		return wizardContent != null && (wizardContent.getMainEntry() == null || wizardContent.getMainEntry().getId() == null);
	}
	public boolean isDirty() {
		return wizardContent != null && wizardContent.getMainEntry().isDirty();
	}
	
	public void syncCurrent() {
		
		boolean canRemove = false;
		boolean canEdit = false;
		
		boolean guest = getOptions().getConfiguration().getUser().hasGuestRole() && !getOptions().getConfiguration().getUser().hasAdminRole(); 
		if (!guest) {
			canRemove = (!isNew() && (wizardContent.isUpdatable() || wizardContent.isRemovable()));
			canEdit = (isNew() || (!isNew() && wizardContent.isUpdatable()));
		}
		
		if (!getOptions().isDeleteButtonVisible()) {
			canRemove = false;
		}

		// Populate header values
		period.select(wizardContent.getMainEntry().getPeriod());
		entryDate.setValue(wizardContent.getMainEntry().getEntryDate());
		checkDate();
		confidential.setValue(wizardContent.getMainEntry().isConfidential());
		if (getOptions().getConfiguration().hasActivities()) {
			activity.setEnabled(canEdit);
			int i = 0;
			for (; i < activity.getItemCount(); i++) {
				Integer a = AonNumberUtils.toInteger(activity.getValue(i));
				Integer b = wizardContent.getMainEntry().getActivity();
				if (AonNumberUtils.equals(a,b)) {
					activity.setSelectedIndex(i);
					break;
				}
			}
			if (i == activity.getItemCount()) {
				activity.setSelectedIndex(0);
			}
		}
		
		hideErrors();
		
		if (!canEdit && wizardContent.isStatusMsgEnabled() ) {
			if (!wizardContent.getMainEntry().isPeriodActive()) {
				if (!canRemove) {
					toolbar.showInfoMessage(AON.MSG.periodStatusWarning(wizardContent.getMainEntry().getPeriodStatus().getDescription()));			
				} else {
					toolbar.showInfoMessage(AON.MSG.automaticEntryNoUpdateWarning());
				}
			} else {
				if (AonStringUtils.isEmpty(wizardContent.getNoUpdatableCause())) {
					toolbar.showInfoMessage(AON.MSG.entryNoUpdatable());
				} else {
					toolbar.showInfoMessage(AON.MSG.entryNoUpdatable() + " ["+ wizardContent.getNoUpdatableCause() +"]");
				}
			}
		}
		refreshIdLabel();
		
		wizardContent.manageWidgets(canRemove,canEdit);

		// Enable/Disable header values
		reset.setVisible(!guest && !getOptions().hasExternalCallback());
		if (getOptions().isJournalTabVisible()) {
			search.setVisible(!getOptions().hasExternalCallback());
		}
		duplicate.setVisible(!guest && !isNew() && isManual());
		period.setEnabled(canEdit);
		entryDate.setEnabled(canEdit);
		confidential.setEnabled(canEdit);
		entryType.setEnabled(canEdit);
		accept.setVisible(canEdit);
		accept.setEnabled(canEdit);
		specialUpdate.setVisible(!isNew());
		specialUpdate.setEnabled(!isNew());
		attachment.setVisible( !isNew() && wizardContent.isAttachmentManagementEnabled() && !wizardContent.hasAttachment() );
		removeAttachment.setVisible( !isNew() && wizardContent.isAttachmentManagementEnabled() && wizardContent.hasAttachment() );
		remove.setVisible(canRemove);
		remove.setEnabled(canRemove);
		if (getOptions().isBackButtonVisible()) {
			back.setVisible(getOptions().hasExternalCallback());
		}
		
		styleCommentsButton();
	}

	public void refreshIdLabel() {
		journal.setText(AonMathUtils.toInt(wizardContent.getMainEntry()
				.getJournal()) == 0 ? AonStringUtils.EMPTY : AON.MSG.journal()
				+ AonStringUtils.SPACE + wizardContent.getMainEntry().getJournal());
		id.setText(
			(isNew()? "[NUEVO]": ("(" + wizardContent.getMainEntry().getId() + ") "))
			+ (!isNew() && wizardContent.getMainEntry().isDirty()?AonStringUtils.ASTERISK:AonStringUtils.EMPTY));
		if (wizardContent.getMainEntry().isDirty()) {
			id.addStyleName(AON.CSS.aonColorBlue());
		} else {
			id.removeStyleName(AON.CSS.aonColorBlue());
		}
	}

	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(wizardContent.getMainEntry().getComments())) {
			commentsButton.addStyleName(AON.CSS.aonIconNoComments());
			commentsButton.removeStyleName(AON.CSS.aonIconComments());
		} else {
			commentsButton.addStyleName(AON.CSS.aonIconComments());
			commentsButton.removeStyleName(AON.CSS.aonIconNoComments());
		}
	}

	public void onAccept(ClickEvent event) {
		accept.setEnabled(false);
		remove.setEnabled(false);
		wizardContent.save(new AsyncCallback<IAccountEntryWrapper>() {

			@Override
			public void onSuccess(IAccountEntryWrapper result) {
				base = result.getAccountEntry();
				if (getOptions().isSessionLogTabVisible()) {
					sessionLog.addSaved(AccountEntryModule.getWrapperArray(result.getAccountEntries()));
				}
				reset();
				
				if (getOptions().isSessionLogTabVisible()) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						public void execute() {
							tabLayout.selectTab( getSessionLogTabIndex() );					}
					});
				}

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						entryDate.setFocus(true);
						entryDate.hideDatePicker();
						entryDate.getTextBox().selectAll();
					}
				});
				if (getOptions().hasExternalCallback()) {
					getOptions().getExternalCallback().onChange(result);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				accept.setEnabled(true);
				remove.setEnabled(true);
				showError(caught.getMessage());
			}
		});
	}

	public void onSearch(ClickEvent event) {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
			int effectiveHeigth = getOptions().isEmbedded()?5:3;
			splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / effectiveHeigth);
			splitLayoutPanel.animate(500, new AnimationCallback() {

				@Override
				public void onLayout(Layer layer, double progress) {
				}

				@Override
				public void onAnimationComplete() {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						public void execute() {
							tabLayout.selectTab( AccountEntryModule.this.getJournalTabIndex() );
							journalPanel.setFocus(true);
						}
					});
				}
			});
		} else {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				public void execute() {
					tabLayout.selectTab(AccountEntryModule.this.getJournalTabIndex());
					journalPanel.setFocus(true);
				}
			});
		}
	}

	public void onReset(ClickEvent event) {
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.newAction() + "?", AON.MSG.newAction(),new AonConfirmDialogCallback() {
			
			@Override
			public void onCancel() {
			}
			
			@Override
			public void onAccept() {
				reset();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						entryDate.setFocus(true);
						entryDate.hideDatePicker();
						entryDate.getTextBox().selectAll();
					}
				});
			}
		});
	}

	public void onRemove(ClickEvent event) {
		remove.setEnabled(false);
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmDeleteAction(), new AonConfirmDialogCallback() {

			@Override
			public void onCancel() {
				remove.setEnabled(true);
			}

			@Override
			public void onAccept() {
				wizardContent.remove(new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						IAccountEntryWrapper wrapper = wizardContent.getEntryWrapper(); 
						if (getOptions().isSessionLogTabVisible() &&
							wizardContent.getMainEntry().getId() != null) {
							wizardContent.getMainEntry().setId(wizardContent.getMainEntry().getId() * -1);
							sessionLog.addDeleted(wizardContent.getEntryWrapper());
						}
						remove.setEnabled(true);
						reset();
						if (getOptions().hasExternalCallback() ) {
							getOptions().getExternalCallback().onRemove(wrapper);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						remove.setEnabled(true);
						showError(caught.getMessage());
					}
				});
			}
		});
	}

	public void onAudit(ClickEvent event) {
		AonAuditDialog dialog = new AonAuditDialog();
		dialog.show(wizardContent.getMainEntry());
	}

	private void selectEntry(final Integer id) {
		if (id != null) {
			ACCOUNT_ENTRY_SERVICE.getAccountEntry(getOptions().getDomainName(),
					getOptions().getDomain(),getOptions().getUser(), id ,
				new AsyncCallback<AccountEntry>() {
					@Override
					public void onSuccess(AccountEntry result) {
						if (result != null) {
							selectEntry(id , new AccountEntryWrapper(result) );
						} else {
							showError("Asiento no encontrado");
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						showError(caught.getMessage());
					}
				});
		} else {
			showError("Asiento no encontrado");
		}
	}
	
	private void selectEntry(final Integer id,final IAccountEntryWrapper wrp) {
		boolean newAndEmpty = false;
		if (!isDirty() ) {
			newAndEmpty = wrp.getAccountEntry().getId() == null &&
					(wrp.getAccountEntry().getDetails() == null
					|| wrp.getAccountEntry().getDetails().size() == 0
					|| (wrp.getAccountEntry().getDetails().size() == 1
					&& wrp.getAccountEntry().getDetails().get(0).getAccount() == null
					&& wrp.getAccountEntry().getDetails().get(0).getDebit() == 0
					&& wrp.getAccountEntry().getDetails().get(0).getCredit() == 0)				
							);
		}
		if (isDirty() && !newAndEmpty && getOptions().isSessionLogTabVisible()) { 
			sessionLog.addSuspended(wizardContent.getEntryWrapper());
		}
		if (getOptions().isPreviewTabVisible()) {
			previewPanel.clearPreview();;
		}
		
		selectWizardContent(id,wrp); 
	}
	
	public void recoverDeletedEntry(final IAccountEntryWrapper wrp) {
		if (wrp.getAccountEntry().getEntryType() != AccountEntryType.MANUAL) {
			showError("No se puede recuperar un asiento borrado");
		} else {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.recoverEntry(),
					new AonConfirmDialogCallback() {
				
				@Override
				public void onCancel() {
				}
				
				@Override
				public void onAccept() {
					wrp.getAccountEntry().setId(null);
					wrp.getAccountEntry().setJournal(null);
					for (AccountEntryDetail aed : wrp.getAccountEntry().getDetails()) {
						aed.setId(null);
					}
					selectWizardContent(null, wrp);
				}
			});
		}
	}

	public void onTypeChanged(ChangeEvent event) {
		final IAccountEntryWrapper wrp = this.wizardContent.getEntryWrapper();
		final IContentAttachCallback cbk = new IContentAttachCallback() {
			@Override
			public void onAttach() {
				AccountEntryModule.this.wizardContent.reset(wrp.getAccountEntry(),new ISelectionCallback() {
					
					@Override
					public void onSuccess() {
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							public void execute() {
								AccountEntryModule.this.wizardContent.setFocus(true);
							}
						});
					}
					
					@Override
					public void onFailure() {
						invalidateModule("Error inesperado");
					}
				});
			}
		};
		EntryType type = EntryType.values()[ entryType.getSelectedIndex()];
		type.visit( new IEntryTypeVisitor() {
			@Override
			public void visitManual() {
				new Manual(moduleCallback).attach(cbk);
			}
			@Override
			public void visitInvoice() {
				createAndAttachInvoicePanel(cbk);
			}
			@Override
			public void visitSalary() {
				SalaryEntryPanel panel = new SalaryEntryPanel(moduleCallback);
				panel.attach(cbk);
			}
			@Override
			public void visitFinance() {
				FinanceEntryPanel panel = new FinanceEntryPanel(moduleCallback);
				panel.attach(cbk);
			}
		});
	}
	
	// ---------------------------------------------------------------- ACTION
	private void reset() {
		if (getOptions().isPreviewTabVisible()) {
			previewPanel.clearPreview();
		}
		final MutableInt first = new MutableInt(0);
		ISelectionCallback selectionCallback = new ISelectionCallback() {
			
			@Override
			public void onSuccess() {
				syncCurrent();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						if (first.getValue() == 0) {
							wizardContent.setFocus(true);
						} else {
							entryDate.setFocus(true);
							entryDate.hideDatePicker();
							entryDate.getTextBox().selectAll();
						}
					}
				});
			}
			
			@Override
			public void onFailure() {
			}
			
		};
		if (this.wizardContent == null) {
			first.setValue(1);
			Manual manual = new Manual(moduleCallback);
			manual.attach(null);
			this.wizardContent = manual;	
		}
		this.wizardContent.reset( getEntryBase(), selectionCallback  );
		
	}
	
	private void createAndAttachInvoicePanel(IContentAttachCallback wizardCbk) {
		InvoicePanel panel = new InvoicePanel(moduleCallback);
		panel.addSelectionHandler(new SelectionHandler<AccountingInvoice>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingInvoice> event) {
				if (getOptions().isSessionLogTabVisible()) {
					IWizardContent wc = AccountEntryModule.this.wizardContent;
					if (wc != null && wc.getEntryWrapper() != null ) {
						sessionLog.addSuspended(wc.getEntryWrapper());
					}
				}
				panel.select(event.getSelectedItem(), new ISelectionCallback() {
					@Override
					public void onSuccess() {
						syncCurrent();
					}
					
					@Override
					public void onFailure() {
						invalidateModule("");
					}
				});
			}
		});
		panel.addSelectionHandler(new AccountEntrySelectionHandler() {
			
			@Override
			public void onSelection(AccountEntrySelectionEvent event) {
				if (getOptions().isSessionLogTabVisible()) {
					IWizardContent wc = AccountEntryModule.this.wizardContent;
					if (wc != null && wc.getEntryWrapper() != null ) {
						sessionLog.addSuspended(wc.getEntryWrapper());
					}
				}
				selectEntry(event.getSelectedItem()==null?null:event.getSelectedItem().getId());
			}
		});
		panel.attach(wizardCbk);
	}

	private AccountEntry getEntryBase() {
		if (base == null) {
			EnterpriseActivity ea = getOptions().getConfiguration().getMainActivity();
			Integer activity = (ea==null?null:ea.getId());
			AccountPeriod period = getOptions().getConfiguration().accounting().getDefaultAccountPeriod();
			Integer periodId = (period == null? null : period.getId());
			base = new AccountEntry()
					.setPeriod(periodId)
					.setDomain(getOptions().getDomain())
					.setConfidential(false)
					.setEntryDate(new Date())
					.setActivity(activity)
					.setDirty(false);
		}
		return base;
	}

	private void selectWizardContent( final Integer id, final IAccountEntryWrapper wrp) {
		final IContentAttachCallback wizardCbk = new IContentAttachCallback() {
			@Override
			public void onAttach() {
				AccountEntryModule.this.wizardContent.select(id,wrp,new ISelectionCallback() {
					
					@Override
					public void onSuccess() {
						syncCurrent();
					}
					
					@Override
					public void onFailure() {
						invalidateModule("");
					}
				});
			}
		};
		
		wrp.getAccountEntry().getEntryType().visit(wrp.getAccountEntry(), new  AccountEntryTypeVisitorAdapter() {
			@Override
			public void visitExpenseInvoice(AccountEntry entry) {
				entryType.setSelectedIndex(EntryType.INVOICE.ordinal());
				createAndAttachInvoicePanel(wizardCbk);
			}

			@Override
			public void visitSalesInvoice(AccountEntry entry) {
				entryType.setSelectedIndex(EntryType.INVOICE.ordinal());
				createAndAttachInvoicePanel(wizardCbk);
			}
			@Override
			public void visitPurchaseInvoice(AccountEntry entry) {
				entryType.setSelectedIndex(EntryType.INVOICE.ordinal());
				createAndAttachInvoicePanel(wizardCbk);
			}
			
			
			private void visitManual() {
				entryType.setSelectedIndex(EntryType.MANUAL.ordinal());
				new Manual(moduleCallback).attach(wizardCbk);
			}
			
			@Override public void visitManual(AccountEntry entry) {visitManual();}
			@Override public void visitOpening(AccountEntry entry) {visitManual();}
			@Override public void visitClosing(AccountEntry entry) {visitManual();}
			@Override public void visitOperating(AccountEntry entry) {visitManual();}
			@Override public void visitInvestmentInvoice(AccountEntry entry) {visitManual();}
			@Override public void visitExpenses(AccountEntry entry) {visitManual();}
			@Override public void visitSalary(AccountEntry entry) {visitManual();}
			@Override public void visitTax(AccountEntry entry) {visitManual();}
			@Override public void visitLoan(AccountEntry entry) {visitManual();}
			@Override public void visitStockVariation(AccountEntry entry) {visitManual();}
			@Override public void visitAmortization(AccountEntry entry) {visitManual();}
			@Override public void visitSocialInsurance(AccountEntry entry) {visitManual();}
			@Override public void visitLoanFee(AccountEntry entry) {visitManual();}
			@Override public void visitSocialInsuranceAdjust(AccountEntry entry) {visitManual();}
			@Override public void visitLeasing(AccountEntry entry) {visitManual();}
			@Override public void visitLeasingFee(AccountEntry entry) {visitManual();}
			
			private void visitFinance() {
				entryType.setSelectedIndex(EntryType.FINANCE.ordinal());
				FinanceEntryPanel panel = new FinanceEntryPanel(moduleCallback);
				panel.attach(wizardCbk);
			}
			
			@Override public void visitFinance(AccountEntry entry) {visitFinance();}
			@Override public void visitPayment(AccountEntry entry) {visitFinance();}
			@Override public void visitCollection(AccountEntry entry) {visitFinance();}
			@Override public void visitReturnedPayment(AccountEntry entry) {visitFinance();}
			@Override public void visitReturnedCollection(AccountEntry entry) {visitFinance();}
		});
			
	}
	
	public void setWizardContent(IWizardContent content,IContentAttachCallback cbk) {
		this.wizardContent = content;
		wizardPanel.setWidget(this.wizardContent);
		if (cbk != null) cbk.onAttach();
	}

	private void showError(String msg) {
		if (AonStringUtils.isBlank(msg)) {
			msg = "Se ha producido un error no codificado.";
		}
		toolbar.showErrorMessage(msg);
	}
	
	private void hideErrors() {
		toolbar.hideMessages();
	}
	
	public static AccountEntryWrapper[] getWrapperArray(LinkedList<AccountEntry> entries ) {
		LinkedList<AccountEntryWrapper> list = new LinkedList<AccountEntryWrapper>();
		for (AccountEntry entry : entries) {
			list.add(new AccountEntryWrapper(entry));
		}
		return list.toArray(new AccountEntryWrapper[list.size()]);
	}
	
	public static AccountEntryWrapper[] getWrapperArray(AccountEntry[] entries ) {
		LinkedList<AccountEntryWrapper> list = new LinkedList<AccountEntryWrapper>();
		for (AccountEntry entry : entries) {
			list.add(new AccountEntryWrapper(entry));
		}
		return list.toArray(new AccountEntryWrapper[list.size()]);
	}

	private void ensurePreviewTab() {
		openFootPanelIfNeeded();
		tabLayout.selectTab(getPreviewTabIndex());
	}

	public void onPreview(IAccountEntryWrapper wrp ) {
		if (getOptions().isPreviewTabVisible()) {
			if (wrp.getAccountEntry() != null 
					&& wrp.getAccountEntry().getDetails() != null 
					&& !wrp.getAccountEntry().getDetails().isEmpty() 
					&& AonStringUtils.isNotBlank(wrp.getAccountEntry().getDetails().get(0).getAccountCode()) ) {
				ensurePreviewTab();
				previewPanel.preview( wrp );
			}
		}
	}

	public void onPreview(IAccountEntryWrapper ... wrapper ) {
		if (getOptions().isPreviewTabVisible()) {
			if (wrapper != null 
					&& wrapper.length > 0
					&& wrapper[0].getAccountEntry() != null 
					&& wrapper[0].getAccountEntry().getDetails() != null 
					&& !wrapper[0].getAccountEntry().getDetails().isEmpty() 
					&& AonStringUtils.isNotBlank(wrapper[0].getAccountEntry().getDetails().get(0).getAccountCode()) ) {
				ensurePreviewTab();
				previewPanel.preview( wrapper );
			}
		}
	}
	public void onClearSessionLog() {
		if (getOptions().isPreviewTabVisible()) {
			previewPanel.clearPreview();
		}
	}
	
	public void onError(String msg) {
		showError(msg);
	}

	public void onHideMessages() {
		hideErrors();
	}

	public AonDateBox getEntryDateBox() {
		return entryDate;
	}
	public Date getEntryDate() {
		return entryDate.getValue();
	}
	public void changeEntryDate(Date date) {
		entryDate.setValue( date , true);
	}

	public Integer getActivity() {
		return AonNumberUtils.toInteger(activity.getSelectedValue());
	}
	public ListBox getActivityBox() {
		return activity;
	}
	public CheckBox getConfidentialBox() {
		return confidential;
	}
	
	public void addExtraInfo( String htmlText) {
		if (getOptions().isExtraInfoTabVisible()) {
			openFootPanelIfNeeded();
			tabLayout.selectTab(getExtraInfoTabIndex());
			HTMLPanel panel = new HTMLPanel(htmlText);
			extraInfoContainer.setWidget(panel);
			extraInfoContainer.scrollToTop();
		}
	}
	
	public void addExtraInfo( Widget widget) {
		if (getOptions().isExtraInfoTabVisible()) {
			openFootPanelIfNeeded();
			tabLayout.selectTab(getExtraInfoTabIndex());
			extraInfoContainer.setWidget(widget);
			extraInfoContainer.scrollToTop();
		}
	}

	public void onBack(ClickEvent event) {
		if (getOptions().hasExternalCallback()) {
			getOptions().getExternalCallback().onExit();
		}
	
	}
	
	private void onAttachment(ClickEvent event) {
		wizardContent.addAttach(new AsyncCallback<IAccountEntryWrapper>() {
			
			@Override
			public void onSuccess(IAccountEntryWrapper result) {
				selectWizardContent(null, result);						
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showError(caught.getMessage());
			}
		});
	}
	
	private void onRemoveAttachment(ClickEvent event) {
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm( AON.MSG.removeAttachConfirmation() , new AonConfirmDialogCallback() {
			
			@Override
			public void onCancel() {
			}
			
			@Override
			public void onAccept() {
				wizardContent.removeAttach( new AsyncCallback<IAccountEntryWrapper>() {
					
					@Override
					public void onSuccess(IAccountEntryWrapper result) {
						selectWizardContent(null, result);						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						showError(caught.getMessage());
					}
				});
			}
		});
	}
	
	private void onSpecialUpdate(ClickEvent event) {
		final IAccountEntryWrapper wrapper = wizardContent.getEntryWrapper();
		ACCOUNT_ENTRY_SERVICE.getAvailableAccountEntryUpdates(getOptions().getDomainName(),
					getOptions().getDomain(),getOptions().getUser(), wrapper, new AsyncCallback<LinkedList<AccountEntryUpdate>>() {
						
				@Override
				public void onSuccess(LinkedList<AccountEntryUpdate> result) {
					if (result == null || result.isEmpty()) {
						AonMessageDialog.show("AVISO", "No se pueden realizar modificaciones adicionales");
					}  else {
						paintOperations(  result );		
					}
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showError(caught.getMessage());
				}
		});
	}


	public void onDuplicate() {
		final AccountEntry orig = wizardContent.getMainEntry();
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption(AON.MSG.duplicate());
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonScrollArea());
		
		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.addStyleName(AON.CSS.aonWidthAll());
		int row = 0;
		
		if (orig.getEntryType() != AccountEntryType.MANUAL) {
			table.setWidget(row,0,new Label(AON.MSG.manualEntryGeneration()));
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonColorRed());
			table.getCellFormatter().addStyleName(row, 0, AON.CSS.aonTextCenter());
			row++;
		}
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.period()));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		AccountPeriodBox period = new AccountPeriodBox();
		period.fill(getOptions().getConfiguration().accounting().getPeriods());
		period.select(orig.getPeriod());
		table.setWidget(row,1,period);
		row++;

		table.setWidget(row,0,new InlineLabel(AON.MSG.accountEntryDate()));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		final AonDateBox issueDate = new AonDateBox();
		issueDate.setValue(orig.getEntryDate());
		table.setWidget(row,1,issueDate);
		row++;


		table.setWidget(row,0,new InlineLabel(AON.MSG.concept()));
		final CheckBox mantainConcept = new CheckBox();
		mantainConcept.setStyleName(AON.CSS.aonNowrap());
		mantainConcept.setValue(true);
		
		final AonTextBox concept = new AonTextBox();
		concept.setVisibleLength(25);
		concept.setMaxLength(64);
		concept.setEnabled(false);
		concept.setValue("Mantener original");
		
		FlowPanel flowPanel0 = new FlowPanel();
		flowPanel0.setStyleName(AON.CSS.aonNowrap() );
		flowPanel0.add(mantainConcept);
		flowPanel0.add(concept);
		mantainConcept.addClickHandler( e -> {
			if (mantainConcept.getValue().booleanValue()) {
				concept.addStyleName(AON.CSS.aonColorGray());
				concept.setEnabled(false);
				concept.setValue("Mantener original");
			} else {
				concept.removeStyleName(AON.CSS.aonColorGray());
				concept.setEnabled(true);
				concept.setValue(null);
			}
		});
		table.setWidget(row,1,flowPanel0);
		row++;
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.document()));
		final CheckBox mantainDocument = new CheckBox();
		mantainDocument.setStyleName(AON.CSS.aonNowrap());
		mantainDocument.setValue(true);
		final AonTextBox document = new AonTextBox();
		document.setVisibleLength(25);
		document.setMaxLength(32);
		document.setEnabled(false);
		document.setValue("Mantener original");
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setStyleName(AON.CSS.aonNowrap() );
		flowPanel.add(mantainDocument);
		flowPanel.add(document);
		mantainDocument.addClickHandler( e -> {
			if (mantainDocument.getValue().booleanValue()) {
				document.addStyleName(AON.CSS.aonColorGray());
				document.setEnabled(false);
				document.setValue("Mantener original");
			} else {
				document.removeStyleName(AON.CSS.aonColorGray());
				document.setEnabled(true);
				document.setValue(null);
			}
		});
		table.setWidget(row,1,flowPanel);
		row++;

		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		final CheckBox invert = new CheckBox( AON.MSG.invertData());
		table.setWidget(row,0,new Label());
		table.setWidget(row,1,invert);
		rootPanel.add( table );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	buttons.addStyleName(AON.CSS.aonMarginTop());
    	buttons.addStyleName(AON.CSS.aonMarginBottom());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(event -> {
			okButton.setEnabled(false);
			AccountEntry dupl = new AccountEntry() 
				.setPeriod(period.getValue())
				.setDomain(orig.getDomain())
				.setEntryDate(issueDate.getValue())
				.setEntryType(AccountEntryType.MANUAL)
				.setActivity(orig.getActivity())
				.setSecurityLevel(orig.getSecurityLevel());
			orig.getDetails()
			.stream()
			.map( aed -> new AccountEntryDetail()
				.setDomain(aed.getDomain())
				.setAccount(aed.getAccount())
				.setAccountCode(aed.getAccountCode())
				.setAccountDescription(aed.getAccountDescription())
				.setLine(aed.getLine())
				.setConcept(mantainConcept.getValue().booleanValue()?aed.getConcept(): concept.getValue())
				.setDebit(invert.getValue().booleanValue()?aed.getCredit():aed.getDebit())
				.setCredit(invert.getValue().booleanValue()?aed.getDebit():aed.getCredit())
				.setBalancingAccount(aed.getBalancingAccount())
				.setBalancingAccountCode(aed.getBalancingAccountCode())
				.setBalancingAccountDescription(aed.getBalancingAccountDescription())
				.setDocumentNumber(mantainDocument.getValue().booleanValue()?aed.getDocumentNumber(): document.getValue()))
			.forEach( aed -> dupl.addDetail(aed));
			AccountEntryWrapper wrp = new AccountEntryWrapper(dupl);
			selectEntry(null, wrp);
				dialog.hide();
			});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(event -> {
			cancelButton.setEnabled(false);
			dialog.hide();
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		dialog.add( rootPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(() -> period.setFocus(true));		
		
	}
	
	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler(new MinimizeHandler() {
			
			@Override
			public void onMinimize(MinimizeEvent event) {
				minimizedByUser = true;
				closeFootPanel();
			}
		});
		footPanel.addMaximizeHandler(new MaximizeHandler() {
			
			@Override
			public void onMaximize(MaximizeEvent event) {
				openFootPanel();
			}
		});
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		
		footPanel.add(tabLayout);
		int tabIndex = 0;
		
		if (getOptions().isSessionLogTabVisible()) {
			sessionLog = new SessionLog();
			sessionLog.addSelectionHandler( new SelectionHandler<IAccountEntryWrapper>() {
				
				@Override
				public void onSelection(SelectionEvent<IAccountEntryWrapper> event) {
					final AccountEntry entry = event.getSelectedItem().getAccountEntry();
					selectEntry(entry.getId(),event.getSelectedItem());
				}
			});
			tabLayout.add(sessionLog, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.sessionLog(), AON.CSS.aonIconHistory()));
			sessionLogTabIndex = tabIndex;
			tabIndex++;
		}

		if (getOptions().isPreviewTabVisible()) {
			previewPanel = new AccountPreviewPanel( getOptions() );
			tabLayout.add(previewPanel, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.previewAccountEntry(), AON.CSS.aonIconPreview()));
			previewTabIndex = tabIndex;
			tabIndex++;
		}

		if (getOptions().isJournalTabVisible()) {
			SimpleLayoutPanel journalPanelContainer = new SimpleLayoutPanel();
			AccountingReportModuleOptions reportOptions = new AccountingReportModuleOptions()
					.setDomainName(getOptions().getDomainName())
					.setDomain(getOptions().getDomain())
					.setUser(getOptions().getUser())
					.setConfiguration(getOptions().getConfiguration())
					.setDontRunOnOpen(true);
			journalPanel = new JournalPanelReport(reportOptions);
			journalPanel.addSelectionHandler(new AccountEntrySelectionHandler() {
				@Override
				public void onSelection(AccountEntrySelectionEvent event) {
					final AccountEntry entry = event.getSelectedItem();
					AccountEntryModule.this.selectEntry(entry.getId());
				}
			});
			journalPanelContainer.setWidget(journalPanel);
			tabLayout.add(journalPanelContainer, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.journalBook(), AON.CSS.aonIconBook()));
			journalTabIndex = tabIndex;
			tabIndex++;
		}
		
		if (getOptions().isExtraInfoTabVisible()) {
			extraInfoContainer = new ScrollPanel();
			tabLayout.add(extraInfoContainer, TABLAYOUT_FOLDER_TEMPLATE.tab(AON.MSG.additionalData(), AON.CSS.aonIconInfo()));
			extraInfoTabIndex = tabIndex;
			tabIndex++;
		}

		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				minimizedByUser = false;
				openFootPanelIfNeeded();
				if (AonNumberUtils.equals( event.getSelectedItem(),journalTabIndex)) {
					journalPanel.run();
				}
			}
		});
		return footPanel; 
	}
	
	private AonToolbar getToolbarPanel() {
		AonToolbar tb = new AonToolbar(AON.MSG.accountEntries());
		if (getOptions().isBackButtonVisible()) {
			back = new AonToolbarButton( AON.MSG.backAction(), AON.CSS.aonIconBack() );
			back.addClickHandler(event -> onBack(event));
			tb.add(back);
		}

		if (getOptions().isJournalTabVisible()) {
			search = new AonToolbarButton( AON.MSG.searchAction(), AON.CSS.aonIconSearch() );
			search.addClickHandler(event -> onSearch(event));
			tb.add(search);
		}

		reset = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() ,AonButton.AON_ACCESSKEY_RESET); 
		reset.addClickHandler(event -> onReset(event));
		tb.add(reset);

		accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() ,AonButton.AON_ACCESSKEY_SAVE);
		accept.addClickHandler(event -> onAccept(event));
		tb.add(accept);

		remove = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete(),AonButton.AON_ACCESSKEY_DELETE);
		remove.addClickHandler(event -> onRemove(event));
		tb.add(remove);
		
		specialUpdate  = new AonToolbarButton( AON.MSG.specialUpdate(), AON.CSS.aonIconDataSettings() );
		specialUpdate.addClickHandler(event -> onSpecialUpdate(event));
		tb.add(specialUpdate);

		attachment  = new AonToolbarButton( AON.MSG.attachDocument(), AON.CSS.aonIconAttach());
		attachment.addClickHandler(event -> onAttachment(event));
		tb.add(attachment);

		removeAttachment  = new AonToolbarButton( AON.MSG.attachRemoveDocument(), AON.CSS.aonIconNoAttach());
		removeAttachment.addClickHandler(event -> onRemoveAttachment(event));
		tb.add(removeAttachment);

		duplicate = new AonToolbarButton( AON.MSG.duplicate(), AON.CSS.aonIconCopy() );
		duplicate.addClickHandler(event -> onDuplicate());
		tb.add(duplicate);

		audit = new AonToolbarButton( AON.MSG.audit(), AON.CSS.aonIconAudit() );
		audit.addClickHandler(event -> onAudit(event));
		tb.add(audit);

		return tb;

	}
	
	private Widget getEntryHeader() {
		entryHeader = new FlowPanel();
		entryHeader.setStyleName(AON.CSS.aonFlexBlock());
		entryHeader.addStyleName(AON.CSS.aonScrollArea());
		
		InlineLabel fiscalYear = new InlineLabel(AON.MSG.fiscalYear());
		fiscalYear.setStyleName(AON.CSS.aonFlexLabel());				
		entryHeader.add( fiscalYear );
		
		
		period = new AccountPeriodBox();
		period.setStyleName(AON.CSS.aonMarginRight());
		period.setTabIndex(1);
		period.addChangeHandler( event -> onChangeAccountPeriod(event));
		entryHeader.add( period );

		InlineLabel dateLabel = new InlineLabel(AON.MSG.date());
		dateLabel.setStyleName(AON.CSS.aonFlexLabel());				
		entryHeader.add( dateLabel );
		
		entryDate = new AonDateBox();
		entryDate.addStyleName(AON.CSS.aonMarginRight());
		entryDate.setTabIndex(2);
		entryDate.addValueChangeHandler(this::onChangeEntryDate);
		entryHeader.add( entryDate );

		InlineLabel typeLabel = new InlineLabel(AON.MSG.type());
		typeLabel.setStyleName(AON.CSS.aonFlexLabel());				
		entryHeader.add( typeLabel );
		

		entryType = new ListBox();
		entryType.setStyleName(AON.CSS.aonMarginRight());
		entryType.setTabIndex(3);
		entryType.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onTypeChanged(event);
			}
		});
		entryHeader.add( entryType );
		
		FlowPanel idsContainer = new FlowPanel();
		idsContainer.setStyleName(AON.CSS.aonFlexGrow1());
		idsContainer.addStyleName(AON.CSS.aonTextCenter());
		journal = new InlineLabel();
		journal.setStyleName(AON.CSS.aonFlexLabel());
		id = new InlineLabel();
		id.setStyleName(AON.CSS.aonFlexLabel());
		idsContainer.add( journal );
		idsContainer.add( id );
		entryHeader.add( idsContainer );

		activity = new ListBox();
		activity.setWidth("120px");
		activity.setVisible(false);
		activity.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				onChangeActivity(event);
			}
		});
		entryHeader.add( activity );
		
		confidential = new CheckBox( AON.MSG.confidential());
		confidential.setStyleName(AON.CSS.aonNowrap());
		confidential.addStyleName(AON.CSS.aonMarginRight());
		confidential.addStyleName(AON.CSS.aonMarginLeft());
		confidential.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onChangeConfidential(event);
			}
		});
		entryHeader.add( confidential );
		
		commentsButton = new AonTableButton(AON.MSG.comments(), AON.CSS.aonIconComments() );
		commentsButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				onComments(event);
			}
		});
		entryHeader.add( commentsButton );
		
		return entryHeader;
	}
	
	protected void paintOperations(LinkedList<AccountEntryUpdate> updates ) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption(AON.MSG.specialUpdate());
		dialog.setWidth("500px");
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonScrollArea());
		AonDisplayTable table = new AonDisplayTable();
		rootPanel.add(table);
		table.addStyleName(AON.CSS.aonWidthAll());
		for (AccountEntryUpdate update : updates ) {
			AonDisplayTableRow updatePanel =  table.addRow();
			InlineLabel descLabel = new InlineLabel(update.getDescription());
			updatePanel.addCell(descLabel,AON.CSS.aonFlexGrow1());

			IAccountEntryUpdateVisitor visitor = new IAccountEntryUpdateVisitor() {

				@Override
				public IAccountEntryWrapper visitManualType(IAccountEntryWrapper wrapper) {
					updatePanel.addCell(new InlineLabel());
					paintToggleButton( false );
					return wrapper;
				}

				@Override
				public IAccountEntryWrapper visitOpeningType(IAccountEntryWrapper wrapper) {
					updatePanel.addCell(new InlineLabel());
					paintToggleButton( false );
					return wrapper;
				}

				@Override
				public IAccountEntryWrapper visitSecurityLevel(IAccountEntryWrapper wrapper) {
					updatePanel.addCell(new InlineLabel());
					paintToggleButton( wrapper.getAccountEntry().isConfidential());
					return wrapper;
				}

				@Override
				public IAccountEntryWrapper visitInvestment(IAccountEntryWrapper wrapper) {
					updatePanel.addCell(new InlineLabel());
					if (wrapper instanceof AccountingInvoice) {
						AccountingInvoice ai = (AccountingInvoice) wrapper;
						paintToggleButton( ai.getInvoice().isInvestment());
					}
					return wrapper;
				}


				@Override
				public IAccountEntryWrapper visitService(IAccountEntryWrapper wrapper) {
					updatePanel.addCell(new InlineLabel());
					if (wrapper instanceof AccountingInvoice) {
						AccountingInvoice ai = (AccountingInvoice) wrapper;
						paintToggleButton( ai.getInvoice().isService());
					}
					return wrapper;
				}

				@Override
				public IAccountEntryWrapper visitVatAccrualPayment(IAccountEntryWrapper wrapper) {
					updatePanel.addCell(new InlineLabel());
					if (wrapper instanceof AccountingInvoice) {
						AccountingInvoice ai = (AccountingInvoice) wrapper;
						paintToggleButton( ai.getInvoice().isVatAccrualPayment());
					}
					return wrapper;
				}
				
				@Override
				public IAccountEntryWrapper visitTaxDate(IAccountEntryWrapper wrapper) {
					if (wrapper instanceof AccountingInvoice) {
						AccountingInvoice ai = (AccountingInvoice) wrapper; 
						AonDateBox taxDate = new AonDateBox();
						taxDate.setValue(ai.getInvoice().getTaxDate());
						taxDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
							
							@Override
							public void onValueChange(ValueChangeEvent<Date> event) {
								ai.getInvoice().setTaxDate(event.getValue());		
							}
						});
						updatePanel.addCell(taxDate);
						paintOkButton();
					} else {
						updatePanel.addCell(new InlineLabel());	
					}
					return wrapper;
				}

				@Override
				public IAccountEntryWrapper visitWithholdingType(IAccountEntryWrapper wrapper) {
					if (wrapper instanceof AccountingInvoice) {
						AccountingInvoice ai = (AccountingInvoice) wrapper; 
						if (ai.getWithholdingData() != null) {
							WithholdingTypeListBox withholdingType = new WithholdingTypeListBox();
							withholdingType.setValue(ai.getWithholdingData().getWithholdingType());
							withholdingType.addChangeHandler(new ChangeHandler() {
								@Override
								public void onChange(ChangeEvent event) {
									ai.setWithholdingType( withholdingType.getValue() );
								}
							});
							updatePanel.addCell(withholdingType);
							paintOkButton();
						} else {
							updatePanel.addCell(new InlineLabel());
						}
					} else {
						updatePanel.addCell(new InlineLabel());
					}
					return wrapper;
				}

				@Override
				public IAccountEntryWrapper visitActivity(IAccountEntryWrapper wrapper) {
					ListBox activity = new ListBox();
					activity.setVisible(true);
					activity.addItem("-- Todas --", "");
					activity.setSelectedIndex(0);
					int i = 1;
					for (EnterpriseActivity ea : getOptions().getConfiguration().getActivities()) {
						activity.addItem(ea.getDescription(), AonNumberUtils.toString( ea.getId()));
						if (AonNumberUtils.equals(wrapper.getAccountEntry().getActivity(), ea.getId())  ) {
							activity.setSelectedIndex(i);
						}
						i++;
					}
					activity.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							Integer act = AonNumberUtils.toInteger(activity.getSelectedValue());
							wrapper.getAccountEntry().setActivity( act );
						}
					});
					updatePanel.addCell(activity);
					paintOkButton();
					return wrapper;
				}


				@Override
				public IAccountEntryWrapper visitOperatingAccount(IAccountEntryWrapper wrapper) {
					if (wrapper instanceof AccountingInvoice) {
						AccountingInvoice ai = (AccountingInvoice) wrapper; 
						AonAccountBox account = new AonAccountBox(getOptions().getDomainName(),getOptions().getDomain(),getOptions().getUser());
						final LinkedList<Account> suggestedAccounts = new LinkedList<Account>();
						if (ai.getVats() != null && ai.getVats().size() > 0) {
							InvoiceVAT vat = ai.getVats().get(0);
							account.setValue(vat.getExpAccountId(), vat.getExpAccountCode(), vat.getExpAccountDescription());
							Account oldAccount = new Account();
							oldAccount.setId(vat.getExpAccountId());
							oldAccount.setCode( vat.getExpAccountCode() );
							oldAccount.setDescription(vat.getExpAccountDescription());
							suggestedAccounts.add(oldAccount);	
						}
						account.addSelectionHandler(new SelectionHandler<Account>() {
					
							@Override
							public void onSelection(SelectionEvent<Account> event) {
								if (event.getSelectedItem() != null) {
									if (suggestedAccounts .size() > 1) {
										suggestedAccounts.set(1,  event.getSelectedItem());
									} else {
										suggestedAccounts.add(event.getSelectedItem());
									}
									ai.setSuggestedAccounts(suggestedAccounts);
								}
							}
						});
						updatePanel.addCell(account);
						paintOkButton();
					} else {
						updatePanel.addCell(new InlineLabel());	
					}
					return wrapper;
				}
				
				private void paintToggleButton(boolean value ) {
					AonTableButton toggleButton = new AonTableButton( AON.MSG.accept() , value?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff());
					updatePanel.addCell(toggleButton);
					toggleButton.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							toggleButton.setEnabled(false);
							AonConfirmDialog acd = new AonConfirmDialog();
							acd.confirm("Confirma \"" + update.getDescription()+"\"?", new AonConfirmDialogCallback() {
								
								@Override
								public void onCancel() {
									toggleButton.setEnabled(true);
								}
								
								@Override
								public void onAccept() {
									ACCOUNT_ENTRY_SERVICE.updateSpecial(getOptions().getDomainName(),
											getOptions().getDomain(),getOptions().getUser(), update, wizardContent.getEntryWrapper(), new AsyncCallback<IAccountEntryWrapper>() {
												
												@Override
												public void onSuccess(IAccountEntryWrapper result) {
													dialog.hide();
													selectWizardContent(result.getAccountEntry().getId(), result);
												}
													
												@Override
												public void onFailure(Throwable caught) {
													showError(caught.getMessage());
												}
											});							
								}
							});
						}
					});
				}
				
				private void paintOkButton() {
					AonTableButton okButton = new AonTableButton( AON.MSG.accept() , AON.CSS.aonIconAccept());
					updatePanel.addCell(okButton);
					okButton.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							okButton.setEnabled(false);
							AonConfirmDialog acd = new AonConfirmDialog();
							acd.confirm("Confirma \"" + update.getDescription()+"\"?", new AonConfirmDialogCallback() {
								
								@Override
								public void onCancel() {
									okButton.setEnabled(true);
								}
								
								@Override
								public void onAccept() {
									ACCOUNT_ENTRY_SERVICE.updateSpecial(getOptions().getDomainName(),
											getOptions().getDomain(),getOptions().getUser(), update, wizardContent.getEntryWrapper(), new AsyncCallback<IAccountEntryWrapper>() {
												
												@Override
												public void onSuccess(IAccountEntryWrapper result) {
													dialog.hide();
													selectWizardContent(result.getAccountEntry().getId(), result);
												}
													
												@Override
												public void onFailure(Throwable caught) {			
													showError(caught.getMessage());
												}
											});							
								}
							});
						}
					});
				}
			};
			update.visit(visitor, wizardContent.getEntryWrapper());
			
		}
		FlowPanel buttonsPanel =  table.addFooterRow();
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginTop());
    	cancelButton.setText( AON.MSG.close());
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
			}
		});
    	buttonsPanel.add(cancelButton);

		dialog.add( rootPanel );
		dialog.center();
		dialog.show();
	}
}
