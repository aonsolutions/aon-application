package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Base.IModel184Partner;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Base.Model184BaseCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model184Partner2014 extends DockLayoutPanel implements IModel184Partner {
	
	protected interface IModel184PartnerCallback {
		void onTableChanged( Mod184Partner partner );
		void onValueChanged( Mod184Partner partner);
	}
	private Model184PartnerTable table;
	
	public Model184Partner2014( Model184BaseCallback callback, Integer selectedIndex ) {
		super(Unit.PX);
		table = new Model184PartnerTable(callback, selectedIndex);
		addWest(table, 300);
		
		SimpleLayoutPanel container = new SimpleLayoutPanel();
		table.addSelectionHandler( new SelectionHandler<Mod184Partner>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod184Partner> event) {
				Model1842014PartnerPanel panel = new Model1842014PartnerPanel(event.getSelectedItem(), new IModel184PartnerCallback() {
					
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
	public Integer getSelectedPartnerIndex() {
		return table.getSelectionIndex();
	}
	
}
