package com.calc.service;

import com.calc.enums.ForecastType;
import com.calc.model.SalaryRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final RedisTemplate<String, byte[]> redisTemplate;

    public byte[] getForecast(SalaryRequest request, ForecastType type) throws Exception {
        String cacheKey = "salary" + request.getId() + ":" + type;

        // Если есть в кэше
        if (redisTemplate.hasKey(cacheKey)) {
            return redisTemplate.opsForValue().get(cacheKey);
        }
        // Если нет
        List<Double> predictedY = calculatePoints(request.getX(), request.getY(), type);

        byte[] imageBytes = generateImage(request.getX(), request.getY(), predictedY);

        redisTemplate.opsForValue().set(cacheKey, imageBytes, 10, TimeUnit.MINUTES);

        return imageBytes;
    }

    // ЛОГИКА МАТЕМАТИКИ (сгенерено ботом)
    private List<Double> calculatePoints(double[] x, double[] y, ForecastType type) {
        List<Double> result = new ArrayList<>();
        double lastX = x[x.length - 1];
        // Вычисляем шаг (разницу между x1 и x0), если точек мало - берем 1.0
        double step = (x.length > 1) ? (x[1] - x[0]) : 1.0;

        if (type == ForecastType.linear) {
            // Линейная регрессия
            SimpleRegression regression = new SimpleRegression();
            for (int i = 0; i < x.length; i++) {
                regression.addData(x[i], y[i]);
            }
            // Прогноз на 10 шагов вперед
            for (int i = 1; i <= 10; i++) {
                result.add(regression.predict(lastX + (step * i)));
            }
        } else if (type == ForecastType.poli) {
            // Полиномиальная регрессия (кривая)
            WeightedObservedPoints obs = new WeightedObservedPoints();
            for (int i = 0; i < x.length; i++) {
                obs.add(x[i], y[i]);
            }
            // Степень 2 (парабола)
            PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
            double[] coeffs = fitter.fit(obs.toList());

            // a0 + a1*x + a2*x^2
            for (int i = 1; i <= 10; i++) {
                double nextX = lastX + (step * i);
                double val = coeffs[0] + coeffs[1] * nextX + coeffs[2] * Math.pow(nextX, 2);
                result.add(val);
            }
        }
        return result;
    }

    // ЛОГИКА РИСОВАНИЯ (сгенерено ботом)
    private byte[] generateImage(double[] x, double[] y, List<Double> predictedY) throws Exception {
        // Данные истории (то, что прислал юзер)
        XYSeries seriesOriginal = new XYSeries("History");
        for (int i = 0; i < x.length; i++) {
            seriesOriginal.add(x[i], y[i]);
        }

        // Данные прогноза (то, что мы насчитали)
        XYSeries seriesForecast = new XYSeries("Forecast");
        double lastX = x[x.length - 1];
        double step = (x.length > 1) ? (x[1] - x[0]) : 1.0;

        for (int i = 0; i < predictedY.size(); i++) {
            seriesForecast.add(lastX + (step * (i + 1)), predictedY.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesOriginal);
        dataset.addSeries(seriesForecast);

        // Создаем график
        JFreeChart chart = ChartFactory.createXYLineChart("Salary Prediction", "Time", "Salary", dataset, PlotOrientation.VERTICAL, true, true, false);

        // Конвертируем в байты (PNG)
        BufferedImage objBufferedImage = chart.createBufferedImage(800, 600);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(objBufferedImage, "png", baos);
        return baos.toByteArray();
    }
}
