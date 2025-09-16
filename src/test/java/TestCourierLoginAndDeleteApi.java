import io.qameta.allure.junit4.DisplayName;
import org.example.constants.ConstantsApiAndUrl;
import org.example.model.Courier;
import org.example.model.CourierLogin;
import org.example.steps.StepCourier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsString;

public class TestCourierLoginApi {

    private Courier courier;
    private StepCourier steps;
    private int courierId = -1;

    @Before
    public void setUp() {
        baseURI = ConstantsApiAndUrl.BASE_URL;
        steps = new StepCourier();
        courier = steps.generateUniqueCourier();
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);
    }

    @After
    public void tearDown() {
        if (courierId > 0) {
            steps.deleteCourier(courierId);
        }
    }

    // Тесты авторизации
    @Test
    @DisplayName("Авторизация курьера и получение ID")
    public void loginCourierTest() {
        steps.loginCourier(courier)
                .then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Ошибка 400 при авторизации без логина")
    public void loginCourierNullLoginTest() {
        CourierLogin loginNull = new CourierLogin(null, courier.getPassword());
        steps.loginCourierNegative(loginNull)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка 400 при авторизации без пароля")
    public void loginCourierNullPassTest() {
        CourierLogin passNull = new CourierLogin(courier.getLogin(), null);
        steps.loginCourierNegative(passNull)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка 400 при авторизации без логина и пароля")
    public void loginCourierNullLoginAndPassTest() {
        CourierLogin nullLoginPass = new CourierLogin(null, null);
        steps.loginCourierNegative(nullLoginPass)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка 404 при авторизации с неверным логином")
    public void loginCourierFakeLoginTest() {
        CourierLogin fakeLogin = new CourierLogin(steps.generateRandomLogin(), courier.getPassword());
        steps.loginCourierNegative(fakeLogin)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка 404 при авторизации с неверным паролем")
    public void loginCourierFakePassTest() {
        CourierLogin fakePass = new CourierLogin(courier.getLogin(), steps.generateRandomPassword());
        steps.loginCourierNegative(fakePass)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка 404 при авторизации с неверным логином и паролем")
    public void loginCourierFakeLogAndPassTest() {
        CourierLogin fakeLoginPass = new CourierLogin(steps.generateRandomLogin(), steps.generateRandomPassword());
        steps.loginCourierNegative(fakeLoginPass)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    // Тесты удаления курьера
    @Test
    @DisplayName("Удаление курьера")
    public void deleteCourierTest() {
        steps.deleteCourier(courierId)
                .statusCode(SC_OK)
                .body("ok", is(true));
        courierId = -1; // чтобы @After не удалял второй раз
    }

    @Test
    @DisplayName("Ошибка 400 при удалении курьера с null id в теле")
    public void deleteCourierNullIdInBodyTest() {
        given()
                .header("Content-Type", "application/json")
                .body("{ \"id\": null }")
                .when()
                .delete("/api/v1/courier")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Ошибка 404 при удалении курьера с несуществующим id")
    public void deleteCourierFakeIdTest() {
        given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/api/v1/courier/12345678")
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString("Курьера с таким id нет"));
    }
}
