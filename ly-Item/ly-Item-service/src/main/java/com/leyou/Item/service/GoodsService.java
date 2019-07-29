package com.leyou.Item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.Item.mapper.*;
import com.leyou.Item.pojo.*;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import net.sf.jsqlparser.statement.select.ExceptOp;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
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
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

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
            //第一种方法reduce((s,s2)->s+s2);
            spu.setCname(StringUtils.join(names,"/"));//显示成这种形式：手机/手机通讯/手机


            //处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }
    //新增spu
    @Transactional
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setId(null);
        spu.setSaleable(true);
        spu.setValid(false);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        int count = spuMapper.insert(spu);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //新增spuDetail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        count = spuDetailMapper.insert(spuDetail);
        if (count != 1) {
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //新增sku和库存
        saveSkuAndStock(spu);
    }
        //新增sku和库存
        private void saveSkuAndStock(Spu spu) {

        //定义库存集合
        List<Stock> stockList=new ArrayList<>();
        //新增sku
        List<Sku> skus=spu.getSkus();
        for(Sku sku:skus){
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            int count = skuMapper.insert(sku);
            if(count!=1){
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            //新增库存
            Stock stock=new Stock();
            stock.setSkuId(sku.getId());
            //设置库存数量
            stock.setStock(sku.getStock());
            stockList.add(stock);
        }
        //批量新增库存
        stockMapper.insertList(stockList);
    }

    public SpuDetail queryDetailById(@PathVariable("id") Long id){
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        if(spuDetail==null){
            throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }

    public List<Sku> querySkuBySpuId(Long spuid) {
        Sku sku = new Sku();
        sku.setSpuId(spuid);
        List<Sku> skuList = skuMapper.select(sku);
        if(CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        //查询库存
//        for (Sku s:skuList) {
//            Stock stock=stockMapper.selectByPrimaryKey(s.getId());
//            if(stock==null){
//                throw new LyException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
//            }
//            s.setStock(stock.getStock());
//        }
        //查询库存
        List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(stockList)){
            throw new LyException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
        }
        //我们把stock变成一个map，其key是：sku的id，值是库存的值
        Map<Long,Integer> stockMap=stockList.stream().collect(Collectors.toMap(Stock::getSkuId,Stock::getStock));
        skuList.forEach(s->s.setStock(stockMap.get(s.getId())));
        return skuList;

    }


    @Transactional
    public void updateGoods(Spu spu) {
        if(spu.getId()==null){
            throw new LyException(ExceptionEnum.GOODS_ID_NOT_BE_NULL);
        }
        //删除sku和stock
        Sku sku=new Sku();
        sku.setSpuId(spu.getId());
        //查询sku
        List<Sku> skuList = skuMapper.select(sku);
        if(!CollectionUtils.isEmpty(skuList)){
            //删除sku
            skuMapper.delete(sku);
            //删除stock
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            //批量删除
            stockMapper.deleteByIdList(ids);
            //修改spu
            spu.setValid(null);
            spu.setSaleable(null);
            spu.setLastUpdateTime(null);
            spu.setCreateTime(null);
            int count = spuMapper.updateByPrimaryKeySelective(spu);
            if(count!=1){
                throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
            }
            //修改detail
            count=spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
            if(count!=1){
                throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
            }


            //新增sku和stock
            saveSkuAndStock(spu);
        }
//        spuMapper.updateByPrimaryKey(spu);
    }


}
