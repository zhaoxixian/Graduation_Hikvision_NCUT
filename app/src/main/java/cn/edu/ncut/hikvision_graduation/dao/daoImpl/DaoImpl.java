package cn.edu.ncut.hikvision_graduation.dao.daoImpl;

import cn.edu.ncut.hikvision_graduation.dao.DeviceDao;
import cn.edu.ncut.hikvision_graduation.pojo.CameraDevice;

/**
 * Created by 赵希贤 on 2019/4/17.
 */

public class DaoImpl implements DeviceDao {
    @Override
    public boolean log_in(String ip, String userName, String passWord, int port) {

        // TODO: 2019/4/17 登陆的瞬间，调用registered()将设备信息存到sharedpreferences，之后再进行登陆，成功或者失败，都用Toast提示，之后在下一个dialog填写信息时，自动读取

        return false;
    }

    @Override
    public void registered(CameraDevice cameraDevice) {
        // TODO: 2019/4/17 存的过程中，正则表达式校验，错误的Toast提示
    }
}
