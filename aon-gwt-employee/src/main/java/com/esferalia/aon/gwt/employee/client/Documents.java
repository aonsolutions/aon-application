package com.esferalia.aon.gwt.employee.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.employee.shared.Document;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class Documents extends Composite {

	/**
	 * Specifies the images that will be bundled for this Composite and specify
	 * that tree's images should also be included in the same bundle.
	 */
	public static interface Images extends ClientBundle, Tree.Resources {

		ImageResource folder();

		ImageResource employee();

		ImageResource enterprise();

		ImageResource doc_pdf();

		ImageResource doc_word();

		ImageResource doc_text();

		ImageResource doc_excel();

		ImageResource doc_powerpoint();

		@Source("noimage.png")
		ImageResource treeLeaf();
	}
	private static final Images IMAGES = GWT.create(Images.class);
	

	private Tree tree;

	private DocumentsServiceAsync documentsService;

	private EmployeeDetail employeeDetail;

	public Documents() {


		tree = new Tree(IMAGES);

		// Create a remote service proxy to talk to the server-side Employees
		// service.
		documentsService = GWT.create(DocumentsService.class);
		documentsService
				.getEnterpriseDocuments(new AsyncCallback<List<Document>>() {

					@Override
					public void onSuccess(List<Document> documents) {

						Map<String, TreeItem> categoryTreeItems = new HashMap<String, TreeItem>();

						for (Document document : documents) {
							TreeItem docItem;
							String category = document.getCategory();
							if (category == null) {
								docItem = new TreeItem(imageItemHTML(
										getImage(document),
										document.getDescription()));
								tree.addItem(docItem);
							} else {
								TreeItem categoryTreeItem = categoryTreeItems
										.get(category);
								if (categoryTreeItem == null) {
									categoryTreeItem = new TreeItem(
											imageItemHTML(IMAGES.folder(),
													category));
									tree.addItem(categoryTreeItem);
									categoryTreeItems.put(category,
											categoryTreeItem);
								}
								docItem = addImageItem(categoryTreeItem,
										document.getDescription(),
										getImage(document));
							}
							docItem.setUserObject(new DocumentReportsModel ( document ));
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert(caught.getLocalizedMessage());
					}
				});

		initWidget(tree);

		tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				TreeItem item = event.getSelectedItem();
				Object userObject = item.getUserObject();
				if (userObject instanceof IReportsModel<?>) {
					onReportsSelected((IReportsModel<IReport>) userObject);
				}

			}
		});
	}

	public void setEmployeeDetail(EmployeeDetail employeeDetail) {
		this.employeeDetail = employeeDetail;
	}

	private void onReportsSelected(IReportsModel<IReport> reports) {
		employeeDetail.getSalaryReceipt().setReports(reports);
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
	
	
	

	private class DocumentReportsModel extends AbstractReportsModel<IReport>
			implements IReport {

		private List<Document> documents;

		public DocumentReportsModel(Document document) {
			this.documents = new ArrayList<Document>(1);
			this.documents.add(document);
			first();
		}

		public DocumentReportsModel(List<Document> documents) {
			this.documents = documents;
			first();
		}

		@Override
		public int size() {
			return documents.size();
		}

		@Override
		public IReport current() {
			return this;
		}

		@Override
		public void getAsHTML(float zoomRatio, AsyncCallback<String> callback) {
			Document doc = documents.get(currentIndex());
			//callback.onSuccess(  "<div><img src='aon_gwt_employee/pdf2Image/"+ doc.getId() +".png'></img> </div>");
			documentsService.getAsHTML(doc, callback);
		}
	}
	
	@SuppressWarnings("serial")
	private static final Map<String, ImageResource> IMAGES_MAP = 
			new HashMap<String, ImageResource>(){
		{
			put("application/pdf", IMAGES.doc_pdf());
			put("application/msword", IMAGES.doc_word());
			put("application/vnd.ms-excel", IMAGES.doc_excel());
			put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", IMAGES.doc_excel());
			put("pplication/vnd.ms-powerpoint", IMAGES.doc_powerpoint());
		}
	};
	
	private static ImageResource getImage(Document doc ) {
		String mimeType = doc.getMimeType();
		return IMAGES_MAP.containsKey(mimeType) ? IMAGES_MAP.get(mimeType) : IMAGES.doc_text();
	}

}
