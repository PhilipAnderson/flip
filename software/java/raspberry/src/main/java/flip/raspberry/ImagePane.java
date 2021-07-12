package flip.raspberry;

import flip.raspberry.trial.TrialEvent;
import flip.raspberry.trial.TrialEventQueue;
import flip.raspberry.trial.TrialConfigException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Base64;
import java.io.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component
public class ImagePane extends Pane {

    final Logger LOG = LoggerFactory.getLogger(getClass());

    final Image              imageNotFound = loadBase64Image(imageNotFoundData);
    final String             imageLibrary  = System.getProperty("user.home") + "/.flip/library/";
    final List<ImageView>    imageViews    = new ArrayList<>();
    final List<TextArea>     textAreas     = new ArrayList<>();
    final Map<String, Image> imageCache    = new HashMap<>();

    @Autowired
    TrialEventQueue trialEventQueue;

    @Value("${flip.stimuli.regions}")
    List<String> regions;


    @EventListener
    public void handleJavaFXReady(JavaFXReadyEvent event) throws Exception {

        LOG.debug("Loading stimuli regions {}.", regions);

        setStyle("-fx-background-color: black");

        for (String region : regions) {
            String[] values = region.split(":");
            int x = Integer.parseInt(values[0].trim());
            int y = Integer.parseInt(values[1].trim());
            int w = Integer.parseInt(values[2].trim());
            int h = Integer.parseInt(values[3].trim());

            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(w);
            imageView.setFitHeight(h);
            imageView.relocate(x, y);

            getChildren().add(imageView);
            imageViews.add(imageView);

            TextArea textArea = new TextArea();
            textArea.setPrefWidth(w);
            textArea.setPrefHeight(h);
            textArea.relocate(x, y);

            textArea.setVisible(false);

            getChildren().add(textArea);
            textAreas.add(textArea);
        }

        for (int i = 0; i < imageViews.size(); i++) {
            final int t = i;
            imageViews.get(i).addEventHandler(
                                              MouseEvent.MOUSE_CLICKED,
                                              e -> {
                                                  LOG.debug("Image view {} clicked.", t);
                                                  TrialEvent.KeyPressed keyEvent = new TrialEvent.KeyPressed();
                                                  keyEvent.setKey(t);
                                                  trialEventQueue.add(keyEvent);
                                                  }
                                              );
        }

        show();
    }

    public int getImageRegionCount() {
        return imageViews.size();
    }

    public void setImage(int position, String image) throws TrialConfigException {
        try {
            imageViews.get(position).setImage(loadImage(image));
        } catch (IndexOutOfBoundsException e) {
            throw new TrialConfigException("Invalid image position " + position + ".");
        }
    }

    public void show() {
        imageViews.forEach(imageView -> imageView.setVisible(true));
    }

    public void clear() {
        imageViews.forEach(imageView -> {
                imageView.setVisible(false);
                imageView.setImage(null);
            });
        textAreas.forEach(textArea -> {
                textArea.setText("");
                textArea.setVisible(false);
            });
    }

    Image loadImage(String filename) {

        if (filename == null) return null;

        Image cached = imageCache.get(filename);

        if (cached != null) return cached;

        try (InputStream file = new FileInputStream(imageLibrary + filename)) {
            Image image = new Image(file);
            imageCache.put(filename, image);
            return image;
        } catch (FileNotFoundException e) {
            LOG.error("Image file '{}' not found in library.", filename);
        } catch (IOException e) {
            LOG.error("Cannot read image file '{}'.", filename);
        }

        return imageNotFound;
    }

    static Image loadBase64Image(String dataURL) {

        String encodingPrefix = "base64,";
        int contentStartIndex = dataURL.indexOf(encodingPrefix) + encodingPrefix.length();

        String base64Data = dataURL.substring(contentStartIndex);
        byte[] binaryData = Base64.getDecoder().decode(base64Data);

        return new Image(new ByteArrayInputStream(binaryData));
    }

    // An embedded image to be displayed if images cannot be found.
    static final String imageNotFoundData = "data:image/png;base64,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";
}
