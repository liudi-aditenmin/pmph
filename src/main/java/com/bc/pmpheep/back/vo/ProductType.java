package com.bc.pmpheep.back.vo;

import com.bc.pmpheep.annotation.ExcelHeader;

import java.io.Serializable;
import java.util.List;

/**
 * 临床决策-内容分类
 */
public class ProductType implements Serializable{
    private Long id	;
    private Long parent_id ;//备用字段，当前要求学科分类无层级关系
    private String type_name; //50长度 名称
    private int typeType; // 1.学科分类 2.内容分类

    private List<ProductType> childType; //子分类列表
    private String fullNamePath; //从顶级分类名称到自身分类名称的全名称路径 分隔符为 '/'

    public ProductType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getTypeType() {
        return typeType;
    }

    public void setTypeType(int typeType) {
        this.typeType = typeType;
    }

    public List<ProductType> getChildType() {
        return childType;
    }

    public void setChildType(List<ProductType> childType) {
        this.childType = childType;
    }

    public String getFullNamePath() {
        return fullNamePath;
    }

    public void setFullNamePath(String fullNamePath) {
        this.fullNamePath = fullNamePath;
    }
}
