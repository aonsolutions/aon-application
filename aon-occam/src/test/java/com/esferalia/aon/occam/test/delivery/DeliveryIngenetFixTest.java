package com.esferalia.aon.occam.test.delivery;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class DeliveryIngenetFixTest extends AbstractOccamTest {

	@Test
	public void test() {
		// TODO
		Sales sales = AonFaker.getSales(ctx);
		int salesId = SalesDAO.insertSales(ctx, sales);
		sales.setId(salesId);
		SalesDetail salesDetail = AonFaker.getSalesDetail(ctx, sales);
		SalesDAO.insertSalesDetail(ctx, salesDetail);
		salesDetail = SalesDAO.getSalesDetail(ctx, f -> f.getSalesProperty().eq(salesId));
		
		Delivery delivery = AonFaker.getDelivery(ctx);
		delivery = DeliveryDAO.save(ctx, delivery);
		DeliveryDetail detail = AonFaker.getDeliveryDetail(ctx, delivery);
		detail.setSalesDetail(salesDetail.getId());
		DeliveryDAO.insertDeliveryDetail(ctx, detail);
		
		LinkedList<Delivery> list = new LinkedList<>();
		list.add(delivery);
		manageSalesDetail(list);
		Integer id = salesDetail.getId();
		SalesDetail s = SalesDAO.getSalesDetail(ctx, f -> f.getIdProperty().eq(id));
		System.out.println(s.getStatus());
	}
	
	
	private void manageSalesDetail(List<Delivery> deliveryList){
		deliveryList.forEach(delivery->{			
			AON.getDeliveryDetailStream(DOMAIN_NAME, DOMAIN_ID, USER,
					f->f.getDelivery().eq(delivery.getId()))
			.filter(d->d.getSalesDetail()!=null)
			.collect(Collectors.groupingBy(DeliveryDetail::getSalesDetail,
					Collectors.summingDouble(DeliveryDetail::getQuantity)))
			.forEach((salesDetailId, totalQuantity) -> {
				SalesDetail salesDetail = AON.getSalesDetailStream(DOMAIN_NAME, DOMAIN_ID, USER,
						f->f.getIdProperty().eq(salesDetailId)).findFirst().orElse(null);
				if(salesDetail!=null){
					Double delivered = salesDetail.getDelivered();
					delivered += totalQuantity;
					salesDetail.setDelivered(delivered);
					if(delivered>0.0 && delivered<=salesDetail.getQuantity()) {
						if(delivered<salesDetail.getQuantity()) {
							salesDetail.setStatus(SalesDetailStatus.PARTIAL_SETTLED);
						} else {
							salesDetail.setStatus(SalesDetailStatus.SETTLED);
						}
					}
					AON.updateSalesDetail(DOMAIN_NAME, DOMAIN_ID, USER, salesDetail);
				}
			});
		});
	}
	
//	private void manageSalesDetail(List<Delivery> deliveryList){
//		deliveryList.forEach(delivery->{			
//			Stream<DeliveryDetail> a = AON.getDeliveryDetailStream(DOMAIN_NAME, DOMAIN_ID, USER,
//					f-> f.getDelivery().eq(delivery.getId()));
//			System.out.println(a.count());
//			
//			Stream<DeliveryDetail> b = AON.getDeliveryDetailStream(DOMAIN_NAME, DOMAIN_ID, USER,
//					f-> f.getDelivery().eq(delivery.getId()));
//			b.forEach(d -> {
//				Integer salesDetailId = d.getSalesDetail();
//				Double totalQuantity = d.getQuantity();
//				if(salesDetailId != null) {
//					SalesDetail salesDetail = AON.getSalesDetailStream(DOMAIN_NAME, DOMAIN_ID, USER,
//						f->f.getIdProperty().eq(salesDetailId)).findFirst().orElse(null);
//					if(salesDetail!=null){
//						Double delivered = salesDetail.getDelivered();
//						delivered += totalQuantity;
//						salesDetail.setDelivered(delivered);
//						if(delivered>0.0 && delivered<=salesDetail.getQuantity()) {
//							if(delivered<salesDetail.getQuantity()) {
//								salesDetail.setStatus(SalesDetailStatus.PARTIAL_SETTLED);
//							} else {
//								salesDetail.setStatus(SalesDetailStatus.SETTLED);
//							}
//						}
//						AON.updateSalesDetail(DOMAIN_NAME, DOMAIN_ID, USER, salesDetail);
//					}
//				}
//			});
//		});
//	}
}
