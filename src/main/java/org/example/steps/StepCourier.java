package org.example.steps;

import io.qameta.allure.Step;



import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.example.constants.ConstantsApiAndUrl;
import org.example.model.Courier;
import org.example.model.CourierLogin;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.example.constants.ConstantsApiAndUrl.BASE_NAME;
import static org.example.constants.ConstantsApiAndUrl.BASE_PASSWORD;

public class StepCourier {

    @Step("Генерация уникального курьера")
    public Courier generateUniqueCourier() {
        String uniqueLogin = "user-" + UUID.randomUUID().toString().substring(0, 8);
        return new Courier(uniqueLogin, BASE_PASSWORD, BASE_NAME);
    }

    @Step("Генерация случайного логина")
    public String generateRandomLogin() {
        return "fake-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Step("Генерация случайного пароля")
    public String generateRandomPassword() {
        return "pass-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Step("Создание курьера")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .when()
                .post(ConstantsApiAndUrl.COURIER_API)
                .then();
    }

    @Step("Авторизация курьера (негативный сценарий)")
    public Response loginCourierNegative(CourierLogin courierLogin) {
        return given()
                .header("Content-Type", "application/json")
                .body(courierLogin)
                .when()
                .post(ConstantsApiAndUrl.COURIER_LOGIN_API);
    }

    @Step("Авторизация курьера (позитивный сценарий)")
    public Response loginCourier(Courier courier) {
        return loginCourierNegative(new CourierLogin(courier.getLogin(), courier.getPassword()));
    }

    @Step("Авторизация курьера и получение id")
    public Integer loginCourierId(Courier courier) {
        return loginCourier(courier)
                .then()
                .extract()
                .path("id");
    }

    @Step("Удаление курьера по id {id}")
    public ValidatableResponse deleteCourier(int id) {
        return given()
                .when()
                .delete(String.format(ConstantsApiAndUrl.COURIER_DELETE_API, id))
                .then();
    }
}
