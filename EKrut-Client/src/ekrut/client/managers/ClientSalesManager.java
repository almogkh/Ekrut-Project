package ekrut.client.managers;

import java.util.ArrayList;

import ekrut.client.EKrutClient;
import ekrut.entity.SaleDiscount;
import ekrut.entity.User;
import ekrut.net.ResultType;
import ekrut.net.SaleDiscountRequest;
import ekrut.net.SaleDiscountRequestType;
import ekrut.net.SaleDiscountResponse;

/**
 * Manages sales related operations on the client.
 * 
 * @author Almog Khaikin
 */
public class ClientSalesManager extends AbstractClientManager<SaleDiscountRequest, SaleDiscountResponse> {

	/**
	 * Constructs a new manager that uses the provided client instance.
	 * 
	 * @param client the client instance the manager will use
	 */
	public ClientSalesManager(EKrutClient client) {
		super(client, SaleDiscountResponse.class);
	}

	/**
	 * Creates a new sale template.
	 * 
	 * @param sale the sale template to create
	 * @return the result the server returned for the operation
	 */
	public ResultType createSaleTemplate(SaleDiscount sale) {
		SaleDiscountRequest request = new SaleDiscountRequest(sale);
		SaleDiscountResponse response = sendRequest(request);

		return response.getResult();
	}

	/**
	 * Retrieves all sale templates from the server.
	 * 
	 * @return the list of sale templates available on the server
	 */
	public ArrayList<SaleDiscount> fetchSaleTemplates() {
		SaleDiscountRequest request = new SaleDiscountRequest();
		SaleDiscountResponse response = sendRequest(request);

		return response.getSales();
	}

	/**
	 * Retrieves the list of active sales for the area the logged in manager is
	 * responsible for.
	 * 
	 * @return the list of active sales for the area
	 */
	public ArrayList<SaleDiscount> fetchActiveSales() {
		User user = client.getClientSessionManager().getUser();
		SaleDiscountRequest request = new SaleDiscountRequest(SaleDiscountRequestType.FETCH_SALES_BY_AREA,
				user.getArea());
		SaleDiscountResponse response = sendRequest(request);

		return response.getSales();
	}

	/**
	 * Fetches all active sales that are currently ongoing for the given ekrut
	 * location.
	 * 
	 * @param ekrutLocation the location for which active sales are to be fetched
	 * @return an ArrayList of SaleDiscount objects representing the active sales
	 */
	public ArrayList<SaleDiscount> fetchActiveSales(String ekrutLocation) {
		SaleDiscountRequest request = new SaleDiscountRequest(SaleDiscountRequestType.FETCH_SALES_BY_LOCATION,
				ekrutLocation);
		SaleDiscountResponse response = sendRequest(request);

		return response.getSales();
	}

	/**
	 * Activates a sale for the area the manager is responsible for.
	 * 
	 * @param discountId the ID of the sale that should be activated
	 * @return the result of the operation
	 */
	public ResultType activateSaleForArea(int discountId) {
		User user = client.getClientSessionManager().getUser();
		SaleDiscountRequest request = new SaleDiscountRequest(SaleDiscountRequestType.ACTIVATE_SALE_FOR_AREA,
				discountId, user.getArea());
		SaleDiscountResponse response = sendRequest(request);

		return response.getResult();
	}

	/**
	 * Deactivates a sale for the area the manager is responsible for.
	 * 
	 * @param discountId the ID of the sale that should be deactivated
	 * @return the result of the operation
	 */
	public ResultType deactivateSaleForArea(int discountId) {
		User user = client.getClientSessionManager().getUser();
		SaleDiscountRequest request = new SaleDiscountRequest(SaleDiscountRequestType.DEACTIVATE_SALE_FOR_AREA,
				discountId, user.getArea());
		SaleDiscountResponse response = sendRequest(request);

		return response.getResult();
	}
}
