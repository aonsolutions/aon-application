package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ListBox;

public class AonActivityBox extends ListBox implements HasSelectionHandlers<EnterpriseActivity> {
	
	private List<EnterpriseActivity> activities = new LinkedList<>();
	private EnterpriseActivity activity;
	
	public <T> AonActivityBox(AonModuleOptions<?> opts) {
		addItem( "------", "" );
		if ( opts != null && opts.getConfiguration() != null ) {
			AonCollectionUtils.stream( opts.getConfiguration().getActivities())
				.forEach( a -> {
					activities.add(a);
					addItem( a.getDescription(), AonNumberUtils.toString(a.getId()) );	
			});
		}
		addChangeHandler(e -> setActivity( AonNumberUtils.toInteger(getSelectedValue()) ));
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<EnterpriseActivity> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public Optional<EnterpriseActivity> getActivity() {
		return Optional.ofNullable(activity);
	}
	
	public void setActivity(EnterpriseActivity activity) {
		setActivity(activity==null?null:activity.getId());
	}
	public void setActivity(Integer activityId) {
		setActivity(activityId, true);
	}
	public void setActivity(Integer activityId, boolean fireEvents) {
		int index = -1;
		for ( int i = 0; i < activities.size(); i++ ) {
			if ( AonNumberUtils.equals( activities.get(i).getId(), activityId) ) {
				this.activity = activities.get(i);
				index = i;
				break;
			}
		}
		int listBoxIndex = index + 1;
		setSelectedIndex( listBoxIndex );
		if (fireEvents) {
			SelectionEvent.fire(this, this.activity);
		}
	}

}
