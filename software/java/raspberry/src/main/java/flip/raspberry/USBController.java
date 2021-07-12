package flip.raspberry;

import flip.raspberry.trial.TrialEvent;
import flip.raspberry.trial.TrialEventQueue;

import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.Scanner;
// import java.time.LocalDateTime;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.beans.factory.annotation.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Signal;
import sun.misc.SignalHandler;


@Component
public class USBController implements Runnable, USBControllerMessage.Visitor {

    final Logger LOG = LoggerFactory.getLogger(getClass());
    private Thread thread;

    @Autowired TrialEventQueue trialEventQueue;
    @Autowired ObjectMapper    objectMapper;
    @Autowired Remote          remote;

    private SerialPort port;

    @EventListener
    public void handleContextStarted(ContextStartedEvent event) {
        thread = new Thread(this);
        thread.start();
    }

    @EventListener
    public void handleContextClosed(ContextClosedEvent event) throws InterruptedException {
        if (port != null) {
            port.closePort();
        }
        thread.interrupt();
        thread.join();
    }

    public void reward() {
        try {
            port.getOutputStream().write("{\"type\": \"dispense\"}".getBytes());
        } catch (IOException e) {
            LOG.warn("Failed to dispense reward.", e);
        }
    }

    public void callibrateScale(long offset, long divider) {
        try {
            port.getOutputStream().write(("{\"type\":\"callibrate_scale\", \"offset\":" + offset + ", \"divider\":" + divider + "}").getBytes());
        } catch (IOException e) {
            LOG.warn("Failed to callibrate scale.", e);
        }
    }

    @Override
    public void run() {

        Signal.handle(
                      new Signal("USR2"),
                      new SignalHandler() {
                          @Override
                          public void handle(Signal signal) {
                              LOG.info("Manual reward.");
                              reward();
                          }
                      });

        LOG.info("USBController started");
        try {
            while (true) {

                Optional<SerialPort> candidatePort = Arrays.stream(SerialPort.getCommPorts())
                    .filter(port -> port.getDescriptivePortName().toLowerCase().contains("arduino due"))
                    .findFirst();

                try {
                    port = candidatePort.get();
                    port.openPort();
                    port.setBaudRate(115200);
                    port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

                    InputStream in = port.getInputStream();
                    Scanner sc = new Scanner(in);

                    while (true) {
                        // objectMapper.readValue(in, USBControllerMessage.class) isn't reading fast enough?
                        String line = sc.nextLine();
                        USBControllerMessage message = objectMapper.readValue(line, USBControllerMessage.class);
                        LOG.debug("Received {} from USB controller.", message);
                        message.accept(this);
                    }

                } catch (IOException e) {
                    if (thread.isInterrupted()) {
                        throw new InterruptedException();
                    } else {
                        LOG.error("IOException in USBController thread.", e);
                        remote.error("IOException in USBController thread.", e);
                        port.closePort();
                    }
                } catch (NoSuchElementException e) {
                    LOG.error("USB controller not found.");
                }

                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            LOG.info("USB controller shutting down.");
        }
    }

    @Override
    public void accept(DebugMessage debugMessage) {
        LOG.debug("Received debug message from usb controller: {}.", debugMessage);
    }

    @Override
    public void accept(ErrorMessage errorMessage) {
        LOG.error("Received error message from USB controller: {}.", errorMessage);
        remote.error("Received error message from USB controller: {}.", errorMessage);
    }

    @Override
    public void accept(TrialEvent trialEvent) {
        trialEventQueue.add(trialEvent);
    }
}
