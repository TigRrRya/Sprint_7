package org.example.steps;

import io.qameta.allure.Step;
import org.example.model.Order;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class StepGenerationOrder extends BaseApi {

    @Step("Генерация случайного заказа")
    public void generateOrder(Order order, String[] colors) {
        order.setColor(colors);
        order.setFirstName("FN-" + UUID.randomUUID().toString().substring(0, 5));
        order.setLastName("LN-" + UUID.randomUUID().toString().substring(0, 5));
        order.setAddress("Address-" + UUID.randomUUID().toString().substring(0, 5));
        order.setPhone("8800" + (10000000 + (int) (Math.random() * 90000000)));
        order.setRentTime(1 + (int) (Math.random() * 10));
        order.setDeliveryDate(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        order.setComment("Comment-" + UUID.randomUUID().toString().substring(0, 5));
    }
}
