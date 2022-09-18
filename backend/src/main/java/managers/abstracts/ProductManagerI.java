package managers.abstracts;

import java.util.List;
import entities.concretes.*;
import results.DataResult;
import managers.base.ManagerI;

public interface ProductManagerI extends ManagerI<Product> {
	DataResult<List<Product>> findByCategory(long categoryId);
	DataResult<List<Product>> listAll();
}
