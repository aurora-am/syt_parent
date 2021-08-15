package com.aurora.yygh.hosp.controller;

import com.aurora.yygh.common.exception.YyghException;
import com.aurora.yygh.common.result.Result;
import com.aurora.yygh.common.utils.MD5;
import com.aurora.yygh.model.hosp.Hospital;
import com.aurora.yygh.model.hosp.HospitalSet;
import com.aurora.yygh.hosp.service.HospitalSetService;
import com.aurora.yygh.vo.hosp.HospitalQueryVo;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin  //允许跨域访问
public class HospitalSetController {

    // http://localhost:8201/admin/hosp/hospitalSet/findAll

    @Autowired  //注入service
    private HospitalSetService hospitalSetService;

    //1 查询医院设置表所有信息
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospitalSet(){
        //调用service的方法
        List<HospitalSet> list = hospitalSetService.list();  //mybatis-plus在mapper和service中都做了封装 所以直接调list()就行
        return Result.ok(list);
    }

    //2 逻辑删除医院设置
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean flag = hospitalSetService.removeById(id);
        if(flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
/*    实体类中属性加上了@TableLogic,
     @TableLogic //@TableLogic注解表示逻辑删除
    private Integer isDeleted;
   调用BaseMapper的deleteById(id)或者调用IService的removeById(id)*/

    //3 条件查询带分页
    @ApiOperation(value = "条件查询带分页")
    @PostMapping("findPageHospSet/{current}/{limit}")    //current 当前页 limit 每页的记录数
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalQueryVo hospitalSetQueryVo){ //条件对象
                                // @RequestBody 通过json数据传参数 然后把这些数据值传到HospitalQueryVo对象中去
                                // 然后required = false 表示这个对象值可以为空  但是用了RequestBody就不能用GetMapping了
                                //因为get 方式得不到值 post 才能得到@RequestBody传回来的值
        //创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current,limit);
        //构建条件  QueryWrapper 是mybatis 中的条件构造器
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();//医院名称
        String hoscode = hospitalSetQueryVo.getHoscode();//医院编号
        if(!StringUtils.isEmpty(hosname)) {     //对条件值进行判断 因为两个条件的满足情况不同
            wrapper.like("hosname",hospitalSetQueryVo.getHosname());    //模糊查询医院的名称
        }
        if(!StringUtils.isEmpty(hoscode)) {
            wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());  //eq查询医院的编号
        }
        //调用方法实现分页查询
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper); //一个是page对象 一个是条件 条件需要构建一下👆

        return Result.ok(pageHospitalSet);  //返回结果
    }

    //4 添加医院设置
    @ApiOperation(value = "添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {   //@RequestBody 没有写(required = false) 是因为这个值必须要有
        //设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);
        //签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        //调用service
        boolean save = hospitalSetService.save(hospitalSet);
        if(save) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //5 根据id获取医院设置
    @ApiOperation(value = "根据id获取医院设置")
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id) {
//        try{
//            //模拟异常
//            int a = 1/0;
//        }catch (Exception e){
//            throw new YyghException("失败",201);
//        }

        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    //6 修改医院设置
    @ApiOperation(value = "修改医院设置")
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if(flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    //7 批量删除医院设置
    @ApiOperation(value = "批量删除医院设置")
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList) {
        hospitalSetService.removeByIds(idList); //removeByIds 里面是一个集合
        return Result.ok();
    }

    //8 医院设置锁定和解锁
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status) {
        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //设置状态
        hospitalSet.setStatus(status);
        //调用方法
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    //9 发送签名秘钥
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TODO 发送短信
        return Result.ok();
    }
}