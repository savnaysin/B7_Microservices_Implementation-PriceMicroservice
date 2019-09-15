package b7.savsi.implementation.price.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import b7.savsi.implementation.price.entity.Price;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PriceRepositoryTest {

	@Autowired
	private PriceRepository priceRepository;
	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void testFindByProductIdOrderByUpdatedOn() {
		Price newPrice = new Price(new BigDecimal(20.00), 1001, "USD");
		Integer savedProductId = testEntityManager.persist(newPrice).getProductId();
		testEntityManager.flush();
		assertEquals("testFindById:TC1", new BigDecimal(20.00),
				priceRepository.findByProductIdOrderByUpdatedOn(savedProductId).getPrice());
		assertEquals("testFindById:TC2", null, priceRepository.findByProductIdOrderByUpdatedOn(123));
	}

	@Test
	public void testSave() {
		Price priceFound = priceRepository.save(new Price(new BigDecimal(20.00), 1001, "USD"));
		assertThat(priceFound).hasFieldOrPropertyWithValue("price", new BigDecimal(20.00));
		assertThat(priceFound).hasFieldOrPropertyWithValue("currency", "USD");

	}

}
