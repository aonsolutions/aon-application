package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTable.ConsoleDomainTableCallback;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
 
public class ConsoleDomainModule extends AonLayoutPanel {

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	private ConsoleModuleOptions options;
	private HashSet<Integer> checkedList = new HashSet<>();
	private SimpleLayoutPanel container = new SimpleLayoutPanel();

	public ConsoleDomainModule(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		this.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		ConsoleDomainFilterPanel filterPanel = new ConsoleDomainFilterPanel(options);
		filterPanel.addValueChangeHandler(e -> search(options, e.getValue()) );
		this.addNorth(filterPanel, ConsoleDomainFilterPanel.HEIGTH);
		this.add(container);
	}
	
	private void search(ConsoleModuleOptions options, DomainParams params) {
		final AonToast toast = new AonToast();
		final InlineLabel label =  new InlineLabel("Un momento, por favor ...");
		toast.show("Cargando ...", label);
		ConsoleModule.CONSOLE_SERVICE.getDomains(options.getOccam(),params,new AsyncCallback<LinkedList<Domain>>() {
			
			public void onFailure(Throwable caught) {
				toast.hide();
				showErrorPanel(caught.getMessage());
			}

			public void onSuccess(LinkedList<Domain> domains) {
				toast.hide();
				container.setWidget(getTable( domains ) );
			}

		});
	}

	private AonToolbar getToolbarPanel() {
		AonToolbar toolbarPanel = new AonToolbar();
		toolbarPanel.setTitle("Extracci\u00F3n de dominios");
		
		
		
		return toolbarPanel;
	}
	
	private Widget getTable(LinkedList<Domain> domains) {
		checkedList.clear();
		ConsoleDomainTable table = new ConsoleDomainTable(domains, new ConsoleDomainTableCallback() {
			@Override
			public Occam getOccam() {
				return options.getOccam();
			}
			
			@Override
			public void onDelete(Domain domain, AsyncCallback<Domain> cbk) {
				ConsoleModule.CONSOLE_SERVICE.deleteDomain(null, null, null);
				
				
//				Window.alert("deleteDomain ..: " + domain.getId() + " " +  domain.getName());
			}

			@Override
			public void showError(String message) {
				// TODO Auto-generated method stub
				
			}
		});
		table.addSelectionHandler(e -> check( e.getSelectedItem() ));
		return table;
	}

	private void check(Domain domain) {
		if (domain != null) {
			if (checkedList.contains(domain.getId())) {
				checkedList.remove(domain.getId());
			} else {
				checkedList.add(domain.getId());
			}
		}
	}

	
}
