package web.category;

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
import managers.abstracts.CategoryManagerI;
import managers.concretes.CategoryManager;
import results.DataResult;
import utils.StreamUtils;
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;
import xmlUtils.concretes.CategoryXmlUtil;

@WebServlet("/api/category/listAll")
public class CategoryListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final CategoryManagerI categoryManager;
	private final XmlUtilI<Category> categoryXmlUtil;

	public CategoryListServlet() {
		super();
		this.categoryManager = new CategoryManager();
		this.categoryXmlUtil = new CategoryXmlUtil();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(WebConstants.XML_CONTENT_TYPE);
		
		try {
			DataResult<List<Category>> resultFromListAll = categoryManager.listAll();
			Document outputDocument;
			
			if(resultFromListAll.isSuccess()) {	
				DataResult<Document> resultFromCreateDocument 
				= XmlUtils.createResponseDocumentFromDataResultList(resultFromListAll, categoryXmlUtil);
				
				outputDocument = resultFromCreateDocument.getData();
				
				StreamUtils.setResponseStatus(resultFromCreateDocument, resp, HttpServletResponse.SC_OK, HttpServletResponse.SC_BAD_REQUEST);
			} else {
				outputDocument = XmlUtils.createSuccessFalseXml();
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		
			XmlUtils.dump(outputDocument, resp.getOutputStream());
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			e.printStackTrace();
		}
	}
}
