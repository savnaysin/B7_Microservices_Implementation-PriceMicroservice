package b7.savsi.implementation.price.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import b7.savsi.implementation.price.entity.Price;
import b7.savsi.implementation.price.repository.PriceRepository;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@WebMvcTest(value = PriceController.class, secure = false)
public class PriceControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	Price mockPriceInfo;
	@MockBean
	PriceRepository mockPriceRepository;

	@Test
	public void testGetPriceInfoSuccess() throws Exception {
		Price mockPriceInfo = new Price(20.00, 1003, "USD");
		Mockito.when(mockPriceRepository.findByProductIdOrderByUpdatedOn(Mockito.anyInt())).thenReturn(mockPriceInfo);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/priceInfo/1003")
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expectedPriceJson = "{\"price\": 20.00,\"productId\": 1003,\"currency\":\"USD\"}";

		JSONAssert.assertEquals(expectedPriceJson, result.getResponse().getContentAsString(), false);
	}

	@Test
	public void testGetPriceInfoNotFound() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/priceInfo/1004")
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
	}

	@Test
	public void testAddPriceInfoSuccess() throws Exception {
		String priceJson = "{\"price\": 30.00,\"productId\": 1002,\"currency\":\"INR\"}";
		Price mockPriceInfo = new Price(30.00, 1002, "INR");
		Mockito.when(mockPriceRepository.save(Mockito.any(Price.class))).thenReturn(mockPriceInfo);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/addPrice").accept(MediaType.APPLICATION_JSON)
				.content(priceJson).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals("testCreateNewAccount:TC1", HttpStatus.CREATED.value(), response.getStatus());

	}

	@Test
	public void testAddPriceInfoBadRequest() throws Exception {
		String priceJson = "{\"price\": 30000.00,\"productId\": 1002,\"currency\":\"INR\"}";
		Price mockPriceInfo = new Price(30000.00, 1002, "INR");
		Mockito.when(mockPriceRepository.save(Mockito.any(Price.class))).thenReturn(mockPriceInfo);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/addPrice").accept(MediaType.APPLICATION_JSON)
				.content(priceJson).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}
	
	
	@Test
	public void testAddPriceInfoNoContent() throws Exception {
		String priceJson = "{\"price\": 30.00,\"productId\": 1002,\"currency\":\"INR\"}";
		Mockito.when(mockPriceRepository.save(Mockito.any(Price.class))).thenReturn(null);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/addPrice").accept(MediaType.APPLICATION_JSON)
				.content(priceJson).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		Assert.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
	}
}
