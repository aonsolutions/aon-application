package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.Model184Callback;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Base.IModel184Partner;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model184Partner2022 extends DockLayoutPanel implements IModel184Partner {
	
	protected interface IModel184PartnerCallback {
		void onTableChanged( Mod184Partner partner );
		void onValueChanged( Mod184Partner partner);
		void onDuplicate(Mod184Partner partner);
	}
	private Model184PartnerTable table;
	
	public Model184Partner2022( Model184Callback callback, Mod184 mod184, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model184PartnerTable(callback, mod184, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( event -> {
			Model1842022PartnerPanel panel = new Model1842022PartnerPanel(event.getSelectedItem(), new IModel184PartnerCallback() {
				
				@Override
				public void onValueChanged(Mod184Partner partner) {
					if (!partner.isDirty()) {
						partner.setDirty(true);
						table.refresh();		
					}
				}
				
				@Override
				public void onTableChanged(Mod184Partner partner) {
					partner.setDirty(true);
					table.refresh();
				}
				@Override
				public void onDuplicate(Mod184Partner partner) {
					table.duplicate(callback,mod184, partner);
				}
			});
			container.setWidget(panel);
			
			Scheduler.get().scheduleDeferred(() -> panel.setFocus(true));		

		});
		add(container);
	}

	@Override
	public Integer getSelectedPartnerIndex() {
		return table.getSelectionIndex();
	}
	
}
