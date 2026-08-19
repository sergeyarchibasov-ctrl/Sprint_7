import api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Courier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CourierLoginTest {

    private final CourierApi courierApi = new CourierApi();

    private Courier testCourier;
    private Integer courierId;

    @Test
    @Description("Проверка успешной авторизации курьера")
    public void courierCanLogin() {
        createTestCourier();

        Response response = courierApi.loginCourier(testCourier);

        checkSuccessfulLogin(response);
    }

    @Test
    @Description("Проверка ошибки при неправильном логине")
    public void cannotLoginWithIncorrectLogin() {
        createTestCourier();

        Courier courierWithIncorrectLogin = new Courier(
                "incorrect" + UUID.randomUUID(),
                testCourier.getPassword()
        );

        Response response =
                courierApi.loginCourier(courierWithIncorrectLogin);

        assertEquals(404, response.statusCode());
    }

    @Test
    @Description("Проверка ошибки при неправильном пароле")
    public void cannotLoginWithIncorrectPassword() {
        createTestCourier();

        Courier courierWithIncorrectPassword = new Courier(
                testCourier.getLogin(),
                "incorrect" + UUID.randomUUID()
        );

        Response response =
                courierApi.loginCourier(courierWithIncorrectPassword);

        assertEquals(404, response.statusCode());
    }

    @Test
    @Description("Проверка ошибки при отсутствии логина")
    public void cannotLoginWithoutLogin() {
        createTestCourier();

        Courier courierWithoutLogin = new Courier(
                null,
                testCourier.getPassword()
        );

        Response response =
                courierApi.loginCourier(courierWithoutLogin);

        assertEquals(400, response.statusCode());
    }

    @Test
    @Description("Проверка ошибки при отсутствии пароля")
    public void cannotLoginWithoutPassword() {
        createTestCourier();

        Courier courierWithoutPassword = new Courier(
                testCourier.getLogin(),
                null
        );

        Response response =
                courierApi.loginCourier(courierWithoutPassword);

        assertEquals(504, response.statusCode());
    }

    @Test
    @Description("Проверка ошибки при авторизации несуществующего курьера")
    public void cannotLoginNonexistentCourier() {
        Courier nonexistentCourier = new Courier(
                "nonexistent" + UUID.randomUUID(),
                "password" + UUID.randomUUID()
        );

        Response response =
                courierApi.loginCourier(nonexistentCourier);

        assertEquals(404, response.statusCode());
    }

    @Step("Создать тестового курьера")
    private void createTestCourier() {
        testCourier = new Courier(
                "courier" + UUID.randomUUID(),
                "password" + UUID.randomUUID()
        );

        Response createResponse =
                courierApi.createCourier(testCourier);

        assertEquals(201, createResponse.statusCode());

        // Ручка создания возвращает только ok:true,
        // поэтому id получаем отдельным запросом авторизации
        Response loginResponse =
                courierApi.loginCourier(testCourier);

        assertEquals(200, loginResponse.statusCode());

        courierId = loginResponse.jsonPath().getInt("id");

        assertNotNull(courierId);
    }

    @Step("Проверить успешную авторизацию")
    private void checkSuccessfulLogin(Response response) {
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("id"));
    }

    @AfterEach
    @Step("Удалить тестового курьера")
    public void deleteCourier() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId);
        }
    }
}