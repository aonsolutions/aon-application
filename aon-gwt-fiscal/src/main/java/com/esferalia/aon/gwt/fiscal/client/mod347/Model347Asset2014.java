package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.fiscal.client.mod347.Model347.Model347Callback;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Base.IModel347Asset;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Asset;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model347Asset2014 extends DockLayoutPanel implements IModel347Asset {
	
	protected interface IModel347AssetCallback {
		void onTableChanged( Mod347Asset asset );
		void onValueChanged( Mod347Asset asset);
	}
	private Model347AssetTable table;
	
	public Model347Asset2014( Model347Callback callback, Mod347 mod347, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model347AssetTable(callback, mod347, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( event -> {				
			
			if (mod347.isGipuzkoa()) {
				Model3472014AssetPanelGipuzkoa panel1 = new Model3472014AssetPanelGipuzkoa(event.getSelectedItem(), new IModel347AssetCallback() {
					
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
							
				container.setWidget(panel1);
				
				Scheduler.get().scheduleDeferred(() -> panel1.setFocus(true));
					
			}
			else {	
				Model3472014AssetPanel panel2 = new Model3472014AssetPanel(event.getSelectedItem(), new IModel347AssetCallback() {
					
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
				container.setWidget(panel2);
				Scheduler.get().scheduleDeferred(() -> panel2.setFocus(true));
			}

		});
		add(container);
	}

	@Override
	public Integer getSelectedAssetIndex() {
		return table.getSelectionIndex();
	}
	
}
