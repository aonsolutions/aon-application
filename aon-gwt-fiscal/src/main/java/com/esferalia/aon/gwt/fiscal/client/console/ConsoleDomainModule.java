package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
 
public class ConsoleDomainModule extends AonLayoutPanel {

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	
	SimpleLayoutPanel container = new SimpleLayoutPanel();
	//	private ConsoleModuleOptions options;

	public ConsoleDomainModule(ConsoleModuleOptions options) {
//		this.options = options;
		AON.ensureInjected();
		this.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		ConsoleDomainFilterPanel filterPanel = new ConsoleDomainFilterPanel(options);
		filterPanel.addValueChangeHandler(e -> search(options, e.getValue()) );
		this.addNorth(filterPanel, 125);
		this.add(container);
	}
	
	private void search(ConsoleModuleOptions options, DomainParams params) {
		ConsoleModule.CONSOLE_SERVICE.getDomains(options.getOccam(),params,new AsyncCallback<LinkedList<Domain>>() {
			
			public void onFailure(Throwable caught) {
				// ERROR
			}

			public void onSuccess(LinkedList<Domain> domains) {
				ConsoleDomainTable table = new ConsoleDomainTable(domains);
				container.setWidget(table);
			}
		});
	}

	private AonToolbar getToolbarPanel() {
		AonToolbar toolbarPanel = new AonToolbar();
		toolbarPanel.setTitle("Extracci\u00F3n de dominios");
		
		return toolbarPanel;
	}

	
}
