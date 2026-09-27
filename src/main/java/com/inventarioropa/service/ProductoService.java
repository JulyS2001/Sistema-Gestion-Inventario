package com.inventarioropa.service;

import java.math.BigDecimal;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventarioropa.model.Producto;
import com.inventarioropa.repository.ProductoRepository;

@Service
public class ProductoService {
	
	private final ProductoRepository productoRepository; 
	
	public ProductoService(ProductoRepository productoRepository) {
		this.productoRepository = productoRepository;
	}
	
	public List<Producto> listarProductos(){
		return productoRepository.findAll();
	}
	
	public Producto buscarPorId(Long id) {
		return productoRepository.findById(id).orElse(null);
	}
	
	public Producto guardarProducto(Producto producto) {
		return productoRepository.save(producto);
	}
	
	public void eliminarProducto(Long id) {
		productoRepository.deleteById(id);
	}
	
	public long contarProductos() {
	    return productoRepository.count();
	}

	public int obtenerStockTotal() {
	    return productoRepository.findAll()
	            .stream()
	            .mapToInt(Producto::getStock)
	            .sum();
	}

	public BigDecimal calcularValorInventario() {
	    return productoRepository.findAll()
	            .stream()
	            .map(producto ->
	                    producto.getPrecio()
	                            .multiply(BigDecimal.valueOf(producto.getStock()))
	            )
	            .reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public long contarStockBajo() {
	    return productoRepository.findAll()
	            .stream()
	            .filter(producto -> producto.getStock() <= 5)
	            .count();
	}
}
