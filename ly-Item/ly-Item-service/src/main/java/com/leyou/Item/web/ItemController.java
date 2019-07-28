package com.leyou.Item.web;

import com.leyou.Item.pojo.Item;
import com.leyou.Item.service.ItemService;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("item")
public class ItemController {
    @Autowired
    private ItemService itemService;
    @PostMapping

    public ResponseEntity<Item> saveItem(Item item){
        //校验价格
        if(item.getPrice()==null){
            //rest风格编写
            //坏的请求
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            //抛出异常，此时是由SpringMvc在处理，返回500
            //但异常不应该有SpringMvc处理，要全部由自己处理，即统一异常处理
            //对web层进行拦截，(Aop思想)
            /*
             */
            throw new LyException(ExceptionEnum.PRICE_CANNOT_BE_NULL);

        }
        item = itemService.saveItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }
}
