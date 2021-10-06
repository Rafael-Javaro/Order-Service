package br.com.codenation.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

	/*Você pode pensar em um stream como um iterator,
	ou seja, um objeto que consegue navegar pelos valores da coleção.*/

	private ProductRepository productRepository = new ProductRepositoryImpl();

	/**
	 * Calculate the sum of all OrderItems
	 */
	@Override
	public Double calculateOrderValue(List<OrderItem> items) {
		return items.stream().mapToDouble(orderItem -> {
			Optional<Product> product = productRepository.findById(orderItem.getProductId());
			Double productValue = product.get().getValue();
			Long quantity = orderItem.getQuantity();
			if (product.get().getIsSale()) { productValue = productValue * 0.8; }
			return productValue * quantity;
		}).sum();
	}

	/**
	 * Map from idProduct List to Product Set
	 */
	@Override
	public Set<Product> findProductsById(List<Long> ids) {
		return this.productRepository.findAll().stream().filter(product -> ids.contains(product.getId())).collect(Collectors.toSet());
	}

	/**
	 * Calculate the sum of all Orders(List<OrderIten>)
	 */
	@Override
	public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
		return orders.stream().mapToDouble(this::calculateOrderValue).sum();
	}

	/**
	 * Group products using isSale attribute as the map key
	 */
	@Override
	public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {
		return this.productRepository.findAll().stream()
				.filter(product -> productIds.contains(product.getId()))
				.collect(Collectors.groupingBy(Product::getIsSale));
	}

}