package ekrut.net;

import java.io.Serializable;

import ekrut.entity.SaleDiscount;

public class SaleDiscountRequest implements Serializable {

	private static final long serialVersionUID = -5571513015464182751L;

	private SaleDiscountRequestType action;
	private SaleDiscount sale;
	private int discountId;
	private String area;

	public SaleDiscountRequest(SaleDiscount sale) {
		this.action = SaleDiscountRequestType.CREATE_SALE;
		this.sale = sale;
	}

	public SaleDiscountRequest(SaleDiscountRequestType action, int discountId, String area) {
		this.action = action;
		this.discountId = discountId;
		this.area = area;
	}

	public SaleDiscountRequest() {
		this.action = SaleDiscountRequestType.FETCH_SALE_TEMPLATES;
	}

	public SaleDiscountRequest(String area) {
		this.action = SaleDiscountRequestType.FETCH_SALES_BY_AREA;
		this.area = area;
	}

	public SaleDiscountRequestType getAction() {
		return action;
	}

	public SaleDiscount getSale() {
		return sale;
	}

	public int getDiscountId() {
		return discountId;
	}

	public String getArea() {
		return area;
	}
}
