import api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Courier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourierCreateTest {

    private final CourierApi courierApi = new CourierApi();

    private String login;
    private String password;
    private Integer courierId;

    @Test
    @Description("Проверка успешного создания курьера")
    public void courierCanBeCreated() {
        Courier courier = createUniqueCourier();

        Response response = courierApi.createCourier(courier);

        checkSuccessfulCreation(response);

        courierId = loginAndGetId(courier);
    }

    @Test
    @Description("Проверка невозможности создания двух одинаковых курьеров")
    public void cannotCreateTwoIdenticalCouriers() {
        Courier courier = createUniqueCourier();

        Response firstResponse = courierApi.createCourier(courier);

        checkSuccessfulCreation(firstResponse);

        courierId = loginAndGetId(courier);

        Response secondResponse = courierApi.createCourier(courier);

        checkErrorResponse(secondResponse, 409);
    }

    @Test
    @Description("Проверка невозможности создания курьера без обязательного поля login")
    public void cannotCreateCourierWithoutLogin() {
        Courier courier = new Courier(
                null,
                "password" + UUID.randomUUID()
        );

        Response response = courierApi.createCourier(courier);

        checkErrorResponse(response, 400);
    }

    @Test
    @Description("Проверка невозможности создания курьера без обязательного поля password")
    public void cannotCreateCourierWithoutPassword() {
        Courier courier = new Courier(
                "courier" + UUID.randomUUID(),
                null
        );

        Response response = courierApi.createCourier(courier);

        checkErrorResponse(response, 400);
    }

    @Step("Создать уникальные данные курьера")
    private Courier createUniqueCourier() {
        login = "courier" + UUID.randomUUID();
        password = "password" + UUID.randomUUID();

        return new Courier(login, password);
    }

    @Step("Проверить успешное создание курьера")
    private void checkSuccessfulCreation(Response response) {
        assertEquals(201, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("ok"));
    }

    @Step("Проверить код ошибки")
    private void checkErrorResponse(
            Response response,
            int expectedStatusCode) {

        assertEquals(expectedStatusCode, response.statusCode());
    }

    @Step("Авторизоваться под созданным курьером и получить его id")
    private Integer loginAndGetId(Courier courier) {
        Response response = courierApi.loginCourier(courier);

        assertEquals(200, response.statusCode());

        return response.jsonPath().getInt("id");
    }

    @AfterEach
    @Step("Удалить созданного курьера")
    public void deleteCourier() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId);
        }
    }
}