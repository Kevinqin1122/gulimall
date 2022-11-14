package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
//2级分类vo
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {
    private String CateLogId;   //  1级父分类id
    private List<Object> catalog3List;  //三级子分类
    private String id;
    private String name;

    //3级分类vo
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catelog3Vo{
        private String catalog2Id;  //父分类，2级分类id
        private String id;
        private String name;
    }
}
