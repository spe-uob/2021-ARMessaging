package com.ajal.arsocialmessaging.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ajal.arsocialmessaging.util.ConnectivityHelper;
import com.ajal.arsocialmessaging.util.HashCreator;
import com.ajal.arsocialmessaging.util.database.server.ServerDBObserver;
import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.server.ServerDBHelper;
import com.ajal.arsocialmessaging.util.database.Message;
import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.databinding.FragmentHomeBinding;
import com.ajal.arsocialmessaging.util.location.GPSObserver;
import com.ajal.arsocialmessaging.util.PermissionHelper;
import com.ajal.arsocialmessaging.ui.home.common.helpers.DepthSettings;
import com.ajal.arsocialmessaging.ui.home.common.helpers.DisplayRotationHelper;
import com.ajal.arsocialmessaging.ui.home.common.helpers.SnackbarHelper;
import com.ajal.arsocialmessaging.ui.home.common.helpers.TrackingStateHelper;
import com.ajal.arsocialmessaging.ui.home.common.samplerender.Framebuffer;
import com.ajal.arsocialmessaging.ui.home.common.samplerender.GLError;
import com.ajal.arsocialmessaging.ui.home.common.samplerender.Mesh;
import com.ajal.arsocialmessaging.ui.home.common.samplerender.SampleRender;
import com.ajal.arsocialmessaging.ui.home.common.samplerender.Shader;
import com.ajal.arsocialmessaging.ui.home.common.samplerender.Texture;
import com.ajal.arsocialmessaging.ui.home.common.samplerender.VertexBuffer;
import com.ajal.arsocialmessaging.ui.home.common.samplerender.arcore.BackgroundRenderer;
import com.ajal.arsocialmessaging.ui.home.common.samplerender.arcore.PlaneRenderer;
import com.ajal.arsocialmessaging.ui.home.common.samplerender.arcore.SpecularCubemapFilter;
import com.ajal.arsocialmessaging.util.location.PostcodeHelper;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.LightEstimate;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingFailureReason;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.core.exceptions.TextureNotSetException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

// REFERENCE: https://github.com/google-ar/arcore-android-sdk/tree/master/samples/hello_ar_java 12/11/2021 @ 3:23pm

/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore API. The application will display any detected planes and will allow the user to tap on a
 * plane to place a 3D model.
 */
public class HomeFragment extends Fragment implements SampleRender.Renderer, ServerDBObserver, GPSObserver {

    private FragmentHomeBinding binding;
    private View root;

    private static final String TAG = "SkyWrite";

    private static final String SEARCHING_PLANE_MESSAGE = "Searching for surfaces...";
    private static final String FOUND_PLANE_MESSAGE = "Look up to view message!";
    private static final String NO_VIRTUAL_MESSAGES_MESSAGE = "This postcode has no messages";
    private static final String IMG_SAVED_MESSAGE = "Image saved to storage!";

    private static final String NETWORK_ERROR_MESSAGE = "Network error. Please try again";
    private static final String LOCATION_ERROR_MESSAGE = "Location is unavailable error. Please try again";

    // See the definition of updateSphericalHarmonicsCoefficients for an explanation of these
    // constants.
    private static final float[] sphericalHarmonicFactors = {
            0.282095f,
            -0.325735f,
            0.325735f,
            -0.325735f,
            0.273137f,
            -0.273137f,
            0.078848f,
            -0.273137f,
            0.136569f,
    };

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100f;

    private static final int CUBEMAP_RESOLUTION = 16;
    private static final int CUBEMAP_NUMBER_OF_IMPORTANCE_SAMPLES = 32;

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;

    private boolean installRequested;

    private Session session;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();;
    private DisplayRotationHelper displayRotationHelper;
    private final TrackingStateHelper trackingStateHelper = new TrackingStateHelper(this.getActivity());
    private SampleRender render;

    private PlaneRenderer planeRenderer;
    private BackgroundRenderer backgroundRenderer;
    private Framebuffer virtualSceneFramebuffer;
    private boolean hasSetTextureNames = false;

    private final DepthSettings depthSettings = new DepthSettings();

    // Point Cloud
    private VertexBuffer pointCloudVertexBuffer;
    private Mesh pointCloudMesh;
    private Shader pointCloudShader;
    // Keep track of the last point cloud rendered to avoid updating the VBO if point cloud
    // was not changed.  Do this using the timestamp since we can't compare PointCloud objects.
    private long lastPointCloudTimestamp = 0;

    // Virtual object
    private List<Mesh> virtualObjectMeshesList = new ArrayList<>();
    private List<Shader> virtualObjectShadersList = new ArrayList<>();
    private final ArrayList<Anchor> anchors = new ArrayList<>();
    private List<VirtualMessage> localVirtualMessages = new ArrayList<>();
    private List<Integer> localBannersId = new ArrayList<>(); // stores Id of the banners
    private List<Banner> globalBanners = new ArrayList<>();

    // Messages, Banners and Location loading
    private List<Message> messages = new ArrayList<>();
    private boolean messagesRetrieved = false;
    private boolean bannersRetrieved = false;
    private boolean locationRetrieved = false;
    private boolean requiredDataRetrieved = false;
    private Semaphore messagesMutex = new Semaphore(1); // to make sure messages are received before loading the textures

    // Environmental HDR
    private Texture dfgTexture;
    private SpecularCubemapFilter cubemapFilter;

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16]; // view x model
    private final float[] modelViewProjectionMatrix = new float[16]; // projection x view x model
    private final float[] sphericalHarmonicsCoefficients = new float[9 * 3];
    private final float[] viewInverseMatrix = new float[16];
    private final float[] worldLightDirection = {0.0f, 0.0f, 0.0f, 0.0f};
    private final float[] viewLightDirection = new float[4]; // view x world light direction

    // For taking pictures
    private int mWidth;
    private int mHeight;
    private  boolean capturePicture = false;
    private String outFile;

    // Boolean to only show tracked points and planes once when it has been found
    private boolean drawTracked = true;

    // Location related attributes
    private Location location;
    private String postcode;

    // MediaPlayer to play audio files
    private boolean audioPlaying;
    private int audioNumber;
    private MediaPlayer mediaPlayer;

    // Preferences
    private SharedPreferences sharedPref;
    private boolean playAudio;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        super.onCreate(savedInstanceState);
        surfaceView = root.findViewById(R.id.surfaceview);
        displayRotationHelper = new DisplayRotationHelper(/*context=*/ this.getContext());

        // SkyWrite: add bottom navigation view to snackbar helper
        View bottomNavigation = super.getActivity().findViewById(R.id.nav_view);
        this.messageSnackbarHelper.setBottomNavigationView(bottomNavigation);

        // Set up renderer.
        render = new SampleRender(surfaceView, this, this.getContext().getAssets());

        // Check if network and location are available
        if (!ConnectivityHelper.getInstance().isNetworkAvailable()
                || !ConnectivityHelper.getInstance().isLocationAvailable()) {
            return root;
        }

        // Request the server to load the results from the database
        ServerDBHelper serverDbHelper = ServerDBHelper.getInstance();
        // Need to clear callbacks or else ServerDBHelper can try to send a context which no longer exists
        serverDbHelper.clearObservers();
        serverDbHelper.registerObserver(this);
        serverDbHelper.retrieveDBResults();

        PostcodeHelper postcodeHelper = PostcodeHelper.getInstance();
        postcodeHelper.clearObservers();
        postcodeHelper.registerObserver(this);
        try {
            messagesMutex.acquire(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return root;
        }

        installRequested = false;

        // SkyWrite: Set up button listener to take photo
        Button snapBtn = (Button) root.findViewById(R.id.snap_button);
        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturePicture = true;
            }
        });

        // Retrieve Shared Preferences
        sharedPref = getActivity().getSharedPreferences(getString(R.string.toggle_audio), Context.MODE_PRIVATE);
        if (sharedPref.getString(getString(R.string.toggle_audio), "Off").equals("On")) {
            playAudio = true;
        }
        else {
            playAudio = false;
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        // Need to clear callbacks or else ServerDBHelper can try to send a context which no longer exists
        ServerDBHelper.getInstance().clearObservers();
        PostcodeHelper.getInstance().clearObservers();

        if (mediaPlayer != null) mediaPlayer.release();
        if (session != null) {
            // Explicitly close ARCore Session to release native resources.
            // Review the API reference for important considerations before calling close() in apps with
            // more complicated lifecycle requirements:
            // https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#close()
            session.close();
            session = null;
            hasSetTextureNames = false; // needed to set the camera textures again when back button is pressed
            virtualObjectMeshesList.clear();
            virtualObjectShadersList.clear();
        }

        super.onDestroyView();
        binding = null;

        this.messageSnackbarHelper.hide(this.getActivity()); // hides the activity
    }

    @Override
    public void onResume() {
        super.onResume();

        // SkyWrite: add bottom navigation view to snackbar helper
        View bottomNavigation = super.getActivity().findViewById(R.id.nav_view);
        this.messageSnackbarHelper.setBottomNavigationView(bottomNavigation);

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this.getActivity(), !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // Check that SkyWrite still has the correct permissions and if not, open the permissions page
                if (!PermissionHelper.hasPermissions(this.getActivity())) {
                    Toast.makeText(this.getContext(), "Permissions are needed to run this application", Toast.LENGTH_LONG).show();
                    PermissionHelper.requestPermissionsIfDenied(this.getActivity());
                    return;
                }

                // Create the session.
                session = new Session(this.getContext());
            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = "This device does not support AR";
                exception = e;
            } catch (Exception e) {
                message = "Failed to create AR session";
                exception = e;
            }

            if (message != null) {
                messageSnackbarHelper.showError(this.getActivity(), message);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            configureSession();
            // To record a live camera session for later playback, call
            // `session.startRecording(recordingConfig)` at anytime. To playback a previously recorded AR
            // session instead of using the live camera feed, call
            // `session.setPlaybackDatasetUri(Uri)` before calling `session.resume()`. To
            // learn more about recording and playback, see:
            // https://developers.google.com/ar/develop/java/recording-and-playback
            session.resume();
        } catch (CameraNotAvailableException e) {
            messageSnackbarHelper.showError(this.getActivity(), "Camera not available. Try restarting the app.");
            session = null;
            return;
        }

        surfaceView.onResume();
        displayRotationHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            hasSetTextureNames = false; // needed to set the camera textures again when back button is pressed
            session.pause();
        }
    }

    @Override
    public void onSurfaceCreated(SampleRender render) {
        // Prepare the rendering objects. This involves reading shaders and 3D model files, so may throw
        // an IOException.

        try {
            planeRenderer = new PlaneRenderer(render);
            backgroundRenderer = new BackgroundRenderer(render);
            virtualSceneFramebuffer = new Framebuffer(render, /*width=*/ 1, /*height=*/ 1);

            cubemapFilter =
                    new SpecularCubemapFilter(
                            render, CUBEMAP_RESOLUTION, CUBEMAP_NUMBER_OF_IMPORTANCE_SAMPLES);
            // Load DFG lookup table for environmental lighting
            dfgTexture =
                    new Texture(
                            render,
                            Texture.Target.TEXTURE_2D,
                            Texture.WrapMode.CLAMP_TO_EDGE,
                            /*useMipmaps=*/ false);
            // The dfg.raw file is a raw half-float texture with two channels.
            final int dfgResolution = 64;
            final int dfgChannels = 2;
            final int halfFloatSize = 2;

            ByteBuffer buffer =
                    ByteBuffer.allocateDirect(dfgResolution * dfgResolution * dfgChannels * halfFloatSize);
            try (InputStream is = this.getContext().getAssets().open("models/dfg.raw")) {
                is.read(buffer.array());
            }
            // SampleRender abstraction leaks here.
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dfgTexture.getTextureId());
            GLError.maybeThrowGLException("Failed to bind DFG texture", "glBindTexture");
            GLES30.glTexImage2D(
                    GLES30.GL_TEXTURE_2D,
                    /*level=*/ 0,
                    GLES30.GL_RG16F,
                    /*width=*/ dfgResolution,
                    /*height=*/ dfgResolution,
                    /*border=*/ 0,
                    GLES30.GL_RG,
                    GLES30.GL_HALF_FLOAT,
                    buffer);
            GLError.maybeThrowGLException("Failed to populate DFG texture", "glTexImage2D");

            // Point cloud
            pointCloudShader =
                    Shader.createFromAssets(
                            render, "shaders/point_cloud.vert", "shaders/point_cloud.frag", /*defines=*/ null)
                            .setVec4(
                                    "u_Color", new float[] {31.0f / 255.0f, 188.0f / 255.0f, 210.0f / 255.0f, 1.0f})
                            .setFloat("u_PointSize", 5.0f);
            // four entries per vertex: X, Y, Z, confidence
            pointCloudVertexBuffer =
                    new VertexBuffer(render, /*numberOfEntriesPerVertex=*/ 4, /*entries=*/ null);
            final VertexBuffer[] pointCloudVertexBuffers = {pointCloudVertexBuffer};
            pointCloudMesh =
                    new Mesh(
                            render, Mesh.PrimitiveMode.POINTS, /*indexBuffer=*/ null, pointCloudVertexBuffers);

            // Store the meshes and the shaders of every message into the respective lists
            virtualObjectMeshesList = new ArrayList<>();
            virtualObjectShadersList = new ArrayList<>();
            messagesMutex.acquire(1); // Need to make sure that the messages has been received before the virtual messages are loaded
            for (int i = 0; i < messages.size(); i++) {
                VirtualMessage virtualMessage = new VirtualMessage(messages.get(i));
                virtualObjectMeshesList.add(VirtualObjectRenderHelper.renderVirtualObjectMesh(render, virtualMessage));
                virtualObjectShadersList.add(VirtualObjectRenderHelper.renderVirtualObjectShader(render, virtualMessage, cubemapFilter, dfgTexture));
            }
            messagesMutex.release();
            // Hide the loading circle once it has finished loading the textures
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    root.findViewById(R.id.loading_circle).setVisibility(View.GONE);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Failed to read a required asset file", e);
            messageSnackbarHelper.showError(this.getActivity(), "Failed to read a required asset file: " + e);
        } catch (InterruptedException e) {
            Log.e(TAG, "Failed to acquire lock", e);
            messageSnackbarHelper.showError(this.getActivity(), "Failed to read a required asset file: " + e);
        }

    }

    @Override
    public void onSurfaceChanged(SampleRender render, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        virtualSceneFramebuffer.resize(width, height);
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void onDrawFrame(SampleRender render) {
        if (session == null) {
            return;
        }

        // Texture names should only be set once on a GL thread unless they change. This is done during
        // onDrawFrame rather than onSurfaceCreated since the session is not guaranteed to have been
        // initialized during the execution of onSurfaceCreated.
        if (!hasSetTextureNames) {
            session.setCameraTextureNames(
                    new int[] {backgroundRenderer.getCameraColorTexture().getTextureId()});
            hasSetTextureNames = true;
        }

        // -- Update per-frame state

        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session);

        // Obtain the current frame from ARSession. When the configuration is set to
        // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
        // camera framerate.
        Frame frame;
        try {
            frame = session.update();
        } catch (CameraNotAvailableException e) {
            Log.e(TAG, "Camera not available during onDrawFrame", e);
            messageSnackbarHelper.showError(this.getActivity(), "Camera not available. Try restarting the app.");
            return;
        } catch (TextureNotSetException e) {
            Log.e(TAG, "No textures", e);
            messageSnackbarHelper.showError(this.getActivity(), "Textures not available. Try restarting the app.");
            return;
        }
        Camera camera = frame.getCamera();

        // Update BackgroundRenderer state to match the depth settings.
        try {
            backgroundRenderer.setUseDepthVisualization(
                    render, depthSettings.depthColorVisualizationEnabled());
            backgroundRenderer.setUseOcclusion(render, depthSettings.useDepthForOcclusion());
        } catch (IOException e) {
            Log.e(TAG, "Failed to read a required asset file", e);
            messageSnackbarHelper.showError(this.getActivity(), "Failed to read a required asset file: " + e);
            return;
        }
        // BackgroundRenderer.updateDisplayGeometry must be called every frame to update the coordinates
        // used to draw the background camera image.
        backgroundRenderer.updateDisplayGeometry(frame);

        if (camera.getTrackingState() == TrackingState.TRACKING
                && (depthSettings.useDepthForOcclusion()
                || depthSettings.depthColorVisualizationEnabled())) {
            try (Image depthImage = frame.acquireDepthImage()) {
                backgroundRenderer.updateCameraDepthTexture(depthImage);
            } catch (NotYetAvailableException e) {
                // This normally means that depth data is not available yet. This is normal so we will not
                // spam the logcat with this.
            }
        }

        // Handle the anchors
        if (drawTracked) {
            handleAnchor(frame, camera);
        }

        /* Show a message based on whether:
         * - tracking has failed,
         * - planes are detected and models are drawn
         * - there are no messages in the area
         */
        String message;
        if (capturePicture) {
            // SkyWrite: display message when image is saved
            // needs to be at the top of the if statements as it takes priority
            message = IMG_SAVED_MESSAGE;
        } else if (!ConnectivityHelper.getInstance().isNetworkAvailable()) {
            message = NETWORK_ERROR_MESSAGE;
            drawTracked = false;
        } else if (!ConnectivityHelper.getInstance().isLocationAvailable()) {
            message = LOCATION_ERROR_MESSAGE;
            drawTracked = false;
        } else if (localBannersId.size() == 0) {
            message = NO_VIRTUAL_MESSAGES_MESSAGE;
            drawTracked = false;
        } else if (camera.getTrackingState() == TrackingState.PAUSED) {
            if (camera.getTrackingFailureReason() == TrackingFailureReason.NONE) {
                message = SEARCHING_PLANE_MESSAGE;
                anchors.clear(); // SkyWrite: will remove anchor whenever you lose track of the points
                drawTracked = true;
            } else {
                message = TrackingStateHelper.getTrackingFailureReasonString(camera);
            }
        } else if (hasTrackingPlane() && requiredDataRetrieved) {
            message = FOUND_PLANE_MESSAGE;
            drawTracked = false;
        } else {
            message = SEARCHING_PLANE_MESSAGE;
            drawTracked = true;
        }

        if (message == null) {
            messageSnackbarHelper.hide(this.getActivity());
        } else {
            messageSnackbarHelper.showMessage(this.getActivity(), message);
        }

        // -- Draw background

        if (frame.getTimestamp() != 0) {
            // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
            // drawing possible leftover data from previous sessions if the texture is reused.
            backgroundRenderer.drawBackground(render);
        }

        // If not tracking, don't draw 3D objects.
        if (camera.getTrackingState() == TrackingState.PAUSED) {
            return;
        }

        // -- Draw non-occluded virtual objects (planes, point cloud)

        // Get projection matrix.
        camera.getProjectionMatrix(projectionMatrix, 0, Z_NEAR, Z_FAR);
        // Get camera matrix and draw.
        camera.getViewMatrix(viewMatrix, 0);

        if (drawTracked) {
            // Visualize tracked points.
            // Use try-with-resources to automatically release the point cloud.
            try (PointCloud pointCloud = frame.acquirePointCloud()) {
                if (pointCloud.getTimestamp() > lastPointCloudTimestamp) {
                    pointCloudVertexBuffer.set(pointCloud.getPoints());
                    lastPointCloudTimestamp = pointCloud.getTimestamp();
                }
                Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
                pointCloudShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix);
                render.draw(pointCloudMesh, pointCloudShader);
            }

            // Visualize planes.
            planeRenderer.drawPlanes(
                    render,
                    session.getAllTrackables(Plane.class),
                    camera.getDisplayOrientedPose(),
                    projectionMatrix);
        }
        if (localBannersId.size() > 0) {
            // Visualize models
            // Update lighting parameters in the shader
            updateLightEstimation(frame.getLightEstimate(), viewMatrix);
            render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f);
            for (int i = 0; i < anchors.size(); i++) {
                Anchor anchor = anchors.get(i);
                if (anchor.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }
                // Get the current pose of an Anchor in world space. The Anchor pose is updated
                // during calls to session.update() as ARCore refines its estimate of the world.

                anchor.getPose().toMatrix(modelMatrix, 0);

                // Scale Matrix - not really too sure how to do this as scaling it makes it look closer to you
                Matrix.scaleM(modelMatrix, 0, 2f, 2f, 2f);

                // Calculate model/view/projection matrices
                Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
                Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

                // Update shader properties and draw
                int objectId = localBannersId.get(i);
                virtualObjectShadersList.get(objectId).setMat4("u_ModelView", modelViewMatrix);
                virtualObjectShadersList.get(objectId).setMat4("u_ModelViewProjection", modelViewProjectionMatrix);
                render.draw(virtualObjectMeshesList.get(objectId), virtualObjectShadersList.get(objectId), virtualSceneFramebuffer);
            }

            // Play audio files
            if (hasTrackingPlane() && playAudio && !audioPlaying && audioNumber <= localBannersId.size() - 1) {
                String audioFile = localVirtualMessages.get(audioNumber).getAudioFile();
                playAudioFile(this.getContext(), audioFile);
                audioNumber++;
            }
        }

        // Compose the virtual scene with the background.
        backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, Z_NEAR, Z_FAR);

        // SkyWrite: Save the picture if the button is pressed
        if (capturePicture) {
            capturePicture = false;
            try {
                SavePicture();
            } catch (IOException e) {
                messageSnackbarHelper.showError(this.getActivity(), "Exception saving image");
                Log.e(TAG, "Exception saving image", e);
            }
        }
    }

    /**
     * Call from the GLThread to save a picture of the current frame.
     * REFERENCE: https://stackoverflow.com/questions/48191513/how-to-take-picture-with-camera-using-arcore 20/11/2021 @ 4:36pm
     */
    public void SavePicture() throws IOException {
        int pixelData[] = new int[mWidth * mHeight];

        // Read the pixels from the current GL frame.
        IntBuffer buf = IntBuffer.wrap(pixelData);
        buf.position(0);
        GLES20.glReadPixels(0, 0, mWidth, mHeight,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);

        // Get time of photo taken to use to store file
        String date = new SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(new Date());

        // Create a file in Internal Storage/DCIM/SkyWrite
        final File out = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/SkyWrite", "IMG_" +
                date+ ".png");

        outFile = out.getName();

        // Make sure the directory exists, if not make it
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }

        // Convert the pixel data from RGBA to what Android wants, ARGB.
        int bitmapData[] = new int[pixelData.length];
        for (int i = 0; i < mHeight; i++) {
            for (int j = 0; j < mWidth; j++) {
                int p = pixelData[i * mWidth + j];
                int b = (p & 0x00ff0000) >> 16;
                int r = (p & 0x000000ff) << 16;
                int ga = p & 0xff00ff00;
                bitmapData[(mHeight - i - 1) * mWidth + j] = ga | r | b;
            }
        }

        // Create a bitmap.
        Bitmap bmp = Bitmap.createBitmap(bitmapData,
                mWidth, mHeight, Bitmap.Config.ARGB_8888);

        // Write it to disk.
        FileOutputStream fos = new FileOutputStream(out);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();

        // Save to gallery
        galleryAddPic();

        Log.d(TAG, "Image "+outFile+" saved");
    }

    /**
     * Adds picture to gallery
     * REFERENCE: https://developer.android.com/training/camera/photobasics.html#TaskGallery 20/11/2021 @ 12:17am
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM) + "/SkyWrite", outFile);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.getActivity().sendBroadcast(mediaScanIntent);
    }

    /**
     * Automatically place an anchor rather than by tap, unlike how hello_ar_java does it
     * @param frame
     * @param camera
     */
    private void handleAnchor(Frame frame, Camera camera) {
        int d = 0; // d is the distance from the first message
        int p = 1; // p is the position around the first message, there will be only 4 positions
        for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {
                while (anchors.size() < localBannersId.size()) {
                    int i = anchors.size();
                    Pose pose = plane.getCenterPose();

                    // Change the rotation of the pose to face the camera
                    if (pose.qy() > 0) {
                        pose = pose.makeRotation(0, -pose.qy(), 0, 1).compose(pose);
                    }
                    else {
                        pose.makeRotation(0, -pose.qy(), 0, 1).compose(pose);
                    }

                    // Calculate where to place anchor
                    /** e.g. for 17 banners, draw the banners out like in the comment below, from 1 to 17:
                     *         14
                     *     10      11
                     *         6
                     *       2   3
                     * 17  9   1   7  15
                     *       5   4
                     *         8
                     *     13      12
                     *         16
                     */
                    pose = pose.makeTranslation(0, 30f, -30f);
                    if (d > 0) {
                        if (d % 2 == 0) { // diamond shape
                            switch (p) {
                                case 1 : pose = pose.makeTranslation(0f, d*5f, 0).compose(pose); p += 1; break; // up
                                case 2 : pose = pose.makeTranslation(d*5f, 0, 0).compose(pose); p += 1; break; // right
                                case 3 : pose = pose.makeTranslation(0, -d*5f, 0).compose(pose); p += 1; break; // down
                                case 4 : pose = pose.makeTranslation(-d*5f, 0, 0).compose(pose); p = 1; d += 1; break; // right
                            }
                        }
                        else { // square shape
                            switch (p) {
                                case 1 : pose = pose.makeTranslation(-d*5f, d*5f, 0).compose(pose); p += 1; break; // up-left
                                case 2 : pose = pose.makeTranslation(d*5f, d*5f, 0).compose(pose); p += 1; break; // up-right
                                case 3 : pose = pose.makeTranslation(d*5f, -d*5f, 0).compose(pose); p += 1; break; // down-right
                                case 4 : pose = pose.makeTranslation(-d*5f, -d*5f, 0).compose(pose); p = 1; d += 1; break; // down-left
                            }
                        }
                    }
                    else { // just increase distance if d == 0
                        d += 1;
                    }
                    Anchor anchor = session.createAnchor(pose);

                    anchors.add(anchor);
                }
            }
            break;
        }
    }

    /** Checks if we detected at least one plane. */
    private boolean hasTrackingPlane() {
        for (Plane plane : session.getAllTrackables(Plane.class)) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {
                return true;
            }
        }
        return false;
    }

    /** Update state based on the current frame's light estimation. */
    private void updateLightEstimation(LightEstimate lightEstimate, float[] viewMatrix) {
        if (lightEstimate.getState() != LightEstimate.State.VALID) {
            for (Shader s : virtualObjectShadersList) {
                s.setBool("u_LightEstimateIsValid", false);
            }
            return;
        }
        for (int i = 0; i < localBannersId.size(); i++) {
            int objectId = localBannersId.get(i);
            virtualObjectShadersList.get(objectId).setBool("u_LightEstimateIsValid", true);
            Matrix.invertM(viewInverseMatrix, 0, viewMatrix, 0);
            virtualObjectShadersList.get(objectId).setMat4("u_ViewInverse", viewInverseMatrix);
        }
        updateMainLight(
                lightEstimate.getEnvironmentalHdrMainLightDirection(),
                lightEstimate.getEnvironmentalHdrMainLightIntensity(),
                viewMatrix);
        updateSphericalHarmonicsCoefficients(
                lightEstimate.getEnvironmentalHdrAmbientSphericalHarmonics());
        cubemapFilter.update(lightEstimate.acquireEnvironmentalHdrCubeMap());
    }

    private void updateMainLight(float[] direction, float[] intensity, float[] viewMatrix) {
        // We need the direction in a vec4 with 0.0 as the final component to transform it to view space
        worldLightDirection[0] = direction[0];
        worldLightDirection[1] = direction[1];
        worldLightDirection[2] = direction[2];
        Matrix.multiplyMV(viewLightDirection, 0, viewMatrix, 0, worldLightDirection, 0);
        for (Shader s : virtualObjectShadersList) {
            s.setVec4("u_ViewLightDirection", viewLightDirection);
            s.setVec3("u_LightIntensity", intensity);
        }
    }

    private void updateSphericalHarmonicsCoefficients(float[] coefficients) {
        // Pre-multiply the spherical harmonics coefficients before passing them to the shader. The
        // constants in sphericalHarmonicFactors were derived from three terms:
        //
        // 1. The normalized spherical harmonics basis functions (y_lm)
        //
        // 2. The lambertian diffuse BRDF factor (1/pi)
        //
        // 3. A <cos> convolution. This is done to so that the resulting function outputs the irradiance
        // of all incoming light over a hemisphere for a given surface normal, which is what the shader
        // (environmental_hdr.frag) expects.
        //
        // You can read more details about the math here:
        // https://google.github.io/filament/Filament.html#annex/sphericalharmonics

        if (coefficients.length != 9 * 3) {
            throw new IllegalArgumentException(
                    "The given coefficients array must be of length 27 (3 components per 9 coefficients");
        }

        // Apply each factor to every component of each coefficient
        for (int i = 0; i < 9 * 3; ++i) {
            sphericalHarmonicsCoefficients[i] = coefficients[i] * sphericalHarmonicFactors[i / 3];
        }
        for (Shader s : virtualObjectShadersList) {
            s.setVec3Array(
                    "u_SphericalHarmonicsCoefficients", sphericalHarmonicsCoefficients);
        }
    }

    /** Configures the session with feature settings. */
    private void configureSession() {
        Config config = session.getConfig();
        config.setLightEstimationMode(Config.LightEstimationMode.ENVIRONMENTAL_HDR);
        config.setDepthMode(Config.DepthMode.DISABLED);
        config.setInstantPlacementMode(Config.InstantPlacementMode.DISABLED);
        session.configure(config);
    }

    private void playAudioFile(Context context, String audioFile) {
        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor descriptor = this.getContext().getAssets().openFd(audioFile);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    audioPlaying = false;
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
            audioPlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onMessageSuccess(List<Message> result) {
        if (result == null) {
            messageSnackbarHelper.showError(this.getActivity(), "Cannot retrieve messages. Please try restarting SkyWrite.");
        }
        else {
            messagesRetrieved = true;
            messages = result; // used to generate virtualObject lists
            generateLocalVirtualMessages();
        }
    }

    @Override
    public void onMessageFailure() {
        Log.e(TAG, "Error receiving messages");
        messageSnackbarHelper.showError(this.getActivity(), "Cannot retrieve messages. Please try restarting SkyWrite.");
        messagesRetrieved = true;
    }

    @Override
    public void onBannerSuccess(List<Banner> result) {
        if (result == null) {
            messageSnackbarHelper.showError(this.getActivity(), "Cannot retrieve banners. Please try restarting SkyWrite.");
        }
        else {
            globalBanners = result;
            bannersRetrieved = true;
            generateLocalVirtualMessages();
        }
    }

    @Override
    public void onBannerFailure() {
        Log.e(TAG, "Error receiving banners");
        messageSnackbarHelper.showError(this.getActivity(), "Cannot retrieve banners. Please try restarting SkyWrite.");
        globalBanners = new ArrayList<>();
        bannersRetrieved = true;
        localBannersId.clear();
    }

    @Override
    public void onLocationSuccess(Location location) {
        this.location = location;

        if (location == null) {
            messageSnackbarHelper.showError(this.getActivity(), "Cannot find location. Please try restarting SkyWrite.");
        }
        else {
            postcode = PostcodeHelper.getPostCode(this.getContext(), location.getLatitude(), location.getLongitude());
            locationRetrieved = true;
            generateLocalVirtualMessages();
        }

        View root = binding.getRoot();
        TextView postcodeTextView = root.findViewById(R.id.postcode_text_view);
        postcodeTextView.setText("Postcode: "+postcode);
    }

    private void generateLocalVirtualMessages() {
        if (messagesRetrieved && bannersRetrieved && locationRetrieved) {
            // If the user switched fragments faster than the request is received (e.g. running Android tests),
            // then this.getContext() will be null. As a result, this if statement is required
            if (this.getContext() != null) {
                localBannersId.clear();
                localVirtualMessages.clear();
                for (Banner b : globalBanners) {
                    // compares current hashed postcode with hashed postcodes from server
                    if (b.getPostcode().equals(HashCreator.createSHAHash(postcode))) {
                        int id = b.getMessage() - 1;
                        localBannersId.add(id);
                        VirtualMessage virtualMessage = new VirtualMessage(messages.get(id));
                        localVirtualMessages.add(virtualMessage);
                    }
                }
                requiredDataRetrieved = true;
                messagesMutex.release();
            }
        }
    }
}