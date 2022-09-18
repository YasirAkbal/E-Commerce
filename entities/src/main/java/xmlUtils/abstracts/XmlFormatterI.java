package xmlUtils.abstracts;

import java.util.List;
import org.w3c.dom.Document;
import entities.abstracts.EntityI;
import results.DataResult;

public interface XmlFormatterI<E extends EntityI> {
	DataResult<Document> format(E entity);
	DataResult<Document> format(List<E> entities);
}
