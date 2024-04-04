package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131.Model131Callback;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131Activity2023.IMod131ActivityCallback;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.mod131.Model131AEATScript;
import com.esferalia.aon.occam.api.model.fiscal.mod131.Model131ScriptProvider;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

class Model131AEAT2023 extends Model131AEAT {
	
	Model131AEAT2023(Mod131 mod131, Model131Callback callback) {
		super(mod131, callback);
	}
	
	@Override
	protected void paintLiquidationTab(TabLayoutPanel tabPanel, final Model131Callback callback) {
		ScrollPanel liquidationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		FlexTable table = new FlexTable();
		container.add(table);
		defineTable(table);
		for (IModelScript<Mod131Key> ms : Model131ScriptProvider.obtainScript(getModel())) {
			if (ms.paintHeaderBefore()) {
				paintHeader(table);
			}
			paintRow(table,getCallback(),ms);	
			if (ms == Model131AEATScript.R00) {
				paintActivityRow(table);
			}
		}
		liquidationScrollPanel.setWidget(container);
		tabPanel.add(liquidationScrollPanel, AON.MSG.liquidacion());
	}
	
	private void paintActivityRow(FlexTable table) {
		int row = table.getRowCount();
		Model131ActivityTable2023 activityTable = new Model131ActivityTable2023();
		activityTable.paint(getModel().getActivities());
		activityTable.addSelectionHandler(event -> {
			final Mod131Activity original = Mod131Activity.clone(event.getSelectedItem()); 
			int idx = 0;
			for (int i = 0; i < getModel().getActivities().size() ; i++ ) {
				if (getModel().getActivities().get(i) == event.getSelectedItem()) {
					idx = i;
				}
			}
			final int currentIndex = idx;
			final AonCustomDialog dialog = new AonCustomDialog();
			IMod131ActivityCallback activityCallback = new IMod131ActivityCallback() {
				
				@Override
				public Mod131 getModel() {
					return Model131AEAT2023.this.getModel();
				}
				
				@Override
				public void onCancel() {
					dialog.hide();
					getModel().getActivities().set(currentIndex, original);
					calculateAndRefresh( getCallback() );
					activityTable.paint(getModel().getActivities());
				}
				
				@Override
				public void onAccept(Mod131Activity act) {
					dialog.hide();
					getModel().getActivities().set(currentIndex, act);
					calculateAndRefresh( getCallback() );
					activityTable.paint(getModel().getActivities());
				}
				
				@Override
				public Mod131Activity getActivity() {
					return event.getSelectedItem();
				}

				@Override
				public void onRemove() {
					dialog.hide();
					for (int i = 0; i < getModel().getActivities().size() ; i++ ) {
						if (getModel().getActivities().get(i) == event.getSelectedItem()) {
							getModel().getActivities().get(i).initialize();
						}
					}
					calculateAndRefresh( getCallback() );
					activityTable.paint(getModel().getActivities());
				}

				@Override
				public Model131ModuleOptions getOptions() {
					return getCallback().getOptions();
				}
			};
			Model131Activity2023 actPanel = new Model131Activity2023(activityCallback);
			dialog.setCaption(event.getSelectedItem().getFullDescription());
			dialog.setGlassEnabled(true);
			dialog.setAnimationEnabled(true);
			dialog.add(actPanel);
			dialog.setWidth("700px");
			dialog.setHeight("600px");
			dialog.show();
			dialog.center();
		});
		FlowPanel tableContainer = new FlowPanel();
		tableContainer.add( activityTable ) ;
		table.setWidget(row, 0, tableContainer );
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	@Override
	protected void decorateLiquidationTab(Model131Callback callback, Mod131 mod) {
		// Nada
	}
	
}
