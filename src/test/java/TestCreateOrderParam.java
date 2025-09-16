import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.constants.ConstantsApiAndUrl;
import org.example.model.Order;
import org.example.steps.StepOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.baseURI;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class TestCreateOrderParam {

    private final String[] colors;
    private StepOrder steps;
    private ValidatableResponse response;

    public TestCreateOrderParam(String[] colors) {
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
        steps = new StepOrder();
        baseURI = ConstantsApiAndUrl.BASE_URL;
    }


    @After
    public void tearDown() {
        if (response != null) {
            int track = steps.getTrackFromResponse(response);
            steps.cancelOrder(track);
        }
    }

    @Test
    @DisplayName("Создание заказа с параметризацией цвета")
    public void createOrderWithColorsTest() {
        Order order = new Order();
        steps.generateOrder(order, colors);


        response = steps.createOrder(order);

        response.statusCode(SC_CREATED)
                .body("track", notNullValue());

    }


}
