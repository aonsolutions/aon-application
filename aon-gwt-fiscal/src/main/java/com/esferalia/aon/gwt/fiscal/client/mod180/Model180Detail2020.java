package com.esferalia.aon.gwt.fiscal.client.mod180;

import com.esferalia.aon.gwt.fiscal.client.mod180.Model180Base.IModel180Detail;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180Base.Model180BaseCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model180Detail2020 extends DockLayoutPanel implements IModel180Detail {
	
	protected interface IModel180DetailCallback {
		void onNameChanged( Mod180Detail detail );
		void onValueChanged( Mod180Detail detail );
	}
	private Model180DetailTable table;
	
	public Model180Detail2020( Model180BaseCallback callback, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model180DetailTable(callback, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( new SelectionHandler<Mod180Detail>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod180Detail> event) {
				Model1802020DetailPanel panel = new Model1802020DetailPanel(event.getSelectedItem(), new IModel180DetailCallback() {
					
					@Override
					public void onValueChanged(Mod180Detail detail) {
						if (!detail.isDirty()) {
							detail.setDirty(true);
							table.refresh();		
						}
					}
					
					@Override
					public void onNameChanged(Mod180Detail detail) {
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

			}
		});
		add(container);
	}

	@Override
	public Integer getSelectedPerceptorIndex() {
		return table.getSelectionIndex();
	}
	
}
