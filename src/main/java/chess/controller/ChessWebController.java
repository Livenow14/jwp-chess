package chess.controller;

import chess.JsonTransformer;
import chess.service.ChessService;
import chess.service.dto.*;
import com.google.gson.Gson;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class ChessWebController {

    private final ChessService chessService;

    public ChessWebController(final ChessService chessService) {
        this.chessService = chessService;
    }

    private static String render(Map<String, Object> model, String templatePath) {
        return new HandlebarsTemplateEngine().render(new ModelAndView(model, templatePath));
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    public void run() {
        staticFiles.location("/static");

        final JsonTransformer jsonTransformer = new JsonTransformer();

        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return render(model, "main.html");
        });

        get("/games", (request, response) -> {
            TilesDto tilesDto = chessService.emptyBoard();
            Map<String, Object> model = new HashMap<>();
            model.put("tilesDto", tilesDto);
            return render(model, "board.html");
        });

        post("/api/games", (request, response) -> {
            ChessSaveRequestDto requestDto = new Gson().fromJson(request.body(), ChessSaveRequestDto.class);
            return chessService.startChess(requestDto);
        }, jsonTransformer);

        put("/api/games", (request, response) -> {
            GameStatusRequestDto requestDto = new Gson().fromJson(request.body(), GameStatusRequestDto.class);
            chessService.changeGameStatus(requestDto);
            return new CommonResponseDto<>(ResponseCode.OK.code(), ResponseCode.OK.message());
        }, jsonTransformer);

        get("/api/games/:name", (request, response) -> {
            String name = request.params(":name");
            return chessService.loadChess(name);
        }, jsonTransformer);

        put("/api/games/:name/pieces", (request, response) -> {
            MoveRequestDto requestDto = new Gson().fromJson(request.body(), MoveRequestDto.class);
            return chessService.movePiece(request.params(":name"), requestDto);
        }, jsonTransformer);
    }
}
