package com.leyou.Item.mapper;

import com.leyou.Item.pojo.Brand;
import com.leyou.Item.pojo.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    //将sql语句写到注解上
    @Insert("INSERT INTO tb_category_brand (category_id,brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid,@Param("bid") Long bid);

    @Insert("SELECT b.* FROM tb_brand b inner JOIN tb_category_brand cb ON b.id = cb.brand_id WHERE cb.category_id=#{cid}")
    List<Brand> queryBrandByCategoryId(@Param("cid") Long cid);
}
