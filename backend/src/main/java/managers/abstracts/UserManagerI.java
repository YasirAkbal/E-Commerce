package managers.abstracts;

import java.util.List;
import entities.concretes.*;
import results.DataResult;
import managers.base.ManagerI;

public interface UserManagerI extends ManagerI<User> {
	DataResult<Long> insertAndReturnGeneratedId(User user);
	DataResult<User> checkCredentials(String username, String password);
	DataResult<List<User>> listAll();
}
