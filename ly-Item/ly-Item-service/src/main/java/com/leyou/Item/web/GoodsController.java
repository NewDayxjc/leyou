package com.leyou.Item.web;

import com.leyou.Item.mapper.SpuDetailMapper;
import com.leyou.Item.pojo.Sku;
import com.leyou.Item.pojo.Spu;
import com.leyou.Item.pojo.SpuDetail;
import com.leyou.Item.service.GoodsService;
import com.leyou.common.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    /**
     * 分页查询SPU
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable",required = false) String saleable,
            @RequestParam(value = "key",required = false) String key
    ){
        return  ResponseEntity.ok(goodsService.querySpuByPage(page,rows,saleable,key));
    }

    /**
     * 商品新增
     * @param spu
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){  //spu是Json数据所以需要加上@RequestBody
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("goods")
    public ResponseEntity<Void>  updateGoods(@RequestBody Spu spu){
        goodsService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据spuId查询商品详情
     * @param id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail>  QueryDetailById(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.queryDetailById(id));
    }

    /**
     *
     * @param spuid
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>>  QuerySkuBySouId(@RequestParam("id") Long spuid){
        return ResponseEntity.ok(goodsService.querySkuBySpuId(spuid));
    }
}
