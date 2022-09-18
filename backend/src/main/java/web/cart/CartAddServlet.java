package web.cart;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import constants.WebConstants;
import entities.concretes.*;
import managers.abstracts.CartManagerI;
import managers.concretes.CartManager;
import results.DataResult;
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;
import xmlUtils.concretes.CartProductXmlUtil;
import xmlUtils.concretes.CartXmlUtil;
import xmlUtils.concretes.ProductXmlUtil;

@WebServlet("/api/cart/add")
public class CartAddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final CartManagerI cartManager;
	private final XmlUtilI<CartProduct> cartProductXmlUtil;

	public CartAddServlet() {
		this.cartManager = new CartManager();
		this.cartProductXmlUtil = new CartProductXmlUtil();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(WebConstants.XML_CONTENT_TYPE);

		Document document;
		try {
			document = XmlUtils.parse(req.getInputStream());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		CartProduct cartProduct = cartProductXmlUtil.parse(document);
		DataResult<Long> result = cartManager.addCardProductToCart(cartProduct);
		Document outputDocument;

		if (result.isSuccess()) {
			cartProduct.setId(result.getData());
			outputDocument = (Document) cartProductXmlUtil.format(cartProduct).getData();
			XmlUtils.setSuccessTrue(outputDocument);
		} else {
			try {
				outputDocument = XmlUtils.createStatusXml(result, resp.getOutputStream());
			} catch (ParserConfigurationException | IOException e) {
				e.printStackTrace();
				return;
			}
		}

		try {
			XmlUtils.dump(outputDocument, resp.getOutputStream());
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
	}
}
