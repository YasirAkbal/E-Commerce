package managers.abstracts;

import java.util.List;
import entities.concretes.*;
import results.DataResult;
import results.Result;
import managers.base.ManagerI;

public interface CartManagerI extends ManagerI<Cart> {
	DataResult<Long> insertAndReturnGeneratedId(Cart cart);

	DataResult<Long> addCardProductToCart(CartProduct cardProduct);

	DataResult<CartProduct> deleteCardProductFromCart(long cardProductId);

	DataResult<List<Cart>> listAll();
}
