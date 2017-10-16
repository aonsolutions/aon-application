package com.code.aon.ui.warehouse.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
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
	
	private TreeNode<EdiStructureItem> ediRootNode = null;

    public TreeNode<EdiStructureItem> getEdiTreeNode() {
        if (ediRootNode == null) {
            loadEdiTree();
        }
        return ediRootNode;
    }
	
	
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
		if(dataAttach!=null && dataAttach.getData()!=null)
			return new String(dataAttach.getData());
		return null;
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
		ediRootNode = null;
	}
	
	public void onLoadPackages(ActionEvent event) {
		try {
			Delivery delivery = (Delivery) controller.getTo();
			detailList = delivery.getDetailList();
			detailList = detailList.stream()
					.map(to -> (DeliveryDetail)to)
					.sorted(Comparator.comparingInt(DeliveryDetail::getLine))
					.collect(Collectors.toList());
			
			dataAttach = DeliveryPackages.obtainPackageDataAttach(
					AonUtil.getDomainName(),
					delivery.getDomain(),
					AonUtil.getRemoteUser(), delivery.getId());
			String data = dataAttach!=null && dataAttach.getData()!=null?new String(dataAttach.getData()):"";
			Map<Integer, List<Integer>> containerMap = DeliveryPackages.loadContainerMap(data);
			Map<Integer, List<Integer>> linesMap = DeliveryPackages.loadLinesMap(data);
//			System.out.println("C: "+containerMap);
//			System.out.println("L: "+linesMap);
			
			// TODO show package info in window
//			Collection<Integer> level1List = DeliveryPackages.loadLevel1List(data);
//			System.out.println("L1: "+level1List);
//			Map<Integer, List<Integer>> level2Map = DeliveryPackages.loadLevel2Map(data);
//			System.out.println("L2: "+level2Map.keySet()+" | "+level2Map);
//			Map<Integer, List<Integer>> level3Map = DeliveryPackages.loadLevel3Map(data);
//			System.out.println("L3: "+level3Map.keySet()+" | "+level3Map);
			
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
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		packagesContainerModel = null;
		linesPackageModel = null;
	}
	
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

	private boolean isPackageItem(Item item) {
		return item != null && item.getSerialNumber() == null
				&& item.getSerialDate() == null;
	}
	
	private void loadEdiTree() {
		ediRootNode = new TreeNodeImpl<>();
		int counter = 1;

		List<EdiStructureItem> list = getEdiStructureList();
		TreeNode<EdiStructureItem> parentNode = null, childNode = null, paletNode = null, caseNode = null;
		for (EdiStructureItem edi : list) {
			if (parentNode == null || edi.getLevel() == 0) {
				parentNode = ediRootNode;
				childNode = paletNode = new TreeNodeImpl<>();
				counter = edi.getLine();
			} else if (edi.getLevel() == 1 && edi.getLine() != null) {
				parentNode = paletNode;
				childNode = caseNode = new TreeNodeImpl<>();
				counter = edi.getLine();
			} else {
				parentNode = edi.getLevel() == 1 ? paletNode : caseNode;
				childNode = new TreeNodeImpl<>();
				Iterator<Entry<Object, TreeNode<EdiStructureItem>>> iterator = parentNode.getChildren();
				counter = 0;
				while (iterator.hasNext()) {
					counter++;
					iterator.next();
				}
			}
			childNode.setData(edi);
			parentNode.addChild(new Integer(counter), childNode);
		}
	}
	
	public List<EdiStructureItem> getEdiStructureList() {
		List<EdiStructureItem> list = new ArrayList<>();
		if(detailList!=null && dataAttach!=null && dataAttach.getData()!=null){
			List<DeliveryDetail> detailList = this.detailList.stream()
					.map(to -> (DeliveryDetail)to)
					.sorted((d1, d2)->d1.getLine().compareTo(d2.getLine()))
					.collect(Collectors.toList());
			String packageData = new String(dataAttach.getData());
			
			Map<Integer, List<Integer>> level1Map = DeliveryPackages.loadLevel1Map(packageData, detailList);
			Map<Integer, List<Integer>> level2Map = DeliveryPackages.loadLevel2Map(packageData);
			Map<Integer, List<Integer>> level3Map = DeliveryPackages.loadLevel3Map(packageData);
			Map<Integer, String> ssccMap = DeliveryPackages.loadSSCCMap(packageData);
			
			int mainPackageLine = 0;
			int packageLine = 0;
			int mainPackageSize = 0;
			for(Integer level1Key: level1Map.keySet()){
				List<Integer> packageLineList = level1Map.get(level1Key);
				
				// MAIN-PACKAGE
				mainPackageSize = (int)detailList.stream()
						.filter(detail->packageLineList.contains(detail.getLine()))
						.mapToDouble(DeliveryDetail::getQuantity).sum();
				mainPackageLine = ++packageLine;
				DeliveryDetail level1Detail = (DeliveryDetail) detailList.get(level1Map.get(level1Key).get(0)-1);
				EdiStructureItem mainPackage = new EdiStructureItem(mainPackageLine, null, 0, mainPackageSize, null, level1Detail);
				list.add(mainPackage);
				
				// SUB-PACKAGE OR PRODUCT OVER MAIN-PACKAGE
				for(Integer level2Key: level2Map.keySet()){
					if(packageLineList.contains(level2Key)){
						List<Integer> level2LineList = new LinkedList<>(level2Map.get(level2Key));
						for(int level2LineId: level2LineList){
							DeliveryDetail level2Detail = (DeliveryDetail) detailList.get(level2LineId-1);
							if(!isPackageItem(level2Detail.getItem())) {
								EdiStructureItem mainPackageItem = new EdiStructureItem(null, mainPackageLine, 1, (int)level2Detail.getQuantity(), null, level2Detail);
								list.add(mainPackageItem);
							} else {
								EdiStructureItem subPackage = new EdiStructureItem(++packageLine, mainPackageLine, 1, (int)level2Detail.getQuantity(), ssccMap.get(level2Key), level2Detail);
								list.add(subPackage);
								
								// PRODUCT OVER SUB-PACKAGE, IF EXIST
								List<Integer> level3LineList = new LinkedList<>(level3Map.get(level2Detail.getLine()));
								for(int level3LineId: level3LineList){
									DeliveryDetail level3Detail = (DeliveryDetail) detailList.get(level3LineId-1);
									
									int quantity = 0;
									
//									quantity = (int) (level3Detail.getQuantity()
//											/ level3Detail.getItem().getPackMeasurement()
//											/ level3Detail.getItem().getPackUnits());
									quantity = (int) (level3Detail.getItem().getPackUnits());
									
//									quantity = (int) (level3Detail.getQuantity()
//											/ level3Detail.getItem().getPackMeasurement());
//									quantity = (int) (level3Detail.getItem().getPackMeasurement());
									
									quantity *= subPackage.getQuantity();
									
									EdiStructureItem subPackageItem = new EdiStructureItem(null, null, 2, quantity, null, level3Detail);
									list.add(subPackageItem);
								}
							}
						}
					}
				}
				
			}
		}
		
		return list;
	}
	
	public class EdiStructureItem implements Serializable {
		private Integer line;
		private Integer parent;
		private int level;
		private int quantity;
		private String sscc;
		private DeliveryDetail detail;
		public EdiStructureItem(Integer line, Integer parent, int level, int quantity, String sscc, DeliveryDetail detail) {
			this.line = line;
			this.parent = parent;
			this.level = level;
			this.quantity = quantity;
			this.detail = detail;
			this.sscc = sscc;
		}
		public Integer getLine() {
			return line;
		}
		public Integer getParent() {
			return parent;
		}
		public int getLevel() {
			return level;
		}
		public int getQuantity() {
			return quantity;
		}
		public DeliveryDetail getDetail() {
			return detail;
		}
		@Override
		public String toString() {
			return quantity + " "
					+ (line==null?detail.getItem().getPackUnitsTag().getName() + " / ":" x ") 
					+ detail.getDescription()
					+ (StringUtils.isNotBlank(sscc)?" (SSCC: "+sscc+")":"");
		}
		
	}
	
    
	
	
}