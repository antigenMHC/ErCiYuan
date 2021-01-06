package cn.antigenmhc.otaku.service.manager.controller.admin;


import cn.antigenmhc.otaku.common.base.result.Result;
import cn.antigenmhc.otaku.common.base.result.ResultCodeEnum;
import cn.antigenmhc.otaku.service.manager.pojo.Admin;
import cn.antigenmhc.otaku.service.manager.pojo.vo.AdminQueryVo;
import cn.antigenmhc.otaku.service.manager.service.AdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 管理员 前端控制器
 * </p>
 *
 * @author antigenmhc
 * @since 2020-11-30
 */
@Api("管理员管理")
@RestController
@RequestMapping("/admin/manager/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @ApiOperation("管理员列表")
    @GetMapping("getall")
    public Result getAll(){
        return Result.ok().setData("items", adminService.list());
    }

    @ApiOperation("删除管理员")
    @DeleteMapping("delete/{id}")
    public Result deleteOneById(@PathVariable("id") String id){
        boolean hashRemove = adminService.removeById(id);
        return hashRemove ? Result.ok().setMessage("删除成功") : Result.error().setMessage("删除失败");
    }

    @ApiOperation("管理员分页")
    @GetMapping("list/{page}/{limit}")
    public Result listPage(@ApiParam(value = "当前页码", required = true) @PathVariable("page") Long page,
                           @ApiParam(value = "每页记录数", required = true) @PathVariable("limit") Long limit,
                           @ApiParam("查询条件") AdminQueryVo queryVo){
        Page<Admin> adminPage = new Page<>(page, limit);
        IPage<Admin> pageModel = adminService.selectPageByQuery(adminPage, queryVo);
        List<Admin> records = pageModel.getRecords();
        long total = pageModel.getTotal();

        return Result.ok().setData("total", total).setData("rows", records);
    }

    @ApiOperation("新增管理员")
    @PostMapping("add")
    public Result addAdmin(@RequestBody Admin admin){
        adminService.save(admin);
        return Result.ok().setMessage("添加成功");
    }

    @ApiOperation("获得指定管理员")
    @GetMapping("get/{id}")
    public Result getOneAdmin(@PathVariable("id") String id){
        Admin admin = adminService.getById(id);
        if (admin == null){
            return Result.error().setMessage("获取失败，管理员不存在");
        }
        return Result.ok().setData("item", admin);
    }

    @ApiOperation("更新管理员")
    @PutMapping("update")
    public Result updateById(@RequestBody Admin admin){
        boolean isUpdateSuccess = adminService.updateById(admin);
        if(!isUpdateSuccess){
            return Result.error().setMessage("更新失败,管理员不存在");
        }
        return Result.ok().setMessage("更新成功");
    }

    @ApiOperation("根据 id 列表批量删除")
    @DeleteMapping("batch-remove")
    public Result deleteAdminsByIdList(
            @ApiParam(value = "id列表")
            @RequestBody List<String> ids){
        System.out.println(ids);
        boolean isDeleteSuccess = adminService.removeByIds(ids);

        if(isDeleteSuccess) {
            return Result.ok().setMessage("批量删除成功");
        }else {
            return Result.error().setMessage("数据不存在");
        }
    }
}
