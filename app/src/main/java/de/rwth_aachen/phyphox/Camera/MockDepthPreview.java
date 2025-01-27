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
        removeOverlay();
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

    private void removeOverlay() {
        if (overlayView != null) {
            removeView(overlayView); // Ensure the overlay view is removed from the layout
            overlayView = null;     // Nullify the reference for cleanup
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

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        if (depthInput == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                depthInput.detachPreviewSurface(); // Stop the old preview session
                depthInput.attachPreviewSurface(surfaceTexture, width, height); // Attach the updated surface
                updateTransformation(width, height); // Adjust transformation
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (depthInput != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                depthInput.detachPreviewSurface(); // Properly release the surface
            }
        }
        return true; // Indicate the texture can be safely destroyed
    }
}
