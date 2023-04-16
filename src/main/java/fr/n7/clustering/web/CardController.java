package fr.n7.clustering.web;

import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class CardController {

    public static RunStatus status = new RunStatus();

    public CardController() {}

    @PostMapping(path = "/run")
    @ResponseBody
    public String run(@RequestBody String body) {
        if (status.running) {
            return "{\"status\": \"Error\",\"message\":\"Already running\"}";
        }

        var obj = new JSONObject(body);
        new CardsBuilder(new RunBody(obj)).start();

        return "{\"status\":\"Running\",\"timestamp\":" + new Date().getTime() + '}';
    }

    @PostMapping(path = "/stop")
    @ResponseBody
    public String stop() {
        status.setStopping(true);
        return "{\"status\":\"Stopping\",\"timestamp\":" + new Date().getTime() + '}';
    }

    @GetMapping(path = "/status")
    @ResponseBody
    public String status() {
        return status.toString();
    }
}
