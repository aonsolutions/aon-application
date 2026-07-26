package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoicingGroup.INVOICING_GROUP;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectHolder.PROJECT_HOLDER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rnote.RNOTE;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CUSTOMER_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TASK_HOLDER_ALIAS;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.script.ScriptException;

import org.jooq.Condition;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.Rnote;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.SellerWorkloadParams;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.SellerWorkload;
import com.esferalia.aon.occam.api.model.registry.SellerWorkloadContent;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.SellerStatus;
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO.FeeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectHolderDAO.ProjectHolderFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RMediaPropertyDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SellerWorkloadDAO {

	// ------------------------------------------------------------------
	// Filtro de estado de cliente.
	//
	// La regla validada (fuente de la verdad = customer.status):
	//   - status 0 (activo): siempre ACTIVO.
	//   - status 1/2 (inactivo/bloqueado): se mira la ULTIMA nota tipo 6
	//     cuya descripcion coincide con el estado (contiene "Inactivo" o
	//     "Bloqueado"). Si esa nota lleva una fecha de expiracion en el
	//     comentario y AUN NO HA LLEGADO (fecha > hoy) -> ACTIVO ese dia.
	//     En caso contrario -> el estado real (INACTIVO o BLOQUEADO).
	//
	// El mapa expMap se calcula UNA sola vez por peticion (no por seller ni
	// por mes) y se consulta en memoria dentro de los bucles.
	// ------------------------------------------------------------------
	public static class StatusFilter {
		// Filtros marcados en pantalla
		public final boolean active;
		public final boolean inactive;
		public final boolean blocked;
		// true si hay que aplicar la regla de estado (customers null o 1)
		public final boolean apply;
		// registry*10 + estado(1|2) -> fecha de expiracion (o null)
		public final Map<Integer, java.sql.Date> expMap;

		public StatusFilter(boolean apply, boolean active, boolean inactive, boolean blocked,
				Map<Integer, java.sql.Date> expMap) {
			this.apply = apply;
			this.active = active;
			this.inactive = inactive;
			this.blocked = blocked;
			this.expMap = expMap;
		}
	}

	private static final Pattern EXP_DATE = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
	private static final java.sql.Date HOY = java.sql.Date.valueOf(LocalDate.now());

	private SellerWorkloadDAO() {
	}

	public static final com.esferalia.aon.jooq.tables.Registry SELLER_ALIAS = REGISTRY.as("registry_seller");

	private static final SellerPropertiesDAO SELLER_PROPERTIES = new SellerPropertiesDAO();

	public static class SellerPropertiesDAO implements SellerProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}

		protected Condition[] getConditions(SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override
		public Property<Integer> getRegistryProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.REGISTRY);
		}

		@Override
		public Property<Integer> getDomainProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.DOMAIN);
		}

		@Override
		public Property<Byte> getStatusProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.STATUS);
		}

		@Override
		public Property<Integer> getScopeProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.SCOPE);
		}

		@Override
		public Property<Integer> getCommissionTypeProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.COMMISSION_TYPE);
		}

		@Override
		public Property<Integer> getTaskHolderProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER.TASK_HOLDER);
		}

		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.ID);
		}

		@Override
		public Property<String> getDocumentProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT);
		}

		@Override
		public Property<Byte> getDocumentTypeProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT_TYPE);
		}

		@Override
		public Property<String> getDocumentCountryProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.DOCUMENT_COUNTRY);
		}

		@Override
		public Property<String> getNameProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.NAME);
		}

		@Override
		public Property<String> getAliasProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.ALIAS);
		}

		@Override
		public Property<Byte> getTypeProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.TYPE);
		}

		@Override
		public Property<String> getNationalityProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.NATIONALITY);
		}

		@Override
		public Property<Byte> getSecurityLevelProperty() {
			return new FilterDAO.PropertyDAO<>(SELLER_ALIAS.SECURITY_LEVEL);
		}

		@Override
		public Property<String> getEmailProperty() {
			return new RegistryDAO.RMediaPropertyDAO(MediaType.EMAIL);
		}
		
		@Override public Property<String> getCelullarProperty() { return new RMediaPropertyDAO(MediaType.CELLULAR); }

	}

	// ------------------------------------------------------------------
	// Carga del mapa de expiraciones (UNA sola query por peticion).
	// Clave: registry*10 + estado (1=inactivo, 2=bloqueado).
	// Valor: fecha de expiracion parseada del comentario (o null si no hay).
	// ------------------------------------------------------------------
	private static Map<Integer, java.sql.Date> loadExpirationMap(AONContext ctx, Integer[] domainIds) {
		Rnote r1 = RNOTE.as("r1");
		Rnote r2 = RNOTE.as("r2");

		// estado_nota normalizado (2 si contiene Bloqueado, 1 si contiene Inactivo)
		Field<Integer> estadoR2 = DSL
				.when(r2.DESCRIPTION.likeIgnoreCase("%Bloqueado%"), 2)
				.when(r2.DESCRIPTION.likeIgnoreCase("%Inactivo%"), 1);

		Table<?> m = DSL
				.select(r2.REGISTRY.as("reg"), estadoR2.as("estado_nota"), DSL.max(r2.ID).as("max_id"))
				.from(r2)
				.where(r2.NOTE_TYPE.eq((byte) 6))
				.and(r2.DOMAIN.in(domainIds))
				.and(r2.DESCRIPTION.likeIgnoreCase("%Inactivo%").or(r2.DESCRIPTION.likeIgnoreCase("%Bloqueado%")))
				.groupBy(r2.REGISTRY, estadoR2)
				.asTable("m");

		Field<Integer> estadoR1 = DSL
				.when(r1.DESCRIPTION.likeIgnoreCase("%Bloqueado%"), 2)
				.when(r1.DESCRIPTION.likeIgnoreCase("%Inactivo%"), 1);

		Result<Record3<Integer, Integer, String>> rows = ctx.getDslContext()
				.select(r1.REGISTRY, estadoR1.as("estado_nota"), r1.COMMENTS)
				.from(r1)
				.join(m).on(r1.ID.eq(m.field("max_id", Integer.class)))
				.where(r1.NOTE_TYPE.eq((byte) 6))
				.and(r1.DOMAIN.in(domainIds))
				.fetch();

		Map<Integer, java.sql.Date> map = new HashMap<>();
		for (Record3<Integer, Integer, String> r : rows) {
			Integer reg = r.value1();
			Integer estado = r.value2();
			if (reg == null || estado == null) {
				continue;
			}
			java.sql.Date fecha = null;
			String comments = r.value3();
			if (comments != null) {
				Matcher mt = EXP_DATE.matcher(comments);
				if (mt.find()) {
					try {
						fecha = java.sql.Date.valueOf(mt.group(1));
					} catch (IllegalArgumentException ignore) {
						// fecha malformada -> null (equivalente a STR_TO_DATE NULL)
					}
				}
			}
			map.put(reg * 10 + estado, fecha);
		}
		return map;
	}

	/** Estado efectivo: 0 activo, 1 inactivo, 2 bloqueado. Reproduce el CASE validado. */
	private static int estadoEfectivo(byte status, Integer registry, Map<Integer, java.sql.Date> expMap) {
		if (status == 0) {
			return 0;
		}
		java.sql.Date fecha = expMap.get(registry * 10 + (int) status);
		if (fecha != null && fecha.after(HOY)) {
			return 0; // inactivacion/bloqueo aun no ha llegado -> activo hoy
		}
		return status; // 1 o 2
	}

	/** true si el cliente debe incluirse segun los filtros marcados. */
	private static boolean incluir(byte status, Integer registry, StatusFilter sf) {
		if (!sf.apply) {
			return true; // sin filtro de estado
		}
		int e = estadoEfectivo(status, registry, sf.expMap);
		return (sf.active && e == 0) || (sf.inactive && e == 1) || (sf.blocked && e == 2);
	}

	public static List<SellerWorkload> getList(CloseableAONContext ctx, SellerWorkloadParams params) {

		Date start = AonDateUtils.toSql(params.getPeriodStart());
		Date end = AonDateUtils.toSql(params.getPeriodEnd());

		Integer[] domainIds = SecurityDAO.getInheritanceDomainIds(ctx);
		StatusFilter sf = getCustomerStatusCondition(ctx, params, domainIds);

		List<SellerWorkload> sellers = new ArrayList<>();

		if (params.getByProject()) {

			Condition condition = paramsProjectToCondition(ctx, params);

			SelectJoinStep<?> base = ctx.getDslContext()
					.select()
					.from(PROJECT_HOLDER)
					.join(PROJECT).on(PROJECT.ID.eq(PROJECT_HOLDER.PROJECT))
					.join(TASK_HOLDER).on(PROJECT_HOLDER.TASK_HOLDER.eq(TASK_HOLDER.REGISTRY))
					.join(TASK_HOLDER_ALIAS).on(PROJECT_HOLDER.TASK_HOLDER.eq(TASK_HOLDER_ALIAS.ID))
					.leftOuterJoin(WORKGROUP).on(PROJECT_HOLDER.WORKGROUP.eq(WORKGROUP.ID))
					.leftJoin(CUSTOMER_FEE)
						.on(CUSTOMER_FEE.PROJECT.eq(PROJECT.ID)
						.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(end))
						.and(CUSTOMER_FEE.FINAL_DATE.isNull()
							.or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(start)))
						.and(CUSTOMER_FEE.DOMAIN.in(domainIds)))
					.leftJoin(CUSTOMER)
						.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER));

			SelectHavingStep<?> select = ((SelectConditionStep<?>) base
					.where(condition)
					.and(PROJECT_HOLDER.START_DATE.le(new Timestamp(end.getTime())))
					.and(PROJECT_HOLDER.END_DATE.isNull()
						.or(PROJECT_HOLDER.END_DATE.le(new Timestamp(end.getTime())))))
					.groupBy(PROJECT_HOLDER.TASK_HOLDER);

			applyProjectOrdering(select, params);
			applyHavingCustomers(select, params);

			sellers = select.limit(params.getOffset(), params.getLimit())
					.fetch()
					.stream()
					.map(r -> {
						SellerWorkload sellerWorkload = new SellerWorkload();
						sellerWorkload.setProjectHolder(ProjectHolderFiller.build(r));
						return sellerWorkload;
					})
					.collect(Collectors.toList());

			sellers.forEach(seller ->
				getProjectCustomerAmount(ctx, seller, seller.getProjectHolder().getTaskHolder().getId(), params, sf));

		} else {

			Condition condition = paramsToCondition(ctx, params);

			SelectJoinStep<?> base = ctx.getDslContext()
					.select(SELLER.REGISTRY, SELLER_ALIAS.NAME, SELLER_ALIAS.DOCUMENT, SCOPE.DESCRIPTION)
					.from(SELLER)
					.join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
					.join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
					.leftJoin(CUSTOMER_FEE)
						.on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY)
						.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(end))
						.and(CUSTOMER_FEE.FINAL_DATE.isNull()
							.or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(start)))
						.and(CUSTOMER_FEE.DOMAIN.in(domainIds)))
					.leftJoin(CUSTOMER)
						.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER));

			SelectHavingStep<?> select = ((SelectConditionStep<?>) base
					.where(condition))
					.groupBy(SELLER.REGISTRY);

			applyOrdering(select, params);
			applyHavingCustomers(select, params);

			sellers = select.limit(params.getOffset(), params.getLimit())
					.fetch()
					.stream()
					.map(new SellerFiller())
					.collect(Collectors.toList());

			sellers.forEach(seller ->
				getCustomerAmount(ctx, seller, seller.getId(), params, sf));
		}

		return sellers;
	}

	public static Integer getListCount(CloseableAONContext ctx, SellerWorkloadParams params) {

		Date start = AonDateUtils.toSql(params.getPeriodStart());
		Date end = AonDateUtils.toSql(params.getPeriodEnd());

		Integer[] domainIds = SecurityDAO.getInheritanceDomainIds(ctx);

		if (params.getByProject()) {

			Condition condition = paramsProjectToCondition(ctx, params);

			SelectJoinStep<Record1<Integer>> base = ctx.getDslContext()
					.selectDistinct(PROJECT_HOLDER.TASK_HOLDER)
					.from(PROJECT_HOLDER)
					.join(PROJECT).on(PROJECT.ID.eq(PROJECT_HOLDER.PROJECT))
					.leftOuterJoin(WORKGROUP).on(PROJECT_HOLDER.WORKGROUP.eq(WORKGROUP.ID))
					.leftOuterJoin(TASK_HOLDER).on(PROJECT_HOLDER.TASK_HOLDER.eq(TASK_HOLDER.REGISTRY))
					.leftOuterJoin(TASK_HOLDER_ALIAS).on(PROJECT_HOLDER.TASK_HOLDER.eq(TASK_HOLDER_ALIAS.ID))
					.leftJoin(CUSTOMER_FEE)
						.on(CUSTOMER_FEE.PROJECT.eq(PROJECT.ID)
						.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(end))
						.and(CUSTOMER_FEE.FINAL_DATE.isNull()
							.or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(start)))
						.and(CUSTOMER_FEE.DOMAIN.in(domainIds)))
					.leftJoin(CUSTOMER)
						.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER));

			SelectHavingStep<Record1<Integer>> select = base
					.where(condition)
					.and(PROJECT_HOLDER.START_DATE.ge(new Timestamp(start.getTime())))
					.and(PROJECT_HOLDER.END_DATE.isNull()
						.or(PROJECT_HOLDER.END_DATE.le(new Timestamp(end.getTime()))))
					.groupBy(PROJECT_HOLDER.TASK_HOLDER);

			applyProjectOrdering(select, params);
			applyHavingCustomers(select, params);

			Result<Record1<Integer>> sellerCount = select.fetch();
			return sellerCount.isEmpty() ? 0 : sellerCount.size();

		} else {

			Condition condition = paramsToCondition(ctx, params);

			SelectJoinStep<Record1<Integer>> base = ctx.getDslContext()
					.selectDistinct(SELLER.REGISTRY)
					.from(SELLER)
					.join(SELLER_ALIAS).on(SELLER_ALIAS.ID.eq(SELLER.REGISTRY))
					.join(SCOPE).on(SCOPE.ID.eq(SELLER.SCOPE))
					.leftJoin(CUSTOMER_FEE)
						.on(CUSTOMER_FEE.SELLER.eq(SELLER.REGISTRY)
						.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(end))
						.and(CUSTOMER_FEE.FINAL_DATE.isNull()
							.or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(start)))
						.and(CUSTOMER_FEE.DOMAIN.in(domainIds)))
					.leftJoin(CUSTOMER)
						.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER));

			SelectHavingStep<Record1<Integer>> select = base
					.where(condition)
					.groupBy(SELLER.REGISTRY);

			applyOrdering(select, params);
			applyHavingCustomers(select, params);

			Result<Record1<Integer>> sellerCount = select.fetch();
			return sellerCount.isEmpty() ? 0 : sellerCount.size();
		}
	}

	// ------------------------------------------------------------------
	// NOTA: este metodo usa el endpoint getFeeIdsList/getInvoiceIdsList,
	// NO getList. Se ha mantenido su logica de filtrado original (con la
	// subconsulta lastNote embebida) porque no ha sido validada en esta
	// tanda de cambios. Si esta pantalla tambien filtra mal por estado,
	// aplicar la misma correccion que en getCustomerAmount.
	// ------------------------------------------------------------------
	public static SellerWorkloadContent getSellersWorkloadContent(CloseableAONContext ctx, SellerWorkloadParams params) {

		Date start = AonDateUtils.toSql(params.getPeriodStart());
		Date endIt = AonDateUtils.toSql(AonDateUtils.getMonthLastDay(start));
		Date end = AonDateUtils.toSql(params.getPeriodEnd());

		Integer[] domainIds = SecurityDAO.getInheritanceDomainIds(ctx);
		StatusFilter sf = getCustomerStatusCondition(ctx, params, domainIds);

		HashSet<Integer> customerFeeIds = new HashSet<Integer>();
		HashSet<Integer> invoiceIds = new HashSet<Integer>();

		Field<Date> recalculatedBillingDateAdjusted = DSL
				.when(CUSTOMER_FEE.PERIOD.eq((short) 0), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 1), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 2), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 2, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 3), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 3, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 4), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 4, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 5), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 6, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 6), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 12, DatePart.MONTH))
				.otherwise(CUSTOMER_FEE.BILLING_DATE);

		List<Integer> projectIds = ctx.getDslContext().selectDistinct(PROJECT.ID)
				.from(PROJECT)
				.join(PROJECT_HOLDER).on(PROJECT_HOLDER.PROJECT.eq(PROJECT.ID))
				.where(PROJECT_HOLDER.TASK_HOLDER.eq(params.getTaskHolder()))
				.and(PROJECT_HOLDER.START_DATE.ge(new Timestamp(start.getTime())))
				.and(PROJECT_HOLDER.END_DATE.isNull().or(PROJECT_HOLDER.END_DATE.le(new Timestamp(end.getTime()))))
				.fetch(PROJECT.ID);

		while (start.before(end)) {

			// ---- FEES por SELLER ----
			if (null != params.getSeller()) {
				SelectConditionStep<Record3<Integer, Integer, Byte>> select = ctx.getDslContext()
						.selectDistinct(CUSTOMER_FEE.ID, CUSTOMER.REGISTRY, CUSTOMER.STATUS)
						.from(CUSTOMER_FEE)
						.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
						.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
						.where(CUSTOMER_FEE.SELLER.eq(params.getSeller()))
						.and(CUSTOMER_FEE.DOMAIN.in(domainIds))
						.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
						.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
						.and(recalculatedBillingDateAdjusted.lessThan(start));

				addFilteredIds(select.fetch(), CUSTOMER_FEE.ID, sf, customerFeeIds);
			}

			// ---- FEES por TASK_HOLDER (proyecto) ----
			if (null != params.getTaskHolder()) {
				SelectConditionStep<Record3<Integer, Integer, Byte>> select = ctx.getDslContext()
						.selectDistinct(CUSTOMER_FEE.ID, CUSTOMER.REGISTRY, CUSTOMER.STATUS)
						.from(CUSTOMER_FEE)
						.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
						.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
						.where(CUSTOMER_FEE.PROJECT.in(projectIds))
						.and(CUSTOMER_FEE.DOMAIN.in(domainIds))
						.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
						.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
						.and(recalculatedBillingDateAdjusted.lessThan(start));

				addFilteredIds(select.fetch(), CUSTOMER_FEE.ID, sf, customerFeeIds);
			}

			// ---- INVOICES por SELLER ----
			if (null != params.getSeller()) {
				SelectConditionStep<Record3<Integer, Integer, Byte>> invoiceSelect = ctx.getDslContext()
						.selectDistinct(INVOICE_DETAIL.ID, CUSTOMER.REGISTRY, CUSTOMER.STATUS)
						.from(INVOICE_DETAIL)
						.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
						.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
						.where(INVOICE_DETAIL.SELLER.eq(params.getSeller()))
						.and(INVOICE_DETAIL.DOMAIN.in(domainIds))
						.and(INVOICE.ISSUE_DATE.between(start, endIt));

				addFilteredIds(invoiceSelect.fetch(), INVOICE_DETAIL.ID, sf, invoiceIds);
			}

			// ---- INVOICES por TASK_HOLDER (proyecto) ----
			if (null != params.getTaskHolder()) {
				SelectConditionStep<Record3<Integer, Integer, Byte>> invoiceSelect = ctx.getDslContext()
						.selectDistinct(INVOICE_DETAIL.ID, CUSTOMER.REGISTRY, CUSTOMER.STATUS)
						.from(INVOICE_DETAIL)
						.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
						.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
						.where(INVOICE_DETAIL.PROJECT.in(projectIds))
						.and(INVOICE_DETAIL.DOMAIN.in(domainIds))
						.and(INVOICE.ISSUE_DATE.between(start, endIt));

				addFilteredIds(invoiceSelect.fetch(), INVOICE_DETAIL.ID, sf, invoiceIds);
			}

			start = AonDateUtils.toSql(AonDateUtils.addMonths(start, 1));
			endIt = AonDateUtils.toSql(AonDateUtils.getMonthLastDay(start));
		}

		if (customerFeeIds.isEmpty() && invoiceIds.isEmpty()) {
			return new SellerWorkloadContent();
		}

		CustomerFeeParams customerFeeParams = new CustomerFeeParams();
		customerFeeParams.setDomain(ctx.getDomainId());
		customerFeeParams.setFeeIds(customerFeeIds.toArray(new Integer[0]));
		customerFeeParams.setInvoiceIds(invoiceIds.toArray(new Integer[0]));
		customerFeeParams.setCustomerName(params.getDescription());
		customerFeeParams.setProductName(params.getDescription());
		customerFeeParams.setOrderBy(params.getOrderBy());
		customerFeeParams.setAsc(params.isAsc());

		LinkedList<Fee> feeList = customerFeeIds.size() > 0 ? getFees(ctx, customerFeeParams) : new LinkedList<Fee>();
		List<InvoiceDetail> invoiceList = invoiceIds.isEmpty()
				? new java.util.ArrayList<>()
				: InvoiceDetailExtendedDAO
						.getInvoiceDetails(ctx, f -> f.getDetailIdProperty().in(customerFeeParams.getInvoiceIds()))
						.collect(Collectors.toList());

		SellerWorkloadContent sellerWorkloadContent = new SellerWorkloadContent()
				.setFees(
					feeList.stream()
					.filter(fee -> AonStringUtils.isBlank(params.getDescription())
							|| (AonStringUtils.containsIgnoreCase(fee.getCustomer().getName(), params.getDescription())
							 || AonStringUtils.containsIgnoreCase(fee.getCustomer().getDocument(), params.getDescription())
							 || AonStringUtils.containsIgnoreCase(fee.getCustomer().getAlias(), params.getDescription())))
					.collect(Collectors.toList()))
				.setInvoiceDetails(invoiceList);

		return sellerWorkloadContent;
	}

	/**
	 * Recorre las filas (id, registry, status) y añade el id a la coleccion
	 * solo si el cliente pasa el filtro de estado.
	 */
	private static void addFilteredIds(Result<Record3<Integer, Integer, Byte>> rows,
			Field<Integer> idField, StatusFilter sf, HashSet<Integer> target) {
		for (Record3<Integer, Integer, Byte> r : rows) {
			Integer id = r.get(idField);
			Integer registry = r.get(CUSTOMER.REGISTRY);
			Byte status = r.get(CUSTOMER.STATUS);
			if (status == null) {
				status = (byte) 0; // sin status -> activo, conservador
			}
			if (registry == null) {
				target.add(id); // sin registry no podemos filtrar; conservador
				continue;
			}
			if (incluir(status, registry, sf)) {
				target.add(id);
			}
		}
	}

	// ------------------------------------------------------------------
	// Construye el StatusFilter: decide si aplica la regla de estado y,
	// si aplica, precarga el mapa de expiraciones UNA sola vez.
	// ------------------------------------------------------------------
	private static StatusFilter getCustomerStatusCondition(AONContext ctx, SellerWorkloadParams params,
			Integer[] domainIds) {

		boolean apply = (params.getCustomers() == null || params.getCustomers() == (byte) 1);

		boolean active = apply && Boolean.TRUE.equals(params.getCustomerActive());
		boolean inactive = apply && Boolean.TRUE.equals(params.getCustomerInactive());
		boolean blocked = apply && Boolean.TRUE.equals(params.getCustomerBlocked());

		// El mapa solo hace falta si hay algun cliente inactivo/bloqueado que
		// pueda tener expiracion futura, es decir si se filtra por estado.
		Map<Integer, java.sql.Date> expMap;
		if (apply && (inactive || blocked || active)) {
			expMap = loadExpirationMap(ctx, domainIds);
		} else {
			expMap = new HashMap<>();
		}

		return new StatusFilter(apply, active, inactive, blocked, expMap);
	}

	public static List<Integer> getFeeIdsList(CloseableAONContext ctx, SellerWorkloadParams params) {

		if (params.getSeller() == null && params.getTaskHolder() == null) {

			List<SellerWorkload> sellers = getList(ctx, params);

			HashSet<Integer> customerFeeIds = new HashSet<Integer>();

			sellers.forEach(seller -> {
				if (null == seller.getProjectHolder()) {
					params.setTaskHolder(null);
					params.setSeller(seller.getId());
				} else {
					params.setTaskHolder(seller.getProjectHolder().getTaskHolder().getId());
					params.setSeller(null);
				}
				SellerWorkloadContent content = getSellersWorkloadContent(ctx, params);
				customerFeeIds.addAll(content.getFees().stream().map(fee -> fee.getId()).distinct().collect(Collectors.toList()));
			});

			return customerFeeIds.stream().collect(Collectors.toList());

		} else {

			HashSet<Integer> customerFeeIds = new HashSet<Integer>();
			SellerWorkloadContent content = getSellersWorkloadContent(ctx, params);
			customerFeeIds.addAll(content.getFees().stream().map(fee -> fee.getId()).distinct().collect(Collectors.toList()));
			return customerFeeIds.stream().collect(Collectors.toList());

		}

	}

	public static List<Integer> getInvoiceIdsList(CloseableAONContext ctx, SellerWorkloadParams params) {

		if (params.getSeller() == null && params.getTaskHolder() == null) {

			List<SellerWorkload> sellers = getList(ctx, params);

			HashSet<Integer> invoiceDetailIds = new HashSet<Integer>();

			sellers.forEach(seller -> {
				if (null == seller.getProjectHolder()) {
					params.setTaskHolder(null);
					params.setSeller(seller.getId());
				} else {
					params.setTaskHolder(seller.getProjectHolder().getTaskHolder().getId());
					params.setSeller(null);
				}
				SellerWorkloadContent content = getSellersWorkloadContent(ctx, params);
				invoiceDetailIds.addAll(content.getInvoiceDetails().stream().map(invoiceDetail -> invoiceDetail.getId()).distinct().collect(Collectors.toList()));
			});

			return invoiceDetailIds.stream().collect(Collectors.toList());

		} else {

			HashSet<Integer> invoiceDetailIds = new HashSet<Integer>();
			SellerWorkloadContent content = getSellersWorkloadContent(ctx, params);
			invoiceDetailIds.addAll(content.getInvoiceDetails().stream().map(invoiceDetail -> invoiceDetail.getId()).distinct().collect(Collectors.toList()));
			return invoiceDetailIds.stream().collect(Collectors.toList());

		}

	}

	public static LinkedList<Fee> getFees(AONContext ctx, CustomerFeeParams customerFeeParams) {
		Condition condition = createFeeCondition(ctx, customerFeeParams);

		SelectConditionStep<Record> fromCustomerRecords = ctx.getDslContext().selectDistinct().from(CUSTOMER_FEE)
				.join(DOMAIN).on(DOMAIN.ID.eq(CUSTOMER_FEE.DOMAIN)).join(CUSTOMER)
				.on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER)).join(CUSTOMER_ALIAS)
				.on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID)).join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM)).join(PRODUCT)
				.on(PRODUCT.ID.eq(ITEM.PRODUCT)).join(WORKPLACE).on(CUSTOMER_FEE.WORKPLACE.eq(WORKPLACE.ID))
				.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY)).leftOuterJoin(INVOICING_GROUP)
				.on(INVOICING_GROUP.ID.eq(CUSTOMER_FEE.INVOICING_GROUP)).leftOuterJoin(PROJECT)
				.on(PROJECT.ID.eq(CUSTOMER_FEE.PROJECT)).where(condition);

		applyCustomerFeeOrdering(fromCustomerRecords, customerFeeParams);

		Result<Record> feeRecords = fromCustomerRecords.groupBy(CUSTOMER_FEE.ID).fetch();

		LinkedList<Fee> fees = feeRecords.stream().map(new FeeFiller())
				.collect(Collectors.toCollection(LinkedList::new));

		return fees;
	}

	private static Condition createFeeCondition(AONContext ctx, CustomerFeeParams customerFeeParams) {
		Condition condition = CUSTOMER_FEE.DOMAIN.eq(customerFeeParams.getDomain());

		User user = SecurityDAO.getUser(ctx);
		if (user.getDomain().getId() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
		}

		if (null != customerFeeParams.getFeeIds() && customerFeeParams.getFeeIds().length > 0) {
			condition = condition.and(CUSTOMER_FEE.ID.in(customerFeeParams.getFeeIds()));
		}

		if (AonStringUtils.isNotBlank(customerFeeParams.getCustomerName())
				|| AonStringUtils.isNotBlank(customerFeeParams.getProductName())) {
			condition = condition
					.and(CUSTOMER_ALIAS.NAME.likeIgnoreCase("%" + customerFeeParams.getCustomerName() + "%").or(
							CUSTOMER_FEE.DESCRIPTION.likeIgnoreCase("%" + customerFeeParams.getProductName() + "%")));
		}

		return condition;
	}

	private static Condition createFeeWorkloadCondition(AONContext ctx, SellerWorkloadParams params) {
		Condition condition = CUSTOMER_FEE.DOMAIN.eq(params.getDomain());

		User user = SecurityDAO.getUser(ctx);
		if (user.getDomain().getId() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
		}

		return condition;
	}

	private static void applyHavingCustomers(SelectHavingStep<?> select, SellerWorkloadParams params) {
		if (params.getCustomers() != null) {
			if (params.getCustomers() == 1) {
				select.having(DSL.countDistinct(CUSTOMER_FEE.CUSTOMER).gt(0));
			} else {
				select.having(DSL.countDistinct(CUSTOMER_FEE.CUSTOMER).eq(0));
			}
		}
	}

	private static void applyOrdering(SelectHavingStep<?> select, SellerWorkloadParams params) {
		HashMap<String, Field<?>> orderFields = new HashMap<>();
		orderFields.put("name", SELLER_ALIAS.NAME);
		orderFields.put("alias", SELLER_ALIAS.ALIAS);
		orderFields.put("document", SELLER_ALIAS.DOCUMENT);

		Field<?> orderField = orderFields.get(params.getOrderBy());

		if (orderField != null) {
			select.orderBy(params.isAsc() ? orderField.asc() : orderField.desc());
		}
	}

	private static void applyProjectOrdering(SelectHavingStep<?> select, SellerWorkloadParams params) {
		HashMap<String, Field<?>> orderFields = new HashMap<>();
		orderFields.put("name", TASK_HOLDER_ALIAS.NAME);
		orderFields.put("alias", TASK_HOLDER_ALIAS.ALIAS);
		orderFields.put("document", TASK_HOLDER_ALIAS.DOCUMENT);

		Field<?> orderField = orderFields.get(params.getOrderBy());

		if (orderField != null) {
			select.orderBy(params.isAsc() ? orderField.asc() : orderField.desc());
		}
	}

	private static void applyFeeOrdering(SelectHavingStep<?> select, SellerWorkloadParams params) {
		HashMap<String, Field<?>> orderFields = new HashMap<>();
		orderFields.put("customer", CUSTOMER_ALIAS.NAME);
		orderFields.put("product", CUSTOMER_FEE.DESCRIPTION);

		Field<?> orderField = orderFields.get(params.getOrderBy());

		if (orderField != null) {
			select.orderBy(params.isAsc() ? orderField.asc() : orderField.desc());
		}
	}

	private static void applyCustomerFeeOrdering(SelectConditionStep<Record> select, CustomerFeeParams params) {
		HashMap<String, Field<?>> orderFields = new HashMap<>();
		orderFields.put("customer", CUSTOMER_ALIAS.NAME);
		orderFields.put("product", CUSTOMER_FEE.DESCRIPTION);

		Field<?> orderField = orderFields.get(params.getOrderBy());

		if (orderField != null) {
			select.orderBy(params.isAsc() ? orderField.asc() : orderField.desc());
		}
	}

	private static Condition paramsToCondition(CloseableAONContext ctx, SellerWorkloadParams params) {
		Condition condition = SELLER.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));

		if (AonStringUtils.isNotBlank(params.getDescription())) {
			condition = condition.and(SELLER_ALIAS.NAME.like("%" + params.getDescription() + "%")
					.or(SELLER_ALIAS.DOCUMENT.like("%" + params.getDescription() + "%"))
					.or(SELLER_ALIAS.ALIAS.like("%" + params.getDescription() + "%")));
		}

		if (AonStringUtils.isNotBlank(params.getName())) {
			condition = condition.and(SELLER_ALIAS.NAME.like("%" + params.getName() + "%"));
		}

		if (AonStringUtils.isNotBlank(params.getAlias())) {
			condition = condition.and(SELLER_ALIAS.ALIAS.like("%" + params.getAlias() + "%"));
		}

		if (AonStringUtils.isNotBlank(params.getDocument())) {
			condition = condition.and(SELLER_ALIAS.DOCUMENT.like("%" + params.getDocument() + "%"));
		}

		if (null != params.getScope()) {
			condition = condition.and(SELLER.SCOPE.eq(params.getScope()));
		}

		if (null != params.getActive()) {
			condition = condition.and(SELLER.STATUS.eq(params.getActive()));
		}

		return condition;
	}

	private static Condition paramsProjectToCondition(CloseableAONContext ctx, SellerWorkloadParams params) {
		Condition condition = PROJECT_HOLDER.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));

		if (AonStringUtils.isNotBlank(params.getDescription())) {
			condition = condition.and(TASK_HOLDER_ALIAS.NAME.like("%" + params.getDescription() + "%")
					.or(TASK_HOLDER_ALIAS.DOCUMENT.like("%" + params.getDescription() + "%"))
					.or(TASK_HOLDER_ALIAS.ALIAS.like("%" + params.getDescription() + "%")));
		}

		if (AonStringUtils.isNotBlank(params.getName())) {
			condition = condition.and(TASK_HOLDER_ALIAS.NAME.like("%" + params.getName() + "%"));
		}

		if (AonStringUtils.isNotBlank(params.getAlias())) {
			condition = condition.and(TASK_HOLDER_ALIAS.ALIAS.like("%" + params.getAlias() + "%"));
		}

		if (AonStringUtils.isNotBlank(params.getDocument())) {
			condition = condition.and(TASK_HOLDER_ALIAS.DOCUMENT.like("%" + params.getDocument() + "%"));
		}

		return condition;
	}

	private static void getCustomerAmount(AONContext ctx, SellerWorkload sellerWorkload, Integer seller,
			SellerWorkloadParams params, StatusFilter sf) {

		Date start = AonDateUtils.toSql(params.getPeriodStart());
		Date endIt = AonDateUtils.toSql(AonDateUtils.getMonthLastDay(start));
		Date end = AonDateUtils.toSql(params.getPeriodEnd());

		Condition condition = createFeeWorkloadCondition(ctx, params);
		Integer[] domainIds = SecurityDAO.getInheritanceDomainIds(ctx);

		Field<Date> recalculatedBillingDateAdjusted = DSL
				.when(CUSTOMER_FEE.PERIOD.eq((short) 0), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 1), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 2), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 2, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 3), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 3, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 4), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 4, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 5), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 6, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 6), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 12, DatePart.MONTH))
				.otherwise(CUSTOMER_FEE.BILLING_DATE);

		while (start.before(end)) {

			Result<Record5<Double, Double, String, Short, Integer>> result = ctx.getDslContext()
					.select(CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR,
							CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.CUSTOMER)
					.from(CUSTOMER_FEE).join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
					.where(CUSTOMER_FEE.SELLER.eq(seller))
					.and(condition)
					.and(CUSTOMER_FEE.DOMAIN.in(domainIds))
					.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
					.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
					.and(recalculatedBillingDateAdjusted.lessThan(start))
					.fetch();

			Result<Record5<Double, Double, String, Integer, Byte>> invoiceResult = ctx.getDslContext()
					.select(INVOICE_DETAIL.QUANTITY, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.DISCOUNT_EXPR,
							INVOICE.REGISTRY, CUSTOMER.STATUS)
					.from(INVOICE_DETAIL)
					.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
					.where(INVOICE_DETAIL.SELLER.eq(seller))
					.and(INVOICE_DETAIL.DOMAIN.in(domainIds))
					.and(INVOICE.ISSUE_DATE.between(start, endIt))
					.fetch();

			// Necesitamos el STATUS del cliente en fees para filtrar. Lo consultamos
			// una vez por seller a un mapa (registry -> status) para no re-consultar.
			Map<Integer, Byte> statusByCustomer = statusMapForFees(ctx, seller, domainIds);

			computePeriod(sellerWorkload, start, result, invoiceResult, sf, statusByCustomer);

			start = AonDateUtils.toSql(AonDateUtils.addMonths(start, 1));
			endIt = AonDateUtils.toSql(AonDateUtils.getMonthLastDay(start));
		}
	}

	private static void getProjectCustomerAmount(AONContext ctx, SellerWorkload sellerWorkload, Integer taskHolderId,
			SellerWorkloadParams params, StatusFilter sf) {

		Date start = AonDateUtils.toSql(params.getPeriodStart());
		Date endIt = AonDateUtils.toSql(AonDateUtils.getMonthLastDay(start));
		Date end = AonDateUtils.toSql(params.getPeriodEnd());

		Condition condition = createFeeWorkloadCondition(ctx, params);
		Integer[] domainIds = SecurityDAO.getInheritanceDomainIds(ctx);

		List<Integer> projectIds = ctx.getDslContext().selectDistinct(PROJECT.ID)
				.from(PROJECT)
				.join(PROJECT_HOLDER).on(PROJECT_HOLDER.PROJECT.eq(PROJECT.ID))
				.where(PROJECT_HOLDER.TASK_HOLDER.eq(taskHolderId))
				.and(PROJECT_HOLDER.START_DATE.ge(new Timestamp(start.getTime())))
				.and(PROJECT_HOLDER.END_DATE.isNull().or(PROJECT_HOLDER.END_DATE.le(new Timestamp(end.getTime()))))
				.fetch(PROJECT.ID);

		Field<Date> recalculatedBillingDateAdjusted = DSL
				.when(CUSTOMER_FEE.PERIOD.eq((short) 0), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 1), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 1, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 2), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 2, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 3), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 3, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 4), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 4, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 5), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 6, DatePart.MONTH))
				.when(CUSTOMER_FEE.PERIOD.eq((short) 6), DSL.dateSub(CUSTOMER_FEE.BILLING_DATE, 12, DatePart.MONTH))
				.otherwise(CUSTOMER_FEE.BILLING_DATE);

		while (start.before(end)) {

			Result<Record5<Double, Double, String, Short, Integer>> result = ctx.getDslContext()
					.select(CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR,
							CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.CUSTOMER)
					.from(CUSTOMER_FEE).join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
					.where(CUSTOMER_FEE.PROJECT.in(projectIds))
					.and(condition)
					.and(CUSTOMER_FEE.DOMAIN.in(domainIds))
					.and(CUSTOMER_FEE.INITIAL_DATE.lessOrEqual(start))
					.and(CUSTOMER_FEE.FINAL_DATE.isNull().or(CUSTOMER_FEE.FINAL_DATE.greaterOrEqual(endIt)))
					.and(recalculatedBillingDateAdjusted.lessThan(start))
					.fetch();

			Result<Record5<Double, Double, String, Integer, Byte>> invoiceResult = ctx.getDslContext()
					.select(INVOICE_DETAIL.QUANTITY, INVOICE_DETAIL.PRICE, INVOICE_DETAIL.DISCOUNT_EXPR,
							INVOICE.REGISTRY, CUSTOMER.STATUS)
					.from(INVOICE_DETAIL)
					.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
					.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(INVOICE.REGISTRY))
					.where(INVOICE_DETAIL.PROJECT.in(projectIds))
					.and(INVOICE_DETAIL.DOMAIN.in(domainIds))
					.and(INVOICE.ISSUE_DATE.between(start, endIt))
					.fetch();

			Map<Integer, Byte> statusByCustomer = statusMapForProject(ctx, projectIds, domainIds);

			computePeriod(sellerWorkload, start, result, invoiceResult, sf, statusByCustomer);

			start = AonDateUtils.toSql(AonDateUtils.addMonths(start, 1));
			endIt = AonDateUtils.toSql(AonDateUtils.getMonthLastDay(start));
		}
	}

	// Mapa registry->status de los clientes de un seller (una consulta ligera).
	private static Map<Integer, Byte> statusMapForFees(AONContext ctx, Integer seller, Integer[] domainIds) {
		Map<Integer, Byte> map = new HashMap<>();
		ctx.getDslContext()
			.selectDistinct(CUSTOMER.REGISTRY, CUSTOMER.STATUS)
			.from(CUSTOMER_FEE)
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
			.where(CUSTOMER_FEE.SELLER.eq(seller))
			.and(CUSTOMER_FEE.DOMAIN.in(domainIds))
			.fetch()
			.forEach(r -> map.put(r.get(CUSTOMER.REGISTRY), r.get(CUSTOMER.STATUS)));
		return map;
	}

	private static Map<Integer, Byte> statusMapForProject(AONContext ctx, List<Integer> projectIds, Integer[] domainIds) {
		Map<Integer, Byte> map = new HashMap<>();
		if (projectIds == null || projectIds.isEmpty()) {
			return map;
		}
		ctx.getDslContext()
			.selectDistinct(CUSTOMER.REGISTRY, CUSTOMER.STATUS)
			.from(CUSTOMER_FEE)
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
			.where(CUSTOMER_FEE.PROJECT.in(projectIds))
			.and(CUSTOMER_FEE.DOMAIN.in(domainIds))
			.fetch()
			.forEach(r -> map.put(r.get(CUSTOMER.REGISTRY), r.get(CUSTOMER.STATUS)));
		return map;
	}

	// Suma un periodo aplicando el filtro de estado en memoria.
	private static void computePeriod(SellerWorkload sellerWorkload, Date periodStart,
			Result<Record5<Double, Double, String, Short, Integer>> feeResult,
			Result<Record5<Double, Double, String, Integer, Byte>> invoiceResult,
			StatusFilter sf, Map<Integer, Byte> statusByCustomer) {

		double netSum = 0.0;
		double totalSum = 0.0;
		int feeCount = 0;
		int invCount = 0;
		HashSet<Integer> customers = new HashSet<>();

		for (Record record : feeResult) {
			Integer cust = record.get(CUSTOMER_FEE.CUSTOMER);
			Byte status = statusByCustomer.get(cust);
			if (status == null) {
				status = (byte) 0; // sin registro de status -> tratar como activo
			}
			if (!incluir(status, cust, sf)) {
				continue;
			}

			Double quantity = record.get(CUSTOMER_FEE.QUANTITY);
			Double price = record.get(CUSTOMER_FEE.PRICE);
			String discountExpr = record.get(CUSTOMER_FEE.DISCOUNT_EXPR);
			short recordPeriod = record.get(CUSTOMER_FEE.PERIOD);

			double discount = 0.0;
			if (discountExpr != null && !discountExpr.isEmpty()) {
				try {
					discount = evaluateDiscount(discountExpr, price);
				} catch (ScriptException e) {
					System.err.println("Error evaluando DISCOUNT_EXPR: " + discountExpr);
					e.printStackTrace();
				}
			}

			double periodValue = getPeriodValue(recordPeriod);
			netSum += (price - discount) * quantity / periodValue;
			totalSum += price * quantity / periodValue;
			customers.add(cust);
			feeCount++;
		}

		for (Record record : invoiceResult) {
			Integer reg = record.get(INVOICE.REGISTRY);
			Byte status = record.get(CUSTOMER.STATUS);
			if (status == null) {
				status = (byte) 0;
			}
			if (!incluir(status, reg, sf)) {
				continue;
			}

			Double quantity = record.get(INVOICE_DETAIL.QUANTITY);
			Double price = record.get(INVOICE_DETAIL.PRICE);
			String discountExpr = record.get(INVOICE_DETAIL.DISCOUNT_EXPR);

			double discount = 0.0;
			if (discountExpr != null && !discountExpr.isEmpty()) {
				try {
					discount = evaluateDiscount(discountExpr, price);
				} catch (ScriptException e) {
					System.err.println("Error evaluando DISCOUNT_EXPR: " + discountExpr);
					e.printStackTrace();
				}
			}

			netSum += (price - discount) * quantity;
			totalSum += price * quantity;
			customers.add(reg);
			invCount++;
		}

		sellerWorkload.addSellerWorkloadPeriod(periodStart, customers.size(), feeCount, invCount, netSum, totalSum);
	}

	// Metodo para evaluar el descuento basado en DISCOUNT_EXPR
	private static double evaluateDiscount(String discountExpr, Double price) throws ScriptException {
		double discount = 0.0;
		double discountedPrice = price;

		String numberPattern = "\\d+(\\.\\d+)?";

		if (discountExpr.matches(numberPattern)) {
			discount = (price * Double.parseDouble(discountExpr)) / 100;
			discountedPrice = price - discount;
		} else {
			String[] discountParts = discountExpr.split("\\+");
			for (String part : discountParts) {
				part = part.trim();
				if (part.matches(numberPattern)) {
					double percentage = Double.parseDouble(part);
					discount = (discountedPrice * percentage) / 100;
					discountedPrice -= discount;
				} else {
					throw new ScriptException("Formato de descuento inválido: " + part);
				}
			}
		}
		return price - discountedPrice;
	}

	// Metodo para calcular el valor de division por periodo (Short Period)
	private static double getPeriodValue(short period) {
		switch (period) {
		case 1:
			return 1.0;
		case 2:
			return 2.0;
		case 3:
			return 3.0;
		case 4:
			return 4.0;
		case 5:
			return 6.0;
		case 6:
			return 12.0;
		default:
			return 1.0;
		}
	}

	protected static class SellerFiller extends Filler implements Function<Record, SellerWorkload> {

		@Override
		public SellerWorkload apply(Record r) {
			return build(r);
		}

		public static SellerWorkload build(Record r) {
			return build(r, SELLER_ALIAS);
		}

		public static SellerWorkload build(Record r, Registry registry) {
			SellerWorkload sellerWorkload = new SellerWorkload();

			sellerWorkload.setId(r.getValue(SELLER.REGISTRY));
			sellerWorkload.setStatus(SellerStatus.safeValueOf(getValue(r, SELLER.STATUS)));
			sellerWorkload.setName(r.getValue(SELLER_ALIAS.NAME));
			sellerWorkload.setDocument(r.getValue(SELLER_ALIAS.DOCUMENT));
			sellerWorkload.setScope(new Scope().setDescription(r.getValue(SCOPE.DESCRIPTION)));

			return sellerWorkload;
		}

	}

}