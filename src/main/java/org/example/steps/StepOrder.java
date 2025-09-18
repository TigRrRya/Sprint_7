package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.model.Order;

import static io.restassured.RestAssured.given;
import static org.example.constants.ConstantsApiAndUrl.*;

public class StepOrder extends BaseApi {

    @Step("Создание заказа с заданными данными: {order}")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(requestSpec)
                .body(order)
                .when()
                .post(ORDERS_CREATE_AND_GET_TRACK_API)
                .then();
    }

    @Step("Получение track из ответа после создания заказа")
    public int getTrackFromResponse(ValidatableResponse response) {
        return response.extract().path("track");
    }

    @Step("Получение объекта заказа после его создания по track: {track}")
    public ValidatableResponse getOrderByTrack(Integer track) {
        return given()
                .spec(requestSpec)
                .queryParam("t", track)
                .when()
                .get(ORDERS_GET_ID_API)
                .then();
    }

    @Step("Получение id заказа по треку: {track}")
    public int getOrderIdByTrack(int track) {
        return given()
                .spec(requestSpec)
                .queryParam("t", track)
                .when()
                .get(ORDERS_GET_ID_API)
                .then()
                .statusCode(200)
                .extract()
                .path("order.id");
    }

    @Step("Отмена заказа по номеру трека: {track}")
    public void cancelOrder(int track) {
        given()
                .spec(requestSpec)
                .queryParam("track", track)
                .when()
                .put(ORDERS_CANSEL_API)
                .then();
    }

    @Step("Принятие заказа. Order ID: {orderId}, Courier ID: {courierId}")
    public ValidatableResponse acceptOrder(Integer orderId, Integer courierId) {
        return given()
                .spec(requestSpec)
                .queryParam("courierId", courierId)
                .when()
                .put(String.format(ORDERS_ACCEPT_API, orderId))
                .then();
    }

    @Step("Получение списка всех заказов")
    public ValidatableResponse getOrdersList() {
        return given()
                .spec(requestSpec)
                .when()
                .get(ORDERS_CREATE_AND_GET_TRACK_API)
                .then();
    }
}
