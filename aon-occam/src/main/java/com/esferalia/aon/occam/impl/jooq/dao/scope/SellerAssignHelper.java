package com.esferalia.aon.occam.impl.jooq.dao.scope;

import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Rrelationship.RRELATIONSHIP;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.scope.UserScopeAssign;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

/**
 * Reasignación de ÁMBITOS (user_scope), CUOTAS (customer_fee) y líneas de
 * FACTURA (invoice_detail / invoice) al cambiar de AGENTE COMERCIAL.
 *
 * Reglas implementadas:
 *
 *  2. Ámbitos  -> siempre: se cierra el user_scope del agente actual con
 *                 (fechaAsignacion - 1) y se crea el del agente receptor.
 *
 *  3.a) Día de asignación entre 1 y 15
 *       - customer_fee.seller = nuevo agente.
 *       - Si la cuota YA está facturada en el mes de asignación
 *         (billing_date ha saltado al mes siguiente o posterior):
 *           * invoice_detail con source = 5 (cuota), source_id = AAAAMM de la
 *             asignación y seller = agente actual  ->  seller = nuevo agente.
 *           * Recalcular invoice.seller: si todas las líneas comparten agente,
 *             ese agente; en caso contrario NULL.
 *
 *  3.b) Día de asignación posterior al 15
 *       - Caso 1 (cuota PENDIENTE de facturar en el mes de asignación,
 *                 es decir AAAAMM(billing_date) <= AAAAMM(asignación)):
 *           * Se duplica la cuota para el nuevo agente con
 *             billing_date = día 1 del mes siguiente.
 *           * La cuota original recibe final_date = último día del mes de
 *             asignación (la factura de ese mes se la lleva el agente actual).
 *       - Caso 2 (cuota YA facturada: AAAAMM(billing_date) >= mes siguiente):
 *           * customer_fee.seller = nuevo agente (sin duplicar).
 *       - Ambos casos pueden coexistir en el mismo cliente: se decide cuota a
 *         cuota, no cliente a cliente.
 */
public class SellerAssignHelper {

	/** invoice_detail.source == 5 -> la línea procede de una CUOTA (customer_fee). */
	private static final byte SOURCE_CUSTOMER_FEE = 5;

	// ------------------------------------------------------------------
	// 2. ASIGNACIÓN DE ÁMBITOS
	// ------------------------------------------------------------------
	public static void assignSellerUserScopes(CloseableAONContext ctx, int domain, String user, UserScopeAssign userScopeAssign) {

		Integer userOriginal = userScopeAssign.getSellerUserOwner();
		Integer userNew = userScopeAssign.getSellerUserNewOwner();
		Date startDate = userScopeAssign.getAssginDate();
		Date endDate = AonDateUtils.addDays(startDate, -1);

		userScopeAssign.getUserScopes().forEach(us -> {
			Integer originalUserScopeId = us.getId();
			Scope scope = us.getScope();

			ctx.getDslContext().update(USER_SCOPE)
				.set(USER_SCOPE.END_DATE, AonDateUtils.toSql(endDate))
				.set(USER_SCOPE.MODIFICATION_USER, user)
				.set(USER_SCOPE.MODIFICATION_DATE, DSL.currentTimestamp())
				.where(USER_SCOPE.ID.eq(originalUserScopeId))
				.and(USER_SCOPE.USER_ID.eq(userOriginal))
				.execute();

			ctx.getDslContext().insertInto(USER_SCOPE)
				.set(USER_SCOPE.DOMAIN, domain)
				.set(USER_SCOPE.USER_ID, userNew)
				.set(USER_SCOPE.SCOPE, scope.getId())
				.set(USER_SCOPE.START_DATE, AonDateUtils.toSql(startDate))
				.set(USER_SCOPE.CREATION_USER, user)
				.set(USER_SCOPE.CREATION_DATE, DSL.currentTimestamp())
				.execute();
		});

		assignSellerCustomerFees(ctx, domain, user, userScopeAssign);
	}

	// ------------------------------------------------------------------
	// 3. MODIFICACIÓN DE CARGAS (cuotas + facturas)
	// ------------------------------------------------------------------
	public static void assignSellerCustomerFees(CloseableAONContext ctx, int domain, String user, UserScopeAssign userScopeAssign) {

		Integer sellerOriginal = userScopeAssign.getSellerOwner();
		Integer sellerNew = userScopeAssign.getSellerNewOwner();

		Date assignDate = userScopeAssign.getAssginDate();
		LocalDate assign = toLocalDate(assignDate);

		int assignDay = assign.getDayOfMonth();
		int assignYm = yearMonth(assign);                  // p.ej. 202605
		int nextYm = yearMonth(assign.plusMonths(1));      // p.ej. 202606

		java.sql.Date assignSql = java.sql.Date.valueOf(assign);
		java.sql.Date feeFinalDate = java.sql.Date.valueOf(assign.withDayOfMonth(assign.lengthOfMonth()));
		java.sql.Date feeNewBillingDate = java.sql.Date.valueOf(assign.plusMonths(1).withDayOfMonth(1));

		// Clientes cuyas líneas de factura del mes de asignación hay que repasar (caso 3.a)
		Set<CustomerKey> customersToReview = new LinkedHashSet<>();
		// Facturas tocadas -> hay que recalcular su cabecera al final
		Set<Integer> affectedInvoices = new LinkedHashSet<>();

		userScopeAssign.getUserScopes().forEach(us -> {
			Scope scope = us.getScope();

			ctx.getDslContext().selectFrom(DOMAIN)
				.where(DOMAIN.SCOPE.eq(scope.getId()))
				.fetch()
				.forEach(domainRecord -> {
					Integer domainId = domainRecord.getId();

					ctx.getDslContext().selectFrom(ENTERPRISE)
						.where(ENTERPRISE.DOMAIN.eq(domainId))
						.fetch()
						.forEach(enterpriseRecord -> {
							Integer enterpriseRegistry = enterpriseRecord.getRegistry();

							ctx.getDslContext().selectFrom(RRELATIONSHIP)
								.where(RRELATIONSHIP.RELATED_REGISTRY.eq(enterpriseRegistry))
								.and(RRELATIONSHIP.DOMAIN.eq(domain))
								.fetch()
								.forEach(relationshipRecord -> {
									Integer customerRegistry = relationshipRecord.getRegistry();

									// Cuotas vivas del agente actual para este cliente
									ctx.getDslContext().selectFrom(CUSTOMER_FEE)
										.where(CUSTOMER_FEE.CUSTOMER.eq(customerRegistry))
										.and(CUSTOMER_FEE.SELLER.eq(sellerOriginal))
										.and(CUSTOMER_FEE.DOMAIN.eq(domain))
										.and(CUSTOMER_FEE.FINAL_DATE.isNull()
												.or(CUSTOMER_FEE.FINAL_DATE.ge(assignSql)))
										.fetch()
										.forEach(fee -> {

											Integer feeId = fee.getId();
											java.sql.Date billingDate = fee.getBillingDate();
											Integer billingYm = (billingDate == null)
													? null
													: yearMonth(billingDate.toLocalDate());

											// ----------------------------------------------
											// a) Asignación entre el día 1 y el 15
											// ----------------------------------------------
											if (assignDay <= 15) {

												ctx.getDslContext().update(CUSTOMER_FEE)
													.set(CUSTOMER_FEE.SELLER, sellerNew)
													.where(CUSTOMER_FEE.ID.eq(feeId))
													.execute();

												// Cuota ya facturada en el mes de asignación:
												// billing_date apunta ya al mes siguiente (o posterior)
												if (billingYm != null && billingYm >= nextYm) {
													customersToReview.add(new CustomerKey(domain, customerRegistry));
												}
											}

											// ----------------------------------------------
											// b) Asignación posterior al día 15
											// ----------------------------------------------
											else {

												// Caso 1: pendiente de facturar en el mes de la asignación
												if (billingYm == null || billingYm <= assignYm) {

													// Duplicado para el nuevo agente, facturando ya el mes siguiente
													ctx.getDslContext().insertInto(CUSTOMER_FEE)
														.set(CUSTOMER_FEE.DOMAIN, fee.getDomain())
														.set(CUSTOMER_FEE.PROJECT, fee.getProject())
														.set(CUSTOMER_FEE.CUSTOMER, fee.getCustomer())
														.set(CUSTOMER_FEE.LINE, fee.getLine())
														.set(CUSTOMER_FEE.ITEM, fee.getItem())
														.set(CUSTOMER_FEE.DESCRIPTION, fee.getDescription())
														.set(CUSTOMER_FEE.QUANTITY, fee.getQuantity())
														.set(CUSTOMER_FEE.PRICE, fee.getPrice())
														.set(CUSTOMER_FEE.DISCOUNT_EXPR, fee.getDiscountExpr())
														.set(CUSTOMER_FEE.INITIAL_DATE, fee.getInitialDate())
														.set(CUSTOMER_FEE.FINAL_DATE, fee.getFinalDate())
														.set(CUSTOMER_FEE.BILLING_DATE, feeNewBillingDate)
														.set(CUSTOMER_FEE.PERIOD, fee.getPeriod())
														.set(CUSTOMER_FEE.SECURITY_LEVEL, fee.getSecurityLevel())
														.set(CUSTOMER_FEE.INVOICING_GROUP, fee.getInvoicingGroup())
														.set(CUSTOMER_FEE.SELLER, sellerNew)
														.set(CUSTOMER_FEE.WORKPLACE, fee.getWorkplace())
														.execute();

													// La cuota original muere a fin del mes de asignación
													ctx.getDslContext().update(CUSTOMER_FEE)
														.set(CUSTOMER_FEE.FINAL_DATE, feeFinalDate)
														.where(CUSTOMER_FEE.ID.eq(feeId))
														.execute();
												}

												// Caso 2: ya facturada -> billing_date >= mes siguiente
												else {
													ctx.getDslContext().update(CUSTOMER_FEE)
														.set(CUSTOMER_FEE.SELLER, sellerNew)
														.where(CUSTOMER_FEE.ID.eq(feeId))
														.execute();
												}
											}
										});
								});
						});
				});
		});

		// ------------------------------------------------------------------
		// 3.a) Líneas de factura del mes de asignación (una sola vez por cliente)
		// ------------------------------------------------------------------
		customersToReview.forEach(key ->
			affectedInvoices.addAll(
				reassignInvoiceDetails(ctx, key.domainId, key.customerRegistry,
						sellerOriginal, sellerNew, assignYm, user)));

		// Cabecera de factura: mismo agente en todas las líneas -> ese agente; si no, NULL
		affectedInvoices.forEach(invoiceId -> refreshInvoiceSeller(ctx, invoiceId, user));
	}

	// ------------------------------------------------------------------
	// Líneas de factura: source = 5 (cuota) + source_id = AAAAMM + seller actual
	// ------------------------------------------------------------------
	private static Set<Integer> reassignInvoiceDetails(CloseableAONContext ctx, Integer domainId,
			Integer customerRegistry, Integer sellerOriginal, Integer sellerNew, int assignYm, String user) {

		List<Integer> invoiceIds = ctx.getDslContext()
			.selectDistinct(INVOICE_DETAIL.INVOICE)
			.from(INVOICE_DETAIL)
			.join(INVOICE).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.where(INVOICE_DETAIL.DOMAIN.eq(domainId))
			.and(InvoiceDAO.NOT_ANNULLED)
			.and(INVOICE_DETAIL.SOURCE.eq(SOURCE_CUSTOMER_FEE))
			.and(INVOICE_DETAIL.SOURCE_ID.eq(assignYm))
			.and(INVOICE_DETAIL.SELLER.eq(sellerOriginal))
			.and(INVOICE.REGISTRY.eq(customerRegistry))
			.fetch(INVOICE_DETAIL.INVOICE);

		if (invoiceIds.isEmpty()) {
			return Collections.emptySet();
		}

		ctx.getDslContext().update(INVOICE_DETAIL)
			.set(INVOICE_DETAIL.SELLER, sellerNew)
			.set(INVOICE_DETAIL.MODIFICATION_USER, user)
			.set(INVOICE_DETAIL.MODIFICATION_DATE, DSL.currentTimestamp())
			.where(INVOICE_DETAIL.INVOICE.in(invoiceIds))
			.and(INVOICE_DETAIL.SOURCE.eq(SOURCE_CUSTOMER_FEE))
			.and(INVOICE_DETAIL.SOURCE_ID.eq(assignYm))
			.and(INVOICE_DETAIL.SELLER.eq(sellerOriginal))
			.execute();

		return new LinkedHashSet<>(invoiceIds);
	}

	// ------------------------------------------------------------------
	// Cabecera: si todas las líneas comparten agente -> ese agente; si no -> NULL
	// ------------------------------------------------------------------
	private static void refreshInvoiceSeller(CloseableAONContext ctx, Integer invoiceId, String user) {

		List<Integer> sellers = ctx.getDslContext()
			.selectDistinct(INVOICE_DETAIL.SELLER)
			.from(INVOICE_DETAIL)
			.where(INVOICE_DETAIL.INVOICE.eq(invoiceId))
			.fetch(INVOICE_DETAIL.SELLER);

		Integer invoiceSeller = (sellers.size() == 1) ? sellers.get(0) : null;

		ctx.getDslContext().update(INVOICE)
			.set(INVOICE.SELLER, invoiceSeller)
			.set(INVOICE.MODIFICATION_USER, user)
			.set(INVOICE.MODIFICATION_DATE, DSL.currentTimestamp())
			.where(INVOICE.ID.eq(invoiceId))
			.execute();
	}

	// ------------------------------------------------------------------
	// Utilidades
	// ------------------------------------------------------------------

	/** AAAAMM como entero, mismo formato que invoice_detail.source_id (202605). */
	private static int yearMonth(LocalDate date) {
		return date.getYear() * 100 + date.getMonthValue();
	}

	private static LocalDate toLocalDate(Date date) {
		if (date instanceof java.sql.Date) {
			return ((java.sql.Date) date).toLocalDate();
		}
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/** Clave (dominio, cliente) para no repasar el mismo cliente varias veces. */
	private static final class CustomerKey {
		private final Integer domainId;
		private final Integer customerRegistry;

		CustomerKey(Integer domainId, Integer customerRegistry) {
			this.domainId = domainId;
			this.customerRegistry = customerRegistry;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof CustomerKey)) return false;
			CustomerKey other = (CustomerKey) o;
			return java.util.Objects.equals(domainId, other.domainId)
					&& java.util.Objects.equals(customerRegistry, other.customerRegistry);
		}

		@Override
		public int hashCode() {
			return java.util.Objects.hash(domainId, customerRegistry);
		}
	}
}