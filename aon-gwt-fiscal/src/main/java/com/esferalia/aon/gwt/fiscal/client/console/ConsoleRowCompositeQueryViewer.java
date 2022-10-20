package com.esferalia.aon.gwt.fiscal.client.console;

import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleRowQueryViewer.AonConsoleRowViewerCallback;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class ConsoleRowCompositeQueryViewer extends SimpleLayoutPanel {
	
	private final AonTabLayoutPanel tab;
	
	ConsoleRowCompositeQueryViewer(ConsoleTableRow tableRow) {
		tab = new AonTabLayoutPanel(30, Unit.PX); 
		setWidget( tab);
		String tabLabel = tableRow.getTable() + " (" + tableRow.getId() + ")";
		tab.add(new ConsoleRowQueryViewer( tableRow, new AonConsoleRowCompositeViewerCallback() )
				,new AonCloseTab(tabLabel, false)
				,tabLabel);
		
	}
	
	class AonConsoleRowCompositeViewerCallback implements AonConsoleRowViewerCallback {

		@Override
		public void onLink(ConsoleTableRow row) {
			ConsoleModule.CONSOLE_SERVICE.getTableRow(row ,new AsyncCallback<ConsoleTableRow>() {
				@Override
				public void onFailure(Throwable caught) {
					AonMessageDialog.error("Error: " + caught.getMessage());
				}

				@Override
				public void onSuccess(ConsoleTableRow tableRow) {
					if (tableRow == null) {
						AonMessageDialog.error("Fila no encontrada");
					} else {
						String tabLabel = tableRow.getTable() + " (" + tableRow.getId() + ")";
						Widget w = tab.getWidget(tabLabel);
						if (w == null) {
							AonCloseTab closeTab = new AonCloseTab(tabLabel, true);
							closeTab.addCloseHandler( e -> tab.remove(tabLabel));
							tab.add(new ConsoleRowQueryViewer( tableRow , new AonConsoleRowCompositeViewerCallback()),closeTab,tabLabel);
						}
						tab.selectTab(tabLabel);
					}
				}

			});
		}
		
	}
}
