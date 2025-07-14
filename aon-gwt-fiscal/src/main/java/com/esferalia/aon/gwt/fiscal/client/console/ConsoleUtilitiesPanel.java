package com.esferalia.aon.gwt.fiscal.client.console;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTable.ConsoleDomainTableCallback;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
 
public class ConsoleUtilitiesPanel extends DockLayoutPanel implements HasValueChangeHandlers<DomainParams>{
	
	private SimpleLayoutPanel container = new SimpleLayoutPanel();
	
	public ConsoleUtilitiesPanel(final ConsoleDomainTableCallback callback) {
		super( Unit.PX );
		addSidebar( callback );
		add(container);
	}

	private void addSidebar(final ConsoleDomainTableCallback callback) {
		SimpleLayoutPanel sidebar = new SimpleLayoutPanel();
		sidebar.setStyleName(AON.CSS.aonBackgroundLigthBlue());
		
		ScrollPanel sidebarScroll = new ScrollPanel();
		sidebarScroll.setStyleName(AON.CSS.aonScrollArea());
		sidebar.setWidget( sidebarScroll );
		
		FlowPanel optionasContainer = new FlowPanel();
		sidebarScroll.setWidget( optionasContainer );
		
		addWest( sidebar , 300 );
		
		AonCollectionUtils.stream(ConsoleUtilities.values())
			.map( u -> {
				Label l =new Label( u.getDescription() );
				l.setStyleName(AON.AON_CSS.aonClickableBlock());
				l.addStyleName(AON.AON_CSS.aonPadding());
				l.addClickHandler( e -> run(callback, u));
				return l;
			})
			.forEach(optionasContainer::add);
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DomainParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	private void run(ConsoleDomainTableCallback callback, ConsoleUtilities u) {
		AonConsoleProgress aonConsole = new AonConsoleProgress( true );
		container.clear();
		container.add(aonConsole);
		callback.runUtility( u, aonConsole);
	}
}
