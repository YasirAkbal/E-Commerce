package managers.abstracts;

import java.util.List;
import entities.concretes.*;
import results.DataResult;
import results.Result;
import managers.base.ManagerI;

public interface CartProductManagerI extends ManagerI<CartProduct> {
	DataResult<List<CartProduct>> listAllByCartId(long cartId);

	DataResult<Long> insertAndReturnGeneratedId(CartProduct cartProduct);

	DataResult<CartProduct> deleteAndReturn(Long id);
}
