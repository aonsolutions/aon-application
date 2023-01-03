package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.fiscal.client.mod190.Model190.Model190Callback;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190Base.IModel190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model190AEATDetail2022 extends DockLayoutPanel implements IModel190Detail {
	
	protected interface IModel190DetailCallback {
		void onNameChanged( Mod190Detail detail );
		void onValueChanged( Mod190Detail detail );
	}
	private Model190DetailTable table;
	
	public Model190AEATDetail2022( Model190Callback callback, Mod190 model, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model190DetailTable(callback, model, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( event -> {
			Model190AEAT2022DetailPanel panel = new Model190AEAT2022DetailPanel(event.getSelectedItem(), new IModel190DetailCallback() {
				
				@Override
				public void onValueChanged(Mod190Detail detail) {
					if (!detail.isDirty()) {
						detail.setDirty(true);
						table.refresh();		
					}
				}
				
				@Override
				public void onNameChanged(Mod190Detail detail) {
					detail.setDirty(true);
					table.refresh();
				}
			});
			container.setWidget(panel);
			
			Scheduler.get().scheduleDeferred(new Command() {
		        public void execute() {
		        	panel.setFocus(true);
		        }
		    });		

		});
		add(container);
	}

	@Override
	public Integer getSelectedPerceptorIndex() {
		return table.getSelectionIndex();
	}
	
}
