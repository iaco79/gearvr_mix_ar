package com.iaco.gearvrar;

import android.opengl.Matrix;

import com.vuforia.ImageTarget;
import com.vuforia.Matrix44F;
import com.vuforia.Tool;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;

import java.util.List;

import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRContext;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.utility.Log;
/**
 * Created by othon on 4/3/16.
 */
public class TrackedModel extends GVRSceneObject {

    private static final String TAG = "gvrar";

    private GVRModelSceneObject mModel;
    private int mTrackingId;
    private boolean mHidden;
    private GVRScene mScene;
    private float[] convertedMVMatrix;
    private float[] vuforiaMVMatrix;
    private float mVuforiaScale;
    private float mModelScale;
    private float mModelYRotate;
    private float mModelXRotate;


    private boolean mTracked;

    private GVRAnimation mAnimation;
    private GVRAnimationEngine mAnimationEngine;


    private float[] gvrMVMatrix;
    private float[] totalMVMatrix;
    private float[] convertMatrix = {1f, 0f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 0f,
            -1f, 0f, 0f, 0f, 0f, 1f};

    public TrackedModel(GVRContext gvrContext, GVRScene scene,GVRAnimationEngine animationEngine,  int trackId, GVRModelSceneObject model) {
        super(gvrContext);
        mAnimationEngine = animationEngine;
        mModel = model;
        mTrackingId = trackId;
        mScene = scene;


        vuforiaMVMatrix = new float[16];
        convertedMVMatrix = new float[16];
        gvrMVMatrix = new float[16];
        totalMVMatrix = new float[16];

        mTracked=false;
        mHidden=true;
        mModelScale=1.0f;
        mModelYRotate=0;
        mModelXRotate=90;


        this.getTransform().setPosition(0.0f, 0.0f, 0.0f);
        addChildObject(model);


    }


    public void startAnimation() {

        if(mAnimation == null) {
            List<GVRAnimation> animations = mModel.getAnimations();

            if (animations.size() >= 1) {

                mAnimation = animations.get(0); //get the first animation
                mAnimation.setRepeatMode(GVRRepeatMode.REPEATED).setRepeatCount(-1); //loop the animation
                mAnimation.start(mAnimationEngine); //start the animation
            }


        }

    }

    public void stopAnimation()
    {
            if(mAnimation!=null)
            {
                mAnimationEngine.stop(mAnimation);
            }

        mAnimation=null;


    }

    public void show() {


        if (mHidden == true) {

            mScene.addSceneObject(this);

            mHidden = false;
        }
        else
        {
            if(mAnimation==null)
            {
                startAnimation();

            }

        }


    }


    public void hide() {
        if (!mHidden) {
            stopAnimation();

            mScene.removeSceneObject(this);

            mHidden = true;
        }

    }

    public void step()
    {
        if (mTracked)
            show();
        else
            hide();

        if(!mHidden && mTracked)
        {

            updateTrackedPose();

        }

    }


    public void setTracked(boolean tracked)
    {
        mTracked=tracked;


    }
    public void setTrackedPose(float[] matrix, float scale)
    {

        for(int i=0;i<16;i++)
            vuforiaMVMatrix[i]=matrix[i];

        mVuforiaScale=scale;

    }


    private void updateTrackedPose( )
    {


        Matrix.multiplyMM(convertedMVMatrix, 0, convertMatrix, 0,
                vuforiaMVMatrix, 0);

        Matrix.rotateM(convertedMVMatrix, 0, mModelXRotate, 1, 0, 0);
        Matrix.rotateM(convertedMVMatrix, 0, mModelYRotate, 0, 1, 0);

        Matrix.scaleM(convertedMVMatrix, 0, mVuforiaScale*mModelScale, mVuforiaScale*mModelScale, mVuforiaScale*mModelScale);

        gvrMVMatrix = mScene.getMainCameraRig()
                .getHeadTransform().getModelMatrix();

        Matrix.multiplyMM(totalMVMatrix, 0, gvrMVMatrix, 0,
                convertedMVMatrix, 0);

        mModel.getTransform().setModelMatrix(totalMVMatrix);


    }

    public int getTrackingId() {
        return mTrackingId;
    }


    public float getModelScale() {
        return mModelScale;
    }

    public void setModelScale(float mModelScale) {
        this.mModelScale = mModelScale;
    }

    public float getModelYRotate() {
        return mModelYRotate;
    }

    public void setModelYRotate(float mModelYRotate) {
        this.mModelYRotate = mModelYRotate;
    }

    public float getModelXRotate() {
        return mModelXRotate;
    }

    public void setModelXRotate(float xRotate) {
        this.mModelXRotate = xRotate;
    }
}
