package entities.concretes;

import java.util.List;

import entities.abstracts.EntityI;

public class Cart implements EntityI {
	private long id;
	private double totalAmount;
	private String customerName;
	private List<CartProduct> cartProducts;
	
	public Cart() {}

	public Cart(double totalAmount, String customerName) {
		this(0, totalAmount, customerName);
	}
	
	public Cart(long id, double totalAmount, String customerName) {
		this(id,totalAmount,customerName,null);
	}
	
	public Cart(long id, double totalAmount, String customerName, List<CartProduct> cartProducts) {
		this.id = id;
		this.totalAmount = totalAmount;
		this.customerName = customerName;
		this.cartProducts = cartProducts;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public List<CartProduct> getCartProducts() {
		return cartProducts;
	}

	public void setCartProducts(List<CartProduct> cartProducts) {
		this.cartProducts = cartProducts;
	}
	
}
