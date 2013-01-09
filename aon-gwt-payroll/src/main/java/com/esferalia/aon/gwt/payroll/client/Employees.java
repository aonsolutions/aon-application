package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class Employees extends ResizeComposite implements AsyncCallback<Enterprise>,
		OpenHandler<TreeItem>, SelectionHandler<TreeItem> {

	interface Listener {
		void onEmployeeSelected(Employee employee);
		void onEnterpriseSelected(Enterprise enterprise);
		void onActivitySelected(Activity activity);
		void onWorkplaceSelected(Workplace workplace);
		void onCostsSelected(CostDocuments docs);
		void onSalariesSelected(SalaryDocuments docs);
		void onDocumentsSelected(ISpinnable<IDocument> docs);
		void onSalaryDratSelected(SalaryDraftDocument salaryDraftDocument);
		
	}
	
	interface Binder extends UiBinder<Widget, Employees> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField Tree tree;
	@UiField Button viewButton;
	@UiField Button collapseAllButton;
	
	private Images images;
	private List<Listener> listeners ;
	private EmployeesServiceAsync employeesService;
	
	
	private boolean formers = true; 
	private boolean currents = true; 
	private boolean extended = false;
	

	public Employees() {

		images = GWT.create(Images.class);

		listeners = new LinkedList<Employees.Listener>();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		employeesService = GWT.create(EmployeesService.class);
		
		
		initWidget(binder.createAndBindUi(this));


		tree.addOpenHandler(this);
		tree.addSelectionHandler(this);
		
		initViewButton();
		
		collapseAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				collapse();
			}
		});
		
		
		
		employeesService.getEnterprise(this);
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
				images.enterprise(), enterprise.getName(), workplaces.size()));

		enterpriseItem.setUserObject(enterprise);
		tree.addItem(enterpriseItem);

		List<Cost> enterpriseCosts = enterprise.getCosts();
		ISpinnable<IDocument> documents = new CostDocuments(enterpriseCosts, employeesService);
		TreeItem enterpriseCostsItem = addImageItem(enterpriseItem, "Costos",
				enterpriseCosts.size(), images.costs());
		enterpriseCostsItem.setUserObject(documents);
		
		if ( extended ) {
			List<Activity> activities = enterprise.getActivities();
			for (Activity activity : activities) {
				String description = activity.getDescription();
				TreeItem activityItem = addImageItem(enterpriseItem, description,
						0, images.ine());
				activityItem.setUserObject(activity);			
			}
		}
		

		for (Workplace workplace : workplaces) {

			List<Employee> employees = workplace.getEmployees();

			String description = workplace.getDescription();

			TreeItem workplaceItem = addImageItem(enterpriseItem, description,
					employees.size(), images.workplace());
			workplaceItem.setUserObject(workplace);

			List<Cost> workplaceCosts = workplace.getCosts();
			ISpinnable<IDocument> workplaceReports = new CostDocuments(
					workplaceCosts, employeesService);

			TreeItem workplaceCostsItem = addImageItem(workplaceItem, "Costos",
					enterpriseCosts.size(), images.costs());
			workplaceCostsItem.setUserObject(workplaceReports);


			for (Employee employee : employees) {
				boolean active = isActive(employee);
				
				String fullName = employee.getFullname();
				// fullName = StringUtils.capitalizeFully(fullName, DELIMITERS);
				
				
				TreeItem employeeItem = addImageItem(workplaceItem, fullName,
						0 /* Not show number of childs */, active ? images.employee() : images.oldemployee());

				employeeItem.setUserObject(employee);
				
				addImageItem(employeeItem, "Nominas", 0, images.salaries());
				
				if ( extended ) {
					TreeItem salaryDraftItem = addImageItem(employeeItem, "Borrador", 0, images.draft() );
					SalaryDraft salaryDraft = new SalaryDraft();
					salaryDraft.setEmployee(employee);
					
					// TODO : This must not be here... and it's wrong.
					// TODO : It doesn't care about employee start and end dates.
					Date startDate = DateUtils.getFirstDayOfMonth();
					Date endDate = DateUtils.getLastDayOfMonth();
					Date issueDate = DateUtils.getLastDayOfMonth();
					
					salaryDraft.setStartDate(startDate);
					salaryDraft.setEndDate(endDate);
					salaryDraft.setIssueDate(issueDate);
					
					SalaryDraftDocument salaryDraftDocument = new SalaryDraftDocument(salaryDraft, employeesService);
					salaryDraftItem.setUserObject(salaryDraftDocument);
				
					//TreeItem dataItem = addImageItem(employeeItem, "Datos Económicos", 0, images.data() );

				}
				
				employeeItem.setVisible(active ? currents : formers );

			}
		}

		enterpriseItem.setState(true, true);
		tree.setSelectedItem(enterpriseItem, true);

	}

	// From OpenHandler<TreeItem>
	@Override
	public void onOpen(OpenEvent<TreeItem> event) {
		TreeItem item = event.getTarget();
		Object userObject = item.getUserObject();
		if (userObject instanceof Employee) {
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
			return;
		}
		if (userObject instanceof Workplace) {
			onWorkplaceSelected((Workplace) userObject);
			return;
		}
		if (userObject instanceof Employee) {
			onEmployeeSelected((Employee) userObject);
			return;
		}
		if (userObject instanceof SalaryDraftDocument) {
			onSalaryDraftSelected((SalaryDraftDocument) userObject);
			return;
		}
		if (userObject instanceof Activity) {
			onActivitySelected((Activity) userObject);
			return;
		}
		if (userObject instanceof CostDocuments) {
			onCostsSelected((CostDocuments) userObject);
			return;
		}
		if (userObject instanceof SalaryDocuments) {
			onSalariesSelected((SalaryDocuments) userObject);
			return;
		}
		if (userObject instanceof ISpinnable<?>) {
			onDocumentsSelected((ISpinnable<IDocument>) userObject);
			return;
		}

	}

	private void onEmployeeOpen(TreeItem employeeItem) {

		final TreeItem salariesItem = employeeItem.getChild(0);
		Object salariesObject = salariesItem.getUserObject();
		if (salariesObject != null) {
			return;
		}

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
						if (salaries.size() > 0) {
							salariesItem.setHTML(imageItemHTML(
									images.salaries(), "Nominas",
									salaries.size()));
						}
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

	private void onSalaryDraftSelected(SalaryDraftDocument salaryDraftDocument) {
		for (Listener listener : listeners) {
			listener.onSalaryDratSelected(salaryDraftDocument);
		}
	}

	private void onActivitySelected(Activity activity) {
		for (Listener listener : listeners) {
			listener.onActivitySelected(activity);
		}
	}

	/**
	 * A helper method to simplify adding tree items that have attached images.
	 * {@link #addImageItem(TreeItem, String, childs, ImageResource) code}
	 * 
	 */
	private TreeItem addImageItem(TreeItem root, String title, int childs,
			ImageResource imageProto) {
		TreeItem item = new TreeItem(imageItemHTML(imageProto, title, childs));
		root.addItem(item);
		return item;
	}

	/**
	 * Generates HTML for a tree item with an attached icon.
	 */
	private String imageItemHTML(ImageResource imageProto, String title,
			int childs) {
		return AbstractImagePrototype.create(imageProto).getHTML() + " "
				+ title + (childs > 0 ? " (" + childs + ")" : "");
	}
	
	private void initViewButton() {
		viewButton.addClickHandler(new ClickHandler() {
			
			private PopupPanel popup = 
					new PopupPanel();
			
			
			private MenuItem formerMenuItem;
			private MenuItem currentMenuItem;
			
			{
				MenuBar menuBar = new MenuBar(true);
				
				formerMenuItem = new MenuItem("Antiguos Empleados", 
						new Command() {
					@Override
					public void execute() {
						setFormerEmployeesVisible(formers = !formers);
						formerMenuItem.setStyleName("aon-MenuItemCheckYes", formers);
						popup.hide();
					}
				});
				formerMenuItem.setStyleName("aon-MenuItemCheckYes", formers);
				menuBar.addItem(formerMenuItem);
				
				currentMenuItem = new MenuItem("Empleados Actuales", 
						new Command() {
					@Override
					public void execute() {
						setCurrentEmployeesVisible(currents = !currents);
						currentMenuItem.setStyleName("aon-MenuItemCheckYes", currents);
						popup.hide();
					}
				});
				currentMenuItem.setStyleName("aon-MenuItemCheckYes", currents);

				menuBar.addItem(currentMenuItem);

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

	private void setFormerEmployeesVisible(boolean visible) {
		for ( int i = 0; i < tree.getItemCount(); i++ ){
			setFormerEmployeesVisible(tree.getItem(i), visible);
		}
	}
	
	private void setCurrentEmployeesVisible(boolean visible) {
		for ( int i = 0; i < tree.getItemCount(); i++ ){
			setCurrentEmployeesVisible(tree.getItem(i), visible);
		}
	}

	private int setFormerEmployeesVisible( TreeItem treeItem, boolean visible ){
		
		Object userObject = treeItem.getUserObject();
		if ( userObject instanceof Employee ) {
			if ( !isActive(((Employee) userObject)) ) {
				treeItem.setVisible(visible);
			}
			return treeItem.isVisible() ? 1 : 0;
		}
		
		int visibles = 0;
		for ( int i = 0; i < treeItem.getChildCount(); i++ ){
			visibles += setFormerEmployeesVisible(treeItem.getChild(i), visible);
		}

		if ( userObject instanceof Workplace ) {
			
			treeItem.setHTML(imageItemHTML(
					images.workplace(), 
					((Workplace)userObject).getDescription(),
					visibles));
		}
		
		return visibles;
	
	}

	private int setCurrentEmployeesVisible( TreeItem treeItem, boolean visible ){
		
		Object userObject = treeItem.getUserObject();
		if ( userObject instanceof Employee ) {
			if ( isActive(((Employee) userObject)) ) {
				treeItem.setVisible(visible);
			}
			return treeItem.isVisible() ? 1 : 0;
		}
		
		int visibles = 0;
		for ( int i = 0; i < treeItem.getChildCount(); i++ ){
			visibles += setCurrentEmployeesVisible(treeItem.getChild(i), visible);
		}

		if ( userObject instanceof Workplace ) {
			
			treeItem.setHTML(imageItemHTML(
					images.workplace(), 
					((Workplace)userObject).getDescription(),
					visibles));
		}
		
		return visibles;
	}
	
	private void collapse() {
		for ( int i = 0; i < tree.getItemCount(); i++ ){
			collapse(tree.getItem(i));
		}
	}
	
	private void collapse(TreeItem treeItem ) {
		for ( int i = 0; i < treeItem.getChildCount(); i++ ){
			TreeItem child = treeItem.getChild(i);
			collapse(child);
		}
		treeItem.setState(false);
	}
	
	private static boolean isActive(Employee employee) {
		Date firsDayOfMonth = DateUtils.getFirstDayOfMonth();
		return DateUtils.isAfterOrEquals(employee.getEndDate(), firsDayOfMonth);
		
	}
}
