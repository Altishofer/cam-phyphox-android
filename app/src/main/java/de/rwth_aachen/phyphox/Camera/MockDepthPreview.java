package de.rwth_aachen.phyphox.Camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class MockDepthPreview extends DepthPreview {

    CameraManager cameraManager;

    public MockDepthPreview(Context context) {
        super(context);
    }

    @Override
    public void setCamera(String id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        try {
            depthInput = new MockDepthInput(
                    depthInput.getExtractionMode(),
                    depthInput.x1,
                    depthInput.x2,
                    depthInput.y1,
                    depthInput.y2,
                    null,
                    null,
                    null,
                    cameraManager
            );
            depthInput.setCamera(id);
            depthInput.startCameras();
        } catch (Exception e) {
            // Handle errors gracefully
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
        if (depthInput == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                depthInput.attachPreviewSurface(st, width, height);
            } catch (Exception e) {
                // Handle exceptions
            }
        }
    }
}
