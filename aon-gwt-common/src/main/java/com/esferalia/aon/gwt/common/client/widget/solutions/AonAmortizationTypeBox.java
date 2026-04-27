package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AonAmortizationTypeBox extends AonCustomListBox implements HasSelectionHandlers<AmortizationType> {
	
	private static final CommonServiceAsync SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	private List<AmortizationType> types = new LinkedList<>();
	private AmortizationType type;
	
	public <T> AonAmortizationTypeBox(AonModuleOptions<?> opts) {
		this(opts, AON.MSG.amortizationType());
	}
	
	public AonAmortizationTypeBox(AonModuleOptions<?> opts, String title) {
		super(title);
		getListBox().addItem( "------", "" );
		SERVICE.getAmortizationTypes(opts.getOccam(), opts.getDomain(), new AsyncCallback<List<AmortizationType>>() {
			@Override
			public void onSuccess(List<AmortizationType> result) {
				 AonCollectionUtils.stream(result)
					.forEach( a -> {
						types.add(a);
						getListBox().addItem( a.getDescription(), AonNumberUtils.toString(a.getId()) );	
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				AonMessageDialog.error(caught.getMessage());
			}
		});
		getListBox().addChangeHandler(e -> setAmortizationType( AonNumberUtils.toInteger( getListBox().getSelectedValue()) ));
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AmortizationType> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Optional<AmortizationType> getAmortizationType() {
		return Optional.ofNullable(type);
	}
	
	public void setAmortizationType(AmortizationType t) {
		setAmortizationType(t==null?null:t.getId());
	}
	public void setAmortizationType(Integer id) {
		setAmortizationType(id, true);
	}
	public void setAmortizationType(Integer id, boolean fireEvents) {
		int index = -1;
		for ( int i = 0; i < types.size(); i++ ) {
			if ( AonNumberUtils.equals( types.get(i).getId(), id) ) {
				this.type = types.get(i);
				index = i;
				break;
			}
		}
		int listBoxIndex = index + 1;
		getListBox().setSelectedIndex( listBoxIndex );
		if (fireEvents) {
			SelectionEvent.fire(this, this.type);
		}
	}

}
