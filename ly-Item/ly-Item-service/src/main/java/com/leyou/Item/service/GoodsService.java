package com.leyou.Item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.Item.mapper.CategoryMapper;
import com.leyou.Item.mapper.SpuDetailMapper;
import com.leyou.Item.mapper.SpuMapper;
import com.leyou.Item.pojo.Category;
import com.leyou.Item.pojo.Spu;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private  BrandService brandService;
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private CategoryService categoryService;

    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, String saleable, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria=example.createCriteria();
        //搜索字段过滤
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }

        //上下架过滤
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");
        //查询
        List<Spu> spus = spuMapper.selectByExample(example);
        //判断
        if(CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //解析分类和品牌的名称,单独封装出去，使逻辑更加清晰
        loadCategoryAndBrandName(spus);

        //解析分页结果
        PageInfo<Spu> info=new PageInfo<>(spus);
        return new PageResult<>(info.getTotal(),spus);
    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //处理分类名称
            List<String> names = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            //名称流
            //一种方法reduce((s,s2)->s+s2);
            spu.setCname(StringUtils.join(names,"/"));//显示成这种形式：手机/手机通讯/手机


            //处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }
}
