package cn.edu.ncut.hikvision_graduation.dao;


import cn.edu.ncut.hikvision_graduation.pojo.CameraDevice;

/**
 * Created by 赵希贤 on 2019/4/10.
 */

public interface DeviceDao {
    public abstract boolean log_in(String ip, String userName, String passWord, int port);


    public abstract void registered(CameraDevice cameraDevice);
}
