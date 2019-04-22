package cn.edu.ncut.hikvision_graduation.pojo;

/**
 * 设备实体类
 */
public class CameraDevice {

    /**
     * IP地址
     */
    private String ip;
    /**
     * 端口
     */
    private int port;

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 通道号
     */
    private int channel;


    public CameraDevice() {
        super();
    }

    public CameraDevice(String ip, int port, String username, String password, int channel) {
        super();
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.channel = channel;
    }


    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "CameraDevice{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", channel=" + channel +
                '}';
    }
}
