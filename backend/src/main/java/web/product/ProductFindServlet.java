package web.product;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.filters.ExpiresFilter.XHttpServletResponse;
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

@WebServlet("/api/product")
public class ProductFindServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final ProductManagerI productManager;
	private final XmlUtilI<Product> productXmlUtil;

	public ProductFindServlet() {
		super();
		this.productManager = new ProductManager();
		this.productXmlUtil = new ProductXmlUtil();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType(WebConstants.XML_CONTENT_TYPE);
		
		long productId = Long.parseLong(request.getParameter("productId"));
		DataResult<Product> resultFromFind = productManager.find(productId);
		
		Document outputDocument;
		try {
			if(resultFromFind.isSuccess()) {
				Product product = resultFromFind.getData();
				DataResult<Document> resultFromFormatter = productXmlUtil.format(product);
				
				outputDocument = XmlUtils.setSuccessTrueIfSuccessfulOtherwiseCreateSuccessFalseDocument(resultFromFormatter);	
				StreamUtils.setResponseStatus(resultFromFormatter, response, HttpServletResponse.SC_OK, HttpServletResponse.SC_BAD_REQUEST);
			} else {
				outputDocument = XmlUtils.createSuccessFalseXml();
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			
			XmlUtils.dump(outputDocument, response.getOutputStream());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			e.printStackTrace();
		}
	}
}
