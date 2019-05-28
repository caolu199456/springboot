package com.example.cms.controller.sys;


import com.alibaba.dubbo.config.annotation.Reference;
import com.example.cms.controller.BaseController;
import com.example.user.module.sys.dto.SysUserDto;
import com.example.user.module.sys.service.SysUserService;
import com.example.util.annotation.RequirePermissions;
import com.example.util.common.Page;
import com.example.util.common.Request;
import com.example.util.common.Response;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 *
 * @author caolu
 * @date 2019-04-01 13:06:24
 */
@RestController
@RequestMapping("sysUser")
public class SysUserController extends BaseController {

    @Reference
    private SysUserService sysUserService;

    /**
	 * 列表
	 */
    @GetMapping("list")
    @RequirePermissions(values ="sysUser:list")
    public Response<Page> list(@RequestParam Map<String, Object> params){
        //查询列表数据
        Request request = Request.configParams(params);

        List<SysUserDto> list = sysUserService.queryList(params);
        int total  = sysUserService.queryTotal(params);


        Page<SysUserDto> page = new Page<SysUserDto>(request.getPageNo(), request.getPageNo(), total, list);

        return Response.ok().wrap(page);
    }


    /**
     * 信息
     */
    @GetMapping("info/{id}")
    @RequirePermissions(values ="sysUser:info")
    public Response info(@PathVariable("id") Long id){
        SysUserDto sysUserDto = sysUserService.selectById(id);

        return Response.ok().wrap(sysUserDto);
    }

    /**
     * 保存
     */
    @PostMapping("save")
    @RequirePermissions(values ="sysUser:edit")
    public Response save(@RequestBody SysUserDto sysUserDto){
        fillSaveCommonData(sysUserDto);
        sysUserService.save(sysUserDto);
        return Response.ok();
    }

    /**
     * 修改
     */
    @PostMapping("update")
    @RequirePermissions(values ="sysUser:edit")
    public Response update(@RequestBody SysUserDto sysUserDto){

        fillEditCommonData(sysUserDto);

        sysUserService.updateById(sysUserDto);

        return Response.ok();
    }
    /**
     * 修改
     */
    @PostMapping("resetPwd")
    @RequirePermissions(values ="sysUser:edit")
    public Response resetPwd(@RequestBody Long[] ids){
        for (Long id : ids) {
            sysUserService.updatePassword(getUserId(), "123456");
        }
        return Response.ok();
    }
    /**
	 * 删除
	 */
    @PostMapping("delete")
    @RequirePermissions(values ="sysUser:delete")
    public Response delete(@RequestBody Long[] ids){
        for (Long id : ids) {
            SysUserDto sysUserDto = sysUserService.selectById(id);
            if ("admin".equalsIgnoreCase(sysUserDto.getAccount())) {
                return Response.error("超级管理员不能被删除");
            }
        }
        sysUserService.deleteBatch(ids);
        return Response.ok();
    }
}
