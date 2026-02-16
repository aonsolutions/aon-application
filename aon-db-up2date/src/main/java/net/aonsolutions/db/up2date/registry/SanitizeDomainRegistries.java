package net.aonsolutions.db.up2date.registry;

import static com.esferalia.aon.jooq.tables.CommercialTracking.COMMERCIAL_TRACKING;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Offer.OFFER;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.ProjectCommercial.PROJECT_COMMERCIAL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rseller.RSELLER;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.TaskHolderWorkgroup.TASK_HOLDER_WORKGROUP;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.ProjectCommercial;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.SellerRecord;
import com.esferalia.aon.jooq.tables.records.TaskHolderRecord;

import net.aonsolutions.db.up2date.Update;

public class SanitizeDomainRegistries implements Update {

	
	public static final SanitizeDomainRegistries SANITIZE_SIG_AONSOLUTIONS_ORG = new SanitizeDomainRegistries(DOMAIN.NAME.eq("sig.aonsolutions.org"));
	
	private static final long serialVersionUID = 1L;
	
	private Condition domainCondition;
	
	private SanitizeDomainRegistries(Condition domainCondition) {
		this.domainCondition = domainCondition;
		
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		dslContext.transaction( config -> {
			config.dsl().selectFrom(DOMAIN).where(domainCondition).fetchInto(DOMAIN)
			.forEach(domain -> {
				config.dsl()
				.select()
				.from(PERSON)
				.innerJoin(REGISTRY).on(PERSON.REGISTRY.eq(REGISTRY.ID))
				.where(PERSON.DOMAIN.eq(domain.getId()).and(DSL.trim(REGISTRY.DOCUMENT).ne("")))
				.fetchInto(REGISTRY).forEach( registry -> {

					List<TaskHolderRecord> duplicateTaskHolders =
					config.dsl()
					.select()
					.from(TASK_HOLDER)
					.innerJoin(REGISTRY).on(TASK_HOLDER.REGISTRY.eq(REGISTRY.ID))
					.where(REGISTRY.DOCUMENT.eq(registry.getDocument()))
					.and(TASK_HOLDER.REGISTRY.notEqual(registry.getId()))
					.fetchInto(TASK_HOLDER);
					

					TaskHolderRecord registryTaskHolder =
					config.dsl()
					.select()
					.from(TASK_HOLDER)
					.innerJoin(REGISTRY).on(TASK_HOLDER.REGISTRY.eq(REGISTRY.ID))
					.where(TASK_HOLDER.REGISTRY.equal(registry.getId()))
					.fetchOptionalInto(TASK_HOLDER)
					.orElseGet(() -> {
						TaskHolderRecord newTaskHolder = 
						config.dsl().newRecord(TASK_HOLDER);
						
						newTaskHolder.setRegistry(registry.getId());
						newTaskHolder.setDomain(registry.getDomain());
						
						duplicateTaskHolders.stream()
						.map(TaskHolderRecord::getUserId)
						.filter(Objects::nonNull)
						.findFirst().ifPresent(newTaskHolder::setUserId);
						
						newTaskHolder.store();
						
						return newTaskHolder;
					});
					
					List<Integer> duplicateTaskHoldersIds = duplicateTaskHolders.stream().map(TaskHolderRecord::getRegistry).toList();
					

					config.dsl()
					.update(TASK_HOLDER_WORKGROUP)
					.set(TASK_HOLDER_WORKGROUP.TASK_HOLDER, registryTaskHolder.getRegistry())
					.where(TASK_HOLDER_WORKGROUP.TASK_HOLDER.in(duplicateTaskHoldersIds))
					.execute();

					config.dsl()
					.update(SELLER)
					.set(SELLER.TASK_HOLDER, registryTaskHolder.getRegistry())
					.where(SELLER.TASK_HOLDER.in(duplicateTaskHoldersIds))
					.execute();
					
					config.dsl()
					.update(SELLER)
					.set(SELLER.TASK_HOLDER, registryTaskHolder.getRegistry())
					.from(REGISTRY)
					.where(SELLER.REGISTRY.eq(REGISTRY.ID))
					.and(REGISTRY.DOCUMENT.eq(registry.getDocument()))
					.and(SELLER.REGISTRY.notEqual(registry.getId()))
					.execute();

					config.dsl()
					.delete(TASK_HOLDER)
					.where(TASK_HOLDER.REGISTRY.in(duplicateTaskHoldersIds));
					
					config.dsl()
					.delete(REGISTRY)
					.where(REGISTRY.ID.in(duplicateTaskHoldersIds));
					
					List<SellerRecord> duplicateSellers =
					config.dsl()
					.select()
					.from(SELLER)
					.where(SELLER.REGISTRY.notEqual(registry.getId()))
					.and(SELLER.TASK_HOLDER.eq(registryTaskHolder.getRegistry()))
					.fetchInto(SELLER);
					

					SellerRecord registrySeller =
					config.dsl()
					.select()
					.from(SELLER)
					.where(SELLER.REGISTRY.eq(registry.getId()))
					.and(SELLER.TASK_HOLDER.eq(registryTaskHolder.getRegistry()))
					.fetchOptionalInto(SELLER)
					.orElseGet(() -> {
						
						SellerRecord newSeller = 
						config.dsl().newRecord(SELLER);
						
						newSeller.setRegistry(registry.getId());
						newSeller.setDomain(registry.getDomain());
						newSeller.setTaskHolder(registryTaskHolder.getRegistry());
						
						duplicateSellers.stream()
						.map(SellerRecord::getStatus)
						.filter(Objects::nonNull)
						.findFirst().ifPresent(newSeller::setStatus);

						duplicateSellers.stream()
						.map(SellerRecord::getScope)
						.filter(Objects::nonNull)
						.findFirst()
						.ifPresentOrElse(
						newSeller::setScope
						, () -> config.dsl()
						.select()
						.from(SCOPE)
						.where(SCOPE.DOMAIN.eq(registry.getDomain()))
						.fetchStreamInto(SCOPE).findFirst().map(ScopeRecord::getId).ifPresent(newSeller::setScope)
						);

						duplicateSellers.stream()
						.map(SellerRecord::getCommissionType)
						.filter(Objects::nonNull)
						.findFirst().ifPresent(newSeller::setCommissionType);
						
						newSeller.store();
						
						return newSeller;
					});
					
					List<Integer> duplicateSellersIds = 
					duplicateSellers.stream().map(SellerRecord::getRegistry).toList();
					
					config.dsl()
					.update(RSELLER)
					.set(RSELLER.SELLER, registrySeller.getRegistry())
					.where(RSELLER.SELLER.in(duplicateSellersIds))
					.execute();
					
					config.dsl()
					.update(CUSTOMER_FEE)
					.set(CUSTOMER_FEE.SELLER, registrySeller.getRegistry())
					.where(CUSTOMER_FEE.SELLER.in(duplicateSellersIds))
					.execute();

					config.dsl()
					.update(INVOICE_DETAIL)
					.set(INVOICE_DETAIL.SELLER, registrySeller.getRegistry())
					.where(INVOICE_DETAIL.SELLER.in(duplicateSellersIds))
					.execute();

					config.dsl()
					.update(INVOICE)
					.set(INVOICE.SELLER, registrySeller.getRegistry())
					.where(INVOICE.SELLER.in(duplicateSellersIds))
					.execute();

					config.dsl()
					.update(OFFER)
					.set(OFFER.SELLER, registrySeller.getRegistry())
					.where(OFFER.SELLER.in(duplicateSellersIds))
					.execute();

					config.dsl()
					.update(COMMERCIAL_TRACKING)
					.set(COMMERCIAL_TRACKING.SELLER, registrySeller.getRegistry())
					.where(COMMERCIAL_TRACKING.SELLER.in(duplicateSellersIds))
					.execute();

					config.dsl()
					.update(PROJECT_COMMERCIAL)
					.set(PROJECT_COMMERCIAL.SELLER, registrySeller.getRegistry())
					.where(PROJECT_COMMERCIAL.SELLER.in(duplicateSellersIds))
					.execute();

					config.dsl()
					.update(SALES)
					.set(SALES.SELLER, registrySeller.getRegistry())
					.where(SALES.SELLER.in(duplicateSellersIds))
					.execute();

					config.dsl()
					.delete(SELLER)
					.where(SELLER.REGISTRY.in(duplicateSellersIds))
					.execute();

					config.dsl()
					.delete(SELLER)
					.where(SELLER.REGISTRY.in(duplicateTaskHoldersIds))
					.execute();
					
				});
			});

		});
	}

}
