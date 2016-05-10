package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Model2002015;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page00;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page01;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page02;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page03;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page04;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page05;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page06;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page07;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page08;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page09;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page10;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page11;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page12;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page13;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015.Page14;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TreeItem;

public class Model2002015TreeNode extends TreeNode<Mod2002015TreeObject>  {

	private Model2002015 model2002015 =  new Model2002015();
	private boolean expanded = false;
	
	public abstract class PageTreeNode extends TreeNode<Mod2002015> {
		
		@Override
		public void select(FiscalTree fiscalTree) {
			selectPage(fiscalTree);
		}

		@Override
		public Mod2002015 getTreeObject() {
			return (Mod2002015) this.getUserObject();
		}

		@Override
		public TreeNode<Mod2002015> render(HasTreeItems parent, Mod2002015 notToBeUsed) {
	    	parent.addItem(this);
	    	this.setUserObject( getParentItem().getUserObject() );
	    	setLabel();
			return this;
		}

		public void setLabel() {
			InlineLabel label = new InlineLabel();
			label.setText(getPageLabel());
			label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
			label.addStyleName(AON.AON_CSS.aonIconModel() );
			this.setWidget(label);	
		}

		protected abstract String getPageLabel();
		protected abstract void selectPage(FiscalTree fiscalTree);

	}

	public PageTreeNode PAGE00 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "1.- " + AON.MSG.identification();
		}

		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page00 widget = (Page00) model2002015.getDeckPanel().getWidget(1);
			widget.dump(Model2002015TreeNode.this.getTreeObject());
			Model2002015TreeNode.this.select( fiscalTree, 1);
		}
	};
	
	public PageTreeNode PAGE01 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "2.- " + AON.MSG.administratorPage();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page01 widget = (Page01) model2002015.getDeckPanel().getWidget(2);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 2);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};

	public PageTreeNode PAGE02 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "3.- " + AON.MSG.participations();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page02 widget = (Page02) model2002015.getDeckPanel().getWidget(3);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 3);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};

	public PageTreeNode PAGE03 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "4.- " + AON.MSG.balanceActivo();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page03 widget = (Page03) model2002015.getDeckPanel().getWidget(4);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 4);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};
	public PageTreeNode PAGE04 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "5.- " + AON.MSG.balancePasivo();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page04 widget = (Page04) model2002015.getDeckPanel().getWidget(5);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 5);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};
	public PageTreeNode PAGE05 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "6.- " + AON.MSG.pyg();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page05 widget = (Page05) model2002015.getDeckPanel().getWidget(6);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 6);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};
	public PageTreeNode PAGE06 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "7.- " + AON.MSG.patrimonioIngresos();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page06 widget = (Page06) model2002015.getDeckPanel().getWidget(7);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 7);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};
	public PageTreeNode PAGE07 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "8.- " + AON.MSG.patrimonioCambios();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page07 widget = (Page07) model2002015.getDeckPanel().getWidget(8);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 8);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};
	public PageTreeNode PAGE08 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "9.- " + AON.MSG.liquidacionI();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page08 widget = (Page08) model2002015.getDeckPanel().getWidget(9);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 9);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};
	public PageTreeNode PAGE09 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "10.- " + AON.MSG.liquidacionII();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page09 widget = (Page09) model2002015.getDeckPanel().getWidget(10);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 10);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};
	public PageTreeNode PAGE10 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "11.- " + AON.MSG.liquidacionIII();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page10 widget = (Page10) model2002015.getDeckPanel().getWidget(11);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 11);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
		
	};
	public PageTreeNode PAGE11 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "12.- " + AON.MSG.liquidacionIV();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page11 widget = (Page11) model2002015.getDeckPanel().getWidget(12);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 12);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};
	public PageTreeNode PAGE12 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "13.- " + AON.MSG.incomeDistribution();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page12 widget = (Page12) model2002015.getDeckPanel().getWidget(13);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 13);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};

	public PageTreeNode PAGE13 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "14.- " + AON.MSG.deducibleLimitation();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page13 widget = (Page13) model2002015.getDeckPanel().getWidget(14);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 14);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};

	public PageTreeNode PAGE14 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return "15.- " + AON.MSG.idDocument();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Mod2002015TreeObject mod200Object = Model2002015TreeNode.this.getTreeObject();
			if (mod200Object.isInitialized()) {
				Page14 widget = (Page14) model2002015.getDeckPanel().getWidget(15);
				widget.dump(Model2002015TreeNode.this.getTreeObject());
				Model2002015TreeNode.this.select( fiscalTree, 15);
			} else {
				Window.alert( AON.MSG.mustInitialzeMod200());
			}
		}
	};
	@Override
	public void select(final FiscalTree fiscalTree) {
		if (!expanded) {
			model2002015.startModel(getTreeObject(), new AsyncCallback<Mod2002015>(){

				@Override
				public void onSuccess(Mod2002015 result) {
			    	TreeItem page00 = PAGE00.render(Model2002015TreeNode.this, null);
					PAGE01.render(Model2002015TreeNode.this, null);
			    	PAGE02.render(Model2002015TreeNode.this, null);
			    	PAGE03.render(Model2002015TreeNode.this, null);
			    	PAGE04.render(Model2002015TreeNode.this, null);
			    	PAGE05.render(Model2002015TreeNode.this, null);
			    	PAGE06.render(Model2002015TreeNode.this, null);
			    	PAGE07.render(Model2002015TreeNode.this, null);
			    	PAGE08.render(Model2002015TreeNode.this, null);
			    	PAGE09.render(Model2002015TreeNode.this, null);
			    	PAGE10.render(Model2002015TreeNode.this, null);
			    	PAGE11.render(Model2002015TreeNode.this, null);
			    	PAGE12.render(Model2002015TreeNode.this, null);
			    	PAGE13.render(Model2002015TreeNode.this, null);
			    	PAGE14.render(Model2002015TreeNode.this, null);
			    	setState(true);
					expanded = true;
					model2002015.getDeckPanel().remove(0);
					model2002015.getDeckPanel().insert(fiscalTree.getGenericContent(Model2002015TreeNode.this), 0);
					// select(fiscalTree, 0);
					fiscalTree.renderGenericContent(Model2002015TreeNode.this);
					getTree().setSelectedItem(page00);
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
				}
			});
		} else {
			select(fiscalTree, 0);	
		}
	}
	
	public void select(final FiscalTree fiscalTree, final int index) {
		fiscalTree.setContent(model2002015);
		model2002015.getDeckPanel().showWidget(index);
	}
	
	@Override
	public Mod2002015TreeObject getTreeObject() {
		return (Mod2002015TreeObject) this.getUserObject();
	}

	@Override
	public Model2002015TreeNode render(final HasTreeItems parent,final Mod2002015TreeObject treeObj) {
		setUserObject(treeObj);
		parent.addItem(Model2002015TreeNode.this);
    	setLabel(treeObj);
    	expanded = false;
		return this;
	}

	public void setLabel(Mod2002015TreeObject mod200Obj) {
		Mod2002015 mod200 = mod200Obj.getMod200();
		String id = mod200Obj.getId()==null?"":" ( Id: "+mod200Obj.getId()+")";
		Administration adm = mod200==null
			?Administration.COMMON_TERRITORY
			:mod200.getAdministration();
		InlineLabel label = new InlineLabel();
		label.setText(AON.MSG.fiscalModelType(FiscalModelType.M200)
				+ (mod200Obj.isComplementary()?" - Compl. ":"")
				+id
				);
		label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
		label.addStyleName(TreeNode.getAdministrationIconBW(adm));
		this.setWidget(label);	
	}

}
