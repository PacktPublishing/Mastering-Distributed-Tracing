package formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FController {
    private final static Logger logger = LoggerFactory.getLogger(FController.class);

    @Autowired
    private lib.CallPath callPath;

    @Autowired
    private lib.ChaosMonkey chaosMonkey;

    @GetMapping("/formatGreeting")
    public String formatGreeting(@RequestParam String name) {
        logger.info("Name: {}", name);

        callPath.append();
        chaosMonkey.maybeFail();

        String response = "Hello, " + name + "!";
        logger.info("Response: {}", response);
        return response;
    }
}
