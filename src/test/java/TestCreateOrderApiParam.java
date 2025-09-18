import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.model.Order;
import org.example.steps.StepOrder;
import org.example.steps.StepGenerationOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class TestCreateOrderApiParam {

    private final String[] colors;
    private StepOrder stepOrder;
    private StepGenerationOrder stepGenOrder;
    private ValidatableResponse response;

    public TestCreateOrderApiParam(String[] colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Цвета заказа: {0}")
    public static Object[][] data() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}
        };
    }

    @Before
    public void setUp() {
        stepOrder = new StepOrder();
        stepGenOrder = new StepGenerationOrder();
    }

    @After
    public void tearDown() {
        if (response != null) {
            int track = stepOrder.getTrackFromResponse(response);
            stepOrder.cancelOrder(track);
        }
    }

    @Test
    @DisplayName("Создание заказа с параметризацией цвета")
    public void createOrderWithColorsTest() {
        Order order = new Order();
        stepGenOrder.generateOrder(order, colors);

        response = stepOrder.createOrder(order);

        response.statusCode(SC_CREATED)
                .body("track", notNullValue());
    }
}
