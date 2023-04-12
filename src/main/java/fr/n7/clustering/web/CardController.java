package fr.n7.clustering.web;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class CardController {
    String[] cards = new String[] {
            "sort",
            "region",
            "zones",
            "2in1",
            "cutting",
    };

    public CardController() {}

    @PostMapping(path = "/run")
    @ResponseBody
    public String run(@RequestBody String body) {
        var obj = new JSONObject(body);
        new CardsBuilder(new RunBody(obj)).start();

        return "{\"status\":\"Running\",\"timestamp\":" + new Date().getTime() + '}';
    }
}
