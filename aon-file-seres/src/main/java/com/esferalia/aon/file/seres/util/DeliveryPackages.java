package com.esferalia.aon.file.seres.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;

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
		String regex = "\\[(ENV=\\d{1,3});(CONT=\\d{1,3})\\]";
		String keyPrefix = "CONT=", valuePrefix = "ENV=";
		int keyGroup = 2, valueGroup = 1;
		return obtainPackagesMap(data, 
				regex, keyPrefix, valuePrefix, keyGroup, valueGroup);
	}
	
	public static Map<Integer, List<Integer>> loadLinesMap(String data) {
		String regex = "\\[(ENV=\\d{1,3});(LIN=\\d{1,3})\\]";
		String keyPrefix = "ENV=", valuePrefix = "LIN=";
		int keyGroup = 1, valueGroup = 2;
		return obtainPackagesMap(data, 
				regex, keyPrefix, valuePrefix, keyGroup, valueGroup);
	}
	
	private static Map<Integer, List<Integer>> obtainPackagesMap(String data,
			String regex,
			String keyPrefix, String valuePrefix, int keyGroup, int valueGroup) {
		Map<Integer, List<Integer>> map = new LinkedHashMap<>();
		if (data != null && !"".equals(data)) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(data);
			while (matcher.find()) {
				String _key = matcher.group(keyGroup).replaceFirst(keyPrefix, "");
				String _value = matcher.group(valueGroup).replaceFirst(valuePrefix, "");
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
	
	
}
