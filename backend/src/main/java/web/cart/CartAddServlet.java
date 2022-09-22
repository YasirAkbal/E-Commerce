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
import utils.StreamUtils;
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

		Document inputDocument;
		try {
			inputDocument = XmlUtils.parse(req.getInputStream());
			CartProduct cartProduct = cartProductXmlUtil.parse(inputDocument);
			DataResult<Long> resultFromManager = cartManager.addCardProductToCart(cartProduct);
			
			Document outputDocument;
			if (resultFromManager.isSuccess()) {
				cartProduct.setId(resultFromManager.getData());
				
				DataResult<Document> resultFromParse = cartProductXmlUtil.format(cartProduct);
				outputDocument = XmlUtils.setSuccessTrueIfSuccessfulOtherwiseCreateSuccessFalseDocument(resultFromParse);	
				
				StreamUtils.setResponseStatus(resultFromParse, resp, HttpServletResponse.SC_OK, HttpServletResponse.SC_BAD_REQUEST);
			} else {
				outputDocument = XmlUtils.createSuccessFalseXml();
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}	
			
			XmlUtils.dump(outputDocument, resp.getOutputStream());
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			e.printStackTrace();
		}

	}
}
