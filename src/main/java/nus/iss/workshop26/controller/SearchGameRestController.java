package nus.iss.workshop26.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import nus.iss.workshop26.models.Comment;
import nus.iss.workshop26.models.Game;
import nus.iss.workshop26.models.Games;
import nus.iss.workshop26.services.SearchBGGService;

@RestController
@RequestMapping
public class SearchGameRestController {

    @Autowired
    private SearchBGGService bggSvc;

    @GetMapping(path = "/games")
    public ResponseEntity<String> getAllGame(@RequestParam(defaultValue = "25") Integer limit, @RequestParam(defaultValue = "0") Integer offset) {
        List<Game> results = bggSvc.searchGame(limit, offset);
        JsonObject result = null;
        // Build the result
        JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        Games gs = new Games();
        gs.setGames(results);
        gs.setTotal(results.size());
        gs.setLimit(limit);
        gs.setOffset(offset);
        gs.setTimestamp(LocalDateTime.now());

        objBuilder.add("bgg", gs.toJSON());
        result = objBuilder.build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toString());
    }

    @GetMapping(path = "/games/rank")
    public ResponseEntity<String> getGameByRanking(@RequestParam(defaultValue = "25") Integer limit, @RequestParam(defaultValue = "0") Integer offset) {
        JsonArray result = null;
        List<Game> results = bggSvc.getGamesByRank();
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (int i = offset; i < limit; i++) {
            arrBuilder.add(results.get(i).toJSON());
        }
        result = arrBuilder.build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toString());
    }

    @GetMapping(path = "/game/{gameId}")
    public ResponseEntity<String> getGameDetailsById(@PathVariable Integer gameId) {
        JsonObject result = null;
        Game gameDetails = bggSvc.getGameDetailsById(gameId);
        if (gameDetails == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\": \"game " + gameId + " not found\"}");
        }
        JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        objBuilder.add("game", gameDetails.toJSON());
        result = objBuilder.build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toString());
    }

    @GetMapping(path = "/comment")
    public ResponseEntity<String> searchComment(@RequestParam String q, @RequestParam Float score) {
        System.out.printf("q=%s, score=%f\n", q, score);
        List<Comment> results = bggSvc.searchComment(q, score, 20, 0);
        JsonArray result = null;
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        for (Comment g : results)
            arrBuilder.add(g.toJSON());
        result = arrBuilder.build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result.toString());
    }
}
