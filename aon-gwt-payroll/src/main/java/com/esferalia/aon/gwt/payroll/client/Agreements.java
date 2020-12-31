package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class Agreements extends ResizeComposite implements
		AgreementsTree.Listener {

	interface Listener {

		void onAgreementSupr(Agreement agreement);

		void onAgreementContextMenu(Agreement agreement, ContextMenuEvent event);

		void onAgreementSelected(Agreement agreement);

		void onCollapseMenuButtonClick();

		void onShowMenuButtonClick();
	}
	
	interface Toolbar {
		
		void onAgreementCtrlC(Agreement agreement);

		void onAgreementCtrlV(Agreement agreement);
		
		void onNewAgreement(Agreement agreement);
		
		void onMoveToTrash(Agreement agreement);
		
		void onCopyAgreement(Agreement agreement);
		
		void onPasteAgreement(Agreement agreement);
	}

	private static final Images IMAGES = GWT.create(Images.class);

	interface Binder extends UiBinder<Widget, Agreements> {
	}

	private static final Binder BINDER = GWT.create(Binder.class);

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String treeItem();
		String rotate();
	}
	
	@UiField
	AgreementsTree agreementsTree;

	private static Integer newsIdCounter = 0;	
	private Integer domain;
	private List<Listener> listeners;
	private List<Toolbar> toolbars;

	public Agreements() {

		initWidget(BINDER.createAndBindUi(this));
		

		this.listeners = new LinkedList<Listener>();
		this.toolbars = new LinkedList<Toolbar>();
		
		agreementsTree.addListener(this);
		
		agreementsTree.getEnterpriseService().getDomain(
				new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						Agreements.this.domain = result;
						getAgreements();
					}
				});
	}
	
	@Override
	public void getAgreements() {
		agreementsTree.clearTree();
		getAgreements(0, 10);
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public void addToolbar(Toolbar toolbar) {
		toolbars.add(toolbar);
	}
	
	public void removeToolbar(Toolbar toolbar) {
		toolbars.remove(toolbar);
	}

	public AgreementsTree getAgreementsTree() {
		return agreementsTree;
	}

	public void reloadAgreements() {
		getAgreements();
	}
	
	public Integer getDomain() {
		return this.domain;
	}
	
	public void addNewItemTree(Agreement agreement) {
		addAgreementItem(newAgreement());
		//Select the Last One
		agreementsTree.getTree().setSelectedItem(
				agreementsTree.getTree().getItem(
						agreementsTree.getTree().getItemCount() - 1));
	}

	// -------------------------------------------------------- Private methods

	private void fireAgreementSelected(Agreement agreement) {
		for (Listener listener : listeners)
			listener.onAgreementSelected(agreement);
	}

	public synchronized Agreement newAgreement() {
		Agreement agreement = new Agreement();
		int newId = newsIdCounter--;
		agreement.setId(newsIdCounter);
		agreement.setDescription("CONVENIO NO GUARDADO " + -newId);
		agreement.setDomain(getDomain());

		return agreement;
	}
	
	public TreeItem addAgreementItem(Agreement agreement) {
		String description = agreement.getDescription();
		if (agreement.isRedefined()) {
			description = "*" + description;
		}

		List<ImageResource> marks = new ArrayList<ImageResource>();
		if (AonNumberUtils.notEquals(0, agreement.getDomain()) 
				&& AonNumberUtils.notEquals(domain, agreement.getDomain()) )
			marks.add(IMAGES.parent());
		
		TreeItem agreementTreeItem = null;
		
		if (AonNumberUtils.notEquals(0, agreement.getDomain()) && AonNumberUtils.equals(domain, agreement.getDomain()))
			agreementTreeItem = new TreeItem(getNewOwnAgreementRow(description));
		else 
			agreementTreeItem = new TreeItem(AgreementsTree.imageItemSafeHtml(description,
				AgreementsTree.getImageResource(agreement, domain)));

		agreementTreeItem.setUserObject(agreement);

		agreementsTree.getTree().addItem(agreementTreeItem);
		
		agreementTreeItem.ensureDebugId(getId(agreement));

		return agreementTreeItem;
	}


	private Widget getNewOwnAgreementRow(String description) {
		HTMLPanel panel = new HTMLPanel("");
		panel.addStyleName(style.treeItem()); 
		AonTableButton arrow = new AonTableButton("", AON.CSS.aonIconBack());
		arrow.addStyleName(style.rotate());
		Label descriptionL = new Label(description);
		panel.add(arrow);
		panel.add(descriptionL);
		return panel;
	}

	@Override
	public boolean evaluateId(Agreement agreement) {
		return agreement.getId() >= 0;
	}

	@Override
	public void onAgreementCtrlC(Agreement agreement) {
		if(agreement.getId() >= 0) {
			for(Toolbar toolbar : toolbars)
				toolbar.onAgreementCtrlC(agreement);
		}
	}

	@Override
	public void onAgreementCtrlV(Agreement agreement) {
		for(Toolbar toolbar : toolbars)
			toolbar.onAgreementCtrlV(agreement);
	}

	@Override
	public void onAgreementSupr(Agreement agreement) {
		for (Listener listener : listeners)
			listener.onAgreementSupr(agreement);
	}
	
	@Override
	public void onTreeItemSelected(SelectionEvent<TreeItem> event) {
		TreeItem selectedItem = event.getSelectedItem();
		Object object = selectedItem.getUserObject();
		
		if(object instanceof Agreement)
			fireAgreementSelected((Agreement) object);
	}

	@Override
	public void onAgreementContextMenu(Agreement agreement,
			ContextMenuEvent event) {
		for (Listener listener : listeners)
			listener.onAgreementContextMenu(agreement, event);

	}
	
	public void filter(String pattern) {
		Tree tree = agreementsTree.tree;
		for ( int i = 0; i < tree.getItemCount(); i++ ) {
			TreeItem item = tree.getItem(i);
			Agreement agreement = (Agreement) item.getUserObject();
			
			boolean visible = AonStringUtils.isBlank(pattern) || 
					( agreement.getDescription().toUpperCase().indexOf(pattern.trim().toUpperCase()) >= 0 );
			
			item.setVisible(visible);
		}
	}
	
	private void getAgreements (int offset, int limit) {
		
		agreementsTree.getEnterpriseService().getAgreements(offset, limit,
				new AsyncCallback<List<Agreement>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(List<Agreement> agreements) {
						int item2Select = -1;
						for (int i = 0; i < agreements.size(); i++) {

							Agreement agreement = agreements.get(i);
							if (evaluateId(agreement))
								addAgreementItem(agreement);

							if (agreement.isRedefined()) {
								if (item2Select == -1)
									item2Select = i;
							}
							if (agreement.hasEmployees()) {
								if (item2Select == -1)
									item2Select = i;
							}

						}
						
						// Select the first one.
						if (offset == 0 && agreementsTree.getTree().getItemCount() > 0)
							agreementsTree.getTree().setSelectedItem(
									agreementsTree.getTree().getItem(
											Math.max(item2Select, 0)), true);
						// Get remainning
						if ( agreements.size() == limit )
							getAgreements(offset + limit, limit);
//						else 
//							toolbar.setVisibleSearchTextBox(true);

					}
				});
	}
	
	private static String getId(Agreement agreement) {
		return agreement.getDescription()
				.toLowerCase()
				.replaceAll("\\s+", "_")
				;
	}

}
