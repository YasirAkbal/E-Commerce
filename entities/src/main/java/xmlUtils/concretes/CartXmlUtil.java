package xmlUtils.concretes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import entities.concretes.*;
import messages.XmlMessages;
import results.DataResult;
import results.ErrorDataResult;
import results.SuccessDataResult;
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;

public class CartXmlUtil implements XmlUtilI<Cart> {
	
	private final CartProductXmlUtil cartProductXmlUtil;
	
	public CartXmlUtil() {
		this.cartProductXmlUtil = new CartProductXmlUtil();
	}
	
	@Override
	public DataResult<Document> format(Cart cart) {
		Document document = null;
		
		try {
			document = XmlUtils.create(XmlAttributeNames.CART);
			
			Element cartElement = document.getDocumentElement();
			
			setAllFields(cart, document, cartElement);
		} catch (ParserConfigurationException e) {
			return new ErrorDataResult<Document>(XmlMessages.PARSING_ERROR);
		}

		return new SuccessDataResult<Document>(document);
	}

	@Override
	public DataResult<Document> format(List<Cart> carts) {
		Document document = null;
		Element cartList;
		
		try {
			document = XmlUtils.create(XmlAttributeNames.CARTS);
			cartList = document.getDocumentElement();
			
			for(Cart cart : carts) {
				Element cartElement = document.createElement(XmlAttributeNames.CART);
				setAllFields(cart, document, cartElement);
			}
		} catch (ParserConfigurationException e) {
			return new ErrorDataResult<Document>(XmlMessages.PARSING_ERROR);
		}
		
		return new SuccessDataResult<Document>(document);
	}

	@Override
	public Cart parse(Document document) {
		Element productElement = document.getDocumentElement();
		
		Cart cart = parseFromXml(productElement);

		return cart;
	}

	@Override
	public List<Cart> parseList(Document document) {
		Element cartListElement = document.getDocumentElement();
		List<Cart> carts = new ArrayList<Cart>();
		NodeList cartsElement = cartListElement.getElementsByTagName(XmlAttributeNames.CART);
		
		for(int i=0;i<cartsElement.getLength();i++) {
			Cart cart = parseCartFromXml((Element) cartsElement.item(i));
			carts.add(cart);
		}
		
		return carts;
	}
		
	private void setAllFields(Cart cart, Document document, Element cartElement) {
		setCartFields(cart, document, cartElement);
		
		if(cart.getCartProducts() != null)  {
			//formatCartProducts(cart.getCartProducts(), document, cartElement);
			cartProductXmlUtil.format(cart.getCartProducts(), document, cartElement);
		}
	}
	
	private void formatCartProducts(List<CartProduct> cartProducts, Document document, Element cartElement) {	
		for(CartProduct cartProduct : cartProducts) {
			Element cartProductElement = document.createElement(CartProductXmlUtil.XmlAttributeNames.CART_PRODUCT);
			cartProductXmlUtil.setFields(cartProduct, document, cartProductElement);
			cartElement.appendChild(cartProductElement);
		}
	}
	
	private void setCartFields(Cart cart, Document document, Element cartElement) {
		cartElement.setAttribute(XmlAttributeNames.ID, String.valueOf(cart.getId()));
		XmlUtils.addSingleElementText(document, cartElement, XmlAttributeNames.TOTAL_AMOUNT, cart.getTotalAmount());
		XmlUtils.addSingleElementText(document, cartElement, XmlAttributeNames.CUSTOMER_NAME, cart.getCustomerName());
	}
	
	private Cart parseFromXml(Element cartElement) {
		Cart cart = parseCartFromXml(cartElement);
		List<CartProduct> cartProducts;
		
		//cartProducts = parseCartProducts(cartElement);
		cartProducts = cartProductXmlUtil.parseList(cartElement);
		cart.setCartProducts(cartProducts);

		return cart;
	}
	
	private Cart parseCartFromXml(Element cartElement) {
		long id = XmlUtils.getAttribute(cartElement, XmlAttributeNames.ID, 0);
		Double totalAmount = XmlUtils.getSingleElementText(cartElement, XmlAttributeNames.TOTAL_AMOUNT, 0.0);
		String customerName = XmlUtils.getSingleElementText(cartElement, XmlAttributeNames.CUSTOMER_NAME, "");
		
		Cart cart = new Cart(id, totalAmount, customerName);
		
		return cart;
	}
	
	private List<CartProduct> parseCartProducts(Element cartElement) {
		List<CartProduct> cartProducts = new ArrayList<CartProduct>();
		NodeList cartProductsElement = cartElement.getElementsByTagName(CartProductXmlUtil.XmlAttributeNames.CART_PRODUCT);
		
		for(int i=0;i<cartProductsElement.getLength();i++) {
			CartProduct cartProduct = cartProductXmlUtil.parse((Element)cartProductsElement.item(i));
			cartProducts.add(cartProduct);
		}
		
		return cartProducts;
	}
	
	private static class XmlAttributeNames {
		private static final String ID = "id";
		private static final String TOTAL_AMOUNT = "totalAmount";
		private static final String CUSTOMER_NAME = "customerName";
		
		private static final String CART = "cart";
		private static final String CARTS = "carts";
	}
	
}
