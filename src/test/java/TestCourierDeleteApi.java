import io.qameta.allure.junit4.DisplayName;
import org.example.steps.StepCourier;
import org.example.steps.StepGenerationCourier;
import org.example.model.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class TestCourierDeleteApi {

    private StepCourier stepCourier;
    private int courierId = -1;

    @Before
    public void setUp() {
        stepCourier = new StepCourier();
        StepGenerationCourier stepGen = new StepGenerationCourier();
        Courier courier = stepGen.generateUniqueCourier();
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
    @DisplayName("Удаление курьера")
    public void deleteCourierTest() {
        stepCourier.deleteCourier(courierId)
                .statusCode(SC_OK)
                .body("ok", is(true));
        courierId = -1; // чтобы @After не удалял второй раз
    }

    @Test
    @DisplayName("Ошибка 400 при удалении курьера с null id в теле")
    public void deleteCourierNullIdInBodyTest() {
        stepCourier.deleteCourierNullInBody()
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Ошибка 404 при удалении курьера с несуществующим id")
    public void deleteCourierFakeIdTest() {
        stepCourier.deleteCourierFakeId()
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString("Курьера с таким id нет"));
    }
}
