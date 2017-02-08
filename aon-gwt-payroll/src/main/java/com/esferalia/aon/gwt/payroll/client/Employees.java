package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.FilterDialog;
import com.esferalia.aon.gwt.common.client.widget.OptionsToolbar;
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
import com.esferalia.aon.gwt.payroll.shared.Category;
import com.esferalia.aon.gwt.payroll.shared.CategoryDraft;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Predicate;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class Employees extends ResizeComposite implements OpenHandler<TreeItem>, SelectionHandler<TreeItem>,
		ScrollHandler, ContextMenuHandler, KeyDownHandler, LoadHandler, OptionsToolbar.Listener {

	private static final int MIN_EMPLOYEE_LIMIT = 10;

	interface Listener {

		void onLoadAvaiableEmployees(Map<String, String> map);

		void onEmployeeSelected(Employee employee);

		void onEnterpriseSelected(Enterprise enterprise);

		void onActivitySelected(Activity activity);

		void onWorkplaceSelected(Workplace workplace);

		void onCostsSelected(CostDocuments docs);

		void onCalendarSelected(CalendarDraftObjectData calendar);

		void onIrpfsSelected(IrpfDocuments docs);

		void onReportsSelected(ReportsObject reports);

		void onStatisticsSelected(Statistics stats);

		void onITDataSelected(ITDataObject dataObject);

		void onSalariesSelected(SalaryDocuments docs);

		void onSalariesSelected(SalariesDocuments docs);

		void onDocumentsSelected(ISpinnable<IDocument> docs);

		void onSalaryDraftSelected(SalaryDraftObject salaryDraftDocument);

		void onSalaryPreviewSelected(SalaryPreviewDocument salaryPreviewDocument);

		void onEventsDraftSelected(EventsDraftObject eventsDraftObject);

		void onEmployeeEventsDraftSelected(EmployeeEventsDraftObject employeeEventsDraft);

		void onCategoryDraftSelected(CategoryDraftObject agreementDraftObject);

		void onAgreementDraftSelected(AgreementDraftObject agreementDraftObject);

		void onEmployeeContextMenu(Employee employee, ContextMenuEvent event);

		void onWorkplaceContextMenu(Workplace workplace, ContextMenuEvent event);

		void onEnterpriseContextMenu(Enterprise enterprise, ContextMenuEvent event);

		void onEmployeeCalendarSelected(EmployeeCalendarDraftObjectData calendar);

		void onEmployeeCopy(Employee employee);

		void onEmployeePaste(Workplace workplace);

		void onEmployeeCut(Employee employee);

		void onSuprPress(Employee employee);
	}

	interface Binder extends UiBinder<Widget, Employees> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static final int EMPLOYEE_SCROLL_GAP = 5;
	private static final int ENTERPRISE_COSTS_INDEX = 0;
	private static final int ENTERPRISE_SALARIES_INDEX = 1;
	private static final int ENTERPRISE_STATISTICS_INDEX = 2;
	private static final int ENTERPRISE_PARTSIT_INDEX = 3;
	private static final int ENTERPRISE_REPORTS_INDEX = 4;

	private static final int WORKPLACE_COSTS_INDEX = 0;
	private static final int WORKPLACE_SALARIES_INDEX = 1;
	private static final int WORKPLACE_CALENDAR_INDEX = 2;
	private static final int WORKPLACE_STATISTICS_INDEX = 3;
	private static final int WORKPLACE_PARTSIT_INDEX = 4;

	private static final int EMPLOYEE_SALARIES_INDEX = 0;
	private static final int EMPLOYEE_IRPFOUTCOMES_INDEX = 2; // TODO : It's not
	private static final int EMPLOYEE_CALENDAR_INDEX = 3;
	private static final int EMPLOYEE_EVENTS_INDEX = 4;

	private static final DateTimeFormat END_DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

	@UiField
	Tree tree;
	@UiField
	ScrollPanel scrollPanel;

	@UiField
	OptionsToolbar toolbar;

	private Images images;
	private List<Listener> listeners;
	private EmployeesServiceAsync employeesService;

	private boolean formers = true;
	private boolean endDate = true;
	private boolean extended = false;
	private boolean inactive = false;

	private Date fromDate = null;
	private String namePattern = null;

	private Storage storage;

	/**
	 * The last scroll position.
	 */
	private int lastScrollPos = 0;

	private List<TreeItem> employeeCentinels;


	public Employees() {
		this(false, true);
	}

	public Employees(boolean extended, boolean formers ) {

		this.formers = formers;
		this.extended = extended;
		
		images = GWT.create(Images.class);
		listeners = new LinkedList<Employees.Listener>();
		employeeCentinels = new LinkedList<TreeItem>();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		EmployeesServiceAsync employeesServiceRaw = GWT.create(EmployeesService.class);
		employeesService = new EmployeesServiceAsyncDecorator(employeesServiceRaw);
		
		initWidget(binder.createAndBindUi(this));


		tree.addOpenHandler(this);
		tree.addSelectionHandler(this);
		tree.addDomHandler(this, ContextMenuEvent.getType());
		tree.addKeyDownHandler(this);

		toolbar.addListener(this);
		// employeesService.getEnterprise(this);


		employeesService.getEnterprises(new AsyncCallback<Enterprise[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Enterprise[] enterprises) {
				for (Enterprise enterprise : enterprises)
					Employees.this.onEnterprise(enterprise);
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

		clearEnterprise(enterprise);

		List<Workplace> workplaces = enterprise.getWorkplaces();

		final TreeItem enterpriseItem = new TreeItem(imageItemHTML(images.enterprise(), enterprise.getName()));
		enterpriseItem.ensureDebugId(getId(enterprise));
		

		enterpriseItem.setUserObject(enterprise);
		tree.addItem(enterpriseItem);

		addImageItem(enterpriseItem, "Costes", images.costs());
		addImageItem(enterpriseItem, "N\u00F3minas", images.salaries());
		addImageItem(enterpriseItem, "Estad\u00EDsticas", images.statistics());

		if (Enterprise.isGPS(enterprise))
			addImageItem(enterpriseItem, "Informes", images.gps())
					.setUserObject(new ReportsObject(enterprise, employeesService));

		if (extended) {
			List<Activity> activities = enterprise.getActivities();
			for (Activity activity : activities) {
				String description = activity.getDescription();
				TreeItem activityItem = addImageItem(enterpriseItem, description, images.ine());
				activityItem.setUserObject(activity);
			}
		}

		TreeItem workplaceItem = null;

		for (Workplace workplace : workplaces) {
			workplaceItem = new TreeItem();
			enterpriseItem.addItem(workplaceItem);
			workplaceItem = loadWorkplace(enterprise, workplaceItem, workplace);
		}

		enterpriseItem.setState(true, true);
		tree.setSelectedItem(enterpriseItem, true); // Send event to show
													// enterprise data
		if (workplaces.size() == 1)
			workplaceItem.setState(true, true); // Send event to show employees

		scrollPanel.scrollToLeft();

		initViewButton(toolbar.getViewButton());
		

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
		if (userObject instanceof Employee) {
			onEmployeeOpen(item);
		}
		if (userObject instanceof Workplace) {
			onWorkplaceOpen(item);
		}
		if (userObject instanceof Enterprise) {
			onEnterpriseOpen(item);
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
		} else if (userObject instanceof Employee) {
			onEmployeeSelected((Employee) userObject);
		} else if (userObject instanceof SalaryDraftObject) {
			onSalaryDraftSelected((SalaryDraftObject) userObject);
		} else if (userObject instanceof SalaryPreviewDocument) {
			onSalaryPreviewSelected((SalaryPreviewDocument) userObject);
		} else if (userObject instanceof Activity) {
			onActivitySelected((Activity) userObject);
		}
		// I apologize about this. Inheritance it's like Kate Beckinsale. Due
		// 'SalariesDocuments' extends 'CostDocuments' its test must be first.
		// If not, onSalariesSelected(SalariesDocuments) method won't be called.
		else if (userObject instanceof SalariesDocuments) {
			// onSalariesSelected((SalariesDocuments) userObject);
			onSalariesDocumentsSelected(item);
		} else if (userObject instanceof CostDocuments) {
			// onCostsSelected((CostDocuments) userObject);
			onCostsDocumentsSelected(item);
		} else if (userObject instanceof ReportsObject) {
			onReportsSelected((ReportsObject) userObject);
		} else if (userObject instanceof Statistics) {
			onStatisticsSelected((Statistics) userObject);
		} else if (userObject instanceof ITDataObject) {
			onITDataSelected((ITDataObject) userObject);
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
		}else if (userObject instanceof EmployeeCalendarDraftObjectData) {
			onEmployeeCalendarDraftSelected((EmployeeCalendarDraftObjectData) userObject);
		} 
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int keyCode = event.getNativeKeyCode();
		Object object = tree.getSelectedItem().getUserObject();

		if ((event.isControlKeyDown() && keyCode == KeyCodes.KEY_C) && (object instanceof Employee)) {
			onEmployeeCopy((Employee) object);
		} else if (event.getNativeEvent().getCtrlKey() && keyCode == KeyCodes.KEY_V && object instanceof Workplace) {
			onEmployeePaste((Workplace) object);
		} else if (event.getNativeEvent().getCtrlKey() && keyCode == KeyCodes.KEY_X && object instanceof Employee) {
			onCtrlXPressed((Employee) object);

		} else if (keyCode == KeyCodes.KEY_DELETE && object instanceof Employee) {
			onSuprPressed((Employee) object);
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
		} else if (userObject instanceof Employee) {
			onEmployeeContextMenu((Employee) userObject, event);
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
			}
		});
	}

	public void refresh(final Workplace workplace) {

		final TreeItem workplaceItem = getWorkplacetItem(workplace.getId());
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
							if (workplace.getId().equals(newWorkplace.getId())) {
								boolean open = workplaceItem.getState();
								workplaceItem.removeItems();
								loadWorkplace(newEnterprise, workplaceItem, newWorkplace);
								if (open)
									onWorkplaceOpen(workplaceItem);

							}
						}

					}
				} // TODO: Only this workplace...

			}

		});
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

	public void selectSalaryDraft(final int employeeId, int workplaceId, boolean fireEvents) {

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
			tree.setSelectedItem(draftItem, fireEvents);
			tree.ensureSelectedItemVisible();
			draftItem.getElement().scrollIntoView();
		} else {
			selectEmployeeItem(workplaceItem, workplaceItem.getChildCount(), predicate, fireEvents);
		}
	}

	// ------------------------------------------------------------------------

	EmployeesServiceAsync getEmployeesService() {
		return employeesService;
	}

	OptionsToolbar getOptionsToolbar() {
		return toolbar;
	}

	// ------------------------------------------------------------------------

	protected Date getFromDate() {
		return formers ? (fromDate == null ? new Date(0) : fromDate) : DateUtils.getFirstDayOfMonth();
	}
	
	public String getNamePattern() {
		return namePattern;
	}

	// ------------------------------------------------------------------------

	private TreeItem loadWorkplace(Enterprise enterprise, final TreeItem workplaceItem, Workplace workplace) {
		String description = workplace.getDescription();

		workplaceItem.setHTML(imageItemHTML(images.workplace(), description));
		workplaceItem.setUserObject(workplace);
		workplaceItem.setVisible(isWorkPlaceVisible(workplace));
		workplaceItem.ensureDebugId(getId(workplace));

		addImageItem(workplaceItem, "Costes", images.costs());
		addImageItem(workplaceItem, "N\u00F3minas", images.salaries());
		addImageItem(workplaceItem, "Calendario", images.laboralCalendar())
				.setUserObject(new CalendarDraftObjectData(workplace.getId(), employeesService));
		addImageItem(workplaceItem, "Estad\u00EDsticas", images.statistics());
		addImageItem(workplaceItem, "Partes IT", images.itDatas())
				.setUserObject(new ITDataObject(workplace.getId(), employeesService));
		if (extended) {

			final TreeItem eventsItem = addImageItem(workplaceItem, "Incidencias", images.data());
			// final TreeItem eventsItem = new TreeItem();

			// --------------------------------------------------------------
			//

			Agreement agreement = workplace.getAgreement();

			final EventsDraftObject eventsDraftObject;

			if (Enterprise.isGPS(enterprise))
				eventsDraftObject = new EventsDraftObject(workplace.getId(),
						agreement != null ? agreement.getId() : null, employeesService,
						// @formatter:off
						new AbstractEventsDraftObject.EnumEventMetaData("DESEMPE\u00D1O",
								"DESEMPE\u00D1O", "Desempe\u00F1o por Trabajador y Jornada", "", new String[] { "4",
										"8", "10", "12", "L", "LT", "LR", "F", "FT", "FR", "V", "B", "P", "AI", "M" },
								DateField.DAY)
				// @formatter:on
				);
			else
				eventsDraftObject = new EventsDraftObject(workplace.getId(),
						agreement != null ? agreement.getId() : null, employeesService,
						new BooleanEventMetaData("DIAS_EFECTIVOS", DateField.DAY),
						new BooleanEventMetaData("DIAS_VACACIONES", DateField.DAY),
						// new BooleanEventMetaData("HUELGA", DAY),
						new DecimalEventMetaData("COEFICIENTE_ERE", DateField.DAY),
						new EventMetaData("OBSERVACIONES", DateField.MONTH));

			Date date = new Date();

			eventsDraftObject.setPeriod(DateUtils.getFirstDayOfWorkWeek(date), DateUtils.getLastDayOfWorkWeek(date),
					new EventsDraftObject.Callback() {

						@Override
						public void onSucces() {
							eventsItem.setUserObject(eventsDraftObject);
						}

						@Override
						public void onFailure(Throwable throwable) {
							eventsItem.setUserObject(eventsDraftObject);
						}

					});
		}

		Agreement agreement = workplace.getAgreement();

		if (extended && (agreement != null)) {

			final TreeItem agreementItem = addImageItem(workplaceItem, agreement.getDescription(), images.agreement());

			AgreementDraft agreementDraft = new AgreementDraft();
			agreementDraft.setId(agreement.getId());
			agreementDraft.setDomain(agreement.getDomain());
			agreementDraft.setDescription(agreement.getDescription());
			agreementDraft.setStartDate(DateUtils.getFirstDayOfMonth());
			agreementDraft.setEndDate(DateUtils.getLastDayOfMonth());
			final AgreementDraftObject agreementDraftObject = new AgreementDraftObject(enterprise.getDomain(),
					agreementDraft, employeesService);

			employeesService.getChanges(agreement, new AsyncCallback<SortedSet<Date>>() {
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
					agreementItem.setHTML(imageItemHTML(resource, agreementDraftObject.getDescription()));
				}
			});

		} // TODO: extended ? Yes I'm know , it's awful.
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
			final TreeItem salariesItem = enterpriseItem.getChild(ENTERPRISE_SALARIES_INDEX);

			employeesService.getEnterpriseCosts(enterprise.getId(), new AsyncCallback<List<Cost>>() {
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					Window.alert(caught.getLocalizedMessage());

				}

				@Override
				public void onSuccess(List<Cost> costs) {
					CostDocuments documents = new CostDocuments(costs, employeesService);
					costsItem.setUserObject(documents);

					SalariesDocuments salariesDocuments = new SalariesDocuments(costs, employeesService);
					salariesItem.setUserObject(salariesDocuments);
				}
			});
		} // end-if: Cost of enterprise have been already loaded.

		final TreeItem statisticsItem = enterpriseItem.getChild(ENTERPRISE_STATISTICS_INDEX);

		if (null == statisticsItem.getUserObject()) {

			employeesService.getEnterpriseStats(enterprise.getId(), new AsyncCallback<Statistics>() {

				@Override
				public void onSuccess(Statistics stats) {
					statisticsItem.setUserObject(stats);
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Apéndice de método generado automáticamente
					Window.alert(caught.getLocalizedMessage());
				}
			});
		}

		final TreeItem partsItItem = enterpriseItem.getChild(ENTERPRISE_PARTSIT_INDEX);

		if (null == partsItItem.getUserObject()) {

			employeesService.getEnterpriseITData(enterprise.getId(), new AsyncCallback<ITData>() {

				@Override
				public void onSuccess(ITData partsIt) {
					partsItItem.setUserObject(partsIt);
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Apéndice de método generado automáticamente
					Window.alert(caught.getLocalizedMessage());
				}
			});
		}
	}

	/*
	 * Workplace Item has been expanded +.
	 */
	private void onWorkplaceOpen(final TreeItem workplaceItem) {

		Workplace workplace = (Workplace) workplaceItem.getUserObject();

		final TreeItem costsItem = workplaceItem.getChild(WORKPLACE_COSTS_INDEX);

		final TreeItem salariesItem = workplaceItem.getChild(WORKPLACE_SALARIES_INDEX);

		if (costsItem.getUserObject() == null) {
			employeesService.getWorkplaceCosts(workplace.getId(), new AsyncCallback<List<Cost>>() {
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					Window.alert(caught.getLocalizedMessage());

				}

				@Override
				public void onSuccess(List<Cost> costs) {
					CostDocuments costDocuments = new CostDocuments(costs, employeesService);
					costsItem.setUserObject(costDocuments);

					SalariesDocuments salariesDocuments = new SalariesDocuments(costs, employeesService);
					salariesItem.setUserObject(salariesDocuments);
				}
			});
		} // end-if: Costs of this workplace haven't been loaded yet.

		final TreeItem statisticsItem = workplaceItem.getChild(WORKPLACE_STATISTICS_INDEX);

		if (null == statisticsItem.getUserObject()) {

			employeesService.getWorkplaceStats(workplace.getId(), new AsyncCallback<Statistics>() {

				@Override
				public void onSuccess(Statistics stats) {
					statisticsItem.setUserObject(stats);
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Apéndice de método generado automáticamente
					Window.alert(caught.getLocalizedMessage());
				}
			});
		}
		if (workplaceItem.getChildCount() > getEmployeesOffset(workplaceItem)) {
			return;
		} // end-if: Employees of this workplace already loaded .

		final int limit = getEmployeeLimit();

		employeesService.getEmployees(workplace.getId(), getFromDate(), namePattern, 0, limit,
				new AsyncCallback<List<Employee>>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getLocalizedMessage());

					}

					@Override
					public void onSuccess(List<Employee> employees) {
						loadEmployess(workplaceItem, employees, limit);
					}
				});

	}

	private void onEmployeeOpen(TreeItem employeeItem) {

		final TreeItem salariesItem = employeeItem.getChild(EMPLOYEE_SALARIES_INDEX);
		if (salariesItem.getUserObject() != null) {
			return;
		} // end-if: Salaries of this employee have been already loaded.

		Employee employee = (Employee) employeeItem.getUserObject();
		employeesService.getSalaries(employee, new AsyncCallback<List<Salary>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(List<Salary> salaries) {
				SalaryDocuments documents = new SalaryDocuments(salaries, employeesService);
				salariesItem.setUserObject(documents);
			}
		});
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

	private void onEmployeeSelected(Employee employee) {
		for (Listener listener : listeners) {
			listener.onEmployeeSelected(employee);
		}
	}

	private void onCostsSelected(CostDocuments docs) {
		for (Listener listener : listeners) {
			listener.onCostsSelected(docs);
		}
	}

	private void onSalariesSelected(SalariesDocuments docs) {
		for (Listener listener : listeners) {
			listener.onSalariesSelected(docs);
		}
	}

	private void onReportsSelected(ReportsObject reports) {
		for (Listener listener : listeners) {
			listener.onReportsSelected(reports);
		}
	}

	private void onCalendarSelected(CalendarDraftObjectData calendar) {
		for (Listener listener : listeners)
			listener.onCalendarSelected(calendar);
	}

	private void onStatisticsSelected(Statistics stats) {
		for (Listener listener : listeners) {
			listener.onStatisticsSelected(stats);
		}
	}

	private void onITDataSelected(ITDataObject dataObject) {
		for (Listener listener : listeners) {
			listener.onITDataSelected(dataObject);
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
				CostDocuments docs = new CostDocuments(costs, employeesService);
				costsItem.setUserObject(docs);

				costsItem.setUserObject(docs);
				for (Listener listener : listeners) {
					listener.onCostsSelected(docs);
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
				CostDocuments docs = new CostDocuments(costs, employeesService);
				costsItem.setUserObject(docs);

				costsItem.setUserObject(docs);
				for (Listener listener : listeners) {
					listener.onCostsSelected(docs);
				}
			}
		});
	}

	private void onSalaryDocumentsSelected(final TreeItem salariesItem) {
		TreeItem employeeItem = salariesItem.getParentItem();
		Employee employee = (Employee) employeeItem.getUserObject();
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

	public void addEmployee(TreeItem workplaceItem, Employees employee, int limit) {

		List<Employees> employees = new ArrayList<Employees>();
		employees.add(employee);

	}

	private void loadEmployess(TreeItem workplaceItem, List<Employee> employees, int limit) {

		int added = 0;

		for (Employee employee : employees) {
			if (employee.getId() < 0)
				continue;
			loadEmployee(workplaceItem, workplaceItem.getChildCount(), employee);
			added++;
		}

		if (added == limit) {
			int last = workplaceItem.getChildCount() - 1;
			TreeItem employeeCentinel = workplaceItem.getChild(last - (EMPLOYEE_SCROLL_GAP));
			employeeCentinels.add(employeeCentinel);
		} // end-if : If's very likely that exists more employees.

	}

	private void loadEmployee(TreeItem workplaceItem, int beforeIndex, Employee employee) {
		TreeItem employeeItem = new TreeItem();
		workplaceItem.insertItem(beforeIndex, employeeItem);
		loadEmployee(workplaceItem, employeeItem, employee);
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

		employeeItem.setHTML(imageItemHTML(current ? images.employee() : images.oldemployee(), text.toString()));
		employeeItem.setUserObject(employee);
		employeeItem.ensureDebugId(getId(employee));

		TreeItem salariestItem = addImageItem(employeeItem, "N\u00F3minas", images.salaries());
		salariestItem.ensureDebugId(getId(employee)+"-salaries");

		//#ifdef env.SNAPSHOT
		//#echo Employee's calendar only visible at SNAPSHOT version
		TreeItem calendarDraftItem = addImageItem(employeeItem, "Calendario", images.laboralCalendar());
		EmployeeCalendarDraftObjectData employeeCalendarDraftobjectData = new EmployeeCalendarDraftObjectData(employee.getId(), 
				employee.getStartDate(), employee.getEndDate(), employeesService);
		calendarDraftItem.setUserObject(employeeCalendarDraftobjectData);
		//#endif
		

		if (extended) {
			ITDataObject dataObject = getITDataObject(workplaceItem);

			Date salaryDate = DateUtils.before(DateUtils.after(new Date(), employee.getStartDate()),
					employee.getEndDate());
			Date startDate = DateUtils.getFirstDayOfMonth(salaryDate);
			Date endDate = DateUtils.getLastDayOfMonth(salaryDate);
			Date issueDate = endDate;

			TreeItem salaryDraftItem = addImageItem(employeeItem, "Borrador", images.draft());
			salaryDraftItem.ensureDebugId(getId(employee)+"-draft");

			SalaryDraft salaryDraft = new SalaryDraft();
			salaryDraft.setEmployee(employee);
			salaryDraft.setStartDate(startDate);
			salaryDraft.setEndDate(endDate);
			salaryDraft.setIssueDate(issueDate);
			salaryDraft.setType(Type.SALARY);
			
			//#ifdef env.SNAPSHOT
			//Pasar SalaryDraft al Calendario para la gestion de horas
			employeeCalendarDraftobjectData.setSalaryDraft(salaryDraft);
			//#endif

			SalaryDraftObject draftObject = new SalaryDraftObject(salaryDraft, dataObject, employeesService);
			salaryDraftItem.setUserObject(draftObject);

			// final TreeItem employeeEventsItem = addImageItem(employeeItem,
			// "Incidencias", images.data());
			final TreeItem employeeEventsItem = new TreeItem();
			employeeEventsItem.ensureDebugId(getId(employee)+"-events");

			final EmployeeEventsDraftObject employeeEventsDraftObject;

			employeeEventsDraftObject = new EmployeeEventsDraftObject(employee, employeesService,
					new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_TRABAJADOS", DateField.DAY),
					new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_EFECTIVOS", DateField.DAY),
					new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_ERE", DateField.DAY),
					new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_HUELGA", DateField.DAY),
					new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_AUSENCIA", DateField.DAY),
					new AbstractEventsDraftObject.DecimalEventMetaData("HORAS_TRABAJADAS", DateField.DAY),
					new AbstractEventsDraftObject.DecimalEventMetaData("HORAS_COMPLEMENTARIAS", DateField.DAY),
					new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_PECNORTA", DateField.DAY),
					new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_MANUTENCION", DateField.DAY),
					new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_PECNORTA_EXTRANJERO", DateField.DAY),
					new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_MANUTENCION_EXTRANJERO", DateField.DAY),
					new AbstractEventsDraftObject.DecimalEventMetaData("KMS", DateField.DAY),
					new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_VACACIONES", DateField.DAY),
					new AbstractEventsDraftObject.DecimalEventMetaData("JORNADAS_REALES", DateField.DAY),
					new AbstractEventsDraftObject.DecimalEventMetaData("HORAS_EXTRAS", DateField.DAY),
					new AbstractEventsDraftObject.DecimalEventMetaData("HORAS_EXTRAS_FZA", DateField.DAY));

			Date date = new Date();

			employeeEventsDraftObject.setPeriod(DateUtils.getFirstDayOfWorkWeek(date),
					DateUtils.getLastDayOfWorkWeek(date), new EmployeeEventsDraftObject.Callback() {

						@Override
						public void onSucces() {
							employeeEventsItem.setUserObject(employeeEventsDraftObject);
						}

						@Override
						public void onFailure(Throwable throwable) {
							employeeEventsItem.setUserObject(employeeEventsDraftObject);
						}
					});

			// A.E.T
			// addImageItem(employeeItem, "Regularizaciones", images.aet());

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

			final TreeItem categoryItem = addImageItem(employeeItem,
					category.getLevel() + ". " + category.getDescription(), images.agreement());
			categoryItem.ensureDebugId(getId(employee)+"-category");
			
			CategoryDraft categoryDraft = new CategoryDraft();
			categoryDraft.setId(agreement.getId());
			categoryDraft.setDomain(agreement.getDomain());
			categoryDraft.setLevelId(category.getLevelId());
			categoryDraft.setDescription(agreement.getDescription());
			categoryDraft.setStartDate(DateUtils.getFirstDayOfMonth());
			categoryDraft.setEndDate(DateUtils.getLastDayOfMonth());
			final CategoryDraftObject categoryDraftObject = new CategoryDraftObject(enterprise.getDomain(),
					categoryDraft, employeesService);
			// categoryItem.setUserObject(categoryDraftObject);

			employeesService.getChanges(agreement, new AsyncCallback<SortedSet<Date>>() {
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

	/**
	 * A helper method to simplify adding tree items that have attached images.
	 * {@link #addImageItem(TreeItem, String, childs, ImageResource) code}
	 * 
	 */
	private TreeItem addImageItem(TreeItem root, String title, ImageResource imageProto) {
		TreeItem item = new TreeItem(imageItemHTML(imageProto, title));
		root.addItem(item);
		return item;
	}

	private TreeItem insertImageItem(TreeItem root, int beforeIndex, String title, ImageResource imageProto) {
		TreeItem item = new TreeItem(imageItemHTML(imageProto, title));
		root.insertItem(beforeIndex, item);
		return item;
	}

	/**
	 * Generates HTML for a tree item with an attached icon.
	 */
	private SafeHtml imageItemHTML(ImageResource imageProto, String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		builder.append(' ');
		builder.appendEscaped(title);
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
				menuBar.addItem(endDateMenuItem);

				formerMenuItem = new MenuItem("Antiguos Empleados", new Command() {
					@Override
					public void execute() {
						formers = !formers;
						changeVisibleEmployees();
						formerMenuItem.setStyleName("aon-MenuItemCheckYes", formers);
						popup.hide();
					}
				});
				formerMenuItem.setStyleName("aon-MenuItemCheckYes", formers);
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

		TreeItem item = root.getChild(ENTERPRISE_COSTS_INDEX);

		int itemHeight = item.getOffsetHeight();
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

		TreeItem enterpriseItem = tree.getItem(0);
		int childCount = enterpriseItem.getChildCount();
		int workplacesOffset = getWorkplacesOffset(enterpriseItem);
		for (int i = workplacesOffset; i < childCount; i++) {
			TreeItem workplaceItem = enterpriseItem.getChild(i);
			boolean inViewport = elementInViewport(workplaceItem.getElement());
			boolean opened = workplaceItem.getState();
			workplaceItem.setState(false); // close workplace
			removeEmployeeItems(workplaceItem);
			if (inViewport & opened) {
				workplaceItem.setState(true);
			}
		}

	}

	private void changeVisibleWorkplaces() {
		TreeItem enterpriseItem = tree.getItem(0);
		int childCount = enterpriseItem.getChildCount();
		int workplacesOffset = getWorkplacesOffset(enterpriseItem);
		for (int j = workplacesOffset; j < childCount; j++) {
			TreeItem workplaceItem = enterpriseItem.getChild(j);
			Workplace workplace = (Workplace) workplaceItem.getUserObject();
			workplaceItem.setVisible(isWorkPlaceVisible(workplace));
		}

	}

	private boolean isWorkPlaceVisible(Workplace workplace) {
		return workplace.isActive() || inactive;
	}

	private int getWorkplacesOffset(TreeItem rootItem) {
		int itemCount = rootItem.getChildCount();

		for (int i = 0; i < itemCount; i++) {
			TreeItem childItem = rootItem.getChild(i);
			Object userObject = childItem.getUserObject();
			if (userObject instanceof Workplace)
				return i;
		}

		return itemCount;

		// return extended ? 3 : 2;
	}

	private int getEmployeesOffset(TreeItem workplaceItem) {

		for (int i = 0; i < workplaceItem.getChildCount(); i++) {
			TreeItem childItem = workplaceItem.getChild(i);
			Object userObject = childItem.getUserObject();
			if (userObject instanceof Employee)
				return i;
		}

		return workplaceItem.getChildCount();

		// return extended ? 7 : 5;
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

		TreeItem enterpriseItem = tree.getItem(0);
		int childCount = enterpriseItem.getChildCount();
		int workplacesOffset = getWorkplacesOffset(enterpriseItem);
		for (int i = workplacesOffset; i < childCount; i++) {
			TreeItem workplaceItem = enterpriseItem.getChild(i);
			int workplaceItems = workplaceItem.getChildCount();
			int employeesOffset = getEmployeesOffset(workplaceItem);
			for (int j = employeesOffset; j < workplaceItems; j++) {
				TreeItem employeeItem = workplaceItem.getChild(j);
				Employee employee = (Employee) employeeItem.getUserObject();

				String fullName = employee.getFullname();
				StringBuffer text = new StringBuffer(fullName);
				if (endDate && (employee.getEndDate() != null)) {
					text.append(" (");
					text.append(END_DATE_FORMAT.format(employee.getEndDate()));
					text.append(")");
				}
				boolean current = isActive(employee);
				employeeItem
						.setHTML(imageItemHTML(current ? images.employee() : images.oldemployee(), text.toString()));

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
			final boolean fireEvents) {
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
					selectEmployeeItem(workplaceItem, start, predicate, fireEvents);
				} else {
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
				return (object instanceof Workplace) && (((Workplace) object).getId() == workplaceId);
			}
		});
	}

	private TreeItem getEmployeeItem(final int employeeId) {
		return getTreeItem(tree, new Predicate<TreeItem>() {
			@Override
			public boolean test(TreeItem t) {
				Object object = t.getUserObject();
				return (object instanceof Employee) && (((Employee) object).getId() == employeeId);
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

	private static ITDataObject getITDataObject(TreeItem workplaceItem) {
		return (ITDataObject) workplaceItem.getChild(WORKPLACE_PARTSIT_INDEX).getUserObject();
	}
	
	private static String getId(Enterprise enterprise) {
		return normalize(enterprise.getName());
	}
	
	private static String getId(Workplace workplace) {
		return normalize(workplace.getDescription());
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

	@Override
	public void onNewButtonClick(ClickEvent event) {

	}

	@Override
	public void onPasteButtonClick(ClickEvent event) {
		Object object = tree.getSelectedItem().getUserObject();

		if (object instanceof Workplace)
			onEmployeePaste((Workplace) object);
	}

	@Override
	public void onCopyButtonClick(ClickEvent event) {

		Object object = tree.getSelectedItem().getUserObject();

		if (object instanceof Employee)
			onEmployeeCopy((Employee) object);
	}

	@Override
	public void onDraftButtonClick(ClickEvent event) {
		Object object = tree.getSelectedItem().getUserObject();
		if (object instanceof Employee)
			onSuprPressed((Employee) object);
	}

	@Override
	public void onCollapseAllButtonClick(ClickEvent event) {
		collapse();
	}
}
