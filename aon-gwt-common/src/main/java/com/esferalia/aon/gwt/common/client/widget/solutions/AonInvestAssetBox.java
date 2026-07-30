package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;

public class AonInvestAssetBox extends AonCustomListBox implements HasSelectionHandlers<InvestAsset> {
	
	private List<InvestAsset> investAssets = new LinkedList<>();
	private InvestAsset investAsset;
	
	public <T> AonInvestAssetBox(AonModuleOptions<?> opts) {
		this(opts, AON.MSG.investAsset());
	}
	
	public AonInvestAssetBox(AonModuleOptions<?> opts, String title) {
		super(title);
		getListBox().addItem( "------", "" );
		if ( opts != null && opts.getConfiguration() != null ) {
			AonCollectionUtils.stream( opts.getConfiguration().getInvestAssets())
				.forEach( a -> {
					investAssets.add(a);
					getListBox().addItem( a.getDescription(), AonNumberUtils.toString(a.getId()) );	
			});
		}
		getListBox().addChangeHandler(e -> setInvestAsset( AonNumberUtils.toInteger( getListBox().getSelectedValue()) ));
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<InvestAsset> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Optional<InvestAsset> getInvestAsset() {
		return Optional.ofNullable(investAsset);
	}
	
	public void setInvestAsset(InvestAsset investAsset) {
		setInvestAsset(investAsset==null?null:investAsset.getId());
	}
	public void setInvestAsset(Integer activityId) {
		setInvestAsset(activityId, true);
	}
	public void setInvestAsset(Integer activityId, boolean fireEvents) {
		int index = -1;
		this.investAsset = null;
		for ( int i = 0; i < investAssets.size(); i++ ) {
			if ( AonNumberUtils.equals( investAssets.get(i).getId(), activityId) ) {
				this.investAsset = investAssets.get(i);
				index = i;
				break;
			}
		}
		int listBoxIndex = index + 1;
		getListBox().setSelectedIndex( listBoxIndex );
		if (fireEvents) {
			SelectionEvent.fire(this, this.investAsset);
		}
	}

}
