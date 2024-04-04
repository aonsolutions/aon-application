package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonGroupPanel;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class Model1312024Activities extends DockLayoutPanel implements HasValueChangeHandlers<Mod131>{
	
	
	static interface IModel131ActivitiesCallback {
		Mod131 getModel();
	}
	
	static interface IModel131ActivityCallback {
		Model131ModuleOptions getOptions();
		Mod131 getModel();
		Mod131Activity getActivity();
		void onAccept();
		void onRemove();
	}
	
	private Model131ActivityTable2024 activityTable;
	private Model131Activity2024 activityPanel;
	private SimpleLayoutPanel contentLayoutPanel;
	
	Model1312024Activities( Model131ModuleOptions options, IModel131ActivitiesCallback callback ) {
		super(Unit.PX);
		
		SimpleLayoutPanel sidebarLayoutPanel = new SimpleLayoutPanel();
		ScrollPanel sidebarScrollPanel = new ScrollPanel();
		FlowPanel sidebarPanel = new FlowPanel();

		contentLayoutPanel = new SimpleLayoutPanel();
		
		activityTable = new Model131ActivityTable2024(callback);
		activityTable.addSelectionHandler( e -> selectActivity(options, callback, e.getSelectedItem().getIndex() ) );
		sidebarPanel.add(AonGroupPanel.get(AON.MSG.simplifieedActivities(), activityTable ));
		sidebarScrollPanel.setWidget(sidebarPanel);
		sidebarLayoutPanel.setWidget(sidebarScrollPanel);
		
		addWest( sidebarLayoutPanel, 400);
		add( contentLayoutPanel);
	}
	
	private void selectActivity(Model131ModuleOptions options, IModel131ActivitiesCallback callback, int index) {
		activityPanel = new Model131Activity2024( new IModel131ActivityCallback() {
			@Override
			public Model131ModuleOptions getOptions() {
				return options;
			}
			@Override
			public void onAccept() {
				ValueChangeEvent.<Mod131>fire(Model1312024Activities.this, callback.getModel());
			}
			
			@Override
			public void onRemove() {
				getModel().getActivities().get( getActivity().getIndex() ).initialize();
				ValueChangeEvent.<Mod131>fire(Model1312024Activities.this, callback.getModel());
			}

			@Override
			public Mod131Activity getActivity() {
				return getModel().getActivities().get(index);
			}

			@Override
			public Mod131 getModel() {
				return callback.getModel();
			}

		});
		activityPanel.addValueChangeHandler(ve -> ValueChangeEvent.<Mod131>fire(Model1312024Activities.this, callback.getModel()));
		contentLayoutPanel.setWidget(activityPanel);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Mod131> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void populate(IModel131ActivitiesCallback callback) {
		activityTable.paint(callback);
		if (activityTable.getSelectedIndex() != null) {
			activityPanel.populate( callback.getModel().getActivities().get( activityTable.getSelectedIndex() ));
		}
	}

}
