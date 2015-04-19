import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@ServerEndpoint("/chatroomServerEndpoint")
public class ChessAnalysisServerEndpoint {
    private static final String SESSION_ID = "sessionId";
    //    private static Set<Session> analysisUsers = Collections.synchronizedSet(new HashSet<Session>());
//    private static String currentFen;
    private static Map<Integer, ChessSession> sessions = Collections.synchronizedMap(new HashMap<Integer, ChessSession>());

    public ChessAnalysisServerEndpoint() {
        if (!sessions.containsKey(1)) {
            sessions.put(1, new ChessSession());
        }
    }

    @OnOpen
    public void onOpen(Session userSession) throws IOException {
        Integer sessionId = (Integer) userSession.getUserProperties().get(SESSION_ID);
//        ChessSession chessSession = ;
        if (sessionId == null) {
            sessionId = Integer.valueOf(1); // all users goes to one analysis board for now
            userSession.getUserProperties().put(SESSION_ID, sessionId);
//            chessSession = new ChessSession();
//            sessions.put(sessionId, chessSession);
        }
//        else {
//            chessSession = sessions.get(sessionId);
//        }
        final ChessSession chessSession = sessions.get(1);
        chessSession.addUser(userSession);
        final String currentFen = chessSession.getCurrentFen();
        if (currentFen != null) {
            userSession.getBasicRemote().sendText(buildJsonData(currentFen));
        }
    }

    @OnMessage
    public void onMessage(String message, Session userSession) throws IOException {
        Integer sessionId = (Integer) userSession.getUserProperties().get(SESSION_ID);
        final ChessSession chessSession = sessions.get(sessionId);
        synchronized (chessSession) {
            chessSession.setCurrentFen(message);
            Iterator<Session> iterator = chessSession.getUsersIterator();
            while (iterator.hasNext()) iterator.next().getBasicRemote().sendText(buildJsonData(message));
        }
    }

    @OnClose
    public void onClose(Session userSession) {
        Integer sessionId = (Integer) userSession.getUserProperties().get(SESSION_ID);
        sessions.get(sessionId).removeUser(userSession);
    }

    private String buildJsonData(String message) {
        JsonObject jsonObject = Json.createObjectBuilder().add("fen", message).build();
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.write(jsonObject);
        }
        return stringWriter.toString();
    }
}
