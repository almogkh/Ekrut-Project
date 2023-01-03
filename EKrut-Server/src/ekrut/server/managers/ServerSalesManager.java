package ekrut.server.managers;

import java.util.ArrayList;

import ekrut.entity.SaleDiscount;
import ekrut.entity.User;
import ekrut.entity.UserType;
import ekrut.net.ResultType;
import ekrut.net.SaleDiscountRequest;
import ekrut.net.SaleDiscountResponse;
import ekrut.server.db.DBController;
import ekrut.server.db.SaleDiscountDAO;
import ocsf.server.ConnectionToClient;

/**
 * Manages sale related operations on the server side.
 * 
 * @author Almog Khaikin
 */
public class ServerSalesManager {

	private SaleDiscountDAO salesDAO;
	private ServerSessionManager serverSessionManager;
	
	public ServerSalesManager(DBController dbCon, ServerSessionManager serverSessionManager) {
		this.salesDAO = new SaleDiscountDAO(dbCon);
		this.serverSessionManager = serverSessionManager;
	}
	
	/**
	 * Dispatches a sale related request to the appropriate handler.
	 * 
	 * @param request the request to dispatch
	 * @param client  the client that sent the request
	 * @return        a response for the request
	 */
	public SaleDiscountResponse handleRequest(SaleDiscountRequest request, ConnectionToClient client) {
		switch (request.getAction()) {
		case CREATE_SALE:
			return createSaleTemplate(request.getSale());
		case FETCH_SALE_TEMPLATES:
			return fetchSaleTemplates();
		case FETCH_SALES_BY_AREA:
			return fetchSalesByArea(request.getArea());
		case ACTIVATE_SALE_FOR_AREA:
			return activateSaleForArea(request.getDiscountId(), request.getArea(), client);
		case DEACTIVATE_SALE_FOR_AREA:
			return deactivateSaleForArea(request.getDiscountId(), request.getArea(), client);
		default:
			return new SaleDiscountResponse(ResultType.UNKNOWN_ERROR);
		}
	}
	
	/**
	 * Creates a new sale template.
	 * 
	 * @param sale the template to create
	 * @return     response indicating whether the operation was successful or not
	 */
	private SaleDiscountResponse createSaleTemplate(SaleDiscount sale) {
		if (!salesDAO.createDiscountTemplate(sale))
			return new SaleDiscountResponse(ResultType.UNKNOWN_ERROR);
		return new SaleDiscountResponse(ResultType.OK);
	}
	
	/**
	 * Retrieves the full list of sale templates that are available.
	 * 
	 * @return the list of sale templates or an error if there was a problem fetching the templates
	 *         from the database
	 */
	private SaleDiscountResponse fetchSaleTemplates() {
		ArrayList<SaleDiscount> res = salesDAO.fetchSaleDiscountTemplatList();
		if (res == null)
			return new SaleDiscountResponse(ResultType.UNKNOWN_ERROR);
		return new SaleDiscountResponse(ResultType.OK, res);
	}
	
	/**
	 * Retrieves the list of sales that are active in an area.
	 * 
	 * @param area the area for which to retrieve the active sales
	 * @return     the list of active sales for the area or an error if there was a problem
	 */
	SaleDiscountResponse fetchSalesByArea(String area) {
		ArrayList<SaleDiscount> res = salesDAO.fetchActivateSaleDiscountListByArea(area);
		if (res == null)
			return new SaleDiscountResponse(ResultType.UNKNOWN_ERROR);
		return new SaleDiscountResponse(ResultType.OK, res);
	}
	
	/**
	 * Activates a sale in an area
	 * 
	 * @param discountId the ID of the sale template to activate
	 * @param area       the area in which to activate the sale
	 * @param client     the client that sent the request
	 * @return           response indicating whether the operation was successful or not
	 */
	private SaleDiscountResponse activateSaleForArea(int discountId, String area, ConnectionToClient client) {
		User user = serverSessionManager.getUser(client);
		
		if (user.getUserType() != UserType.MARKETING_WORKER || !user.getArea().equals(area))
			return new SaleDiscountResponse(ResultType.PERMISSION_DENIED);
		
		if (!salesDAO.activateSaleForArea(discountId, area))
			return new SaleDiscountResponse(ResultType.UNKNOWN_ERROR);
		
		return new SaleDiscountResponse(ResultType.OK);
	}
	
	/**
	 * Deactivates a sale in an area
	 * 
	 * @param discountId the ID of the sale template to deactivate
	 * @param area       the area in which to deactivate the sale
	 * @param client     the client that sent the request
	 * @return           response indicating whether the operation was successful or not
	 */
	private SaleDiscountResponse deactivateSaleForArea(int discountId, String area, ConnectionToClient client) {
		User user = serverSessionManager.getUser(client);
		
		if (user.getUserType() != UserType.MARKETING_WORKER || !user.getArea().equals(area))
			return new SaleDiscountResponse(ResultType.PERMISSION_DENIED);
		
		if (!salesDAO.deactivateSaleForArea(discountId, area))
			return new SaleDiscountResponse(ResultType.UNKNOWN_ERROR);
		
		return new SaleDiscountResponse(ResultType.OK);
	}
}
