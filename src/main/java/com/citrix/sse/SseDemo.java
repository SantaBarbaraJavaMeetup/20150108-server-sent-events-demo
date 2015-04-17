package com.citrix.sse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebServlet(value = "/sse", asyncSupported = true)
public class SseDemo extends HttpServlet {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // enabled async request processing
        request.startAsync();

        // sse media type
        response.setContentType("text/event-stream");

        // send comment every second
        executor.scheduleWithFixedDelay(() -> send(response, ": keep-alive\n\n"), 0, 1000, TimeUnit.MILLISECONDS);

        // send date every 5 seconds
        executor.scheduleWithFixedDelay(() -> send(response, "event: date\ndata: " + new Date() + "\n\n"), 0, 5000, TimeUnit.MILLISECONDS);
    }

    private void send(HttpServletResponse resp, String data) {
        try {
            resp.getOutputStream().write(data.getBytes());
            resp.flushBuffer();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


}
