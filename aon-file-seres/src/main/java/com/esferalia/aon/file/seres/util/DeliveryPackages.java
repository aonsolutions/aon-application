package com.esferalia.aon.file.seres.util;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;

public class DeliveryPackages {

	
//	private boolean isPackageItem(Item item) {
//		return item != null && item.getSerialNumber() == null
//				&& item.getSerialDate() == null;
//	}
//
//	private String getFormatName(DeliveryDetail detail) {
//		String format = "";
//		try {
//			if(detail.getItem().getProduct().getName()!=null){
//				format = detail.getItem().getProduct().getName();
//			}
//			if(detail.getItem().getProduct().getBaseItem().getPackFormatTag()!=null){
//				format = detail.getItem().getProduct().getBaseItem().getPackFormatTag().getName();
//			}
//		} catch (ManagerBeanException e) {
//			LOGGER.error("Error obtaining product format name.", e);
//		}
//		return format;
//	}

	
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
	
	
	public static Map<Integer, List<Integer>> loadPackagesContainerMap(String data) {
		String regex = "\\[(ENV=\\d{1,3});(CONT=\\d{1,3})\\]";
		String keyPrefix = "CONT=", valuePrefix = "ENV=";
		int keyGroup = 2, valueGroup = 1;
		return obtainPackagesMap(data, 
				regex, keyPrefix, valuePrefix, keyGroup, valueGroup);
	}
	
	public static Map<Integer, List<Integer>> loadLinesPackageMap(String data) {
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
