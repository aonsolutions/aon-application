package com.esferalia.aon.gwt.marketing.client.taskholder;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTaskHolderWorkgroupPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTaskHolderWorkgroupPanel.AonTaskHolderWorkgroupPanelCallback;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
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

public abstract class WorkgroupTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(WorkgroupTable.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private SimplePanel container;
	private ScrollPanel scrollPanel;
	private AonCustomTable tab;
	
	private String domainName;
	private Integer domain;
	private String user;
	
	private TaskHolder taskHolder;
	
	private static enum COLS {
		  NAM("Nombre"								,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, USR("Tipo Usuario"						,"10rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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
	
	public WorkgroupTable(String domainName, int domain, String user, TaskHolder taskHolder) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.domainName = domainName;
		this.domain = domain;
		this.user = user;
		this.taskHolder = taskHolder;
		
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
		getTaskHolderWorkgroups(this.taskHolder.getId(), taskHolderWorkgroups -> {
			if(taskHolderWorkgroups.isEmpty()) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel("No existen grupos de trabajo asociandos al operario");
				line.add(label);
				container.clear();
				container.add(line);
			} else 
				taskHolderWorkgroups.forEach(taskHolderWorkgroup -> paintRow(taskHolderWorkgroup) );
			
		});
	}
	
	private void paintRow(TaskHolderWorkgroup taskHolderWorkgroup) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton deleteButton = new AonTableButton("Borrar Grupo Trabajo", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Grupo Trabajo",
					new HTML("Se va a proceder a eliminar el Grupo Trabajo <b>" + taskHolderWorkgroup.getWorkgroup().getDescription() + "</b> del operario <b>" + taskHolderWorkgroup.getTaskHolderObj().getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					deleteTaskHolderWorkgroup(taskHolderWorkgroup.getId(), deleted -> onDelete());
				}
			});
		});
		buttonContainer.add(deleteButton);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> editTaskHolderWorkgroup(taskHolderWorkgroup), ClickEvent.getType());
		
		String nameValue = taskHolderWorkgroup.getWorkgroup().getDescription();
		Label type = new Label(nameValue);
		type.setTitle(nameValue);
		tab.addInlineStyle(type, COLS.NAM.getStyles());
		tab.addRow(row, type, COLS.NAM.getColWidth());
		
		String userTypeValue = null == taskHolderWorkgroup.getTaskHolderWorkgroupType() ? "" : taskHolderWorkgroup.getTaskHolderWorkgroupType().getDescription();
		Label userType = new Label(userTypeValue);
		userType.setTitle(userTypeValue);
		tab.addInlineStyle(userType, COLS.USR.getStyles());
		tab.addRow(row, userType, COLS.USR.getColWidth());
		
		Label start = new Label(taskHolderWorkgroup.getStartDate() == null ? "" : formatDate.format(taskHolderWorkgroup.getStartDate()));
		tab.addInlineStyle(start, COLS.STA.getStyles());
		tab.addRow(row, start, COLS.STA.getColWidth());
		
		Label end = new Label(taskHolderWorkgroup.getEndDate() == null ? "" : formatDate.format(taskHolderWorkgroup.getEndDate()));
		tab.addInlineStyle(end, COLS.END.getStyles());
		tab.addRow(row, end, COLS.END.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
	}
	
	private void editTaskHolderWorkgroup(TaskHolderWorkgroup taskHolderWorkgroup) {
		new AonTaskHolderWorkgroupPanel( domainName, domain, user, taskHolderWorkgroup,  new AonTaskHolderWorkgroupPanelCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept(TaskHolderWorkgroup taskHolderWorkgroup) {
				onTaskHolderWorkgroupAdd();
			}
		});
	}

	private void deleteTaskHolderWorkgroup(Integer taskHolderWorkgroupId, Consumer<Void> success) {
		COMMON_SERVICE.deleteTaskHolderWorkgroup(domainName, domain, user, taskHolderWorkgroupId, new AsyncCallback<Void>() {
			
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
	
	private void getTaskHolderWorkgroups (Integer taskHolderId, Consumer<List<TaskHolderWorkgroup>> success) {
		COMMON_SERVICE.getTaskHolderWorkgroupList(domainName, domain, user, taskHolderId, new AsyncCallback<List<TaskHolderWorkgroup>>() {
			
			@Override
			public void onSuccess(List<TaskHolderWorkgroup> taskHolderWorkgroupsDB) {
				success.accept(taskHolderWorkgroupsDB);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}

	protected abstract void onDelete();
	protected abstract void onTaskHolderWorkgroupAdd();
	
}

