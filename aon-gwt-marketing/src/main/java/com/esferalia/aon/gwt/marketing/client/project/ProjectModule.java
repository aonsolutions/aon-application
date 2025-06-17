package com.esferalia.aon.gwt.marketing.client.project;

import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ProjectParams;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

public class ProjectModule extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(ProjectModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private static CommonServiceAsync COMMON_SERVICE;

	private ProjectModuleOptions options;

	private DeckLayoutPanel deckLayoutPanel;
	private ProjectModulePanel projectModulePanel;
	private ProjectEntryPanel projectEntryPanel;
	
	private Integer customerId;
	private Integer officeDomain;

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		options = new ProjectModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		
		customerId = getCustomer() > 0 ? getCustomer() : null;
		officeDomain = getOfficeDomain() > 0 ? getOfficeDomain() : getCurrentDomain();

		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(),
				new AsyncCallback<AonConfiguration>() {

					@Override
					public void onSuccess(AonConfiguration config) {
						options.setConfiguration(config);
						moduleLoad();
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al cargar el module");
						moduleLoad();
					}
				});
	}

	public void moduleLoad() {
		AON.ensureInjected();

		deckLayoutPanel = new DeckLayoutPanel();

		projectModulePanel = new ProjectModulePanel(options, customerId, officeDomain) {

			@Override
			protected void onProjectSelect(Project project) {
				showSelectedProject(project);
			}

			@Override
			protected void onProjectCreate(Project project) {
				showCreatedSeller(project);
			}

		};

		projectEntryPanel = new ProjectEntryPanel(options, officeDomain) {

			@Override
			protected void onBackClick() {
				showProjectList();
			}

			@Override
			protected void onProjectDeleteClick(Integer projectId) {
				deleteProject(projectId);
			}

			@Override
			protected void getProjectListCount(Consumer<Integer> finish) {
				projectModulePanel.getSellerListCount(count -> finish.accept(count));
			}

			@Override
			protected void onProjectSelectionChange(Project project, Integer position) {
				showSelectedProject(project, position);
			}

			@Override
			protected ProjectParams getProjectListParams() {
				return projectModulePanel.getProjectListParams();
			}
		};

		deckLayoutPanel.add(projectModulePanel);
		deckLayoutPanel.add(projectEntryPanel);
		deckLayoutPanel.showWidget(projectModulePanel);

		options.getParentWidget().add(deckLayoutPanel);
		
		// Remove customer from LS
		removeCustomer();
		removeOfficeDomain();
	}

	private void showProjectList() {
		deckLayoutPanel.showWidget(projectModulePanel);
		projectModulePanel.onSearch();
	}

	private void showSelectedProject(Project project) {
		deckLayoutPanel.showWidget(projectEntryPanel);
		projectEntryPanel.setProject(project, projectModulePanel.getSellerListPosition(project.getId()));
	}

	private void showSelectedProject(Project project, Integer position) {
		deckLayoutPanel.showWidget(projectEntryPanel);
		projectEntryPanel.setProject(project, position);
	}

	private void showCreatedSeller(Project project) {
		deckLayoutPanel.showWidget(projectEntryPanel);
		projectEntryPanel.setProject(project, -1);
	}

	private void deleteProject(Integer projectId) {
		COMMON_SERVICE.deleteProject(options.getDomainName(), options.getDomain(), options.getUser(), projectId,
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						showProjectList();
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error borrado: " + caught.getMessage());
					}
				});
	}

}
