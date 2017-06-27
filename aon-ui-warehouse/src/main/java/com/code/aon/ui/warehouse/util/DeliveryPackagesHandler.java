package com.code.aon.ui.warehouse.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.file.seres.util.DeliveryPackages;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.Pair;

public class DeliveryPackagesHandler implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryPackagesHandler.class);

	private IController controller;
	
	private boolean showPackages;
	
	private Attach dataAttach;
	
	private List<ITransferObject> detailList;
	
	
	private List<Pair<DeliveryDetail, DeliveryDetail>> packagesContainerList;
	
	private List<Pair<DeliveryDetail, DeliveryDetail>> linesPackageList;

	private SerializableListDataModel packagesContainerModel;
	
	private SerializableListDataModel linesPackageModel;
	
	
	public DeliveryPackagesHandler(IController controller) {
		this.controller = controller;
	}
	
	public boolean isShowPackages() {
		return this.showPackages;
	}
	
	public void setShowPackages(boolean showPackages) {
		this.showPackages = showPackages;
	}
	
	public Attach getDataAttach(){
		return dataAttach;
	}
	
	public String getDataAttachValue(){
		return new String(dataAttach.getData());
	}
	
	public void setDataAttachValue(String data){
		dataAttach.setData(data.getBytes());
	}
	
	public List<ITransferObject> getDetailList() {
		return detailList;
	}
	
	public int getLinesCount(){
		return detailList.size();
	}
	
	public boolean isPackagesInRemark() {
		Delivery delivery = (Delivery) controller.getTo();
		String remarks = delivery.getRemarks().replaceAll("\r|\n", "");
		return remarks!=null
				&& (remarks.matches(".*\\[ENV=\\d{1,3};CONT=\\d{1,3}\\].*")
				|| remarks.matches(".*\\[ENV=\\d{1,3};LIN=\\d{1,3}\\].*"));
	}
	
	
	public SerializableListDataModel getPackagesContainerModel(){
		if(packagesContainerModel==null){
			packagesContainerModel = new SerializableListDataModel(packagesContainerList);
		}
		return packagesContainerModel;
	}
	
	public SerializableListDataModel getLinesPackageModel(){
		if(linesPackageModel==null){
			linesPackageModel = new SerializableListDataModel(linesPackageList);
		}
		return linesPackageModel;
	}
	

	public void init() throws ManagerBeanException {
		setShowPackages(isPackagesDefined());
		packagesContainerList = null;
		linesPackageList = null;
		packagesContainerModel = null;
		linesPackageModel = null;
		dataAttach = null;
	}
	
	public void onLoadPackages(ActionEvent event) {
		Delivery delivery = (Delivery) controller.getTo();
		detailList = delivery.getDetailList();
		detailList = detailList.stream()
				.map(to -> (DeliveryDetail)to)
				.sorted(Comparator.comparingInt(DeliveryDetail::getLine))
				.collect(Collectors.toList());
		
//		dataAttach = obtainPackageDataAttach(delivery);
		dataAttach = DeliveryPackages.obtainPackageDataAttach(
				AonUtil.getDomainName(),
				delivery.getDomain(),
				AonUtil.getRemoteUser(), delivery.getId());
		String data = dataAttach!=null && dataAttach.getData()!=null?new String(dataAttach.getData()):"";
//		value = value.substring(value.indexOf("["));
		Map<Integer, List<Integer>> containerMap = DeliveryPackages.loadContainerMap(data);
		Map<Integer, List<Integer>> linesMap = DeliveryPackages.loadLinesMap(data);
//		System.out.println("C: "+containerMap);
//		System.out.println("L: "+linesMap);
		
		// TODO show package info in window
//		Collection<Integer> level1List = DeliveryPackages.loadLevel1List(data);
//		System.out.println("L1: "+level1List);
//		Map<Integer, List<Integer>> level2Map = DeliveryPackages.loadLevel2Map(data);
//		System.out.println("L2: "+level2Map.keySet()+" | "+level2Map);
//		Map<Integer, List<Integer>> level3Map = DeliveryPackages.loadLevel3Map(data);
//		System.out.println("L3: "+level3Map.keySet()+" | "+level3Map);
		
		packagesContainerList = new LinkedList<Pair<DeliveryDetail,DeliveryDetail>>();
		for(int key: containerMap.keySet()) {
			for(int id: containerMap.get(key)) {
				DeliveryDetail first = detailList.size()>=key?
						(DeliveryDetail)detailList.get(key-1):new DeliveryDetail();
				DeliveryDetail second = detailList.size()>=id?
						(DeliveryDetail)detailList.get(id-1):new DeliveryDetail();
				packagesContainerList.add(new Pair<>(first, second));
			}
		}
		
		linesPackageList = new LinkedList<Pair<DeliveryDetail,DeliveryDetail>>();
		for(int key: linesMap.keySet()) {
			for(int id: linesMap.get(key)) {
				DeliveryDetail first = detailList.size()>=key?
						(DeliveryDetail)detailList.get(key-1):new DeliveryDetail();
				DeliveryDetail second = detailList.size()>=id?
						(DeliveryDetail)detailList.get(id-1):new DeliveryDetail();
				linesPackageList.add(new Pair<>(first, second));
			}
		}
		
		packagesContainerModel = null;
		linesPackageModel = null;
	}
	
//	private Attach obtainPackageDataAttach(Delivery delivery) {
//		if (delivery != null && delivery.getId() != null) {
//			Attach attach = AON.getAttach(
//					AonUtil.getDomainName(),
//					delivery.getDomain(),
//					AonUtil.getRemoteUser(),
//					f -> f.getDomainProperty()
//							.eq(delivery.getDomain())
//							.and(f.getSourceTypeProperty().eq(
//									DataAttachSource.DELIVERY.value()))
//							.and(f.getSourceBatchProperty()
//									.eq(delivery.getId())), AttachType.DATA,
//					true);
//			attach.setAttachType(AttachType.DATA);
//			return attach;
//		}
//		return null;
//	}

	public boolean isPackagesDefined() throws ManagerBeanException {
		showPackages = false;
		Delivery delivery = (Delivery) controller.getTo();
		if (delivery != null && delivery.getId() != null) {
			Attach attach = AON.getAttach(
					AonUtil.getDomainName(),
					delivery.getDomain(),
					AonUtil.getRemoteUser(),
					f -> f.getDomainProperty()
							.eq(delivery.getDomain())
							.and(f.getSourceTypeProperty().eq(
									DataAttachSource.DELIVERY.value()))
							.and(f.getSourceBatchProperty()
									.eq(delivery.getId())), AttachType.DATA,
					true);
			showPackages = attach!=null && attach.getId()!=null;
		}
		return showPackages;
	}
	
//	private boolean isPackageItem(Item item) {
//		return item != null && item.getSerialNumber() == null
//				&& item.getSerialDate() == null;
//	}
	
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
	
//	public List<DeliveryDetail> getContainerList() {
//		List<DeliveryDetail> list = new LinkedList<>();
//		for(ITransferObject to: detailList) {
//			DeliveryDetail detail = (DeliveryDetail) to;
//			if(isPackageItem(detail.getItem())
//					&& getFormatName(detail).toLowerCase().contains("palet")){
//				list.add(detail);
//			}
//		}
//		return list;
//	}
//	
//	public List<DeliveryDetail> getPackageList() {
//		List<DeliveryDetail> list = new LinkedList<>();
//		for(ITransferObject to: detailList) {
//			DeliveryDetail detail = (DeliveryDetail) to;
//			if(isPackageItem(detail.getItem())
//					&& !getFormatName(detail).toLowerCase().contains("palet")){
//				list.add(detail);
//			}
//		}
//		return list;
//	}
	
//	private Map<Integer, List<Integer>> loadPackagesContainerMap(String data) {
//		String regex = "\\[(ENV=\\d{1,3});(CONT=\\d{1,3})\\]";
//		String keyPrefix = "CONT=", valuePrefix = "ENV=";
//		int keyGroup = 2, valueGroup = 1;
//		return obtainPackagesMap(data, getContainerList(),
//				regex, keyPrefix, valuePrefix, keyGroup, valueGroup);
//	}
//	
//	private Map<Integer, List<Integer>> loadLinesPackageMap(String data) {
//		String regex = "\\[(ENV=\\d{1,3});(LIN=\\d{1,3})\\]";
//		String keyPrefix = "ENV=", valuePrefix = "LIN=";
//		int keyGroup = 1, valueGroup = 2;
//		return obtainPackagesMap(data, getPackageList(),
//				regex, keyPrefix, valuePrefix, keyGroup, valueGroup);
//	}
	
//	private Map<Integer, List<Integer>> obtainPackagesMap(String data,
//			List<DeliveryDetail> packageList, String regex,
//			String keyPrefix, String valuePrefix, int keyGroup, int valueGroup) {
//		Map<Integer, List<Integer>> map = new LinkedHashMap<>();
//		if (data != null && !"".equals(data)) {
//			Pattern pattern = Pattern.compile(regex);
//			Matcher matcher = pattern.matcher(data);
//			while (matcher.find()) {
//				String _key = matcher.group(keyGroup).replaceFirst(keyPrefix, "");
//				String _value = matcher.group(valueGroup).replaceFirst(valuePrefix, "");
//				Integer key = Integer.parseInt(_key);
//				Integer value = Integer.parseInt(_value);
//				List<Integer> list = new LinkedList<>();
//				list.add(value);
//				if (map.containsKey(key))
//					map.get(key).addAll(list);
//				else
//					map.put(key, list);
//			}
//		}
//		map = map
//				.entrySet()
//				.stream()
//				.sorted(Map.Entry.comparingByKey())
//				.collect(
//						Collectors.toMap(Map.Entry::getKey,
//								Map.Entry::getValue, (x, y) -> {
//									throw new AssertionError();
//								}, LinkedHashMap::new));
//		return map;
//	}

	public void onPackagesContainerLineChange(ActionEvent event) {
		if(getPackagesContainerModel().isRowAvailable()){
			Pair<DeliveryDetail, DeliveryDetail> pair = (Pair<DeliveryDetail, DeliveryDetail>) getPackagesContainerModel().getRowData();
			DeliveryDetail left = pair.getLeft();
			DeliveryDetail right = pair.getRight();
			if(left.getLine()!=null){
				DeliveryDetail detail = (DeliveryDetail) detailList.get(left.getLine()-1);
				left.setId(detail.getId());
				left.setDescription(detail.getDescription());
				left.setQuantity(detail.getQuantity());
			}
			if(right.getLine()!=null){
				DeliveryDetail detail = (DeliveryDetail) detailList.get(right.getLine()-1);
				right.setId(detail.getId());
				right.setDescription(detail.getDescription());
				right.setQuantity(detail.getQuantity());
			}
		}
	}

	public void onLinesPackageLineChange(ActionEvent event) {
		onPackagesContainerLineChange(event);
	}
	
	public void onCreateAttachFromRemarks(ActionEvent event) {
		Delivery delivery = (Delivery) controller.getTo();
		String data = delivery.getRemarks();
		if(data.replaceAll("\r|\n", "").matches(".*\\[ENV=.*\\].*")){
			data = data.substring(data.indexOf("["));
			data = data.substring(0, data.lastIndexOf("]")+1);
			
			dataAttach.setDomain(new Domain().setId(delivery.getDomain()));
			dataAttach.setSourceType(DataAttachSource.DELIVERY.value());
			dataAttach.setSourceBatch(delivery.getId());
			dataAttach.setAttachType(AttachType.DATA);
			dataAttach.setType((byte)0);
			dataAttach.setData(data.getBytes());
			dataAttach.setMimeType(MimeType.TXT);
			
			saveOrUpdate();
			
			delivery.setRemarks(delivery
					.getRemarks()
					.substring(0, delivery.getRemarks().indexOf("["))
					.concat(delivery.getRemarks().substring(
							delivery.getRemarks().lastIndexOf("]") + 1,
							delivery.getRemarks().length())));
			
			onLoadPackages(event);
		}
	}
	
	public void saveOrUpdate() {
		if(isShowPackages()){
			Delivery delivery = (Delivery) controller.getTo();
			if(delivery!=null && delivery.getId()!=null){
				if(dataAttach.getId()==null){
					AON.insertAttach(AonUtil.getDomainName(), delivery.getDomain(),
							AonUtil.getRemoteUser(), dataAttach);
				} else {
					AON.updateAttach(AonUtil.getDomainName(), delivery.getDomain(),
							AonUtil.getRemoteUser(), dataAttach);
				}
			}
		}
	}
	
	public void removePackages() {
		Delivery delivery = (Delivery) controller.getTo();
//		Attach attach = obtainPackageDataAttach(delivery);
		Attach attach = DeliveryPackages.obtainPackageDataAttach(
				AonUtil.getDomainName(),
				delivery.getDomain(),
				AonUtil.getRemoteUser(), delivery.getId());
		if (delivery != null && delivery.getId() != null) {
			if (attach != null && attach.getId() != null) {
				AON.deleteAttach(AonUtil.getDomainName(), delivery.getDomain(),
						AonUtil.getRemoteUser(),
						f -> f.getIdProperty().eq(attach.getId()),
						AttachType.DATA);
			}
		}
	}
	
}