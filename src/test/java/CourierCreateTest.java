import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourierCreateTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    private String login;
    private String password;
    private Integer courierId;

    @Test
    public void courierCanBeCreated() {
        prepareCourierData();

        Response response = createCourier(login, password);

        checkSuccessfulCreation(response);

        courierId = loginCourier();
    }

    @Test
    public void cannotCreateTwoIdenticalCouriers() {
        prepareCourierData();

        Response firstResponse = createCourier(login, password);

        checkSuccessfulCreation(firstResponse);

        courierId = loginCourier();

        Response secondResponse = createCourier(login, password);

        checkErrorResponse(secondResponse, 409);
    }

    @Test
    public void cannotCreateCourierWithoutLogin() {
        prepareCourierData();

        Response response = createCourierWithoutLogin();

        checkErrorResponse(response, 400);
    }

    @Test
    public void cannotCreateCourierWithoutPassword() {
        prepareCourierData();

        Response response = createCourierWithoutPassword();

        checkErrorResponse(response, 400);
    }

    @Step("Подготовить уникальные данные курьера")
    private void prepareCourierData() {
        login = "courier" + UUID.randomUUID();
        password = "password" + UUID.randomUUID();
    }

    @Step("Создать курьера")
    private Response createCourier(String login, String password) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"login\":\"" + login + "\","
                        + "\"password\":\"" + password + "\""
                        + "}")
                .when()
                .post("/api/v1/courier");
    }

    @Step("Создать курьера без логина")
    private Response createCourierWithoutLogin() {
        return given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"password\":\"" + password + "\""
                        + "}")
                .when()
                .post("/api/v1/courier");
    }

    @Step("Создать курьера без пароля")
    private Response createCourierWithoutPassword() {
        return given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"login\":\"" + login + "\""
                        + "}")
                .when()
                .post("/api/v1/courier");
    }

    @Step("Проверить успешное создание курьера")
    private void checkSuccessfulCreation(Response response) {
        assertEquals(201, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("ok"));
    }

    @Step("Проверить код ошибки")
    private void checkErrorResponse(Response response, int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.statusCode());
    }

    @Step("Авторизоваться под созданным курьером и получить его id")
    private Integer loginCourier() {
        return given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"login\":\"" + login + "\","
                        + "\"password\":\"" + password + "\""
                        + "}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @AfterEach
    @Step("Удалить созданного курьера")
    public void deleteCourier() {
        if (courierId != null) {
            given()
                    .baseUri(BASE_URL)
                    .when()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }
}