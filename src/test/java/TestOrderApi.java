import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.constants.ConstantsApiAndUrl;
import org.example.model.Courier;
import org.example.model.Order;
import org.example.steps.StepCourier;
import org.example.steps.StepOrder;
import org.junit.*;

import static io.restassured.RestAssured.baseURI;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class TestOrderApi {

    private StepOrder stepsOrder;
    private StepCourier stepsCourier;
    private Courier courier;
    private int courierId;
    private Order order;
    private int track;
    private int orderId;

    @Before
    public void setUp() {
        baseURI = ConstantsApiAndUrl.BASE_URL;
        stepsOrder = new StepOrder();
        stepsCourier = new StepCourier();


        courier = stepsCourier.generateUniqueCourier();
        stepsCourier.createCourier(courier);
        courierId = stepsCourier.loginCourierId(courier);


        order = new Order();
        stepsOrder.generateOrder(order, new String[]{"BLACK"});
        ValidatableResponse response = stepsOrder.createOrder(order);
        track = stepsOrder.getTrackFromResponse(response);
        orderId = stepsOrder.getOrderIdByTrack(track);
    }

    @After
    public void tearDown() {

        if (track > 0) {
            stepsOrder.cancelOrder(track);
        }


        if (courierId > 0) {
            stepsCourier.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Получение списка всех заказов")
    public void getListOrderTest() {
        stepsOrder.getOrdersList()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders", isA(java.util.List.class));
    }

    @Test
    @DisplayName("Получить заказ по реальному треку")
    public void getOrderObjectByRealTrackTest() {
        stepsOrder.getOrderByTrack(track)
                .statusCode(SC_OK)
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Ошибка 400 при запросе без track")
    public void getOrderNullTrackTest() {
        stepsOrder.getOrderByTrack(null)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Ошибка 404 при запросе с нереальным track")
    public void getOrderFakeTrackTest() {
        stepsOrder.getOrderByTrack(9999111)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказ не найден"));
    }

    @Test
    @DisplayName("Принятие заказа существующим курьером")
    public void acceptRealOrderRealCourierTest() {
        stepsOrder.acceptOrder(orderId, courierId)
                .statusCode(SC_OK)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Ошибка 400 при принятии заказа без id курьера")
    public void acceptRealOrderNullIdCourierTest() {
        stepsOrder.acceptOrder(orderId, null)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Ошибка 404 при принятии заказа с несуществующим id курьера")
    public void acceptRealOrderFakeIdCourierTest() {
        stepsOrder.acceptOrder(orderId, 9999999)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Ошибка 400 при принятии заказа с null id заказа")
    public void acceptNullIdOrderRealCourierTest() {
        stepsOrder.acceptOrder(null, courierId)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Ошибка 404 при принятии заказа с несуществующим id заказа")
    public void acceptFakeIdOrderRealCourierTest() {
        stepsOrder.acceptOrder(12345678, courierId)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказа с таким id не существует"));
    }
}