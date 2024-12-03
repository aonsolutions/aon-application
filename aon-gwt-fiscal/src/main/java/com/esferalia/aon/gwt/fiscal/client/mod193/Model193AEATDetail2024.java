package com.esferalia.aon.gwt.fiscal.client.mod193;

import com.esferalia.aon.gwt.fiscal.client.mod193.Model193.Model193Callback;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193Base.IModel193Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model193AEATDetail2024 extends DockLayoutPanel implements IModel193Detail {
	
	protected interface IModel193DetailCallback {
		void onNameChanged( Mod193Detail detail );
		void onValueChanged( Mod193Detail detail );
	}
	private Model193DetailTable table;
	
	public Model193AEATDetail2024( Model193Callback callback, Mod193 model, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model193DetailTable(callback, model, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( event -> {
			Model193AEAT2024DetailPanel panel = new Model193AEAT2024DetailPanel(event.getSelectedItem(), new IModel193DetailCallback() {
					
				@Override
				public void onValueChanged(Mod193Detail detail) {
					if (!detail.isDirty()) {
						detail.setDirty(true);
						table.refresh();		
					}
				}
				
				@Override
				public void onNameChanged(Mod193Detail detail) {
					detail.setDirty(true);
					table.refresh();
				}
			});
			
			container.setWidget(panel);
			
			Scheduler.get().scheduleDeferred(() -> {
		        panel.setFocus(true);		        
		    });
		});
		add(container);
	}

	@Override
	public Integer getSelectedPerceptorIndex() {
		return table.getSelectionIndex();
	}
	
}
