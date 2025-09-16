import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.example.ConstantsApiAndUrl;
import org.example.Courier;
import org.example.CourierLogin;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.example.ConstantsApiAndUrl.BASE_NAME;
import static org.example.ConstantsApiAndUrl.BASE_PASSWORD;

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
    public ValidatableResponse deleteCourier(int id) {
        return given()
                .when()
                .delete(String.format(ConstantsApiAndUrl.COURIER_DELETE_API, id))
                .then();
    }
}
