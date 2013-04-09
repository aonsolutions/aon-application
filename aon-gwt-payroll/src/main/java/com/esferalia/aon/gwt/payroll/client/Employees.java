package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
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

public class Employees extends ResizeComposite implements
		AsyncCallback<Enterprise>, OpenHandler<TreeItem>,
		SelectionHandler<TreeItem>, ScrollHandler, ContextMenuHandler {

	interface Listener {
		void onEmployeeSelected(Employee employee);

		void onEnterpriseSelected(Enterprise enterprise);

		void onActivitySelected(Activity activity);

		void onWorkplaceSelected(Workplace workplace);

		void onCostsSelected(CostDocuments docs);

		void onSalariesSelected(SalaryDocuments docs);

		void onDocumentsSelected(ISpinnable<IDocument> docs);

		void onSalaryDraftSelected(SalaryDraftObject salaryDraftDocument);

		void onSalaryPreviewSelected(SalaryPreviewDocument salaryPreviewDocument);

		void onAgreementDraftSelected(AgreementDraftObject agreementDraftObject);
		
		void onEmployeeContextMenu(Employee employee, ContextMenuEvent event);

		void onWorkplaceContextMenu(Workplace workplace, ContextMenuEvent event);
		
		void onEnterpriseContextMenu(Enterprise enterprise, ContextMenuEvent event);
	}

	interface Binder extends UiBinder<Widget, Employees> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static final int EMPLOYEE_SCROLL_GAP = 5;
	private static final int ENTERPRISE_COSTS_INDEX = 0;
	private static final int WORKPLACE_COSTS_INDEX = 0;
	private static final int EMPLOYEE_SALARIES_INDEX = 0;

	private static final DateTimeFormat END_DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);

	@UiField
	Tree tree;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	Button viewButton;
	@UiField
	Button collapseAllButton;

	private Images images;
	private List<Listener> listeners;
	private EmployeesServiceAsync employeesService;

	private boolean formers = true;
	private boolean endDate = true;
	private boolean extended = false;

	private Date fromDate = null;
	private String namePattern = null;

	/**
	 * The last scroll position.
	 */
	private int lastScrollPos = 0;
	private List<TreeItem> employeeCentinels;

	public Employees() {

		images = GWT.create(Images.class);

		listeners = new LinkedList<Employees.Listener>();
		employeeCentinels = new LinkedList<TreeItem>();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		EmployeesServiceAsync employeesServiceRaw = GWT
				.create(EmployeesService.class);
		employeesService = new EmployeesServiceAsyncDecorator(
				employeesServiceRaw);

		initWidget(binder.createAndBindUi(this));

		tree.addOpenHandler(this);
		tree.addSelectionHandler(this);
		tree.addDomHandler(this, ContextMenuEvent.getType());

		collapseAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				collapse();
			}
		});

		employeesService.getEnterprise(this);

		scrollPanel.addScrollHandler(this);

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
	
	@Override
	public void onFailure(Throwable caught) {
		// TODO Auto-generated method stub
		Window.alert(caught.getLocalizedMessage());
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@Override
	public void onSuccess(Enterprise enterprise) {

		List<Workplace> workplaces = enterprise.getWorkplaces();

		final TreeItem enterpriseItem = new TreeItem(imageItemHTML(
				images.enterprise(), enterprise.getName()));

		enterpriseItem.setUserObject(enterprise);
		tree.addItem(enterpriseItem);

		addImageItem(enterpriseItem, "Costos", images.costs());

		if (extended) {
			List<Activity> activities = enterprise.getActivities();
			for (Activity activity : activities) {
				String description = activity.getDescription();
				TreeItem activityItem = addImageItem(enterpriseItem,
						description, images.ine());
				activityItem.setUserObject(activity);
			}
		}

		for (Workplace workplace : workplaces) {

			String description = workplace.getDescription();

			TreeItem workplaceItem = addImageItem(enterpriseItem, description,
					images.workplace());
			workplaceItem.setUserObject(workplace);

			addImageItem(workplaceItem, "Costos", images.costs());

			Agreement agreement = workplace.getAgreement();
			if (extended && (agreement != null)) {
				TreeItem agreementItem = addImageItem(workplaceItem,
						agreement.getDescription(), images.agreement());
				
				AgreementDraft agreementDraft = new AgreementDraft();
				agreementDraft.setId(agreement.getId());
				agreementDraft.setDescription(agreement.getDescription());
				agreementDraft.setStartDate(DateUtils.getFirstDayOfMonth());
				agreementDraft.setEndDate(DateUtils.getLastDayOfMonth());
				AgreementDraftObject agreementDraftObject = 
						new AgreementDraftObject(agreementDraft, employeesService);
				agreementItem.setUserObject(agreementDraftObject);
			} // TODO: extended ? Yes I'm know , it's awful.
		}

		enterpriseItem.setState(true, true);
		tree.setSelectedItem(enterpriseItem, true);

		scrollPanel.scrollToLeft();

		initViewButton();

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
		} else if (userObject instanceof CostDocuments) {
			onCostsSelected((CostDocuments) userObject);
		} else if (userObject instanceof SalaryDocuments) {
			onSalariesSelected((SalaryDocuments) userObject);
		} else if (userObject instanceof ISpinnable<?>) {
			onDocumentsSelected((ISpinnable<IDocument>) userObject);
		} else if (userObject instanceof AgreementDraftObject) {
			onAgreementDraftSelected((AgreementDraftObject) userObject);
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
				Workplace workplace = (Workplace) workplaceItem.getUserObject();

				int offset = workplaceItem.getChildCount() - 1;

				employeesService.getEmployees(workplace.getId(), getFromDate(),
						namePattern, offset, limit,
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

		return elTop < (windowTop + windowHeight)
				&& elLeft < (windowLeft + windowWidth)
				&& (elTop + elHeight) > windowTop
				&& (elLeft + elWidth) > windowLeft;

	}

	EmployeesServiceAsync getEmployeesService() {
		return employeesService;
	}


	private void onEnterpriseOpen(TreeItem enterpriseItem) {

		final TreeItem costsItem = enterpriseItem
				.getChild(ENTERPRISE_COSTS_INDEX);
		if (null != costsItem.getUserObject()) {
			return;
		} // end-if: Cost of enterprise have been already loaded.

		Enterprise enterprise = (Enterprise) enterpriseItem.getUserObject();

		employeesService.getEnterpriseCosts(enterprise.getId(),
				new AsyncCallback<List<Cost>>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getLocalizedMessage());

					}

					@Override
					public void onSuccess(List<Cost> costs) {
						CostDocuments documents = new CostDocuments(costs,
								employeesService);
						costsItem.setUserObject(documents);
					}
				});
	}

	private void onWorkplaceOpen(final TreeItem workplaceItem) {

		Workplace workplace = (Workplace) workplaceItem.getUserObject();

		final TreeItem costsItem = workplaceItem
				.getChild(WORKPLACE_COSTS_INDEX);

		if (costsItem.getUserObject() == null) {
			employeesService.getWorkplaceCosts(workplace.getId(),
					new AsyncCallback<List<Cost>>() {
						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							Window.alert(caught.getLocalizedMessage());

						}

						@Override
						public void onSuccess(List<Cost> costs) {
							CostDocuments documents = new CostDocuments(costs,
									employeesService);
							costsItem.setUserObject(documents);
						}
					});
		} // end-if: Costs of this workplace haven't been loaded yet.

		if (workplaceItem.getChildCount() > getEmployeesOffset()) {
			return;
		} // end-if: Employees of this workplace already loaded .

		final int limit = getEmployeeLimit();

		employeesService.getEmployees(workplace.getId(), getFromDate(),
				namePattern, 0, limit, new AsyncCallback<List<Employee>>() {
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

		final TreeItem salariesItem = employeeItem
				.getChild(EMPLOYEE_SALARIES_INDEX);
		if (salariesItem.getUserObject() != null) {
			return;
		} // end-if: Salaries of this employee have been already loaded.

		Employee employee = (Employee) employeeItem.getUserObject();

		employeesService.getSalaries(employee,
				new AsyncCallback<List<Salary>>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getLocalizedMessage());

					}

					@Override
					public void onSuccess(List<Salary> salaries) {
						SalaryDocuments documents = new SalaryDocuments(
								salaries, employeesService);
						salariesItem.setUserObject(documents);
					}
				});
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

	private void onSalariesSelected(SalaryDocuments docs) {
		for (Listener listener : listeners) {
			listener.onSalariesSelected(docs);
		}
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

	private void onSalaryPreviewSelected(
			SalaryPreviewDocument salaryPreviewDocument) {
		for (Listener listener : listeners) {
			listener.onSalaryPreviewSelected(salaryPreviewDocument);
		}
	}

	private void onActivitySelected(Activity activity) {
		for (Listener listener : listeners) {
			listener.onActivitySelected(activity);
		}
	}

	private void onWorkplaceContextMenu(Workplace workplace,
			ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onWorkplaceContextMenu(workplace, event);
		}
	}

	private void onEnterpiseContextMenu(Enterprise enterprise,
			ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onEnterpriseContextMenu(enterprise, event);
		}
	}

	private void onEmployeeContextMenu(Employee employee,
			ContextMenuEvent event) {
		for (Listener listener : listeners) {
			listener.onEmployeeContextMenu(employee, event);
		}
	}

	private void onAgreementDraftSelected(AgreementDraftObject agreementDraftObject) {
		for (Listener listener : listeners) {
			listener.onAgreementDraftSelected(agreementDraftObject);
		}
	}

	private void loadEmployess(TreeItem workplaceItem,
			List<Employee> employees, int limit) {

		int added = 0;

		for (Employee employee : employees) {

			boolean current = isActive(employee);

			String fullName = employee.getFullname();

			StringBuffer text = new StringBuffer(fullName);
			if (endDate && (employee.getEndDate() != null)) {
				text.append(" (");
				text.append(END_DATE_FORMAT.format(employee.getEndDate()));
				text.append(")");
			}

			TreeItem employeeItem = addImageItem(workplaceItem,
					text.toString(),
					current ? images.employee() : images.oldemployee());

			employeeItem.setUserObject(employee);

			addImageItem(employeeItem, "Nominas", images.salaries());

			if (extended) {
				TreeItem salaryPreviewItem = addImageItem(employeeItem,
						"Preliminar", images.preview());
				SalaryPreview salaryPreview = new SalaryPreview();
				salaryPreview.setEmployee(employee);

				Date salaryDate = DateUtils.before(
						DateUtils.after(new Date(), employee.getStartDate()),
						employee.getEndDate());

				Date startDate = DateUtils.getFirstDayOfMonth(salaryDate);
				Date endDate = DateUtils.getLastDayOfMonth(salaryDate);
				Date issueDate = endDate;

				salaryPreview.setStartDate(startDate);
				salaryPreview.setEndDate(endDate);
				salaryPreview.setIssueDate(issueDate);

				SalaryPreviewDocument salaryPreviewDocument = new SalaryPreviewDocument(
						salaryPreview, employeesService);
				salaryPreviewItem.setUserObject(salaryPreviewDocument);

				TreeItem salaryDraftItem = addImageItem(employeeItem,
						"Borrador", images.draft());

				SalaryDraft salaryDraft = new SalaryDraft();
				salaryDraft.setEmployee(employee);
				salaryDraft.setStartDate(startDate);
				salaryDraft.setEndDate(endDate);
				salaryDraft.setIssueDate(issueDate);
				SalaryDraftObject draftObject = new SalaryDraftObject(
						salaryDraft, employeesService);
				salaryDraftItem.setUserObject(draftObject);
			}

			added++;

		}

		if (added == limit) {
			int last = workplaceItem.getChildCount() - 1;
			TreeItem employeeCentinel = workplaceItem.getChild(last
					- (EMPLOYEE_SCROLL_GAP));
			employeeCentinels.add(employeeCentinel);
		} // end-if : If's very likely that exists more employees.

	}

	/**
	 * A helper method to simplify adding tree items that have attached images.
	 * {@link #addImageItem(TreeItem, String, childs, ImageResource) code}
	 * 
	 */
	private TreeItem addImageItem(TreeItem root, String title,
			ImageResource imageProto) {
		TreeItem item = new TreeItem(imageItemHTML(imageProto, title));
		root.addItem(item);
		return item;
	}

	/**
	 * Generates HTML for a tree item with an attached icon.
	 */
	private String imageItemHTML(ImageResource imageProto, String title) {
		return AbstractImagePrototype.create(imageProto).getHTML() + " "
				+ title;
	}

	private void initViewButton() {
		viewButton.addClickHandler(new ClickHandler() {

			private PopupPanel popup = new PopupPanel();

			private MenuItem formerMenuItem;
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
						endDateMenuItem.setStyleName("aon-MenuItemCheckYes",
								endDate);
						popup.hide();
					}
				});
				endDateMenuItem.setStyleName("aon-MenuItemCheckYes", endDate);
				menuBar.addItem(endDateMenuItem);

				formerMenuItem = new MenuItem("Antiguos Empleados",
						new Command() {
							@Override
							public void execute() {
								formers = !formers;
								changeVisibleEmployees();
								formerMenuItem.setStyleName(
										"aon-MenuItemCheckYes", formers);
								popup.hide();
							}
						});
				formerMenuItem.setStyleName("aon-MenuItemCheckYes", formers);
				menuBar.addItem(formerMenuItem);

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

							boolean nameChanged = !StringUtils
									.equalsIgnoreCase(namePattern,
											newNamePattern);

							boolean dateChanged = !DateUtils.equals(fromDate,
									newFromDate);

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
				int top = viewButton.getAbsoluteTop()
						+ viewButton.getOffsetHeight();
				popup.setPopupPosition(left, top);
				popup.show();
			}
		});

	}

	private Date getFromDate() {
		return formers ? (fromDate == null ? new Date(0) : fromDate)
				: DateUtils.getFirstDayOfMonth();
	}

	private String getNamePattern() {
		return namePattern;
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
		return visibleItems + 1;
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
		int workplacesOffset = getWorkplacesOffset();
		for (int i = workplacesOffset; i < childCount; i++) {
			TreeItem workplaceItem = enterpriseItem.getChild(i);
			boolean inViewport = elementInViewport(workplaceItem.getElement());
			boolean opened = workplaceItem.getState();
			workplaceItem.setState(false); // close workplace
			removeEmployeeItems(workplaceItem);
			removeEmployeeItems(workplaceItem);
			if (inViewport & opened) {
				workplaceItem.setState(true);
			}
		}

	}

	private int getWorkplacesOffset() {
		return extended ? 2 : 1;
	}

	/**
	 * Removes employees. Note that we assume that employees start at position
	 * 1, second child and extend until last one.
	 * 
	 * @param workplaceItem
	 */
	private void removeEmployeeItems(TreeItem workplaceItem) {
		int childCount = workplaceItem.getChildCount();
		int employeesOffset = getEmployeesOffset();
		for (int i = childCount - 1; i >= employeesOffset; i--) {
			workplaceItem.getChild(i).remove();
		}
	}

	private int getEmployeesOffset() {
		return 2;
	}

	private void showEndDate(boolean endDate) {

		TreeItem enterpriseItem = tree.getItem(0);
		int childCount = enterpriseItem.getChildCount();
		int workplacesOffset = getWorkplacesOffset();
		for (int i = workplacesOffset; i < childCount; i++) {
			TreeItem workplaceItem = enterpriseItem.getChild(i);
			int workplaceItems = workplaceItem.getChildCount();
			int employeesOffset = getEmployeesOffset();
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
				employeeItem.setHTML(imageItemHTML(current ? images.employee()
						: images.oldemployee(), text.toString()));

			}
		}
	}

	private boolean setCurrentsVisible(boolean currents) {
		return currents;
	}

	private static boolean isActive(Employee employee) {
		Date firsDayOfMonth = DateUtils.getFirstDayOfMonth();
		return DateUtils.isAfterOrEquals(employee.getEndDate(), firsDayOfMonth);

	}
}
