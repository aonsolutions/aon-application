package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131.Model131Callback;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.mod131.Model131ScriptProvider;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

class Model131AEAT2024 extends Model131AEAT {
	
	private Model1312024Activities activities;
	
	Model131AEAT2024(Mod131 mod131, Model131Callback callback) {
		super(mod131, callback);
	}
	
	@Override
	protected void decorateLiquidationTab(final Model131Callback callback, Mod131 model) {
		activities.populate( () -> model );
	}
	
	@Override
	protected void paintLiquidationTab(TabLayoutPanel tabPanel, final Model131Callback callback) {
		activities = new Model1312024Activities(callback.getOptions(),() -> getModel());
		activities.addValueChangeHandler(e -> calculateAndRefresh( callback ));
		tabPanel.add(activities, AON.MSG.liquidacion() + ": ACTIVIDADES");
		

		SimpleLayoutPanel layout = new SimpleLayoutPanel();
		ScrollPanel scroll = new ScrollPanel();
		FlexTable table = new FlexTable();
		defineTable(table);
		for (IModelScript<Mod131Key> ms : Model131ScriptProvider.obtainScript(getModel())) {
			if (ms.paintHeaderBefore()) {
				paintHeader(table);
			}
			paintRow(table,getCallback(),ms);	
		}
		scroll.setWidget(table);
		layout.setWidget(scroll);
		tabPanel.add(layout, AON.MSG.liquidacion() + ": RESULTADO");
		

//		DockLayoutPanel dockContainer = new DockLayoutPanel(Unit.PX);
//		
//		activities = new Model1312024Activities(callback.getOptions(),() -> getModel());
//		activities.addValueChangeHandler(e -> calculateAndRefresh( callback ));
//		dockContainer.addNorth( activities, 300);
//		
//		SimpleLayoutPanel layout = new SimpleLayoutPanel();
//		ScrollPanel scroll = new ScrollPanel();
//		FlexTable table = new FlexTable();
//		defineTable(table);
//		for (IModelScript<Mod131Key> ms : Model131ScriptProvider.obtainScript(getModel())) {
//			if (ms.paintHeaderBefore()) {
//				paintHeader(table);
//			}
//			paintRow(table,getCallback(),ms);	
//		}
//		scroll.setWidget(table);
//		layout.setWidget(scroll);
//		dockContainer.add( layout );
//		tabPanel.add(dockContainer, AON.MSG.liquidacion());
	}
	
	
}
