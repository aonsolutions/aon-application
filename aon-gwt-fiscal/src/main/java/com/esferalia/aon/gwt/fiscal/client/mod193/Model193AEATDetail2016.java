package com.esferalia.aon.gwt.fiscal.client.mod193;

import com.esferalia.aon.gwt.fiscal.client.mod193.Model193AEAT2016DetailPanel;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193Base.IModel193Detail;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193Base.Model193BaseCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model193AEATDetail2016 extends DockLayoutPanel implements IModel193Detail {
	
	protected interface IModel193DetailCallback {
		void onNameChanged( Mod193Detail detail );
		void onValueChanged( Mod193Detail detail );
	}
	private Model193DetailTable table;
	
	public Model193AEATDetail2016( Model193BaseCallback callback, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model193DetailTable(callback, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( new SelectionHandler<Mod193Detail>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod193Detail> event) {
				Model193AEAT2016DetailPanel panel = new Model193AEAT2016DetailPanel(event.getSelectedItem(), new IModel193DetailCallback() {
					
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
