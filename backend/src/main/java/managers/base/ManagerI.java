package managers.base;

import entities.abstracts.EntityI;
import results.DataResult;
import results.Result;

public interface ManagerI<T extends EntityI> {
	Result insert(T entity);

	Result update(T entity);

	Result delete(Long id);

	DataResult<T> find(Long id);
}
