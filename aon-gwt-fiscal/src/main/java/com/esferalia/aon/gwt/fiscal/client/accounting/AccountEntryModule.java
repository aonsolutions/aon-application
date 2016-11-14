package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;

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
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.IWizardContent;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.InvoicePanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.Manual;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.WizardContentBase.ISelectionCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryTypeVisitorAdapter;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
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

	static FiscalServiceAsync fiscalService;
	static CommonServiceAsync commonService;

	interface AccountEntryModuleBinder extends
			UiBinder<Widget, AccountEntryModule> {
	}

	private static final AccountEntryModuleBinder BINDER = GWT
			.create(AccountEntryModuleBinder.class);
	
	public static interface IAccountEntryModuleCallback {
		void attach(IWizardContent content,IContentAttchCallback wizardCbk);
		
		AonConfiguration getConfiguration();
		String getDomainName();
		int getDomainId();
		
		IWizardContent getWizardContent();
		
		void onRefreshId();
		void onBalance(Account account);
		void onBalance(AccountEntry entry);
		void onStatement(Integer accountId);
		void onError(String msg);
		void save(ClickEvent event);
		

		
		
	}
	
	private final IAccountEntryModuleCallback callback = new IAccountEntryModuleCallback() {
		
		@Override
		public AonConfiguration getConfiguration() {
			return configuration;
		}
		@Override
		public String getDomainName() {
			return getCurrentDomainName();
		};
		@Override
		public int getDomainId() {
			return getCurrentDomain();
		};
		@Override
		public IWizardContent getWizardContent() {
			return wizardContent;
		}

		@Override
		public void onRefreshId() {
			refreshIdLabel();
		}
		
		@Override
		public void onBalance(Account account) {
			openFootPanelIfNeeded();
			Date from = DateUtils.getFirstDayOfYear(entryDate.getValue());
			balancePanel.add(account, from, entryDate.getValue());			
		}

		@Override
		public void onBalance(AccountEntry entry) {
			if (entry.getDetails() != null 
				&& !entry.getDetails().isEmpty() 
				&& entry.getDetails().get(0).getAccount() != null) {
				openFootPanelIfNeeded();
				balancePanel.add(entry);			
			}
		}

		@Override
		public void onStatement(Integer accountId) {
			showFullStatement(accountId);
		};

		@Override
		public void onError(String msg) {
			showError(msg);
			
		}

		@Override
		public void save(ClickEvent event) {
			onAccept(event);
		}
		@Override
		public void attach(IWizardContent content,IContentAttchCallback cbk) {
			setWizardContent(content,cbk);
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
	CheckBox invoice;
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
	@UiField(provided = true)
	JournalPanel journalPanel;
	@UiField
	AccountBalancePanel balancePanel;
	@UiField
	AccountStatementPanel statementPanel;

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
		journalPanel = new JournalPanel(getCurrentDomainName(), getCurrentDomain());

		Widget ui = BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		confidential.setTabIndex(Integer.MAX_VALUE - 1);
		commentsButton.setTabIndex(Integer.MAX_VALUE);
		root.add(ui);
		tabLayout.setAnimationDuration(300);
		tabLayout.selectTab(BALANCES_TAB);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				openFootPanelIfNeeded();
			}
		});
		accept.setAccessKey('G');
		reset.setAccessKey('N');
		remove.setAccessKey('B');
		
		entryDate.getTextBox().addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_F9) {
					entryDate.hideDatePicker();
		            wizardContent.setFocus(true);
		        }
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
								if (ea.isPrincipal()) activity.setSelectedIndex(i);
								i++;
							}
						} else {
							activity.setVisible(false);
						}

						reset();
					}

					@Override
					public void onFailure(Throwable caught) {
						invalidateModule(AON.MSG.noActiveAccountPeriod());
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
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
		splitLayoutPanel.animate(500);
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
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
		splitLayoutPanel.animate(500);
	}

	
	@UiHandler("entryDate")
	void onChangeEntryDate(ValueChangeEvent<Date> event) {
		this.wizardContent.getAccountEntryWrapper().getAccountEntry().setEntryDate(event.getValue());
		checkDate();
		refreshIdLabel();
	}

	@UiHandler("period")
	void onChangeAccountPeriod(ChangeEvent event) {
		Integer ap = AonNumberUtils.toInteger(period.getSelectedValue());
		this.wizardContent.getAccountEntryWrapper().getAccountEntry().setPeriod(ap);
		checkDate();
		refreshIdLabel();
	}
	
	@UiHandler("activity")
	void onChangeActivity(ChangeEvent event) {
		Integer act = AonNumberUtils.toInteger(activity.getSelectedValue());
		this.wizardContent.getAccountEntryWrapper().getAccountEntry().setActivity(act);
		refreshIdLabel();
	}

	@UiHandler("confidential")
	void onChangeConfidential(ClickEvent event) {
		this.wizardContent.getAccountEntryWrapper().getAccountEntry().setConfidential(confidential.getValue());
		refreshIdLabel();
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
				wizardContent.getAccountEntryWrapper().getAccountEntry().setComments(event.getValue());
				styleCommentsButton();
				refreshIdLabel();
				toast.hide();
			}
		});
		comment.setText(wizardContent.getAccountEntryWrapper().getAccountEntry().getComments());
		comment.setWidth("90%");
		comment.setHeight("5em");
		commentPanel.add(comment);

		toast.show(AON.MSG.comments(), commentPanel);
	}

	private void syncCurrent() {
		boolean canRemove = (!wizardContent.isNew() && wizardContent.isUpdatable());
		boolean canEdit = wizardContent.isNew() || canRemove;
		
		// Populate header values
		period.select(wizardContent.getAccountEntryWrapper().getAccountEntry().getPeriod());
		entryDate.setValue(wizardContent.getAccountEntryWrapper().getAccountEntry().getEntryDate());
		confidential.setValue(wizardContent.getAccountEntryWrapper().getAccountEntry().isConfidential());
		journal.setText(AonMathUtils.toInt(wizardContent.getAccountEntryWrapper().getAccountEntry()
				.getJournal()) == 0 ? AonStringUtils.EMPTY : AON.MSG.journal()
				+ AonStringUtils.SPACE + wizardContent.getAccountEntryWrapper().getAccountEntry().getJournal());
		if (configuration.hasActivities()) {
			activity.setEnabled(canEdit);
			int i = 0;
			for (; i < activity.getItemCount(); i++) {
				if (AonNumberUtils.toInteger(activity.getValue(i)) == wizardContent.getAccountEntryWrapper().getAccountEntry().getActivity()) {
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
			if (!wizardContent.getAccountEntryWrapper().getAccountEntry().isPeriodActive()) {
				statusMsg.setText(
						AON.MSG.periodStatusWarning(
								AON.MSG.accountPeriodStatus(wizardContent.getAccountEntryWrapper().getAccountEntry().getPeriodStatus())));
			} else {
				statusMsg.setText(AON.MSG.automaticEntryWarning());
				
			}
		}
		refreshIdLabel();
		
		wizardContent.enableElements(canRemove,canEdit);

		// Enable/Disable header values
		period.setEnabled(canEdit);
		entryDate.setEnabled(canEdit);
		confidential.setEnabled(canEdit);
		invoice.setEnabled(canEdit);
		accept.setEnabled(canEdit);
		remove.setEnabled(canRemove);
		
		styleCommentsButton();
		errors = new ErrorPanel();
		errorsContainer.setWidget(errors);
	}

	private void refreshIdLabel() {
		id.setText((wizardContent.isNew() 
				? "[NUEVO]" 
				: ("(" + wizardContent.getAccountEntryWrapper().getAccountEntry().getId() + ") "))
				+ (wizardContent.getAccountEntryWrapper().getAccountEntry().isDirty()?AonStringUtils.ASTERISK:AonStringUtils.EMPTY)
				);
		if (wizardContent.getAccountEntryWrapper().getAccountEntry().isDirty()) {
			id.addStyleName(AON.AON_CSS.aonColorRed());
		} else {
			id.removeStyleName(AON.AON_CSS.aonColorRed());
		}
	}

	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(wizardContent.getAccountEntryWrapper().getAccountEntry().getComments())) {
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
				sessionLog.addSaved(result);
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
						if (wizardContent.getAccountEntryWrapper().getAccountEntry().getId() != null) {
							wizardContent.getAccountEntryWrapper().getAccountEntry().setId(
									wizardContent.getAccountEntryWrapper().getAccountEntry().getId() * -1);
							sessionLog.addDeleted(wizardContent.getAccountEntryWrapper().getAccountEntry());
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
		dialog.show(wizardContent.getAccountEntryWrapper().getAccountEntry());
	}

	@UiHandler("sessionLog")
	public void onSelectJournal(SelectionEvent<AccountEntry> event) {
		final AccountEntry entry = event.getSelectedItem();
		selectEntry(entry.getId(),entry);
	}

	@UiHandler("journalPanel")
	public void onSelectJournalPanel(SelectionEvent<AccountEntry> event) {
		final AccountEntry entry = event.getSelectedItem();
		selectEntry(entry.getId(),entry);
	}
	@UiHandler("statementPanel")
	public void onSelectStatement(SelectionEvent<Integer> event) {
		selectEntry(event.getSelectedItem(),null);
	}
	
	private void selectEntry(final Integer id,final AccountEntry entry) {
		if (wizardContent.isDirty()) {
			sessionLog.addSuspended(wizardContent.getAccountEntryWrapper().getAccountEntry());
		}
		balancePanel.clearBalances();
		final PopupPanel waitPopup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		waitPopup.add(label);
		waitPopup.setGlassEnabled(true);
		waitPopup.setAnimationEnabled(true);
		waitPopup.center();
		try {
			if (id != null) {
				fiscalService.getAccountEntry(getCurrentDomainName(),
						getCurrentDomain(), id ,
						new AsyncCallback<AccountEntry>() {
							@Override
							public void onSuccess(AccountEntry result) {
								if (result != null) {
									selectWizardContent( result );
								} else {
									if (entry != null) {
										ConfirmDialog cd = new ConfirmDialog();
										cd.confirm(AON.MSG.recoverEntry(),
												new ConfirmDialogCallback() {
											
											@Override
											public void onCancel() {
											}
											
											@Override
											public void onAccept() {
												AccountEntry cloned = AccountEntry.clone(entry);
												cloned.setId(null);
												cloned.setJournal(null);
												for (AccountEntryDetail aed : cloned.getDetails()) {
													aed.setId(null);
												}
												selectWizardContent( cloned );
											}
										});
									} else {
										showError("Asiento no encontrado");
									}
								}
								waitPopup.hide();
							}

							@Override
							public void onFailure(Throwable caught) {
								showError(caught.getMessage());
								waitPopup.hide();
							}
						});
			} else {
				selectWizardContent( entry );
			}
		} finally {
			if (waitPopup.isShowing()) 
				waitPopup.hide();
		}
	}
	
	@UiHandler("balancePanel")
	public void onSelectBalance(SelectionEvent<Integer> event) {
		showFullStatement(event.getSelectedItem());
	}

	// ---------------------------------------------------------------- ACTION
	private void reset() {
		balancePanel.clearBalances();
		confidential.setVisible(configuration.getUser().hasConfidentialityRole());
		journalPanel.setUser(configuration.getUser());
		final MutableInt first = new MutableInt(0);
		AccountEntry ae =  null;
		if (this.wizardContent == null) {
			first.setValue(1);
			new Manual(this.callback).attach(null);
			EnterpriseActivity ea = configuration.getMainActivity();
			ae =  new AccountEntry()
					.setPeriod(AonNumberUtils.toInteger(period.getSelectedValue()))
					.setEntryType(this.wizardContent.getAccountEntryType())
					.setDomain(getCurrentDomain())
					.setConfidential(false)
					.setEntryDate(new Date())
					.setActivity(ea==null?null:ea.getId())
					.setDirty(false);
		} else {
			ae =  new AccountEntry()
					.setPeriod(this.wizardContent.getAccountEntryWrapper().getAccountEntry().getPeriod())
					.setEntryType(this.wizardContent.getAccountEntryWrapper().getAccountEntry().getEntryType())
					.setDomain(getCurrentDomain())
					.setConfidential(this.wizardContent.getAccountEntryWrapper().getAccountEntry().isConfidential())
					.setEntryDate(this.wizardContent.getAccountEntryWrapper().getAccountEntry().getEntryDate())
					.setActivity(this.wizardContent.getAccountEntryWrapper().getAccountEntry().getActivity())
					.setDirty(false);
		}
		this.wizardContent.select(ae, new ISelectionCallback() {
			
			@Override
			public void onSucces() {
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
		});
		
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

	@UiHandler("invoice")
	public void onClickInvoice(ClickEvent event) {
		final AccountEntry ae = this.wizardContent.getAccountEntryWrapper().getAccountEntry();
		IContentAttchCallback cbk = new IContentAttchCallback() {
			@Override
			public void onAttach() {
				AccountEntryModule.this.wizardContent.select(ae,new ISelectionCallback() {
					
					@Override
					public void onSucces() {
						AccountEntryModule.this.wizardContent.setFocus(true);
					}
					
					@Override
					public void onFailure() {
						invalidateModule("Error inesperado");
					}
				});
				
			}
		};
		if (invoice.getValue()) {
			// No se llama a selectWizardContent, porque es una factura 
			// nueva y todavía no sabemos el tipo que tiene. 
			setWizardContent(new InvoicePanel(callback),cbk);
		} else {
			ae.setEntryType(AccountEntryType.MANUAL);
			selectWizardContent(ae);
		}
	}
	
	public static interface IContentAttchCallback {
		void onAttach();
	}
	
	private void selectWizardContent( final AccountEntry entry ) {
		final IContentAttchCallback wizardCbk = new IContentAttchCallback() {
			@Override
			public void onAttach() {
				AccountEntryModule.this.wizardContent.select(entry, new ISelectionCallback() {
					
					@Override
					public void onSucces() {
						syncCurrent();
					}
					
					@Override
					public void onFailure() {
						invalidateModule("");
					}
				});
			}
		};
		
		entry.getEntryType().visit(entry, new  AccountEntryTypeVisitorAdapter() {
			@Override
			public void visitExpenseInvoice(AccountEntry entry) {
				invoice.setValue(true,false);
				new InvoicePanel(callback).attach(wizardCbk);
			}
			@Override
			public void visitSalesInvoice(AccountEntry entry) {
				invoice.setValue(true,false);
				new InvoicePanel(callback).attach(wizardCbk);
			}
			@Override
			public void visitPurchaseInvoice(AccountEntry entry) {
				invoice.setValue(true,false);
				new InvoicePanel(callback).attach(wizardCbk);
			}
			@Override
			public void visitManual(AccountEntry entry) {visitManual();}
			
			private void visitManual() {
				invoice.setValue(false,false);
				new Manual(callback).attach(wizardCbk);
			}
			
			@Override public void visitOpening(AccountEntry entry) {visitManual();}
			@Override public void visitClosing(AccountEntry entry) {visitManual();}
			@Override public void visitOperating(AccountEntry entry) {visitManual();}
			@Override public void visitInvestmentInvoice(AccountEntry entry) {visitManual();}
			@Override public void visitExpenses(AccountEntry entry) {visitManual();}
			@Override public void visitSalary(AccountEntry entry) {visitManual();}
			@Override public void visitTax(AccountEntry entry) {visitManual();}
			@Override public void visitLoan(AccountEntry entry) {visitManual();}
			@Override public void visitPayment(AccountEntry entry) {visitManual();}
			@Override public void visitCollection(AccountEntry entry) {visitManual();}
			@Override public void visitStockVariation(AccountEntry entry) {visitManual();}
			@Override public void visitAmortization(AccountEntry entry) {visitManual();}
			@Override public void visitSocialInsurance(AccountEntry entry) {visitManual();}
			@Override public void visitLoanFee(AccountEntry entry) {visitManual();}
			@Override public void visitReturnedPayment(AccountEntry entry) {visitManual();}
			@Override public void visitReturnedCollection(AccountEntry entry) {visitManual();}
			@Override public void visitSocialInsuranceAdjust(AccountEntry entry) {visitManual();}
			@Override public void visitLeasing(AccountEntry entry) {visitManual();}
			@Override public void visitLeasingFee(AccountEntry entry) {visitManual();}
			
		});
			
	}
	
	private void setWizardContent(IWizardContent content,IContentAttchCallback cbk) {
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
}
