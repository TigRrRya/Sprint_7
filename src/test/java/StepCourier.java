import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.example.ConstantsApiAndUrl;
import org.example.Courier;
import org.example.CourierLogin;

import static io.restassured.RestAssured.given;

public class StepCourier {

    // Создание курьера
    @Step("POST " + ConstantsApiAndUrl.COURIER_API + " - создать курьера")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .header("Content-Type", "application/json")
                .body(courier)
                .when()
                .post(ConstantsApiAndUrl.COURIER_API)
                .then();
    }


    // Авторизация для негативных тестов
    @Step("POST " + ConstantsApiAndUrl.COURIER_LOGIN_API + " - авторизация курьера по классу CourierLogin")
    public Response loginCourierNegative(CourierLogin courierLogin) {
        return given()
                .header("Content-Type", "application/json")
                .body(courierLogin)
                .when()
                .post(ConstantsApiAndUrl.COURIER_LOGIN_API);
    }


    // Авторизация для успешного логина (возврат Response)
    @Step("POST " + ConstantsApiAndUrl.COURIER_LOGIN_API + " - авторизация курьера по классу Courier")
    public Response loginCourierNegative(Courier courier) {
        return loginCourierNegative(new CourierLogin(courier.getLogin(), courier.getPassword()));
    }


    // Получение ID курьера
    @Step("Авторизация курьера и получение id")
    public Integer loginCourierId(Courier courier) {
        return loginCourierNegative(courier)
                .then()
                .extract()
                .path("id");
    }

    //Удаление курьера
    @Step("DELETE " + ConstantsApiAndUrl.COURIER_DELETE_API + " - удаление курьера")
    public static ValidatableResponse deleteCourier(int id) {
        return given()
                .when()
                .delete(String.format(ConstantsApiAndUrl.COURIER_DELETE_API, id))
                .then();
    }
}
