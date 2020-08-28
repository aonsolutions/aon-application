package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
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
	
	private Integer domainId;
	
	
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
		
		this.domainId = -1;
		
	}
	
//	public void getEnterprises(Consumer<List<Enterprise>> success, Consumer<Throwable> failure){
//		
//		impl.getEnterprises(new AsyncCallback<List<Enterprise>>() {
//			
//			@Override
//			public void onSuccess(List<Enterprise> enterprises) {
//				initEnterpriseMap(enterprises);
//				getEnterprisesCCCInfo(s -> {
//					success.accept(enterprises);
//				}, 
//				f -> {});
//			}
//
//			@Override
//			public void onFailure(Throwable caught) { }
//		});
//		
//	}
	
	public void getEnterprisesCCCInfo(long findPeriodTime, Consumer<List<CCCInfo>> success, Consumer<Throwable> failure){
		
		impl.getEnterprisesCCCInfo(findPeriodTime, new AsyncCallback<List<CCCInfo>>() {
			
			@Override
			public void onSuccess(List<CCCInfo> enterprisesCCCInfo) {
//				initEnterpriseMap(enterprisesCCCInfo);
				enterpriseCCCs.clear();
				allEnterpriseCCCs.clear();
				enterpriseCCCs.addAll(enterprisesCCCInfo);
				allEnterpriseCCCs.addAll(enterprisesCCCInfo);
				success.accept(enterprisesCCCInfo);
				
//				getCRAs(
//					s -> {
//						success.accept(enterprisesCCCInfo);
//					}, 
//					f -> {});	
			}
	
			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}

	
//	public void getEnterpriseCCCs(Consumer<List<Enterprise>> success, Consumer<Throwable> failure){
//		
//		impl.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {
//			
//			@Override
//			public void onSuccess(List<Enterprise> enterprises) {
//				initEnterpriseMap(enterprises);
//				initEnterpriseCCCs(enterprises);
//				getCRAs(
//					s -> {
//						success.accept(enterprises);
//					}, 
//					f -> {});	
//			}
//
//			@Override
//			public void onFailure(Throwable caught) { }
//		});
//		
//	}
	
	public void getCRAs(Consumer<List<CRA>> success, Consumer<Throwable> failure){
		
		impl.getCRAs(new AsyncCallback<List<CRA>>() {
			
			@Override
			public void onSuccess(List<CRA> dbCRAs) {
				initCRAs(dbCRAs);
				getDomainId(s-> {
					success.accept(dbCRAs);
				}, f -> {});
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void getDomainId(Consumer<Integer> success, Consumer<Throwable> failure){
		
		impl.getDomain(new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer domain) {
				domainId = domain;
				success.accept(domainId);
			}
	
			@Override
			public void onFailure(Throwable caught) { }
		});
		
	}
	
	public void createNewCRA (Date startDate, ArrayList<String> cccList, ArrayList<Integer> cccIdList, Integer cccId, String type, Consumer<String> success, Consumer<Throwable> failure){
		impl.createNewCRA(startDate.getTime(), cccList, cccIdList, cccId, type, new AsyncCallback<String>() {
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
	
	public Integer getDomainId() {
		return this.domainId;
	}

	public void filterCras(Date startDate, Date endDate, String geozoneName, Byte cccType) {
		List<CRA> newCRAs = new ArrayList<CRA>();
		List<CRA> auxListCRAs = new ArrayList<CRA>();
		
		// NOT FILTER BY DATE CAUSE THE TABLE CAN BE ORDERED BY PERIOD
		
		for(CRA cra : allCRAs) {
			if("-" == geozoneName || "-".equals(geozoneName)) {
				auxListCRAs.add(cra);
				continue;
			}
			
			if(cra.getCccProvince() == geozoneName || cra.getCccProvince().equals(geozoneName))
				auxListCRAs.add(cra);
			
		}
		
		for(CRA cra : auxListCRAs) {
			if((byte) -1 == cccType || cccType.equals((byte)-1)) {
				newCRAs.add(cra);
				continue;
			}
			
			if(cra.getCccType() == cccType || cra.getCccType().equals(cccType))
				newCRAs.add(cra);
			
		}
		
		filterCRAs = newCRAs;
	}
	
	public void filterEmitedCCC(Date date) {
		List<CCCInfo> emitedCCCs = new ArrayList<CCCInfo>();
		DateUtils.resetTime(date);
		
		for(CCCInfo cccInfo : allEnterpriseCCCs) {
			if(cccInfo.getCRADates().contains(date))
				emitedCCCs.add(cccInfo);
		}
		
		setEnterpriseCCCs(emitedCCCs);
	}
	
	public void filterPenddingCCC(Date date) {
		List<CCCInfo> peddingCCCs = new ArrayList<CCCInfo>();
		DateUtils.resetTime(date);
		
		for(CCCInfo cccInfo : allEnterpriseCCCs) {
			if(!cccInfo.getCRADates().contains(date))
				peddingCCCs.add(cccInfo);
		}
		
		setEnterpriseCCCs(peddingCCCs);
	}

	public void removeCCCCRADate(CRA cra) {
		Date date = cra.getCreationDate();
		DateUtils.resetTime(date);
		
		for(CCCInfo cccInfo : cra.getIncludeCCCs()) {
			for(CCCInfo enterpriseCCC : allEnterpriseCCCs) {
				if(cccInfo.getCccId() == enterpriseCCC.getCccId() || cccInfo.getCccId().equals(enterpriseCCC.getCccId())) {
					List<Date> craDatesAux = new ArrayList<Date>();
					craDatesAux.addAll(enterpriseCCC.getCRADates());
					for(Date craDate : craDatesAux)
						if(date == craDate || date.equals(craDate) || date.getTime() == craDate.getTime())
							enterpriseCCC.getCRADates().remove(craDate);
					
				}
			}
		}
	}
		
}
