package com.leyou.Item.web;

import com.leyou.Item.pojo.Category;
import com.leyou.Item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
//先分析思路，通过页面请求，从业务需求出发，从而快速写出代码实现
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam("pid") Long pid ){
        //将categoryService.queryCategoryListByPid(pid)查询的结果放到ResponseEntity，因此要返回
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }

}
