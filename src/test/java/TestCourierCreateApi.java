import io.qameta.allure.junit4.DisplayName;
import org.example.constants.ConstantsApiAndUrl;
import org.example.model.Courier;

import org.example.steps.StepCourier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.baseURI;

import static org.apache.http.HttpStatus.*;
import static org.example.constants.ConstantsApiAndUrl.BASE_NAME;
import static org.example.constants.ConstantsApiAndUrl.BASE_PASSWORD;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsString;

public class TestCourierCreateApi {

    private Courier courier;
    private StepCourier steps;
    private int courierId = -1;

    @Before
    public void setUp() {
        baseURI = ConstantsApiAndUrl.BASE_URL;
        steps = new StepCourier();
        courier = steps.generateUniqueCourier();
    }

    @After
    public void tearDown() {
        if (courierId > 0) {
            steps.deleteCourier(courierId);
        }
    }



    @Test
    @DisplayName("Регистрация курьера с заданными данными")
    public void createCourierTest() {
        steps.createCourier(courier)
                .statusCode(SC_CREATED)
                .body("ok", is(true));
        courierId = steps.loginCourierId(courier);
    }

    @Test
    @DisplayName("Получение ошибки 409 при регистрация 2х одинаковых курьеров")
    public void createTwoCourierTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        steps.createCourier(courier)
                .statusCode(SC_CONFLICT);
    }

    @Test
    @DisplayName("Получение ошибки 409 при регистрации курьера с существующим логином")
    public void createCourierDoubleLoginTest() {
        steps.createCourier(courier);
        courierId = steps.loginCourierId(courier);

        Courier anotherCourier = new Courier(courier.getLogin(), steps.generateRandomPassword(), "Aleksandr");
        steps.createCourier(anotherCourier)
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Получение ошибки 400 при регистрация без логина")
    public void createCourierNoLoginTest() {
        Courier courierNoLogin = new Courier(null, BASE_PASSWORD, BASE_NAME);

        steps.createCourier(courierNoLogin)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Получение ошибки 400 при регистрация без пароля")
    public void createCourierNoPassTest() {
        Courier courierNotPass = new Courier(steps.generateUniqueCourier().getLogin(), null, BASE_NAME);

        steps.createCourier(courierNotPass)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}