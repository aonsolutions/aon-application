package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Base.IModel347Asset;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Base.Model347BaseCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Asset;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model347Asset2014 extends DockLayoutPanel implements IModel347Asset {
	
	protected interface IModel347AssetCallback {
		void onTableChanged( Mod347Asset asset );
		void onValueChanged( Mod347Asset asset);
	}
	private Model347AssetTable table;
	
	public Model347Asset2014( Model347BaseCallback callback, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model347AssetTable(callback, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( new SelectionHandler<Mod347Asset>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod347Asset> event) {				
				
				if (callback.getMod347().getAdministration() == Administration.GIPUZKOA) {
					// Gipuzkoa
					Model3472014AssetPanelGipuzkoa panel = new Model3472014AssetPanelGipuzkoa(event.getSelectedItem(), new IModel347AssetCallback() {
						
						@Override
						public void onValueChanged(Mod347Asset asset) {
							if (!asset.isDirty()) {
								asset.setDirty(true);
								table.refresh();		
							}
						}
						
						@Override
						public void onTableChanged(Mod347Asset asset) {
							asset.setDirty(true);
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
				else {	
					// Resto de Administraciones
					Model3472014AssetPanel panel = new Model3472014AssetPanel(event.getSelectedItem(), new IModel347AssetCallback() {
						
							@Override
							public void onValueChanged(Mod347Asset asset) {
								if (!asset.isDirty()) {
									asset.setDirty(true);
									table.refresh();		
								}
							}
							
							@Override
							public void onTableChanged(Mod347Asset asset) {
								asset.setDirty(true);
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

			}
		});
		add(container);
	}

	@Override
	public Integer getSelectedAssetIndex() {
		return table.getSelectionIndex();
	}
	
}
