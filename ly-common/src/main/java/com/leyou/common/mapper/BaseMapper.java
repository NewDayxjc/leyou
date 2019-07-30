package com.leyou.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;//不支持主键策略，插入前需要设置好主键的值
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper; //本方法中id值只能定义为"id",才能被识别
@RegisterMapper    //tkMapper底层实现了这个注解，自定义也需要加上这个注解
public interface BaseMapper<T> extends Mapper<T>, IdListMapper<T,Long>, InsertListMapper<T> {

}
