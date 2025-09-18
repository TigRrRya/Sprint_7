import io.qameta.allure.junit4.DisplayName;
import org.example.steps.StepCourier;
import org.example.steps.StepGenerationCourier;
import org.example.model.Courier;
import org.example.model.CourierLogin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class TestCourierLoginApi {

    private StepCourier stepCourier;
    private StepGenerationCourier stepGen;
    private Courier courier;
    private int courierId = -1;

    @Before
    public void setUp() {
        stepCourier = new StepCourier();
        stepGen = new StepGenerationCourier();
        courier = stepGen.generateUniqueCourier();
        stepCourier.createCourier(courier);
        courierId = stepCourier.loginCourierId(courier);
    }

    @After
    public void tearDown() {
        if (courierId > 0) {
            stepCourier.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Авторизация курьера и получение ID")
    public void loginCourierTest() {
        stepCourier.loginCourier(courier)
                .then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Ошибка 400 при авторизации без логина")
    public void loginCourierNullLoginTest() {
        CourierLogin loginNull = new CourierLogin(null, courier.getPassword());
        stepCourier.loginCourierNegative(loginNull)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка 400 при авторизации без пароля")
    public void loginCourierNullPassTest() {
        CourierLogin passNull = new CourierLogin(courier.getLogin(), null);
        stepCourier.loginCourierNegative(passNull)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка 400 при авторизации без логина и пароля")
    public void loginCourierNullLoginAndPassTest() {
        CourierLogin nullLoginPass = new CourierLogin(null, null);
        stepCourier.loginCourierNegative(nullLoginPass)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка 404 при авторизации с неверным логином")
    public void loginCourierFakeLoginTest() {
        CourierLogin fakeLogin = new CourierLogin(stepGen.generateRandomLogin(), courier.getPassword());
        stepCourier.loginCourierNegative(fakeLogin)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка 404 при авторизации с неверным паролем")
    public void loginCourierFakePassTest() {
        CourierLogin fakePass = new CourierLogin(courier.getLogin(), stepGen.generateRandomPassword());
        stepCourier.loginCourierNegative(fakePass)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка 404 при авторизации с неверным логином и паролем")
    public void loginCourierFakeLogAndPassTest() {
        CourierLogin fakeLoginPass = new CourierLogin(stepGen.generateRandomLogin(), stepGen.generateRandomPassword());
        stepCourier.loginCourierNegative(fakeLoginPass)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
