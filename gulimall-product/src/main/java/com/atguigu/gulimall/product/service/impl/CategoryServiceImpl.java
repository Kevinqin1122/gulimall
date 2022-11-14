package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2.1）、找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
             categoryEntity.getParentCid() == 0
        ).map((menu)->{
            menu.setChildren(getChildrens(menu,entities));
            return menu;
        }).sorted((menu1,menu2)->{
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public List<CategoryEntity> listAndThreeTree() {
        //查询全部分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        //查询一级分类
        List<CategoryEntity> First_level_classification = categoryEntities.stream().filter(entity -> entity.getParentCid() == 0)
                .map((item) -> {
                    item.setChildren(getSuns(item, categoryEntities));
                    return item;
                }).sorted((item1, item2) -> {
                    return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
                }).collect(Collectors.toList());
        return First_level_classification;  //一级分类
    }

    @Override
    public List<CategoryEntity> listAndTrees() {
        //查询全部分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        //查询一级分类
        List<CategoryEntity> level1 = categoryEntities.stream().filter(entity -> entity.getParentCid() == 0)
                .map((item) -> {
                    item.setChildren(getSun(item, categoryEntities));
                    return item;
                }).sorted((item1, item2) -> {
                    return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
                }).collect(Collectors.toList());
        return level1;
    }

    @Override
    public List<CategoryEntity> getTree() {
        //查询全部分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        //查询一级分类
        List<CategoryEntity> level1 = categoryEntities.stream().filter(entity -> entity.getParentCid() == 0)
                .map((entity) -> {
                    entity.setChildren(getZi(entity, categoryEntities));
                    return entity;
                }).sorted((item1, item2) -> {
                    return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
                }).collect(Collectors.toList());
        return level1;
    }

    @Override
    public List<CategoryEntity> getThree() {
        //查询全部分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //查询一级分类
        List<CategoryEntity> level1 = entities.stream().filter(entity -> entity.getParentCid() == 0)
                .map((item) -> {
                    item.setChildren(getSons(item, entities));
                    return item;
                }).sorted((item1, item2) -> {
                    return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
                }).collect(Collectors.toList());
                    return level1;
    }

    @Override
    public List<CategoryEntity> queryListTree() {
        //查询所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //查询一级分类
        List<CategoryEntity> level1 = entities.stream().filter((entity -> entity.getParentCid() == 0))
                .map((item) -> {
                    item.setChildren(getSONs(item, entities));
                    return item;
                }).sorted((item1, item2) -> {
                    return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
                }).collect(Collectors.toList());
        return level1;
    }

    private List<CategoryEntity> getSONs(CategoryEntity item, List<CategoryEntity> entities) {
        List<CategoryEntity> level2 = entities.stream().filter(entity -> entity.getParentCid() == item.getParentCid())
                .map((entity -> {
                    entity.setChildren(getSONs(entity, entities));
                    return entity;
                })).sorted((item1, item2) -> {
                    return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
                }).collect(Collectors.toList());
        return level2;
    }

    private List<CategoryEntity> getSons(CategoryEntity item, List<CategoryEntity> entities) {
        List<CategoryEntity> level2 = entities.stream().filter(entity -> entity.getParentCid() == item.getParentCid())
                .map((enty) -> {
                    enty.setChildren(getSons(enty, entities));
                    return enty;
                }).sorted((item1, item2) -> {
                    return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getParentCid() == null ? 0 : item2.getSort());
                }).collect(Collectors.toList());
        return level2;
    }

    private List<CategoryEntity> getZi(CategoryEntity entity, List<CategoryEntity> categoryEntities) {
        //查询子分类
        List<CategoryEntity> leves = categoryEntities.stream().filter(item -> item.getParentCid() == entity.getParentCid())
                .map((num) -> {
                    num.setChildren(getZi(num, categoryEntities));
                    return num;
                }).sorted((item1, item2) -> {
                    return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
                }).collect(Collectors.toList());
        return leves;
    }

    private List<CategoryEntity> getSun(CategoryEntity item, List<CategoryEntity> categoryEntities) {
        List<CategoryEntity> suns = categoryEntities.stream().filter(entity -> entity.getParentCid() == item.getParentCid())
                .map((num) -> {
                    num.setChildren(getSun(num, categoryEntities));
                    return num;
                }).sorted((number1, number2) -> {
                    return (number1.getSort() == null ? 0 : number1.getSort()) - (number2.getSort() == null ? 0 : number2.getSort());
                }).collect(Collectors.toList());
        return suns;
    }


    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public void removeByNums(List<Long> asList) {
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    //批量删除
    @Override
    public void deleteByIds(Long[] catIds) {
        baseMapper.deleteBatchIds(Arrays.asList(catIds));
    }

    @Override
    public void removeByNumbers(List<CategoryEntity> asList) {
        baseMapper.deleteBatchIds(asList);
    }

    //[2,25,225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);


        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    @Override
    public List<CategoryEntity> getCateList() {

        List<CategoryEntity> list = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return list;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1.查出所有1级分类
        List<CategoryEntity> leve1Categorys = getCateList();

        //2.封装数据
        Map<String, List<Catelog2Vo>> parent_cid = leve1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            //2.封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //找当前的二级分类的三级分类封装成vo
                    List<CategoryEntity> leve3Catelog = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                    if (leve3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = leve3Catelog.stream().map(l3 -> {
                            //封装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(Collections.singletonList(collect));
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return parent_cid;
    }



    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parentCid) {
        List<CategoryEntity> categoryEntities = selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return categoryEntities;
        // return this.baseMapper.selectList(
        //         new QueryWrapper<CategoryEntity>().eq("parent_cid", parentCid));
    }

    //225,25,2
    private List<Long> findParentPath(Long catelogId,List<Long> paths){
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid()!=0){
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;

    }


    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all){

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> categoryEntity.getParentCid() == root.getCatId())
                .map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity,all));
            return categoryEntity;
        }).sorted((menu1,menu2)->{
            //2、菜单的排序
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

    //递归查询所有菜单的子菜单
    private List<CategoryEntity> getSuns(CategoryEntity item, List<CategoryEntity> categoryEntities) {
        List<CategoryEntity> suns = categoryEntities.stream().filter(entity -> entity.getParentCid() == item.getParentCid())
                .map(entity -> {
                    //继续递归找到子菜单
                    entity.setChildren(getSuns(entity, categoryEntities));
                    return entity;
                }).sorted((item1, item2) -> {
                    return (item1.getSort() == null ? 0 : item1.getSort()) - (item2.getSort() == null ? 0 : item2.getSort());
                }).collect(Collectors.toList());
        return suns;
    }

}