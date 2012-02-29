package com.esferalia.aon.gwt.employee.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.employee.shared.Salary;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class Salaries extends Composite {
	
	
	/**
	 * Specifies the images that will be bundled for this Composite and specify
	 * that tree's images should also be included in the same bundle.
	 */
	interface Images extends ClientBundle, Tree.Resources {

		ImageResource folder();

		ImageResource salaries();


		@Source("noimage.png")
		ImageResource treeLeaf();
	}

	private Tree 							tree;
	private Images 							images;
	private EmployeesServiceAsync 			employeesService;

	private EmployeeDetail 					employeeDetail;
	
	public Salaries() {
		// Create a remote service proxy to talk to the server-side Employees
		// service.
		employeesService = GWT.create(EmployeesService.class);
		

		images = GWT.create(Images.class);
		tree = new Tree(images);
		
		employeesService.getSalaries(null, new AsyncCallback<List<Salary>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(List<Salary> salaries) {
				Map<Salary.Type, TreeItem> types = 
						new HashMap<Salary.Type, TreeItem>();  
				for (Salary salary : salaries) {
					Salary.Type type = salary.getType();
					TreeItem typeItem = types.get(salary.getType());
					if ( typeItem == null) {
						String title = getTypeDescription(salary.getType());
						typeItem = new TreeItem(
								imageItemHTML(images.folder(), 
										title));
						tree.addItem(typeItem);
						types.put(type, typeItem);
					}
					String title = 
							DATE_TIME_FORMAT.format(salary.getChargeDate());
					TreeItem salaryItem = new TreeItem(
							imageItemHTML(images.salaries(), 
									title));
					typeItem.addItem(salaryItem);
					IReportsModel<IReport> model = 
							new SalaryReportsModel(salary, employeesService);
					salaryItem.setUserObject(model);
				}
			}
			
		});
		
		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem item = event.getSelectedItem();
				Object userObject = item.getUserObject();
				if ( userObject == null  ) {
					return;
				}
				if ( userObject instanceof IReportsModel ) {
					employeeDetail.getSalaryReceipt().setReports((IReportsModel<IReport>)userObject);
				}
			}
		});

		initWidget(tree);
	}

	public void setEmployeeDetail(EmployeeDetail employeeDetail) {
		this.employeeDetail = employeeDetail;
	}
	
	/**
	 * Generates HTML for a tree item with an attached icon.
	 */
	private static String imageItemHTML(ImageResource imageProto, String title) {
		return AbstractImagePrototype.create(imageProto).getHTML() + " "
				+ title;
	}

	
	private static Map<Salary.Type, String> TYPES_DESCRIPTIONS = 
			new HashMap<Salary.Type, String>(){
		{
			put(Salary.Type.SALARY, "Nominas");
			put(Salary.Type.EXTRA, "Pagas extras");
			put(Salary.Type.DELAY, "Atrasos");
			put(Salary.Type.SETTLE, "Finiquitos");
			put(Salary.Type.NOT_ENJOYED_VACATIONS, "Vacaciones no disfrutadas");
		}
	};
	
	private static String getTypeDescription(Salary.Type type) {
		return TYPES_DESCRIPTIONS.get(type);
	}

	private static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat
			.getFormat("MMMM 'del' yyyy");
	
	
}
