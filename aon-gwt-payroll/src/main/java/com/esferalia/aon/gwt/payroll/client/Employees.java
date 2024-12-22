package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.FilterDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonEmployeesTreeToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.CollectionUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.NumberUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.DateField;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.BooleanEventMetaData;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.DecimalEventMetaData;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.EventMetaData;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.Category;
import com.esferalia.aon.gwt.payroll.shared.CategoryDraft;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Predicate;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.StatisticYears;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.IsTreeItem;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class Employees extends ResizeComposite implements OpenHandler<TreeItem>, SelectionHandler<TreeItem>,
		ScrollHandler, ContextMenuHandler, KeyDownHandler, LoadHandler, AonEmployeesTreeToolbar.Listener {

	private static final int MIN_EMPLOYEE_LIMIT = 35;

	interface Listener {

		void onLoadAvaiableEmployees(Map<String, String> map);

		void onEmployeeSelected(Employee employee);

		void onEnterpriseSelected(Enterprise enterprise);

		void onCCCSelected(CCC ccc);

		void onActivitySelected(Activity activity);

		void onWorkplaceSelected(Workplace workplace);

		void onWorkplaceCostsSelected(CostDocuments docs);

		void onEnterpriseCostsSelected(CostDocuments docs);

		void onCalendarSelected(CalendarDraftObjectData calendar);

		void onIrpfsSelected(IrpfDocuments docs);

		void onWorkplaceStatisticsSelected(Statistics stats);
		
		void onEnterpriseStatisticsSelected(Statistics stats);

		void onEnterpriseSalariesSelected(EnterpriseSalaryObject enterpiseSalary);

		void onITDataSelected(ITDataObject dataObject);
		
		void onWorkplaceSalarySelected(WorkplaceSalaryObject workplaceSalary);

		void onSalariesSelected(SalaryDocuments docs);

		void onSalariesSelected(SalariesDocuments docs);

		void onDocumentsSelected(ISpinnable<IDocument> docs);

		void onSalaryDraftSelected(SalaryDraftObject salaryDraftDocument);

		void onSalaryPreviewSelected(SalaryPreviewDocument salaryPreviewDocument);

		void onEventsDraftSelected(EventsDraftObject eventsDraftObject);

		void onEmployeeEventsDraftSelected(EmployeeEventsDraftObject employeeEventsDraft);
		
		void onEmployeeDraftSelected(EmployeeDraftObject employeeDraft);
		
		void onCategoryDraftSelected(CategoryDraftObject agreementDraftObject);

		void onAgreementDraftSelected(AgreementDraftObject agreementDraftObject);

		void onEmployeeContextMenu(Employee employee, ContextMenuEvent event);

		void onCCCContextMenu(CCC ccc, ContextMenuEvent event);

		void onWorkplaceContextMenu(Workplace workplace, ContextMenuEvent event);

		void onEnterpriseContextMenu(Enterprise enterprise, ContextMenuEvent event);

		void onEmployeeCalendarSelected(EmployeeCalendarDraftObjectData calendar);
		
		void onEmployeeNewCalendarSelected(EmployeeCalendarDraftObject calendar);
		
		void onEmployeeSSBonusSelected(ContractBonusObject contractBonusObject);
		
		void onEmployeeSalarySelected(EmployeeSalaryObject employeeSalary);

		void onEmployeeCopy(Employee employee);

		void onEmployeePaste(Workplace workplace);

		void onEmployeeCut(Employee employee);

		void onSuprPress(Employee employee);
		
		void onCollapseEmployees();
		
		void onShowEmployees(boolean isCollapsed);
		
	}
	
	protected static class HideScrollPanel extends ScrollPanel {
		
		public HideScrollPanel() {
			super();
			hideScrollBars();
			addDomHandler(e -> hideScrollBars() , BlurEvent.getType());
			addDomHandler(e -> hideScrollBars() , MouseOutEvent.getType());
			addDomHandler(e -> showScrollBars() , FocusEvent.getType());
			addDomHandler(e -> showScrollBars() , MouseOverEvent.getType());
		}
		
		private void hideScrollBars() {
			getScrollableElement().getStyle().setOverflow(Overflow.HIDDEN);
		}
		
		private void showScrollBars() {
			getScrollableElement().getStyle().setOverflow(Overflow.AUTO);
		}

	}
	
	protected static class EnterpriseCostDocuments extends CostDocuments {

		public EnterpriseCostDocuments(List<Cost> costs) {
			super(costs);
		}
		
	}

	protected static class WorkplaceCostDocuments extends CostDocuments {
		
		public WorkplaceCostDocuments(List<Cost> costs) {
			super(costs);
		}
	}

	protected static class EnterpriseStatistics extends Statistics {
		
		private Statistics statistics;
		
		public EnterpriseStatistics(Statistics statistics) {
			this.statistics = statistics;
		}
		@Override
		public void initializedListYears(int pCont) {
			statistics.initializedListYears(pCont);
		}

		@Override
		public void addYear(int pCont, int pYear) {
			statistics.addYear(pCont, pYear);
		}

		@Override
		public LinkedList<StatisticYears> getStatisticYears() {
			return statistics.getStatisticYears();
		}
		
	}

	protected static class WorkplaceStatistics extends Statistics {

		private Statistics statistics;
		
		public WorkplaceStatistics(Statistics statistics) {
			this.statistics = statistics;
		}

		@Override
		public void initializedListYears(int pCont) {
			statistics.initializedListYears(pCont);
		}

		@Override
		public void addYear(int pCont, int pYear) {
			statistics.addYear(pCont, pYear);
		}

		@Override
		public LinkedList<StatisticYears> getStatisticYears() {
			return statistics.getStatisticYears();
		}
		
	}

	interface Template extends SafeHtmlTemplates {

		@SafeHtmlTemplates.Template("<span class=\"material-icons\" style=\"vertical-align: middle; color: black; font-size: 20px;\" >{0}</span>")
		SafeHtml materialIcon(String materialIcon);

	}

	private static final Template TEMPLATE = GWT.create(Template.class);

	interface Binder extends UiBinder<Widget, Employees> {}

	private static final Binder binder = GWT.create(Binder.class);

	private static final int EMPLOYEE_SCROLL_GAP = 5;
	private static final int ENTERPRISE_COSTS_INDEX = 0;
	private static final int ENTERPRISE_STATISTICS_INDEX = 2;
	private static final int ENTERPRISE_REPORTS_INDEX = 6;

	private static final int WORKPLACE_COSTS_INDEX = 0;
	private static final int WORKPLACE_CALENDAR_INDEX = 2;
	private static final int WORKPLACE_STATISTICS_INDEX = 3;

	private static final int EMPLOYEE_IRPFOUTCOMES_INDEX = 2; 
	private static final int EMPLOYEE_CALENDAR_INDEX = 3;
	private static final int EMPLOYEE_EVENTS_INDEX = 4;

	private static final DateTimeFormat END_DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String staticEmployees();
		default String title() { 
			return "aon-EmployeesToolbar-Title"; 
		}
	}

	@UiField
	DeckPanel employeesDeck;
	
	@UiField
	HTMLPanel dynamicEmployees;
	
	@UiField
	HTMLPanel employeesToolbar;
	
	@UiField
	AonEmployeesTreeToolbar toolbar;
	
	@UiField(provided = true)
	ScrollPanel scrollPanel;
	
	@UiField(provided = true)
	Tree tree;
	
	@UiField
	HTMLPanel staticEmployees;

	private Images images;
	private List<Listener> listeners;
	private DomainEmployeesServiceAsync employeesService;
	private DomainEnterprisesServiceAsync enterprisesService;

	private boolean formers = true;
	private boolean endDate = true;
	private boolean extended = false;
	private boolean inactive = false;

	private Date fromDate = null;
	private String namePattern = null;

	private Storage storage;

	private Timer searchTimer ;

	/**
	 * The last scroll position.
	 */
	private int lastScrollPos = 0;

	private List<TreeItem> employeeCentinels;
	
	private AonButton showMenuButton;
	private boolean employeeTreeShowed = true;
	private boolean employeeTreeCollapsed = false;

	public Employees() {
		this(false, true);
	}

	public Employees(boolean extended, boolean formers) {

		this.formers = formers;
		this.extended = extended;
		
		images = GWT.create(Images.class);
		listeners = new LinkedList<Employees.Listener>();
		employeeCentinels = new LinkedList<TreeItem>();

		employeesService = DomainEmployeesServiceAsync.newInstance();		
		enterprisesService = DomainEnterprisesServiceAsync.newInstance();
		
		tree = new Tree(new Tree.Resources() {
			
			@Override
			public ImageResource treeOpen() {
				return images.aon_icon_tree_open();
			}
			
			@Override
			public ImageResource treeLeaf() {
				return images.aon_icon_tree_closed();
			}
			
			@Override
			public ImageResource treeClosed() {
				return images.aon_icon_tree_closed();
			}
		}, false);
		
		scrollPanel = new HideScrollPanel();
		
		initWidget(binder.createAndBindUi(this));
		
		employeesDeck.getElement().getStyle().setProperty("margin-top", ".5rem");
		showEmployees();
		
		createEmployeesToolbar();

		tree.addOpenHandler(this);
		tree.addSelectionHandler(this);
		tree.addDomHandler(this, ContextMenuEvent.getType());
		tree.addKeyDownHandler(this);
		
		toolbar.addListener(this);

		toolbar.setVisibleLoadingButton(true);
		
		employeesService.getEnterprises(new AsyncCallback<Enterprise[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Enterprise[] enterprises) {
				for (Enterprise enterprise : enterprises)
					Employees.this.onEnterprise(enterprise);
				toolbar.setVisibleLoadingButton(false);
			}

		});

		scrollPanel.addScrollHandler(this);
		
		employeesService.getAvaiableEmployees(new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("NO");

			}

			@Override
			public void onSuccess(Map<String, String> result) {
				Employees.this.onAvaiableEmployees(result);
			}
		});
		
		searchTimer = new Timer() {
			
			@Override
			public void run() {
				filter(toolbar.getSearchTextBox().getValue());
			}
		};

	}

	public boolean isExtended() {
		return extended;
	}

	public void setExtended(boolean isExtended) {
		this.extended = isExtended;
	}

	public void setFormers(boolean formers) {
		this.formers = formers;

	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void onAvaiableEmployees(Map<String, String> map) {
		onLoadAvaiableEmployees(map);
	}

	public void onEnterprise(Enterprise enterprise) {
		onEnterprise(enterprise, true);
	}

	public void onEnterprise(Enterprise enterprise, boolean open) {
		
		clearEnterprise(enterprise);

		List<Workplace> workplaces = enterprise.getWorkplaces();

		final TreeItem enterpriseItem = addEnterpriseItem(enterprise);
		
		addEnterpriseCostsItem(enterpriseItem, enterprise);
		
		addEnterpriseSalariesItem(enterpriseItem, enterprise);

		addEnterpriseStatsItem(enterpriseItem, enterprise);
		
		if (extended) {
			List<Activity> activities = enterprise.getActivities();
			for (Activity activity : activities) {
				addEnterpriseActivityIem(enterpriseItem, activity);
			}
		}

		List<TreeItem> workplaceItems = new ArrayList<>();

		for (Workplace workplace : workplaces) {
			workplaceItems.add(addEnterpriseWorkplaceItem(enterpriseItem, enterprise, workplace));
		}
		
		enterpriseItem.setState(true, true);
		tree.setSelectedItem(enterpriseItem, true); // Send event to show
													// enterprise data
		
		TreeItem visibleWorkplacesItems [] = workplaceItems.stream().filter( w -> w.isVisible()).toArray(TreeItem[]::new);
		
		if (visibleWorkplacesItems.length == 1) {
			visibleWorkplacesItems[0].setState(open, true); // Send event to show employees
		} else if ( visibleWorkplacesItems.length == 0 ){
			this.inactive = true;
			workplaceItems.forEach( workplaceItem -> workplaceItem.setVisible(true));
		}
		
		scrollPanel.scrollToLeft();

		initViewButton(toolbar.getCollapseAllButton());

	}


	public void clearEnterprise(Enterprise enterprise) {
		for (int i = 0; i < tree.getItemCount(); i++) {
			TreeItem treeItem = tree.getItem(i);
			if (enterprise.equals(treeItem.getUserObject())) {
				tree.removeItem(treeItem);
				return;
			}
		}
	}

	// From OpenHandler<TreeItem>
	@Override
	public void onOpen(OpenEvent<TreeItem> event) {
		TreeItem item = event.getTarget();
		Object userObject = item.getUserObject();
		if (userObject instanceof Workplace) {
			onWorkplaceOpen(item);
		}
		if (userObject instanceof Enterprise) {
			onEnterpriseOpen(item);
		}
		if (userObject instanceof EmployeeDraftObject) {
			onEmployeeOpen(item);
		}
	}

	// From SelectionHandler<TreeItem>
	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {

		TreeItem item = event.getSelectedItem();
		Object userObject = item.getUserObject();

		// TODO : I know that's so ugly and not Object oriented. But
		// it's much more clear than anything else. I promise
		// to change ( even improve ) it soon.
		if (userObject instanceof Enterprise) {
			onEnterpriseSelected((Enterprise) userObject);
		} else if (userObject instanceof Workplace) {
			onWorkplaceSelected((Workplace) userObject);
		} else if (userObject instanceof SalaryDraftObject) {
			onSalaryDraftSelected((SalaryDraftObject) userObject);
		} else if (userObject instanceof SalaryPreviewDocument) {
			onSalaryPreviewSelected((SalaryPreviewDocument) userObject);
		} else if (userObject instanceof Activity) {
			onActivitySelected((Activity) userObject);
		} else if (userObject instanceof CCC) {
			onCCCSelected((CCC) userObject);
		}
		// I apologize about this. Inheritance it's like Kate Beckinsale. Due
		// 'SalariesDocuments' extends 'CostDocuments' its test must be first.
		// If not, onSalariesSelected(SalariesDocuments) method won't be called.
		else if (userObject instanceof SalariesDocuments) {
			// onSalariesSelected((SalariesDocuments) userObject);
			// onSalariesDocumentsSelected(item);
		} else if (userObject instanceof CostDocuments) {
			onCostsDocumentsSelected(item);
		} else if (userObject instanceof EnterpriseStatistics) {
			onEnterpriseStatisticsSelected((Statistics) userObject);
		} else if (userObject instanceof WorkplaceStatistics) {
			onWorkplaceStatisticsSelected((Statistics) userObject);
		} else if (userObject instanceof EnterpriseSalaryObject) {
			onEnterpriseSalariesSelected((EnterpriseSalaryObject) userObject);
		} else if (userObject instanceof ITDataObject) {
			onITDataSelected((ITDataObject) userObject);
		} else if (userObject instanceof WorkplaceSalaryObject) {
			onWorkplaceSalarySelected((WorkplaceSalaryObject) userObject); 
		} else if (userObject instanceof CalendarDraftObjectData) {
			onCalendarSelected((CalendarDraftObjectData) userObject);
		} else if (userObject instanceof SalaryDocuments) {
			onSalaryDocumentsSelected(item);
		} else if (userObject instanceof ISpinnable<?>) {
			onDocumentsSelected((ISpinnable<IDocument>) userObject);
		} else if (userObject instanceof EventsDraftObject) {
			onEventsDraftSelected((EventsDraftObject) userObject);
		} else if (userObject instanceof EmployeeEventsDraftObject) {
			onEmployeeEventsDraftSelected((EmployeeEventsDraftObject) userObject);
		} else if (userObject instanceof CategoryDraftObject) {
			onCategoryDraftSelected((CategoryDraftObject) userObject);
		} else if (userObject instanceof AgreementDraftObject) {
			onAgreementDraftSelected((AgreementDraftObject) userObject);
		} else if (userObject instanceof EmployeeCalendarDraftObjectData) {
			onEmployeeCalendarDraftSelected((EmployeeCalendarDraftObjectData) userObject);
		} else if (userObject instanceof EmployeeCalendarDraftObject) {
			onEmployeeNewCalendarDraftSelected((EmployeeCalendarDraftObject) userObject);
		} else if (userObject instanceof ContractBonusUI) {
			onEmployeeSSBonusSelected((ContractBonusObject) userObject);
		} else if (userObject instanceof EmployeeSalaryObject) {
			onEmployeeSalarySelected((EmployeeSalaryObject) userObject);
		} else if (userObject instanceof EmployeeDraftObject) {
			onEmployeeDraftSelected((EmployeeDraftObject) userObject);
		} 
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int keyCode = event.getNativeKeyCode();
		Object object = tree.getSelectedItem().getUserObject();

		if ((event.isControlKeyDown() && keyCode == KeyCodes.KEY_C) && (object instanceof EmployeeDraftObject)) {
			onEmployeeCopy(((EmployeeDraftObject) object).getEmployee());
		} else if (event.getNativeEvent().getCtrlKey() && keyCode == KeyCodes.KEY_V && object instanceof Workplace) {
			onEmployeePaste((Workplace) object);
		} else if (event.getNativeEvent().getCtrlKey() && keyCode == KeyCodes.KEY_X && object instanceof EmployeeDraftObject) {
			onCtrlXPressed(((EmployeeDraftObject) object).getEmployee());

		} else if (keyCode == KeyCodes.KEY_DELETE && object instanceof EmployeeDraftObject) {
			onSuprPressed(((EmployeeDraftObject) object).getEmployee());
		}
	}

	@Override
	public void onScroll(ScrollEvent event) {
		// If scrolling up, ignore the event.
		int oldScrollPos = lastScrollPos;
		lastScrollPos = scrollPanel.getVerticalScrollPosition();
		if (oldScrollPos >= lastScrollPos) {
			return;
		}

		for (TreeItem employeeItem : employeeCentinels) {

			if (elementInViewport(employeeItem.getElement())) {

				final int limit = getEmployeeLimit();

				final TreeItem workplaceItem = employeeItem.getParentItem();

				loadEmployees(workplaceItem, limit);

				employeeCentinels.remove(employeeItem);
			}
		}
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// stop the browser from opening the context menu
		event.preventDefault();
		event.stopPropagation();

		TreeItem item = tree.getSelectedItem();
		Object userObject = item.getUserObject();
		// TODO : I know that's so ugly and not Object oriented. But
		// it's much more clear than anything else. I promise
		// to change ( even improve ) it soon.
		if (userObject instanceof Enterprise) {
			onEnterpiseContextMenu((Enterprise) userObject, event);
		} else if (userObject instanceof Workplace) {
			onWorkplaceContextMenu((Workplace) userObject, event);
		} else if (userObject instanceof EmployeeDraftObject) {
			onEmployeeContextMenu(((EmployeeDraftObject) userObject).getEmployee(), event);
		} else if (userObject instanceof CCC) {
			onCCCContextMenu((CCC) userObject, event);
		} else if (userObject instanceof SalaryPreviewDocument) {
		} else if (userObject instanceof Activity) {
		} else if (userObject instanceof CostDocuments) {
		} else if (userObject instanceof SalaryDocuments) {
		} else if (userObject instanceof ISpinnable<?>) {
		}

	}

	public boolean elementInViewport(Element el) {

		int elTop = el.getAbsoluteTop();
		int elLeft = el.getAbsoluteLeft();
		int elWidth = el.getOffsetWidth();
		int elHeight = el.getOffsetHeight();

		int windowTop = Window.getScrollTop();
		int windowLeft = Window.getScrollLeft();
		int windowWidth = Window.getClientWidth();
		int windowHeight = Window.getClientHeight();

		return elTop < (windowTop + windowHeight) && elLeft < (windowLeft + windowWidth)
				&& (elTop + elHeight) > windowTop && (elLeft + elWidth) > windowLeft;

	}

	public SalaryDraftObject getSalaryDraftObject(int employeeId) {
		return null;
	}

	public void refresh(Employee employee) {
		final TreeItem employeeItem = getEmployeeItem(employee.getId());
		final TreeItem workplaceItem = employeeItem.getParentItem();
		employeesService.getEmployee(employee.getId(), new AsyncCallback<Employee>() {
			@Override
			public void onSuccess(Employee result) {
				employeeItem.removeItems();
				loadEmployee(workplaceItem, employeeItem, result);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO: Log???
			}
		});
	}

	public TreeItem refresh(final Workplace workplace) {

		return refreshWorkplace(workplace.getId(), ( treeItem ) -> {} );
	}

	public TreeItem refreshWorkplace(final Integer workplaceId, Consumer<TreeItem> callback ) {
		final TreeItem workplaceItem = getWorkplacetItem(workplaceId);
		final Integer enterpriseId = ((Enterprise) workplaceItem.getParentItem().getUserObject()).getId();
		employeesService.getEnterprises(new AsyncCallback<Enterprise[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Enterprise[] enterprises) {
				for (Enterprise newEnterprise : enterprises) {
					if (newEnterprise.getId().equals(enterpriseId)) {
						for (Workplace newWorkplace : newEnterprise.getWorkplaces()) {
							if (workplaceId.equals(newWorkplace.getId())) {
								boolean open = workplaceItem.getState();
								workplaceItem.removeItems();
								loadWorkplace(newEnterprise, workplaceItem, newWorkplace);
								if (open)
									onWorkplaceOpen(workplaceItem);
								
							}
						}

					}
				} // TODO: Only this workplace...
				callback.accept(workplaceItem);
			}

		});
		return workplaceItem;
	}

	public void refresh(final Activity activity) {
		final TreeItem activityItem = getActivityItem(activity.getId());

		employeesService.getEnterprises(new AsyncCallback<Enterprise[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Enterprise[] enterprises) {
				for (Enterprise enterprise : enterprises) {
					for (Activity newActivity : enterprise.getActivities()) {
						if (activity.getId().equals(newActivity.getId())) {
							String description = newActivity.getDescription();
							activityItem.setUserObject(newActivity);
							activityItem.setHTML(imageItemHTML(images.ine(), description));
						}
					}

				}
			} // TODO: Only this activity...

		});
	}

	public void refresh(final Enterprise enterprise) {
		final TreeItem enterpriseItem = getEnterpriseItem(enterprise.getId());
		employeesService.getEnterprises(new AsyncCallback<Enterprise[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Enterprise[] enterprises) {
				for (Enterprise newEnterprise : enterprises) {
					if (enterprise.getId().equals(newEnterprise.getId())) {
						enterpriseItem.setHTML(imageItemHTML(images.enterprise(), newEnterprise.getName()));
						enterpriseItem.setUserObject(newEnterprise);
					}

				}
			} // TODO: Only this enterprise...

		});
	}

	public void selectEmployee(int employeeId, boolean fireEvents) {

		TreeItem treeItem = getEmployeeItem(employeeId);
		
		for ( TreeItem item = treeItem.getParentItem(); item != null; item = item.getParentItem() )
			item.setState(true);
		
		tree.setSelectedItem(treeItem, fireEvents);
	}

	public void selectEmployee(String naf, boolean fireEvents) {
		select(naf);
	}

	public void selectItem(Predicate<TreeItem> predicate, boolean fireEvents) {

		TreeItem treeItem = getTreeItem(tree, predicate); //getEmployeeItem(employeeId);

		tree.setSelectedItem(treeItem, fireEvents);
	}

	public void selectSalaryDraft(int employeeId, boolean fireEvents) {
		TreeItem treeItem = getSalaryDraftItem(employeeId);
		tree.setSelectedItem(treeItem, fireEvents);
	}

	public SalaryDraftObject getSalaryDraft(int employeeId) {
		TreeItem treeItem = getSalaryDraftItem(employeeId);
		return treeItem == null ? null : (SalaryDraftObject) treeItem.getUserObject();
	}

	public void selectSalaryDraft(final int employeeId, int workplaceId, Date startDate, Date endDate, boolean fireEvents) {

		class SalaryDraftPredicate implements Predicate<TreeItem> {
			public boolean test(TreeItem t) {
				Object object = t.getUserObject();
				return (object instanceof SalaryDraftObject)
						&& (((SalaryDraftObject) object).getEmployee().getId() == employeeId);
			}

		}
		Predicate<TreeItem> predicate = new SalaryDraftPredicate();
		TreeItem workplaceItem = getWorkplacetItem(workplaceId);

		TreeItem draftItem = getTreeItem(workplaceItem, predicate, 0);

		if (draftItem != null) {
			Object object = draftItem.getUserObject();
			
			SalaryDraft salaryDraft = ((SalaryDraftObject) object).getSalaryDraft();
			salaryDraft.setStartDate(startDate);
			salaryDraft.setEndDate(endDate);
			salaryDraft.setIssueDate(endDate);
			
			tree.setSelectedItem(draftItem, fireEvents);
			tree.ensureSelectedItemVisible();
			draftItem.getElement().scrollIntoView();
		} else {
			selectEmployeeItem(workplaceItem, workplaceItem.getChildCount(), predicate, startDate, endDate, fireEvents);
		}
	}
	
	public int getVerticalScrollPosition() {
		return scrollPanel.getVerticalScrollPosition();
	}

	public void setVerticalScrollPosition(int position) {
		scrollPanel.setVerticalScrollPosition(position);
	}
	
	

	// ------------------------------------------------------------------------

	DomainEmployeesServiceAsync getEmployeesService() {
		return employeesService;
	}
	
	AonEmployeesTreeToolbar getOptionsToolbar() {
		return toolbar;
	}

	// -------------------------------------------------------------- protected

	protected Date getFromDate() {
		return formers ? (fromDate == null ? new Date(0) : fromDate) : DateUtils.getFirstDayOfMonth();
	}
	
	public String getNamePattern() {
		return namePattern;
	}

	protected TreeItem addEnterpriseItem(Enterprise enterprise) {
		final TreeItem enterpriseItem = new TreeItem(materialIconItemHTML("domain", enterprise.getName()));
		enterpriseItem.ensureDebugId(getId(enterprise));
		
		enterpriseItem.setUserObject(enterprise);
		tree.addItem(enterpriseItem);
		return enterpriseItem;
	}

	protected <T extends HasTreeItems> void addEnterpriseStatsItem(final T enterpriseItem, Enterprise enterprise) {
		addMaterialIconItem(enterpriseItem, "Estad\u00EDsticas", "bar_chart");
	}

	protected <T extends HasTreeItems> void addWorkplaceStatsItem(final T workplaceItem, Workplace workplace) {
		addMaterialIconItem(workplaceItem, "Estad\u00EDsticas", "bar_chart");
	}

	protected <T extends HasTreeItems> void addEnterpriseSalariesItem(final T enterpriseItem, Enterprise enterprise) {
		// Nominas Beta Empresa
		addMaterialIconItem(enterpriseItem, "N\u00F3minas", "payments")
			.setUserObject(new EnterpriseSalaryObject(enterprise));
	}

	protected <T extends HasTreeItems> void addWorkplaceSalariesItem(final T workplaceItem, Workplace workplace) {
		addMaterialIconItem(workplaceItem, "N\u00F3minas", "payments")
			.setUserObject(new WorkplaceSalaryObject(workplace));
	}

	protected <T extends HasTreeItems> void addEnterpriseCostsItem(final T enterpriseItem, Enterprise enterprise) {
		addMaterialIconItem(enterpriseItem, "Costes", "euro");
	}

	protected <T extends HasTreeItems> void addWorkplaceCostsItem(final T workplaceItem, Workplace workplace) {
		addMaterialIconItem(workplaceItem, "Costes", "euro");
	}

	protected <T extends HasTreeItems> void addWorkplaceCalendarItem(final T workplaceItem, Workplace workplace) {
		addMaterialIconItem(workplaceItem, "Calendario", "calendar_today")
				.setUserObject(new CalendarDraftObjectData(workplace.getId()));
	}

	protected <T extends HasTreeItems> void addEnterpriseActivityIem(final T enterpriseItem, Activity activity) {
		String description = activity.getDescription();
		TreeItem activityItem = addImageItem(enterpriseItem, description, images.ine());
		activityItem.setUserObject(activity);
		activityItem.ensureDebugId(getId(activity));
		activityItem.getElement().getStyle().setWidth(100, Unit.PCT);
		
		activity.getCccs().forEach( (ccc ) ->{
			addActivityCCCItem(activityItem, ccc);
		} );
	}

	protected <T extends HasTreeItems> void addActivityCCCItem(T activityItem, CCC ccc) {
		TreeItem cccItem = addImageItem(activityItem, ccc.getRegime() + ccc.getGeozone() + ccc.getCode(), images.segsocial());
		cccItem.setUserObject(ccc);
	}

	protected <T extends HasTreeItems> TreeItem addEnterpriseWorkplaceItem(final T enterpriseItem, Enterprise enterprise, Workplace workplace) {
		TreeItem workplaceItem;
		workplaceItem = new TreeItem();
		String description = workplace.getDescription();
		workplaceItem.setHTML(materialIconItemHTML("place", description));
		workplaceItem.setUserObject(workplace);
		workplaceItem.setVisible(isWorkPlaceVisible(workplace));
		workplaceItem.ensureDebugId(getId(workplace));	
		enterpriseItem.addItem(workplaceItem);
		loadWorkplace(enterprise, workplaceItem, workplace);
		workplaceItem.getElement().getParentElement().getStyle().setWidth(100, Unit.PCT);
		return workplaceItem;
	}

	protected <T extends HasTreeItems> void addWorkplaceEventsItem(final T workplaceItem, final EventsDraftObject eventsDraftObject) {
		final TreeItem eventsItem = addMaterialIconItem(workplaceItem, "Variables C\u00E1lculo", "calendar_month");
		eventsItem.setUserObject(eventsDraftObject);
	}

	protected <T extends HasTreeItems> void addWorkplaceAgreementItem(final T workplaceItem, Enterprise enterprise, Agreement agreement) {
		final TreeItem agreementItem = addMaterialIconItem(workplaceItem, agreement.getDescription(), "article");

		AgreementDraft agreementDraft = new AgreementDraft();
		agreementDraft.setId(agreement.getId());
		agreementDraft.setDomain(agreement.getDomain());
		agreementDraft.setDescription(agreement.getDescription());
		agreementDraft.setSSNumber(agreement.getSSNumber());
		agreementDraft.setStartDate(DateUtils.getFirstDayOfMonth());
		agreementDraft.setEndDate(DateUtils.getLastDayOfMonth());
		final AgreementDraftObject agreementDraftObject = new AgreementDraftObject(
				enterprise.getDomain(),
				Wnd.getCurrentDomainNameURL(),
				Wnd.getCurrentUser(),
				agreementDraft, employeesService);

		employeesService.getChanges(
				Wnd.getCurrentDomainNameURL(),
				agreement, new AsyncCallback<SortedSet<Date>>() {
			@Override
			public void onFailure(Throwable caught) {
				agreementItem.setUserObject(agreementDraftObject);
			}

			public void onSuccess(SortedSet<Date> result) {
				if (!CollectionUtils.isEmpty(result)) {
					Date lastChange = result.last();
					agreementDraftObject.setStartDate(DateUtils.getFirstDayOfMonth(lastChange));
					agreementDraftObject.setEndDate(DateUtils.getLastDayOfMonth(lastChange));
				}
				agreementItem.setUserObject(agreementDraftObject);
			};
		});

		agreementDraftObject.addListener(new UndoManager.Listener() {
			@Override
			public void onChange(UndoManager undoManager) {
				ImageResource resource = agreementDraftObject.canUndo() ? images.agreement_changed()
						: images.agreement();
				agreementItem.setHTML(materialIconItemHTML("article", agreementDraftObject.getDescription()));
			}
		});
	}

	protected void getServiceEnterpriseStats(Enterprise enterprise, Consumer<EnterpriseStatistics> consumer) {
		employeesService.getEnterpriseStats(enterprise.getId(), new AsyncCallback<Statistics>() {

			@Override
			public void onSuccess(Statistics stats) {
				consumer.accept(new EnterpriseStatistics(stats));
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Apéndice de método generado automáticamente
				Window.alert(caught.getLocalizedMessage());
			}
		});
	}
	
	protected void getServiceEnterpriseCost(Enterprise enterprise, Consumer<EnterpriseCostDocuments> consumer ) {
		employeesService.getEnterpriseCosts(enterprise.getId(), new AsyncCallback<List<Cost>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<Cost> costs) {
				consumer.accept(new EnterpriseCostDocuments(costs));
			}
		});
		
	}

	protected void getServiceWorkplaceEmployees(Workplace workplace, int offset, int limit, Consumer<List<Employee>> consumer) {
		employeesService.getEmployees(workplace.getId(), getFromDate(), namePattern, offset, limit,
				new AsyncCallback<List<Employee>>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getLocalizedMessage());

					}

					@Override
					public void onSuccess(List<Employee> employees) {
						consumer.accept(employees);
					}
				});
	}
	
	protected void getServiceWorkplaceCost(Workplace workplace, Consumer<WorkplaceCostDocuments> consumer) {
		employeesService.getWorkplaceCosts(workplace.getId(), new AsyncCallback<List<Cost>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<Cost> costs) {
				consumer.accept(new WorkplaceCostDocuments(costs));
			}
		});
	}

	protected void getServiceWorkplaceStatistics(Workplace workplace, Consumer<Statistics> consumer) {
		employeesService.getWorkplaceStats(workplace.getId(), new AsyncCallback<Statistics>() {

			@Override
			public void onSuccess(Statistics stats) {
				consumer.accept(new WorkplaceStatistics(stats));
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Apéndice de método generado automáticamente
				Window.alert(caught.getLocalizedMessage());
			}
		});
	}

	protected void loadWorkplaceCosts(final TreeItem workplaceItem, Workplace workplace) {
		final TreeItem costsItem = workplaceItem.getChild(WORKPLACE_COSTS_INDEX);

		if (costsItem.getUserObject() == null) {
			getServiceWorkplaceCost(workplace, costsItem::setUserObject );
		} // end-if: Costs of this workplace haven't been loaded yet.
	}

	protected void loadWorkplaceStats(final TreeItem workplaceItem, Workplace workplace) {
		final TreeItem statisticsItem = workplaceItem.getChild(WORKPLACE_STATISTICS_INDEX);

		if (null == statisticsItem.getUserObject()) {
			getServiceWorkplaceStatistics(workplace, statisticsItem::setUserObject );
		}
	}
	protected <T extends HasTreeItems & IsTreeItem> void addWorkplaceEmployeeItem(T workplaceItem, Employee employee) {
		TreeItem employeeItem = new TreeItem();
		workplaceItem.addItem(employeeItem);
		loadEmployee(workplaceItem.asTreeItem(), employeeItem, employee);
	}

	protected void addWorkplaceEmployeeItems(TreeItem workplaceItem, TreeItem employeeItem, Employee employee,
			EmployeeDraftObject employeeDraftObject) {
		TreeItem salarytItem = addMaterialIconItem(employeeItem, "N\u00F3minas", "payments");
		EmployeeSalaryObject employeeSalaryObject = new EmployeeSalaryObject(employee.getId(), employee.getFullname());
		salarytItem.setUserObject(employeeSalaryObject);
		salarytItem.ensureDebugId(getId(employee)+"-employeesalary");
		
		//Employee Calendar (BETA)
		TreeItem calendarNewDraftItem = addMaterialIconItem(employeeItem, "Calendario", "calendar_today");
		EmployeeCalendarDraftObject employeeCalendarDraftObject = new EmployeeCalendarDraftObject(employee.getId());
		
		calendarNewDraftItem.setUserObject(employeeCalendarDraftObject);
		calendarNewDraftItem.ensureDebugId(getId(employee)+"-employeecalendarnew");
		
		
		if (extended) {

			Date salaryDate = DateUtils.before(DateUtils.after(new Date(), employee.getStartDate()),
					employee.getEndDate());
			Date startDate = DateUtils.getFirstDayOfMonth(salaryDate);
			Date endDate = DateUtils.getLastDayOfMonth(salaryDate);
			Date issueDate = endDate;

			TreeItem salaryDraftItem = addMaterialIconItem(employeeItem, "Borrador", "edit");
			salaryDraftItem.ensureDebugId(getId(employee)+"-draft");

			SalaryDraft salaryDraft = new SalaryDraft();
			salaryDraft.setEmployee(employee);
			salaryDraft.setStartDate(startDate);
			salaryDraft.setEndDate(endDate);
			salaryDraft.setIssueDate(issueDate);
			salaryDraft.setType(Type.SALARY);

			SalaryDraftObject draftObject = new SalaryDraftObject(salaryDraft, /*dataObject,*/ employeesService);
			salaryDraftItem.setUserObject(draftObject);

			final TreeItem employeeEventsItem = addMaterialIconItem(employeeItem, "Variables C\u00E1lculo", "calendar_month");
			EmployeeEventsDraftObject employeeEventsDraftObject = new EmployeeEventsDraftObject(employee.getId());
			
			employeeEventsItem.setUserObject(employeeEventsDraftObject);
			
			employeeEventsItem.ensureDebugId(getId(employee)+"-events");

			//Add employeeCalendar to Draft
			employeeDraftObject.setEmployeeCalendar(employeeCalendarDraftObject);
			employeeEventsDraftObject.setEmployeeCalendar(employeeCalendarDraftObject);
						
			Category category = employee.getCategory();

			// Agreement Category
			if (category == null)
				return;

			Agreement agreement = category.getAgreement();
			Agreement workplaceAgreement = ((Workplace) workplaceItem.getUserObject()).getAgreement();

			if (workplaceAgreement != null && NumberUtils.equals(workplaceAgreement.getId(), agreement.getId()))
				return;

			TreeItem enterpriseItem = workplaceItem.getParentItem();
			Enterprise enterprise = (Enterprise) enterpriseItem.getUserObject();

			final TreeItem categoryItem = addMaterialIconItem(employeeItem,
					category.getLevel() + ". " + category.getDescription(), "article");
			categoryItem.ensureDebugId(getId(employee)+"-category");
			
			CategoryDraft categoryDraft = new CategoryDraft();
			categoryDraft.setId(agreement.getId());
			categoryDraft.setDomain(agreement.getDomain());
			categoryDraft.setLevelId(category.getLevelId());
			categoryDraft.setDescription(agreement.getDescription());
			categoryDraft.setSSNumber(agreement.getSSNumber());
			categoryDraft.setStartDate(DateUtils.getFirstDayOfMonth());
			categoryDraft.setEndDate(DateUtils.getLastDayOfMonth());
			final CategoryDraftObject categoryDraftObject = new CategoryDraftObject(
					enterprise.getDomain(),
					Wnd.getCurrentDomainNameURL(),
					Wnd.getCurrentUser(),
					categoryDraft, 
					employeesService);

			employeesService.getChanges(Wnd.getCurrentDomainNameURL(),agreement, new AsyncCallback<SortedSet<Date>>() {
				@Override
				public void onFailure(Throwable caught) {
					categoryItem.setUserObject(categoryDraftObject);
				}

				public void onSuccess(SortedSet<Date> result) {
					if (!CollectionUtils.isEmpty(result)) {
						Date lastChange = result.last();
						categoryDraftObject.setStartDate(DateUtils.getFirstDayOfMonth(lastChange));
						categoryDraftObject.setEndDate(DateUtils.getLastDayOfMonth(lastChange));
					}
					categoryItem.setUserObject(categoryDraftObject);
				};
			});
			
		}
	}

	protected SalaryDraftObject newSalaryDraftObject(Employee employee) {
		Date salaryDate = DateUtils.before(DateUtils.after(new Date(), employee.getStartDate()),
				employee.getEndDate());
		Date startDate = DateUtils.getFirstDayOfMonth(salaryDate);
		Date endDate = DateUtils.getLastDayOfMonth(salaryDate);
		Date issueDate = endDate;


		SalaryDraft salaryDraft = new SalaryDraft();
		salaryDraft.setEmployee(employee);
		salaryDraft.setStartDate(startDate);
		salaryDraft.setEndDate(endDate);
		salaryDraft.setIssueDate(issueDate);
		salaryDraft.setType(Type.SALARY);

		SalaryDraftObject draftObject = new SalaryDraftObject(salaryDraft, /*dataObject,*/ employeesService);
		return draftObject;
	}

	protected EmployeeDraftObject newEmployeeDraftObject(Employee employee) {
		TreeItem employeeItem = 
		getTreeItem(tree, t ->  
		( t.getUserObject() instanceof SalaryDraftObject )
		&& ((SalaryDraftObject) t.getUserObject() ).getEmployee() == employee
		);
		TreeItem workplaceItem = employeeItem.getParentItem();
		while ( !( workplaceItem.getUserObject() instanceof Workplace ) )
			workplaceItem = workplaceItem.getParentItem();
		
		Workplace workplace = ( Workplace ) workplaceItem.getUserObject();
		return new EmployeeDraftObject(workplace, employee);
	}

	// ------------------------------------------------------------------------
	private TreeItem loadWorkplace(Enterprise enterprise, final TreeItem workplaceItem, Workplace workplace) {

		addWorkplaceCostsItem(workplaceItem, workplace);
		
		addWorkplaceSalariesItem(workplaceItem, workplace);

		addWorkplaceCalendarItem(workplaceItem, workplace);
		
		addWorkplaceStatsItem(workplaceItem, workplace);
		
		if (extended) {

			// final TreeItem eventsItem = new TreeItem();

			// --------------------------------------------------------------
			//

			Agreement agreement = workplace.getAgreement();

			final EventsDraftObject eventsDraftObject;

			if (Enterprise.isGPS(enterprise))
				eventsDraftObject = new EventsDraftObject(workplace.getId(),
						agreement != null ? agreement.getId() : null,
						// @formatter:off
						new AbstractEventsDraftObject.EnumEventMetaData("DESEMPE\u00D1O",
								"DESEMPE\u00D1O", "Desempe\u00F1o por Trabajador y Jornada", "", new String[] { "4",
										"8", "10", "12", "L", "LT", "LR", "F", "FT", "FR", "V", "B", "P", "AI", "M" },
								DateField.DAY)
				// @formatter:on
				);
			else
				eventsDraftObject = new EventsDraftObject(workplace.getId(),
						agreement != null ? agreement.getId() : null,
						new BooleanEventMetaData("DIAS_EFECTIVOS", DateField.DAY),
						new BooleanEventMetaData("DIAS_VACACIONES", DateField.DAY),
						// new BooleanEventMetaData("HUELGA", DAY),
						new DecimalEventMetaData("COEFICIENTE_ERE", DateField.DAY),
						new EventMetaData("OBSERVACIONES", DateField.MONTH));

			
			addWorkplaceEventsItem(workplaceItem, eventsDraftObject);
		}

		// TODO: comento convenio antiguo
//		Agreement agreement = workplace.getAgreement();
		
//		if (extended && (agreement != null)) {
//
//			addWorkplaceAgreementItem(workplaceItem, enterprise, agreement);
//
//		} 
		
		if ( workplaceItem.getChildCount() == 0 ) {
			workplaceItem.addItem(new SafeHtmlBuilder().appendEscaped("fake").toSafeHtml());
		}
		
		return workplaceItem;
	}


	private void loadEmployees(final TreeItem workplaceItem, final int limit) {
		loadEmployees(workplaceItem, limit, null);
	}

	private void loadEmployees(final TreeItem workplaceItem, final int limit,
			final AsyncCallback<List<Employee>> callback) {

		Workplace workplace = (Workplace) workplaceItem.getUserObject();

		int offset = workplaceItem.getChildCount() - getEmployeesOffset(workplaceItem);

		employeesService.getEmployees(workplace.getId(), getFromDate(), namePattern, offset, limit,
				new AsyncCallback<List<Employee>>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getLocalizedMessage());
						if (callback != null)
							callback.onFailure(caught);

					}

					@Override
					public void onSuccess(List<Employee> employees) {
						loadEmployess(workplaceItem, employees, limit);
						if (callback != null)
							callback.onSuccess(employees);
					}
				});
	}

	private void onEnterpriseOpen(TreeItem enterpriseItem) {

		Enterprise enterprise = (Enterprise) enterpriseItem.getUserObject();

		final TreeItem costsItem = enterpriseItem.getChild(ENTERPRISE_COSTS_INDEX);

		if (null == costsItem.getUserObject()) {
			getServiceEnterpriseCost(enterprise, costsItem::setUserObject);
		} // end-if: Cost of enterprise have been already loaded.

		final TreeItem statisticsItem = enterpriseItem.getChild(ENTERPRISE_STATISTICS_INDEX);

		if (null == statisticsItem.getUserObject()) {
			getServiceEnterpriseStats(enterprise, statisticsItem::setUserObject);
		}

	}

	/*
	 * Workplace Item has been expanded +.
	 */
	private void onWorkplaceOpen(final TreeItem workplaceItem) {
		removeFakeChild(workplaceItem);
		onWorkplaceOpen(workplaceItem, getEmployeeLimit(), () -> {} );
	}
	
	private void removeFakeChild(TreeItem treeItem) {
		TreeItem child = treeItem.getChild(0);
		if ( child.getUserObject() == null ) {
			child.remove();
		}
	}
	
	private void onWorkplaceOpen(final TreeItem workplaceItem, final int limit, Runnable callback) {
		Workplace workplace = (Workplace) workplaceItem.getUserObject();

		loadWorkplaceCosts(workplaceItem, workplace);

		loadWorkplaceStats(workplaceItem, workplace);
		
		//if (workplaceItem.getChildCount() > getEmployeesOffset(workplaceItem)) {
		//	callback.run();
		//	return;
		//} 
		// end-if: Employees of this workplace already loaded .
		
		int workplaceChilds = workplaceItem.getChildCount();
		int employeesOffset = getEmployeesOffset(workplaceItem);
		
		int start = Math.max(workplaceChilds - employeesOffset ,0);
		
		getServiceWorkplaceEmployees(workplace, start, limit, employees -> {
			loadEmployess(workplaceItem, employees, limit);
			callback.run();
		});

	}
	
	
	private void onEmployeeOpen(TreeItem employeeItem) {

//		final TreeItem salariesItem = employeeItem.getChild(EMPLOYEE_SALARIES_INDEX);
//		if (salariesItem.getUserObject() != null) {
//			return;
//		} // end-if: Salaries of this employee have been already loaded.

		Employee employee = ((EmployeeDraftObject) employeeItem.getUserObject()).getEmployee();
//		employeesService.getSalaries(employee, new AsyncCallback<List<Salary>>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				// TODO Auto-generated method stub
//				Window.alert(caught.getLocalizedMessage());
//
//			}
//
//			@Override
//			public void onSuccess(List<Salary> salaries) {
//				SalaryDocuments documents = new SalaryDocuments(salaries, employeesService);
//				salariesItem.setUserObject(documents);
//			}
//		});
		final TreeItem irpfOutcomesItem = employeeItem.getChild(EMPLOYEE_IRPFOUTCOMES_INDEX);
		if (irpfOutcomesItem.getUserObject() != null) {
			return;
		} // end-if: Salaries of this employee have been already loaded.

		employeesService.getIrpfs(employee, new AsyncCallback<List<Irpf>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<Irpf> irpfOutcomes) {
				IrpfDocuments documents = new IrpfDocuments(irpfOutcomes, employeesService);
				irpfOutcomesItem.setUserObject(documents);
			}
		});
	}

	private void onLoadAvaiableEmployees(Map<String, String> map) {
		for (Listener listener : listeners) {
			listener.onLoadAvaiableEmployees(map);
		}
	}

	private void onEnterpriseSelected(Enterprise enterprise) {
		for (Listener listener : listeners) {
			listener.onEnterpriseSelected(enterprise);
		}
	}

	private void onWorkplaceSelected(Workplace workplace) {
		for (Listener listener : listeners) {
			listener.onWorkplaceSelected(workplace);
		}
	}

	private void onWorkplaceCostsSelected(CostDocuments docs) {
		for (Listener listener : listeners) {
			listener.onWorkplaceCostsSelected(docs);
		}
	}

	private void onEnetrpriseCostsSelected(CostDocuments docs) {
		for (Listener listener : listeners) {
			listener.onEnterpriseCostsSelected(docs);
		}
	}

	private void onSalariesSelected(SalariesDocuments docs) {
		for (Listener listener : listeners) {
			listener.onSalariesSelected(docs);
		}
	}

	private void onCalendarSelected(CalendarDraftObjectData calendar) {
		for (Listener listener : listeners)
			listener.onCalendarSelected(calendar);
	}

	private void onWorkplaceStatisticsSelected(Statistics stats) {
		for (Listener listener : listeners) {
			listener.onWorkplaceStatisticsSelected(stats);
		}
	}
	
	private void onEnterpriseStatisticsSelected(Statistics stats) {
		for (Listener listener : listeners) {
			listener.onEnterpriseStatisticsSelected(stats);
		}
	}

	private void onEnterpriseSalariesSelected(EnterpriseSalaryObject enterpriseSalaryObject) {
		for (Listener listener : listeners) {
			listener.onEnterpriseSalariesSelected(enterpriseSalaryObject);
		}
	}

	private void onITDataSelected(ITDataObject dataObject) {
		for (Listener listener : listeners) {
			listener.onITDataSelected(dataObject);
		}
	}
	
	private void onWorkplaceSalarySelected(WorkplaceSalaryObject dataObject) {
		for (Listener listener : listeners) {
			listener.onWorkplaceSalarySelected(dataObject);
		}
	}
	
	private void onEmployeeCopy(Employee employee) {
		for (Listener listener : listeners) {
			listener.onEmployeeCopy(employee);
		}
	}

	private void onEmployeePaste(Workplace workplace) {
		for (Listener listener : listeners) {
			listener.onEmployeePaste(workplace);
		}
	}

	private void onCtrlXPressed(Employee employee) {
		for (Listener listener : listeners) {
			listener.onEmployeeCut(employee);
		}
	}

	private void onSuprPressed(Employee employee) {
		for (Listener listener : listeners) {
			listener.onSuprPress(employee);
		}
	}
	
	private void onCollapseEmployees() {
		for (Listener listener : listeners) {
			listener.onCollapseEmployees();
		}
	}
	
	private void onShowEmployees(boolean isCollapsed) {
		for (Listener listener : listeners) {
			listener.onShowEmployees(isCollapsed);
		}
	}

	private void onSalariesDocumentsSelected(final TreeItem salariesItem) {
		TreeItem parentItem = salariesItem.getParentItem();
		Object parent = parentItem.getUserObject();
		if (parent instanceof Workplace)
			onSalariesDocumentsSelected((Workplace) parent, salariesItem);
		else
			onSalariesDocumentsSelected((Enterprise) parent, salariesItem);

	}

	private void onSalariesDocumentsSelected(Workplace workplace, final TreeItem salariesItem) {
		employeesService.getWorkplaceCosts(workplace.getId(), new AsyncCallback<List<Cost>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<Cost> costs) {
				// CostDocuments documents = new CostDocuments(costs,
				// employeesService);
				// costsItem.setUserObject(documents);

				SalariesDocuments docs = new SalariesDocuments(costs, employeesService);
				salariesItem.setUserObject(docs);
				for (Listener listener : listeners) {
					listener.onSalariesSelected(docs);
				}
			}
		});
	}

	private void onSalariesDocumentsSelected(Enterprise enterprise, final TreeItem salariesItem) {
		employeesService.getEnterpriseCosts(enterprise.getId(), new AsyncCallback<List<Cost>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<Cost> costs) {
				// CostDocuments documents = new CostDocuments(costs,
				// employeesService);
				// costsItem.setUserObject(documents);

				SalariesDocuments docs = new SalariesDocuments(costs, employeesService);
				salariesItem.setUserObject(docs);
				for (Listener listener : listeners) {
					listener.onSalariesSelected(docs);
				}
			}
		});
	}

	private void onCostsDocumentsSelected(final TreeItem costsItem) {
		TreeItem parentItem = costsItem.getParentItem();
		Object parent = parentItem.getUserObject();
		if (parent instanceof Workplace)
			onCostsDocumentsSelected((Workplace) parent, costsItem);
		else
			onCostsDocumentsSelected((Enterprise) parent, costsItem);
	}

	private void onCostsDocumentsSelected(Workplace workplace, final TreeItem costsItem) {
		employeesService.getWorkplaceCosts(workplace.getId(), new AsyncCallback<List<Cost>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<Cost> costs) {
				CostDocuments docs = new WorkplaceCostDocuments(costs);
				costsItem.setUserObject(docs);

				costsItem.setUserObject(docs);
				for (Listener listener : listeners) {
					listener.onWorkplaceCostsSelected(docs);
				}
			}
		});
	}

	private void onCostsDocumentsSelected(Enterprise enterprise, final TreeItem costsItem) {
		employeesService.getEnterpriseCosts(enterprise.getId(), new AsyncCallback<List<Cost>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<Cost> costs) {
				CostDocuments docs = new EnterpriseCostDocuments(costs);
				costsItem.setUserObject(docs);

				costsItem.setUserObject(docs);
				for (Listener listener : listeners) {
					listener.onEnterpriseCostsSelected(docs);
				}
			}
		});
	}

	private void onSalaryDocumentsSelected(final TreeItem salariesItem) {
		TreeItem employeeItem = salariesItem.getParentItem();
		Employee employee = ((EmployeeDraftObject) employeeItem.getUserObject()).getEmployee();
		employeesService.getSalaries(employee, new AsyncCallback<List<Salary>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<Salary> salaries) {
				SalaryDocuments docs = new SalaryDocuments(salaries, employeesService);
				salariesItem.setUserObject(docs);
				for (Listener listener : listeners) {
					listener.onSalariesSelected(docs);
				}
			}
		});
	}

	private void onDocumentsSelected(ISpinnable<IDocument> docs) {
		for (Listener listener : listeners) {
			listener.onDocumentsSelected(docs);
		}
	}

	private void onSalaryDraftSelected(SalaryDraftObject salaryDraftObject) {
		for (Listener listener : listeners) {
			listener.onSalaryDraftSelected(salaryDraftObject);
		}
	}

	private void onSalaryPreviewSelected(SalaryPreviewDocument salaryPreviewDocument) {
		for (Listener listener : listeners) {
			listener.onSalaryPreviewSelected(salaryPreviewDocument);
		}
	}

	private void onActivitySelected(Activity activity) {
		for (Listener listener : listeners) {
			listener.onActivitySelected(activity);
		}
	}

	private void onCCCSelected(CCC ccc) {
		for (Listener listener : listeners) {
			listener.onCCCSelected(ccc);
		}
	}

	private void onCCCContextMenu(CCC ccc, ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onCCCContextMenu(ccc, event);
		}
	}

	private void onWorkplaceContextMenu(Workplace workplace, ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onWorkplaceContextMenu(workplace, event);
		}
	}

	private void onEnterpiseContextMenu(Enterprise enterprise, ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onEnterpriseContextMenu(enterprise, event);
		}
	}

	private void onEmployeeContextMenu(Employee employee, ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onEmployeeContextMenu(employee, event);
		}
	}

	private void onEventsDraftSelected(EventsDraftObject eventsDraftObject) {
		for (Listener listener : listeners) {
			listener.onEventsDraftSelected(eventsDraftObject);
		}
	}

	private void onEmployeeEventsDraftSelected(EmployeeEventsDraftObject employeeEventsDraftObject) {
		for (Listener listener : listeners) {
			listener.onEmployeeEventsDraftSelected(employeeEventsDraftObject);
		}
	}

	private void onCategoryDraftSelected(CategoryDraftObject categoryDraftObject) {
		for (Listener listener : listeners) {
			listener.onCategoryDraftSelected(categoryDraftObject);
		}
	}

	private void onAgreementDraftSelected(AgreementDraftObject agreementDraftObject) {
		for (Listener listener : listeners) {
			listener.onAgreementDraftSelected(agreementDraftObject);
		}
	}

	private void onEmployeeCalendarDraftSelected(EmployeeCalendarDraftObjectData employeeEventsDraftObject) {
		for (Listener listener : listeners) {
			listener.onEmployeeCalendarSelected(employeeEventsDraftObject);
		}
	}
	
	private void onEmployeeNewCalendarDraftSelected(EmployeeCalendarDraftObject employeeEventsDraftObject) {
		for (Listener listener : listeners) {
			listener.onEmployeeNewCalendarSelected(employeeEventsDraftObject);
		}
	}
	
	private void onEmployeeSSBonusSelected(ContractBonusObject contractBonusObject) {
		for (Listener listener : listeners) {
			listener.onEmployeeSSBonusSelected(contractBonusObject);
		}
	}
	
	private void onEmployeeSalarySelected(EmployeeSalaryObject employeeSalaryObject) {
		for (Listener listener : listeners) {
			listener.onEmployeeSalarySelected(employeeSalaryObject);
		}
	}
	
	private void onEmployeeDraftSelected(EmployeeDraftObject employeeDraftObject) {
		for (Listener listener : listeners) {
			listener.onEmployeeDraftSelected(employeeDraftObject);
		}	
	}

	public void addEmployee(TreeItem workplaceItem, Employees employee, int limit) {

		List<Employees> employees = new ArrayList<Employees>();
		employees.add(employee);

	}

	private void loadEmployess(TreeItem workplaceItem, List<Employee> employees, int limit) {
		for (Employee employee : employees) {
			if (employee.getId() < 0)
				continue;
			addWorkplaceEmployeeItem(workplaceItem, employee);
		}

		if (employees.size() >= limit) {
			int last = workplaceItem.getChildCount() - 1;
			TreeItem employeeCentinel = workplaceItem.getChild(last - (EMPLOYEE_SCROLL_GAP));
			employeeCentinels.add(employeeCentinel);
		} // end-if : If's very likely that exists more employees.

	}

	private void loadEmployee(TreeItem workplaceItem, TreeItem employeeItem, Employee employee) {

		boolean current = isActive(employee);

		String fullName = employee.getFullname();

		StringBuffer text = new StringBuffer(fullName);
		if (endDate && (employee.getEndDate() != null)) {
			text.append(" (");
			text.append(END_DATE_FORMAT.format(employee.getEndDate()));
			text.append(")");
		}

		employeeItem.setHTML(materialIconItemHTML(current ? "person" : "person_off", text.toString()));
		EmployeeDraftObject employeeDraftObject = new EmployeeDraftObject(((Workplace) workplaceItem.getUserObject()), employee);
		employeeItem.setUserObject(employeeDraftObject);
		employeeItem.ensureDebugId(getId(employee));

		addWorkplaceEmployeeItems(workplaceItem, employeeItem, employee, employeeDraftObject);
		
		tryNewAONTheme(employeeItem, employeeDraftObject);
	}

	protected void tryNewAONTheme(TreeItem employeeItem, EmployeeDraftObject employeeDraftObject) {
		SalaryDraftObject salaryDraftObject = getUserObject(employeeDraftObject, SalaryDraftObject.class);
		if ( salaryDraftObject == null ) {
			salaryDraftObject = newSalaryDraftObject(employeeDraftObject.getEmployee());
			employeeItem.setUserObject(salaryDraftObject);
		}else {
			employeeItem.setUserObject(salaryDraftObject);
			TreeItem salaryDraftItem = getTreeItem(salaryDraftObject);
			salaryDraftItem.setUserObject(employeeDraftObject);
			salaryDraftItem.setHTML(materialIconItemHTML("edit", "Contrato"));
			salaryDraftItem.ensureDebugId(getId(employeeDraftObject.getEmployee())+"-contract");
		}
	}


	/**
	 * A helper method to simplify adding tree items that have attached material icon.
	 * {@link #addImageItem(TreeItem, String, childs, ImageResource) code}
	 * 
	 */
	private <T extends HasTreeItems> TreeItem addMaterialIconItem(T root, String title, String materialIcon) {
		TreeItem item = new TreeItem(materialIconItemHTML(materialIcon, title));
		root.addItem(item);
		return item;
	}

	private TreeItem insertMatrialIconItem(TreeItem root, int beforeIndex, String title, String materialIcon) {
		TreeItem item = new TreeItem(materialIconItemHTML(materialIcon, title));
		root.insertItem(beforeIndex, item);
		return item;
	}

	/**
	 * A helper method to simplify adding tree items that have attached images.
	 * {@link #addImageItem(TreeItem, String, childs, ImageResource) code}
	 * 
	 */
	private <T extends HasTreeItems> TreeItem addImageItem(T root, String title, ImageResource imageProto) {
		TreeItem item = new TreeItem(imageItemHTML(imageProto, title));
		root.addItem(item);
		return item;
	}

	/**
	 * Generates HTML for a tree item with an attached icon.
	 */
	private SafeHtml imageItemHTML(ImageResource imageProto, String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		builder.append(' ');
		builder.appendEscaped(capitalize(title));
		return builder.toSafeHtml();
	}

	/**
	 * Generates HTML for a tree item with an attached material icon.
	 */
	private SafeHtml materialIconItemHTML(String materialIcon, String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(TEMPLATE.materialIcon(materialIcon));
		builder.append(' ');
		builder.appendEscaped(capitalize(title));
		return builder.toSafeHtml();
	}

	private void initViewButton(final Button viewButton) {
		viewButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel();

			private MenuItem formerMenuItem;
			private MenuItem inactiveMenuItem;
			private MenuItem endDateMenuItem;
			private MenuItem filterMenuItem;
			private FilterDialog filterDialog;

			{
				MenuBar menuBar = new MenuBar(true);

				endDateMenuItem = new MenuItem("Fecha Fin", new Command() {
					@Override
					public void execute() {
						endDate = !endDate;
						showEndDate(endDate);
						endDateMenuItem.setStyleName("aon-MenuItemCheckYes", endDate);
						popup.hide();
					}
				});
				endDateMenuItem.setStyleName("aon-MenuItemCheckYes", endDate);
				endDateMenuItem.ensureDebugId("endDateMenuItem");
				menuBar.addItem(endDateMenuItem);

				formerMenuItem = new MenuItem("Antiguos Empleados", new Command() {
					@Override
					public void execute() {
						formers = !formers;
						changeVisibleWorkplaces();
						changeVisibleEmployees();
						formerMenuItem.setStyleName("aon-MenuItemCheckYes", formers);
						popup.hide();
					}
				});
				formerMenuItem.setStyleName("aon-MenuItemCheckYes", formers);
				formerMenuItem.ensureDebugId("formerMenuItem");
				menuBar.addItem(formerMenuItem);

				inactiveMenuItem = new MenuItem("Centros Inactivos", new Command() {
					@Override
					public void execute() {
						try {
							inactive = !inactive;
							changeVisibleWorkplaces();
							inactiveMenuItem.setStyleName("aon-MenuItemCheckYes", inactive);
							popup.hide();
						} catch (Throwable t) {
							Window.alert(t.getMessage());
						}
					}
				});
				inactiveMenuItem.setStyleName("aon-MenuItemCheckYes", inactive);
				inactiveMenuItem.ensureDebugId("inactiveMenuItem");
				menuBar.addItem(inactiveMenuItem);

				filterDialog = new FilterDialog() {
					{
						setName(namePattern);
						setDateFrom(fromDate);
						setDateTimeFormat(END_DATE_FORMAT);
					}

					protected void onAccept() {
						try {

							String newNamePattern = getName();
							if (newNamePattern != null) {
								newNamePattern = newNamePattern.trim();
								if (newNamePattern.isEmpty()) {
									newNamePattern = null;
								}
							}

							Date newFromDate = getDateFrom();
							if (newFromDate != null)
								newFromDate = DateUtils.toUTC(getDateFrom());

							boolean nameChanged = !StringUtils.equalsIgnoreCase(namePattern, newNamePattern);

							boolean dateChanged = !DateUtils.equals(fromDate, newFromDate);

							if (nameChanged || dateChanged) {
								fromDate = newFromDate;
								namePattern = newNamePattern;
								changeVisibleEmployees();
							}

						} catch (Throwable e) {
							Window.alert(e.getLocalizedMessage());
						}
					};
				};

				filterMenuItem = new MenuItem("Filtros...", new Command() {
					@Override
					public void execute() {
						popup.hide();
						filterDialog.center();
						filterDialog.show();
					}
				});
				filterMenuItem.ensureDebugId("filterMenuItem");
				menuBar.addItem(filterMenuItem);

				popup.add(menuBar);
				popup.setStyleName("gwt-MenuBarPopup");
				popup.setAutoHideEnabled(true);
			}

			@Override
			public void onClick(ClickEvent event) {

				int left = viewButton.getAbsoluteLeft();
				int top = viewButton.getAbsoluteTop() + viewButton.getOffsetHeight();
				popup.setPopupPosition(left, top);
				popup.show();
			}
		});

	}

	private void collapse() {
		for (int i = 0; i < tree.getItemCount(); i++) {
			collapse(tree.getItem(i));
		}
	}

	private void collapse(TreeItem treeItem) {
		for (int i = 0; i < treeItem.getChildCount(); i++) {
			TreeItem child = treeItem.getChild(i);
			collapse(child);
		}
		treeItem.setState(false);
	}

	private int getEmployeeLimit() {
		TreeItem root = tree.getItem(0);

		int itemHeight = root.getOffsetHeight();
		int browserHeight = Window.getClientHeight();
		int visibleItems = browserHeight / itemHeight;
		return Math.max(visibleItems + 1, MIN_EMPLOYEE_LIMIT);
	}

	/**
	 * Change formers. Note that we assume that workplaces start at position 2,
	 * third child and extend until last one.
	 * 
	 * @param formers
	 */

	private void changeVisibleEmployees() {
		int itemCount = tree.getItemCount();
		int workplacesOffset = getWorkplacesOffset(tree);
		for (int i = workplacesOffset; i < itemCount; i++) {
			TreeItem workplaceItem = tree.getItem(i);
			boolean inViewport = elementInViewport(workplaceItem.getElement());
			boolean opened = workplaceItem.getState();
			try {
				workplaceItem.setState(false); // close workplace
			}catch ( Exception e ) {
			}
			removeEmployeeItems(workplaceItem);
			if ( workplaceItem.getChildCount() == 0 ) {
				workplaceItem.addItem(new SafeHtmlBuilder().appendEscaped("fake").toSafeHtml());
			}
			if (inViewport & opened) {
				workplaceItem.setState(true);
			}
		}
	}

	private void changeVisibleWorkplaces() {
		int itemCount = tree.getItemCount();
		int workplacesOffset = getWorkplacesOffset(tree);
		for (int j = workplacesOffset; j < itemCount; j++) {
			TreeItem workplaceItem = tree.getItem(j);
			Workplace workplace = (Workplace) workplaceItem.getUserObject();
			workplaceItem.setVisible(isWorkPlaceVisible(workplace));
		}

	}

	private boolean isWorkPlaceVisible(Workplace workplace) {
		return inactive || (workplace.isActive() && workplace.getDate().compareTo(getFromDate()) >= 0 );
	}

	private int getWorkplacesOffset(Tree tree) {
		int itemCount = tree.getItemCount();

		for (int i = 0; i < itemCount; i++) {
			TreeItem childItem = tree.getItem(i);
			Object userObject = childItem.getUserObject();
			if (userObject instanceof Workplace)
				return i;
		}

		return itemCount;
	}

//	private int getWorkplacesOffset(TreeItem enterpriseItem) {
//		int itemCount = enterpriseItem.getChildCount();
//
//		for (int i = 0; i < itemCount; i++) {
//			TreeItem childItem = enterpriseItem.getChild(i);
//			Object userObject = childItem.getUserObject();
//			if (userObject instanceof Workplace)
//				return i;
//		}
//
//		return itemCount;
//	}

	private int getEmployeesOffset(TreeItem workplaceItem) {

		for (int i = 0; i < workplaceItem.getChildCount(); i++) {
			TreeItem childItem = workplaceItem.getChild(i);
			Object userObject = childItem.getUserObject();
			if (userObject instanceof EmployeeDraftObject)
				return i;
			if (userObject instanceof SalaryDraftObject)
				return i;
		}

		return workplaceItem.getChildCount();

	}

	/**
	 * Removes employees. Note that we assume that employees start at position
	 * 1, second child and extend until last one.
	 * 
	 * @param workplaceItem
	 */
	private void removeEmployeeItems(TreeItem workplaceItem) {
		int childCount = workplaceItem.getChildCount();
		int employeesOffset = getEmployeesOffset(workplaceItem);
		for (int i = childCount - 1; i >= employeesOffset; i--) {
			workplaceItem.getChild(i).remove();
		}
	}

	private void showEndDate(boolean endDate) {
		int itemCount = tree.getItemCount();
		int workplacesOffset = getWorkplacesOffset(tree);
		for (int i = workplacesOffset; i < itemCount; i++) {
			TreeItem workplaceItem = tree.getItem(i);
			int workplaceItems = workplaceItem.getChildCount();
			int employeesOffset = getEmployeesOffset(workplaceItem);
			for (int j = employeesOffset; j < workplaceItems; j++) {
				TreeItem employeeItem = workplaceItem.getChild(j);
				Employee employee = ((EmployeeDraftObject) employeeItem.getUserObject()).getEmployee();

				String fullName = employee.getFullname();
				StringBuffer text = new StringBuffer(fullName);
				if (endDate && (employee.getEndDate() != null)) {
					text.append(" (");
					text.append(END_DATE_FORMAT.format(employee.getEndDate()));
					text.append(")");
				}
				boolean current = isActive(employee);
				employeeItem
						.setHTML(materialIconItemHTML(current ? "person" : "person_off", text.toString()));

			}
		}
	}

	private boolean setCurrentsVisible(boolean currents) {
		return currents;
	}

	private TreeItem getSalaryDraftItem(final int employeeId) {
		TreeItem treeItem = getTreeItem(tree, new Predicate<TreeItem>() {
			@Override
			public boolean test(TreeItem t) {
				Object object = t.getUserObject();
				return (object instanceof SalaryDraftObject)
						&& (((SalaryDraftObject) object).getEmployee().getId() == employeeId);
			}
		});

		return treeItem;
	}

	private void selectEmployeeItem(final TreeItem workplaceItem, final int start, final Predicate<TreeItem> predicate,
			Date startDate, Date endDate, final boolean fireEvents) {
		loadEmployees(workplaceItem, getEmployeeLimit(), new AsyncCallback<List<Employee>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<Employee> result) {
				if (result.size() == 0)
					return;

				TreeItem draftItem = getTreeItem(workplaceItem, predicate, start);
				if (draftItem == null) {
					selectEmployeeItem(workplaceItem, start, predicate, startDate, endDate, fireEvents);
				} else {
					Object object = draftItem.getUserObject();
					
					SalaryDraft salaryDraft = ((SalaryDraftObject) object).getSalaryDraft();
					salaryDraft.setStartDate(startDate);
					salaryDraft.setEndDate(endDate);
					salaryDraft.setIssueDate(endDate);
					
					tree.setSelectedItem(draftItem, fireEvents);
					tree.ensureSelectedItemVisible();
					draftItem.getElement().scrollIntoView();
				}

			}
		});

	}

	private TreeItem getWorkplacetItem(final int workplaceId) {

		return getTreeItem(tree, new Predicate<TreeItem>() {
			@Override
			public boolean test(TreeItem t) {
				
				Object object = t.getUserObject();
				
				if ( !(object instanceof Workplace) )
					return false;
				
				Workplace workplace = (Workplace) object;
				int itemId = (int) workplace.getId();
				return (workplaceId == itemId );
			}
		});
	}

	private TreeItem getEmployeeItem(final int employeeId) {
		return getTreeItem(tree, new Predicate<TreeItem>() {
			@Override
			public boolean test(TreeItem t) {
				Object object = t.getUserObject();
				
				if ( !(object instanceof EmployeeDraftObject) )
					return false;	
				
				EmployeeDraftObject employeeDraftObject = (EmployeeDraftObject) object;
				int itemId = (int) employeeDraftObject.getEmployee().getId() ;				
				return (employeeId == itemId );
			}
		});
	}

	private TreeItem getActivityItem(final int activityId) {
		return getTreeItem(tree, new Predicate<TreeItem>() {
			@Override
			public boolean test(TreeItem t) {
				Object object = t.getUserObject();
				return (object instanceof Activity) && (((Activity) object).getId() == activityId);
			}
		});
	}

	private TreeItem getEnterpriseItem(final int enterpriseId) {
		return getTreeItem(tree, new Predicate<TreeItem>() {
			@Override
			public boolean test(TreeItem t) {
				Object object = t.getUserObject();
				return (object instanceof Enterprise) && (((Enterprise) object).getId() == enterpriseId);
			}
		});
	}

	private static TreeItem getTreeItem(Tree root, Predicate<TreeItem> predicate) {
		TreeItem treeItem = null;

		for (int i = 0; treeItem == null && i < root.getItemCount(); i++)
			treeItem = getTreeItem(root.getItem(i), predicate, 0);

		return treeItem;

	}

	private static TreeItem getTreeItem(TreeItem root, Predicate<TreeItem> predicate, int start) {
		if (predicate.test(root))
			return root;
		TreeItem treeItem = null;

		for (int i = start; treeItem == null && i < root.getChildCount(); i++)
			treeItem = getTreeItem(root.getChild(i), predicate, 0);

		return treeItem;
	}

	private static boolean isActive(Employee employee) {
		Date firsDayOfMonth = DateUtils.getFirstDayOfMonth();
		return DateUtils.isAfterOrEquals(employee.getEndDate(), firsDayOfMonth);

	}

//	private static ITDataObject getITDataObject(TreeItem workplaceItem) {
//		return (ITDataObject) workplaceItem.getChild(WORKPLACE_PARTSIT_INDEX).getUserObject();
//	}
	
	private static String getId(Enterprise enterprise) {
		return normalize(enterprise.getName());
	}
	
	private static String getId(Workplace workplace) {
		return normalize(workplace.getDescription());
	}
	
	private static String getId(Activity activity) {
		return normalize(activity.getDescription());
	}
	
	private static String getId(Employee employee) {
		return normalize(employee.getFullname());
	}
	
	private static String normalize(String str){
		return str
		.toLowerCase()
		.replace('\u00E1', 'a')
		.replace('\u00E9', 'e')
		.replace('\u00ED', 'i')
		.replace('\u00F3', 'o')
		.replace('\u00FA', 'u')
		.replace('\u00F1', 'n')
		.replace('\u00FC', 'u')
		.replaceAll("\\s+", "_")
		;
		
	}
	
	@Override
	public void onLoad(LoadEvent event) {

	}

//	@Override
//	public void onNewButtonClick(ClickEvent event) {
//
//	}
//
//	@Override
//	public void onPasteButtonClick(ClickEvent event) {
//		Object object = tree.getSelectedItem().getUserObject();
//
//		if (object instanceof Workplace)
//			onEmployeePaste((Workplace) object);
//	}
//
//	@Override
//	public void onCopyButtonClick(ClickEvent event) {
//
//		Object object = tree.getSelectedItem().getUserObject();
//
//		if (object instanceof EmployeeDraftObject)
//			onEmployeeCopy(((EmployeeDraftObject) object).getEmployee());
//	}
//
//	@Override
//	public void onDraftButtonClick(ClickEvent event) {
//		Object object = tree.getSelectedItem().getUserObject();
//		if (object instanceof EmployeeDraftObject)
//			onSuprPressed(((EmployeeDraftObject) object).getEmployee());
//	}

	@Override
	public void onCollapseAllButtonClick(ClickEvent event) {
//		collapse();
	}
	
	@Override
	public void onKeyUpSearchTextBox(KeyUpEvent event) {
		//filter(toolbar.getSearchTextBox().getValue());
		searchTimer.schedule(1000);
	}
	
	// ------------------------------------------------------------------------
	
	public void search(String pattern) {
		select(pattern);
	}
	
	// ------------------------------------------------------------------------
	
	
	public void getEnterpriseCost(Enterprise enterprise, Consumer<CostDocuments> consumer) {
		consumer.accept(getUserObject(enterprise, EnterpriseCostDocuments.class));
	}
	
	public void getEnterpriseStatistics(Enterprise enterprise, Consumer<Statistics> consumer) {
		consumer.accept(getUserObject(enterprise, EnterpriseStatistics.class));
	}
	
	public void getEnterpriseIT(Enterprise enterprise, Consumer<Void> consumer) {
		consumer.accept(getUserObject(enterprise, null));
	}

	public void getEnterpriseSalary(Enterprise enterprise, Consumer<EnterpriseSalaryObject> consumer) {
		consumer.accept(getUserObject(enterprise, EnterpriseSalaryObject.class));
	}

	public void getWorkplaceCost(Workplace workplace, Consumer<CostDocuments> consumer) {
		CostDocuments costDocuments = getUserObject(workplace, WorkplaceCostDocuments.class);
		if ( costDocuments != null ) {
			consumer.accept(getUserObject(workplace, CostDocuments.class));
		} else {
			getServiceWorkplaceCost(workplace, aCostDocuments -> {
				consumer.accept(aCostDocuments );
				TreeItem workplaceItem = getTreeItem(workplace);
				TreeItem costsItem = workplaceItem.getChild(WORKPLACE_COSTS_INDEX);
				costsItem.setUserObject(aCostDocuments );
			});
		}
	}

	public void getWorkplaceStatistics(Workplace workplace, Consumer<Statistics> consumer) {
		Statistics statistics = getUserObject(workplace, WorkplaceStatistics.class);
		if ( statistics != null ) {
			consumer.accept(statistics);
		} else {
			getServiceWorkplaceStatistics(workplace, aStatistics -> {
				consumer.accept(aStatistics);
				TreeItem workplaceItem = getTreeItem(workplace);
				TreeItem statisticsItem = workplaceItem.getChild(WORKPLACE_STATISTICS_INDEX);
				statisticsItem.setUserObject(aStatistics);
				
			});
		}
	}
	
	public void getWorkplaceCalendar(Workplace workplace, Consumer<CalendarDraftObjectData> consumer) {
		consumer.accept(getUserObject(workplace, CalendarDraftObjectData.class));
	}

	public void getWorkplaceSalary(Workplace workplace, Consumer<WorkplaceSalaryObject> consumer) {
		consumer.accept(getUserObject(workplace, WorkplaceSalaryObject.class));
	}
	
	public void getWorkplaceIT(Workplace workplace, Consumer<Void> consumer) {
		consumer.accept(getUserObject(workplace, null));
	}

	public void getWorkplaceEvents(Workplace workplace, Consumer<EventsDraftObject> consumer) {
		consumer.accept(getUserObject(workplace, EventsDraftObject.class));
	}
	
	public void getEmployeeCalendar(SalaryDraftObject salaryDraft, Consumer<EmployeeCalendarDraftObject> consumer) {
		consumer.accept(getUserObject(salaryDraft, EmployeeCalendarDraftObject.class));
	}
	
	public void getEmployeeSSBonus(SalaryDraftObject salaryDraft, Consumer<ContractBonusObject> consumer) {
		consumer.accept(getUserObject(salaryDraft, ContractBonusObject.class));
	}

//	public void getEmployeeSalaryDraft(SalaryDraftObject salaryDraft, Consumer<SalaryDraftObject> consumer) {
//		consumer.accept(getUserObject(salaryDraft, SalaryDraftObject.class));
//	}

	public void getEmployeeEvents(SalaryDraftObject salaryDraft, Consumer<EmployeeEventsDraftObject> consumer) {
		consumer.accept(getUserObject(salaryDraft, EmployeeEventsDraftObject.class));
	}
	
	public void getEmployeeSalary(SalaryDraftObject employee, Consumer<EmployeeSalaryObject> consumer) {
		consumer.accept(getUserObject(employee, EmployeeSalaryObject.class));
	}
	
	public void getEmployeeDraft(SalaryDraftObject salaryDraft, Consumer<EmployeeDraftObject> consumer) {
		consumer.accept(getUserObject(salaryDraft, EmployeeDraftObject.class));
	}
	

	public void getEmployeeSalaryDraft(Object userObject, Consumer<SalaryDraftObject> consumer) {
		TreeItem treeItem = getTreeItem(userObject);
		TreeItem parentItem = treeItem.getParentItem();
		consumer.accept((SalaryDraftObject) parentItem.getUserObject());
	}
	// ------------------------------------------------------------------------

	
	private void loadWorkplace(TreeItem workplaceItem) {
		int employeesOffset = getEmployeesOffset(workplaceItem);
		for ( int i = employeesOffset; i < workplaceItem.getChildCount(); i++ ) {
			return;
		}
		onWorkplaceOpen(workplaceItem);
	}

	private void filter( String pattern ) {
		
		filterEnterprise( pattern, tree, workplaceItem -> {
		});

//		for ( int i = 0; i < tree.getItemCount(); i++ ) {
//			TreeItem enterpriseItem = tree.getItem(i);	
//
//			filterEnterprise( pattern, enterpriseItem, workplaceItem -> {
//				enterpriseItem.setState(true, false); // open
//			});
//		}
//		
	}

	private void filterEnterprise( String pattern, Tree tree  , Consumer<TreeItem> found) {
		int workplacesOffset = getWorkplacesOffset(tree);
		for ( int i = workplacesOffset; i < tree.getItemCount(); i++ ) {			
			TreeItem workplaceItem = tree.getItem(i);	

			workplaceItem.setVisible(false);	// hides
			workplaceItem.setState(false, false);// close

			loadAndfilterWorkplace( pattern, workplaceItem, employeeItem -> {
				workplaceItem.setVisible(true);	// display
				workplaceItem.setState(true, false); 	// open	
				found.accept( workplaceItem );
			});
		}
	}

	private void loadAndfilterWorkplace( String pattern, TreeItem workplaceItem, Consumer<TreeItem> found ) {
		onWorkplaceOpen(workplaceItem, Integer.MAX_VALUE, () -> filterWorkplace(pattern, workplaceItem, found) );
	}
	
	
	private void filterWorkplace( String pattern, TreeItem workplaceItem, Consumer<TreeItem> found ) {
		int employeesOffset = getEmployeesOffset(workplaceItem);
		for ( int i = employeesOffset; i < workplaceItem.getChildCount(); i++ ) {
			TreeItem employeeItem = workplaceItem.getChild(i);
			
			Employee employee = null;
			try {
				employee = ((EmployeeDraftObject) employeeItem.getUserObject()).getEmployee();
			} catch (Exception e ) {
				employee = ((SalaryDraftObject) employeeItem.getUserObject()).getEmployee();
			}
			
			boolean visible  = 
			AonStringUtils.isBlank(pattern)
			|| AonStringUtils.containsIgnoreCase(employee.getFullname(), pattern)
			|| AonStringUtils.containsIgnoreCase(employee.getDocument(), pattern)
			|| AonStringUtils.containsIgnoreCase(employee.getSocialSecurity(), pattern)
			;
			
			employeeItem.setVisible(visible);
			if ( visible ) {
				found.accept(employeeItem);
			}
		}
	}
	
	
	private void select( String pattern ) {
		
		if ( tree.getItemCount() > 0 ) {
			TreeItem enterpriseItem = tree.getItem(0);	
			selectEmployee( pattern, 0, workplaceItem -> {
				enterpriseItem.setState(true); // open
			});
		}
	}
	
	
	private void selectEmployee( String pattern, int index, Consumer<TreeItem> found) {
		int workplacesOffset = getWorkplacesOffset(tree);
		int i = workplacesOffset + index ; 
		log("selectEmployee(" + pattern +"," + index +") " 
		+ "[" + i +"," + tree.getItemCount() + "]");
		if ( i >= tree.getItemCount() )
			return;
		
		TreeItem workplaceItem = tree.getItem(i);	
		
		loadAndFindemployee( pattern, 
		workplaceItem, 
		employeeItem -> {
			workplaceItem.setVisible(true);	// display
			workplaceItem.setState(true); 	// open	
			
			employeeItem.setState(true); // open
			tree.setSelectedItem(employeeItem, true);
			
			found.accept( workplaceItem );
		},
		() ->selectEmployee(pattern, index+1, found)
		);
		
	}

	private void loadAndFindemployee( String pattern, TreeItem workplaceItem, Consumer<TreeItem> found, Runnable lost) {
		onWorkplaceOpen(
		workplaceItem, 
		Integer.MAX_VALUE, 
		() -> { 
			
			TreeItem employeeItem = findEmployee(pattern, workplaceItem );
			if ( employeeItem != null )
				found.accept(employeeItem);
			else 
				lost.run();
		}
		);
	}
	
	private TreeItem findEmployee( String pattern, TreeItem workplaceItem) {
		int employeesOffset = getEmployeesOffset(workplaceItem);
		for ( int i = employeesOffset; i < workplaceItem.getChildCount(); i++ ) {
			TreeItem employeeItem = workplaceItem.getChild(i);
			Object userObject = employeeItem.getUserObject();
			
			Employee employee = null;
			if ( userObject instanceof EmployeeDraftObject )
				employee = ((EmployeeDraftObject) userObject).getEmployee();
			else if ( userObject instanceof SalaryDraftObject )
				employee = ((SalaryDraftObject) userObject).getEmployee();
			else 
				continue;
			
			boolean found  = 
			AonStringUtils.isBlank(pattern)
			|| AonStringUtils.containsIgnoreCase(employee.getFullname(), pattern)
			|| AonStringUtils.containsIgnoreCase(employee.getDocument(), pattern)
			|| AonStringUtils.containsIgnoreCase(employee.getSocialSecurity(), pattern)
			;
			
			if ( found )
				return employeeItem;
		}
		
		return null;
		
		
	}
	
	private <T> T  getUserObject(TreeItem treeItem, Class<T> clazz) {
		for (int i = 0; i < treeItem.getChildCount(); i++) {
			TreeItem child = treeItem.getChild(i);
			Object userObject = child.getUserObject();
			if ( userObject != null && userObject.getClass() == clazz )
				return (T) userObject;
		}
		return null;
	}
	
	private <T> T getUserObject(Enterprise enterprise, Class<T> clazz) {
		for (int i = 0; i < tree.getItemCount(); i++) {
			if ( enterprise == tree.getItem(i).getUserObject() )
				return getUserObject(tree.getItem(i), clazz);
		}
		return null;
	}

	private <T> T getUserObject(EmployeeDraftObject employee, Class<T> clazz) {
		TreeItem employeeTreeItem = getTreeItem(employee);
		return getUserObject(employeeTreeItem, clazz);
	}
	
	private <T> T getUserObject(SalaryDraftObject salary, Class<T> clazz) {
		TreeItem employeeTreeItem = getTreeItem(salary);
		return getUserObject(employeeTreeItem, clazz);
	}

	private <T> T getUserObject(Workplace workplace, Class<T> clazz) {
		TreeItem workplaceTreeItem = getTreeItem(workplace);
		return getUserObject(workplaceTreeItem, clazz);
	}

	private TreeItem getTreeItem( Object userObject  ) {
		for (int i = 0; i < tree.getItemCount(); i++) {
			TreeItem treeItem = tree.getItem(i);
			if ( treeItem.getUserObject() == userObject ) { 
				return treeItem;
			}
		}
		
		for (int i = 0; i < tree.getItemCount(); i++) {
			TreeItem treeItem = getTreeItem(tree.getItem(i), userObject);
			if ( treeItem != null ) {
				return treeItem;
			}
		}
		return null;
	}

	private TreeItem getTreeItem( TreeItem treeItem, Object userObject  ) {
		for (int i = 0; i < treeItem.getChildCount(); i++) {
			TreeItem child = treeItem.getChild(i);
			if ( child.getUserObject() == userObject ) { 
				return child;
			}
		}
		
		for (int i = 0; i < treeItem.getChildCount(); i++) {
			TreeItem child = getTreeItem(treeItem.getChild(i), userObject);
			if ( child != null  ) { 
				return child;
			}
		}

		return null;
	}
	
	private static native void log (String message ) /*-{
		console.log(message);
	}-*/;
	
	private void createEmployeesToolbar() {
		showMenuButton = new AonToolbarButton("Ocultar", AON.CSS.aonIconMenuCollapse() );
		showMenuButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(employeeTreeShowed) {
					showMenuButton.setTitle("Mostrar");
					showMenuButton.removeStyleName(AON.CSS.aonIconMenuCollapse());
					showMenuButton.addStyleName(AON.CSS.aonIconMenu());
					employeeTreeCollapsed = true;
					onCollapseEmployees();
				} else {
					showMenuButton.setTitle("Ocultar");
					showMenuButton.removeStyleName(AON.CSS.aonIconMenu());
					showMenuButton.addStyleName(AON.CSS.aonIconMenuCollapse());
					onShowEmployees(false);
				}
				
				employeeTreeShowed = !employeeTreeShowed;
				
			}
		});
		
		employeesToolbar.add(showMenuButton);
		
		Label title = new Label("Integral de N\u00f3minas");
		title.addStyleName(style.title());
		employeesToolbar.add(title);
		
		dynamicEmployees.addDomHandler(new MouseOutHandler() {
			  @Override
			  public void onMouseOut(MouseOutEvent event) {
				  if(employeeTreeCollapsed) {
					  showMenuButton.setTitle("Mostrar");
						showMenuButton.removeStyleName(AON.CSS.aonIconMenuCollapse());
						showMenuButton.addStyleName(AON.CSS.aonIconMenu());
						removeStyleName(style.staticEmployees());
						scrollPanel.setHeight("100%");
						onCollapseEmployees();
				  }
					  
			  }
			}, MouseOutEvent.getType());
	}

	public void showEmployees() {
		employeesDeck.showWidget(0);
	}
	
	private void showStaticEmployees() {
		employeesDeck.showWidget(1);
	}

	public void createStaticEmployees() {
		staticEmployees.clear();
		
		AonToolbarButton menuBtn = new AonToolbarButton("Mostrar", AON.CSS.aonIconMenu());
		menuBtn.addClickHandler(e -> {
			employeeTreeCollapsed = false;
			removeStyleName(style.staticEmployees());
			showMenuButton.click();
		});
		staticEmployees.add(menuBtn);
		
		showStaticEmployees();
	}
	
	private static String capitalize(String str) {
		if ( AonStringUtils.isBlank(str) )
			return str;
		
		boolean capitalizeNext = true;
		StringBuilder builder = new StringBuilder(str.length());
		
		for ( int i = 0; i < str.length(); i++ ) {
			char ch = str.charAt(i);
			if ( Character.isWhitespace(ch) 
				|| ch == ',' || ch == '.') {
				builder.append(ch);
				capitalizeNext = true;
			} else if ( capitalizeNext ) {
				builder.append(Character.toUpperCase(ch));
				capitalizeNext = false;
			} else {
				builder.append(Character.toLowerCase(ch));
			}
		}
		return builder.toString();
	}
	
	
	
}
