import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderCreateTest {

    private static final String BASE_URL =
            "https://qa-scooter.praktikum-services.ru";

    @ParameterizedTest(name = "Создание заказа: {0}")
    @MethodSource("orderColors")
    public void orderCanBeCreatedWithDifferentColors(
            String testName,
            String colorJson) {

        Response response = createOrder(colorJson);

        checkSuccessfulOrderCreation(response);
    }

    static Stream<Arguments> orderColors() {
        return Stream.of(
                Arguments.of("BLACK", "[\"BLACK\"]"),
                Arguments.of("GREY", "[\"GREY\"]"),
                Arguments.of("BLACK и GREY", "[\"BLACK\",\"GREY\"]"),
                Arguments.of("без цвета", "[]")
        );
    }

    @Step("Создать заказ с выбранными цветами")
    private Response createOrder(String colorJson) {
        String body = "{"
                + "\"firstName\":\"Иван\","
                + "\"lastName\":\"Иванов\","
                + "\"address\":\"Москва, улица Ленина, 1\","
                + "\"metroStation\":4,"
                + "\"phone\":\"+79991234567\","
                + "\"rentTime\":5,"
                + "\"deliveryDate\":\"2026-12-31\","
                + "\"comment\":\"Тестовый заказ\","
                + "\"color\":" + colorJson
                + "}";

        return given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Проверить успешное создание заказа")
    private void checkSuccessfulOrderCreation(Response response) {
        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().get("track"));
    }
}