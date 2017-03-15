package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.ErrorPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.AccountBalancePanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.AccountStatementPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.JournalPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.SessionLog;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.FinanceEntryPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.IWizardContent;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoicePanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.Manual;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.SalaryEntryPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.WizardContentBase.ISelectionCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryTypeVisitorAdapter;
import com.esferalia.aon.occam.api.model.AccountEntryWrapper;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class AccountEntryModule extends MainEntryPoint {
	final static int ERROR_LOG_TAB = 0;
	final static int SESSION_LOG_TAB = 1;
	final static int BALANCES_TAB = 2;
	final static int STATEMENT_TAB = 3;
	final static int JOURNAL_TAB = 4;
	final static int EXTRA_INFO_TAB = 5;

	public final static int JOURNAL_PANEL_TAB_OFFSET = 1000000;

	static FiscalServiceAsync fiscalService;
	static CommonServiceAsync commonService;
	private AccountEntry base;

	interface AccountEntryModuleBinder extends
			UiBinder<Widget, AccountEntryModule> {
	}

	private static final AccountEntryModuleBinder BINDER = GWT
			.create(AccountEntryModuleBinder.class);
	
	public interface IAccountEntryModuleCallback {
		AccountEntryModule getModule();
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
	};

	AonConfiguration configuration;
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	DockLayoutPanel centerDockLayoutPanel;
	@UiField(provided = true)
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	Button reset;
	@UiField
	Button accept;
	@UiField
	Button remove;
	@UiField
	Button search;
	@UiField
	Button audit;
	@UiField
	SimpleLayoutPanel wizardPanel;
	@UiField
	HTMLPanel entryHeader;
	@UiField
	InlineLabel id;
	@UiField
	AccountPeriodBox period;
	@UiField
	DateBoxEx entryDate;
	@UiField
	ListBox activity;
	@UiField
	InlineLabel journal;
	@UiField
	CheckBox confidential;
	@UiField
	ListBox entryType;
	@UiField
	Button commentsButton;
	@UiField
	InlineLabel statusMsg;	
	@UiField
	MinimizePanel footPanel;
	@UiField
	TabLayoutPanel tabLayout;
	@UiField
	SessionLog sessionLog;
	JournalPanel journalPanel;
	@UiField
	SimpleLayoutPanel journalPanelContainer;
	@UiField
	AccountBalancePanel balancePanel;
	@UiField
	AccountStatementPanel statementPanel;
	@UiField
	ScrollPanel extraInfoContainer;
	
	@UiField
	SimplePanel errorsContainer;
	ErrorPanel errors;

	boolean minimizedByUser;
	
	
	private boolean periodErrorShown;
	private IWizardContent wizardContent;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);

		splitLayoutPanel = new SplitLayoutPanel(4);
		

//		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
//			public void onUncaughtException(Throwable e) {
//				showError("ERROR INESPERADO! [" + e.getMessage() + "]");
//			}
//		});
		        
		Widget ui = BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		activity.setTabIndex(-1);
		confidential.setTabIndex(-1);
		commentsButton.setTabIndex(-1);
		root.add(ui);
		tabLayout.setAnimationDuration(300);
		tabLayout.selectTab(BALANCES_TAB);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				minimizedByUser = false;
				openFootPanelIfNeeded();
			}
		});
		accept.setAccessKey('G');
		reset.setAccessKey('N');
		remove.setAccessKey('B');
		
		for (EntryType et : EntryType.values() ) {
			entryType.addItem(et.getDescription());
		}
		
		errors = new ErrorPanel();
		errorsContainer.setWidget(errors);

		entryDate.getTextBox().addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					entryDate.hideDatePicker();
		            wizardContent.setFocus(true);
		        }
			}
		});
		
		entryType.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				wizardContent.setFocus(true);
			}
			
		});
		
		commonService.getAonConfiguration(getCurrentDomainName(),
				getCurrentDomain(),
				new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						configuration = result;
						
						if (configuration.getPeriods() != null && !configuration.getPeriods().isEmpty()) {
							period.fill(configuration.getPeriods());
						} else {
							invalidateModule(AON.MSG.noActiveAccountPeriod());
						}

						if (configuration.hasActivities() && activity.getItemCount() == 0) {
							activity.setVisible(true);
							activity.addItem("-- Todas --", "");
							activity.setSelectedIndex(0);
							int i = 1;
							for (EnterpriseActivity ea : configuration.getActivities()) {
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
						confidential.setVisible(configuration.getUser().hasConfidentialityRole());
						
						journalPanel = new JournalPanel(getCurrentDomainName(), getCurrentDomain(), JOURNAL_PANEL_TAB_OFFSET, result);
						journalPanel.addSelectionHandler(new SelectionHandler<AccountEntry>() {
							@Override
							public void onSelection(SelectionEvent<AccountEntry> event) {
								final AccountEntry entry = event.getSelectedItem();
								selectEntry(entry.getId());
							}
						});
						journalPanelContainer.setWidget(journalPanel);
						reset();
					}

					@Override
					public void onFailure(Throwable caught) {
						invalidateModule(AON.MSG.noActiveAccountPeriod() + "[Interno: " + caught.getMessage()+ "]");
					}
				});
	}

	protected void invalidateModule(String msg) {
		errors = new ErrorPanel();
		showError(msg);
		errorsContainer.setWidget(errors);
		entryHeader.setVisible(false);
		search.setVisible(false);
		reset.setVisible(false);
		accept.setVisible(false);
		remove.setVisible(false);
		audit.setVisible(false);
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	// -------------------------------------------------------------- UiHandler

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		minimizedByUser = true;
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
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
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 3);
		splitLayoutPanel.animate(500);
	}

	
	@UiHandler("entryDate")
	void onChangeEntryDate(ValueChangeEvent<Date> event) {
		this.wizardContent.getMainEntry().setEntryDate(event.getValue());
		checkDate();
		refreshIdLabel();
		this.wizardContent.entryDateChanged(event.getValue());
	}

	@UiHandler("period")
	void onChangeAccountPeriod(ChangeEvent event) {
		Integer ap = AonNumberUtils.toInteger(period.getSelectedValue());
		this.wizardContent.getMainEntry().setPeriod(ap);
		checkDate();
		refreshIdLabel();
	}
	
	@UiHandler("activity")
	void onChangeActivity(ChangeEvent event) {
		Integer act = AonNumberUtils.toInteger(activity.getSelectedValue());
		this.wizardContent.getMainEntry().setActivity(act);
		refreshIdLabel();
		this.wizardContent.activityChanged(act);
	}

	@UiHandler("confidential")
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
				errors.hide();
			}
		}
	}

	@UiHandler("commentsButton")
	public void onComments(ClickEvent event) {
		final AonToast toast = new AonToast();
		
		FlowPanel commentPanel = new FlowPanel();
		commentPanel.setStyleName(AON.AON_CSS.aonHeightAll());
		commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		TextArea comment = new TextArea();
		comment.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				wizardContent.getMainEntry().setComments(event.getValue());
				styleCommentsButton();
				refreshIdLabel();
				toast.hide();
			}
		});
		comment.setText(wizardContent.getMainEntry().getComments());
		comment.setWidth("90%");
		comment.setHeight("5em");
		commentPanel.add(comment);

		toast.show(AON.MSG.comments(), commentPanel);
	}

	public boolean isNew() {
		return wizardContent.getMainEntry() == null || wizardContent.getMainEntry().getId() == null;
	}
	public boolean isDirty() {
		return wizardContent.getMainEntry().isDirty();
	}
	
	private void syncCurrent() {
		boolean canRemove = (!isNew() && (wizardContent.isUpdatable() || wizardContent.isRemovable()));
		boolean canEdit = isNew() || (!isNew() && wizardContent.isUpdatable());
		
		// Populate header values
		period.select(wizardContent.getMainEntry().getPeriod());
		entryDate.setValue(wizardContent.getMainEntry().getEntryDate());
		checkDate();
		confidential.setValue(wizardContent.getMainEntry().isConfidential());
		if (configuration.hasActivities()) {
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
		
		statusMsg.setText(AonStringUtils.EMPTY);
		statusMsg.removeStyleName(AON.AON_CSS.aonInfoMessage());
		
		if (!canEdit) {
			statusMsg.addStyleName(AON.AON_CSS.aonInfoMessage());
			if (!wizardContent.getMainEntry().isPeriodActive()) {
				if (!canRemove) {
					statusMsg.setText(AON.MSG.periodStatusWarning(AON.MSG.accountPeriodStatus(wizardContent.getMainEntry().getPeriodStatus())));
				} else {
					statusMsg.setText(AON.MSG.automaticEntryNoUpdateWarning());
				}
			} else {
				
				if (AonStringUtils.isEmpty(wizardContent.getNoUpdatableCause())) {
					statusMsg.setText(AON.MSG.entryNoUpdatable());	
				} else {
					statusMsg.setText(AON.MSG.entryNoUpdatable() + " ["+ wizardContent.getNoUpdatableCause() +"]");
				}
			}
		}
		refreshIdLabel();
		
		wizardContent.manageWidgets(canRemove,canEdit);

		// Enable/Disable header values
		period.setEnabled(canEdit);
		entryDate.setEnabled(canEdit);
		confidential.setEnabled(canEdit);
		entryType.setEnabled(canEdit);
		accept.setEnabled(canEdit);
		remove.setEnabled(canRemove);
		
		styleCommentsButton();
		errors = new ErrorPanel();
		errorsContainer.setWidget(errors);
	}

	public void refreshIdLabel() {
		journal.setText(AonMathUtils.toInt(wizardContent.getMainEntry()
				.getJournal()) == 0 ? AonStringUtils.EMPTY : AON.MSG.journal()
				+ AonStringUtils.SPACE + wizardContent.getMainEntry().getJournal());
		id.setText(
			(isNew()? "[NUEVO]": ("(" + wizardContent.getMainEntry().getId() + ") "))
			+ (!isNew() && wizardContent.getMainEntry().isDirty()?AonStringUtils.ASTERISK:AonStringUtils.EMPTY));
		if (wizardContent.getMainEntry().isDirty()) {
			id.addStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id.removeStyleName(AON.AON_CSS.aonColorRed());
		}
	}

	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(wizardContent.getMainEntry().getComments())) {
			commentsButton.addStyleName(AON.AON_CSS.aonIconComment());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconCommentRed());
		} else {
			commentsButton.addStyleName(AON.AON_CSS.aonIconCommentRed());
			commentsButton.removeStyleName(AON.AON_CSS.aonIconComment());
		}
	}

	@UiHandler("accept")
	public void onAccept(ClickEvent event) {
		accept.setEnabled(false);
		wizardContent.save(new AsyncCallback<AccountEntry[]>() {

			@Override
			public void onSuccess(AccountEntry[] result) {
				base = result[0];
				sessionLog.addSaved(AccountEntryModule.getWrapperArray(result));
				reset();
				
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						tabLayout.selectTab(SESSION_LOG_TAB);					}
				});

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						entryDate.setFocus(true);
						entryDate.hideDatePicker();
						entryDate.getTextBox().selectAll();
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				accept.setEnabled(true);
				showError(caught.getMessage());
			}
		});
	}

	@UiHandler("search")
	public void onSearch(ClickEvent event) {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
			splitLayoutPanel.setWidgetSize(footPanel,
					Window.getClientHeight() / 2);
			splitLayoutPanel.animate(500, new AnimationCallback() {

				@Override
				public void onLayout(Layer layer, double progress) {
				}

				@Override
				public void onAnimationComplete() {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						public void execute() {
							tabLayout.selectTab(JOURNAL_TAB);
							journalPanel.setFocus(true);
						}
					});
				}
			});
		} else {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				public void execute() {
					tabLayout.selectTab(JOURNAL_TAB);
					journalPanel.setFocus(true);
				}
			});
		}
	}

	@UiHandler("reset")
	public void onReset(ClickEvent event) {
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm("Nuevo?", "Nuevo",new ConfirmDialogCallback() {
			
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

	@UiHandler("remove")
	public void onRemove(ClickEvent event) {
		remove.setEnabled(false);
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {

			@Override
			public void onCancel() {
				remove.setEnabled(true);
			}

			@Override
			public void onAccept() {
				wizardContent.remove(new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						if (wizardContent.getMainEntry().getId() != null) {
							wizardContent.getMainEntry().setId(
									wizardContent.getMainEntry().getId() * -1);
							sessionLog.addDeleted(wizardContent.getEntryWrapper());
						}
						remove.setEnabled(true);
						reset();
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

	@UiHandler("audit")
	public void onAudit(ClickEvent event) {
		AuditDialog dialog = new AuditDialog();
		dialog.show(wizardContent.getMainEntry());
	}

	@UiHandler("sessionLog")
	public void onSelectJournal(SelectionEvent<IAccountEntryWrapper> event) {
		final AccountEntry entry = event.getSelectedItem().getAccountEntry();
		selectEntry(entry.getId(),event.getSelectedItem());
	}

	
	@UiHandler("statementPanel")
	public void onSelectStatement(SelectionEvent<Integer> event) {
		selectEntry(event.getSelectedItem());
	}
	private void addSelectionEvent(final InvoicePanel panel) {
		panel.addSelectionHandler(new SelectionHandler<AccountingInvoice>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingInvoice> event) {
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
	}
	private void selectEntry(final Integer id) {
		if (id != null) {
			fiscalService.getAccountEntry(getCurrentDomainName(),
				getCurrentDomain(), id ,
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
		if (isDirty() && !newAndEmpty) { 
			sessionLog.addSuspended(wizardContent.getEntryWrapper());
		}
		balancePanel.clearBalances();
		selectWizardContent(id,wrp); 
	}
	
	public void recoverDeletedEntry(final IAccountEntryWrapper wrp) {
		if (wrp.getAccountEntry().getEntryType() != AccountEntryType.MANUAL) {
			showError("No se puede recuperar un asiento borrado");
		} else {
			ConfirmDialog cd = new ConfirmDialog();
			cd.confirm(AON.MSG.recoverEntry(),
					new ConfirmDialogCallback() {
				
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

	@UiHandler("balancePanel")
	public void onSelectBalance(SelectionEvent<Integer> event) {
		showFullStatement(event.getSelectedItem());
	}
	
	@UiHandler("entryType")
	public void onTypeChanged(ChangeEvent event) {
		final IAccountEntryWrapper wrp = this.wizardContent.getEntryWrapper();
		final IContentAttachCallback cbk = new IContentAttachCallback() {
			@Override
			public void onAttach() {
				AccountEntryModule.this.wizardContent.reset(wrp.getAccountEntry(),new ISelectionCallback() {
					
					@Override
					public void onSuccess() {
						AccountEntryModule.this.wizardContent.setFocus(true);
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
		balancePanel.clearBalances();
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
		addSelectionEvent(panel);
		panel.attach(wizardCbk);
	}

	private AccountEntry getEntryBase() {
		if (base == null) {
			EnterpriseActivity ea = getConfiguration().getMainActivity();
			Integer activity = (ea==null?null:ea.getId());
			AccountPeriod period = getConfiguration().getDefaultAccountPeriod();
			Integer periodId = (period == null? null : period.getId());
			base = new AccountEntry()
					.setPeriod(periodId)
					.setDomain(AccountEntryModule.getCurrentDomain())
					.setConfidential(false)
					.setEntryDate(new Date())
					.setActivity(activity)
					.setDirty(false);
		}
		return base;
	}

	private void showFullStatement(Integer selectedItem) {
		tabLayout.selectTab(STATEMENT_TAB);
		Date from = DateUtils.getFirstDayOfYear(entryDate.getValue());
		statementPanel.show(
			new AccountStatementParams()
				.setAccount(selectedItem)
				.setFromDate(from)
				.setToDate(entryDate.getValue()));
	}

	public static interface IContentAttachCallback {
		void onAttach();
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
		
		wrp.getAccountEntry().getEntryType().visit(null, new  AccountEntryTypeVisitorAdapter() {
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
			@Override public void visitReturnedPayment(AccountEntry entry) {visitManual();}
			@Override public void visitReturnedCollection(AccountEntry entry) {visitManual();}
			
			private void visitFinance() {
				entryType.setSelectedIndex(EntryType.FINANCE.ordinal());
				FinanceEntryPanel panel = new FinanceEntryPanel(moduleCallback);
				panel.attach(wizardCbk);
			}
			
			@Override public void visitFinance(AccountEntry entry) {visitFinance();}
			@Override public void visitPayment(AccountEntry entry) {visitFinance();}
			@Override public void visitCollection(AccountEntry entry) {visitFinance();}
		});
			
	}
	
	public void setWizardContent(IWizardContent content,IContentAttachCallback cbk) {
		this.wizardContent = content;
		wizardPanel.setWidget(this.wizardContent);
		if (cbk != null) cbk.onAttach();
	}

	private void showError(String msg) {
		errors.showError(msg);
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 30) {
			splitLayoutPanel.setWidgetSize(footPanel,
					Window.getClientHeight() / 2);
			splitLayoutPanel.animate(500, new AnimationCallback() {

				@Override
				public void onLayout(Layer layer, double progress) {
				}

				@Override
				public void onAnimationComplete() {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						public void execute() {
							tabLayout.selectTab(ERROR_LOG_TAB);
						}
					});
				}
			});
		} else {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				public void execute() {
					tabLayout.selectTab(ERROR_LOG_TAB);
				}
			});
		}
	}
	
	public static AccountEntryWrapper[] getWrapperArray(AccountEntry[] entries ) {
		LinkedList<AccountEntryWrapper> list = new LinkedList<AccountEntryWrapper>();
		for (AccountEntry entry : entries) {
			list.add(new AccountEntryWrapper(entry));
		}
		return list.toArray(new AccountEntryWrapper[list.size()]);
	}
	
	private void ensureBalanceTab() {
		openFootPanelIfNeeded();
		tabLayout.selectTab(BALANCES_TAB);
	}

	public void onBalance(Account account) {
		if (account != null && account.getId() != null) {
			ensureBalanceTab();
			Date from = DateUtils.getFirstDayOfYear(entryDate.getValue());
			balancePanel.add(account, from, entryDate.getValue());
		}
	}

	public void onBalance(AccountEntry entry) {
		if (entry.getDetails() != null 
			&& !entry.getDetails().isEmpty() 
			&& entry.getDetails().get(0).getAccount() != null) {
			ensureBalanceTab();
			balancePanel.add(entry);
		}
	}
	public void onBalance(IAccountEntryWrapper wrp) {
		onBalance(wrp.getAccountEntry());
	}
	
	public void onPreview(IAccountEntryWrapper wrp) {
		if (wrp.getAccountEntry() != null 
			&& wrp.getAccountEntry().getDetails() != null 
			&& !wrp.getAccountEntry().getDetails().isEmpty() 
			&& wrp.getAccountEntry().getDetails().get(0).getAccount() != null) {
			ensureBalanceTab();
			balancePanel.preview( wrp );
		}
	}
	public void onPreview(IAccountEntryWrapper[] wrapperArray) {
		if (wrapperArray != null 
			&& wrapperArray.length > 0
			&& wrapperArray[0].getAccountEntry() != null 
			&& wrapperArray[0].getAccountEntry().getDetails() != null 
			&& !wrapperArray[0].getAccountEntry().getDetails().isEmpty() 
			&& wrapperArray[0].getAccountEntry().getDetails().get(0).getAccount() != null) {
			ensureBalanceTab();
			balancePanel.preview( wrapperArray );
		}
	}
	public void onClearSessionLog() {
		balancePanel.clearBalances();
	}
	
	public void onStatement(Integer accountId) {
		showFullStatement(accountId);
	};
	public void onError(String msg) {
		showError(msg);
	}
	public AonConfiguration getConfiguration() {
		return configuration;
	}

	public Date getEntryDate() {
		return entryDate.getValue();
	}
	public Integer getActivity() {
		return AonNumberUtils.toInteger(activity.getSelectedValue());
	}
	public void changeEntryDate(Date date) {
		entryDate.setValue( date , true);
	}

	public void addExtraInfo( String htmlText) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(EXTRA_INFO_TAB);
		HTMLPanel panel = new HTMLPanel(htmlText);
		extraInfoContainer.setWidget(panel);
		extraInfoContainer.scrollToTop();
	}
	
}
