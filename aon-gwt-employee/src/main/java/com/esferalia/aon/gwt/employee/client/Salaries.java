package com.esferalia.aon.gwt.employee.client;

import java.util.AbstractList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.employee.shared.Salary;
import com.esferalia.aon.gwt.employee.shared.Salary.Type;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ImageLoadingCell.DefaultRenderers;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class Salaries extends Composite implements AsyncCallback<List<Salary>> {

	private static final float THUMBNAIL_ZOOM_RATION = 0.20f;

	private static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_SHORT);

	interface Handler {
		void onSalarySelected(Salary salary);
	}

	static class SalaryThumbnail extends AbstractList<SalaryThumbnail> {

		private Salary salary;

		private String html;

		public Salary getSalary() {
			return salary;
		}

		public SalaryThumbnail(Salary salary) {
			this.salary = salary;
		}

		public int getId() {
			return salary.getId();
		}

		public Type getType() {
			return salary.getType();
		}

		public Date getStartDate() {
			return salary.getStartDate();
		}

		public Date getEndDate() {
			return salary.getEndDate();
		}

		public Date getIssueDate() {
			return salary.getIssueDate();
		}

		public Date getChargeDate() {
			return salary.getChargeDate();
		}

		public String getHtml() {
			return html;
		}

		public void setHtml(String thumbnail) {
			this.html = thumbnail;
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public SalaryThumbnail get(int index) {
			return this;
		}

		/**
		 * The key provider that provides the unique ID of a contact.
		 */
		public static final ProvidesKey<SalaryThumbnail> KEY_PROVIDER = new ProvidesKey<SalaryThumbnail>() {
			public Object getKey(SalaryThumbnail item) {
				return item == null ? null : item.getId();
			}
		};

	}

	interface ListResources extends CellList.Resources
	{
	  @Source({"aonCellList.css"})
	  ListStyle cellListStyle();

	} 	
	
	interface ListStyle extends CellList.Style {} 
	
	
	
	/**
	 * The Cell used to render a salary html.
	 * 
	 * @author rtrepiana
	 * 
	 */
	static class SalaryThumbnailCell extends AbstractCell<SalaryThumbnail> {

		interface Template extends SafeHtmlTemplates {
			@Template("<div style='margin: auto;' >{0}</div>")
			SafeHtml loading(SafeHtml loadingHtml);
		}

		private static Template template = GWT.create(Template.class);

		/**
		 * The images used by the {@link DefaultRenderers}.
		 */
		interface Resources extends ClientBundle {
			ImageResource loading();
		}

		private SafeHtml loadingSafeHtml;

		public SalaryThumbnailCell() {
			Resources resources = GWT.create(Resources.class);
			ImageResource loadingResource = resources.loading();
			loadingSafeHtml = AbstractImagePrototype.create(loadingResource)
					.getSafeHtml();
		}

		@Override
		public void render(Context context, SalaryThumbnail salaryThumbnail,
				SafeHtmlBuilder sb) {
			/*
			 * Always do a null check on the value. Cell widgets can pass null
			 * to cells if the underlying data contains a null, or if the data
			 * arrives out of order.
			 */
			if (salaryThumbnail == null) {
				return;
			}
			sb.appendHtmlConstant("<table style='margin: auto; '>");
			sb.appendHtmlConstant("<tr>");
			sb.appendHtmlConstant("<td align='center' >");
			sb.appendHtmlConstant("<div style='width:119px; height: 173px ; border: 1px solid #e5e5e5;box-shadow: 4px 4px 4px #e5e5e5;' >");
			String html = salaryThumbnail.getHtml();
			if (html != null) {
				sb.appendHtmlConstant(html);
			} else {
				sb.append(template.loading(loadingSafeHtml));
			}
			sb.appendHtmlConstant("</div>");
			sb.appendHtmlConstant("</td>");
			sb.appendHtmlConstant("</tr>");
			sb.appendHtmlConstant("<tr>");
			sb.appendHtmlConstant("<td align='center' >");
			sb.appendHtmlConstant(DATE_TIME_FORMAT.format(salaryThumbnail
					.getChargeDate()));
			sb.appendHtmlConstant("</td>");
			sb.appendHtmlConstant("</tr>");
			sb.appendHtmlConstant("</table>");

		}

	}

	class HTMLLoader implements AsyncCallback<String> {

		private int start;
		private int end;
		private SalaryThumbnail salaryThumbnail;

		public void load(int start, int end) {

			if (start >= end) {
				return;
			}

			this.start = start;
			this.end = end;
			this.salaryThumbnail = salariesThumbnails.get(start);
			employeesService.getSalaryReceiptHTML(salaryThumbnail.getSalary(),
					THUMBNAIL_ZOOM_RATION, this);
		}

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub
			Window.alert(caught.getLocalizedMessage());
		}

		@Override
		public void onSuccess(String result) {
			salaryThumbnail.setHtml(result);
			cellList.setRowData(start, salaryThumbnail);
			load(start + 1, end);
		}
	}

	private CellList<SalaryThumbnail> cellList;
	private List<SalaryThumbnail> salariesThumbnails;

	private List<Handler> handlers;
	private EmployeesServiceAsync employeesService;

	public Salaries() {

		CellList.Resources cellListResources = 
				GWT.create(ListResources.class); 		

		handlers = new LinkedList<Salaries.Handler>();

		// Create a CellList.
		SalaryThumbnailCell salaryCell = new SalaryThumbnailCell();
		// Set a key provider that provides a unique key for each salary. If key
		// is
		// used to identify salary when fields change.
		cellList = new CellList<SalaryThumbnail>(salaryCell,
				cellListResources,
				SalaryThumbnail.KEY_PROVIDER);
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		final SingleSelectionModel<SalaryThumbnail> selectionModel = new SingleSelectionModel<SalaryThumbnail>(
				SalaryThumbnail.KEY_PROVIDER);
		cellList.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						SalaryThumbnail salaryThumbnail = selectionModel
								.getSelectedObject();
						if (salaryThumbnail != null) {
							fireSalarySelected(salaryThumbnail.getSalary());
						}
					}
				});

		initWidget(cellList);

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		employeesService = GWT.create(EmployeesService.class);
		employeesService.getSalaries(null, this);

	}

	public void addHandler(Handler handler) {
		handlers.add(handler);
	}

	@Override
	public void onFailure(Throwable caught) {
		// TODO Auto-generated method stub
		Window.alert(caught.getLocalizedMessage());
	}

	@Override
	public void onSuccess(List<Salary> salaries) {

		salariesThumbnails = new LinkedList<Salaries.SalaryThumbnail>();
		for (Salary salary : salaries) {
			salariesThumbnails.add(new SalaryThumbnail(salary));
		}

		cellList.setRowData(salariesThumbnails);
		HTMLLoader loader = new HTMLLoader();
		loader.load(0, salaries.size());
	}

	private void fireSalarySelected(Salary salary) {
		for (Handler handler : handlers) {
			handler.onSalarySelected(salary);
		}
	}
}
