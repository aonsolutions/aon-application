package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DomainLinked;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class DomainLinkedDAO {

	private static final String AON_DOMAIN = "AON_DOMAIN";
	
	public static List<DomainLinked> getList(AONContext ctx, Integer registry) {
		Map<Integer, DomainLinked> map = new HashMap<>();
		RegistryOldDAO.getRegistryAddInfoStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getRegistryProperty().eq(registry))
			.and(f.getAttributeProperty().like("AON_DOMAIN%"))
		).forEach(r -> {
			String[] split = r.getAttribute().substring(10).split("_");
			Integer key = AonNumberUtils.toint(split[0]);
			if(!map.containsKey(key)) map.put(key, new DomainLinked());
			if("ID".equalsIgnoreCase(split[1])) {
				map.get(key).setId(AonNumberUtils.toInteger(r.getValue()));
			} else if("NAME".equalsIgnoreCase(split[1])) {
				map.get(key).setName(r.getValue());
			} else if("SCHEMA".equalsIgnoreCase(split[1])) {
				map.get(key).setSchema(r.getValue());
			} else if("TYPE".equalsIgnoreCase(split[1])) {
				map.get(key).setType(r.getValue());
			}
		});
		
		return new LinkedList<>(map.values());
	}
	
	public static DomainLinked save(AONContext ctx, DomainLinked domainLinked) {
		RegistryAddInfo addInfo = new RegistryAddInfo()
				.setRegistry(domainLinked.getRegistry())
				.setDomain(ctx.getDomainId())
				.setAttribute(AON_DOMAIN + domainLinked.getIndex() + "_NAME")
				.setValue(domainLinked.getName())
				.setDate(new Date());
		RegistryOldDAO.insertRegistryAddInfo(ctx, addInfo);
		
		RegistryAddInfo addInfo1 = new RegistryAddInfo()
				.setRegistry(domainLinked.getRegistry())
				.setDomain(ctx.getDomainId())
				.setAttribute(AON_DOMAIN + domainLinked.getIndex() + "_ID")
				.setValue(domainLinked.getId().toString())
				.setDate(new Date());
		RegistryOldDAO.insertRegistryAddInfo(ctx, addInfo1);
		
		RegistryAddInfo addInfo2 = new RegistryAddInfo()
				.setRegistry(domainLinked.getRegistry())
				.setDomain(ctx.getDomainId())
				.setAttribute(AON_DOMAIN + domainLinked.getIndex() + "_SCHEMA")
				.setValue(domainLinked.getSchema())
				.setDate(new Date());
		RegistryOldDAO.insertRegistryAddInfo(ctx, addInfo2);

		RegistryAddInfo addInfo3 = new RegistryAddInfo()
				.setRegistry(domainLinked.getRegistry())
				.setDomain(ctx.getDomainId())
				.setAttribute(AON_DOMAIN + domainLinked.getIndex() + "_TYPE")
				.setValue(domainLinked.getType())
				.setDate(new Date());
		RegistryOldDAO.insertRegistryAddInfo(ctx, addInfo3);

		return domainLinked;
	}
	
}
