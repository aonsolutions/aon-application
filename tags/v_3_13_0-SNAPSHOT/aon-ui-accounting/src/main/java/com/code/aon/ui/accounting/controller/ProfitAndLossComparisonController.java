package com.code.aon.ui.accounting.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.accounting.ProfitAndLossComparison;
import com.code.aon.accounting.summary.Summary;
import com.code.aon.accounting.summary.SummaryProviderParameters;

public class ProfitAndLossComparisonController {

	private List<ProfitAndLossComparison> list;
	private SummaryProviderParameters parameters;

	public List<ProfitAndLossComparison> getList() {
		return list;
	}

	public void setList(List<ProfitAndLossComparison> list) {
		this.list = list;
	}

	/**
	 * Genera una coleccion, producto de la union de la lista de margen bruto (gmList) y 
	 * la de total gastos (teList).
	 */
	public void loadCollection(){
		ProfitAndLossReportController plBudgetController;
		ProfitAndLossReportController plController;
		list = new LinkedList<ProfitAndLossComparison>();
		List<ProfitAndLossComparison> gmList = new LinkedList<ProfitAndLossComparison>();
		List<ProfitAndLossComparison> teList = new LinkedList<ProfitAndLossComparison>();
		List<ProfitAndLossComparison> resultList = new LinkedList<ProfitAndLossComparison>();
		
		getParameters().setBudgeted(true);
		plBudgetController = new ProfitAndLossReportController();
		plBudgetController.setParameters(parameters);
		plBudgetController.calculateSummaryCollections(null);
		
		// controller de cuentas ordinarias
		parameters.setBudgeted(false);
		plController = new ProfitAndLossReportController();
		plController.setParameters(parameters);
		plController.calculateSummaryCollections(null);

		// se carga la lista de margen neto y la de coste total, por separado (de los presupuestos) 
		boolean isGrossMargin = true;
		for(Summary sumB: plBudgetController.getCollection()){
			ProfitAndLossComparison plComp = new ProfitAndLossComparison();
			plComp.setId(sumB.getId());
			plComp.setDescription(sumB.getDescription());
			plComp.setBudgetCreditBalance(sumB.getCreditBalance());
			plComp.setBudgetUnpaidBalance(sumB.getUnpaidBalance());
			if(isGrossMargin){
				if(sumB.getId() != null ){
					gmList.add(plComp);
				} else {
					addOrUpdateListResultRow(gmList, plComp);
					isGrossMargin = false;
				}
			} else {
				if(sumB.getId() != null ){
					teList.add(plComp);
				} else {
					if(teList.get(teList.size()-1).getId()!=null){
						addOrUpdateListResultRow(teList, plComp);
					} else {
						addOrUpdateListResultRow(resultList, plComp);
					}
				}
			}
		}
		
		// se carga la lista de margen neto y la de coste total, por separado (de cuentas ordinarias) 
		isGrossMargin = true;
		for(Summary sum: plController.getCollection()){
			ProfitAndLossComparison plComp = new ProfitAndLossComparison();
			plComp.setId(sum.getId());
			plComp.setDescription(sum.getDescription());
			plComp.setCreditBalance(sum.getCreditBalance());
			plComp.setUnpaidBalance(sum.getUnpaidBalance());
			if(isGrossMargin){
				if(sum.getId() != null ){
					addOrdered(gmList, plComp);
				} else {
					addOrUpdateListResultRow(gmList, plComp);
					isGrossMargin = false;
				}
			} else {
				if(sum.getId() != null ){
					addOrdered(teList, plComp);
				} else {
					if(teList.get(teList.size()-1).getId()!=null){
						addOrUpdateListResultRow(teList, plComp);
					} else {
						addOrUpdateListResultRow(resultList, plComp);
					}
				}
			}
		}
		orderComparisonList(gmList);
		orderComparisonList(teList);
		
		list.addAll(gmList);
		list.addAll(teList);
		list.addAll(resultList);		
	}

	/**
	 * Añade a la lista de ProfitAndLossComparison el objeto, ordenado por id
	 * Si existe, actualiza el objeto de la lista. Si no, lo añade. 
	 * @param list
	 * @param obj
	 */
	private void addOrdered(List<ProfitAndLossComparison> list, ProfitAndLossComparison obj){
		boolean founded = false;
		ProfitAndLossComparison pl;
		int index=-1;
		
		Iterator<ProfitAndLossComparison> it = list.iterator();
		while(it.hasNext() && !founded){
			pl = it.next();
			if(pl.getId()==null){
				System.out.println("");
			} else if(pl.getId().equals(obj.getId())){
				pl.setCreditBalance(obj.getCreditBalance());
				pl.setUnpaidBalance(obj.getUnpaidBalance());
				founded = true;
			} else {
				// guarda el indice de la lista para guardarlo en orden
				Integer plId = new Integer(pl.getId());
				Integer objId = new Integer(obj.getId());
				if(objId.compareTo(plId)<0 && index==-1){
					index = list.indexOf(pl);	
				}
			}
		}
		if(!founded){
			if(index>=0)
				list.add(index,obj);
			else
				list.add(list.size()-1,obj);
		} 
	}
	
	/**
	 * Ordena la lista comparativa 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List orderComparisonList(List list) {
		class ProfitAndLossComparisonComparator implements Comparator<ProfitAndLossComparison> {
			public int compare(ProfitAndLossComparison plc1, ProfitAndLossComparison plc2) {
				String account1 = plc1.getId();
				String account2 = plc2.getId();
				if (account1 == null || account2 == null) {
					return -1;
				} 
				if (account1.substring(0, 1).equals(
						account2.substring(0, 1))) {
					return account1.compareTo(account2);
				}
				return (account1.substring(0, 1).equals("7")) ? -1 : 1;
			}
		}
		Collections.sort(list, new ProfitAndLossComparisonComparator());
		return list;
	}
	
	/**
	 * Añade o, si esta ya existe, actualiza la fila correspondiente a los totales de la lista 
	 * @param list
	 * @param obj
	 */
	private void addOrUpdateListResultRow(List<ProfitAndLossComparison> list, ProfitAndLossComparison obj){
		if(list.isEmpty()){
			list.add(obj);
		}else if(list.get(list.size()-1).getId()!=null){
			list.add(obj);
		} else {
			ProfitAndLossComparison element = list.get(list.size()-1);
	
			element.setBudgetCreditBalance(element.getBudgetCreditBalance()+obj.getBudgetCreditBalance());
			element.setBudgetUnpaidBalance(element.getBudgetUnpaidBalance()+obj.getBudgetUnpaidBalance());
			element.setCreditBalance(element.getCreditBalance()+obj.getCreditBalance());
			element.setUnpaidBalance(element.getUnpaidBalance()+obj.getUnpaidBalance());
			list.set(list.size()-1, element);
		}
	}

	public void onSearch(ActionEvent event) {
		if (parameters == null)
			parameters = new SummaryProviderParameters();
		loadCollection();
	}

	public SummaryProviderParameters getParameters() {
		if (parameters == null) {
			SummaryProviderParameters p = new SummaryProviderParameters();
			//p.setBudgeted(null);
			p.setLowerLevelVisible(false);
			setParameters(p);
		}
		return parameters;
	}

	public void setParameters(SummaryProviderParameters parameters) {
		this.parameters = parameters;
	}


}
