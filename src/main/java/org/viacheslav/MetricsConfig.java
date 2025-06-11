package org.viacheslav;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

@WebListener
public class MetricsConfig implements ServletContextListener {

    static {
        System.out.println("✅ MetricsConfig: Класс загружен!");
    }

    private PrometheusMeterRegistry registry;
    private HttpServer server;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("MetricsConfig: Контекст начат!");
        // Инициализируем реестр метрик
        registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        // Регистрируем системные метрики
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);

        // Простой счетчик
        Counter requestCounter = registry.counter("myapp_http_requests_total");
        requestCounter.increment();

        try {
            // Запускаем HTTP-сервер на порту 9095 для метрик
            server = HttpServer.create(new InetSocketAddress(9095), 0);
            server.createContext("/metrics", httpExchange -> {
                String response = registry.scrape();
                httpExchange.sendResponseHeaders(200, response.length());
                try (var os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            //server.setExecutor(null); // creates a default executor
            server.start();
            System.out.println("Сервер метрик запущен на http://localhost:9095/metrics");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (server != null) {
            server.stop(0);
        }
    }
}
