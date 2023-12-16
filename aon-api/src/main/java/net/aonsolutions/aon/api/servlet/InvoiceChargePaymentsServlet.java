package net.aonsolutions.aon.api.servlet;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "ChargePaymentsServlet", urlPatterns = {"/ms/api/charge_payments/*"})
public class InvoiceChargePaymentsServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(InvoiceChargePaymentsServlet.class.getName());
	
	public static final String DOCUMENTS = "/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			String period = JsonUtils.getString(api.getData(), "period");
			Date filterDate;
			switch (period) {
				case "current_month":
					filterDate = AonDateUtils.getMonthLastDay(new Date());
					break;
				case "next_month":
					filterDate = AonDateUtils.getMonthLastDay(AonDateUtils.addMonths(new Date(), 1));
					break;
				case "next_3month":
					filterDate = AonDateUtils.getMonthLastDay(AonDateUtils.addMonths(new Date(), 3));
					break;
				case "next_6month":
					filterDate = AonDateUtils.getMonthLastDay(AonDateUtils.addMonths(new Date(), 6));
					break;
				case "yearly":
					filterDate = AonDateUtils.getYearLastDay(new Date());
					break;
				default:
					filterDate = new Date();
					break;
			}
			
			
			// Charges
			
			Double chargePreviusPending = AON_SOLUTIONS.getFinanceGroupStatus(
					api.getDomain(), 
					api.getUser(), 
					f-> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getDueDateProperty().lt(new Date()))
						.and(f.getStatusProperty().eq(FinanceStatus.PENDING.value()))
						.and(f.getPaymentProperty().eq((byte)0))
			);
			
			Double chargePreviusReturned = AON_SOLUTIONS.getFinanceGroupStatus(
					api.getDomain(), 
					api.getUser(), 
					f-> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getDueDateProperty().lt(new Date()))
						.and(f.getStatusProperty().eq(FinanceStatus.RETURNED.value()))
						.and(f.getPaymentProperty().eq((byte)0))
			);
			
			Double chargePeriodPending = AON_SOLUTIONS.getFinanceGroupStatus(
					api.getDomain(), 
					api.getUser(), 
					f-> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getDueDateProperty().ge(new Date()).and(f.getDueDateProperty().le(filterDate)))
						.and(f.getStatusProperty().eq(FinanceStatus.PENDING.value()))
						.and(f.getPaymentProperty().eq((byte)0))
			);
			
			Double chargePeriodReturned = AON_SOLUTIONS.getFinanceGroupStatus(
					api.getDomain(), 
					api.getUser(), 
					f-> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getDueDateProperty().ge(new Date()).and(f.getDueDateProperty().le(filterDate)))
						.and(f.getStatusProperty().eq(FinanceStatus.RETURNED.value()))
						.and(f.getPaymentProperty().eq((byte)0))
			);
			
			
			Double chargeAccumulatePending = chargePreviusPending + chargePeriodPending;
			Double chargeAccumulateReturned = chargePreviusReturned + chargePeriodReturned;
			
			// Payments
			
			Double paymentPreviusPending = AON_SOLUTIONS.getFinanceGroupStatus(
					api.getDomain(), 
					api.getUser(), 
					f-> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getDueDateProperty().lt(new Date()))
						.and(f.getStatusProperty().eq(FinanceStatus.PENDING.value()))
						.and(f.getPaymentProperty().eq((byte)1))
			);
			paymentPreviusPending *= -1;
			
			Double paymentPreviusReturned = AON_SOLUTIONS.getFinanceGroupStatus(
					api.getDomain(), 
					api.getUser(), 
					f-> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getDueDateProperty().lt(new Date()))
						.and(f.getStatusProperty().eq(FinanceStatus.RETURNED.value()))
						.and(f.getPaymentProperty().eq((byte)1))
			);
			paymentPreviusReturned *= -1;
			
			Double paymentPeriodPending = AON_SOLUTIONS.getFinanceGroupStatus(
					api.getDomain(), 
					api.getUser(), 
					f-> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getDueDateProperty().ge(new Date()).and(f.getDueDateProperty().le(filterDate)))
						.and(f.getStatusProperty().eq(FinanceStatus.PENDING.value()))
						.and(f.getPaymentProperty().eq((byte)1))
			);
			paymentPeriodPending *= -1;
			
			Double paymentPeriodReturned = AON_SOLUTIONS.getFinanceGroupStatus(
					api.getDomain(), 
					api.getUser(), 
					f-> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getDueDateProperty().ge(new Date()).and(f.getDueDateProperty().le(filterDate)))
						.and(f.getStatusProperty().eq(FinanceStatus.RETURNED.value()))
						.and(f.getPaymentProperty().eq((byte)1))
			);
			paymentPeriodReturned *= -1;
			
			
			Double paymentAccumulatePending = paymentPreviusPending + paymentPeriodPending;
			Double paymentAccumulateReturned = paymentPreviusReturned + paymentPeriodReturned;
			
			// CashFlow
			
			Double cashFlowPrevius = chargePreviusPending + chargePreviusReturned + paymentPreviusPending + paymentPreviusReturned;
			Double cashFlowPeriod = chargePeriodPending + chargePeriodReturned + paymentPeriodPending + paymentPeriodReturned;
			Double cashFlowAccumulate = chargeAccumulatePending + chargeAccumulateReturned + paymentAccumulatePending + paymentAccumulateReturned;
			
			// Result Json
			
			// Charges
			JSONObject chargesPreviusJson = new JSONObject();
			chargesPreviusJson.put("pending", chargePreviusPending);
			chargesPreviusJson.put("returns", chargePreviusReturned);
			
			JSONObject chargesPeriodJson = new JSONObject();
			chargesPeriodJson.put("pending", chargePeriodPending);
			chargesPeriodJson.put("returns", chargePeriodReturned);
			
			JSONObject chargesAccumulateJson = new JSONObject();
			chargesAccumulateJson.put("pending", chargeAccumulatePending);
			chargesAccumulateJson.put("returns", chargeAccumulateReturned);
			
			JSONObject chargesJson = new JSONObject();
			chargesJson.put("previous", chargesPreviusJson);
			chargesJson.put("period", chargesPeriodJson);
			chargesJson.put("accumulate", chargesAccumulateJson);
			
			// Payments
			JSONObject paymentsPreviusJson = new JSONObject();
			paymentsPreviusJson.put("pending", paymentPreviusPending);
			paymentsPreviusJson.put("returns", paymentPreviusReturned);
			
			JSONObject paymentsPeriodJson = new JSONObject();
			paymentsPeriodJson.put("pending", paymentPeriodPending);
			paymentsPeriodJson.put("returns", paymentPeriodReturned);
			
			JSONObject paymentsAccumulateJson = new JSONObject();
			paymentsAccumulateJson.put("pending", paymentAccumulatePending);
			paymentsAccumulateJson.put("returns", paymentAccumulateReturned);
			
			JSONObject paymentsJson = new JSONObject();
			paymentsJson.put("previous", paymentsPreviusJson);
			paymentsJson.put("period", paymentsPeriodJson);
			paymentsJson.put("accumulate", paymentsAccumulateJson);
			
			// CachFlow
			JSONObject cashFlowson = new JSONObject();
			cashFlowson.put("previous", cashFlowPrevius);
			cashFlowson.put("period", cashFlowPeriod);
			cashFlowson.put("accumulate", cashFlowAccumulate);
			
			JSONObject json = new JSONObject();
			json.put("charges", chargesJson);
			json.put("payments", paymentsJson);
			json.put("cashFlow", cashFlowson);
			  
			response(req, resp, json);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
}
