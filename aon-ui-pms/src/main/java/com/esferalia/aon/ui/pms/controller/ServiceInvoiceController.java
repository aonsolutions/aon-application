package com.esferalia.aon.ui.pms.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR;
import static com.code.aon.ui.common.ICommonMessages.PMS_DAMAGES;
import static com.code.aon.ui.common.ICommonMessages.PMS_DEPOSITS;
import static com.code.aon.ui.common.ICommonMessages.PMS_SERVICES;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.asset.Asset;
import com.code.aon.asset.AssetActivity;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.invoicing.ReservationInvoiceTo;
import com.esferalia.aon.pms.invoicing.ReservationInvoiceTo.HotelService;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.pms.invoicing.ReservationInvoicing;
import com.esferalia.aon.pms.sql.ISQLConstants;
import com.esferalia.aon.pms.sql.SQLUtils;

public class ServiceInvoiceController extends BasicController implements IPmsConstants, ICalculableContainer, ISQLConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInvoiceController.class);

	private ReservationUtils reservationUtils;
	private ReservationInvoiceTo reservationInvoiceTo;
	private boolean showRectificationWindow;
	private Invoice invoiceToRectify;
	private ProjectReservation projectReservation;
	private List<SelectItem> roomList;
	
	private IControllerListener currentReservationFilter;
	
	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		}
		return reservationUtils;
	}

	public ReservationInvoiceTo getReservationInvoiceTo() {
		return reservationInvoiceTo;
	}

	public void setReservationInvoiceTo(ReservationInvoiceTo reservationInvoiceTo) {
		this.reservationInvoiceTo = reservationInvoiceTo;
	}

	public boolean isShowRectificationWindow() {
		return showRectificationWindow;
	}

	public void setShowRectificationWindow(boolean showRectificationWindow) {
		this.showRectificationWindow = showRectificationWindow;
	}

	public Invoice getInvoiceToRectify() {
		return invoiceToRectify;
	}

	public void setInvoiceToRectify(Invoice invoiceToRectify) {
		this.invoiceToRectify = invoiceToRectify;
	}
	
	public ProjectReservation getProjectReservation() {
		return projectReservation;
	}

	public void setProjectReservation(ProjectReservation projectReservation) {
		this.projectReservation = projectReservation;
	}

	public List<SelectItem> getRoomList() {
		if (roomList == null || roomList.size() == 0) {
			roomList = obtainRoomList();
		}
		return roomList;
	}

	public void setRoomList(List<SelectItem> roomList) {
		this.roomList = roomList;
	}

	public IControllerListener getCurrentReservationFilter() {
		if ( this.currentReservationFilter == null ) {
			this.currentReservationFilter = new CurrentReservationFilter();
		}
		return this.currentReservationFilter;
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), new Date());
		onSearch(event);
	}

	@Override
	public void onReset(ActionEvent event) {
		try {
			if (!PosUtils.isUserPosShiftOpened()) {
				String msg = "No se puede Facturar. El Usuario no ha abierto la Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			setNevv(true);
			setProjectReservation((ProjectReservation)BeanManager.getManagerBean(ProjectReservation.class).createNewTo());
			setReservationInvoiceTo(new ReservationInvoiceTo(true));
			getReservationInvoiceTo().setHotel(obtainHotel());
			fillHotelData();
			onNewService(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private Hotel obtainHotel() throws ManagerBeanException {
		PmsCollectionsController collections = (PmsCollectionsController)AonUtil.getRegisteredBean(COLLECTIONS_CONTROLLER_NAME);
		List<SelectItem> hotelList = collections.getCurrentUserServiceHotels();
		return (hotelList.size() > 0) ? (Hotel)hotelList.get(0).getValue() : null;
	}

	public void onReservationChanged(LookupChangeEvent event) throws ManagerBeanException{
		ProjectReservation reservation = (ProjectReservation)event.getNewValue();
		if (reservation != null && reservation.getId() != null) {
			setReservationInvoiceTo(new ReservationInvoiceTo(true));
			getReservationInvoiceTo().setHotel(reservation.getHotel());
			fillHotelData();

			getReservationInvoiceTo().setServices(new LinkedList<HotelService>());
			onNewService(null);
		}
	}
	
	public void onInvoiceHotelChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			getReservationInvoiceTo().setHotel((Hotel)event.getNewValue());
			fillHotelData();

			getReservationInvoiceTo().setServices(new LinkedList<HotelService>());
			onNewService(null);
		}
	}

	private void fillHotelData() throws ManagerBeanException {
		getReservationInvoiceTo().getRegistry().setId(getReservationInvoiceTo().getHotel().getCustomer().getRegistry().getId());
		getReservationInvoiceTo().setRoom(null);
		getReservationInvoiceTo().setGuest(null);
		setRoomList(null);
	}

	private String obtainHotelInvoiceSeries() throws ManagerBeanException {
		List<ITransferObject> seriesList = getHotelSeries(false);
		return (seriesList.size() > 0) ? ((Series)seriesList.get(0)).getCode() : "";
	}

	private String obtainHotelRectificationSeries() throws ManagerBeanException {
		List<ITransferObject> seriesList = getHotelSeries(true);
		return (seriesList.size() > 0) ? ((Series)seriesList.get(0)).getCode() : "";
	}

	public List<ITransferObject> getHotelSeries(boolean rectification) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), getReservationInvoiceTo().getHotel().getScope().getId());
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), true);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		if (rectification) {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_RECTIFICATION), true);
		} else {
			criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), true);
		}
		return seriesBean.getList(criteria);
	}

	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private List<SelectItem> obtainRoomList() {
		List<SelectItem> roomList = new LinkedList<SelectItem>();
		if (getReservationInvoiceTo().getHotel() != null) {
			Connection connection = null;
			PreparedStatement roomStmt = null;
			ResultSet roomRs = null;
			try {
				boolean isReservation = getProjectReservation() != null && getProjectReservation().getId() != null;
				connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
				roomStmt = connection.prepareStatement(getRoomListSQL(isReservation), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				Date roomDate = getReservationInvoiceTo().getIssueDate();
				if (!isReservation) {
					SQLUtils.setInt(roomStmt, 1, getReservationInvoiceTo().getHotel().getId());
				} else {
					SQLUtils.setInt(roomStmt, 1, getProjectReservation().getId());
					roomDate = DateUtils.addDays(getProjectReservation().getEndDate(), -1);
				}
				SQLUtils.setDate(roomStmt, 2, roomDate);
				SQLUtils.setDate(roomStmt, 3, roomDate);
				SQLUtils.setDate(roomStmt, 4, roomDate);
				roomRs = roomStmt.executeQuery();
				while (roomRs.next()) {
					int reservationRoomDetailId = roomRs.getInt(RESERVATION_ROOM_DETAIL);
					int domain = roomRs.getInt(DOMAIN);
					int reservationRoomId = roomRs.getInt(RESERVATION_ROOM);
					int reservationId = roomRs.getInt(RESERVATION);
					int assetActivityId = roomRs.getInt(ASSET_ACTIVITY);
					Date stayDate = roomRs.getDate(STAY_DATE);
					int assetId = roomRs.getInt(ASSET);
					String roomNumber = roomRs.getString(ROOM_NUMBER);

					ProjectReservationRoom reservationRoom = new ProjectReservationRoom();
					reservationRoom.setId(reservationRoomId);
					reservationRoom.setProjectReservation(new ProjectReservation());
					reservationRoom.getProjectReservation().setId(reservationId);

					AssetActivity assetActivity = new AssetActivity();
					assetActivity.setId(assetActivityId);
					assetActivity.setDate(stayDate);
					assetActivity.setAsset(new Asset()); 
					assetActivity.getAsset().setId(assetId);
					assetActivity.getAsset().setName(roomNumber);

					ProjectReservationRoomDetail reservationRoomDetail = new ProjectReservationRoomDetail();
					reservationRoomDetail.setId(reservationRoomDetailId);
					reservationRoomDetail.setDomain(domain);
					reservationRoomDetail.setProjectReservationRoom(reservationRoom);
					reservationRoomDetail.setAssetActivity(assetActivity);

					SelectItem selectItem = new SelectItem(reservationRoomDetail, roomNumber);
					roomList.add(selectItem);
				}
			} catch (Throwable e) {
				try {
					connection.rollback();
				} catch (SQLException ex) {
				}
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			} finally {
				SQLUtils.closeQuietly(roomRs);
				SQLUtils.closeQuietly(roomStmt);
				SQLUtils.closeQuietly(connection);
			}
		}
		return roomList;
	}

	private String getRoomListSQL(boolean isReservation) throws ManagerBeanException {
		StringBuffer stmt = new StringBuffer();
		stmt.append("SELECT PRRD.id AS " + RESERVATION_ROOM_DETAIL + ", PRRD.domain AS " + DOMAIN);
		stmt.append(", PRR.id AS " + RESERVATION_ROOM + ", PR.project AS " + RESERVATION); 
		stmt.append(", AA.id AS " + ASSET_ACTIVITY + ", AA.date AS " + STAY_DATE);
		stmt.append(", A.id AS " + ASSET + ", A.name AS " + ROOM_NUMBER);
		stmt.append(" FROM project_reservation AS PR, project_reservation_room AS PRR, project_reservation_room_detail AS PRRD");
		stmt.append(", asset_activity AS AA, asset AS A");
		stmt.append(" WHERE" + DomainManager.getSQLWhereClause("PR.domain"));
		stmt.append((!isReservation) ? " AND PR.hotel = ?" : " AND PR.project = ?");
		stmt.append(" AND PR.start_date <= ?");
		stmt.append(" AND PR.end_date > ?");
		stmt.append(" AND PR.status != " + ReservationStatus.CANCELLED.ordinal());
		stmt.append(" AND PR.status != " + ReservationStatus.BLOCKED.ordinal());
		stmt.append(" AND PR.project = PRR.project_reservation");
		stmt.append(" AND PRR.id = PRRD.project_reservation_room");
		stmt.append(" AND PRRD.asset_activity = AA.id");
		stmt.append(" AND AA.date = ?");
		stmt.append(" AND AA.asset = A.id");
		stmt.append(" ORDER BY A.name");

		return stmt.toString();
	}

	public void onInvoiceRoomChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			getReservationInvoiceTo().setRoom((ProjectReservationRoomDetail)event.getNewValue());

			getReservationInvoiceTo().setServices(new LinkedList<HotelService>());
			onNewService(null);
		}
	}

	public List<SelectItem> getGuests() throws ManagerBeanException {
		List<SelectItem> guestList = new LinkedList<SelectItem>();
		if (getReservationInvoiceTo().getRoom() != null) {
			IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
			Criteria criteria = new Criteria();
			String alias = reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_ID);
			criteria.addEqualExpression(alias, getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation().getId());
			criteria.addOrder(reservationGuestBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_GUEST_INDEX));
			for (ITransferObject ito : reservationGuestBean.getList(criteria)) {
				ProjectReservationGuest reservationGuest = (ProjectReservationGuest)ito;
				SelectItem selectItem = new SelectItem(reservationGuest, reservationGuest.getFullName());
				guestList.add(selectItem);
			}
		}
		return guestList;
	}

	public void onInvoiceGuestChanged(ValueChangeEvent event) throws ManagerBeanException {
		ProjectReservationGuest reservationGuest = new ProjectReservationGuest();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservationGuest = (ProjectReservationGuest)event.getNewValue();
		}
		fillGuestData(reservationGuest);
	}

	private void fillGuestData(ProjectReservationGuest reservationGuest) {
		getReservationInvoiceTo().getRegistry().setName(reservationGuest.getFullName());
		getReservationInvoiceTo().getRegistry().setDocumentType(reservationGuest.getDocumentType());
		getReservationInvoiceTo().getRegistry().setDocumentCountry(reservationGuest.getDocumentCountry());
		getReservationInvoiceTo().getRegistry().setDocument(reservationGuest.getDocument());

		getReservationInvoiceTo().getAddress().setAddress(StringUtils.abbreviate(reservationGuest.getAddress(), 45));
		getReservationInvoiceTo().getAddress().setNumber(StringUtils.abbreviate(reservationGuest.getNumber(), 12));
		getReservationInvoiceTo().getAddress().setAddress2(StringUtils.abbreviate(reservationGuest.getAddress2(), 45));
		getReservationInvoiceTo().getAddress().setZip(StringUtils.abbreviate(reservationGuest.getZip(), 16));
		getReservationInvoiceTo().getAddress().setCity(StringUtils.abbreviate(reservationGuest.getCity(), 45));
		getReservationInvoiceTo().getAddress().setProvince(StringUtils.abbreviate(reservationGuest.getProvince(), 45));
	}

	public List<SelectItem> getServiceTypes() {
		List<SelectItem> serviceTypes = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(ProductType.SERVICE, AonUtil.getMessage(PMS_SERVICES));
		serviceTypes.add(item);
		item = new SelectItem(ProductType.COMMERCIAL_PRODUCT, AonUtil.getMessage(PMS_DEPOSITS));
		serviceTypes.add(item);
		item = new SelectItem(ProductType.EXTERNAL_WORK, AonUtil.getMessage(PMS_DAMAGES));
		serviceTypes.add(item);
		return serviceTypes;
	}

	public void onInvoiceServiceTypeChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			getReservationInvoiceTo().setServiceType((ProductType)event.getNewValue());
			getReservationInvoiceTo().setServices(new LinkedList<HotelService>());
			onNewService(null);
		}
	}

	public void onNewService(ActionEvent event) {
		try {
			double quantity = 1;
			Date fromDate = new Date();
			Date toDate = new Date();
			if (getInvoiceServicesCount() > 0) {
				quantity = getReservationInvoiceTo().getLastService().getQuantity();
				fromDate = getReservationInvoiceTo().getLastService().getFromDate();
				toDate = getReservationInvoiceTo().getLastService().getToDate();
			} else if (getReservationInvoiceTo().getRoom() != null) {
				fromDate = getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation().getStartDate();
				toDate = getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation().getEndDate();
				toDate = DateUtils.addDays(toDate, -1);
			}

			HotelService hotelService = getReservationInvoiceTo().getNewService();
			hotelService.setFromDate(fromDate);
			hotelService.setToDate(toDate);
			hotelService.setItem((Item)getHotelServices().get(0).getValue());
			hotelService.setQuantity(quantity);
			getReservationInvoiceTo().getServices().add(hotelService);
			onInvoiceServiceChanged(null);
		} catch (ManagerBeanException ex) {
			String msg = "Error al seleccionar Servicio.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onRemoveService(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        int serviceIndex = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("hotelInvoiceServiceIndex"));
        getReservationInvoiceTo().getServices().remove(serviceIndex);
		onInvoiceServiceChanged(null);
	}

	public int getInvoiceServicesCount() {
		return getReservationInvoiceTo().getServicesCount();
	}

	public List<SelectItem> getHotelServices() throws ManagerBeanException {
		List<SelectItem> servicesList = new LinkedList<SelectItem>();
		if (getReservationInvoiceTo().getHotel() != null) {
			IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
			Criteria criteria = new Criteria();
			String alias = catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID);
			criteria.addEqualExpression(alias, getReservationInvoiceTo().getHotel().getServiceCatalogue().getId());
			alias = catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_PRODUCT_TYPE);
			criteria.addEqualExpression(alias, getReservationInvoiceTo().getServiceType());
			criteria.addNotNullExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM));
			criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_PRODUCT_NAME));
			criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_DETAIL));
			criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_DETAIL2));
			criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_DETAIL3));
			criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_SERIAL_NUMBER));
			for (ITransferObject ito : catalogueItemBean.getList(criteria)) {
				CatalogueItem catalogueItem = (CatalogueItem)ito;
				SelectItem selectItem = new SelectItem(catalogueItem.getItem(), catalogueItem.getItem().getFullName());
				servicesList.add(selectItem);
			}
		}
		return servicesList;
	}

	public void onInvoiceServiceChanged(ActionEvent event) {
		getReservationInvoiceTo().setFinances(new LinkedList<Finance>());
		onNewFinance(null);
	}

	public double getServicesAmount() {
		IPriceStrategy strategy = PriceStrategyFactory.getPriceStrategy();
		for (HotelService hotelService : getReservationInvoiceTo().getServices()) {
			if (hotelService.getItem() != null) {
				double prices = 0;
				Date date = hotelService.getFromDate();
				while (date.compareTo(hotelService.getToDate()) <= 0) {
					prices += strategy.getUnitPrice(hotelService, date, getReservationInvoiceTo().getHotel().getCustomer());
					date = DateUtils.addDays(date, 1);
				}
				hotelService.setPrice(CommonUtil.round(prices, 4));
				hotelService.setTaxableBase(strategy.getBasePrice(hotelService));
			}
		}
		return strategy.getTotalPrice(this, getReservationInvoiceTo().getHotel().getCustomer());
	}

	public void onNewFinance(ActionEvent event) {
		Finance finance = new Finance();
		finance.setAmount(CommonUtil.round(getServicesAmount() - getReservationInvoiceTo().getFinancesAmount()));
		getReservationInvoiceTo().getFinances().add(finance);
	}

	public void onRemoveFinance(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        int financeIndex = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("hotelInvoiceFinanceIndex"));

        Finance financeToRemove = getReservationInvoiceTo().getFinances().get(financeIndex);
        getReservationInvoiceTo().getFinances().remove(financeIndex);

        Finance previousFinance = getReservationInvoiceTo().getFinances().get(financeIndex-1);
        previousFinance.setAmount(CommonUtil.round(previousFinance.getAmount() + financeToRemove.getAmount()));
	}

	public void onInvoice(ActionEvent event) {
		try {
			if (validateInvoice()) {
				ProjectReservation reservation = null;
				if (getReservationInvoiceTo().getRoom() != null) {
					reservation = getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation();
				}
				getReservationInvoiceTo().setSeries(obtainHotelInvoiceSeries());
				getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));
				getReservationInvoiceTo().setComments(obtainInvoiceComments(getReservationInvoiceTo().getServices()));
				getReservationInvoiceTo().setDirectCustomer(true);
				getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());
		
				ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
				Invoice invoice = reservationInvoicing.invoice(getReservationInvoiceTo(), reservation);
		
				onEditSearch(event);
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
				onSearch(event);
				getModel().setRowIndex(0);
				onSelect(event);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private String obtainInvoiceComments(List<HotelService> services) {
		String comments = "";
		for (HotelService service: services) {
			if (StringUtils.isNotEmpty(service.getItem().getDescription())) {
				comments += service.getItem().getProduct().getName() + " - "+ service.getItem().getDescription() + "\n";
			}
		}
		return comments;
	}

	private boolean validateInvoice() throws ManagerBeanException {
		if (!isFinancesAmountOk()) {
			String msg = "El importe de los Pagos no coincide con el importe de la Reserva.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if (!isPayMethodOk()) {
			String msg = "La Forma de Pago es obligatoria.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		Double dailyCashLimit = getReservationUtils().obtainDailyCashLimit();
		if (dailyCashLimit != null) {
			double cashAmount = getReservationInvoiceTo().getFinancesCashAmount();
			if (cashAmount != 0) {
				Registry registry = getReservationInvoiceTo().getRegistry();
				cashAmount += getReservationUtils().getDailyRegistryFinanceCashAmount(getReservationInvoiceTo().getIssueDate(), registry);
				if (cashAmount > dailyCashLimit) {
					String msg = "No se puede Facturar. El importe en Efectivo supera el límite diario.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
		}

		return isServicesDatesOk();
	}

	public boolean isFinancesAmountOk() {
		return CommonUtil.round(getServicesAmount() - getReservationInvoiceTo().getFinancesAmount()) == 0;
	}

	public boolean isPayMethodOk() {
		for (Finance finance : getReservationInvoiceTo().getFinances()) {
			if (finance.getPayMethod() == null && finance.getTotalAmount() != 0) {
				return false;
			}
		}
		return true;
	}

	private boolean isServicesDatesOk() {
		ProjectReservation reservation = null;
		if (getReservationInvoiceTo().getRoom() != null) {
			reservation = getReservationInvoiceTo().getRoom().getProjectReservationRoom().getProjectReservation();
		}

		for (HotelService hotelService : getReservationInvoiceTo().getServices()) {
			Date fromDate = hotelService.getFromDate();
			Date toDate = hotelService.getToDate();
			if (fromDate.compareTo(toDate) > 0) {
				String msg = "La Fecha de inicio del Servicio " + hotelService.getItem().getProduct().getName() + " no puede ser mayor que la Fecha de fin.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else if (CommonUtil.getDaysBetweenDates(fromDate, toDate) > 30) {
				String msg = "No se puede facturar un Servicio de mas de 30 dias.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} else if ((reservation != null) && (fromDate.compareTo(reservation.getStartDate()) < 0 || toDate.compareTo(reservation.getEndDate()) > 0)) {
				String msg = "Las fechas del Servicio no estan dentro de la Reserva.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		return true;
	}

	public void onRectifyInvoiceShow(ActionEvent event) {
		try {
			if (!getModel().isRowAvailable()) {
				setShowRectificationWindow(false);
				String msg = "No se puede Abonar. Factura no disponible.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (!PosUtils.isUserPosShiftOpened()) {
				setShowRectificationWindow(false);
				String msg = "No se puede Abonar. El Usuario no ha abierto la Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			Invoice invoice = (Invoice)getModel().getRowData();
			if (!FinanceUtil.isValidLimitRectificationDate(invoice)) {
				String message = AonUtil.addErrorMessageFromBundle(FINANCE_OPERATION_NOT_ALLOWED_PERIOD_EXCEEDED_ERROR);
				throw new AbortProcessingException(message);
			}

			setInvoiceToRectify(invoice);
			setReservationInvoiceTo(new ReservationInvoiceTo(true));
			getReservationInvoiceTo().setHotel(obtainRectificationHotel());
			getReservationInvoiceTo().setPosShift(PosUtils.getUserPosShift());
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private Hotel obtainRectificationHotel() throws ManagerBeanException {
		for (ITransferObject ito : getInvoiceToRectify().getDetailList()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)ito;
			IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_ID), invoiceDetail.getWorkPlace().getId());
			for (ITransferObject itr : hotelBean.getList(criteria)) {
				return (Hotel)itr;
			}
		}
		return null;
	}

	public void onRectify(ActionEvent event) {
		try {
			getReservationInvoiceTo().setSeries(obtainHotelRectificationSeries());
			getReservationInvoiceTo().setNumber(obtainSeriesMaxNumber(getReservationInvoiceTo().getSeries()));

			ReservationInvoicing reservationInvoicing = new ReservationInvoicing();
			Invoice rectifier = reservationInvoicing.rectify(getInvoiceToRectify(), getReservationInvoiceTo(), false);

			onEditSearch(event);
			getCriteria().addEqualExpression(getFieldName(IEntityAlias.INVOICE_ID), rectifier.getId());
			onSearch(event);
			getModel().setRowIndex(0);
			onSelect(event);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onPrintInvoice(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			SaleInvoiceController invoiceController = (SaleInvoiceController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.load(event, ((Invoice)getModel().getRowData()).getId());
		}
	}


	@Override
	public Registry getRegistry() {
		return getReservationInvoiceTo().getRegistry();
	}

	@Override
	public Date getDate() {
		return getReservationInvoiceTo().getIssueDate();
	}

	@Override
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0.0");
	}

	@Override
	public List<?> getDetailList() {
		return getReservationInvoiceTo().getServices();
	}
	
	public boolean isInvoiceRectificable() {
		try {
			if (getModel().isRowAvailable()) {
				Invoice invoice = (Invoice)getModel().getRowData();
				return ProjectReservationController.isInvoiceRectificable(invoice, null);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
		return false;
	}

	private static class CurrentReservationFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		@Override
		public void beforeModelSearched(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {					
				controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.BLOCKED);
				controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.CANCELLED);
				controller.getCriteria().addLessThanOrEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), new Date());
				controller.getCriteria().addGreaterThanOrEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_END_DATE), new Date());
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering reservation", e);
			}
		}
		
	}
	
}