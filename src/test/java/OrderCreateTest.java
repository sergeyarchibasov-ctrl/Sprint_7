import api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderCreateTest {

    private final OrderApi orderApi = new OrderApi();

    @ParameterizedTest(name = "Создание заказа: {0}")
    @MethodSource("orderColors")
    @Description("Проверка создания заказа с разными вариантами цвета")
    public void orderCanBeCreatedWithDifferentColors(
            String testName,
            List<String> colors) {

        Order order = createOrder(colors);

        Response response = orderApi.createOrder(order);

        checkSuccessfulOrderCreation(response);
    }

    static Stream<Arguments> orderColors() {
        return Stream.of(
                Arguments.of(
                        "BLACK",
                        Collections.singletonList("BLACK")
                ),
                Arguments.of(
                        "GREY",
                        Collections.singletonList("GREY")
                ),
                Arguments.of(
                        "BLACK и GREY",
                        Arrays.asList("BLACK", "GREY")
                ),
                Arguments.of(
                        "без цвета",
                        null
                )
        );
    }

    @Step("Подготовить данные заказа")
    private Order createOrder(List<String> colors) {
        return new Order(
                "Иван",
                "Иванов",
                "Москва, улица Ленина, 1",
                4,
                "+7999" + (1000000 + Math.abs(UUID.randomUUID().hashCode() % 8999999)),
                5,
                "2026-12-31",
                "Тестовый заказ",
                colors
        );
    }

    @Step("Проверить успешное создание заказа")
    private void checkSuccessfulOrderCreation(Response response) {
        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().get("track"));
    }
}