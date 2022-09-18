package entities.concretes;

import entities.abstracts.EntityI;

public class CartProduct implements EntityI {
	private long id;
	private long cartId;
	private long productId;
	private int salesQuantity;
	private double salesPrice;
	private double taxRate;
	private double lineAmount;
	
	public CartProduct() {}
	
	public CartProduct(long cartId, long productId, int salesQuantity, double salesPrice, double taxRate,
			double lineAmount) {
		this(0, cartId, productId, salesQuantity, salesPrice, taxRate, lineAmount);
	}

	public CartProduct(long id, long cartId, long productId, int salesQuantity, double salesPrice, double taxRate,
			double lineAmount) {
		super();
		this.id = id;
		this.cartId = cartId;
		this.productId = productId;
		this.salesQuantity = salesQuantity;
		this.salesPrice = salesPrice;
		this.taxRate = taxRate;
		this.lineAmount = lineAmount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCartId() {
		return cartId;
	}

	public void setCartId(long cartId) {
		this.cartId = cartId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public int getSalesQuantity() {
		return salesQuantity;
	}

	public void setSalesQuantity(int salesQuantity) {
		this.salesQuantity = salesQuantity;
	}

	public double getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(double salesPrice) {
		this.salesPrice = salesPrice;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

	public double getLineAmount() {
		return lineAmount;
	}

	public void setLineAmount(double lineAmount) {
		this.lineAmount = lineAmount;
	}
	
}
