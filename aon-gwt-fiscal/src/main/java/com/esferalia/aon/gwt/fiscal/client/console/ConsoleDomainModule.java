package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTable.ConsoleDomainTableCallback;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
 
public class ConsoleDomainModule extends AonLayoutPanel {

	private static final String DOMAIN_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/ConsoleDomainReportExcelPrint";

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	private ConsoleModuleOptions options;
	private HashSet<Integer> checkedList = new HashSet<>();
	private SimpleLayoutPanel container = new SimpleLayoutPanel();
	private ConsoleDomainFilterPanel filterPanel;
	
	abstract class AbsConsoleDomainTableCallback implements ConsoleDomainTableCallback {
		public void showError(String message) {
			ConsoleDomainModule.this.showErrorPanel(message);			
		};
		public void showInfo(String message) {
			ConsoleDomainModule.this.showInfoPanel(message);			
		}
	}
	

	public ConsoleDomainModule(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		this.addNorth(getToolbarPanel(options), AonToolbar.HEIGTH);
		filterPanel = new ConsoleDomainFilterPanel(options);
		filterPanel.addValueChangeHandler(e -> search(options, e.getValue()) );
		this.addNorth(filterPanel, ConsoleDomainFilterPanel.HEIGTH);
		this.add(container);
	}
	
	private void search(ConsoleModuleOptions options, DomainParams params) {
		final AonToast toast = new AonToast();
		toast.show("Cargando ...", new InlineLabel("Un momento, por favor ..."));
		ConsoleModule.CONSOLE_SERVICE.getDomains(params,new AsyncCallback<LinkedList<Domain>>() {
			
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

	private AonToolbar getToolbarPanel( ConsoleModuleOptions options) {
		AonToolbar toolbar = new AonToolbar();
		toolbar.setTitle("Gesti\u00F3n de dominios");
		
		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		Hidden domainParamsHidden = new Hidden(IRequestParamsNames.DOMAIN_PARAMS);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(domainParamsHidden);
		toolbar.add(diskForm);

		AonToolbarButton exportButton = new AonToolbarButton( AON.MSG.export(), AON.CSS.aonIconExcel() );
		exportButton.addClickHandler(event -> {
			DomainParams params = filterPanel.getParams(options);
			if (!AonStringUtils.isBlank(params.getSchema())) {
				diskForm.setAction(GWT.getHostPageBaseURL() + DOMAIN_REPORT_EXCEL_PRINT);
				domainParamsHidden.setValue(JsonParams.convert(params));
				diskForm.submit();
			} else {
				AonMessageDialog.show("AVISO", "Seleccione un esquema");
			}
		});
		toolbar.add(exportButton);
		
		return toolbar;
	}
	
	private Widget getTable(LinkedList<Domain> domains) {
		checkedList.clear();
		ConsoleDomainTable table = new ConsoleDomainTable(domains, new AbsConsoleDomainTableCallback() {
			
			@Override
			public void onDelete(Domain domain, AsyncCallback<Boolean> cbk) {
				DomainParams params = filterPanel.getParams(options);
				ConsoleModule.CONSOLE_SERVICE.deleteDomain(params, 
					domain.getId(),new AsyncCallbackWrapper<>( cbk ));
			}

			@Override
			public void onChangeActive(Domain domain, AsyncCallback<Domain> cbk) {
				DomainParams params = filterPanel.getParams(options);
				ConsoleModule.CONSOLE_SERVICE.changeActive(params,domain,new AsyncCallbackWrapper<>( cbk ));
			}
			
			@Override
			public void onChangeExpirationDate(Domain domain, AsyncCallback<Domain> cbk) {
				DomainParams params = filterPanel.getParams(options);
				ConsoleModule.CONSOLE_SERVICE.changeExpirationDate(params,domain,new AsyncCallbackWrapper<>( cbk ));
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
