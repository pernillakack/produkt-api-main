package com.example.produktapi.service;

import com.example.produktapi.exception.BadRequestException;
import com.example.produktapi.exception.EntityNotFoundException;
import com.example.produktapi.model.Product;
import com.example.produktapi.repository.ProductRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) //bibliotek med funktionalitet för Mock

class ProductServiceTest {
    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService underTest;

    @Captor
    ArgumentCaptor<Product> productCaptor;

    @Test
    void whenGetAllProducts_thenExactlyOneInteractionWithRepositoryMethodFindAll() {
        //when
        underTest.getAllProducts();
        //then
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void whenGetAllCategories_thenExactlyOneInteractionWithRepositoryMethodGetByCategory() {
        //when
        underTest.getAllCategories();
        //then
        verify(repository, times(1)).findAllCategories();
        verifyNoMoreInteractions(repository);
    }
    @Test
    void whenGetProductsByCategory_thenExactlyOneInteractionWithRepositoryMethodFindByCategory() {
        //when
        underTest.getProductsByCategory("Electronics");
        //then
        verify(repository,times(1)).findByCategory("Electronics");
        verifyNoMoreInteractions(repository);
    }
    @Test
    void whenGetProductById_thenGettingThatExactProduct() {
        //given
        int id = 99;
        Product product = new Product("Nytt igen", 30.00, "","","");
        product.setId(id);
        given(repository.findById(id)).willReturn(Optional.of(product));
        //when
        Product product2 = underTest.getProductById(id);
        //then
        assertEquals(product2, product);
    }
    @Test
    void whenGetProductById_ifProductDoesNotExist_thenThrowException(){
        //given
        int id = 99;
        Product product = new Product("Rätt objekt", 4000.0, "","","");
        given(repository.findById(id))
                .willReturn(Optional.empty());
        //when
        //then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                ()-> underTest.getProductById(id));
        assertEquals("Produkt med id " + id + " hittades inte", exception.getMessage());
    }
    @Test
    void whenAddingAProduct_thenSaveMethodShouldBeCalled() {
        //given
        Product product = new Product("Rätt objekt", 4000.0, "","","");
        //when
        underTest.addProduct(product);
        //then
        verify(repository).save(productCaptor.capture());
        assertEquals(product, productCaptor.getValue());
    }
    @Test
    void whenAddingProductWithDuplicateTitle_thenThrowError() {
        //given
        String title ="vår test-title";
        Product product = new Product(title, 50.0,"","","");
        given(repository.findByTitle(title)).willReturn(Optional.of(product));
        //then
        BadRequestException exception = assertThrows(BadRequestException.class,
                //when
                ()-> underTest.addProduct(product));
        assertEquals("En produkt med titeln: " + title + " finns redan", exception.getMessage());
        verify(repository,times(1)).findByTitle(title);
        verify(repository, never()).save(any());

    }
    @Test
    void whenUpdatingProduct_thenSaveMethodShouldBeCalled() {
        //given
        int id = 25;
        Product product = new Product("Nytt igen", 30.00, "","","");
        given(repository.findById(id)).willReturn(Optional.of(product));
        //when
        underTest.updateProduct(product, id);
        //then
        verify(repository,times(1)).save(productCaptor.capture());
        assertEquals(product, productCaptor.getValue());
    }
    @Test
    void whenUpdatingProduct_ifProductIsEmpty_thenThrowException() {
        //given
        int id = 99;
        Product product = new Product("Nytt igen", 30.00, "","","");
        given(repository.findById(99)).willReturn(Optional.empty());
        //when
        //then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> underTest.updateProduct(product,id));
        assertEquals("Produkt med id " + id +" hittades inte", exception.getMessage());
        verify(repository, never()).save(any());
    }
    @Test
    void whenDeletingProduct_thenDeleteByIdShouldBeCalled() {
        //given
        int id = 44;
        Product product = new Product("Ta bort", 30.00,"", "","");
        given(repository.findById(id)).willReturn(Optional.of(product));
        //when
        underTest.deleteProduct(id);
        //then
        verify(repository, times(1)).deleteById(id);
    }
    @Test
    void whenDeletingProduct_ifIdDoesNotExist_thenThrowException() {
        //given
        int id = 99;
        //Product product = new Product("Ta bort", 30.00,"", "","");
        given(repository.findById(id)).willReturn(Optional.empty());
        //when
        //then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                ()-> underTest.deleteProduct(id));
        assertEquals("Produkt med id " + id +" hittades inte", exception.getMessage());
    }

}