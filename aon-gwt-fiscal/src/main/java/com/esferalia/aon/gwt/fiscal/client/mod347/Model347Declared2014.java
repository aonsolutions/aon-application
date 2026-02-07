package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.Model347Callback;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Base.IModel347Declared;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model347Declared2014 extends DockLayoutPanel implements IModel347Declared {
	
	protected interface IModel347DeclaredCallback {
		void onTableChanged( Mod347Declared declared );
		void onValueChanged( Mod347Declared declared);
	}
	private Model347DeclaredTable table;
	private Model3472014DeclaredPanel declaredPanel;
	private Model3472025DeclaredPanelCanarias declaredPanelCanarias;
	
	public Model347Declared2014( Model347Callback callback, Mod347 mod347, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model347DeclaredTable(callback, mod347, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( event -> {
			
			IModel347DeclaredCallback model347DeclaredCallback = new IModel347DeclaredCallback() {
				
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
				
			};
			
			if (mod347.isCanarias()) {
				declaredPanelCanarias = new Model3472025DeclaredPanelCanarias(callback, mod347, event.getSelectedItem(), model347DeclaredCallback);	
			} else {
				declaredPanel = new Model3472014DeclaredPanel(callback, mod347, event.getSelectedItem(), model347DeclaredCallback);	
			}
			
//			SimpleLayoutPanel panel = mod347.isCanarias() ? getDeclaredPanelCanarias(callback, mod347, event) : getDeclaredPanel(callback, mod347, event);	
			
//			if (mod347.isCanarias()) {
//				panel = getDeclaredPanelCanarias(callback, mod347, event);
//			} else {
//				panel = getDeclaredPanel(callback, mod347, event);	
//			}
			
//			Model3472014DeclaredPanel panel = new Model3472014DeclaredPanel(callback,mod347,event.getSelectedItem(), new IModel347DeclaredCallback() {
//				
//				@Override
//				public void onValueChanged(Mod347Declared declared) {
//					if (!declared.isDirty()) {
//						declared.setDirty(true);
//						table.refresh();		
//					}
//				}
//				
//				@Override
//				public void onTableChanged(Mod347Declared declared) {
//					declared.setDirty(true);
//					table.refresh();
//				}
//				
//			});
//			container.setWidget(panel);
//			
//			Scheduler.get().scheduleDeferred(new Command() {
//		        public void execute() {
//		        	panel.setFocus(true);
//		        }
//		    });
			
			container.setWidget(mod347.isCanarias() ? declaredPanelCanarias : declaredPanel);
			
			Scheduler.get().scheduleDeferred(new Command() {
		        public void execute() {
		        	if (mod347.isCanarias()) 
		        		declaredPanelCanarias.setFocus(true);
		        	else 
		        		declaredPanel.setFocus(true);	  
		        }
			});

		});
		add(container);
	}

//	private Model3472014DeclaredPanel getDeclaredPanel(Model347Callback callback, Mod347 mod347, SelectionEvent<Mod347Declared> event) {
//		
//		return new Model3472014DeclaredPanel(callback,mod347,event.getSelectedItem(), new IModel347DeclaredCallback() {
//		
//			@Override
//			public void onValueChanged(Mod347Declared declared) {
//				if (!declared.isDirty()) {
//					declared.setDirty(true);
//					table.refresh();		
//				}
//			}
//			
//			@Override
//			public void onTableChanged(Mod347Declared declared) {
//				declared.setDirty(true);
//				table.refresh();
//			}
//			
//		});
//		
//	}
//	
//	private Model3472025DeclaredPanelCanarias getDeclaredPanelCanarias(Model347Callback callback, Mod347 mod347, SelectionEvent<Mod347Declared> event) {
//		
//		return new Model3472025DeclaredPanelCanarias(callback,mod347,event.getSelectedItem(), new IModel347DeclaredCallback() {
//			
//			@Override
//			public void onValueChanged(Mod347Declared declared) {
//				if (!declared.isDirty()) {
//					declared.setDirty(true);
//					table.refresh();		
//				}
//			}
//			
//			@Override
//			public void onTableChanged(Mod347Declared declared) {
//				declared.setDirty(true);
//				table.refresh();
//			}
//			
//		});		
//		
//	}
	
	@Override
	public Integer getSelectedDeclaredIndex() {
		return table.getSelectionIndex();
	}
	
}
