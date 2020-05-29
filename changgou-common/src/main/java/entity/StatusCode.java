package entity;

/**
 * 返回码
 */
public class StatusCode {
    public static final int OK = 20000;//成功   2000 开头的都是成功的一些提示信息
    public static final int ERROR = 20001;//失败
    public static final int LOGINERROR = 20002;//用户名或密码错误
    public static final int ACCESSERROR = 20003;//权限不足
    public static final int REMOTEERROR = 20004;//远程调用失败
    public static final int REPERROR = 20005;//重复操作
    public static final int NOTFOUNDERROR = 20006;//没有对应的抢购数据


    //50000开头的都是 错误相关 商品部存在   系统异常   人品太差 颜值不够.
}
