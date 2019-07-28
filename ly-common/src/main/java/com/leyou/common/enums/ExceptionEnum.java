package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor   //获取空参构造  默认私有
@AllArgsConstructor   //获取全参构造
public enum ExceptionEnum {
    //如果枚举有多个对象，以逗号隔开，且只能放在枚举类最前面
    PRICE_CANNOT_BE_NULL(400,"价格不能为空"),
    CATEGORY_NOT_FOUND(404,"商品分类没查到"),
    BRAND_NOT_FOUND(404,"品牌不存在"),
    BRAND_SAVE_ERROR(500,"新增品牌失败"),
    UPDATE_FILE_ERROR(500,"文件上传失败"),
    INVALID_FILE_TYPE(400,"无效的文件类型"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组不存在"),
    SPEC_PARAM_NOT_FOUND(404,"商品规格参数不存在"),
    GOODS_NOT_FOUND(404,"商品不存在"),
    ;
    //枚举是只能有固定实体，可以做单例(只允许自己new)
//    private static final ExceptionEnum ff=new ExceptionEnum(1,"sjk");sjk
    //简写


    private int code;
    private String msg;
}
