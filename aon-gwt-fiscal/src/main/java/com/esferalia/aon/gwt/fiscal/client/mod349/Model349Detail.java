package com.esferalia.aon.gwt.fiscal.client.mod349;

import com.esferalia.aon.gwt.fiscal.client.mod349.Model349Base.IModel349Detail;
import com.esferalia.aon.gwt.fiscal.client.mod349.Model349Base.Model349BaseCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model349Detail extends DockLayoutPanel implements IModel349Detail {
	
	protected interface IModel349DetailCallback {
		void onNameChanged( Mod349Detail detail );
		void onValueChanged( Mod349Detail detail );
	}
	private Model349DetailTable table;
	
	public Model349Detail( Model349BaseCallback callback, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model349DetailTable(callback, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( new SelectionHandler<Mod349Detail>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod349Detail> event) {
				Model349DetailPanel panel = new Model349DetailPanel( event.getSelectedItem(), new IModel349DetailCallback() {					
					
					@Override
					public void onValueChanged(Mod349Detail detail) {
						if (!detail.isDirty()) {
							detail.setDirty(true);
							table.refresh();		
						}
					}
					
					@Override
					public void onNameChanged(Mod349Detail detail) {
						detail.setDirty(true);
						table.refresh();
					}
				}, callback);
				container.setWidget(panel);
				
				Scheduler.get().scheduleDeferred(new Command() {
			        public void execute() {
			        	panel.setFocus(true);
			        }
			    });		

			}
		});
		add(container);
	}

	@Override
	public Integer getSelectedOperatorIndex() {
		return table.getSelectionIndex();
	}
	
}
