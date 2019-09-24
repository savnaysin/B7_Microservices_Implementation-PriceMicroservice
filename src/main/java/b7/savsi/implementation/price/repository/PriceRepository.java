package b7.savsi.implementation.price.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import b7.savsi.implementation.price.entity.Price;

@Repository
public interface PriceRepository extends JpaRepository<Price, Integer> {
	Price findByProductIdOrderByUpdatedOn(Integer productId);

	void deleteByProductId(Integer productId);
}
