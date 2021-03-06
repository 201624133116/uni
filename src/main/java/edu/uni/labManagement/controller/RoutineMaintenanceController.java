package edu.uni.labManagement.controller;

import edu.uni.bean.Result;
import edu.uni.bean.ResultType;
import edu.uni.labManagement.bean.MaintenanceRecords;
import edu.uni.labManagement.bean.RoutineMaintenance;
import edu.uni.labManagement.bean.RoutineMaintenanceDetail;
import edu.uni.labManagement.pojo.RoutineMaintenanceDetailPojo;
import edu.uni.labManagement.pojo.RoutineMaintenancePojo;
import edu.uni.labManagement.service.MaintenanceRecordsService;
import edu.uni.labManagement.service.RoutineMaintenanceService;
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
 * @date 2019/05/06 1:15
 */
@Api(description = "日常维护模块")
@Controller
@RequestMapping("json/labManagement/routineMaintenance")
public class RoutineMaintenanceController {
	static class CacheNameHelper{
		private static final String base = "lm_routineMaintenance_*";
//		lm_routineMaintenance_DeviceId_{deviceId}
		private static final String listByDeviceId = "lm_routineMaintenance_DeviceId_";
//		lm_routineMaintenance_LabId_{labId}
		private static final String listByLabId = "lm_routineMaintenance_LabId_";
	}

	@Autowired
	private RedisCache cache;
	@Autowired
	private RoutineMaintenanceService routineMaintenanceService;

	@GetMapping("/listByDeviceId/{deviceId}")
	@ApiOperation(value = "通过设备ID查询日常维护记录")
	@ResponseBody
	public void receive(HttpServletResponse response, @PathVariable long deviceId) throws Exception{
		response.setContentType("application/json;charset=utf-8");
		String cacheName = CacheNameHelper.listByDeviceId + deviceId;
		String json = cache.get(cacheName);

		if (json == null || json == "") {
			List<RoutineMaintenance> maintenanceRecords = routineMaintenanceService.listByDeviceId(deviceId);
			json = Result.build(ResultType.Success).appendData("res", maintenanceRecords).convertIntoJSON();
			if (maintenanceRecords != null) {
				cache.set(cacheName, json);
			}
		}
		response.getWriter().write(json);
	}

	@GetMapping("/listByLabId/{labId}")
	@ApiOperation(value = "通过实验室ID查询日常维护记录")
	@ResponseBody
	public void receive2(HttpServletResponse response, @PathVariable long labId) throws Exception{
		response.setContentType("application/json;charset=utf-8");
		String cacheName = CacheNameHelper.listByLabId + labId;
		cache.deleteByPaterm(cacheName);
		String json = cache.get(cacheName);

		if (json == null || json == "") {
			List<RoutineMaintenancePojo> pojos = routineMaintenanceService.listByLabId(labId);
			json = Result.build(ResultType.Success).appendData("res", pojos).convertIntoJSON();
			System.out.println("------------>" + pojos);
			if (pojos != null) {
				cache.set(cacheName, json);
			}
		}
		response.getWriter().write(json);
	}

	@PostMapping
	@ApiOperation(value = "创建设备维护", notes = "待测试")
	@ResponseBody
	public Result create(@RequestBody RoutineMaintenanceDetailPojo pojo) {

		boolean success = routineMaintenanceService.createMaintenance(pojo.getMaintenance(), pojo.getDetails());
		if (success) {
			cache.deleteByPaterm(CacheNameHelper.base);
			cache.deleteByPaterm("lm_routineMaintenanceDetail_*");
			return Result.build(ResultType.Success);
		}
		return Result.build(ResultType.Failed);
	}
}
