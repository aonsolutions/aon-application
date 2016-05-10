package com.esferalia.aon.gwt.fiscal.client.tree.node;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.FiscalTreeCallback;
import com.esferalia.aon.gwt.fiscal.client.tree.content.EnterpriseYear;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TreeItem;

public class FiscalModelsTreeNode extends TreeNode<EnterpriseYear> {
	
	public static abstract class TreeNodeFiscalModelTypes<T> extends TreeNodeTypes<T>{

		public static TreeNodeFiscalModelTypes<Mod2002013TreeObject> MODEL_200_2013 = new TreeNodeFiscalModelTypes<Mod2002013TreeObject>() {
			@Override
			public TreeNode<Mod2002013TreeObject> getInstance() {
				return new Model2002013TreeNode();
			}
			@Override
			public boolean accept(IFiscalModel fm) {
				return (fm.getModel() == FiscalModelType.M200 && fm.getYear() == 2013);
			}
			@Override
			public Mod2002013TreeObject getFiscalModel(IFiscalModel fm) {
				
				Mod2002013TreeObject treeObj = fm.getId() == null
					?new Mod2002013TreeObject(FiscalTree.getCurrentDomainName(), fm.getDomain(), fm.getYear())
					:new Mod2002013TreeObject(FiscalTree.getCurrentDomainName(), fm.getDomain(), fm.getYear(), fm.getId());
				return treeObj;
			}
		};
		
		public static TreeNodeFiscalModelTypes<Mod2002014TreeObject> MODEL_200_2014 = new TreeNodeFiscalModelTypes<Mod2002014TreeObject>() {
			@Override
			public TreeNode<Mod2002014TreeObject> getInstance() {
				return new Model2002014TreeNode();
			}
			@Override
			public boolean accept(IFiscalModel fm) {
				return (fm.getModel() == FiscalModelType.M200 && fm.getYear() == 2014);
			}
			@Override
			public Mod2002014TreeObject getFiscalModel(IFiscalModel fm) {
				Mod2002014TreeObject treeObj = fm.getId() == null
					?new Mod2002014TreeObject(FiscalTree.getCurrentDomainName(), fm.getDomain(), fm.getYear())
					:new Mod2002014TreeObject(FiscalTree.getCurrentDomainName(), fm.getDomain(), fm.getYear(), fm.getId(), fm.isComplementary());
				return treeObj;
			}
		};

		public static TreeNodeFiscalModelTypes<Mod2002015TreeObject> MODEL_200_2015 = new TreeNodeFiscalModelTypes<Mod2002015TreeObject>() {
			@Override
			public TreeNode<Mod2002015TreeObject> getInstance() {
				return new Model2002015TreeNode();
			}
			@Override
			public boolean accept(IFiscalModel fm) {
				return (fm.getModel() == FiscalModelType.M200 && fm.getYear() == 2015);
			}
			@Override
			public Mod2002015TreeObject getFiscalModel(IFiscalModel fm) {
				Mod2002015TreeObject treeObj = fm.getId() == null
					?new Mod2002015TreeObject(FiscalTree.getCurrentDomainName(), fm.getDomain(), fm.getYear())
					:new Mod2002015TreeObject(FiscalTree.getCurrentDomainName(), fm.getDomain(), fm.getYear(), fm.getId(), fm.isComplementary());
				return treeObj;
			}
		};

		public static TreeNodeFiscalModelTypes<Mod202> MODEL_202 = new TreeNodeFiscalModelTypes<Mod202>() {
			@Override
			public TreeNode<Mod202> getInstance() {
				return new Model202TreeNode();
			}
			@Override
			public boolean accept(IFiscalModel fm) {
				return (fm.getModel() == FiscalModelType.M202);
			}
			@Override
			public Mod202 getFiscalModel(IFiscalModel fm) {
				Mod202 mod202 = new Mod202();
				copy(fm, mod202);
				return mod202;
			}
		};

		public abstract boolean accept(IFiscalModel fm);
		public abstract T getFiscalModel(IFiscalModel fm);
		
		public static void copy(IFiscalModel from,FiscalModel to) {
			to.setId(from.getId());
			to.setAdministration(from.getAdministration());
			to.setModel(from.getModel());
			to.setYear(from.getYear());
			to.setPeriod(from.getPeriod());
			to.setReplacement(from.isReplacement());
			to.setDomain(from.getDomain());
			to.setStatus(from.getStatus());
			to.setComplementary(from.isComplementary());
			to.setDocument(from.getDocument());
			to.setSurname(from.getSurname());
			to.setName(from.getName());
		}
		
		static TreeNodeFiscalModelTypes<?>[] NODE_TYPES = new TreeNodeFiscalModelTypes<?>[]{
			MODEL_202	
		};
		
	}
	
	@Override
	public EnterpriseYear getTreeObject() {
		return (EnterpriseYear) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalTree) {
		fiscalTree.renderGenericContent(this);
	}
	
	public YearTreeNode getYearNode(int year) {
		YearTreeNode yearNode = null;
		for (int i = 0; i < getChildCount(); i++) {
			if (getChild(i) instanceof YearTreeNode) {
				yearNode = (YearTreeNode) getChild(i);
				if (yearNode.getTreeObject().getYear() == year) {
					yearNode.setState(true);
					return yearNode;
				}
			}
		}
		EnterpriseYear ey = new EnterpriseYear();
		ey.setEnterprise(getTreeObject().getEnterprise());
		ey.setYear(year);
		yearNode = (YearTreeNode) TreeNodeFiscalModelTypes.YEAR.getInstance().render(this, ey);
		yearNode.setState(true);
		return yearNode;
	}
	
	public ModelTreeNode getModelNode(int year,FiscalModelType type) {
		//YearTreeNode yearNode = getYearNode(year);
		ModelTreeNode modelNode = null;
		for (int i = 0; i < this.getChildCount(); i++) {
			if (this.getChild(i) instanceof ModelTreeNode) {
				modelNode = (ModelTreeNode) this.getChild(i);
				if (modelNode.getTreeObject() == type) {
					modelNode.setState(true);
					return modelNode;
				}
			}
		}
		modelNode = (ModelTreeNode) TreeNodeFiscalModelTypes.MODEL.getInstance().render(this, type);
		//yearNode.setState(true);
		modelNode.setState(true);
		return modelNode;
	}

	
	@Override
	public TreeNode<EnterpriseYear> render(final HasTreeItems parent,final EnterpriseYear ey) {
    	InlineLabel label = new InlineLabel();
    	label.setText(AON.MSG.fiscalModels());
    	label.addStyleName(AON.AON_CSS.aonIconModel());
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setWidget(label);
    	setUserObject(ey);
    	parent.addItem(this);
    	FiscalTree.FISCAL_SERVICE.getAllModels(FiscalTree.getCurrentDomainName(), ey.getEnterprise().getDomain(), new AsyncCallback<LinkedList<IFiscalModel>>() {
    			
    			@Override
    			public void onSuccess(LinkedList<IFiscalModel> result) {
    				for (IFiscalModel fm : result) {
    					// TODO HACER ALGO CON ESTO!!!
    					if(fm.getYear() == ey.getYear()){
    						
    						if (TreeNodeFiscalModelTypes.MODEL_202.accept(fm) ) {
        						final ModelTreeNode parentNode = getModelNode(fm.getYear(),fm.getModel());
    							TreeNodeFiscalModelTypes.MODEL_202.getInstance().render(parentNode, TreeNodeFiscalModelTypes.MODEL_202.getFiscalModel(fm));
    							parentNode.setState(true);	
        					} else if (TreeNodeFiscalModelTypes.MODEL_200_2013.accept(fm) ) {
        						final ModelTreeNode parentNode = getModelNode(fm.getYear(),fm.getModel());
        						Mod2002013TreeObject to = TreeNodeFiscalModelTypes.MODEL_200_2013.getFiscalModel(fm);
        						final TreeItem item = TreeNodeFiscalModelTypes.CORPORATE_TAX_2013.getInstance().render(parentNode,to);
        						to.setFiscalTreeCallback(new FiscalTreeCallback<Mod2002013TreeObject>() {
								
									@Override
									public void remove(Mod2002013TreeObject treeObject) {
										item.remove();
										parentNode.getTree().setSelectedItem(parentNode);
									}
									@Override
									public void onError(Mod2002013TreeObject treeObject) {}
									@Override
									public void changeLabel(Mod2002013TreeObject treeObject) {}
									
								});
            		    		parentNode.setState(true);
        					} else if (TreeNodeFiscalModelTypes.MODEL_200_2014.accept(fm) ) {
        						final ModelTreeNode parentNode = getModelNode(fm.getYear(),fm.getModel());
        						Mod2002014TreeObject to = TreeNodeFiscalModelTypes.MODEL_200_2014.getFiscalModel(fm);
        						final TreeItem item = TreeNodeFiscalModelTypes.CORPORATE_TAX_2014.getInstance().render(parentNode,to);
        						to.setFiscalTreeCallback(new FiscalTreeCallback<Mod2002014TreeObject>() {
								
									@Override
									public void remove(Mod2002014TreeObject treeObject) {
										item.remove();
										parentNode.getTree().setSelectedItem(parentNode);
									}

									@Override
									public void onError(Mod2002014TreeObject treeObject) {
									
									}
									@Override
									public void changeLabel(Mod2002014TreeObject treeObject) {
									}
								});
            		    		parentNode.setState(true);
        					}
    					}
    				}
    				FiscalModelsTreeNode.this.setState(true);
    				
    			}

				@Override
    			public void onFailure(Throwable caught) {
    			}
    		});
    	return this;
	}
	
}
