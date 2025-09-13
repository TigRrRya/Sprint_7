import io.qameta.allure.junit4.DisplayName;
import org.example.ConstantsApiAndUrl;
import org.example.Courier;
import org.example.CourierLogin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsString;

public class TestCourierApi {

    private Courier courier;
    private StepCourier steps;
    private int courierId = -1;

    @Before
    public void setUp() {
        baseURI = ConstantsApiAndUrl.BASE_URL;
        steps = new StepCourier();
        courier = new Courier("Sam-test", "qwer", "Sam");
    }

    @After
    public void tearDown() {
        if (courierId > 0) {
            StepCourier.deleteCourier(courierId)
                    .statusCode(200);
        }
    }

    // Тесты регистрации
    @Test
    @DisplayName("Регистрация курьера с заданными данными")
    public void createCourierTest() {
        steps.createCourier(courier)
                .statusCode(201)
                .statusLine(containsString("Created"))
                .body("ok", is(true));
        courierId = steps.loginCourierId(courier);
    }

    @Test
    @DisplayName("Регистрация 2х одинаковых курьеров")
    public void createTwoCourierTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        steps.createCourier(courier)
                .statusCode(409)
                .statusLine(containsString("Conflict"));
    }

    @Test
    @DisplayName("Регистрация курьера с существующим логином")
    public void createCourierDoubleLoginTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        Courier courierDoubleLogin = new Courier("Sam-tester", "4321", "Вини-пух");
        steps.createCourier(courierDoubleLogin)
                .statusCode(409)
                .statusLine(containsString("Conflict"))
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Регистрация без логина")
    public void createCourierNoLoginTest() {
        Courier courierNotLogin = new Courier(null, "998", "Александр");

        steps.createCourier(courierNotLogin)
                .statusCode(400)
                .statusLine(containsString("Bad Request"))
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Регистрация без пароля")
    public void createCourierNoPassTest() {
        Courier courierNotPass = new Courier("Sam-test", null, "Александр");

        steps.createCourier(courierNotPass)
                .statusCode(400)
                .statusLine(containsString("Bad Request"))
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Регистрация без имени")
    public void createCourierNoNameTest() {
        Courier courierNotName = new Courier("Sam-test", "qwer", null);

        steps.createCourier(courierNotName)
                .statusCode(400)
                .statusLine(containsString("Bad Request"))
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

    }


    // Тесты авторизации
    @Test
    @DisplayName("Авторизация курьера и получение ID")
    public void loginCourierTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        steps.loginCourierNegative(courier)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Авторизация без логина")
    public void loginCourierNullLoginTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        CourierLogin loginNull = new CourierLogin(null, courier.getPassword());

        steps.loginCourierNegative(loginNull)
                .then()
                .statusCode(400)
                .statusLine(containsString("Bad Request"))
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация без пароля")
    public void loginCourierNullPassTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        CourierLogin passNull = new CourierLogin(courier.getLogin(), null);

        steps.loginCourierNegative(passNull)
                .then()
                .statusCode(400)
                .statusLine(containsString("Bad Request"))
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация без логина и пароля")
    public void loginCourierNullLoginAndPassTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        CourierLogin nullLoginPass = new CourierLogin(null, null);

        steps.loginCourierNegative(nullLoginPass)
                .then()
                .statusCode(400)
                .statusLine(containsString("Bad Request"))
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    public void loginCourierFakeLoginTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        CourierLogin fakeLogin = new CourierLogin("wtf", courier.getPassword());

        steps.loginCourierNegative(fakeLogin)
                .then()
                .statusCode(404)
                .statusLine(containsString("Not Found"))
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    public void loginCourierFakePassTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        CourierLogin fakePass = new CourierLogin(courier.getLogin(), "wtf");

        steps.loginCourierNegative(fakePass)
                .then()
                .statusCode(404)
                .statusLine(containsString("Not Found"))
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация с неверным логином и паролем")
    public void loginCourierFakeLogAndPassTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        CourierLogin fakeLoginPass = new CourierLogin("afc", "wtf");

        steps.loginCourierNegative(fakeLoginPass)
                .then()
                .statusCode(404)
                .statusLine(containsString("Not Found"))
                .body("message", equalTo("Учетная запись не найдена"));
    }

}
