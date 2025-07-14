package com.esferalia.aon.gwt.marketing.client.taskholder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.TaskHolderParams;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class TaskHolderPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(TaskHolderPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private TaskHolderParams params;
	private Map<Integer, TaskHolder> rowProjects = new HashMap<>();
	
	private static enum COLS {
		  DOC(AON.MSG.document()					,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, NAM(AON.MSG.name()						,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
//		, ALI("Alias"								,"8rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("Estado"								,"5rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, WOR("Grupo Trabajo"						,"15rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, USR("Usuario"								,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, USD("Dom."								,"4rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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

	public TaskHolderPanel(TaskHolderParams params) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.rowProjects.clear();

		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						searchData();
					}
				}
			}
		});
		
		onSearch();
		
	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	private void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		tab = new AonCustomTable();
		setWidget(tab);
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
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(taskHolders -> {
			boolean something = false;
			
			for(TaskHolder taskHolder : taskHolders) {
				something = true;
				paintRow(taskHolder);
			}
			
			if (taskHolders.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + taskHolders.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				paintEmptyRow();
				disableMoreData();
			}
			enableSearch();
			
		});
		
	}
	
	private void paintEmptyRow() {
		HTMLPanel row = tab.createRow();
		
		Label name = new Label("No existen operarios");
		name.setTitle("No existen operarios");
		tab.addInlineStyle(name, COLS.DOC.getStyles());
		tab.addRow(row, name, COLS.DOC.getColWidth());
	}

	private void paintRow(TaskHolder taskHolder) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton deleteButton = new AonTableButton("Borrar operario", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Operario",
					new HTML("Se va a proceder a eliminar el operario <b>" + taskHolder.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					delete(taskHolder.getId(), deleteButton);
				}
			});
		});
		buttonContainer.add(deleteButton);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onTaskHolderOpen(taskHolder), ClickEvent.getType());
		
		Label document = new Label(taskHolder.getDocument());
		tab.addInlineStyle(document, COLS.DOC.getStyles());
		tab.addRow(row, document, COLS.DOC.getColWidth());
		
		Label name = new Label(taskHolder.getName());
		name.setTitle(taskHolder.getName());
		tab.addInlineStyle(name, COLS.NAM.getStyles());
		tab.addRow(row, name, COLS.NAM.getColWidth());
		
//		Label alias = new Label(taskHolder.getAlias());
//		alias.setTitle(taskHolder.getAlias());
//		tab.addInlineStyle(alias, COLS.ALI.getStyles());
//		tab.addRow(row, alias, COLS.ALI.getColWidth());
		
		Label status = new Label(taskHolder.getStatus().getDescription());
		tab.addInlineStyle(status, COLS.STA.getStyles());
		tab.addRow(row, status, COLS.STA.getColWidth());
		
		String workgroupsValue = taskHolder.getWorkgroups().stream()
			    .map(Workgroup::getDescription)
			    .collect(Collectors.joining(", "));
		Label workgroups = new Label(workgroupsValue);
		workgroups.setTitle(workgroupsValue);
		tab.addInlineStyle(workgroups, COLS.WOR.getStyles());
		tab.addRow(row, workgroups, COLS.WOR.getColWidth());
		
		Label user = new Label(null != taskHolder.getUser() ? taskHolder.getUser().getLogin() : AonStringUtils.EMPTY);
		user.setTitle(null != taskHolder.getUser() ? taskHolder.getUser().getLogin() : AonStringUtils.EMPTY);
		tab.addInlineStyle(user, COLS.USR.getStyles());
		tab.addRow(row, user, COLS.USR.getColWidth());
		
		Widget userDomain;
		if(null != taskHolder.getUser() && null != taskHolder.getUser().getDomain() && null != taskHolder.getUser().getDomain().getId() && !taskHolder.getUser().getDomain().getId().equals(taskHolder.getDomain().getId())){
			userDomain = new AonTableButton("Padre", AON.CSS.aonIconEnterprise());
			userDomain.addStyleName(AON.CSS.aonCustomRowButtom());
		} else
			userDomain = new Label();
		
		tab.addRow(row, userDomain, COLS.USD.getColWidth());
		
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
		
		rowProjects.put(taskHolder.getId(), taskHolder);
	}
	
	private void getList(Consumer<List<TaskHolder>> success) {
		COMMON_SERVICE.getTaskHolderList(params, new AsyncCallback<List<TaskHolder>>() {
			
			@Override
			public void onSuccess(List<TaskHolder> taskHolders) {
				success.accept(taskHolders);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error obteniendo operarios : " + caught.getMessage());
			}
		});
	}
	
	private void delete(Integer taskHolderId, AonTableButton deleteButton) {
		COMMON_SERVICE.deleteTaskHolder(params.getDomainName(), params.getDomain(), params.getUser(), taskHolderId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
				deleteButton.setEnabled(true);
			}
		});
	}
	
	public void resetSearchOffset() {
		offset.setValue(0);
		rowProjects.clear();
	}
	
	public AonCustomTable getTable() {
		return tab;
	}

	public Integer getTaskHolderListPosition(Integer taskHolderId) {
		List<TaskHolder> taskHolders = rowProjects.values().stream().collect(Collectors.toList());
		for(int i=0; i<taskHolders.size(); i++)
			if(taskHolders.get(i).getId().equals(taskHolderId))
				return i;
		return 0;
	}
	
	public void getTaskHolderListCount(Consumer<Integer> finish) {
		COMMON_SERVICE.getTaskHoldersCount(params, new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer count) {
						finish.accept(count);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						finish.accept(null);
					}
				});
	}

	protected abstract void onShowSuccessMessage(String successMessage);
	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowLoadingMessage(String loadingMessage);
	protected abstract void onTaskHolderOpen(TaskHolder taskHolder);
	protected abstract void onTaskHolderCreation(TaskHolder taskHolder);
	
}

