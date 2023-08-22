package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainCRAObject {
	
	// ------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private HashMap<Integer, String> enterprisesMap;
	
	private List<CCCInfo> allEnterpriseCCCs;
	private List<CCCInfo> enterpriseCCCs;
	
	private List<CRA> allCRAs;
	private List<CRA> cras;
	
	// ------------------------------------------- Constructor
	
	public MainCRAObject() {
		super();
		
		this.enterprisesMap = new HashMap<>();
		
		this.allEnterpriseCCCs = new ArrayList<>();
		this.enterpriseCCCs = new ArrayList<>();
		
		this.allCRAs = new ArrayList<>();
		this.cras = new ArrayList<>();
	}
	
	// ------------------------------------------- DataBase Methods
	
	public void getEnterprisesCCCInfo(long findPeriodTime, Consumer<List<CCCInfo>> success, Consumer<Throwable> failure){
		
		impl.getEnterprisesCCCInfo(findPeriodTime, new AsyncCallback<List<CCCInfo>>() {
			
			@Override
			public void onSuccess(List<CCCInfo> enterprisesCCCInfo) {
				initEnterpriseMap(enterprisesCCCInfo);
				enterpriseCCCs.clear();
				allEnterpriseCCCs.clear();
				enterpriseCCCs.addAll(enterprisesCCCInfo);
				allEnterpriseCCCs.addAll(enterprisesCCCInfo);
				
				success.accept(enterprisesCCCInfo);
			}
	
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void getCRAs(long liquidDateTime ,Consumer<List<CRA>> success, Consumer<Throwable> failure){
		
		impl.getCRAs(liquidDateTime, new AsyncCallback<List<CRA>>() {
			
			@Override
			public void onSuccess(List<CRA> dbCRAs) {
				allCRAs.clear();
				cras.clear();
				allCRAs.addAll(dbCRAs);
				cras.addAll(dbCRAs);
				
				success.accept(dbCRAs);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	// ------------------------------------------- DataBase Methods (CRAs)
	
	public void createNewCRA (Date startDate, ArrayList<String> cccList, ArrayList<Integer> cccIdList, Integer cccId, String type, Consumer<Void> success, Consumer<Throwable> failure){
		impl.createNewCRA(startDate.getTime(), cccList, cccIdList, cccId, type, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);	
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
		});
	}
	
	public void checkCreateNewCRA (Date startDate, ArrayList<Integer> cccList, Consumer<Void> success, Consumer<Throwable> failure){
		impl.checkCreateNewCRA(startDate.getTime(), cccList, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);	
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
		});
	}
	
	public void deteleCRA (Integer code, Consumer<Void> success, Consumer<Throwable> failure){
		impl.deleteCRA(code, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
		});
	}
	
	public void checkIfRectificative(Date findingDate, ArrayList<Integer> selectedCCCList, Consumer<Boolean> success, Consumer<Throwable> failure) {
		impl.checkIfRectificative(findingDate, selectedCCCList, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				success.accept(result);
			}
		});
	}
	
	// ------------------------------------------- Auxiliar Methods (EnterpriseMap)
	
	public HashMap<Integer, String> getEnterprisesMap() {
		return enterprisesMap;
	}
	
	private void initEnterpriseMap(List<CCCInfo> enterprisesCCCInfo) {
		this.enterprisesMap.clear();
		enterprisesCCCInfo.forEach(cccInfo -> enterprisesMap.put(cccInfo.getEnterpriseId(), cccInfo.getEnterpriseDesciption()));
	}
	
	public List<Integer> getEnterprisesIds(String pattern) {
		return this.allEnterpriseCCCs.stream()
				.filter(cccInfo -> AonStringUtils.containsIgnoreCase(cccInfo.getEnterpriseDesciption(), pattern))
				.map(cccInfo -> cccInfo.getEnterpriseId())
				.collect(Collectors.toList());
	}
	
	// ------------------------------------------- Auxiliar Methods (CCCs)
	
	public List<CCCInfo> getEnterpriseCCCs() {
		return enterpriseCCCs;
	}

	public void filterCCC(List<Integer> enterprisesIds, int status, Date date) {
		
		this.enterpriseCCCs.clear();
		DateUtils.resetTime(date);
		
		for(CCCInfo cccInfo :  allEnterpriseCCCs) {
			if( 
				(enterprisesIds.isEmpty() || enterprisesIds.contains(cccInfo.getEnterpriseId())) &&
				(status == 0 || (status == 1 && cccInfo.getCRADates().contains(date)) || (status == 2 && !cccInfo.getCRADates().contains(date)))
			) {
				this.enterpriseCCCs.add(cccInfo);
			}
		}
		
	}
	
	// ------------------------------------------- Auxiliar Methods (CRA)
	
	public List<CRA> getCRAs() {
		return cras;
	}
	
	public void filterCras(String geozoneName, Byte cccType) {
		cras.clear();
		
		for(CRA cra : allCRAs) {
			if(
				(AonStringUtils.equalsIgnoreCase(geozoneName, "-") || craIncludeGeozone(cra, geozoneName)) &&
				(cccType == (byte) -1 || craIncludeCCCType(cra, cccType))
			)
				cras.add(cra);
		}
	}

	private boolean craIncludeGeozone(CRA cra, String geozoneName) {
		return AonStringUtils.equalsIgnoreCase(cra.getCccProvince(), geozoneName) || cra.getIncludeCCCs().stream().filter(cccInfo -> AonStringUtils.equalsIgnoreCase(cccInfo.getGeozone(), geozoneName)).count() > 0;
	}
	
	private boolean craIncludeCCCType(CRA cra, Byte cccType) {
		return cra.getCccType() == cccType || cra.getIncludeCCCs().stream().filter(cccInfo -> cccInfo.getType() == cccType).count() > 0;
	}
		
}
