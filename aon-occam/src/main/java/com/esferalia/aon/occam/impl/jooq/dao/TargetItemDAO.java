package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;

public class TargetItemDAO {
	public static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();

	public static void updateAllTargetItem(AONContext ctx, InvoiceFilter filter, boolean disable) {
		Integer domainId = ctx.getDomainId();
		Map<Integer, List<Integer>> map = getRegistryItems(ctx, filter);
		Set<Integer> registryIds = map.keySet();
		
		checkTargetExists(ctx, registryIds);
		
		Set<Integer> customerIds = CustomerDAO.getStream(ctx, f -> f.getDomainProperty().eq(domainId)).map(Customer::getId).collect(Collectors.toSet());
		
		if (disable) {
			ItemDAO.updateRItemStatus(ctx, RegistryItemStatus.INACTIVE,
					f -> f.getDomainProperty().eq(domainId)
					.and(f.getTypeProperty().eq(RegistryMode.TARGET.value()))
					.and(f.getStatusProperty().eq(RegistryItemStatus.ACTIVE.value()))
					.and(f.getRegistryProperty().in(customerIds.toArray(Integer[]::new)))
			);
		}
		for (Integer registry: registryIds) {
			Set<Integer> items = map.get(registry).stream().filter(Objects::nonNull).collect(Collectors.toSet());
			
			
			ItemDAO.updateRItemStatus(ctx, RegistryItemStatus.ACTIVE,
					f -> f.getDomainProperty().eq(domainId)
					.and(f.getRegistryProperty().eq(registry))
					.and(f.getItemProperty().in(items.toArray(Integer[]::new)))
					.and(f.getTypeProperty().eq(RegistryMode.TARGET.value())));
			
			RegistryItem[] ritemArray = items.stream().filter(Objects::nonNull).map(item -> new RegistryItem()
					.setDomain(domainId)
					.setRegistry(registry)
					.setType(RegistryMode.TARGET)
					.setStatus(RegistryItemStatus.ACTIVE)
					.setPriority(Priority.NORMAL)
					.setItem(item)).toArray(RegistryItem[]::new);
			
			ItemDAO.saveRItem(ctx, ritemArray);
		}
	}
	
	private static Map<Integer, List<Integer>> getRegistryItems(AONContext ctx, InvoiceFilter filter) {
		Map<Integer, List<Integer>> map = new LinkedHashMap<>();
		ctx.getDslContext()
		.select(INVOICE.REGISTRY, INVOICE_DETAIL.ITEM)
		.from(INVOICE)
		.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.eq(INVOICE.ID))
		.where(INVOICE_PROPERTIES.getConditions(filter))
		.and(INVOICE.TYPE.eq(InvoiceType.SALES.value()))
		.fetch()
		.stream()
		.forEach(r -> {
			Integer registry = r.getValue(INVOICE.REGISTRY);
			Integer item = r.getValue(INVOICE_DETAIL.ITEM);
			List<Integer> list = map.getOrDefault(registry, new LinkedList<>());
			list.add(item);
			map.put(registry, list);
		});
		
		return map;
	}
	
	private static void checkTargetExists(AONContext ctx, Set<Integer> registryIds) {
		Integer domainId = ctx.getDomainId();
		Set<Integer> targetIds = TargetDAO.getStream(ctx, f -> f.getIdProperty().in(registryIds.toArray(Integer[]::new)))
		.map(target -> target.getId())
		.collect(Collectors.toSet());
		
		for (Integer registryId : registryIds) {
			if (!targetIds.contains(registryId)) {
				Customer customer = CustomerDAO.get(ctx, f -> f.getDomainProperty().eq(domainId).and(f.getRegistryProperty().eq(registryId)));
				if (customer != null && customer.getId() != null) {
					CustomerDAO.saveTargetByCustomer(ctx, customer);
				}
			}
		}
		
	}
	
}
