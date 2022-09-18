package entities.concretes;

import entities.abstracts.EntityI;

public class Product implements EntityI {
	private long productId;
	private String productName;
	private double salesPrice;
	private String imagePath;
	private Category category;
	
	public Product() {}
	
	public Product(long productId, String productName, double salesPrice, String imagePath) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.salesPrice = salesPrice;
		this.imagePath = imagePath;
	}

	public Product(long productId, String productName, double salesPrice, String imagePath, Category category) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.salesPrice = salesPrice;
		this.imagePath = imagePath;
		this.category = category;
	}

	public long getProductId() {
		return productId;
	}
	
	public void setProductId(long productId) {
		this.productId = productId;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public double getSalesPrice() {
		return salesPrice;
	}
	
	public void setSalesPrice(double salesPrice) {
		this.salesPrice = salesPrice;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	
}
