import io.qameta.allure.junit4.DisplayName;
import org.example.model.Courier;
import org.example.steps.StepCourier;
import org.example.steps.StepGenerationCourier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class TestCourierCreateApi {

    private Courier courier;
    private StepCourier stepCourier;
    private StepGenerationCourier stepGen;
    private int courierId = -1;

    @Before
    public void setUp() {
        stepCourier = new StepCourier();
        stepGen = new StepGenerationCourier();
        courier = stepGen.generateUniqueCourier();
    }

    @After
    public void tearDown() {
        if (courierId > 0) {
            stepCourier.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Регистрация курьера с заданными данными")
    public void createCourierTest() {
        stepCourier.createCourier(courier)
                .statusCode(SC_CREATED)
                .body("ok", is(true));
        courierId = stepCourier.loginCourierId(courier);
    }

    @Test
    @DisplayName("Ошибка 409 при регистрации двух одинаковых курьеров")
    public void createTwoCourierTest() {
        stepCourier.createCourier(courier);
        courierId = stepCourier.loginCourierId(courier);

        stepCourier.createCourier(courier)
                .statusCode(SC_CONFLICT);
    }

    @Test
    @DisplayName("Ошибка 409 при регистрации курьера с существующим логином")
    public void createCourierDoubleLoginTest() {
        stepCourier.createCourier(courier);
        courierId = stepCourier.loginCourierId(courier);

        Courier another = new Courier(courier.getLogin(), stepGen.generateRandomPassword(), "Aleksandr");
        stepCourier.createCourier(another)
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Ошибка 400 при регистрации без логина")
    public void createCourierNoLoginTest() {
        Courier courierNoLogin = new Courier(null, "1234", "Aleksandr");
        stepCourier.createCourier(courierNoLogin)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Ошибка 400 при регистрации без пароля")
    public void createCourierNoPassTest() {
        Courier courierNoPass = new Courier(stepGen.generateUniqueCourier().getLogin(), null, "Aleksandr");
        stepCourier.createCourier(courierNoPass)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}
