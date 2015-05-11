package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.TreeItem;

public abstract class TreeNode<T> extends TreeItem {

	
	public abstract void select(FiscalTree fiscalPanel);
	public abstract T getTreeObject();
	
	public void setTreeObject(T t) {
		setUserObject(t);
	}

	public abstract TreeNode<T> render(HasTreeItems parent,T t);
	
	
	public static String getAdministrationIconBW(Administration admon) {
		if (admon == Administration.ALAVA) {
			return AON.AON_CSS.aonIconArabaBW();
		} else if (admon == Administration.BIZKAIA) {
			return AON.AON_CSS.aonIconBizkaiaBW();
		} else if (admon == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonIconGipuzkoaBW();
		} else if (admon == Administration.NAVARRA) {
			return AON.AON_CSS.aonIconNavarraBW();
		} else {
			return AON.AON_CSS.aonIconAeatBW();
		}
	}

	public static String getAdministrationIcon(Administration admon) {
		if (admon == Administration.ALAVA) {
			return AON.AON_CSS.aonIconAraba();
		} else if (admon == Administration.BIZKAIA) {
			return AON.AON_CSS.aonIconBizkaia();
		} else if (admon == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonIconGipuzkoa();
		} else if (admon == Administration.NAVARRA) {
			return AON.AON_CSS.aonIconNavarra();
		} else {
			return AON.AON_CSS.aonIconAeat();
		}
	}

	public static String getAdministrationBG(Administration admon) {
		if (admon == Administration.ALAVA) {
			return AON.AON_CSS.aonFiscalArabaBg();
		} else if (admon == Administration.BIZKAIA) {
			return AON.AON_CSS.aonFiscalBizkaiaBg();
		} else if (admon == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonFiscalGipuzkoaBg();
		} else if (admon == Administration.NAVARRA) {
			return AON.AON_CSS.aonFiscalNavarraBg();
		} else {
			return AON.AON_CSS.aonFiscalAeatBg();
		}
	}

	public static String getAdministrationImage(Administration admon) {
		if (admon == Administration.ALAVA) {
			return AON.AON_CSS.aonArabaHeaderImage();
		} else if (admon == Administration.BIZKAIA) {
			return AON.AON_CSS.aonBizkaiaHeaderImage();
		} else if (admon == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonGipuzkoaHeaderImage();
		} else if (admon == Administration.NAVARRA) {
			return AON.AON_CSS.aonNavarraHeaderImage();
		} else {
			return AON.AON_CSS.aonAeatHeaderImage();
		}
	}
	
}
