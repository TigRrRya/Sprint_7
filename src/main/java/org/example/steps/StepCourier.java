package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.example.constants.ConstantsApiAndUrl;
import org.example.model.Courier;
import org.example.model.CourierLogin;

import static io.restassured.RestAssured.given;

public class StepCourier extends BaseApi {

    @Step("Создание курьера")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .spec(requestSpec)
                .body(courier)
                .when()
                .post(ConstantsApiAndUrl.COURIER_API)
                .then();
    }

    @Step("Авторизация курьера (негативный сценарий)")
    public Response loginCourierNegative(CourierLogin courierLogin) {
        return given()
                .spec(requestSpec)
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
                .spec(requestSpec)
                .when()
                .delete(String.format(ConstantsApiAndUrl.COURIER_DELETE_API, id))
                .then();
    }

    @Step("Удаление курьера с null в теле запроса")
    public ValidatableResponse deleteCourierNullInBody() {
        return given()
                .spec(requestSpec)
                .body("{ \"id\": null }")
                .when()
                .delete(ConstantsApiAndUrl.COURIER_API)
                .then();
    }

    @Step("Удаление курьера с несуществующим id")
    public ValidatableResponse deleteCourierFakeId() {
        return given()
                .spec(requestSpec)
                .when()
                .delete("/api/v1/courier/12345678")
                .then();
    }
}

