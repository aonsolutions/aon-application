package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AccountEntryModule extends MainEntryPoint {
	final static int SESSION_LOG_TAB = 0;
	final static int BALANCES_TAB = 1;
	final static int STATEMENT_TAB = 2;
	final static int JOURNAL_TAB = 3;

	static FiscalServiceAsync fiscalService;
	static CommonServiceAsync commonService;

	interface AccountEntryModuleBinder extends
			UiBinder<Widget, AccountEntryModule> {
	}

	private static final AccountEntryModuleBinder BINDER = GWT
			.create(AccountEntryModuleBinder.class);

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
	InlineLabel journal;
	@UiField
	CheckBox confidential;
	@UiField
	InlineLabel type;
	@UiField
	Button commentsButton;
	@UiField
	InlineLabel statusMsg;	
	@UiField
	ScrollPanel tableContainer;
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


	VerticalPanel tableInnerContainer;
	ErrorPanel errors;
	AccountEntryTable tab;
	final PopupPanel waitPopup = new PopupPanel(false, true);
	
	private boolean periodErrorShown;
	private User user;
	private AccountEntryObject current;

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		fiscalService = new FiscalServiceAsyncDecorator(fiscalServiceRaw);
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		commonService = new CommonServiceAsyncDecorator(commonServiceRaw);

		splitLayoutPanel = new SplitLayoutPanel(4);
		journalPanel = new JournalPanel(getCurrentDomainName(),
				getCurrentDomain());

		Widget ui = BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		confidential.setTabIndex(Integer.MAX_VALUE - 1);
		commentsButton.setTabIndex(Integer.MAX_VALUE);
		root.add(ui);
		tabLayout.setAnimationDuration(300);
		accept.setAccessKey('G');
		reset.setAccessKey('N');

		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		waitPopup.add(label);
		waitPopup.setGlassEnabled(true);

		fiscalService.getDomainPeriods(getCurrentDomainName(),
				getCurrentDomain(),
				new AsyncCallback<LinkedList<AccountPeriod>>() {
					@Override
					public void onSuccess(LinkedList<AccountPeriod> result) {
						if (result != null && !result.isEmpty()) {
							period.fill(result);
							commonService.getCurrentUser(getCurrentDomainName(),
									getCurrentDomain(), new AsyncCallback<User>() {

										@Override
										public void onSuccess(User result) {
											setUser(result);
											journalPanel.setUser(result);
											reset();
										}

										@Override
										public void onFailure(Throwable caught) {
											invalidateModule("Imposible determinar el usuario conectado");
										}
									});
						} else {
							invalidateModule(AON.MSG.noActiveAccountPeriod());
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						invalidateModule(AON.MSG.noActiveAccountPeriod());
					}
				});
	}

	protected void invalidateModule(String msg) {
		errors = new ErrorPanel();
		errors.showError(msg);
		tableContainer.setWidget(errors);
		entryHeader.setVisible(false);
		search.setVisible(false);
		reset.setVisible(false);
		accept.setVisible(false);
		remove.setVisible(false);
		audit.setVisible(false);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		confidential.setVisible(user.hasConfidentialityRole());
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
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
		splitLayoutPanel.animate(500);
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
		splitLayoutPanel.animate(500);
	}

	private void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
		splitLayoutPanel.animate(500);
	}

	@UiHandler("entryDate")
	void onChangeEntryDate(ValueChangeEvent<Date> event) {
		this.current.getAccountEntry().setEntryDate(event.getValue());
		checkDate();
	}

	@UiHandler("period")
	void onChangeAccountPeriod(ChangeEvent event) {
		Integer ap = AonNumberUtils.toInteger(period.getSelectedValue());
		this.current.getAccountEntry().setPeriod(ap);
		checkDate();
	}

	@UiHandler("confidential")
	void onChangeConfidential(ClickEvent event) {
		this.current.getAccountEntry().setConfidential(confidential.getValue());
	}

	private void checkDate() {
		if (period.isOutOfRange(entryDate.getValue())) {
			periodErrorShown = true;
			errors.showError(AON.MSG.accountEntryOutOfRange());
		} else {
			if (periodErrorShown) {
				periodErrorShown = false;
				errors.hide();
			}
		}
	}

	private void syncCurrent() {
		balancePanel.clearBalances();
		// Populate header values
		period.select(current.getAccountEntry().getPeriod());
		period.setEnabled(current.isUpdatable());
		entryDate.setValue(current.getAccountEntry().getEntryDate());
		entryDate.setEnabled(current.isUpdatable());
		confidential.setValue(current.getAccountEntry().isConfidential());
		confidential.setEnabled(current.isUpdatable());
		journal.setText(AonMathUtils.toInt(current.getAccountEntry()
				.getJournal()) == 0 ? AonStringUtils.EMPTY : AON.MSG.journal()
				+ AonStringUtils.SPACE + current.getAccountEntry().getJournal());
		type.setText(AON.MSG.accountEntryType(current.getAccountEntry().getEntryType()));
		statusMsg.setText(AonStringUtils.EMPTY);
		statusMsg.removeStyleName(AON.AON_CSS.aonInfoMessage());
		
		remove.setEnabled(current.isUpdatable());
		if (!current.isUpdatable()) {
			statusMsg.addStyleName(AON.AON_CSS.aonInfoMessage());
			if (!current.isPeriodActive()) {
				statusMsg.setText(
						AON.MSG.periodStatusWarning(
								AON.MSG.accountPeriodStatus(current.getAccountEntry().getPeriodStatus())));
			} else {
				statusMsg.setText(AON.MSG.automaticEntryWarning());
				
			}
		}
		id.setText(current.isNew() ? AonStringUtils.EMPTY : "("
				+ current.getAccountEntry().getId() + ")");
		styleCommentsButton();

		// Populate detail values
		tableInnerContainer = new VerticalPanel();
		tableInnerContainer.addStyleName(AON.AON_CSS.aonWidthAll());
		errors = new ErrorPanel();
		tableInnerContainer.add(errors);
		tab = new AccountEntryTable(current);
		tab.addErrorHandler(new ErrorHandler() {

			@Override
			public void onError(ErrorEvent event) {
				errors.showWarning(event.getRelativeElement().getAttribute(
						"ERROR"));

			}
		});
		tab.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				if (splitLayoutPanel.getWidgetSize(footPanel) == 0) {
					splitLayoutPanel.setWidgetSize(footPanel,
							Window.getClientHeight() / 4);
					splitLayoutPanel.animate(500);
				}
				tabLayout.selectTab(BALANCES_TAB);
				Account account = event.getSelectedItem();
				Date from = DateUtils.getFirstDayOfYear(entryDate.getValue());
				balancePanel.add(account, from, entryDate.getValue());
			}
		});
		tab.paintTable();
		tableInnerContainer.add(tab);
		tableContainer.setWidget(tableInnerContainer);
		tab.setFocus(true);
	}

	private void styleCommentsButton() {
		if (AonStringUtils.isEmpty(current.getAccountEntry().getComments())) {
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
		current.save(new AsyncCallback<AccountEntry>() {

			@Override
			public void onSuccess(AccountEntry result) {
				accept.setEnabled(true);
				current.setAccountEntry(result);
				addToJournalLog(result);
				reset();
			}

			@Override
			public void onFailure(Throwable caught) {
				accept.setEnabled(true);
				errors.showError(caught);
			}
		});
	}

	@UiHandler("search")
	public void onSearch(ClickEvent event) {
		if (splitLayoutPanel.getWidgetSize(footPanel) == 0) {
			splitLayoutPanel.setWidgetSize(footPanel,
					Window.getClientHeight() / 2);
			splitLayoutPanel.animate(500, new AnimationCallback() {

				@Override
				public void onLayout(Layer layer, double progress) {
					// TODO Auto-generated method stub

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
			tabLayout.selectTab(JOURNAL_TAB);
			journalPanel.setFocus(true);
		}
	}

	@UiHandler("reset")
	public void onReset(ClickEvent event) {
		reset();
		tab.setFocus(true);
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
				current.remove(new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						if (current.getAccountEntry().getId() != null) {
							current.getAccountEntry().setId(
									current.getAccountEntry().getId() * -1);
							addToJournalLog(current.getAccountEntry());
						}
						remove.setEnabled(true);
						reset();
					}

					@Override
					public void onFailure(Throwable caught) {
						remove.setEnabled(true);
						errors.showError(caught);
					}
				});
			}
		});
	}

	@UiHandler("audit")
	public void onAudit(ClickEvent event) {
		AuditDialog dialog = new AuditDialog();
		dialog.show(current.getAccountEntry());
	}

	@UiHandler("sessionLog")
	public void onSelectJournal(SelectionEvent<AccountEntry> event) {
		final AccountEntry entry = event.getSelectedItem();
		if (entry.getId() == null) {
			current.setAccountEntry(entry);
			syncCurrent();
		} else {
			selectEntry(entry);
		}
	}

	@UiHandler("journalPanel")
	public void onSelectJournalPanel(SelectionEvent<AccountEntry> event) {
		addToJournalLog(current.getAccountEntry());
		final AccountEntry entry = event.getSelectedItem();
		selectEntry(entry);
	}

	private void selectEntry(final AccountEntry entry) {
		waitPopup.center();
		fiscalService.getAccountEntry(getCurrentDomainName(),
				getCurrentDomain(), entry.getId(),
				new AsyncCallback<AccountEntry>() {
					@Override
					public void onSuccess(AccountEntry result) {
						if (result != null && result.getId() != null) {
							current.setAccountEntry(result);
							syncCurrent();
							balancePanel.add(result);
						} else {
							ConfirmDialog cd = new ConfirmDialog();
							cd.confirm(AON.MSG.recoverEntry(),
									new ConfirmDialogCallback() {

										@Override
										public void onCancel() {
										}

										@Override
										public void onAccept() {
											entry.setId(null);
											for (AccountEntryDetail aed : entry
													.getDetails()) {
												aed.setId(null);
											}
											current.setAccountEntry(entry);
											syncCurrent();
										}
									});
						}
						waitPopup.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						waitPopup.hide();
						errors.showError(caught);
					}
				});
	}

	@UiHandler("balancePanel")
	public void onSelectBalance(SelectionEvent<Integer> event) {
		showFullStatement(event.getSelectedItem());
	}

	// ---------------------------------------------------------------- ACTION
	private void reset() {
		this.current = AccountEntryObject.newInstance(getCurrentDomainName(),
				getCurrentDomain());
		if (entryDate.getValue() != null) {
			this.current.getAccountEntry().setEntryDate(entryDate.getValue());
		}
		this.current.getAccountEntry().setPeriod(
				AonNumberUtils.toInteger(period.getSelectedValue()));
		syncCurrent();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				entryDate.setFocus(true);
				entryDate.hideDatePicker();
				entryDate.getTextBox().selectAll();
			}
		});
	}

	private void addToJournalLog(AccountEntry entry) {
//		tabLayout.selectTab(SESSION_LOG_TAB);
		sessionLog.add(entry);
//		if (splitLayoutPanel.getWidgetSize(footPanel) == 0) {
//			splitLayoutPanel.setWidgetSize(footPanel,
//					Window.getClientHeight() / 4);
//			splitLayoutPanel.animate(500);
//		}
	}

	private void showFullStatement(Integer selectedItem) {
		tabLayout.selectTab(STATEMENT_TAB);
		Date from = DateUtils.getFirstDayOfYear(entryDate.getValue());
		statementPanel.show(selectedItem, from, entryDate.getValue());
	}

	public static interface ConfirmDialogCallback {
		void onAccept();

		void onCancel();
	}

	@UiHandler("commentsButton")
	public void onComments(ClickEvent event) {
		if (splitLayoutPanel.getWidgetSize(wizardPanel) == 0) {
			splitLayoutPanel.setWidgetSize(wizardPanel, 105);
			splitLayoutPanel.animate(500);
		} else {
			splitLayoutPanel.setWidgetSize(wizardPanel, 0);
			splitLayoutPanel.animate(500);
		}
		ScrollPanel root = new ScrollPanel();
		root.setStyleName(AON.AON_CSS.aonScrollArea());

		FlowPanel commentPanel = new FlowPanel();
		commentPanel.setStyleName(AON.AON_CSS.aonPanelGridSearch());
		commentPanel.addStyleName(AON.AON_CSS.aonTextCenter());
		commentPanel.addStyleName(AON.AON_CSS.aonPadding());

		TextArea comment = new TextArea();
		comment.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				current.getAccountEntry().setComments(event.getValue());
				styleCommentsButton();
			}
		});
		comment.setText(current.getAccountEntry().getComments());
		comment.setWidth("80%");
		comment.setHeight("4em");
		commentPanel.add(comment);

		root.setWidget(commentPanel);
		wizardPanel.setWidget(root);
	}
}
