package com.code.aon.ui.commercial.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.util.DiscountExpression;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.commission.CommissionCategory;
import com.esferalia.aon.occam.api.model.commission.CommissionItem;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.commission.CommissionTypeCommission;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommission;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommissionStatus;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferProperties;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.watson.server.AonDateUtils;

@WebServlet(name = "CommissionCalculationServlet", urlPatterns = {"/commission_calculation/*",
												   "/aon_gwt_aio/commission_calculation/*",
												   "/aon_gwt_commercial/commission_calculation/*"})
public class CommissionCalculationServlet extends HttpServlet implements Serializable {
	private static final Logger LOGGER  = Logger.getLogger(CommissionCalculationServlet.class.getName());
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject json = getRequestJSON(req);
		complete(json);		
		
		String domainName = req.getServerName();
		Integer domainId = Integer.parseInt(req.getParameter("domain"));
		String userName = req.getRemoteUser();
	
		Domain domain = AON.getDomain(domainName, domainId, userName);
		
		calculate(domain, userName);
	}
	
	public JSONObject getRequestJSON(HttpServletRequest req){
		String line = "";
		StringBuilder bld = new StringBuilder();
		try {
			while((line = req.getReader().readLine()) != null){
				bld.append(" " + line);
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		String s = checkString(bld.toString());
		if(s == null || "".equals(s)){
			s = "{}";
		}
		try {
			return new JSONObject(s);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void complete(JSONObject json) {
		try {
			setSeller(json.opt("seller") != null ? json.optInt("seller") : null);
			setConfidential(json.opt("confidential") != null ? json.optInt("confidential") == 1 : false);
			setFromDate(json.opt("from_date") != null ? AonDateUtils.dateTimeParse(json.getString("from_date")) : null);
			setToDate(json.opt("to_date") != null ? AonDateUtils.dateTimeParse(json.getString("to_date")) : null);
			setFromNumber(json.opt("from_number") != null ? json.optInt("from_number") : null);
			setToNumber(json.opt("toNumber") != null ? json.optInt("toNumber") : null);
			setSeries(json.opt("series") != null ? json.optString("series") : null);
			setWorkplace(json.opt("workplace") != null ? json.optInt("workplace") : null);
			setTarget(json.opt("targer") != null ? json.optInt("target") : null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String checkString(String str){
		return new String(str.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8") );
	}
	
	private Integer seller;
	private Integer workplace;
	private String series;

	private Date fromDate;
	private Date toDate;

	private Integer fromNumber;
	private Integer toNumber;
	
	private Boolean confidential;
	
	private Integer target;
		
	public Integer getSeller() {
		return seller;
	}
	public void setSeller(Integer seller) {
		this.seller = seller;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public Integer getFromNumber() {
		return fromNumber;
	}
	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}
	public Integer getToNumber() {
		return toNumber;
	}
	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
	}
	public Integer getTarget() {
		return target;
	}
	public void setTarget(Integer target) {
		this.target = target;
	}
	public boolean isConfidential() {
		return confidential;
	}
	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
	}

	public Integer getWorkplace() {
		return workplace;
	}
	public void setWorkplace(Integer workplace) {
		this.workplace = workplace;
	}

	public void calculate(Domain domain, String login) {
		getOfferDetailStream(domain, login).forEach(od -> {
			OfferDetailCommission odc = AON.getOfferDetailCommission(domain.getName(), domain.getId(), login, f -> f.getOfferDetailProperty().eq(od.getId()));
			if(odc != null && odc.getAmount().equals(1.2)) {
				System.out.println(odc);
			}
			if(odc != null && OfferDetailCommissionStatus.PENDING.equals(odc.getStatus())) {
				double commission = getCommission(domain, login, od);
				double amount = CommonUtil.round(getBasePrice(od) * commission / 100);
				
				odc.setCommission(commission);
				odc.setAmount(amount);
				
				if(odc.getId() != null) AON.updateOfferDetailCommission(domain.getName(), domain.getId(), login, odc);
				else  AON.insertOfferDetailCommission(domain.getName(), domain.getId(), login, odc);
			}
		});
	}
	
	private Stream<com.esferalia.aon.occam.api.model.management.OfferDetail> getOfferDetailStream(Domain domain, String login){
		return AON.getOfferDetails(domain.getName(), domain.getId(), login, f -> offerDetailFilter(domain, f));
	}
	
	public Filter offerDetailFilter(Domain domain, OfferProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(getSeller() != null) {
			filter = filter.and(f.getSellerProperty().eq(getSeller()));
		}
		if(getWorkplace() != null) {
			filter = filter.and(f.getWorkplaceProperty().eq(getWorkplace()));			
		}
		if(getSeries() != null) {
			filter = filter.and(f.getSeriesProperty().eq(getSeries()));
		}
		
		return filter;
    }
	
	public double getCommission(Domain domain, String login, OfferDetail od) {
		Double commission = 0.0;
		if(od != null && od.getOffer() != null && od.getOffer().getSeller() != null) {
			Seller seller = AON.getSeller(domain.getName(), domain.getId(), login, f -> f.getRegistryProperty().eq(od.getOffer().getSeller().getId()));			
			CommissionType commissionType = seller.getCommissionType();
	
			if (commissionType != null && commissionType.getId() != null) {
				commission = commissionType.getRate();
				Date date = od.getOffer().getIssueDate();
				LinkedList<CommissionTypeCommission> ctcList = AON.getCommissionTypeCommissionStream(domain.getName(), domain.getId(), login,
					f -> f.getCommissionTypeProperty().eq(commissionType.getId())
					.and(f.getStartDateProperty().le(AonDateUtils.toSql(date)))
					.and(f.getEndDateProperty().ge(AonDateUtils.toSql(date)).or(f.getEndDateProperty().isNull())))
					.collect(Collectors.toCollection(LinkedList::new));
				
				for(CommissionTypeCommission ctc : ctcList) {
					LinkedList<CommissionItem> ciList = AON.getCommissionItemStream(domain.getName(), domain.getId(), login,
						f -> f.getCommissionProperty().eq(ctc.getCommission())
						.and(f.getItemProperty().eq(od.getItem().getId()))
						.and(f.getQuantityProperty().le(od.getQuantity())))
						.collect(Collectors.toCollection(LinkedList::new));
					for(CommissionItem ci : ciList) {
						return ci.getRate();
					}
				
					LinkedList<CommissionCategory> ccList = AON.getCommissionCategoryStream(domain.getName(), domain.getId(), login,
						f -> f.getCommissionProperty().eq(ctc.getCommission())
						.and(f.getCategoryProperty().eq(od.getItem().getProduct().getCategory()))
						.and(f.getQuantityProperty().le(od.getQuantity())))
						.collect(Collectors.toCollection(LinkedList::new));
					for(CommissionCategory cc : ccList) {
						return cc.getRate();
					}
				}
			}
		}
		return commission;
	}
	
	public double getBasePrice(OfferDetail od) {
		double price = 0;
		price = od.getPrice();
		price = price * od.getQuantity();
		DiscountExpression de = new DiscountExpression(od.getDiscountExpression());
		if (de.getDiscounts() != null) {
			for (int i = 0;i<de.getDiscounts().length;i++) {
				price = price * ( 1 - de.getDiscounts()[i] /100);
			}
		}
		return CommonUtil.round(price, 4);
	}
	
}