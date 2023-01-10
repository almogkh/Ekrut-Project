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

	public SaleDiscount(int discountId, LocalTime startTime, LocalTime endTime, String dayOfSale, SaleDiscountType type,
			String area) {
		this.discountId = discountId;
		this.type = type;
		this.startTime = startTime;
		this.endTime = endTime;
		this.dayOfSale = dayOfSale;
		this.area = area;
		this.isActive = true;
	}

	// fetch Template
	public SaleDiscount(int discountId, LocalTime startTime, LocalTime endTime, String dayOfSale, SaleDiscountType type) {
		this.discountId = discountId;
		this.type = type;
		this.startTime = startTime;
		this.endTime = endTime;
		this.dayOfSale = dayOfSale;
	}

	// create template
	public SaleDiscount(LocalTime startTime, LocalTime endTime, String dayOfSale, SaleDiscountType type) {
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

	public String getDayOfSale() {
		return dayOfSale;
	}

	public boolean isActive() {
		return isActive;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SaleDiscount other = (SaleDiscount) obj;

		if (dayOfSale == null) {
			if (other.dayOfSale != null)
				return false;
		} else if (!dayOfSale.equals(other.dayOfSale))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	

}
