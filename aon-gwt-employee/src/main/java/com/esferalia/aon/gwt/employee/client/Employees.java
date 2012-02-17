package com.esferalia.aon.gwt.employee.client;

import java.util.List;

import com.esferalia.aon.gwt.employee.shared.Cost;
import com.esferalia.aon.gwt.employee.shared.Employee;
import com.esferalia.aon.gwt.employee.shared.Enterprise;
import com.esferalia.aon.gwt.employee.shared.Salary;
import com.esferalia.aon.gwt.employee.shared.Workplace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class Employees extends Composite implements AsyncCallback<Enterprise>,
		OpenHandler<TreeItem>, SelectionHandler<TreeItem> {

	/**
	 * Specifies the images that will be bundled for this Composite and specify
	 * that tree's images should also be included in the same bundle.
	 */
	public interface Images extends ClientBundle, Tree.Resources {
		ImageResource draft();

		ImageResource enterprise();

		ImageResource workplace();

		ImageResource salaries();

		ImageResource employee();

		@Source("noimage.png")
		ImageResource treeLeaf();
	}

	private Tree tree;
	private Images images;
	private EmployeesServiceAsync employeesService;

	private EmployeeDetail employeeDetail;

	public Employees() {

		images = GWT.create(Images.class);
		tree = new Tree(images);

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		employeesService = GWT.create(EmployeesService.class);
		initWidget(tree);


		tree.addOpenHandler(this);
		tree.addSelectionHandler(this);

		employeesService.getEnterprise(this);


	}

	public void setEmployeeDetail(EmployeeDetail employeeDetail) {
		this.employeeDetail = employeeDetail;
	}

	@Override
	public void onFailure(Throwable caught) {
		// TODO Auto-generated method stub
		Window.alert(caught.getLocalizedMessage());
	}

	@Override
	public void onSuccess(Enterprise enterprise) {

		List<Workplace> workplaces = enterprise.getWorkplaces();

		final TreeItem enterpriseItem = new TreeItem(imageItemHTML(
				images.enterprise(), enterprise.getName(), workplaces.size()));

		List<Cost> enterpriseCosts = enterprise.getCosts();
		IReportsModel<IReport> reports = 
				new CostReportsModel(enterpriseCosts);
		enterpriseItem.setUserObject(reports);

		tree.addItem(enterpriseItem);

		for (Workplace workplace : workplaces) {

			List<Employee> employees = workplace.getEmployees();

			String description = workplace.getDescription();

			TreeItem workplaceItem = addImageItem(enterpriseItem, description,
					employees.size(), images.workplace());

			List<Cost> workplaceCosts = workplace.getCosts();
			IReportsModel<IReport> workplaceReports = 
					new CostReportsModel(workplaceCosts);
			workplaceItem.setUserObject(workplaceReports);

			for (Employee employee : employees) {
				String fullName = employee.getFullname();
				// fullName = StringUtils.capitalizeFully(fullName, DELIMITERS);
				TreeItem employeeItem = addImageItem(workplaceItem, fullName,
						0 /* Not show number of childs */, images.employee());

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
		if (userObject instanceof IReportsModel<?>) {
			onReportsSelected((IReportsModel<IReport>) userObject);
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
						IReportsModel<IReport> reports = new SalaryReportsModel(
								salaries);
						salariesItem.setUserObject(reports);
					}
				});
	}

	private void onReportsSelected(IReportsModel<IReport> reports) {
		employeeDetail.getSalaryReceipt().setReports(reports);
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

	private class SalaryReportsModel extends AbstractReportsModel<IReport>
			implements IReport {

		private List<Salary> salaries;

		public SalaryReportsModel(List<Salary> salaries) {
			this.salaries = salaries;
			first();
		}

		@Override
		public int size() {
			return salaries.size();
		}

		@Override
		public IReport current() {
			return this;
		}
		
		@Override
		public void print() {
			Salary salary = salaries.get(currentIndex());
			String printURL = URL.encode(GWT.getHostPageBaseURL() + "salary/" + salary.getId() + ".pdf");
			Window.open(printURL, "_blank", null);
		}

		@Override
		public void getAsHTML(int zoom, AsyncCallback<String> callback) {
			Salary salary = salaries.get(currentIndex());
			employeesService.getSalaryReceiptHTML(salary, zoom, callback);
		}
	}

	private class CostReportsModel extends AbstractReportsModel<IReport>
			implements IReport {

		private List<Cost> costs;

		public CostReportsModel(List<Cost> costs) {
			this.costs = costs;
			first();
		}

		@Override
		public int size() {
			return costs.size();
		}

		@Override
		public IReport current() {
			return this;
		}
		
		@Override
		public void print() {
			Cost cost = costs.get(currentIndex());
			String printURL = URL.encode(GWT.getHostPageBaseURL() + "cost/" 
					+ cost.getMonth()  
					+ "_" + cost.getYear()  
					+ "_" + cost.getEnterpriseId() 
					+ "_" + cost.getWorkplaceId() 
					+  ".pdf");
			Window.open(printURL, "_blank", null);
		}

		@Override
		public void getAsHTML(int zoom, AsyncCallback<String> callback) {
			Cost cost = costs.get(currentIndex());
			employeesService.getCostReceiptHTML(cost, zoom, callback);
		}
	}
}
