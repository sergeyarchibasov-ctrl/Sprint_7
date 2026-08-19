package api;

import config.ApiConfig;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Courier;

import static io.restassured.RestAssured.given;

public class CourierApi {

    @Step("Создать курьера")
    public Response createCourier(Courier courier) {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .header("Content-Type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Авторизоваться под курьером")
    public Response loginCourier(Courier courier) {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .header("Content-Type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Удалить курьера")
    public Response deleteCourier(Integer courierId) {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .when()
                .delete("/api/v1/courier/" + courierId);
    }
}