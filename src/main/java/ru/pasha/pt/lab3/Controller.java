package ru.pasha.pt.lab3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.pasha.pt.lab3.game.Game;
import ru.pasha.pt.lab3.game.GameException;

@org.springframework.stereotype.Controller
public class Controller {
    private final Game game;

    @Autowired
    public Controller(Game game) {
        this.game = game;
    }

    @ResponseBody
    @GetMapping(value = "/connect", produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer connect() {
        try {
            return Answer.ok(game.addNewPlayer());
        } catch (GameException e) {
            return Answer.error(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return Answer.error("Произошла ошибка на сервере");
        }
    }

    @ResponseBody
    @GetMapping(value = "/go", produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer go(@RequestParam double angle, @RequestParam String token) {
        try {
            return Answer.ok(game.step(token, angle));
        } catch (GameException e) {
            return Answer.error(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return Answer.error("Произошла ошибка на сервере");
        }
    }

    @ResponseBody
    @GetMapping(value = "/canGo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer canGo(@RequestParam String token) {
        try {
            return Answer.ok(game.canStep(token));
        } catch (GameException e) {
            return Answer.error(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return Answer.error("Произошла ошибка на сервере");
        }
    }

    @ResponseBody
    @GetMapping(value = "/validateToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer validateToken(@RequestParam String token) {
        return Answer.ok(game.validateToken(token));
    }

    @ResponseBody
    @GetMapping(value = "/getState", produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer getSate(@RequestParam String token) {
        try {
            return Answer.ok(game.getState(token));
        } catch (GameException e) {
            return Answer.error(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return Answer.error("Произошла ошибка на сервере");
        }
    }
}
