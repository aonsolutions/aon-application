package com.esferalia.aon.gwt.marketing.client.project;

import java.util.Comparator;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProjectPanel.AonProjectPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.ProjectParams;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class ProjectPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(ProjectPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private ProjectParams params;
	private Map<Integer, Project> rowProjects = new HashMap<>();
	
	private Integer customerId;
	private Integer customerDomain;
	
	private static enum COLS {
		  DES(AON.MSG.name()						,"10rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, WOR("Operario Activo"						,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUD("Alias"								,"8rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, CUS("Titular"								,"18rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, ACT("Actividad"							,"10rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DAT("Fecha"								,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, TYP("T. Expediente"						,"7rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("Estado"								,"5rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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

	public ProjectPanel(ProjectParams params, Integer customerId, Integer customerDomain) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.params = params;
		this.rowProjects.clear();
		
		this.customerId = customerId;
		this.customerDomain = customerDomain;

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
		for ( COLS col : COLS.values()) {
			if(null != customerId && col.equals(COLS.CUS)) continue;
			
			if(null != customerId && col.equals(COLS.BUT)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
				
				AonTableButton addButton = new AonTableButton("Nuevo expediente", AON.CSS.aonIconAdd());
				addButton.addClickHandler(e -> {
					e.stopPropagation();
					showSellerDialog();
				});
				buttonContainer.add(addButton);
				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
				continue;
			}
			
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		}
	}
	
	private void showSellerDialog() {
		new AonProjectPanel( params.getDomainName(), params.getDomain(), params.getUser(), customerId, customerDomain, new AonProjectPanelCallback() {
			
			@Override
			public void onCancel() { }
			
			@Override
			public void onAccept(Project project) {
				onProjectCreation(project);
			}
		});
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(projects -> {
			boolean something = false;
			
			for(Project project : projects) {
				something = true;
				paintRow(project);
			}
			
			if (projects.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + projects.size() - 1);
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
		
		Label name = new Label("No existen expdientes");
		name.setTitle("No existen expdientes");
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
	}

	private void paintRow(Project project) {
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		AonTableButton deleteButton = new AonTableButton("Borrar expediente", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
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
					delete(project.getId());
				}
			});
		});
		buttonContainer.add(deleteButton);
		
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onProjectOpen(project), ClickEvent.getType());
		
		Label name = new Label(project.getName());
		name.setTitle(project.getName());
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		project.getProjectHolders().sort(Comparator.comparing(
                ProjectHolder::getEndDate,
                Comparator.nullsFirst(Comparator.reverseOrder())
        ));
		
		String holdersValue = "";
		
		if(!project.getProjectHolders().isEmpty()) {
			ProjectHolder projectHolder = project.getProjectHolders().get(0);
			String pworkgroupsValue = projectHolder.getWorkgroup().getId() == null ? null : "(GT) " + projectHolder.getWorkgroup().getDescription();
			String projectHoldersValue = projectHolder.getTaskHolder().getId() == null ? null : projectHolder.getTaskHolder().getName();
			holdersValue = AonStringUtils.isBlank(pworkgroupsValue) 
					? projectHoldersValue : 
					(pworkgroupsValue + (AonStringUtils.isBlank(projectHoldersValue) ? "" : ", " + projectHoldersValue));
			holdersValue += " (" + formatDate.format(projectHolder.getStartDate()) + (null == projectHolder.getEndDate() ? "" : " - " + formatDate.format(projectHolder.getEndDate())) + ")";
		} 
		
		Label projectHolders = new Label(holdersValue);
		projectHolders.setTitle(holdersValue);
		tab.addInlineStyle(projectHolders, COLS.WOR.getStyles());
		tab.addRow(row, projectHolders, COLS.WOR.getColWidth());
		
		Label alias = new Label(project.getAlias());
		alias.setTitle(project.getAlias());
		tab.addInlineStyle(alias, COLS.BUD.getStyles());
		tab.addRow(row, alias, COLS.BUD.getColWidth());
		
		
		if(null == customerId) {
			Label customer = new Label(project.getRegistry().getName());
			customer.setTitle(project.getRegistry().getName());
			tab.addInlineStyle(customer, COLS.CUS.getStyles());
			tab.addRow(row, customer, COLS.CUS.getColWidth());
		}
		
		String activityValue = project.getProjectActivities().isEmpty() ? null : project.getProjectActivities().stream()
				.filter(projectActivity -> null != projectActivity.getActivityType())
				.map(projectActivity -> projectActivity.getActivityType().getDescription())
				.sorted()
				.collect(Collectors.joining (", "));
		
		Label activity = new Label(activityValue);
		activity.setTitle(activityValue);
		tab.addInlineStyle(activity, COLS.ACT.getStyles());
		tab.addRow(row, activity, COLS.ACT.getColWidth());
		
		Label date = new Label(null == project.getDate() ? "" : formatDate.format(project.getDate()));
		date.setTitle(null == project.getDate() ? "" : formatDate.format(project.getDate()));
		tab.addInlineStyle(date, COLS.DAT.getStyles());
		tab.addRow(row, date, COLS.DAT.getColWidth());
		
		Label type = new Label(project.getType().getDescription());
		type.setTitle(project.getType().getDescription());
		tab.addInlineStyle(type, COLS.TYP.getStyles());
		tab.addRow(row, type, COLS.TYP.getColWidth());
		
		Label status = new Label(project.isActive() ? "Activo" : "Inactivo");
		status.setTitle(project.isActive() ? "Activo" : "Inactivo");
		tab.addInlineStyle(status, COLS.STA.getStyles());
		tab.addRow(row, status, COLS.STA.getColWidth());
		
		tab.addRow(row, buttonContainer, COLS.BUT.getColWidth());
		
		rowProjects.put(project.getId(), project);
	}
	
	private void getList(Consumer<List<Project>> success) {
		COMMON_SERVICE.getProjects(params, new AsyncCallback<List<Project>>() {
			
			@Override
			public void onSuccess(List<Project> projects) {
				success.accept(projects);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error obteniendo operarios / grupos de trabajo: " + caught.getMessage());
			}
		});
	}
	
	private void delete(Integer projectId) {
		COMMON_SERVICE.deleteProject(params.getDomainName(), params.getDomain(), params.getUser(), projectId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onSearch();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error borrado: " + caught.getMessage());
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

	public Integer getSellerListPosition(Integer sellerId) {
		List<Project> projects = rowProjects.values().stream().collect(Collectors.toList());
		for(int i=0; i<projects.size(); i++)
			if(projects.get(i).getId().equals(sellerId))
				return i;
		return 0;
	}
	
	public void getSellerListCount(Consumer<Integer> finish) {
		COMMON_SERVICE.getProjectsCount(params, new AsyncCallback<Integer>() {
					
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
	protected abstract void onProjectOpen(Project project);
	protected abstract void onProjectCreation(Project project);
	
}

