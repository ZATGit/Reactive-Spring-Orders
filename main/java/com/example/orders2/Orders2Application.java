package com.example.orders2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class Orders2Application {

	public static void main(String[] args) {
		SpringApplication.run(Orders2Application.class, args);
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Order {
	private Integer id;
	private Integer customerId;
}

@Controller
class OrderRSocketController {

	private final Map<Integer, Collection<Order>> db = new ConcurrentHashMap<>();

	OrderRSocketController() {
		for (var customerId = 1; customerId <= 8; customerId++) {
			var orders = new ArrayList<Order>();
			for (var orderId = 1; orderId <= (Math.random() * 100); orderId++) {
				orders.add(new Order(orderId, customerId));
			}
			this.db.put(customerId, orders);
		}
	}

	@MessageMapping("orders.{customerId}")
	Flux<Order> getOrdersFor(@DestinationVariable Integer customerId) {
		var list = this.db.get(customerId);
		return Flux.fromIterable(list);
	}
}
