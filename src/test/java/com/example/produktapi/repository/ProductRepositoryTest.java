package com.example.produktapi.repository;

import com.example.produktapi.model.Product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository underTest;


    @Test
    void testingOurRepository() {
        List<Product> products = underTest.findAll();
        assertFalse(products.isEmpty());
    }
    @Test
    void whenSearchingForAnExistingCategory_thenReturnThatCategory() {
        //given
        String category = "ny";
        underTest.save(new Product("Ny", 35.00, category, "",""));
        //when
        List<Product>productList = underTest.findByCategory(category);
        //then
        assertFalse(productList.isEmpty());
        assertEquals(category, productList.get(0).getCategory());
    }
    @Test
    void whenSearchingForAnExistingCategory_thenAssertProductListIsNotEmpty() {
        //given
        String category = "ny";
        underTest.save(new Product("Ny", 35.00, category, "", ""));
        //when
        List<Product> productList = underTest.findByCategory(category);
        //then
        assertFalse(productList.isEmpty());
    }
    @Test
    void whenSearchingForNonExistingCategory_thenAssertProductListIsEmpty() {
        //given
        String category = "En kategori som inte finns";
        underTest.save(new Product("Ny", 35.00, "Electronik", "", ""));
        //when
        List<Product> productList = underTest.findByCategory(category);
        //then
        assertTrue(productList.isEmpty());
    }
    @Test
    @Disabled
    void whenSearchingForNonExistingCategory_thenThrowException() {
        String category = "En kategori som inte finns";
        underTest.save(new Product("Ny", 35.00, "Electronics", "", ""));
        //when
        List<Product> productList = underTest.findByCategory(category);
        //then
        assertTrue(productList.isEmpty());
        //assertThrows(IndexOutOfBoundsException.class, ()->); LÄGG TILL TEXT SOM TALAR OM ATT CATEGORIN INTE FINNS!!!
    }
    @Test
    void whenSearchingForAnExistingTitle_thenReturnThatProduct(){
        //given
        String title = "En dator";
        underTest.save(new Product(title, 2500.0, "Elektronik", "",""));
        //when
        Optional<Product> optionalProduct = underTest.findByTitle("En dator");
        //then
        assertTrue(optionalProduct.isPresent());
        assertFalse(optionalProduct.isEmpty());
        assertEquals(title, optionalProduct.get().getTitle());
    }
    @Test
    void whenSearchingForNonExistingTitle_thenReturnEmptyOptional(){
        //given
        String title = "En titel som absolut inte finns";
        //when
        Optional<Product>optionalProduct = underTest.findByTitle(title);
        //then
        assertAll(
                ()-> assertFalse(optionalProduct.isPresent()),
                () -> assertTrue(optionalProduct.isEmpty()),
                () -> assertThrows(NoSuchElementException.class, ()-> optionalProduct.get())
        );
    }
    @Test
    void whenSearchingForAllCategories_thenReturnAllCategories() {
        //given
        List<String>categories = new ArrayList<>();
        categories.add("electronics");
        categories.add("jewelery");
        categories.add("men's clothing");
        categories.add("women's clothing");
        //when
        List<String>categoryList = underTest.findAllCategories();
        //then
        assertFalse(categoryList.isEmpty());
        assertEquals(categories,categoryList);
    }
}