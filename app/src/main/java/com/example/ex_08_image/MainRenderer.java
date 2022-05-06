package com.example.ex_08_image;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.google.ar.core.Session;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRenderer implements GLSurfaceView.Renderer {

    CameraPreView mCamera;
    ObjRenderer mObj;

    boolean isImgFind = false;

    boolean mViewportChanged;
    int mViewportWidth, mViewportHeight;
    RenderCallback mRenderCallback;

    MainRenderer(Context context, RenderCallback callback){
        mRenderCallback = callback;
        mCamera = new  CameraPreView();

//        mObj = new  ObjRenderer(context, "andy.obj", "andy.png");
        mObj = new  ObjRenderer(context, "dog.obj", "dog.jpg");
    }


    interface RenderCallback{
        void preRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 3차원좌표
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(1.0f,1.0f, 0.0f, 1.0f);

        mCamera.init();
        mObj.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width, height);
        mViewportChanged = true;
        mViewportWidth = width;
        mViewportHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mRenderCallback.preRender();


        // 깊이 버퍼. 3차원 접근 끄고 카메라 그리고 다시 3차원 접근 살리기
        // DEPTH_TEST 를 활성화 했을 때만 사용 가능
        GLES20.glDepthMask(false);
        mCamera.draw();
        GLES20.glDepthMask(true);

        if(isImgFind) {
            mObj.draw();
        }
    }

    void updateSession(Session session, int displayRotation){
        // 화면이 변경됐다면
        if(mViewportChanged){
            session.setDisplayGeometry(displayRotation, mViewportWidth, mViewportHeight);
            mViewportChanged = false;
        }
    }

    void setProjectionMatrix(float [] matrix){
        mObj.setProjectionMatrix(matrix);

    }

    void updateViewMatrix(float [] matrix){
        mObj.setViewMatrix(matrix);
    }

    // 카메라로부터 텍스쳐 처리
    int getTextureId(){
        return mCamera == null ? -1 : mCamera.mTextures[0];
    }
}