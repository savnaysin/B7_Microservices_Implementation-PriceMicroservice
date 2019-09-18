package b7.savsi.implementation.price.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import b7.savsi.implementation.price.entity.Price;
import b7.savsi.implementation.price.exception.BadRequestException;
import b7.savsi.implementation.price.exception.NotFoundException;
import b7.savsi.implementation.price.repository.PriceRepository;

@RefreshScope
@RestController
public class PriceController {
	@Autowired
	private Environment env;
	@Autowired
	PriceRepository priceRepository;

	@GetMapping(path = "/price/priceInfo/{productId}")
	public ResponseEntity<Price> getPriceInfo(@PathVariable("productId") Integer productId) throws NotFoundException {
		Price price = priceRepository.findByProductIdOrderByUpdatedOn(productId);
		if (price == null)
			throw new NotFoundException("No Price Found for the product id:: " + productId);
		return ResponseEntity.ok().body(price);
	}

	@GetMapping(path = "/price/priceInfoList/{productIds}")
	public ResponseEntity<List<Price>> getPriceList(@PathVariable List<Integer> productIds) throws NotFoundException {
		List<Price> priceList = new ArrayList<Price>();
		Price price = null;
		for (Integer productId : productIds) {
			price = priceRepository.findByProductIdOrderByUpdatedOn(productId);
			if (price != null)
				priceList.add(price);
		}
		if (priceList.isEmpty())
			throw new NotFoundException("No Prices Found for any of the product ids");
		return ResponseEntity.ok().body(priceList);
	}

	@PostMapping(path = "/price/addPrice", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Void> addPriceInfo(@RequestBody Price price) throws BadRequestException {
		if (price.getPrice() >= new Double(env.getProperty("price.minValue"))
				&& price.getPrice() <= new Double(env.getProperty("price.maxValue"))) {
			Price newPrice = priceRepository
					.save(new Price(price.getPrice(), price.getProductId(), price.getCurrency()));

			if (newPrice == null)
				return ResponseEntity.noContent().build();

			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(newPrice.getPriceId()).toUri();
			return ResponseEntity.created(location).build();
		} else
			throw new BadRequestException("Price should be between minValue: " + env.getProperty("price.minValue")
					+ " & maxValue: " + env.getProperty("price.maxValue"));

	}

}
