package frontend;

public final class WebApiUrls {
	private WebApiUrls() {}
	
	public static final String categoryListAll = "http://localhost:8080/backend/api/category/listAll";
	public static final String listProductsByCategory = "http://localhost:8080/backend/api/products?categoryId=%d";
	public static final String findProductById = "http://localhost:8080/backend/api/product?productId=%d";
	
	public static final String userCheck = "http://localhost:8080/backend/api/user/check";
	public static final String userCreate = "http://localhost:8080/backend/api/user/create";
	
	public static final String cartList = "http://localhost:8080/backend/api/cart/view?cartId=%d";
	public static final String cartCreate = "http://localhost:8080/backend/api/cart/create?customerName=%s";
	public static final String addProductToCart = "http://localhost:8080/backend/api/cart/add";
	public static final String deleteCardProduct = "http://localhost:8080/backend/api/cart/remove?cartProductId=%d";
}
