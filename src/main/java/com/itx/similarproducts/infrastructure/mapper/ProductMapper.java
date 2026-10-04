package com.itx.similarproducts.infrastructure.mapper;

import com.itx.similarproducts.domain.model.Product;
import com.itx.similarproducts.infrastructure.dto.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  Product toEntity(ProductResponse response);
}
