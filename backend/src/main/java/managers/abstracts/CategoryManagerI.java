package managers.abstracts;

import java.util.List;
import entities.concretes.*;
import results.DataResult;
import managers.base.ManagerI;

public interface CategoryManagerI extends ManagerI<Category> {
	DataResult<List<Category>> listAll();
}
