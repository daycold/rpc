# README

微服务

## 功能
课件 demo 目录
例：
    
    @RequestMapping("/test")
    public interface TestController {
        @GetMapping("")
        public String doTest(@RequestParam("name") String name);
    }

    在一个服务中实现该接口

    public class TestControllerImpl {
      ......
    }

    另一个服务中使用代理注册接口成 bean，直接调用接口即可访问服务。
    TestController controller = Proxy.newProxy(RpcProxy.class.getClassLoad(), new Class[] ......)
    controller.doTest("name"); 代理将执行一次远程调用，并反序列化结果。


## 安全性
1. 使用局域网，只能内部访问
2. 服务端提供的接口，需要请求端传入公钥，做非对称性验证
