package edu.uni.labManagement.controller;

import com.github.pagehelper.PageInfo;
import edu.uni.bean.Result;
import edu.uni.bean.ResultType;
import edu.uni.labManagement.service.DeviceMoveInService;
import edu.uni.utils.RedisCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(description = "设备管理与实验室管理模块 - 设备移入")
@Controller
@RequestMapping("json/labManagement")
public class DeviceMoveInController {
    @Resource
    private DeviceMoveInService deviceMoveInService;
    @Resource
    private RedisCache cache;

    /**
     * 内部类，专门用来管理每个方法所对应缓存的名称。
     */
    private static class CacheNameHelper{
        private static final String base = "lm_deviceMoveIn_*";
        // lm_deviceMoveIn_{设备移入记录id}
        private static final String Receive_CacheNamePrefix = "lm_deviceMoveIn_";
        // lm_deviceMoveIns_list_{页码}
        private static final String List_CacheNamePrefix = "lm_deviceMoveIn_list_";
    }

    /**
     * 新增设备移入记录
     * @param json 请求参数
     * @return Result
     */
    @ApiOperation(value="新增设备移入记录", notes = "")
    @ApiImplicitParam(name= "json", value = "请求参数", required = true, dataType = "Map")
    @PostMapping("deviceMoveIn")
    @ResponseBody
    public Result create(@RequestBody(required = false) Map<String,Object> json){
        if(json != null){
            boolean success = deviceMoveInService.insert(json);
            if(success){
                cache.deleteByPaterm(CacheNameHelper.base);
                return Result.build(ResultType.Success);
            }else{
                return Result.build(ResultType.Failed);
            }
        }
        return Result.build(ResultType.ParamError);
    }

    /**
     * 删除设备移入记录
     * @param id 主键
     * @return Result
     */
    @ApiOperation(value = "删除设备移入记录", notes = "")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "Long", paramType = "path")
    @DeleteMapping("deviceMoveIn/{id}")
    @ResponseBody
    public Result destroy(@PathVariable Long id){
        boolean success = deviceMoveInService.delete(id);
        if(success){
            cache.deleteByPaterm(CacheNameHelper.base);
            return Result.build(ResultType.Success);
        }else{
            return Result.build(ResultType.Failed);
        }
    }

    /**
     * 修改设备移入记录
     * @param json 请求参数
     * @return Result
     */
    @ApiOperation(value="修改设备移入记录")
    @ApiImplicitParam(name="json", value = "请求参数", required = true, dataType = "Map")
    @PutMapping("deviceMoveIn")
    @ResponseBody
    public Result update(@RequestBody(required = false) Map<String,Object> json){
        if(json != null && json.get("id") != null){
            boolean success = deviceMoveInService.update(json);
            if(success){
                cache.deleteByPaterm(CacheNameHelper.base);
                return Result.build(ResultType.Success);
            }else{
                return Result.build(ResultType.Failed);
            }
        }
        return Result.build(ResultType.ParamError);
    }

    /**
     * 查询单条设备移入记录
     * @param id 主键
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "查询单条设备移入记录", notes = "")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "Long", paramType = "path")
    @GetMapping("deviceMoveIn/{id}")
    public void receive(@PathVariable Long id,
                        HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        String cacheName = CacheNameHelper.Receive_CacheNamePrefix + id;
        String json = cache.get(cacheName);
        if(json == null){
            Map<String,Object> res = deviceMoveInService.select(id);
            json = Result.build(ResultType.Success).appendData("res", res).convertIntoJSON();
            if(res != null){
                cache.set(cacheName, json);
            }
        }
        response.getWriter().write(json);
    }

    /**
     * 分页查询所有设备移入记录
     * @param pageNum 页码
     * @param response
     * @throws IOException
     */
    @ApiOperation(value = "分页查询所有设备移入记录", notes = "")
    @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "Integer", paramType = "path")
    @GetMapping(value = "/deviceMoveIns/list/{pageNum}")
    public void list(
            @PathVariable Integer pageNum ,
            HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        String cacheName = CacheNameHelper.List_CacheNamePrefix + pageNum;
        String json = cache.get(cacheName);
        if(json == null){
            PageInfo<Map<String,Object>> pageInfo = deviceMoveInService.selectPage(pageNum);
            json = Result.build(ResultType.Success).appendData("pageInfo", pageInfo).convertIntoJSON();
            if(pageInfo != null) {
                cache.set(cacheName, json);
            }
        }
        response.getWriter().write(json);
    }

    /**
     * 删除所有设备移入记录缓存
     * @return Result
     */
    @ApiOperation(value = "删除所有设备移入记录缓存", notes = "")
    @DeleteMapping("deviceMoveIns/cache")
    @ResponseBody
    public Result destroyCache(){
        cache.deleteByPaterm(CacheNameHelper.base);
        return Result.build(ResultType.Success);
    }
}
