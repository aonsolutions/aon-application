package com.esferalia.aon.gwt.fiscal.client.mod369;

import com.esferalia.aon.gwt.fiscal.client.mod369.Model369.Model369Callback;
import com.esferalia.aon.gwt.fiscal.client.mod369.Model369Base.IModel369Correction;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.fiscal.Mod369DetailCorrection;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

//public class Model369Correction extends DockLayoutPanel implements IModel369Correction {
//	
//	protected interface IModel369CorrectionCallback {
//		void onTableChanged( Mod369DetailCorrection partner );
//		void onValueChanged( Mod369DetailCorrection partner);
//		void onDuplicate(Mod369DetailCorrection partner);
//	}
//	private Model369CorrectionTable table;
//	
//	public Model369Correction( Model369Callback callback, Mod369 mod369, Integer selectedIndex ) {
//		super(Unit.PX);
////		table = new Model369CorrectionTable(callback, mod369, selectedIndex);
////		addWest(table, 300);
////		
////		SimpleLayoutPanel container = new SimpleLayoutPanel();
////		table.addSelectionHandler( event -> {
////			Model369CorrectionPanel panel = new Model369CorrectionPanel(event.getSelectedItem(), new IModel369CorrectionCallback() {
////				
////				@Override
////				public void onValueChanged(Mod369DetailCorrection partner) {
////					if (!partner.isDirty()) {
////						partner.setDirty(true);
////						table.refresh();		
////					}
////				}
////				
////				@Override
////				public void onTableChanged(Mod369DetailCorrection partner) {
////					partner.setDirty(true);
////					table.refresh();
////				}
////				@Override
////				public void onDuplicate(Mod369DetailCorrection partner) {
////					table.duplicate(callback,mod369, partner);
////				}
////			});
////			container.setWidget(panel);
////			
////			Scheduler.get().scheduleDeferred(() -> panel.setFocus(true));		
////
////		});
////		add(container);
//	}
//
//	@Override
//	public Integer getSelectedPartnerIndex() {
//		return table.getSelectionIndex();
//	}
//	
//}
