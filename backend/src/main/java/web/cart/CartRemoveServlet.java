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

@WebServlet("/api/cart/remove")
public class CartRemoveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CartManagerI cartManager;
	private final XmlUtilI<CartProduct> cartProductXmlUtil;

	public CartRemoveServlet() {
		this.cartManager = new CartManager();
		this.cartProductXmlUtil = new CartProductXmlUtil();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(WebConstants.XML_CONTENT_TYPE);

		long cartProductId = Long.parseLong(req.getParameter("cartProductId"));
		DataResult<CartProduct> resultFromManager = cartManager.deleteCardProductFromCart(cartProductId);
		
		Document outputDocument;
		try {
			if (resultFromManager.isSuccess()) {
				CartProduct deletedCartProduct = resultFromManager.getData();
				
				DataResult<Document> resultFromFormatter = cartProductXmlUtil.format(deletedCartProduct);
				outputDocument = XmlUtils.setSuccessTrueIfSuccessfulOtherwiseCreateSuccessFalseDocument(resultFromFormatter);
				
				StreamUtils.setResponseStatus(resultFromFormatter, resp, HttpServletResponse.SC_OK, HttpServletResponse.SC_BAD_REQUEST);
			} else {
				outputDocument = XmlUtils.createSuccessFalseXml();
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
						
			XmlUtils.dump(outputDocument, resp.getOutputStream());
		} catch (IOException | TransformerException | ParserConfigurationException e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
