package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page00;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page01;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page02;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page03;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page04;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page05;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page06;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page07;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page08;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page09;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page10;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page11;
import com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013.Page12;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.mod200.Mod200;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class Model2002013TreeNode extends TreeNode<Mod2002013TreeObject>  {

	public abstract class PageTreeNode extends TreeNode<Mod200> {
		
		@Override
		public void select(FiscalTree fiscalTree) {
			selectPage(fiscalTree);
		}

		@Override
		public Mod200 getTreeObject() {
			return (Mod200) this.getUserObject();
		}

		@Override
		public TreeNode<Mod200> render(HasTreeItems parent, Mod200 notToBeUsed) {
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
			return AON.MSG.identification();
		}

		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page00 widget =  new Page00();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};
	public PageTreeNode PAGE01 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.administratorPage();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page01 widget =  new Page01();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};

	public PageTreeNode PAGE02 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.participations();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page02 widget =  new Page02();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};

	public PageTreeNode PAGE03 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.balanceActivo();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page03 widget =  new Page03();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};

	public PageTreeNode PAGE04 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.patrimonioIngresos();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page04 widget =  new Page04();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};
	public PageTreeNode PAGE05 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.patrimonioCambios();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page05 widget =  new Page05();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};
	public PageTreeNode PAGE06 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.liquidacionI();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page06 widget =  new Page06();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};
	public PageTreeNode PAGE07 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.liquidacionII();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page07 widget =  new Page07();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};
	public PageTreeNode PAGE08 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.liquidacionIII();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page08 widget =  new Page08();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};
	public PageTreeNode PAGE09 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.liquidacionIV();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page09 widget =  new Page09();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};
	public PageTreeNode PAGE10 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.incomeDistribution();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page10 widget =  new Page10();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
		
	};
	public PageTreeNode PAGE11 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.deducibleLimitation();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page11 widget =  new Page11();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};
	public PageTreeNode PAGE12 = new PageTreeNode() {
		@Override
		protected String getPageLabel() {
			return AON.MSG.idDocument();
		}
		@Override
		protected void selectPage(FiscalTree fiscalTree) {
			Page12 widget =  new Page12();
			widget.dump(Model2002013TreeNode.this.getTreeObject());
			fiscalTree.setContent(widget);
		}
	};

	@Override
	public void select(final FiscalTree fiscalTree) {
		fiscalTree.renderGenericContent(this);
	}
	
	@Override
	public Mod2002013TreeObject getTreeObject() {
		return (Mod2002013TreeObject) this.getUserObject();
	}

	@Override
	public Model2002013TreeNode render(final HasTreeItems parent,final Mod2002013TreeObject treeObj) {
		setUserObject(treeObj);
		parent.addItem(Model2002013TreeNode.this);
		treeObj.getMod200ByYear( new AsyncCallback<Mod200>() {

			@Override
			public void onSuccess(Mod200 result) {
		    	setLabel(treeObj);
		    	PAGE00.render(Model2002013TreeNode.this, null);
		    	PAGE01.render(Model2002013TreeNode.this, null);
		    	PAGE02.render(Model2002013TreeNode.this, null);
		    	PAGE03.render(Model2002013TreeNode.this, null);
		    	PAGE04.render(Model2002013TreeNode.this, null);
		    	PAGE05.render(Model2002013TreeNode.this, null);
		    	PAGE06.render(Model2002013TreeNode.this, null);
		    	PAGE07.render(Model2002013TreeNode.this, null);
		    	PAGE08.render(Model2002013TreeNode.this, null);
		    	PAGE09.render(Model2002013TreeNode.this, null);
		    	PAGE10.render(Model2002013TreeNode.this, null);
		    	PAGE11.render(Model2002013TreeNode.this, null);
		    	PAGE12.render(Model2002013TreeNode.this, null);
		    	setState(true);
			}
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert(caught.getMessage() );
			}
			
		});
		return this;
	}

	public void setLabel(Mod2002013TreeObject mod200Obj) {
		Mod200 mod200 = mod200Obj.getMod200();
		InlineLabel label = new InlineLabel();
		label.setText(AON.MSG.fiscalModelType(FiscalModelType.M200)
				+ (mod200.isComplementary()?" - Compl.":"") 
				);
		label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
		label.addStyleName(TreeNode.getAdministrationIconBW(mod200.getAdministration()));
		this.setWidget(label);	
	}
}
