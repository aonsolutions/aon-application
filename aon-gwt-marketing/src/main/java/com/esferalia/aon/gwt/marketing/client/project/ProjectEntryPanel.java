package com.esferalia.aon.gwt.marketing.client.project;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog.AonCustomDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectHolderPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectHolderPanel.AonProjectHolderPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.ProjectParams;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class ProjectEntryPanel extends AonCustomDockLayout {
	
	// ------------------------------------------------- CommonServiceAsync
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private final String EMPTY_STRING = "";
	
	private Integer position = -1;
	private AonToolbarButton previusProject;
	private Label projectIteration;
	private AonToolbarButton nextProject;
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private SimplePanel centerPanel;
	private ScrollPanel scrollPanel;
	
	private AonCustomTable tab;
	
	private ProjectModuleOptions options;
	private List<ProjectHolder> projectHolders;
	private Project project;
	private Integer count;
	
	private static enum COLS {
		  TYP("Tipo"								,"10rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, HOL("Operario"							,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, WOR("G. Trabajo"							,"10rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("F. Desde"							,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, END("F. Hasta"							,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					,"3rem"				,"")
		;

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel,String colWidth,String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getStyles() {
			return styles;
		}
	}

	
	public ProjectEntryPanel(ProjectModuleOptions options) {
		super("Expediente");
		
		this.options = options;
		initializeCommonService();
		
		addButtonsToolbar();
		hideSearchWidget();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");
		
		container.add(centerPanel);
		
		scrollPanel = new ScrollPanel();
		centerPanel.setWidget(scrollPanel);
		
		add(container);
	}
	
	@Override
	protected void onClearFilter() {}
	
	private void addButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton( "Volver", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onBackClick());
		addToolbarButton(backButton);
		
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Agente", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showProjectHolderDialog());
		addToolbarButton(newButton);
		
		AonToolbarButton deleteButton = new AonToolbarButton( "Borrar Expediente", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Expediente",
					new HTML("Se va a proceder a eliminar el expediente <b>" + project.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					onProjectDeleteClick(project.getId());
				}
			});
		});
		addToolbarButton(deleteButton);
		
		previusProject = new AonToolbarButton("Anterior Expediente", AON.CSS.aonIconLeft());
		previusProject.setEnabled(position > 0);
		previusProject.addClickHandler(e -> {
			position = position - 1;
			getNextProject(position, nextProject -> onProjectSelectionChange(nextProject, position));
		});
		addToolbarButton(previusProject);
		
		getProjectListCount(count -> {
			this.count = count;
			
			projectIteration = new Label((null == project ? "ND" : (position + 1)) + " / " + this.count);
			addToolbarButton(projectIteration);
			
			nextProject = new AonToolbarButton("Siguiente Expediente", AON.CSS.aonIconRight());
			nextProject.setEnabled(position < (this.count - 1));
			nextProject.addClickHandler(e -> {
				position = position + 1;
				getNextProject(position, nextProject -> onProjectSelectionChange(nextProject, position));
			});
			addToolbarButton(nextProject);
		});
		
		
	}
	
	private void showProjectHolderDialog() {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Nuevo Operario" );
		AonProjectHolderPanel projectPanel = new AonProjectHolderPanel( options.getDomainName(), options.getDomain(), options.getUser(), project.getId(), projectHolders, new AonProjectHolderPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(ProjectHolder projectHolder) {
				dialog.hide();
				setProject(project, position);
			}
		}) {

			@Override
			protected void onResize() {
				dialog.showLoadedCB(new AonCustomDialogCallback() {
					@Override public void onEnd() { dialog.center(); }
				});
			}};
		
		dialog.add( projectPanel );
	}
	
	private void editProjectHolder(ProjectHolder projectHolder) {
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption( "Editar Operario" );
		AonProjectHolderPanel projectPanel = new AonProjectHolderPanel( options.getDomainName(), options.getDomain(), options.getUser(), project.getId(), projectHolders, projectHolder, new AonProjectHolderPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(ProjectHolder projectHolder) {
				dialog.hide();
				setProject(project, position);
			}
		}) {

			@Override
			protected void onResize() {
				dialog.showLoadedCB(new AonCustomDialogCallback() {
					@Override public void onEnd() { dialog.center(); }
				});
				
			}};
		
		dialog.add( projectPanel );
	}

	private void getNextProject(Integer nextPos, Consumer<Project> projectLoad) {
		ProjectParams params = getProjectListParams();
		params.setOffset(nextPos);
		params.setLimit(1);
		
		commonService.getProjects(params, new AsyncCallback<List<Project>>() {
			
			@Override
			public void onSuccess(List<Project> projects) {
				projectLoad.accept(projects.get(0));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

	private void hideNavegationOptions() {
		previusProject.setVisible(false);
		projectIteration.setVisible(false);
		nextProject.setVisible(false);
	}

	private void showNavegationOptions() {
		previusProject.setVisible(true);
		projectIteration.setVisible(true);
		nextProject.setVisible(true);
	}

	public void setProject(Project project, Integer sellectPos) {
		this.position = sellectPos;
		this.project = project;
		setProject(project, finish -> {
			if(position >= 0) showNavegationOptions();
			else hideNavegationOptions();
		});
	}
	
	public void setProject(Project project, Consumer<Void> finish) {
		this.project = project;
		getProjectHolders(project.getId(), projectHoldersDb -> {
			getProjectListCount(count -> {
				this.count = count;
				
				this.projectHolders = projectHoldersDb;
				
				setToolbarTitle("Expediente / " + project.getName());
				projectIteration.setText((null == project ? "ND" : (position + 1)) + " / " + count);
				previusProject.setEnabled(position > 0);
				nextProject.setEnabled(position < (this.count - 1));
				
				search();
				
				finish.accept(null);
			});
			
		});
	}
	
	private void search() {
		tab = new AonCustomTable();
		scrollPanel.setWidget(tab);
		getElement().getStyle().setProperty("margin", "0 1rem");
		
		paintHeader();
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}
	
	private void searchData() {
		if(this.projectHolders.isEmpty()) {
			scrollPanel.clear();
			AonMessagePanel.showWarning(messagePanel, "No existen operarios ni grupos de trabajo asociandos al expediente " + project.getName());	
		} else {
			AonMessagePanel.hideMessage(messagePanel);
			this.projectHolders.forEach(projectHolder -> paintRow(projectHolder) );
		}
			
	}
	
	private void paintRow(ProjectHolder projectHolder) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton deleteButton = new AonTableButton("Borrar Operario", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Operario",
					new HTML("Se va a proceder a eliminar el operario <b>" + projectHolder.getTaskHolder().getName() + "</b> del expediente <b>" + project.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					deleteProjectHolder(projectHolder.getId(), deleted -> setProject(project, position));
				}
			});
		});
		buttonContainer.add(deleteButton);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> editProjectHolder(projectHolder), ClickEvent.getType());
		
		String typeValue = projectHolder.getTaskHolder().getId() != null ? "Operario" : "Grupo Trabajo";
		Label type = new Label(typeValue);
		type.setTitle(typeValue);
		tab.addInlineStyle(type, COLS.TYP.getStyles());
		tab.addRow(row, type, COLS.TYP.getColWidth());
		
		String taskHolder = projectHolder.getTaskHolder().getId() != null ? projectHolder.getTaskHolder().getName() : "";
		Label name = new Label(taskHolder);
		name.setTitle(taskHolder);
		tab.addInlineStyle(name, COLS.HOL.getStyles());
		tab.addRow(row, name, COLS.HOL.getColWidth());
		
		String workgroupValue = projectHolder.getWorkgroup().getId() != null ? projectHolder.getWorkgroup().getDescription() : "";
		Label wrokgroup = new Label(workgroupValue);
		wrokgroup.setTitle(workgroupValue);
		tab.addInlineStyle(wrokgroup, COLS.WOR.getStyles());
		tab.addRow(row, wrokgroup, COLS.WOR.getColWidth());
		
		Label start = new Label(projectHolder.getStartDate() == null ? "" : formatDate.format(projectHolder.getStartDate()));
		tab.addInlineStyle(start, COLS.STA.getStyles());
		tab.addRow(row, start, COLS.STA.getColWidth());
		
		Label end = new Label(projectHolder.getEndDate() == null ? "" : formatDate.format(projectHolder.getEndDate()));
		tab.addInlineStyle(end, COLS.END.getStyles());
		tab.addRow(row, end, COLS.END.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private void getProjectHolders(Integer projectId, Consumer<List<ProjectHolder>> success) {
		commonService.getProjectHolders(options.getDomainName(), options.getDomain(), options.getUser(), projectId, new AsyncCallback<List<ProjectHolder>>() {
			
			@Override
			public void onSuccess(List<ProjectHolder> projectHoldersDb) {
				success.accept(projectHoldersDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo expediente: " + caught.getMessage());
			}
			
		});
	}
	
	private void deleteProjectHolder(Integer projectHolderId, Consumer<Void> success) {
		commonService.deleteProjectHolder(options.getDomainName(), options.getDomain(), options.getUser(), projectHolderId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void deleted) {
				success.accept(deleted);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	protected abstract void onBackClick();
	protected abstract void onProjectDeleteClick(Integer projectId);
	
	protected abstract void getProjectListCount(Consumer<Integer> finish);
	protected abstract ProjectParams getProjectListParams();
	
	protected abstract void onProjectSelectionChange(Project project, Integer position);

}
