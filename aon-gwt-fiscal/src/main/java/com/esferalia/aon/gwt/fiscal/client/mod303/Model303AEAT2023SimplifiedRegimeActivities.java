package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonGroupPanel;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod303Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod303ActivityFarmer;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class Model303AEAT2023SimplifiedRegimeActivities extends DockLayoutPanel implements HasValueChangeHandlers<Mod303>{
	
	static interface IModel303AEATSimplifiedRegimeCallback {
		Mod303 getModel();
	}
	
	static interface IModel303AEATActivityCallback<E> {
		Mod303 getModel();
		E getActivity();
		void onAccept();
		void onRemove();
	}
	
	private Model303AEAT2023ActivityFarmerTable farmerTable;
	private Model303AEAT2023ActivityFarmer farmerActivityPanel;
	
	private Model303AEAT2023ActivityTable activityTable;
	private Model303AEAT2023Activity activityPanel;
	private SimpleLayoutPanel contentLayoutPanel;
	
	Model303AEAT2023SimplifiedRegimeActivities( IModel303AEATSimplifiedRegimeCallback callback ) {
		super(Unit.PX);
		
		SimpleLayoutPanel sidebarLayoutPanel = new SimpleLayoutPanel();
		ScrollPanel sidebarScrollPanel = new ScrollPanel();
		FlowPanel sidebarPanel = new FlowPanel();

		contentLayoutPanel = new SimpleLayoutPanel();
		farmerTable = new Model303AEAT2023ActivityFarmerTable(callback);
		sidebarPanel.add( AonGroupPanel.get(AON.MSG.farmerActivity(), farmerTable ) );
		farmerTable.addSelectionHandler( e -> selectFarmerActivity(callback, e.getSelectedItem().getIndex() ) );
		
		activityTable = new Model303AEAT2023ActivityTable(callback);
		activityTable.addSelectionHandler( e -> selectActivity(callback, e.getSelectedItem().getIndex() ) );
		sidebarPanel.add(AonGroupPanel.get(AON.MSG.simplifieedActivities(), activityTable ));
		sidebarScrollPanel.setWidget(sidebarPanel);
		sidebarLayoutPanel.setWidget(sidebarScrollPanel);
		
		addWest( sidebarLayoutPanel, 400);
		add( contentLayoutPanel);
	}
	
	private void selectFarmerActivity(IModel303AEATSimplifiedRegimeCallback callback, int index) {
		activityTable.clearSelectedIndex();
		farmerActivityPanel = new Model303AEAT2023ActivityFarmer( new IModel303AEATActivityCallback<Mod303ActivityFarmer>() {
					
			@Override
			public void onAccept() {
				ValueChangeEvent.<Mod303>fire(Model303AEAT2023SimplifiedRegimeActivities.this, getModel());
			}
			
			@Override
			public void onRemove() {
				getModel().getActivityFarmerList().get( getActivity().getIndex() ).initialize();
				ValueChangeEvent.<Mod303>fire(Model303AEAT2023SimplifiedRegimeActivities.this, getModel());
			}
	
			@Override
			public Mod303ActivityFarmer getActivity() {
				return getModel().getActivityFarmerList().get(index);
			}
	
			@Override
			public Mod303 getModel() {
				return callback.getModel();
			}
		});
		farmerActivityPanel.addValueChangeHandler(ve -> ValueChangeEvent.<Mod303>fire(
			Model303AEAT2023SimplifiedRegimeActivities.this, callback.getModel()));
		contentLayoutPanel.setWidget(farmerActivityPanel);
	}

	private void selectActivity(IModel303AEATSimplifiedRegimeCallback callback, int index) {
		farmerTable.clearSelectedIndex();
		activityPanel = new Model303AEAT2023Activity( new IModel303AEATActivityCallback<Mod303Activity>() {
					
			@Override
			public void onAccept() {
				ValueChangeEvent.<Mod303>fire(Model303AEAT2023SimplifiedRegimeActivities.this, callback.getModel());
			}
			
			@Override
			public void onRemove() {
				getModel().getActivityList().get( getActivity().getIndex() ).initialize();
				ValueChangeEvent.<Mod303>fire(Model303AEAT2023SimplifiedRegimeActivities.this, callback.getModel());
			}

			@Override
			public Mod303Activity getActivity() {
				return getModel().getActivityList().get(index);
			}

			@Override
			public Mod303 getModel() {
				return callback.getModel();
			}

		});
		activityPanel.addValueChangeHandler(ve -> ValueChangeEvent.<Mod303>fire(
			Model303AEAT2023SimplifiedRegimeActivities.this, callback.getModel()));
		contentLayoutPanel.setWidget(activityPanel);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod303> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void populate(Mod303 mod303) {
		farmerTable.paint();
		if (farmerTable.getSelectedIndex() != null) {
			farmerActivityPanel.populate( mod303.getActivityFarmerList().get( farmerTable.getSelectedIndex() ));
		}
		activityTable.paint();
		if (activityTable.getSelectedIndex() != null) {
			activityPanel.populate( mod303.getActivityList().get( activityTable.getSelectedIndex() ));
		}
	}

}
