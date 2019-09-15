package b7.savsi.implementation.price.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import b7.savsi.implementation.price.entity.Price;
import b7.savsi.implementation.price.exception.NotFoundException;
import b7.savsi.implementation.price.repository.PriceRepository;

@RefreshScope
@RestController
public class PriceController {
	@Autowired
	PriceRepository priceRepository;

	@GetMapping(path = "/priceInfo/{productId}")
	public ResponseEntity<Price> getPriceInfo(@PathVariable("productId") Integer productId)
			throws NotFoundException {
		Price price = priceRepository.findByProductIdOrderByUpdatedOn(productId);
		if (price == null)
			throw new NotFoundException("No Price Found for the product id:: " + productId);
		return ResponseEntity.ok().body(price);
	}

	@PostMapping(path = "/addPrice", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Void> addPriceInfo(@RequestBody Price price) {
		Price newPrice = priceRepository.save(new Price(price.getPrice(), price.getProductId(), price.getCurrency()));

		if (newPrice == null)
			return ResponseEntity.noContent().build();

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newPrice.getPriceId()).toUri();
		return ResponseEntity.created(location).build();
	}

}
