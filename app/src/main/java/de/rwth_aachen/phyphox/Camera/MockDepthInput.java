package de.rwth_aachen.phyphox.Camera;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;

import de.rwth_aachen.phyphox.DataOutput;
import de.rwth_aachen.phyphox.ExperimentTimeReference;

public class MockDepthInput extends DepthInput {

    public MockDepthInput(DepthExtractionMode mode, float x1, float x2, float y1, float y2, Vector<DataOutput> buffers, Lock lock, ExperimentTimeReference experimentTimeReference, CameraManager cameraManager) {
        super(mode, x1, x2, y1, y2, buffers, lock, experimentTimeReference, cameraManager);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void startCamera() throws DepthInputException, CameraAccessException {
        previewCameraId = findPreviewCamera(-1);
        if (previewCameraId == null) {
            throw new DepthInputException("No preview camera found.");
        }

        imageReader = null; // No depth data required

        try {
            cameraManager.openCamera(previewCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    MockDepthInput.this.cameraDevice = camera;
                    List<Surface> surfaces = new ArrayList<>();
                    try {
                        cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                MockDepthInput.this.session = session;
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) {
                                stop();
                            }
                        }, null);
                    } catch (Exception e) {
                        stop();
                    }
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    stop();
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    stop();
                }
            }, null);
        } catch (SecurityException e) {
            throw new DepthInputException("Permissions not granted.");
        }
    }

    @Override
    public void start() throws DepthInputException {
        // Simulate depth measurements by writing constant values
        dataLock.lock();
        try {
            if (dataZ != null) {
                dataZ.append(0.0); // Depth = 0
            }
        } finally {
            dataLock.unlock();
        }
    }
}
