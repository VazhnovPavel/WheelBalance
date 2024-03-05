package com.testSpringBoot.SpringDemoBot.model;

import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс для создания визуальных графиков для пользователей
 */
@Component
public class Chart {

    public String createLabel (Map<String, Double> chartToSend){

        String labels = chartToSend.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() != 0 && entry.getValue() != 0.0)
                .map(entry -> entry.getKey() + ": " + entry.getValue()) // добавляем значение key и value
                .map(label -> "'" + label + "'")
                .collect(Collectors.joining(", "));
        return labels;
    }

    public String createData (Map<String, Double> chartToSend){

        String data = chartToSend.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() != 0 && entry.getValue() != 0.0)
                .map(Map.Entry::getValue)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        return data;
    }

    public String generatePieChart (String labels, String data, String titleString){
        String setConfig= "{"
                + "type: 'polarArea',"
                + "data: {"
                + "labels: [" + labels + "],"
                + "datasets: [{"
                + "data: [" + data + "],"
                + "backgroundColor: ["
                + "'rgb(255, 99, 132)',"
                + "'rgb(75, 192, 192)',"
                + "'rgb(255, 205, 86)',"
                + "'rgb(201, 203, 207)',"
                + "'rgb(54, 162, 235)',"
                + "'rgb(167, 238, 133)',"
                + "'rgb(153, 102, 255)',"
                + "'rgb(255, 99, 64)',"
                + "'rgb(75, 192, 64)',"
                + "'rgb(255, 159, 64)'"
                + "]"
                + "}]"
                + "},"
                + "options: {"
                + "padding: 200," // Добавляем отступы
                + "title: {"
                + "display: true,"
                + "text: '" + titleString + "',"
                + "fontColor: '#141449',"
                + "fontSize: 25,"
                + "fontFamily: 'Georgia',"
                + "fontStyle: 'normal',"
                + "padding: 20"
                + "},"
                + "legend: {"
                + "position: 'left',"
                + "labels: {"
                + "fontColor: '#141449',"
                + "fontSize: 22,"
                + "fontFamily: 'Georgia',"
                + "fontStyle: 'normal',"
                + "padding: 20"
                + "},"
                + "},"
                + "scale: {"
                + "gridLines: {"
                + "color: '#9E9E9E'"
                + "},"
                + "ticks: {"
                + "display: false,"
                + "min: 0,"
                + "max: 10,"
                + "}"
                + "},"
                + "plugins: {"
                + "datalabels: {"
                + "color: 'white',"
                + "font: {"
                + "size: 18,"
                + "family: 'Georgia'"
                + "},"
                + "display: true"
                + "}"
                + "}"
                + "}"
                + "}";
        return setConfig;
    }

    public String generateRadarChart (String labels, String data1, String data2, String firstCompareName,
                                      String secondCompareName, String titleString) {

        String setConfig= "{"
                + "type: 'radar',"
                + "data: {"
                + "labels: [" + labels + "],"
                + "datasets: [{"
                + "label: '"+firstCompareName+"',"
                + "data: [" + data1 + "],"
                + "backgroundColor: 'rgba(255, 99, 132, 0.2)',"
                + "borderColor: 'rgba(255, 99, 132, 1)',"
                + "borderWidth: 2,"
                + "pointBackgroundColor: 'rgba(255, 99, 132, 1)'"
                + "}, {"
                + "label: '"+secondCompareName+"',"
                + "data: [" + data2 + "],"
                + "backgroundColor: 'rgba(54, 162, 235, 0.2)',"
                + "borderColor: 'rgba(54, 162, 235, 1)',"
                + "borderWidth: 2,"
                + "pointBackgroundColor: 'rgba(54, 162, 235, 1)'"
                + "}]"
                + "},"
                + "options: {"
                + "title: {"
                + "display: true,"
                + "text: '" + titleString + "',"
                + "fontColor: '#141449',"
                + "fontSize: 25,"
                + "fontFamily: 'Georgia',"
                + "fontStyle: 'normal',"
                + "padding: 20"
                + "},"
                + "legend: {"
                + "position: 'bottom',"
                + "labels: {"
                + "fontColor: '#141449',"
                + "fontSize: 25,"
                + "fontFamily: 'Georgia',"
                + "fontStyle: 'normal',"
                + "padding: 20"
                + "}"
                + "},"
                + "scale: {"
                + "gridLines: {"
                + "color: '#9E9E9E'"
                + "},"
                + "pointLabels: {"
                + "fontSize: 18,"
                + "fontColor: '#9E9E9E'"
                + "},"
                + "ticks: {"
                + "display: false,"
                + "min: 0,"
                + "max: 10,"
                + "color: '#9E9E9E'"
                + "}"
                + "},"
                + "elements: {"
                + "line: {"
                + "tension: 0.4"
                + "}"
                + "}"
                + "}"
                + "}";
        return setConfig;

    }

    public String generateTitleString (int currentDays){
        // Высчитываем диапазон дат, за которые необходимо вывести статистику. Опираемся на данные, переданные
        // в currentDays

        Calendar endDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -currentDays +1);
        DateFormat dateFormatFirst = new SimpleDateFormat("d MMMM", new Locale("ru"));
        DateFormat dateFormatSecond = new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));

        // Если в промежуток аналитики попадает два года (например старт в декабре 2022, а конец в январе 2023)
        // то выводим оба года. Если один год, то выводим год только в конце предложения

        String titleString;
        if (startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR)) {
            titleString = String.format("@Wheel_Balance_bot                      Отчет c %s по %s года",
                    dateFormatFirst.format(startDate.getTime()),
                    dateFormatSecond.format(endDate.getTime()));
        } else {
            titleString = String.format("@Wheel_Balance_bot                      Отчет c %s года по %s года",
                    dateFormatSecond.format(startDate.getTime()),
                    dateFormatSecond.format(endDate.getTime()));
        }
        return titleString;
    }
    public String generateTitleString(String month, int year) {
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.YEAR, year);
        startDate.set(Calendar.MONTH, getMonthNumber(month) - 1); // Месяцы в Calendar начинаются с 0

        DateFormat dateFormatMonth = new SimpleDateFormat("MMMM", new Locale("ru"));
        DateFormat dateFormatYear = new SimpleDateFormat("yyyy", new Locale("ru"));

        String monthName = dateFormatMonth.format(startDate.getTime());
        String yearString = dateFormatYear.format(startDate.getTime());

        String titleString = String.format("@Wheel_Balance_bot                                 " +
                "Отчет за %s %s года", monthName, yearString);

        return titleString;
    }

    private int getMonthNumber(String month) {
        switch (month.toUpperCase()) {
            case "ЯНВАРЬ":
                return 1;
            case "ФЕВРАЛЬ":
                return 2;
            case "МАРТ":
            case "МАРТА": // Дополнительное название для марта
                return 3;
            case "АПРЕЛЬ":
                return 4;
            case "МАЙ":
                return 5;
            case "ИЮНЬ":
                return 6;
            case "ИЮЛЬ":
                return 7;
            case "АВГУСТ":
                return 8;
            case "СЕНТЯБРЬ":
                return 9;
            case "ОКТЯБРЬ":
                return 10;
            case "НОЯБРЬ":
                return 11;
            case "ДЕКАБРЬ":
                return 12;
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

}






