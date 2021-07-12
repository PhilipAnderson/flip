package flip.raspberry;

import java.util.concurrent.ForkJoinPool;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.scene.image.Image;
import javafx.scene.Cursor;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;


@Component
class JavaFXComponent {

    @Autowired Environment               environment;
    @Autowired ImagePane                 imagePane;
    @Autowired ApplicationArguments      arguments;
    @Autowired ApplicationEventPublisher publisher;


    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        JavaFXRoot.environment = environment;
        JavaFXRoot.imagePane   = imagePane;
        JavaFXRoot.publisher   = publisher;

        ForkJoinPool
            .commonPool()
            .execute(() -> JavaFXRoot.launch(JavaFXRoot.class, arguments.getSourceArgs()));
    }
}

class JavaFXReadyEvent extends ApplicationEvent {
    JavaFXReadyEvent(Object source) {
        super(source);
    }
}

public class JavaFXRoot extends Application {

    static Environment               environment;
    static ImagePane                 imagePane;
    static ApplicationEventPublisher publisher;

    @Override
    public void start(Stage stage) {

        Screen screen = Screen.getPrimary();
        Scene scene   = new Scene(imagePane, 480, 330);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        if (environment.acceptsProfiles(Profiles.of("release"))) {
            scene.setCursor(Cursor.NONE);
            stage.setFullScreen(true);
            stage.setX(screen.getVisualBounds().getMinX());
            stage.setY(screen.getVisualBounds().getMinY());
            stage.setWidth (screen.getVisualBounds().getWidth());
            stage.setHeight(screen.getVisualBounds().getHeight());
        } else {
            stage.setFullScreen(false);
            stage.setResizable(false);
            stage.sizeToScene();
        }

        stage.setScene(scene);
        stage.show();

        publisher.publishEvent(new JavaFXReadyEvent(this));
    }
}
