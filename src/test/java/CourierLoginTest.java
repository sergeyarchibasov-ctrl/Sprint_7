import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CourierLoginTest {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    private String login;
    private String password;
    private Integer courierId;

    @Test
    public void courierCanLogin() {
        createTestCourier();

        Response response = loginCourier(login, password);

        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("id"));

        courierId = response.jsonPath().getInt("id");
    }

    @Test
    public void cannotLoginWithIncorrectLogin() {
        createTestCourier();

        Response response = loginCourier(
                "incorrect" + UUID.randomUUID(),
                password
        );

        assertEquals(404, response.statusCode());
    }

    @Test
    public void cannotLoginWithIncorrectPassword() {
        createTestCourier();

        Response response = loginCourier(
                login,
                "incorrect" + UUID.randomUUID()
        );

        assertEquals(404, response.statusCode());
    }

    @Test
    public void cannotLoginWithoutLogin() {
        createTestCourier();

        Response response = loginWithoutLogin();

        assertEquals(400, response.statusCode());
    }

    @Test
    public void cannotLoginWithoutPassword() {
        createTestCourier();

        Response response = loginWithoutPassword();

        assertEquals(504, response.statusCode());
    }

    @Test
    public void cannotLoginNonexistentCourier() {
        String nonexistentLogin = "nonexistent" + UUID.randomUUID();
        String nonexistentPassword = "password" + UUID.randomUUID();

        Response response = loginCourier(
                nonexistentLogin,
                nonexistentPassword
        );

        assertEquals(404, response.statusCode());
    }

    @Step("Создать тестового курьера")
    private void createTestCourier() {
        login = "courier" + UUID.randomUUID();
        password = "password" + UUID.randomUUID();

        Response response = given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"login\":\"" + login + "\","
                        + "\"password\":\"" + password + "\""
                        + "}")
                .when()
                .post("/api/v1/courier");

        assertEquals(201, response.statusCode());
    }

    @Step("Авторизоваться под курьером")
    private Response loginCourier(String login, String password) {
        return given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"login\":\"" + login + "\","
                        + "\"password\":\"" + password + "\""
                        + "}")
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Авторизоваться без логина")
    private Response loginWithoutLogin() {
        return given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"password\":\"" + password + "\""
                        + "}")
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Авторизоваться без пароля")
    private Response loginWithoutPassword() {
        return given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body("{"
                        + "\"login\":\"" + login + "\""
                        + "}")
                .when()
                .post("/api/v1/courier/login");
    }

    @AfterEach
    @Step("Удалить тестового курьера")
    public void deleteCourier() {
        if (login != null && password != null) {
            Integer id = getCourierIdForDeletion();

            if (id != null) {
                given()
                        .baseUri(BASE_URL)
                        .when()
                        .delete("/api/v1/courier/" + id);
            }
        }
    }

    @Step("Получить id курьера для удаления")
    private Integer getCourierIdForDeletion() {
        Response response = loginCourier(login, password);

        if (response.statusCode() == 200) {
            return response.jsonPath().getInt("id");
        }

        return null;
    }
}