package com.leyou.Item.service;

import com.leyou.Item.mapper.CategoryMapper;
import com.leyou.Item.pojo.Category;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryListByPid(Long pid) {
        Category t=new Category();
        //自己new一个Category对象，将这个非空条件进行查询
        //查询条件，mapper会把对象中的非空属性作为查询条件
        t.setParentId(pid);
        List<Category> list = categoryMapper.select(t);
        //判断结果 查询失败放回404
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return  list;
    }
}
