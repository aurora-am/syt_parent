package com.aurora.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data   //生成get set方法
public class UserData {
    //在对应属性上面添加注解，设置表头内容

    /*@ExcelProperty("用户编号")  //Excel表格第一行表头显示的名称
    private int uid;

    @ExcelProperty("用户名称")
    private String username;*/   //写操作数据的写法

    @ExcelProperty(value = "用户编号",index = 0)  //读操作要确定每一列对应的什么数据
    private int uid;

    @ExcelProperty(value = "用户名称",index = 1)
    private String username;
}
