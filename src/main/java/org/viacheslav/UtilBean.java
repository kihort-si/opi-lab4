package org.viacheslav;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.*;

@Named("utilBean")
@ApplicationScoped
public class UtilBean implements Serializable {
    public static final Logger logger = Logger.getLogger("UtilBean");
    @Getter
    @Setter
    private double x;
    @Getter
    @Setter
    private double y;
    @Setter
    @Getter
    private double r;

    @Setter
    @Getter
    private ArrayList<Point> pointsList;

    private DBController dbController;

    @PostConstruct
    public void init() {
        x = 0;
        y = 0;
        r = 1;
        dbController = DBController.getInstance();
        pointsList = dbController.getAll();
        if (pointsList == null) {
            pointsList = new ArrayList<>();
        }
    }

    public String clear() {
        dbController.clear(getSessionId());
        //pointsList.clear();
        pointsList = dbController.getAll();
        return "goToMain?faces-redirect=true";
    }

    private String getSessionId() {
        // Получаем текущий контекст
        FacesContext context = FacesContext.getCurrentInstance();
        // Получаем HttpServletRequest из контекста
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        // Получаем HttpSession
        HttpSession session = request.getSession(false); // false - не создавать новую сессию
        if (session != null) {
            return session.getId(); // Возвращаем ID сессии
        }
        return null; // Или обработайте случай, когда сессия отсутствует
    }

    public String checkAndAdd() {
        logger.info("Пришёл запрос на добавление точки: x = " + x + ", y = " + y + ", r = " + r);
        logger.info("SessionID=" + getSessionId());
        Point point = new Point(x, y, r);
        point.setSession(getSessionId());
        dbController.addPoint(point);
        pointsList.add(point);
        return "goToMain?faces-redirect=true";
    }

    public String pointsToString() {
        String pointsStr = "";
        for (Point point : pointsList) {
            pointsStr += point.getX() + "," + point.getY() + "," + point.getR() + "," + point.isResult() +";";
        }
        if (pointsStr.isEmpty()) return pointsStr;
        return pointsStr.substring(0, pointsStr.length() - 1);
    }

    public boolean checkSession(String sessionId) {
        return sessionId.equals(getSessionId());
    }

}