package com.aurora.yygh.cmn.controller;

import com.aurora.yygh.cmn.service.DictService;
import com.aurora.yygh.common.result.Result;
import com.aurora.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin  //跨域
public class DictController {

    @Autowired  //注入service
    private DictService dictService;

    //根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }

    //导出数据字典接口
    @ApiOperation(value="导出")
    @GetMapping("exportData")
    public void exportDict(HttpServletResponse response){
        dictService.exportDictData(response);
    }

    //导入数据字典数据
    @ApiOperation(value = "导入")
    @PostMapping("importData")
    public Result importDict(MultipartFile file){   //MultipartFile 得到上传的文件
        dictService.importDictData(file);
        return Result.ok();
    }
}