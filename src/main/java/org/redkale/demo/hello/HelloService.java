package org.redkale.demo.hello;

import org.redkale.net.http.*;
import org.redkale.service.Service;

@RestService(name = "hello")
public class HelloService implements Service {

    @RestMapping(name = "say")
    public String sayHello() {
        return "Hello World！";
    }

    @RestMapping(name = "hi")
    public String hi(@RestParam(name = "name") String name) {
        return "Hi, " + name + "！";
    }

}
