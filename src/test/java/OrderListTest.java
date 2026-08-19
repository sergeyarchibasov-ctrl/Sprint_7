import api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderListTest {

    private final OrderApi orderApi = new OrderApi();

    @Test
    @Description("Проверка получения списка заказов")
    public void ordersListIsReturned() {

        Response response = orderApi.getOrders();

        checkSuccessfulResponse(response);
        checkOrdersList(response);
    }

    @Step("Проверить успешный ответ")
    private void checkSuccessfulResponse(Response response) {
        assertEquals(200, response.statusCode());
    }

    @Step("Проверить, что тело ответа содержит список заказов")
    private void checkOrdersList(Response response) {
        List<?> orders = response.jsonPath().getList("orders");

        assertNotNull(orders);
        assertTrue(orders instanceof List);
    }
}