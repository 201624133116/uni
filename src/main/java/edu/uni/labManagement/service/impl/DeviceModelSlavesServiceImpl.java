package edu.uni.labManagement.service.impl;

import edu.uni.auth.bean.User;
import edu.uni.auth.service.AuthService;
import edu.uni.labManagement.bean.DeviceModelExample;
import edu.uni.labManagement.bean.DeviceModelSlaves;
import edu.uni.labManagement.bean.DeviceModelSlavesExample;
import edu.uni.labManagement.mapper.DeviceModelSlavesMapper;
import edu.uni.labManagement.service.DeviceModelSlavesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Create by Administrator
 *
 * @author sola
 * @date 2019/04/30 13:27
 */
@Service
public class DeviceModelSlavesServiceImpl implements DeviceModelSlavesService {

	@Resource
	private DeviceModelSlavesMapper deviceModelSlavesMapper;
	@Autowired
	private AuthService authService;

	@Override
	public boolean insert(DeviceModelSlaves deviceModelSlaves) {
		deviceModelSlaves.setDeleted(false);
		deviceModelSlaves.setDatetime(new Date());
		deviceModelSlaves.setByWho(((User)authService.getUser()).getId());
		deviceModelSlaves.setUniversityId(((User)authService.getUser()).getUniversityId());

		return deviceModelSlavesMapper.insert(deviceModelSlaves) > 0 ? true : false;
	}

	@Override
	public boolean update(DeviceModelSlaves deviceModelSlaves) {
		DeviceModelSlavesExample example = new DeviceModelSlavesExample();
		DeviceModelSlavesExample.Criteria criteria = example.createCriteria();
		criteria.andMaterIdEqualTo(deviceModelSlaves.getMaterId());
		criteria.andSlaveIdEqualTo(deviceModelSlaves.getSlaveId());
		criteria.andDeletedEqualTo(false);
		List<DeviceModelSlaves> modelSlaves = deviceModelSlavesMapper.selectByExample(example);
		if(modelSlaves == null) {
			return false;
		}
		modelSlaves.get(0).setDeleted(true);
		deviceModelSlavesMapper.insert(modelSlaves.get(0));

		deviceModelSlaves.setUniversityId(((User)authService.getUser()).getUniversityId());
		deviceModelSlaves.setByWho(((User)authService.getUser()).getId());
		return deviceModelSlavesMapper.updateByExampleSelective(deviceModelSlaves, example) > 0 ? true : false;
	}

	@Override
	public boolean delete(DeviceModelSlaves deviceModelSlaves) {
		DeviceModelSlavesExample example = new DeviceModelSlavesExample();
		DeviceModelSlavesExample.Criteria criteria = example.createCriteria();
		criteria.andMaterIdEqualTo(deviceModelSlaves.getMaterId());
		criteria.andSlaveIdEqualTo(deviceModelSlaves.getSlaveId());
		return deviceModelSlavesMapper.deleteByExample(example) > 0 ? true : false;
	}

	@Override
	public List<DeviceModelSlaves> listByPid(long pid) {
		DeviceModelSlavesExample example = new DeviceModelSlavesExample();
		DeviceModelSlavesExample.Criteria criteria = example.createCriteria();
		criteria.andMaterIdEqualTo(pid);
		return deviceModelSlavesMapper.selectByExample(example);
	}

	@Override
	public List<DeviceModelSlaves> listBySid(long sid) {
		DeviceModelSlavesExample example = new DeviceModelSlavesExample();
		DeviceModelSlavesExample.Criteria criteria = example.createCriteria();
		criteria.andSlaveIdEqualTo(sid);
		return deviceModelSlavesMapper.selectByExample(example);
	}
}
