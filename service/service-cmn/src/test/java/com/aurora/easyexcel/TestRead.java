package com.aurora.easyexcel;

import com.alibaba.excel.EasyExcel;

public class TestRead {
    public static void main(String[] args) {
        //读取文件路径和文件名称
        String filename = "E:\\excel\\01.xlsx";

        //调用方法实现读取操作
        EasyExcel.read(filename,UserData.class,new ExcelListener()).sheet().doRead();
    }
}
