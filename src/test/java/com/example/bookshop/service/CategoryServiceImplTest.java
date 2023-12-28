package com.example.bookshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bookshop.dto.category.CategoryDto;
import com.example.bookshop.dto.category.CreateCategoryRequestDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.CategoryMapper;
import com.example.bookshop.model.Category;
import com.example.bookshop.repository.category.CategoryRepository;
import com.example.bookshop.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Biography");
        category.setDescription("Biographical books");
    }

    @AfterEach
    void tearDown() {
        category = null;
    }

    @Test
    @DisplayName("Verify findAll() method works")
    public void findAll_ValidPageable_ShouldReturnListCategoryDto() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());
        CategoryDto categoryDto = createCategoryDto();
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);
        List<CategoryDto> actual = categoryService.findAll(pageable);
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(categoryDto);
    }

    @Test
    @DisplayName("Verify getById() method works")
    public void getById_ValidId_ShouldReturnCategoryDto() {
        CategoryDto expected = createCategoryDto();
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.getById(category.getId());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify getById() method does not work for a category with a non-existent id")
    public void getById_WithNonExistingCategoryId_ShouldThrowException() {
        category.setId(Long.MAX_VALUE);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(category.getId()));
        String actual = exception.getMessage();
        String expected = "Can't find a category with id " + category.getId();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify save() method works")
    public void save_ValidCreateCategoryRequestDto_ShouldReturnValidCategoryDto() {
        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
        CategoryDto expected = createCategoryDto();
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.save(requestDto);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify update() method works")
    public void update_ValidId_ShouldUpdateCategory() {
        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
        CategoryDto expected = createCategoryDto();
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.update(category.getId(), requestDto);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify update() method does not work for a category with a non-existent id")
    public void update_WithNonExistingCategoryId_ShouldThrowException() {
        category.setId(Long.MAX_VALUE);
        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(category.getId(), requestDto));
        String expected = "Can't update a category with id " + category.getId();
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify delete() method works")
    public void delete_ValidId_ShouldDeleteCategory() {
        categoryService.deleteById(category.getId());
        verify(categoryRepository, times(1)).deleteById(category.getId());
    }

    private CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto()
                .setName(category.getName())
                .setDescription(category.getDescription());
    }

    private CategoryDto createCategoryDto() {
        return new CategoryDto()
                .setId(category.getId())
                .setName(category.getName())
                .setDescription(category.getDescription());
    }
}
