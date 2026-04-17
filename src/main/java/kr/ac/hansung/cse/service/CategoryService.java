package kr.ac.hansung.cse.service;

import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.repository.CategoryRepository;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category createCategory(String name) {

        // 🔥 중복 검사
        categoryRepository.findByName(name)
                .ifPresent(c -> {
                    throw new DuplicateCategoryException(name);
                });

        return categoryRepository.save(new Category(name));
    }

    @Transactional
    public void deleteCategory(Long id) {

        long count = categoryRepository.countProductsByCategoryId(id);

        if (count > 0) {
            throw new IllegalStateException(
                    "상품 " + count + "개가 연결되어 있어 삭제할 수 없습니다."
            );
        }

        categoryRepository.delete(id);
    }
}