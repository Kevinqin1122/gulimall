package com.atguigu.gulimall.product.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.common.utils.R;


/**
 * 商品三级分类
 *
 * @author leifengyang
 * @email leifengyang@gmail.com
 * @date 2019-10-01 22:50:32
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     */
//    @RequestMapping("/list/tree")
//    public R list(){
//
////        List<CategoryEntity> entities = categoryService.listWithTree();
////        List<CategoryEntity> entities = categoryService.listAndThreeTree();
////        List<CategoryEntity> entities = categoryService.listAndTrees();
////        List<CategoryEntity> entities = categoryService.getTree();
//
//        List<CategoryEntity> entities = categoryService.getThree();
//        return R.ok().put("data", entities);
//    }

    /**
     * 查询所有分类及子分类，以树形结构组装起来
     * @return
     */
    @GetMapping("/list/there")
    public R list(){
        List<CategoryEntity> entities = categoryService.queryListTree();
        return R.ok().put("date",entities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    //批量修改
    @PostMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    //批量删除
    @PostMapping("/update/sort2")
    public R updateByIds(@RequestBody CategoryEntity[] category){
        categoryService.removeByNumbers(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    //@RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateCascade(category);
        return R.ok();
    }


    /**
     * 删除
     * @RequestBody:获取请求体，必须发送POST请求
     * SpringMVC自动将请求体的数据（json），转为对应的对象
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){


		//categoryService.removeByIds(Arrays.asList(catIds));

//        categoryService.removeMenuByIds(Arrays.asList(catIds));
//        categoryService.removeByNums(Arrays.asList(catIds));
        categoryService.deleteByIds(catIds);
        return R.ok();
    }

}
