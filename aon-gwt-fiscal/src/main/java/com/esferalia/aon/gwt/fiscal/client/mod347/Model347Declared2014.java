package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Base.IModel347Declared;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Base.Model347BaseCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model347Declared2014 extends DockLayoutPanel implements IModel347Declared {
	
	protected interface IModel347DeclaredCallback {
		void onTableChanged( Mod347Declared declared );
		void onValueChanged( Mod347Declared declared);
	}
	private Model347DeclaredTable table;
	
	public Model347Declared2014( Model347ModuleOptions options, Model347BaseCallback callback, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model347DeclaredTable(callback, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( new SelectionHandler<Mod347Declared>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod347Declared> event) {
				Model3472014DeclaredPanel panel = new Model3472014DeclaredPanel(options,event.getSelectedItem(), new IModel347DeclaredCallback() {
					
					@Override
					public void onValueChanged(Mod347Declared declared) {
						if (!declared.isDirty()) {
							declared.setDirty(true);
							table.refresh();		
						}
					}
					
					@Override
					public void onTableChanged(Mod347Declared declared) {
						declared.setDirty(true);
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
	public Integer getSelectedDeclaredIndex() {
		return table.getSelectionIndex();
	}
	
}
