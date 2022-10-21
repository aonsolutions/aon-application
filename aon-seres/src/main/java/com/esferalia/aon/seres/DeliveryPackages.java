package com.esferalia.aon.seres;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class DeliveryPackages {

	
	public static Attach obtainPackageDataAttach(String domainName, int domainId, String user, Integer deliveryId) {
		if (deliveryId != null) {
			Attach attach = AON.getAttach(
					domainName,
					domainId,
					user,
					f -> f.getDomainProperty()
							.eq(domainId)
							.and(f.getSourceTypeProperty().eq(
									DataAttachSource.DELIVERY.value()))
							.and(f.getSourceBatchProperty()
									.eq(deliveryId)), AttachType.DATA,
					true);
			attach.setAttachType(AttachType.DATA);
			if(attach != null && !attach.isEmpty() && attach.getData() == null && !AonStringUtils.isBlank(attach.getDriveId())) {
				DomainGserviceaccount g = AON.getDomainGserviceaccount(domainName, domainId, user);
				Drive drive = AonDrive.getInstace().serviceInitialize(g);
				attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
			}
			return attach;
		}
		return null;
	}
	
	private static Collection<Integer> loadLevel1List(String data) {
		Map<Integer, List<Integer>> containerMap = loadContainerMap(data);
		Map<Integer, List<Integer>> linesMap = loadLinesMap(data);
		Collection<Integer> c = CollectionUtils.subtract(linesMap.keySet(), containerMap.keySet());
		containerMap.values().forEach( list -> {
			c.removeAll(CollectionUtils.subtract(list, c));
		});
		return CollectionUtils.union(containerMap.keySet(), c);
	}
	
	public static Map<Integer, List<Integer>> loadLevel1Map(String data, List<DeliveryDetail> detailList) {
		Collection<Integer> level1List =  loadLevel1List(data);
		Map<Integer, List<Integer>> level1Map = new LinkedHashMap<>();
		if(detailList!=null){
			level1List.forEach(id->{
				DeliveryDetail packageLine = (DeliveryDetail) detailList.get(id-1);
				
				List<Integer> packageList = new LinkedList<>();
				packageList.add(id);
				int itemId = packageLine.getItem().getId();
				if (level1Map.containsKey(itemId))
					level1Map.get(itemId).addAll(packageList);
				else
					level1Map.put(itemId, packageList);
			});
		}
		return level1Map;
	}
	
	public static Map<Integer, List<Integer>> loadLevel2Map(String data) {
		Collection<Integer> containerList =  loadLevel1List(data);
		Map<Integer, List<Integer>> containerMap = loadContainerMap(data);
		Map<Integer, List<Integer>> linesMap = loadLinesMap(data);
		
		
		Map<Integer, List<Integer>> level2Map = new LinkedHashMap<>();
		containerList.forEach(id->{			
			if(containerMap.containsKey(id)){
				level2Map.put(id, containerMap.get(id));
			} else if(linesMap.containsKey(id)){
				level2Map.put(id, linesMap.get(id));
			} else {
				System.out.println("ERROR! package-id not found");
			}
		});
		return level2Map;
	}
	
	public static Map<Integer, List<Integer>> loadLevel3Map(String data) {
		Map<Integer, List<Integer>> containerMap = loadContainerMap(data);
		Map<Integer, List<Integer>> linesMap = loadLinesMap(data);
		
		Map<Integer, List<Integer>> level3Map = new LinkedHashMap<>();
		containerMap.keySet().forEach(key->{
			containerMap.get(key).forEach( id -> {
				if(linesMap.containsKey(key)){
					level3Map.put(id, linesMap.get(key));
				}
			});
		});
		return level3Map;
	}
	
	public static Map<Integer, List<Integer>> loadContainerMap(String data) {
		String regex = "\\[(ENV=\\d{1,3});(CONT=\\d{1,3})(;SSCC=\\w{1,})?\\]";
		String keyPrefix = "CONT=", valuePrefix = "ENV=";
		int keyGroup = 2, valueGroup = 1;
		return obtainPackagesMap(data, 
				regex, keyPrefix, valuePrefix, keyGroup, valueGroup);
	}
	
	public static Map<Integer, List<Integer>> loadLinesMap(String data) {
		String regex = "\\[(ENV=\\d{1,3});(LIN=\\d{1,3})(;SSCC=\\w{1,})?\\]";
		String keyPrefix = "ENV=", valuePrefix = "LIN=";
		int keyGroup = 1, valueGroup = 2;
		return obtainPackagesMap(data, 
				regex, keyPrefix, valuePrefix, keyGroup, valueGroup);
	}
	
	public static Map<Integer, String> loadSSCCMap(String data) {
		String regexCont = "\\[(ENV=\\d{1,3});(CONT=\\d{1,3})(;SSCC=\\w{1,})?\\]";
		String keyPrefixCont = "CONT=", ssccPrefixCont = "SSCC=";
		int keyGroupCont = 2, ssccGroupCont = 3;
		Map<Integer, String> contMap = obtainSSCCMap(data, 
				regexCont, keyPrefixCont, ssccPrefixCont, keyGroupCont, ssccGroupCont);
		
		String regexLin = "\\[(ENV=\\d{1,3});(LIN=\\d{1,3})(;SSCC=\\w{1,})?\\]";
		String keyPrefixLin = "ENV=", ssccPrefixLin = "SSCC=";
		int keyGroupLin = 1, ssccGroupLin = 3;		
		Map<Integer, String> linMap = obtainSSCCMap(data, 
				regexLin, keyPrefixLin, ssccPrefixLin, keyGroupLin, ssccGroupLin);
		
		contMap.putAll(linMap);
		return contMap;
	}
	
	private static Map<Integer, List<Integer>> obtainPackagesMap(String data,
			String regex,
			String keyPrefix, String valuePrefix, int keyGroup, int valueGroup) {
		Map<Integer, List<Integer>> map = new LinkedHashMap<>();
		if (data != null && !"".equals(data)) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(data);
			while (matcher.find()) {
				String _key = matcher.group(keyGroup).replaceFirst(keyPrefix, "").replaceAll(";", "");
				String _value = matcher.group(valueGroup).replaceFirst(valuePrefix, "").replaceAll(";", "");
				Integer key = Integer.parseInt(_key);
				Integer value = Integer.parseInt(_value);
				List<Integer> list = new LinkedList<>();
				list.add(value);
				if (map.containsKey(key))
					map.get(key).addAll(list);
				else
					map.put(key, list);
			}
		}
		map = map
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(
						Collectors.toMap(Map.Entry::getKey,
								Map.Entry::getValue, (x, y) -> {
									throw new AssertionError();
								}, LinkedHashMap::new));
		return map;
	}
	
	private static Map<Integer, String> obtainSSCCMap(String data,
			String regex,
			String keyPrefix, String ssccPrefix, int keyGroup, int ssccGroup) {
		Map<Integer, String> map = new LinkedHashMap<>();
		if (data != null && !"".equals(data)) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(data);
			while (matcher.find()) {
				String _sscc = matcher.group(ssccGroup);
				if(StringUtils.isNotBlank(_sscc)){
					String _key = matcher.group(keyGroup).replaceFirst(keyPrefix, "").replaceAll(";", "");
					_sscc = _sscc.replaceFirst(ssccPrefix, "").replaceAll(";", "");
					Integer key = Integer.parseInt(_key);
					map.put(key, _sscc);
				}
			}
		}
		map = map
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(
						Collectors.toMap(Map.Entry::getKey,
								Map.Entry::getValue, (x, y) -> {
									throw new AssertionError();
								}, LinkedHashMap::new));
		return map;
	}
	
	
}
