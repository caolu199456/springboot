package com.example.cms.controller.sys;


import com.alibaba.dubbo.config.annotation.Reference;
import com.example.cms.controller.BaseController;
import com.example.user.module.sys.dto.SysRoleDto;
import com.example.user.module.sys.dto.SysUserDto;
import com.example.user.module.sys.service.SysRoleService;
import com.example.util.annotation.RequirePermissions;
import com.example.util.common.Page;
import com.example.util.common.Request;
import com.example.util.common.Response;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author caolu
 * @date 2019-04-01 22:52:09
 */
@RestController
@RequestMapping("sysRole")
public class SysRoleController extends BaseController {

    @Reference
    private SysRoleService sysRoleService;

    /**
	 * 列表
	 */
    @GetMapping("list")
    @RequirePermissions(values ="sysRole:list")
    public Response<Page> list(@RequestParam Map<String, Object> params){
        //查询列表数据
        Request request = Request.configParams(params);

        List<SysRoleDto> list = sysRoleService.queryList(params);
        int total = sysRoleService.queryTotal(params);

        Page<SysRoleDto> page = new Page<SysRoleDto>(request.getPageNo(), request.getPageNo(), total, list);

        return Response.ok().wrap(page);
    }


    /**
     * 列表
     */
    @GetMapping("queryAllRoles")
    public Response<List<SysRoleDto>> queryAllRoles(){
        return Response.ok().wrap(sysRoleService.queryAllRoles());
    }

    /**
     * 信息
     */
    @RequestMapping("info/{id}")
    @RequirePermissions(values ="sysRole:info")
    public Response info(@PathVariable("id") Long id){

        SysRoleDto sysRoleDto = sysRoleService.selectById(id);

        return Response.ok().wrap(sysRoleDto);
    }

    /**
     * 保存
     */
    @RequestMapping("save")
    @RequirePermissions(values ="sysRole:edit")
    public Response save(@RequestBody SysRoleDto sysRoleDto){

        fillSaveCommonData(sysRoleDto);

        sysRoleService.save(sysRoleDto);

        return Response.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("update")
    @RequirePermissions(values ="sysRole:edit")
    public Response update(@RequestBody SysRoleDto sysRoleDto){

        fillEditCommonData(sysRoleDto);

        sysRoleService.updateById(sysRoleDto);

        return Response.ok();
    }

    /**
	 * 删除
	 */
    @RequestMapping("delete")
    @RequirePermissions(values ="sysRole:delete")
    public Response delete(@RequestBody Long[] ids){
        for (Long id : ids) {
            SysRoleDto sysRoleDto = sysRoleService.selectById(id);
            if (Objects.equals(sysRoleDto.getRoleName(), "Super")) {
                return Response.error("超级管理员角色不能被删除");
            }
        }
        sysRoleService.deleteBatch(ids);
        return Response.ok();
    }
}
