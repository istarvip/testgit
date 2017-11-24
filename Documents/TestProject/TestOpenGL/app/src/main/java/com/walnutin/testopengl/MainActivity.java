package com.walnutin.testopengl;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity implements IOpenGlDemo {

    private GLSurfaceView mGLSurfaceView;

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private ShortBuffer indexBuffer;
//    private float angle = 0f;
//    private float[] vertexArray = new float[]{
//            -0.8f , -0.4f * 1.732f , 0.0f ,
//            0.8f , -0.4f * 1.732f , 0.0f ,
//            0.0f , 0.4f * 1.732f , 0.0f ,
//    };


//    private float[] vertexArray = new float[]{
//            0f , 0f  , 0.0f ,
//            0.2f , 0.4f  , 0.0f ,
//            1f , 1f  , 0.0f ,
//    };


//    float vertexArray[] = {
//            -0.8f, -0.4f * 1.732f, 0.0f,
//            -0.4f, 0.4f * 1.732f, 0.0f,
//            0.0f, -0.4f * 1.732f, 0.0f,
//            0.4f, 0.4f * 1.732f, 0.0f,
//    };

    float vertexArray[] = {
            -1f, -1f, 0.0f,
            -0.4f, 0.4f * 1.732f, 0.0f,
            0.0f, -1f, 0.0f,
            0.4f, 0.4f * 1.732f, 0.0f,
    };


    static final float X = .525731112119133606f;
    static final float Z = .850650808352039932f;
    static float vertices[] = new float[]{
            -X, 0.0f, Z, X, 0.0f, Z, -X, 0.0f, -Z, X, 0.0f, -Z,
            0.0f, Z, X, 0.0f, Z, -X, 0.0f, -Z, X, 0.0f, -Z, -X,
            Z, X, 0.0f, -Z, X, 0.0f, Z, -X, 0.0f, -Z, -X, 0.0f
    };
    static short indices[] = new short[]{
            0, 4, 1, 0, 9, 4, 9, 5, 4, 4, 5, 8, 4, 8, 1,
            8, 10, 1, 8, 3, 10, 5, 3, 8, 5, 2, 3, 2, 7, 3,
            7, 10, 3, 7, 6, 10, 7, 11, 6, 11, 0, 6, 0, 1, 6,
            6, 1, 10, 9, 0, 11, 9, 11, 2, 9, 2, 5, 7, 2, 11};


//    float[] colors = {
//            0f, 0f, 0f, 1f,
//            0f, 0f, 1f, 1f,
//            0f, 1f, 0f, 1f,
//            0f, 1f, 1f, 1f,
//            1f, 0f, 0f, 1f,
//            1f, 0f, 1f, 1f,
//            1f, 1f, 0f, 1f,
//            1f, 1f, 1f, 1f,
//            1f, 0f, 0f, 1f,
//            0f, 1f, 0f, 1f,
//            0f, 0f, 1f, 1f,
//            1f, 0f, 1f, 1f
//    };


    float[] colors = {
            0f, 0f, 0f, 1f,
            1f, 1f, 1f, 1f,
            0f, 0f, 0f, 1f,
            0f, 0f, 0f, 1f,
            0f, 0f, 0f, 1f,
            0f, 0f, 0f, 1f,
            0f, 0f, 0f, 1f,
            0f, 0f, 0f, 1f,
            0f, 0f, 0f, 1f,
            0f, 0f, 0f, 1f,
            0f, 0f, 0f, 1f,
            0f, 0f, 0f, 1f,
    };


    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setRenderer(new MyRenderer(this));
        setContentView(mGLSurfaceView);

        init20Faces();

    }


    private void init20Faces() {
        ByteBuffer vbb
                = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        ByteBuffer cbb
                = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);
        ByteBuffer ibb
                = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
    }


    @Override
    public void DrawScene(GL10 gl) {

        gl.glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT
                | GL10.GL_DEPTH_BUFFER_BIT);

//        drawPoints(gl);

//          drawLines(gl);
//            draw20Faces(gl);

//        drawSunAera(gl);
        drawQiu(gl);
    }


    @Override
    protected void onResume() {
        // Ideally a game should implement
        // onResume() and onPause()
        // to take appropriate action when the
        //activity looses focus
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume()
        //and onPause()
        // to take appropriate action when the
        //activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
    }


    public void testBuff() {
        float[] vertexArray = new float[]{
                -0.8f, -0.4f * 1.732f, 0.0f,
                0.8f, -0.4f * 1.732f, 0.0f,
                0.0f, 0.4f * 1.732f, 0.0f,
        };
        ByteBuffer buffer = ByteBuffer.allocateDirect(vertexArray.length * 4);
        FloatBuffer floatBuffer = buffer.asFloatBuffer();
        floatBuffer.put(vertexArray);
        floatBuffer.position(0);


    }


    public void drawPoints(GL10 gl) {
        ByteBuffer vbb
                = ByteBuffer.allocateDirect(vertexArray.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertex = vbb.asFloatBuffer();
        vertex.put(vertexArray);
        vertex.position(0);
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        gl.glPointSize(28f);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -3);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
        gl.glDrawArrays(GL10.GL_POINTS, 0, 3);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public void drawLines(GL10 gl) {
        ByteBuffer vbb
                = ByteBuffer.allocateDirect(vertexArray.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertex = vbb.asFloatBuffer();
        vertex.put(vertexArray);
        vertex.position(0);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -4);
        gl.glLineWidth(28f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
//        index++;
//        index%=10;
//        switch(index){
//            case 0:
//            case 1:
//            case 2:

//                break;
//            case 3:
//            case 4:
//            case 5:


//                break;
//            case 6:
//            case 7:
//            case 8:
//            case 9:

//                break;
//        }

//    gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
//    gl.glDrawArrays(GL10.GL_LINES, 0, 4);

        gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 4);
//
//    gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
//    gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, 4);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }


    public void draw20Faces(GL10 gl) {


        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -4);
        gl.glRotatef(angle, 0, 1, 0);
//            gl.glFrontFace(GL10.GL_CCW);
//            gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//            gl.glDisable(GL10.GL_CULL_FACE);
        angle++;


    }


    private Star sun = new Star();
    private Star earth = new Star();
    private Star moon = new Star();
    private float angle = 0.01f;

    public void drawSunAera(GL10 gl) {
        gl.glLoadIdentity();
        GLU.gluLookAt(gl, 0f, 0f, 20f,
                0.0f, 0.0f, 0.0f,
                0f, 1f, 0f);
        // Star A
        // Save the current matrix.
        gl.glPushMatrix();           //保存设置
        // Rotate Star A counter-clockwise.
        gl.glRotatef(angle, 0, 0, 1);
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        // Draw Star A.
        sun.draw(gl);
        // Restore the last matrix.
        gl.glPopMatrix();            //恢复设置
        // Star B
        // Save the current matrix
        gl.glPushMatrix();
        // Rotate Star B before moving it,
        //making it rotate around A.
        gl.glRotatef(-angle, 0, 0, 1);
        // Move Star B.
        gl.glTranslatef(3, 0, 0);

        gl.glRotatef(angle * 365, 0, 0, 1);
        // Scale it to 50% of Star A
        gl.glScalef(.5f, .5f, .5f);
        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
        // Draw Star B.
        earth.draw(gl);
        // Star C
        // Save the current matrix
        gl.glPushMatrix();
        // Make the rotation around B
        gl.glRotatef(-angle, 0, 0, 1);
        gl.glTranslatef(2, 0, 0);
        // Scale it to 50% of Star B
        gl.glScalef(.5f, .5f, .5f);
        // Rotate around it's own center.
        gl.glRotatef(angle * 10, 0, 0, 1);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        // Draw Star C.
        moon.draw(gl);
        // Restore to the matrix as it was before C.
        gl.glPopMatrix();
        // Restore to the matrix as it was before B.
        gl.glPopMatrix();
        // Increse the angle.
        angle += 0.01f;


    }


    public void drawQiu(GL10 gl) {
        float theta, pai;
        float co, si;
        float r1, r2;
        float h1, h2;
        float step = 2.0f;
        float[][] v = new float[32][3];
        ByteBuffer vbb;
        FloatBuffer vBuf;
        vbb = ByteBuffer.allocateDirect(v.length  * v[0].length*4);
        vbb.order(ByteOrder.nativeOrder());
        vBuf = vbb.asFloatBuffer();


//        gl.glLoadIdentity();
//        GLU.gluLookAt(gl, 0f, 0f, 20f,
//                0.0f, 0.0f, 0.0f,
//                0f, 1f, 0f);

        initScene(gl);


        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

        gl.glRotatef(angle, 0, 0, 1);
        gl.glColor4f(1.0f, 0.3f, 1.0f, 1.0f);

        for (pai = -90.0f; pai < 90.0f; pai += step) {
            int n = 0;
            r1 = (float) Math.cos(pai  * Math.PI / 180.0);
            r2 = (float) Math.cos((pai + step)  * Math.PI / 180.0);
            h1 = (float) Math.sin(pai  * Math.PI / 180.0);
            h2 = (float) Math.sin((pai + step)  * Math.PI / 180.0);
            for (theta = 0.0f; theta <= 360.0f; theta += step) {
                co = (float) Math.cos(theta  * Math.PI / 180.0);
                si = -(float) Math.sin(theta  * Math.PI / 180.0);
                v[n][0] = (r2*co);
                v[n][1] = (h2);
                v[n][2] = (r2*si);
                v[n + 1][0] = (r1*co);
                v[n + 1][1] = (h1);
                v[n + 1][2] = (r1*si);
                vBuf.put(v[n]);
                vBuf.put(v[n + 1]);
                n += 2;
                if (n > 31) {
                    vBuf.position(0);
                    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vBuf);
                    gl.glNormalPointer(GL10.GL_FLOAT, 0, vBuf);
                    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, n);
                    n = 0;
                    theta -= step;
                }
            }
            vBuf.position(0);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vBuf);
            gl.glNormalPointer(GL10.GL_FLOAT, 0, vBuf);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, n);
        }
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);


        angle++;
    }


    private void initScene(GL10 gl){
        float[] mat_amb = {0.2f * 1.0f, 0.2f * 0.4f, 0.2f * 0.4f, 1.0f,};
        float[] mat_diff = {1.0f, 0.4f, 0.4f, 1.0f,};
        float[] mat_spec = {1.0f, 1.0f, 1.0f, 1.0f,};

        ByteBuffer mabb = ByteBuffer.allocateDirect(mat_amb.length*4);
        mabb.order(ByteOrder.nativeOrder());
        FloatBuffer mat_ambBuf = mabb.asFloatBuffer();
        mat_ambBuf.put(mat_amb);
        mat_ambBuf.position(0);

        ByteBuffer mdbb = ByteBuffer.allocateDirect(mat_diff.length*4);
        mdbb.order(ByteOrder.nativeOrder());
        FloatBuffer mat_diffBuf = mdbb.asFloatBuffer();
        mat_diffBuf.put(mat_diff);
        mat_diffBuf.position(0);

        ByteBuffer msbb = ByteBuffer.allocateDirect(mat_spec.length*4);
        msbb.order(ByteOrder.nativeOrder());
        FloatBuffer mat_specBuf = msbb.asFloatBuffer();
        mat_specBuf.put(mat_spec);
        mat_specBuf.position(0);



        gl.glClearColor(0.8f, 0.8f, 0.8f, 0.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glShadeModel(GL10.GL_SMOOTH);

        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);

        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mat_ambBuf);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mat_diffBuf);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mat_specBuf);
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 64.0f);

        gl.glLoadIdentity();
        GLU.gluLookAt(gl,0.0f, 0.0f, 10.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);

    }


}
