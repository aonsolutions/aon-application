package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonOptionsToolbar;
import com.esferalia.aon.gwt.common.shared.NumberUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class Agreements extends ResizeComposite implements
		AgreementsTree.Listener, AonOptionsToolbar.Listener {

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

//	@UiField
//	OptionsToolbar toolbar;
	@UiField
	AonOptionsToolbar toolbar;
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
		toolbar.addListener(this);
		
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

	private synchronized Agreement newAgreement() {
		Agreement agreement = new Agreement();
		int newId = newsIdCounter--;
		agreement.setId(newsIdCounter);
		agreement.setDescription("CONVENIO NO GUARDADO " + -newId);
		agreement.setDomain(getDomain());

		return agreement;
	}
	
	private TreeItem addAgreementItem(Agreement agreement) {
		String description = agreement.getDescription();
		if (agreement.isRedefined()) {
			description = "*" + description;
		}

		List<ImageResource> marks = new ArrayList<ImageResource>();
		if (NumberUtils.notEquals(0, agreement.getDomain()) 
				&& NumberUtils.notEquals(domain, agreement.getDomain()) )
			marks.add(IMAGES.parent());

//		TreeItem agreementTreeItem = new TreeItem(AgreementsTree.imageItemSafeHtml(description,
//				AgreementsTree.getImageResource(agreement, domain),
//				marks.toArray(new ImageResource[marks.size()])));
		
		TreeItem agreementTreeItem = new TreeItem(AgreementsTree.imageItemSafeHtml(description,
				AgreementsTree.getImageResource(agreement, domain)));

		agreementTreeItem.setUserObject(agreement);

		agreementsTree.getTree().addItem(agreementTreeItem);
		
		agreementTreeItem.ensureDebugId(getId(agreement));

		return agreementTreeItem;
	}


	@Override
	public boolean evaluateId(Agreement agreement) {
		return agreement.getId() >= 0;
	}
	
	@Override
	public void onNewButtonClick(ClickEvent event) {
		addNewItemTree(null);
	}

	@Override
	public void onPasteButtonClick(ClickEvent event) {
		Object object = getAgreementsTree().getTree().getSelectedItem().getUserObject();
		
		if(object instanceof Agreement) {
			for(Toolbar toolbar : toolbars)
				toolbar.onAgreementCtrlV((Agreement) object);
		}
	}

	@Override
	public void onCopyButtonClick(ClickEvent event) {
		Object object = getAgreementsTree().getTree().getSelectedItem().getUserObject();
		
		if(object instanceof Agreement) {
			Agreement agreement = (Agreement) object;
			
			if(agreement.getId() >= 0) {
				for(Toolbar toolbar : toolbars)
					toolbar.onAgreementCtrlC((Agreement) object);
			}
		}
	}

	@Override
	public void onDraftButtonClick(ClickEvent event) {
		Object object = agreementsTree.getTree().getSelectedItem().getUserObject();
		if(object instanceof Agreement) {
			for(Toolbar toolbar : toolbars)
				toolbar.onMoveToTrash((Agreement) object);
		}
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
	public void onCollapseMenuButtonClick(ClickEvent event) {
		for (Listener listener : listeners)
			listener.onCollapseMenuButtonClick();
	}
	
	@Override
	public void onShowMenuButtonClick(ClickEvent event) {
		for (Listener listener : listeners)
			listener.onShowMenuButtonClick();
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
	
	@Override
	public void onCollapseAllButtonClick(ClickEvent event) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onKeyUpSearchTextBox(KeyUpEvent event) {
		filter(toolbar.getSearchTextBox().getValue());
	}
	
	private void filter(String pattern) {
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
						else 
							toolbar.setVisibleSearchTextBox(true);

					}
				});
	}
	
	private static String getId(Agreement agreement) {
		return agreement.getDescription()
				.toLowerCase()
				.replaceAll("\\s+", "_")
				;
	}

	@Override
	public void onImportButtonClick(ClickEvent event) {
		ServiAgreementDialog serviAgreementDialog = new ServiAgreementDialog() {

			@Override
			protected void onAccept(String serviAgreementCode) {
				agreementsTree.getEnterpriseService().getServiAgreement(serviAgreementCode, 
						new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						getAgreements();
					}
				});
			}
			
		};
		
		serviAgreementDialog.center();
		serviAgreementDialog.show();
	}

}
