package com.esferalia.aon.gwt.marketing.client.taskholder;

import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.TaskHolderParams;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

public class TaskHolderModule extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(TaskHolderModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private static CommonServiceAsync COMMON_SERVICE;

	private TaskHolderModuleOptions options;

	private DeckLayoutPanel deckLayoutPanel;
	private TaskHolderModulePanel taskHolderModulePanel;
	private TaskHolderEntryPanel taskHolderEntryPanel;

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		options = new TaskHolderModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		
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

		taskHolderModulePanel = new TaskHolderModulePanel(options) {

			@Override
			protected void onTaskHolderSelect(TaskHolder taskHolder) {
				showSelectedTaskHolder(taskHolder);
			}

			@Override
			protected void onTaskHolderCreate(TaskHolder taskHolder) {
				showCreatedTaskHolder(taskHolder);
			}

		};

		taskHolderEntryPanel = new TaskHolderEntryPanel(options) {

			@Override
			protected void onBackClick() {
				showProjectList();
			}

			@Override
			protected void onTaskHolderDeleteClick(Integer taskHolderId) {
				deleteTaskHolder(taskHolderId);
			}

			@Override
			protected void getTaskHolderListCount(Consumer<Integer> finish) {
				taskHolderModulePanel.getTaskHolderListCount(count -> finish.accept(count));
			}

			@Override
			protected void onTaskHolderSelectionChange(TaskHolder taskHolder, Integer position) {
				showSelectedTaskHolder(taskHolder, position);
			}

			@Override
			protected TaskHolderParams getTaskHolderListParams() {
				return taskHolderModulePanel.getTaskHolderListParams();
			}
		};

		deckLayoutPanel.add(taskHolderModulePanel);
		deckLayoutPanel.add(taskHolderEntryPanel);
		deckLayoutPanel.showWidget(taskHolderModulePanel);

		options.getParentWidget().add(deckLayoutPanel);
		
		// Remove customer from LS
		removeCustomer();
		removeOfficeDomain();
	}

	private void showProjectList() {
		deckLayoutPanel.showWidget(taskHolderModulePanel);
		taskHolderModulePanel.onSearch();
	}

	private void showSelectedTaskHolder(TaskHolder taskHolder) {
		deckLayoutPanel.showWidget(taskHolderEntryPanel);
		taskHolderEntryPanel.setTaskHolder(taskHolder, taskHolderModulePanel.getTaskHolderListPosition(taskHolder.getId()));
	}

	private void showSelectedTaskHolder(TaskHolder taskHolder, Integer position) {
		deckLayoutPanel.showWidget(taskHolderEntryPanel);
		taskHolderEntryPanel.setTaskHolder(taskHolder, position);
	}

	private void showCreatedTaskHolder(TaskHolder taskHolder) {
		deckLayoutPanel.showWidget(taskHolderEntryPanel);
		taskHolderEntryPanel.setTaskHolder(taskHolder, -1);
	}

	private void deleteTaskHolder(Integer taskHolderId) {
		COMMON_SERVICE.deleteTaskHolder(options.getDomainName(), options.getDomain(), options.getUser(), taskHolderId,
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						showProjectList();
						taskHolderModulePanel.showSuccess("Operario eliminado correctamente");
					}

					@Override
					public void onFailure(Throwable caught) {
						taskHolderEntryPanel.showError("Error borrado: " + caught.getMessage());
					}
				});
	}

}
