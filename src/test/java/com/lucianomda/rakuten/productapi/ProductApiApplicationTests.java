package com.lucianomda.rakuten.productapi;

import com.google.common.io.Resources;
import com.lucianomda.rakuten.productapi.client.fixerio.FixerIoProperties;
import com.lucianomda.rakuten.productapi.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockserver.model.HttpResponse.response;

/**
 * Integration tests.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductApiApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class ProductApiApplicationTests {

	@LocalServerPort
	private int localServerPort;

	@Autowired
	private FixerIoProperties fixerIoProperties;

	private ClientAndServer mockServer;

	@Before
	public void startServer() throws IOException {
		mockServer = ClientAndServer.startClientAndServer(fixerIoProperties.getPort());
		mockFixerIoServerResponse(Constants.DEFAULT_CURRENCY_CODE);
	}

	@After
	public void stopServer(){
		mockServer.stop();
	}

	private void mockFixerIoServerResponse(String baseCurrencyCode) throws IOException {
		URL responseBodyUrl = this.getClass().getResource("/mock-server/fixer-io/latest-exchange-response.json");

		new MockServerClient("127.0.0.1", fixerIoProperties.getPort())
				.when(
						HttpRequest.request()
								.withMethod("GET")
								.withPath("/latest")
								.withQueryStringParameter("access_key", fixerIoProperties.getAccessKey())
								.withQueryStringParameter("base", baseCurrencyCode)
								.withHeader("\"Content-type\", \"application/json\"")
				)
				.respond(
						response()
								.withStatusCode(200)
								.withHeaders(
										new Header("Content-Type", "application/json; charset=utf-8"))
								.withBody(Resources.toString(responseBodyUrl, Charset.forName("UTF-8")))
				);
	}

	/**
	 * Creates 2 categories, a parent and a child, then get the categories by id.
	 * Also verifies categories with child categories can't be deleted. Finally both categories are deleted.
	 */
	@Test
	public void testCategoryAndDeleteIt() {
		long parentCategoryId =
				given()
					.contentType("application/json")
				.body("{\"name\": \"category test 0\",\"parent_id\": null}")
					.port(localServerPort)
				.when()
						.post("/categories")
				.then()
						.statusCode(201)
				.extract()
						.jsonPath().getLong("id");

		long categoryId =
				given()
						.contentType("application/json")
						.body("{\"name\": \"category test 1\",\"parent_id\": " + parentCategoryId + "}")
						.port(localServerPort)
				.when()
						.post("/categories")
				.then()
						.statusCode(201)
				.extract()
						.jsonPath().getLong("id");

		given()
				.port(localServerPort)
		.when()
				.get("/categories/{categoryId}", parentCategoryId)
		.then()
				.statusCode(200)
				.body("id", equalTo((int) parentCategoryId))
				.body("name", equalTo("category test 0"))
				.body("parent_id", nullValue())
				.body("children_ids", containsInAnyOrder((int) categoryId));

		given()
				.port(localServerPort)
		.when()
				.get("/categories/{categoryId}", categoryId)
		.then()
				.statusCode(200)
				.body("id", equalTo((int) categoryId))
				.body("name", equalTo("category test 1"))
				.body("parent_id", equalTo((int) parentCategoryId))
				.body("children_ids", empty());

		given()
				.port(localServerPort)
		.when()
				.delete("/categories/{categoryId}", parentCategoryId)
		.then()
				.statusCode(400)
				.body("error_code", equalTo("BAD_REQUEST"))
				.body("message", equalTo("Category with id " + parentCategoryId + " still has children."));

		given()
				.port(localServerPort)
		.when()
				.delete("/categories/{categoryId}", categoryId)
		.then()
				.statusCode(200);

		given()
				.port(localServerPort)
		.when()
				.delete("/categories/{categoryId}", parentCategoryId)
		.then()
				.statusCode(200);

		given()
				.port(localServerPort)
		.when()
				.get("/categories/{categoryId}", categoryId)
		.then()
				.statusCode(400)
				.body("error_code", equalTo("BAD_REQUEST"))
				.body("message", equalTo("Invalid category id " + categoryId + "."));

		given()
				.port(localServerPort)
		.when()
				.get("/categories/{categoryId}", parentCategoryId)
		.then()
				.statusCode(400)
				.body("error_code", equalTo("BAD_REQUEST"))
				.body("message", equalTo("Invalid category id " + parentCategoryId + "."));
	}

	/**
	 * Creates a category, then create a product and finally deletes created product.
	 * Also verifies categories can be deleted when they have children or if they have products asociated.
	 */
	@Test
	public void testProductAndDeleteIt() {
		testProductAndDeleteIt("1000000", "USD", "890620.25");
		testProductAndDeleteIt("123000.00", "CLP", "165.38");
		testProductAndDeleteIt("5000.25", "ARS", "106.49");
		testProductAndDeleteIt("305.00", "BRL", "68.96");
		testProductAndDeleteIt("1", "KWD", "2.93");
	}

	private void testProductAndDeleteIt(String price, String currencyCode, String expectedPriceInEuros) {
		long categoryId =
				given()
						.contentType("application/json")
						.body("{\"name\": \"Knives\",\"parent_id\": null}")
						.port(localServerPort)
				.when()
						.post("/categories")
				.then()
						.statusCode(201)
				.extract()
						.jsonPath().getLong("id");

		given()
				.port(localServerPort)
		.when()
				.get("/categories/{categoryId}", categoryId)
		.then()
				.statusCode(200);

		long productId =
				given()
						.contentType("application/json")
						.body("{\"name\":\"ginsu 2000\",\"category_ids\":[" + categoryId + "],\"price\": \"" + price + "\",\"currency_code\":\"" + currencyCode +"\"}")
						.port(localServerPort)
				.when()
						.post("/products")
				.then()
						.statusCode(201)
				.extract()
						.jsonPath().getLong("id");

		given()
				.port(localServerPort)
		.when()
				.get("/products/{productId}", productId)
		.then()
				.statusCode(200)
				.body("id", equalTo((int) productId))
				.body("name", equalTo("ginsu 2000"))
				.body("category_ids", containsInAnyOrder((int) categoryId))
				.body("currency_code", equalTo("EUR"))
				.body("price", equalTo(Float.valueOf(expectedPriceInEuros).floatValue()));

		given()
				.port(localServerPort)
		.when()
				.delete("/categories/{categoryId}", categoryId)
		.then()
				.statusCode(400)
				.body("error_code", equalTo("BAD_REQUEST"))
				.body("message", equalTo("Category " + categoryId + " can't be deleted because still have products assigned."));

		given()
				.port(localServerPort)
		.when()
				.delete("/products/{productId}", productId)
		.then()
				.statusCode(200);

		given()
				.port(localServerPort)
		.when()
				.delete("/categories/{categoryId}", categoryId)
		.then()
				.statusCode(200);

		given()
				.port(localServerPort)
		.when()
				.get("/categories/{categoryId}", categoryId)
		.then()
				.statusCode(400)
				.body("error_code", equalTo("BAD_REQUEST"))
				.body("message", equalTo("Invalid category id " + categoryId + "."));

		given()
				.port(localServerPort)
		.when()
				.get("/products/{productId}", productId)
		.then()
				.statusCode(400)
				.body("error_code", equalTo("BAD_REQUEST"))
				.body("message", equalTo("Invalid product id " + productId + "."));
	}
}
