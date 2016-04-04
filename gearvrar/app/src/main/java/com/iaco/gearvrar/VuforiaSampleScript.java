package com.iaco.gearvrar;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;
import com.vuforia.GLTextureData;
import com.vuforia.ImageTarget;
import com.vuforia.Matrix44F;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.TextureData;
import com.vuforia.Tool;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial.GVRShaderType;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRRenderTexture;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRScript;
import org.gearvrf.GVRActivity;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.scene_objects.GVRModelSceneObject;
/**
 * Created by othon on 4/3/16.
 *
 * Display Mixed AR using 3d animated models.
 *
 * Based on gearvr framework vuforia sample.
 */

public class VuforiaSampleScript extends GVRScript {

    private static final String TAG = "gvrar";

    private GVRContext gvrContext = null;
    private GVRRenderTexture passThroughTexture;

    private List<TrackedModel> mTrackedModels = new ArrayList<TrackedModel>();


    private GVRActivity mActivity;

    static final int VUFORIA_CAMERA_WIDTH = 1280;
    static final int VUFORIA_CAMERA_HEIGHT = 720;

    
    private volatile boolean init = false;

    private GVRScene mainScene;
    private GVRAnimationEngine mAnimationEngine;

    private float[] vuforiaMVMatrix;

    private boolean teapotVisible = false;
    
    @Override
    public void onInit(GVRContext gvrContext) {
        this.gvrContext = gvrContext;


        mAnimationEngine = gvrContext.getAnimationEngine();

        mainScene = gvrContext.getNextMainScene(new Runnable() {


            @Override
            public void run() {

                //setup the see-trough render texture after initialization is completed.
                TextureData data = new GLTextureData( passThroughTexture.getId());
                Renderer.getInstance().setVideoBackgroundTexture(data);




            }
        });



        createCameraPassThrough();
        createTRex();
        createDolphin();


        init = true;
    }
    
    public boolean isInit() {
        return init;
    }

    private void createCameraPassThrough() {

        passThroughTexture = new GVRRenderTexture(gvrContext,
                VUFORIA_CAMERA_WIDTH, VUFORIA_CAMERA_HEIGHT);

        GVRSceneObject passThroughObject = new GVRSceneObject(gvrContext, 16.0f / 9.0f, 1.0f);

        passThroughObject.getTransform().setPosition(0.0f, 0.0f, -1000.0f);
        passThroughObject.getTransform().setScaleX(1000f);
        passThroughObject.getTransform().setScaleY(1000f);


        GVRRenderData renderData = passThroughObject.getRenderData();
        GVRMaterial material = new GVRMaterial(gvrContext);
        renderData.setMaterial(material);
        material.setMainTexture(passThroughTexture);
        material.setShaderType(GVRShaderType.Texture.ID);

        // the following texture coordinate values are determined empirically
        // and do not match what we expect them to be. but still they work :)
        float[] texCoords = { 0.0f, 0.0f, 0.0f, 0.70f, 0.62f, 0.0f, 0.62f, 0.7f };
        GVRMesh mesh = renderData.getMesh();
        mesh.setTexCoords(texCoords);
        renderData.setMesh(mesh);


        mainScene.getMainCameraRig().addChildObject(passThroughObject);

    }


    void createTRex ()
    {
        TrackedModel trackedModel =null;
        try {
             GVRModelSceneObject rexModel = gvrContext.loadModel("Tyrannosaurus.dae");
             trackedModel = new TrackedModel(gvrContext,mainScene, mAnimationEngine, 2, rexModel);
            trackedModel.setmModelScale(0.1f);
             mTrackedModels.add(trackedModel);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to load rexModel model: %s", e);
        }

    }


    void createDolphin ()
    {
        TrackedModel trackedModel =null;
        try {
            GVRModelSceneObject dolphinModel = gvrContext.loadModel("dolphin.dae");
            trackedModel = new TrackedModel(gvrContext,mainScene, mAnimationEngine, 1, dolphinModel);
            trackedModel.setmModelScale(0.1f);

            mTrackedModels.add(trackedModel);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to load dolphinModel model: %s", e);
        }

    }



    @Override
    public void onStep() {

        for(TrackedModel model : mTrackedModels)
        {
            model.step();

        }
        if (VuforiaSampleActivity.isVuforiaActive())
        {
            Renderer.getInstance().begin();
            if (!Renderer.getInstance().updateVideoBackgroundTexture())
            {
                Log.e("VUFORIA", "Unable to bind video background texture!!");
            }
            Renderer.getInstance().end();
        }
    }

    public void updateObjectPose(State state) {
        // did we find any trackables this frame?
        int numDetectedMarkers = state.getNumTrackableResults();


        if (numDetectedMarkers == 0) {

            for(TrackedModel model : mTrackedModels) {
                model.setTracked(false);
            }
		}

        for(TrackedModel model : mTrackedModels)
        {
           boolean tracked=false;

            for (int tIdx = 0; tIdx < numDetectedMarkers; tIdx++)
            {
                TrackableResult result = state.getTrackableResult(tIdx);
                Trackable trackable = result.getTrackable();


                if (trackable.getId() == model.getTrackingId())
                {

                    Matrix44F modelViewMatrix_Vuforia = Tool
                            .convertPose2GLMatrix(result.getPose());

                    float scaleFactor = ((ImageTarget) trackable).getSize()
                            .getData()[0];


                    vuforiaMVMatrix = modelViewMatrix_Vuforia.getData();



                    model.setTrackedPose(vuforiaMVMatrix, scaleFactor);
                    model.setTracked(true);
                    tracked = true;
                }
            }
            if(!tracked) {
                model.setTracked(false);

            }

        }

    }

}
