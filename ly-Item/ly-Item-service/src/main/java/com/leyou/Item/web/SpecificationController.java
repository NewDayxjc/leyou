package com.leyou.Item.web;

import com.leyou.Item.pojo.SpecGroup;
import com.leyou.Item.pojo.SpecParam;
import com.leyou.Item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specService;

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(specService.queryGroupByCid(cid));
    }

    /**
     * 查询参数集合
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamList(
            @RequestParam(value = "gid",required = false) Long gid, //根据组查  //因为这里实际只需传一个参数
            @RequestParam(value = "cid",required = false) Long cid,  //根据商品分类id查
            @RequestParam(value = "searching",required = false) Boolean searching  //更具搜索条件查
    ){
        return  ResponseEntity.ok(specService.queryParamByList(gid,cid,searching));
    }

}
