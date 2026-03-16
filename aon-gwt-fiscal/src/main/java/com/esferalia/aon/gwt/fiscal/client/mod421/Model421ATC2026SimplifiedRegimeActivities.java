package com.esferalia.aon.gwt.fiscal.client.mod421;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonGroupPanel;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.Mod421Activity;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class Model421ATC2026SimplifiedRegimeActivities extends DockLayoutPanel implements HasValueChangeHandlers<Mod421>{
	
	static interface IModel421ATCSimplifiedRegimeCallback {
		Mod421 getModel();
	}
	
	static interface IModel421ATCActivityCallback<E> {
		Mod421 getModel();
		E getActivity();
		void onAccept();
		void onRemove();
	}
	
	private Model421ATC2026ActivityTable activityTable;
	private Model421ATC2026Activity activityPanel;
	private SimpleLayoutPanel contentLayoutPanel;
	
	Model421ATC2026SimplifiedRegimeActivities( IModel421ATCSimplifiedRegimeCallback callback ) {
		super(Unit.PX);
		
		SimpleLayoutPanel sidebarLayoutPanel = new SimpleLayoutPanel();
		ScrollPanel sidebarScrollPanel = new ScrollPanel();
		FlowPanel sidebarPanel = new FlowPanel();

		contentLayoutPanel = new SimpleLayoutPanel();
		
		activityTable = new Model421ATC2026ActivityTable(callback);
		activityTable.addSelectionHandler( e -> selectActivity(callback, e.getSelectedItem().getIndex()) );
		sidebarPanel.add(AonGroupPanel.get(AON.MSG.simplifieedActivities(), activityTable ));
		sidebarScrollPanel.setWidget(sidebarPanel);
		sidebarLayoutPanel.setWidget(sidebarScrollPanel);
		
		addWest( sidebarLayoutPanel, 400);
		add( contentLayoutPanel);
	}
	
	private void selectActivity(IModel421ATCSimplifiedRegimeCallback callback, int index) {
		activityPanel = new Model421ATC2026Activity( new IModel421ATCActivityCallback<Mod421Activity>() {
					
			@Override
			public void onAccept() {
				ValueChangeEvent.<Mod421>fire(Model421ATC2026SimplifiedRegimeActivities.this, callback.getModel());
			}
			
			@Override
			public void onRemove() {
				getModel().getActivityList().get( getActivity().getIndex() ).initialize();
				ValueChangeEvent.<Mod421>fire(Model421ATC2026SimplifiedRegimeActivities.this, callback.getModel());
				selectActivity(callback, index);
			}

			@Override
			public Mod421Activity getActivity() {
				return getModel().getActivityList().get(index);
			}

			@Override
			public Mod421 getModel() {
				return callback.getModel();
			}

		});
		activityPanel.addValueChangeHandler(ve -> ValueChangeEvent.<Mod421>fire(Model421ATC2026SimplifiedRegimeActivities.this, callback.getModel()));
		contentLayoutPanel.setWidget(activityPanel);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod421> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void populate(Mod421 mod421) {
		activityTable.paint();
		if (activityTable.getSelectedIndex() != null) {
			activityPanel.populate(mod421.getActivityList().get(activityTable.getSelectedIndex()), mod421.isEditable());
		}
	}

}
