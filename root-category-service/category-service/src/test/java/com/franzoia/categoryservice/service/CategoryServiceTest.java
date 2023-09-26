package com.franzoia.categoryservice.service;

import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CategoryServiceTest {

    @SpyBean
    CategoryService service;

    @Test
    void shouldReturnTheActualNumberOfRecords() {

        //given
        int minNumberOfRecords = 2;

        //when
        int actual = service.findAll().size();

        //then
        assertTrue(actual >= minNumberOfRecords);

    }

    @Test
    void shouldInsertCategoryProperly() throws Exception {
        // given
        CategoryDTO dto = CategoryDTO.builder()
                .name("testing")
                .build();

        // when
        dto = service.create(dto);

        // then
        assertNotNull(dto.id());
    }

    @Test
    void shouldFindCategoryById() throws EntityNotFoundException {
        //given
        long categoryId = 1L;

        //when
        CategoryDTO category = service.getMapper().convertEntityToDTO(service.find(categoryId));

        //then
        assertEquals(categoryId, category.id());
    }

    @Test
    void shouldThrowExceptionWhenNotFoundCategory() {
        // given
        long categoryId = -21312;

        // when
        Executable executable = () -> service.find(categoryId);

        // then
        assertThrows(EntityNotFoundException.class, executable);
    }

    @Test
    void shouldNotAllowAddExistingName() {
        // given
        CategoryDTO dto1 = CategoryDTO.builder()
                .name("first testing")
                .build();

        CategoryDTO dto2 = CategoryDTO.builder()
                .name("first testing")
                .build();

        // when
        Executable executable1 = () -> service.create(dto1);
        Executable executable2 = () -> service.create(dto2);

        // then
        assertAll(
                () -> assertDoesNotThrow(executable1),
                () -> assertThrows(Exception.class, executable2)
        );

    }
}
