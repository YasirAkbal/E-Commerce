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

public class CartProductXmlUtil implements XmlUtilI<CartProduct> {
	
	@Override
	public DataResult<Document> format(CartProduct cartProduct) {
		Document document = null;
		
		try {
			document = XmlUtils.create(XmlAttributeNames.CART_PRODUCT);
			Element cartProductElement = document.getDocumentElement();
			
			setFields(cartProduct, document, cartProductElement);
		} catch (ParserConfigurationException e) {
			return new ErrorDataResult<Document>(XmlMessages.PARSING_ERROR);
		}

		return new SuccessDataResult<Document>(document);
	}

	@Override
	public DataResult<Document> format(List<CartProduct> cartProducts) {
		Document document = null;
		Element cartProductList;
		
		try {
			document = XmlUtils.create(XmlAttributeNames.CART_PRODUCTS);
			cartProductList = document.getDocumentElement();
			
			for(CartProduct cartProduct : cartProducts) {
				Element cartProductElement = document.createElement(XmlAttributeNames.CART_PRODUCT);
				setFields(cartProduct, document, cartProductElement);
				cartProductList.appendChild(cartProductElement);
			}
		} catch (ParserConfigurationException e) {
			return new ErrorDataResult<Document>(XmlMessages.PARSING_ERROR);
		}
		
		return new SuccessDataResult<Document>(document);
	}
	
	@Override
	public CartProduct parse(Document document) {
		Element element = document.getDocumentElement();
		
		CartProduct cartProduct = parse(element);
		
		return cartProduct;
	}
	
	@Override
	public List<CartProduct> parseList(Document document) {
		Element element = document.getDocumentElement();
		
		return parseList(element);
	}
	
	public List<CartProduct> parseList(Element element) {
		List<CartProduct> cartProducts = new ArrayList<CartProduct>();
		NodeList cartProductsElementParent = element.getElementsByTagName(XmlAttributeNames.CART_PRODUCTS);
		
		if(cartProductsElementParent.getLength() > 0) {
			NodeList cartProductsElementChilds = ((Element)cartProductsElementParent.item(0)).getElementsByTagName(XmlAttributeNames.CART_PRODUCT);
			for(int i=0;i<cartProductsElementChilds.getLength();i++) {
				CartProduct cartProduct = parse((Element)cartProductsElementChilds.item(i));
				cartProducts.add(cartProduct);
			}
		}
		
		return cartProducts;
	}
	
	public CartProduct parse(Element cartProductElement) {
		long id = XmlUtils.getAttribute(cartProductElement, XmlAttributeNames.ID, 0L);
		long cartId = XmlUtils.getSingleElementText(cartProductElement, XmlAttributeNames.CART_ID, 0L);
		long productId = XmlUtils.getSingleElementText(cartProductElement, XmlAttributeNames.PRODUCT_ID, 0L);
		int salesQuantity = XmlUtils.getSingleElementText(cartProductElement, XmlAttributeNames.SALES_QUANTITY, 0);
		double salesPrice = XmlUtils.getSingleElementText(cartProductElement, XmlAttributeNames.SALES_PRICE, 0.0);
		double taxRate = XmlUtils.getSingleElementText(cartProductElement, XmlAttributeNames.TAX_RATE, 0.0);
		double lineAmount = XmlUtils.getSingleElementText(cartProductElement, XmlAttributeNames.LINE_AMOUNT, 0.0);
		
		CartProduct cartProduct = new CartProduct(id,cartId,productId,salesQuantity,salesPrice,taxRate,lineAmount);
		
		return cartProduct;
	}

	public void setFields(CartProduct cartProduct, Document document, Element cartProductElement) {
		cartProductElement.setAttribute(XmlAttributeNames.ID, String.valueOf(cartProduct.getId()));
		XmlUtils.addSingleElementText(document, cartProductElement, XmlAttributeNames.CART_ID, cartProduct.getCartId());
		XmlUtils.addSingleElementText(document, cartProductElement, XmlAttributeNames.PRODUCT_ID, cartProduct.getProductId());
		XmlUtils.addSingleElementText(document, cartProductElement, XmlAttributeNames.SALES_QUANTITY, cartProduct.getSalesQuantity());
		XmlUtils.addSingleElementText(document, cartProductElement, XmlAttributeNames.SALES_PRICE, cartProduct.getSalesPrice());
		XmlUtils.addSingleElementText(document, cartProductElement, XmlAttributeNames.TAX_RATE, cartProduct.getTaxRate());
		XmlUtils.addSingleElementText(document, cartProductElement, XmlAttributeNames.LINE_AMOUNT, cartProduct.getLineAmount());
	}
	
	public DataResult<Document> format(List<CartProduct> cartProducts, Document document, Element cartElement) {
		Element cartProductsElement = document.createElement(XmlAttributeNames.CART_PRODUCTS);
		
		for(CartProduct cartProduct : cartProducts) {
			Element cartProductElement = document.createElement(XmlAttributeNames.CART_PRODUCT);
			setFields(cartProduct, document, cartProductElement);
			cartProductsElement.appendChild(cartProductElement);
		}

		cartElement.appendChild(cartProductsElement);
		
		return new SuccessDataResult<Document>(document);
	}
	
	public static class XmlAttributeNames {
		private static final String ID = "id";
		private static final String CART_ID = "cartId";
		private static final String PRODUCT_ID = "productId";
		private static final String SALES_QUANTITY = "salesQuantity";
		private static final String SALES_PRICE = "salesPrice";
		private static final String TAX_RATE = "taxRate";
		private static final String LINE_AMOUNT = "lineAmount";
		
		public static final String CART_PRODUCT = "cartProduct";
		private static final String CART_PRODUCTS = "cartProducts";
	}

}
