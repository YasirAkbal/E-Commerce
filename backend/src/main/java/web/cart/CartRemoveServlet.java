package web.cart;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import constants.WebConstants;
import entities.concretes.*;
import managers.abstracts.CartManagerI;
import managers.concretes.CartManager;
import results.DataResult;
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

		DataResult<CartProduct> result = cartManager.deleteCardProductFromCart(cartProductId);
		if (result.isSuccess()) {
			Document document = (Document) cartProductXmlUtil.format(result.getData()).getData();

			try {
				XmlUtils.dump(document, resp.getOutputStream());
			} catch (IOException | TransformerException e) {
				e.printStackTrace();
			}
		}
	}
}
