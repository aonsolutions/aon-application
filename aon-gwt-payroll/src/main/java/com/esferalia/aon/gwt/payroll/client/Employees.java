package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
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
		void onMinimize();
		void onMaximize();
	}
	
	interface Binder extends UiBinder<Widget, Employees> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField Tree tree;
	@UiField Button viewButton;
	@UiField Button minimizeButton;
	@UiField Button maximizeButton;
	
	private Images images;
	private EmployeesServiceAsync employeesService;

	private DetailPanel detailPanel;

	private JSF jsf;
	private Documents documents;
	
	private List<Listener> listeners ;
	

	public Employees() {

		jsf = new JSF();
		documents = new Documents();
		images = GWT.create(Images.class);

		listeners = new LinkedList<Employees.Listener>();

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		employeesService = GWT.create(EmployeesService.class);
		
		
		initWidget(binder.createAndBindUi(this));


		tree.addOpenHandler(this);
		tree.addSelectionHandler(this);
		
		initViewBUtton();
		
		minimizeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onMinimize();
			}
		});
		maximizeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onMaximize();
			}
		});
		
		employeesService.getEnterprise(this);
	}

	public void setDetailPanel(DetailPanel employeeDetail) {
		this.detailPanel = employeeDetail;
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
				String fullName = employee.getFullname();
				// fullName = StringUtils.capitalizeFully(fullName, DELIMITERS);
				
				
				boolean active = isActive(employee); 
				
				TreeItem employeeItem = addImageItem(workplaceItem, fullName,
						0 /* Not show number of childs */, active ? images.employee() : images.oldemployee());

				employeeItem.setUserObject(employee);

				addImageItem(employeeItem, "Nominas", 0, images.salaries());


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
						ISpinnable<IDocument> documents = new SalaryDocuments(
								salaries, employeesService);
						salariesItem.setUserObject(documents);
					}
				});
	}

	private void onEnterpriseSelected(Enterprise enterprise) {
		jsf.setUrl(GWT.getHostPageBaseURL()
				+ "/com/esferalia/aon/gwt/payroll/facelet/enterprise/enterprise.jsf");
		detailPanel.setWidget(jsf);
	}

	private void onWorkplaceSelected(Workplace workplace) {
		jsf.setUrl(GWT.getHostPageBaseURL()
				+ "/com/esferalia/aon/gwt/payroll/facelet/enterprise/workplace.jsf"
				+ "?controller=payrollWorkPlace&payrollWorkPlace_id="
				+ workplace.getId());
		detailPanel.setWidget(jsf);
	}

	private void onEmployeeSelected(Employee employee) {
		jsf.setUrl(GWT.getHostPageBaseURL()
				+ "/com/esferalia/aon/gwt/payroll/facelet/enterprise/contract.jsf"
				+ "?controller=contract&contract_id=" + employee.getId()
				+ "&controller=person&person_id=" + employee.getPerson());
		detailPanel.setWidget(jsf);
	}

	private void onDocumentsSelected(ISpinnable<IDocument> docs) {
		detailPanel.setWidget(documents);
		documents.setDocuments(docs);
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
	
	protected void onMinimize() {
		for (Listener listener : listeners) {
			listener.onMinimize();
		}
	}

	protected void onMaximize() {
		for (Listener listener : listeners) {
			listener.onMaximize();
		}
	}
	
	private void initViewBUtton() {
		viewButton.addClickHandler(new ClickHandler() {
			
			private PopupPanel popup = 
					new PopupPanel();
			
			
			private boolean oldVisible = true; 
			private boolean newVisible = true; 
			
			private MenuItem oldMenuItem;
			private MenuItem newMenuItem;
			
			{
				MenuBar menuBar = new MenuBar(true);
				
				oldMenuItem = new MenuItem("Antiguos Empleados", 
						new Command() {
					@Override
					public void execute() {
						setOldEmployeesVisible(oldVisible = !oldVisible);
						oldMenuItem.setStyleName("aon-MenuItemCheckYes", oldVisible);
						popup.hide();
					}
				});
				oldMenuItem.setStyleName("aon-MenuItemCheckYes", oldVisible);
				menuBar.addItem(oldMenuItem);
				
				newMenuItem = new MenuItem("Empleados Actuales", 
						new Command() {
					@Override
					public void execute() {
						setNewEmployeesVisible(newVisible = !newVisible);
						newMenuItem.setStyleName("aon-MenuItemCheckYes", newVisible);
						popup.hide();
					}
				});
				newMenuItem.setStyleName("aon-MenuItemCheckYes", newVisible);

				menuBar.addItem(newMenuItem);

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

	private void setOldEmployeesVisible(boolean visible) {
		for ( int i = 0; i < tree.getItemCount(); i++ ){
			setOldEmployeesVisible(tree.getItem(i), visible);
		}
	}
	
	private void setNewEmployeesVisible(boolean visible) {
		for ( int i = 0; i < tree.getItemCount(); i++ ){
			setNewEmployeesVisible(tree.getItem(i), visible);
		}
	}

	private void setOldEmployeesVisible( TreeItem treeItem, boolean visible ){
		
		Object userObject = treeItem.getUserObject();
		if ( userObject instanceof Employee ) {
			if ( !isActive(((Employee) userObject)) ) {
				treeItem.setVisible(visible);
			}
		}
		
		for ( int i = 0; i < treeItem.getChildCount(); i++ ){
			setOldEmployeesVisible(treeItem.getChild(i), visible);
		}
	}

	private void setNewEmployeesVisible( TreeItem treeItem, boolean visible ){
		
		Object userObject = treeItem.getUserObject();
		if ( userObject instanceof Employee ) {
			if ( isActive(((Employee) userObject)) ) {
				treeItem.setVisible(visible);
			}
		}
		
		for ( int i = 0; i < treeItem.getChildCount(); i++ ){
			setNewEmployeesVisible(treeItem.getChild(i), visible);
		}
	}
	
	private static boolean isActive(Employee employee) {
		Date firsDayOfMonth = DateUtils.getFirstDayOfMonth();
		return DateUtils.isAfterOrEquals(employee.getEndDate(), firsDayOfMonth);
		
	}
}
