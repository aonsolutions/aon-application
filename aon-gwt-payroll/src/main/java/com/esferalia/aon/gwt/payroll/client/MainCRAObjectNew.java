package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseCCCFilter;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainCRAObjectNew {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private List<CCCInfo> allEnterpriseCCCs;
	private List<CCCInfo> enterpriseCCCs;
	private EnterpriseCCCFilter enterpriseCCCFilter;
	private HashMap<Integer, String> enterprisesMap;
	
	private List<CRA> allCRAs;
	private List<CRA> filterCRAs;
	private List<CRA> cras;
	private List<CRA> crasRectif;
	
	
	public MainCRAObjectNew() {
		super();
		
		this.allEnterpriseCCCs = new ArrayList<CCCInfo>();
		this.enterpriseCCCs = new ArrayList<CCCInfo>();
		this.enterpriseCCCFilter = new EnterpriseCCCFilter();
		this.enterprisesMap = new HashMap<Integer, String>();
		
		this.allCRAs = new ArrayList<CRA>();
		this.filterCRAs = new ArrayList<CRA>();
		this.cras = new ArrayList<CRA>();
		this.crasRectif = new ArrayList<CRA>();
		
	}
	
	public void getEnterpriseCCCs(Consumer<List<Enterprise>> success, Consumer<Throwable> failure){
		
		impl.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {
			
			@Override
			public void onSuccess(List<Enterprise> enterprises) {
				initEnterpriseMap(enterprises);
				initEnterpriseCCCs(enterprises);
				getCRAs(
					s -> {
						success.accept(enterprises);
					}, 
					f -> {});	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void getCRAs(Consumer<List<CRA>> success, Consumer<Throwable> failure){
		
		impl.getCRAs(new AsyncCallback<List<CRA>>() {
			
			@Override
			public void onSuccess(List<CRA> dbCRAs) {
				initCRAs(dbCRAs);
				success.accept(dbCRAs);
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void createNewCRA (Date startDate, ArrayList<String> cccList, Integer cccId, String type, Consumer<String> success, Consumer<Throwable> failure){
		impl.createNewCRA(startDate.getTime(), cccList, cccId, type, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());	
			}

			@Override
			public void onSuccess(String result) {
				getCRAs(s -> {
					success.accept(result);
				}, f -> {});
			}
		});
	}
	
	public void checkCreateNewCRA (Date startDate, ArrayList<Integer> cccList, Consumer<String> success, Consumer<Throwable> failure){
		impl.checkCreateNewCRA(startDate.getTime(), cccList, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());	
			}

			@Override
			public void onSuccess(String result) {
				success.accept(result);
			}
		});
	}
	
	public void deteleCRA (Integer code, Consumer<String> success, Consumer<Throwable> failure){
		impl.deleteCRA(code, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) { }

			@Override
			public void onSuccess(String result) {
				getCRAs(s -> {
					success.accept(result);
				}, f -> {});
			}
		});
	}
	
	private void initEnterpriseMap(List<Enterprise> enterprises) {
		this.enterprisesMap.clear();
		
		for(Enterprise enterprise: enterprises) {
			this.enterprisesMap.put(enterprise.getId(), enterprise.getName());
		}
	}
	
	private void initEnterpriseCCCs(List<Enterprise> enterprises) {
		enterpriseCCCs.clear();
		
		for(Enterprise enterprise: enterprises) {
			String enterpriseName = enterprise.getName();
			Integer enterpriseId = enterprise.getId();
			for(Activity activity : enterprise.getActivities()) {
				Integer activityId = activity.getId();
				String activityDescription = activity.getDescription();
				for(CCC ccc : activity.getCccs()) {
					if(ccc.getEmployees().isEmpty())
						continue;
					
					CCCInfo cccInfo = new CCCInfo();
					cccInfo.setCccId(ccc.getId());
					cccInfo.setCcc(ccc.getCode());
					cccInfo.setCccAccount(ccc.getCode());
					cccInfo.setCccRegimeCode(ccc.getRegime());
					cccInfo.setTypeStr(ccc.getRegime());
					cccInfo.setGeozone(ccc.getGeozone());
					cccInfo.setType(ccc.getType());
					cccInfo.setActivityId(activityId);
					cccInfo.setActivityDescription(activityDescription);
					cccInfo.setUseByContracts(ccc.getEmployees().isEmpty() ? false : true);
					cccInfo.setEnterpriseDesciption(enterpriseName);
					cccInfo.setEnterpriseId(enterpriseId);
					
					enterpriseCCCs.add(cccInfo);
					allEnterpriseCCCs.add(cccInfo);
				}
			}
		}
		
	}

	public List<CCCInfo> getEnterpriseCCCs() {
		return enterpriseCCCs;
	}

	public void setEnterpriseCCCs(List<CCCInfo> enterpriseCCCs) {
		this.enterpriseCCCs = enterpriseCCCs;
	}

	public EnterpriseCCCFilter getEnterpriseCCCFilter() {
		return enterpriseCCCFilter;
	}

	public void setEnterpriseCCCFilter(EnterpriseCCCFilter enterpriseCCCFilter) {
		this.enterpriseCCCFilter = enterpriseCCCFilter;
	}

	public HashMap<Integer, String> getEnterprisesMap() {
		return enterprisesMap;
	}

	public void setEnterprisesMap(HashMap<Integer, String> enterprisesMap) {
		this.enterprisesMap = enterprisesMap;
	}

	public void filterEnterpriseCCCListByEnterprise(Integer enterpriseId) {
		List<CCCInfo> newEnterpriseCCCs = new ArrayList<CCCInfo>();
		for(CCCInfo cccInfo :  getEnterpriseCCCs().size() != 0 ? getEnterpriseCCCs() : this.allEnterpriseCCCs) {
			if(cccInfo.getEnterpriseId() == enterpriseId || cccInfo.getEnterpriseId().equals(enterpriseId)) {
				newEnterpriseCCCs.add(cccInfo);
			}
		}

		setEnterpriseCCCs(newEnterpriseCCCs);

	}
	
	public void filterEnterpriseCCCListByEnterprise(List<Integer> enterprisesIds) {
		List<CCCInfo> newEnterpriseCCCs = new ArrayList<CCCInfo>();
		for(CCCInfo cccInfo :  getEnterpriseCCCs().size() != 0 ? getEnterpriseCCCs() : this.allEnterpriseCCCs) {
			if(enterprisesIds.contains(cccInfo.getEnterpriseId())) {
				newEnterpriseCCCs.add(cccInfo);
			}
		}

		setEnterpriseCCCs(newEnterpriseCCCs);

	}

	public void filterCRAsListByType(Byte type) {
		List<CRA> newCRAs = new ArrayList<CRA>();
		
		for(CRA cra : filterCRAs.size() != 0 ? filterCRAs : allCRAs) {
			if(cra.getCccType() == type || cra.getCccType().equals(type)) {
				newCRAs.add(cra);
			}
		}
		
		filterCRAs = newCRAs;
	}
	
	public void filterCRAListByGeozone(String geozone) {
		List<CRA> newCRAs = new ArrayList<CRA>();
		
		for(CRA cra : filterCRAs.size() != 0 ? filterCRAs : allCRAs) {
			if(cra.getCccProvince() == geozone || cra.getCccProvince().equals(geozone)) {
				newCRAs.add(cra);
			}
		}
		
		filterCRAs = newCRAs;
	}

	public void resetEnterpriseCCCList() {
		this.enterpriseCCCs = allEnterpriseCCCs;
	}
	
	public void resetCRAsList() {
		this.filterCRAs = allCRAs;
	}
	
	private void initCRAs(List<CRA> dbCRAs) {
		this.allCRAs.clear();
		this.filterCRAs.clear();
		this.cras.clear();
		this.crasRectif.clear();
		
		this.allCRAs.addAll(dbCRAs);
		this.filterCRAs.addAll(dbCRAs);
		
		for(CRA cra : dbCRAs){
			if(cra.getType() == "N") {
				cras.add(cra);
			} else if(cra.getType() == "R") {
				crasRectif.add(cra);
			}
		}
	}

	public List<CRA> getAllCRAs() {
		return allCRAs;
	}
	
	public List<CRA> getFilteredCRAs() {
		return filterCRAs;
	}

	public List<CRA> getCras() {
		return cras;
	}

	public List<CRA> getCrasRectif() {
		return crasRectif;
	}
	
	public void filterCrasByDates(Date startDate, Date endDate) {
		List<CRA> newCRAs = new ArrayList<CRA>();
		for(CRA cra : filterCRAs.size() != 0 ? filterCRAs : allCRAs) {
			if( (cra.getDate().after(startDate) || cra.getDate().equals(startDate)) &&
				(cra.getDate().before(endDate) || cra.getDate().equals(endDate)) ) {
				newCRAs.add(cra);
			}
		}
		
		filterCRAs = newCRAs;
	}

//	public List<Integer> getEnterprisesIds(String pattern) {
//		List<Integer> ids = new ArrayList<Integer>();
//		
//		for(CCCInfo cccInfo : this.allEnterpriseCCCs) {
//			if(StringUtils.containsIgnoreCase(cccInfo.getEnterpriseDesciption(), pattern))
//				ids.add(cccInfo.getEnterpriseId());
//		}
//		return ids;
//	}
		
}
