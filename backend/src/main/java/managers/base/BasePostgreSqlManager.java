package managers.base;

import constants.PostgreSqlDbConstants;
import entities.abstracts.EntityI;

public abstract class BasePostgreSqlManager<E extends EntityI> extends BaseManager<E> {

	public BasePostgreSqlManager() {
		super(PostgreSqlDbConstants.URL, PostgreSqlDbConstants.USER, PostgreSqlDbConstants.PASSWORD,
				PostgreSqlDbConstants.DRIVER);
	}

}
