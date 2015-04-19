import javax.websocket.Session;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChessSession {
    private Set<Session> analysisUsers = new HashSet<Session>();
    private String currentFen;

    public void addUser(Session session) {
        analysisUsers.add(session);
    }

    public Iterator<Session> getUsersIterator() {
        return analysisUsers.iterator();
    }

    public boolean removeUser(Session session) {
        return analysisUsers.remove(session);
    }

    public String getCurrentFen() {
        return currentFen;
    }

    public void setCurrentFen(String currentFen) {
        this.currentFen = currentFen;
    }
}
