import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderListTest {

    private static final String BASE_URL =
            "https://qa-scooter.praktikum-services.ru";

    @Test
    public void ordersListIsReturned() {
        Response response = getOrders();

        checkSuccessfulResponse(response);
        checkOrdersList(response);
    }

    @Step("Получить список заказов")
    private Response getOrders() {
        return given()
                .baseUri(BASE_URL)
                .when()
                .get("/api/v1/orders");
    }

    @Step("Проверить успешный ответ")
    private void checkSuccessfulResponse(Response response) {
        assertEquals(200, response.statusCode());
    }

    @Step("Проверить, что тело ответа содержит список заказов")
    private void checkOrdersList(Response response) {
        Object orders = response.jsonPath().get("orders");

        assertNotNull(orders);
        assertTrue(orders instanceof java.util.List);
    }
}