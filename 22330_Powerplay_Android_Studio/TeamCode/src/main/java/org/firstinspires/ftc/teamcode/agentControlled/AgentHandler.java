package org.firstinspires.ftc.teamcode.agentControlled;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import java.nio.ByteBuffer;
import java.util.Map;

import ai.onnxruntime.OnnxJavaType;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxTensorLike;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

public class AgentHandler {
    OrtEnvironment env;
    OrtSession session;

    private int signalInt = -1;

    private static final String VUFORIA_KEY = "ATZ/F4X/////AAABmSjnLIM91kSRo8TfH6CpvkpQb02HUOXzsAmc9sWr5aQKwBP0+GpVCddkSd7qVIgzYGRsutM1OEr4dRHyoy7G3gE8kovM+mnw5nVVkEJQEOhXlUt8ZN23VxVEMHO9qDIcH4vEv6w105kXo9FLJlikfRmKzVjMF/YAS4bU9UQVYpVzXCrEaoSE67McYRahSc3JfFmVkMqUCS2DDqyBC3MkN/YsO+EPmjz4iDIGz9HkSHkxylCOQ3rSHZQwZoGyrPJfkpl4XJoH+dKIawL3KeEWbMOIwDFR/IECVa8SNEeeaThDF3pvha2lTtdtgh5XLIcdSi27UQVTnaaM+5/G2gHLPMQ4n3DHIg4CQvmChLZTwD65";

    private VuforiaLocalizer vuforia;

    public void initEnvironment(String modelPath) {
        try {
            env = OrtEnvironment.getEnvironment();
            OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
            session = env.createSession(modelPath, opts);
        } catch ( OrtException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeEnvironment() {
        try {
            session.close();
            env.close();
        } catch (OrtException e) {
            throw new RuntimeException(e);
        }
    }

    public void initVuforia(HardwareMap map) {
        // Usual code to initialize Vuforia

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = map.get(WebcamName.class, "Webcam 1");

        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Set the frame queue capacity (3 has always worked for us)

        vuforia.setFrameQueueCapacity(3);
    }

    public ByteBuffer getFrame() {
        VuforiaLocalizer.CloseableFrame frame = vuforia.getFrameQueue().poll();

        for (int i = 0; i < frame.getNumImages(); i++) {
            Image image = frame.getImage(i);

            if (image.getFormat() == PIXEL_FORMAT.GRAYSCALE) {
                int width = image.getWidth();

                int height = image.getHeight();

                // Once you have selected a specific RGB565 image, you can fill a ByteBuffer with its

                // pixel contents.

                ByteBuffer buf = image.getPixels();

                // And you can fill an array of bytes with the contents of the byte buffer (two bytes per pixel)

                byte[] bytes = new byte[2 * width * height];

                return buf;
            }
        }

        return null;
    }

    public float/*[]*/ runAuto(/*ByteBuffer image, */float rotationFloat/*, float timeFloat*/) {
        try {
            ByteBuffer rotation = ByteBuffer.allocateDirect(4).putFloat(rotationFloat);
            /*ByteBuffer time = ByteBuffer.allocateDirect(4).putFloat(timeFloat);
            ByteBuffer signal = ByteBuffer.allocateDirect(4).putInt(signalInt);*/

            Map<String, ? extends OnnxTensorLike> tensor = Map.of(
                    //"image", OnnxTensor.createTensor(env, image, new long[]{1, 1, 256, 256}, OnnxJavaType.FLOAT
                    /*), */"rotation", OnnxTensor.createTensor(env, rotation, new long[]{1}, OnnxJavaType.FLOAT
                    //), "seconds", OnnxTensor.createTensor(env, time, new long[]{1}, OnnxJavaType.FLOAT
                    //), "signal", OnnxTensor.createTensor(env, signal, new long[]{1}, OnnxJavaType.FLOAT
                    ));

            float[] output = new float[4];

            // Run the model
            OrtSession.Result result = session.run(tensor);

            //for (int i = 0; i < 4; i++) {
            //    output[i] = (float)result.get(i).getValue();
            //}
            //signalInt = (int)result.get(5).getValue();

            return output[0];

        } catch (OrtException e) {
            throw new RuntimeException(e);
        }
    }
}