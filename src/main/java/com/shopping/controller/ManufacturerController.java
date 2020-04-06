package com.shopping.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopping.dto.ListDTO;
import com.shopping.entity.Manufacturer;
import com.shopping.service.IManufacturerService;
import com.shopping.util.PageModel;
import com.shopping.util.ResponseModel;

@RestController
@RequestMapping("/manufacturers")
public class ManufacturerController {
	
	@Autowired
	private IManufacturerService manufacturerService;
	
	@GetMapping
	public ResponseModel<PageModel<Manufacturer>> getManufacturerPage(@RequestParam int page, @RequestParam int size) {
		return manufacturerService.findAll(page, size);
	}
	
//	@GetMapping
//	public List<Manufacturer> getAllManufacturers() {
//		return manufacturerService.findAll();
//	}
//	
//	@GetMapping("/{manufacturerId}")
//	public Optional<Manufacturer> getOneManufacturer( @PathVariable int manufacturerId) {
//		return manufacturerService.findById(manufacturerId);
//	}
//	
	@PostMapping
	public  ResponseModel<Manufacturer> addManufacturer(@RequestBody Manufacturer manufacturer) {
		return manufacturerService.add(manufacturer);
	}
//	
//	@DeleteMapping("/{manufacturerId}")
//	public void deleteManufacturerById(@PathVariable int manufacturerId) {
//		manufacturerService.deleteById(manufacturerId);
//	}
//	
//	@PutMapping
//	public void updateManufacturer(@RequestBody Manufacturer manufacturer) {
//		manufacturerService.update(manufacturer);
//	}
	
	
}
