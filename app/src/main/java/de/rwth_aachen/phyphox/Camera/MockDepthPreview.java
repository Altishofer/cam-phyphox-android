package de.rwth_aachen.phyphox.Camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class MockDepthPreview extends DepthPreview {

    CameraManager cameraManager;
    private static final int FIXED_WIDTH = 1080;
    private static final int FIXED_HEIGHT = 1080;



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
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (depthInput == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                // Set the wide, low resolution for the preview
                surfaceTexture.setDefaultBufferSize(FIXED_WIDTH, FIXED_HEIGHT);
                depthInput.detachPreviewSurface(); // Ensure no leftover sessions
                depthInput.attachPreviewSurface(surfaceTexture, FIXED_WIDTH, FIXED_HEIGHT);
                updateTransformation(FIXED_WIDTH, FIXED_HEIGHT); // Update transformation with fixed resolution
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        if (depthInput == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                // Keep the wide, low resolution for the preview
                surfaceTexture.setDefaultBufferSize(FIXED_WIDTH, FIXED_HEIGHT);
                depthInput.detachPreviewSurface(); // Stop the old preview session
                depthInput.attachPreviewSurface(surfaceTexture, FIXED_WIDTH, FIXED_HEIGHT); // Attach the updated surface
                updateTransformation(FIXED_WIDTH, FIXED_HEIGHT); // Adjust transformation
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
