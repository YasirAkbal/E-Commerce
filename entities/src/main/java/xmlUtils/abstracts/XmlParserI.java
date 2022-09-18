package xmlUtils.abstracts;

import java.util.List;

import org.w3c.dom.Document;
import entities.abstracts.EntityI;

public interface XmlParserI<E extends EntityI> {
	E parse(Document document);
	List<E> parseList(Document document);
}
