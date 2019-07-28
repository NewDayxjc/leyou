package com.leyou.Item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.Item.mapper.BrandMapper;
import com.leyou.Item.pojo.Brand;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.awt.image.PixelGrabber;
import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;
    public PageResult<Brand> queryBrandByPage(Integer page,Integer rows,String sortBy,Boolean desc,String key){
        //分页
        /*采用分页助手，利用Mybatis拦截器进行拦截对接下来要执行的sql语句进行拦截，自动在其后面拼上limit
        PageHelper.startPage(当前页,每页显示条数);
        select * form tb_brand where "name" like  "%x%" or letter=='x' order by id desc;*/
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Brand.class);//利用反射获取brand定义的表的名字，属性，这样其关系sql知道去哪查看
        if(StringUtils.isNotBlank(key)){
            //过滤条件
            //createCriteria:创建条件,sql语句
            example.createCriteria().orLike("name","%"+key+"%")
                    .orEqualTo("letter",key.toUpperCase());

        }
        //排序
        if(StringUtils.isNotBlank(sortBy)){
            String orderByClause=sortBy+" "+(desc ? " DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }
        //查询
        List<Brand> list= brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //解析分页结果
        PageInfo<Brand> info = new PageInfo<>(list);
        //获取总条数
        //return new PageResult<>(总条数,当前查询的数据) ;
        return new PageResult<>(info.getTotal(),list) ;
    }
    //添加事务
    @Transactional
    public void saveBrand(Brand brand,List<Long> cids){
        //新增品牌
        brand.setId(null);
        int count = brandMapper.insert(brand);
//        brandMapper.insertSelective(),有选择性的新增，它会判断数据是否有空的字段，若有则对相应字段不进行新增
        if(count!=1){
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        //新增中间表
        for (Long cid:cids){
            count=brandMapper.insertCategoryBrand(cid,brand.getId());
                if(count!=1){
                    throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
                }
        }

    }
    public Brand queryById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }


    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list=brandMapper.queryBrandByCategoryId(cid);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }
}
