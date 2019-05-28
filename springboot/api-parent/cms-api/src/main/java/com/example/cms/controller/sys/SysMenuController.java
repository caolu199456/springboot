package com.example.cms.controller.sys;


import com.alibaba.dubbo.config.annotation.Reference;
import com.example.cms.controller.BaseController;
import com.example.user.module.sys.dto.SysMenuDto;
import com.example.user.module.sys.service.SysMenuService;
import com.example.util.annotation.RequirePermissions;
import com.example.util.common.Response;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author caolu
 * @date 2019-04-02 09:42:45
 */
@RestController
@RequestMapping("sysMenu")
public class SysMenuController extends BaseController {

    @Reference
    private SysMenuService sysMenuService;

    /**
	 * 菜单树操作
	 */
    @GetMapping("list")
    @RequirePermissions(values ="sysMenu:list")
    public Response<List<SysMenuDto>> list(){

        List<SysMenuDto> list = sysMenuService.queryList();

        return Response.ok().wrap(list);
    }

    /**
     * 得到所有启用的菜单
     */
    @GetMapping("queryCanUsefulMenus")
    @RequirePermissions(values ="sysMenu:list")
    public Response<List<SysMenuDto>> queryCanUsefulMenus(){

        List<SysMenuDto> list = sysMenuService.queryCanUsefulMenus();

        return Response.ok().wrap(list);
    }


    /**
     * 信息
     */
    @GetMapping("info/{id}")
    @RequirePermissions(values ="sysMenu:info")
    public Response info(@PathVariable("id") Long id){

        SysMenuDto sysMenuDto = sysMenuService.selectById(id);

        return Response.ok().wrap(sysMenuDto);
    }
    /**
     * 保存
     */
    @PostMapping("save")
    @RequirePermissions(values ="sysMenu:edit")
    public Response save(@RequestBody SysMenuDto sysMenuDto){

        fillSaveCommonData(sysMenuDto);

        sysMenuService.save(sysMenuDto);

        return Response.ok();
    }

    /**
     * 修改
     */
    @PostMapping("update")
    @RequirePermissions(values ="sysMenu:edit")
    public Response update(@RequestBody SysMenuDto sysMenuDto){

        fillEditCommonData(sysMenuDto);

        sysMenuService.updateById(sysMenuDto);

        return Response.ok();
    }

    /**
	 * 删除
	 */
    @PostMapping("delete")
    @RequirePermissions(values ="sysMenu:delete")
    public Response delete(@RequestBody Long[] ids){
        sysMenuService.deleteBatch(ids);
        return Response.ok();
    }
}
