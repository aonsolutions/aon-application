package com.esferalia.aon.gwt.marketing.client.marketing;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonQuestionPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonQuestionPanel.AonQuestionPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.gwt.marketing.client.marketing.panel.QuestionModulePanel;
import com.esferalia.gwt.marketing.shared.IRequestParamsNames;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;

public class QuestionModule extends MainEntryPoint {
	
	private static final String ACC_QUESTION_REPORT_PRINT = "/aon_gwt_marketing/roms/QuestionReportExcelPrint";
	
	private static final Logger LOGGER = Logger.getLogger(QuestionModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		MarketingModuleOptions options = new MarketingModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad(final MarketingModuleOptions options) {
		AON.ensureInjected();
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		QuestionModulePanel questionPanel = new QuestionModulePanel(options);
		
		AonToolbar toolbar = new AonToolbar( "PREGUNTAS" );
		
		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);		
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden = new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		toolbar.add(diskForm);

		// new question button
		final AonToolbarButton newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showQuestiontDialog(questionPanel, options));
		toolbar.add(newButton);
		
		// excel button
		final AonToolbarButton excel = new AonToolbarButton( AON.MSG.export() , AON.CSS.aonIconExcel());

		excel.addClickHandler(event -> {
				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_QUESTION_REPORT_PRINT);
				domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
				domainNameHidden.setValue(getCurrentDomainName());
				userHidden.setValue(getCurrentUser());
				diskForm.submit();
		});
		
		toolbar.add(excel);

		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		
		dockLayoutPanel.add( questionPanel );
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}
	
	private void showQuestiontDialog(QuestionModulePanel questionModulePanel, MarketingModuleOptions options) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption(AON.MSG.questionPanel());
		final AonQuestionPanel questionPanel = new AonQuestionPanel( options.getDomainName(), options.getDomain(), options.getUser(), new AonQuestionPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept() {
				dialog.hide();
				questionModulePanel.onSearch(options);
			}
		}) {

			@Override
			protected void onResize() {
				dialog.showLoaded();
			}};
		
		dialog.add( questionPanel );
		dialog.showLoaded();
	}
}
