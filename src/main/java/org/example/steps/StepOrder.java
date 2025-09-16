package org.example.steps;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.example.model.Order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.example.constants.ConstantsApiAndUrl.*;


public class StepOrder {

    @Step("Генерация случайного заказа")
    public void generateOrder(Order order, String[] colors) {
        order.setColor(colors);
        order.setFirstName("FN-" + UUID.randomUUID().toString().substring(0, 5));
        order.setLastName("LN-" + UUID.randomUUID().toString().substring(0, 5));
        order.setAddress("Address-" + UUID.randomUUID().toString().substring(0, 5));
        order.setPhone("8800" + (10000000 + (int) (Math.random() * 90000000)));
        order.setRentTime(1 + (int) (Math.random() * 10));
        order.setDeliveryDate(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        order.setComment("Comment-" + UUID.randomUUID().toString().substring(0, 5));
    }

    @Step("Создание заказа с заданными данными: {order} ")
    public ValidatableResponse createOrder(Order order) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(ORDERS_CREATE_AND_GET_TRACK_API)
                .then();
    }


    @Step("Получение track из ответа после создания заказа: {response}")
    public int getTrackFromResponse(ValidatableResponse response) {
        return response.extract().path("track");
    }

    @Step("Получение объекта заказа после его создания по track: {track}")
    public ValidatableResponse getOrderByTrack(Integer track) {
        var request = RestAssured.given().header("Content-Type", "application/json");

        if (track != null) {
            request.queryParam("t", track);
        }

        return request
                .when()
                .get(ORDERS_GET_ID_API)
                .then();
    }

    @Step("Получение id заказа по треку: {track}")
    public int getOrderIdByTrack(int track) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
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
        RestAssured.given()
                .header("Content-type", "application/json")
                .queryParam("track", track)
                .when()
                .put(ORDERS_CANSEL_API)
                .then();
    }

    @Step("Принятие заказа. Order ID: {orderId}, Courier ID: {courierId}")
    public ValidatableResponse acceptOrder(Integer orderId, Integer courierId) {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .queryParam("courierId", courierId)
                .when()
                .put(String.format(ORDERS_ACCEPT_API, orderId))
                .then();
    }

    @Step("Получение списка всех заказов")
    public ValidatableResponse getOrdersList() {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .when()
                .get(ORDERS_CREATE_AND_GET_TRACK_API)
                .then();
    }


}
