package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleRowCompositeQueryViewer.AonConsoleRowCompositeViewerCallback;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConsoleRowQueryList extends SimpleLayoutPanel {

	public ConsoleRowQueryList(LinkedList<ConsoleTableRow> rows, AonConsoleRowCompositeViewerCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		setWidget( scroll);
		FlowPanel container = new FlowPanel();
		container.setStyleName(AON.CSS.aonMarginBottom());
		scroll.setWidget( container );

		FlowPanel header = new FlowPanel();
		header.setStyleName(AON.CSS.aonMarginBottom());
		String msg = "Tabla: " + rows.get(0).getTable();
		Label headerLabel = new Label(msg);
		headerLabel.setStyleName(AON.CSS.aonBold());
		headerLabel.addStyleName(AON.CSS.aonFontLarger());
		headerLabel.addStyleName(AON.CSS.aonTextCenter());
		headerLabel.addStyleName(AON.CSS.aonBorderBottom());
		header.add(headerLabel);
		
		container.add(header);
		container.add(getGrid(rows, callback));
	}

	private Widget getGrid(LinkedList<ConsoleTableRow> rows, AonConsoleRowCompositeViewerCallback callback) {
		AonDisplayGrid grid = new AonDisplayGrid();
		AonDisplayGridHeaderRow headerRow = grid.addHeaderRow();
		rows.get(0).getFields()
			.values()
			.stream()
			.forEach( field -> headerRow.addCell( new Label(field.getColumn())));
		AonCollectionUtils.stream(rows)
			.forEach( tr -> {
				AonDisplayGridRow gridRow = grid.addRow();
				tr.getFields()
					.values()
					.stream()
					.forEach( field -> gridRow.addCell( ConsoleRowQueryViewer.getTextBoxForList(tr, callback, field) ));
			});
		return grid;
	}

}
