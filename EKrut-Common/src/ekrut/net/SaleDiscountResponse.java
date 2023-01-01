package ekrut.net;

import java.io.Serializable;
import java.util.ArrayList;

import ekrut.entity.SaleDiscount;

public class SaleDiscountResponse implements Serializable {

	private static final long serialVersionUID = 392297841156967599L;
	
	private ResultType result;
	private ArrayList<SaleDiscount> sales;
	
	public SaleDiscountResponse(ResultType result) {
		this.result = result;
	}
	
	public SaleDiscountResponse(ResultType result, ArrayList<SaleDiscount> sales) {
		this.result = result;
		this.sales = sales;
	}

	public ResultType getResult() {
		return result;
	}

	public ArrayList<SaleDiscount> getSales() {
		return sales;
	}
}
