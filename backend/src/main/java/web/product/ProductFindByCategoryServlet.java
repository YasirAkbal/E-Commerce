package web.product;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import constants.WebConstants;
import entities.concretes.*;
import managers.abstracts.ProductManagerI;
import managers.concretes.ProductManager;
import results.DataResult;
import utils.StreamUtils;
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;
import xmlUtils.concretes.ProductXmlUtil;

//http://localhost:8080/backend/api/products?categoryId=1
@WebServlet("/api/products")
public class ProductFindByCategoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final ProductManagerI productManager;
	private final XmlUtilI<Product> productXmlUtil;

	public ProductFindByCategoryServlet() {
		super();
		this.productManager = new ProductManager();
		this.productXmlUtil = new ProductXmlUtil();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType(WebConstants.XML_CONTENT_TYPE);

		long categoryId = Long.parseLong(request.getParameter("categoryId"));
		DataResult<List<Product>> resultFromFindByCategoryId = productManager.findByCategory(categoryId);
		
		Document outputDocument;
		try {
			if(resultFromFindByCategoryId.isSuccess()) {
				List<Product> products = resultFromFindByCategoryId.getData();
				DataResult<Document> resultFromFormatter = productXmlUtil.format(products);
				
				outputDocument = XmlUtils.setSuccessTrueIfSuccessfulOtherwiseCreateSuccessFalseDocument(resultFromFormatter);	
				
				StreamUtils.setResponseStatus(resultFromFormatter, response, HttpServletResponse.SC_OK, HttpServletResponse.SC_BAD_REQUEST);
			} else {
				outputDocument = XmlUtils.createSuccessFalseXml();
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}

			XmlUtils.dump(outputDocument, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

	}
}
