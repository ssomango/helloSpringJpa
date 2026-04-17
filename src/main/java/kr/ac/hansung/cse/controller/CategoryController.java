package kr.ac.hansung.cse.controller;

import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.ac.hansung.cse.service.CategoryService;
import kr.ac.hansung.cse.model.CategoryForm;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 목록
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categoryList";
    }

    // 등록 폼
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        return "categoryForm";
    }

    // 등록 처리
    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute CategoryForm categoryForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) return "categoryForm";

        try {
            categoryService.createCategory(categoryForm.getName());
            redirectAttributes.addFlashAttribute("successMessage", "등록 완료");
        } catch (DuplicateCategoryException e) {
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
            return "categoryForm";
        }

        return "redirect:/categories";
    }

    // 삭제
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "삭제 완료");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/categories";
    }
}