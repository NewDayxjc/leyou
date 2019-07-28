package com.leyou.Item.mapper;

import com.leyou.Item.pojo.Category;
<<<<<<< HEAD
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;
//IdListMapper<实体类,实体类主键>IdListMapper<Category,Long>
public interface CategoryMapper extends Mapper<Category>, IdListMapper<Category,Long> {

=======
import tk.mybatis.mapper.common.Mapper;

public interface CategoryMapper extends Mapper<Category> {
>>>>>>> 9c700ece80ce94ad4812eaf1a4d21747bedf3887
}
