//package org.redkale.demo.hello;
//
//import java.io.IOException;
//
//import org.redkale.annotation.Resource;
//import org.redkale.convert.json.JsonConvert;
//import org.redkale.net.http.HttpRequest;
//import org.redkale.net.http.HttpResponse;
//import org.redkale.net.http.HttpServlet;
//import org.redkale.net.http.WebServlet;
//
//@WebServlet({"/hello/*"})
//public class HelloWorldServlet extends HttpServlet {
//
//    @Resource
//    private HelloWorldService helloWorldService;
//
//    @Override
//    public void execute(HttpRequest request, HttpResponse response) throws IOException {
//        response.finishJson(JsonConvert.root().convertTo(helloWorldService));
//    }
//}
