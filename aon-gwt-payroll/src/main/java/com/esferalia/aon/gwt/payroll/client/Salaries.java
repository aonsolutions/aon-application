package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class Salaries extends Composite implements AsyncCallback<Enterprise>,
		OpenHandler<TreeItem>, SelectionHandler<TreeItem> {

	private static final DateTimeFormat MONTH_FORMAT = DateTimeFormat
			.getFormat("MMMM 'del' yyyy");

	private Tree tree;
	private Images images;
	private EmployeesServiceAsync employeesService;

	private DetailPanel detailPanel;

	private JSF jsf;
	private Documents documents;

	public Salaries() {

		jsf = new JSF();
		documents = new Documents();

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

	public void setDetailPanel(DetailPanel employeeDetail) {
		this.detailPanel = employeeDetail;
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
		ISpinnable<IDocument> documents = new CostDocuments(enterpriseCosts, employeesService);
		TreeItem enterpriseCostsItem = addImageItem(enterpriseItem, "Costos",
				enterpriseCosts.size(), images.costs());
		enterpriseCostsItem.setUserObject(documents);

		for (Workplace workplace : workplaces) {

			String description = workplace.getDescription();

			TreeItem workplaceItem = addImageItem(enterpriseItem, description,
					0, images.workplace());
			workplaceItem.setUserObject(workplace);

			List<Cost> workplaceCosts = workplace.getCosts();
			ISpinnable<IDocument> workplaceReports = new CostDocuments(
					workplaceCosts, employeesService);

			TreeItem workplaceCostsItem = addImageItem(workplaceItem, "Costos",
					enterpriseCosts.size(), images.costs());
			workplaceCostsItem.setUserObject(workplaceReports);

			for (Cost cost : workplaceCosts) {
				Date date = DateUtils.getDate(cost.getMonth(), cost.getYear());
				String title = StringUtils
						.capitalize(MONTH_FORMAT.format(date));

				TreeItem salariesItem = addImageItem(workplaceItem, title, 0,
						images.calendar());
				salariesItem.setUserObject(new SalaryCostDocuments(cost));
								
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
		if (userObject instanceof ISpinnable<?>) {
			onDocumentsSelected((ISpinnable<IDocument>) userObject);
			return;
		}

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

	private class SalaryCostDocuments extends AbstractSpinnable<IDocument>
			implements IDocument {

		private Cost cost;

		public SalaryCostDocuments(Cost cost) {
			this.cost = cost;
			first();
		}

		

		@Override
		public int size() {
			return 1;
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
			String printURL = URL.encode(GWT.getModuleBaseURL() + "salary/"
					+ cost.getMonth() + "_" + cost.getYear() + "_"
					+ cost.getEnterpriseId() + "_" + cost.getWorkplaceId()
					+ "." + format);
			Window.open(printURL, "_blank", null);
		}

		@Override
		public void getAsHTML(int zoom, AsyncCallback<String> callback) {
			employeesService.getSalaryReceiptHTML(cost, zoom, callback);
		}

		@Override
		public String[] getSupportedFormats() {
			return new String[] {};
		}
	}

}
