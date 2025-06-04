package com.esferalia.aon.gwt.marketing.client.project;

import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectHolderPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectHolderPanel.AonProjectHolderPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class ProjectHolderTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(ProjectHolderTable.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	
	private String domainName;
	private Integer domain;
	private String user;
	
	private Project project;
	
	private static enum COLS {
		  TYP("Tipo"								,"5rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, HOL("Operario"							,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, WOR("G. Trabajo"							,"7rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("F. Desde"							,"5rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, END("F. Hasta"							,"5rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					,"2rem"				,"")
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
	
	public ProjectHolderTable(String domainName, int domain, String user, Project project) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.project = project;

		container = new SimplePanel();
		container.getElement().getStyle().setProperty("max-height", "200px");
		container.getElement().getStyle().setProperty("padding-left", "1px");
		setWidget(container);
		
		search();
	}

	private void search() {
		container.clear();
		tab = new AonCustomTable();
		tab.setMaxHeight("160x");
		scrollPanel = new ScrollPanel(tab);
		
		paintHeader();
		container.setWidget(scrollPanel);
		searchData();
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}
	
	private void searchData() {
		if(this.project.getProjectHolders().isEmpty()) {
			FlowPanel line = new FlowPanel();
			InlineLabel label = new InlineLabel("No existen operarios ni grupos de trabajo asociandos al expediente");
			line.add(label);
			container.clear();
			container.add(line);
		} else {
			this.project.getProjectHolders().forEach(projectHolder -> paintRow(projectHolder) );
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
					deleteProjectHolder(projectHolder.getId(), deleted -> onDelete());
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
	
	private void editProjectHolder(ProjectHolder projectHolder) {
		new AonProjectHolderPanel( domainName, domain, user, project.getId(), project.getProjectHolders(), projectHolder, new AonProjectHolderPanelCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept(ProjectHolder projectHolder) {
				onProjectHolderAdd();
			}
		});
	}

	private void deleteProjectHolder(Integer projectHolderId, Consumer<Void> success) {
		COMMON_SERVICE.deleteProjectHolder(domainName, domain, user, projectHolderId, new AsyncCallback<Void>() {
			
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

	protected abstract void onDelete();
	protected abstract void onProjectHolderAdd();
	
}

