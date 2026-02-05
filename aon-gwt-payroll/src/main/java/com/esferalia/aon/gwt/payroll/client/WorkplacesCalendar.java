package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class WorkplacesCalendar extends SimpleLayoutPanel {
	
	// ----- LOGGER 
	
	private static final Logger LOGGER = Logger.getLogger(WorkplacesCalendar.class.getName());
	static { LOGGER.addHandler(new ConsoleLogHandler()); }
	
	// ----- TabPanel
	
	private AonTabLayoutPanel tabPanel;
	
	// ----- Variables
	
	private final DomainEnterprisesServiceAsync service = DomainEnterprisesServiceAsync.newInstance();

	private List<Workplace> workplaces;
	
	// ----- Constructor
	
	public WorkplacesCalendar() {
		service.getWorkplaces(new AsyncCallback<List<Workplace>>() {
			
			@Override
			public void onSuccess(List<Workplace> workplacesDB) {
				workplaces = workplacesDB;
				initializeTabPanel();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Get Workplaces : " + caught.getMessage());
			}
			
		});
	}
	
	private void initializeTabPanel() {
		tabPanel = new AonTabLayoutPanel(35, Unit.PX);
		tabPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		if(workplaces.isEmpty()) {
			
			tabPanel.add(new Label("No existen centros de trabajo para el dominio seleccionado"), "Centro Trabajo");
			
		} else {
			
			workplaces.forEach(workplace -> {
				tabPanel.add(new WorkplaceCalendar(workplace), workplace.getDescription());
			});
			
		}
		
		setWidget(tabPanel);
	}
	
	
}
