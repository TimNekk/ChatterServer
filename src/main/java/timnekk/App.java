package timnekk;

public class App {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try (Server server = new Server(9999)) {
            server.run();
        } catch (Exception e) {
            // TODO: handle
        }
    }
}
