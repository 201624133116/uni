package edu.uni.labManagement.controller;

import edu.uni.bean.Result;
import edu.uni.bean.ResultType;
import edu.uni.labManagement.bean.MaintenanceRecords;
import edu.uni.labManagement.pojo.MaintenanceRecordsPojo;
import edu.uni.labManagement.service.MaintenanceRecordsService;
import edu.uni.utils.RedisCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Create by Administrator
 *
 * @author sola
 * @date 2019/05/04 15:34
 */
@Api(description = "设备维修记录模块")
@Controller
@RequestMapping("json/labManagement/maintenanceRecords")
public class MaintenanceRecordsController {

	static class CacheNameHelper{
//		由于这里的接口使用不频繁，所以不是用Redis
	}

	@Autowired
	private MaintenanceRecordsService maintenanceRecordsService;


	@ApiOperation(value = "通过设备维修申请id查询")
	@GetMapping("{id}")
	@ResponseBody
	public void receive(HttpServletResponse response, @PathVariable Integer id) throws Exception{
		response.setContentType("application/json;charset=utf-8");
		MaintenanceRecords maintenanceRecords = maintenanceRecordsService.listByRepairApplyId(id);
		System.out.println(maintenanceRecords);
		String json = Result.build(ResultType.Success).appendData("res", maintenanceRecords).convertIntoJSON();
		response.getWriter().write(json);
	}

	/**
	 * 根据id更新维修记录
	 * @param maintenanceRecords
	 * @return
	 */
	@ApiOperation(value="根据设备维修记录ID可选性地更新")
	@PutMapping
	@ResponseBody
	public Result update(@RequestBody(required = false) MaintenanceRecords maintenanceRecords){
		if(maintenanceRecords != null && maintenanceRecords.getId() != null){
			boolean success = maintenanceRecordsService.update(maintenanceRecords);
			if(success){
				return Result.build(ResultType.Success);
			}else{
				return Result.build(ResultType.Failed);
			}
		}
		return Result.build(ResultType.ParamError);
	}

	/**
	 * 创建设备维修
	 * @param maintenanceRecords
	 * @return
	 */
	@ApiOperation(value = "创建设备维修")
	@PostMapping
	@ResponseBody
	public Result create(@RequestBody(required = false) MaintenanceRecords maintenanceRecords){
		if(maintenanceRecords != null){
			boolean success = maintenanceRecordsService.insert(maintenanceRecords);
			if(success) {
				return Result.build(ResultType.Success);
			} else {
				return Result.build(ResultType.Failed);
			}
		}
		return  Result.build(ResultType.ParamError);
	}

	/**
	 * 根据设备维修记录ID删除记录
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "根据设备维修记录ID删除记录")
	@DeleteMapping("{id}")
	@ResponseBody
	public Result destroy(@PathVariable long id){
		boolean success = maintenanceRecordsService.delete(id);
		if(success) {
			return Result.build(ResultType.Success);
		}else{
			return Result.build(ResultType.Failed);
		}
	}

	@ApiOperation(value = "通过实验室id查询")
	@GetMapping("/byLabid/{labId}")
	@ResponseBody
	public void receive2(HttpServletResponse response, @PathVariable long labId) throws Exception{
		response.setContentType("application/json;charset=utf-8");
		List<MaintenanceRecordsPojo> records = maintenanceRecordsService.listByLabId(labId);
		System.out.println(records);
		String json = Result.build(ResultType.Success).appendData("res", records).convertIntoJSON();
		response.getWriter().write(json);
	}

	@ApiOperation(value = "通过设备的ID查询维修记录", notes = "待测试")
	@GetMapping("/byDeviceId/{deviceId}")
	@ResponseBody
	public void receive3(HttpServletResponse response, @PathVariable long deviceId) throws Exception{
		response.setContentType("application/json;charset=utf-8");
		List<MaintenanceRecordsPojo> pojos = maintenanceRecordsService.listByDeviceId(deviceId);
		if (pojos != null) {
			response.getWriter().write(Result.build(ResultType.Success).appendData("res", pojos).convertIntoJSON());
		} else {
			response.getWriter().write(Result.build(ResultType.Failed).convertIntoJSON());
		}
	}
}
