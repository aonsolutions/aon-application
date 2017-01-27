package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryTable;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryWrapper;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Manual extends WizardContentBase<AccountEntryWrapper> {
	
	private static final String BACKGROUND_COLOR = "#EEEEEE";
	private ScrollPanel tableContainer;
	private VerticalPanel tableInnerContainer;
	private AccountEntryTable table;
	private AccountEntryWrapper wrapper;
	
	public Manual(final IAccountEntryModuleCallback callback) {
		setCallback(callback);
		tableContainer = new ScrollPanel();
		initWidget(tableContainer);
	}
	
	@Override
	public AccountEntryWrapper getWrapper() {
		return this.wrapper;
	}

	@Override
	public void setWrapper(AccountEntryWrapper wrapper) {
		this.wrapper = wrapper; 
	}

	@Override
	public void select(final Integer id,final IAccountEntryWrapper wrp,final ISelectionCallback cbk) {
		if (id != null) {
			getFiscalService().getAccountEntry(AccountEntryModule.getCurrentDomainName(),
					AccountEntryModule.getCurrentDomain(), id ,
					new AsyncCallback<AccountEntry>() {
						@Override
						public void onSuccess(AccountEntry result) {
							if (result != null) {
								select(new AccountEntryWrapper( result ),cbk);
							} else {
								if (wrp != null && wrp.getAccountEntry() != null) {
									getCallback().getModule().recoverDeletedEntry(wrp);
								} else {
									getCallback().getModule().onError("Asiento no encontrado");
								}
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							getCallback().getModule().onError(caught.getMessage());
						}
					});
		} else {
			if (wrp != null) {
				select( wrp ,cbk);
			} else {
				getCallback().getModule().onError("Asiento no encontrado");
			}
		}
	}		
		
	private void select(IAccountEntryWrapper wrp,ISelectionCallback cbk) {
		setWrapper((AccountEntryWrapper) wrp);
		paint();
		table.paintTable();
		tableInnerContainer.add(table);
		tableContainer.setWidget(tableInnerContainer);
		getCallback().getModule().onBalance(getWrapper());
		if (cbk != null) cbk.onSuccess();
	}

	@Override
	public boolean isUpdatable() {
		return super.isUpdatable() && getAccountEntry().isManual();
	}
	
	@Override
	public void reset(AccountEntry base, ISelectionCallback cbk) {
		AccountEntryWrapper wrapper = create(base);
		select(wrapper, cbk);
	}

	private AccountEntryWrapper create(AccountEntry base) {
		return new AccountEntryWrapper( new AccountEntry()
				.setPeriod(base.getPeriod())
				.setEntryType(AccountEntryType.MANUAL)
				.setDomain(AccountEntryModule.getCurrentDomain())
				.setConfidential(false)
				.setEntryDate(base.getEntryDate())
				.setActivity(base.getActivity()));
	}
	
	private void paint() {
		tableContainer.setStyleName(AON.AON_CSS.aonScrollArea());
		tableInnerContainer = new VerticalPanel();
		tableInnerContainer.addStyleName(AON.AON_CSS.aonWidthAll());
		
		table = new AccountEntryTable(this);
		table.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);
		table.addErrorHandler(new ErrorHandler() {

			@Override
			public void onError(ErrorEvent event) {
				getCallback().getModule().onError(event.getRelativeElement().getAttribute("ERROR"));
			}
			
		});
		table.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				Account account = event.getSelectedItem();
				if (account != null) getCallback().getModule().onBalance(account);
			}
		});
		table.addValueChangeHandler(new ValueChangeHandler<AccountEntryDetail>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<AccountEntryDetail> event) {
				getCallback().getModule().refreshIdLabel();
			}
		});
	}
	
	@Override
	public void manageWidgets(boolean canRemove, boolean canEdit) {
		
	}

	@Override
	public int getTabIndex() {
		return table!=null?table.getTabIndex():0;
	}

	@Override
	public void setAccessKey(char key) {
		if (table!=null) table.setAccessKey(key);
		
	}

	@Override
	public void setTabIndex(int index) {
		if (table!=null) table.setTabIndex(index);
	}

	@Override
	public void setFocus(boolean b) {
		if (table!=null) table.setFocus(b);
	}
}
