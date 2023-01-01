package ekrut.entity;

import java.io.Serializable;
import java.time.LocalTime;

public class SaleDiscount implements Serializable {

	private static final long serialVersionUID = -8240267976906125824L;
	
	private int discountId;
	private SaleDiscountType type;
	private String area;
	private LocalTime startTime;
	private LocalTime endTime;
	private String dayOfSale;
	private boolean isActive;

	// Active Sale
	public SaleDiscount(int discountId, LocalTime startTime, LocalTime endTime,
						String dayOfSale, SaleDiscountType type, String area) {
		this.discountId = discountId;
		this.type = type;
		this.startTime = startTime;
		this.endTime = endTime;
		this.dayOfSale = dayOfSale;
		this.area = area;
		this.isActive = true;
	}
	// Template
	public SaleDiscount(int discountId, LocalTime startTime, LocalTime endTime,
						String dayOfSale, SaleDiscountType type) {
		this.discountId = discountId;
		this.type = type;
		this.startTime = startTime;
		this.endTime = endTime;
		this.dayOfSale = dayOfSale;
	}
	
	public int getDiscountId() {
		return discountId;
	}

	public SaleDiscountType getType() {
		return type;
	}

	public String getArea() {
		return area;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	// day of sale is a 7 length string which each day with sale will be in T for example:
	// TFTFFFF means that Sunday and Tuesday the sale is active.
	public String getDayOfSale() {
		return dayOfSale;
	}
	
	public boolean isActive() {
		return isActive;
	}

}
