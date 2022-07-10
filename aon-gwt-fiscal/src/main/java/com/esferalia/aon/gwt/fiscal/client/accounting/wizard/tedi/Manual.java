package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleTEDI.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.ISelectionCallback;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryWrapper;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Manual extends WizardContentBase<AccountEntryWrapper> {
	
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
		getCallback().getModule().onClearSessionLog();
		if (id != null) {
			getAccountEntryService().getAccountEntry(getCallback().getCurrentDomainName(),
					getCallback().getCurrentDomainId(),getCallback().getCurrentUser(), id ,
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
		tableContainer.setStyleName(AON.AON_CSS.aonScrollArea());
		tableInnerContainer = new VerticalPanel();
		tableInnerContainer.addStyleName(AON.AON_CSS.aonWidthAll());
		
		table = new AccountEntryTable(getCallback().getModuleOptions(),this);
		table.addErrorHandler(new ErrorHandler() {

			@Override
			public void onError(ErrorEvent event) {
				getCallback().getModule().onError(event.getRelativeElement().getAttribute("ERROR"));
			}
			
		});
		table.addValueChangeHandler(new ValueChangeHandler<AccountEntryDetail>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<AccountEntryDetail> event) {
				getCallback().getModule().refreshIdLabel();
				getCallback().getModule().onPreview(getWrapper());
			}
		});
		tableInnerContainer.add(table);
		tableContainer.setWidget(tableInnerContainer);
		getCallback().getModule().refreshIdLabel();
		getCallback().getModule().onPreview(getWrapper());
		if (cbk != null) cbk.onSuccess();
	}

	@Override
	public boolean isUpdatable() {
		return super.isUpdatable() && isManual(getAccountEntry().getEntryType());
	}
	
	private boolean isManual(AccountEntryType type) {
		return ( type == AccountEntryType.MANUAL
			 || type == AccountEntryType.EXPENSES
			 || type == AccountEntryType.SALARY
			 || type == AccountEntryType.SOCIAL_INSURANCE
			 || type == AccountEntryType.SOCIAL_INSURANCE_ADJUST
			 || type == AccountEntryType.LOAN
			 || type == AccountEntryType.TAX
			 || type == AccountEntryType.LOAN_FEE );
	}
	@Override
	public String getNoUpdatableCause() {
		if (!isManual(getAccountEntry().getEntryType())) {
			return AON.MSG.automaticEntryWarning();
		}
		return null;
	}
	
	@Override
	public boolean isAttachmentManagementEnabled() {
		return false;
	}
	@Override
	public boolean hasAttachment() {
		return false;
	}
	@Override
	public void removeAttach(final AsyncCallback<IAccountEntryWrapper> cbk) {
	}
	@Override
	public void addAttach(final AsyncCallback<IAccountEntryWrapper> cbk) {
	}

	@Override
	public boolean isRemovable() {
		AccountEntryType type = getAccountEntry().getEntryType();
		return super.isRemovable() || (
				type == AccountEntryType.OPERATING
			 || type == AccountEntryType.CLOSING
			 || type == AccountEntryType.OPENING);
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
				.setDomain(getCallback().getCurrentDomainId())
				.setConfidential(false)
				.setEntryDate(base.getEntryDate())
				.setActivity(base.getActivity())
				.setJournal(null));
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

	public void entryDateChanged(Date entryDate) {
		getWrapper().getAccountEntry().setEntryDate(entryDate);
	}
	public void activityChanged(Integer activty) {
		getWrapper().getAccountEntry().setActivity(activty);
	}
	public void confidentialChanged(boolean confidential) {
		getWrapper().getAccountEntry().setConfidential(confidential);
	}
}
