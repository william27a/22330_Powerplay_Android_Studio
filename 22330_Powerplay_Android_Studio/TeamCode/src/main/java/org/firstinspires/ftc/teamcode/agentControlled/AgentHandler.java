package org.firstinspires.ftc.teamcode.agentControlled;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.Image;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.nio.ByteBuffer;

public class AgentHandler {
    private Interpreter interpreter;

    private static final String VUFORIA_KEY = "ATZ/F4X/////AAABmSjnLIM91kSRo8TfH6CpvkpQb02HUOXzsAmc9sWr5aQKwBP0+GpVCddkSd7qVIgzYGRsutM1OEr4dRHyoy7G3gE8kovM+mnw5nVVkEJQEOhXlUt8ZN23VxVEMHO9qDIcH4vEv6w105kXo9FLJlikfRmKzVjMF/YAS4bU9UQVYpVzXCrEaoSE67McYRahSc3JfFmVkMqUCS2DDqyBC3MkN/YsO+EPmjz4iDIGz9HkSHkxylCOQ3rSHZQwZoGyrPJfkpl4XJoH+dKIawL3KeEWbMOIwDFR/IECVa8SNEeeaThDF3pvha2lTtdtgh5XLIcdSi27UQVTnaaM+5/G2gHLPMQ4n3DHIg4CQvmChLZTwD65";

    private VuforiaLocalizer vuforia;

    public AgentHandler(File agentFile) {
        interpreter = new Interpreter(agentFile);
    }

    public AgentHandler() {}

    public void initVuforia(HardwareMap map) {
        // Usual code to initialize Vuforia

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = map.get(WebcamName.class, "Webcam 1");

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Set the frame queue capacity (3 has always worked for us)

        vuforia.setFrameQueueCapacity(3);
    }

    public byte[] getFrame() {
        VuforiaLocalizer.CloseableFrame frame = vuforia.getFrameQueue().poll();

        if (frame == null) {
            frame.close();
            return new byte[0];
        }

        for (int i = 0; i < frame.getNumImages(); i++) {
            Image image = frame.getImage(i);

            // RGB565 is 1
            if (image.getFormat() == 1) {
                int width = image.getWidth();

                int height = image.getHeight();

                // Once you have selected a specific RGB565 image, you can fill a ByteBuffer with its

                // pixel contents.

                ByteBuffer buf = image.getPixels();

                // And you can fill an array of bytes with the contents of the byte buffer (two bytes per pixel)

                byte[] bytes = new byte[2 * width * height];

                buf.get(bytes); // Fills the array from the byte buffer

                return buf.array();
            }
        }

        return new byte[0];
    }

    public String[] getSignatureKeys() {
        return interpreter.getSignatureKeys();
    }

    public void forward(byte[] frame, double degrees, double seconds) {}
}