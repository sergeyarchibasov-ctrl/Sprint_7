package api;

import config.ApiConfig;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderApi {

    @Step("Создать заказ")
    public Response createOrder(Order order) {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Получить список заказов")
    public Response getOrders() {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .when()
                .get("/api/v1/orders");
    }
}