package com.esferalia.aon.gwt.fiscal.client.mod369;

import com.esferalia.aon.gwt.fiscal.client.mod369.Model369.Model369Callback;
import com.esferalia.aon.gwt.fiscal.client.mod369.Model369Base.IModel369Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Detail;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

//public class Model369Detail extends DockLayoutPanel implements IModel369Detail {
//	
//	protected interface IModel369DetailCallback {
//		void onTableChanged( Mod369Detail detail );
//		void onValueChanged( Mod369Detail detail);
//	}
//	private Model369DetailTable table;
//	
//	public Model369Detail(Model369Callback callback, Mod369 mod369, Integer selectedIndex) {
//		super(Unit.PX);
//		table = new Model369DetailTable(callback, mod369, selectedIndex);
//		addWest(table, 300);
//		
//		SimpleLayoutPanel container = new SimpleLayoutPanel();
//		table.addSelectionHandler( event -> {
//			Model369DetailPanel panel = new Model369DetailPanel(event.getSelectedItem(), new IModel369DetailCallback() {
//				
//				@Override
//				public void onValueChanged(Mod369Detail detail) {
//					if (!detail.isDirty()) {
//						detail.setDirty(true);
//						table.refresh();		
//					}
//				}
//				
//				@Override
//				public void onTableChanged(Mod369Detail detail) {
//					detail.setDirty(true);
//					table.refresh();
//				}
//			});
//			container.setWidget(panel);
//			
//			Scheduler.get().scheduleDeferred(() -> {
//		        panel.setFocus(true);
//		    });		
//		});
//		add(container);
//	}
//
//	@Override
//	public Integer getSelectedDetailIndex() {
//		return table.getSelectionIndex();
//	}
//	
//}
