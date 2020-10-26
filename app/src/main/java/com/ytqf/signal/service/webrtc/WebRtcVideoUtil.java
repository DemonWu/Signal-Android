package com.ytqf.signal.service.webrtc;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ytqf.signal.components.webrtc.BroadcastVideoSink;
import com.ytqf.signal.ringrtc.Camera;
import com.ytqf.signal.ringrtc.CameraEventListener;
import com.ytqf.signal.ringrtc.CameraState;
import com.ytqf.signal.service.webrtc.state.WebRtcServiceState;
import com.ytqf.signal.service.webrtc.state.WebRtcServiceStateBuilder;
import com.ytqf.signal.util.Util;
import org.webrtc.EglBase;

/**
 * Helper for initializing, reinitializing, and deinitializing the camera and it's related
 * infrastructure.
 */
public final class WebRtcVideoUtil {

  private WebRtcVideoUtil() {}

  public static @NonNull WebRtcServiceState initializeVideo(@NonNull Context context,
                                                            @NonNull CameraEventListener cameraEventListener,
                                                            @NonNull WebRtcServiceState currentState)
  {
    final WebRtcServiceStateBuilder builder = currentState.builder();

    Util.runOnMainSync(() -> {
      EglBase            eglBase   = EglBase.create();
      BroadcastVideoSink localSink = new BroadcastVideoSink(eglBase);
      Camera             camera    = new Camera(context, cameraEventListener, eglBase, CameraState.Direction.FRONT);

      builder.changeVideoState()
             .eglBase(eglBase)
             .localSink(localSink)
             .camera(camera)
             .commit()
             .changeLocalDeviceState()
             .cameraState(camera.getCameraState())
             .commit();
    });

    return builder.build();
  }

  public static @NonNull WebRtcServiceState reinitializeCamera(@NonNull Context context,
                                                               @NonNull CameraEventListener cameraEventListener,
                                                               @NonNull WebRtcServiceState currentState)
  {
    final WebRtcServiceStateBuilder builder = currentState.builder();

    Util.runOnMainSync(() -> {
      Camera camera = currentState.getVideoState().requireCamera();
      camera.setEnabled(false);
      camera.dispose();

      camera = new Camera(context,
                          cameraEventListener,
                          currentState.getVideoState().requireEglBase(),
                          currentState.getLocalDeviceState().getCameraState().getActiveDirection());

      builder.changeVideoState()
             .camera(camera)
             .commit()
             .changeLocalDeviceState()
             .cameraState(camera.getCameraState())
             .commit();
    });

    return builder.build();
  }

  public static @NonNull WebRtcServiceState deinitializeVideo(@NonNull WebRtcServiceState currentState) {
    Camera camera = currentState.getVideoState().getCamera();
    if (camera != null) {
      camera.dispose();
    }

    EglBase eglBase = currentState.getVideoState().getEglBase();
    if (eglBase != null) {
      eglBase.release();
    }

    return currentState.builder()
                       .changeVideoState()
                       .eglBase(null)
                       .camera(null)
                       .localSink(null)
                       .commit()
                       .changeLocalDeviceState()
                       .cameraState(CameraState.UNKNOWN)
                       .build();
  }
}
