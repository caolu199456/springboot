package com.example.cms.controller.sys;


import com.alibaba.dubbo.config.annotation.Reference;
import com.example.user.module.sys.dto.SysUserDto;
import com.example.user.module.sys.service.SysConfigService;
import com.example.util.annotation.RequirePermissions;
import com.example.cms.controller.BaseController;
import com.example.user.module.sys.dto.SysConfigDto;
import com.example.user.module.sys.service.SysConfigService;
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
 * @date 2019-04-18 09:43:48
 */
@RestController
@RequestMapping("sysConfig")
public class SysConfigController extends BaseController {

    @Reference
    private SysConfigService sysConfigService;

    /**
	 * 列表
	 */
    @GetMapping("list")
    @RequirePermissions(values ="sysConfig:list")
    public Response<Page<SysConfigDto>> list(@RequestParam Map<String, Object> params){
        //查询列表数据
        Request request = Request.configParams(params);

        List<SysConfigDto> list = sysConfigService.queryList(params);
        int total = sysConfigService.queryTotal(params);

        Page<SysConfigDto> page = new Page<SysConfigDto>(request.getPageNo(), request.getPageNo(), total, list);

        return Response.ok().wrap(page);
    }


    /**
     * 信息
     */
    @GetMapping("info/{id}")
    @RequirePermissions(values ="sysConfig:info")
    public Response<SysConfigDto> info(@PathVariable("id") Long id){

        SysConfigDto sysConfigDto = sysConfigService.selectById(id);

        return Response.ok().wrap(sysConfigDto);
    }

    /**
     * 保存
     */
    @PostMapping("save")
    @RequirePermissions(values ="sysConfig:edit")
    public Response save(@RequestBody SysConfigDto sysConfigDto){

        //填充基础数据
        fillSaveCommonData(sysConfigDto);

        sysConfigService.save(sysConfigDto);

        return Response.ok();
    }

    /**
     * 修改
     */
    @PostMapping("update")
    @RequirePermissions(values ="sysConfig:edit")
    public Response update(@RequestBody SysConfigDto sysConfigDto){

        //填充基础数据
        fillEditCommonData(sysConfigDto);

        sysConfigService.updateById(sysConfigDto);

        return Response.ok();
    }

    /**
	 * 删除
	 */
    @PostMapping("delete")
    @RequirePermissions(values ="sysConfig:delete")
    public Response delete(@RequestBody Long[] ids){

        sysConfigService.deleteBatch(ids);

        return Response.ok();
    }
}
