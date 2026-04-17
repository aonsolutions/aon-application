package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AonAccountListBox extends AonCustomListBox implements HasSelectionHandlers<Account> {
	
	private static final CommonServiceAsync SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	private final String prefix;
	
	private List<Account> accounts = new LinkedList<>();
	private Account account;
	
	public AonAccountListBox(AonModuleOptions<?> opts, String prefix, String title) {
		super(title);
		this.prefix = prefix;
		getListBox().addItem( "------", "" );
		SERVICE.getAccounts(opts.getDomainName(),opts.getDomain(),opts.getUser(), this.prefix, new AsyncCallback<LinkedList<Account>>() {
			@Override
			public void onSuccess(LinkedList<Account> result) {
				AonCollectionUtils.stream(result)
					.forEach( a -> {
						accounts.add(a);
						getListBox().addItem( a.getFullName(), AonNumberUtils.toString(a.getId()) );	
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				AonMessageDialog.error(caught.getMessage());
			}
		});
		getListBox().addChangeHandler(e -> setAccount( AonNumberUtils.toInteger( getListBox().getSelectedValue()) ));
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Account> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Optional<Account> getAccount() {
		return Optional.ofNullable(account);
	}
	
	public void setAccount(Account acc) {
		setAccount(acc==null?null:acc.getId());
	}
	public void setAccount(Integer id) {
		setAccount(id, true);
	}
	public void setAccount(Integer id, boolean fireEvents) {
		int index = -1;
		for ( int i = 0; i < accounts.size(); i++ ) {
			if ( AonNumberUtils.equals( accounts.get(i).getId(), id) ) {
				this.account = accounts.get(i);
				index = i;
				break;
			}
		}
		int listBoxIndex = index + 1;
		getListBox().setSelectedIndex( listBoxIndex );
		if (fireEvents) {
			SelectionEvent.fire(this, this.account);
		}
	}

}
