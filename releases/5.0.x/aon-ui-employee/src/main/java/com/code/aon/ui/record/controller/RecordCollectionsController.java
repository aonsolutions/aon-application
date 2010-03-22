package com.code.aon.ui.record.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.record.enumeration.ContractType;

public class RecordCollectionsController {
	
	private List<SelectItem> contractTypes;

	public List<SelectItem> getContractTypes() {
		if(contractTypes == null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			contractTypes = new LinkedList<SelectItem>();
			for (ContractType contractType : ContractType.values()) {
				String name = contractType.getName(locale);
				SelectItem item = new SelectItem(contractType, name);
				contractTypes.add(item);
			}
		}
		return contractTypes;
	}

//	public List<SelectItem> getDepartments() {
//		List<SelectItem> departments = new LinkedList<SelectItem>();
//		TreeDepartment tree = (TreeDepartment)AonUtil.getRegisteredBean("tree");
//        ITreeNode root = tree.getTreeModel().getRoot();
//		SelectItem item = new SelectItem( new Integer(0), root.getUserObject().toString() );
//		departments.add(item);
//		Iterator<ITreeNode> iter = root.getChildren().iterator();
//		int deep = 1;
//		while (iter.hasNext()) {
//			ITreeNode node = iter.next();
//			if (node.getChildCount() > 0)
//				printSelectItem( node, departments, deep );
//			look4children( node, departments, deep );
//		}
//		return departments;
//	}
//
//	private void look4children(ITreeNode root, List<SelectItem> departments, int deep) {
//		if (root.getChildCount() > 0) {
//			deep++;
//			Iterator<ITreeNode> iter = root.getChildren().iterator();
//			while (iter.hasNext()) {
//				ITreeNode node = iter.next();
//				look4children( node, departments, deep );
//			}
//		} else {
//			printSelectItem( root, departments, deep-- );
//		}
//		
//	}
//
//	private void printSelectItem(ITreeNode root, List<SelectItem> departments, int deep) {
//		Department d = (Department) root.getUserObject();
//		String deepStr = "";
//        while (deep-- > 0) {
//        	deepStr += "--";
//        }
//    	deepStr += ">";
//		SelectItem item = new SelectItem( d.getId(), deepStr + d.getDescription() );
//		departments.add(item);
//	}
}
