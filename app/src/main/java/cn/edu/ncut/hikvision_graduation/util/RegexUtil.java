package cn.edu.ncut.hikvision_graduation.util;

/**
 * 用于登陆信息校验
 * <p>
 * Created by 赵希贤 on 2019/4/21.
 */

public class RegexUtil {

    private static final String IP_REGEX = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";
    private static final String USERNAME_REGEX = "[a-zA-Z]{5,7}";// 大小写都行，必须是5到7个
    private static final String PASSWORD_REGEX = "\\w{6,12}";
    private static final String PORT_REGEX = "^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)";

    //私有构造，禁止造对象
    private RegexUtil() {
    }


    /**
     * 返回boolean，参数列表，ip地址的字符串
     *
     * @param Ip
     * @return true 校验通过，否则不通过，那么清空ip输入框，重新输入，光标集中此框
     */
    public static boolean isMatchIp(String Ip) {
        if (!Ip.matches(IP_REGEX)) {
            return false;
        }

        return true;
    }


    /**
     * 返回boolean，参数列表，用户名的字符串
     *
     * @param Username
     * @return true 校验通过，否则不通过，那么清空用户输入框，重新输入，光标集中此框
     */
    public static boolean isMatchUsername(String Username) {
        if (!Username.matches(USERNAME_REGEX)) {
            return false;
        }

        return true;
    }


    /**
     * 返回boolean，参数列表，密码的字符串
     *
     * @param Password
     * @return true 校验通过，否则不通过，那么清空密码输入框，重新输入，光标集中此框
     */
    public static boolean isMatchPassword(String Password) {
        if (!Password.matches(PASSWORD_REGEX)) {
            return false;
        }

        return true;
    }

    /**
     * 返回boolean，参数列表，密码的字符串
     *
     * @param Port
     * @return true 校验通过，否则不通过，那么清空端口号输入框，重新输入，光标集中此框
     */
    public static boolean isMatchPort(String Port) {
        if (!Port.trim().matches(PORT_REGEX)) {
            return false;
        }

        return true;
    }
}
