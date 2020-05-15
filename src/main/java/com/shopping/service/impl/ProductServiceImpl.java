package com.shopping.service.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shopping.dao.IProductAttributeDAO;
import com.shopping.dao.IProductDAO;
import com.shopping.dao.IProductImageDAO;
import com.shopping.dto.ProductDTO;
import com.shopping.entity.Manufacturer;
import com.shopping.entity.Product;
import com.shopping.entity.ProductAttribute;
import com.shopping.entity.ProductImage;
import com.shopping.service.IProductService;
import com.shopping.util.Constants;
import com.shopping.util.PageModel;
import com.shopping.util.ResponseModel;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {

	@Autowired
	private IProductDAO productDAO;
	
	@Autowired
	private IProductImageDAO productImageDAO;
	
	@Autowired
	private IProductAttributeDAO productAttributeDAO;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public ResponseModel<Product> findById(int productId) {
		// TODO Auto-generated method stub
		Optional<Product> product = productDAO.findById(productId);
		if (product.isPresent())
			return new ResponseModel<Product>(product.get(), HttpStatus.OK, "Get OK");
		else
			return new ResponseModel<Product>(null, HttpStatus.NOT_FOUND, "Get Fail");
	}

	@Override
	public ResponseModel<Product> add(Product product) {
		// TODO Auto-generated method stub			
		String nameWithoutSpecialCharacter = product.getName().replaceAll("[^a-zA-Z0-9\\s+]", "");
		String nameWithoutUnecesarySpace = nameWithoutSpecialCharacter.trim().replaceAll(" +", " ");
		String url = nameWithoutUnecesarySpace.replace(' ', '-').toLowerCase();
		product.setUrl(url);
		productDAO.insertOrUpdate(product);
		
		if(product.getImage()!=null) {
			ProductImage image = new ProductImage(0, product.getImage(), 1, product);
			productImageDAO.insertOrUpdate(image);
		}
			
		return new ResponseModel<Product>(null, HttpStatus.OK, "Insert Product Succesful");
	}

	@Override
	public ResponseModel<Product> update(Product product) {
		// TODO Auto-generated method stub
		ModelMapper modelMapper2 = new ModelMapper();
		modelMapper2.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
		
		Product p = productDAO.findById(product.getId()).get();
		modelMapper2.map(product, p);
		
		//productDAO.insertOrUpdate(p);
		return new ResponseModel<Product>(null, HttpStatus.OK, "Update Product Succesful");
	}

	@Override
	public ResponseModel<PageModel<ProductDTO>> findAll(int pageNumber, int pageSize, Map<String, Object> map) {
		// TODO Auto-generated method stub
		Page<Product> page = productDAO.page(pageNumber, pageSize, map);
		PageModel<Product> pageModel = new PageModel<Product>(page.getContent(), pageNumber, page.getTotalPages());
		ModelMapper modelMapper = new ModelMapper();
		List<ProductDTO> list = Arrays.asList(modelMapper.map(page.getContent(), ProductDTO[].class));
		PageModel<ProductDTO> pagemodel2 = new PageModel<ProductDTO>(list, pageNumber, page.getTotalPages());
		
		
		return new ResponseModel<PageModel<ProductDTO>>(pagemodel2, HttpStatus.OK, "All products");
	}

	@Override
	public ResponseModel<PageModel<Product>> getAll() {
		// TODO Auto-generated method stub
		PageModel<Product> pageModel = new PageModel<Product>(productDAO.getAllProduct(), 1, 0);
		return new ResponseModel<PageModel<Product>>(pageModel, HttpStatus.OK, "All products");
	}
	
	private final Path rootLocation = Paths.get("C://Users//Blindfold Gang//Desktop//images/");

	@Override
	public String storeFile(MultipartFile file) throws IOException {
		// TODO Auto-generated method stub
			String extension = FilenameUtils.getExtension(file.getOriginalFilename());
			String fileName = UUID.randomUUID().toString() +"."+ extension;
			System.out.println(file.getOriginalFilename());
			System.out.println(file.getSize());
			Files.copy(file.getInputStream(), this.rootLocation.resolve(fileName));
			return fileName;
	}

	@Override
	public ResponseModel<Product> deleteById(int manufacturerId) {
		// TODO Auto-generated method stub
		productDAO.deleteById(manufacturerId);
		return new ResponseModel<Product>(null, HttpStatus.OK, "Delete Product Successful");
	}

	@Override
	public ResponseModel<Boolean> isNameExist(String name) {
		// TODO Auto-generated method stub
		boolean isNameExist = productDAO.isNameExist(name);
		return new ResponseModel<Boolean>(isNameExist, HttpStatus.OK, "isNameExist");
	}

	@Override
	public ResponseModel<Boolean> isSkuExist(String sku) {
		// TODO Auto-generated method stub
		boolean isSkuExist = productDAO.isSkuExist(sku);
		return new ResponseModel<Boolean>(isSkuExist, HttpStatus.OK, "isNameExist");
	}

	@Override
	public ResponseModel<Boolean> isUrlExist(String url) {
		// TODO Auto-generated method stub
		boolean isUrlExist = productDAO.isUrlExist(url);
		return new ResponseModel<Boolean>(isUrlExist, HttpStatus.OK, "isNameExist");
	}

	@Override
	public ResponseModel<Product> updateDisplayOrder(int imageId1, int imageId2) {
		// TODO Auto-generated method stub
		ProductImage image1 = productImageDAO.findById(imageId1).get();
		ProductImage image2 = productImageDAO.findById(imageId2).get();
		
		int temp1 = image1.getDisplayOrder();
		int temp2 = image2.getDisplayOrder();
		image1.setDisplayOrder(temp2);
		image2.setDisplayOrder(temp1);
		
		return new ResponseModel<Product>(null, HttpStatus.OK, "Update Display Order");
	}

	@Override
	public ResponseModel<List<ProductImage>> deleteProductImage(int imageId) {
		// TODO Auto-generated method stub
		ProductImage image = productImageDAO.findById(imageId).get();
		int productId = image.getProductEntity().getId();
		int displayOrder = image.getDisplayOrder();
		productImageDAO.delete(image);
		
		List<ProductImage> listNeedRearrange = productImageDAO.findListAfterDelete(displayOrder, productId);
		if(listNeedRearrange.size()!=0) {
			for (ProductImage productImage : listNeedRearrange) {
				productImage.setDisplayOrder(productImage.getDisplayOrder()-1);
			}
		}
		
		List<ProductImage> listImage = productImageDAO.findByProductId(productId);

		return new ResponseModel<List<ProductImage>>(listImage, HttpStatus.OK, "Delete Success");
	}

	@Override
	public ResponseModel<List<ProductImage>> addProductImage(ProductImage productImage) {
		// TODO Auto-generated method stub
		//System.out.println(productImage.getProductEntity().getId());
		productImageDAO.insertOrUpdate(productImage);
		List<ProductImage> listImage = productImageDAO.findByProductId(productImage.getProductEntity().getId());
		return new ResponseModel<List<ProductImage>>(listImage, HttpStatus.OK, "Add Image Success");
	}

	@Override
	public ResponseModel<List<ProductAttribute>> deleteProductAttribute(int productAttributeId) {
		// TODO Auto-generated method stub
		int productId = productAttributeDAO.findById(productAttributeId).get().getProductEntity().getId();
		productAttributeDAO.deleteById(productAttributeId);
		List<ProductAttribute> list = productAttributeDAO.findByProductId(productId);
		return new ResponseModel<List<ProductAttribute>>(list, HttpStatus.OK, "Delete Success");
	}

	@Override
	public ResponseModel<Boolean> isValueExist(String value, int productId) {
		// TODO Auto-generated method stub
		boolean isValueExist = productAttributeDAO.isValueExist(value, productId);
		return new ResponseModel<Boolean>(isValueExist, HttpStatus.OK, "isValueExist");
	}

	@Override
	public ResponseModel<List<ProductAttribute>> insertOrUpdateAttribue(ProductAttribute productAttribute) {
		// TODO Auto-generated method stub
		ModelMapper modelMapper2 = new ModelMapper();
		modelMapper2.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
		
		if(productAttribute.getId()==0) {
			productAttributeDAO.insertOrUpdate(productAttribute);
		} else {
			ProductAttribute p = productAttributeDAO.findById(productAttribute.getId()).get();		
			modelMapper2.map(productAttribute, p);
		}
		
		List<ProductAttribute> list = productAttributeDAO.findByProductId(productAttribute.getProductEntity().getId());
		return new ResponseModel<List<ProductAttribute>>(list, HttpStatus.OK, "Update Success");
		
	}

}
