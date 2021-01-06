package karnaugh;

// The sole purspose of this class is to allow jar file compilation
// without it, jar doesn't run because of missing javaFX components
public class Launcher {
    
    public static void main(String[] args) {
        App app = new App();

        app.main(args);
    }
}
