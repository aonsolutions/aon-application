package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleRowQueryViewer.AonConsoleRowViewerCallback;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class ConsoleRowCompositeQueryViewer extends SimpleLayoutPanel {
	
	private final AonTabLayoutPanel tab = new AonTabLayoutPanel(30, Unit.PX);
	
	ConsoleRowCompositeQueryViewer(ConsoleTableRow tableRow) {
		show(tableRow);
	}
	
	ConsoleRowCompositeQueryViewer(LinkedList<ConsoleTableRow> rows) {
		if (AonCollectionUtils.isEmpty(rows)) {
			Label label = new Label("Fila no encontrada");
			label.setStyleName(AON.CSS.aonMargin());
			label.addStyleName(AON.CSS.aonBorder());
			label.addStyleName(AON.CSS.aonBold());
			label.addStyleName(AON.CSS.aonTextCenter());
			setWidget( label );
		} else  if (AonCollectionUtils.size(rows) == 1) {
			show(rows.get(0));
		} else {
			setWidget( tab );
			String tabLabel = "Lista de " + rows.get(0).getTable();
			tab.add(new ConsoleRowQueryList( rows, new AonConsoleRowCompositeViewerCallback() )
					,new AonCloseTab(tabLabel, false)
					,tabLabel);
		}
	}

	private void show(ConsoleTableRow tableRow) {
		setWidget( tab);
		String tabLabel = tableRow.getTable() + " (" + tableRow.getId() + ")";
		tab.add(new ConsoleRowQueryViewer( tableRow, new AonConsoleRowCompositeViewerCallback() )
				,new AonCloseTab(tabLabel, false)
				,tabLabel);
	}

	class AonConsoleRowCompositeViewerCallback implements AonConsoleRowViewerCallback {
		@Override
		public void deleted(ConsoleTableRow row) {
			if (row == null) {
				AonMessageDialog.error("Fila no encontrada");
			} else {
				String tabLabel = row.getTable() + " (" + row.getId() + ")";
				tab.remove(tabLabel);
			}
		}

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
