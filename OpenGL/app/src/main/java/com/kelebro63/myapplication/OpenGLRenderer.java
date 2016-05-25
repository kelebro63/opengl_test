package com.kelebro63.myapplication;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.kelebro63.myapplication.Utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.*;

/**
 * Created by Bistrov Alexey on 23.05.2016.
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private final static int POSITION_COUNT = 3;

    private Context context;

    private int programId;

    private FloatBuffer vertexData;
    private int uColorLocation;
    private int aPositionLocation;
    private int uMatrixLocation;

    private float[] mProjectionMatrix = new float[16];

    public OpenGLRenderer(Context context) {
        this.context = context;
        prepareData();
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glEnable(GL_DEPTH_TEST);    //включает буфер глубины
        glClearColor(0f, 0f, 0f, 1f);
        int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(programId);
        bindData();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);
        bindMatrix(width, height);
    }

    private void prepareData() {
        float z1 = -3.0f, z2 = -1.0f;

        float[] vertices = {
                // первый треугольник
                -0.7f, -0.5f, z1,
                0.3f, -0.5f, z1,
                -0.2f, 0.3f, z1,

                // второй треугольник
                -0.3f, -0.4f, z2,
                0.7f, -0.4f, z2,
                0.2f, 0.4f, z2,
        };

        vertexData = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertices);
    }

    private void bindData() {
        // координаты
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GL_FLOAT,
                false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        // цвет
        uColorLocation = glGetUniformLocation(programId, "u_Color");

        // матрица
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);         //очистка буфера цвета и глубины

        // зеленый треугольник
        glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 3);


        // синий треугольник
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 3, 3);
    }

    private void bindMatrix(int width, int height) {
        float ratio = 1.0f;
        float left = -1.0f;
        float right = 1.0f;
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 1.0f;
        float far = 8.0f;
        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        glUniformMatrix4fv(uMatrixLocation, 1, false, mProjectionMatrix, 0);
    }

}