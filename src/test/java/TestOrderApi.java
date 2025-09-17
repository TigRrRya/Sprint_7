import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.model.Courier;
import org.example.model.Order;
import org.example.steps.StepCourier;
import org.example.steps.StepGenerationCourier;
import org.example.steps.StepGenerationOrder;
import org.example.steps.StepOrder;
import org.junit.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class TestOrderApi {

    private StepOrder stepOrder;
    private StepGenerationOrder stepGenOrder;
    private StepCourier stepCourier;
    private StepGenerationCourier stepGenCourier;

    private Courier courier;
    private int courierId;
    private Order order;
    private int track;
    private int orderId;
    private ValidatableResponse response;

    @Before
    public void setUp() {
        stepOrder = new StepOrder();
        stepGenOrder = new StepGenerationOrder();
        stepCourier = new StepCourier();
        stepGenCourier = new StepGenerationCourier();

        courier = stepGenCourier.generateUniqueCourier();
        stepCourier.createCourier(courier);
        courierId = stepCourier.loginCourierId(courier);

        order = new Order();
        stepGenOrder.generateOrder(order, new String[]{"BLACK"});
        response = stepOrder.createOrder(order); // сохраняем ответ
        track = stepOrder.getTrackFromResponse(response);
        orderId = stepOrder.getOrderIdByTrack(track);
    }

    @After
    public void tearDown() {
        if (track > 0) {
            stepOrder.cancelOrder(track);
        }

        if (courierId > 0) {
            stepCourier.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Получение списка всех заказов")
    public void getListOrderTest() {
        stepOrder.getOrdersList()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders", isA(java.util.List.class));
    }

    @Test
    @DisplayName("Получить заказ по реальному треку")
    public void getOrderObjectByRealTrackTest() {
        stepOrder.getOrderByTrack(track)
                .statusCode(SC_OK)
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Ошибка 400 при запросе без track")
    public void getOrderNullTrackTest() {
        stepOrder.getOrderByTrack(null)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Ошибка 404 при запросе с нереальным track")
    public void getOrderFakeTrackTest() {
        stepOrder.getOrderByTrack(9999111)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказ не найден"));
    }

    @Test
    @DisplayName("Принятие заказа существующим курьером")
    public void acceptRealOrderRealCourierTest() {
        stepOrder.acceptOrder(orderId, courierId)
                .statusCode(SC_OK)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Ошибка 400 при принятии заказа без id курьера")
    public void acceptRealOrderNullIdCourierTest() {
        stepOrder.acceptOrder(orderId, null)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Ошибка 404 при принятии заказа с несуществующим id курьера")
    public void acceptRealOrderFakeIdCourierTest() {
        stepOrder.acceptOrder(orderId, 9999999)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Ошибка 400 при принятии заказа с null id заказа")
    public void acceptNullIdOrderRealCourierTest() {
        stepOrder.acceptOrder(null, courierId)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Ошибка 404 при принятии заказа с несуществующим id заказа")
    public void acceptFakeIdOrderRealCourierTest() {
        stepOrder.acceptOrder(12345678, courierId)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказа с таким id не существует"));
    }
}
