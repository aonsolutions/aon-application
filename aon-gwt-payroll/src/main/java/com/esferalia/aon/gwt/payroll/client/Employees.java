package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.code.aon.ui.registry.controller.PersonController;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
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

		ImageResource data();

		ImageResource calendar();

		ImageResource enterprise();

		ImageResource workplace();

		ImageResource costs();

		ImageResource salaries();

		ImageResource employee();

		@Source("noimage.png")
		ImageResource treeLeaf();
	}

	private Tree tree;
	private Images images;
	private EmployeesServiceAsync employeesService;

	private EmployeeDetail employeeDetail;

	private JSF jsf;
	private Documents documents;
	private SalaryDraft salaryDraft;

	public Employees() {

		jsf = new JSF();
		documents = new Documents();
		salaryDraft = new SalaryDraft();

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

		enterpriseItem.setUserObject(enterprise);
		tree.addItem(enterpriseItem);

		List<Cost> enterpriseCosts = enterprise.getCosts();
		ISpinnable<IDocument> documents = new CostDocuments(enterpriseCosts);
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
					workplaceCosts);

			TreeItem workplaceCostsItem = addImageItem(workplaceItem, "Costos",
					enterpriseCosts.size(), images.costs());
			workplaceCostsItem.setUserObject(workplaceReports);

			TreeItem workplaceSalariesItem = addImageItem(workplaceItem,
					"Nominas", workplaceCosts.size(), images.salaries());
			workplaceSalariesItem.setUserObject(new SalaryCostDocuments(
					workplaceCosts));

			for (Employee employee : employees) {
				String fullName = employee.getFullname();
				// fullName = StringUtils.capitalizeFully(fullName, DELIMITERS);
				TreeItem employeeItem = addImageItem(workplaceItem, fullName,
						0 /* Not show number of childs */, images.employee());

				employeeItem.setUserObject(employee);

				addImageItem(employeeItem, "Nominas", 0, images.salaries());

				TreeItem salaryDraftItem = addImageItem(employeeItem,
						"Borrador", 0, images.draft());

				com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft = 
						new com.esferalia.aon.gwt.payroll.shared.SalaryDraft();
				salaryDraft.setEmployee(employee);
				salaryDraft.setType(Salary.Type.SALARY);

				salaryDraft.setStartDate(DateUtils.getFirstDayOfMonth());
				salaryDraft.setEndDate(DateUtils.getLastDayOfMonth());
				salaryDraft.setIssueDate(salaryDraft.getEndDate());

				salaryDraftItem.setUserObject(new SalaryDraftDocument(
						salaryDraft));

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
		if (userObject instanceof SalaryDraftDocument) {
			onSalaryDraftSelected((SalaryDraftDocument) userObject);
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
				+ "/com/esferalia/aon/gwt/payroll/facelet/employee/enterprise.jsf");
		employeeDetail.setWidget(jsf);
	}

	private void onWorkplaceSelected(Workplace workplace) {
		jsf.setUrl(GWT.getHostPageBaseURL()
				+ "/com/esferalia/aon/gwt/payroll/facelet/employee/workplace.jsf"
				+ "?controller=payrollWorkPlace&payrollWorkPlace_id="
				+ workplace.getId());
		employeeDetail.setWidget(jsf);
	}

	private void onEmployeeSelected(Employee employee) {
		jsf.setUrl(GWT.getHostPageBaseURL()
				+ "/com/esferalia/aon/gwt/payroll/facelet/employee/contract.jsf"
				+ "?controller=contract&contract_id=" + employee.getId()
				+ "&controller=person&person_id=" + employee.getPerson());
		employeeDetail.setWidget(jsf);
	}

	private void onDocumentsSelected(ISpinnable<IDocument> docs) {
		employeeDetail.setWidget(documents);
		documents.setDocuments(docs);
	}

	private void onSalaryDraftSelected(SalaryDraftDocument draft) {
		employeeDetail.setWidget(salaryDraft);
		salaryDraft.setSalaryDraft(draft);
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

	private class CostDocuments extends AbstractSpinnable<IDocument> implements
			IDocument {

		private List<Cost> costs;

		public CostDocuments(List<Cost> costs) {
			this.costs = costs;
			first();
		}

		@Override
		public int size() {
			return costs.size();
		}

		@Override
		public IDocument current() {
			return this;
		}

		@Override
		public void print() {
			download();
		}

		@Override
		public void download() {
			download("pdf");
		}

		@Override
		public void download(String format) {
			Cost cost = costs.get(currentIndex());
			String printURL = URL.encode(GWT.getModuleBaseURL() + "cost/"
					+ cost.getMonth() + "_" + cost.getYear() + "_"
					+ cost.getEnterpriseId() + "_" + cost.getWorkplaceId()
					+ "." + format);
			Window.open(printURL, "_blank", null);
		}

		@Override
		public void getAsHTML(int zoom, AsyncCallback<String> callback) {
			Cost cost = costs.get(currentIndex());
			employeesService.getCostReceiptHTML(cost, zoom, callback);
		}

		@Override
		public String[] getSupportedFormats() {
			return new String[] { "xls" };
		}
	}

	private class SalaryCostDocuments extends AbstractSpinnable<IDocument>
			implements IDocument {

		private List<Cost> costs;

		public SalaryCostDocuments(List<Cost> costs) {
			this.costs = costs;
			first();
		}

		@Override
		public int size() {
			return costs.size();
		}

		@Override
		public IDocument current() {
			return this;
		}

		@Override
		public void print() {
			download();
		}

		@Override
		public void download() {
			download("pdf");
		}

		@Override
		public void download(String format) {
			Cost cost = costs.get(currentIndex());
			String printURL = URL.encode(GWT.getModuleBaseURL() + "salary/"
					+ cost.getMonth() + "_" + cost.getYear() + "_"
					+ cost.getEnterpriseId() + "_" + cost.getWorkplaceId()
					+ "." + format);
			Window.open(printURL, "_blank", null);
		}

		@Override
		public void getAsHTML(int zoom, AsyncCallback<String> callback) {
			Cost cost = costs.get(currentIndex());
			employeesService.getSalaryReceiptHTML(cost, zoom, callback);
		}

		@Override
		public String[] getSupportedFormats() {
			return new String[] {};
		}
	}

	private class SalaryDraftDocument implements ISalaryDraft {

		private com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft;

		public SalaryDraftDocument(
				com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft) {
			this.salaryDraft = salaryDraft;
		}

		@Override
		public void print() {
			// TODO Auto-generated method stub

		}

		@Override
		public void download() {
			// TODO Auto-generated method stub

		}

		@Override
		public void download(String format) {
			// TODO Auto-generated method stub

		}

		@Override
		public String[] getSupportedFormats() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void getAsHTML(int zoom, AsyncCallback<String> callback) {
			employeesService.getSalaryDraftReceiptHTML(salaryDraft, zoom,
					callback);
		}
		
		@Override
		public com.esferalia.aon.gwt.payroll.shared.SalaryDraft getSalaryDraft() {
			return salaryDraft;
		}
	}
}
