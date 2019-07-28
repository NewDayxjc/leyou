package com.leyou.Item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
@Data
@Table(name = "tb_spu")
public class SpuVo {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String title;// 标题
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    private Boolean valid;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间
    //可以添加数据，不过这样会与数据库数据字段不符
    @Transient     //不提交到数据库中
    private String cname;
    @Transient
    private String bname;

    //不过这样业务上会变麻烦
}
